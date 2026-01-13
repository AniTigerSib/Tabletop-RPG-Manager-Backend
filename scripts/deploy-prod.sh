#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if [[ ! -f .env.prod ]]; then
    echo "Missing .env.prod. Create it based on .env.prod.example." >&2
    exit 1
fi

echo ">>> Building boot jar"
./gradlew --no-daemon bootJar

echo ">>> Building and starting containers"
if docker compose version >/dev/null 2>&1; then
    compose_cmd=(docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
    compose_cmd=(docker-compose)
else
    echo "Docker Compose is required to deploy." >&2
    exit 1
fi

"${compose_cmd[@]}" --env-file "$ROOT_DIR/.env.prod" -f "$ROOT_DIR/docker-compose.prod.yml" up -d --build
