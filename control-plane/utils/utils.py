import hashlib
import logging
import os.path
import paramiko
import re
import shutil
import stat
import subprocess
import threading
import time
from packaging.version import Version
from typing import Tuple, Callable, Optional

from utils.colors import RED, GREEN, YELLOW, RESET


def handle_remove_readonly(func, path, exc_info) -> None:
    os.chmod(path, stat.S_IWRITE)
    func(path)


def reset_directory(path: str) -> str:
    if os.path.exists(path):
        shutil.rmtree(path, onerror=handle_remove_readonly)
    os.makedirs(path)
    return os.path.abspath(path)


HOME_DIR = os.path.expanduser('~')
SCRIPT_DIR: str = os.path.dirname(os.path.abspath(__file__))
BUILD_DIR: str = reset_directory(f"{SCRIPT_DIR}/../build")
ERROR_DIR: str = reset_directory(f"{SCRIPT_DIR}/../errors")


def clear_terminal() -> None:
    os.system('cls' if os.name == 'nt' else 'clear')


def run_timed(context: str, action: str, func: Callable[[], Tuple[bool, any]]) -> Tuple[bool, any]:
    action = f"{action}..."
    text = f"[{context.ljust(50)}] {action.ljust(100)}"
    print(text, flush=True)

    start_time = time.time()
    success, result = func()
    end_time = time.time()
    duration = end_time - start_time

    print(text, end="", flush=True)
    if success:
        print(f"{GREEN}SUCCESS ({duration:.2f}s)\033[0m")
    else:
        print(f"{RED}FAILED ({duration:.2f}s)\033[0m")

    return success, result


def run_timed_shell_commands(
        context: str,
        action: str,
        commands: list[str],
        error_log: str = None,
        environment: dict | None = None,
) -> Tuple[bool, Tuple[any, any]]:
    for command in commands:
        success, (stdout, stderr) = run_timed_shell_command(
            context=context,
            action=action,
            command=command,
            error_log=error_log,
            environment=environment
        )

        if not success:
            return False, (stdout, stderr)

    return True, (None, None)


def run_timed_shell_command(
        context: str,
        action: str,
        command: str,
        error_log: str = None,
        environment: dict | None = None,
) -> Tuple[bool, Tuple[any, any]]:
    success, (stdout, stderr) = run_timed(
        context=context,
        action=action,
        func=lambda: run_shell_command(
            command=command,
            environment=environment,
            error_log=error_log
        )
    )

    return success, (stdout, stderr)


def read_stream(stream, store):
    while True:
        line = stream.readline()
        if not line:
            break
        if logging.getLogger().getEffectiveLevel() == logging.DEBUG:
            print(line.decode(), end='', flush=True)
        store.append(line)


def run_shell_command(
        command: str,
        input=None,
        environment: dict | None = None,
        working_directory: str | None = None,
        error_log: str = None,
        supress_error_log: bool = False
) -> Tuple[bool, tuple[str | None, str | None]]:
    if working_directory is None:
        working_directory = os.getcwd()

    logging.debug("")
    logging.debug(f"Executing command: {command} in directory: {working_directory}")

    env = os.environ.copy()
    if environment:
        env.update(environment)

    env = {k: v for k, v in env.items() if v is not None}

    bad_env = {k: v for k, v in env.items() if v is None}
    if bad_env:
        logging.error("Environment contains None values: %r", bad_env)

    process = subprocess.Popen(
        command,
        shell=True,
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        cwd=working_directory,
        env=env,
    )

    if input is not None:
        process.stdin.write(input.encode("utf-8"))
        process.stdin.close()

    stdout_data = []
    stderr_data = []

    stdout_thread = threading.Thread(target=read_stream, args=(process.stdout, stdout_data))
    stderr_thread = threading.Thread(target=read_stream, args=(process.stderr, stderr_data))

    stdout_thread.start()
    stderr_thread.start()

    stdout_thread.join()
    stderr_thread.join()

    process.wait()

    captured_stdout = b''.join(stdout_data).decode()
    captured_stderr = b''.join(stderr_data).decode()

    success = process.returncode == 0

    if not success:
        log = f"""
Process exited with code {process.returncode} (Success? {success})

Command: {command}

Standard Output:
{captured_stdout}

Standard Error:
{captured_stderr}
""".strip()

        if error_log is None:
            if not supress_error_log:
                print(log)
        else:
            error_log = os.path.expanduser(f"{get_error_log_directory()}/{error_log}")
            print(f"{YELLOW}Saved error log in {error_log}{RESET}")
            write(error_log, log)

    return success, (captured_stdout, captured_stderr)


def write(path: str, content: str):
    parent_dir = os.path.dirname(path)
    if parent_dir and not os.path.exists(parent_dir):
        os.makedirs(parent_dir)
    with open(path, "w") as f:
        f.write(content)


def read(path: str) -> str:
    if not os.path.isfile(path):
        return ""

    with open(path, "r") as f:
        return f.read()


def get_jar_metadata(jar_name: str) -> Tuple[str, str, str]:
    name = jar_name.removesuffix(".jar")

    base = platform = version = "unknown"

    for key in ("fabric", "neoforge", "velocity"):
        needle = f"-{key}-"
        if needle in name:
            base, version = name.split(needle, 1)
            platform = key
            break

    return base, platform, version


def copy_build_libs(source_base_path: str, target_base_path: str):
    run_timed(
        context="Copy Build Libs",
        action="Copying build libraries",
        func=lambda: __copy_build_libs(source_base_path, target_base_path)
    )


def __copy_build_libs(source_base_path: str, target_base_path: str) -> Tuple[bool, None]:
    for file_name in os.listdir(source_base_path):
        file_path = os.path.join(source_base_path, file_name)

        if not os.path.isfile(file_path):
            continue

        base_name, base_platform, base_version = get_jar_metadata(file_name)

        if base_platform == "unknown":
            logging.warning(f"Skipping unrecognized jar: {file_name}")
            continue

        velocity_search_directories = {
            f"{target_base_path}/cobblemon/proxy/plugins/": "velocity",
            f"{target_base_path}/pixelmon/proxy/plugins/": "velocity",
        }

        fabric_search_directories = {
            f"{target_base_path}/cobblemon/spawn/mods/": "fabric",
            f"{target_base_path}/cobblemon/realms/mods/": "fabric",
            f"{target_base_path}/cobblemon/wild/mods/": "fabric",
            f"{target_base_path}/cobblemon/wild_generator/mods/": "fabric",
            f"{target_base_path}/cobblemon/gyms/mods/": "fabric",
        }

        neoforge_search_directories = {
            f"{target_base_path}/pixelmon/proxy/plugins/": "velocity",
            f"{target_base_path}/pixelmon/spawn/mods/": "neoforge",
            f"{target_base_path}/pixelmon/realms/mods/": "neoforge",
            f"{target_base_path}/pixelmon/wild/mods/": "neoforge",
            f"{target_base_path}/pixelmon/gyms/mods/": "neoforge",
        }

        search_directories = {
        }

        # no_velocity and no_neoforge patche
        for line in read(f"{SCRIPT_DIR}/../managed_mods.txt").splitlines():
            url = line.split(" ")[0]
            # print(f"{url} vs https://github.com/MMO-REALMS/{base_name}")
            if url == f"https://github.com/MMO-REALMS/{base_name}" or \
                url == f"https://git.mmorealms.gg/mmorealms/{base_name}":
                tags = line.split(" ")[1:]
                if "no_velocity" not in tags:
                    search_directories.update(velocity_search_directories)
                if "no_neoforge" not in tags:
                    search_directories.update(neoforge_search_directories)
                if "no_fabric" not in tags:
                    search_directories.update(fabric_search_directories)
                break

        logging.debug(f"Search directories: {search_directories}")

        for search_directory in search_directories.keys():
            search_platform = search_directories[search_directory]

            if search_platform != base_platform:
                continue

            if not os.path.exists(search_directory):
                continue

            for target_file in os.listdir(search_directory):
                target_file_path = os.path.join(search_directory, target_file)

                if not os.path.isfile(target_file_path):
                    continue

                target_name, target_platform, target_version = get_jar_metadata(target_file)

                if base_name.lower() == target_name.lower() and target_platform.lower() == base_platform.lower():
                    logging.debug(f"Removing old file: {target_file_path}")
                    os.remove(target_file_path)

        for target_directory, target_platform in search_directories.items():
            if base_platform == target_platform:
                copy_file(file_path, f"{target_directory}/{file_name}")

    return True, None


def copy_file(source: str, destination: str) -> None:
    if not os.path.isfile(source):
        raise ValueError(f"The source file '{source}' does not exist or is not a file.")

    source = os.path.realpath(source)
    source = source.replace("\\", "/")

    destination = os.path.realpath(destination)
    destination = destination.replace("\\", "/")

    dest_parent_dir = os.path.dirname(destination)
    if dest_parent_dir and not os.path.exists(dest_parent_dir):
        os.makedirs(dest_parent_dir)

    logging.debug(f"Copying file from '{source}' to '{destination}'")
    shutil.copyfile(source, destination)


def increment_version(version: str) -> str:
    parts = version.split(".")
    if len(parts) != 3:
        return "0.0.1"
    try:
        major, minor, patch = map(int, parts)
        patch += 1
        return f"{major}.{minor}.{patch}"
    except ValueError:
        return "0.0.1"


def upload_file_sftp_key(host: str, port: int, username: str, private_key_path: str, local_path: str, remote_path: str,
                         passphrase: str = None):
    private_key = paramiko.RSAKey(filename=private_key_path, password=passphrase)

    transport = paramiko.Transport((host, port))
    transport.connect(username=username, pkey=private_key)

    sftp = paramiko.SFTPClient.from_transport(transport)
    sftp.put(local_path, remote_path)

    sftp.close()
    transport.close()


def sha1_of_file(file_path: str) -> str:
    sha1 = hashlib.sha1()
    with open(file_path, 'rb') as f:
        while chunk := f.read(8192):  # Read in chunks for efficiency
            sha1.update(chunk)
    return sha1.hexdigest()


def get_max_version(versions: list[str]) -> str:
    return str(max(versions, key=Version))


def get_min_version(versions: list[str]) -> str:
    return str(min(versions, key=Version))


def parse_version(tag: str) -> Optional[Tuple[int, int, int]]:
    m = re.fullmatch(r"(\d+)\.(\d+)\.(\d+)", tag)
    if m:
        return int(m.group(1)), int(m.group(2)), int(m.group(3))
    return None


def get_error_log_directory() -> str:
    return ERROR_DIR


def read_managed_mods() -> str:
    return read(f"{SCRIPT_DIR}/../managed_mods.txt")


def get_build_dir() -> str:
    return BUILD_DIR


def read_gradle_properties() -> dict[str, str]:
    properties = {}
    gradle_properties_path = os.path.join(HOME_DIR, '.gradle', 'gradle.properties')
    data = read(gradle_properties_path)

    for line in data.splitlines():
        if '=' in line:
            key, value = line.split('=', 1)
            properties[key.strip()] = value.strip()

    return properties


def get_property(property: str, default_value: str = "") -> str:
    properties = read_gradle_properties()

    if property in properties:
        return properties[property]

    property = property.upper().replace(".", "_")

    return os.getenv(property, default_value)


def bump_version(version: str) -> str:
    parsed = parse_version(version)
    if parsed is None:
        return "0.0.1"

    major, minor, patch = parsed
    patch += 1
    return f"{major}.{minor}.{patch}"
