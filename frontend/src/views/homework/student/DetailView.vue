<template>
  <div class="app-page-shell app-panel-grid" v-if="homework">
    <div class="hw-page-header">
      <div class="hw-page-header__left">
        <h1 class="hw-page-header__title">{{ homework.title }}</h1>
        <p class="hw-page-header__desc">{{ homework.summary }}</p>
      </div>
      <div class="hw-page-header__actions">
        <a-button @click="router.push('/homework')">返回作业列表</a-button>
        <a-button @click="router.push(`/homework/${homework.id}/score`)">查看成绩</a-button>
      </div>
    </div>

    <HomeworkMetaGrid
      topicLabel=""
      :teacher="homework.teacher"
      :open-time="homework.openTime"
      :deadline="homework.deadline"
    />

    <a-alert type="info" show-icon style="margin-bottom: var(--space-5)">
      <template #message>提交提示</template>
      <template #description>
        <ul class="hw-tip-list" style="margin:0;padding-left:18px">
          <li>选择题点击选项即可作答</li>
          <li>填空题点击横线处输入答案</li>
          <li>简答题请在文本框中作答</li>
          <li>截止前可多次提交更新</li>
        </ul>
      </template>
    </a-alert>

    <a-spin :spinning="loading">
      <section v-for="(q, idx) in questions" :key="q.id" class="app-surface-card app-section-card hw-question-block">
        <div class="hw-question-header">
          <span class="hw-question-number">{{ idx + 1 }}</span>
          <span class="hw-question-type-tag">{{ typeLabel(q.questionType) }}</span>
          <span v-if="q.maxScore" class="hw-question-score">（{{ q.maxScore }}分）</span>
        </div>

        <div class="hw-question-stem">
          <template v-if="q.questionType === 1">
            <!-- 填空题 -->
            <span v-for="(segment, sIdx) in parseFillBlank(q.question)" :key="sIdx">
              <template v-if="segment.type === 'text'">{{ segment.content }}</template>
              <template v-else>
                <a-input
                  v-model:value="fillAnswers[`${q.id}_${segment.blankIndex}`]"
                  class="hw-fill-input"
                  placeholder="填写答案"
                  style="width: 120px"
                />
              </template>
            </span>
          </template>
          <template v-else>
            {{ q.question }}
          </template>
        </div>

        <div v-if="q.questionType === 2" class="hw-question-options">
          <!-- 单选题 -->
          <a-radio-group v-model:value="choiceAnswers[q.id]" class="hw-options-list">
            <a-radio v-for="opt in parseOptions(q.optionsText)" :key="opt.value" :value="opt.value" class="hw-option-item">
              {{ opt.label }}
            </a-radio>
          </a-radio-group>
        </div>

        <div v-if="q.questionType === 3" class="hw-question-options">
          <!-- 多选题 -->
          <a-checkbox-group v-model:value="multipleAnswers[q.id]" class="hw-options-list">
            <a-checkbox v-for="opt in parseOptions(q.optionsText)" :key="opt.value" :value="opt.value" class="hw-option-item">
              {{ opt.label }}
            </a-checkbox>
          </a-checkbox-group>
        </div>

        <div v-if="q.questionType === 4" class="hw-question-options">
          <!-- 判断题 -->
          <a-radio-group v-model:value="choiceAnswers[q.id]" class="hw-options-list">
            <a-radio value="T" class="hw-option-item">正确（T）</a-radio>
            <a-radio value="F" class="hw-option-item">错误（F）</a-radio>
          </a-radio-group>
        </div>

        <div v-if="q.questionType === 5" class="hw-question-answer-area">
          <!-- 简答题 -->
          <a-textarea v-model:value="shortAnswers[q.id]" :rows="4" placeholder="请输入你的答案" />
        </div>
      </section>
    </a-spin>

    <section class="app-surface-card app-section-card" style="display:flex;justify-content:flex-end;gap:12px;">
      <a-button size="large" :loading="saving" @click="saveDraft">暂存答案</a-button>
      <a-button type="primary" size="large" :loading="submitting" @click="submitAll">提交全部答案</a-button>
    </section>
  </div>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/user/auth'
import HomeworkMetaGrid from '@/components/homework/HomeworkMetaGrid.vue'
import { getEduExerciseVoById } from '@/api/eduExerciseController'
import { listEduExerciseItemVoByPage } from '@/api/eduExerciseItemController'
import { submitExerciseAnswers, saveExerciseDraft, getExerciseProgress } from '@/api/eduExerciseSubmissionController'

interface QuestionItem {
  id: number
  question: string
  questionType: number
  optionsText?: string
  standardAnswer?: string
  maxScore: number
}

interface HomeworkDetail {
  id: string
  title: string
  teacher: string
  openTime: string
  deadline: string
  summary: string
}

interface FillSegment {
  type: 'text' | 'blank'
  content: string
  blankIndex: number
}

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const saving = ref(false)
const submitting = ref(false)
const homework = ref<HomeworkDetail | null>(null)
const questions = ref<QuestionItem[]>([])

const choiceAnswers = reactive<Record<number, string>>({})
const multipleAnswers = reactive<Record<number, string[]>>({})
const fillAnswers = reactive<Record<string, string>>({})
const shortAnswers = reactive<Record<number, string>>({})

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

function typeLabel(questionType: number): string {
  const typeMap: Record<number, string> = {
    1: '填空题',
    2: '单选题',
    3: '多选题',
    4: '判断题',
    5: '简答题',
    6: '编程题',
    7: '综合题'
  }
  return typeMap[questionType] || '未知'
}

function parseOptions(optionsText?: string): { label: string; value: string }[] {
  if (!optionsText) return []
  // 简单解析：假设格式为 "A选项内容,B选项内容,..."
  const options = optionsText.split(',')
  return options.map((opt, idx) => {
    const letter = String.fromCharCode(65 + idx) // A, B, C, ...
    return {
      label: opt.trim() || `${letter}. 选项`,
      value: letter
    }
  })
}

function parseFillBlank(stem: string): FillSegment[] {
  const segments: FillSegment[] = []
  const parts = stem.split('___')
  for (let i = 0; i < parts.length; i++) {
    if (parts[i]) {
      segments.push({ type: 'text', content: parts[i], blankIndex: 0 })
    }
    if (i < parts.length - 1) {
      segments.push({ type: 'blank', content: '', blankIndex: i + 1 })
    }
  }
  return segments
}

async function loadData() {
  const homeworkId = route.params.id as string

  if (!homeworkId) {
    loading.value = false
    return
  }

  try {
    // 加载作业基本信息
    const exerciseResponse = await getEduExerciseVoById({ id: homeworkId })
    const exercise = exerciseResponse.data?.data

    if (exercise) {
      homework.value = {
        id: homeworkId,
        title: exercise.taskName || '',
        teacher: '',
        openTime: formatDateTime(exercise.startTime),
        deadline: formatDateTime(exercise.endTime),
        summary: exercise.description || ''
      }
    }

    // 加载题目列表
    const itemsResponse = await listEduExerciseItemVoByPage({
      current: 1,
      pageSize: 50,
      exerciseId: Number(homeworkId)
    })

    const itemRecords = itemsResponse.data?.data?.records ?? []
    if (Array.isArray(itemRecords)) {
      questions.value = itemRecords.map((item) => ({
        id: item.id || 0,
        question: item.question || '',
        questionType: item.questionType || 0,
        optionsText: item.optionsText,
        standardAnswer: item.standardAnswer,
        maxScore: item.maxScore || 0
      }))
    }

    // 加载已有答题进度（恢复暂存）
    const studentId = Number(authStore.session?.id) || 0
    if (studentId) {
      const progressResponse = await getExerciseProgress({
        exerciseId: Number(homeworkId),
        studentId
      })
      const progress = progressResponse.data?.data

      if (progress?.items) {
        for (const item of progress.items) {
          if (item.choiceAnswer) {
            choiceAnswers[item.itemId] = item.choiceAnswer
          }
          if (item.textContent) {
            shortAnswers[item.itemId] = item.textContent
          }
        }
      }
    }
  } catch (error) {
    console.error('加载作业数据失败:', error)
    message.error('加载作业数据失败')
  } finally {
    loading.value = false
  }
}

async function saveDraft() {
  const homeworkId = route.params.id as string
  const studentId = Number(authStore.session?.id) || 0

  if (!homeworkId || !studentId) {
    message.error('请先登录')
    return
  }

  saving.value = true
  try {
    const answers = buildAnswers()
    await saveExerciseDraft({
      exerciseId: Number(homeworkId),
      studentId,
      answers
    })
    message.success('答案已暂存')
  } catch (error) {
    console.error('暂存失败:', error)
    message.error('暂存失败')
  } finally {
    saving.value = false
  }
}

async function submitAll() {
  const homeworkId = route.params.id as string
  const studentId = Number(authStore.session?.id) || 0

  if (!homeworkId || !studentId) {
    message.error('请先登录')
    return
  }

  submitting.value = true
  try {
    const answers = buildAnswers()
    const response = await submitExerciseAnswers({
      exerciseId: Number(homeworkId),
      studentId,
      answers
    })
    const submitResult = response.data?.data

    if (submitResult?.success) {
      message.success('提交成功')
      router.push(`/homework/${homeworkId}/score`)
    } else {
      message.error(submitResult?.message || '提交失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    message.error('提交失败')
  } finally {
    submitting.value = false
  }
}

function buildAnswers(): API.AnswerItem[] {
  return questions.value.map((q) => {
    const answer: API.AnswerItem = {
      itemId: q.id,
      questionType: q.questionType
    }

    if (q.questionType === 2 || q.questionType === 4) {
      // 单选或判断
      answer.choiceAnswer = choiceAnswers[q.id] || ''
    } else if (q.questionType === 3) {
      // 多选
      answer.choiceAnswer = (multipleAnswers[q.id] || []).join('')
    } else if (q.questionType === 1) {
      // 填空
      const fills: API.FillBlankAnswer[] = []
      const segments = parseFillBlank(q.question)
      for (let i = 0; i < segments.length; i++) {
        if (segments[i].type === 'blank') {
          fills.push({
            blankIndex: segments[i].blankIndex,
            answerContent: fillAnswers[`${q.id}_${segments[i].blankIndex}`] || ''
          })
        }
      }
      answer.fillBlanks = fills
    } else if (q.questionType === 5) {
      // 简答
      answer.textContent = shortAnswers[q.id] || ''
    }

    return answer
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.hw-question-block {
  margin-bottom: var(--space-4);
}

.hw-question-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.hw-question-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--color-primary);
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  flex-shrink: 0;
}

.hw-question-type-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 10px;
  border-radius: 4px;
  background: var(--color-bg-muted);
  border: 1px solid var(--color-border);
  font-size: 12px;
  color: var(--color-text-secondary);
}

.hw-question-score {
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.hw-question-stem {
  font-size: 15px;
  line-height: 1.8;
  color: var(--color-text-main);
  margin-bottom: 16px;
}

.hw-question-options {
  margin-bottom: 4px;
}

.hw-options-list {
  display: grid;
  gap: 10px;
}

.hw-option-item {
  display: flex;
  align-items: flex-start;
  padding: 10px 14px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  background: var(--color-bg-muted);
  transition: border-color 0.15s, background 0.15s;
  cursor: pointer;
  line-height: 1.6;
}

.hw-option-item:hover {
  border-color: var(--color-primary);
  background: rgba(31, 95, 174, 0.04);
}

.hw-question-answer-area {
  margin-bottom: 4px;
}

.hw-fill-input {
  display: inline-block;
  border-bottom: 2px solid var(--color-primary);
  border-radius: 0;
  text-align: center;
  font-weight: 600;
}

.hw-fill-input :deep(.ant-input) {
  border: none;
  text-align: center;
  font-weight: 600;
}

.hw-fill-input :deep(.ant-input:focus) {
  box-shadow: none;
  border-bottom: 2px solid var(--color-primary);
}
</style>
