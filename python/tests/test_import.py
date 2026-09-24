"""Public package import regression."""

import subprocess
import sys
import unittest


class ImportTests(unittest.TestCase):
    def test_public_client_can_be_imported(self) -> None:
        result = subprocess.run(
            [sys.executable, "-B", "-c", "from newnanmanager import NewNanManagerClient"],
            check=False,
            capture_output=True,
            text=True,
        )
        self.assertEqual(result.returncode, 0, result.stderr)
