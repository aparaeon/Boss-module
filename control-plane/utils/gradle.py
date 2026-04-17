import logging
import os

from utils.utils import run_timed_shell_command, get_property, copy_file, get_build_dir

home_dir = os.path.expanduser('~')
script_dir: str = os.path.dirname(os.path.abspath(__file__))

GG_MMOREALMS_USERNAME = "gg.mmorealms.username"
GG_MMOREALMS_PASSWORD = "gg.mmorealms.password"


def get_credentials() -> tuple[str, str]:
    """
    :return: (url, username, password)
    """

    username = get_property(GG_MMOREALMS_USERNAME)
    password = get_property(GG_MMOREALMS_PASSWORD)

    return username, password


def build(
        build_name: str,
        working_directory: str,
        publish: bool = False,
        environment: dict | None = None,
) -> bool:
    logging.debug(f"Building {build_name} in directory {working_directory}")

    original_cwd = os.getcwd()
    os.chdir(working_directory)

    run_timed_shell_command(
        context=build_name,
        action="Making gradlew executable",
        command=f"chmod +x gradlew",
        error_log=f"{build_name}#chmod-gradlew.log",
        environment=environment
    )

    run_timed_shell_command(
        context=build_name,
        action="Stopping Gradle Daemon",
        command=f"{get_gradle_command()} --stop",
        error_log=f"{build_name}#stop-gradle-daemon.log",
        environment=environment
    )

    success, (_, _) = run_timed_shell_command(
        context=build_name,
        action="Building",
        command=f"{get_gradle_command()} clean build",
        error_log=f"{build_name}#build.log",
        environment=environment
    )

    if not success:
        return False

    if publish:
        success, (_, _) = run_timed_shell_command(
            context=build_name,
            action="Publishing",
            command=f"{get_gradle_command()} publish",
            error_log=f"{build_name}#build.log",
            environment=environment
        )

        if not success:
            if is_windows():
                logging.error("Per-Module publishing is not supported on Windows.")
                return False

            success, (stdout, _) = run_timed_shell_command(
                context=build_name,
                action="Publishing",
                command=f"{get_gradle_command()} projects | grep \"Project\" | awk -F: '{{print $2}}' | tr -d \"'\" | tr -d \" \" | grep -v \"^$\"",
                error_log=f"{build_name}#build.log",
                environment=environment
            )

            if not success:
                return False

            modules: str = stdout.strip()
            published_at_least_one = False

            for module in modules.splitlines():
                logging.debug(f"Publishing module: {module}")

                success, (_, _) = run_timed_shell_command(
                    context=build_name,
                    action=f"Publishing module {module}",
                    command=f"{get_gradle_command()} {module}:publish",
                    error_log=f"{build_name}#publish-{module}.log",
                    environment=environment
                )

                if success:
                    published_at_least_one = True

            if not published_at_least_one:
                return False

    for file in os.listdir(f"{working_directory}/build/libs"):
        if file.endswith(".jar"):
            copy_file(
                f"{working_directory}/build/libs/{file}",
                f"{get_build_dir()}/{file}"
            )

    os.chdir(original_cwd)

    return True


def is_windows():
    return os.name == 'nt'


def get_gradle_command() -> str:
    return "pwd; dos2unix gradlew; ./gradlew" if not is_windows() else ".\\gradlew.bat"
