from fastapi import APIRouter, HTTPException
from app.modules.embedding.ollama_embedder import embed
from app.modules.vector_store.qdrant_store import get_client, search
from app.schemas.query import QueryRequest, QueryResponse, ChunkResult

router = APIRouter()


@router.post("/query", response_model=QueryResponse)
async def query(request: QueryRequest):
    try:
        embeddings = await embed([request.text])
    except Exception as e:
        raise HTTPException(status_code=502, detail=f"Ollama embedding failed: {e}")

    try:
        client = get_client()
        results = await search(client, query_vector=embeddings[0], top_k=request.top_k)
    except Exception as e:
        raise HTTPException(status_code=502, detail=f"Qdrant search failed: {e}")

    return QueryResponse(
        results=[
            ChunkResult(
                text=r.payload["text"],
                note_id=r.payload["note_id"],
                notebook_id=r.payload["notebook_id"],
                title=r.payload["title"],
                chunk_index=r.payload["chunk_index"],
                score=r.score,
            )
            for r in results
        ]
    )
