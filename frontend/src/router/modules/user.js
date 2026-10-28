import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'
const route = [
  {
    path: '/user',
    component: Layout,
    meta: { title: '用户管理', icon: 'sfont system-user' },
    children: [
      {
        path: 'edit',
        component: createNameComponent(() => import('@/views/user/edit.vue')),
        meta: { title: '修改信息', icon: { name: 'User' } }
      }
    ]
  }
]

export default route