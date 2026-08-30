import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path' // 导入path模块

export default defineConfig({
  plugins: [vue()],
  // 新增别名配置
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    strictPort: true,
    host: '0.0.0.0',
    proxy: {
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/record': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/order': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/consult': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})