import httpx
from app.config import settings


class OllamaEmbedder:
    """Embedding via Ollama /api/embed. BGE-M3 produces 1024-dim vectors."""

    @property
    def vector_size(self) -> int:
        return 1024

    async def embed(self, texts: list[str]) -> list[list[float]]:
        async with httpx.AsyncClient(timeout=120.0) as client:
            response = await client.post(
                f"{settings.OLLAMA_BASE_URL}/api/embed",
                json={"model": settings.OLLAMA_MODEL, "input": texts},
            )
            response.raise_for_status()
            return response.json()["embeddings"]
