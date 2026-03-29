import asyncpg
from app.config import settings


async def load_notes() -> list[dict]:
    conn = await asyncpg.connect(
        host=settings.POSTGRES_HOST,
        port=settings.POSTGRES_PORT,
        database=settings.POSTGRES_DB,
        user=settings.POSTGRES_USER,
        password=settings.POSTGRES_PASSWORD,
    )
    try:
        rows = await conn.fetch("""
            SELECT id, title, question, content, keypoint, notebook_id
            FROM notes
            WHERE content IS NOT NULL
               OR question IS NOT NULL
               OR keypoint IS NOT NULL
        """)
        return [dict(row) for row in rows]
    finally:
        await conn.close()
