import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        vueDevTools(),
    ],
    server: {
        port: 5173,
        proxy: {
            '/auth-api': {
                target: 'http://localhost:8089',
                changeOrigin: true
            },
            '/user-api': {target: 'http://localhost:8089', changeOrigin: true},
            '/booking-api': { target: 'http://localhost:8089', changeOrigin: true },
            '/notification-api': { target: 'http://localhost:8089', changeOrigin: true },
            '/payment-api': { target: 'http://localhost:8085', changeOrigin: true }
        }
    },
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        },
    },
})
