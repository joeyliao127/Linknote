#!/bin/bash
set -e

docker pull ghcr.io/joeyliao127/linknote/springboot:latest
docker pull ghcr.io/joeyliao127/linknote/nuxt:latest
docker pull ghcr.io/joeyliao127/linknote/fastapi:latest

echo "All images pulled successfully."
