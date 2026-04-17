<template>
  <div class="profile-page">
    <a-card :bordered="false" class="profile-card profile-card--hero">
      <div class="profile-hero">
        <div class="profile-hero__left">
          <div class="profile-avatar">{{ session?.avatar || '学' }}</div>
          <div class="profile-hero__info">
            <h1>{{ session?.name || '学生用户' }}</h1>
            <div class="profile-tags">
              <a-tag color="cyan">{{ session?.major ? `${session.major}专业` : '未设置专业' }}</a-tag>
            </div>
          </div>
        </div>
        <a-button type="primary" size="large" @click="saveProfile">保存个人信息</a-button>
      </div>
      <section class="profile-section">
        <h3 class="profile-section__title">当前登录学生信息</h3>
        <div class="login-info-grid">
          <div class="login-info-item">
            <span>用户ID</span>
            <strong>{{ session?.id || '--' }}</strong>
          </div>
          <div class="login-info-item">
            <span>学号</span>
            <strong>{{ session?.account || '--' }}</strong>
          </div>
          <div class="login-info-item">
            <span>姓名</span>
            <strong>{{ session?.name || '--' }}</strong>
          </div>
          <div class="login-info-item">
            <span>角色</span>
            <strong>{{ session?.major ? `${session.major}专业学生` : '学生' }}</strong>
          </div>
          <div class="login-info-item">
            <span>班级</span>
            <strong>{{ session?.classCode || '未分配班级' }}</strong>
          </div>
          <div class="login-info-item">
            <span>最后登录IP</span>
            <strong>{{ session?.lastLoginIp || '--' }}</strong>
          </div>
        </div>
      </section>

      <section class="profile-section">
        <h3 class="profile-section__title">账号与安全</h3>
        <a-descriptions :column="1" size="small">
          <a-descriptions-item label="账号">{{ session?.account || '--' }}</a-descriptions-item>
          <a-descriptions-item label="身份">{{ session?.title || '学生' }}</a-descriptions-item>
          <a-descriptions-item label="最近登录">{{ session?.lastLogin || '--' }}</a-descriptions-item>
        </a-descriptions>
        <div class="profile-security-tip">
          密码可在个人中心自行修改，修改成功后会自动退出，请使用新密码重新登录。
        </div>
        <a-button type="primary" ghost class="profile-password-btn" @click="openPasswordModal">
          修改密码
        </a-button>
      </section>
    </a-card>
    <a-modal
      v-model:open="passwordModalVisible"
      title="修改密码"
      :confirm-loading="passwordSubmitting"
      ok-text="确认修改"
      cancel-text="取消"
      @ok="submitPasswordChange"
      @cancel="closePasswordModal"
    >
      <a-form layout="vertical">
        <a-form-item label="旧密码" required>
          <a-input-password v-model:value="passwordForm.oldPassword" placeholder="请输入当前密码" />
        </a-form-item>
        <a-form-item label="新密码" required>
          <a-input-password v-model:value="passwordForm.newPassword" placeholder="请输入新密码（至少 8 位）" />
        </a-form-item>
        <a-form-item label="确认新密码" required>
          <a-input-password v-model:value="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { changePassword } from '@/api/authController'
import { useAppStore } from '@/stores/common/app'
import { useAuthStore } from '@/stores/user/auth'
import type { NotificationPreference } from '@/stores/user/types'

interface ProfileFormState {
  email: string
  phone: string
  location: string
  signature: string
  focusAreasText: string
}

interface PasswordFormState {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()
const { studentOverview } = storeToRefs(appStore)
const session = computed(() => authStore.session)

const formState = reactive<ProfileFormState>({
  email: '',
  phone: '',
  location: '',
  signature: '',
  focusAreasText: ''
})
const preferences = ref<NotificationPreference[]>([])
const passwordModalVisible = ref(false)
const passwordSubmitting = ref(false)
const passwordForm = reactive<PasswordFormState>({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

watch(
  () => authStore.session,
  (nextSession) => {
    if (!nextSession) {
      return
    }
    formState.email = nextSession.email
    formState.phone = nextSession.phone
    formState.location = nextSession.location
    formState.signature = nextSession.signature
    formState.focusAreasText = nextSession.focusAreas.join(' / ')
    preferences.value = nextSession.preferences.map((item) => ({ ...item }))
  },
  { immediate: true }
)

function parseFocusAreas(value: string): string[] {
  return value
    .split('/')
    .map((item) => item.trim())
    .filter((item) => item.length > 0)
}

function saveProfile(): void {
  authStore.updateProfile({
    email: formState.email,
    phone: formState.phone,
    location: formState.location,
    signature: formState.signature,
    focusAreas: parseFocusAreas(formState.focusAreasText),
    preferences: preferences.value.map((item) => ({ ...item }))
  })
  message.success('学生个人设置已保存。')
}

function resetPasswordForm(): void {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

function openPasswordModal(): void {
  resetPasswordForm()
  passwordModalVisible.value = true
}

function closePasswordModal(): void {
  passwordModalVisible.value = false
}

async function submitPasswordChange(): Promise<void> {
  if (!passwordForm.oldPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
    message.warning('请完整填写密码信息')
    return
  }
  if (passwordForm.newPassword.length < 8) {
    message.warning('新密码长度不能小于 8 位')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    message.warning('两次输入的新密码不一致')
    return
  }
  passwordSubmitting.value = true
  try {
    const response = await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
      confirmPassword: passwordForm.confirmPassword
    })
    if (response.data?.code !== 0 || !response.data?.data) {
      throw new Error(response.data?.message || '修改密码失败')
    }
    message.success('密码修改成功，请重新登录')
    closePasswordModal()
    authStore.logout()
    await router.replace('/user/login')
  } catch (error) {
    const err = error as Error
    message.error(err.message || '修改密码失败')
  } finally {
    passwordSubmitting.value = false
  }
}

onMounted(async () => {
  await authStore.refreshSessionFromServer()
  await appStore.ensureReady()
})
</script>

<style scoped>
.profile-page { display: grid; }

.profile-card {
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(18, 38, 63, 0.06);
}

.profile-card--hero {
  background: linear-gradient(135deg, #f6faff 0%, #ffffff 100%);
}

.profile-section {
  margin-top: 16px;
  padding: 16px;
  border-radius: 12px;
  background: #fbfdff;
  border: 1px solid #e9f0f7;
}

.profile-section__title {
  margin: 0 0 14px;
  font-size: 16px;
  font-weight: 700;
  color: #203040;
}

.profile-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.profile-hero__left {
  display: flex;
  align-items: center;
  gap: 18px;
}

.profile-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  border-radius: 999px;
  background: linear-gradient(135deg, #1f5fae 0%, #4a7fc4 100%);
  color: #fff;
  font-size: 28px;
  font-weight: 800;
}

.profile-hero__info h1 {
  margin: 0 0 6px;
  font-size: 28px;
  font-weight: 700;
}

.profile-hero__info p {
  margin: 0;
  color: #5b6b7a;
}

.profile-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.profile-security-tip {
  margin-top: 12px;
  padding: 10px 12px;
  border-radius: 10px;
  background: #f6f9fc;
  color: #5b6b7a;
  font-size: 13px;
}

.profile-password-btn {
  margin-top: 12px;
}

.login-info-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.login-info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 10px 12px;
  border-radius: 10px;
  background: #f8fbff;
  border: 1px solid #e7eef6;
}

.login-info-item span {
  font-size: 12px;
  color: #6f8191;
}

.login-info-item strong {
  font-size: 14px;
  color: #203040;
  word-break: break-all;
}

.overview-list {
  display: grid;
  gap: 10px;
}

.overview-item {
  padding: 10px 12px;
  border-radius: 10px;
  background: #f8fbff;
  border: 1px solid #e7eef6;
}

.overview-item__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}

.overview-item__head span {
  color: #5b6b7a;
  font-size: 13px;
}

.overview-item__head strong {
  color: #1f5fae;
  font-size: 16px;
}

.overview-item p {
  margin: 0;
  color: #7b8b99;
  font-size: 12px;
}

.profile-preferences {
  display: grid;
  gap: 12px;
}

.profile-preferences__item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f8fbff;
  border: 1px solid #e7eef6;
}

.profile-preferences__item h3 {
  margin: 0 0 6px;
  font-size: 16px;
}

.profile-preferences__item p {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 13px;
}

@media (max-width: 720px) {
  .login-info-grid {
    grid-template-columns: 1fr;
  }

  .profile-hero,
  .profile-hero__left,
  .profile-preferences__item {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
