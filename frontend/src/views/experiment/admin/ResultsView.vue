<template>
  <div class="app-panel-grid">
    <section v-if="currentExperiment" class="app-surface-card app-section-card">
      <SectionHeader eyebrow="实验结果处理" :title="`${currentExperiment.title} · 结果列表`" description="统一查看实验结果、报告文件和批阅入口。">
        <template #actions>
          <a-button @click="router.push('/admin/experiments')">返回实验列表</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="app-surface-card app-section-card">
      <a-table :columns="columns" :data-source="results" :loading="loading" row-key="id" :pagination="false">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <StatusTag :type="record.status === 'reviewed' ? 'success' : record.status === 'submitted' ? 'processing' : 'warning'" :label="record.status === 'reviewed' ? '已处理' : record.status === 'submitted' ? '待处理' : '待开始'" />
          </template>
          <template v-else-if="column.key === 'score'">
            <span class="app-inline-stat">{{ record.score ?? '待评分' }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space :size="8">
              <a-button type="link" @click="router.push(`/experiments/${record.experimentId}/report`)">查看报告</a-button>
              <a-button type="primary" ghost @click="router.push(`/admin/experiments/results/${record.experimentId}/${record.id}`)">
                批改
              </a-button>
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
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { useExperimentAdminStore } from '@/stores/experiment/admin'

const route = useRoute()
const router = useRouter()
const experimentStore = useExperimentAdminStore()
const { currentExperiment, loading, results } = storeToRefs(experimentStore)

const columns = [
  { title: '学生', dataIndex: 'studentName', key: 'studentName' },
  { title: '班级', dataIndex: 'className', key: 'className' },
  { title: '提交时间', dataIndex: 'submittedAt', key: 'submittedAt' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '得分', dataIndex: 'score', key: 'score' },
  { title: '说明', dataIndex: 'summary', key: 'summary' },
  { title: '操作', key: 'action' }
]

onMounted(async () => {
  const experimentId = String(route.params.id)
  await experimentStore.selectExperiment(experimentId)
  await experimentStore.loadResults(experimentId)
})
</script>
