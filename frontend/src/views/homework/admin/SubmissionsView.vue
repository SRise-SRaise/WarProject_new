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
        <a-table :columns="columns" :data-source="submissions" row-key="id" :pagination="false">
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
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import StatusTag from '@/components/common/StatusTag.vue'
import { getMyExerciseScore } from '@/api/eduExerciseSubmissionController'
import { listAuthStudentVoByPage } from '@/api/authStudentController'

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

const route = useRoute()
const router = useRouter()

const homeworkId = computed(() => String(route.params.id || ''))
const loading = ref(false)
const submissions = ref<SubmissionItem[]>([])

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

async function loadSubmissions() {
  if (!homeworkId.value) return

  loading.value = true
  try {
    // 获取班级学生列表
    const studentsResponse = await listAuthStudentVoByPage({ current: 1, pageSize: 50 })
    if (studentsResponse.data?.records) {
      const students = studentsResponse.data.records

      // 查询每个学生的成绩状态
      const submissionList: SubmissionItem[] = []

      for (const student of students) {
        try {
          const scoreResponse = await getMyExerciseScore({
            exerciseId: Number(homeworkId.value),
            studentId: student.id || 0
          })

          const scoreData = scoreResponse.data
          if (scoreData) {
            submissionList.push({
              id: `sub-${student.id}`,
              homeworkId: homeworkId.value,
              studentId: student.id || 0,
              studentName: student.studentName || '',
              className: student.classCode || '',
              submittedAt: '',
              status: scoreData.status === 'reviewed' ? 'reviewed' : 'submitted',
              score: `${scoreData.totalScore} / ${scoreData.maxScore}`,
              summary: ''
            })
          }
        } catch {
          // 学生未提交，跳过
        }
      }

      submissions.value = submissionList
    }
  } catch (error) {
    console.error('加载提交记录失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadSubmissions()
})
</script>