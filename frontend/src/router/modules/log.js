import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'
const route = [
  {
    path: '/log',
    component: Layout,
    meta: { title: '日志查询', icon: 'sfont system-document' },
    children: [
      {
        path: '',
        component: createNameComponent(() => import('@/views/log/list.vue')),
        meta: { title: '日志查询', icon: 'sfont system-document' }
      }
    ]
  }
]

export default route