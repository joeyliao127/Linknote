from pydantic import BaseModel


class NoteInput(BaseModel):
    note_id: str
    user_id: str
    notebook_id: str
    title: str = ""
    question: str | None = None
    content: str | None = None
    keypoint: str | None = None


class IngestRequest(BaseModel):
    notes: list[NoteInput]


class IngestResponse(BaseModel):
    notes_processed: int
    chunks_ingested: int
    message: str
