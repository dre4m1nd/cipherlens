import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'
const route = [
  {
    path: '/password',
    component: Layout,
    meta: { title: '密码管理', icon: 'sfont system-mima' },
    children: [
      {
        path: '',
        component: createNameComponent(() => import('@/views/password/list.vue')),
        meta: { title: '密码管理', icon: 'sfont system-mima' }
      }
    ]
  }
]

export default route