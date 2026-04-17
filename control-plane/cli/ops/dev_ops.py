import os

from utils.utils import copy_build_libs

SCRIPT_DIR: str = os.path.dirname(os.path.abspath(__file__))

def copy_to_servers():
    copy_build_libs("build/libs", f"{SCRIPT_DIR}/../../run")
