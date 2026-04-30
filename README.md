# Coffee Phenix Monorepo

This repository contains two independent Vue + Vite applications:

- `apps/coffee` - public coffee shop website (dev port `5173`)
- `apps/partners` - franchise/partners website (dev port `5174`)

## Install

```bash
npm install
```

## Run in Development

Run each project separately:

```bash
npm run dev:coffee
npm run dev:partners
```

## Build

```bash
npm run build
```

## Production Domains

- `coffeephoenix.ru` and `www.coffeephoenix.ru` -> server `93.183.83.197`
- `partniers.coffeephoenix.ru` -> server `89.23.99.243`

Ready-to-use nginx configs are in `deploy/nginx`.

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

Remote target folders:

- coffee: `/var/www/coffeephoenix/coffee/dist`
- partniers: `/var/www/coffeephoenix/partniers/dist`

## Enable Nginx Configs (once per server)

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
