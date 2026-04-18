<template>
  <div class="score-grading-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">成绩分析与批改</h1>
        <p class="page-desc">查看考试成绩统计，批改简答题和编程题</p>
      </div>
    </div>

    <!-- 考试选择 -->
    <div class="exam-selector">
      <a-card :bordered="false">
        <div class="selector-row">
          <span class="selector-label">选择考试：</span>
          <a-select
            v-model:value="selectedExamId"
            placeholder="请选择要查看/批改的考试"
            style="width: 320px"
            :loading="gradingLoading"
            @change="handleExamChange"
          >
            <a-select-option v-for="exam in gradingExams" :key="exam.id" :value="exam.id">
              {{ exam.examName }}
              <span class="exam-option-info">
                ({{ exam.submittedCount }}人提交, {{ exam.pendingCount }}人待批)
              </span>
            </a-select-option>
          </a-select>
        </div>
      </a-card>
    </div>

    <!-- 成绩统计卡片 -->
    <div v-if="selectedExamId" class="stats-cards">
      <a-row :gutter="16">
        <a-col :xs="12" :sm="6">
          <div class="stat-card">
            <div class="stat-icon submitted">
              <TeamOutlined />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ scoreStats.submittedCount }}</div>
              <div class="stat-label">提交人数</div>
            </div>
          </div>
        </a-col>
        <a-col :xs="12" :sm="6">
          <div class="stat-card">
            <div class="stat-icon graded">
              <CheckCircleOutlined />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ scoreStats.gradedCount }}</div>
              <div class="stat-label">已批改</div>
            </div>
          </div>
        </a-col>
        <a-col :xs="12" :sm="6">
          <div class="stat-card">
            <div class="stat-icon pending">
              <ClockCircleOutlined />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ scoreStats.pendingCount }}</div>
              <div class="stat-label">待批改</div>
            </div>
          </div>
        </a-col>
        <a-col :xs="12" :sm="6">
          <div class="stat-card">
            <div class="stat-icon average">
              <BarChartOutlined />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ scoreStats.averageScore }}</div>
              <div class="stat-label">平均分</div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <!-- 成绩列表表格 -->
    <div v-if="selectedExamId" class="score-table-section">
      <a-card title="学生成绩列表" :bordered="false">
        <template #extra>
          <a-space>
            <a-tag color="green">及格率: {{ scoreStats.passRate }}%</a-tag>
            <a-tag color="blue">最高分: {{ scoreStats.highestScore }}</a-tag>
            <a-tag color="orange">最低分: {{ scoreStats.lowestScore }}</a-tag>
          </a-space>
        </template>
        
        <a-table
          :columns="scoreColumns"
          :data-source="studentRecords"
          :loading="gradingLoading"
          :pagination="{ pageSize: 10 }"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'studentInfo'">
              <div class="student-info">
                <span class="student-name">{{ record.studentName }}</span>
                <span class="student-no">{{ record.studentNo }}</span>
              </div>
            </template>
            <template v-else-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record.status)">
                {{ getStatusText(record.status) }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'score'">
              <span v-if="record.status === 'graded'" class="score-value">
                {{ record.earnedScore }} / {{ record.totalScore }}
              </span>
              <span v-else class="score-pending">待批改</span>
            </template>
            <template v-else-if="column.key === 'action'">
              <a-button type="link" size="small" @click="openGradingModal(record)">
                {{ record.status === 'graded' ? '查看详情' : '批改试卷' }}
              </a-button>
            </template>
          </template>
        </a-table>
      </a-card>
    </div>

    <!-- 空状态 -->
    <div v-if="!selectedExamId && !gradingLoading" class="empty-state">
      <a-empty description="请先选择一个考试查看成绩">
        <template #image>
          <FileSearchOutlined style="font-size: 64px; color: #d9d9d9;" />
        </template>
      </a-empty>
    </div>

    <!-- 批改弹窗 -->
    <a-modal
      v-model:open="gradingModalVisible"
      :title="null"
      width="960px"
      :footer="null"
      :bodyStyle="{ padding: 0 }"
      @cancel="closeGradingModal"
    >
      <div v-if="currentRecord" class="grading-modal-content">
        <!-- 弹窗顶部导航 -->
        <div class="modal-header">
          <div class="header-left">
            <h3 class="modal-title">
              {{ currentRecord.record.status === 'graded' ? '查看详情' : '批改试卷' }}
            </h3>
            <span class="student-indicator">
              第 {{ currentRecordIndex + 1 }} / {{ studentRecords.length }} 位学生
            </span>
          </div>
          <div class="header-nav">
            <a-button 
              :disabled="currentRecordIndex <= 0"
              @click="goToPrevStudent"
            >
              <LeftOutlined /> 上一个
            </a-button>
            <a-button 
              :disabled="currentRecordIndex >= studentRecords.length - 1"
              @click="goToNextStudent"
            >
              下一个 <RightOutlined />
            </a-button>
          </div>
        </div>

        <!-- 学生信息 -->
        <div class="student-header">
          <div class="student-detail">
            <span class="label">学生姓名：</span>
            <span class="value">{{ currentRecord.record.studentName }}</span>
          </div>
          <div class="student-detail">
            <span class="label">学号：</span>
            <span class="value">{{ currentRecord.record.studentNo }}</span>
          </div>
          <div class="student-detail">
            <span class="label">班级：</span>
            <span class="value">{{ currentRecord.record.className }}</span>
          </div>
          <div class="student-detail">
            <span class="label">提交时间：</span>
            <span class="value">{{ currentRecord.record.submittedAt || '--' }}</span>
          </div>
          <div class="student-detail">
            <span class="label">当前得分：</span>
            <span class="value score">{{ calculatedTotalScore }} / {{ currentRecord.record.totalScore }}</span>
          </div>
          <div class="student-detail">
            <span class="label">状态：</span>
            <a-tag :color="getStatusColor(currentRecord.record.status)">
              {{ getStatusText(currentRecord.record.status) }}
            </a-tag>
          </div>
        </div>

        <!-- 题目批改列表 -->
        <div class="questions-grading">
          <div
            v-for="(pq, index) in currentRecord.paper.questions"
            :key="pq.id"
            class="question-grading-item"
          >
            <div class="question-header">
              <span class="question-number">第 {{ index + 1 }} 题</span>
              <a-tag :color="getQuestionTypeColor(pq.question?.questionType)">
                {{ QUESTION_TYPE_MAP[pq.question?.questionType as QuestionType] || '未知' }}
              </a-tag>
              <span class="question-score">（{{ pq.score }} 分）</span>
              <span class="question-graded-status">
                <a-tag v-if="getQuestionGradedScore(pq.questionId) !== null" color="green" size="small">
                  已评: {{ getQuestionGradedScore(pq.questionId) }}分
                </a-tag>
              </span>
            </div>

            <div class="question-content">
              <div class="content-label">题目：</div>
              <div class="content-text" v-html="formatQuestionContent(pq.question?.questionContent || '')"></div>
            </div>

            <!-- 选项（选择题） -->
            <div v-if="pq.question?.optionsText" class="question-options">
              <div class="content-label">选项：</div>
              <div class="options-list">
                <div v-for="opt in parseOptions(pq.question.optionsText)" :key="opt.key" class="option-item">
                  <span class="option-key">{{ opt.key }}.</span>
                  <span class="option-label">{{ opt.label }}</span>
                </div>
              </div>
            </div>

            <div class="answer-section">
              <div class="student-answer">
                <div class="content-label">学生答案：</div>
                <div class="answer-text">{{ formatStudentAnswer(currentRecord.record.answers[pq.questionId]?.answer, pq.question?.questionType) }}</div>
              </div>
              <div class="standard-answer">
                <div class="content-label">参考答案：</div>
                <div class="answer-text correct">{{ formatStandardAnswer(pq.question?.standardAnswer || '', pq.question?.questionType) }}</div>
              </div>
            </div>

            <!-- 评分区域 -->
            <div class="grading-section">
              <!-- 自动评分的题目（填空题、选择题） -->
              <div v-if="!needsManualGrading(pq.question?.questionType)" class="auto-graded">
                <span class="label">系统自动评分：</span>
                <a-tag :color="currentRecord.record.answers[pq.questionId]?.autoScore === pq.score ? 'green' : 'orange'">
                  {{ currentRecord.record.answers[pq.questionId]?.autoScore ?? 0 }} / {{ pq.score }} 分
                </a-tag>
              </div>
              
              <!-- 需要手动批改的题目（简答题、编程题、综合题） -->
              <div v-else class="manual-grading">
                <div class="grading-row">
                  <div class="grading-input">
                    <span class="label">评分（满分 {{ pq.score }} 分）：</span>
                    <a-input-number
                      v-model:value="gradingScores[pq.questionId]"
                      :min="0"
                      :max="pq.score"
                      :disabled="currentRecord.record.status === 'graded'"
                      style="width: 100px"
                    />
                  </div>
                  <div class="grading-comment-inline">
                    <span class="label">评语：</span>
                    <a-input
                      v-model:value="gradingComments[pq.questionId]"
                      placeholder="输入评语（可选）"
                      :disabled="currentRecord.record.status === 'graded'"
                      style="flex: 1"
                    />
                  </div>
                </div>
                <div v-if="currentRecord.record.answers[pq.questionId]?.comment && currentRecord.record.status === 'graded'" class="existing-comment">
                  <span class="label">已有评语：</span>
                  <span class="comment-text">{{ currentRecord.record.answers[pq.questionId]?.comment }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 底部操作栏 -->
        <div class="modal-footer">
          <div class="footer-left">
            <span class="total-info">
              当前评分合计：<strong>{{ calculatedTotalScore }}</strong> / {{ currentRecord.record.totalScore }} 分
            </span>
          </div>
          <div class="footer-right">
            <a-button @click="closeGradingModal">关闭</a-button>
            <a-button 
              v-if="currentRecord.record.status !== 'graded'"
              type="primary"
              :loading="gradingSubmitting"
              @click="submitAllGrades"
            >
              确认评分并保存
            </a-button>
            <a-button 
              v-if="currentRecordIndex < studentRecords.length - 1"
              type="primary"
              ghost
              @click="submitAndNext"
              :loading="gradingSubmitting"
            >
              {{ currentRecord.record.status !== 'graded' ? '保存并批改下一个' : '批改下一个' }}
            </a-button>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import {
  TeamOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  BarChartOutlined,
  FileSearchOutlined,
  LeftOutlined,
  RightOutlined,
} from '@ant-design/icons-vue'
import { useExamAdminStore } from '@/stores/exam/admin'
import { QUESTION_TYPE_MAP, type QuestionType, type StudentAnswerRecord } from '@/stores/exam/types'

const examStore = useExamAdminStore()
const { gradingExams, studentRecords, currentRecord, scoreStats, gradingLoading } = storeToRefs(examStore)

const selectedExamId = ref<number | null>(null)
const gradingModalVisible = ref(false)
const gradingSubmitting = ref(false)
const gradingScores = reactive<Record<number, number>>({})
const gradingComments = reactive<Record<number, string>>({})
const currentRecordIndex = ref(0)

const scoreColumns = [
  { title: '学生信息', key: 'studentInfo', width: 180 },
  { title: '班级', dataIndex: 'className', key: 'className', width: 180 },
  { title: '提交时间', dataIndex: 'submittedAt', key: 'submittedAt', width: 160 },
  { title: '状态', key: 'status', width: 100 },
  { title: '得分', key: 'score', width: 120 },
  { title: '操作', key: 'action', width: 120 },
]

// 计算当前总得分（包括自动评分 + 手动输入的评分）
const calculatedTotalScore = computed(() => {
  if (!currentRecord.value) return 0
  
  let total = 0
  currentRecord.value.paper.questions.forEach(pq => {
    const answer = currentRecord.value!.record.answers[pq.questionId]
    if (answer?.autoScore !== null) {
      total += answer.autoScore
    }
    if (needsManualGrading(pq.question?.questionType)) {
      // 使用输入框中的值
      total += gradingScores[pq.questionId] || 0
    }
  })
  return total
})

onMounted(async () => {
  await examStore.loadGradingExams()
})

async function handleExamChange(examId: number) {
  selectedExamId.value = examId
  await examStore.loadStudentRecords(examId)
}

function getStatusColor(status: string) {
  switch (status) {
    case 'graded': return 'green'
    case 'grading': return 'orange'
    case 'submitted': return 'blue'
    default: return 'default'
  }
}

function getStatusText(status: string) {
  switch (status) {
    case 'graded': return '已批改'
    case 'grading': return '批改中'
    case 'submitted': return '待批改'
    default: return '未知'
  }
}

function getQuestionTypeColor(type?: QuestionType) {
  switch (type) {
    case 1: return 'cyan'
    case 2: return 'blue'
    case 3: return 'purple'
    case 4: return 'geekblue'
    case 5: return 'orange'
    case 6: return 'green'
    case 7: return 'red'
    default: return 'default'
  }
}

function needsManualGrading(type?: QuestionType) {
  return type === 5 || type === 6 || type === 7  // 简答、编程、综合题需要手动批改
}

function getQuestionGradedScore(questionId: number): number | null {
  if (!currentRecord.value) return null
  const answer = currentRecord.value.record.answers[questionId]
  if (answer?.autoScore !== null) return answer.autoScore
  if (answer?.manualScore !== null) return answer.manualScore
  return null
}

function formatQuestionContent(content: string) {
  return content.replace(/____/g, '<span class="blank-mark">______</span>')
}

function formatStudentAnswer(answer: string | string[] | undefined, questionType?: QuestionType) {
  if (!answer) return '未作答'
  if (Array.isArray(answer)) return answer.join(', ')
  // 判断题特殊处理
  if (questionType === 4) {
    return answer === '1' ? '正确' : '错误'
  }
  return answer
}

function formatStandardAnswer(standardAnswer: string, questionType?: QuestionType) {
  if (questionType === 4) {
    return standardAnswer === '1' ? '正确' : '错误'
  }
  return standardAnswer
}

function parseOptions(optionsText: string) {
  try {
    return JSON.parse(optionsText)
  } catch {
    return []
  }
}

async function openGradingModal(record: StudentAnswerRecord) {
  // 找到当前记录的索引
  currentRecordIndex.value = studentRecords.value.findIndex(r => r.id === record.id)
  await loadRecordAndInitScores(record.id)
  gradingModalVisible.value = true
}

async function loadRecordAndInitScores(recordId: number) {
  await examStore.loadRecordDetail(recordId)
  
  // 初始化评分和评语
  if (currentRecord.value) {
    currentRecord.value.paper.questions.forEach(pq => {
      const answer = currentRecord.value!.record.answers[pq.questionId]
      gradingScores[pq.questionId] = answer?.manualScore ?? 0
      gradingComments[pq.questionId] = answer?.comment ?? ''
    })
  }
}

function closeGradingModal() {
  gradingModalVisible.value = false
}

async function goToPrevStudent() {
  if (currentRecordIndex.value <= 0) return
  currentRecordIndex.value--
  const prevRecord = studentRecords.value[currentRecordIndex.value]
  await loadRecordAndInitScores(prevRecord.id)
}

async function goToNextStudent() {
  if (currentRecordIndex.value >= studentRecords.value.length - 1) return
  currentRecordIndex.value++
  const nextRecord = studentRecords.value[currentRecordIndex.value]
  await loadRecordAndInitScores(nextRecord.id)
}

async function submitAllGrades() {
  if (!currentRecord.value) return
  
  gradingSubmitting.value = true
  try {
    // 批量提交所有需要手动批改的题目
    const manualQuestions = currentRecord.value.paper.questions.filter(
      pq => needsManualGrading(pq.question?.questionType)
    )
    
    for (const pq of manualQuestions) {
      const score = gradingScores[pq.questionId]
      if (score === undefined || score < 0 || score > pq.score) {
        message.error(`第 ${currentRecord.value.paper.questions.indexOf(pq) + 1} 题评分必须在 0 到 ${pq.score} 之间`)
        return
      }
      
      await examStore.gradeAnswer({
        recordId: currentRecord.value.record.id,
        questionId: pq.questionId,
        score,
        comment: gradingComments[pq.questionId] || undefined
      })
    }
    
    message.success('评分保存成功')
    
    // 刷新学生记录列表
    if (selectedExamId.value) {
      await examStore.loadStudentRecords(selectedExamId.value)
    }
  } finally {
    gradingSubmitting.value = false
  }
}

async function submitAndNext() {
  if (currentRecord.value?.record.status !== 'graded') {
    await submitAllGrades()
  }
  await goToNextStudent()
}
</script>

<style scoped>
.score-grading-page {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px 0;
}

.page-desc {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.exam-selector {
  margin-bottom: 24px;
}

.selector-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.selector-label {
  font-weight: 500;
  color: #333;
}

.exam-option-info {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
}

.stats-cards {
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.submitted {
  background: #e6f7ff;
  color: #1890ff;
}

.stat-icon.graded {
  background: #f6ffed;
  color: #52c41a;
}

.stat-icon.pending {
  background: #fff7e6;
  color: #fa8c16;
}

.stat-icon.average {
  background: #f9f0ff;
  color: #722ed1;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.score-table-section {
  margin-bottom: 24px;
}

.student-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.student-name {
  font-weight: 500;
  color: #1a1a1a;
}

.student-no {
  font-size: 12px;
  color: #999;
}

.score-value {
  font-weight: 600;
  color: #1890ff;
}

.score-pending {
  color: #999;
}

.empty-state {
  background: #fff;
  padding: 80px 24px;
  border-radius: 8px;
  text-align: center;
}

/* 批改弹窗样式 */
.grading-modal-content {
  display: flex;
  flex-direction: column;
  max-height: 85vh;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.student-indicator {
  font-size: 14px;
  color: #666;
  padding: 4px 12px;
  background: #e6f7ff;
  border-radius: 4px;
}

.header-nav {
  display: flex;
  gap: 8px;
}

.student-header {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.student-detail {
  display: flex;
  align-items: center;
  gap: 8px;
}

.student-detail .label {
  color: #666;
  font-size: 14px;
}

.student-detail .value {
  font-weight: 500;
  color: #1a1a1a;
}

.student-detail .value.score {
  color: #1890ff;
  font-size: 18px;
}

.questions-grading {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.question-grading-item {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 20px;
  background: #fff;
}

.question-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.question-number {
  font-weight: 600;
  color: #1a1a1a;
  font-size: 16px;
}

.question-score {
  color: #666;
  font-size: 14px;
}

.question-graded-status {
  margin-left: auto;
}

.question-content {
  margin-bottom: 12px;
}

.content-label {
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
  font-size: 14px;
}

.content-text {
  color: #1a1a1a;
  line-height: 1.6;
}

.content-text :deep(.blank-mark) {
  display: inline-block;
  min-width: 60px;
  border-bottom: 2px solid #1890ff;
  color: #1890ff;
  text-align: center;
  margin: 0 4px;
}

.question-options {
  margin-bottom: 12px;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.option-item {
  display: flex;
  gap: 8px;
  padding: 4px 0;
}

.option-key {
  font-weight: 500;
  color: #333;
}

.option-label {
  color: #666;
}

.answer-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 6px;
}

.student-answer,
.standard-answer {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.answer-text {
  color: #1a1a1a;
  word-break: break-word;
}

.answer-text.correct {
  color: #52c41a;
}

.grading-section {
  padding-top: 16px;
  border-top: 1px dashed #e8e8e8;
}

.auto-graded {
  display: flex;
  align-items: center;
  gap: 8px;
}

.auto-graded .label {
  color: #666;
}

.manual-grading {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.grading-row {
  display: flex;
  align-items: center;
  gap: 24px;
}

.grading-input {
  display: flex;
  align-items: center;
  gap: 8px;
}

.grading-input .label {
  color: #333;
  font-weight: 500;
  white-space: nowrap;
}

.grading-comment-inline {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.grading-comment-inline .label {
  color: #333;
  font-weight: 500;
  white-space: nowrap;
}

.existing-comment {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px 12px;
  background: #f6ffed;
  border-radius: 4px;
}

.existing-comment .label {
  color: #52c41a;
  font-weight: 500;
  white-space: nowrap;
}

.existing-comment .comment-text {
  color: #333;
}

/* 底部操作栏 */
.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.footer-left {
  font-size: 14px;
  color: #666;
}

.footer-left strong {
  color: #1890ff;
  font-size: 18px;
}

.footer-right {
  display: flex;
  gap: 12px;
}

@media (max-width: 768px) {
  .score-grading-page {
    padding: 16px;
  }
  
  .answer-section {
    grid-template-columns: 1fr;
  }
  
  .student-header {
    flex-direction: column;
    gap: 12px;
  }
  
  .modal-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .grading-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .grading-comment-inline {
    width: 100%;
  }
  
  .modal-footer {
    flex-direction: column;
    gap: 12px;
  }
  
  .footer-right {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
