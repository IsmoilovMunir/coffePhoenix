import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 502 у /api/*: чаще всего нет процесса на target (Spring не запущен) или другой порт.
// Переопределение: FRANCHISE_API_PROXY=http://127.0.0.1:9090 npm run dev
const franchiseApiTarget =
  process.env.FRANCHISE_API_PROXY ?? 'http://127.0.0.1:8080'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5174,
    proxy: {
      '/api': {
        target: franchiseApiTarget,
        changeOrigin: true,
        timeout: 120_000,
        proxyTimeout: 120_000,
        configure: (proxy) => {
          proxy.on('error', (err) => {
            console.error('[vite proxy /api →', franchiseApiTarget, ']', err.message)
          })
        },
      },
    },
  },
})
