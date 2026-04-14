<template>
  <div class="auth-page app-page-shell app-page-shell--wide">
    <div class="auth-page__grid">
      <AuthShowcasePanel
        eyebrow="学生注册"
        title="把注册保留为可开关能力，但仍提供完整、得体的首次进入体验。"
        description="当前首波界面默认将注册账号视为学生账号。后续若业务要求关闭注册入口，路由与页面结构仍可保留为受控能力。"
        :highlights="showcaseHighlights"
      >
        <template #footer>
          <a-alert
            type="warning"
            show-icon
            message="注册成功后默认进入学生身份，可使用登录页中的示例账号切换教师体验。"
          />
        </template>
      </AuthShowcasePanel>

      <section class="auth-panel app-surface-card">
        <span class="app-pill">创建账号</span>
        <h1 class="auth-panel__title">注册你的学习账户</h1>
        <p class="auth-panel__description">填写基础账号信息后即可进入学生学习侧页面，后续可在个人中心补充资料。</p>

        <a-form :model="formState" layout="vertical" @finish="handleSubmit">
          <a-form-item label="账号" name="account" :rules="[{ required: true, message: '请输入账号' }, { min: 4, message: '账号至少 4 位' }]">
            <a-input v-model:value="formState.account" size="large" placeholder="建议使用学号或统一账号">
              <template #prefix>
                <UserOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item label="密码" name="password" :rules="[{ required: true, message: '请输入密码' }, { min: 8, message: '密码至少 8 位' }]">
            <a-input-password v-model:value="formState.password" size="large" placeholder="请设置密码">
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item label="确认密码" name="confirmPassword" :rules="[{ required: true, message: '请再次输入密码' }]">
            <a-input-password v-model:value="formState.confirmPassword" size="large" placeholder="请再次输入密码">
              <template #prefix>
                <CheckCircleOutlined />
              </template>
            </a-input-password>
          </a-form-item>

          <div class="auth-panel__extra">
            <span>注册完成后可直接回到登录页体验分流逻辑</span>
            <a-button type="link" @click="router.push('/user/login')">已有账号？去登录</a-button>
          </div>

          <a-button type="primary" html-type="submit" size="large" block :loading="loading">
            注册并创建学生账号
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
import { CheckCircleOutlined, LockOutlined, UserOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import AuthShowcasePanel from '@/components/user/AuthShowcasePanel.vue'
import { useAuthStore } from '@/stores/user/auth'

interface RegisterFormState {
  account: string
  password: string
  confirmPassword: string
}

const router = useRouter()
const authStore = useAuthStore()
const { loading } = storeToRefs(authStore)

const showcaseHighlights = [
  {
    title: '注册仍服从模块化组织',
    detail: '页面在 user 模块下落位，但继续由 basic.ts 统一承载公共入口层路由。'
  },
  {
    title: '后端接口就绪后可无缝切换',
    detail: '页面已复用生成 auth API，当前缺口由本地 fallback repository 兜底。'
  },
  {
    title: '后续资料与任务入口保持稳定',
    detail: '注册成功后的学生会进入相同学习壳层，无需重新适配导航结构。'
  }
]

const formState = reactive<RegisterFormState>({
  account: '',
  password: '',
  confirmPassword: ''
})

function getErrorMessage(error: unknown): string {
  return error instanceof Error ? error.message : '注册失败，请稍后重试。'
}

async function handleSubmit(): Promise<void> {
  if (formState.password !== formState.confirmPassword) {
    message.error('两次输入的密码不一致。')
    return
  }

  try {
    await authStore.register({
      account: formState.account,
      password: formState.password
    })
    message.success('注册成功，请使用新账号登录。')
    router.push({ name: 'Login', query: { account: formState.account } })
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
  margin: 0 0 24px;
  color: var(--color-text-secondary);
  line-height: 1.8;
}

.auth-panel__extra {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  color: var(--color-text-tertiary);
  font-size: 13px;
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
