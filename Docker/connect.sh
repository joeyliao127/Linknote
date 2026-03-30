#!/bin/bash

if [ -f .env ]; then
    source .env
fi

echo "🔌 正在連線至 PostgreSQL ($DB_NAME)..."
echo "提示: 輸入 '\q' 即可退出，輸入 '\d' 查看資料表。"

docker exec -it linknote_pg psql -U $POSTGRES_USER -d $POSTGRES_DB
