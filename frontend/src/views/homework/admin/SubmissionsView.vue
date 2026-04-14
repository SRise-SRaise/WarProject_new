<template>
  <div class="app-panel-grid">
    <section v-if="currentHomework" class="app-surface-card app-section-card">
      <SectionHeader eyebrow="提交记录" :title="`${currentHomework.title} · 提交情况`" description="统一查看学生提交状态、班级来源和批阅入口。">
        <template #actions>
          <a-button @click="router.push('/admin/homework')">返回作业列表</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="app-surface-card app-section-card">
      <a-table :columns="columns" :data-source="submissions" :loading="loading" row-key="id" :pagination="false">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <StatusTag :type="statusTone(record.status)" :label="statusLabel(record.status)" />
          </template>
          <template v-else-if="column.key === 'score'">
            <span class="app-inline-stat">{{ record.score ?? '待评分' }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" @click="router.push(`/admin/homework/review/${record.homeworkId}/${record.id}`)">进入批改</a-button>
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
import { useHomeworkAdminStore } from '@/stores/homework/admin'
import type { HomeworkSubmissionStatus } from '@/stores/homework/types'

const route = useRoute()
const router = useRouter()
const homeworkStore = useHomeworkAdminStore()
const { currentHomework, loading, submissions } = storeToRefs(homeworkStore)

const columns = [
  { title: '学生', dataIndex: 'studentName', key: 'studentName' },
  { title: '班级', dataIndex: 'className', key: 'className' },
  { title: '提交时间', dataIndex: 'submittedAt', key: 'submittedAt' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '得分', dataIndex: 'score', key: 'score' },
  { title: '说明', dataIndex: 'summary', key: 'summary' },
  { title: '操作', key: 'action' }
]

function statusTone(status: HomeworkSubmissionStatus): 'success' | 'processing' | 'warning' | 'default' {
  if (status === 'reviewed') return 'success'
  if (status === 'submitted') return 'processing'
  if (status === 'late') return 'warning'
  return 'default'
}

function statusLabel(status: HomeworkSubmissionStatus): string {
  if (status === 'reviewed') return '已批阅'
  if (status === 'submitted') return '待批阅'
  if (status === 'late') return '逾期'
  return '草稿'
}

onMounted(async () => {
  const homeworkId = String(route.params.id)
  await homeworkStore.selectHomework(homeworkId)
  await homeworkStore.loadSubmissions(homeworkId)
})
</script>
