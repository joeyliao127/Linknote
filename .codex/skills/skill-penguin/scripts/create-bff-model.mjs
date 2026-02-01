#!/usr/bin/env node
import fs from "node:fs";
import path from "node:path";
import { createRequire } from "module";
import { createInterface } from "node:readline/promises";
import { stdin as input, stdout as output } from "node:process";
import { fileURLToPath, pathToFileURL } from "node:url";

/**
 * ============================
 * Env / Utils
 * ============================
 */

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
let inquirerCache = null;

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
function parseArgs(argv) {
    const options = {
        modelName: null,
        projectRoot: null,
    };

    const takeValue = (i) => (i + 1 < argv.length ? argv[i + 1] : null);

    for (let i = 0; i < argv.length; i += 1) {
        const arg = argv[i];
        if (!arg.startsWith("-")) continue;

        const [key, inlineValue] = arg.split("=");
        const value = inlineValue ?? takeValue(i);

        switch (key) {
            case "--model":
            case "--name":
            case "-m":
                if (value && !value.startsWith("-")) {
                    options.modelName = value;
                    if (!inlineValue) i += 1;
                }
                break;
            case "--project-root":
                if (value && !value.startsWith("-")) {
                    options.projectRoot = value;
                    if (!inlineValue) i += 1;
                }
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
    if (fs.existsSync(path.join(cwd, "nuxt.config.ts"))) return cwd;

    const feNuxt = path.join(cwd, "FE", "Nuxt");
    if (fs.existsSync(path.join(feNuxt, "nuxt.config.ts"))) return feNuxt;

    return cwd;
}

function ensureNuxtRoot(root) {
    if (!fs.existsSync(path.join(root, "nuxt.config.ts"))) {
        throw new Error(
            `Nuxt project root not found at: ${root}. Use --project-root to point at FE/Nuxt.`,
        );
    }
}

const cliOptions = parseArgs(process.argv.slice(2));
const projectRoot = resolveProjectRoot(cliOptions.projectRoot);

const capitalize = (str) => str.charAt(0).toUpperCase() + str.slice(1);

const replaceTemplate = (template, vars) =>
    template
        .replace(/!\{upper\}/g, vars.upper)
        .replace(/!\{lower\}/g, vars.lower);

/**
 * ============================
 * Templates
 * ============================
 */

const composableTemplate = `
import { useRuntimeConfig } from "#imports";
import type { Pagination } from "~~/types";
import type { !{upper}, Create!{upper}DTO, Update!{upper}DTO } from "~~/types/!{upper}";

const _use!{upper} = () => {
    const runtimeConfig = useRuntimeConfig();
    const baseURL = \`\${runtimeConfig.public.API_URL}/!{lower}s\`;

    const index!{upper} = async (): Promise<Pagination<!{upper}>> => {
        const response: Pagination<!{upper}> = await $fetch(baseURL, {
            method: "GET"
        });

        return response;
    };

    const create!{upper} = async (!{lower}: Create!{upper}DTO) => {
        const response: Pagination<!{upper}> = await $fetch(baseURL, {
            method: "POST",
            body: !{lower},
        });

        return response;
    };

    const update!{upper} = async (!{lower}Id: string, !{lower}: Update!{upper}DTO) => {
        const response: Pagination<!{upper}> = await $fetch(
            \`\${baseURL}/\${!{lower}Id}\`,
            {
                method: "PUT",
                body: !{lower},
            }
        );

        return response;
    };

    const delete!{upper} = async (!{lower}Id: string) => {
        await $fetch(\`\${baseURL}/\${!{lower}Id}\`, {
            method: "DELETE"
        });
    };

    return {
        index!{upper},
        create!{upper},
        update!{upper},
        delete!{upper},
    };
};

export const use!{upper} = _use!{upper};
`.trim();

const typeTemplate = `
import type { Pagination } from ".";

// TODO: add fileds
export interface !{upper} {
    createdAt: Date | string;
    updatedAt: Date | string;
}

// TODO: omit fileds
export type Create!{upper}DTO = Omit<
    !{upper},
    "id" | "createdAt" | "updatedAt"
>;

// TODO: omit fileds
export type Update!{upper}DTO = Omit<
    !{upper},
    "id" | "createdAt" | "updatedAt"
>;
`.trim();

/**
 * ============================
 * Generators
 * ============================
 */

function generateComposable({ upper, lower }) {
    const composableDir = path.join(projectRoot, "app/composables/model");
    const composableFile = path.join(composableDir, `use${upper}.ts`);

    fs.mkdirSync(composableDir, { recursive: true });

    const content = replaceTemplate(composableTemplate, {
        upper,
        lower,
    });

    fs.writeFileSync(composableFile, content, "utf8");

    return composableFile;
}

function generateType({ upper, lower }) {
    const typesDir = path.join(projectRoot, "types");
    const typeFile = path.join(typesDir, `${upper}.d.ts`);

    fs.mkdirSync(typesDir, { recursive: true });

    const content = replaceTemplate(typeTemplate, {
        upper,
        lower,
    });

    fs.writeFileSync(typeFile, content, "utf8");

    return typeFile;
}

/**
 * ============================
 * Main
 * ============================
 */

async function main() {
    console.log(`
========================================
 🚀 MVVM Model 產生器
========================================
`);

    let modelName = cliOptions.modelName;

    if (!modelName) {
        const inquirer = await loadInquirer(projectRoot);
        if (inquirer) {
            const promptResult = await inquirer.prompt([
                {
                    type: "input",
                    name: "modelName",
                    message: "請輸入 model 名稱（例如：notebook）",
                    validate(inputValue) {
                        if (!inputValue) return "model 名稱不能為空";
                        if (!/^[a-zA-Z][a-zA-Z0-9]*$/.test(inputValue)) {
                            return "請輸入合法的命名（英文字母開頭）";
                        }
                        return true;
                    },
                },
            ]);
            modelName = promptResult.modelName;
        } else {
            modelName = await promptText("請輸入 model 名稱（例如：notebook）", (inputValue) => {
                if (!inputValue) return "model 名稱不能為空";
                if (!/^[a-zA-Z][a-zA-Z0-9]*$/.test(inputValue)) {
                    return "請輸入合法的命名（英文字母開頭）";
                }
                return true;
            });
        }
    }

    if (!/^[a-zA-Z][a-zA-Z0-9]*$/.test(modelName)) {
        throw new Error("請輸入合法的命名（英文字母開頭）");
    }

    const lower = modelName.toLowerCase();
    const upper = capitalize(modelName);

    console.log("📁 建立資料夾與檔案中...");

    const composablePath = generateComposable({ upper, lower });
    const typePath = generateType({ upper, lower });

    console.log(`
✅ 成功建立以下檔案：
  - ${path.relative(projectRoot, composablePath)}
  - ${path.relative(projectRoot, typePath)}

🎉 模組 ${upper} 已成功產生完成！
✨ 小提醒：請檢查欄位定義與 DTO，完成後再啟動開發伺服器。
`);
}

try {
    ensureNuxtRoot(projectRoot);
} catch (error) {
    console.error(`❌ ${error.message}`);
    process.exit(1);
}

main().catch((err) => {
    console.error("❌ 發生錯誤：", err);
    process.exit(1);
});
