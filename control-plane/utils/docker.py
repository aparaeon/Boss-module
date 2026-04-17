import base64
import json
import os
import shutil
import sys
from pathlib import Path
from typing import Any, Dict, List, Optional, Tuple
from packaging.version import Version, InvalidVersion

import requests

from utils import settings
from utils.utils import increment_version, write, run_shell_command, handle_remove_readonly

SCRIPT_DIR: str = os.path.dirname(os.path.abspath(__file__))
DEFAULT_DOCKER_FILE: str = "Dockerfile"


def check_docker_installed() -> None:
    success, _ = run_shell_command("docker --version")

    if not success:
        print(
            "Docker does not appear to be installed. If it it make sure it is added in the PATH env variable for the command `docker` to be recognized")
        sys.exit(1)


def load_docker_config() -> Dict[str, Any]:
    docker_config_path = Path.home() / ".docker" / "config.json"
    if not docker_config_path.exists():
        raise FileNotFoundError(f"Docker config file not found: {docker_config_path}")
    with docker_config_path.open("r", encoding="utf-8") as f:
        return json.load(f)


def get_credentials_from_auths(registry: str, config: Dict[str, Any]) -> Optional[Tuple[str, str]]:
    auths = config.get("auths", {})
    if registry in auths and "auth" in auths[registry]:
        encoded_auth = auths[registry]["auth"]
        try:
            decoded_auth = base64.b64decode(encoded_auth).decode("utf-8")
            if ":" in decoded_auth:
                username, password = decoded_auth.split(":", 1)
                return username, password
        except Exception as e:
            print(f"Error decoding auth for {registry}: {e}")
    return None


def get_credentials_from_creds_store(registry: str, config: Dict[str, Any]) -> Optional[Tuple[str, str]]:
    creds_store = config.get("credsStore")
    if not creds_store:
        return None

    command = f"docker-credential-{creds_store} get"

    success, (stdout, stderr) = run_shell_command(
        command=command,
        input=registry
    )

    if not success:
        print(f"{command=}")
        print(f"{registry=}")
        print("\n\n\n")
        print(stdout)
        print("\n\n\n")
        print(stderr)
        print(f"Credential store command failed")
        exit(1)

    cred_data = json.loads(stdout)
    username = cred_data.get("Username")
    password = cred_data.get("Secret")
    if username and password:
        return username, password

    return None


def get_credentials(registry: str, config: Dict[str, Any]) -> Tuple[str, str]:
    if os.getenv("DOCKER_USERNAME") and os.getenv("DOCKER_PASSWORD"):
        return os.getenv("DOCKER_USERNAME"), os.getenv("DOCKER_PASSWORD")

    creds = get_credentials_from_auths(registry, config)
    if creds:
        return creds
    creds = get_credentials_from_creds_store(registry, config)
    if creds:
        return creds
    raise RuntimeError(f"No credentials found for {registry} in Docker config.")


def fetch_tags(registry: str, repository: str, username: str, password: str) -> List[str] | None:
    url = f"https://{registry}/v2/{repository}/tags/list"
    response = requests.get(url, auth=(username, password))
    if response.status_code % 100 == 2:
        return None

    data = response.json()
    return data.get("tags", [])


def get_latest_tag(images: list[str], registry: str = settings.DOCKER_REGISTRY) -> Optional[str]:
    config = load_docker_config()
    username, password = get_credentials(registry, config)
    tags: list[list[str]] = []

    for image in images:
        image_tags = fetch_tags(registry, image, username, password)
        if image_tags is None:
            return "0.0.1"
        tags.append(image_tags)

    if not tags:
        return None

    common_tags = set(tags[0])
    for tag_list in tags[1:]:
        common_tags &= set(tag_list)

    if not common_tags:
        return None

    valid_versions = []
    for tag in common_tags:
        try:
            valid_versions.append((Version(tag), tag))
        except InvalidVersion:
            pass

    if not valid_versions:
        return None

    valid_versions.sort(key=lambda x: x[0])
    return valid_versions[-1][1]


def compose_docker_image(image: str, version: str, registry: str = settings.DOCKER_REGISTRY):
    result = f"{image}:{version}"

    if registry is not None:
        result = f"{registry}/{result}"

    return result


def pull_image(image: str, version: str, registry: str = settings.DOCKER_REGISTRY) -> None:
    print(f"Pulling image: {image}")
    run_shell_command(f"docker pull {compose_docker_image(image, version, registry)}")


def export_container(container_id: str, output_tar: str) -> None:
    run_shell_command(f"docker export {container_id} -o {output_tar}")


def extract_app_contents(tar_file: str, extract_dir: str) -> None:
    run_shell_command(f"tar -xf {tar_file} --strip-components=1 -C {extract_dir} app")
    if not os.listdir(extract_dir):
        run_shell_command(f"tar -xf {tar_file} --strip-components=1 -C {extract_dir} app.orig")


def create_container(image: str, version: str, registry: str = settings.DOCKER_REGISTRY) -> str:
    success, (stdout, stderr) = run_shell_command(f"docker create {compose_docker_image(image, version, registry)}")
    return stdout.strip()


def remove_container(container_id: str) -> None:
    run_shell_command(f"docker rm {container_id}")


def extract_app_from_image(
        image: str,
        registry: str = settings.DOCKER_REGISTRY,
        version: str = None,
        directory: str = None
) -> str:
    check_docker_installed()

    if version is None or version == "latest":
        version = get_latest_tag(images=[image], registry=registry)

    sanitized_image_name = f"{registry}_{image}_{version}".replace("/", "_")
    export_tar = f"{sanitized_image_name}.tar"
    extract_dir = directory if directory is not None else sanitized_image_name

    # Image prep
    pull_image(image=image, version=version, registry=registry)
    container_id = create_container(image=image, version=version, registry=registry)
    export_container(container_id, export_tar)

    # Extraction
    shutil.rmtree(extract_dir, ignore_errors=True)
    os.makedirs(extract_dir, exist_ok=True)
    extract_app_contents(export_tar, extract_dir)

    # Cleanup
    os.remove(export_tar)
    remove_container(container_id)

    return extract_dir


def build_image(
        image: str,
        version: str,
        build_dir: str,
        registry: str = settings.DOCKER_REGISTRY,
        docker_file: str = DEFAULT_DOCKER_FILE
) -> str:
    if not os.path.isdir(build_dir):
        raise ValueError(f"The build directory '{build_dir}' does not exist or is not a directory.")

    version_file_path = os.path.join(build_dir, "version.txt")

    if version is None or version == "latest":
        version = get_latest_tag(images=[image], registry=registry)
        version = increment_version(version)

    write(version_file_path, version)

    print(f"Building {image}:{version} for {registry} in directory: {build_dir}")
    run_shell_command(
        command=f"docker build -f {docker_file} -t {compose_docker_image(image, version, registry)} .",
        working_directory=build_dir
    )

    return version


def push_image(image: str, version: str, registry: str = settings.DOCKER_REGISTRY) -> None:
    print(f"Pushing {image}:{version} to {registry}")

    attempt = 1

    while attempt <= 3:
        success, (_, _) = run_shell_command(f"docker push {compose_docker_image(image, version, registry)}", supress_error_log=attempt!=3)

        if success:
            return

        print(f"Attempt {attempt} failed. Retrying...")
        attempt += 1

    raise RuntimeError(f"Failed to push {image}:{version} to {registry} after 3 attempts.")


# def build_and_push(
#         directory: str,
#         image: str,
#         registry: str = settings.DOCKER_REGISTRY,
#         version=None,
#         delete_build_dir: bool = True,
#         docker_file: str = DEFAULT_DOCKER_FILE
# ) -> Tuple[str, str]:
#     build_image(registry=registry, image=image, version=version, build_dir=directory, docker_file=docker_file)
#     push_image(registry=registry, image=image, version=version)
#
#     if delete_build_dir:
#         shutil.rmtree(directory, onerror=handle_remove_readonly)
#
#     return image, version


def docker_compose_up(docker_compose_file: str) -> None:
    if not os.path.isfile(docker_compose_file):
        raise ValueError(f"The docker-compose file '{docker_compose_file}' does not exist or is not a file.")

    docker_compose_down(docker_compose_file)

    run_shell_command(
        command="docker-compose up -d --build --force-recreate --remove-orphans",
        working_directory=os.path.dirname(docker_compose_file)
    )


def docker_compose_down(docker_compose_file: str) -> None:
    if not os.path.isfile(docker_compose_file):
        raise ValueError(f"The docker-compose file '{docker_compose_file}' does not exist or is not a file.")

    run_shell_command(
        command="docker-compose down --remove-orphans",
        working_directory=os.path.dirname(docker_compose_file)
    )
