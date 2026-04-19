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
        <a-spin :spinning="loading">
        <a-table :columns="columns" :data-source="submissions" row-key="id" :pagination="tablePagination">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <StatusTag :type="statusTone(record.status)" :label="statusLabel(record.status)" />
            </template>
            <template v-else-if="column.key === 'score'">
              <span class="app-inline-stat">{{ record.score || '待评分' }}</span>
            </template>
            <template v-else-if="column.key === 'action'">
              <a-button type="link" @click="router.push(`/admin/homework/review/${homeworkId}/${record.studentId}`)">进入批改</a-button>
            </template>
          </template>
        </a-table>
      </a-spin>
    </section>
  </div>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/request'
import StatusTag from '@/components/common/StatusTag.vue'
import { getSubmissionDetail } from '@/api/eduExerciseSubmissionController'

type SubmissionStatus = 'draft' | 'submitted' | 'reviewed' | 'late'

interface SubmissionItem {
  id: string
  homeworkId: string
  studentId: number
  studentName: string
  className: string
  submittedAt: string
  status: SubmissionStatus
  score?: string
  summary: string
}

interface SubmissionRecordResponseItem {
  id?: number
  exerciseId?: number
  studentId?: number
  studentName?: string
  className?: string
  classCode?: string
  status?: string
  submittedAt?: string
  totalScore?: number
  gradingSummary?: string
}

const route = useRoute()
const router = useRouter()

const homeworkId = computed(() => String(route.params.id || ''))
const loading = ref(false)
const submissions = ref<SubmissionItem[]>([])
const tablePagination = computed(() => ({
  pageSize: 10,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50'],
  showTotal: (total: number) => `共 ${total} 条提交记录`
}))

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

function formatDateTime(value: unknown): string {
  if (value === null || value === undefined || String(value).trim().length === 0) {
    return '--'
  }
  const parsed = dayjs(String(value))
  if (!parsed.isValid()) {
    return String(value)
  }
  return parsed.format('YYYY-MM-DD HH:mm')
}

function toTimestamp(value: string): number {
  const parsed = dayjs(value)
  return parsed.isValid() ? parsed.valueOf() : 0
}

async function loadSubmissionDetailTimes(records: SubmissionItem[]): Promise<SubmissionItem[]> {
  const detailResults = await Promise.allSettled(
    records.map(async (record) => {
      const detailResponse = await getSubmissionDetail({
        exerciseId: Number(homeworkId.value),
        studentId: record.studentId
      })
      return {
        studentId: record.studentId,
        submittedAt: formatDateTime(detailResponse.data?.data?.submittedAt)
      }
    })
  )

  const submittedAtMap = new Map<number, string>()
  for (const result of detailResults) {
    if (result.status === 'fulfilled') {
      submittedAtMap.set(result.value.studentId, result.value.submittedAt)
    }
  }

  return records.map((record) => ({
    ...record,
    submittedAt: submittedAtMap.get(record.studentId) ?? record.submittedAt
  }))
}

async function loadSubmissions() {
  if (!homeworkId.value) {
    message.warning('请先从作业列表选择具体作业，再查看提交记录')
    await router.replace('/admin/homework')
    return
  }

  loading.value = true
  try {
    const response = await request<{ code?: number; data?: SubmissionRecordResponseItem[] }>(
      '/homework/submission/listRecords',
      {
        method: 'GET',
        params: { exerciseId: Number(homeworkId.value) }
      }
    )

    const records = Array.isArray(response.data?.data) ? response.data.data : []
    const normalizedRecords = records.map((record) => ({
      id: `sub-${Number(record.studentId ?? 0)}`,
      homeworkId: homeworkId.value,
      studentId: Number(record.studentId ?? 0),
      studentName: String(record.studentName ?? ''),
      className: String(record.className ?? record.classCode ?? ''),
      submittedAt: formatDateTime(record.submittedAt),
      status: record.status === 'reviewed' ? 'reviewed' : 'submitted',
      score: String(Number(record.totalScore ?? 0)),
      summary: String(record.gradingSummary ?? '')
    }))
    const recordsWithDetail = await loadSubmissionDetailTimes(normalizedRecords)

    submissions.value = recordsWithDetail
      .filter((record) => record.studentId > 0)
      .sort((left, right) => toTimestamp(right.submittedAt) - toTimestamp(left.submittedAt))
  } catch (error) {
    console.error('加载提交记录失败:', error)
    message.error('加载提交记录失败')
    submissions.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadSubmissions()
})
</script>
