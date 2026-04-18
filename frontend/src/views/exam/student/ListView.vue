<template>
  <div class="student-exam-page">
    <header class="page-header">
      <div class="header-content">
        <h1 class="page-title">我的考试</h1>
        <p class="page-desc">查看可参加考试、已提交试卷和成绩状态</p>
      </div>
      <a-button @click="router.push('/learning')">返回学习概览</a-button>
    </header>

    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon blue"><FileTextOutlined /></div>
        <div class="stat-info">
          <span class="stat-value">{{ activeExamCount }}</span>
          <span class="stat-label">可参加考试</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green"><TrophyOutlined /></div>
        <div class="stat-info">
          <span class="stat-value">{{ releasedResultCount }}</span>
          <span class="stat-label">已出成绩</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange"><ClockCircleOutlined /></div>
        <div class="stat-info">
          <span class="stat-value">{{ pendingResultCount }}</span>
          <span class="stat-label">待教师批改</span>
        </div>
      </div>
    </div>

    <div class="exam-list-section">
      <div class="section-header">
        <h2 class="section-title">考试列表</h2>
      </div>

      <a-spin :spinning="loading">
        <div v-if="publishedExams.length === 0" class="empty-state">
          <InboxOutlined class="empty-icon" />
          <p class="empty-text">暂无可参加的考试</p>
        </div>

        <a-table
          v-else
          :data-source="publishedExams"
          :columns="columns"
          :pagination="false"
          row-key="id"
          class="exam-table"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'examName'">
              <div class="exam-name-cell">
                <span class="exam-name">{{ record.examName }}</span>
                <span v-if="record.paper" class="paper-name">试卷：{{ record.paper.paperName }}</span>
              </div>
            </template>
            <template v-else-if="column.key === 'info'">
              <div class="exam-info-cell">
                <span v-if="record.paper" class="info-item"><FileTextOutlined /> {{ record.paper.questionCount }} 题</span>
                <span v-if="record.paper" class="info-item"><TrophyOutlined /> {{ record.paper.totalScore }} 分</span>
                <span v-if="record.durationMin" class="info-item"><ClockCircleOutlined /> {{ record.durationMin }} 分钟</span>
              </div>
            </template>
            <template v-else-if="column.key === 'startTime'">
              <div class="time-block">
                <span v-if="record.startTime">开始：{{ formatDateTime(record.startTime) }}</span>
                <span v-else class="text-muted">开始：随时可考</span>
                <span v-if="record.endTime">结束：{{ formatDateTime(record.endTime) }}</span>
                <span v-else class="text-muted">结束：未设置</span>
              </div>
            </template>
            <template v-else-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record)">{{ getStatusText(record) }}</a-tag>
            </template>
            <template v-else-if="column.key === 'action'">
              <a-button
                :type="actionButtonType(record)"
                size="small"
                :disabled="actionDisabled(record)"
                @click="handleAction(record)"
              >
                {{ actionLabel(record) }}
              </a-button>
            </template>
          </template>
        </a-table>
      </a-spin>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import { FileTextOutlined, ClockCircleOutlined, TrophyOutlined, InboxOutlined } from '@ant-design/icons-vue'
import { useExamStudentStore } from '@/stores/exam/student'
import type { Exam } from '@/stores/exam/types'

const router = useRouter()
const examStore = useExamStudentStore()
const { publishedExams, loading } = storeToRefs(examStore)

const activeExamCount = computed(() => publishedExams.value.filter((exam) => examStore.canEnterExam(exam)).length)
const releasedResultCount = computed(() => publishedExams.value.filter((exam) => examStore.getSubmissionState(exam.id) === 'immediate').length)
const pendingResultCount = computed(() => publishedExams.value.filter((exam) => examStore.getSubmissionState(exam.id) === 'pending_teacher').length)

const columns = [
  { title: '考试名称', key: 'examName', width: '30%' },
  { title: '考试信息', key: 'info', width: '25%' },
  { title: '时间安排', key: 'startTime', width: '20%' },
  { title: '状态', key: 'status', width: '12%' },
  { title: '操作', key: 'action', width: '13%' },
]

function formatDateTime(dateStr: string): string {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

function resolveExamEnd(exam: Exam): Date | null {
  if (exam.endTime) return new Date(exam.endTime)
  if (exam.startTime && exam.durationMin) {
    return new Date(new Date(exam.startTime).getTime() + exam.durationMin * 60 * 1000)
  }
  return null
}

function isExamExpired(exam: Exam): boolean {
  const end = resolveExamEnd(exam)
  return !!end && new Date() > end
}

function getStatusColor(exam: Exam): string {
  const submissionState = examStore.getSubmissionState(exam.id)
  if (submissionState === 'immediate') return 'green'
  if (submissionState === 'pending_teacher') return 'orange'
  if (!exam.startTime) return isExamExpired(exam) ? 'default' : 'green'
  const now = new Date()
  const start = new Date(exam.startTime)
  const end = resolveExamEnd(exam)
  if (now < start) return 'blue'
  if (!end || now <= end) return 'green'
  return 'default'
}

function getStatusText(exam: Exam): string {
  const submissionState = examStore.getSubmissionState(exam.id)
  if (submissionState === 'immediate') return '已出成绩'
  if (submissionState === 'pending_teacher') return '待教师批改'
  if (!exam.startTime) return isExamExpired(exam) ? '已过期' : '可开始'
  const now = new Date()
  const start = new Date(exam.startTime)
  const end = resolveExamEnd(exam)
  if (now < start) return '未开始'
  if (!end || now <= end) return '进行中'
  return '已过期'
}

function actionLabel(exam: Exam): string {
  const submissionState = examStore.getSubmissionState(exam.id)
  if (submissionState === 'immediate') return '查看成绩'
  if (submissionState === 'pending_teacher') return '查看状态'
  if (examStore.canEnterExam(exam)) return '进入考试'
  if (exam.startTime && new Date() < new Date(exam.startTime)) return '未开始'
  if (isExamExpired(exam)) return '考试已过期'
  return '不可进入'
}

function actionButtonType(exam: Exam): 'default' | 'primary' {
  return examStore.hasSubmittedExam(exam.id) || examStore.canEnterExam(exam) ? 'primary' : 'default'
}

function actionDisabled(exam: Exam): boolean {
  return !examStore.hasSubmittedExam(exam.id) && !examStore.canEnterExam(exam)
}

function handleAction(exam: Exam): void {
  const submissionState = examStore.getSubmissionState(exam.id)
  if (submissionState !== 'none') {
    router.push(`/exams/${exam.id}/result`)
    return
  }
  if (!examStore.canEnterExam(exam)) return
  router.push(`/exams/${exam.id}/take`)
}

onMounted(async () => {
  await examStore.ensureLoaded()
})
</script>

<style scoped>
.student-exam-page { padding: 24px; max-width: 1200px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; padding-bottom: 20px; border-bottom: 1px solid #f0f0f0; }
.page-title { font-size: 24px; font-weight: 600; color: #1a1a1a; margin: 0 0 8px 0; }
.page-desc { color: #666; margin: 0; }
.stats-row { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 16px; margin-bottom: 24px; }
.stat-card { display: flex; align-items: center; gap: 16px; padding: 20px 24px; background: #fff; border: 1px solid #f0f0f0; border-radius: 8px; }
.stat-icon { width: 48px; height: 48px; display: flex; align-items: center; justify-content: center; border-radius: 12px; font-size: 24px; }
.stat-icon.blue { background: #e6f4ff; color: #1677ff; }
.stat-icon.green { background: #f6ffed; color: #52c41a; }
.stat-icon.orange { background: #fff7e6; color: #fa8c16; }
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 28px; font-weight: 600; color: #1a1a1a; line-height: 1.2; }
.stat-label { font-size: 14px; color: #666; }
.exam-list-section { background: #fff; border: 1px solid #f0f0f0; border-radius: 10px; padding: 20px 24px; }
.section-header { margin-bottom: 16px; }
.section-title { margin: 0; font-size: 18px; font-weight: 600; }
.exam-name-cell { display: flex; flex-direction: column; gap: 4px; }
.exam-name { font-weight: 600; color: #1f1f1f; }
.paper-name, .text-muted { color: #8c8c8c; font-size: 13px; }
.exam-info-cell, .time-block { display: flex; flex-direction: column; gap: 6px; }
.info-item { display: inline-flex; align-items: center; gap: 6px; color: #595959; }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 60px 20px; }
.empty-icon { font-size: 48px; color: #d9d9d9; margin-bottom: 12px; }
.empty-text { color: #8c8c8c; }
@media (max-width: 960px) { .stats-row { grid-template-columns: 1fr; } .page-header { gap: 12px; flex-direction: column; } }
</style>
