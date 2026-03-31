from openai import AsyncOpenAI
from app.config import settings


class OpenAIEmbedder:
    """Embedding via OpenAI Embeddings API. text-embedding-3-small produces 1536-dim vectors."""

    def __init__(self) -> None:
        self._client = AsyncOpenAI(api_key=settings.OPENAPI_EMBEDDING)

    @property
    def vector_size(self) -> int:
        return 1536

    async def embed(self, texts: list[str]) -> list[list[float]]:
        response = await self._client.embeddings.create(
            model=settings.OPENAI_EMBEDDING_MODEL,
            input=texts,
        )
        return [item.embedding for item in sorted(response.data, key=lambda x: x.index)]
