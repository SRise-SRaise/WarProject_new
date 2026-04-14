<template>
  <a-layout class="admin-layout">
    <a-layout-sider v-model:collapsed="collapsed" collapsible :trigger="null" :width="248" class="sider">
      <div class="sider__inner">
        <button class="brand-panel" type="button" @click="router.push('/admin/dashboard')">
          <span class="brand-panel__mark">EH</span>
          <div v-show="!collapsed" class="brand-panel__copy">
            <strong>教师管理层</strong>
            <small>路由元数据驱动导航</small>
          </div>
        </button>

        <a-menu
          v-model:selectedKeys="selectedKeys"
          v-model:openKeys="openKeys"
          mode="inline"
          theme="dark"
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
                {{ child.title }}
              </a-menu-item>
            </a-sub-menu>
            <a-menu-item v-else :key="item.key">
              <template #icon>
                <component :is="item.icon" />
              </template>
              <span>{{ item.title }}</span>
            </a-menu-item>
          </template>
        </a-menu>

        <div v-show="!collapsed" class="sider__footer">
          <p class="sider__footer-title">当前视角</p>
          <strong>{{ authStore.session?.name ?? '教师账号' }}</strong>
          <span>{{ authStore.session?.department ?? '教学组织与协同' }}</span>
        </div>
      </div>
    </a-layout-sider>

    <a-layout class="admin-shell">
      <a-layout-header class="header">
        <div class="header__left">
          <a-button type="text" class="collapse-button" @click="toggleCollapsed">
            <MenuUnfoldOutlined v-if="collapsed" />
            <MenuFoldOutlined v-else />
          </a-button>
          <div>
            <p class="header__eyebrow">教师管理层</p>
            <h1 class="header__title">{{ currentTitle }}</h1>
            <p class="header__summary">{{ currentSummary }}</p>
          </div>
        </div>

        <div class="header__right">
          <a-space :size="12" wrap>
            <a-tag color="processing">教师态</a-tag>
            <a-button @click="router.push('/')">公共首页</a-button>
            <a-button @click="router.push('/user/login')">切换账号</a-button>
            <a-button type="primary" @click="handleLogout">退出</a-button>
          </a-space>
        </div>
      </a-layout-header>

      <a-layout-content class="content">
        <div class="content__inner">
          <router-view />
        </div>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import type { Component } from 'vue'
import { message } from 'ant-design-vue'
import { MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons-vue'
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
const currentSummary = computed(() => route.meta.summary ?? '围绕教学任务、资源分发与学生状态组织管理界面。')

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
  message.success('已退出教师工作台')
  router.push('/')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: linear-gradient(180deg, #0f2848 0%, #12355f 100%);
}

.sider {
  background: linear-gradient(180deg, #102847 0%, #0d213d 100%);
  border-inline-end: 1px solid rgba(255, 255, 255, 0.08);
}

.sider__inner {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.brand-panel {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 18px 16px 14px;
  padding: 14px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.06);
  color: var(--color-text-on-dark);
  cursor: pointer;
  text-align: left;
}

.brand-panel__mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: linear-gradient(135deg, #d8a545 0%, #f0c878 100%);
  color: #1a2b40;
  font-size: 14px;
  font-weight: 900;
  letter-spacing: 0.08em;
}

.brand-panel__copy {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.brand-panel__copy strong {
  font-size: 16px;
}

.brand-panel__copy small {
  color: rgba(247, 251, 255, 0.72);
  font-size: 12px;
}

.menu {
  flex: 1;
  padding: 8px 10px 18px;
  background: transparent;
}

.menu :deep(.ant-menu-item),
.menu :deep(.ant-menu-submenu-title) {
  margin-inline: 0;
  width: 100%;
  border-radius: 12px;
}

.sider__footer {
  margin: 0 16px 16px;
  padding: 16px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(247, 251, 255, 0.82);
}

.sider__footer-title {
  margin: 0 0 8px;
  font-size: 12px;
  color: rgba(247, 251, 255, 0.62);
}

.sider__footer strong,
.sider__footer span {
  display: block;
}

.sider__footer strong {
  margin-bottom: 6px;
  font-size: 15px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-5);
  height: auto;
  min-height: 92px;
  padding: 18px 28px;
  background: rgba(248, 251, 255, 0.88);
  border-bottom: 1px solid rgba(215, 222, 234, 0.8);
  backdrop-filter: blur(16px);
}

.header__left {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.header__eyebrow {
  margin: 0 0 6px;
  color: var(--color-primary);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.header__title {
  margin: 0;
  color: var(--color-text-main);
  font-family: Georgia, 'Times New Roman', 'Songti SC', serif;
  font-size: 30px;
  line-height: 1.15;
}

.header__summary {
  margin: 8px 0 0;
  color: var(--color-text-secondary);
  font-size: 14px;
}

.collapse-button {
  margin-top: 8px;
}

.content {
  padding: 24px 28px 32px;
  background: linear-gradient(180deg, #edf2f7 0%, #e8eef5 100%);
}

.content__inner {
  min-height: calc(100vh - 148px);
}

@media (max-width: 960px) {
  .header {
    align-items: flex-start;
    flex-direction: column;
    padding: 18px;
  }

  .content {
    padding: 16px;
  }
}
</style>
