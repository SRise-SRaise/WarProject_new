<template>
  <div class="app-panel-grid">
    <div class="hw-page-header">
      <div class="hw-page-header__left">
        <h1 class="hw-page-header__title">{{ pageTitle }}</h1>
        <p class="hw-page-header__desc">维护作业基本信息，保存后进入布置作业页面。</p>
      </div>
      <div class="hw-page-header__actions">
        <a-button @click="router.push('/admin/homework')">返回作业列表</a-button>
      </div>
    </div>

    <section class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <a-form layout="vertical">
          <a-form-item label="作业标题" required>
            <a-input v-model:value="formState.title" size="large" placeholder="输入作业标题" />
          </a-form-item>
          <a-form-item label="关联实验">
            <a-select v-model:value="formState.relateExpId" size="large" placeholder="选择关联实验（可选）" allow-clear :options="experimentOptions" />
          </a-form-item>
          <a-form-item label="开始时间">
            <a-input v-model:value="formState.startTime" size="large" placeholder="例如：2026-04-14 08:00" />
          </a-form-item>
          <a-form-item label="截止时间" required>
            <a-input v-model:value="formState.deadline" size="large" placeholder="例如：2026-04-20 20:00" />
          </a-form-item>
          <a-form-item label="作业描述">
            <a-textarea v-model:value="formState.summary" :rows="4" placeholder="简要描述作业目标和要求" />
          </a-form-item>
          <a-button type="primary" size="large" @click="saveHomework">保存作业</a-button>
        </a-form>
      </section>

      <div class="hw-side-column">
        <a-alert type="info" message="提示" description="保存后进入布置页面，可设置发布班级和补交策略。" show-icon />
        <div class="hw-tip-card">
          <p class="hw-tip-card__title">作业题目</p>
          <p class="hw-tip-card__text">保存作业后，可在布置页面从题库中选择题目，或临时新建题目加入作业。</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'

interface HomeworkEditMock {
  id: string
  title: string
  summary: string
  deadline: string
  startTime: string
  relateExpId: string | null
}

const editMockData: HomeworkEditMock[] = [
  {
    id: 'hw-101',
    title: '需求分析作业一：角色旅程拆解',
    summary: '围绕教学平台案例输出角色旅程和验收边界。',
    deadline: '2026-04-20 20:00',
    startTime: '2026-04-14 08:00',
    relateExpId: null
  }
]

const experimentOptions = [
  { label: '实验一：HTML基础', value: '1' },
  { label: '实验二：CSS布局', value: '2' },
  { label: '实验三：JavaScript交互', value: '3' }
]

const route = useRoute()
const router = useRouter()
const homeworkId = computed(() => (typeof route.params.id === 'string' ? route.params.id : ''))
const selected = computed(() => editMockData.find((item) => item.id === homeworkId.value))
const pageTitle = computed(() => (homeworkId.value ? '编辑作业' : '新增作业'))

const formState = reactive({
  title: selected.value?.title ?? '',
  startTime: selected.value?.startTime ?? '',
  deadline: selected.value?.deadline ?? '',
  summary: selected.value?.summary ?? '',
  relateExpId: selected.value?.relateExpId ?? null as string | null
})

function saveHomework(): void {
  if (formState.title.trim().length === 0) {
    message.error('请先填写标题。')
    return
  }
  message.success('作业已保存（Mock）。')
  const targetId = homeworkId.value || 'hw-new'
  router.push(`/admin/homework/assign/${targetId}`)
}
</script>