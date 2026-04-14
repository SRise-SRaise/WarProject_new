<template>
  <div class="app-page-shell app-panel-grid">
    <section v-if="currentExam" class="app-surface-card app-section-card">
      <SectionHeader eyebrow="开始答题" :title="currentExam.title" description="保持节奏稳定，先完成选择题，再处理简答题。提交后会立即同步到结果页和教师后台成绩记录。">
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="router.push(`/exams/${currentExam.id}/confirm`)">返回确认页</a-button>
            <a-button type="primary" :loading="submitting" @click="submitExam">提交试卷</a-button>
          </a-space>
        </template>
      </SectionHeader>
    </section>

    <section v-if="currentExam" class="app-split-grid">
      <section class="app-panel-grid">
        <article v-for="(question, index) in currentExam.questions" :key="question.id" class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader :eyebrow="`第 ${index + 1} 题 · ${question.kind === 'short' ? '简答题' : question.kind === 'multiple' ? '多选题' : '单选题'}`" :title="question.title" :description="question.prompt" tight />
          <a-radio-group v-if="question.kind === 'single'" v-model:value="answerState[question.id]" class="exam-answer-grid">
            <a-radio v-for="option in question.options" :key="option.key" :value="option.key">{{ option.label }}</a-radio>
          </a-radio-group>
          <a-checkbox-group v-else-if="question.kind === 'multiple'" v-model:value="answerState[question.id]" class="exam-answer-grid">
            <a-checkbox v-for="option in question.options" :key="option.key" :value="option.key">{{ option.label }}</a-checkbox>
          </a-checkbox-group>
          <a-textarea v-else v-model:value="answerState[question.id]" :rows="5" placeholder="请输入结构化答题内容，建议围绕共享资产、模块边界和业务主场景展开。" />
          <div class="exam-question__footer">
            <span class="app-inline-stat">{{ question.score }} 分</span>
            <span class="app-subtle-text">{{ question.answerTip }}</span>
          </div>
        </article>
      </section>

      <section class="app-panel-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="答题信息" title="当前考试概览" description="确认时长、题量和共享题库提示，避免在题目中来回跳转。" tight />
          <ExamMetaGrid :items="metaItems" />
        </section>
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="共享题库" title="本场考试使用说明" :description="currentExam.questionBankNote" tight />
          <div class="app-list-card">
            <p class="app-list-card__meta">题库由 exam 模块统一维护，但同一资产也会为作业抽题提供题目来源。</p>
          </div>
        </section>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import ExamMetaGrid from '@/components/exam/ExamMetaGrid.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useExamStudentStore } from '@/stores/exam/student'

const route = useRoute()
const router = useRouter()
const examStore = useExamStudentStore()
const { currentExam } = storeToRefs(examStore)
const submitting = ref(false)
const answerState = reactive<Record<string, string | string[]>>({})

const metaItems = computed(() => {
  if (!currentExam.value) return []
  return [
    { label: '所属主题', value: currentExam.value.topicLabel },
    { label: '考试时长', value: currentExam.value.duration },
    { label: '题目数量', value: `${currentExam.value.questionCount} 题` },
    { label: '考试安排', value: currentExam.value.schedule },
  ]
})

async function submitExam(): Promise<void> {
  if (!currentExam.value) return
  if (Object.keys(answerState).length === 0) {
    message.error('请先完成至少一道题目。')
    return
  }
  submitting.value = true
  try {
    await examStore.submitExam({ ...answerState })
    message.success('试卷已提交。')
    router.push(`/exams/${currentExam.value.id}/result`)
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  const exam = await examStore.selectExam(String(route.params.id))
  if (!exam) return
  Object.entries(exam.answerDraft).forEach(([key, value]) => {
    answerState[key] = Array.isArray(value) ? [...value] : value
  })
})
</script>

<style scoped>
.exam-answer-grid {
  display: grid;
  gap: 12px;
}

.exam-question__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

@media (max-width: 720px) {
  .exam-question__footer {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
