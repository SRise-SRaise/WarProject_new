import type { RouteRecordRaw } from 'vue-router'
import BasicLayout from '@/layouts/BasicLayout.vue'
import PlaceholderView from '@/views/PlaceholderView.vue'

const LandingView = () => import('@/views/common/public/LandingView.vue')
const LoginView = () => import('@/views/user/auth/LoginView.vue')
const RegisterView = () => import('@/views/user/auth/RegisterView.vue')

const basicRoutes: RouteRecordRaw[] = [
  {
    path: '/user',
    component: BasicLayout,
    children: [
      {
        path: 'login',
        name: 'Login',
        component: LoginView,
        meta: {
          title: '用户登录',
          shell: 'auth',
          guestOnly: true,
          audience: 'public',
          summary: '通过统一登录入口进入学生学习层或教师管理层。'
        }
      },
      {
        path: 'register',
        name: 'Register',
        component: RegisterView,
        meta: {
          title: '用户注册',
          shell: 'auth',
          guestOnly: true,
          audience: 'public',
          summary: '注册作为学生自助开通入口，可按业务策略开启或关闭。'
        }
      }
    ]
  },
  {
    path: '/',
    component: BasicLayout,
    children: [
      {
        path: '',
        name: 'PublicLanding',
        component: LandingView,
        meta: {
          title: '首页',
          shell: 'public',
          audience: 'public',
          wide: true,
          navLabel: '平台首页',
          summary: '统一承载公共品牌介绍、学生入口与教师入口。'
        }
      }
    ]
  },
  {
    path: '/404',
    component: BasicLayout,
    children: [
      {
        path: '',
        name: 'NotFound',
        component: PlaceholderView,
        meta: {
          title: '页面不存在',
          shell: 'public',
          audience: 'public'
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

export default basicRoutes
