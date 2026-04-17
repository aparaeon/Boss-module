import cProfile
import logging
import unittest

from utils.git import get_repositories_unified
from utils.nexus import get_latest_version
from utils.utils import parse_version


class TestStringMethods(unittest.TestCase):

    def test_foo(self):
        print(get_latest_version(
            artifact_id="loader-common"
        ))

    """
    Profiler code snippet:
    
    pr = cProfile.Profile()
    pr.enable()

    repositories = get_repositories_unified(ids_and_versions=["core", "chat-module"])

    pr.disable()
    pr.dump_stats("profile.pst")
    """

    def test_get_repositories_specific_no_version(self):
        repositories = get_repositories_unified(ids_and_versions=["core", "chat-module"])

        self.assertEqual(len(repositories), 2)
        self.assertEqual(repositories[0].url, "https://github.com/MMO-REALMS/core-module")
        self.assertEqual(repositories[1].url, "https://github.com/MMO-REALMS/chat-module")

        self.assertNotEqual(repositories[0].version, None)
        self.assertNotEqual(repositories[0].version.get_version(), None)
        self.assertNotEqual(parse_version(repositories[0].version.get_version()), None)
        self.assertGreaterEqual(parse_version(repositories[0].version.get_version())[0], 1)
        self.assertGreaterEqual(parse_version(repositories[0].version.get_version())[1], 0)
        self.assertGreaterEqual(parse_version(repositories[0].version.get_version())[2], 0)
        self.assertEqual(repositories[0].version.get_branch(), "master")

        self.assertNotEqual(repositories[1].version, None)
        self.assertNotEqual(repositories[1].version.get_version(), None)
        self.assertNotEqual(parse_version(repositories[1].version.get_version()), None)
        self.assertGreaterEqual(parse_version(repositories[1].version.get_version())[0], 1)
        self.assertGreaterEqual(parse_version(repositories[1].version.get_version())[1], 0)
        self.assertGreaterEqual(parse_version(repositories[1].version.get_version())[2], 0)
        self.assertEqual(repositories[1].version.get_branch(), "master")

    def test_get_repositories_specific_specific_version(self):
        repositories = get_repositories_unified(ids_and_versions=["core:1.2.3", "chat-module:feature/gambling", "economy-module:vlatest", "essentials:v3.2.1"])
        self.assertEqual(len(repositories), 4)

        self.assertEqual(repositories[0].url, "https://github.com/MMO-REALMS/core-module")
        self.assertEqual(repositories[1].url, "https://github.com/MMO-REALMS/chat-module")
        self.assertEqual(repositories[2].url, "https://github.com/MMO-REALMS/economy-module")
        self.assertEqual(repositories[3].url, "https://github.com/MMO-REALMS/essentials-module")

        self.assertEqual(repositories[0].version.get_version(), "1.2.3")
        self.assertEqual(repositories[0].version.get_branch(), "master")

        # If this test fails, make sure the branch exists. The code checks if the branch exists and if it does not, it falls back to master.
        self.assertTrue(repositories[1].version.get_version().startswith("feature_gambling"))
        self.assertEqual(repositories[1].version.get_branch(), "feature/gambling")

        self.assertNotEqual(repositories[2].version, None)
        self.assertNotEqual(repositories[2].version.get_version(), None)
        self.assertNotEqual(parse_version(repositories[2].version.get_version()), None)
        self.assertGreaterEqual(parse_version(repositories[2].version.get_version())[0], 1)
        self.assertGreaterEqual(parse_version(repositories[2].version.get_version())[1], 0)
        self.assertGreaterEqual(parse_version(repositories[2].version.get_version())[2], 0)

        self.assertEqual(repositories[3].version.get_version(), "3.2.1")
        self.assertEqual(repositories[3].version.get_branch(), "master")

    def test_get_repositories_all_no_version(self):
        repositories = get_repositories_unified(ids_and_versions=None)
        self.assertGreater(len(repositories), 20)


if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG, format="[%(levelname)s] %(message)s")
    unittest.main()
