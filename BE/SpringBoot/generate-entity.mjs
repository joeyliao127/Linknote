#!/usr/bin/env node
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";
import inquirer from "inquirer";
import chalk from "chalk";
import { execSync } from "child_process";

// Resolve root directory
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const ROOT = __dirname; // 現在這層與 package.json 同層
const SRC_ROOT = path.join(ROOT, "src", "main", "java", "com", "penguin", "linknote");
const TEMPLATE_ROOT = path.join(ROOT, "template");

// Colors and symbols
const log = {
    info: (msg) => console.log(chalk.cyan("ℹ️  " + msg)),
    success: (msg) => console.log(chalk.green("✅ " + msg)),
    warn: (msg) => console.log(chalk.yellow("⚠️  " + msg)),
    error: (msg) => console.log(chalk.red("❌ " + msg)),
};

// --- Check Prettier Setup ---
async function ensurePrettierSetup() {
    const pkgPath = path.join(ROOT, "package.json");
    const prettierrcPath = path.join(ROOT, ".prettierrc");
    let needInstall = false;

    if (!fs.existsSync(pkgPath)) {
        fs.writeFileSync(pkgPath, JSON.stringify({ name: "java-generator", type: "module" }, null, 4));
        log.success("已建立 package.json");
    }

    const pkg = JSON.parse(fs.readFileSync(pkgPath, "utf-8"));
    const deps = { ...pkg.dependencies, ...pkg.devDependencies };

    if (!deps?.prettier || !deps?.["prettier-plugin-java"]) {
        log.warn("未偵測到 prettier 或 prettier-plugin-java。");
        needInstall = true;
    }

    if (!fs.existsSync(prettierrcPath)) {
        fs.writeFileSync(
            prettierrcPath,
            JSON.stringify(
                {
                    plugins: ["prettier-plugin-java"],
                    tabWidth: 4,
                    printWidth: 120,
                    useTabs: false,
                    semi: true,
                },
                null,
                4,
            ),
        );
        log.success("已建立 .prettierrc 設定檔。");
    }

    if (needInstall) {
        const { install } = await inquirer.prompt([
            {
                type: "confirm",
                name: "install",
                message: "是否安裝 prettier 與 prettier-plugin-java？",
                default: true,
            },
        ]);
        if (install) {
            execSync("pnpm add -D prettier prettier-plugin-java", {
                stdio: "inherit",
            });
        }
    }
}

// --- Replace Utility ---
function replaceContent(content, entity) {
    const upper = entity.charAt(0).toUpperCase() + entity.slice(1);
    const lower = entity.toLowerCase();
    return content.replaceAll("!{upper}", upper).replaceAll("!{lower}", lower);
}

// --- Copy with replacement ---
async function copyFileWithReplace(src, dest, entity) {
    const content = fs.readFileSync(src, "utf-8");
    const replaced = replaceContent(content, entity);

    if (fs.existsSync(dest)) {
        const { overwrite } = await inquirer.prompt([
            {
                type: "confirm",
                name: "overwrite",
                message: chalk.yellow(`檔案已存在：${dest}，是否覆蓋？`),
                default: false,
            },
        ]);
        if (!overwrite) {
            log.warn(`跳過：${dest}`);
            return;
        }
    }

    fs.mkdirSync(path.dirname(dest), { recursive: true });
    fs.writeFileSync(dest, replaced, "utf-8");
    log.success(`建立檔案：${dest}`);
}

// --- Recursively copy domain folder ---
async function copyDomainSample(entity) {
    const upper = entity.charAt(0).toUpperCase() + entity.slice(1);
    const lower = entity.toLowerCase();

    const srcDir = path.join(TEMPLATE_ROOT, "domain", "sample");
    const destDir = path.join(SRC_ROOT, "domain", lower);

    fs.mkdirSync(destDir, { recursive: true });

    const entries = fs.readdirSync(srcDir, { withFileTypes: true });

    for (const entry of entries) {
        const srcPath = path.join(srcDir, entry.name);
        let destPath = path.join(destDir, entry.name.replace("sample", upper));

        if (entry.isDirectory()) {
            if (entry.name === "exception") {
                const exceptionDir = path.join(destDir, "exception");
                fs.mkdirSync(exceptionDir, { recursive: true });
                const exFiles = fs.readdirSync(srcPath);
                for (const f of exFiles) {
                    const srcEx = path.join(srcPath, f);
                    const destEx = path.join(exceptionDir, f.replace("Sample", upper));
                    await copyFileWithReplace(srcEx, destEx, entity);
                }
            }
        } else {
            await copyFileWithReplace(srcPath, destPath, entity);
        }
    }
}

// --- Main ---
(async () => {
    console.log(chalk.magentaBright("\n=== 🚀 Spring Boot Entity Generator ===\n"));

    await ensurePrettierSetup();

    const { entity, selections } = await inquirer.prompt([
        {
            type: "input",
            name: "entity",
            message: "請輸入 Entity 名稱（英文）:",
            validate: (input) => /^[A-Za-z]+$/.test(input) || "請輸入英文名稱",
        },
        {
            type: "checkbox",
            name: "selections",
            message: "請選擇要建立的檔案類型：",
            choices: ["repository", "service", "controller"],
            validate: (choices) => choices.length > 0 || "請至少選擇一項",
        },
    ]);

    const entityLower = entity.toLowerCase();
    const entityUpper = entity.charAt(0).toUpperCase() + entity.slice(1);

    // --- repository ---
    if (selections.includes("repository")) {
        await copyDomainSample(entity);
        await copyFileWithReplace(
            path.join(TEMPLATE_ROOT, "repository", "sampleRepository.java.disabled"),
            path.join(SRC_ROOT, "repository", `${entityUpper}Repository.java`),
            entity,
        );
        await copyFileWithReplace(
            path.join(TEMPLATE_ROOT, "entity", "sampleEntity.java"),
            path.join(SRC_ROOT, "entity", `${entityUpper}.java`),
            entity,
        );
        await copyFileWithReplace(
            path.join(TEMPLATE_ROOT, "repository/impl", "sampleRepositoryImpl.java"),
            path.join(SRC_ROOT, "repository/impl", `${entityUpper}RepositoryImpl.java`),
            entity,
        );
        await copyFileWithReplace(
            path.join(TEMPLATE_ROOT, "repository/rowmapper", "sampleRowMapper.java"),
            path.join(SRC_ROOT, "repository/rowmapper", `${entityUpper}RowMapper.java`),
            entity,
        );
    }

    // --- service ---
    if (selections.includes("service")) {
        await copyFileWithReplace(
            path.join(TEMPLATE_ROOT, "service", "sampleService.java"),
            path.join(SRC_ROOT, "service", `${entityUpper}Service.java`),
            entity,
        );

        // 新增 ServiceImpl 複製
        await copyFileWithReplace(
            path.join(TEMPLATE_ROOT, "service/impl", "sampleServiceImpl.java"),
            path.join(SRC_ROOT, "service/impl", `${entityUpper}ServiceImpl.java`),
            entity,
        );
    }

    // --- controller ---
    if (selections.includes("controller")) {
        await copyFileWithReplace(
            path.join(TEMPLATE_ROOT, "controller", "sampleController.java"),
            path.join(SRC_ROOT, "controller", `${entityUpper}Controller.java`),
            entity,
        );
    }

    // --- format new java files ---
    const allNewFiles = [];
    const addFilesRecursively = (dir) => {
        const entries = fs.readdirSync(dir, { withFileTypes: true });
        for (const e of entries) {
            const full = path.join(dir, e.name);
            if (e.isDirectory()) addFilesRecursively(full);
            else if (full.endsWith(".java")) allNewFiles.push(full);
        }
    };
    addFilesRecursively(path.join(SRC_ROOT, "domain", entityLower));
    if (selections.includes("repository"))
        allNewFiles.push(
            path.join(SRC_ROOT, "repository", `${entityUpper}Repository.java`),
            path.join(SRC_ROOT, "entity", `${entityUpper}.java`),
            path.join(SRC_ROOT, "repository/impl", `${entityUpper}RepositoryImpl.java`),
            path.join(SRC_ROOT, "repository/rowmapper", `${entityUpper}RowMapper.java`),
        );
    if (selections.includes("service"))
        allNewFiles.push(
            path.join(SRC_ROOT, "service", `${entityUpper}Service.java`),
            path.join(SRC_ROOT, "service/impl", `${entityUpper}ServiceImpl.java`),
        );
    if (selections.includes("controller"))
        allNewFiles.push(path.join(SRC_ROOT, "controller", `${entityUpper}Controller.java`));

    if (allNewFiles.length > 0) {
        log.info("格式化產生的 Java 檔案...");
        for (const file of allNewFiles) {
            try {
                execSync(`npx prettier --write "${file}"`, { cwd: ROOT });
                log.success(`格式化完成：${file}`);
            } catch {
                log.warn(`格式化失敗：${file}`);
            }
        }
    }

    console.log(chalk.greenBright("\n✨ 所有作業完成！享受自動化開發的快感吧 😎\n"));
})();
