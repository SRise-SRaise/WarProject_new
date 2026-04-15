<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="题目编辑" :title="pageTitle" description="用于新增或编辑题库题目，作业创建时可从题库选择。">
        <template #actions>
          <a-button @click="router.push('/admin/questions')">返回题目列表</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <a-form layout="vertical">
          <a-form-item label="题目题型">
            <a-select v-model:value="formState.type" size="large" :options="typeOptions" />
          </a-form-item>
          <a-form-item label="题目题干">
            <a-textarea v-model:value="formState.stem" :rows="4" />
          </a-form-item>
          <a-form-item label="选项（每行：A. 选项内容）">
            <a-textarea v-model:value="formState.optionsText" :rows="5" placeholder="简答/填空可留空" />
          </a-form-item>
          <a-form-item label="标准答案">
            <a-input v-model:value="formState.answer" size="large" />
          </a-form-item>
          <a-form-item label="标签（用 / 分隔）">
            <a-input v-model:value="formState.tagsText" size="large" />
          </a-form-item>
          <a-button type="primary" size="large" @click="saveQuestion">保存题目</a-button>
        </a-form>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="字段说明" title="录入规范" description="题库字段结构统一后，后端接口替换会更直接。" tight />
        <div class="app-list">
          <article class="app-list-card">
            <p class="app-list-card__meta">单选答案填写单个选项值，多选答案用逗号分隔，例如 A,C。</p>
          </article>
          <article class="app-list-card">
            <p class="app-list-card__meta">填空/简答可不填选项，但需要填标准答案用于后续评分规则。</p>
          </article>
        </div>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'

interface QuestionEditMock {
  id: string
  type: 'single' | 'multiple' | 'blank' | 'short'
  stem: string
  optionsText: string
  answer: string
  tagsText: string
}

// 作业模块Mock数据占位符，后续需替换到真实后端接口：GET /t_question_edit.do
const editMock: QuestionEditMock[] = [
  {
    id: 'q-101',
    type: 'single',
    stem: '角色旅程首层分段最优方式是什么？',
    optionsText: 'A. 按数据库表名\nB. 按用户任务阶段\nC. 按代码目录结构',
    answer: 'B',
    tagsText: '需求分析 / 角色旅程'
  }
]

const route = useRoute()
const router = useRouter()
const questionId = computed(() => String(route.params.id || ''))
const selected = computed(() => editMock.find((item) => item.id === questionId.value))
const pageTitle = computed(() => (questionId.value ? '编辑题目' : '新增题目'))

const typeOptions = [
  { label: '单选题', value: 'single' },
  { label: '多选题', value: 'multiple' },
  { label: '填空题', value: 'blank' },
  { label: '简答题', value: 'short' }
]

const formState = reactive({
  type: selected.value?.type ?? 'single',
  stem: selected.value?.stem ?? '',
  optionsText: selected.value?.optionsText ?? '',
  answer: selected.value?.answer ?? '',
  tagsText: selected.value?.tagsText ?? ''
})

function saveQuestion(): void {
  if (formState.stem.trim().length === 0 || formState.answer.trim().length === 0) {
    message.error('请先填写题干和标准答案。')
    return
  }
  // 作业模块Mock数据占位符，后续需替换到真实后端接口：POST /t_question_save.do
  message.success('题目已保存（Mock）。')
  router.push('/admin/questions')
}
</script>
