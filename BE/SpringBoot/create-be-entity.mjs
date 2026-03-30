#!/usr/bin/env node
import { spawn } from "node:child_process";
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const target = path.resolve(
    __dirname,
    "..",
    "..",
    ".codex",
    "skills",
    "skill-penguin",
    "scripts",
    "create-be-entity.mjs",
);

if (!fs.existsSync(target)) {
    console.error(`❌ 找不到腳本：${target}`);
    process.exit(1);
}

const child = spawn(process.execPath, [target, ...process.argv.slice(2)], {
    stdio: "inherit",
});

child.on("exit", (code) => process.exit(code ?? 1));
