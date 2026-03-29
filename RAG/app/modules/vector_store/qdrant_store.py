import uuid
from qdrant_client import AsyncQdrantClient
from qdrant_client.models import Distance, VectorParams, PointStruct
from app.config import settings


def _get_distance() -> Distance:
    return Distance[settings.QDRANT_DISTANCE.upper()]


def get_client() -> AsyncQdrantClient:
    return AsyncQdrantClient(host=settings.QDRANT_HOST, port=settings.QDRANT_PORT)


async def ensure_collection(client: AsyncQdrantClient, vector_size: int) -> None:
    collections = await client.get_collections()
    existing = {c.name for c in collections.collections}
    if settings.QDRANT_COLLECTION not in existing:
        await client.create_collection(
            collection_name=settings.QDRANT_COLLECTION,
            # distance 類型需要根據 embedding model 選擇 dot(方向、長度), consine(餘弦), euclid(直線距離)
            vectors_config=VectorParams(size=vector_size, distance=_get_distance()),
        )


async def upsert_chunks(
    client: AsyncQdrantClient,
    chunks: list[dict],
    embeddings: list[list[float]],
) -> None:
    """
    用 uuid5(note_id:chunk_index) 作為 point ID，確保重複 ingest 時 upsert 而非重複建立。
    """
    points = [
        PointStruct(
            id=str(uuid.uuid5(uuid.NAMESPACE_OID, f"{chunk['note_id']}:{chunk['chunk_index']}")),
            vector=embedding,
            payload={
                "text": chunk["text"],
                "note_id": chunk["note_id"],
                "notebook_id": chunk["notebook_id"],
                "title": chunk["title"],
                "chunk_index": chunk["chunk_index"],
            },
        )
        for chunk, embedding in zip(chunks, embeddings)
    ]
    await client.upsert(collection_name=settings.QDRANT_COLLECTION, points=points)


async def search(
    client: AsyncQdrantClient,
    query_vector: list[float],
    top_k: int = 5,
):
    result = await client.query_points(
        collection_name=settings.QDRANT_COLLECTION,
        query=query_vector,
        limit=top_k,
    )
    return result.points
