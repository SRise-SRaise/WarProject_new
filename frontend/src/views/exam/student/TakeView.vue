<template>
  <div class="exam-take-page">
    <!-- 顶部工具栏 -->
    <header class="exam-header">
      <div class="header-left">
        <h1 class="exam-title">{{ currentExam?.examName || '考试' }}</h1>
        <span v-if="currentPaper" class="exam-meta">
          共 {{ currentPaper.questionCount }} 题 · 总分 {{ currentPaper.totalScore }} 分
        </span>
      </div>
      <div class="header-right">
        <div v-if="currentExam?.durationMin" class="timer" :class="{ warning: remainingTime <= 300, danger: remainingTime <= 60 }">
          <ClockCircleOutlined />
          <span class="timer-text">{{ formatTime(remainingTime) }}</span>
        </div>
        <a-button @click="confirmBack">退出考试</a-button>
        <a-button type="primary" :loading="submitting" @click="confirmSubmit">提交试卷</a-button>
      </div>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <a-spin size="large" tip="加载试卷中..." />
    </div>

    <!-- 考试内容 -->
    <div v-else-if="currentPaper && !examResult" class="exam-content">
      <!-- 左侧：题目列表 -->
      <main class="questions-panel">
        <div 
          v-for="(pq, index) in currentPaper.questions" 
          :key="pq.id" 
          :id="`question-${index + 1}`"
          class="question-card"
        >
          <div class="question-header">
            <div class="question-number">第 {{ index + 1 }} 题</div>
            <div class="question-meta">
              <a-tag>{{ getQuestionTypeName(pq.question?.questionType) }}</a-tag>
              <span class="question-score">{{ pq.score }} 分</span>
            </div>
          </div>
          
          <!-- 题目内容 -->
          <div class="question-content">
            <!-- 填空题：内联输入框 -->
            <template v-if="pq.question?.questionType === 1">
              <div class="fill-blank-content" v-html="renderFillBlankQuestion(pq.question, pq.questionId)"></div>
            </template>
            <!-- 其他题型：普通显示 -->
            <template v-else>
              <div class="question-text">{{ pq.question?.questionContent }}</div>
            </template>
          </div>

          <!-- 答题区域 -->
          <div class="answer-area">
            <!-- 单选题 -->
            <template v-if="pq.question?.questionType === 2">
              <a-radio-group 
                :value="getAnswer(pq.questionId)" 
                @change="(e: any) => setAnswer(pq.questionId, e.target.value)"
                class="options-group"
              >
                <a-radio 
                  v-for="opt in parseOptions(pq.question.optionsText)" 
                  :key="opt.key" 
                  :value="opt.key"
                  class="option-item"
                >
                  <span class="option-key">{{ opt.key }}</span>
                  <span class="option-label">{{ opt.label }}</span>
                </a-radio>
              </a-radio-group>
            </template>

            <!-- 多选题 -->
            <template v-else-if="pq.question?.questionType === 3">
              <a-checkbox-group 
                :value="getMultiAnswer(pq.questionId)" 
                @change="(vals: any) => setAnswer(pq.questionId, vals)"
                class="options-group"
              >
                <a-checkbox 
                  v-for="opt in parseOptions(pq.question.optionsText)" 
                  :key="opt.key" 
                  :value="opt.key"
                  class="option-item"
                >
                  <span class="option-key">{{ opt.key }}</span>
                  <span class="option-label">{{ opt.label }}</span>
                </a-checkbox>
              </a-checkbox-group>
            </template>

            <!-- 简答题 / 编程题 / 综合题 -->
            <template v-else-if="pq.question?.questionType === 5 || pq.question?.questionType === 6 || pq.question?.questionType === 7">
              <a-textarea
                :value="getAnswer(pq.questionId) as string"
                @change="(e: any) => setAnswer(pq.questionId, e.target.value)"
                :rows="pq.question?.questionType === 6 ? 10 : 6"
                :placeholder="getPlaceholder(pq.question?.questionType)"
                class="answer-textarea"
              />
            </template>
          </div>
        </div>
      </main>

      <!-- 右侧：答题卡 -->
      <aside class="answer-card-panel">
        <div class="answer-card">
          <h3 class="card-title">答题卡</h3>
          <div class="card-grid">
            <div 
              v-for="(pq, index) in currentPaper.questions" 
              :key="pq.id"
              class="card-item"
              :class="{ answered: isAnswered(pq.questionId) }"
              @click="scrollToQuestion(index + 1)"
            >
              {{ index + 1 }}
            </div>
          </div>
          <div class="card-legend">
            <span class="legend-item"><span class="dot answered"></span>已答</span>
            <span class="legend-item"><span class="dot"></span>未答</span>
          </div>
          <div class="card-stats">
            <span>已答 {{ answeredCount }} / {{ currentPaper.questions.length }} 题</span>
          </div>
        </div>
      </aside>
    </div>

    <!-- 提交结果 -->
    <div v-else-if="examResult" class="result-container">
      <div class="result-card">
        <div class="result-icon">
          <CheckCircleOutlined />
        </div>
        <h2 class="result-title">考试已提交</h2>
        <div class="result-score">
          <span class="score-earned">{{ examResult.earnedScore }}</span>
          <span class="score-total">/ {{ examResult.totalScore }} 分</span>
        </div>
        <p class="result-hint">您的答卷已成功提交，感谢参加本次考试</p>
        <a-button type="primary" size="large" @click="router.push('/exams')">返回考试列表</a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoute, useRouter } from 'vue-router'
import { Modal, message } from 'ant-design-vue'
import { ClockCircleOutlined, CheckCircleOutlined } from '@ant-design/icons-vue'
import { useExamStudentStore } from '@/stores/exam/student'
import { QUESTION_TYPE_MAP, type QuestionType } from '@/stores/exam/types'

const route = useRoute()
const router = useRouter()
const examStore = useExamStudentStore()
const { currentExam, currentPaper, answers, examResult, loading } = storeToRefs(examStore)

const submitting = ref(false)
const remainingTime = ref(0) // 剩余时间（秒）
let timerInterval: ReturnType<typeof setInterval> | null = null

// 已答题数
const answeredCount = computed(() => {
  if (!currentPaper.value) return 0
  return currentPaper.value.questions.filter(pq => isAnswered(pq.questionId)).length
})

// 格式化时间
function formatTime(seconds: number): string {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  if (h > 0) {
    return `${h}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
  }
  return `${m}:${String(s).padStart(2, '0')}`
}

// 获取题型名称
function getQuestionTypeName(type?: QuestionType): string {
  if (!type) return '未知'
  return QUESTION_TYPE_MAP[type] || '未知'
}

// 解析选项
function parseOptions(optionsText: string | null): { key: string; label: string }[] {
  if (!optionsText) return []
  try {
    return JSON.parse(optionsText)
  } catch {
    return []
  }
}

// 获取答案
function getAnswer(questionId: number): string | string[] | undefined {
  return examStore.getAnswer(questionId)
}

// 获取多选答案（确保是数组）
function getMultiAnswer(questionId: number): string[] {
  const ans = examStore.getAnswer(questionId)
  if (Array.isArray(ans)) return ans
  return []
}

// 设置答案
function setAnswer(questionId: number, value: string | string[]): void {
  examStore.setAnswer(questionId, value)
}

// 判断是否已答
function isAnswered(questionId: number): boolean {
  const ans = examStore.getAnswer(questionId)
  if (ans === undefined || ans === null) return false
  if (typeof ans === 'string') return ans.trim().length > 0
  if (Array.isArray(ans)) return ans.length > 0
  return false
}

// 渲染填空题（将____替换为输入框）
function renderFillBlankQuestion(question: any, questionId: number): string {
  if (!question) return ''
  const content = question.questionContent
  const answers = examStore.getAnswer(questionId)
  const answerArr = typeof answers === 'string' ? answers.split(',') : []
  
  let blankIndex = 0
  // 将 ____ 替换为带有data属性的input标签
  return content.replace(/____/g, () => {
    const idx = blankIndex++
    const value = answerArr[idx] || ''
    return `<input 
      type="text" 
      class="fill-blank-input" 
      data-question-id="${questionId}" 
      data-blank-index="${idx}"
      value="${value.replace(/"/g, '&quot;')}"
      placeholder="填写答案"
    />`
  })
}

// 获取placeholder
function getPlaceholder(type?: QuestionType): string {
  if (type === 6) return '请在此处编写代码...'
  if (type === 7) return '请详细作答，可以分点阐述...'
  return '请输入您的答案...'
}

// 滚动到指定题目
function scrollToQuestion(index: number): void {
  const el = document.getElementById(`question-${index}`)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

// 确认退出
function confirmBack(): void {
  Modal.confirm({
    title: '确认退出？',
    content: '退出后答题进度不会保存，确定要退出吗？',
    okText: '确定退出',
    cancelText: '继续答题',
    onOk: () => {
      examStore.resetExam()
      router.push('/exams')
    }
  })
}

// 确认提交
function confirmSubmit(): void {
  const unanswered = (currentPaper.value?.questions.length || 0) - answeredCount.value
  if (unanswered > 0) {
    Modal.confirm({
      title: '确认提交？',
      content: `您还有 ${unanswered} 道题未作答，确定要提交吗？`,
      okText: '确定提交',
      cancelText: '继续答题',
      onOk: submitExam
    })
  } else {
    Modal.confirm({
      title: '确认提交？',
      content: '提交后将无法修改答案，确定要提交吗？',
      okText: '确定提交',
      cancelText: '再检查一下',
      onOk: submitExam
    })
  }
}

// 提交考试
async function submitExam(): Promise<void> {
  submitting.value = true
  try {
    await examStore.submitExam()
    if (timerInterval) {
      clearInterval(timerInterval)
      timerInterval = null
    }
    message.success('考试已成功提交')
  } catch (error) {
    message.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 启动倒计时
function startTimer(): void {
  if (!currentExam.value?.durationMin) return
  remainingTime.value = currentExam.value.durationMin * 60
  
  timerInterval = setInterval(() => {
    remainingTime.value--
    if (remainingTime.value <= 0) {
      if (timerInterval) {
        clearInterval(timerInterval)
        timerInterval = null
      }
      message.warning('考试时间已到，系统将自动提交')
      submitExam()
    }
  }, 1000)
}

// 处理填空题输入事件
function handleFillBlankInput(event: Event): void {
  const target = event.target as HTMLInputElement
  if (target.classList.contains('fill-blank-input')) {
    const questionId = parseInt(target.dataset.questionId || '0')
    const blankIndex = parseInt(target.dataset.blankIndex || '0')
    const value = target.value
    examStore.setFillBlankAnswer(questionId, blankIndex, value)
  }
}

onMounted(async () => {
  const examId = parseInt(route.params.id as string)
  const success = await examStore.loadExamDetail(examId)
  
  if (!success) {
    message.error('考试不存在或未发布')
    router.push('/exams')
    return
  }
  
  // 启动倒计时
  startTimer()
  
  // 监听填空题输入
  await nextTick()
  document.addEventListener('input', handleFillBlankInput)
})

onUnmounted(() => {
  if (timerInterval) {
    clearInterval(timerInterval)
  }
  document.removeEventListener('input', handleFillBlankInput)
})
</script>

<style scoped>
.exam-take-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.exam-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.exam-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.exam-meta {
  font-size: 14px;
  color: #666;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.timer {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f0f5ff;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  color: #1677ff;
}

.timer.warning {
  background: #fff7e6;
  color: #fa8c16;
}

.timer.danger {
  background: #fff1f0;
  color: #ff4d4f;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

.timer-text {
  font-variant-numeric: tabular-nums;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 70px);
}

.exam-content {
  display: flex;
  gap: 24px;
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.questions-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.question-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.question-number {
  font-size: 16px;
  font-weight: 600;
  color: #1677ff;
}

.question-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.question-score {
  font-size: 14px;
  color: #666;
}

.question-content {
  margin-bottom: 20px;
}

.question-text {
  font-size: 15px;
  line-height: 1.8;
  color: #333;
}

.fill-blank-content {
  font-size: 15px;
  line-height: 2.2;
  color: #333;
}

.fill-blank-content :deep(.fill-blank-input) {
  display: inline-block;
  width: 120px;
  height: 32px;
  padding: 4px 12px;
  margin: 0 4px;
  font-size: 14px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: #fafafa;
  transition: all 0.3s;
  vertical-align: middle;
}

.fill-blank-content :deep(.fill-blank-input:focus) {
  border-color: #1677ff;
  background: #fff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.1);
}

.answer-area {
  margin-top: 16px;
}

.options-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-item {
  display: flex;
  align-items: flex-start;
  padding: 12px 16px;
  background: #fafafa;
  border-radius: 6px;
  transition: all 0.2s;
}

.option-item:hover {
  background: #f0f5ff;
}

.option-key {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  margin-right: 12px;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 500;
  color: #666;
}

.option-label {
  flex: 1;
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

.answer-textarea {
  font-size: 14px;
  line-height: 1.6;
}

/* 答题卡 */
.answer-card-panel {
  width: 240px;
  flex-shrink: 0;
}

.answer-card {
  position: sticky;
  top: 90px;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 16px 0;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
  margin-bottom: 16px;
}

.card-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 36px;
  background: #f5f5f5;
  border-radius: 4px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.card-item:hover {
  background: #e6f4ff;
  color: #1677ff;
}

.card-item.answered {
  background: #1677ff;
  color: #fff;
}

.card-legend {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 12px;
  color: #999;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 2px;
  background: #f5f5f5;
}

.dot.answered {
  background: #1677ff;
}

.card-stats {
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  font-size: 13px;
  color: #666;
  text-align: center;
}

/* 结果页面 */
.result-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 70px);
  padding: 24px;
}

.result-card {
  text-align: center;
  background: #fff;
  border-radius: 12px;
  padding: 48px 60px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.result-icon {
  font-size: 64px;
  color: #52c41a;
  margin-bottom: 20px;
}

.result-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 24px 0;
}

.result-score {
  margin-bottom: 16px;
}

.score-earned {
  font-size: 48px;
  font-weight: 700;
  color: #1677ff;
}

.score-total {
  font-size: 24px;
  color: #999;
}

.result-hint {
  font-size: 14px;
  color: #666;
  margin-bottom: 32px;
}

@media (max-width: 900px) {
  .exam-content {
    flex-direction: column;
  }
  
  .answer-card-panel {
    width: 100%;
    order: -1;
  }
  
  .answer-card {
    position: relative;
    top: 0;
  }
}
</style>
