import os

import requests
import re
from packaging.version import Version
from requests.auth import HTTPBasicAuth

home_dir = os.path.expanduser('~')
INTERNAL_LIBS_PATH = "src/main/kotlin/utils/InternalLibs.kt"


def get_latest_version(artifact_id: str, username: str, password: str, base_url: str = "https://repo.mmorealms.gg",
                       group_id: str = "gg.mmorealms", repository: str = "maven-releases"):
    search_url = f"{base_url}/service/rest/v1/search?repository={repository}&group={group_id}&name={artifact_id}&sort=version&direction=desc"
    response = requests.get(search_url, auth=HTTPBasicAuth(username, password) if username else None)
    response.raise_for_status()

    data = response.json()
    if not data['items']:
        raise Exception(f"No artifact versions found for {group_id}:{artifact_id}")

    semver_pattern = re.compile(r'^\d+\.\d+\.\d+$')
    for item in data['items']:
        version = item['version']
        if semver_pattern.match(version):
            return version


def read_gradle_properties():
    properties = {}
    gradle_properties_path = os.path.join(home_dir, '.gradle', 'gradle.properties')
    if os.path.exists(gradle_properties_path):
        with open(gradle_properties_path, 'r') as f:
            for line in f:
                if '=' in line:
                    key, value = line.split('=', 1)
                    properties[key.strip()] = value.strip()
    return properties


def write(path: str, content: str):
    with open(path, "w") as f:
        f.write(content)


def read(path: str) -> str:
    with open(path, "r") as f:
        return f.read()


class Library:
    original_line: str
    new_line: str
    id: str
    version: str

    def find(self, source: str, target: str) -> str:
        return (
            source.split(target)[1].strip()
            .split("=")[1].strip()
            .split(",")[0].strip()

            .replace("\"", "")
            .replace("gg.mmorealms:", "")
            .replace(")", "")
            .replace("}", "")
            .strip()
        )

    def __init__(self, line: str):
        self.original_line = line
        self.new_line = line
        self.id = self.find(line, "base")
        self.version = self.find(line, "version")

    def update(self, version: str):
        print(f"[UPDATING] {self.id} from {self.version} to {version}")
        self.new_line = self.original_line.replace(f'version = "{self.version}"', f'version = "{version}"')
        self.version = version


def find_libraries(lines: list[str]) -> list[Library]:
    libraries = []

    for line in lines:
        if line.count("InternalLib") == 2:
            libraries.append(Library(line))

    return libraries


def get_min_version(versions: list[str]) -> str:
    return str(min(versions, key=Version))


def main():
    gradle_properties = read_gradle_properties()

    username = gradle_properties.get("gg.mmorealms.username") or os.getenv("GG_MMOREALMS_USERNAME")
    password = gradle_properties.get("gg.mmorealms.password") or os.getenv("GG_MMOREALMS_PASSWORD")

    data_lines = read(INTERNAL_LIBS_PATH).splitlines()
    libraries = find_libraries(data_lines)

    for library in libraries:
        latest_version = "1000.0.0"
        for platform in ["common", "backend-common", "backend-fabric",
                         "velocity"]:  # TODO add backend-neoforge when enabled
            try:
                latest_version = get_min_version([
                    latest_version,
                    get_latest_version(
                        username=username,
                        password=password,
                        artifact_id=f"{library.id}-{platform}",
                    )
                ])
            except Exception as e:
                print(f"Failed to get latest version for {library.id}: {e}")

        if latest_version == "1000.0.0":
            print(f"Failed to find any versions for {library.id}, skipping...")
            continue

        if latest_version != library.version:
            library.update(latest_version)
        else:
            print(f"[UP TO DATE] Latest version for {library.id} is already {library.version}")

    new_lines = []

    for line in data_lines:

        for library in libraries:
            if line == library.original_line:
                line = library.new_line

        new_lines.append(line)

    write(INTERNAL_LIBS_PATH, "\n".join(new_lines))


if __name__ == '__main__':
    main()
