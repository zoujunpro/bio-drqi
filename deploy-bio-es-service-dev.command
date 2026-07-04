#!/bin/zsh -l
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SERVICE_DIR="/Users/zoujun/data/service/bio-es-dev"
COMPOSE_FILE="${SERVICE_DIR}/docker-compose.yml"
IMAGE_NAME="bio-es-service:dev"
BASE_IMAGE="bio-es-service:dev-base"
LOCAL_JAVA_BASE_IMAGE="nacos/nacos-server:v2.3.2"
CONTAINER_NAME="bio-es-service-dev"
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
cat > "${COMPOSE_FILE}" <<'YAML'
services:
  bio-es-service-dev:
    image: bio-es-service:dev
    container_name: bio-es-service-dev
    environment:
      - NACOS_HOST=nacos-standalone
      - DISCOVERY_HOST=bio-es-service-dev
      - NACOS_PORT=8848
      - SPRING_PROFILES_ACTIVE=dev
      - SERVER_PORT=18094
      - DEBUG_PORT=28094
      # 本地开发环境不启动 Canal/Kafka 增量同步，避免没有 kafka 容器时 ES 查询服务启动失败。
      - BIO_ES_CANAL_ENABLED=false
      - BIO_ES_CANAL_FORCE_DISABLED=true
      - SPRING_KAFKA_LISTENER_AUTO_STARTUP=false
      - JAVA_OPTS=-Xmx768m -Xms768m -Xmn384m -Xss256k -Dbio.es.canal.enabled=false -Dbio.es.canal.force-disabled=true -Dspring.kafka.listener.auto-startup=false
      - TZ=Asia/Shanghai
    restart: always
    volumes:
      - "./logs:/app/logs"
    ports:
      - 18094:18094
      - 28094:28094
    networks:
      - bio-dev
networks:
  bio-dev:
    external: true
YAML

cd "${PROJECT_DIR}"

echo "Building bio-drqi-es jar..."
mvn -pl bio-drqi-es -am clean package -DskipTests

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
docker build --platform linux/amd64 --build-arg BASE_IMAGE="${BASE_IMAGE}" -f bio-drqi-es/Dockerfile.dev-local -t "${IMAGE_NAME}" bio-drqi-es

echo "Restarting service with docker compose..."
if ! docker network inspect bio-dev >/dev/null 2>&1; then
  echo "Creating docker network bio-dev..."
  docker network create bio-dev
fi
for container in nacos-standalone mysql-dev redis-dev elasticsearch; do
  if docker container inspect "${container}" >/dev/null 2>&1; then
    echo "Ensuring ${container} is connected to bio-dev..."
    docker network connect bio-dev "${container}" >/dev/null 2>&1 || true
  fi
done
cd "${SERVICE_DIR}"
docker compose -f "${COMPOSE_FILE}" up -d --force-recreate bio-es-service-dev

echo "Cleaning old unused images..."
if [ "${CREATED_TEMP_BASE}" = "true" ]; then
  docker image rm "${BASE_IMAGE}" >/dev/null 2>&1 || true
fi
docker image prune -f

echo "Done. bio-es-service-dev is deployed."
trap - EXIT
read -r "?Press Enter to close..." || true
