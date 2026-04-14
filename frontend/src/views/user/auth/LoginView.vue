<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="login-page__bg">
      <div class="login-page__bg-shape login-page__bg-shape--1"></div>
      <div class="login-page__bg-shape login-page__bg-shape--2"></div>
      <div class="login-page__bg-shape login-page__bg-shape--3"></div>
    </div>

    <div class="login-page__container">
      <!-- 左侧品牌区域 -->
      <div class="login-page__brand">
        <div class="login-page__brand-content">
          <div class="login-page__logo">
            <span class="login-page__logo-icon">E</span>
            <span class="login-page__logo-text">EduHub</span>
          </div>
          <h1 class="login-page__title">教学协同平台</h1>
          <p class="login-page__subtitle">
            连接师生，赋能教学<br />
            让学习更高效，让教育更精彩
          </p>
          <div class="login-page__features">
            <div class="login-page__feature">
              <div class="login-page__feature-icon">
                <BookOutlined />
              </div>
              <div class="login-page__feature-text">
                <h4>智能课程管理</h4>
                <p>一站式课程资源管理</p>
              </div>
            </div>
            <div class="login-page__feature">
              <div class="login-page__feature-icon">
                <ExperimentOutlined />
              </div>
              <div class="login-page__feature-text">
                <h4>实验实训系统</h4>
                <p>沉浸式实践教学体验</p>
              </div>
            </div>
            <div class="login-page__feature">
              <div class="login-page__feature-icon">
                <TrophyOutlined />
              </div>
              <div class="login-page__feature-text">
                <h4>考试评测中心</h4>
                <p>全面准确的学业评估</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录区域 -->
      <div class="login-page__form-area">
        <div class="login-page__form-card">
          <div class="login-page__form-header">
            <h2>欢迎登录</h2>
            <p>请选择您的身份并输入账号密码</p>
          </div>

          <!-- 角色选择 -->
          <div class="login-page__role-selector">
            <div
              class="login-page__role-card"
              :class="{ 'login-page__role-card--active': selectedRole === 'student' }"
              @click="selectedRole = 'student'"
            >
              <div class="login-page__role-icon login-page__role-icon--student">
                <UserOutlined />
              </div>
              <div class="login-page__role-info">
                <h4>学生登录</h4>
                <p>学习、作业、考试</p>
              </div>
              <div class="login-page__role-check" v-if="selectedRole === 'student'">
                <CheckOutlined />
              </div>
            </div>
            <div
              class="login-page__role-card"
              :class="{ 'login-page__role-card--active': selectedRole === 'teacher' }"
              @click="selectedRole = 'teacher'"
            >
              <div class="login-page__role-icon login-page__role-icon--teacher">
                <SolutionOutlined />
              </div>
              <div class="login-page__role-info">
                <h4>教师登录</h4>
                <p>管理、教学、评测</p>
              </div>
              <div class="login-page__role-check" v-if="selectedRole === 'teacher'">
                <CheckOutlined />
              </div>
            </div>
          </div>

          <!-- 登录表单 -->
          <a-form
            :model="formState"
            class="login-page__form"
            @finish="handleSubmit"
          >
            <a-form-item
              name="account"
              :rules="[{ required: true, message: '请输入账号' }]"
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
              :rules="[{ required: true, message: '请输入密码' }]"
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

            <div class="login-page__form-options">
              <a-checkbox v-model:checked="rememberMe">记住账号</a-checkbox>
              <a class="login-page__forgot">忘记密码?</a>
            </div>

            <a-form-item>
              <a-button
                type="primary"
                html-type="submit"
                size="large"
                block
                :loading="loading"
                class="login-page__submit-btn"
              >
                <template #icon>
                  <LoginOutlined />
                </template>
                {{ selectedRole === 'student' ? '学生登录' : '教师登录' }}
              </a-button>
            </a-form-item>
          </a-form>

          <!-- 演示账号提示 -->
          <div class="login-page__demo-hint">
            <a-divider>
              <span class="login-page__divider-text">演示账号</span>
            </a-divider>
            <div class="login-page__demo-accounts">
              <div class="login-page__demo-account" @click="fillDemoAccount('student')">
                <span class="login-page__demo-label">学生</span>
                <span class="login-page__demo-value">student / 123456</span>
              </div>
              <div class="login-page__demo-account" @click="fillDemoAccount('teacher')">
                <span class="login-page__demo-label">教师</span>
                <span class="login-page__demo-value">teacher / 123456</span>
              </div>
            </div>
          </div>
        </div>

        <p class="login-page__footer">
          EduHub 教学协同平台 &copy; 2024
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  LockOutlined,
  LoginOutlined,
  CheckOutlined,
  SolutionOutlined,
  BookOutlined,
  ExperimentOutlined,
  TrophyOutlined
} from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/user/auth'

type RoleType = 'student' | 'teacher'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const selectedRole = ref<RoleType>('student')
const rememberMe = ref(false)
const loading = ref(false)

const formState = reactive({
  account: '',
  password: ''
})

const fillDemoAccount = (role: RoleType): void => {
  selectedRole.value = role
  if (role === 'student') {
    formState.account = 'student'
    formState.password = '123456'
  } else {
    formState.account = 'teacher'
    formState.password = '123456'
  }
}

const handleSubmit = async (): Promise<void> => {
  if (!selectedRole.value) {
    message.warning('请先选择登录身份')
    return
  }

  loading.value = true
  try {
    const session = await authStore.login({
      account: formState.account,
      password: formState.password
    })

    message.success(`欢迎回来，${session.name}`)

    const redirect = route.query.redirect as string
    if (redirect) {
      router.push(redirect)
    } else if (selectedRole.value === 'teacher' || session.role === 'teacher') {
      router.push('/admin/dashboard')
    } else {
      router.push('/learning')
    }
  } catch (error) {
    const err = error as Error
    message.error(err.message || '登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8fbff 0%, #eef3f8 50%, #e8f0f8 100%);
  position: relative;
  overflow: hidden;
  padding: 24px;
}

.login-page__bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.login-page__bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.login-page__bg-shape--1 {
  top: -15%;
  right: -10%;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(31, 95, 174, 0.15) 0%, transparent 70%);
}

.login-page__bg-shape--2 {
  bottom: -20%;
  left: -15%;
  width: 700px;
  height: 700px;
  background: radial-gradient(circle, rgba(216, 165, 69, 0.12) 0%, transparent 70%);
}

.login-page__bg-shape--3 {
  top: 40%;
  left: 30%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(47, 143, 78, 0.08) 0%, transparent 70%);
}

.login-page__container {
  position: relative;
  z-index: 1;
  display: flex;
  width: 100%;
  max-width: 1100px;
  min-height: 680px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  border-radius: 32px;
  box-shadow: 
    0 32px 64px rgba(17, 47, 87, 0.08),
    0 16px 32px rgba(17, 47, 87, 0.04),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  overflow: hidden;
}

.login-page__brand {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background: linear-gradient(135deg, #1f5fae 0%, #174a89 50%, #112f57 100%);
  position: relative;
  overflow: hidden;
}

.login-page__brand::before {
  content: '';
  position: absolute;
  inset: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  opacity: 0.6;
}

.login-page__brand-content {
  position: relative;
  z-index: 1;
  color: #fff;
  max-width: 380px;
}

.login-page__logo {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 32px;
}

.login-page__logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8px);
  border-radius: 16px;
  font-size: 28px;
  font-weight: 800;
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.login-page__logo-text {
  font-size: 32px;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.login-page__title {
  font-size: 40px;
  font-weight: 800;
  margin: 0 0 16px;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.login-page__subtitle {
  font-size: 18px;
  line-height: 1.8;
  opacity: 0.9;
  margin: 0 0 48px;
}

.login-page__features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.login-page__feature {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(8px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition: all 0.3s ease;
}

.login-page__feature:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(8px);
}

.login-page__feature-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  font-size: 20px;
}

.login-page__feature-text h4 {
  margin: 0 0 4px;
  font-size: 15px;
  font-weight: 700;
}

.login-page__feature-text p {
  margin: 0;
  font-size: 13px;
  opacity: 0.8;
}

.login-page__form-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 48px;
}

.login-page__form-card {
  max-width: 400px;
  margin: 0 auto;
  width: 100%;
}

.login-page__form-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-page__form-header h2 {
  font-size: 28px;
  font-weight: 800;
  color: var(--color-text-main);
  margin: 0 0 8px;
}

.login-page__form-header p {
  font-size: 15px;
  color: var(--color-text-secondary);
  margin: 0;
}

.login-page__role-selector {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 32px;
}

.login-page__role-card {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 16px;
  background: #fff;
  border: 2px solid var(--color-border);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.login-page__role-card:hover {
  border-color: var(--color-primary);
  box-shadow: 0 8px 24px rgba(31, 95, 174, 0.1);
}

.login-page__role-card--active {
  border-color: var(--color-primary);
  background: linear-gradient(180deg, rgba(31, 95, 174, 0.04) 0%, rgba(31, 95, 174, 0.08) 100%);
  box-shadow: 0 8px 24px rgba(31, 95, 174, 0.15);
}

.login-page__role-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 14px;
  font-size: 24px;
  margin-bottom: 12px;
}

.login-page__role-icon--student {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #fff;
}

.login-page__role-icon--teacher {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: #fff;
}

.login-page__role-info {
  text-align: center;
}

.login-page__role-info h4 {
  margin: 0 0 4px;
  font-size: 15px;
  font-weight: 700;
  color: var(--color-text-main);
}

.login-page__role-info p {
  margin: 0;
  font-size: 12px;
  color: var(--color-text-tertiary);
}

.login-page__role-check {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-primary);
  color: #fff;
  border-radius: 50%;
  font-size: 12px;
}

.login-page__form :deep(.ant-input-affix-wrapper) {
  padding: 12px 16px;
  border-radius: 12px;
  border-color: var(--color-border);
}

.login-page__form :deep(.ant-input-affix-wrapper:hover),
.login-page__form :deep(.ant-input-affix-wrapper-focused) {
  border-color: var(--color-primary);
}

.login-page__form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.login-page__forgot {
  color: var(--color-primary);
  font-size: 14px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.login-page__forgot:hover {
  opacity: 0.8;
}

.login-page__submit-btn {
  height: 52px;
  font-size: 16px;
  font-weight: 700;
  border-radius: 14px;
  background: linear-gradient(135deg, #1f5fae 0%, #174a89 100%);
  border: none;
  box-shadow: 0 8px 24px rgba(31, 95, 174, 0.25);
  transition: all 0.3s ease;
}

.login-page__submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(31, 95, 174, 0.35);
}

.login-page__demo-hint {
  margin-top: 32px;
}

.login-page__demo-hint :deep(.ant-divider) {
  margin: 16px 0;
  border-color: var(--color-border);
}

.login-page__divider-text {
  color: var(--color-text-tertiary);
  font-size: 12px;
}

.login-page__demo-accounts {
  display: flex;
  gap: 12px;
}

.login-page__demo-account {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 12px;
  background: var(--color-bg-muted);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.login-page__demo-account:hover {
  background: var(--color-bg-spotlight);
  transform: scale(1.02);
}

.login-page__demo-label {
  font-size: 11px;
  font-weight: 600;
  color: var(--color-text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.login-page__demo-value {
  font-size: 13px;
  color: var(--color-text-main);
  font-family: monospace;
}

.login-page__footer {
  text-align: center;
  margin-top: 32px;
  font-size: 13px;
  color: var(--color-text-tertiary);
}

@media (max-width: 900px) {
  .login-page__container {
    flex-direction: column;
    max-width: 480px;
    min-height: auto;
  }

  .login-page__brand {
    padding: 40px 32px;
  }

  .login-page__brand-content {
    max-width: 100%;
  }

  .login-page__title {
    font-size: 28px;
  }

  .login-page__subtitle {
    font-size: 15px;
    margin-bottom: 32px;
  }

  .login-page__features {
    display: none;
  }

  .login-page__form-area {
    padding: 32px 24px;
  }
}

@media (max-width: 480px) {
  .login-page {
    padding: 16px;
  }

  .login-page__container {
    border-radius: 24px;
  }

  .login-page__brand {
    padding: 32px 24px;
  }

  .login-page__logo-icon {
    width: 44px;
    height: 44px;
    font-size: 22px;
  }

  .login-page__logo-text {
    font-size: 24px;
  }

  .login-page__title {
    font-size: 24px;
  }

  .login-page__role-selector {
    gap: 12px;
  }

  .login-page__role-card {
    padding: 16px 12px;
  }

  .login-page__role-icon {
    width: 44px;
    height: 44px;
    font-size: 20px;
  }
}
</style>
