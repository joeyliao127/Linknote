#!/bin/bash

# 定義憑證路徑與名稱
DOMAIN="linknote.local.com"
SSL_DIR="$(cd "$(dirname "$0")" && pwd)"
KEY_FILE="$SSL_DIR/$DOMAIN-key.pem"
CERT_FILE="$SSL_DIR/$DOMAIN.pem"

echo "正在生成 $DOMAIN 的自簽章 SSL 憑證..."

# 使用 openssl 生成憑證
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout "$KEY_FILE" \
  -out "$CERT_FILE" \
  -subj "/C=TW/ST=Taipei/L=Taipei/O=Linknote/CN=$DOMAIN"

if [ $? -eq 0 ]; then
  echo "--------------------------------------------------"
  echo "成功！憑證已生成於："
  echo "Key:  $KEY_FILE"
  echo "Cert: $CERT_FILE"
  echo "--------------------------------------------------"
  echo "請確保您的 /etc/hosts 檔案中包含以下內容："
  echo "127.0.0.1 $DOMAIN"
else
  echo "憑證生成失敗，請檢查是否安裝了 openssl。"
fi
