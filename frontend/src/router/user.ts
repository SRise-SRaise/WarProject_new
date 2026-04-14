import type { RouteRecordRaw } from 'vue-router'
import BasicLayout from '@/layouts/BasicLayout.vue'
import { ApartmentOutlined, TeamOutlined, UserOutlined } from '@ant-design/icons-vue'
import type { MenuGroup } from './meta'

const StudentProfileView = () => import('@/views/user/student/ProfileView.vue')
const AdminUserManagementView = () => import('@/views/user/admin/UserManagementView.vue')
const AdminClassManagementView = () => import('@/views/user/admin/ClassManagementView.vue')
const AdminProfileView = () => import('@/views/user/admin/ProfileView.vue')

const peopleGroup: MenuGroup = {
  key: 'people',
  title: '用户与班级',
  icon: TeamOutlined
}

const accountGroup: MenuGroup = {
  key: 'account',
  title: '账户中心',
  icon: UserOutlined
}

const userRoutes: RouteRecordRaw[] = [
  {
    path: '/profile',
    component: BasicLayout,
    children: [
      {
        path: '',
        name: 'StudentProfile',
        component: StudentProfileView,
        meta: {
          title: '个人中心',
          requiresAuth: true,
          audience: 'student',
          shell: 'student',
          navLabel: '个人中心',
          summary: '维护学生个人资料、联系方式和学习提醒偏好。'
        }
      }
    ]
  }
]

export const adminUserRoutes: RouteRecordRaw[] = [
  {
    path: 'users',
    name: 'AdminUserManagement',
    component: AdminUserManagementView,
    meta: {
      title: '用户管理',
      icon: TeamOutlined,
      group: peopleGroup,
      order: 20,
      requiresAuth: true,
      audience: 'admin',
      summary: '查看学生名册、协同教师和学习进度风险。'
    }
  },
  {
    path: 'classes',
    name: 'AdminClassManagement',
    component: AdminClassManagementView,
    meta: {
      title: '班级管理',
      icon: ApartmentOutlined,
      group: peopleGroup,
      order: 21,
      requiresAuth: true,
      audience: 'admin',
      summary: '查看班级结构、完课情况和重点跟进事项。'
    }
  },
  {
    path: 'profile',
    name: 'AdminTeacherProfile',
    component: AdminProfileView,
    meta: {
      title: '个人设置',
      icon: UserOutlined,
      group: accountGroup,
      order: 90,
      requiresAuth: true,
      audience: 'admin',
      hideInMenu: true,
      summary: '维护教师资料、教学偏好和通知方式。'
    }
  }
]

export default userRoutes
