import logging
import os.path

import requests
from requests.auth import HTTPBasicAuth

from utils import settings
from utils.gradle import get_credentials
from utils.utils import run_timed

LATEST_VERSION_CACHE = {}


def get_latest_version_any_platform(
        artifact_id_prefix: str,
        username: str = None,
        password: str = None,
        base_url: str = settings.NEXUS_BASE_URL,
        group_id: str = "gg.mmorealms",
        repository: str = "maven-releases"
) -> str:
    return get_latest_version(
        artifact_id=f"{artifact_id_prefix}-*",
        username=username,
        password=password,
        base_url=base_url,
        group_id=group_id,
        repository=repository,
    )

def get_latest_version(
        artifact_id: str,
        username: str = None,
        password: str = None,
        base_url: str = settings.NEXUS_BASE_URL,
        group_id: str = "gg.mmorealms",
        repository: str = "maven-releases"
) -> str:
    global LATEST_VERSION_CACHE

    if username is None or password is None:
        username, password = get_credentials()

    search_urls = []
    codes = []

    for lead in range(10):
        search_urls.append(f"{base_url}/service/rest/v1/search/assets/download?repository={repository}&group={group_id}&name={artifact_id}&sort=version&direction=desc&version={lead}.*.*&maven.extension=pom")

    for search_url in search_urls:
        if search_url in LATEST_VERSION_CACHE:
            cached = LATEST_VERSION_CACHE[search_url]
            if "-" not in cached and "+" not in cached:
                return cached
            continue

        response = requests.get(search_url, auth=HTTPBasicAuth(username, password) if username else None)
        if response.status_code / 100 != 2:
            codes.append(response.status_code)
            continue

        version = response.text.split("<version>")[1].split("</version>")[0]

        # Skip branch/pre-release versions (e.g. 0.0.0-feature_chat-games+f83a7dd)
        if "-" in version or "+" in version:
            continue

        LATEST_VERSION_CACHE[search_url] = version
        return version

    logging.warning(f"Could not fetch latest version for {artifact_id}, status codes {codes}. Defaulting to 1.0.0")
    return "1.0.0"


def __download_jar(
        artifact_id: str,
        version: str,
        target_file: str,
        username: str = None,
        password: str = None,
        base_url: str = settings.NEXUS_BASE_URL,
        group_id: str = "gg.mmorealms",
        repository: str = "maven-releases"
) -> tuple[bool, str | None]:
    target_file = os.path.realpath(target_file)

    if version == "latest":
        try:
            version = get_latest_version(
                base_url=base_url,
                username=username,
                password=password,
                group_id=group_id,
                artifact_id=artifact_id,
            )
        except Exception as _:
            return False, None

    target_file = target_file.replace("{version}", version)

    if username is None or password is None:
        username, password = get_credentials()

    jar_path = f"{group_id.replace('.', '/')}/{artifact_id}/{version}/{artifact_id}-{version}.jar"
    jar_url = f"{base_url}/repository/{repository}/{jar_path}"

    response = requests.get(jar_url, auth=HTTPBasicAuth(username, password) if username else None)
    if response.status_code / 100 != 2:
        return False, None

    with open(target_file, "wb") as file:
        file.write(response.content)

    return True, version


def download_jar(
        artifact_id: str,
        version: str,
        target_file: str,
        username: str = None,
        password: str = None,
        base_url: str = settings.NEXUS_BASE_URL,
        group_id: str = "gg.mmorealms",
        repository: str = "maven-releases"
) -> str | None:
    result, version = run_timed(
        context=artifact_id,
        action=f"Downloading v{version}",
        func=lambda: __download_jar(
            artifact_id=artifact_id,
            version=version,
            target_file=target_file,
            username=username,
            password=password,
            base_url=base_url,
            group_id=group_id,
            repository=repository
        ),
    )

    if not result:
        return None

    return version
