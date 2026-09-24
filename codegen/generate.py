"""Generate and verify the low-level SDKs from the canonical OpenAPI contract."""

from __future__ import annotations

import argparse
import hashlib
import json
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path
from typing import Any


TOOL_NAME = "newnanmanager-openapi-codegen"
MARKER_NAME = ".nnm-generated.json"
IGNORED_OUTPUT_PARTS = {
    ".gradle",
    "__pycache__",
    "bin",
    "build",
    "dist",
    "node_modules",
    "obj",
    "target",
}
IGNORED_OUTPUT_SUFFIXES = {".pyc", ".tsbuildinfo"}
IGNORED_OUTPUT_FILES = {"gradle/wrapper/gradle-wrapper.jar"}


class CodegenError(RuntimeError):
    """Raised when generation cannot be completed safely."""


def digest_bytes(path: Path) -> bytes:
    """Read bytes for a cross-platform text-oriented generated tree digest."""

    return path.read_bytes().replace(b"\r\n", b"\n").replace(b"\r", b"\n")


def load_manifest(path: Path) -> dict[str, Any]:
    """Load and validate the checked-in generator manifest."""

    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        raise CodegenError(f"无法读取生成器清单: {path}: {exc}") from exc
    if not isinstance(data, dict) or not isinstance(data.get("languages"), dict):
        raise CodegenError("生成器清单缺少 languages 对象")
    return data


def resolve_npx() -> str:
    """Return the platform-specific npx executable."""

    executable = shutil.which("npx.cmd") or shutil.which("npx")
    if executable is None:
        raise CodegenError("未找到 npx；需要 Node.js/npm 才能运行 OpenAPI Generator")
    return executable


def run_command(command: list[str], *, cwd: Path) -> str:
    """Run a command and return combined output on success."""

    try:
        result = subprocess.run(
            command,
            cwd=cwd,
            check=False,
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
        )
    except OSError as exc:
        raise CodegenError(f"无法启动命令 {command[0]}: {exc}") from exc
    output = f"{result.stdout}{result.stderr}"
    if result.returncode != 0:
        raise CodegenError(
            f"命令失败（退出码 {result.returncode}）:\n"
            f"{' '.join(command)}\n{output.strip()}"
        )
    return output


def cli_base(manifest: dict[str, Any], npx: str) -> list[str]:
    """Build the pinned OpenAPI Generator CLI prefix."""

    generator = manifest["generator"]
    return [
        npx,
        "--yes",
        f"{generator['cliPackage']}@{generator['cliVersion']}",
    ]


def verify_engine_version(manifest: dict[str, Any], npx: str, sdk_root: Path) -> None:
    """Fail if the pinned npm wrapper resolves a different engine version."""

    output = run_command([*cli_base(manifest, npx), "version"], cwd=sdk_root)
    clean = re.sub(r"\x1b\[[0-9;]*m", "", output)
    versions = [line.strip() for line in clean.splitlines() if line.strip()]
    expected = str(manifest["generator"]["engineVersion"])
    if not versions or versions[-1] != expected:
        raise CodegenError(
            f"OpenAPI Generator 版本不一致：期望 {expected}，实际输出为 {versions[-1:] or '空'}"
        )


def validate_contract(manifest: dict[str, Any], npx: str, sdk_root: Path, contract: Path) -> None:
    """Run the generator's structural validation before generation."""

    output = run_command(
        [*cli_base(manifest, npx), "validate", "-i", str(contract)],
        cwd=sdk_root,
    )
    if "Errors:" in output:
        raise CodegenError(f"OpenAPI 契约验证报告 errors:\n{output}")


def tree_digest(root: Path) -> dict[str, str]:
    """Return deterministic SHA-256 values for every regular file below root."""

    result: dict[str, str] = {}
    if not root.exists():
        return result
    for path in sorted(path for path in root.rglob("*") if path.is_file()):
        relative_path = path.relative_to(root)
        if set(relative_path.parts) & IGNORED_OUTPUT_PARTS:
            continue
        if path.suffix in IGNORED_OUTPUT_SUFFIXES:
            continue
        relative = relative_path.as_posix()
        if relative in IGNORED_OUTPUT_FILES:
            continue
        result[relative] = hashlib.sha256(digest_bytes(path)).hexdigest()
    return result


def manifest_digest(manifest_path: Path) -> str:
    """Hash the canonical manifest bytes used by generation."""

    return hashlib.sha256(digest_bytes(manifest_path)).hexdigest()


def marker_payload(
    manifest: dict[str, Any], manifest_path: Path, language: str, contract: Path
) -> dict[str, str]:
    """Build the ownership marker stored beside generated files."""

    config = manifest["languages"][language]
    configured_contract = (manifest_path.parent / str(manifest["contract"])).resolve()
    if contract.resolve() == configured_contract:
        contract_reference = Path(str(manifest["contract"])).as_posix()
    else:
        contract_reference = contract.as_posix()
    return {
        "tool": TOOL_NAME,
        "language": language,
        "generator": str(config["generator"]),
        "cliPackage": str(manifest["generator"]["cliPackage"]),
        "cliVersion": str(manifest["generator"]["cliVersion"]),
        "engineVersion": str(manifest["generator"]["engineVersion"]),
        "manifestSha256": manifest_digest(manifest_path),
        "contract": contract_reference,
    }


def write_marker(root: Path, payload: dict[str, str]) -> None:
    """Write the deterministic ownership marker."""

    (root / MARKER_NAME).write_text(
        json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8"
    )


def read_marker(root: Path) -> dict[str, Any] | None:
    """Read a generated-output ownership marker, if present."""

    path = root / MARKER_NAME
    if not path.is_file():
        return None
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError):
        return None
    return data if isinstance(data, dict) else None


def build_generate_command(
    manifest: dict[str, Any],
    npx: str,
    language: str,
    contract: Path,
    output: Path,
) -> list[str]:
    """Build the language-specific generation command."""

    config = manifest["languages"][language]
    properties = config.get("properties", {})
    property_text = ",".join(
        f"{key}={properties[key]}" for key in sorted(properties)
    )
    ignore_list = list(manifest["generator"].get("ignoreList", []))
    ignore_list.extend(str(item) for item in config.get("ignoreList", []))
    return [
        *cli_base(manifest, npx),
        "generate",
        "-i",
        str(contract),
        "-g",
        str(config["generator"]),
        "-o",
        str(output),
        "--additional-properties",
        property_text,
        "--global-property",
        ",".join(manifest["generator"]["globalProperty"]),
        "--git-user-id",
        str(manifest["generator"]["gitUserId"]),
        "--git-repo-id",
        str(manifest["generator"]["gitRepoId"]),
        "--ignore-file-override",
        str(Path(__file__).with_name(str(manifest["generator"]["ignoreFile"]))),
        "--openapi-generator-ignore-list",
        ",".join(dict.fromkeys(ignore_list)),
        "--skip-validate-spec",
    ]


def ensure_safe_output(output: Path, language: str, *, force: bool) -> None:
    """Protect existing non-generated files from accidental replacement."""

    if not output.exists() or not any(output.iterdir()):
        return
    marker = read_marker(output)
    if not force:
        raise CodegenError(
            f"输出目录非空：{output}。使用 --force 前必须确认其中只有本工具生成的文件。"
        )
    if (
        marker is None
        or marker.get("tool") != TOOL_NAME
        or marker.get("language") != language
    ):
        raise CodegenError(f"拒绝覆盖没有 {MARKER_NAME} 所有权标记的目录：{output}")
    shutil.rmtree(output)


def postprocess_generated_output(
    manifest: dict[str, Any], language: str, output: Path
) -> None:
    """Apply deterministic metadata fixes that the upstream templates cannot parameterize."""

    if language == "kotlin":
        postprocess_kotlin_output(manifest, output)
        return
    if language != "golang":
        return
    module_path = str(manifest["languages"][language]["modulePath"])
    go_mod = output / "go.mod"
    readme = output / "README.md"
    if not go_mod.is_file() or not readme.is_file():
        raise CodegenError("Go 生成物缺少 go.mod 或 README.md，无法应用模块元数据")
    module_text = go_mod.read_text(encoding="utf-8")
    rewritten, count = re.subn(
        r"^module\s+[^\r\n]+",
        f"module {module_path}",
        module_text,
        count=1,
        flags=re.MULTILINE,
    )
    if count != 1:
        raise CodegenError("Go 生成物的 module 声明格式发生变化，拒绝猜测替换")
    go_version = str(manifest["languages"][language].get("goVersion", "1.25.0"))
    rewritten, version_count = re.subn(
        r"^go\s+[^\r\n]+",
        f"go {go_version}",
        rewritten,
        count=1,
        flags=re.MULTILINE,
    )
    if version_count != 1:
        raise CodegenError("Go 生成物的 go 版本声明格式发生变化，拒绝猜测替换")
    go_mod.write_text(rewritten, encoding="utf-8")
    readme_text = readme.read_text(encoding="utf-8")
    placeholder = "github.com/NewNanCity/NewNanManager-SDK"
    import_line = f'import newnanmanagerclient "{placeholder}"'
    if import_line not in readme_text:
        raise CodegenError("Go 生成 README 的 import 示例格式发生变化，拒绝猜测替换")
    readme.write_text(
        readme_text.replace(import_line, f'import newnanmanagerclient "{module_path}"', 1),
        encoding="utf-8",
    )


def replace_once(text: str, pattern: str, replacement: str, *, label: str) -> str:
    """Replace one known generator fragment and fail if the template moved."""

    rewritten, count = re.subn(
        pattern, replacement, text, count=1, flags=re.MULTILINE | re.DOTALL
    )
    if count != 1:
        raise CodegenError(f"Kotlin 生成模板缺少预期片段: {label}")
    return rewritten


def postprocess_kotlin_output(manifest: dict[str, Any], output: Path) -> None:
    """Pin Kotlin's generated build to the repository's verified toolchain."""

    config = manifest["languages"]["kotlin"]
    build_config = config.get("build")
    if not isinstance(build_config, dict):
        raise CodegenError("Kotlin 清单缺少 build 版本配置")
    required_keys = {"kotlinVersion", "jacksonVersion", "okhttpVersion", "gradleVersion"}
    if not required_keys.issubset(build_config):
        raise CodegenError("Kotlin build 配置缺少固定版本")

    build_gradle = output / "build.gradle"
    wrapper_properties = output / "gradle" / "wrapper" / "gradle-wrapper.properties"
    readme = output / "README.md"
    for path in (build_gradle, wrapper_properties, readme):
        if not path.is_file():
            raise CodegenError(f"Kotlin 生成物缺少构建文件: {path.name}")

    build_text = build_gradle.read_text(encoding="utf-8")
    kotlin_version = str(build_config["kotlinVersion"])
    jackson_version = str(build_config["jacksonVersion"])
    okhttp_version = str(build_config["okhttpVersion"])
    gradle_version = str(build_config["gradleVersion"])
    plugins_block = (
        "plugins {\n"
        f"    id 'org.jetbrains.kotlin.jvm' version '{kotlin_version}'\n"
        "    id 'maven-publish'\n"
        "}\n"
    )
    build_text = replace_once(
        build_text,
        r"buildscript \{.*?\n\}\n\n",
        plugins_block + "\n",
        label="buildscript block",
    )
    build_text = replace_once(
        build_text,
        r"\napply plugin: 'kotlin'\napply plugin: 'maven-publish'\n",
        "\n",
        label="legacy Kotlin plugins",
    )
    build_text = replace_once(
        build_text,
        r"\napply plugin: 'com\.diffplug\.spotless'\n",
        "\n",
        label="spotless plugin",
    )
    build_text = replace_once(
        build_text,
        r'maven \{ url "https://repo1\.maven\.org/maven2" \}',
        "mavenCentral()",
        label="Maven Central repository",
    )
    build_text = replace_once(
        build_text,
        r"plugins \{.*?\n\}\n\n",
        "",
        label="plugins block extraction",
    )
    build_text = plugins_block + "\n" + build_text.lstrip()
    if "$kotlin_version" not in build_text:
        raise CodegenError("Kotlin 生成模板缺少 kotlin_version 依赖占位符")
    build_text = build_text.replace("$kotlin_version", kotlin_version)
    build_text = replace_once(
        build_text,
        r"gradleVersion\s*=\s*'[^']+'",
        f"gradleVersion = '{gradle_version}'",
        label="Gradle build version",
    )
    build_text = replace_once(
        build_text,
        r'distributionUrl\s*=\s*"https://services\.gradle\.org/distributions/gradle-\$gradleVersion-all\.zip"',
        'distributionUrl = "https://services.gradle.org/distributions/gradle-$gradleVersion-bin.zip"',
        label="Gradle build distribution",
    )
    build_text = replace_once(
        build_text,
        r"\n// Use spotless plugin.*?\n\ntest \{",
        "\n\ntest {",
        label="spotless comments",
    )
    build_text = replace_once(
        build_text,
        r"com\.fasterxml\.jackson\.module:jackson-module-kotlin:[^\"']+",
        f"com.fasterxml.jackson.module:jackson-module-kotlin:{jackson_version}",
        label="jackson-module-kotlin",
    )
    build_text = replace_once(
        build_text,
        r"com\.fasterxml\.jackson\.datatype:jackson-datatype-jsr310:[^\"']+",
        f"com.fasterxml.jackson.datatype:jackson-datatype-jsr310:{jackson_version}",
        label="jackson-datatype-jsr310",
    )
    build_text = replace_once(
        build_text,
        r"com\.squareup\.okhttp3:okhttp:[^\"']+",
        f"com.squareup.okhttp3:okhttp:{okhttp_version}",
        label="okhttp",
    )
    build_text = replace_once(
        build_text,
        r'implementation "org\.jetbrains\.kotlin:kotlin-stdlib-jdk8:[^\"]+"',
        f'implementation "org.jetbrains.kotlin:kotlin-stdlib:{kotlin_version}"',
        label="Kotlin stdlib",
    )
    for pattern, label in (
        (r'\n\s*implementation "org\.jetbrains\.kotlin:kotlin-reflect:[^\"]+"', "Kotlin reflect"),
        (r'\n\s*implementation "com\.fasterxml\.jackson\.datatype:jackson-datatype-jsr310:[^\"]+"', "Jackson jsr310"),
        (r'\n\s*testImplementation "io\.kotlintest:kotlintest-runner-junit5:[^\"]+"', "Kotlin test dependency"),
    ):
        build_text = replace_once(build_text, pattern, "", label=label)
    build_gradle.write_text(build_text, encoding="utf-8")

    wrapper_text = wrapper_properties.read_text(encoding="utf-8")
    wrapper_text = replace_once(
        wrapper_text,
        r"^distributionUrl=.*$",
        f"distributionUrl=https\\://services.gradle.org/distributions/gradle-{build_config['gradleVersion']}-bin.zip",
        label="Gradle wrapper",
    )
    wrapper_properties.write_text(wrapper_text, encoding="utf-8")

    readme_text = readme.read_text(encoding="utf-8")
    readme_text = readme_text.replace("* Kotlin 2.4.0", f"* Kotlin {kotlin_version}")
    readme_text = readme_text.replace("* Gradle 8.14", f"* Gradle {build_config['gradleVersion']}")
    readme.write_text(readme_text, encoding="utf-8")


def generate_to_directory(
    manifest: dict[str, Any],
    manifest_path: Path,
    sdk_root: Path,
    npx: str,
    language: str,
    contract: Path,
    output: Path,
    *,
    force: bool,
) -> dict[str, str]:
    """Generate one language into a safe, owned output directory."""

    ensure_safe_output(output, language, force=force)
    output.parent.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix=f"nnm-codegen-{language}-") as temporary:
        temporary_output = Path(temporary) / language
        run_command(
            build_generate_command(manifest, npx, language, contract, temporary_output),
            cwd=sdk_root,
        )
        postprocess_generated_output(manifest, language, temporary_output)
        shutil.copytree(temporary_output, output)
    write_marker(output, marker_payload(manifest, manifest_path, language, contract))
    return tree_digest(output)


def generated_tree(
    manifest: dict[str, Any],
    sdk_root: Path,
    npx: str,
    language: str,
    contract: Path,
) -> dict[str, str]:
    """Generate one language in a temporary directory and return its digest."""

    with tempfile.TemporaryDirectory(prefix=f"nnm-codegen-check-{language}-") as temporary:
        temporary_output = Path(temporary) / language
        run_command(
            build_generate_command(manifest, npx, language, contract, temporary_output),
            cwd=sdk_root,
        )
        postprocess_generated_output(manifest, language, temporary_output)
        write_marker(
            temporary_output,
            marker_payload(manifest, Path(manifest["_manifestPath"]), language, contract),
        )
        return tree_digest(temporary_output)


def parse_args() -> argparse.Namespace:
    """Parse command-line arguments."""

    parser = argparse.ArgumentParser(description=__doc__)
    selection = parser.add_mutually_exclusive_group(required=True)
    selection.add_argument("--all", action="store_true", help="处理六种语言")
    selection.add_argument(
        "--language",
        choices=["typescript", "golang", "python", "csharp", "kotlin", "java"],
    )
    parser.add_argument("--check", action="store_true", help="只生成到临时目录并比较已有输出")
    parser.add_argument("--validate-only", action="store_true", help="只验证 OpenAPI 契约")
    parser.add_argument("--force", action="store_true", help="仅覆盖带所有权标记的生成目录")
    parser.add_argument("--input", type=Path, help="覆盖 manifest 中的契约路径")
    parser.add_argument("--output", type=Path, default=Path("generated"), help="SDK 根输出目录")
    return parser.parse_args()


def main() -> int:
    """Run validation, generation, or drift checking."""

    args = parse_args()
    sdk_root = Path(__file__).resolve().parents[1]
    manifest_path = Path(__file__).with_name("manifest.json")
    manifest = load_manifest(manifest_path)
    manifest["_manifestPath"] = str(manifest_path)
    contract_value = args.input or (Path(__file__).parent / manifest["contract"])
    contract = contract_value.resolve()
    if not contract.is_file():
        raise CodegenError(f"找不到 OpenAPI 契约：{contract}")

    npx = resolve_npx()
    verify_engine_version(manifest, npx, sdk_root)
    validate_contract(manifest, npx, sdk_root, contract)
    if args.validate_only:
        print(f"OpenAPI contract valid: {contract}")
        return 0

    languages = list(manifest["languages"]) if args.all else [args.language]
    output_root = args.output if args.output.is_absolute() else sdk_root / args.output
    failures: list[str] = []
    for language in languages:
        output = output_root / language
        if args.check:
            if not output.is_dir():
                failures.append(f"{language}: 输出目录不存在 {output}")
                continue
            expected = generated_tree(manifest, sdk_root, npx, language, contract)
            actual = tree_digest(output)
            if expected != actual:
                expected_paths = set(expected)
                actual_paths = set(actual)
                added = sorted(expected_paths - actual_paths)
                removed = sorted(actual_paths - expected_paths)
                changed = sorted(
                    path
                    for path in expected_paths & actual_paths
                    if expected[path] != actual[path]
                )
                failures.append(
                    f"{language}: 生成漂移 added={added[:5]} removed={removed[:5]} changed={changed[:5]}"
                )
            else:
                print(f"{language}: generated output is up to date")
            continue
        digest = generate_to_directory(
            manifest,
            manifest_path,
            sdk_root,
            npx,
            language,
            contract,
            output,
            force=args.force,
        )
        print(f"{language}: generated {len(digest)} files at {output}")

    if failures:
        for failure in failures:
            print(failure, file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except CodegenError as exc:
        print(f"codegen error: {exc}", file=sys.stderr)
        raise SystemExit(2) from exc
