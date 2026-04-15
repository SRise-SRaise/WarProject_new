<template>
  <div class="app-panel-grid">
    <div class="hw-page-header">
      <div class="hw-page-header__left">
        <h1 class="hw-page-header__title">作业提交记录</h1>
        <p class="hw-page-header__desc">查看学生提交明细和进入批改。</p>
      </div>
      <div class="hw-page-header__actions">
        <a-button @click="router.push('/admin/homework')">返回作业列表</a-button>
      </div>
    </div>

    <section class="app-surface-card app-section-card">
      <a-table :columns="columns" :data-source="submissions" row-key="id" :pagination="false">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <StatusTag :type="statusTone(record.status)" :label="statusLabel(record.status)" />
          </template>
          <template v-else-if="column.key === 'score'">
            <span class="app-inline-stat">{{ record.score || '待评分' }}</span>
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
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import StatusTag from '@/components/common/StatusTag.vue'

type SubmissionStatus = 'draft' | 'submitted' | 'reviewed' | 'late'

interface SubmissionMock {
  id: string
  homeworkId: string
  studentName: string
  className: string
  submittedAt: string
  status: SubmissionStatus
  score?: string
  summary: string
}

const submissionsMock: SubmissionMock[] = [
  { id: 'sub-1', homeworkId: 'hw-101', studentName: '李明', className: '软工 2402', submittedAt: '2026-04-18 21:32', status: 'submitted', summary: '已完成角色旅程与异常流梳理。' },
  { id: 'sub-2', homeworkId: 'hw-101', studentName: '张宁', className: '软工 2402', submittedAt: '2026-04-18 20:45', status: 'reviewed', score: '87 分', summary: '结构完整，边界说明待加强。' },
  { id: 'sub-3', homeworkId: 'hw-102', studentName: '王若溪', className: '前端 2401', submittedAt: '2026-04-18 23:02', status: 'late', summary: '补交组件结构复盘文档。' }
]

const route = useRoute()
const router = useRouter()
const homeworkId = computed(() => String(route.params.id || ''))
const submissions = computed(() => submissionsMock.filter((item) => item.homeworkId === homeworkId.value || homeworkId.value.length === 0))

const columns = [
  { title: '学生', dataIndex: 'studentName', key: 'studentName' },
  { title: '班级', dataIndex: 'className', key: 'className' },
  { title: '提交时间', dataIndex: 'submittedAt', key: 'submittedAt' },
  { title: '状态', dataIndex: 'status', key: 'status', width: 110 },
  { title: '得分', dataIndex: 'score', key: 'score', width: 100 },
  { title: '说明', dataIndex: 'summary', key: 'summary' },
  { title: '操作', key: 'action', width: 120 }
]

function statusTone(status: SubmissionStatus): 'success' | 'processing' | 'warning' | 'default' {
  if (status === 'reviewed') return 'success'
  if (status === 'submitted') return 'processing'
  if (status === 'late') return 'warning'
  return 'default'
}

function statusLabel(status: SubmissionStatus): string {
  if (status === 'reviewed') return '已批阅'
  if (status === 'submitted') return '待批阅'
  if (status === 'late') return '逾期提交'
  return '草稿'
}
</script>