<template>
  <div class="exam-take-page">
    <header class="exam-header">
      <div class="header-left">
        <h1 class="exam-title">{{ currentExam?.examName || '考试' }}</h1>
        <span v-if="currentPaper" class="exam-meta">共 {{ currentPaper.questionCount }} 题 · 总分 {{ currentPaper.totalScore }} 分</span>
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

    <div v-if="loading" class="loading-container">
      <a-spin size="large" tip="加载试卷中..." />
    </div>

    <div v-else-if="currentPaper" class="exam-content">
      <main class="questions-panel">
        <div v-for="(pq, index) in currentPaper.questions" :key="pq.id" :id="`question-${index + 1}`" class="question-card">
          <div class="question-header">
            <div class="question-number">第 {{ index + 1 }} 题</div>
            <div class="question-meta">
              <a-tag>{{ getQuestionTypeName(pq.question?.questionType) }}</a-tag>
              <span class="question-score">{{ pq.score }} 分</span>
            </div>
          </div>

          <div class="question-content">
            <template v-if="pq.question?.questionType === 1">
              <div class="fill-blank-content" v-html="renderFillBlankQuestion(pq.question, pq.questionId)"></div>
            </template>
            <template v-else>
              <div class="question-text">{{ pq.question?.questionContent }}</div>
            </template>
          </div>

          <div class="answer-area">
            <template v-if="pq.question?.questionType === 2">
              <a-radio-group :value="getAnswer(pq.questionId)" @change="(e: any) => setAnswer(pq.questionId, e.target.value)" class="options-group">
                <a-radio v-for="opt in parseOptions(pq.question.optionsText)" :key="opt.key" :value="opt.key" class="option-item">
                  <span class="option-key">{{ opt.key }}</span>
                  <span class="option-label">{{ opt.label }}</span>
                </a-radio>
              </a-radio-group>
            </template>
            <template v-else-if="pq.question?.questionType === 3">
              <a-checkbox-group :value="getMultiAnswer(pq.questionId)" @change="(vals: any) => setAnswer(pq.questionId, vals)" class="options-group">
                <a-checkbox v-for="opt in parseOptions(pq.question.optionsText)" :key="opt.key" :value="opt.key" class="option-item">
                  <span class="option-key">{{ opt.key }}</span>
                  <span class="option-label">{{ opt.label }}</span>
                </a-checkbox>
              </a-checkbox-group>
            </template>
            <template v-else-if="pq.question?.questionType === 4">
              <a-radio-group :value="getAnswer(pq.questionId)" @change="(e: any) => setAnswer(pq.questionId, e.target.value)" class="judge-options">
                <a-radio value="1" class="judge-option correct"><CheckCircleFilled class="judge-icon" /><span>正确</span></a-radio>
                <a-radio value="0" class="judge-option wrong"><CloseCircleFilled class="judge-icon" /><span>错误</span></a-radio>
              </a-radio-group>
            </template>
            <template v-else-if="pq.question?.questionType === 5 || pq.question?.questionType === 6 || pq.question?.questionType === 7">
              <a-textarea :value="getAnswer(pq.questionId) as string" @change="(e: any) => setAnswer(pq.questionId, e.target.value)" :rows="pq.question?.questionType === 6 ? 10 : 6" :placeholder="getPlaceholder(pq.question?.questionType)" class="answer-textarea" />
            </template>
          </div>
        </div>
      </main>

      <aside class="answer-card-panel">
        <div class="answer-card">
          <h3 class="card-title">答题卡</h3>
          <div class="card-grid">
            <div v-for="(pq, index) in currentPaper.questions" :key="pq.id" class="card-item" :class="{ answered: isAnswered(pq.questionId) }" @click="scrollToQuestion(index + 1)">{{ index + 1 }}</div>
          </div>
          <div class="card-legend">
            <span class="legend-item"><span class="dot answered"></span>已答</span>
            <span class="legend-item"><span class="dot"></span>未答</span>
          </div>
          <div class="card-stats"><span>已答 {{ answeredCount }} / {{ currentPaper.questions.length }} 题</span></div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoute, useRouter } from 'vue-router'
import { Modal, message } from 'ant-design-vue'
import { ClockCircleOutlined, CheckCircleFilled, CloseCircleFilled } from '@ant-design/icons-vue'
import { useExamStudentStore } from '@/stores/exam/student'
import { QUESTION_TYPE_MAP, type Exam, type QuestionType } from '@/stores/exam/types'

const route = useRoute()
const router = useRouter()
const examStore = useExamStudentStore()
const { currentExam, currentPaper, loading } = storeToRefs(examStore)

const submitting = ref(false)
const remainingTime = ref(0)
let timerInterval: ReturnType<typeof setInterval> | null = null

const answeredCount = computed(() => currentPaper.value ? currentPaper.value.questions.filter((pq) => isAnswered(pq.questionId)).length : 0)

function formatTime(seconds: number): string {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  return h > 0 ? `${h}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}` : `${m}:${String(s).padStart(2, '0')}`
}

function getQuestionTypeName(type?: QuestionType): string {
  return type ? QUESTION_TYPE_MAP[type] || '未知' : '未知'
}

function parseOptions(optionsText: string | null): { key: string; label: string }[] {
  if (!optionsText) return []
  try { return JSON.parse(optionsText) } catch { return [] }
}

function getAnswer(questionId: number): string | string[] | undefined {
  return examStore.getAnswer(questionId)
}

function getMultiAnswer(questionId: number): string[] {
  const ans = examStore.getAnswer(questionId)
  return Array.isArray(ans) ? ans : []
}

function setAnswer(questionId: number, value: string | string[]): void {
  examStore.setAnswer(questionId, value)
}

function isAnswered(questionId: number): boolean {
  const ans = examStore.getAnswer(questionId)
  if (ans === undefined || ans === null) return false
  if (typeof ans === 'string') return ans.trim().length > 0
  return Array.isArray(ans) && ans.length > 0
}

function renderFillBlankQuestion(question: any, questionId: number): string {
  if (!question) return ''
  const answerValue = examStore.getAnswer(questionId)
  const answerArr = typeof answerValue === 'string' ? answerValue.split(',') : []
  let blankIndex = 0
  return String(question.questionContent || '').replace(/____/g, () => {
    const idx = blankIndex++
    const value = answerArr[idx] || ''
    return `<input type="text" class="fill-blank-input" data-question-id="${questionId}" data-blank-index="${idx}" value="${value.replace(/"/g, '&quot;')}" placeholder="填写答案" />`
  })
}

function getPlaceholder(type?: QuestionType): string {
  if (type === 6) return '请在此处编写代码...'
  if (type === 7) return '请详细作答，可以分点阐述...'
  return '请输入您的答案...'
}

function scrollToQuestion(index: number): void {
  document.getElementById(`question-${index}`)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function confirmBack(): void {
  Modal.confirm({
    title: '确认退出？',
    content: '退出后答题进度不会保存，确定要退出吗？',
    okText: '确定退出',
    cancelText: '继续答题',
    onOk: () => {
      examStore.resetExam()
      router.push('/exams')
    },
  })
}

function confirmSubmit(): void {
  const unanswered = (currentPaper.value?.questions.length || 0) - answeredCount.value
  Modal.confirm({
    title: '确认提交？',
    content: unanswered > 0 ? `您还有 ${unanswered} 道题未作答，确定要提交吗？` : '提交后将无法再次进入本场考试，确定要提交吗？',
    okText: '确定提交',
    cancelText: unanswered > 0 ? '继续答题' : '再检查一下',
    onOk: submitExam,
  })
}

async function submitExam(): Promise<void> {
  submitting.value = true
  try {
    const result = await examStore.submitExam()
    if (!result || !currentExam.value) return
    if (timerInterval) {
      clearInterval(timerInterval)
      timerInterval = null
    }
    message.success('考试已成功提交')
    router.replace(`/exams/${currentExam.value.id}/result`)
  } catch {
    message.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

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

function handleFillBlankInput(event: Event): void {
  const target = event.target as HTMLInputElement
  if (target.classList.contains('fill-blank-input')) {
    const questionId = parseInt(target.dataset.questionId || '0')
    const blankIndex = parseInt(target.dataset.blankIndex || '0')
    examStore.setFillBlankAnswer(questionId, blankIndex, target.value)
  }
}

onMounted(async () => {
  const examId = parseInt(route.params.id as string)
  await examStore.ensureLoaded()
  const submitted = examStore.loadSubmissionResult(examId)
  if (submitted) {
    router.replace(`/exams/${examId}/result`)
    return
  }
  const examMeta: Exam | null = examStore.getExamFromList(examId)
  if (examMeta && !examStore.canEnterExam(examMeta)) {
    message.warning('当前考试尚未开始或已结束，无法进入答题')
    router.replace('/exams')
    return
  }
  const success = await examStore.loadExamDetail(examId)
  if (!success) {
    message.error('考试不存在或未发布')
    router.replace('/exams')
    return
  }
  startTimer()
  await nextTick()
  document.addEventListener('input', handleFillBlankInput)
})

onUnmounted(() => {
  if (timerInterval) clearInterval(timerInterval)
  document.removeEventListener('input', handleFillBlankInput)
})
</script>

<style scoped>
.exam-take-page { min-height: 100vh; background: #f5f7fa; }
.exam-header { position: sticky; top: 0; z-index: 100; display: flex; justify-content: space-between; align-items: center; padding: 16px 24px; background: #fff; border-bottom: 1px solid #e8e8e8; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06); }
.header-left { display: flex; align-items: center; gap: 16px; }
.exam-title { margin: 0; font-size: 24px; font-weight: 600; }
.exam-meta { color: #8c8c8c; }
.header-right { display: flex; align-items: center; gap: 12px; }
.timer { display: inline-flex; align-items: center; gap: 6px; padding: 10px 14px; border-radius: 999px; background: #f0f5ff; color: #1677ff; font-weight: 600; }
.timer.warning { background: #fffbe6; color: #faad14; }
.timer.danger { background: #fff1f0; color: #f5222d; }
.exam-content { display: grid; grid-template-columns: minmax(0, 1fr) 280px; gap: 24px; padding: 24px; }
.questions-panel { display: flex; flex-direction: column; gap: 20px; }
.question-card, .answer-card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 6px 18px rgba(15, 23, 42, 0.05); }
.question-header { display: flex; justify-content: space-between; gap: 12px; margin-bottom: 16px; }
.question-meta, .options-group, .judge-options { display: flex; flex-direction: column; gap: 12px; }
.question-number { font-weight: 600; }
.question-score { color: #1677ff; font-weight: 600; }
.question-content { margin-bottom: 16px; line-height: 1.8; }
.question-text { white-space: pre-wrap; }
.option-item, .judge-option { display: flex; align-items: center; gap: 8px; }
.answer-textarea { width: 100%; }
.answer-card-panel { position: sticky; top: 96px; align-self: start; }
.card-grid { display: grid; grid-template-columns: repeat(5, minmax(0, 1fr)); gap: 10px; margin-top: 16px; }
.card-item { height: 42px; display: flex; align-items: center; justify-content: center; border-radius: 10px; background: #f5f5f5; cursor: pointer; font-weight: 600; }
.card-item.answered { background: #e6f4ff; color: #1677ff; }
.card-legend, .card-stats { display: flex; gap: 16px; margin-top: 16px; color: #8c8c8c; }
.dot { display: inline-block; width: 10px; height: 10px; border-radius: 50%; background: #d9d9d9; }
.dot.answered { background: #1677ff; }
.loading-container { display: flex; justify-content: center; padding: 120px 24px; }
@media (max-width: 1100px) { .exam-content { grid-template-columns: 1fr; } .answer-card-panel { position: static; } }
@media (max-width: 720px) { .exam-header { align-items: flex-start; flex-direction: column; } .header-left, .header-right { flex-wrap: wrap; } }
</style>
