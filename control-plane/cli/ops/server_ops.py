import logging
import os

from utils.docker import docker_compose_up, docker_compose_down

SCRIPT_DIR: str = os.path.dirname(os.path.abspath(__file__))


def start_cobblemon_server() -> None:
    logging.info("Starting Cobblemon (Fabric) server...")
    docker_compose_up(f"{SCRIPT_DIR}/../../run/cobblemon/docker-compose.yml")


def stop_cobblemon_server() -> None:
    logging.info("Stopping Cobblemon (Fabric) server...")
    docker_compose_down(f"{SCRIPT_DIR}/../../run/cobblemon/docker-compose.yml")


def start_pixelmon_server() -> None:
    logging.info("Starting Pixelmon (NeoForge) server...")
    docker_compose_up(f"{SCRIPT_DIR}/../../run/pixelmon/docker-compose.yml")


def stop_pixelmon_server() -> None:
    logging.info("Stopping Pixelmon (NeoForge) server...")
    docker_compose_down(f"{SCRIPT_DIR}/../../run/pixelmon/docker-compose.yml")
