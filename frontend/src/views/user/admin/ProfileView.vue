<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <div class="profile-hero">
        <div class="profile-hero__identity">
          <div class="profile-avatar">{{ session?.avatar }}</div>
          <div>
            <h1 class="profile-hero__title">{{ session?.name }}</h1>
            <p class="profile-hero__meta">{{ session?.title }} · {{ session?.department }}</p>
            <div class="profile-hero__tags">
              <a-tag v-for="focus in session?.focusAreas" :key="focus">{{ focus }}</a-tag>
            </div>
          </div>
        </div>
        <a-button type="primary" size="large" @click="saveProfile">保存教师设置</a-button>
      </div>
    </section>

    <section class="app-kpi-grid">
      <MetricCard
        v-for="item in teacherDashboard.metrics.slice(0, 3)"
        :key="item.label"
        :title="item.label"
        :value="item.value"
        :description="item.description"
        :trend="item.trend"
        :tone="item.tone"
      />
    </section>

    <section class="app-split-grid">
      <div class="app-panel-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="教师资料" title="当前档案" description="保留对教师工作最有帮助的核心资料。" tight />
          <div class="app-key-value">
            <div class="app-key-value__item">
              <p class="app-key-value__label">院系</p>
              <p class="app-key-value__value">{{ session?.department }}</p>
            </div>
            <div class="app-key-value__item">
              <p class="app-key-value__label">邮箱</p>
              <p class="app-key-value__value">{{ session?.email }}</p>
            </div>
            <div class="app-key-value__item">
              <p class="app-key-value__label">联系电话</p>
              <p class="app-key-value__value">{{ session?.phone }}</p>
            </div>
            <div class="app-key-value__item">
              <p class="app-key-value__label">最近登录</p>
              <p class="app-key-value__value">{{ session?.lastLogin }}</p>
            </div>
          </div>
        </section>

        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="通知偏好" title="后台提醒" description="控制班级风险、资料发布和教研协同提醒。" tight />
          <div class="profile-preferences">
            <div v-for="item in preferences" :key="item.key" class="profile-preferences__item">
              <div>
                <h3>{{ item.label }}</h3>
                <p>保存后仅作用于当前本地教师会话。</p>
              </div>
              <a-switch v-model:checked="item.enabled" />
            </div>
          </div>
        </section>
      </div>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="可编辑项" title="更新教师信息" description="围绕工作台使用场景，仅保留对教学协同有帮助的常用字段。" tight />
        <a-form layout="vertical">
          <a-form-item label="邮箱">
            <a-input v-model:value="formState.email" size="large" />
          </a-form-item>
          <a-form-item label="手机号">
            <a-input v-model:value="formState.phone" size="large" />
          </a-form-item>
          <a-form-item label="办公地点">
            <a-input v-model:value="formState.location" size="large" />
          </a-form-item>
          <a-form-item label="简介说明">
            <a-textarea v-model:value="formState.signature" :rows="4" />
          </a-form-item>
          <a-form-item label="当前关注（用 / 分隔）">
            <a-input v-model:value="formState.focusAreasText" size="large" placeholder="例如：班级组织 / 资料 / 教学反馈" />
          </a-form-item>
        </a-form>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import MetricCard from '@/components/common/MetricCard.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
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
const { teacherDashboard } = storeToRefs(appStore)
const session = computed(() => authStore.session)

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
  message.success('教师个人设置已保存。')
}

onMounted(async () => {
  await appStore.ensureReady()
})
</script>

<style scoped>
.profile-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-5);
}

.profile-hero__identity {
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
  background: linear-gradient(135deg, var(--color-primary-deep) 0%, var(--color-primary) 100%);
  color: var(--color-text-on-dark);
  font-size: 28px;
  font-weight: 800;
}

.profile-hero__title {
  margin: 0;
  font-size: 32px;
  font-family: Georgia, 'Times New Roman', 'Songti SC', serif;
}

.profile-hero__meta {
  margin: 10px 0 0;
  color: var(--color-text-secondary);
}

.profile-hero__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}

.profile-preferences {
  display: grid;
  gap: 14px;
}

.profile-preferences__item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px;
  border-radius: var(--radius-md);
  background: var(--color-bg-muted);
  border: 1px solid rgba(194, 206, 222, 0.55);
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
  .profile-hero,
  .profile-hero__identity,
  .profile-preferences__item {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
