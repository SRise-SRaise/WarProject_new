import type { Router } from 'vue-router'
import { useAuthStore } from '@/stores/user/auth'

const APP_TITLE = 'EduHub 教学协同平台'

function buildDefaultPath(role: 'student' | 'teacher' | null): string {
  return role === 'teacher' ? '/admin/dashboard' : '/learning'
}

export function setupRouterGuards(router: Router): void {
  router.beforeEach(async (to) => {
    const authStore = useAuthStore()
    authStore.restoreSession()

    if (to.meta?.title) {
      document.title = `${to.meta.title} - ${APP_TITLE}`
    } else {
      document.title = APP_TITLE
    }

    if (to.meta.guestOnly && authStore.isAuthenticated) {
      return buildDefaultPath(authStore.role)
    }

    if (!to.meta.requiresAuth) {
      return true
    }

    if (!authStore.isAuthenticated) {
      return {
        name: 'Login',
        query: { redirect: to.fullPath }
      }
    }

    if (to.meta.audience === 'admin' && authStore.role !== 'teacher') {
      return buildDefaultPath(authStore.role)
    }

    if (to.meta.audience === 'student' && authStore.role === 'teacher') {
      return buildDefaultPath(authStore.role)
    }

    return true
  })

  router.afterEach(() => {
    return undefined
  })

  router.onError((error) => {
    console.error('路由错误:', error)
  })
}
