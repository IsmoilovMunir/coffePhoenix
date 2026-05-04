#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 <coffee|partniers>"
  exit 1
fi

APP_KEY="$1"

# Один сервер для обоих доменов (как у DNS A на 93.183…). Для второго сервера:
#   COFFEE_DEPLOY_HOST=… PARTNIERS_DEPLOY_HOST=… ./scripts/deploy-static.sh partniers
DEFAULT_DEPLOY_HOST="${DEFAULT_DEPLOY_HOST:-93.183.83.197}"

case "$APP_KEY" in
  coffee)
    APP_PATH="apps/coffee"
    BUILD_CMD="npm run build -w apps/coffee"
    HOST="${COFFEE_DEPLOY_HOST:-$DEFAULT_DEPLOY_HOST}"
    REMOTE_DIR="/var/www/coffeephoenix/coffee"
    ;;
  partniers)
    APP_PATH="apps/partniers"
    BUILD_CMD="npm run build -w apps/partniers"
    HOST="${PARTNIERS_DEPLOY_HOST:-$DEFAULT_DEPLOY_HOST}"
    REMOTE_DIR="/var/www/coffeephoenix/partniers"
    ;;
  *)
    echo "Unknown app '$APP_KEY'. Use: coffee or partniers"
    exit 1
    ;;
esac

echo "Building $APP_KEY..."
$BUILD_CMD

echo "Creating remote folders on $HOST..."
ssh root@"$HOST" "mkdir -p '$REMOTE_DIR'"

echo "Uploading dist to $HOST..."
rsync -az --delete "$APP_PATH/dist/" root@"$HOST":"$REMOTE_DIR/dist/"

echo "Reloading nginx on $HOST..."
ssh root@"$HOST" "nginx -t && systemctl reload nginx"

echo "Done: $APP_KEY deployed to $HOST"
