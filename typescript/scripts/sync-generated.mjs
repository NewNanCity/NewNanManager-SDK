import { cpSync, existsSync, mkdirSync, readFileSync, rmSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const scriptDirectory = dirname(fileURLToPath(import.meta.url));
const packageDirectory = resolve(scriptDirectory, '..');
const sourceDirectory = resolve(packageDirectory, '..', 'generated', 'typescript');
const targetDirectory = resolve(packageDirectory, 'src', 'generated');
const markerPath = resolve(sourceDirectory, '.nnm-generated.json');
const generatedFiles = ['api.ts', 'base.ts', 'common.ts', 'configuration.ts', 'index.ts'];

if (!existsSync(markerPath)) {
  throw new Error(`generated TypeScript marker is missing: ${markerPath}`);
}

const marker = JSON.parse(readFileSync(markerPath, 'utf8'));
if (marker.language !== 'typescript') {
  throw new Error(`unexpected generated language: ${String(marker.language)}`);
}

rmSync(targetDirectory, { recursive: true, force: true });
mkdirSync(targetDirectory, { recursive: true });
for (const fileName of generatedFiles) {
  cpSync(resolve(sourceDirectory, fileName), resolve(targetDirectory, fileName));
}

console.log(`synced ${generatedFiles.length} generated TypeScript files`);
