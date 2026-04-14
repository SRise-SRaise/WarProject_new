<template>
  <div class="basic-layout">
    <!-- 登录/注册页不显示顶栏 -->
    <template v-if="shellMode !== 'auth'">
      <header class="top-bar">
        <div class="top-bar__inner">
          <!-- Logo -->
          <button class="brand" type="button" @click="router.push('/learning')">
            <span class="brand__mark">EH</span>
            <div class="brand__copy">
              <strong>EduHub</strong>
              <small>学生学习空间</small>
            </div>
          </button>

          <!-- 导航菜单 -->
          <nav class="nav-menu" aria-label="主导航">
            <button
              v-for="item in studentMenuItems"
              :key="item.key"
              class="nav-item"
              :class="{ 'nav-item--active': isActive(item.path) }"
              type="button"
              @click="router.push(item.path)"
            >
              {{ item.label }}
            </button>
          </nav>

          <!-- 右侧用户区 -->
          <div class="top-bar__right">
            <a-dropdown placement="bottomRight" :trigger="['click']">
              <div class="user-avatar-btn">
                <a-avatar :size="34" class="avatar">
                  <template #icon><UserOutlined /></template>
                </a-avatar>
                <span class="user-name">{{ authStore.session?.name }}</span>
                <DownOutlined class="avatar-arrow" />
              </div>
              <template #overlay>
                <a-menu>
                  <div class="user-dropdown-header">
                    <a-avatar :size="44" class="avatar avatar--lg">
                      <template #icon><UserOutlined /></template>
                    </a-avatar>
                    <div>
                      <p class="user-dropdown-name">{{ authStore.session?.name }}</p>
                      <p class="user-dropdown-role">学生</p>
                    </div>
                  </div>
                  <a-menu-divider />
                  <a-menu-item key="profile" @click="router.push('/profile')">
                    <template #icon><IdcardOutlined /></template>
                    个人中心
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
        </div>
      </header>
    </template>

    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import {
  UserOutlined,
  DownOutlined,
  IdcardOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/user/auth'

interface NavItem {
  key: string
  label: string
  path: string
}

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const shellMode = computed(() => route.meta.shell ?? 'public')

const studentMenuItems: NavItem[] = [
  { key: 'learning', label: '学习概览', path: '/learning' },
  { key: 'materials', label: '资料', path: '/materials' },
  { key: 'homework', label: '作业', path: '/homework' },
  { key: 'experiments', label: '实验', path: '/experiments' },
  { key: 'exams', label: '考试', path: '/exams' }
]

const isActive = (path: string): boolean => {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

const handleLogout = (): void => {
  authStore.logout()
  message.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.basic-layout {
  min-height: 100vh;
  background: #f5f6f8;
}

/* ── 顶部导航栏 ─────────────────────────────── */
.top-bar {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: #fff;
  border-bottom: 1px solid #e8eaed;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.06);
}

.top-bar__inner {
  display: flex;
  align-items: center;
  gap: 0;
  height: 64px;
  padding: 0 24px;
  max-width: 1440px;
  margin: 0 auto;
}

/* ── Logo ─────────────────────────────── */
.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 0;
  border: none;
  background: transparent;
  cursor: pointer;
  text-align: left;
  flex-shrink: 0;
  margin-right: 32px;
}

.brand__mark {
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
}

.brand__copy {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.brand__copy strong {
  font-size: 15px;
  font-weight: 700;
  color: #1a1a2e;
  line-height: 1.2;
}

.brand__copy small {
  font-size: 11px;
  color: #8c929a;
  line-height: 1.2;
}

/* ── 导航菜单 ─────────────────────────────── */
.nav-menu {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.nav-item {
  padding: 6px 14px;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: #595959;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
  white-space: nowrap;
}

.nav-item:hover {
  background: #f0f5ff;
  color: #1677ff;
}

.nav-item--active {
  background: #f0f5ff;
  color: #1677ff;
  font-weight: 600;
}

/* ── 右侧用户区 ─────────────────────────────── */
.top-bar__right {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
  flex-shrink: 0;
}

.user-avatar-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 5px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.15s;
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

/* ── 下拉菜单内部 ─────────────────────────────── */
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
.main-content {
  padding: 24px;
  min-height: calc(100vh - 64px);
}

/* ── 响应式 ─────────────────────────────── */
@media (max-width: 900px) {
  .top-bar__inner {
    padding: 0 16px;
  }

  .brand__copy {
    display: none;
  }

  .brand {
    margin-right: 16px;
  }

  .nav-item {
    padding: 6px 10px;
    font-size: 13px;
  }

  .user-name {
    display: none;
  }
}

@media (max-width: 640px) {
  .nav-menu {
    display: none;
  }

  .main-content {
    padding: 16px;
  }
}
</style>
