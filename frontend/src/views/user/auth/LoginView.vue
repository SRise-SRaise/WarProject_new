<template>
  <div class="auth-page app-page-shell app-page-shell--wide">
    <div class="auth-page__grid">
      <AuthShowcasePanel
        eyebrow="身份入口"
        title="让学生学习流和教师管理流在同一套前端里自然分流。"
        description="登录优先尝试生成的 auth API；如果后端接口尚未就绪，页面会自动回退到本地体验账号与已注册账号。"
        :highlights="showcaseHighlights"
      >
        <template #footer>
          <div class="sample-account">
            <p class="sample-account__title">直接体验</p>
            <div class="sample-account__list">
              <button
                v-for="item in sampleLoginAccounts"
                :key="item.account"
                type="button"
                class="sample-account__item"
                @click="applySampleAccount(item.account, item.password)"
              >
                <strong>{{ item.label }}</strong>
                <span>{{ item.account }} / {{ item.password }}</span>
              </button>
            </div>
          </div>
        </template>
      </AuthShowcasePanel>

      <section class="auth-panel app-surface-card">
        <span class="app-pill">统一登录</span>
        <h1 class="auth-panel__title">登录到你的学习空间</h1>
        <p class="auth-panel__description">系统会根据当前身份自动进入学生学习概览或教师工作台。</p>

        <a-alert
          type="info"
          show-icon
          class="auth-panel__alert"
          message="优先走生成 auth API，接口不可用时回退到本地体验数据。"
        />

        <a-form :model="formState" layout="vertical" @finish="handleSubmit">
          <a-form-item label="账号" name="account" :rules="[{ required: true, message: '请输入账号' }]">
            <a-input v-model:value="formState.account" size="large" placeholder="请输入账号">
              <template #prefix>
                <UserOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item label="密码" name="password" :rules="[{ required: true, message: '请输入密码' }]">
            <a-input-password v-model:value="formState.password" size="large" placeholder="请输入密码">
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>

          <div class="auth-panel__extra">
            <span>支持学生和教师统一登录</span>
            <a-button type="link" @click="router.push('/user/register')">还没有账号？立即注册</a-button>
          </div>

          <a-button type="primary" html-type="submit" size="large" block :loading="loading">
            登录并自动分流
          </a-button>
        </a-form>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import { LockOutlined, UserOutlined } from '@ant-design/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import AuthShowcasePanel from '@/components/user/AuthShowcasePanel.vue'
import { sampleLoginAccounts } from '@/stores/user/fixtures'
import { useAuthStore } from '@/stores/user/auth'
import type { UserRole } from '@/stores/user/types'

interface LoginFormState {
  account: string
  password: string
}

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const { loading } = storeToRefs(authStore)

const showcaseHighlights = [
  {
    title: '学生入口优先服务学习闭环',
    detail: '登录后直达学习概览、资料和个人中心，不再使用模板式首页。'
  },
  {
    title: '教师入口聚焦组织管理',
    detail: '教师工作台、用户管理、班级管理与个人设置均由后台壳层统一承载。'
  },
  {
    title: '后续流程模块可继续接入',
    detail: '当前路由与布局基础已经为作业、实验和考试模块预留了稳定扩展位。'
  }
]

const formState = reactive<LoginFormState>({
  account: typeof route.query.account === 'string' ? route.query.account : '2024001',
  password: 'learn2024'
})

function applySampleAccount(account: string, password: string): void {
  formState.account = account
  formState.password = password
}

function getErrorMessage(error: unknown): string {
  return error instanceof Error ? error.message : '登录失败，请稍后重试。'
}

function resolveRedirect(role: UserRole, redirect: string | undefined): string {
  if (!redirect) {
    return role === 'teacher' ? '/admin/dashboard' : '/learning'
  }

  if (role === 'teacher') {
    return redirect.startsWith('/admin') ? redirect : '/admin/dashboard'
  }

  return redirect.startsWith('/admin') ? '/learning' : redirect
}

async function handleSubmit(): Promise<void> {
  try {
    const session = await authStore.login({
      account: formState.account,
      password: formState.password
    })
    message.success(`${session.name}，欢迎回来`)
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : undefined
    router.push(resolveRedirect(session.role, redirect))
  } catch (error) {
    message.error(getErrorMessage(error))
  }
}
</script>

<style scoped>
.auth-page__grid {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(380px, 0.95fr);
  gap: var(--space-5);
  align-items: stretch;
}

.auth-panel {
  padding: 32px;
}

.auth-panel__title {
  margin: 20px 0 12px;
  color: var(--color-text-main);
  font-family: Georgia, 'Times New Roman', 'Songti SC', serif;
  font-size: clamp(28px, 3vw, 38px);
  line-height: 1.15;
}

.auth-panel__description {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.8;
}

.auth-panel__alert {
  margin: 24px 0;
}

.auth-panel__extra {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  color: var(--color-text-tertiary);
  font-size: 13px;
}

.sample-account__title {
  margin: 0 0 12px;
  color: rgba(247, 251, 255, 0.86);
  font-size: 13px;
  font-weight: 700;
}

.sample-account__list {
  display: grid;
  gap: 10px;
}

.sample-account__item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 100%;
  padding: 14px 16px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.08);
  color: inherit;
  cursor: pointer;
  text-align: left;
}

.sample-account__item span {
  color: rgba(247, 251, 255, 0.72);
  font-size: 12px;
}

@media (max-width: 980px) {
  .auth-page__grid {
    grid-template-columns: 1fr;
  }

  .auth-panel {
    padding: 24px;
  }

  .auth-panel__extra {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
  }
}
</style>
