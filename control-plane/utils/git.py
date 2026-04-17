import json
import logging
import os
import time
from enum import Enum
from typing import Callable
import json
import base64
import os
import requests

import requests

from utils.colors import YELLOW, RESET, GREEN, RED
from utils.gradle import build as gradle_build
from utils.nexus import get_latest_version_any_platform, download_jar
from utils.utils import read_managed_mods, reset_directory, parse_version, run_timed_shell_commands
from utils.utils import run_timed_shell_command, run_shell_command, get_build_dir

HOME_DIR = os.path.expanduser('~')
SCRIPT_DIR: str = os.path.dirname(os.path.abspath(__file__))
CLONE_DIR: str = reset_directory(f"{SCRIPT_DIR}/../tmp/")

REPO_UPDATE_ONLY_TAG: str = "repo_update_only"
NO_UNMANAGED_UPDATE_TAG: str = "no_unmanaged_update"
NO_VELOCITY_TAG: str = "no_velocity"
NO_NEOFORGE_TAG: str = "no_neoforge"
NO_FABRIC_TAG: str = "no_fabric"

PLATFORMS: dict[str, str] = {  # <platform-name>: <simple-platform-name>
    "backend-fabric": "fabric",
    "backend-neoforge": "neoforge",
    "velocity": "velocity"
}

BRANCH_EXISTENCE_CACHE: dict[str, bool] = {}
REPOSITORY_CACHE: dict["VersionedRepository", "Repository"] = {}


class VersionedRepository:
    url: str
    version: "RepositoryVersion"

    def __init__(self, url: str, version: "RepositoryVersion"):
        self.url = url
        self.version = version

    def __eq__(self, other):
        if not isinstance(other, VersionedRepository):
            return False

        return self.url == other.url and self.version == other.version

    def __hash__(self):
        return hash((self.url, self.version))


class RepositoryVersion:
    __version: str | None
    __branch: str | None

    __branch_latest_commit: str | None
    __computed_version: str | None

    @classmethod
    def copy(cls, other: "RepositoryVersion") -> "RepositoryVersion":
        result = RepositoryVersion("master")
        result.__version = other.__version
        result.__branch = other.__branch
        return result

    def __init__(self, branch_or_version: str):
        self.__version = None
        self.__branch = None
        self.__branch_latest_commit = None
        self.__computed_version = None

        starts_with_v = branch_or_version.startswith("v")
        is_latest = branch_or_version == "latest"
        is_valid_sym_ver = parse_version(branch_or_version) is not None

        if starts_with_v:
            self.__version = branch_or_version[1:]
        elif is_latest or is_valid_sym_ver:
            self.__version = branch_or_version
        else:
            self.__branch = branch_or_version

    def compute(self, repository: "Repository") -> None:
        if self.__branch is not None:
            if not self.__does_branch_exist(repository=repository):
                logging.warning(f"Branch {self.__branch} does not exist in repository {repository.id}. Defaulting to master.")
                self.__branch = "master"

            if self.__branch == "master":
                self.__computed_version = get_latest_version_any_platform(artifact_id_prefix=repository.id)
            else:
                self.__computed_version = "0.0.0-" + self.__branch.replace("/", "_") + "+" + (self.__get_latest_commit(repository=repository) or "unknown")
        elif self.__version is not None:
            self.__computed_version = self.__version
            if self.__version == "latest":
                self.__computed_version = get_latest_version_any_platform(artifact_id_prefix=repository.id)

        logging.debug(f"Set version for repository {repository.id} to branch={self.__branch}, version={self.__version}")

    def get_branch(self):
        if self.__branch is None:
            return "master"

        return self.__branch

    def get_version(self):
        return self.__computed_version

    def bump(self):
        self.get_version()
        if self.__computed_version is None:
            return

        parsed_version = parse_version(self.__computed_version)
        if parsed_version is None:
            return

        major, minor, patch = parsed_version
        patch += 1
        self.__computed_version = f"{major}.{minor}.{patch}"

    def __does_branch_exist(self, repository: "Repository") -> bool:
        if self.__branch == "master":
            return True

        global BRANCH_EXISTENCE_CACHE
        command = f"git ls-remote --heads {repository.url} {self.__branch}"

        if command in BRANCH_EXISTENCE_CACHE:
            return BRANCH_EXISTENCE_CACHE[command]

        result, (stdout, stderr) = run_shell_command(
            command=command,
            error_log=f"{repository.id}#check-branch#{self.__branch}.log"
        )

        if stdout is None:
            result = False
        else:
            result = len(stdout.strip()) > 0

        BRANCH_EXISTENCE_CACHE[command] = result
        return result

    def __get_latest_commit(self, repository: "Repository") -> str | None:
        if self.__branch_latest_commit is not None:
            return self.__branch_latest_commit

        result, (stdout, stderr) = run_timed_shell_command(
            context=repository.id,
            action=f"Retrieving latest commit for branch {self.__branch}",
            command=f"git ls-remote {repository.url} refs/heads/{self.__branch}",
            error_log=f"{repository.id}-latest-commit.log"
        )

        if not result:
            return None

        self.__branch_latest_commit = stdout.split(" ")[0].split("\t")[0].strip()[:7]
        return self.__branch_latest_commit

    def __str__(self):
        return self.get_version()

    def __hash__(self):
        return hash((self.__version, self.__branch))

    def __eq__(self, other):
        if not isinstance(other, RepositoryVersion):
            return False

        return self.__version == other.__version and self.__branch == other.__branch


class Result(Enum):
    SUCCESS = 0
    NO_RUN = 1
    FAILED = 2
    SKIPPED = 3


class Repository:
    repo_owner: str  # MMO-REALMS
    id: str  # core-module
    url: str  # https://github.com/MMO-REALMS/core-module
    version: RepositoryVersion
    directory: str
    tags: list[str]

    get_result: Result = Result.NO_RUN
    download_result: Result = Result.NO_RUN
    build_result: Result = Result.NO_RUN
    pull_result: Result = Result.NO_RUN

    dependencies: list["Repository"] = []

    def __init__(self, url: str, tags: list[str], version: RepositoryVersion):
        self.url = url
        self.id = self.url.split("/")[-1].replace(".git", "")
        self.version = RepositoryVersion.copy(version)
        self.tags = tags

        # noinspection HttpUrlsUsage
        self.repo_owner = (
            self.url
            .replace("https://", "")
            .replace("http://", "")
            .replace("github.com/", "")
            .replace("git.mmorealms.gg/", "")
        ).split("/")[0]

        self.directory = f"{CLONE_DIR}/{self.id}"
        self.version.compute(repository=self)
        self.dependencies = self.__find_dependencies()

    def reset_results(self):
        self.get_result = Result.NO_RUN
        self.download_result = Result.NO_RUN
        self.build_result = Result.NO_RUN
        self.pull_result = Result.NO_RUN

    def __find_dependencies(self) -> list["Repository"] | None:
        logging.debug(f"Finding dependencies for repository {self.id}...")
        data = self.__read_file_from_git("gradle.properties")

        if data is None:
            return None

        dependency_ids = []

        for line in data.splitlines():
            if not line.startswith("module_dependencies="):
                continue

            module_dependencies = line.split("=")[1].strip()
            if len(module_dependencies) == 0:
                break

            dependency_ids = module_dependencies.split(",")

        if "-module" in self.id:
            dependency_ids.append("loader")

        dependency_ids = list(set(dependency_ids))

        logging.debug(f"Found dependencies for repository {self.id}: {dependency_ids}")
        result = get_repositories_explicit(dependency_ids, [self.version] * len(dependency_ids))

        logging.debug(f"Initialized dependencies for repository {self.id}: {[str(dependency) for dependency in self.dependencies]}")
        return result

    def get(self, is_managed: bool = False, publish=True, bump_version: bool = False) -> Result:
        if self.get_result != Result.NO_RUN:
            return self.get_result

        self.get_result = self.__run_timed(
            context=self.id,
            action=f"Generating {self.id}",
            func=lambda: self.__get(is_managed=is_managed, publish=publish, bump_version=bump_version)
        )

        return self.get_result

    def __get(self, is_managed: bool = False, publish=True, bump_version: bool = False) -> Result:
        result = self.download(is_managed=is_managed, skip_dependencies=False)

        if result != Result.FAILED:
            return result

        # we want to make sure we have a clean build directory
        for file in os.listdir(get_build_dir()):
            file_path = os.path.join(self.directory, file)
            if os.path.isfile(file_path):
                os.remove(file_path)

        return self.build(is_managed=is_managed, publish=publish, bump_version=bump_version)

    def download(self, is_managed: bool, skip_dependencies: bool) -> Result:
        if REPO_UPDATE_ONLY_TAG in self.tags:
            logging.warning(f"[REPO_UPDATE_ONLY_TAG] Skipping {self.id}#download.")
            return Result.SKIPPED

        if NO_UNMANAGED_UPDATE_TAG in self.tags and not is_managed:
            logging.warning(f"[NO_UNMANAGED_UPDATE_TAG] Skipping {self.id}#download.")
            return Result.SKIPPED

        if self.download_result != Result.NO_RUN:
            return self.download_result

        self.download_result = self.__run_timed(
            context=self.id,
            action=f"Downloading",
            func=lambda: self.__download(is_managed=is_managed, skip_dependencies=skip_dependencies)
        )

        return self.download_result

    def __download(self, is_managed: bool, skip_dependencies: bool) -> Result:
        if not skip_dependencies:
            for repository in self.dependencies:
                if not self.should_recurse(repository):
                    continue
                result = repository.download(is_managed=is_managed, skip_dependencies=skip_dependencies)
                if result == Result.FAILED:
                    return result

        for platform in ["velocity", "backend-fabric", "backend-neoforge"]:
            simple_platform: str = platform
            match platform:
                case "backend-fabric":
                    simple_platform = "fabric"
                case "backend-neoforge":
                    simple_platform = "neoforge"
                case _:
                    simple_platform = platform

            if NO_VELOCITY_TAG in self.tags and platform == "velocity":
                continue

            if NO_FABRIC_TAG in self.tags and platform == "backend-fabric":
                continue

            if NO_NEOFORGE_TAG in self.tags and platform == "backend-neoforge":
                continue

            version = download_jar(
                artifact_id=f"{self.id}-{platform}",
                version=self.version.get_version(),
                target_file=f"{get_build_dir()}/{self.id}-{simple_platform}-{{version}}.jar",
            )

            if version is None:
                logging.warning(f"Failed to download {self.id} version {self.version} for platform {platform}.")
                return Result.FAILED

        return Result.SUCCESS

    def build(self, is_managed: bool, publish=True, bump_version: bool = False) -> Result:
        if self.build_result != Result.NO_RUN:
            return self.build_result

        self.build_result = self.__run_timed(
            context=self.id,
            action=f"Building {self.id}",
            func=lambda: self.__build(is_managed=is_managed, publish=publish, bump_version=bump_version)
        )

        return self.build_result

    def __build(self, is_managed: bool, publish=True, bump_version=False) -> Result:
        if REPO_UPDATE_ONLY_TAG in self.tags:
            print(f"{YELLOW}[REPO_UPDATE_ONLY_TAG] Skipping {self.id}#build.{RESET}")
            return Result.SKIPPED

        if NO_UNMANAGED_UPDATE_TAG in self.tags and not is_managed:
            logging.warning(f"[NO_UNMANAGED_UPDATE_TAG] Skipping {self.id}#download.")
            return Result.SKIPPED

        logging.debug(f"Building repository {self.id} with dependencies {self.dependencies}...")

        result = self.pull(is_managed=is_managed)

        if result == Result.FAILED:
            return self.get(is_managed=is_managed, publish=publish)

        for repository in self.dependencies:
            if not self.should_recurse(repository):
                continue
            result = repository.get(is_managed=is_managed, publish=True)

            if result == Result.FAILED:
                return result

        if bump_version:
            self.version.bump()

        result = gradle_build(
            build_name=self.id,
            working_directory=self.directory,
            publish=publish,
            environment={
                "GG_MMOREALMS_PUBLISH_VERSION": self.version.get_version(),
                "LOCAL_DEPENDENCIES": ",".join(
                    [f"{dependency.id}={dependency.version.get_version()}" for dependency in self.dependencies]),
            }
        )

        return Result.SUCCESS if result else Result.FAILED

    def pull(self, is_managed: bool) -> Result:
        if self.pull_result != Result.NO_RUN:
            return self.pull_result

        self.pull_result = self.__run_timed(
            context=self.id,
            action=f"Pulling {self.id}",
            func=lambda: self.__pull(is_managed=is_managed)
        )
        return self.pull_result

    def __pull(self, is_managed: bool) -> Result:
        if NO_UNMANAGED_UPDATE_TAG in self.tags and not is_managed:
            print(f"{YELLOW}[NO_UNMANAGED_UPDATE_TAG] Skipping {self.id}#pull.{RESET}")
            return Result.SKIPPED

        if self.version.get_branch() is None:
            print(f"{RED}Tried to pull a repository without specifying a branch.")
            return Result.FAILED

        original_cwd = os.getcwd()

        success, (_, _) = run_timed_shell_command(
            context=self.id,
            action="Cloning",
            command=f"git clone -b {self.version.get_branch()} {self.url} {self.directory} && cd {self.directory} && git submodule update --init --recursive --remote --merge",
            error_log=f"{self.id}-cloning.log"
        )

        if not success:
            os.chdir(original_cwd)
            return Result.FAILED

        os.chdir(self.directory)

        success, (stdout, stderr) = run_timed_shell_command(
            context=self.id,
            action="Getting submodule status",
            command=f"git submodule status",
            error_log=f"{self.id}#get_submodule_status.log"
        )

        if not success:
            os.chdir(original_cwd)
            return Result.FAILED

        stdout: str = stdout.strip()
        for line in stdout.splitlines():
            line = line.strip()
            commit, submodule, branch = line.strip().rsplit(" ", 2)

            submodule = submodule.strip()

            original_cwd2 = os.getcwd()
            os.chdir(submodule)
            success, (_, _) = run_timed_shell_commands(
                context=self.id,
                action=f"Updating submodule {submodule} to master",
                commands=[
                    f"git fetch",
                    f"git pull origin master",
                ],
                error_log=f"{self.id}#update_sub_module#{submodule}#master.log"
            )

            if self.version.get_branch() != "master":
                success, (_, _) = run_timed_shell_commands(
                    context=self.id,
                    action=f"Updating submodule {submodule} to {self.version.get_branch()}",
                    commands=[
                        f"git fetch",
                        f"git pull origin {self.version.get_branch()}",
                    ],
                    error_log=f"{self.id}#update_sub_module#{submodule}#{self.version.get_branch()}.log"
                )

            os.chdir(original_cwd2)

        os.chdir(original_cwd)
        return Result.SUCCESS

    def __str__(self) -> str:
        return self.id

    # ======================================== Git Methods ========================================

    def __read_file_from_git(self, file: str) -> str | None:
        branch = self.version.get_branch()
        env = self.__detect_git_env()

        if env == "gitlab":
            return self.__read_file_gitlab(file, branch)
        else:
            return self.__read_file_github(file, branch)


    def __detect_git_env(self) -> str:
        # 1. Explicit override
        override = os.environ.get("GIT_ENV", "").lower()
        if override in ("gitlab", "github"):
            return override

        # 2. CI environment variables
        if os.environ.get("GITLAB_CI") or os.environ.get("CI_SERVER_URL"):
            return "gitlab"
        if os.environ.get("GITHUB_ACTIONS") or os.environ.get("GITHUB_REPOSITORY"):
            return "github"

        # 3. Fallback: check which CLI is available
        result_glab, _ = run_shell_command("glab --version", error_log="detect_git_env.log")
        if result_glab:
            return "gitlab"

        return "github"


    def __read_file_gitlab(self, file: str, branch: str) -> str | None:
        # GitLab requires the file path to be URL-encoded (/ → %2F)
        encoded_file = file.replace("/", "%2F")
        project = f"{self.repo_owner}%2F{self.id}"

        result, (stdout, stderr) = run_shell_command(
            command=f"glab api --hostname git.mmorealms.gg projects/{project}/repository/files/{encoded_file}?ref={branch}",
            error_log=f"{self.id}#read_file_from_git#{file}.log"
        )

        if not result or stdout is None:
            return None

        try:
            data = json.loads(stdout)
        except json.JSONDecodeError:
            return None

        encoding = data.get("encoding")
        content = data.get("content")

        if not content:
            return None

        if encoding == "base64":
            return base64.b64decode(content).decode("utf-8").strip()

        return content.strip()


    def __read_file_github(self, file: str, branch: str) -> str | None:
        result, (stdout, stderr) = run_shell_command(
            command=f"gh api repos/{self.repo_owner}/{self.id}/contents/{file}?ref={branch}",
            error_log=f"{self.id}#read_file_from_git#{file}.log"
        )

        if not result or stdout is None:
            return None

        try:
            data = json.loads(stdout)
        except json.JSONDecodeError:
            return None

        download_url = data.get("download_url")
        if not download_url:
            return None

        response = requests.get(download_url)
        if response.status_code // 100 != 2:
            return None

        return response.content.decode("utf-8").strip()

    # ======================================== Utils Methods ========================================

    def __run_timed(self, context: str, action: str, func: Callable[[], Result]) -> Result:
        action = f"{action}..."
        text = f"[{context.ljust(50)}] {action.ljust(100)}"
        print(text, flush=True)

        start_time = time.time()
        result = func()
        end_time = time.time()
        duration = end_time - start_time

        print(text, end="", flush=True)
        if result == Result.SUCCESS:
            print(f"{GREEN}SUCCESS ({duration:.2f}s)\033[0m")
        elif result == Result.FAILED:
            print(f"{RED}FAILED ({duration:.2f}s)\033[0m")
        elif result == Result.SKIPPED:
            print(f"{YELLOW}SKIPPED ({duration:.2f}s)\033[0m")
        else:
            print(f"{YELLOW}UNKNOWN ({duration:.2f}s)\033[0m")

        return result

    def should_recurse(self, repository: "Repository") -> bool:
        return repository.version.get_branch() == self.version.get_branch()

    def trigger_build_action(self) -> None:
        if REPO_UPDATE_ONLY_TAG in self.tags:
            print(f"{YELLOW}[REPO_UPDATE_ONLY_TAG] Skipping {self.id}#trigger_build_action.{RESET}")
            self.is_pulled = False
            return

        self.__run_timed(
            context=self.id,
            action="Triggering build and publish action",
            func=lambda: self.__trigger_build_action(),
        )

    def __trigger_build_action(self) -> Result:
        url = f"https://api.github.com/repos/{self.repo_owner}/{self.id}/actions/workflows/build_and_publish.yml/dispatches"
        token = os.getenv("GITHUB_TOKEN")

        if not token:
            print()
            print("There was an error retrieving GitHub token.")
            print()
            return Result.FAILED

        headers = {
            "Accept": "application/vnd.github+json",
            "Authorization": f"Bearer {token}",
            "X-GitHub-Api-Version": "2022-11-28"
        }
        data = {
            "ref": "master",
        }
        response = requests.post(url, headers=headers, json=data)

        if response.status_code // 100 != 2:
            print()
            print(response.status_code)
            print(response.text)
            print()

        if response.status_code // 100 == 2:
            return Result.SUCCESS

        return Result.FAILED


def compare_repositories_ids(id1: str, id2: str) -> bool:
    id1 = id1.replace("-module", "")
    id2 = id2.replace("-module", "")

    return id1.lower() == id2.lower()


def get_repositories_explicit(ids: list[str], versions: list[RepositoryVersion]) -> list[Repository]:
    result = []

    for index, id in enumerate(ids):
        version = versions[index]
        url_and_tags: tuple[str, list[str]] = get_url_and_tags_by_id(id)

        if url_and_tags is None:
            logging.warning(f"Could not find repository with id {id}.")
            continue

        url = url_and_tags[0]
        tags = url_and_tags[1]

        versioned_repository = VersionedRepository(url, version)

        if versioned_repository in REPOSITORY_CACHE:
            repository = REPOSITORY_CACHE[versioned_repository]
            result.append(repository)
            continue

        repository = Repository(url, tags, version)
        result.append(repository)
        REPOSITORY_CACHE[versioned_repository] = repository

    return result


def get_repositories_with_branch(branch: str) -> list[Repository]:
    ids = fetch_all_ids()

    versions: list[RepositoryVersion] = []

    for _ in ids:
        versions.append(RepositoryVersion(branch))

    return get_repositories_explicit(ids, versions)


def get_repositories_unified(ids_and_versions: list[str] | None = None) -> list[Repository]:
    if ids_and_versions is None or len(ids_and_versions) == 0:
        ids_and_versions = fetch_all_ids()

    ids: list[str] = []
    versions: list[RepositoryVersion] = []

    for id_and_version in ids_and_versions:
        split_result = id_and_version.split(":", 1)

        id = split_result[0]
        branch_or_version: str = split_result[1] if len(split_result) > 1 else "master"

        ids.append(id)
        versions.append(RepositoryVersion(branch_or_version))

    return get_repositories_explicit(ids, versions)


def get_url_and_tags_by_id(id: str) -> tuple[str, list[str]] | None:
    lines = read_managed_mods().splitlines()
    lines = [line.strip() for line in lines if line.strip() and not line.startswith("#")]
    lines.sort()

    searched_ids = []

    for index, line in enumerate(lines):
        if line.startswith("#") or not line.strip():
            continue

        url: str = line.split(" ")[0].strip()
        repository_id = url.split("/")[-1].replace(".git", "")
        tags: list[str] = line.split(" ")[1:]

        if compare_repositories_ids(repository_id, id):
            return url, tags

        searched_ids.append(repository_id)

    logging.debug(f"Failed to find repository id {id}. Searched ids: {searched_ids}")
    return None


def fetch_all_ids() -> list[str]:
    lines = read_managed_mods().splitlines()
    lines = [line.strip() for line in lines if line.strip() and not line.startswith("#")]
    lines.sort()

    ids = []

    for index, line in enumerate(lines):
        if line.startswith("#") or not line.strip():
            continue

        url: str = line.split(" ")[0].strip()
        repository_id = url.split("/")[-1].replace(".git", "")
        ids.append(repository_id)

    return ids


