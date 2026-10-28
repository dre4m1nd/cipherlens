/*
 * @Date: 2022-07-24 21:43:47
 * @Description: 
 */
/** 引入类型 */

/** 引入路由相关的资源 */
import router, { modules } from './index'
/** 引入vuex实例 */
import store from '@/store'
/** 引入后端路由控制器 */
import { isBackMenu } from '@/config'
import Layout from '@/layout/index.vue'
import { createNameComponent } from './createNode'

/** 引入纯前端路由 */
import FrontRoutes from './permission/front'
/** 引入后端路由 */
import getMenu from './permission/back'

/** 
 * @name 动态路由的权限新增，供登录后调用
 * @other 如果需要进行后端接口控制菜单的话，请在此拿到后端的菜单树与asyncRoutes对比，生成一个新的值
 */
async function addRoutes() {
  // 已验证完成，下面代码添加的可以实时同步至菜单中去，可以添加setTimeout(() => {}) 模拟异步代码的操作
  // 利用前端路由表模拟后端数据问题
  // 等待后端接口返回数据后再回调出去，防止刷新跳转404
  return new Promise((resolve) => {
    if (isBackMenu) {
      getMenu()
      .then((data) => {
        data.forEach(item => {
          // 检查路由是否已存在，避免重复添加
          const routeExists = modules.some(route => route.path === item.path)
          if (!routeExists) {
            modules.push(item)
            router.addRoute(item)
          }
        })
        // 添加通配符路由，确保在所有动态路由加载完成后添加
        addWildcardRoute()
        resolve()
      })
      return
    }    
    FrontRoutes.forEach(item => {
      // 检查路由是否已存在，避免重复添加
      const routeExists = modules.some(route => route.path === item.path)
      if (!routeExists) {
        modules.push(item)
        router.addRoute(item)
      }
    })
    // 添加通配符路由，确保在所有动态路由加载完成后添加
    addWildcardRoute()
    resolve()
  })
}

/**
 * 添加通配符路由，用于处理404页面
 */
function addWildcardRoute() {
  const wildcardRoute = {
    path: "/:pathMatch(.*)",
    component: Layout,
    redirect: "/404",
    hideMenu: true,
    meta: { title: '' },
  }
  
  // 检查通配符路由是否已存在
  const routeExists = modules.some(route => route.path === wildcardRoute.path)
  if (!routeExists) {
    modules.push(wildcardRoute)
    router.addRoute(wildcardRoute)
  }
}

/**
 * @des 登录了之后会执行这个方法，实现动态路由的功能
 */
export async function getAuthRoutes() {
  // 判断token是否存在，存在则调用添加路由的方法
  if (store.state.user.token) {
    await addRoutes()
  }
}
