#!/bin/bash
cd "$(dirname "$0")/../../.."

docker buildx build --platform linux/amd64 \
  -t ghcr.io/joeyliao127/linknote/fastapi:latest \
  --push \
  ./RAG
