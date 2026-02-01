#!/usr/bin/env node
import fs from "fs";
import path from "path";
import { createRequire } from "module";
import { createInterface } from "node:readline/promises";
import { stdin as input, stdout as output } from "node:process";
import { fileURLToPath, pathToFileURL } from "url";
import { execSync } from "child_process";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

let inquirerCache = null;
let chalkCache = null;

async function loadInquirer(projectRoot) {
    if (inquirerCache) return inquirerCache;

    try {
        const requireFromProject = createRequire(path.join(projectRoot, "package.json"));
        const resolved = requireFromProject.resolve("inquirer");
        const mod = await import(pathToFileURL(resolved).href);
        inquirerCache = mod.default ?? mod;
        return inquirerCache;
    } catch {
        try {
            const mod = await import("inquirer");
            inquirerCache = mod.default ?? mod;
            return inquirerCache;
        } catch {
            return null;
        }
    }
}

async function loadChalk(projectRoot) {
    if (chalkCache) return chalkCache;

    try {
        const requireFromProject = createRequire(path.join(projectRoot, "package.json"));
        const resolved = requireFromProject.resolve("chalk");
        const mod = await import(pathToFileURL(resolved).href);
        chalkCache = mod.default ?? mod;
        return chalkCache;
    } catch {
        try {
            const mod = await import("chalk");
            chalkCache = mod.default ?? mod;
            return chalkCache;
        } catch {
            return null;
        }
    }
}

async function promptText(message, validate) {
    const rl = createInterface({ input, output });
    const answer = (await rl.question(`${message} `)).trim();
    rl.close();

    if (validate) {
        const validation = validate(answer);
        if (validation !== true) {
            throw new Error(typeof validation === "string" ? validation : "Invalid input");
        }
    }

    return answer;
}

async function promptConfirm(message, defaultValue, projectRoot) {
    const inquirer = await loadInquirer(projectRoot);
    if (inquirer) {
        const result = await inquirer.prompt([
            {
                type: "confirm",
                name: "confirm",
                message,
                default: defaultValue,
            },
        ]);
        return result.confirm;
    }

    const suffix = defaultValue ? " [Y/n]" : " [y/N]";
    const answer = (await promptText(`${message}${suffix}`)).toLowerCase();
    if (!answer) return defaultValue;
    return ["y", "yes"].includes(answer);
}

async function promptCheckbox(message, choices, projectRoot) {
    const inquirer = await loadInquirer(projectRoot);
    if (inquirer) {
        const result = await inquirer.prompt([
            {
                type: "checkbox",
                name: "selections",
                message,
                choices,
                validate: (selected) => selected.length > 0 || "請至少選擇一項",
            },
        ]);
        return result.selections;
    }

    const answer = await promptText(`${message}（逗號分隔）`);
    const selected = answer
        .split(/[,\s]+/)
        .map((item) => item.trim())
        .filter(Boolean);

    if (selected.length === 0) {
        throw new Error("請至少選擇一項");
    }

    return selected;
}

function parseArgs(argv) {
    const options = {
        entity: null,
        selections: [],
        all: false,
        overwrite: false,
        installPrettier: false,
        skipFormat: false,
        projectRoot: null,
        templateRoot: null,
        nonInteractive: false,
    };

    const takeValue = (i) => (i + 1 < argv.length ? argv[i + 1] : null);

    for (let i = 0; i < argv.length; i += 1) {
        const arg = argv[i];
        if (!arg.startsWith("-")) continue;

        const [key, inlineValue] = arg.split("=");
        const value = inlineValue ?? takeValue(i);

        switch (key) {
            case "--entity":
            case "--name":
            case "-e":
                if (value && !value.startsWith("-")) {
                    options.entity = value;
                    if (!inlineValue) i += 1;
                }
                break;
            case "--types":
            case "--selections":
                if (value && !value.startsWith("-")) {
                    options.selections.push(
                        ...value
                            .split(/[,\s]+/)
                            .map((item) => item.trim())
                            .filter(Boolean),
                    );
                    if (!inlineValue) i += 1;
                }
                break;
            case "--all":
                options.all = true;
                break;
            case "--overwrite":
            case "--force":
                options.overwrite = true;
                break;
            case "--install-prettier":
                options.installPrettier = true;
                break;
            case "--skip-format":
                options.skipFormat = true;
                break;
            case "--project-root":
                if (value && !value.startsWith("-")) {
                    options.projectRoot = value;
                    if (!inlineValue) i += 1;
                }
                break;
            case "--template-root":
                if (value && !value.startsWith("-")) {
                    options.templateRoot = value;
                    if (!inlineValue) i += 1;
                }
                break;
            case "--non-interactive":
                options.nonInteractive = true;
                break;
            default:
                break;
        }
    }

    return options;
}

function resolveProjectRoot(explicitRoot) {
    if (explicitRoot) return path.resolve(explicitRoot);

    const cwd = process.cwd();
    if (fs.existsSync(path.join(cwd, "pom.xml"))) return cwd;

    const springRoot = path.join(cwd, "BE", "SpringBoot");
    if (fs.existsSync(path.join(springRoot, "pom.xml"))) return springRoot;

    return cwd;
}

function resolveTemplateRoot(explicitRoot, skillRoot, projectRoot) {
    if (explicitRoot) return path.resolve(explicitRoot);

    const skillTemplate = path.join(skillRoot, "assets", "be-template");
    if (fs.existsSync(skillTemplate)) return skillTemplate;

    const projectTemplate = path.join(projectRoot, "template");
    if (fs.existsSync(projectTemplate)) return projectTemplate;

    return null;
}

function ensureSpringRoot(root) {
    if (!fs.existsSync(path.join(root, "pom.xml"))) {
        throw new Error(
            `Spring Boot project root not found at: ${root}. Use --project-root to point at BE/SpringBoot.`,
        );
    }
}

const cliOptions = parseArgs(process.argv.slice(2));
const SKILL_ROOT = path.resolve(__dirname, "..");
const PROJECT_ROOT = resolveProjectRoot(cliOptions.projectRoot);
const TEMPLATE_ROOT = resolveTemplateRoot(cliOptions.templateRoot, SKILL_ROOT, PROJECT_ROOT);
const SRC_ROOT = path.join(PROJECT_ROOT, "src", "main", "java", "com", "penguin", "linknote");
const NON_INTERACTIVE =
    cliOptions.nonInteractive ||
    Boolean(
        cliOptions.entity ||
            cliOptions.selections.length ||
            cliOptions.all ||
            cliOptions.projectRoot ||
            cliOptions.templateRoot ||
            cliOptions.overwrite ||
            cliOptions.installPrettier ||
            cliOptions.skipFormat,
    );

// Colors and symbols
const colors = {
    cyan: (msg) => msg,
    green: (msg) => msg,
    yellow: (msg) => msg,
    red: (msg) => msg,
    magentaBright: (msg) => msg,
    greenBright: (msg) => msg,
};

const log = {
    info: (msg) => console.log(colors.cyan("ℹ️  " + msg)),
    success: (msg) => console.log(colors.green("✅ " + msg)),
    warn: (msg) => console.log(colors.yellow("⚠️  " + msg)),
    error: (msg) => console.log(colors.red("❌ " + msg)),
};

// --- Check Prettier Setup ---
async function ensurePrettierSetup(options) {
    const pkgPath = path.join(PROJECT_ROOT, "package.json");
    const prettierrcPath = path.join(PROJECT_ROOT, ".prettierrc");
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
        if (options.nonInteractive) {
            if (options.installPrettier) {
                execSync("pnpm add -D prettier prettier-plugin-java", {
                    stdio: "inherit",
                    cwd: PROJECT_ROOT,
                });
            } else {
                log.warn("已略過 prettier 安裝（可用 --install-prettier 開啟）。");
            }
        } else {
            const install = await promptConfirm(
                "是否安裝 prettier 與 prettier-plugin-java？",
                true,
                PROJECT_ROOT,
            );
            if (install) {
                execSync("pnpm add -D prettier prettier-plugin-java", {
                    stdio: "inherit",
                    cwd: PROJECT_ROOT,
                });
            }
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
async function copyFileWithReplace(src, dest, entity, options) {
    const content = fs.readFileSync(src, "utf-8");
    const replaced = replaceContent(content, entity);

    if (fs.existsSync(dest)) {
        if (options.overwrite) {
            log.warn(`覆蓋檔案：${dest}`);
        } else if (options.nonInteractive) {
            log.warn(`跳過：${dest}`);
            return;
        } else {
            const shouldOverwrite = await promptConfirm(
                `檔案已存在：${dest}，是否覆蓋？`,
                false,
                PROJECT_ROOT,
            );
            if (!shouldOverwrite) {
                log.warn(`跳過：${dest}`);
                return;
            }
        }
    }

    fs.mkdirSync(path.dirname(dest), { recursive: true });
    fs.writeFileSync(dest, replaced, "utf-8");
    log.success(`建立檔案：${dest}`);
}

// --- Recursively copy domain folder ---
async function copyDomainSample(entity, options) {
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
                    await copyFileWithReplace(srcEx, destEx, entity, options);
                }
            }
        } else {
            await copyFileWithReplace(srcPath, destPath, entity, options);
        }
    }
}

// --- Main ---
(async () => {
    const chalkInstance = await loadChalk(PROJECT_ROOT);
    if (chalkInstance) {
        colors.cyan = chalkInstance.cyan ?? colors.cyan;
        colors.green = chalkInstance.green ?? colors.green;
        colors.yellow = chalkInstance.yellow ?? colors.yellow;
        colors.red = chalkInstance.red ?? colors.red;
        colors.magentaBright = chalkInstance.magentaBright ?? colors.magentaBright;
        colors.greenBright = chalkInstance.greenBright ?? colors.greenBright;
    }

    console.log(colors.magentaBright("\n=== 🚀 Spring Boot Entity Generator ===\n"));

    try {
        ensureSpringRoot(PROJECT_ROOT);
    } catch (error) {
        console.error(`❌ ${error.message}`);
        process.exit(1);
    }

    if (!TEMPLATE_ROOT) {
        console.error("❌ 找不到 template 來源，請使用 --template-root 指定。");
        process.exit(1);
    }

    await ensurePrettierSetup({ nonInteractive: NON_INTERACTIVE, installPrettier: cliOptions.installPrettier });

    let entity = cliOptions.entity;
    let selections = cliOptions.selections;

    const normalizeSelection = (item) => {
        const lowered = item.toLowerCase();
        if (lowered.startsWith("repo")) return "repository";
        if (lowered.startsWith("svc") || lowered.startsWith("service")) return "service";
        if (lowered.startsWith("ctrl") || lowered.startsWith("controller")) return "controller";
        return lowered;
    };

    selections = selections.map(normalizeSelection);
    selections = [...new Set(selections)];

    if (!entity) {
        if (NON_INTERACTIVE) {
            console.error("❌ 缺少 entity 名稱，請使用 --entity 指定。");
            process.exit(1);
        }
        entity = await promptText("請輸入 Entity 名稱（英文）:", (inputValue) =>
            /^[A-Za-z]+$/.test(inputValue) || "請輸入英文名稱",
        );
    }

    if (!/^[A-Za-z]+$/.test(entity)) {
        console.error("❌ 請輸入英文名稱");
        process.exit(1);
    }

    if (!selections || selections.length === 0) {
        if (cliOptions.all || NON_INTERACTIVE) {
            selections = ["repository", "service", "controller"];
            log.info("未指定 selections，預設建立 repository/service/controller。");
        } else {
            selections = await promptCheckbox(
                "請選擇要建立的檔案類型：",
                ["repository", "service", "controller"],
                PROJECT_ROOT,
            );
        }
    }

    const entityLower = entity.toLowerCase();
    const entityUpper = entity.charAt(0).toUpperCase() + entity.slice(1);

    // --- repository ---
    if (selections.includes("repository")) {
        await copyDomainSample(entity, { overwrite: cliOptions.overwrite, nonInteractive: NON_INTERACTIVE });
        await copyFileWithReplace(
            path.join(TEMPLATE_ROOT, "repository", "sampleRepository.java"),
            path.join(SRC_ROOT, "repository", `${entityUpper}Repository.java`),
            entity,
            { overwrite: cliOptions.overwrite, nonInteractive: NON_INTERACTIVE },
        );
        await copyFileWithReplace(
            path.join(TEMPLATE_ROOT, "entity", "sampleEntity.java"),
            path.join(SRC_ROOT, "entity", `${entityUpper}.java`),
            entity,
            { overwrite: cliOptions.overwrite, nonInteractive: NON_INTERACTIVE },
        );
        await copyFileWithReplace(
            path.join(TEMPLATE_ROOT, "repository/impl", "sampleRepositoryImpl.java"),
            path.join(SRC_ROOT, "repository/impl", `${entityUpper}RepositoryImpl.java`),
            entity,
            { overwrite: cliOptions.overwrite, nonInteractive: NON_INTERACTIVE },
        );
        await copyFileWithReplace(
            path.join(TEMPLATE_ROOT, "repository/rowmapper", "sampleRowMapper.java"),
            path.join(SRC_ROOT, "repository/rowmapper", `${entityUpper}RowMapper.java`),
            entity,
            { overwrite: cliOptions.overwrite, nonInteractive: NON_INTERACTIVE },
        );
    }

    // --- service ---
    if (selections.includes("service")) {
        await copyFileWithReplace(
            path.join(TEMPLATE_ROOT, "service", "sampleService.java"),
            path.join(SRC_ROOT, "service", `${entityUpper}Service.java`),
            entity,
            { overwrite: cliOptions.overwrite, nonInteractive: NON_INTERACTIVE },
        );

        // 新增 ServiceImpl 複製
        await copyFileWithReplace(
            path.join(TEMPLATE_ROOT, "service/impl", "sampleServiceImpl.java"),
            path.join(SRC_ROOT, "service/impl", `${entityUpper}ServiceImpl.java`),
            entity,
            { overwrite: cliOptions.overwrite, nonInteractive: NON_INTERACTIVE },
        );
    }

    // --- controller ---
    if (selections.includes("controller")) {
        await copyFileWithReplace(
            path.join(TEMPLATE_ROOT, "controller", "sampleController.java"),
            path.join(SRC_ROOT, "controller", `${entityUpper}Controller.java`),
            entity,
            { overwrite: cliOptions.overwrite, nonInteractive: NON_INTERACTIVE },
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

    if (allNewFiles.length > 0 && !cliOptions.skipFormat) {
        log.info("格式化產生的 Java 檔案...");
        for (const file of allNewFiles) {
            try {
                execSync(`npx prettier --write "${file}"`, { cwd: PROJECT_ROOT });
                log.success(`格式化完成：${file}`);
            } catch {
                log.warn(`格式化失敗：${file}`);
            }
        }
    }

    console.log(colors.greenBright("\n✨ 所有作業完成！享受自動化開發的快感吧 😎\n"));
})();
