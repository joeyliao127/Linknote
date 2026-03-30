from fastapi import APIRouter, HTTPException
from app.modules.splitter.text_splitter import split_note
from app.modules.embedding.ollama_embedder import embed
from app.modules.vector_store.qdrant_store import (
    get_client, ensure_collection, delete_by_note_id, upsert_chunks,
)
from app.schemas.ingest import IngestRequest, IngestResponse, DeleteNoteRequest

router = APIRouter()


@router.post("/ingest", response_model=IngestResponse)
async def ingest(request: IngestRequest):
    if not request.notes:
        return IngestResponse(notes_processed=0, chunks_ingested=0, message="No notes provided")

    all_chunks: list[dict] = []
    for note in request.notes:
        all_chunks.extend(split_note(note.model_dump()))

    if not all_chunks:
        return IngestResponse(
            notes_processed=len(request.notes),
            chunks_ingested=0,
            message="Notes provided but all had empty content",
        )

    try:
        embeddings = await embed([c["text"] for c in all_chunks])
    except Exception as e:
        raise HTTPException(status_code=502, detail=f"Ollama embedding failed: {e}")

    try:
        client = get_client()
        await ensure_collection(client, vector_size=len(embeddings[0]))
        # 先刪除各 note 的舊 chunks，確保 idempotent（chunk 數量改變時不留孤兒資料）
        note_user_pairs = {(note.note_id, note.user_id) for note in request.notes}
        for note_id, user_id in note_user_pairs:
            await delete_by_note_id(client, note_id, user_id)
        await upsert_chunks(client, all_chunks, embeddings)
    except Exception as e:
        raise HTTPException(status_code=502, detail=f"Qdrant operation failed: {e}")

    return IngestResponse(
        notes_processed=len(request.notes),
        chunks_ingested=len(all_chunks),
        message="Ingestion complete",
    )


@router.delete("/notes/{note_id}")
async def delete_note(note_id: str, request: DeleteNoteRequest):
    try:
        client = get_client()
        await delete_by_note_id(client, note_id, request.user_id)
    except Exception as e:
        raise HTTPException(status_code=502, detail=f"Qdrant delete failed: {e}")
    return {"deleted": True, "note_id": note_id}
