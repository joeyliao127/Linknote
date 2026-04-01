#!/bin/bash
set -e
cd "$(dirname "$0")/../.."

docker buildx build --platform linux/amd64 \
  -f BE/Dockerfile.prod \
  -t ghcr.io/joeyliao127/linknote/springboot:latest \
  --push \
  ./BE

docker buildx build --platform linux/amd64 \
  -f FE/Dockerfile.prod \
  -t ghcr.io/joeyliao127/linknote/nuxt:latest \
  --push \
  ./FE

docker buildx build --platform linux/amd64 \
  -t ghcr.io/joeyliao127/linknote/fastapi:latest \
  --push \
  ./RAG
