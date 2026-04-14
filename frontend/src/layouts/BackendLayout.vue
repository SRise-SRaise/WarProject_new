<template>
  <a-layout class="admin-layout">
    <a-layout-sider
      v-model:collapsed="collapsed"
      collapsible
      :trigger="null"
      :width="220"
      class="sider"
    >
      <div class="sider__inner">
        <div class="brand-panel" :class="{ 'brand-panel--collapsed': collapsed }">
          <div class="brand-panel__mark">EH</div>
          <div v-show="!collapsed" class="brand-panel__copy">
            <strong>EduHub</strong>
            <small>教师管理工作台</small>
          </div>
        </div>

        <a-menu
          v-model:selectedKeys="selectedKeys"
          v-model:openKeys="openKeys"
          mode="inline"
          theme="light"
          class="menu"
          @click="handleMenuClick"
        >
          <template v-for="item in menuItems" :key="item.key">
            <a-sub-menu v-if="item.children" :key="item.key">
              <template #icon>
                <component :is="item.icon" />
              </template>
              <template #title>{{ item.title }}</template>
              <a-menu-item v-for="child in item.children" :key="child.key">
                <template v-if="child.icon" #icon>
                  <component :is="child.icon" />
                </template>
                {{ child.title }}
              </a-menu-item>
            </a-sub-menu>
            <a-menu-item v-else :key="item.key">
              <template v-if="item.icon" #icon>
                <component :is="item.icon" />
              </template>
              <span>{{ item.title }}</span>
            </a-menu-item>
          </template>
        </a-menu>
      </div>
    </a-layout-sider>

    <a-layout class="admin-shell">
      <a-layout-header class="header">
        <div class="header__left">
          <a-button type="text" class="collapse-btn" @click="toggleCollapsed">
            <MenuUnfoldOutlined v-if="collapsed" />
            <MenuFoldOutlined v-else />
          </a-button>
          <div class="header__breadcrumb">
            <span class="header__eyebrow">教师工作台</span>
            <span class="header__title">{{ currentTitle }}</span>
          </div>
        </div>

        <div class="header__right">
          <a-dropdown placement="bottomRight" :trigger="['click']">
            <div class="user-avatar-btn">
              <a-avatar :size="36" class="avatar">
                <template #icon>
                  <UserOutlined />
                </template>
              </a-avatar>
              <span v-show="true" class="user-name">{{ authStore.session?.name }}</span>
              <DownOutlined class="avatar-arrow" />
            </div>
            <template #overlay>
              <a-menu class="user-dropdown-menu">
                <div class="user-dropdown-header">
                  <a-avatar :size="44" class="avatar avatar--lg">
                    <template #icon>
                      <UserOutlined />
                    </template>
                  </a-avatar>
                  <div>
                    <p class="user-dropdown-name">{{ authStore.session?.name }}</p>
                    <p class="user-dropdown-role">教师</p>
                  </div>
                </div>
                <a-menu-divider />
                <a-menu-item key="profile" @click="router.push({ name: 'AdminTeacherProfile' })">
                  <template #icon><IdcardOutlined /></template>
                  个人设置
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" class="logout-item" @click="handleLogout">
                  <template #icon><LogoutOutlined /></template>
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>

      <a-layout-content class="content">
        <div class="content__inner">
          <router-view v-slot="{ Component }">
            <template v-if="Component">
              <component :is="Component" />
            </template>
            <template v-else>
              <div style="padding: 40px; text-align: center">
                <p>页面加载中...</p>
              </div>
            </template>
          </router-view>
        </div>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import type { Component } from 'vue'
import { message } from 'ant-design-vue'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  DownOutlined,
  IdcardOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/common/app'
import { useAuthStore } from '@/stores/user/auth'

interface MenuItem {
  key: string
  title: string
  icon?: Component
  children?: MenuItem[]
}

const router = useRouter()
const route = useRoute()
const appStore = useAppStore()
const authStore = useAuthStore()

const selectedKeys = ref<string[]>([])
const openKeys = ref<string[]>([])

const collapsed = computed({
  get: () => appStore.backendCollapsed,
  set: (value: boolean) => appStore.setBackendCollapsed(value)
})

const menuItems = computed<MenuItem[]>(() => {
  const adminRoute = router.getRoutes().find((record) => record.path === '/admin')
  const grouped = new Map<string, MenuItem>()
  const items: MenuItem[] = []
  const children = [...(adminRoute?.children ?? [])].sort((left, right) => {
    const leftOrder = Number(left.meta?.order ?? 999)
    const rightOrder = Number(right.meta?.order ?? 999)
    return leftOrder - rightOrder
  })

  for (const child of children) {
    if (child.meta?.hideInMenu) {
      continue
    }
    const key = typeof child.name === 'string' ? child.name : child.path
    const title = child.meta?.title ?? key
    const icon = child.meta?.icon
    const group = child.meta?.group

    if (group) {
      const existing = grouped.get(group.key)
      if (existing) {
        existing.children?.push({ key, title, icon })
        continue
      }
      const nextGroup: MenuItem = {
        key: group.key,
        title: group.title,
        icon: group.icon,
        children: [{ key, title, icon }]
      }
      grouped.set(group.key, nextGroup)
      items.push(nextGroup)
      continue
    }

    items.push({ key, title, icon })
  }

  return items
})

const currentTitle = computed(() => route.meta.title ?? '教师管理后台')

const syncMenuState = (): void => {
  if (typeof route.name === 'string') {
    selectedKeys.value = [route.name]
  }
  const adminRoute = router.getRoutes().find((record) => record.path === '/admin')
  const matchedChild = adminRoute?.children.find((child) => child.name === route.name)
  openKeys.value = matchedChild?.meta?.group ? [matchedChild.meta.group.key] : []
}

watch(() => route.name, syncMenuState, { immediate: true })

const toggleCollapsed = (): void => {
  collapsed.value = !collapsed.value
}

const handleMenuClick = ({ key }: { key: string }): void => {
  router.push({ name: key })
}

const handleLogout = (): void => {
  authStore.logout()
  message.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: #f5f6f8;
}

/* ── 侧边栏 ─────────────────────────────── */
.sider {
  background: #fff;
  border-right: 1px solid #e8eaed;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.04);
}

.sider :deep(.ant-layout-sider-children) {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.sider__inner {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.brand-panel {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 64px;
  padding: 0 20px;
  border-bottom: 1px solid #e8eaed;
  flex-shrink: 0;
}

.brand-panel--collapsed {
  justify-content: center;
  padding: 0;
}

.brand-panel__mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #1677ff;
  color: #fff;
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0.05em;
  flex-shrink: 0;
}

.brand-panel__copy {
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow: hidden;
}

.brand-panel__copy strong {
  font-size: 15px;
  font-weight: 700;
  color: #1a1a2e;
  white-space: nowrap;
}

.brand-panel__copy small {
  font-size: 11px;
  color: #8c929a;
  white-space: nowrap;
}

.menu {
  flex: 1;
  overflow-y: auto;
  border-right: none;
  padding: 8px 0;
}

/* ── 顶部栏 ─────────────────────────────── */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #e8eaed;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header__left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  color: #595959;
  font-size: 18px;
}

.collapse-btn:hover {
  color: #1677ff;
  background: #f0f5ff;
}

.header__breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header__eyebrow {
  font-size: 13px;
  color: #8c929a;
}

.header__eyebrow::after {
  content: '/';
  margin-left: 8px;
}

.header__title {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
}

.header__right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* ── 头像按钮 ─────────────────────────────── */
.user-avatar-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.user-avatar-btn:hover {
  background: #f5f6f8;
}

.avatar {
  background: #1677ff;
  color: #fff;
  flex-shrink: 0;
}

.avatar--lg {
  background: #1677ff;
  color: #fff;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a2e;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.avatar-arrow {
  font-size: 11px;
  color: #8c929a;
}

/* ── 下拉菜单 ─────────────────────────────── */
.user-dropdown-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px 10px;
}

.user-dropdown-name {
  margin: 0 0 2px;
  font-size: 14px;
  font-weight: 600;
  color: #1a1a2e;
}

.user-dropdown-role {
  margin: 0;
  font-size: 12px;
  color: #8c929a;
}

:deep(.logout-item) {
  color: #ff4d4f !important;
}

:deep(.logout-item .anticon) {
  color: #ff4d4f !important;
}

/* ── 内容区 ─────────────────────────────── */
.admin-shell {
  background: #f5f6f8;
}

.content {
  padding: 24px;
  background: #f5f6f8;
  min-height: calc(100vh - 64px);
}

.content__inner {
  min-height: 100%;
}
</style>
