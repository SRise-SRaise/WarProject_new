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
        <a-form layout="vertical">
          <a-form-item label="题目题型" required>
            <a-select v-model:value="formState.type" size="large" :options="typeOptions" />
          </a-form-item>
          <a-form-item label="题目题干" required>
            <a-textarea v-model:value="formState.stem" :rows="4" placeholder="输入题目内容" />
          </a-form-item>
          <a-form-item label="选项（每行：A. 选项内容）">
            <a-textarea v-model:value="formState.optionsText" :rows="5" placeholder="简答/填空可留空" />
          </a-form-item>
          <a-form-item label="标准答案" required>
            <a-input v-model:value="formState.answer" size="large" />
          </a-form-item>
          <a-form-item label="分值">
            <a-input-number v-model:value="formState.maxScore" size="large" :min="1" :max="100" placeholder="满分分值" />
          </a-form-item>
          <a-form-item label="题目解析">
            <a-textarea v-model:value="formState.analysis" :rows="3" placeholder="选填，帮助学生理解答案" />
          </a-form-item>
          <a-form-item label="难度系数">
            <a-select v-model:value="formState.difficulty" size="large" placeholder="选择难度" allow-clear :options="difficultyOptions" />
          </a-form-item>
          <a-button type="primary" size="large" @click="saveQuestion">保存题目</a-button>
        </a-form>
      </section>

      <div class="hw-side-column">
        <a-alert type="info" message="录入规范" show-icon>
          <template #description>
            <ul class="hw-tip-list">
              <li>单选答案填写单个选项值，多选答案用逗号分隔，例如 A,C。</li>
              <li>填空/简答可不填选项，但需要填标准答案用于后续评分。</li>
              <li>分值和解析为选填，后续接题库时自动关联。</li>
            </ul>
          </template>
        </a-alert>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'

interface QuestionEditMock {
  id: string
  type: number
  stem: string
  optionsText: string
  answer: string
  maxScore: number | null
  analysis: string
  difficulty: number | null
}

const editMock: QuestionEditMock[] = [
  {
    id: 'q-101',
    type: 2,
    stem: '角色旅程首层分段最优方式是什么？',
    optionsText: 'A. 按数据库表名\nB. 按用户任务阶段\nC. 按代码目录结构',
    answer: 'B',
    maxScore: 10,
    analysis: '按用户任务阶段分段能更好反映业务流程。',
    difficulty: 2
  }
]

const route = useRoute()
const router = useRouter()
const questionId = computed(() => String(route.params.id || ''))
const selected = computed(() => editMock.find((item) => item.id === questionId.value))
const pageTitle = computed(() => (questionId.value ? '编辑题目' : '新增题目'))

const typeOptions = [
  { label: '填空题', value: 1 },
  { label: '单选题', value: 2 },
  { label: '多选题', value: 3 },
  { label: '判断题', value: 4 },
  { label: '简答题', value: 5 }
]

const difficultyOptions = [
  { label: '1 - 简单', value: 1 },
  { label: '2 - 一般', value: 2 },
  { label: '3 - 中等', value: 3 },
  { label: '4 - 较难', value: 4 },
  { label: '5 - 困难', value: 5 }
]

const formState = reactive({
  type: selected.value?.type ?? 2,
  stem: selected.value?.stem ?? '',
  optionsText: selected.value?.optionsText ?? '',
  answer: selected.value?.answer ?? '',
  maxScore: selected.value?.maxScore ?? null as number | null,
  analysis: selected.value?.analysis ?? '',
  difficulty: selected.value?.difficulty ?? null as number | null
})

function saveQuestion(): void {
  if (formState.stem.trim().length === 0 || formState.answer.trim().length === 0) {
    message.error('请先填写题干和标准答案。')
    return
  }
  message.success('题目已保存（Mock）。')
  router.push('/admin/questions')
}
</script>