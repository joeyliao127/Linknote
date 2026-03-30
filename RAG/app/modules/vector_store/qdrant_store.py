import uuid
from qdrant_client import AsyncQdrantClient
from qdrant_client.models import (
    Distance, VectorParams, PointStruct,
    Filter, FilterSelector, FieldCondition, MatchValue,
)
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
            # distance 類型需要根據 embedding model 選擇 dot(方向、長度), cosine(餘弦), euclid(直線距離)
            vectors_config=VectorParams(size=vector_size, distance=_get_distance()),
        )


async def delete_by_note_id(client: AsyncQdrantClient, note_id: str, user_id: str) -> None:
    """刪除指定 note_id + user_id 的所有 chunks，用於 re-ingest 前清除舊向量。"""
    await client.delete(
        collection_name=settings.QDRANT_COLLECTION,
        points_selector=FilterSelector(
            filter=Filter(
                must=[
                    FieldCondition(key="note_id", match=MatchValue(value=note_id)),
                    FieldCondition(key="user_id", match=MatchValue(value=user_id)),
                ]
            )
        ),
    )


async def upsert_chunks(
    client: AsyncQdrantClient,
    chunks: list[dict],
    embeddings: list[list[float]],
) -> None:
    """
    Point ID 用 uuid5(user_id:note_id:chunk_index)，確保不同使用者 ingest 同一篇筆記時互不衝突。
    """
    points = [
        PointStruct(
            id=str(uuid.uuid5(
                uuid.NAMESPACE_OID,
                f"{chunk['user_id']}:{chunk['note_id']}:{chunk['chunk_index']}"
            )),
            vector=embedding,
            payload={
                "text": chunk["text"],
                "note_id": chunk["note_id"],
                "user_id": chunk["user_id"],
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
    user_id: str | None = None,
):
    query_filter = None
    if user_id:
        query_filter = Filter(
            must=[FieldCondition(key="user_id", match=MatchValue(value=user_id))]
        )

    result = await client.query_points(
        collection_name=settings.QDRANT_COLLECTION,
        query=query_vector,
        query_filter=query_filter,
        limit=top_k,
    )
    return result.points
