<template>
  <div class="app-panel-grid">
    <div class="hw-page-header">
      <div class="hw-page-header__left">
        <h1 class="hw-page-header__title">题目列表</h1>
        <p class="hw-page-header__desc">独立管理题库题目，供教师在新增/编辑作业时选择组卷。</p>
      </div>
      <div class="hw-page-header__actions">
        <a-button type="primary" @click="router.push('/admin/questions/add')">新增题目</a-button>
      </div>
    </div>

    <section class="app-surface-card app-section-card">
      <div class="question-toolbar">
        <a-input v-model:value="keyword" allow-clear size="large" placeholder="搜索题干或标签" />
        <a-select v-model:value="typeFilter" size="large" :options="typeOptions" />
      </div>
      <a-spin :spinning="loading">
        <a-table :columns="columns" :data-source="filteredQuestions" row-key="id" :pagination="false">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'type'">
              <a-tag>{{ record.type }}</a-tag>
            </template>
            <template v-else-if="column.key === 'action'">
              <a-space :size="8">
                <a-button type="link" @click="router.push(`/admin/questions/edit/${record.id}`)">编辑</a-button>
                <a-popconfirm title="确认删除该题目？" ok-text="删除" cancel-text="取消" @confirm="deleteQuestion(record.id)">
                  <a-button type="link" danger>删除</a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-spin>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { deleteEduQuestionBank, listEduQuestionBankByPage } from '@/api/eduQuestionBankController'

interface QuestionItem {
  id: string
  stem: string
  type: '填空' | '单选' | '多选' | '判断' | '简答'
  tags: string
  updatedAt: string
}

const router = useRouter()
const loading = ref(false)
const questions = ref<QuestionItem[]>([])
const keyword = ref('')
const typeFilter = ref<'all' | QuestionItem['type']>('all')

const typeMap: Record<number, QuestionItem['type']> = {
  1: '填空',
  2: '单选',
  3: '多选',
  4: '判断',
  5: '简答'
}

function formatDateText(value?: string): string {
  if (!value) return '--'
  const parsed = new Date(value)
  if (Number.isNaN(parsed.getTime())) return value
  const year = parsed.getFullYear()
  const month = String(parsed.getMonth() + 1).padStart(2, '0')
  const day = String(parsed.getDate()).padStart(2, '0')
  const hour = String(parsed.getHours()).padStart(2, '0')
  const minute = String(parsed.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

function buildTags(item: API.EduQuestionBank): string {
  const labels: string[] = []
  if (item.questionType) labels.push(typeMap[item.questionType] || '未知题型')
  if (item.difficulty) labels.push(`难度 ${item.difficulty}`)
  return labels.length > 0 ? labels.join(' / ') : '--'
}

async function loadQuestions(): Promise<void> {
  loading.value = true
  try {
    const response = await listEduQuestionBankByPage({ current: 1, pageSize: 50 })
    const records = (response as any)?.data?.data?.records ?? (response as any)?.data?.records ?? []
    questions.value = Array.isArray(records)
      ? records.map((item: API.EduQuestionBank) => ({
          id: String(item.id || ''),
          stem: item.questionContent || '',
          type: typeMap[item.questionType || 1] || '填空',
          tags: buildTags(item),
          updatedAt: formatDateText(item.updatedAt)
        }))
      : []
  } catch (error) {
    console.error('加载题目列表失败:', error)
    message.error('加载题目列表失败')
  } finally {
    loading.value = false
  }
}

const typeOptions = [
  { label: '全部题型', value: 'all' },
  { label: '填空', value: '填空' },
  { label: '单选', value: '单选' },
  { label: '多选', value: '多选' },
  { label: '判断', value: '判断' },
  { label: '简答', value: '简答' }
]

const columns = [
  { title: '题目', dataIndex: 'stem', key: 'stem' },
  { title: '题型', dataIndex: 'type', key: 'type', width: 110 },
  { title: '标签', dataIndex: 'tags', key: 'tags', width: 220 },
  { title: '更新时间', dataIndex: 'updatedAt', key: 'updatedAt', width: 180 },
  { title: '操作', key: 'action', width: 130 }
]

const filteredQuestions = computed(() => {
  const lowerKeyword = keyword.value.trim().toLowerCase()
  return questions.value.filter((item) => {
    const byKeyword = lowerKeyword.length === 0 || item.stem.toLowerCase().includes(lowerKeyword) || item.tags.toLowerCase().includes(lowerKeyword)
    const byType = typeFilter.value === 'all' || item.type === typeFilter.value
    return byKeyword && byType
  })
})

async function deleteQuestion(questionId: string): Promise<void> {
  try {
    await deleteEduQuestionBank({ id: questionId })
    message.success('题目已删除')
    await loadQuestions()
  } catch (error) {
    console.error('删除题目失败:', error)
    message.error('删除题目失败')
  }
}

onMounted(() => {
  loadQuestions()
})
</script>

<style scoped>
.question-toolbar {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) 220px;
  gap: 12px;
  margin-bottom: 16px;
}

@media (max-width: 960px) {
  .question-toolbar {
    grid-template-columns: 1fr;
  }
}
</style>
