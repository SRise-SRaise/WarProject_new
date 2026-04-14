import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8200',
        changeOrigin: true
      },
      '/auth': {
        target: 'http://localhost:8200',
        changeOrigin: true
      },
      '/users': {
        target: 'http://localhost:8200',
        changeOrigin: true
      }
    }
  },
  preview: {
    proxy: {
      '/api': {
        target: 'http://localhost:8200',
        changeOrigin: true
      },
      '/auth': {
        target: 'http://localhost:8200',
        changeOrigin: true
      },
      '/users': {
        target: 'http://localhost:8200',
        changeOrigin: true
      }
    }
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
