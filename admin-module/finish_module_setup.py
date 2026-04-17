import os
import sys

script_dir: str = os.path.dirname(os.path.abspath(__file__))


def to_pascal_case(s):
    return ''.join(word.capitalize() for word in s.replace("-", "_").replace('_', ' ').split())


def main():
    args = sys.argv[1:]

    if len(args) != 1:
        print("Usage: python finish_module_setup.py <new_module_name>")
        sys.exit(1)

    id = args[0].lower()
    friendly_name = to_pascal_case(id)
    package_name = id.replace("-", "_")

    exclude = ["build", ".gradle", ".git", ".idea", ".run", ".github", "buildSrc"]
    excluded_files = ["finish_module_setup.py", "gradlew", "gradlew.bat", ".gitignore", ".gitmodules",
                      "gradle-wrapper.jar"]
    id_files = ["settings.gradle", "settings.gradle.kts"]

    files_to_delete: list[str] = []

    for root, directories, files in os.walk(script_dir, topdown=True):
        directories[:] = [directory for directory in directories if directory not in exclude]

        for file_name in files:
            file_path: str = str(os.path.join(root, file_name))
            new_file_path = file_path.replace("example", package_name).replace("Example", friendly_name)
            new_file_directory = os.path.dirname(new_file_path)

            if file_name in excluded_files:
                continue

            new_content = ""

            with open(file_path, "r", encoding="utf-8", errors="ignore") as file:
                content: str = file.read()
                lines = content.splitlines()

                if file_name in id_files:
                    new_lines =  [line.replace("example", id).replace("Example", friendly_name) for line in lines]
                else:
                    new_lines = [line.replace("example", package_name).replace("Example", friendly_name) for line in lines]

                new_content = "\n".join(new_lines)

            if not os.path.exists(new_file_directory):
                os.makedirs(new_file_directory)

            with open(new_file_path, "w", encoding="utf-8") as new_file:
                new_file.write(new_content)

            if new_file_path != file_path:
                files_to_delete.append(file_path)

    for file in files_to_delete:
        try:
            os.remove(file)
        except Exception as e:
            print(f"Error removing file {file}: {e}")

    repeat: bool = True

    while repeat:
        repeat = False
        for root, directories, files in os.walk(script_dir, topdown=False):
            for directory in directories:
                dir_path = os.path.join(root, directory)
                try:
                    os.rmdir(dir_path)
                    repeat = True
                except OSError:
                    pass

    os.remove(os.path.abspath(__file__))


if __name__ == "__main__":
    main()
