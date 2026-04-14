<template>
  <div class="register-page">
    <!-- 背景装饰 -->
    <div class="register-page__bg">
      <div class="register-page__bg-shape register-page__bg-shape--1"></div>
      <div class="register-page__bg-shape register-page__bg-shape--2"></div>
    </div>

    <div class="register-page__container">
      <div class="register-page__card">
        <router-link to="/" class="register-page__back">
          <ArrowLeftOutlined />
          返回登录
        </router-link>

        <div class="register-page__header">
          <div class="register-page__logo">
            <span class="register-page__logo-icon">E</span>
          </div>
          <h2>创建账号</h2>
          <p>注册成为学生用户</p>
        </div>

        <a-form
          :model="formState"
          class="register-page__form"
          layout="vertical"
          @finish="handleSubmit"
        >
          <a-form-item
            name="account"
            label="账号"
            :rules="[{ required: true, message: '请输入账号' }, { min: 4, message: '账号至少4位' }]"
          >
            <a-input
              v-model:value="formState.account"
              size="large"
              placeholder="请输入账号"
            >
              <template #prefix>
                <UserOutlined style="color: #9ca3af" />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item
            name="password"
            label="密码"
            :rules="[{ required: true, message: '请输入密码' }, { min: 6, message: '密码至少6位' }]"
          >
            <a-input-password
              v-model:value="formState.password"
              size="large"
              placeholder="请输入密码"
            >
              <template #prefix>
                <LockOutlined style="color: #9ca3af" />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item
            name="confirmPassword"
            label="确认密码"
            :rules="[
              { required: true, message: '请确认密码' },
              { validator: validateConfirmPassword }
            ]"
          >
            <a-input-password
              v-model:value="formState.confirmPassword"
              size="large"
              placeholder="请再次输入密码"
            >
              <template #prefix>
                <LockOutlined style="color: #9ca3af" />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              size="large"
              block
              :loading="loading"
              class="register-page__submit-btn"
            >
              注册
            </a-button>
          </a-form-item>
        </a-form>

        <p class="register-page__login-hint">
          已有账号？<router-link to="/">立即登录</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  LockOutlined,
  ArrowLeftOutlined
} from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/user/auth'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)

const formState = reactive({
  account: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (_rule: unknown, value: string): Promise<void> => {
  if (value !== formState.password) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

const handleSubmit = async (): Promise<void> => {
  loading.value = true
  try {
    await authStore.register({
      account: formState.account,
      password: formState.password
    })
    message.success('注册成功，请登录')
    router.push('/')
  } catch (error) {
    const err = error as Error
    message.error(err.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8fbff 0%, #eef3f8 50%, #e8f0f8 100%);
  padding: 24px;
  position: relative;
  overflow: hidden;
}

.register-page__bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.register-page__bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.register-page__bg-shape--1 {
  top: -15%;
  right: -10%;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(31, 95, 174, 0.12) 0%, transparent 70%);
}

.register-page__bg-shape--2 {
  bottom: -20%;
  left: -15%;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(216, 165, 69, 0.1) 0%, transparent 70%);
}

.register-page__container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 440px;
}

.register-page__card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 28px;
  padding: 40px;
  box-shadow: 
    0 32px 64px rgba(17, 47, 87, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.register-page__back {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--color-text-secondary);
  font-size: 14px;
  margin-bottom: 24px;
  transition: color 0.2s;
  text-decoration: none;
}

.register-page__back:hover {
  color: var(--color-primary);
}

.register-page__header {
  text-align: center;
  margin-bottom: 32px;
}

.register-page__logo {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.register-page__logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #1f5fae 0%, #174a89 100%);
  border-radius: 16px;
  font-size: 28px;
  font-weight: 800;
  color: #fff;
}

.register-page__header h2 {
  font-size: 26px;
  font-weight: 800;
  color: var(--color-text-main);
  margin: 0 0 8px;
}

.register-page__header p {
  font-size: 15px;
  color: var(--color-text-secondary);
  margin: 0;
}

.register-page__form :deep(.ant-input-affix-wrapper) {
  padding: 12px 16px;
  border-radius: 12px;
  border-color: var(--color-border);
}

.register-page__form :deep(.ant-input-affix-wrapper:hover),
.register-page__form :deep(.ant-input-affix-wrapper-focused) {
  border-color: var(--color-primary);
}

.register-page__form :deep(.ant-form-item-label > label) {
  font-weight: 600;
  color: var(--color-text-main);
}

.register-page__submit-btn {
  height: 52px;
  font-size: 16px;
  font-weight: 700;
  border-radius: 14px;
  background: linear-gradient(135deg, #1f5fae 0%, #174a89 100%);
  border: none;
  margin-top: 8px;
  box-shadow: 0 8px 24px rgba(31, 95, 174, 0.25);
  transition: all 0.3s ease;
}

.register-page__submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(31, 95, 174, 0.35);
}

.register-page__login-hint {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.register-page__login-hint a {
  color: var(--color-primary);
  font-weight: 600;
  text-decoration: none;
}

.register-page__login-hint a:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .register-page {
    padding: 16px;
  }

  .register-page__card {
    padding: 32px 24px;
    border-radius: 24px;
  }

  .register-page__header h2 {
    font-size: 22px;
  }
}
</style>
