<template>
  <div class="app-page-shell app-panel-grid">
    <section v-if="currentExam" class="app-surface-card app-section-card app-panel-grid">
      <SectionHeader eyebrow="考试确认" :title="currentExam.title" description="开始答题前先确认规则、题量和共享题库说明，减少误入或中途退出带来的不确定性。">
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="router.push('/exams')">返回考试列表</a-button>
            <a-button type="primary" @click="router.push(`/exams/${currentExam.id}/take`)">开始答题</a-button>
          </a-space>
        </template>
      </SectionHeader>
      <ExamMetaGrid :items="metaItems" />
    </section>

    <section v-if="currentExam" class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="规则说明" title="进入前请先确认" description="这些规则会直接影响学生端流程体验，也是考试确认页存在的主要价值。" tight />
        <div class="app-list">
          <article v-for="rule in currentExam.rules" :key="rule.label" class="app-list-card">
            <h3 class="app-list-card__title">{{ rule.label }}</h3>
            <p class="app-list-card__meta">{{ rule.content }}</p>
          </article>
        </div>
      </section>

      <section class="app-panel-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="共享题库" title="题库资产说明" :description="currentExam.questionBankNote" tight />
          <div class="app-list-card exam-note-card">
            <p class="app-list-card__meta">题库由 exam 模块统一维护，但同一资产可以被作业抽题和考试组卷共同消费。</p>
          </div>
        </section>

        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="开始前检查" title="确认就绪" description="在进入答题页前完成这些检查，可显著降低中途回退与误操作。" tight />
          <div class="app-list">
            <article v-for="tip in currentExam.readyChecklist" :key="tip" class="app-list-card">
              <p class="app-list-card__meta">{{ tip }}</p>
            </article>
          </div>
        </section>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoute, useRouter } from 'vue-router'
import ExamMetaGrid from '@/components/exam/ExamMetaGrid.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useExamStudentStore } from '@/stores/exam/student'

const route = useRoute()
const router = useRouter()
const examStore = useExamStudentStore()
const { currentExam } = storeToRefs(examStore)

const metaItems = computed(() => {
  if (!currentExam.value) return []
  return [
    { label: '所属主题', value: currentExam.value.topicLabel },
    { label: '任课教师', value: currentExam.value.teacher },
    { label: '考试时长', value: currentExam.value.duration },
    { label: '题目数量', value: `${currentExam.value.questionCount} 题` },
    { label: '考试安排', value: currentExam.value.schedule },
  ]
})

onMounted(async () => {
  await examStore.selectExam(String(route.params.id))
})
</script>

<style scoped>
.exam-note-card {
  background: linear-gradient(180deg, rgba(31, 95, 174, 0.08) 0%, rgba(255, 255, 255, 0.96) 100%);
}
</style>
