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
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'

interface QuestionItem {
  id: string
  stem: string
  type: '填空' | '单选' | '多选' | '判断' | '简答'
  tags: string
  updatedAt: string
}

const questionMock = ref<QuestionItem[]>([
  { id: 'q-101', stem: '角色旅程首层分段最优方式是什么？', type: '单选', tags: '需求分析 / 角色旅程', updatedAt: '2026-04-15 09:20' },
  { id: 'q-102', stem: '哪些内容必须出现在验收边界中？', type: '多选', tags: '验收口径 / 需求边界', updatedAt: '2026-04-14 18:30' },
  { id: 'q-103', stem: '请填写"异常流"的定义。', type: '填空', tags: '基础概念', updatedAt: '2026-04-13 11:05' },
  { id: 'q-104', stem: '简述角色旅程图的核心价值。', type: '简答', tags: '分析表达', updatedAt: '2026-04-12 16:40' },
  { id: 'q-105', stem: 'HTML中用于插入图片的标签是？', type: '填空', tags: 'HTML基础', updatedAt: '2026-04-11 14:22' },
  { id: 'q-106', stem: '以下哪些是CSS布局方式？', type: '多选', tags: 'CSS布局', updatedAt: '2026-04-10 10:15' },
  { id: 'q-107', stem: 'JavaScript中===和==的区别是什么？', type: '简答', tags: 'JavaScript基础', updatedAt: '2026-04-09 17:00' },
  { id: 'q-108', stem: 'Vue3的组合式API使用哪个函数创建响应式数据？', type: '单选', tags: 'Vue3', updatedAt: '2026-04-08 09:45' }
])

const router = useRouter()
const keyword = ref('')
const typeFilter = ref<'all' | QuestionItem['type']>('all')

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
  return questionMock.value.filter((item) => {
    const byKeyword = lowerKeyword.length === 0 || item.stem.toLowerCase().includes(lowerKeyword) || item.tags.toLowerCase().includes(lowerKeyword)
    const byType = typeFilter.value === 'all' || item.type === typeFilter.value
    return byKeyword && byType
  })
})

function deleteQuestion(questionId: string): void {
  questionMock.value = questionMock.value.filter((item) => item.id !== questionId)
  message.success('题目已删除（Mock）。')
}
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