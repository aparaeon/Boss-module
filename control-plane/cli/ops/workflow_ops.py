import logging

from update_all.update_all import main as update_all
from utils.git import get_repositories_unified


def update_all_repositories(args = "master") -> None:
    logging.warning("Updating all repositories - developer use only.")
    update_all(args)
    logging.info("All repositories updated successfully.")


def trigger_all_github_actions() -> None:
    logging.warning("Triggering all GitHub Actions workflows - developer use only.")
    repos = get_repositories_unified()
    for repo in repos:
        logging.info(f"Triggering build workflow for repository: {repo}")
        repo.trigger_build_action()
    logging.info("Triggered workflows for all repositories successfully.")
