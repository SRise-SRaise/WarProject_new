<template>
  <div class="login-page">
    <!-- 动态背景 -->
    <div class="login-page__bg">
      <div class="bg-orb bg-orb--1"></div>
      <div class="bg-orb bg-orb--2"></div>
      <div class="bg-orb bg-orb--3"></div>
      <div class="bg-grid"></div>
    </div>

    <div class="login-page__container">
      <!-- 左侧视觉区域 - 简化设计 -->
      <div class="login-page__visual">
        <div class="visual-content">
          <!-- 3D风格图形装饰 -->
          <div class="visual-shapes">
            <div class="shape shape--book">
              <BookOutlined />
            </div>
            <div class="shape shape--bulb">
              <BulbOutlined />
            </div>
            <div class="shape shape--trophy">
              <TrophyOutlined />
            </div>
          </div>
          
          <!-- 简洁标语 -->
          <div class="visual-text">
            <h1 class="visual-title">EduHub</h1>
            <p class="visual-slogan">智慧教学，无限可能</p>
          </div>
        </div>
      </div>

      <!-- 右侧登录区域 -->
      <div class="login-page__form-area">
        <div class="login-page__form-wrapper">
          <!-- Logo和欢迎语 -->
          <div class="form-header">
            <div class="form-logo">
              <span class="logo-icon">E</span>
            </div>
            <h2 class="form-title">欢迎回来</h2>
            <p class="form-subtitle">选择身份登录您的账户</p>
          </div>

          <!-- 角色切换 - 简洁标签式 -->
          <div class="role-tabs">
            <button
              class="role-tab"
              :class="{ 'role-tab--active': selectedRole === 'student' }"
              @click="selectedRole = 'student'"
            >
              <UserOutlined />
              <span>学生</span>
            </button>
            <button
              class="role-tab"
              :class="{ 'role-tab--active': selectedRole === 'teacher' }"
              @click="selectedRole = 'teacher'"
            >
              <SolutionOutlined />
              <span>教师</span>
            </button>
          </div>

          <!-- 登录表单 -->
          <a-form
            :model="formState"
            class="login-form"
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
                class="form-input"
              >
                <template #prefix>
                  <UserOutlined class="input-icon" />
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
                class="form-input"
              >
                <template #prefix>
                  <LockOutlined class="input-icon" />
                </template>
              </a-input-password>
            </a-form-item>

            <div class="form-options">
              <a-checkbox v-model:checked="rememberMe" class="remember-check">记住账号</a-checkbox>
              <a class="forgot-link">忘记密码?</a>
            </div>

            <a-form-item class="submit-item">
              <a-button
                type="primary"
                html-type="submit"
                size="large"
                block
                :loading="loading"
                class="submit-btn"
              >
                登录
                <ArrowRightOutlined />
              </a-button>
            </a-form-item>
          </a-form>

          <!-- 底部信息 -->
          <p class="form-footer">
            EduHub 教学协同平台
          </p>
        </div>
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
  ArrowRightOutlined,
  SolutionOutlined,
  BookOutlined,
  BulbOutlined,
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

const handleSubmit = async (): Promise<void> => {
  if (!selectedRole.value) {
    message.warning('请先选择登录身份')
    return
  }

  loading.value = true
  try {
    const session = await authStore.login({
      account: formState.account,
      password: formState.password,
      role: selectedRole.value
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
  background: #0a0a0f;
  position: relative;
  overflow: hidden;
  padding: 20px;
}

/* 动态背景 */
.login-page__bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
  opacity: 0.6;
  animation: float 20s ease-in-out infinite;
}

.bg-orb--1 {
  top: -20%;
  left: -10%;
  width: 600px;
  height: 600px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  animation-delay: 0s;
}

.bg-orb--2 {
  bottom: -30%;
  right: -15%;
  width: 700px;
  height: 700px;
  background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%);
  animation-delay: -7s;
}

.bg-orb--3 {
  top: 50%;
  left: 50%;
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  transform: translate(-50%, -50%);
  animation-delay: -14s;
}

.bg-grid {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(rgba(255,255,255,0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.02) 1px, transparent 1px);
  background-size: 60px 60px;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.05); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}

/* 主容器 */
.login-page__container {
  position: relative;
  z-index: 1;
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
  background: rgba(18, 18, 24, 0.8);
  backdrop-filter: blur(40px);
  border-radius: 28px;
  box-shadow: 
    0 0 0 1px rgba(255, 255, 255, 0.08),
    0 40px 80px rgba(0, 0, 0, 0.5);
  overflow: hidden;
}

/* 左侧视觉区域 */
.login-page__visual {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background: linear-gradient(160deg, rgba(59, 130, 246, 0.15) 0%, rgba(139, 92, 246, 0.1) 100%);
  border-right: 1px solid rgba(255, 255, 255, 0.06);
  position: relative;
}

.visual-content {
  text-align: center;
  position: relative;
  z-index: 1;
}

/* 3D风格图形装饰 */
.visual-shapes {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-bottom: 48px;
}

.shape {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  font-size: 32px;
  color: #fff;
  position: relative;
  animation: bounce 3s ease-in-out infinite;
}

.shape::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: inherit;
  filter: blur(20px);
  opacity: 0.5;
  z-index: -1;
}

.shape--book {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  animation-delay: 0s;
}

.shape--bulb {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  animation-delay: -1s;
  transform: translateY(-20px);
}

.shape--trophy {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  animation-delay: -2s;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-12px); }
}

.shape--bulb {
  animation-name: bounce-alt;
}

@keyframes bounce-alt {
  0%, 100% { transform: translateY(-20px); }
  50% { transform: translateY(-32px); }
}

.visual-title {
  font-size: 48px;
  font-weight: 800;
  background: linear-gradient(135deg, #fff 0%, rgba(255,255,255,0.7) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 12px;
  letter-spacing: -0.02em;
}

.visual-slogan {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
  letter-spacing: 0.1em;
}

/* 右侧表单区域 */
.login-page__form-area {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
}

.login-page__form-wrapper {
  width: 100%;
  max-width: 360px;
}

.form-header {
  text-align: center;
  margin-bottom: 36px;
}

.form-logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  border-radius: 16px;
  margin-bottom: 20px;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.3);
}

.logo-icon {
  font-size: 28px;
  font-weight: 800;
  color: #fff;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 8px;
}

.form-subtitle {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

/* 角色标签 */
.role-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 28px;
  padding: 4px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 14px;
}

.role-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 20px;
  background: transparent;
  border: none;
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.5);
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s ease;
}

.role-tab:hover {
  color: rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.05);
}

.role-tab--active {
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  color: #fff;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.role-tab--active:hover {
  color: #fff;
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
}

/* 表单样式 */
.login-form :deep(.ant-form-item) {
  margin-bottom: 20px;
}

.login-form :deep(.ant-input-affix-wrapper) {
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  transition: all 0.25s ease;
}

.login-form :deep(.ant-input-affix-wrapper:hover),
.login-form :deep(.ant-input-affix-wrapper-focused) {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(59, 130, 246, 0.5);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

.login-form :deep(.ant-input) {
  background: transparent;
  color: #fff;
  font-size: 15px;
}

.login-form :deep(.ant-input::placeholder) {
  color: rgba(255, 255, 255, 0.35);
}

.input-icon {
  color: rgba(255, 255, 255, 0.4);
  font-size: 16px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.remember-check :deep(.ant-checkbox-wrapper) {
  color: rgba(255, 255, 255, 0.5);
}

.remember-check :deep(.ant-checkbox-inner) {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
}

.forgot-link {
  color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
  cursor: pointer;
  transition: color 0.2s;
}

.forgot-link:hover {
  color: #3b82f6;
}

.submit-item {
  margin-bottom: 0;
}

.submit-btn {
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.3);
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(59, 130, 246, 0.4);
}

.form-footer {
  text-align: center;
  margin-top: 32px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.3);
}

/* 响应式 */
@media (max-width: 800px) {
  .login-page__container {
    flex-direction: column;
    max-width: 440px;
    min-height: auto;
  }

  .login-page__visual {
    padding: 40px 32px;
    border-right: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  }

  .visual-shapes {
    gap: 16px;
    margin-bottom: 32px;
  }

  .shape {
    width: 60px;
    height: 60px;
    font-size: 24px;
    border-radius: 16px;
  }

  .visual-title {
    font-size: 36px;
  }

  .visual-slogan {
    font-size: 15px;
  }

  .login-page__form-area {
    padding: 32px 24px;
  }
}

@media (max-width: 480px) {
  .login-page {
    padding: 12px;
  }

  .login-page__container {
    border-radius: 20px;
  }

  .login-page__visual {
    padding: 32px 24px;
  }

  .shape {
    width: 52px;
    height: 52px;
    font-size: 20px;
  }

  .visual-title {
    font-size: 28px;
  }

  .form-title {
    font-size: 24px;
  }

  .role-tabs {
    gap: 8px;
  }

  .role-tab {
    padding: 12px 16px;
    font-size: 14px;
  }
}
</style>
