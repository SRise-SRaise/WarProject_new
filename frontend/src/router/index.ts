import { createRouter, createWebHistory, type Router, type RouteRecordRaw } from 'vue-router'
import { setupRouterGuards } from './guards'

const modules = import.meta.glob('./*.ts', { eager: true })

const routeModules: RouteRecordRaw[] = []

Object.entries(modules).forEach(([key, value]) => {
  if (key !== './index.ts' && key !== './guards.ts' && key !== './meta.ts') {
    const moduleRoutes = (value as { default?: unknown }).default || value
    if (Array.isArray(moduleRoutes)) {
      routeModules.push(...moduleRoutes)
    } else {
      routeModules.push(moduleRoutes as RouteRecordRaw)
    }
  }
})

const router: Router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routeModules
})

setupRouterGuards(router)

export default router
