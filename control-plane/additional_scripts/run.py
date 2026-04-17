import importlib.util
import os
import shutil

SCRIPTS_DIR: str = os.path.dirname(os.path.abspath(__file__))
RESULT_DIR: str = os.path.join(SCRIPTS_DIR, "result")
RUN_DIR: str = os.path.abspath(os.path.join(SCRIPTS_DIR, "../run"))


def clear_result_dir() -> None:
    if os.path.exists(RESULT_DIR):
        shutil.rmtree(RESULT_DIR)
    os.makedirs(RESULT_DIR)


def run_scripts() -> None:
    original_dir = os.getcwd()
    for entry in sorted(os.scandir(SCRIPTS_DIR), key=lambda e: e.name):
        if not entry.is_dir() or entry.name.startswith('.'):
            continue
        app_py = os.path.join(entry.path, "app.py")
        if not os.path.exists(app_py):
            continue
        print(f"[additional_scripts] Running {entry.name}/app.py...")
        try:
            os.chdir(entry.path)
            spec = importlib.util.spec_from_file_location("app", app_py)
            module = importlib.util.module_from_spec(spec)
            spec.loader.exec_module(module)
            module.main()
        finally:
            os.chdir(original_dir)


def copy_results(server_type: str = None) -> None:
    if not os.path.exists(RESULT_DIR):
        return

    for type_entry in os.scandir(RESULT_DIR):
        if not type_entry.is_dir():
            continue
        if server_type and type_entry.name != server_type:
            continue

        for root, dirs, files in os.walk(type_entry.path):
            for filename in files:
                src = os.path.join(root, filename)
                rel = os.path.relpath(src, RESULT_DIR)
                dst = os.path.join(RUN_DIR, rel)
                os.makedirs(os.path.dirname(dst), exist_ok=True)
                shutil.copy2(src, dst)
                print(f"[additional_scripts] Copied {rel} to {dst}")


def run(server_type: str = None) -> None:
    print("Running additional scripts...")
    clear_result_dir()
    run_scripts()
    copy_results(server_type)


if __name__ == "__main__":
    run()
