<template>
  <div class="app-page-shell app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader
        eyebrow="作业模块"
        title="学生作业列表"
        description="按主题与状态组织当前作业，先看截止时间，再进入详情、提交或结果页。"
      >
        <template #actions>
          <a-button type="primary" @click="router.push('/learning')">返回学习概览</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="app-surface-card app-section-card app-panel-grid">
      <div class="homework-toolbar">
        <a-input v-model:value="homeworkStore.filters.keyword" allow-clear size="large" placeholder="搜索作业标题或摘要" />
        <a-select v-model:value="homeworkStore.filters.topicLabel" size="large" :options="topicLabelOptions" />
        <a-select v-model:value="homeworkStore.filters.status" size="large" :options="statusOptions" />
        <a-button size="large" @click="homeworkStore.resetFilters()">重置筛选</a-button>
      </div>
    </section>

    <section class="homework-grid">
      <article v-for="item in filteredHomeworks" :key="item.id" class="app-surface-card homework-card">
        <div class="homework-card__head">
          <div>
            <div class="homework-card__tags">
              <a-tag v-for="tag in item.tags" :key="tag">{{ tag }}</a-tag>
            </div>
            <h2 class="homework-card__title">{{ item.title }}</h2>
            <p class="app-list-card__meta">{{ item.topicLabel }} · {{ item.teacher }} · 截止 {{ item.deadline }}</p>
          </div>
          <StatusTag :type="statusTone(item.status)" :label="statusLabel(item.status)" />
        </div>
        <p class="homework-card__summary">{{ item.summary }}</p>
        <div class="homework-card__footer">
          <span class="app-inline-stat">{{ item.openTime }}</span>
          <a-space :size="10" wrap>
            <a-button @click="router.push(`/homework/${item.id}`)">查看详情</a-button>
            <a-button v-if="item.status === 'pending' || item.status === 'overdue'" type="primary" @click="router.push(`/homework/${item.id}/submit`)">去提交</a-button>
            <a-button v-else type="primary" @click="router.push(`/homework/${item.id}/result`)">查看结果</a-button>
          </a-space>
        </div>
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
import { useHomeworkStudentStore } from '@/stores/homework/student'
import type { HomeworkStudentStatus } from '@/stores/homework/types'

const router = useRouter()
const homeworkStore = useHomeworkStudentStore()
const { topicLabelOptions, filteredHomeworks, statusOptions } = storeToRefs(homeworkStore)

function statusTone(status: HomeworkStudentStatus): 'warning' | 'processing' | 'success' | 'default' {
  if (status === 'pending' || status === 'overdue') return 'warning'
  if (status === 'submitted') return 'processing'
  if (status === 'reviewed') return 'success'
  return 'default'
}

function statusLabel(status: HomeworkStudentStatus): string {
  if (status === 'pending') return '待完成'
  if (status === 'submitted') return '待批阅'
  if (status === 'reviewed') return '已批阅'
  return '已逾期'
}

onMounted(async () => {
  await homeworkStore.ensureLoaded()
})
</script>

<style scoped>
.homework-toolbar {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) 220px 180px auto;
  gap: 12px;
}

.homework-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: var(--space-5);
}

.homework-card {
  padding: 24px;
}

.homework-card__head,
.homework-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.homework-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 14px;
}

.homework-card__title {
  margin: 0;
  font-size: 24px;
}

.homework-card__summary {
  margin: 18px 0 20px;
  color: var(--color-text-secondary);
  line-height: 1.8;
}

@media (max-width: 960px) {
  .homework-toolbar {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .homework-card__head,
  .homework-card__footer {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
