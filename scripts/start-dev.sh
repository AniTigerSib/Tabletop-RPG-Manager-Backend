#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

export SPRING_PROFILES_ACTIVE=dev

echo ">>> Ensuring development infrastructure is running"
if docker compose version >/dev/null 2>&1; then
    compose_cmd=(docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
    compose_cmd=(docker-compose)
else
    echo "Docker Compose is required to start the development services." >&2
    exit 1
fi

"${compose_cmd[@]}" -f "$ROOT_DIR/docker-compose.yml" up -d develop_db develop_redis

echo ">>> Starting Spring Boot application with dev profile"
./gradlew bootRun
