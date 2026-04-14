<template>
  <div class="app-page-shell app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="考试模块" title="学生考试列表" description="围绕待参加、即将开放与已完成考试组织确认、答题和结果入口。">
        <template #actions>
          <a-button type="primary" @click="router.push('/learning')">返回学习概览</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="app-kpi-grid">
      <MetricCard title="待参加" :value="String(readyCount)" description="已开放且可进入确认页的考试。" tone="warning" />
      <MetricCard title="已完成" :value="String(completedCount)" description="已经提交并生成结果摘要的考试。" tone="success" />
      <MetricCard title="共享题库" value="3 套" description="题库继续由 exam 模块维护，但同时服务作业与考试。" tone="accent" />
    </section>

    <section class="exam-grid">
      <article v-for="item in exams" :key="item.id" class="app-surface-card exam-card">
        <div class="exam-card__head">
          <div>
            <div class="exam-card__tags"><a-tag v-for="tag in item.tags" :key="tag">{{ tag }}</a-tag></div>
            <h2 class="exam-card__title">{{ item.title }}</h2>
            <p class="app-list-card__meta">{{ item.topicLabel }} · {{ item.teacher }} · {{ item.schedule }}</p>
          </div>
          <StatusTag :type="statusTone(item.status)" :label="statusLabel(item.status)" />
        </div>
        <p class="exam-card__summary">{{ item.summary }}</p>
        <div class="exam-card__stats">
          <span class="app-inline-stat">时长 {{ item.duration }}</span>
          <span class="app-inline-stat">题目 {{ item.questionCount }}</span>
        </div>
        <a-space :size="10" wrap>
          <a-button v-if="item.status === 'ready' || item.status === 'in_progress'" type="primary" @click="router.push(`/exams/${item.id}/confirm`)">进入确认</a-button>
          <a-button v-else-if="item.status === 'completed'" type="primary" @click="router.push(`/exams/${item.id}/result`)">查看结果</a-button>
          <a-button v-else>等待开放</a-button>
        </a-space>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import MetricCard from '@/components/common/MetricCard.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { useExamStudentStore } from '@/stores/exam/student'
import type { ExamStudentStatus } from '@/stores/exam/types'

const router = useRouter()
const examStore = useExamStudentStore()
const { exams } = storeToRefs(examStore)

const readyCount = computed(() => exams.value.filter((item) => item.status === 'ready' || item.status === 'in_progress').length)
const completedCount = computed(() => exams.value.filter((item) => item.status === 'completed').length)

function statusTone(status: ExamStudentStatus): 'warning' | 'processing' | 'success' | 'default' {
  if (status === 'ready') return 'warning'
  if (status === 'in_progress') return 'processing'
  if (status === 'completed') return 'success'
  return 'default'
}

function statusLabel(status: ExamStudentStatus): string {
  if (status === 'ready') return '待参加'
  if (status === 'in_progress') return '进行中'
  if (status === 'completed') return '已完成'
  return '未开放'
}

onMounted(async () => {
  await examStore.ensureLoaded()
})
</script>

<style scoped>
.exam-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: var(--space-5);
}

.exam-card {
  padding: 24px;
}

.exam-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.exam-card__tags,
.exam-card__stats {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.exam-card__tags {
  margin-bottom: 14px;
}

.exam-card__title {
  margin: 0;
  font-size: 24px;
}

.exam-card__summary {
  margin: 18px 0;
  color: var(--color-text-secondary);
  line-height: 1.8;
}

.exam-card__stats {
  margin-bottom: 18px;
}
</style>
