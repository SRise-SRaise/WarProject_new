<template>
  <header class="navbar" :class="`navbar--${shellMode}`">
    <div class="navbar-container">
      <button class="navbar-brand" type="button" @click="router.push('/')">
        <span class="navbar-brand__mark">EH</span>
        <span class="navbar-brand__copy">
          <strong>EduHub</strong>
          <small>{{ brandSubtitle }}</small>
        </span>
      </button>

      <nav class="navbar-menu" aria-label="主导航">
        <button
          v-for="item in menuItems"
          :key="item.key"
          class="menu-item"
          :class="{ active: isActive(item.path) }"
          type="button"
          @click="router.push(item.path)"
        >
          {{ item.label }}
        </button>
      </nav>

      <div class="navbar-right">
        <div v-if="authStore.isAuthenticated" class="navbar-user">
          <span class="navbar-user__role">{{ roleLabel }}</span>
          <span class="navbar-user__name">{{ authStore.session?.name }}</span>
        </div>

        <a-space :size="12" wrap>
          <a-button v-if="authStore.isTeacher" @click="router.push('/admin/dashboard')">教师工作台</a-button>
          <template v-if="authStore.isAuthenticated">
            <a-button @click="goPrimary">{{ primaryActionLabel }}</a-button>
            <a-button type="primary" @click="handleLogout">退出登录</a-button>
          </template>
          <template v-else>
            <a-button @click="router.push('/user/login')">登录</a-button>
            <a-button type="primary" @click="router.push('/user/register')">注册</a-button>
          </template>
        </a-space>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/user/auth'

interface NavItem {
  key: string
  label: string
  path: string
}

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const publicMenuItems: NavItem[] = [
  { key: 'landing', label: '平台首页', path: '/' }
]

const studentMenuItems: NavItem[] = [
  { key: 'learning', label: '学习概览', path: '/learning' },
  { key: 'materials', label: '资料', path: '/materials' },
  { key: 'homework', label: '作业', path: '/homework' },
  { key: 'experiments', label: '实验', path: '/experiments' },
  { key: 'exams', label: '考试', path: '/exams' },
  { key: 'profile', label: '个人中心', path: '/profile' }
]

const shellMode = computed(() => route.meta.shell ?? 'public')
const menuItems = computed(() => {
  if (authStore.isStudent) {
    return studentMenuItems
  }
  return publicMenuItems
})

const brandSubtitle = computed(() => {
  if (shellMode.value === 'auth') {
    return '统一登录与注册入口'
  }
  if (authStore.isStudent) {
    return '学生学习空间'
  }
  if (authStore.isTeacher) {
    return '公共入口 / 教师工作台联动'
  }
  return '校内资料与教学协同'
})

const roleLabel = computed(() => (authStore.isTeacher ? '教师身份' : '学生身份'))
const primaryActionLabel = computed(() => (authStore.isTeacher ? '继续访问公共页' : '回到学习概览'))

const isActive = (path: string): boolean => {
  if (path === '/') {
    return route.path === '/'
  }
  return route.path.startsWith(path)
}

const goPrimary = (): void => {
  router.push(authStore.defaultRoute)
}

const handleLogout = (): void => {
  authStore.logout()
  message.success('已退出当前会话')
  router.push('/')
}
</script>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 1000;
  padding: 16px 20px 0;
}

.navbar-container {
  display: flex;
  align-items: center;
  gap: var(--space-5);
  width: min(100%, var(--layout-wide-max));
  min-height: var(--layout-header-height);
  margin: 0 auto;
  padding: 12px 18px;
  border: 1px solid rgba(255, 255, 255, 0.72);
  border-radius: 24px;
  background: rgba(248, 251, 255, 0.82);
  box-shadow: var(--shadow-soft);
  backdrop-filter: blur(18px);
}

.navbar--student .navbar-container {
  background: rgba(255, 255, 255, 0.9);
}

.navbar-brand {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 0;
  border: 0;
  background: transparent;
  cursor: pointer;
  text-align: left;
}

.navbar-brand__mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: linear-gradient(135deg, var(--color-primary-deep) 0%, var(--color-primary) 100%);
  color: var(--color-text-on-dark);
  font-size: 15px;
  font-weight: 800;
  letter-spacing: 0.08em;
}

.navbar-brand__copy {
  display: inline-flex;
  flex-direction: column;
  gap: 4px;
}

.navbar-brand__copy strong {
  color: var(--color-text-main);
  font-size: 18px;
}

.navbar-brand__copy small {
  color: var(--color-text-tertiary);
  font-size: 12px;
}

.navbar-menu {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.menu-item {
  padding: 10px 14px;
  border: 0;
  border-radius: 999px;
  background: transparent;
  color: var(--color-text-secondary);
  cursor: pointer;
  font-weight: 600;
  transition: background-color var(--transition-base), color var(--transition-base);
}

.menu-item:hover,
.menu-item.active {
  color: var(--color-primary-deep);
  background: rgba(17, 47, 87, 0.08);
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  margin-left: auto;
}

.navbar-user {
  display: inline-flex;
  flex-direction: column;
  gap: 4px;
  padding-right: 12px;
  border-right: 1px solid rgba(194, 206, 222, 0.8);
  text-align: right;
}

.navbar-user__role {
  color: var(--color-text-tertiary);
  font-size: 12px;
}

.navbar-user__name {
  color: var(--color-text-main);
  font-size: 14px;
  font-weight: 700;
}

@media (max-width: 980px) {
  .navbar {
    padding: 12px 12px 0;
  }

  .navbar-container {
    flex-wrap: wrap;
    gap: var(--space-4);
    padding: 14px;
  }

  .navbar-menu {
    order: 3;
    width: 100%;
    overflow-x: auto;
    padding-bottom: 4px;
  }

  .navbar-right {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 640px) {
  .navbar-user {
    display: none;
  }
}
</style>
