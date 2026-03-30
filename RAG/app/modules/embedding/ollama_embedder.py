import httpx
from app.config import settings


async def embed(texts: list[str]) -> list[list[float]]:
    """
    呼叫 Ollama /api/embed（batch），回傳每個 text 的 embedding vector。
    需要 Ollama >= 0.3.x，BGE-M3 產生 1024 維向量。
    """
    async with httpx.AsyncClient(timeout=120.0) as client:
        response = await client.post(
            f"{settings.OLLAMA_BASE_URL}/api/embed",
            json={"model": settings.OLLAMA_MODEL, "input": texts},
        )
        response.raise_for_status()
        return response.json()["embeddings"]
