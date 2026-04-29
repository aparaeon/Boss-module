import os
from typing import Callable

from utils.git import Result, Repository, get_repositories_unified, fetch_all_ids
from utils.utils import copy_build_libs, get_build_dir, write

SCRIPT_DIR: str = os.path.dirname(os.path.abspath(__file__))
TARGET_PATH = f"{SCRIPT_DIR}/../../run"


def get_ids_from_args(args: str) -> list[str] | None:
    return None if args == "*" else [repository.strip() for repository in args.split(",") if repository.strip()]


def download(args: str = "*") -> None:
    execute(args=args, operation=lambda repository, is_managed: repository.download(is_managed=is_managed, skip_dependencies=(args == "*")))


def get(args: str = "*") -> None:
    execute(args=args, operation=lambda repository, is_managed: repository.get(is_managed=is_managed, bump_version=False, publish=True))

def get_branch(args: str) -> None:
    if not args or args == "*":
        print("Error: --get-branch requires a branch name argument (e.g., --get-branch=feature/new-combat)")
        return

    target_branch = args.strip()
    all_ids = fetch_all_ids()

    unified_args = ",".join([f"{repo_id}:{target_branch}" for repo_id in all_ids])

    def get_if_branch_matches(repository: Repository, is_managed: bool) -> Result:
        if target_branch != "master" and repository.version.get_branch() == "master":
            print(f"Skipping {repository.id}: Branch '{target_branch}' not found (defaulted to master).")
            return Result.SKIPPED

        return repository.get(is_managed=is_managed, bump_version=False, publish=True)

    execute(args=unified_args, operation=get_if_branch_matches)


def build(args: str = "*") -> None:
    execute(args=args, operation=lambda repository, is_managed: repository.build(is_managed=is_managed, bump_version=False, publish=True))


def build_and_bump(args: str = "*") -> None:
    execute(args=args, operation=lambda repository, is_managed: repository.build(is_managed=is_managed, bump_version=True, publish=True))


def execute(args: str, operation: Callable[[Repository, bool], Result]) -> None:
    if args == "":
        args = "*"

    ids = get_ids_from_args(args)
    is_managed = args != "*"
    print("[DEBUG] Args: " + args + ", is_managed: " + str(is_managed) + ", ids: " + str(ids))

    repositories = get_repositories_unified(ids)
    print_tree(repositories)

    for repository in repositories:
        result = operation(repository, is_managed)

        if result == Result.FAILED:
            exit(2)

        write(f"{SCRIPT_DIR}/../../versions/{repository.id}.version", repository.version.get_version() or "unknown")

    copy_build_libs(get_build_dir(), TARGET_PATH)


def print_tree(repositories: list[Repository], level: int = 0, prefix: str = "", is_last: bool = True) -> None:
    for idx, repository in enumerate(repositories):
        is_last_item = idx == len(repositories) - 1
        connector = "└─" if is_last_item else "├─"

        print(f"{prefix}{connector} {repository.id} (version: {repository.version.get_version() or 'unknown'})")

        extension = "   " if is_last_item else "│  "
        if repository.dependencies == None:
            print(f"[ERROR] Dependencies for repository {repository} are null")
        print_tree([repo for repo in repository.dependencies if repository.should_recurse(repo)], level + 1, prefix + extension, is_last_item)
