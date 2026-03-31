from functools import lru_cache
from app.config import settings
from app.modules.embedding.base import Embedder
from app.modules.embedding.ollama_embedder_impl import OllamaEmbedder
from app.modules.embedding.openai_embedder import OpenAIEmbedder


@lru_cache(maxsize=1)
def get_embedder() -> Embedder:
    provider = settings.EMBEDDING_PROVIDER.lower()
    if provider == "openai":
        return OpenAIEmbedder()
    elif provider == "ollama":
        return OllamaEmbedder()
    else:
        raise ValueError(
            f"Unknown EMBEDDING_PROVIDER '{provider}'. Must be 'openai' or 'ollama'."
        )
