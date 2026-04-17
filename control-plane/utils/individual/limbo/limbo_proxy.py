#!/usr/bin/env python3
import os

from control_plane.utils.docker import extract_app_from_image

script_dir: str = os.path.dirname(os.path.abspath(__file__))


def pull(version: str = ""):
    extract_app_from_image(
        image="limbo/proxy",
        directory=f"{script_dir}/../../../../run/limbo/proxy",
        version=version
    )


def main():
    pull()


if __name__ == "__main__":
    main()
