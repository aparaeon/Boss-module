import os
import re
from concurrent.futures import ThreadPoolExecutor, as_completed

import requests
from requests.auth import HTTPBasicAuth

home_dir = os.path.expanduser('~')
INTERNAL_LIBS_PATH = "src/main/kotlin/utils/InternalLibs.kt"
VERSIONS_FILE = "internal-libs.versions"

VERSION_PATTERN = re.compile(r'^\d+\.\d+\.\d+-\w+-(\d+)$')
# Longest suffixes must come first — "backend-common" must match before "common" so that
# "trade-module-backend-common" strips to "trade-module", not "trade-module-backend".
PLATFORMS = ["backend-common", "backend-fabric", "common", "velocity"]  # TODO add backend-neoforge when enabled
MAX_WORKERS = 20

# Module IDs listed here will never be added by auto-discovery.
# Existing entries already in the file are always kept regardless of this list.
EXCLUDED_MODULE_IDS: set[str] = {
    "lobby",
    "lobby-standalone"
}


def extract_build_count(version: str) -> int:
    match = VERSION_PATTERN.match(version)
    return int(match.group(1)) if match else -1


def get_min_version(versions: list[str]) -> str:
    return min(versions, key=extract_build_count)


def search_all_pages(url: str, auth) -> list[dict]:
    """Paginate through all Nexus search results and return every item."""
    items = []
    continuation_token = None
    while True:
        paged_url = f"{url}&continuationToken={continuation_token}" if continuation_token else url
        response = requests.get(paged_url, auth=auth)
        response.raise_for_status()
        data = response.json()
        items.extend(data.get('items', []))
        continuation_token = data.get('continuationToken')
        if not continuation_token:
            break
    return items


def get_latest_version(artifact_id: str, username: str, password: str, base_url: str = "https://repo.mmorealms.gg",
                       group_id: str = "gg.mmorealms", repository: str = "maven-releases") -> str:
    auth = HTTPBasicAuth(username, password) if username else None
    search_url = (f"{base_url}/service/rest/v1/search"
                  f"?repository={repository}&group={group_id}&name={artifact_id}"
                  f"&sort=version&direction=desc")

    items = search_all_pages(search_url, auth)

    if not items:
        raise Exception(f"No artifact versions found for {group_id}:{artifact_id}")

    matching = [item['version'] for item in items if VERSION_PATTERN.match(item['version'])]
    if not matching:
        raise Exception(f"No versions matching expected format found for {group_id}:{artifact_id}")

    return max(matching, key=extract_build_count)


def get_all_module_ids(username: str, password: str, base_url: str = "https://repo.mmorealms.gg",
                       group_id: str = "gg.mmorealms", repository: str = "maven-releases") -> set[str]:
    """Discover all base module IDs that have at least one platform artifact with a matching version."""
    auth = HTTPBasicAuth(username, password) if username else None
    search_url = (f"{base_url}/service/rest/v1/search"
                  f"?repository={repository}&group={group_id}")

    module_ids = set()
    for item in search_all_pages(search_url, auth):
        if not VERSION_PATTERN.match(item['version']):
            continue
        for platform in PLATFORMS:
            if item['name'].endswith(f"-{platform}"):
                module_ids.add(item['name'][: -len(f"-{platform}")])
                break

    return module_ids


def resolve_versions_parallel(module_ids: list[str], username: str,
                              password: str) -> dict[str, str | None]:
    """
    Fetch the latest version for every module×platform combination in parallel.
    All futures are submitted at once so the full batch is in-flight simultaneously.
    Returns {module_id: min_version_across_platforms | None}.
    """
    platform_versions: dict[str, list[str]] = {mid: [] for mid in module_ids}
    futures: dict = {}

    with ThreadPoolExecutor(max_workers=MAX_WORKERS) as executor:
        for module_id in module_ids:
            for platform in PLATFORMS:
                future = executor.submit(get_latest_version, f"{module_id}-{platform}", username, password)
                futures[future] = (module_id, platform)

        for future in as_completed(futures):
            module_id, platform = futures[future]
            try:
                platform_versions[module_id].append(future.result())
            except Exception as e:
                print(f"Failed to get latest version for {module_id}-{platform}: {e}")

    return {
        mid: get_min_version(versions) if versions else None
        for mid, versions in platform_versions.items()
    }


def detect_alignment(lib_lines: list[str]) -> tuple[int, int]:
    """
    Detect column widths from existing InternalLib lines.

    Returns:
        val_col:  width of the "val name: ..." portion up to (not including) "InternalLib"
        base_col: width of the '"gg.mmorealms:...", ' portion up to (not including) "version"

    Example line:
        '    val store:            InternalLib = InternalLib(base = "gg.mmorealms:store-module",             version = "...")'
        val_col  = pos("InternalLib") - pos("val ") = 22
        base_col = len('"gg.mmorealms:tebex-integration-module",') = 40  (max across all lines)
    """
    val_cols, base_cols = [], []
    for line in lib_lines:
        if line.count("InternalLib") != 2:
            continue
        val_pos = line.find("val ")
        lib_pos = line.find("InternalLib")
        if val_pos >= 0 and lib_pos > val_pos:
            val_cols.append(lib_pos - val_pos)
        m = re.search(r'"gg\.mmorealms:[^"]+",', line)
        if m:
            base_cols.append(len(m.group(0)))
    return max(val_cols, default=22), max(base_cols, default=40)


def format_lib_line(indent: str, val_name: str, module_id: str, version: str,
                    val_col: int, base_col: int) -> str:
    val_part  = f"val {val_name}:"
    base_part = f'"gg.mmorealms:{module_id}",'
    return (f'{indent}'
            f'{val_part:<{val_col}}'
            f'InternalLib = InternalLib(base = '
            f'{base_part:<{base_col}} '
            f'version = "{version}")')


def replace_version_in_line(line: str, new_version: str) -> str:
    return re.sub(r'(version\s*=\s*)"[^"]*"', rf'\1"{new_version}"', line, count=1)


def derive_val_name(module_id: str) -> str:
    """Convert a module ID like 'some-special-module' to a Kotlin val name like 'someSpecial'."""
    name = module_id.removesuffix("-module")
    parts = name.split("-")
    return parts[0] + "".join(p.capitalize() for p in parts[1:])


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
    id: str
    version: str

    def find(self, source: str, target: str) -> str:
        part = source.split(target)[1]
        match = re.search(r'"([^"]+)"', part)
        if not match:
            raise ValueError(f"Could not find quoted value after '{target}' in: {source}")
        return match.group(1).replace("gg.mmorealms:", "").strip()

    def __init__(self, line: str):
        self.id = self.find(line, "base")
        self.version = self.find(line, "version")


def find_libraries(lines: list[str]) -> list[Library]:
    libraries = []
    for line in lines:
        if line.count("InternalLib") == 2:
            libraries.append(Library(line))
    return libraries


def write_versions_file(versions: dict[str, str]):
    header = [
        "# Auto-generated by scripts/update_internal_libs.py.",
        "# Canonical versions for gg.mmorealms internal libraries.",
        "# Read at Gradle configuration time; editing this file does NOT",
        "# recompile buildSrc (it is outside the buildSrc source set).",
        "# Per-developer overrides in the local.dependencies file still win.",
        "# Format: <module-id>=<version>   (module-id = base artifact name)",
    ]
    body = [f"{mid}={versions[mid]}" for mid in sorted(versions)]
    write(VERSIONS_FILE, "\n".join(header + body) + "\n")


def versions_file_key(module_id: str) -> str:
    return module_id.removesuffix("-module")


def main():
    gradle_properties = read_gradle_properties()

    username = gradle_properties.get("gg.mmorealms.username") or os.getenv("GG_MMOREALMS_USERNAME")
    password = gradle_properties.get("gg.mmorealms.password") or os.getenv("GG_MMOREALMS_PASSWORD")

    data_lines = read(INTERNAL_LIBS_PATH).splitlines()
    libraries = find_libraries(data_lines)
    known_ids = {lib.id for lib in libraries}

    # Step 1: discover all modules — sequential, pages depend on continuation tokens
    print("Discovering modules...")
    all_module_ids = get_all_module_ids(username=username, password=password)
    new_ids = sorted(all_module_ids - known_ids - EXCLUDED_MODULE_IDS)

    # Step 2: resolve versions for all modules (existing + new) in one parallel batch
    all_ids = [lib.id for lib in libraries] + new_ids
    print(f"Resolving versions for {len(all_ids)} modules × {len(PLATFORMS)} platforms "
          f"({len(all_ids) * len(PLATFORMS)} requests, {MAX_WORKERS} workers)...")
    version_map = resolve_versions_parallel(all_ids, username, password)

    # Step 3: write canonical versions to the external versions file.
    # This file is read at Gradle configuration time (outside the buildSrc
    # source set), so editing it does NOT recompile buildSrc. It is the
    # source of truth consumed at runtime by InternalLib.version().
    versions_to_write: dict[str, str] = {}
    for module_id in all_ids:
        latest_version = version_map.get(module_id)
        if latest_version is None:
            print(f"Failed to find any versions for {module_id}, skipping...")
            continue

        prev = next((lib.version for lib in libraries if lib.id == module_id), None)
        if module_id in known_ids:
            if latest_version != prev:
                print(f"[UPDATING] {module_id} {prev} -> {latest_version} ({VERSIONS_FILE})")
            else:
                print(f"[UP TO DATE] {module_id} {latest_version}")
        else:
            print(f"[DISCOVERED] {module_id} at {latest_version}")

        versions_to_write[versions_file_key(module_id)] = latest_version

    write_versions_file(versions_to_write)
    print(f"Wrote {len(versions_to_write)} entries to {VERSIONS_FILE}")

    # Step 4: rewrite the version literals in InternalLibs.kt to match the latest
    # resolved versions, and append any newly discovered modules. Only libs whose
    # current version is in the auto-managed format are touched; deprecated or
    # manually-pinned versions (e.g. "1.0.38", "1.0.2") are left alone.
    lib_indices = [i for i, line in enumerate(data_lines) if line.count("InternalLib") == 2]

    bumped = 0
    for lib, idx in zip(libraries, lib_indices):
        latest_version = version_map.get(lib.id)
        if latest_version is None:
            continue
        if not VERSION_PATTERN.match(lib.version):
            continue
        if latest_version == lib.version:
            continue
        data_lines[idx] = replace_version_in_line(data_lines[idx], latest_version)
        print(f"[UPDATING .kt] {lib.id} {lib.version} -> {latest_version}")
        bumped += 1

    added = 0
    if new_ids:
        indent = "    "
        for line in data_lines:
            if line.count("InternalLib") == 2:
                indent = line[: len(line) - len(line.lstrip())]
                break

        val_col, base_col = detect_alignment(data_lines)
        insert_after = lib_indices[-1] if lib_indices else len(data_lines) - 1

        lines_to_insert = []
        for module_id in new_ids:
            latest_version = version_map.get(module_id)
            if latest_version is None:
                print(f"No matching versions found for newly discovered {module_id}, skipping...")
                continue

            val_name = derive_val_name(module_id)
            new_line = format_lib_line(indent, val_name, module_id, latest_version, val_col, base_col)
            lines_to_insert.append(new_line)

        for i, line in enumerate(lines_to_insert):
            data_lines.insert(insert_after + 1 + i, line)
        added = len(lines_to_insert)

    if bumped or added:
        write(INTERNAL_LIBS_PATH, "\n".join(data_lines))
        print(f"Updated {bumped} version literal(s) and added {added} new module(s) to {INTERNAL_LIBS_PATH}")
    else:
        print(f"InternalLibs.kt unchanged (all version literals up to date, no new modules).")


if __name__ == '__main__':
    main()