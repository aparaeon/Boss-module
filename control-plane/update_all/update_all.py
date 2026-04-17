import logging
import os
import shutil

from utils.git import Repository, Result, get_repositories_unified, get_repositories_with_branch
from utils.utils import handle_remove_readonly, read, write, run_timed_shell_command

SCRIPT_DIR: str = os.path.dirname(os.path.abspath(__file__))


def copy_update_files(repository: Repository):
    source_directory = f"{SCRIPT_DIR}/update"

    for item in os.listdir(source_directory):
        source = os.path.join(source_directory, item)
        destination = os.path.join(repository.directory, item)

        if os.path.isdir(source):
            shutil.copytree(source, destination, dirs_exist_ok=True)
        else:
            shutil.copy2(source, destination)


def update_gradle_properties(repository: Repository):
    original_file = read(f"{repository.directory}/gradle.properties")
    updated_file = (
        original_file
        .replace("org.gradle.jvmargs=-Xmx16G", "org.gradle.jvmargs=-Xmx10G")
    )
    write(f"{repository.directory}/gradle.properties", updated_file)


def remove_unwanted_files(repository: Repository) -> None:
    files = [
        f"{repository.directory}/.run/Debug.run.xml"
        f"{repository.directory}/.run/Debug Proxy.run.xml"
        f"{repository.directory}/.run/Debug Realms.run.xml"
        f"{repository.directory}/.run/Debug Spawn.run.xml"
        f"{repository.directory}/.run/Debug Wild.run.xml"
    ]

    for file in files:
        if os.path.exists(file):
            os.remove(file)


def update_app_run_xml(repository: Repository):
    path = f"{repository.directory}/.run/app.run.xml"
    data = read(path)
    data = data.replace(
        "<module name=\"loader\"/>", f"<module name=\"{repository.id}\"/>"
    )
    write(path, data)


def delete_copy_run_xml(repository: Repository):
    path = f"{repository.directory}/.run/copy.run.xml"
    if os.path.exists(path):
        os.remove(path)


def process_repo(repository: Repository):
    original_cwd = os.getcwd()

    copy_update_files(repository)
    # update_gradle_properties(repository) # Legacy
    delete_copy_run_xml(repository)
    update_app_run_xml(repository)

    os.chdir(original_cwd)


def main(branch: str = "master") -> None:
    set_upstream:bool = False

    for repository in get_repositories_with_branch(branch=branch):
        result = repository.pull(is_managed=True)

        if result == Result.FAILED:
            logging.warning(f"Skipping repository {repository.id} due to failed pull")
            continue

        os.chdir(f"{repository.directory}")
        process_repo(repository)

        success, (_,_) = run_timed_shell_command(
            context=repository.id,
            action="Adding to git",
            command=f"git add -A",
            error_log=f"{repository.id}-git-add.log"
        )

        if not success:
            continue

        success, (_,_) = run_timed_shell_command(
            context=repository.id,
            action="Commiting",
            command=f"git commit -m \"Automated Update\"",
            error_log=f"{repository.id}-git-commit.log"
        )

        if not success:
            continue

        success, (_,_) = run_timed_shell_command(
            context=repository.id,
            action="Pushing",
            command=f"git push {f"--set-upstream origin {branch}" if set_upstream else ""}",
            error_log=f"{repository.id}-git-push.log"
        )

        if not success:
            continue

if __name__ == "__main__":
    main()
