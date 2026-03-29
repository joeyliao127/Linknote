import re
import html
from langchain_text_splitters import RecursiveCharacterTextSplitter
from app.config import settings


def _strip_html(text: str) -> str:
    text = html.unescape(text)
    text = re.sub(r"<[^>]+>", " ", text)
    text = re.sub(r"\s+", " ", text).strip()
    return text


def _build_splitter() -> RecursiveCharacterTextSplitter:
    return RecursiveCharacterTextSplitter(
        chunk_size=settings.CHUNK_SIZE,
        chunk_overlap=settings.CHUNK_OVERLAP,
        # 中英文混合優先按段落、句子、詞切分
        separators=["\n\n", "\n", "。", "！", "？", ".", "!", "?", " ", ""],
    )


def split_note(note: dict) -> list[dict]:
    """將一筆 note 的各欄位合併後切成 chunks，每個 chunk 帶上 metadata。"""
    parts = [
        note.get("title") or "",
        _strip_html(note.get("question") or ""),
        _strip_html(note.get("content") or ""),
        _strip_html(note.get("keypoint") or ""),
    ]
    full_text = "\n\n".join(p for p in parts if p.strip())
    if not full_text.strip():
        return []

    splitter = _build_splitter()
    chunks = splitter.split_text(full_text)

    return [
        {
            "text": chunk,
            "note_id": str(note["id"]),
            "notebook_id": str(note["notebook_id"]),
            "title": note.get("title") or "",
            "chunk_index": i,
        }
        for i, chunk in enumerate(chunks)
    ]
