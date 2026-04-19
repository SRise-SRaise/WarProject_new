<template>
  <section class="app-surface-card app-section-card app-panel-grid">
    <div class="panel-head">
      <h3 class="panel-title">题目配置</h3>
      <span class="panel-stat">已选 {{ selectedCount }} 题 / 总分 {{ totalScore }}</span>
    </div>

    <a-alert
      v-if="!exerciseId"
      type="info"
      message="请先保存作业基础信息"
      description="保存后即可从题库加入题目。"
      show-icon
    />

    <template v-else>
      <section class="selected-board">
        <div class="panel-subtitle-row">
          <h4 class="panel-subtitle">已选题目卡片</h4>
          <a-space :size="8" wrap>
            <a-tag color="blue">共 {{ selectedCount }} 题</a-tag>
            <a-tag color="geekblue">总分 {{ totalScore }}</a-tag>
          </a-space>
        </div>

        <a-empty v-if="selectedQuestions.length === 0" description="当前还没有加入题目" />

        <TransitionGroup v-else name="selected-list" tag="div" class="selected-card-list">
          <article
            v-for="(item, index) in selectedQuestions"
            :key="item.id"
            class="selected-card"
            :class="{
              'selected-card--drag-over': dragOverItemId === item.id,
              'selected-card--dragging': dragItemId === item.id
            }"
            draggable="true"
            @dragstart="onCardDragStart(item.id, $event)"
            @dragover.prevent="onCardDragOver(item.id)"
            @drop.prevent="onCardDrop(item.id)"
            @dragend="onCardDragEnd"
          >
            <div class="selected-card__head">
              <div class="selected-card__meta">
                <span class="drag-handle" title="拖拽排序">⋮⋮</span>
                <strong>第 {{ index + 1 }} 题</strong>
                <a-tag>{{ typeLabel(item.questionType) }}</a-tag>
                <a-tag color="cyan">{{ item.maxScore || 0 }} 分</a-tag>
                <a-tag color="purple">{{ item.questionBankId ? '题库来源' : '自建题目' }}</a-tag>
              </div>
              <a-button type="link" danger @click="removeQuestion(item.id)">移除</a-button>
            </div>

            <p class="selected-card__question">{{ item.question }}</p>

            <div v-if="parseOptions(item.optionsText).length > 0" class="selected-card__options">
              <p class="detail-label">选项</p>
              <ul>
                <li v-for="option in parseOptions(item.optionsText)" :key="option">{{ option }}</li>
              </ul>
            </div>

            <div class="selected-card__actions">
              <a-button size="small" @click="toggleAnswer(item.id)">
                {{ isAnswerVisible(item.id) ? '收起答案' : '显示答案' }}
              </a-button>
            </div>

            <div v-if="isAnswerVisible(item.id)" class="selected-card__answer">
              <p><span class="detail-label">参考答案：</span>{{ item.standardAnswer || '未设置' }}</p>
              <p v-if="item.analysis"><span class="detail-label">解析：</span>{{ item.analysis }}</p>
            </div>
          </article>
        </TransitionGroup>
      </section>

      <div class="panel-tools">
        <a-input v-model:value="keyword" allow-clear size="large" placeholder="搜索题干" />
        <a-select v-model:value="typeFilter" size="large" :options="typeOptions" />
        <a-button :loading="loading" @click="loadData">刷新</a-button>
      </div>

      <section class="panel-col">
        <div class="panel-subtitle-row">
          <h4 class="panel-subtitle">题库候选</h4>
          <a-button type="primary" :disabled="selectedBankIds.length === 0" @click="addSelectedQuestions">加入作业</a-button>
        </div>
        <a-table
          size="small"
          :columns="bankColumns"
          :data-source="filteredBankQuestions"
          :pagination="{ pageSize: 6 }"
          row-key="id"
          :row-selection="bankRowSelection"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'type'">
              <a-tag>{{ typeLabel(record.questionType) }}</a-tag>
            </template>
            <template v-else-if="column.key === 'question'">
              <p class="bank-question-cell">{{ record.question }}</p>
            </template>
            <template v-else-if="column.key === 'optionsPreview'">
              <p class="option-preview" :title="optionsPreview(record.optionsText)">{{ optionsPreview(record.optionsText) }}</p>
            </template>
            <template v-else-if="column.key === 'answer'">
              <a-tag color="blue">{{ record.standardAnswer || '未设' }}</a-tag>
            </template>
          </template>
        </a-table>
      </section>
    </template>
  </section>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { addEduExerciseItem, deleteEduExerciseItem, listEduExerciseItemVoByPage } from '@/api/eduExerciseItemController'
import type { HomeworkQuestionStat } from '@/types/homework/assignment'

interface ExerciseItem {
  id: string
  exerciseId?: string
  question: string
  questionType: number
  optionsText?: string
  standardAnswer?: string
  maxScore?: number
  analysis?: string
  difficulty?: number
  questionBankId?: string
}

const props = defineProps<{ exerciseId?: string | number }>()
const emit = defineEmits<{
  change: [HomeworkQuestionStat]
}>()

const loading = ref(false)
const keyword = ref('')
const typeFilter = ref<number | 'all'>('all')

const bankQuestions = ref<ExerciseItem[]>([])
const selectedQuestions = ref<ExerciseItem[]>([])
const selectedBankIds = ref<string[]>([])

const typeOptions = [
  { label: '全部题型', value: 'all' },
  { label: '填空题', value: 1 },
  { label: '单选题', value: 2 },
  { label: '多选题', value: 3 },
  { label: '判断题', value: 4 },
  { label: '简答题', value: 5 }
]

const bankColumns = [
  { title: '题干', dataIndex: 'question', key: 'question', ellipsis: true },
  { title: '选项预览', key: 'optionsPreview', width: 320 },
  { title: '答案', key: 'answer', width: 100 },
  { title: '题型', key: 'type', width: 90 },
  { title: '分值', dataIndex: 'maxScore', key: 'maxScore', width: 80 }
]

const answerVisibleIds = ref<string[]>([])
const dragItemId = ref('')
const dragOverItemId = ref('')

const selectedCount = computed(() => selectedQuestions.value.length)
const totalScore = computed(() => selectedQuestions.value.reduce((sum, item) => sum + Number(item.maxScore || 0), 0))

const filteredBankQuestions = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  return bankQuestions.value.filter((item) => {
    const byKeyword = key.length === 0 || item.question.toLowerCase().includes(key)
    const byType = typeFilter.value === 'all' || item.questionType === typeFilter.value
    return byKeyword && byType
  })
})

const bankRowSelection = computed(() => ({
  selectedRowKeys: selectedBankIds.value,
  onChange: (keys: Array<string | number>) => {
    selectedBankIds.value = keys.map((key) => String(key))
  }
}))

function typeLabel(type: number): string {
  const map: Record<number, string> = {
    1: '填空',
    2: '单选',
    3: '多选',
    4: '判断',
    5: '简答'
  }
  return map[type] || '未知'
}

function unwrapRecords(response: any): any[] {
  return response?.data?.data?.records ?? response?.data?.records ?? []
}

function toItem(raw: any): ExerciseItem {
  const id = raw?.id == null ? '' : String(raw.id)
  const exerciseId = raw?.exerciseId == null ? undefined : String(raw.exerciseId)
  const questionBankId = raw?.questionBankId == null ? undefined : String(raw.questionBankId)
  return {
    id,
    exerciseId,
    question: String(raw.question || ''),
    questionType: Number(raw.questionType || 0),
    optionsText: raw.optionsText || '',
    standardAnswer: raw.standardAnswer || '',
    maxScore: raw.maxScore ?? 0,
    analysis: raw.analysis || '',
    difficulty: raw.difficulty ?? undefined,
    questionBankId
  }
}

function parseOptions(optionsText?: string): string[] {
  const text = String(optionsText || '').trim()
  if (!text) return []
  if (text.includes('\n')) {
    return text
      .split('\n')
      .map((item) => item.trim())
      .filter((item) => item.length > 0)
  }
  if (text.includes(';')) {
    return text
      .split(';')
      .map((item) => item.trim())
      .filter((item) => item.length > 0)
  }
  const optionPattern = /(?=[A-H][\.、\)])/g
  if (optionPattern.test(text)) {
    return text
      .split(optionPattern)
      .map((item) => item.trim())
      .filter((item) => item.length > 0)
  }
  return [text]
}

function optionsPreview(optionsText?: string): string {
  const options = parseOptions(optionsText)
  if (options.length === 0) return '无选项'
  return options.join(' / ')
}

function orderStorageKey(): string {
  return `homework:selected-order:${String(props.exerciseId || '')}`
}

function persistSelectedOrder(): void {
  if (!props.exerciseId) return
  try {
    const ids = selectedQuestions.value.map((item) => item.id)
    if (ids.length === 0) {
      localStorage.removeItem(orderStorageKey())
      return
    }
    localStorage.setItem(orderStorageKey(), JSON.stringify(ids))
  } catch (error) {
    console.warn('保存题目排序失败:', error)
  }
}

function applyStoredOrder(items: ExerciseItem[]): ExerciseItem[] {
  if (!props.exerciseId || items.length <= 1) return items
  try {
    const raw = localStorage.getItem(orderStorageKey())
    if (!raw) return items
    const storedIds = JSON.parse(raw)
    if (!Array.isArray(storedIds) || storedIds.length === 0) return items

    const map = new Map(items.map((item) => [item.id, item]))
    const ordered: ExerciseItem[] = []

    for (const id of storedIds) {
      const item = map.get(String(id))
      if (item) {
        ordered.push(item)
        map.delete(String(id))
      }
    }

    for (const item of items) {
      if (map.has(item.id)) {
        ordered.push(item)
      }
    }
    return ordered
  } catch (error) {
    console.warn('读取题目排序失败:', error)
    return items
  }
}

function moveDraggedItem(targetId: string): void {
  const sourceId = dragItemId.value
  if (!sourceId || sourceId === targetId) return
  const list = [...selectedQuestions.value]
  const fromIndex = list.findIndex((item) => item.id === sourceId)
  const toIndex = list.findIndex((item) => item.id === targetId)
  if (fromIndex < 0 || toIndex < 0 || fromIndex === toIndex) return
  const [moved] = list.splice(fromIndex, 1)
  list.splice(toIndex, 0, moved)
  selectedQuestions.value = list
}

function onCardDragStart(id: string, event?: DragEvent): void {
  dragItemId.value = id
  if (event?.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move'
  }
}

function onCardDragOver(id: string): void {
  if (!dragItemId.value || dragItemId.value === id) return
  moveDraggedItem(id)
  dragOverItemId.value = id
}

function onCardDrop(targetId: string): void {
  moveDraggedItem(targetId)
  persistSelectedOrder()
  onCardDragEnd()
}

function onCardDragEnd(): void {
  dragItemId.value = ''
  dragOverItemId.value = ''
}

function isAnswerVisible(id: string): boolean {
  return answerVisibleIds.value.includes(id)
}

function toggleAnswer(id: string): void {
  if (isAnswerVisible(id)) {
    answerVisibleIds.value = answerVisibleIds.value.filter((item) => item !== id)
    return
  }
  answerVisibleIds.value = [...answerVisibleIds.value, id]
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

async function loadBankQuestions(): Promise<void> {
  const response = await listEduExerciseItemVoByPage({ current: 1, pageSize: 50 })
  const allItems = unwrapRecords(response).map(toItem)
  const selectedIdSet = new Set(selectedQuestions.value.map((item) => item.id))
  const selectedSourceSet = new Set(selectedQuestions.value.map((item) => item.questionBankId || '').filter((id) => id.length > 0))
  bankQuestions.value = allItems.filter((item) => {
    if (selectedIdSet.has(item.id)) return false
    if (selectedSourceSet.has(item.id)) return false
    return true
  })
}

async function loadSelectedQuestions(): Promise<void> {
  if (!props.exerciseId) {
    selectedQuestions.value = []
    emit('change', { count: 0, totalScore: 0 })
    return
  }
  const response = await listEduExerciseItemVoByPage({
    current: 1,
    pageSize: 50,
    exerciseId: props.exerciseId as any
  })
  selectedQuestions.value = unwrapRecords(response)
    .map(toItem)
    .filter((item) => String(item.exerciseId || '') === String(props.exerciseId || ''))
  selectedQuestions.value = applyStoredOrder(selectedQuestions.value)
  emit('change', { count: selectedCount.value, totalScore: totalScore.value })
}

async function loadData(): Promise<void> {
  if (!props.exerciseId) return
  loading.value = true
  try {
    await loadSelectedQuestions()
    await loadBankQuestions()
  } catch (error) {
    console.error('加载题目数据失败:', error)
    message.error(getErrorMessage(error, '加载题目数据失败'))
  } finally {
    loading.value = false
  }
}

async function addSelectedQuestions(): Promise<void> {
  if (!props.exerciseId) return
  const selectedSet = new Set(selectedQuestions.value.map((item) => item.questionBankId || item.id))
  const candidates = bankQuestions.value.filter((item) => selectedBankIds.value.includes(item.id))
  const toAdd = candidates.filter((item) => !selectedSet.has(item.id))
  if (toAdd.length === 0) {
    message.warning('没有可新增的题目')
    return
  }

  loading.value = true
  try {
    for (const item of toAdd) {
      await addEduExerciseItem({
        exerciseId: props.exerciseId,
        questionBankId: item.id,
        question: item.question,
        questionType: item.questionType,
        optionsText: item.optionsText,
        standardAnswer: item.standardAnswer,
        maxScore: item.maxScore,
        analysis: item.analysis,
        difficulty: item.difficulty
      } as any)
    }
    message.success(`已加入 ${toAdd.length} 道题目`)
    selectedBankIds.value = []
    await loadSelectedQuestions()
    await loadBankQuestions()
    persistSelectedOrder()
  } catch (error) {
    console.error('加入题目失败:', error)
    message.error(getErrorMessage(error, '加入题目失败'))
  } finally {
    loading.value = false
  }
}

async function removeQuestion(itemId: string): Promise<void> {
  loading.value = true
  try {
    const response = await deleteEduExerciseItem({ id: itemId, exerciseId: props.exerciseId } as any)
    const code = (response as any)?.data?.code
    const success = (response as any)?.data?.data ?? (response as any)?.data
    const messageText = (response as any)?.data?.message
    if (code !== undefined && code !== 0) {
      message.error(messageText || '移除失败')
      return
    }
    if (!success) {
      message.error(messageText || '移除失败，请刷新后重试')
      return
    }
    message.success('已移除题目')
    answerVisibleIds.value = answerVisibleIds.value.filter((item) => item !== itemId)
    await loadSelectedQuestions()
    await loadBankQuestions()
    persistSelectedOrder()
  } catch (error) {
    console.error('移除题目失败:', error)
    message.error(getErrorMessage(error, '移除题目失败'))
  } finally {
    loading.value = false
  }
}

watch(
  () => props.exerciseId,
  () => {
    if (props.exerciseId) {
      loadData()
      return
    }
    selectedQuestions.value = []
    selectedBankIds.value = []
    answerVisibleIds.value = []
    onCardDragEnd()
    emit('change', { count: 0, totalScore: 0 })
  },
  { immediate: true }
)
</script>

<style scoped>
.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.panel-title {
  margin: 0;
  font-size: 18px;
}

.panel-stat {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.panel-tools {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 180px 88px;
  gap: 10px;
}

.panel-col {
  min-width: 0;
}

.selected-board {
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fafcff;
}

.selected-card-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 12px;
}

.selected-list-move {
  transition: transform 0.22s cubic-bezier(0.2, 0.7, 0.2, 1);
}

.selected-card {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #fff;
  padding: 12px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  will-change: transform;
}

.selected-card--drag-over {
  border-color: #60a5fa;
  box-shadow: 0 0 0 2px rgba(96, 165, 250, 0.2);
}

.selected-card--dragging {
  opacity: 0.68;
  transform: scale(0.985);
}

.selected-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.selected-card__meta {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.drag-handle {
  color: #6b7280;
  cursor: grab;
  user-select: none;
}

.selected-card--dragging .drag-handle {
  cursor: grabbing;
}

.selected-card__question {
  margin: 10px 0;
  color: #1f2937;
  line-height: 1.5;
}

.selected-card__options ul {
  margin: 6px 0 0;
  padding-left: 18px;
}

.selected-card__options li {
  margin-bottom: 4px;
  color: #374151;
}

.selected-card__actions {
  margin-top: 10px;
}

.selected-card__answer {
  margin-top: 10px;
  padding: 10px;
  border-radius: 8px;
  background: #f3f7ff;
}

.detail-label {
  color: #4b5563;
  font-weight: 600;
}

.bank-question-cell {
  margin: 0;
  color: #1f2937;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.option-preview {
  color: #4b5563;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.panel-subtitle-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 8px;
}

.panel-subtitle {
  margin: 0 0 8px;
  font-size: 14px;
}

@media (max-width: 960px) {
  .panel-tools {
    grid-template-columns: 1fr;
  }
}
</style>
