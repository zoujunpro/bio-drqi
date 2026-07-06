#!/bin/zsh -l
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SERVICE_DIR="/Users/zoujun/data/service/bio-ai-dev"
COMPOSE_FILE="${SERVICE_DIR}/docker-compose.yml"
IMAGE_NAME="bio-ai-service:dev"
BASE_IMAGE="bio-ai-service:dev-base"
LOCAL_JAVA_BASE_IMAGE="nacos/nacos-server:v2.3.2"
CONTAINER_NAME="bio-ai-service-dev"
JDK8_HOME="/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home"
CREATED_TEMP_BASE="false"

function pause_on_exit() {
  local code=$?
  if [ "${code}" -ne 0 ]; then
    echo
    echo "ERROR: deploy failed with exit code ${code}."
    read -r "?Press Enter to close..."
  fi
}
trap pause_on_exit EXIT

echo "Project: ${PROJECT_DIR}"
echo "Service: ${SERVICE_DIR}"

if [ -x "${JDK8_HOME}/bin/javac" ]; then
  export JAVA_HOME="${JDK8_HOME}"
  export PATH="${JAVA_HOME}/bin:${PATH}"
elif [ -n "${JAVA_HOME:-}" ] && [ -x "${JAVA_HOME}/bin/javac" ]; then
  export PATH="${JAVA_HOME}/bin:${PATH}"
else
  echo "ERROR: JDK not found. Please install/configure a JDK, not a JRE."
  read -r "?Press Enter to close..."
  exit 1
fi

echo "JAVA_HOME: ${JAVA_HOME}"

if ! command -v docker >/dev/null 2>&1; then
  echo "ERROR: Docker command not found."
  read -r "?Press Enter to close..."
  exit 1
fi

mkdir -p "${SERVICE_DIR}/logs"
if [ ! -f "${COMPOSE_FILE}" ]; then
  cat > "${COMPOSE_FILE}" <<'YAML'
services:
  bio-ai-service-dev:
    image: bio-ai-service:dev
    container_name: bio-ai-service-dev
    environment:
      - NACOS_HOST=nacos-standalone
      - DISCOVERY_HOST=bio-ai-service-dev
      - NACOS_PORT=8848
      - NACOS_REGISTER_ENABLED=true
      - SPIRNG_PROFILES_ACTIVE=dev
      - SERVER_PORT=18095
      - DEBUG_PORT=28095
      - AI_LLM_BASE_URL=${AI_LLM_BASE_URL:-https://ws-g96i4edvjqyqgx4m.cn-beijing.maas.aliyuncs.com/compatible-mode/v1}
      - AI_LLM_API_KEY=${AI_LLM_API_KEY:-}
      - AI_LLM_MODEL=${AI_LLM_MODEL:-qwen-plus}
      - AI_LLM_TIMEOUT=${AI_LLM_TIMEOUT:-180000}
      - AI_DB_URL=jdbc:mysql://mysql-dev:3306/bio_cer_local?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
      - AI_DB_USERNAME=root
      - AI_DB_PASSWORD=mysql2026
      - REDIS_HOST=redis-dev
      - REDIS_PORT=6379
      - REDIS_PASSWORD=redis2026
      - JAVA_OPTS= -Xmx768m -Xms768m -Xmn384m -Xss256k
      - TZ=Asia/Shanghai
    restart: always
    volumes:
      - "./logs:/app/logs"
    ports:
      - 18095:18095
      - 28095:28095
    networks:
      - bio-dev
networks:
  bio-dev:
    external: true
YAML
else
  if grep -q "NACOS_REGISTER_ENABLED=" "${COMPOSE_FILE}"; then
    sed -i.bak 's/NACOS_REGISTER_ENABLED=.*/NACOS_REGISTER_ENABLED=true/' "${COMPOSE_FILE}"
  fi
  if ! grep -q "REDIS_HOST=" "${COMPOSE_FILE}"; then
    sed -i.bak '/AI_DB_PASSWORD=/a\
      - REDIS_HOST=redis-dev\
      - REDIS_PORT=6379\
      - REDIS_PASSWORD=redis2026
' "${COMPOSE_FILE}"
  elif ! grep -q "REDIS_PASSWORD=" "${COMPOSE_FILE}"; then
    sed -i.bak '/REDIS_PORT=/a\
      - REDIS_PASSWORD=redis2026
' "${COMPOSE_FILE}"
  fi
  if ! grep -q "AI_LLM_API_KEY=" "${COMPOSE_FILE}"; then
    sed -i.bak '/DEBUG_PORT=/a\
      - AI_LLM_BASE_URL=${AI_LLM_BASE_URL:-https://ws-g96i4edvjqyqgx4m.cn-beijing.maas.aliyuncs.com/compatible-mode/v1}\
      - AI_LLM_API_KEY=${AI_LLM_API_KEY:-}\
      - AI_LLM_MODEL=${AI_LLM_MODEL:-qwen-plus}\
      - AI_LLM_TIMEOUT=${AI_LLM_TIMEOUT:-180000}
' "${COMPOSE_FILE}"
  fi
fi

cd "${PROJECT_DIR}"

echo "Building bio-drqi-ai app jar..."
mvn -pl bio-drqi-ai/bio-drqi-ai-app -am clean package -DskipTests

echo "Building Docker image ${IMAGE_NAME}..."
if docker image inspect "${IMAGE_NAME}" >/dev/null 2>&1; then
  echo "Found local ${IMAGE_NAME}; using it as the runtime base."
  docker tag "${IMAGE_NAME}" "${BASE_IMAGE}"
  CREATED_TEMP_BASE="true"
elif docker container inspect "${CONTAINER_NAME}" >/dev/null 2>&1; then
  echo "Found local container ${CONTAINER_NAME}; creating runtime base image from it."
  docker commit "${CONTAINER_NAME}" "${BASE_IMAGE}"
  CREATED_TEMP_BASE="true"
elif docker image inspect "${LOCAL_JAVA_BASE_IMAGE}" >/dev/null 2>&1; then
  echo "Using local Java base image ${LOCAL_JAVA_BASE_IMAGE}."
  BASE_IMAGE="${LOCAL_JAVA_BASE_IMAGE}"
else
  echo "ERROR: No local Java runtime image found."
  echo "Need one of: ${IMAGE_NAME}, ${CONTAINER_NAME}, or ${LOCAL_JAVA_BASE_IMAGE}."
  read -r "?Press Enter to close..."
  exit 1
fi
docker build --platform linux/amd64 --build-arg BASE_IMAGE="${BASE_IMAGE}" -f bio-drqi-ai/Dockerfile.dev-local -t "${IMAGE_NAME}" bio-drqi-ai

echo "Restarting service with docker compose..."
if ! docker network inspect bio-dev >/dev/null 2>&1; then
  echo "Creating docker network bio-dev..."
  docker network create bio-dev
fi
for container in nacos-standalone mysql-dev redis-dev; do
  if docker container inspect "${container}" >/dev/null 2>&1; then
    echo "Ensuring ${container} is connected to bio-dev..."
    docker network connect bio-dev "${container}" >/dev/null 2>&1 || true
  fi
done
cd "${SERVICE_DIR}"
docker compose -f "${COMPOSE_FILE}" up -d --force-recreate bio-ai-service-dev

echo "Cleaning old unused images..."
if [ "${CREATED_TEMP_BASE}" = "true" ]; then
  docker image rm "${BASE_IMAGE}" >/dev/null 2>&1 || true
fi
docker image prune -f

echo "Done. bio-ai-service-dev is deployed."
trap - EXIT
read -r "?Press Enter to close..." || true
