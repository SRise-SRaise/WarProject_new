<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="实验管理" title="教师实验列表" description="围绕实验任务、实验项和实验报告评分组织教师处理入口。">
        <template #actions>
          <a-button type="primary" @click="router.push('/admin/experiments/edit')">新建实验</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="app-surface-card experiment-admin-table-card">
      <a-table :columns="columns" :data-source="experiments" :loading="loading" row-key="id" :pagination="false" :scroll="{ x: 960 }">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'experiment'">
            <div class="experiment-cell">
              <div class="experiment-cell__head">
                <h2 class="experiment-cell__title">{{ record.title }}</h2>
                <div class="experiment-cell__tags">
                  <a-tag v-for="tag in record.tags" :key="tag">{{ tag }}</a-tag>
                </div>
              </div>
              <p class="app-list-card__meta">{{ record.topicLabel }} · {{ record.scope }} · {{ record.schedule }}</p>
              <p class="experiment-cell__summary">{{ record.summary }}</p>
            </div>
          </template>
          <template v-else-if="column.key === 'stats'">
            <div class="experiment-cell__stats">
              <span class="app-inline-stat">实验项 {{ record.itemCount }}</span>
              <span class="app-inline-stat">结果 {{ record.resultCount }}</span>
              <span class="app-inline-stat">更新 {{ record.updatedAt }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'status'">
            <StatusTag :type="statusTone(record.status)" :label="statusLabel(record.status)" />
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space :size="8" wrap>
              <a-button @click="router.push(`/admin/experiments/edit/${record.id}`)">编辑</a-button>
              <a-button @click="router.push(`/admin/experiments/items/${record.id}`)">实验项</a-button>
              <a-button type="primary" @click="router.push(`/admin/experiments/results/${record.id}`)">实验报告评分</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { useExperimentAdminStore } from '@/stores/experiment/admin'
import type { ExperimentAdminStatus } from '@/stores/experiment/types'

const router = useRouter()
const experimentStore = useExperimentAdminStore()
const { experiments, loading } = storeToRefs(experimentStore)

const columns = [
  { title: '实验信息', key: 'experiment' },
  { title: '数据概览', key: 'stats', width: 300 },
  { title: '状态', key: 'status', width: 120 },
  { title: '操作', key: 'action', width: 260 }
]

function statusTone(status: ExperimentAdminStatus): 'warning' | 'success' | 'processing' | 'default' {
  if (status === 'draft') return 'warning'
  if (status === 'published') return 'success'
  if (status === 'running') return 'processing'
  return 'default'
}

function statusLabel(status: ExperimentAdminStatus): string {
  if (status === 'draft') return '草稿'
  if (status === 'published') return '已发布'
  if (status === 'running') return '进行中'
  return '已结束'
}

onMounted(async () => {
  await experimentStore.ensureLoaded()
})
</script>

<style scoped>
.experiment-admin-table-card {
  padding: var(--space-3);
  overflow: hidden;
}

.experiment-cell {
  display: grid;
  gap: var(--space-3);
}

.experiment-cell__head {
  display: grid;
  gap: var(--space-2);
}

.experiment-cell__title {
  margin: 0;
  color: var(--color-text-main);
  font-size: 20px;
}

.experiment-cell__tags,
.experiment-cell__stats {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.experiment-cell__summary {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.75;
}

:deep(.ant-table-wrapper) {
  overflow: hidden;
}

:deep(.ant-table-thead > tr > th) {
  padding: var(--space-4);
}

:deep(.ant-table-tbody > tr > td) {
  padding: var(--space-5) var(--space-4);
  vertical-align: top;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: var(--color-bg-spotlight);
}
</style>
