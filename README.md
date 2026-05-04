# Coffee Phenix Monorepo

This repository contains two independent Vue + Vite applications:

- `apps/coffee` — публичный сайт кофейни (dev порт `5173`)
- `apps/partniers` — сайт франшизы / партнёров (dev порт `5174`)

## Install

```bash
npm install
```

## Run in Development

Run each project separately:

```bash
npm run dev:coffee
npm run dev:partniers
```

## Build

```bash
npm run build
```

## Production Domains

- `coffeephoenix.ru` и `www.coffeephoenix.ru` → обычно `93.183.83.197`
- `partniers.coffeephoenix.ru` → **тот же IP в DNS**, что и основной сайт, если поддомен ведёт на одну VM (иначе откроется дефолтный vhost с **coffee**). На этой машине должны быть **оба** nginx-конфига (`coffeephoenix.ru` и `partniers`) и каталоги **`…/coffee/dist`** и **`…/partniers/dist`**.

Если партнёры крутятся на **другом** сервере, при деплое задайте хост: `PARTNIERS_DEPLOY_HOST=89.23.99.243 ./scripts/deploy-static.sh partniers` (или `COFFEE_DEPLOY_HOST` / общий `DEFAULT_DEPLOY_HOST`).

Ready-to-use nginx configs are in `deploy/nginx`. Для **`partniers.coffeephoenix.ru`** в конфиге есть **`location /api/`** → `proxy_pass` на `127.0.0.1:8080` (franchise-api). Если на сервере HTTPS добавлен отдельным `server { listen 443 ... }`, скопируйте туда тот же блок `location ^~ /api/ { ... }`, иначе POST на `/api/...` снова уйдёт в SPA и даст **405**.

## Docker: API заявок (`franchise-api`) на сервере

Проще, чем вручную Java + systemd: один каталог с кодом или только `Dockerfile` + `docker-compose.yml` + `.env`.

```bash
cd apps/franchise-api
cp .env.example .env   # заполните MAIL_*, CORS_ORIGINS (прод-домен фронта)
docker compose --env-file compose.env up -d --build
```

Проверка: `curl -s http://127.0.0.1:8080/api/v1/franchise/health`. Порт **8080** в compose проброшен только на **127.0.0.1** — наружу пусть смотрит **nginx** (`proxy_pass http://127.0.0.1:8080` для префикса `/api` или отдельный поддомен).

Обновление после `git pull`:

```bash
cd apps/franchise-api && docker compose --env-file compose.env up -d --build
```

Если на сервере **`git pull`** ругается на **`package-lock.json`**: локально его менял `npm install` — либо **`git checkout -- package-lock.json`** (и при необходимости **`package.json`**) из репо, затем снова `git pull`, либо **`git stash -u`** → `git pull` → разрулить stash.

**Лимит Docker Hub (429 / `toomanyrequests`):** в `Dockerfile` базовые образы — **`public.ecr.aws/docker/library/...`**. Файл **`compose.env`** задаёт `COMPOSE_BAKE=false`, чтобы не было предупреждения про Bake/buildx (альтернатива: пакет `docker-buildx-plugin` или `docker login` на Hub, если снова тянете с `docker.io`).

## Deploy to Servers

Use one script for both projects:

```bash
./scripts/deploy-static.sh coffee
./scripts/deploy-static.sh partniers
```

What it does:

1. Builds the app.
2. Uploads `dist` via `rsync`.
3. Runs `nginx -t` and `systemctl reload nginx` on remote server.

Remote target folders (по умолчанию один хост `93.183.83.197`, см. переменные в `scripts/deploy-static.sh`):

- coffee: `/var/www/coffeephoenix/coffee/dist`
- partniers: `/var/www/coffeephoenix/partniers/dist`

## Enable Nginx Configs (once per server)

Если **оба** домена смотрят на **одну** VM (`93.183.83.197`), выполните **оба** блока ниже на этой машине: два `server { server_name ... }` и разные `root`, иначе поддомен отдаст чужой сайт (часто основной coffee как `default_server`).

For `coffeephoenix.ru` server:

```bash
sudo cp deploy/nginx/coffeephoenix.ru.conf /etc/nginx/sites-available/coffeephoenix.ru.conf
sudo ln -sf /etc/nginx/sites-available/coffeephoenix.ru.conf /etc/nginx/sites-enabled/coffeephoenix.ru.conf
sudo nginx -t && sudo systemctl reload nginx
sudo certbot --nginx -d coffeephoenix.ru -d www.coffeephoenix.ru
```

For `partniers.coffeephoenix.ru` server:

```bash
sudo cp deploy/nginx/partniers.coffeephoenix.ru.conf /etc/nginx/sites-available/partniers.coffeephoenix.ru.conf
sudo ln -sf /etc/nginx/sites-available/partniers.coffeephoenix.ru.conf /etc/nginx/sites-enabled/partniers.coffeephoenix.ru.conf
sudo nginx -t && sudo systemctl reload nginx
sudo certbot --nginx -d partniers.coffeephoenix.ru
```
