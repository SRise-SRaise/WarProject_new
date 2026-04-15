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
          密码修改、班级分配由教师端统一管理，个人中心仅维护基础资料与偏好设置。
        </div>
      </section>

      <section class="profile-section">
        <h3 class="profile-section__title">学习概览</h3>
        <div class="overview-list">
          <div v-for="item in studentOverview.metrics.slice(0, 3)" :key="item.label" class="overview-item">
            <div class="overview-item__head">
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}</strong>
            </div>
            <p>{{ item.description }}</p>
          </div>
        </div>
      </section>

      <section class="profile-section">
        <h3 class="profile-section__title">基本信息</h3>
        <a-form layout="vertical">
          <a-row :gutter="16">
            <a-col :xs="24" :md="12">
              <a-form-item label="邮箱">
                <a-input v-model:value="formState.email" size="large" placeholder="请输入邮箱" />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="12">
              <a-form-item label="手机号">
                <a-input v-model:value="formState.phone" size="large" placeholder="请输入手机号" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="16">
            <a-col :xs="24" :md="12">
              <a-form-item label="常用学习地点">
                <a-input v-model:value="formState.location" size="large" placeholder="如：图书馆3楼" />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="12">
              <a-form-item label="关注方向（用 / 分隔）">
                <a-input
                  v-model:value="formState.focusAreasText"
                  size="large"
                  placeholder="例如：资料 / 实验准备 / 阶段复盘"
                />
              </a-form-item>
            </a-col>
          </a-row>
          <a-form-item label="个性签名">
            <a-textarea v-model:value="formState.signature" :rows="4" placeholder="介绍一下你的学习目标" />
          </a-form-item>
        </a-form>
      </section>

      <section class="profile-section">
        <h3 class="profile-section__title">通知偏好</h3>
        <div class="profile-preferences">
          <div v-for="item in preferences" :key="item.key" class="profile-preferences__item">
            <div>
              <h3>{{ item.label }}</h3>
              <p>开启后将接收对应学习提醒。</p>
            </div>
            <a-switch v-model:checked="item.enabled" />
          </div>
        </div>
      </section>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
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

const authStore = useAuthStore()
const appStore = useAppStore()
const { studentOverview } = storeToRefs(appStore)
const session = computed(() => authStore.session)
const focusAreas = computed(() => session.value?.focusAreas ?? [])

const formState = reactive<ProfileFormState>({
  email: '',
  phone: '',
  location: '',
  signature: '',
  focusAreasText: ''
})
const preferences = ref<NotificationPreference[]>([])

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
