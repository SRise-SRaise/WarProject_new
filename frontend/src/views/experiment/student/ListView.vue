<template>
  <div class="app-page-shell app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="实验模块" title="学生实验列表" description="先看开放安排与当前状态，再进入实验详情、处理页或结果页。">
        <template #actions>
          <a-button type="primary" @click="router.push('/learning')">返回学习概览</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="experiment-grid">
      <article v-for="item in visibleExperiments" :key="item.id" class="app-surface-card experiment-card">
        <div class="experiment-card__head">
          <div>
            <div class="experiment-card__tags"><a-tag v-for="tag in item.tags" :key="tag">{{ tag }}</a-tag></div>
            <h2 class="experiment-card__title">{{ item.title }}</h2>
            <p class="app-list-card__meta">{{ item.topicLabel }} · {{ item.teacher }} · {{ item.schedule }}</p>
          </div>
          <StatusTag :type="statusTone(item.status)" :label="statusLabel(item.status)" />
        </div>
        <p class="experiment-card__summary">{{ item.summary }}</p>
        <a-space :size="10" wrap>
          <a-button @click="router.push(`/experiments/${item.id}`)">查看详情</a-button>
          <a-button v-if="item.status === 'pending' || item.status === 'in_progress'" type="primary" @click="router.push(`/experiments/${item.id}/work`)">进入实验</a-button>
          <a-button v-else type="primary" @click="router.push(`/experiments/${item.id}/result`)">查看结果</a-button>
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
import { useExperimentStudentStore } from '@/stores/experiment/student'
import type { ExperimentStudentStatus } from '@/stores/experiment/types'

const router = useRouter()
const experimentStore = useExperimentStudentStore()
const { visibleExperiments } = storeToRefs(experimentStore)

function statusTone(status: ExperimentStudentStatus): 'warning' | 'processing' | 'success' | 'default' {
  if (status === 'pending') return 'warning'
  if (status === 'in_progress') return 'processing'
  if (status === 'reviewed') return 'success'
  return 'default'
}

function statusLabel(status: ExperimentStudentStatus): string {
  if (status === 'pending') return '待开始'
  if (status === 'in_progress') return '进行中'
  if (status === 'completed') return '已提交'
  return '已批阅'
}

onMounted(async () => {
  await experimentStore.ensureLoaded()
})
</script>

<style scoped>
.experiment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: var(--space-5);
}

.experiment-card {
  padding: 24px;
}

.experiment-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.experiment-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 14px;
}

.experiment-card__title {
  margin: 0;
  font-size: 24px;
}

.experiment-card__summary {
  margin: 18px 0;
  color: var(--color-text-secondary);
  line-height: 1.8;
}
</style>
