<template>
  <div class="answer-sheet-page">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="header-info">
        <h1 class="experiment-title">{{ experimentTitle }}</h1>
        <p class="experiment-meta">
          <span class="meta-item">
            <FileTextOutlined />
            共 {{ totalQuestions }} 题
          </span>
          <span class="meta-item">
            <StarOutlined />
            满分 {{ totalScore }} 分
          </span>
          <span class="meta-item" :class="{ 'meta-item--saved': lastSaved }">
            <SyncOutlined :spin="isAutoSaving" />
            {{ autoSaveStatus }}
          </span>
        </p>
      </div>
      <div class="header-actions">
        <a-button @click="handleSaveDraft" :loading="isSaving">
          <SaveOutlined />
          保存草稿
        </a-button>
        <a-button type="primary" @click="handleSubmit" :loading="isSubmitting">
          <CheckOutlined />
          提交报告
        </a-button>
      </div>
    </header>

    <!-- 答题卡片主体 -->
    <main class="answer-sheet-content">
      <div class="questions-container">
        <template v-if="isLoading">
          <a-spin size="large" class="loading-spinner">
            <template #tip>正在加载题目...</template>
          </a-spin>
        </template>

        <template v-else-if="questions.length === 0">
          <a-empty description="暂无题目">
            <a-button type="primary" @click="router.push(`/experiments/${experimentId}`)">
              返回实验详情
            </a-button>
          </a-empty>
        </template>

        <template v-else>
          <!-- 实验目标说明 -->
          <section v-if="experimentObjective" class="instruction-section">
            <h2 class="section-label">
              <BookOutlined />
              实验目标
            </h2>
            <div class="instruction-content">{{ experimentObjective }}</div>
          </section>

          <!-- 题目列表 -->
          <ExperimentStepItem
            v-for="(question, index) in questions"
            :key="question.id"
            :step-number="index + 1"
            :title="question.title"
            :type="question.type"
            :score="question.score"
            :is-answered="isQuestionAnswered(question.id)"
            :last-saved="getAnswerTime(question.id)"
          >
            <template #question>
              <!-- 选择题 -->
              <SingleChoiceQuestion
                v-if="question.type === 1"
                :question-content="question.content"
                :options="question.options || []"
                :model-value="getSimpleAnswer(question.id)"
                @update:model-value="(val) => handleSimpleAnswerChange(question.id, val)"
              />

              <!-- 填空题 -->
              <FillBlankQuestion
                v-else-if="question.type === 2"
                :content="question.content"
                :model-value="getFillBlanksAnswer(question.id)"
                @update:model-value="(val) => handleFillBlanksChange(question.id, val)"
              />

              <!-- 编程题 -->
              <CodeQuestion
                v-else-if="question.type === 3"
                :question-content="question.content"
                :language="question.language"
                :allow-paste="question.allowPaste"
                :model-value="getSimpleAnswer(question.id)"
                @update:model-value="(val) => handleSimpleAnswerChange(question.id, val)"
              />

              <!-- 简答题 -->
              <ShortAnswerQuestion
                v-else-if="question.type === 4"
                :question-content="question.content"
                :model-value="getSimpleAnswer(question.id)"
                @update:model-value="(val) => handleSimpleAnswerChange(question.id, val)"
              />

              <!-- 多选题（类型 5） -->
              <MultipleChoiceQuestion
                v-else-if="question.type === 5"
                :question-content="question.content"
                :options="question.options || []"
                :model-value="getMultipleAnswer(question.id)"
                @update:model-value="(val) => handleMultipleAnswerChange(question.id, val)"
              />

              <!-- 判断题（类型 6） -->
              <JudgmentQuestion
                v-else-if="question.type === 6"
                :question-content="question.content"
                :model-value="getSimpleAnswer(question.id)"
                @update:model-value="(val) => handleSimpleAnswerChange(question.id, val)"
              />

              <!-- 其他题型（兜底） -->
              <ShortAnswerQuestion
                v-else
                :question-content="question.content"
                :model-value="getSimpleAnswer(question.id)"
                @update:model-value="(val) => handleSimpleAnswerChange(question.id, val)"
              />
            </template>
          </ExperimentStepItem>
        </template>
      </div>

      <!-- 侧边进度条 -->
      <aside class="progress-sidebar">
        <div class="progress-card">
          <h3 class="progress-title">答题进度</h3>
          <div class="progress-stats">
            <div class="stat-item">
              <span class="stat-value answered">{{ answeredCount }}</span>
              <span class="stat-label">已答</span>
            </div>
            <div class="stat-item">
              <span class="stat-value unanswered">{{ totalQuestions - answeredCount }}</span>
              <span class="stat-label">未答</span>
            </div>
          </div>
          <a-progress
            :percent="progressPercent"
            :show-info="false"
            stroke-color="#52c41a"
            trail-color="#f0f0f0"
          />
          <div class="progress-percent">{{ progressPercent }}%</div>
        </div>

        <div class="questions-nav">
          <h4 class="nav-title">题目导航</h4>
          <div class="nav-grid">
            <button
              v-for="(question, index) in questions"
              :key="question.id"
              class="nav-item"
              :class="{
                'nav-item--answered': isQuestionAnswered(question.id),
                'nav-item--current': currentQuestionIndex === index
              }"
              @click="scrollToQuestion(index)"
            >
              {{ index + 1 }}
            </button>
          </div>
        </div>

        <div class="submit-section">
          <a-button
            type="primary"
            block
            size="large"
            :disabled="answeredCount === 0"
            @click="handleSubmit"
          >
            提交全部报告
          </a-button>
        </div>
      </aside>
    </main>

    <!-- 提交确认弹窗 -->
    <a-modal
      v-model:open="submitModalVisible"
      title="确认提交报告"
      :width="460"
      @ok="confirmSubmit"
      @cancel="submitModalVisible = false"
    >
      <div class="submit-confirm-content">
        <p v-if="unansweredCount > 0" class="warning-text">
          <WarningOutlined />
          您还有 <strong>{{ unansweredCount }}</strong> 道题未作答
        </p>
        <p>确认提交后将无法再次修改报告。</p>
        <div class="confirm-summary">
          <span>已答：{{ answeredCount }} 题</span>
          <span>未答：{{ unansweredCount }} 题</span>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  FileTextOutlined,
  StarOutlined,
  SyncOutlined,
  SaveOutlined,
  CheckOutlined,
  BookOutlined,
  WarningOutlined
} from '@ant-design/icons-vue'
import ExperimentStepItem from '@/components/experiment/ExperimentStepItem.vue'
import FillBlankQuestion from '@/components/experiment/question/FillBlankQuestion.vue'
import SingleChoiceQuestion from '@/components/experiment/question/SingleChoiceQuestion.vue'
import MultipleChoiceQuestion from '@/components/experiment/question/MultipleChoiceQuestion.vue'
import CodeQuestion from '@/components/experiment/question/CodeQuestion.vue'
import JudgmentQuestion from '@/components/experiment/question/JudgmentQuestion.vue'
import ShortAnswerQuestion from '@/components/experiment/question/ShortAnswerQuestion.vue'
import { useExperimentStudentStore } from '@/stores/experiment/student'
import type {
  ExperimentQuestion,
  StudentAnswer,
  AnswerSaveRequest
} from '@/stores/experiment/types'

const route = useRoute()
const router = useRouter()
const experimentStore = useExperimentStudentStore()

// 页面状态
const isLoading = ref(true)
const isSaving = ref(false)
const isSubmitting = ref(false)
const isAutoSaving = ref(false)
const lastSaved = ref<string | null>(null)
const submitModalVisible = ref(false)
const currentQuestionIndex = ref(0)

// 实验数据
const experimentId = computed(() => String(route.params.id))
const experimentTitle = ref('')
const experimentObjective = ref('')

// 题目列表
const questions = ref<ExperimentQuestion[]>([])

// 答题记录 Map
const answersMap = ref<Map<string, StudentAnswer>>(new Map())

// 自动保存定时器
let autoSaveTimer: ReturnType<typeof setTimeout> | null = null
const AUTO_SAVE_INTERVAL = 30000 // 30秒自动保存

// 计算属性
const totalQuestions = computed(() => questions.value.length)

const totalScore = computed(() => {
  return questions.value.reduce((sum, q) => sum + q.score, 0)
})

const answeredCount = computed(() => {
  let count = 0
  answersMap.value.forEach((answer) => {
    if (answer.answer && answer.answer.trim() !== '') {
      count++
    } else if (answer.filledBlanks && answer.filledBlanks.length > 0 && answer.filledBlanks.some(b => b.trim() !== '')) {
      count++
    }
  })
  return count
})

const unansweredCount = computed(() => totalQuestions.value - answeredCount.value)

const progressPercent = computed(() => {
  if (totalQuestions.value === 0) return 0
  return Math.round((answeredCount.value / totalQuestions.value) * 100)
})

const autoSaveStatus = computed(() => {
  if (isAutoSaving.value) return '自动保存中...'
  if (isSaving.value) return '保存中...'
  if (lastSaved.value) return `上次保存: ${lastSaved.value}`
  return '尚未保存'
})

// 方法
function isQuestionAnswered(questionId: string): boolean {
  const answer = answersMap.value.get(questionId)
  if (!answer) return false
  if (answer.answer && answer.answer.trim() !== '') return true
  if (answer.filledBlanks && answer.filledBlanks.length > 0 && answer.filledBlanks.some(b => b.trim() !== '')) {
    return true
  }
  return false
}

function getAnswerTime(questionId: string): string {
  const answer = answersMap.value.get(questionId)
  return answer?.answeredAt || ''
}

function getSimpleAnswer(questionId: string): string {
  const answer = answersMap.value.get(questionId)
  return answer?.answer || ''
}

function getFillBlanksAnswer(questionId: string): string[] {
  const answer = answersMap.value.get(questionId)
  return answer?.filledBlanks || []
}

function getMultipleAnswer(questionId: string): string[] {
  const answer = answersMap.value.get(questionId)
  if (!answer?.answer) return []
  return answer.answer.split(',').filter(Boolean)
}

function handleSimpleAnswerChange(questionId: string, value: string) {
  updateAnswer(questionId, value)
  scheduleAutoSave()
}

function handleFillBlanksChange(questionId: string, values: string[]) {
  updateAnswerWithBlanks(questionId, values.join(','), values)
  scheduleAutoSave()
}

function handleMultipleAnswerChange(questionId: string, values: string[]) {
  updateAnswer(questionId, values.sort().join(','))
  scheduleAutoSave()
}

function updateAnswer(questionId: string, answer: string) {
  const existing = answersMap.value.get(questionId) || { questionId }
  answersMap.value.set(questionId, {
    ...existing,
    answer,
    answeredAt: new Date().toLocaleString()
  })
}

function updateAnswerWithBlanks(questionId: string, answer: string, filledBlanks: string[]) {
  const existing = answersMap.value.get(questionId) || { questionId }
  answersMap.value.set(questionId, {
    ...existing,
    answer,
    filledBlanks,
    answeredAt: new Date().toLocaleString()
  })
}

function scrollToQuestion(index: number) {
  currentQuestionIndex.value = index
  const container = document.querySelector('.questions-container')
  const items = container?.querySelectorAll('.experiment-step-item') || []
  if (items[index]) {
    items[index].scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

// 加载实验数据
async function loadExperiment() {
  isLoading.value = true
  try {
    // 获取实验信息
    const experiment = await experimentStore.selectExperiment(experimentId.value)
    console.log('[AnswerSheet] 获取到的实验:', experiment)
    if (!experiment) {
      message.error('实验不存在')
      router.push('/experiments')
      return
    }

    experimentTitle.value = experiment.title
    experimentObjective.value = experiment.objective || ''

    // 从真实 API 获取题目数据
    const fetchedQuestions = await experimentStore.loadExperimentQuestions(experimentId.value)
    console.log('[AnswerSheet] 获取到的题目:', fetchedQuestions)
    questions.value = fetchedQuestions

    // 加载已保存的答题记录
    await loadSavedAnswers()

  } catch (error) {
    message.error('加载实验失败')
    console.error(error)
  } finally {
    isLoading.value = false
  }
}

// 加载已保存的答题记录
async function loadSavedAnswers(): Promise<void> {
  try {
    // 优先从后端加载已保存的答题记录
    const savedAnswers = await experimentStore.getSavedAnswers(experimentId.value)

    if (savedAnswers && savedAnswers.size > 0) {
      savedAnswers.forEach((answer, questionId) => {
        answersMap.value.set(questionId, {
          questionId,
          answer: answer.answer,
          filledBlanks: answer.filledBlanks,
          answeredAt: ''
        })
      })
      lastSaved.value = '已加载草稿'
    }

    // 同时检查本地存储
    const localDraft = localStorage.getItem(`experiment-draft-${experimentId.value}`)
    if (localDraft) {
      try {
        const draft = JSON.parse(localDraft)
        draft.answers?.forEach((a: { questionId: string; answer: string; filledBlanks?: string[] }) => {
          if (!answersMap.value.has(a.questionId)) {
            answersMap.value.set(a.questionId, {
              questionId: a.questionId,
              answer: a.answer,
              filledBlanks: a.filledBlanks,
              answeredAt: ''
            })
          }
        })
        if (!lastSaved.value) {
          lastSaved.value = '本地草稿'
        }
      } catch {
        // 忽略解析错误
      }
    }
  } catch (error) {
    console.error('加载已保存答题记录失败:', error)
  }
}

// 保存草稿
async function handleSaveDraft() {
  isSaving.value = true
  try {
    const request = buildSaveRequest()
    const success = await experimentStore.saveAnswers(request)

    lastSaved.value = new Date().toLocaleTimeString()
    message.success('保存成功')

    // 同时保存到本地作为备份
    localStorage.setItem(`experiment-draft-${experimentId.value}`, JSON.stringify({
      experimentId: experimentId.value,
      answers: Array.from(answersMap.value.values()),
      savedAt: new Date().toISOString()
    }))

  } catch (error) {
    message.error('保存失败')
    console.error(error)
  } finally {
    isSaving.value = false
  }
}

// 自动保存
function scheduleAutoSave() {
  if (autoSaveTimer) {
    clearTimeout(autoSaveTimer)
  }
  autoSaveTimer = setTimeout(() => {
    autoSave()
  }, AUTO_SAVE_INTERVAL)
}

async function autoSave() {
  if (isAutoSaving.value || answersMap.value.size === 0) return

  isAutoSaving.value = true
  try {
    const request = buildSaveRequest()
    await experimentStore.saveAnswers(request)
    lastSaved.value = new Date().toLocaleTimeString()
  } catch {
    // 自动保存失败不提示，避免干扰
  } finally {
    isAutoSaving.value = false
  }
}

function buildSaveRequest(): AnswerSaveRequest {
  const answers: Array<{ questionId: string; answer: string; filledBlanks?: string[] }> = []
  answersMap.value.forEach((answer, questionId) => {
    answers.push({
      questionId,
      answer: answer.answer,
      filledBlanks: answer.filledBlanks
    })
  })
  return {
    experimentId: experimentId.value,
    answers,
    isSubmit: false
  }
}

// 提交答案
function handleSubmit() {
  submitModalVisible.value = true
}

async function confirmSubmit() {
  submitModalVisible.value = false
  isSubmitting.value = true

  try {
    const request = buildSaveRequest()
    request.isSubmit = true
    await experimentStore.submitAnswers(request)

    // 清除本地草稿
    localStorage.removeItem(`experiment-draft-${experimentId.value}`)

    message.success('提交成功！现在可以查看实验报告')

    // 跳转到报告页面
    router.push(`/experiments/${experimentId.value}/report`)

  } catch (error) {
    message.error('提交失败')
    console.error(error)
  } finally {
    isSubmitting.value = false
  }
}

// 生命周期
onMounted(() => {
  loadExperiment()

  // 绑定键盘事件（防止粘贴快捷键）
  document.addEventListener('keydown', handleGlobalKeyDown)
})

onUnmounted(() => {
  if (autoSaveTimer) {
    clearTimeout(autoSaveTimer)
  }
  document.removeEventListener('keydown', handleGlobalKeyDown)
})

// 全局键盘事件处理（防作弊）
function handleGlobalKeyDown(e: KeyboardEvent) {
  // 检查是否在代码编辑器中且禁止粘贴
  const codeQuestions = questions.value.filter(q => q.type >= 5 && !q.allowPaste)
  if (codeQuestions.length > 0) {
    // 如果按下 Ctrl+V / Cmd+V，阻止默认行为
    if ((e.ctrlKey || e.metaKey) && e.key === 'v') {
      const activeElement = document.activeElement
      if (activeElement?.tagName === 'TEXTAREA' || activeElement?.tagName === 'INPUT') {
        // 检查是否在代码编辑器中
        const container = (activeElement as HTMLElement).closest('.code-question')
        if (container) {
          e.preventDefault()
        }
      }
    }
  }
}
</script>

<style scoped>
.answer-sheet-page {
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

.experiment-title {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
}

.experiment-meta {
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

.meta-item--saved {
  color: #52c41a;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 内容区域 */
.answer-sheet-content {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 24px;
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.questions-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.loading-spinner {
  display: flex;
  justify-content: center;
  padding: 60px 0;
}

/* 说明区域 */
.instruction-section {
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e8e8e8;
}

.section-label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-primary, #1890ff);
}

.instruction-content {
  font-size: 15px;
  line-height: 1.8;
  color: var(--color-text-secondary, #595959);
}

/* 侧边栏 */
.progress-sidebar {
  position: sticky;
  top: 88px;
  height: fit-content;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.progress-card {
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e8e8e8;
}

.progress-title {
  margin: 0 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.progress-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
}

.stat-value.answered {
  color: #52c41a;
}

.stat-value.unanswered {
  color: #8c8c8c;
}

.stat-label {
  font-size: 12px;
  color: #8c8c8c;
}

.progress-percent {
  text-align: center;
  margin-top: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #52c41a;
}

/* 题目导航 */
.questions-nav {
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e8e8e8;
}

.nav-title {
  margin: 0 0 14px;
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  background: #fff;
  font-size: 14px;
  font-weight: 500;
  color: #595959;
  cursor: pointer;
  transition: all 0.2s ease;
}

.nav-item:hover {
  border-color: var(--color-primary, #1890ff);
  color: var(--color-primary, #1890ff);
}

.nav-item--answered {
  background: #f6ffed;
  border-color: #b7eb8f;
  color: #52c41a;
}

.nav-item--current {
  background: var(--color-primary, #1890ff);
  border-color: var(--color-primary, #1890ff);
  color: #fff;
}

/* 提交区域 */
.submit-section {
  margin-top: auto;
}

/* 提交确认弹窗 */
.submit-confirm-content {
  padding: 8px 0;
}

.warning-text {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fffbe6;
  border-radius: 8px;
  border: 1px solid #ffe58f;
  color: #fa8c16;
  margin-bottom: 16px;
}

.warning-text strong {
  color: #d46b08;
}

.confirm-summary {
  display: flex;
  gap: 24px;
  margin-top: 16px;
  font-size: 14px;
  color: #595959;
}

/* 响应式 */
@media (max-width: 1024px) {
  .answer-sheet-content {
    grid-template-columns: 1fr;
  }
  
  .progress-sidebar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    flex-direction: row;
    padding: 12px;
    background: #fff;
    border-top: 1px solid #e8e8e8;
    box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
    z-index: 99;
  }
  
  .progress-card {
    flex: 1;
    padding: 12px;
  }
  
  .questions-nav {
    display: none;
  }
  
  .submit-section {
    margin-top: 0;
  }
  
  .questions-container {
    padding-bottom: 80px;
  }
}
</style>