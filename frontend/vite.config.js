/*
 * @Author: luoxi
 * @Date: 2022-01-25 09:51:12
 * @LastEditors: Please set LastEditors
 * @LastEditTime: 2025-12-12 15:00:00
 * @FilePath: \vue-admin-box\vite.config.js
 * @Description: Vite 配置文件（Vue3 + JS）- 适配 Docker 生产部署（移除 Mock 版本）
 */
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { vitePluginSvg } from "@webxrd/vite-plugin-svg"
import WindiCSS from 'vite-plugin-windicss'
import { resolve } from 'path'

// 路径解析函数
const pathResolve = (dir) => {
  return resolve(__dirname, dir)
}

// 路径别名配置
const alias = {
  '@': pathResolve("src")
}

export default defineConfig((configEnv) => {
  const { mode, command } = configEnv
  const env = loadEnv(mode, process.cwd(), 'VITE_')

  return {
    base: env.VITE_BASE_PATH || '/',
    resolve: {
      alias,
      extensions: ['.mjs', '.js', '.jsx', '.json', '.vue']
    },
    server: {
      port: env.VITE_PORT || 3001,
      host: '0.0.0.0',
      open: env.VITE_AUTO_OPEN === 'true',
      cors: true,
      // 开发环境代理：保留 /api 前缀（不再 rewrite 删除），与生产环境一致
      proxy: command === 'serve' 
        ? { 
            '/api': {
              target: env.VITE_API_URL || 'https://dev-api.xxx.com', // 开发环境真实后端地址
              changeOrigin: true,
              secure: false,
              // 移除 rewrite！开发环境也保留 /api 前缀，和生产环境逻辑一致
               rewrite: (path) => path.replace(/^\/api/, '') 
            }
          }
        : undefined
    },
    build: {
      target: 'es2015',
      outDir: 'dist',
      assetsDir: 'assets',
      minify: 'terser',
      sourcemap: env.VITE_BUILD_SOURCEMAP === 'true',
      chunkSizeWarningLimit: 1500,
      rollupOptions: {
        output: {
          manualChunks: {
            'echarts': ['echarts'],
            'vue-vendor': ['vue', 'vue-router', 'vuex']
          },
          assetFileNames: 'assets/[ext]/[name]-[hash].[ext]',
          chunkFileNames: 'js/[name]-[hash].js',
          entryFileNames: 'js/[name]-[hash].js'
        }
      }
    },
    plugins: [
      vue(),
      WindiCSS(),
      vitePluginSvg({
        iconDirs: [pathResolve('src/assets/svg')],
        main: pathResolve('src/main.js'),
        symbolIdFormat: 'icon-[name]'
      }),
    ],
    css: {
      postcss: {
        plugins: [
          {
            postcssPlugin: 'internal:charset-removal',
            AtRule: {
              charset: (atRule) => {
                if (atRule.name === 'charset') {
                  atRule.remove();
                }
              }
            }
          }
        ],
      },
    }
  }
})