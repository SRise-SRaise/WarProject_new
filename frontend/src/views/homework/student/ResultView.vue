<template>
  <div class="app-page-shell app-panel-grid" v-if="reviewData">
    <div class="hw-page-header">
      <div class="hw-page-header__left">
        <h1 class="hw-page-header__title">{{ reviewData.title }} · 成绩反馈</h1>
        <p class="hw-page-header__desc">{{ reviewData.summary }}</p>
      </div>
      <div class="hw-page-header__actions">
        <a-button @click="router.push(`/homework/${reviewData.id}/do`)">回到做作业</a-button>
        <a-button type="primary" @click="router.push('/homework')">返回作业列表</a-button>
      </div>
    </div>

    <a-spin :spinning="loading">
      <section class="review-layout">
        <section class="review-main app-panel-grid">
          <section class="app-surface-card app-section-card app-panel-grid">
            <div class="summary-head">
              <h2 class="summary-title">学习结果总览</h2>
              <span class="summary-status-pill" :class="statusClass(reviewData.status)">{{ statusText(reviewData.status) }}</span>
            </div>
            <div class="summary-grid">
              <article class="summary-metric">
                <p class="summary-metric__label">总分</p>
                <p class="summary-metric__value">{{ reviewData.totalScore }} <span>/ {{ reviewData.maxScore }}</span></p>
              </article>
              <article class="summary-metric">
                <p class="summary-metric__label">提交时间</p>
                <p class="summary-metric__value summary-metric__value--sm">{{ reviewData.submittedAt || '未提交' }}</p>
              </article>
              <article class="summary-metric">
                <p class="summary-metric__label">正确题数</p>
                <p class="summary-metric__value">{{ fullyCorrectCount }} <span>/ {{ reviewData.items.length }}</span></p>
              </article>
            </div>
            <a-alert
              v-if="reviewData.overallComment"
              type="info"
              :message="`教师反馈：${reviewData.overallComment}`"
              show-icon
            />
          </section>

          <section
            v-for="(item, idx) in reviewData.items"
            :key="item.itemId"
            :id="questionDomId(item.itemId)"
            :ref="(el) => setQuestionRef(item.itemId, el)"
            class="app-surface-card app-section-card question-card"
            :class="{ 'question-card--active': activeQuestionId === item.itemId }"
          >
            <header class="question-card__head">
              <div class="question-card__meta">
                <span class="question-order">第 {{ idx + 1 }} 题</span>
                <span class="question-type">{{ questionTypeLabel(item.questionType) }}</span>
              </div>
              <span class="question-score">{{ item.score }} / {{ item.maxScore }}</span>
            </header>

            <h3 class="question-stem">{{ item.question || '题干缺失' }}</h3>

            <div v-if="isChoiceQuestion(item.questionType) && item.options.length > 0" class="option-list">
              <article
                v-for="opt in item.options"
                :key="`${item.itemId}_${opt.value}`"
                class="option-item"
                :class="optionClass(item, opt.value)"
              >
                <span class="option-item__tag">{{ opt.value }}</span>
                <span class="option-item__content">{{ opt.label }}</span>
              </article>
            </div>

            <div class="answer-row">
              <p class="answer-row__label">标准答案</p>
              <p class="answer-row__value answer-row__value--correct">{{ displayAnswer(item.correctAnswer) }}</p>
            </div>

            <div class="answer-row">
              <p class="answer-row__label">你的答案</p>
              <p
                class="answer-row__value"
                :class="{
                  'answer-row__value--correct': item.isCorrect,
                  'answer-row__value--wrong': !item.isCorrect && !!item.studentAnswer,
                  'answer-row__value--empty': !item.studentAnswer
                }"
              >
                {{ displayAnswer(item.studentAnswer) }}
              </p>
            </div>

            <p v-if="item.comment" class="teacher-comment">教师评语：{{ item.comment }}</p>
          </section>
        </section>

        <aside class="review-side app-surface-card app-section-card">
          <h2 class="sheet-title">答题卡</h2>
          <div class="sheet-summary">
            <span class="sheet-tag sheet-tag--full">正确 {{ fullyCorrectCount }}</span>
            <span class="sheet-tag sheet-tag--partial">部分得分 {{ partialCorrectCount }}</span>
            <span class="sheet-tag sheet-tag--wrong">错误 {{ wrongCount }}</span>
            <span class="sheet-tag sheet-tag--empty">未作答 {{ unansweredCount }}</span>
          </div>
          <div class="sheet-grid">
            <button
              v-for="(item, idx) in reviewData.items"
              :key="`sheet_${item.itemId}`"
              type="button"
              class="sheet-index"
              :class="[sheetClass(item), { 'sheet-index--active': activeQuestionId === item.itemId }]"
              @click="scrollToQuestion(item.itemId)"
            >
              {{ idx + 1 }}
            </button>
          </div>
        </aside>
      </section>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/user/auth'
import { getMyExerciseScore, getSubmissionDetail } from '@/api/eduExerciseSubmissionController'
import { getEduExerciseVoById } from '@/api/eduExerciseController'

interface QuestionOption {
  value: string
  label: string
}

interface ReviewItem {
  itemId: number
  itemOrder: number
  question: string
  questionType: number
  maxScore: number
  score: number
  studentAnswer: string
  correctAnswer: string
  comment?: string
  options: QuestionOption[]
  isCorrect: boolean
}

interface ReviewData {
  id: string
  title: string
  summary: string
  status: string
  totalScore: number
  maxScore: number
  submittedAt: string
  overallComment?: string
  items: ReviewItem[]
}

type SheetState = 'full' | 'partial' | 'wrong' | 'empty'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const reviewData = ref<ReviewData | null>(null)
const activeQuestionId = ref<number | null>(null)
const questionRefs = new Map<number, HTMLElement>()
let activeTimer: number | null = null

const fullyCorrectCount = computed(() => {
  if (!reviewData.value) return 0
  return reviewData.value.items.filter((item) => item.maxScore > 0 && item.score >= item.maxScore).length
})

const partialCorrectCount = computed(() => {
  if (!reviewData.value) return 0
  return reviewData.value.items.filter((item) => item.score > 0 && item.score < item.maxScore).length
})

const unansweredCount = computed(() => {
  if (!reviewData.value) return 0
  return reviewData.value.items.filter((item) => !item.studentAnswer).length
})

const wrongCount = computed(() => {
  if (!reviewData.value) return 0
  return reviewData.value.items.filter((item) => item.score <= 0 && !!item.studentAnswer).length
})

function formatDateTime(value: unknown): string {
  if (value === null || value === undefined || String(value).trim().length === 0) {
    return ''
  }
  const parsed = dayjs(String(value))
  if (!parsed.isValid()) {
    return String(value)
  }
  return parsed.format('YYYY-MM-DD HH:mm')
}

function normalizeAnswer(answer: string, questionType: number): string {
  const compact = answer.toUpperCase().replace(/[^A-Z0-9]/g, '')
  if (questionType === 3) {
    return compact.split('').sort().join('')
  }
  return compact
}

function parseOptions(optionsText: string): QuestionOption[] {
  if (!optionsText) return []
  const segments = optionsText
    .split(',')
    .map((item) => item.trim())
    .filter((item) => item.length > 0)
  return segments.map((segment, index) => {
    const matched = segment.match(/^([A-Z])[.、:：\s-]*(.*)$/i)
    const fallback = String.fromCharCode(65 + index)
    if (!matched) {
      return { value: fallback, label: segment }
    }
    return {
      value: matched[1].toUpperCase(),
      label: matched[2]?.trim() || `${matched[1].toUpperCase()} 选项`
    }
  })
}

function parseQuestionOptions(questionType: number, optionsText: string): QuestionOption[] {
  const parsed = parseOptions(optionsText)
  if (parsed.length > 0) {
    return parsed
  }
  if (questionType === 4) {
    return [
      { value: 'T', label: '正确' },
      { value: 'F', label: '错误' }
    ]
  }
  return []
}

function isChoiceQuestion(questionType: number): boolean {
  return questionType === 2 || questionType === 3 || questionType === 4
}

function questionTypeLabel(questionType: number): string {
  const labels: Record<number, string> = {
    1: '填空题',
    2: '单选题',
    3: '多选题',
    4: '判断题',
    5: '简答题',
    6: '编程题',
    7: '综合题'
  }
  return labels[questionType] || '未知题型'
}

function statusText(status: string): string {
  const statusMap: Record<string, string> = {
    submitted: '已提交（待批阅）',
    reviewed: '已批阅',
    pending: '未提交'
  }
  return statusMap[status] || status
}

function statusClass(status: string): string {
  if (status === 'reviewed') return 'summary-status-pill--reviewed'
  if (status === 'submitted') return 'summary-status-pill--submitted'
  return 'summary-status-pill--pending'
}

function displayAnswer(value: string): string {
  return value && value.trim().length > 0 ? value : '未作答'
}

function optionClass(item: ReviewItem, optionValue: string): string {
  const correctSet = normalizeAnswer(item.correctAnswer, item.questionType)
  const studentSet = normalizeAnswer(item.studentAnswer, item.questionType)
  if (correctSet.includes(optionValue)) {
    return 'option-item--correct'
  }
  if (studentSet.includes(optionValue)) {
    return 'option-item--wrong'
  }
  return ''
}

function sheetState(item: ReviewItem): SheetState {
  if (!item.studentAnswer) return 'empty'
  if (item.maxScore > 0 && item.score >= item.maxScore) return 'full'
  if (item.score > 0) return 'partial'
  return 'wrong'
}

function sheetClass(item: ReviewItem): string {
  const state = sheetState(item)
  if (state === 'full') return 'sheet-index--full'
  if (state === 'partial') return 'sheet-index--partial'
  if (state === 'wrong') return 'sheet-index--wrong'
  return 'sheet-index--empty'
}

function setQuestionRef(itemId: number, element: Element | null): void {
  if (element instanceof HTMLElement) {
    questionRefs.set(itemId, element)
    return
  }
  questionRefs.delete(itemId)
}

function questionDomId(itemId: number): string {
  return `hw-score-question-${itemId}`
}

function scrollToQuestion(itemId: number): void {
  const target = questionRefs.get(itemId)
  if (!target) return
  target.scrollIntoView({ behavior: 'smooth', block: 'start' })
  activeQuestionId.value = itemId
  if (activeTimer !== null) {
    window.clearTimeout(activeTimer)
  }
  activeTimer = window.setTimeout(() => {
    activeQuestionId.value = null
  }, 1800)
}

async function loadScoreData() {
  const homeworkId = route.params.id as string
  const studentId = Number(authStore.session?.id) || 0

  if (!homeworkId || !studentId) {
    loading.value = false
    return
  }

  try {
    const [exerciseResponse, scoreResponse, detailResponse] = await Promise.all([
      getEduExerciseVoById({ id: homeworkId }),
      getMyExerciseScore({ exerciseId: Number(homeworkId), studentId }),
      getSubmissionDetail({ exerciseId: Number(homeworkId), studentId })
    ])

    const exercise = exerciseResponse.data?.data
    const score = scoreResponse.data?.data
    const detail = detailResponse.data?.data

    if (!exercise || !score) {
      return
    }

    const scoreMap = new Map<number, any>()
    for (const item of score.items || []) {
      const key = Number(item.itemId)
      if (Number.isFinite(key) && key > 0) {
        scoreMap.set(key, item)
      }
    }

    const detailItems = Array.isArray(detail?.answers) ? detail.answers : []
    const sourceItems = detailItems.length > 0 ? detailItems : (score.items || [])

    const mergedItems: ReviewItem[] = sourceItems.map((detailItem: any, index: number) => {
      const itemId = Number(detailItem.itemId) || 0
      const scoreItem = scoreMap.get(itemId) || {}
      const maxScore = Number(detailItem.maxScore ?? scoreItem.maxScore) || 0
      const studentScore = Number(detailItem.score ?? scoreItem.studentScore) || 0
      const questionType = Number(detailItem.questionType ?? scoreItem.questionType) || 0
      const studentAnswer = String(detailItem.studentAnswer ?? scoreItem.studentAnswer ?? '').trim()
      const correctAnswer = String(detailItem.standardAnswer ?? scoreItem.correctAnswer ?? '').trim()
      const isCorrect = questionType === 5 || questionType === 1
        ? studentScore >= maxScore && maxScore > 0
        : normalizeAnswer(studentAnswer, questionType) === normalizeAnswer(correctAnswer, questionType)

      return {
        itemId,
        itemOrder: Number(detailItem.itemOrder ?? scoreItem.itemOrder ?? index + 1) || index + 1,
        question: String(detailItem.question ?? scoreItem.question ?? ''),
        questionType,
        maxScore,
        score: studentScore,
        studentAnswer,
        correctAnswer,
        comment: detailItem.comment ?? scoreItem.comment,
        options: parseQuestionOptions(questionType, String(detailItem.optionsText ?? '')),
        isCorrect
      }
    })

    reviewData.value = {
      id: homeworkId,
      title: exercise.taskName || '',
      summary: exercise.description || '',
      status: score.status || 'submitted',
      totalScore: Number(score.totalScore) || 0,
      maxScore: Number(score.maxScore) || 0,
      submittedAt: formatDateTime(detail?.submittedAt),
      overallComment: score.overallComment,
      items: mergedItems
    }
  } catch (error) {
    console.error('加载成绩数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadScoreData()
})
</script>

<style scoped>
.review-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.9fr) minmax(320px, 1fr);
  gap: var(--space-5);
}

.summary-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.summary-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text-main);
}

.summary-status-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 32px;
  padding: 0 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  border: 1px solid transparent;
}

.summary-status-pill--reviewed {
  background: rgba(47, 143, 78, 0.12);
  border-color: rgba(47, 143, 78, 0.35);
  color: var(--color-success);
}

.summary-status-pill--submitted {
  background: rgba(31, 95, 174, 0.1);
  border-color: rgba(31, 95, 174, 0.3);
  color: var(--color-info);
}

.summary-status-pill--pending {
  background: rgba(183, 121, 31, 0.12);
  border-color: rgba(183, 121, 31, 0.28);
  color: var(--color-warning);
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.summary-metric {
  padding: 14px 16px;
  border-radius: var(--radius-md);
  border: 1px solid rgba(194, 206, 222, 0.6);
  background: var(--color-bg-muted);
}

.summary-metric__label {
  margin: 0;
  font-size: 12px;
  color: var(--color-text-tertiary);
}

.summary-metric__value {
  margin: 8px 0 0;
  font-size: 26px;
  line-height: 1;
  font-weight: 800;
  color: var(--color-primary-deep);
}

.summary-metric__value span {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-secondary);
}

.summary-metric__value--sm {
  font-size: 18px;
  line-height: 1.2;
}

.question-card {
  border-left: 4px solid transparent;
  transition: border-color var(--transition-base), box-shadow var(--transition-base);
}

.question-card--active {
  border-left-color: var(--color-primary);
  box-shadow: 0 10px 24px rgba(31, 95, 174, 0.16);
}

.question-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.question-card__meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.question-order {
  font-size: 14px;
  font-weight: 700;
  color: var(--color-text-main);
}

.question-type {
  padding: 2px 10px;
  border-radius: 999px;
  background: rgba(31, 95, 174, 0.08);
  border: 1px solid rgba(31, 95, 174, 0.2);
  font-size: 12px;
  color: var(--color-primary-deep);
}

.question-score {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-primary-deep);
}

.question-stem {
  margin: 12px 0 0;
  font-size: 18px;
  line-height: 1.5;
  font-weight: 700;
  color: var(--color-text-main);
}

.option-list {
  display: grid;
  gap: 10px;
  margin-top: 14px;
}

.option-item {
  display: grid;
  grid-template-columns: 28px minmax(0, 1fr);
  gap: 12px;
  align-items: center;
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  background: #fff;
}

.option-item__tag {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  color: var(--color-text-secondary);
  background: var(--color-bg-muted);
}

.option-item__content {
  font-size: 14px;
  color: var(--color-text-main);
  line-height: 1.5;
}

.option-item--correct {
  border-color: rgba(47, 143, 78, 0.56);
  background: rgba(47, 143, 78, 0.08);
}

.option-item--correct .option-item__tag {
  color: var(--color-success);
  background: rgba(47, 143, 78, 0.15);
}

.option-item--wrong {
  border-color: rgba(180, 35, 24, 0.45);
  background: rgba(180, 35, 24, 0.06);
}

.option-item--wrong .option-item__tag {
  color: var(--color-danger);
  background: rgba(180, 35, 24, 0.14);
}

.answer-row {
  margin-top: 12px;
  display: grid;
  grid-template-columns: 92px minmax(0, 1fr);
  gap: 10px;
  align-items: start;
}

.answer-row__label {
  margin: 0;
  font-size: 13px;
  color: var(--color-text-tertiary);
  line-height: 1.6;
}

.answer-row__value {
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
  color: var(--color-text-main);
}

.answer-row__value--correct {
  color: var(--color-success);
  font-weight: 700;
}

.answer-row__value--wrong {
  color: var(--color-danger);
  font-weight: 700;
}

.answer-row__value--empty {
  color: var(--color-text-tertiary);
}

.teacher-comment {
  margin: 12px 0 0;
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  border-left: 3px solid var(--color-primary);
  background: rgba(31, 95, 174, 0.08);
  font-size: 13px;
  line-height: 1.6;
  color: var(--color-primary-deep);
}

.review-side {
  position: sticky;
  top: calc(var(--layout-header-height) + 20px);
  height: fit-content;
}

.sheet-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text-main);
}

.sheet-summary {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.sheet-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  border: 1px solid transparent;
}

.sheet-tag--full {
  color: var(--color-success);
  background: rgba(47, 143, 78, 0.1);
  border-color: rgba(47, 143, 78, 0.28);
}

.sheet-tag--partial {
  color: #9a6700;
  background: rgba(215, 153, 33, 0.14);
  border-color: rgba(215, 153, 33, 0.34);
}

.sheet-tag--wrong {
  color: var(--color-danger);
  background: rgba(180, 35, 24, 0.1);
  border-color: rgba(180, 35, 24, 0.28);
}

.sheet-tag--empty {
  color: var(--color-text-secondary);
  background: rgba(77, 90, 107, 0.08);
  border-color: rgba(77, 90, 107, 0.24);
}

.sheet-grid {
  margin-top: 14px;
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 8px;
}

.sheet-index {
  border: 1px solid var(--color-border);
  background: #fff;
  border-radius: 8px;
  height: 34px;
  font-size: 13px;
  font-weight: 700;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.sheet-index:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 14px rgba(17, 47, 87, 0.12);
}

.sheet-index--active {
  outline: 2px solid var(--color-primary);
  outline-offset: -1px;
}

.sheet-index--full {
  border-color: rgba(47, 143, 78, 0.42);
  background: rgba(47, 143, 78, 0.16);
  color: var(--color-success);
}

.sheet-index--partial {
  border-color: rgba(215, 153, 33, 0.45);
  background: rgba(215, 153, 33, 0.2);
  color: #9a6700;
}

.sheet-index--wrong {
  border-color: rgba(180, 35, 24, 0.42);
  background: rgba(180, 35, 24, 0.15);
  color: var(--color-danger);
}

.sheet-index--empty {
  border-color: rgba(77, 90, 107, 0.24);
  background: rgba(77, 90, 107, 0.08);
  color: var(--color-text-secondary);
}

@media (max-width: 1200px) {
  .review-layout {
    grid-template-columns: 1fr;
  }

  .review-side {
    position: static;
  }

  .sheet-grid {
    grid-template-columns: repeat(10, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }

  .sheet-grid {
    grid-template-columns: repeat(7, minmax(0, 1fr));
  }

  .question-stem {
    font-size: 16px;
  }

  .answer-row {
    grid-template-columns: 80px minmax(0, 1fr);
  }
}
</style>
