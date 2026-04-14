import type { Component } from 'vue'

export interface MenuGroup {
  key: string
  title: string
  icon: Component
}

export type RouteAudience = 'public' | 'student' | 'admin'
export type BasicShell = 'public' | 'student' | 'auth'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: Component
    group?: MenuGroup
    order?: number
    requiresAuth?: boolean
    guestOnly?: boolean
    audience?: RouteAudience
    shell?: BasicShell
    navLabel?: string
    wide?: boolean
    summary?: string
    hideInMenu?: boolean
  }
}
