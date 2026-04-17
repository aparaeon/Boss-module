import os
import shutil
import threading
from typing import Callable

import utils.settings as settings
from additional_scripts.run import run as run_additional_scripts
from utils.docker import build_image, extract_app_from_image, get_latest_tag
from utils.docker import push_image as push_docker_image
from utils.utils import increment_version, handle_remove_readonly

SCRIPT_DIRECTORY: str = os.path.dirname(os.path.abspath(__file__))
RUN_DIRECTORY: str = f"{SCRIPT_DIRECTORY}/../../run"


class Image:
    id: str
    path: str
    docker_file: str
    can_be_pulled: bool

    def __init__(self, id: str, path: str, docker_file: str = "Dockerfile", can_be_pulled: bool = True):
        self.id = id
        self.path = path
        self.docker_file = docker_file
        self.can_be_pulled = can_be_pulled

    def build(self, version: str):
        if version == "latest" or version is None or version == "":
            version = increment_version(get_latest_tag([self.id]))

        build_image(
            image=self.id,
            build_dir=self.path,
            docker_file=self.docker_file,
            version=version,
        )

    def push(self, version: str):
        push_docker_image(
            image=self.id,
            version=version,
        )

        self.cleanup()

    def pull(self, version: str):
        if version == "latest" or version is None or version == "":
            version = get_latest_tag([self.id])

        if not self.can_be_pulled:
            return

        extract_app_from_image(
            image=self.id,
            directory=self.path,
            version=version
        )

    def cleanup(self):
        if self.can_be_pulled:
            shutil.rmtree(self.path, onerror=handle_remove_readonly)

    def create_build_function(self, version: str) -> Callable[[], None]:
        return lambda: self.build(version)

    def create_pull_function(self, version: str) -> Callable[[], None]:
        return lambda: self.pull(version)


LOBBY_HAPROXY_IMAGE = Image("lobby/haproxy", f"{RUN_DIRECTORY}/lobby/haproxy")

COBBLEMON_WILD_GENERATOR_IMAGE = Image("cobblemon/wild_generator", f"{RUN_DIRECTORY}/cobblemon/wild_generator")
COBBLEMON_RESOURCE_PACK_IMAGE = Image("cobblemon/resource_pack", f"{RUN_DIRECTORY}/resource_pack", docker_file="Dockerfile-cobblemon", can_be_pulled=False)
COBBLEMON_IMAGES: list[Image] = [
    Image("cobblemon/proxy", f"{RUN_DIRECTORY}/cobblemon/proxy"),
    Image("cobblemon/realms", f"{RUN_DIRECTORY}/cobblemon/realms"),
    Image("cobblemon/spawn", f"{RUN_DIRECTORY}/cobblemon/spawn"),
    Image("cobblemon/wild", f"{RUN_DIRECTORY}/cobblemon/wild"),
    Image("cobblemon/gyms", f"{RUN_DIRECTORY}/cobblemon/gyms"),
    COBBLEMON_RESOURCE_PACK_IMAGE,
]

PIXELMON_WILD_GENERATOR_IMAGE = Image("pixelmon/wild_generator", f"{RUN_DIRECTORY}/pixelmon/wild_generator")
PIXELMON_RESOURCE_PACK_IMAGE = Image("pixelmon/resource_pack", f"{RUN_DIRECTORY}/resource_pack", docker_file="Dockerfile-pixelmon", can_be_pulled=False)
PIXELMON_IMAGES: list[Image] = [
    Image("pixelmon/proxy", f"{RUN_DIRECTORY}/pixelmon/proxy"),
    Image("pixelmon/realms", f"{RUN_DIRECTORY}/pixelmon/realms"),
    Image("pixelmon/spawn", f"{RUN_DIRECTORY}/pixelmon/spawn"),
    Image("pixelmon/wild", f"{RUN_DIRECTORY}/pixelmon/wild"),
    Image("pixelmon/gyms", f"{RUN_DIRECTORY}/pixelmon/gyms"),
    PIXELMON_RESOURCE_PACK_IMAGE,
]


def reset_live():
    shutil.rmtree(f"{SCRIPT_DIRECTORY}/../../run/cobblemon/.live", ignore_errors=True)
    shutil.rmtree(f"{SCRIPT_DIRECTORY}/../../run/pixelmon/.live", ignore_errors=True)


def push_cobblemon_resource_pack(version: str = None) -> None:
    COBBLEMON_RESOURCE_PACK_IMAGE.build(version)
    COBBLEMON_RESOURCE_PACK_IMAGE.push(version)


def push_pixelmon_resource_pack(version: str = None) -> None:
    PIXELMON_RESOURCE_PACK_IMAGE.build(version)
    PIXELMON_RESOURCE_PACK_IMAGE.push(version)


def __execute_multiple(functions: list[Callable[[], None]]) -> None:
    if settings.PARALLEL_EXECUTION:
        threads = []

        for function in functions:
            thread = threading.Thread(target=function)
            threads.append(thread)
            thread.start()

        for thread in threads:
            thread.join()

        return

    else:
        for function in functions:
            function()


def find_common_version(images: list[Image]) -> str:
    return get_latest_tag([image.id for image in images])


def __push(images: list[Image], version: str) -> None:
    reset_live()

    if version == "latest" or version is None or version == "":
        version = find_common_version(images=images)
        version = increment_version(version=version)

    __execute_multiple(functions=[image.create_build_function(version) for image in images])

    for image in images:
        image.push(version=version)


def __pull(images: list[Image], version: str) -> None:
    reset_live()

    if version == "latest" or version is None or version == "":
        version = find_common_version(images)

    __execute_multiple(functions=[image.create_pull_function(version) for image in images])


def push_pixelmon_images(version="latest") -> None:
    run_additional_scripts("pixelmon")
    __push(PIXELMON_IMAGES, version)


def pull_pixelmon_images(version="latest") -> None:
    __pull(PIXELMON_IMAGES, version)


def push_cobblemon_images(version="latest") -> None:
    run_additional_scripts("cobblemon")
    __push(COBBLEMON_IMAGES, version)


def pull_cobblemon_images(version="latest") -> None:
    __pull(COBBLEMON_IMAGES, version)


def push_cobblemon_wild_generator_image(version="latest") -> None:
    run_additional_scripts("cobblemon")
    __push([COBBLEMON_WILD_GENERATOR_IMAGE], version)


def pull_cobblemon_wild_generator_image(version="latest") -> None:
    __pull([COBBLEMON_WILD_GENERATOR_IMAGE], version)


def push_pixelmon_wild_generator_image(version="latest") -> None:
    run_additional_scripts("pixelmon")
    __push([PIXELMON_WILD_GENERATOR_IMAGE], version)


def pull_pixelmon_wild_generator_image(version="latest") -> None:
    __pull([PIXELMON_WILD_GENERATOR_IMAGE], version)


def push_all(version="latest") -> None:
    run_additional_scripts()
    __push(COBBLEMON_IMAGES + PIXELMON_IMAGES, version)


def pull_all(version="latest") -> None:
    __pull(COBBLEMON_IMAGES + PIXELMON_IMAGES, version)


def push_haproxy(version="latest") -> None:
    __push([LOBBY_HAPROXY_IMAGE], version)


def pull_haproxy(version="latest") -> None:
    __pull([LOBBY_HAPROXY_IMAGE], version)
