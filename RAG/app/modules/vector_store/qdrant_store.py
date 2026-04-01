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
    return AsyncQdrantClient(
        host=settings.QDRANT_HOST,
        port=settings.QDRANT_PORT,
        api_key=settings.QDRANT_API_KEY or None,
        https=False,
    )


async def ensure_collection(client: AsyncQdrantClient, vector_size: int) -> str:
    """確保對應 vector_size 的 collection 存在，回傳實際使用的 collection 名稱。
    若 base collection 存在且維度相符則直接使用；若維度不符則自動使用 {base}_{vector_size}。
    """
    base = settings.QDRANT_COLLECTION
    collections = await client.get_collections()
    existing_names = {c.name for c in collections.collections}

    # 判斷 base collection 是否存在且維度相符
    if base in existing_names:
        info = await client.get_collection(base)
        if info.config.params.vectors.size == vector_size:
            return base
        # 維度不符，改用帶 suffix 的 collection
        collection_name = f"{base}_{vector_size}"
    else:
        collection_name = base

    if collection_name not in existing_names:
        await client.create_collection(
            collection_name=collection_name,
            vectors_config=VectorParams(size=vector_size, distance=_get_distance()),
        )
    return collection_name


async def delete_by_note_id(
    client: AsyncQdrantClient, note_id: str, user_id: str, collection_name: str | None = None
) -> None:
    """刪除指定 note_id + user_id 的所有 chunks，用於 re-ingest 前清除舊向量。"""
    col = collection_name or settings.QDRANT_COLLECTION
    await client.delete(
        collection_name=col,
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
    collection_name: str | None = None,
) -> None:
    """
    Point ID 用 uuid5(user_id:note_id:chunk_index)，確保不同使用者 ingest 同一篇筆記時互不衝突。
    """
    col = collection_name or settings.QDRANT_COLLECTION
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
    await client.upsert(collection_name=col, points=points)


async def search(
    client: AsyncQdrantClient,
    query_vector: list[float],
    top_k: int = 5,
    user_id: str | None = None,
    collection_name: str | None = None,
):
    col = collection_name or settings.QDRANT_COLLECTION
    query_filter = None
    if user_id:
        query_filter = Filter(
            must=[FieldCondition(key="user_id", match=MatchValue(value=user_id))]
        )

    result = await client.query_points(
        collection_name=col,
        query=query_vector,
        query_filter=query_filter,
        limit=top_k,
    )
    return result.points
