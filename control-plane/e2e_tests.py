import os
import re
import sys

from utils.colors import GREEN, RED, YELLOW
from utils.utils import run_shell_command, reset_directory

HOME_DIR = os.path.expanduser('~')
SCRIPT_DIR: str = os.path.dirname(os.path.abspath(__file__))
PROXY_DIR: str = reset_directory(f"{SCRIPT_DIR}/run/cobblemon/proxy")
SPAWN_DIR: str = reset_directory(f"{SCRIPT_DIR}/run/cobblemon/spawn")
REALMS_DIR: str = reset_directory(f"{SCRIPT_DIR}/run/cobblemon/realms")
WILD_DIR: str = reset_directory(f"{SCRIPT_DIR}/run/cobblemon/wild")
GYMS_DIR: str = reset_directory(f"{SCRIPT_DIR}/run/cobblemon/gyms")

IS_WINDOWS = sys.platform == "win32"
PYTHON_EXECUTABLE = "python.exe" if IS_WINDOWS else "python3"

# If you want to test a different version, change these variables.
DEBUG = True
LOADER_VERSION = "1.0.58"
LOADER_BRANCH = "feature/trade"
TRADE_VERSION = "1.0.68"
TRADE_BRANCH = "feature/trade"


def reset_run_directory():
    global PROXY_DIR, SPAWN_DIR, REALMS_DIR, WILD_DIR, GYMS_DIR

    PROXY_DIR = reset_directory(f"{SCRIPT_DIR}/run/cobblemon/proxy")
    SPAWN_DIR = reset_directory(f"{SCRIPT_DIR}/run/cobblemon/spawn")
    REALMS_DIR = reset_directory(f"{SCRIPT_DIR}/run/cobblemon/realms")
    WILD_DIR = reset_directory(f"{SCRIPT_DIR}/run/cobblemon/wild")
    GYMS_DIR = reset_directory(f"{SCRIPT_DIR}/run/cobblemon/gyms")


def get_paths(module: str, version: str) -> list[str]:
    return [
        f"{PROXY_DIR}/plugins/{module}-velocity-{version}.jar",
        f"{SPAWN_DIR}/mods/{module}-fabric-{version}.jar",
        f"{REALMS_DIR}/mods/{module}-fabric-{version}.jar",
        f"{WILD_DIR}/mods/{module}-fabric-{version}.jar",
        f"{GYMS_DIR}/mods/{module}-fabric-{version}.jar",
    ]


def run_shell_command_and_debug_fail(command: str):
    result, (stdout, stderr) = run_shell_command(command)
    print(f"Executed command: {command} -> Success? {result}")
    if not result and DEBUG:
        print(stdout)
        print(stderr)


def __generic_test(name: str, action: str, module: str, version: str, expected_files: list[tuple[str, str]]):
    """
    :param name:
    :param action:
    :param module:
    :param version:
    :param expected_files: [(module, version), ...]
    :return:
    """
    reset_run_directory()
    run_shell_command_and_debug_fail(f"{PYTHON_EXECUTABLE} app.py --{action}={module}:{version}")

    for check_module, check_version in expected_files:
        for path in get_paths(check_module, check_version):
            directory = os.path.dirname(path)
            if not os.path.isdir(directory):
                raise AssertionError(f"Expected directory {directory} to exist.")

            file_pattern = os.path.basename(path)

            try:
                rx = re.compile(file_pattern)
            except re.error as e:
                raise AssertionError(f"Invalid regex pattern {file_pattern!r}: {e}")

            files_in_directory = os.listdir(directory)
            matches = [name for name in files_in_directory if rx.fullmatch(name)]

            if not matches:
                raise AssertionError(
                    f"Expected a file matching regex {file_pattern!r} to exist in directory {directory}. "
                    f"Found: {files_in_directory}"
                )

    print(f"{GREEN}Test {name} passed.")


def test_download_simple_dependency_tree():
    __generic_test(
        "download-loader",
        "download",
        "loader", LOADER_VERSION,
        [
            ("loader", LOADER_VERSION)
        ],
    )


def test_download_complex_dependency_tree():
    __generic_test(
        "download-trade",
        "download",
        "trade-module", TRADE_VERSION,
        [
            ("trade-module", TRADE_VERSION),
        ],
    )


def test_build_simple_dependency_tree():
    __generic_test(
        "build-loader",
        "build",
        "loader", LOADER_BRANCH,
        [
            ("loader", f"{LOADER_BRANCH.replace("/", "_")}+.*")
        ],
    )


def test_build_complex_dependency_tree():
    __generic_test(
        "build-trade",
        "build",
        "trade-module", TRADE_BRANCH,
        [
            ("chat-module", f".*"),
            ("core-module", f"{TRADE_BRANCH.replace("/", "_")}+.*"),
            ("economy-module", f"{TRADE_BRANCH.replace("/", "_")}+.*"),
            ("essentials-module", f"{TRADE_BRANCH.replace("/", "_")}+.*"),
            ("loader-module", f"{TRADE_BRANCH.replace("/", "_")}+.*"),
            ("pokemon-module", f"{TRADE_BRANCH.replace("/", "_")}+.*"),
            ("trade-module", f"{TRADE_BRANCH.replace("/", "_")}+.*"),
            ("user-data-module", f".*"),
        ],
    )


def main():
    print(f"{RED}WARNING: THIS WILL DELETE YOUR RUN DIRECTORY!")
    input(f"{YELLOW}Press Enter to continue...")

    test_download_simple_dependency_tree()
    test_download_complex_dependency_tree()
    test_build_simple_dependency_tree()
    test_build_complex_dependency_tree()

    print(f"{GREEN}All tests passed!")


if __name__ == '__main__':
    main()
