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
      :title="gradingModalTitle"
      width="900px"
      :footer="null"
      @cancel="closeGradingModal"
    >
      <div v-if="currentRecord" class="grading-modal-content">
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
            <span class="label">当前得分：</span>
            <span class="value score">{{ currentRecord.record.earnedScore }} / {{ currentRecord.record.totalScore }}</span>
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
                <div class="answer-text">{{ formatStudentAnswer(currentRecord.record.answers[pq.questionId]?.answer) }}</div>
              </div>
              <div class="standard-answer">
                <div class="content-label">参考答案：</div>
                <div class="answer-text correct">{{ pq.question?.standardAnswer }}</div>
              </div>
            </div>

            <!-- 评分区域 -->
            <div class="grading-section">
              <div class="auto-score" v-if="currentRecord.record.answers[pq.questionId]?.autoScore !== null">
                <span class="label">系统评分：</span>
                <span class="score">{{ currentRecord.record.answers[pq.questionId]?.autoScore }} 分</span>
              </div>
              
              <!-- 需要手动批改的题目（简答题、编程题、综合题） -->
              <div v-if="needsManualGrading(pq.question?.questionType)" class="manual-grading">
                <div class="grading-input">
                  <span class="label">评分（满分 {{ pq.score }} 分）：</span>
                  <a-input-number
                    v-model:value="gradingScores[pq.questionId]"
                    :min="0"
                    :max="pq.score"
                    :disabled="currentRecord.record.status === 'graded'"
                  />
                  <a-button
                    v-if="currentRecord.record.status !== 'graded'"
                    type="primary"
                    size="small"
                    :loading="gradingSubmitting"
                    @click="submitGrade(pq.questionId, pq.score)"
                  >
                    确认评分
                  </a-button>
                </div>
                <div class="grading-comment">
                  <span class="label">评语：</span>
                  <a-textarea
                    v-model:value="gradingComments[pq.questionId]"
                    :rows="2"
                    placeholder="输入评语（可选）"
                    :disabled="currentRecord.record.status === 'graded'"
                  />
                </div>
                <div v-if="currentRecord.record.answers[pq.questionId]?.manualScore !== null" class="graded-info">
                  <a-tag color="green">
                    已评分: {{ currentRecord.record.answers[pq.questionId]?.manualScore }} 分
                  </a-tag>
                  <span v-if="currentRecord.record.answers[pq.questionId]?.comment" class="graded-comment">
                    评语: {{ currentRecord.record.answers[pq.questionId]?.comment }}
                  </span>
                </div>
              </div>
              
              <!-- 自动评分的题目 -->
              <div v-else class="auto-graded">
                <a-tag :color="currentRecord.record.answers[pq.questionId]?.autoScore === pq.score ? 'green' : 'orange'">
                  得分: {{ currentRecord.record.answers[pq.questionId]?.autoScore ?? 0 }} / {{ pq.score }}
                </a-tag>
              </div>
            </div>
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

const scoreColumns = [
  { title: '学生信息', key: 'studentInfo', width: 180 },
  { title: '班级', dataIndex: 'className', key: 'className', width: 180 },
  { title: '提交时间', dataIndex: 'submittedAt', key: 'submittedAt', width: 160 },
  { title: '状态', key: 'status', width: 100 },
  { title: '得分', key: 'score', width: 120 },
  { title: '操作', key: 'action', width: 120 },
]

const gradingModalTitle = computed(() => {
  if (!currentRecord.value) return '批改试卷'
  const status = currentRecord.value.record.status === 'graded' ? '查看详情' : '批改试卷'
  return `${status} - ${currentRecord.value.record.studentName}`
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
    case 5: return 'orange'
    case 6: return 'green'
    case 7: return 'red'
    default: return 'default'
  }
}

function needsManualGrading(type?: QuestionType) {
  return type === 5 || type === 6 || type === 7  // 简答、编程、综合题需要手动批改
}

function formatQuestionContent(content: string) {
  // 将填空题的 ____ 转换为高亮显示
  return content.replace(/____/g, '<span class="blank-mark">______</span>')
}

function formatStudentAnswer(answer: string | string[] | undefined) {
  if (!answer) return '未作答'
  if (Array.isArray(answer)) return answer.join(', ')
  return answer
}

function parseOptions(optionsText: string) {
  try {
    return JSON.parse(optionsText)
  } catch {
    return []
  }
}

async function openGradingModal(record: StudentAnswerRecord) {
  await examStore.loadRecordDetail(record.id)
  
  // 初始化评分和评语
  if (currentRecord.value) {
    currentRecord.value.paper.questions.forEach(pq => {
      const answer = currentRecord.value!.record.answers[pq.questionId]
      gradingScores[pq.questionId] = answer?.manualScore ?? 0
      gradingComments[pq.questionId] = answer?.comment ?? ''
    })
  }
  
  gradingModalVisible.value = true
}

function closeGradingModal() {
  gradingModalVisible.value = false
}

async function submitGrade(questionId: number, maxScore: number) {
  if (!currentRecord.value) return
  
  const score = gradingScores[questionId]
  if (score === undefined || score < 0 || score > maxScore) {
    message.error(`评分必须在 0 到 ${maxScore} 之间`)
    return
  }
  
  gradingSubmitting.value = true
  try {
    const result = await examStore.gradeAnswer({
      recordId: currentRecord.value.record.id,
      questionId,
      score,
      comment: gradingComments[questionId] || undefined
    })
    
    if (result) {
      message.success('评分成功')
      // 刷新学生记录列表
      if (selectedExamId.value) {
        await examStore.loadStudentRecords(selectedExamId.value)
      }
    } else {
      message.error('评分失败')
    }
  } finally {
    gradingSubmitting.value = false
  }
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
  max-height: 70vh;
  overflow-y: auto;
}

.student-header {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 24px;
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
  display: flex;
  flex-direction: column;
  gap: 24px;
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

.auto-score {
  margin-bottom: 12px;
}

.auto-score .label {
  color: #666;
}

.auto-score .score {
  font-weight: 500;
  color: #1890ff;
}

.manual-grading {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.grading-input {
  display: flex;
  align-items: center;
  gap: 12px;
}

.grading-input .label {
  color: #333;
  font-weight: 500;
}

.grading-comment {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.grading-comment .label {
  color: #333;
  font-weight: 500;
}

.graded-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
}

.graded-comment {
  color: #666;
  font-size: 13px;
}

.auto-graded {
  display: flex;
  align-items: center;
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
}
</style>
