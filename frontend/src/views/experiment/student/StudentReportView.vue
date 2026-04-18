<template>
  <div class="student-report-page">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="header-info">
        <h1 class="report-title">实验报告</h1>
        <p class="report-meta">
          <span class="meta-item">
            <BookOutlined />
            {{ reportData.experiment.courseName }}
          </span>
          <span class="meta-item">
            <CalendarOutlined />
            {{ reportData.experiment.schedule }}
          </span>
          <span class="meta-item" :class="statusClass">
            <CheckCircleOutlined v-if="reportData.status === 'reviewed'" />
            <ClockCircleOutlined v-else />
            {{ statusText }}
          </span>
        </p>
      </div>
      <div class="header-actions">
        <a-button @click="handlePrint">
          <PrinterOutlined />
          打印报告
        </a-button>
        <a-button v-if="reportData.status === 'reviewed'" type="primary" @click="showFeedbackModal = true">
          <MessageOutlined />
          查看教师反馈
        </a-button>
      </div>
    </header>

    <!-- 报告主体 -->
    <main class="report-content">
      <!-- 报告封面 -->
      <section class="report-cover">
        <div class="school-badge">
          <span class="badge-text">南昌航空大学</span>
        </div>
        <h1 class="cover-title">实验报告</h1>
        <p class="cover-subtitle">{{ reportData.experiment.name }}</p>
        <div class="cover-divider"></div>
        <div class="cover-info">
          <div class="info-row">
            <span class="info-label">课程名称：</span>
            <span class="info-value">{{ reportData.experiment.courseName }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">实验名称：</span>
            <span class="info-value">{{ reportData.experiment.name }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">班 级：</span>
            <span class="info-value">{{ reportData.student.clazzNo }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">学 号：</span>
            <span class="info-value">{{ reportData.student.no }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">姓 名：</span>
            <span class="info-value">{{ reportData.student.name }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">同组人：</span>
            <span class="info-value">-</span>
          </div>
          <div class="info-row">
            <span class="info-label">教师评定：</span>
            <span class="info-value score-highlight">{{ reportData.summary || '-' }} 分</span>
          </div>
          <div class="info-row">
            <span class="info-label">签 名：</span>
            <span class="info-value">________</span>
          </div>
        </div>
        <div class="cover-date">日期：{{ reportData.submittedAt }}</div>
      </section>

      <!-- 实验目的 -->
      <section v-if="reportData.objective" class="report-section">
        <h2 class="section-title">
          <span class="section-number">一、</span>
          实验目的
        </h2>
        <div class="section-content">
          <ol class="purpose-list">
            <li v-for="(item, index) in parsedObjective" :key="index">{{ item }}</li>
          </ol>
        </div>
      </section>

      <!-- 实验内容 -->
      <section v-if="reportData.content" class="report-section">
        <h2 class="section-title">
          <span class="section-number">二、</span>
          实验内容
        </h2>
        <div class="section-content">
          <ol class="content-list">
            <li v-for="(item, index) in parsedContent" :key="index">{{ item }}</li>
          </ol>
        </div>
      </section>

      <!-- 实验步骤与答案 -->
      <section class="report-section">
        <h2 class="section-title">
          <span class="section-number">三、</span>
          实验步骤
        </h2>
        <div class="steps-list">
          <div v-for="(step, index) in reportData.steps" :key="step.id" class="step-item">
            <div class="step-header">
              <span class="step-label">第 {{ step.stepNo }} 步</span>
              <span class="step-title">{{ step.title }}</span>
              <a-tag :color="getTypeColor(step.type)" class="step-type">
                {{ QUESTION_TYPE_NAMES[step.type] }}
              </a-tag>
            </div>
            <div class="step-content">
              <div class="question-content">
                <div class="content-label">题目：</div>
                <div class="content-text" v-html="renderQuestionContent(step)"></div>
              </div>
              <div class="answer-content">
                <div class="content-label">解答：</div>
                <div class="answer-text" v-html="renderAnswerContent(step)"></div>
              </div>
            </div>
            <!-- 已批改则显示得分 -->
            <div v-if="reportData.status === 'reviewed'" class="step-grade">
              <a-tag v-if="step.teacherScore !== undefined" :color="getScoreColor(step.teacherScore, step.score)">
                得分：{{ step.teacherScore }} / {{ step.score }}
              </a-tag>
              <span v-if="step.teacherComment" class="teacher-comment">{{ step.teacherComment }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 实验小结 -->
      <section class="report-section">
        <h2 class="section-title">
          <span class="section-number">四、</span>
          实验小结
        </h2>
        <div class="summary-content">
          {{ reportData.summaryNote || '（未填写）' }}
        </div>
        <div v-if="reportData.status === 'reviewed' && reportData.teacherFeedback" class="feedback-section">
          <h3 class="feedback-title">
            <MessageOutlined />
            教师评语
          </h3>
          <div class="feedback-content">{{ reportData.teacherFeedback }}</div>
        </div>
      </section>

      <!-- 教师签名区 -->
      <section class="signature-section">
        <div class="signature-info">
          <span>指导教师签名：________</span>
          <span>日期：________</span>
        </div>
      </section>
    </main>

    <!-- 教师反馈弹窗 -->
    <a-modal
      v-model:open="showFeedbackModal"
      title="教师反馈"
      :width="600"
      :footer="null"
    >
      <div class="feedback-modal-content">
        <div class="feedback-score">
          <span class="score-label">总分：</span>
          <span class="score-value">{{ reportData.summary || 0 }} 分</span>
        </div>
        <div class="feedback-text">
          <h4>教师评语：</h4>
          <p>{{ reportData.teacherFeedback || '暂无评语' }}</p>
        </div>
        <div v-if="reportData.reviewedAt" class="feedback-time">
          批改时间：{{ reportData.reviewedAt }}
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  BookOutlined,
  CalendarOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  PrinterOutlined,
  MessageOutlined
} from '@ant-design/icons-vue'
import type { ExperimentReport, ReportQuestion } from '@/stores/experiment/types'
import { QUESTION_TYPE_NAMES } from '@/stores/experiment/types'
import { experimentRepository } from '@/stores/experiment/repository'

const route = useRoute()
const router = useRouter()

// 获取当前用户信息
function getCurrentUser() {
  try {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      return JSON.parse(userStr)
    }
  } catch (e) {
    console.error('获取用户信息失败:', e)
  }
  return { id: 1, studentCode: '2023001234', studentName: '张三', classCode: 'CS2301' }
}

// 获取当前登录用户（从正确的 session key 读取）
function getLoginUser(): { id: string | number; studentCode?: string; studentName?: string; name?: string; classCode?: string } | null {
  // 优先从 eduhub.auth.session 获取
  try {
    const sessionStr = localStorage.getItem('eduhub.auth.session')
    if (sessionStr) {
      console.log('[StudentReport] session原始数据:', sessionStr.substring(0, 200))
      const session = JSON.parse(sessionStr)
      console.log('[StudentReport] session解析后:', { id: session?.id, userId: session?.userId, account: session?.account, name: session?.name })
      if (session && (session.id || session.userId)) {
        return {
          id: session.id || session.userId,
          studentCode: session.account || '',
          studentName: session.name || '',
          name: session.name || '',
          classCode: session.classCode || session.className || ''
        }
      }
    }
  } catch (e) {
    console.error('获取登录用户失败:', e)
  }

  // 备用：从 user 获取
  try {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      const user = JSON.parse(userStr)
      console.log('[StudentReport] user数据:', user)
      if (user && user.id) {
        return {
          id: user.id,
          studentCode: user.studentCode || user.account || '',
          studentName: user.studentName || user.name || '',
          name: user.studentName || user.name || '',
          classCode: user.classCode || ''
        }
      }
    }
  } catch (e) {
    console.error('获取 user 失败:', e)
  }

  console.log('[StudentReport] 无法获取用户信息，返回null')
  return null
}

// 页面状态
const showFeedbackModal = ref(false)
const isLoading = ref(true)

// 报告数据
const reportData = ref<ExperimentReport>({
  student: {
    id: '',
    no: '',
    name: '',
    clazzNo: ''
  },
  experiment: {
    id: '',
    name: '',
    courseName: '',
    schedule: ''
  },
  summary: '',
  objective: '',
  content: '',
  steps: [],
  summaryNote: '',
  submittedAt: '',
  status: 'pending'
})

// 计算属性
const statusClass = computed(() => {
  if (reportData.value.status === 'reviewed') return 'status--reviewed'
  if (reportData.value.status === 'submitted') return 'status--submitted'
  return 'status--pending'
})

const statusText = computed(() => {
  if (reportData.value.status === 'reviewed') return '已批改'
  if (reportData.value.status === 'submitted') return '待批改'
  return '未提交'
})

const parsedObjective = computed(() => {
  if (!reportData.value.objective) return []
  return reportData.value.objective.split(/\n|；|;/).filter(Boolean)
})

const parsedContent = computed(() => {
  if (!reportData.value.content) return []
  return reportData.value.content.split(/\n|；|;/).filter(Boolean)
})

// 方法
function getTypeColor(type: number): string {
  const colors: Record<number, string> = {
    1: 'blue',
    2: 'green',
    3: 'cyan',
    4: 'orange',
    5: 'purple',
    6: 'red',
    7: 'magenta'
  }
  return colors[type] || 'default'
}

function getScoreColor(score: number, total: number): string {
  const ratio = score / total
  if (ratio >= 0.9) return 'success'
  if (ratio >= 0.7) return 'processing'
  if (ratio >= 0.6) return 'warning'
  return 'error'
}

function renderQuestionContent(step: ReportQuestion): string {
  if (step.type === 1) {
    return step.content.replace(/____/g, '<span class="blank-line">________</span>')
  }
  return step.content
}

function renderAnswerContent(step: ReportQuestion): string {
  if (step.type === 1 && step.filledBlanks?.length) {
    return step.filledBlanks.map((b, i) => `(${i + 1}) ${b}`).join('&nbsp;&nbsp;&nbsp;&nbsp;')
  }
  return step.studentAnswer || '（未作答）'
}

function handlePrint() {
  window.print()
}

// 加载报告数据
async function loadReport() {
  isLoading.value = true
  try {
    const experimentId = String(route.params.id)
    // 优先从登录 session 获取用户信息
    const loginUser = getLoginUser()
    const currentUser = loginUser || getCurrentUser()
    const studentId = String(currentUser.id || 1)

    console.log('[StudentReport] 加载报告:', { experimentId, studentId, loginUser: !!loginUser })

    // 调用真实 API 获取报告数据
    const report = await experimentRepository.getStudentReport(experimentId, studentId)

    if (report) {
      reportData.value = report
    } else {
      // 如果没有报告数据，使用空模板
      reportData.value = {
        student: {
          id: studentId,
          no: currentUser.studentCode || '',
          name: currentUser.studentName || '',
          clazzNo: currentUser.classCode ? `${currentUser.classCode}班` : ''
        },
        experiment: {
          id: experimentId,
          name: '实验报告',
          courseName: '',
          schedule: ''
        },
        summary: '',
        objective: '',
        content: '',
        steps: [],
        summaryNote: '',
        submittedAt: '',
        status: 'pending'
      }

      // 尝试获取实验信息填充
      const experiment = await experimentRepository.getStudentExperimentById(experimentId)
      if (experiment) {
        reportData.value.experiment.name = experiment.title
        reportData.value.experiment.courseName = experiment.topicLabel
        reportData.value.objective = experiment.objective || ''
        reportData.value.content = experiment.summary || ''

        // 获取题目并构建步骤
        const questions = await experimentRepository.getExperimentQuestions(experimentId)
        if (questions.length > 0) {
          reportData.value.steps = questions.map((q, index) => ({
            id: q.id,
            experimentItemId: q.experimentItemId,
            stepNo: index + 1,
            title: q.title,
            type: q.type,
            content: q.content,
            score: q.score,
            options: q.options,
            studentAnswer: '',
            filledBlanks: [],
            teacherScore: undefined,
            teacherComment: undefined
          }))
        }
      }
    }
  } catch (error) {
    message.error('加载报告失败')
    console.error(error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  loadReport()
})
</script>

<style scoped>
.student-report-page {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 页面头部 */
.page-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.report-title {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
}

.report-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  margin: 0;
  font-size: 14px;
  color: #8c8c8c;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.status--reviewed {
  color: #52c41a;
}

.status--submitted {
  color: #fa8c16;
}

.status--pending {
  color: #8c8c8c;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 报告主体 */
.report-content {
  max-width: 900px;
  margin: 24px auto;
  padding: 0 24px;
}

/* 报告封面 */
.report-cover {
  padding: 48px;
  background: #fff;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.school-badge {
  display: inline-block;
  padding: 8px 24px;
  border: 2px solid #1890ff;
  border-radius: 4px;
  margin-bottom: 24px;
}

.badge-text {
  font-size: 14px;
  color: #1890ff;
  font-weight: 600;
  letter-spacing: 0.1em;
}

.cover-title {
  margin: 0 0 8px;
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
}

.cover-subtitle {
  margin: 0 0 24px;
  font-size: 18px;
  color: #595959;
}

.cover-divider {
  width: 100px;
  height: 2px;
  background: #1890ff;
  margin: 0 auto 32px;
}

.cover-info {
  text-align: left;
  display: inline-block;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 15px;
  line-height: 1.8;
}

.info-label {
  width: 100px;
  color: #595959;
}

.info-value {
  color: #262626;
  min-width: 200px;
}

.score-highlight {
  color: #1890ff;
  font-weight: 600;
}

.cover-date {
  margin-top: 24px;
  text-align: right;
  color: #8c8c8c;
  font-size: 14px;
}

/* 报告章节 */
.report-section {
  background: #fff;
  border-radius: 8px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-title {
  display: flex;
  align-items: center;
  margin: 0 0 20px;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  border-bottom: 2px solid #1890ff;
  padding-bottom: 12px;
}

.section-number {
  color: #1890ff;
  margin-right: 8px;
}

.section-content {
  padding-left: 24px;
}

.purpose-list,
.content-list {
  margin: 0;
  padding-left: 24px;
}

.purpose-list li,
.content-list li {
  margin-bottom: 12px;
  font-size: 15px;
  line-height: 1.8;
  color: #595959;
}

/* 步骤列表 */
.steps-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.step-item {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
}

.step-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
}

.step-label {
  font-weight: 600;
  color: #1890ff;
}

.step-title {
  flex: 1;
  font-weight: 500;
  color: #262626;
}

.step-type {
  margin: 0;
}

.step-content {
  padding: 16px;
}

.content-label {
  font-weight: 500;
  color: #595959;
  margin-bottom: 8px;
}

.content-text {
  margin-bottom: 16px;
  font-size: 15px;
  line-height: 1.8;
  color: #262626;
}

:deep(.blank-line) {
  display: inline-block;
  border-bottom: 2px solid #1890ff;
  color: transparent;
  min-width: 80px;
}

.answer-content {
  padding-left: 16px;
  border-left: 3px solid #e8e8e8;
}

.answer-text {
  font-size: 15px;
  line-height: 1.8;
  color: #262626;
  white-space: pre-wrap;
}

/* 步骤得分 */
.step-grade {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f6ffed;
  border-top: 1px solid #b7eb8f;
}

.teacher-comment {
  font-size: 13px;
  color: #8c8c8c;
  font-style: italic;
}

/* 实验小结 */
.summary-content {
  font-size: 15px;
  line-height: 1.8;
  color: #595959;
  text-indent: 2em;
}

.feedback-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px dashed #e8e8e8;
}

.feedback-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
  color: #1890ff;
}

.feedback-content {
  font-size: 15px;
  line-height: 1.8;
  color: #595959;
  padding: 16px;
  background: #f6ffed;
  border-radius: 8px;
  border: 1px solid #b7eb8f;
}

/* 签名区 */
.signature-section {
  padding: 32px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.signature-info {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #595959;
}

/* 反馈弹窗 */
.feedback-modal-content {
  padding: 8px 0;
}

.feedback-score {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #e6f7ff;
  border-radius: 8px;
  margin-bottom: 20px;
}

.score-label {
  font-size: 14px;
  color: #595959;
}

.score-value {
  font-size: 24px;
  font-weight: 700;
  color: #1890ff;
}

.feedback-text h4 {
  margin: 0 0 8px;
  font-size: 14px;
  color: #595959;
}

.feedback-text p {
  margin: 0;
  font-size: 15px;
  line-height: 1.8;
  color: #262626;
}

.feedback-time {
  margin-top: 16px;
  font-size: 13px;
  color: #8c8c8c;
  text-align: right;
}

/* 打印样式 */
@media print {
  .page-header,
  .header-actions {
    display: none;
  }

  .student-report-page {
    background: #fff;
  }

  .report-content {
    max-width: 100%;
    padding: 0;
  }

  .report-section,
  .report-cover,
  .signature-section {
    box-shadow: none;
    border-radius: 0;
  }
}
</style>
