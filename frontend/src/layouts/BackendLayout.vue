<template>
  <a-layout class="admin-layout">
    <a-layout-header class="header">
      <div class="header-content">
        <div class="logo">
          <span class="logo-text">管理后台模板</span>
        </div>
        <div class="header-right">
          <a-space :size="12">
            <a-tag color="blue">无鉴权模式</a-tag>
            <a-button size="small" @click="router.push('/')">返回前台</a-button>
          </a-space>
        </div>
      </div>
    </a-layout-header>

    <a-layout>
      <a-layout-sider
        v-model:collapsed="collapsed"
        :trigger="null"
        collapsible
        :width="200"
        class="sider"
      >
        <a-menu
          v-model:selectedKeys="selectedKeys"
          v-model:openKeys="openKeys"
          mode="inline"
          :theme="theme"
          class="menu"
          @click="handleMenuClick"
        >
          <template v-for="item in menuItems" :key="item.key">
            <a-sub-menu v-if="item.children" :key="`sub-${item.key}`">
              <template #icon>
                <component :is="item.icon" />
              </template>
              <template #title>{{ item.title }}</template>
              <a-menu-item v-for="child in item.children" :key="child.key">
                {{ child.title }}
              </a-menu-item>
            </a-sub-menu>
            <a-menu-item v-else :key="item.key">
              <component :is="item.icon" />
              <span>{{ item.title }}</span>
            </a-menu-item>
          </template>
        </a-menu>
      </a-layout-sider>

      <a-layout 
        class="content-layout" 
        :class="collapsed ? 'content-layout-collapsed' : 'content-layout-expanded'"
      >
        <a-layout-content class="content">
          <router-view />
        </a-layout-content>
      </a-layout>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import type { Component } from 'vue';
import type { MenuGroup } from '@/router/admin';

const router = useRouter();
const route = useRoute();

const collapsed = ref(false);
const theme = ref<'light' | 'dark'>('light');
const selectedKeys = ref<string[]>(['dashboard']);
const openKeys = ref<string[]>([]);

interface MenuItem {
  key: string;
  title: string;
  icon?: Component;
  children?: MenuItem[];
}

/** 从路由配置动态构建菜单树，路由是唯一数据源 */
const menuItems = computed((): MenuItem[] => {
  const adminRoute = router.getRoutes().find((r) => r.path === '/admin');
  const groups = new Map<string, MenuItem>();
  const items: MenuItem[] = [];

  for (const child of adminRoute?.children ?? []) {
    const { title, icon, group } = child.meta ?? {};
    const key = child.name as string;

    if (group) {
      const { key: gKey, title: gTitle, icon: gIcon } = group as MenuGroup;
      if (!groups.has(gKey)) {
        const groupItem: MenuItem = { key: gKey, title: gTitle, icon: gIcon, children: [] };
        groups.set(gKey, groupItem);
        items.push(groupItem);
      }
      groups.get(gKey)!.children!.push({ key, title: title ?? key });
    } else {
      items.push({ key, title: title ?? key, icon: icon as Component });
    }
  }

  return items;
});

// 路由 name 与 menu key 保持一致，直接用 route.name 驱动高亮
watch(
  () => route.name,
  (name) => {
    if (name) {
      selectedKeys.value = [name as string];
    }
  },
  { immediate: true }
);

const handleMenuClick = ({ key }: { key: string }) => {
  router.push({ name: key });
};
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.header {
  background: #fff;
  padding: 0;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  position: sticky;
  top: 0;
  z-index: 1001;
  height: 64px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  color: var(--color-primary);
}

.header-right {
  display: flex;
  align-items: center;
}

.sider {
  background: #fff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  height: calc(100vh - 64px);
  position: fixed;
  left: 0;
  top: 64px;
  overflow-y: auto;
  z-index: 100;
}

.menu {
  border-right: none;
  padding-top: 16px;
}

.content-layout {
  background: #f0f2f5;
  transition: all 0.2s;
}

.content-layout-collapsed {
  margin-left: 80px;
}

.content-layout-expanded {
  margin-left: 200px;
}

.content {
  margin: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  min-height: calc(100vh - 112px);
}
</style>

