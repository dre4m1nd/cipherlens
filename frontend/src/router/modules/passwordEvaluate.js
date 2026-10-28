import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'
const route = [
  {
    path: '/password-evaluate',
    component: Layout,
    meta: { title: '密码评估', icon: { name: 'Key' } },
    children: [
      {
        path: '',
        component: createNameComponent(() => import('@/views/password/evaluate.vue')),
        meta: { title: '密码评估', icon: { name: 'Key' } }
      }
    ]
  }
]

export default route