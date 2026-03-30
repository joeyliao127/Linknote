from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    POSTGRES_HOST: str
    POSTGRES_PORT: int = 5432
    POSTGRES_DB: str
    POSTGRES_USER: str
    POSTGRES_PASSWORD: str

    QDRANT_HOST: str
    QDRANT_PORT: int = 6333
    QDRANT_COLLECTION: str = "notes"

    OLLAMA_BASE_URL: str = "http://localhost:11434"
    OLLAMA_MODEL: str = "bge-m3"
    # distance 類型需要根據 embedding model 選擇：
    #   COSINE  — 餘弦相似度，適合語意搜尋（BGE、text-embedding 系列）
    #   DOT     — 內積，適合已 normalize 的向量（OpenAI ada-002）
    #   EUCLID  — 歐氏距離，適合需要絕對距離的場景
    QDRANT_DISTANCE: str = "COSINE"

    CHUNK_SIZE: int = 500
    CHUNK_OVERLAP: int = 50

    class Config:
        env_file = ".env"


settings = Settings()
