<template>
  <div class="app-page-shell app-panel-grid">
    <section v-if="currentExam" class="app-surface-card app-section-card">
      <SectionHeader eyebrow="考试结果" :title="currentExam.title" :description="currentExam.result.feedback">
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="router.push('/exams')">返回考试列表</a-button>
            <a-button type="primary" @click="router.push('/materials')">继续查看资料</a-button>
          </a-space>
        </template>
      </SectionHeader>
    </section>

    <section v-if="currentExam" class="app-kpi-grid">
      <MetricCard title="成绩" :value="currentExam.result.score" description="本次作答结果会同步到教师成绩汇总页。" tone="primary" />
      <MetricCard title="排名提示" :value="currentExam.result.rankHint" description="结合当前批次成绩给出的即时反馈。" tone="success" />
      <MetricCard title="用时" :value="currentExam.result.durationUsed" description="用于判断答题节奏与准备充分度。" tone="accent" />
      <MetricCard title="共享题库" value="已复用" description="本场考试继续复用 exam 模块维护的共享题库资产。" tone="warning" />
    </section>

    <section v-if="currentExam" class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="结果亮点" title="这次作答的即时反馈" description="学生端先看结果亮点，再回到资料或下一场考试准备。" tight />
        <div class="app-list">
          <article v-for="highlight in currentExam.result.highlights" :key="highlight" class="app-list-card">
            <p class="app-list-card__meta">{{ highlight }}</p>
          </article>
        </div>
      </section>
      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="提交信息" title="成绩同步状态" description="确认提交时间、结果同步和共享题库说明。" tight />
        <ExamMetaGrid :items="metaItems" />
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoute, useRouter } from 'vue-router'
import ExamMetaGrid from '@/components/exam/ExamMetaGrid.vue'
import MetricCard from '@/components/common/MetricCard.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useExamStudentStore } from '@/stores/exam/student'

const route = useRoute()
const router = useRouter()
const examStore = useExamStudentStore()
const { currentExam } = storeToRefs(examStore)

const metaItems = computed(() => {
  if (!currentExam.value) return []
  return [
    { label: '提交时间', value: currentExam.value.result.submittedAt },
    { label: '考试时长', value: currentExam.value.duration },
    { label: '本次用时', value: currentExam.value.result.durationUsed },
    { label: '题库说明', value: '由 exam 模块统一维护，服务作业与考试' },
  ]
})

onMounted(async () => {
  await examStore.selectExam(String(route.params.id))
})
</script>
