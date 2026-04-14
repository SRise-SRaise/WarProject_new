import type { RouteRecordRaw } from 'vue-router'
import BasicLayout from '@/layouts/BasicLayout.vue'

const LearningOverviewView = () => import('@/views/common/student/LearningOverviewView.vue')
const MaterialListView = () => import('@/views/common/material/MaterialListView.vue')
const MaterialDetailView = () => import('@/views/common/material/MaterialDetailView.vue')

const commonRoutes: RouteRecordRaw[] = [
  {
    path: '/learning',
    component: BasicLayout,
    children: [
      {
        path: '',
        name: 'StudentLearningOverview',
        component: LearningOverviewView,
        meta: {
          title: '学习概览',
          requiresAuth: true,
          audience: 'student',
          shell: 'student',
          navLabel: '学习概览',
          summary: '聚合待办任务、最近结果和个人学习提醒。'
        }
      }
    ]
  },
  {
    path: '/materials',
    component: BasicLayout,
    children: [
      {
        path: '',
        name: 'MaterialList',
        component: MaterialListView,
        meta: {
          title: '资料',
          requiresAuth: true,
          audience: 'student',
          shell: 'student',
          navLabel: '资料',
          summary: '按主题与资料类型查看学习资源，并进入详情页。'
        }
      },
      {
        path: ':id',
        name: 'MaterialDetail',
        component: MaterialDetailView,
        meta: {
          title: '资料详情',
          requiresAuth: true,
          audience: 'student',
          shell: 'student',
          summary: '查看资料说明、章节摘要和附件信息。'
        }
      }
    ]
  }
]

export default commonRoutes
