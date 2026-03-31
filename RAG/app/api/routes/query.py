from fastapi import APIRouter, Depends, HTTPException
from app.modules.embedding.base import Embedder
from app.modules.embedding.factory import get_embedder
from app.modules.vector_store.qdrant_store import get_client, ensure_collection, search
from app.schemas.query import QueryRequest, QueryResponse, ChunkResult

router = APIRouter()


@router.post("/query", response_model=QueryResponse)
async def query(request: QueryRequest, embedder: Embedder = Depends(get_embedder)):
    try:
        embeddings = await embedder.embed([request.text])
    except Exception as e:
        raise HTTPException(status_code=502, detail=f"Embedding failed: {e}")

    try:
        client = get_client()
        collection_name = await ensure_collection(client, vector_size=embedder.vector_size)
        results = await search(
            client,
            query_vector=embeddings[0],
            top_k=request.top_k,
            user_id=request.user_id,
            collection_name=collection_name,
        )
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
