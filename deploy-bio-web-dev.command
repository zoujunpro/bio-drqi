#!/bin/zsh -l
set -euo pipefail

PROJECT_DIR="/Users/zoujun/work/workspace/qibio/bio-drqi/qi-gateway"
SERVICE_DIR="/Users/zoujun/data/service/bio-web-dev"
HTML_DIR="${SERVICE_DIR}/html"
NGINX_COMMON_DIR="/Users/zoujun/data/common/nginx"
NGINX_DIR="${NGINX_COMMON_DIR}/nginx/conf.d"
NGINX_CONF="${NGINX_DIR}/bio-web-dev.conf"
NGINX_PORT="18080"
GATEWAY_URL="http://bio-base-gateway-dev:18090"
CER_LOCAL_URL="http://bio-cer-service-dev:18093"
AI_LOCAL_URL="http://bio-ai-service-dev:18095"

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
echo "Nginx port: ${NGINX_PORT}"
echo "Gateway: ${GATEWAY_URL}"
echo "CER local service: ${CER_LOCAL_URL}"
echo "AI local service: ${AI_LOCAL_URL}"

if ! command -v node >/dev/null 2>&1; then
  echo "ERROR: node command not found."
  read -r "?Press Enter to close..."
  exit 1
fi

if ! command -v npm >/dev/null 2>&1; then
  echo "ERROR: npm command not found."
  read -r "?Press Enter to close..."
  exit 1
fi

mkdir -p "${HTML_DIR}" "${NGINX_DIR}"
cd "${PROJECT_DIR}"

echo "Node: $(node -v)"
echo "NPM: $(npm -v)"

NPM_VERSION="$(npm -v)"
NPM_MAJOR="${NPM_VERSION%%.*}"
if [ "${NPM_MAJOR}" -lt 7 ]; then
  echo "Current npm ${NPM_VERSION} cannot read lockfile v3; using temporary npm@9."
  NPM_RUN="npx -p npm@9 npm"
else
  NPM_RUN="npm"
fi

if [ ! -x node_modules/.bin/vite ]; then
  echo "Installing npm dependencies..."
  eval "${NPM_RUN} install --no-package-lock --legacy-peer-deps"
else
  echo "node_modules found; skip npm install."
fi

echo "Building qi-gateway dev package..."
eval "${NPM_RUN} run build:develop"

if [ ! -d dist ]; then
  echo "ERROR: build output dist not found."
  read -r "?Press Enter to close..."
  exit 1
fi

echo "Publishing static files to ${HTML_DIR}..."
rsync -a --delete dist/ "${HTML_DIR}/"

cat > "${NGINX_CONF}" <<EOF
server {
    listen ${NGINX_PORT};
    server_name localhost 127.0.0.1;

    access_log /var/log/nginx/bio-web-dev.access.log main;
    error_log /var/log/nginx/bio-web-dev.error.log;

    location / {
        root /home/web/bio-web-dev;
        index index.html index.htm;
        try_files \$uri \$uri/ /index.html;
    }

    location /dev-api/ai/admin/config/ {
        proxy_pass ${AI_LOCAL_URL}/ai/admin/config/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    location = /dev-api/ai/chat {
        proxy_pass ${AI_LOCAL_URL}/ai/chat;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    location /dev-api/ai/chat/ {
        proxy_pass ${AI_LOCAL_URL}/ai/chat/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    location /dev-api/ai/ {
        proxy_pass ${AI_LOCAL_URL}/ai/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    location /dev-api/ {
        proxy_pass ${GATEWAY_URL}/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

echo "Nginx config generated: ${NGINX_CONF}"

if ! command -v docker >/dev/null 2>&1; then
  echo "ERROR: Docker command not found; static files are published but nginx cannot be restarted."
  read -r "?Press Enter to close..."
  exit 1
fi

if ! docker network inspect bio-dev >/dev/null 2>&1; then
  echo "Creating docker network bio-dev..."
  docker network create bio-dev
fi

cd "${NGINX_COMMON_DIR}"
echo "Starting/reloading nginx container..."
docker compose up -d

echo "Testing nginx inside container..."
docker exec nginx-test nginx -t

echo "Reloading nginx inside container..."
docker exec nginx-test nginx -s reload

echo "Done. bio-web-dev deployed: http://localhost:${NGINX_PORT}"

trap - EXIT
read -r "?Press Enter to close..." || true
