from fastapi import FastAPI
from app.api.routes.ingest import router as ingest_router
from app.api.routes.query import router as query_router

app = FastAPI(title="LinkNote RAG Service")

app.include_router(ingest_router)
app.include_router(query_router)
