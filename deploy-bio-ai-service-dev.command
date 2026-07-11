#!/bin/zsh -l
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

# AI Controller 位于 bio-drqi-ai-admin，本脚本部署独立 AI 服务。
SERVICE_DIR="${SERVICE_DIR:-/Users/zoujun/data/service/bio-ai-dev}"
COMPOSE_FILE="${COMPOSE_FILE:-${SERVICE_DIR}/docker-compose.yml}"
IMAGE_NAME="${IMAGE_NAME:-bio-ai-service:dev}"
BASE_IMAGE="${BASE_IMAGE:-bio-ai-service:dev-base}"
LOCAL_JAVA_BASE_IMAGE="${LOCAL_JAVA_BASE_IMAGE:-nacos/nacos-server:v2.3.2}"
CONTAINER_NAME="${CONTAINER_NAME:-bio-ai-service-dev}"
JDK8_HOME="${JDK8_HOME:-/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home}"
CREATED_TEMP_BASE="false"
MAVEN_SNAPSHOT_ARGS="-nsu"

SERVER_PORT="${SERVER_PORT:-18095}"
DEBUG_PORT="${DEBUG_PORT:-28095}"
AI_LLM_BASE_URL="${AI_LLM_BASE_URL:-https://ws-g96i4edvjqyqgx4m.cn-beijing.maas.aliyuncs.com/compatible-mode/v1}"
AI_LLM_API_KEY="${AI_LLM_API_KEY:-}"
AI_LLM_MODEL="${AI_LLM_MODEL:-qwen-plus}"
AI_LLM_TEMPERATURE="${AI_LLM_TEMPERATURE:-0.1}"
AI_LLM_TIMEOUT="${AI_LLM_TIMEOUT:-180000}"
MYSQL_URL="${MYSQL_URL:-jdbc:mysql://mysql-dev:3306/bio_cer_local?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai}"
MYSQL_USERNAME="${MYSQL_USERNAME:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-mysql2026}"
REDIS_HOST="${REDIS_HOST:-redis-dev}"
REDIS_PORT="${REDIS_PORT:-6379}"
REDIS_PASSWORD="${REDIS_PASSWORD:-redis2026}"

if [ "${FORCE_SNAPSHOT_UPDATE:-false}" = "true" ] || [ "${FORCE_SNAPSHOT_UPDATE:-0}" = "1" ]; then
  MAVEN_SNAPSHOT_ARGS="-U"
fi

function log() {
  echo "[$(date '+%H:%M:%S')] $*"
}

function fail() {
  echo "ERROR: $*" >&2
  exit 1
}

function pause_on_exit() {
  local code=$?
  if [ "${code}" -ne 0 ]; then
    echo
    echo "ERROR: deploy failed with exit code ${code}."
    if [ -t 0 ]; then
      read -r "?Press Enter to close..."
    fi
  fi
}
trap pause_on_exit EXIT

function require_cmd() {
  command -v "$1" >/dev/null 2>&1 || fail "$1 command not found."
}

log "Project: ${PROJECT_DIR}"
log "Service: ${SERVICE_DIR}"
log "Container: ${CONTAINER_NAME}"
log "Port: ${SERVER_PORT}"
log "Maven snapshot mode: ${MAVEN_SNAPSHOT_ARGS}"

if [ -x "${JDK8_HOME}/bin/javac" ]; then
  export JAVA_HOME="${JDK8_HOME}"
  export PATH="${JAVA_HOME}/bin:${PATH}"
elif [ -n "${JAVA_HOME:-}" ] && [ -x "${JAVA_HOME}/bin/javac" ]; then
  export PATH="${JAVA_HOME}/bin:${PATH}"
else
  fail "JDK not found. Please install/configure a JDK, not a JRE."
fi

log "JAVA_HOME: ${JAVA_HOME}"

require_cmd mvn
require_cmd docker

[ -f "${PROJECT_DIR}/bio-drqi-ai/bio-drqi-ai-admin/pom.xml" ] || fail "bio-drqi-ai-admin module not found."
[ -f "${PROJECT_DIR}/bio-drqi-ai/Dockerfile.dev-local" ] || fail "bio-drqi-ai/Dockerfile.dev-local not found."

mkdir -p "${SERVICE_DIR}/logs" "${SERVICE_DIR}/signData"

if [ -f "${COMPOSE_FILE}" ]; then
  cp "${COMPOSE_FILE}" "${COMPOSE_FILE}.bak"
fi

cat > "${COMPOSE_FILE}" <<YAML
services:
  bio-ai-service-dev:
    image: ${IMAGE_NAME}
    container_name: ${CONTAINER_NAME}
    environment:
      - NACOS_HOST=nacos-standalone
      - DISCOVERY_HOST=${CONTAINER_NAME}
      - NACOS_PORT=8848
      - NACOS_REGISTER_ENABLED=true
      - SPRING_PROFILES_ACTIVE=dev
      - SPIRNG_PROFILES_ACTIVE=dev
      - SERVER_PORT=${SERVER_PORT}
      - DEBUG_PORT=${DEBUG_PORT}
      - AI_LLM_BASE_URL=${AI_LLM_BASE_URL}
      - AI_LLM_API_KEY=${AI_LLM_API_KEY}
      - AI_LLM_MODEL=${AI_LLM_MODEL}
      - AI_LLM_TEMPERATURE=${AI_LLM_TEMPERATURE}
      - AI_LLM_TIMEOUT=${AI_LLM_TIMEOUT}
      - AI_DB_URL=${MYSQL_URL}
      - AI_DB_USERNAME=${MYSQL_USERNAME}
      - AI_DB_PASSWORD=${MYSQL_PASSWORD}
      - REDIS_HOST=${REDIS_HOST}
      - REDIS_PORT=${REDIS_PORT}
      - REDIS_PASSWORD=${REDIS_PASSWORD}
      - JAVA_OPTS= -Xmx768m -Xms768m -Xmn384m -Xss256k
      - TZ=Asia/Shanghai
    restart: always
    volumes:
      - "./logs:/app/logs"
      - "./signData:/signData"
    ports:
      - ${SERVER_PORT}:${SERVER_PORT}
      - ${DEBUG_PORT}:${DEBUG_PORT}
    networks:
      - bio-dev
networks:
  bio-dev:
    external: true
YAML

cd "${PROJECT_DIR}"

log "Building bio-drqi-ai-admin jar..."
mvn ${MAVEN_SNAPSHOT_ARGS} -pl bio-drqi-ai/bio-drqi-ai-admin -am clean package -DskipTests

log "Preparing Docker runtime base..."
if docker image inspect "${IMAGE_NAME}" >/dev/null 2>&1; then
  log "Found local ${IMAGE_NAME}; using it as temporary runtime base."
  docker tag "${IMAGE_NAME}" "${BASE_IMAGE}"
  CREATED_TEMP_BASE="true"
elif docker container inspect "${CONTAINER_NAME}" >/dev/null 2>&1; then
  log "Found local container ${CONTAINER_NAME}; creating temporary runtime base from it."
  docker commit "${CONTAINER_NAME}" "${BASE_IMAGE}"
  CREATED_TEMP_BASE="true"
elif docker image inspect "${LOCAL_JAVA_BASE_IMAGE}" >/dev/null 2>&1; then
  log "Using local Java base image ${LOCAL_JAVA_BASE_IMAGE}."
  BASE_IMAGE="${LOCAL_JAVA_BASE_IMAGE}"
else
  fail "No local Java runtime image found. Need one of: ${IMAGE_NAME}, ${CONTAINER_NAME}, or ${LOCAL_JAVA_BASE_IMAGE}."
fi

log "Building Docker image ${IMAGE_NAME}..."
docker build --platform linux/amd64 --build-arg BASE_IMAGE="${BASE_IMAGE}" -f bio-drqi-ai/Dockerfile.dev-local -t "${IMAGE_NAME}" bio-drqi-ai

log "Ensuring docker network bio-dev..."
if ! docker network inspect bio-dev >/dev/null 2>&1; then
  docker network create bio-dev
fi

for container in nacos-standalone mysql-dev redis-dev minio-nginx-1; do
  if docker container inspect "${container}" >/dev/null 2>&1; then
    log "Ensuring ${container} is connected to bio-dev..."
    docker network connect bio-dev "${container}" >/dev/null 2>&1 || true
  fi
done

cd "${SERVICE_DIR}"
log "Restarting ${CONTAINER_NAME}..."
docker compose -f "${COMPOSE_FILE}" up -d --force-recreate bio-ai-service-dev

log "Cleaning old unused images..."
if [ "${CREATED_TEMP_BASE}" = "true" ]; then
  docker image rm "${BASE_IMAGE}" >/dev/null 2>&1 || true
fi
docker image prune -f

log "Done. AI endpoints are deployed in ${CONTAINER_NAME}: http://localhost:${SERVER_PORT}/ai"

trap - EXIT
if [ -t 0 ]; then
  read -r "?Press Enter to close..." || true
fi
