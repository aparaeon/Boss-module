# Requirements
# - Have at least one of the files `gradle/libs.versions.toml` `build.gradle.kts` `build.gradle`
# - Have at least one of them have either `project = "some_version"` or `version = "some_version"`

import os


def increment_version(current_version: str):
    version_codes = current_version.split(".")
    version_codes[-1] = str(int(version_codes[-1]) + 1)
    new_version = ".".join(version_codes)
    return new_version


def process_version_line(line: str):
    simple_line = line.strip().replace(" ", "")

    replace_str: str = ""

    if simple_line.startswith("project="):
        replace_str = "project="
    if simple_line.startswith("version="):
        replace_str = "version="

    current_version = simple_line.split(replace_str)[1].replace("\"", "")

    for char in current_version:
        if char.isdigit() or char == ".":
            new_version = increment_version(current_version)
            return line.replace(current_version, new_version)
    else:
        return line


def main():
    version_containers_files = ["gradle/libs.versions.toml", "build.gradle.kts", "build.gradle"]

    for file_path in version_containers_files:
        if not os.path.exists(file_path):
            continue

        new_file_contents = ""

        with open(file_path, "r") as file:
            file_content = file.read()

            for line in file_content.splitlines():
                simple_line = line.strip().replace(" ", "")

                if simple_line.startswith("version=") or simple_line.startswith("project="):
                    try:
                        new_file_contents += f"{process_version_line(line)}\n"
                    except:
                        new_file_contents += f"{line}\n"
                else:
                    new_file_contents += f"{line}\n"

        with open(file_path, "w") as file:
            file.write(new_file_contents)


if __name__ == "__main__":
    main()
