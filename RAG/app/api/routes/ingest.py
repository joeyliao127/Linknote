from fastapi import APIRouter, HTTPException
from app.modules.ingestion.pg_loader import load_notes
from app.modules.splitter.text_splitter import split_note
from app.modules.embedding.ollama_embedder import embed
from app.modules.vector_store.qdrant_store import get_client, ensure_collection, upsert_chunks
from app.schemas.ingest import IngestResponse

router = APIRouter()


@router.post("/ingest", response_model=IngestResponse)
async def ingest():
    notes = await load_notes()
    if not notes:
        return IngestResponse(notes_processed=0, chunks_ingested=0, message="No notes found in database")

    all_chunks: list[dict] = []
    for note in notes:
        all_chunks.extend(split_note(note))

    if not all_chunks:
        return IngestResponse(
            notes_processed=len(notes),
            chunks_ingested=0,
            message="Notes found but all had empty content",
        )

    try:
        embeddings = await embed([c["text"] for c in all_chunks])
    except Exception as e:
        raise HTTPException(status_code=502, detail=f"Ollama embedding failed: {e}")

    try:
        client = get_client()
        await ensure_collection(client, vector_size=len(embeddings[0]))
        await upsert_chunks(client, all_chunks, embeddings)
    except Exception as e:
        raise HTTPException(status_code=502, detail=f"Qdrant upsert failed: {e}")

    return IngestResponse(
        notes_processed=len(notes),
        chunks_ingested=len(all_chunks),
        message="Ingestion complete",
    )
