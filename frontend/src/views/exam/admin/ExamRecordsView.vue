<template>
  <div class="app-panel-grid">
    <section v-if="currentExam" class="app-surface-card app-section-card">
      <SectionHeader eyebrow="考试记录" :title="currentExam.title" description="查看单场考试的学生提交状态、风险提示与当前成绩。">
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="router.push('/admin/exams')">返回考试列表</a-button>
            <a-button type="primary" @click="router.push('/admin/scores/summary')">查看成绩汇总</a-button>
          </a-space>
        </template>
      </SectionHeader>
    </section>

    <section class="record-grid">
      <article v-for="item in records" :key="item.id" class="app-surface-card record-card">
        <div class="record-card__head">
          <div>
            <h2 class="record-card__title">{{ item.studentName }}</h2>
            <p class="app-list-card__meta">{{ item.className }} · 开始 {{ item.startedAt }} · 提交 {{ item.submittedAt ?? '--' }}</p>
          </div>
          <StatusTag :type="riskTone(item.risk)" :label="statusLabel(item.status)" />
        </div>
        <p class="record-card__summary">{{ item.note }}</p>
        <div class="record-card__stats">
          <span class="app-inline-stat">风险 {{ item.risk }}</span>
          <span class="app-inline-stat">成绩 {{ item.score ?? '--' }}</span>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { useExamAdminStore } from '@/stores/exam/admin'
import type { ExamRecordStatus } from '@/stores/exam/types'

const route = useRoute()
const router = useRouter()
const examStore = useExamAdminStore()
const { currentExam, records } = storeToRefs(examStore)

function riskTone(risk: 'low' | 'medium' | 'high'): 'success' | 'warning' | 'error' {
  if (risk === 'low') return 'success'
  if (risk === 'medium') return 'warning'
  return 'error'
}

function statusLabel(status: ExamRecordStatus): string {
  if (status === 'not_started') return '未开始'
  if (status === 'in_progress') return '进行中'
  if (status === 'submitted') return '已提交'
  return '已批阅'
}

onMounted(async () => {
  const examId = String(route.params.id)
  await examStore.selectExam(examId)
  await examStore.loadRecords(examId)
})
</script>

<style scoped>
.record-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: var(--space-5); }
.record-card { padding: 24px; }
.record-card__head { display: flex; align-items: flex-start; justify-content: space-between; gap: 12px; }
.record-card__title { margin: 0; font-size: 24px; }
.record-card__summary { margin: 18px 0; color: var(--color-text-secondary); line-height: 1.8; }
.record-card__stats { display: flex; flex-wrap: wrap; gap: 8px; }
</style>
