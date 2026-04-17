PARALLEL_EXECUTION = True
NEXUS_BASE_URL = "https://repo.mmorealms.gg"
DOCKER_REGISTRY = "docker.mmorealms.gg"

def update(key: str, value: str) -> None:
    global PARALLEL_EXECUTION
    global NEXUS_BASE_URL
    global DOCKER_REGISTRY

    match key:
        case "PARALLEL_EXECUTION":
            PARALLEL_EXECUTION = value.lower() in ("true", "1", "yes")
        case "NEXUS_BASE_URL":
            NEXUS_BASE_URL = value
        case "DOCKER_REGISTRY":
            DOCKER_REGISTRY = value

def get_all() -> dict[str, object]:
    return {
        "PARALLEL_EXECUTION": PARALLEL_EXECUTION,
        "NEXUS_BASE_URL": NEXUS_BASE_URL,
        "DOCKER_REGISTRY": DOCKER_REGISTRY,
    }