#!/usr/bin/env node
import fs from "node:fs";
import path from "node:path";
import inquirer from "inquirer";
import { fileURLToPath } from "node:url";

/**
 * ============================
 * Env / Utils
 * ============================
 */

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const projectRoot = path.resolve(__dirname, "..");

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

    const index!{upper} = async (
        userId: string
    ): Promise<Pagination<!{upper}>> => {
        const response: Pagination<!{upper}> = await $fetch(baseURL, {
            method: "GET",
            headers: {
                Authorization: userId,
            },
        });

        return response;
    };

    const create!{upper} = async (
        userId: string,
        !{lower}: Create!{upper}DTO
    ) => {
        const response: Pagination<!{upper}> = await $fetch(baseURL, {
            method: "POST",
            headers: {
                Authorization: userId,
            },
            body: !{lower},
        });

        return response;
    };

    const update!{upper} = async (
        userId: string,
        !{lower}Id: string,
        !{lower}: Update!{upper}DTO
    ) => {
        const response: Pagination<!{upper}> = await $fetch(
            \`\${baseURL}/\${!{lower}Id}\`,
            {
                method: "PUT",
                headers: {
                    Authorization: userId,
                },
                body: !{lower},
            }
        );

        return response;
    };

    const delete!{upper} = async (userId: string, !{lower}Id: string) => {
        await $fetch(\`\${baseURL}/\${!{lower}Id}\`, {
            method: "DELETE",
            headers: {
                Authorization: userId,
            },
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

    const { modelName } = await inquirer.prompt([
        {
            type: "input",
            name: "modelName",
            message: "請輸入 model 名稱（例如：notebook）",
            validate(input) {
                if (!input) return "model 名稱不能為空";
                if (!/^[a-zA-Z][a-zA-Z0-9]*$/.test(input)) {
                    return "請輸入合法的命名（英文字母開頭）";
                }
                return true;
            },
        },
    ]);

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

main().catch((err) => {
    console.error("❌ 發生錯誤：", err);
    process.exit(1);
});
