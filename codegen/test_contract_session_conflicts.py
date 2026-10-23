import json
import unittest
from pathlib import Path
from typing import Any


CODEGEN_ROOT = Path(__file__).resolve().parent
MANIFEST_PATH = CODEGEN_ROOT / "manifest.json"
SESSION_CONFLICT_DESCRIPTION = "服务器会话已被新代次接管"
TARGET_OPERATIONS = {
    "POST /api/v1/monitor/{server_id}/heartbeat",
    "POST /api/v1/players/validate",
    "POST /api/v1/servers/players/offline",
}
EXISTING_CONFLICT_OPERATIONS = {
    "POST /api/v1/players",
    "POST /api/v1/servers",
    "POST /api/v1/tokens",
    "POST /api/v1/towns",
    "PUT /api/v1/tokens/{id}",
    "PUT /api/v1/towns/{id}",
}
HTTP_METHODS = ("get", "post", "put", "delete", "patch")


def load_contract() -> dict[str, Any]:
    manifest = json.loads(MANIFEST_PATH.read_text(encoding="utf-8"))
    contract_path = (CODEGEN_ROOT / manifest["contract"]).resolve()
    return json.loads(contract_path.read_text(encoding="utf-8"))


def iter_operations(contract: dict[str, Any]) -> dict[str, dict[str, Any]]:
    operations: dict[str, dict[str, Any]] = {}
    for path, path_item in contract["paths"].items():
        for method in HTTP_METHODS:
            operation = path_item.get(method)
            if operation is not None:
                operations[f"{method.upper()} {path}"] = operation
    return operations


class SessionConflictContractTests(unittest.TestCase):
    def setUp(self) -> None:
        self.contract = load_contract()
        self.operations = iter_operations(self.contract)

    def test_session_protected_operations_return_standard_conflict_error(self) -> None:
        for operation_name in TARGET_OPERATIONS:
            operation = self.operations[operation_name]
            conflict = operation["responses"]["409"]
            self.assertEqual(conflict["description"], SESSION_CONFLICT_DESCRIPTION)
            self.assertEqual(
                conflict["content"]["application/json"]["schema"]["$ref"],
                "#/components/schemas/ErrorResponse",
            )

        required = set(self.contract["components"]["schemas"]["ErrorResponse"]["required"])
        self.assertEqual(required, {"code", "message", "error"})

    def test_conflict_responses_are_limited_to_known_operations(self) -> None:
        conflict_operations = {
            operation_name
            for operation_name, operation in self.operations.items()
            if "409" in operation["responses"]
        }
        self.assertEqual(
            conflict_operations,
            TARGET_OPERATIONS | EXISTING_CONFLICT_OPERATIONS,
        )
        for operation_name in conflict_operations:
            conflict = self.operations[operation_name]["responses"]["409"]
            self.assertEqual(
                conflict["content"]["application/json"]["schema"]["$ref"],
                "#/components/schemas/ErrorResponse",
            )


if __name__ == "__main__":
    unittest.main()
