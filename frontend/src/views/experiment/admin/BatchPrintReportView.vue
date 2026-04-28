<template>
  <div class="batch-print-page">
    <!-- 操作栏：打印时隐藏 -->
    <header class="print-header no-print">
      <div class="header-info">
        <h1 class="page-title">按班打印实验报告</h1>
        <p class="page-meta">
          <span v-if="experimentInfo.name">
            <BookOutlined /> {{ experimentInfo.name }}
          </span>
          <span v-if="clazzNo">
            <TeamOutlined /> {{ clazzNo }}
          </span>
          <span>
            <FileTextOutlined /> 共 {{ reports.length }} 份报告
          </span>
        </p>
      </div>
      <div class="header-actions">
        <a-button @click="router.push('/admin/experiments/reports')">
          <LeftOutlined /> 返回列表
        </a-button>
        <a-button type="primary" @click="handlePrint" :disabled="reports.length === 0">
          <PrinterOutlined /> 打印全部
        </a-button>
      </div>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container no-print">
      <a-spin size="large" tip="正在加载报告数据..." />
    </div>

    <!-- 空状态 -->
    <div v-else-if="reports.length === 0" class="empty-container no-print">
      <a-empty description="暂无已提交的实验报告" />
    </div>

    <!-- 报告列表（打印内容） -->
    <div v-else class="reports-container">
      <div
        v-for="(report, reportIndex) in reports"
        :key="report.studentId"
        class="report-wrapper"
        :class="{ 'page-break': reportIndex > 0 }"
      >
        <!-- 报告封面 -->
        <section class="report-cover">
          <div class="school-badge">
            <span class="badge-text">南昌航空大学</span>
          </div>
          <h1 class="cover-title">实验报告</h1>
          <p class="cover-subtitle">{{ report.experimentName }}</p>
          <div class="cover-divider"></div>
          <div class="cover-info">
            <div class="info-row">
              <span class="info-label">课程名称：</span>
              <span class="info-value">{{ report.courseName || experimentInfo.courseName || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">实验名称：</span>
              <span class="info-value">{{ report.experimentName }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">班 级：</span>
              <span class="info-value">{{ report.clazzNo }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">学 号：</span>
              <span class="info-value">{{ report.studentNo }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">姓 名：</span>
              <span class="info-value">{{ report.studentName }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">同组人：</span>
              <span class="info-value">-</span>
            </div>
            <div class="info-row">
              <span class="info-label">教师评定：</span>
              <span class="info-value score-highlight">{{ report.totalScore ?? '-' }} 分</span>
            </div>
            <div class="info-row">
              <span class="info-label">签 名：</span>
              <span class="info-value">________</span>
            </div>
          </div>
          <div class="cover-date">日期：{{ report.submittedAt || '-' }}</div>
        </section>

        <!-- 实验目的 -->
        <section v-if="report.objective" class="report-section">
          <h2 class="section-title"><span class="section-number">一、</span>实验目的</h2>
          <div class="section-content">
            <ol class="purpose-list">
              <li v-for="(item, idx) in parseList(report.objective)" :key="idx">{{ item }}</li>
            </ol>
          </div>
        </section>

        <!-- 实验内容 -->
        <section v-if="report.content" class="report-section">
          <h2 class="section-title"><span class="section-number">二、</span>实验内容</h2>
          <div class="section-content">
            <ol class="content-list">
              <li v-for="(item, idx) in parseList(report.content)" :key="idx">{{ item }}</li>
            </ol>
          </div>
        </section>

        <!-- 实验步骤 -->
        <section class="report-section">
          <h2 class="section-title"><span class="section-number">三、</span>实验步骤</h2>
          <div class="steps-list">
            <div v-for="(step, stepIndex) in report.steps" :key="step.id" class="step-item">
              <div class="step-header">
                <span class="step-label">第 {{ stepIndex + 1 }} 步</span>
                <span class="step-title">{{ step.title }}</span>
                <span class="step-type-tag" :style="{ background: getTypeColor(step.type) }">
                  {{ QUESTION_TYPE_NAMES[step.type] || '题目' }}
                </span>
              </div>
              <div class="step-content">
                <div class="question-content">
                  <div class="content-label">题目：</div>
                  <div class="content-text" v-html="renderQuestionContent(step)"></div>
                </div>
                <div class="answer-content">
                  <div class="content-label">解答：</div>
                  <div v-if="step.type === 3" class="answer-code-block">
                    <div class="code-block-header">
                      <span class="language-tag">{{ step.language || '代码' }}</span>
                    </div>
                    <pre class="answer-code-pre">{{ step.studentAnswer || '（未作答）' }}</pre>
                  </div>
                  <div v-else class="answer-text">{{ step.studentAnswer || '（未作答）' }}</div>
                </div>
              </div>
              <div v-if="step.teacherScore !== undefined" class="step-grade">
                <span class="grade-tag">得分：{{ step.teacherScore }} / {{ step.score }}</span>
                <span v-if="step.teacherComment" class="teacher-comment">{{ step.teacherComment }}</span>
              </div>
            </div>
          </div>
        </section>

        <!-- 实验小结 -->
        <section class="report-section">
          <h2 class="section-title"><span class="section-number">四、</span>实验小结</h2>
          <div class="summary-content">{{ report.summaryNote || '（未填写）' }}</div>
          <div v-if="report.teacherFeedback" class="feedback-section">
            <h3 class="feedback-title">教师评语</h3>
            <div class="feedback-content">{{ report.teacherFeedback }}</div>
          </div>
        </section>

        <!-- 签名区 -->
        <section class="signature-section">
          <div class="signature-info">
            <span>指导教师签名：________</span>
            <span>日期：________</span>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  BookOutlined,
  FileTextOutlined,
  LeftOutlined,
  PrinterOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'
import { getTeacherReportListPage } from '@/api/eduExperimentReportController'
import { experimentRepository } from '@/stores/experiment/repository'
import { QUESTION_TYPE_NAMES } from '@/stores/experiment/types'

interface ReportStep {
  id: string
  title: string
  type: number
  content: string
  score: number
  studentAnswer?: string
  teacherScore?: number
  teacherComment?: string
  language?: string
}

interface ReportData {
  studentId: string
  studentNo: string
  studentName: string
  clazzNo: string
  experimentId: string
  experimentName: string
  courseName?: string
  objective?: string
  content?: string
  steps: ReportStep[]
  summaryNote?: string
  totalScore?: number
  teacherFeedback?: string
  submittedAt?: string
}

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const experimentId = String(route.params.id || '')
const clazzNo = String(route.query.clazz || '')
const reports = ref<ReportData[]>([])
const experimentInfo = ref<{ name: string; courseName: string }>({ name: '', courseName: '' })

function parseList(text?: string): string[] {
  if (!text) return []
  return text.split(/\n|；|;/).filter(Boolean)
}

function getTypeColor(type: number): string {
  const colors: Record<number, string> = {
    1: '#1890ff',
    2: '#52c41a',
    3: '#13c2c2',
    4: '#fa8c16',
    5: '#722ed1',
    6: '#f5222d',
    7: '#eb2f96'
  }
  return colors[type] || '#8c8c8c'
}

function renderQuestionContent(step: ReportStep): string {
  if (step.type === 1) {
    return step.content.replace(/____/g, '<span class="blank-line">________</span>')
  }
  return step.content
}

function handlePrint() {
  window.print()
}

async function loadReports() {
  loading.value = true
  try {
    // 获取实验基本信息
    const experiment = await experimentRepository.getAdminExperimentById(experimentId)
    if (experiment) {
      experimentInfo.value = {
        name: experiment.title,
        courseName: experiment.topicLabel || ''
      }
    }

    // 获取报告列表
    const res = await getTeacherReportListPage({
      experimentId,
      clazzNo: clazzNo || undefined,
      current: 1,
      pageSize: 200
    })

    const page = res.data?.data
    if (!page?.records) {
      reports.value = []
      return
    }

    // 只处理已提交的报告
    const submittedRecords = page.records.filter((r: any) =>
      ['submitted', 'reviewed'].includes(r.status)
    )

    // 逐个获取完整报告数据
    const fullReports: ReportData[] = []
    for (const record of submittedRecords) {
      try {
        const report = await experimentRepository.getStudentReport(
          String(record.experimentId),
          String(record.studentId)
        )
        if (report) {
          fullReports.push({
            studentId: String(record.studentId),
            studentNo: record.studentNo || '-',
            studentName: record.studentName || '-',
            clazzNo: record.clazzNo || '-',
            experimentId: String(record.experimentId),
            experimentName: report.experiment?.name || experimentInfo.value.name,
            courseName: report.experiment?.courseName || experimentInfo.value.courseName,
            objective: report.objective,
            content: report.content,
            steps: report.steps.map((s: any) => ({
              id: s.id,
              title: s.title,
              type: s.type,
              content: s.content,
              score: s.score,
              studentAnswer: s.studentAnswer,
              teacherScore: s.teacherScore,
              teacherComment: s.teacherComment,
              language: s.language
            })),
            summaryNote: report.summaryNote,
            totalScore: record.totalScore,
            teacherFeedback: report.teacherFeedback,
            submittedAt: report.submittedAt
          })
        }
      } catch (e) {
        console.error(`加载学生 ${record.studentId} 报告失败:`, e)
      }
    }

    // 按学号排序
    fullReports.sort((a, b) => a.studentNo.localeCompare(b.studentNo))
    reports.value = fullReports
  } catch (error) {
    console.error('加载报告列表失败:', error)
    message.error('加载报告失败')
    reports.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (!experimentId) {
    message.error('缺少实验ID参数')
    router.push('/admin/experiments/reports')
    return
  }
  loadReports()
})
</script>

<style scoped>
.batch-print-page {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 操作栏 */
.print-header {
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

.page-title {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
}

.page-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  margin: 0;
  font-size: 14px;
  color: #8c8c8c;
}

.page-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 加载和空状态 */
.loading-container,
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  background: #fff;
  margin: 24px;
  border-radius: 8px;
}

/* 报告容器 */
.reports-container {
  padding: 24px;
}

.report-wrapper {
  background: #fff;
  margin-bottom: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

/* 报告封面 */
.report-cover {
  padding: 48px;
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
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
  padding: 32px;
  border-bottom: 1px solid #f0f0f0;
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

.step-type-tag {
  padding: 2px 10px;
  color: #fff;
  font-size: 12px;
  border-radius: 4px;
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

/* 编程题代码块 */
.answer-code-block {
  border: 1px solid #3c3c3c;
  border-radius: 8px;
  overflow: hidden;
  background: #1e1e1e;
  margin-top: 8px;
}

.code-block-header {
  display: flex;
  align-items: center;
  padding: 8px 14px;
  background: #252526;
  border-bottom: 1px solid #3c3c3c;
}

.language-tag {
  padding: 3px 10px;
  background: #0066cc;
  color: #fff;
  font-size: 12px;
  border-radius: 4px;
  font-weight: 500;
}

.answer-code-pre {
  margin: 0;
  padding: 12px 16px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  color: #d4d4d4;
  overflow-x: auto;
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

.grade-tag {
  padding: 4px 12px;
  background: #52c41a;
  color: #fff;
  font-size: 13px;
  border-radius: 4px;
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
  min-height: 60px;
}

.feedback-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px dashed #e8e8e8;
}

.feedback-title {
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
}

.signature-info {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #595959;
}

/* 打印样式 */
@media print {
  .no-print {
    display: none !important;
  }

  .batch-print-page {
    background: #fff;
  }

  .reports-container {
    padding: 0;
  }

  .report-wrapper {
    box-shadow: none;
    border-radius: 0;
    margin-bottom: 0;
  }

  .page-break {
    page-break-before: always;
  }

  .report-section,
  .report-cover,
  .signature-section {
    border: none;
  }

  /* 避免步骤在页面中间断开 */
  .step-item {
    page-break-inside: avoid;
  }
}

@media screen and (max-width: 768px) {
  .print-header {
    flex-direction: column;
    gap: 16px;
  }

  .report-cover {
    padding: 24px;
  }

  .report-section {
    padding: 20px;
  }

  .cover-title {
    font-size: 24px;
  }
}
</style>
