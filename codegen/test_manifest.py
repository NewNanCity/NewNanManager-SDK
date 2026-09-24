import hashlib
import json
import unittest
from pathlib import Path

from generate import IGNORED_OUTPUT_FILES


CODEGEN_ROOT = Path(__file__).resolve().parent
SDK_ROOT = CODEGEN_ROOT.parent
MANIFEST_PATH = CODEGEN_ROOT / "manifest.json"


class CodegenManifestTests(unittest.TestCase):
    def setUp(self) -> None:
        self.manifest = json.loads(MANIFEST_PATH.read_text(encoding="utf-8"))
        self.manifest_sha = hashlib.sha256(MANIFEST_PATH.read_bytes()).hexdigest()

    def test_manifest_declares_all_supported_languages(self) -> None:
        self.assertEqual(
            set(self.manifest["languages"]),
            {"typescript", "golang", "python", "csharp", "kotlin", "java"},
        )
        self.assertEqual(self.manifest["generator"]["cliVersion"], "2.41.0")
        self.assertEqual(self.manifest["generator"]["engineVersion"], "7.25.0")

    def test_generated_outputs_are_owned_by_current_manifest(self) -> None:
        for language in self.manifest["languages"]:
            output = SDK_ROOT / "generated" / language
            marker_path = output / ".nnm-generated.json"
            self.assertTrue(output.is_dir(), language)
            marker = json.loads(marker_path.read_text(encoding="utf-8"))
            self.assertEqual(marker["tool"], "newnanmanager-openapi-codegen")
            self.assertEqual(marker["language"], language)
            self.assertEqual(marker["manifestSha256"], self.manifest_sha)
            self.assertFalse(Path(marker["contract"]).is_absolute())

    def test_canonical_contract_is_outside_the_sdk_copy(self) -> None:
        contract = (CODEGEN_ROOT / self.manifest["contract"]).resolve()
        self.assertTrue(contract.is_file())
        self.assertEqual(contract.name, "newnanmanager.openapi.json")

    def test_generated_outputs_exclude_duplicate_contract_and_publish_scripts(self) -> None:
        forbidden_names = {
            "openapi.yaml",
            "git_push.sh",
            "git_push.ps1",
            "test-requirements.txt",
            ".gitlab-ci.yml",
        }
        for language in self.manifest["languages"]:
            output = SDK_ROOT / "generated" / language
            files = {path.name for path in output.rglob("*") if path.is_file()}
            self.assertTrue(forbidden_names.isdisjoint(files), language)
            self.assertFalse((output / "docs").exists(), language)
            self.assertFalse((output / ".github").exists(), language)

    def test_generated_go_module_matches_sdk_baseline(self) -> None:
        go_mod = (SDK_ROOT / "generated" / "golang" / "go.mod").read_text(encoding="utf-8")
        self.assertIn("\ngo 1.25.0\n", f"\n{go_mod}")

    def test_generated_java_project_uses_okhttp_client(self) -> None:
        java_root = SDK_ROOT / "generated" / "java"
        pom = (java_root / "pom.xml").read_text(encoding="utf-8")
        self.assertIn("newnanmanager-generated-java", pom)
        self.assertIn("com.squareup.okhttp3", pom)
        self.assertTrue(
            (
                java_root
                / "src"
                / "main"
                / "java"
                / "com"
                / "newnancity"
                / "newnanmanager"
                / "generated"
            ).is_dir()
        )
        self.assertEqual(self.manifest["languages"]["java"]["properties"]["library"], "okhttp-gson")
        self.assertEqual(
            self.manifest["languages"]["java"]["properties"]["serializationLibrary"],
            "gson",
        )
        self.assertTrue((java_root / "pom.xml").is_file())
        self.assertFalse((java_root / "build.gradle").exists())
        self.assertFalse((java_root / "gradlew").exists())
        self.assertFalse((java_root / "gradle").exists())
        self.assertFalse((java_root / "src" / "main" / "AndroidManifest.xml").exists())

    def test_generated_java_preserves_both_authentication_schemes(self) -> None:
        java_root = SDK_ROOT / "generated" / "java"
        api_client = (
            java_root
            / "src"
            / "main"
            / "java"
            / "com"
            / "newnancity"
            / "newnanmanager"
            / "generated"
            / "ApiClient.java"
        ).read_text(encoding="utf-8")
        player_api = (
            java_root
            / "src"
            / "main"
            / "java"
            / "com"
            / "newnancity"
            / "newnanmanager"
            / "generated"
            / "api"
            / "PlayerServiceApi.java"
        ).read_text(encoding="utf-8")
        self.assertIn('authentications.put("BearerAuth"', api_client)
        self.assertIn('authentications.put("ApiKeyAuth"', api_client)
        self.assertIn('"X-API-Token"', api_client)
        self.assertIn('"ApiKeyAuth", "BearerAuth"', player_api)

    def test_generated_kotlin_build_is_pinned_to_verified_toolchain(self) -> None:
        kotlin_config = self.manifest["languages"]["kotlin"]
        self.assertEqual(
            kotlin_config["build"],
            {
                "kotlinVersion": "2.2.0",
                "jacksonVersion": "2.18.10",
                "okhttpVersion": "5.1.0",
                "gradleVersion": "8.14",
            },
        )
        build_gradle = (SDK_ROOT / "generated" / "kotlin" / "build.gradle").read_text(
            encoding="utf-8"
        )
        self.assertIn("id 'org.jetbrains.kotlin.jvm' version '2.2.0'", build_gradle)
        self.assertIn("kotlin-stdlib:2.2.0", build_gradle)
        self.assertIn("jackson-module-kotlin:2.18.10", build_gradle)
        self.assertIn("okhttp:5.1.0", build_gradle)
        self.assertNotIn("spotless-plugin-gradle", build_gradle)
        self.assertNotIn("jackson-datatype-jsr310", build_gradle)
        self.assertNotIn("kotlin-reflect", build_gradle)

    def test_generated_kotlin_wrapper_cache_is_outside_drift_digest(self) -> None:
        generated_ignore = (SDK_ROOT / "generated" / ".gitignore").read_text(encoding="utf-8")
        self.assertIn("**/gradle/wrapper/gradle-wrapper.jar", generated_ignore)
        self.assertIn("gradle/wrapper/gradle-wrapper.jar", IGNORED_OUTPUT_FILES)


if __name__ == "__main__":
    unittest.main()
