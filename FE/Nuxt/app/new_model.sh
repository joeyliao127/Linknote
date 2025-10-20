#!/bin/bash

# === 顏色設定 ===
GREEN="\033[1;32m"
YELLOW="\033[1;33m"
BLUE="\033[1;34m"
RED="\033[1;31m"
RESET="\033[0m"

echo -e "${BLUE}"
echo "========================================"
echo " 🚀 MVVM Model 產生器"
echo "========================================"
echo -e "${RESET}"

# === 輸入模型名稱 ===
read -p "請輸入 model 名稱（例如：notebook）： " model_name

# 檢查輸入是否為空
if [ -z "$model_name" ]; then
    echo -e "${RED}❌ 錯誤：model 名稱不能為空。${RESET}"
    exit 1
fi

# === 名稱轉換 ===
lower_name=$(echo "$model_name" | tr '[:upper:]' '[:lower:]')
upper_name="$(tr '[:lower:]' '[:upper:]' <<< ${model_name:0:1})${model_name:1}"

# === 路徑設定 ===
SRC_COMPOSABLE="composables/sample.ts.disabled"
DST_COMPOSABLE_DIR="composables/${lower_name}"
DST_COMPOSABLE_FILE="${DST_COMPOSABLE_DIR}/use${upper_name}.ts"

SRC_TYPE="types/sample.d.ts.disabled"
DST_TYPE_FILE="types/${upper_name}.d.ts"

# === 開始執行 ===
echo -e "${YELLOW}📁 建立資料夾與檔案中...${RESET}"

# 建立資料夾
mkdir -p "$DST_COMPOSABLE_DIR"

# === 檔案複製與替換 ===
# 複製 composable
sed "s/!{upper}/${upper_name}/g; s/!{lower}/${lower_name}/g" "$SRC_COMPOSABLE" > "$DST_COMPOSABLE_FILE"

# 複製 type
sed "s/!{upper}/${upper_name}/g; s/!{lower}/${lower_name}/g" "$SRC_TYPE" > "$DST_TYPE_FILE"

# === 檢查結果 ===
if [ -f "$DST_COMPOSABLE_FILE" ] && [ -f "$DST_TYPE_FILE" ]; then
    echo -e "${GREEN}✅ 成功建立以下檔案：${RESET}"
    echo -e "   - ${BLUE}${DST_COMPOSABLE_FILE}${RESET}"
    echo -e "   - ${BLUE}${DST_TYPE_FILE}${RESET}"
    echo ""
    echo -e "${GREEN}🎉 模組 ${upper_name} 已成功產生完成！${RESET}"
else
    echo -e "${RED}❌ 檔案建立失敗，請檢查路徑與權限設定。${RESET}"
    exit 1
fi

echo ""
echo -e "${YELLOW}✨ 小提醒：請檢查欄位定義與 DTO，完成後再啟動開發伺服器。${RESET}"
echo ""