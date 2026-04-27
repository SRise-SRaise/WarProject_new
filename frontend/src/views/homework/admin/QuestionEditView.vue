<template>
  <div class="app-panel-grid">
    <div class="hw-page-header">
      <div class="hw-page-header__left">
        <h1 class="hw-page-header__title">{{ pageTitle }}</h1>
        <p class="hw-page-header__desc">新增或编辑题库题目，作业创建时可从题库选择。</p>
      </div>
      <div class="hw-page-header__actions">
        <a-button @click="router.push('/admin/questions')">返回题目列表</a-button>
      </div>
    </div>

    <section class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <a-spin :spinning="loading">
          <a-form layout="vertical">
          <a-form-item label="题目题型" required>
            <a-select v-model:value="formState.type" size="large" :options="typeOptions" />
          </a-form-item>
          <a-form-item label="题目题干" required>
            <a-textarea v-model:value="formState.stem" :rows="4" placeholder="输入题目内容" />
          </a-form-item>

          <a-form-item v-if="isChoiceType" label="选项" required>
            <div class="option-editor">
              <div v-for="(item, index) in formState.options" :key="item.key" class="option-row">
                <div class="option-label">{{ optionLabel(index) }}</div>
                <a-input v-model:value="item.content" size="large" placeholder="请输入选项内容" />
                <a-button
                  v-if="canRemoveOption"
                  type="text"
                  danger
                  :disabled="formState.options.length <= minOptionCount"
                  @click="removeOption(index)"
                >
                  删除
                </a-button>
              </div>

              <div class="option-actions">
                <a-button v-if="canAddOption" @click="addOption">新增选项</a-button>
                <span class="option-hint">{{ optionHintText }}</span>
              </div>
            </div>
          </a-form-item>

          <a-form-item v-if="formState.type === 2 || formState.type === 4" label="标准答案" required>
            <a-radio-group v-model:value="singleAnswer">
              <a-space direction="vertical" :size="8">
                <a-radio
                  v-for="item in optionEntriesWithLabel"
                  :key="item.key"
                  :value="item.label"
                >
                  {{ item.label }}. {{ item.content.trim() || '（未填写）' }}
                </a-radio>
              </a-space>
            </a-radio-group>
          </a-form-item>

          <a-form-item v-else-if="formState.type === 3" label="标准答案" required>
            <a-checkbox-group v-model:value="multiAnswers">
              <a-space direction="vertical" :size="8">
                <a-checkbox
                  v-for="item in optionEntriesWithLabel"
                  :key="item.key"
                  :value="item.label"
                >
                  {{ item.label }}. {{ item.content.trim() || '（未填写）' }}
                </a-checkbox>
              </a-space>
            </a-checkbox-group>
          </a-form-item>

          <a-form-item v-else label="标准答案" required>
            <a-textarea
              v-model:value="textAnswer"
              :rows="formState.type === 5 ? 4 : 2"
              :placeholder="formState.type === 1 ? '填空题请填写参考答案' : '请输入标准答案'"
            />
          </a-form-item>

          <a-form-item label="题目解析">
            <a-textarea v-model:value="formState.analysis" :rows="3" placeholder="选填，帮助学生理解答案" />
          </a-form-item>
          <a-form-item label="分值">
            <a-input-number v-model:value="formState.maxScore" size="large" :min="1" :max="100" placeholder="满分分值" />
          </a-form-item>
          <a-form-item label="难度系数">
            <a-select v-model:value="formState.difficulty" size="large" placeholder="选择难度" allow-clear :options="difficultyOptions" />
          </a-form-item>
            <a-button type="primary" size="large" :loading="saving" @click="saveQuestion">保存题目</a-button>
          </a-form>
        </a-spin>
      </section>

      <div class="hw-side-column">
        <a-alert type="info" message="录入规范" show-icon>
          <template #description>
            <ul class="hw-tip-list">
              <li>选择题请先维护选项，再从选项中勾选标准答案。</li>
              <li>判断题默认两个选项：A 正确、B 错误，无需手工录入。</li>
              <li>填空/简答不需要选项，直接填写参考答案文本。</li>
            </ul>
          </template>
        </a-alert>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import {
  addEduExerciseItem,
  getEduExerciseItemVoById,
  updateEduExerciseItem
} from '@/api/eduExerciseItemController'

interface OptionItem {
  key: string
  content: string
}

const route = useRoute()
const router = useRouter()
const questionId = computed<string | null>(() => {
  const idValue = route.params.id
  if (!idValue || typeof idValue !== 'string' || idValue.trim() === '') {
    return null
  }
  return idValue
})
const pageTitle = computed(() => (questionId.value ? '编辑题目' : '新增题目'))
const loading = ref(false)
const saving = ref(false)
const isLoadingForm = ref(false)
const suppressTypeAnswerReset = ref(false)

const typeOptions = [
  { label: '填空题', value: 1 },
  { label: '单选题', value: 2 },
  { label: '多选题', value: 3 },
  { label: '判断题', value: 4 },
  { label: '简答题', value: 5 }
]

function optionLabel(index: number): string {
  return String.fromCharCode(65 + index)
}

function parseOptionsText(text: string): OptionItem[] {
  return text
    .replace(/，/g, ',')
    .split(/[\n,]/)
    .map((line) => line.trim())
    .filter((line) => line.length > 0)
    .map((line, index) => ({
      key: `opt-${Date.now()}-${index}`,
      content: line.replace(/^[A-Z]\s*[.、:)：-]?\s*/i, '').trim()
    }))
}

function createDefaultOptions(count = 4): OptionItem[] {
  return Array.from({ length: count }, (_, index) => ({
    key: `opt-${Date.now()}-${index}`,
    content: ''
  }))
}

function createJudgeOptions(): OptionItem[] {
  return [
    { key: 'judge-true', content: '正确' },
    { key: 'judge-false', content: '错误' }
  ]
}

function normalizeChoiceAnswer(raw: string): string[] {
  return raw
    .toUpperCase()
    .replace(/[^A-Z]/g, '')
    .split('')
    .filter((item) => /^[A-Z]$/.test(item))
}

const formState = reactive({
  type: 2,
  stem: '',
  options: [] as OptionItem[],
  answer: '',
  maxScore: null as number | null,
  analysis: '',
  difficulty: null as number | null
})

const difficultyOptions = [
  { label: '1 - 简单', value: 1 },
  { label: '2 - 一般', value: 2 },
  { label: '3 - 中等', value: 3 },
  { label: '4 - 较难', value: 4 },
  { label: '5 - 困难', value: 5 }
]

const isChoiceType = computed(() => [2, 3, 4].includes(formState.type))
const canAddOption = computed(() => formState.type === 2 || formState.type === 3)
const canRemoveOption = computed(() => formState.type === 2 || formState.type === 3)
const minOptionCount = computed(() => 2)
const optionHintText = computed(() => {
  if (formState.type === 4) return '判断题固定为两个选项。'
  if (formState.type === 3) return '多选题建议至少 3 个选项。'
  return '单选题建议 3-5 个选项。'
})

const optionEntriesWithLabel = computed(() => formState.options.map((item, index) => ({
  ...item,
  label: optionLabel(index)
})))

const singleAnswer = computed({
  get: () => normalizeChoiceAnswer(formState.answer)[0] || '',
  set: (value: string) => {
    formState.answer = value
  }
})

const multiAnswers = computed({
  get: () => normalizeChoiceAnswer(formState.answer),
  set: (values: string[]) => {
    formState.answer = values
      .map((item) => item.toUpperCase())
      .sort()
      .join('')
  }
})

const textAnswer = computed({
  get: () => formState.answer,
  set: (value: string) => {
    formState.answer = value
  }
})

function addOption(): void {
  formState.options.push({ key: `opt-${Date.now()}-${formState.options.length}`, content: '' })
}

function removeOption(index: number): void {
  if (formState.options.length <= minOptionCount.value) return
  formState.options.splice(index, 1)
  formState.answer = ''
}

function ensureTypeDefaults(type: number): void {
  if (type === 4) {
    formState.options = createJudgeOptions()
    if (!['A', 'B'].includes(singleAnswer.value)) {
      singleAnswer.value = 'A'
    }
    return
  }

  if (type === 2 || type === 3) {
    if (formState.options.length < 2) {
      formState.options = createDefaultOptions(4)
    }
    return
  }

  formState.options = []
}

watch(
  () => formState.type,
  (nextType, prevType) => {
    if (nextType !== prevType) {
      if (suppressTypeAnswerReset.value) {
        suppressTypeAnswerReset.value = false
      } else if (!isLoadingForm.value) {
        formState.answer = ''
      }
    }
    ensureTypeDefaults(nextType)
  }
)

ensureTypeDefaults(formState.type)

function fillForm(data: API.EduExerciseItemVO): void {
  isLoadingForm.value = true
  suppressTypeAnswerReset.value = true
  formState.type = data.questionType || 2
  formState.stem = data.question || ''
  const rawAnswer = data.standardAnswer || ''
  if (isChoiceTypeByType(data.questionType)) {
    formState.options = data.optionsText ? parseOptionsText(data.optionsText) : []
    formState.answer = rawAnswer
  } else {
    formState.options = []
    formState.answer = rawAnswer
  }
  formState.maxScore = data.maxScore ?? null
  formState.analysis = data.analysis || ''
  formState.difficulty = data.difficulty ?? null
  ensureTypeDefaults(formState.type)
  isLoadingForm.value = false
}

function isChoiceTypeByType(t: number | undefined): boolean {
  return t != null && [2, 3, 4].includes(t)
}

function getErrorMessage(error: unknown, fallback: string): string {
  const responseMessage = (error as any)?.response?.data?.message
  const directMessage = (error as any)?.message
  if (typeof responseMessage === 'string' && responseMessage.trim().length > 0) {
    return responseMessage
  }
  if (typeof directMessage === 'string' && directMessage.trim().length > 0) {
    return directMessage
  }
  return fallback
}

async function loadQuestion(): Promise<void> {
  if (!questionId.value) return

  loading.value = true
  try {
    const response = await getEduExerciseItemVoById({ id: questionId.value })
    const raw = response as any
    const candidate = raw?.data?.data ?? raw?.data
    if (candidate && candidate.id != null) {
      fillForm(candidate as API.EduExerciseItemVO)
    }
  } catch (error) {
    console.error('加载题目失败:', error)
    message.error(getErrorMessage(error, '加载题目失败'))
  } finally {
    loading.value = false
  }
}

async function saveQuestion(): Promise<void> {
  if (formState.stem.trim().length === 0) {
    message.error('请先填写题干。')
    return
  }

  if (isChoiceType.value) {
    const hasInvalidOption = formState.options.some((item) => item.content.trim().length === 0)
    if (formState.options.length < minOptionCount.value || hasInvalidOption) {
      message.error('请完整填写选项内容。')
      return
    }
  }

  if (formState.type === 2 || formState.type === 4) {
    if (!singleAnswer.value) {
      message.error('请选择标准答案。')
      return
    }
  } else if (formState.type === 3) {
    if (multiAnswers.value.length === 0) {
      message.error('请至少选择一个标准答案。')
      return
    }
  } else if (formState.answer.trim().length === 0) {
    message.error('请填写标准答案。')
    return
  }

  const optionsText = formState.options
    .map((item, index) => `${optionLabel(index)}. ${item.content.trim()}`)
    .join(',')

  saving.value = true
  try {
    const payload = {
      questionType: formState.type,
      question: formState.stem,
      optionsText: isChoiceType.value ? optionsText : '',
      standardAnswer: formState.answer.trim(),
      maxScore: formState.maxScore ?? undefined,
      analysis: formState.analysis.trim() || undefined,
      difficulty: formState.difficulty ?? undefined
    }

    if (questionId.value) {
      await updateEduExerciseItem({
        id: questionId.value,
        ...payload
      })
      message.success('题目已更新')
    } else {
      await addEduExerciseItem(payload)
      message.success('题目已创建')
    }
    router.push('/admin/questions')
  } catch (error) {
    console.error('保存题目失败:', error)
    message.error(getErrorMessage(error, '保存题目失败'))
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadQuestion()
})
</script>

<style scoped>
.option-editor {
  display: grid;
  gap: 10px;
}

.option-row {
  display: grid;
  grid-template-columns: 40px minmax(0, 1fr) 64px;
  align-items: center;
  gap: 10px;
}

.option-label {
  font-weight: 600;
  color: var(--color-text-main);
}

.option-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.option-hint {
  font-size: 12px;
  color: var(--color-text-tertiary);
}

@media (max-width: 960px) {
  .option-row {
    grid-template-columns: 32px minmax(0, 1fr);
  }
}
</style>
