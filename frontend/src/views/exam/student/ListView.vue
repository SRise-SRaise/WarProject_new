<template>
  <div class="student-exam-page">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1 class="page-title">我的考试</h1>
          <p class="page-desc">查看可参加考试、已提交试卷和成绩状态</p>
        </div>
        <a-button class="back-btn" @click="router.push('/learning')">
          <ArrowLeftOutlined />
          返回学习概览
        </a-button>
      </div>
    </header>

    <!-- 统计卡片 -->
    <section class="stats-section">
      <div class="stats-grid">
        <article class="stat-card stat-card--primary">
          <div class="stat-visual">
            <FileTextOutlined />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ activeExamCount }}</span>
            <span class="stat-label">可参加考试</span>
          </div>
        </article>
        <article class="stat-card stat-card--success">
          <div class="stat-visual">
            <TrophyOutlined />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ releasedResultCount }}</span>
            <span class="stat-label">已出成绩</span>
          </div>
        </article>
        <article class="stat-card stat-card--warning">
          <div class="stat-visual">
            <ClockCircleOutlined />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ pendingResultCount }}</span>
            <span class="stat-label">待教师批改</span>
          </div>
        </article>
      </div>
    </section>

    <!-- 考试列表 -->
    <section class="exam-list-section">
      <div class="section-header">
        <h2 class="section-title">
          <OrderedListOutlined />
          考试列表
        </h2>
      </div>

      <a-spin :spinning="loading">
        <div v-if="publishedExams.length === 0" class="empty-state">
          <div class="empty-content">
            <InboxOutlined class="empty-icon" />
            <h3>暂无可参加的考试</h3>
            <p>当前没有发布的考试，请稍后再查看</p>
          </div>
        </div>

        <div v-else class="exam-grid">
          <article
            v-for="exam in publishedExams"
            :key="exam.id"
            class="exam-card"
            :class="getCardClass(exam)"
          >
            <!-- 状态指示条 -->
            <div class="card-status-bar" :class="getStatusBarClass(exam)"></div>

            <div class="card-body">
              <!-- 卡片头部 -->
              <div class="card-header">
                <div class="exam-info">
                  <h3 class="exam-name">{{ exam.examName }}</h3>
                  <span v-if="exam.paper" class="paper-name">
                    <FileTextOutlined />
                    {{ exam.paper.paperName }}
                  </span>
                </div>
                <div class="status-badge" :class="getStatusBadgeClass(exam)">
                  {{ getStatusText(exam) }}
                </div>
              </div>

              <!-- 考试信息 -->
              <div class="card-meta">
                <div class="meta-item" v-if="exam.paper">
                  <FormOutlined />
                  <span>{{ exam.paper.questionCount }} 题</span>
                </div>
                <div class="meta-item" v-if="exam.paper">
                  <TrophyOutlined />
                  <span>{{ exam.paper.totalScore }} 分</span>
                </div>
                <div class="meta-item" v-if="exam.durationMin">
                  <FieldTimeOutlined />
                  <span>{{ exam.durationMin }} 分钟</span>
                </div>
              </div>

              <!-- 时间信息 -->
              <div class="card-time">
                <div class="time-row">
                  <CalendarOutlined />
                  <span v-if="exam.startTime">开始：{{ formatDateTime(exam.startTime) }}</span>
                  <span v-else class="time-flexible">随时可考</span>
                </div>
                <div class="time-row" v-if="exam.endTime">
                  <ClockCircleOutlined />
                  <span>结束：{{ formatDateTime(exam.endTime) }}</span>
                </div>
              </div>

              <!-- 操作按钮 -->
              <div class="card-actions">
                <a-button
                  :type="actionButtonType(exam)"
                  :disabled="actionDisabled(exam)"
                  block
                  size="large"
                  class="action-btn"
                  @click="handleAction(exam)"
                >
                  <component :is="getActionIcon(exam)" />
                  {{ actionLabel(exam) }}
                </a-button>
              </div>
            </div>
          </article>
        </div>
      </a-spin>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import {
  FileTextOutlined,
  ClockCircleOutlined,
  TrophyOutlined,
  InboxOutlined,
  ArrowLeftOutlined,
  OrderedListOutlined,
  FormOutlined,
  FieldTimeOutlined,
  CalendarOutlined,
  PlayCircleOutlined,
  EyeOutlined,
  LockOutlined,
  CheckCircleOutlined
} from '@ant-design/icons-vue'
import { useExamStudentStore } from '@/stores/exam/student'
import type { Exam } from '@/stores/exam/types'

const router = useRouter()
const examStore = useExamStudentStore()
const { publishedExams, loading } = storeToRefs(examStore)

const activeExamCount = computed(() => publishedExams.value.filter((exam) => examStore.canEnterExam(exam)).length)
const releasedResultCount = computed(() => publishedExams.value.filter((exam) => examStore.getSubmissionState(exam.id) === 'immediate').length)
const pendingResultCount = computed(() => publishedExams.value.filter((exam) => examStore.getSubmissionState(exam.id) === 'pending_teacher').length)

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

function getStatusText(exam: Exam): string {
  const submissionState = examStore.getSubmissionState(exam.id)
  if (submissionState === 'immediate') return '已出成绩'
  if (submissionState === 'pending_teacher') return '待批改'
  if (!exam.startTime) return isExamExpired(exam) ? '已过期' : '可开始'
  const now = new Date()
  const start = new Date(exam.startTime)
  const end = resolveExamEnd(exam)
  if (now < start) return '未开始'
  if (!end || now <= end) return '进行中'
  return '已过期'
}

function getCardClass(exam: Exam): string {
  const submissionState = examStore.getSubmissionState(exam.id)
  if (submissionState === 'immediate') return 'exam-card--completed'
  if (submissionState === 'pending_teacher') return 'exam-card--pending'
  if (examStore.canEnterExam(exam)) return 'exam-card--available'
  return 'exam-card--disabled'
}

function getStatusBarClass(exam: Exam): string {
  const submissionState = examStore.getSubmissionState(exam.id)
  if (submissionState === 'immediate') return 'status-bar--success'
  if (submissionState === 'pending_teacher') return 'status-bar--warning'
  if (examStore.canEnterExam(exam)) return 'status-bar--primary'
  return 'status-bar--disabled'
}

function getStatusBadgeClass(exam: Exam): string {
  const submissionState = examStore.getSubmissionState(exam.id)
  if (submissionState === 'immediate') return 'badge--success'
  if (submissionState === 'pending_teacher') return 'badge--warning'
  if (examStore.canEnterExam(exam)) return 'badge--primary'
  if (exam.startTime && new Date() < new Date(exam.startTime)) return 'badge--info'
  return 'badge--disabled'
}

function actionLabel(exam: Exam): string {
  const submissionState = examStore.getSubmissionState(exam.id)
  if (submissionState === 'immediate') return '查看成绩'
  if (submissionState === 'pending_teacher') return '查看状态'
  if (examStore.canEnterExam(exam)) return '进入考试'
  if (exam.startTime && new Date() < new Date(exam.startTime)) return '未开始'
  if (isExamExpired(exam)) return '已过期'
  return '不可进入'
}

function getActionIcon(exam: Exam) {
  const submissionState = examStore.getSubmissionState(exam.id)
  if (submissionState === 'immediate') return CheckCircleOutlined
  if (submissionState === 'pending_teacher') return EyeOutlined
  if (examStore.canEnterExam(exam)) return PlayCircleOutlined
  return LockOutlined
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
.student-exam-page {
  min-height: 100vh;
  background: #f8fafc;
  padding: 24px;
}

/* 页面头部 */
.page-header {
  max-width: 1200px;
  margin: 0 auto 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-bottom: 20px;
  border-bottom: 1px solid #e2e8f0;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px;
}

.page-desc {
  color: #64748b;
  margin: 0;
  font-size: 15px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  border-color: #e2e8f0;
  border-radius: 10px;
}

.back-btn:hover {
  color: #1e40af;
  border-color: #1e40af;
}

/* 统计卡片 */
.stats-section {
  max-width: 1200px;
  margin: 0 auto 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
  transition: all 0.25s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.stat-visual {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  font-size: 26px;
}

.stat-card--primary .stat-visual {
  background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%);
  color: #4f46e5;
}

.stat-card--success .stat-visual {
  background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
  color: #16a34a;
}

.stat-card--warning .stat-visual {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #d97706;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  margin-top: 4px;
}

/* 考试列表 */
.exam-list-section {
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

/* 空状态 */
.empty-state {
  padding: 80px 24px;
}

.empty-content {
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  color: #cbd5e1;
  margin-bottom: 20px;
}

.empty-content h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px;
}

.empty-content p {
  color: #64748b;
  margin: 0;
}

/* 考试卡片网格 */
.exam-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

.exam-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
  overflow: hidden;
  transition: all 0.25s ease;
}

.exam-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
}

.exam-card--disabled {
  opacity: 0.7;
}

.exam-card--disabled:hover {
  transform: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
}

/* 状态指示条 */
.card-status-bar {
  height: 4px;
}

.status-bar--primary {
  background: linear-gradient(90deg, #3b82f6 0%, #6366f1 100%);
}

.status-bar--success {
  background: linear-gradient(90deg, #10b981 0%, #22c55e 100%);
}

.status-bar--warning {
  background: linear-gradient(90deg, #f59e0b 0%, #f97316 100%);
}

.status-bar--disabled {
  background: #e2e8f0;
}

.card-body {
  padding: 20px;
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
}

.exam-info {
  flex: 1;
  min-width: 0;
}

.exam-name {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 6px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.paper-name {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
}

.status-badge {
  flex-shrink: 0;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
}

.badge--primary {
  background: #e0e7ff;
  color: #4f46e5;
}

.badge--success {
  background: #dcfce7;
  color: #16a34a;
}

.badge--warning {
  background: #fef3c7;
  color: #d97706;
}

.badge--info {
  background: #e0f2fe;
  color: #0284c7;
}

.badge--disabled {
  background: #f1f5f9;
  color: #94a3b8;
}

/* 考试元信息 */
.card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #475569;
}

/* 时间信息 */
.card-time {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 20px;
}

.time-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #64748b;
}

.time-flexible {
  color: #16a34a;
  font-weight: 500;
}

/* 操作按钮 */
.action-btn {
  height: 48px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.action-btn:not(:disabled)[type="primary"] {
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.25);
}

.action-btn:not(:disabled)[type="primary"]:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.35);
}

/* 响应式 */
@media (max-width: 1024px) {
  .exam-grid {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  }
}

@media (max-width: 768px) {
  .student-exam-page {
    padding: 16px;
  }

  .header-content {
    flex-direction: column;
    gap: 16px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .stat-card {
    padding: 20px;
  }

  .exam-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 24px;
  }

  .stat-value {
    font-size: 28px;
  }

  .exam-name {
    font-size: 16px;
  }
}
</style>
