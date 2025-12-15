#!/usr/bin/env node
import fs from "fs";
import path from "path";
import inquirer from "inquirer";

const METHODS = {
    GET: "GET",
    POST: "POST",
    PUT: "PUT",
    DELETE: "DELETE",
};

/**
 * 1. 自訂 Checkbox — 按 a 可以 toggle 全選
 */
function methodCheckboxPrompt() {
    return inquirer.prompt([
        {
            type: "checkbox",
            name: "methods",
            message: "哪些 HTTP method 要建立？（按 'a' 全選 / 全不選）",
            loop: false,
            pageSize: 10,

            /**
             * 自訂 checkbox 行為：按下 a 時 toggle 全選
             */
            transformer(input) {
                return input;
            },
            /** 核心邏輯：用 internal key → 註冊 'a' toggler */
            choices: Object.keys(METHODS).map((m) => ({ name: m, value: m })),
        },
    ]);
}

/**
 * 2. 建立 BFF API 檔案
 */
function createApiFile(resourceName, method) {
    const folder = path.join("../server/api", resourceName);
    const IdFolder = path.join("../server/api", resourceName, "[id]");
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

    const { resourceName } = await inquirer.prompt([
        {
            type: "input",
            name: "resourceName",
            message: "API 資源名稱（例如：notebooks、notes）：",
            validate: (input) => !!input || "資源名稱不能是空字串",
        },
    ]);

    const { methods } = await methodCheckboxPrompt();

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

main();
