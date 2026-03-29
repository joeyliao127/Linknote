from pydantic import BaseModel, Field


class QueryRequest(BaseModel):
    text: str
    top_k: int = Field(default=5, ge=1, le=20)


class ChunkResult(BaseModel):
    text: str
    note_id: str
    notebook_id: str
    title: str
    chunk_index: int
    score: float


class QueryResponse(BaseModel):
    results: list[ChunkResult]
