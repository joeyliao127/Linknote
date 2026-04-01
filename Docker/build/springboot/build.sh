#!/bin/bash
cd "$(dirname "$0")/../../.."

docker buildx build --platform linux/amd64 \
  -f BE/Dockerfile.prod \
  -t ghcr.io/joeyliao127/linknote/springboot:latest \
  --push \
  ./BE
