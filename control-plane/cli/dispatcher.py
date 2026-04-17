import logging
import os
import shutil
from typing import Any

from cli.command import Command
from cli.ops import repository_ops, docker_ops, server_ops, workflow_ops, readme_ops, dev_ops
from additional_scripts.run import run as run_additional_scripts

SCRIPT_DIR: str = os.path.dirname(os.path.abspath(__file__))

COMMANDS: list[Command] = [
    Command(
        arguments=["--start-cobblemon"],
        function=server_ops.start_cobblemon_server,
        description="Start the Cobblemon servers via Docker Compose"
    ),
    Command(
        arguments=["--stop-cobblemon"],
        function=server_ops.stop_cobblemon_server,
        description="Stop the Cobblemon servers via Docker Compose"
    ),
    Command(
        arguments=["--start-pixelmon"],
        function=server_ops.start_pixelmon_server,
        description="Start the Pixelmon servers via Docker Compose"
    ),
    Command(
        arguments=["--stop-pixelmon"],
        function=server_ops.stop_pixelmon_server,
        description="Stop the Pixelmon servers via Docker Compose"
    ),
    Command(
        arguments=["--push-cobblemon-docker-images", "--push-cobblemon"],
        function=docker_ops.push_cobblemon_images,
        description="Build and push all Cobblemon Docker images to the configured docker registry (docker.mmorealms.gg by default)"
    ),
    Command(
        arguments=["--pull-cobblemon-docker-images", "--pull-cobblemon"],
        function=docker_ops.pull_cobblemon_images,
        description="Pull all Cobblemon Docker images from the configured docker registry (docker.mmorealms.gg by default)"
    ),
    Command(
        arguments=["--push-pixelmon-docker-images", "--push-pixelmon"],
        function=docker_ops.push_pixelmon_images,
        description="Build and push all Pixelmon Docker images to the configured docker registry (docker.mmorealms.gg by default)"
    ),
    Command(
        arguments=["--pull-pixelmon-docker-images", "--pull-pixelmon"],
        function=docker_ops.pull_pixelmon_images,
        description="Pull all Pixelmon Docker images from the configured docker registry (docker.mmorealms.gg by default)"
    ),
    Command(
        arguments=["--pull-cobblemon-wild-generator"],
        function=docker_ops.pull_cobblemon_wild_generator_image,
        description="N/A"
    ),
    Command(
        arguments=["--push-cobblemon-wild-generator"],
        function=docker_ops.push_cobblemon_wild_generator_image,
        description="N/A"
    ),
    Command(
        arguments=["--pull-pixelmon-wild-generator"],
        function=docker_ops.pull_pixelmon_wild_generator_image,
        description="N/A"
    ),
    Command(
        arguments=["--push-pixelmon-wild-generator"],
        function=docker_ops.push_pixelmon_wild_generator_image,
        description="N/A"
    ),
    Command(
        arguments=["--push-cobblemon-resource-pack"],
        function=docker_ops.push_cobblemon_resource_pack,
        description="N/A"
    ),
    Command(
        arguments=["--push-pixelmon-resource-pack"],
        function=docker_ops.push_pixelmon_resource_pack,
        description="N/A"
    ),
    Command(
        arguments=["--push-haproxy"],
        function=docker_ops.push_haproxy,
        description="N/A"
    ),
    Command(
        arguments=["--pull-haproxy"],
        function=docker_ops.pull_haproxy,
        description="N/A"
    ),
    Command(
        arguments=["--push-all"],
        function=docker_ops.push_all,
        description="Short hand notation for --push-cobblemon --push-pixelmon"
    ),
    Command(
        arguments=["--pull-all"],
        function=docker_ops.pull_all,
        description="Short hand notation for --pull-cobblemon --pull-pixelmon"
    ),
    Command(
        arguments=["--update-repos"],
        function=workflow_ops.update_all_repositories,
        description="Update all repositories with the files from update_all/update"
    ),
    Command(
        arguments=["--trigger-workflows"],
        function=workflow_ops.trigger_all_github_actions,
        description="Trigger build and publish workflows for all repositories on GitHub"
    ),
    Command(
        arguments=["--download"],
        function=repository_ops.download,
        description="Download all modules from the configured maven repository (by default, repo.mmorealms.gg)"
    ),
    Command(
        arguments=["--build"],
        function=repository_ops.build,
        description="N/A"
    ),
    Command(
        arguments=["--build-and-bump"],
        function=repository_ops.build_and_bump,
        description="N/A"
    ),
    Command(
        arguments=["--get"],
        function=repository_ops.get,
        description="N/A"
    ),
    Command(
        arguments=["--get-branch"],
        function=repository_ops.get_branch,
        description="Get all module versions with this branch name"
    ),
    Command(
        arguments=["--generate-readme"],
        function=readme_ops.generate_readme,
        description="N/A"
    ),
    Command(
        arguments=["--copy-dev-files"],
        function=dev_ops.copy_to_servers,
        description="N/A"
    ),
    Command(
        arguments=["--run-additional-scripts"],
        function=run_additional_scripts,
        description="Run all additional scripts and copy their results to the run directories"
    )
]


def parse_command(raw_command: str) -> tuple[str, str]:
    parts = raw_command.split("=")
    cmd = parts[0]
    args = None if len(parts) < 2 else parts[1]
    return cmd, args



def find_command(cmd: str) -> Any:
    for command in COMMANDS:
        if command.matches(cmd):
            return command.function

    return None


def dispatch_command(cmd: str, args=None):
    function = find_command(cmd)

    if function is None:
        return False

    if args is None:
        logging.info(f"Executing command: {cmd}")
        function()
    else:
        logging.info(f"Executing command: {cmd} with args {args}")
        function(args)
    return True


def show_help_message() -> None:
    print("")
    print("")
    print("")
    print("MMORealms Control Plane CLI")
    print("")
    print("Usage: control-plane [options] [commands]")
    print("")
    print("Options:")
    print("  --help                Show this help message and exit")
    print("  --debug               Enable debug logging")
    print("")
    print("Commands:")
    for command in COMMANDS:
        print(f"  {command}")
    print("")
    print("You can chain multiple commands by separating them with spaces.")
    print("For example: control-plane --update-repos --build --start-cobblemon")
    print("")
    print("")
    print("")


def dispatch_multiple(commands: list[str]):
    if "--help" in commands:
        show_help_message()
        return

    logging.info(f"Dispatching {len(commands)} queued command(s)...")
    for raw in commands:
        cmd, cmd_args = parse_command(raw)
        ok = dispatch_command(cmd, cmd_args)
        if not ok:
            logging.error(f"Command '{cmd}' failed or not found — stopping sequence.")
            show_help_message()
            break
