#!/usr/bin/env node
import fs from "fs";
import path from "path";
import { createRequire } from "module";
import { createInterface } from "node:readline/promises";
import { stdin as input, stdout as output } from "node:process";
import { fileURLToPath, pathToFileURL } from "url";

const METHODS = {
    GET: "GET",
    POST: "POST",
    PUT: "PUT",
    DELETE: "DELETE",
};

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
        resourceName: null,
        methods: [],
        all: false,
        projectRoot: null,
    };

    const takeValue = (i) => (i + 1 < argv.length ? argv[i + 1] : null);

    for (let i = 0; i < argv.length; i += 1) {
        const arg = argv[i];
        if (!arg.startsWith("-")) continue;

        const [key, inlineValue] = arg.split("=");
        const value = inlineValue ?? takeValue(i);

        switch (key) {
            case "--resource":
            case "--name":
            case "-r":
                if (value && !value.startsWith("-")) {
                    options.resourceName = value;
                    if (!inlineValue) i += 1;
                }
                break;
            case "--methods":
            case "--method":
                if (value && !value.startsWith("-")) {
                    options.methods.push(
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

/**
 * 1. 自訂 Checkbox — 按 a 可以 toggle 全選
 */
async function promptMethods() {
    const inquirer = await loadInquirer(projectRoot);
    if (inquirer) {
        return inquirer.prompt([
            {
                type: "checkbox",
                name: "methods",
                message: "哪些 HTTP method 要建立？（按 'a' 全選 / 全不選）",
                loop: false,
                pageSize: 10,
                transformer(inputValue) {
                    return inputValue;
                },
                choices: Object.keys(METHODS).map((m) => ({ name: m, value: m })),
            },
        ]);
    }

    const answer = await promptText(
        "請輸入要建立的 methods（逗號分隔，例如 GET,POST,PUT,DELETE）:",
    );
    const methods = answer
        .split(/[,\s]+/)
        .map((item) => item.trim())
        .filter(Boolean);

    return { methods };
}

/**
 * 2. 建立 BFF API 檔案
 */
function createApiFile(resourceName, method) {
    const folder = path.join(projectRoot, "server/api", resourceName);
    const IdFolder = path.join(projectRoot, "server/api", resourceName, "[id]");
    if (!fs.existsSync(folder)) fs.mkdirSync(folder, { recursive: true });
    if (!fs.existsSync(IdFolder)) fs.mkdirSync(IdFolder, { recursive: true });

    let fileName = null;
    let filePath = null;

    switch (method) {
        case "GET":
        case "POST":
            fileName = `index.${method.toLowerCase()}.ts`;
            filePath = path.join(folder, fileName);
            break;

        case "PUT":
        case "DELETE":
            fileName = `index.${method.toLowerCase()}.ts`;
            filePath = path.join(folder, "[id]", fileName);
            break;

        case "CATCH_ALL":
            fileName = `[...${resourceName}].ts`;
            filePath = path.join(folder, fileName);
            break;

        default:
            break;
    }

    let template = null;

    const pathMap = {
        GET: `\`\$\{config.RESOURCE_API\}/${resourceName}\``,
        POST: `\`\$\{config.RESOURCE_API\}/${resourceName}\``,
        PUT: `\`\$\{config.RESOURCE_API\}/${resourceName}/\$\{event.context.params?.id\}\``,
        DELETE: `\`\$\{config.RESOURCE_API\}/${resourceName}/\$\{event.context.params?.id\}\``,
    };

    // CRUD 模板
    const template_crud = `
export default defineEventHandler(async (event) => {
    const session = event.context.session;
    ${
        method === METHODS.GET || method === METHODS.DELETE
            ? "const query = getQuery(event);"
            : "const body = await readBody(event);"
    }

    // TODO(Forward): 這裡自動 forward 到 BE，可依照架構調整 path
    const config = useRuntimeConfig();
    return await $fetch(${pathMap[method]}, {
        method: "${method === "CATCH_ALL" ? "GET/POST/PUT/DELETE" : method}",
        headers: {
            Authorization: \`Bearer \${session?.token}\`,
        },
        ${
            method === METHODS.GET || method === METHODS.DELETE
                ? "query"
                : "body"
        }
    });
});`;

    // [...resource].ts 檔案用於處理未定義的 api path，並返回 403
    const template_catch_all = `
import { useThrowApiError } from "~~/server/composables/useThrowApiError";
import { ErrorCodes } from "~~/server/error/ErrorCode";

export default defineEventHandler(() => {
    useThrowApiError(ErrorCodes.FORBIDDEN);
});
    `;

    if (method === "CATCH_ALL") {
        template = template_catch_all;
    } else {
        template = template_crud;
    }

    fs.writeFileSync(filePath, template);
    return filePath;
}

/**
 * 3. 主程式
 */
async function main() {
    console.log("✨ BFF API Generator");

    let resourceName = cliOptions.resourceName;
    let methods = cliOptions.all ? Object.keys(METHODS) : cliOptions.methods;

    if (!resourceName) {
        const inquirer = await loadInquirer(projectRoot);
        if (inquirer) {
            const promptResult = await inquirer.prompt([
                {
                    type: "input",
                    name: "resourceName",
                    message: "API 資源名稱（例如：notebooks、notes）：",
                    validate: (inputValue) => !!inputValue || "資源名稱不能是空字串",
                },
            ]);
            resourceName = promptResult.resourceName;
        } else {
            resourceName = await promptText("API 資源名稱（例如：notebooks、notes）：", (inputValue) =>
                !!inputValue || "資源名稱不能是空字串",
            );
        }
    }

    if (!resourceName) {
        throw new Error("資源名稱不能是空字串");
    }

    if (!methods || methods.length === 0) {
        const promptResult = await promptMethods();
        methods = promptResult.methods;
    }

    methods = methods.map((method) => method.toUpperCase());
    const invalidMethods = methods.filter((method) => !METHODS[method]);
    if (invalidMethods.length > 0) {
        throw new Error(`Unsupported methods: ${invalidMethods.join(", ")}`);
    }

    if (methods.length === 0) {
        console.log("❌ 未選擇任何 method，結束。");
        process.exit(0);
    }

    console.log("\n📁 開始建立檔案...\n");

    // 建立 CRUD 檔案
    methods.forEach((m) => {
        const fp = createApiFile(resourceName, m);
        console.log(`  ✔ 建立 ${fp}`);
    });

    // 建立 catch-all API
    const catchAll = createApiFile(resourceName, "CATCH_ALL");
    console.log(`  ✔ 建立 ${catchAll}`);

    console.log("\n🎉 完成！\n");
}

const cliOptions = parseArgs(process.argv.slice(2));
const projectRoot = resolveProjectRoot(cliOptions.projectRoot);

try {
    ensureNuxtRoot(projectRoot);
} catch (error) {
    console.error(`❌ ${error.message}`);
    process.exit(1);
}

main().catch((error) => {
    console.error("❌ 發生錯誤：", error);
    process.exit(1);
});
