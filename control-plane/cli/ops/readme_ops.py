import os

from utils.git import get_repositories_unified
from utils.utils import write

script_dir: str = os.path.dirname(os.path.abspath(__file__))

BADGE_TEMPLATE = "[![Build and Publish](https://github.com/MMO-REALMS/{REPO_NAME}/actions/workflows/build_and_publish.yml/badge.svg?branch=master)](https://github.com/MMO-REALMS/{REPO_NAME}/actions/workflows/build_and_publish.yml)"

def generate_readme():
    repos = get_repositories_unified()

    readme_data = "| Repository | Status |\n"
    readme_data += "|--|--|\n"

    for repo in repos:
        readme_data += f"| {repo.id} | {BADGE_TEMPLATE.replace('{REPO_NAME}', repo.id)} |\n"

    write(f"{script_dir}/../../README.md", readme_data)


