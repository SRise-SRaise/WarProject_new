<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="考试管理" title="教师考试列表" description="围绕考试安排、提交进度与成绩汇总组织后台入口。">
        <template #actions>
          <a-button type="primary" @click="router.push('/admin/scores/summary')">查看成绩汇总</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="exam-admin-grid">
      <article v-for="item in exams" :key="item.id" class="app-surface-card exam-admin-card">
        <div class="exam-admin-card__head">
          <div>
            <div class="exam-admin-card__tags"><a-tag v-for="tag in item.tags" :key="tag">{{ tag }}</a-tag></div>
            <h2 class="exam-admin-card__title">{{ item.title }}</h2>
            <p class="app-list-card__meta">{{ item.topicLabel }} · {{ item.scope }} · {{ item.schedule }}</p>
          </div>
          <StatusTag :type="statusTone(item.status)" :label="statusLabel(item.status)" />
        </div>
        <p class="exam-admin-card__summary">{{ item.summary }}</p>
        <div class="exam-admin-card__stats">
          <span class="app-inline-stat">试卷 {{ item.paperTitle }}</span>
          <span class="app-inline-stat">应参加 {{ item.candidateCount }}</span>
          <span class="app-inline-stat">已提交 {{ item.submittedCount }}</span>
          <span class="app-inline-stat">均分 {{ item.averageScore }}</span>
        </div>
        <a-space :size="10" wrap>
          <a-button @click="router.push(`/admin/exams/records/${item.id}`)">考试记录</a-button>
          <a-button type="primary" @click="router.push('/admin/scores/summary')">成绩汇总</a-button>
        </a-space>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { useExamAdminStore } from '@/stores/exam/admin'
import type { ExamAdminStatus } from '@/stores/exam/types'

const router = useRouter()
const examStore = useExamAdminStore()
const { exams } = storeToRefs(examStore)

function statusTone(status: ExamAdminStatus): 'warning' | 'processing' | 'success' | 'default' {
  if (status === 'draft') return 'warning'
  if (status === 'scheduled') return 'processing'
  if (status === 'running') return 'success'
  return 'default'
}

function statusLabel(status: ExamAdminStatus): string {
  if (status === 'draft') return '草稿'
  if (status === 'scheduled') return '已排期'
  if (status === 'running') return '进行中'
  return '已结束'
}

onMounted(async () => {
  await examStore.ensureLoaded()
})
</script>

<style scoped>
.exam-admin-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: var(--space-5); }
.exam-admin-card { padding: 24px; }
.exam-admin-card__head { display: flex; align-items: flex-start; justify-content: space-between; gap: 12px; }
.exam-admin-card__tags,.exam-admin-card__stats { display: flex; flex-wrap: wrap; gap: 8px; }
.exam-admin-card__tags { margin-bottom: 14px; }
.exam-admin-card__title { margin: 0; font-size: 24px; }
.exam-admin-card__summary { margin: 18px 0; color: var(--color-text-secondary); line-height: 1.8; }
.exam-admin-card__stats { margin-bottom: 18px; }
</style>
