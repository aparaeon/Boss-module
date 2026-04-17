from typing import Callable


class Command:
    arguments: list[str]
    function: Callable
    description: str

    def __init__(self, arguments: list[str], function: Callable, description: str = ""):
        self.arguments = arguments
        self.function = function
        self.description = description

    def matches(self, arg: str) -> bool:
        return arg in self.arguments

    def __str__(self) -> str:
        return f"{(" ".join(self.arguments).ljust(40))} - {self.description}"