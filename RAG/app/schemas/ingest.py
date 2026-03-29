from pydantic import BaseModel


class IngestResponse(BaseModel):
    notes_processed: int
    chunks_ingested: int
    message: str
