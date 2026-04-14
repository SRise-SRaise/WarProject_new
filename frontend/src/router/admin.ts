import type { RouteRecordRaw } from 'vue-router'
import BackendLayout from '@/layouts/BackendLayout.vue'
import { DashboardOutlined, ReadOutlined } from '@ant-design/icons-vue'
import { adminExamRoutes } from './exam'
import { adminExperimentRoutes } from './experiment'
import { adminHomeworkRoutes } from './homework'
import { adminUserRoutes } from './user'

const TeacherDashboardView = () => import('@/views/common/admin/TeacherDashboardView.vue')
const MaterialManagementView = () => import('@/views/common/admin/MaterialManagementView.vue')

const adminRoutes: RouteRecordRaw = {
  path: '/admin',
  component: BackendLayout,
  redirect: '/admin/dashboard',
  meta: {
    title: '教师管理后台',
    requiresAuth: true,
    audience: 'admin'
  },
  children: [
    {
      path: 'dashboard',
      name: 'AdminDashboard',
      component: TeacherDashboardView,
      meta: {
        title: '教学工作台',
        icon: DashboardOutlined,
        order: 1,
        requiresAuth: true,
        audience: 'admin',
        summary: '汇总待处理教学任务、资源更新与班级状态。'
      }
    },
    {
      path: 'materials',
      name: 'AdminMaterialManagement',
      component: MaterialManagementView,
      meta: {
        title: '资料管理',
        icon: ReadOutlined,
        order: 12,
        requiresAuth: true,
        audience: 'admin',
        summary: '查看共享资料台账、开放范围与最近更新，当前波次保持只读管理。'
      }
    },
    ...adminUserRoutes,
    ...adminHomeworkRoutes,
    ...adminExperimentRoutes,
    ...adminExamRoutes,
  ]
}

export default adminRoutes
