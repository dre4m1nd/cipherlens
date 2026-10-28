/*
 * @Date: 2022-05-22 20:44:25
 * @Description: 
 */
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import { baidu } from './utils/system/statistics'
import 'element-plus/theme-chalk/display.css' // 引入基于断点的隐藏类
import 'element-plus/dist/index.css'
import 'normalize.css' // css初始化
import 'virtual:windi.css' // 引入Windicss样式
import './assets/style/common.css'
import './assets/style/transition.css'
import './theme/index.css'
import App from './App.vue'
import store from './store'
import router from './router'
import { getAuthRoutes } from './router/permission'
// Element Plus图标组件
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
if (import.meta.env.MODE !== 'development') { // 非开发环境调用百度统计
  baidu()
}

/** 权限路由处理主方法 */
getAuthRoutes().then(() => {
  const app = createApp(App)
  app.use(ElementPlus, { size: store.state.app.elementSize })
  app.use(store)
  app.use(router)
  
  // 全局注册Element Plus图标组件
  for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }
  
  // app.config.performance = true
  app.mount('#app')
})

