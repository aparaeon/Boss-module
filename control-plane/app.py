import logging
import sys

import utils.settings as settings

TO_REMOVE_AFTER_SETTINGS = [
    "--debug",
    "--no-parallel",
]

def main() -> None:
    sys.stdout = open(sys.stdout.fileno(), mode='w', buffering=1)

    commands = sys.argv[1:]

    debug = True if "--debug" in commands else False
    settings.PARALLEL_EXECUTION = False if "--no-parallel" in commands else True

    to_remove = []

    for cmd in commands:
        if cmd.startswith("--setting="):
            key, value = cmd.split("--setting=", 1)[1].split(":", 1)
            settings.update(key, value)
            to_remove.append(cmd)

    for arg in TO_REMOVE_AFTER_SETTINGS:
        if arg in commands:
            to_remove.append(arg)

    for arg in to_remove:
        commands.remove(arg)

    logging.basicConfig(level=logging.DEBUG if debug else logging.INFO, format="[%(levelname)s] %(message)s")

    logging.info("Settings:")
    for key, value in settings.get_all().items():
        logging.info(f" - {key}: {value}")

    from cli.dispatcher import dispatch_multiple
    dispatch_multiple(commands)


if __name__ == "__main__":
    main()
