<template>
  <div class="exam-result-page">
    <!-- 返回导航 -->
    <header class="result-header">
      <a-button type="text" class="back-btn" @click="router.push('/exams')">
        <ArrowLeftOutlined />
        <span>返回考试列表</span>
      </a-button>
    </header>

    <div v-if="!submission" class="empty-state">
      <div class="empty-content">
        <FileSearchOutlined class="empty-icon" />
        <h3>未找到考试记录</h3>
        <p>当前浏览器没有找到该考试的提交记录</p>
        <a-button type="primary" @click="router.push('/exams')">返回考试列表</a-button>
      </div>
    </div>

    <template v-else>
      <!-- 成绩概览卡片 -->
      <section class="score-hero" :class="{ 'score-hero--pending': submission.releaseMode !== 'immediate' }">
        <div class="score-hero__content">
          <!-- 左侧成绩展示 -->
          <div class="score-display">
            <template v-if="submission.releaseMode === 'immediate'">
              <div class="score-ring">
                <svg viewBox="0 0 120 120" class="ring-svg">
                  <circle cx="60" cy="60" r="54" class="ring-bg" />
                  <circle cx="60" cy="60" r="54" class="ring-progress" :style="{ strokeDashoffset: progressOffset }" />
                </svg>
                <div class="score-value">
                  <span class="score-num">{{ submission.finalScore }}</span>
                  <span class="score-unit">分</span>
                </div>
              </div>
              <div class="score-label">
                <span class="label-text">满分 {{ submission.totalScore }} 分</span>
                <span class="label-percent">得分率 {{ scorePercent }}%</span>
              </div>
            </template>
            <template v-else>
              <div class="pending-indicator">
                <ClockCircleOutlined class="pending-icon" />
              </div>
              <div class="score-label">
                <span class="label-text">成绩待发布</span>
              </div>
            </template>
          </div>

          <!-- 右侧信息 -->
          <div class="score-info">
            <h1 class="exam-name">{{ submission?.examName || '考试结果' }}</h1>
            <div class="info-badges">
              <span class="info-badge">
                <FileTextOutlined />
                {{ submission.paper.paperName }}
              </span>
              <span class="info-badge">
                <FormOutlined />
                {{ submission.paper.questionCount }} 题
              </span>
              <span class="info-badge" v-if="submission.submittedAt">
                <CalendarOutlined />
                {{ formatTime(submission.submittedAt) }}
              </span>
            </div>
            <div class="status-tag" :class="statusClass">
              <component :is="statusIcon" />
              {{ statusText }}
            </div>
          </div>
        </div>
      </section>

      <!-- 统计卡片 -->
      <section v-if="submission.releaseMode === 'immediate'" class="stats-section">
        <div class="stats-grid">
          <article class="stat-card stat-card--correct">
            <div class="stat-icon"><CheckCircleOutlined /></div>
            <div class="stat-content">
              <span class="stat-value">{{ correctCount }}</span>
              <span class="stat-label">正确题数</span>
            </div>
          </article>
          <article class="stat-card stat-card--wrong">
            <div class="stat-icon"><CloseCircleOutlined /></div>
            <div class="stat-content">
              <span class="stat-value">{{ wrongCount }}</span>
              <span class="stat-label">错误题数</span>
            </div>
          </article>
          <article class="stat-card stat-card--partial">
            <div class="stat-icon"><MinusCircleOutlined /></div>
            <div class="stat-content">
              <span class="stat-value">{{ partialCount }}</span>
              <span class="stat-label">部分得分</span>
            </div>
          </article>
          <article class="stat-card stat-card--accuracy">
            <div class="stat-icon"><PieChartOutlined /></div>
            <div class="stat-content">
              <span class="stat-value">{{ accuracyRate }}%</span>
              <span class="stat-label">正确率</span>
            </div>
          </article>
        </div>
      </section>

      <!-- 题目详情 -->
      <section v-if="submission.releaseMode === 'immediate'" class="detail-section">
        <div class="section-header">
          <h2 class="section-title">
            <OrderedListOutlined />
            答题详情
          </h2>
          <div class="filter-tabs">
            <button class="filter-tab" :class="{ 'filter-tab--active': filterType === 'all' }" @click="filterType = 'all'">
              全部
            </button>
            <button class="filter-tab" :class="{ 'filter-tab--active': filterType === 'wrong' }" @click="filterType = 'wrong'">
              错误
            </button>
            <button class="filter-tab" :class="{ 'filter-tab--active': filterType === 'correct' }" @click="filterType = 'correct'">
              正确
            </button>
          </div>
        </div>

        <div class="question-list">
          <article 
            v-for="(question, index) in filteredQuestions" 
            :key="question.id" 
            class="question-card"
            :class="getQuestionClass(question.questionId)"
          >
            <div class="question-header">
              <div class="question-number">
                <span class="number-badge">{{ getQuestionIndex(question.id) }}</span>
                <span class="question-type">{{ getQuestionTypeName(question.question?.questionType) }}</span>
              </div>
              <div class="question-score-display">
                <span class="earned-score">{{ getEarnedScore(question.questionId) }}</span>
                <span class="max-score">/ {{ question.score }} 分</span>
              </div>
            </div>

            <div class="question-content">
              <p class="question-text">{{ question.questionContent || question.question?.questionContent }}</p>
            </div>

            <div class="answer-comparison">
              <div class="answer-block answer-block--yours">
                <div class="answer-label">
                  <UserOutlined />
                  你的答案
                </div>
                <div class="answer-value" :class="{ 'answer-value--empty': !hasAnswer(question.questionId) }">
                  {{ formatAnswer(submission.answers[question.questionId]) || '未作答' }}
                </div>
              </div>
              <div class="answer-block answer-block--correct">
                <div class="answer-label">
                  <CheckOutlined />
                  参考答案
                </div>
                <div class="answer-value">
                  {{ formatCorrectAnswer(question.question) || '暂无' }}
                </div>
              </div>
            </div>

            <div class="result-indicator" :class="getResultClass(question.questionId)">
              <component :is="getResultIcon(question.questionId)" />
              <span>{{ getResultText(question.questionId) }}</span>
            </div>
          </article>
        </div>
      </section>

      <!-- 待批改状态 -->
      <section v-else class="pending-section">
        <div class="pending-content">
          <div class="pending-visual">
            <div class="pending-animation">
              <div class="pulse-ring"></div>
              <div class="pulse-ring pulse-ring--delay"></div>
              <EditOutlined class="pending-main-icon" />
            </div>
          </div>
          <h2 class="pending-title">试卷正在批改中</h2>
          <p class="pending-desc">
            当前试卷包含主观题（如简答、编程等），需要教师手动批改。<br />
            系统已记录您的作答内容，批改完成后可在此页面查看最终成绩。
          </p>
          <div class="pending-tips">
            <div class="tip-item">
              <HistoryOutlined />
              <span>您可以稍后返回查看批改结果</span>
            </div>
            <div class="tip-item">
              <BellOutlined />
              <span>批改完成后会更新成绩状态</span>
            </div>
          </div>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { useExamStudentStore } from '@/stores/exam/student'
import { QUESTION_TYPE_MAP, type QuestionType } from '@/stores/exam/types'
import {
  ArrowLeftOutlined,
  FileSearchOutlined,
  FileTextOutlined,
  FormOutlined,
  CalendarOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  MinusCircleOutlined,
  PieChartOutlined,
  ClockCircleOutlined,
  OrderedListOutlined,
  UserOutlined,
  CheckOutlined,
  EditOutlined,
  HistoryOutlined,
  BellOutlined
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const examStore = useExamStudentStore()
const examId = computed(() => Number(route.params.id))
const submission = computed(() => examStore.currentSubmission)

const filterType = ref<'all' | 'correct' | 'wrong'>('all')

// 计算属性
const scorePercent = computed(() => {
  if (!submission.value) return 0
  return Math.round((submission.value.finalScore / submission.value.totalScore) * 100)
})

const progressOffset = computed(() => {
  const circumference = 2 * Math.PI * 54
  return circumference - (scorePercent.value / 100) * circumference
})

const statusClass = computed(() => {
  if (!submission.value) return ''
  return submission.value.releaseMode === 'immediate' ? 'status-tag--released' : 'status-tag--pending'
})

const statusText = computed(() => {
  if (!submission.value) return ''
  if (submission.value.releaseMode === 'immediate') {
    return submission.value.releaseSource === 'auto' ? '自动判分完成' : '教师批改完成'
  }
  return '待教师批改'
})

const statusIcon = computed(() => {
  return submission.value?.releaseMode === 'immediate' ? CheckCircleOutlined : ClockCircleOutlined
})

const correctCount = computed(() => {
  if (!submission.value) return 0
  return Object.values(submission.value.questionScores).filter(s => s.earned === s.max && s.max > 0).length
})

const wrongCount = computed(() => {
  if (!submission.value) return 0
  return Object.values(submission.value.questionScores).filter(s => s.earned === 0).length
})

const partialCount = computed(() => {
  if (!submission.value) return 0
  return Object.values(submission.value.questionScores).filter(s => s.earned > 0 && s.earned < s.max).length
})

const accuracyRate = computed(() => {
  if (!submission.value) return 0
  const total = Object.keys(submission.value.questionScores).length
  if (total === 0) return 0
  return Math.round((correctCount.value / total) * 100)
})

const filteredQuestions = computed(() => {
  if (!submission.value?.paper?.questions) return []
  const questions = submission.value.paper.questions
  if (filterType.value === 'all') return questions
  return questions.filter(q => {
    const isCorrect = isQuestionCorrect(q.questionId)
    return filterType.value === 'correct' ? isCorrect : !isCorrect
  })
})

// 方法
function isQuestionCorrect(questionId: number): boolean {
  const detail = submission.value?.questionScores[questionId]
  if (!detail) return false
  return detail.earned === detail.max && detail.max > 0
}

function getQuestionIndex(id: number): number {
  if (!submission.value?.paper?.questions) return 0
  return submission.value.paper.questions.findIndex(q => q.id === id) + 1
}

function getQuestionTypeName(type?: QuestionType): string {
  return type !== undefined ? QUESTION_TYPE_MAP[type] || '未知' : '未知'
}

function getEarnedScore(questionId: number): number {
  return submission.value?.questionScores[questionId]?.earned ?? 0
}

function hasAnswer(questionId: number): boolean {
  const ans = submission.value?.answers[questionId]
  if (!ans) return false
  if (typeof ans === 'string') return ans.trim().length > 0
  return Array.isArray(ans) && ans.length > 0
}

function formatAnswer(answer: string | string[] | undefined): string {
  if (!answer) return ''
  if (Array.isArray(answer)) return answer.join('、')
  return answer
}

function formatCorrectAnswer(question: any): string {
  if (!question) return ''
  // 根据题目类型返回正确答案
  if (question.standardAnswer) return question.standardAnswer
  if (question.correctAnswer) return question.correctAnswer
  // 主观题暂不显示参考答案
  if (question.questionType === 5 || question.questionType === 6 || question.questionType === 7) {
    return '主观题，请参考教师批改'
  }
  return '暂无'
}

function formatTime(dateStr: string): string {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', { 
    month: '2-digit', 
    day: '2-digit', 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

function getQuestionClass(questionId: number): string {
  const detail = submission.value?.questionScores[questionId]
  if (!detail) return ''
  if (detail.earned === detail.max && detail.max > 0) return 'question-card--correct'
  if (detail.earned === 0) return 'question-card--wrong'
  return 'question-card--partial'
}

function getResultClass(questionId: number): string {
  const detail = submission.value?.questionScores[questionId]
  if (!detail) return ''
  if (detail.earned === detail.max && detail.max > 0) return 'result-indicator--correct'
  if (detail.earned === 0) return 'result-indicator--wrong'
  return 'result-indicator--partial'
}

function getResultIcon(questionId: number) {
  const detail = submission.value?.questionScores[questionId]
  if (!detail) return MinusCircleOutlined
  if (detail.earned === detail.max && detail.max > 0) return CheckCircleOutlined
  if (detail.earned === 0) return CloseCircleOutlined
  return MinusCircleOutlined
}

function getResultText(questionId: number): string {
  const detail = submission.value?.questionScores[questionId]
  if (!detail) return '未知'
  if (detail.earned === detail.max && detail.max > 0) return '回答正确'
  if (detail.earned === 0) return '回答错误'
  return '部分得分'
}

onMounted(async () => {
  try {
    await examStore.loadSubmissionResult(examId.value)
  } catch {
    message.error('刷新考试结果失败')
  }
})
</script>

<style scoped>
.exam-result-page {
  min-height: 100vh;
  background: #f8fafc;
  padding: 24px;
}

/* 头部导航 */
.result-header {
  max-width: 960px;
  margin: 0 auto 24px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 10px;
  transition: all 0.2s;
}

.back-btn:hover {
  color: #1e40af;
  background: #e0e7ff;
}

/* 空状态 */
.empty-state {
  max-width: 960px;
  margin: 0 auto;
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
  margin: 0 0 24px;
}

/* 成绩概览卡片 */
.score-hero {
  max-width: 960px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, #1e40af 0%, #3b82f6 50%, #0ea5e9 100%);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(30, 64, 175, 0.2);
  position: relative;
  overflow: hidden;
}

.score-hero::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -30%;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  pointer-events: none;
}

.score-hero--pending {
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%);
  box-shadow: 0 20px 40px rgba(245, 158, 11, 0.2);
}

.score-hero__content {
  display: flex;
  align-items: center;
  gap: 48px;
  position: relative;
  z-index: 1;
}

/* 成绩环形展示 */
.score-display {
  text-align: center;
}

.score-ring {
  position: relative;
  width: 140px;
  height: 140px;
  margin: 0 auto 16px;
}

.ring-svg {
  width: 100%;
  height: 100%;
  transform: rotate(-90deg);
}

.ring-bg {
  fill: none;
  stroke: rgba(255, 255, 255, 0.2);
  stroke-width: 8;
}

.ring-progress {
  fill: none;
  stroke: #fff;
  stroke-width: 8;
  stroke-linecap: round;
  stroke-dasharray: 339.292;
  transition: stroke-dashoffset 1s ease-out;
}

.score-value {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2px;
}

.score-num {
  font-size: 48px;
  font-weight: 800;
  color: #fff;
  line-height: 1;
}

.score-unit {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.8);
  align-self: flex-end;
  margin-bottom: 8px;
}

.score-label {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.label-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.label-percent {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.pending-indicator {
  width: 140px;
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  margin: 0 auto 16px;
}

.pending-icon {
  font-size: 56px;
  color: #fff;
}

/* 考试信息 */
.score-info {
  flex: 1;
}

.exam-name {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 16px;
  line-height: 1.3;
}

.info-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
}

.info-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 999px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
}

.status-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
}

.status-tag--released {
  background: rgba(255, 255, 255, 0.95);
  color: #059669;
}

.status-tag--pending {
  background: rgba(255, 255, 255, 0.95);
  color: #d97706;
}

/* 统计卡片 */
.stats-section {
  max-width: 960px;
  margin: 0 auto 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
}

.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 22px;
}

.stat-card--correct .stat-icon {
  background: #dcfce7;
  color: #16a34a;
}

.stat-card--wrong .stat-icon {
  background: #fee2e2;
  color: #dc2626;
}

.stat-card--partial .stat-icon {
  background: #fef3c7;
  color: #d97706;
}

.stat-card--accuracy .stat-icon {
  background: #e0e7ff;
  color: #4f46e5;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
}

/* 题目详情 */
.detail-section {
  max-width: 960px;
  margin: 0 auto;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.filter-tabs {
  display: flex;
  gap: 8px;
  padding: 4px;
  background: #f1f5f9;
  border-radius: 10px;
}

.filter-tab {
  padding: 8px 16px;
  background: transparent;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-tab:hover {
  color: #1e293b;
}

.filter-tab--active {
  background: #fff;
  color: #1e40af;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 题目卡片 */
.question-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.question-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
  position: relative;
  overflow: hidden;
}

.question-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
}

.question-card--correct::before {
  background: #16a34a;
}

.question-card--wrong::before {
  background: #dc2626;
}

.question-card--partial::before {
  background: #d97706;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.question-number {
  display: flex;
  align-items: center;
  gap: 12px;
}

.number-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: #f1f5f9;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.question-type {
  padding: 4px 10px;
  background: #e0e7ff;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  color: #4f46e5;
}

.question-score-display {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.earned-score {
  font-size: 24px;
  font-weight: 700;
  color: #1e40af;
}

.max-score {
  font-size: 14px;
  color: #64748b;
}

.question-content {
  margin-bottom: 20px;
}

.question-text {
  font-size: 15px;
  color: #334155;
  line-height: 1.8;
  margin: 0;
  white-space: pre-wrap;
}

/* 答案对比 */
.answer-comparison {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.answer-block {
  padding: 16px;
  border-radius: 12px;
}

.answer-block--yours {
  background: #fef3c7;
  border: 1px solid #fcd34d;
}

.answer-block--correct {
  background: #dcfce7;
  border: 1px solid #86efac;
}

.answer-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 8px;
}

.answer-block--yours .answer-label {
  color: #92400e;
}

.answer-block--correct .answer-label {
  color: #166534;
}

.answer-value {
  font-size: 14px;
  color: #1e293b;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.answer-value--empty {
  color: #94a3b8;
  font-style: italic;
}

/* 结果指示器 */
.result-indicator {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
}

.result-indicator--correct {
  background: #dcfce7;
  color: #16a34a;
}

.result-indicator--wrong {
  background: #fee2e2;
  color: #dc2626;
}

.result-indicator--partial {
  background: #fef3c7;
  color: #d97706;
}

/* 待批改状态 */
.pending-section {
  max-width: 960px;
  margin: 0 auto;
  background: #fff;
  border-radius: 24px;
  padding: 60px 40px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
  text-align: center;
}

.pending-visual {
  margin-bottom: 32px;
}

.pending-animation {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pulse-ring {
  position: absolute;
  inset: 0;
  border: 3px solid #f59e0b;
  border-radius: 50%;
  animation: pulse 2s ease-out infinite;
}

.pulse-ring--delay {
  animation-delay: 1s;
}

@keyframes pulse {
  0% { transform: scale(0.8); opacity: 1; }
  100% { transform: scale(1.5); opacity: 0; }
}

.pending-main-icon {
  font-size: 48px;
  color: #f59e0b;
}

.pending-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 12px;
}

.pending-desc {
  font-size: 15px;
  color: #64748b;
  line-height: 1.8;
  margin: 0 0 32px;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
}

.pending-tips {
  display: flex;
  justify-content: center;
  gap: 32px;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  font-size: 14px;
}

/* 响应式 */
@media (max-width: 768px) {
  .exam-result-page {
    padding: 16px;
  }

  .score-hero {
    padding: 28px;
    border-radius: 20px;
  }

  .score-hero__content {
    flex-direction: column;
    gap: 24px;
    text-align: center;
  }

  .score-info {
    text-align: center;
  }

  .info-badges {
    justify-content: center;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .answer-comparison {
    grid-template-columns: 1fr;
  }

  .pending-tips {
    flex-direction: column;
    gap: 16px;
  }
}

@media (max-width: 480px) {
  .score-ring {
    width: 120px;
    height: 120px;
  }

  .score-num {
    font-size: 40px;
  }

  .exam-name {
    font-size: 22px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .question-card {
    padding: 20px;
  }
}
</style>
