#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo ">>> Building executable jar"
./gradlew bootJar

BOOT_JAR="$(find "$ROOT_DIR/build/libs" -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' | head -n 1)"

if [[ -z "${BOOT_JAR}" ]]; then
    echo "No bootable jar found in build/libs. Did the build succeed?" >&2
    exit 1
fi

echo ">>> Starting Spring Boot application in production mode"
exec java -jar "$BOOT_JAR"
