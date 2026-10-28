import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'
const route = [
  {
    path: '/system',
    component: Layout,
    redirect: '/404',
    hideMenu: true,
    meta: { title: '系统目录' },
    children: [
      {
        path: '/404',
        component: createNameComponent(() => import('@/views/system/404.vue')),
        meta: { title: '404', hideTabs: true }
      },
      {
        path: '/401',
        component: createNameComponent(() => import('@/views/system/401.vue')),
        meta: { title: '401', hideTabs: true }
      },
      {
        path: '/redirect/:path(.*)',
        component: createNameComponent(() => import('@/views/system/redirect.vue')),
        meta: { title: '重定向页面', hideTabs: true }
      }
    ]
  },
  {
    path: '/login',
    component: createNameComponent(() => import('@/views/system/login.vue')),
    hideMenu: true,
    meta: { title: '登录', hideTabs: true }
  },
  {
    path: '/register',
    component: createNameComponent(() => import('@/views/system/register.vue')),
    hideMenu: true,
    meta: { title: '注册', hideTabs: true }
  },
  {
    path: '/forgot-password',
    component: createNameComponent(() => import('@/views/system/forgot-password.vue')),
    hideMenu: true,
    meta: { title: '忘记密码', hideTabs: true }
  },
  // 通配符路由移到permission.js中动态添加，确保动态路由加载完成后才生效
]

export default route