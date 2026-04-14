<template>
  <div class="app-page-shell app-panel-grid">
    <section v-if="currentExperiment" class="app-surface-card app-section-card app-panel-grid">
      <SectionHeader eyebrow="实验结果" :title="`${currentExperiment.title} · 结果反馈`" :description="currentExperiment.summary">
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="router.push(`/experiments/${currentExperiment.id}`)">回到详情</a-button>
            <a-button type="primary" @click="router.push('/experiments')">查看其他实验</a-button>
          </a-space>
        </template>
      </SectionHeader>
      <ExperimentMetaGrid
        :topicLabel="currentExperiment.topicLabel"
        :teacher="currentExperiment.teacher"
        :schedule="currentExperiment.schedule"
        :objective="currentExperiment.objective"
      />
    </section>

    <section v-if="currentExperiment" class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="实验记录" title="我的实验结果" description="实验处理页提交的内容会在这里汇总展示。" tight />
        <article class="app-list-card">
          <p class="app-list-card__meta">{{ currentExperiment.work.note }}</p>
          <div class="result-grid">
            <span class="app-inline-stat">报告：{{ currentExperiment.work.reportName ?? '未填写' }}</span>
            <span class="app-inline-stat">得分：{{ currentExperiment.work.score ?? '待处理' }}</span>
            <span class="app-inline-stat">更新：{{ currentExperiment.work.updatedAt }}</span>
          </div>
        </article>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="教师反馈" title="处理意见" description="实验结果页承担学生查看教师反馈的唯一入口。" tight />
        <div class="app-list">
          <article v-for="item in currentExperiment.work.highlights" :key="item" class="app-list-card"><p class="app-list-card__meta">{{ item }}</p></article>
          <article class="app-list-card">
            <h3 class="app-list-card__title">教师说明</h3>
            <p class="app-list-card__meta">{{ currentExperiment.work.teacherFeedback ?? '教师尚未填写反馈。' }}</p>
          </article>
        </div>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import ExperimentMetaGrid from '@/components/experiment/ExperimentMetaGrid.vue'
import { useExperimentStudentStore } from '@/stores/experiment/student'

const route = useRoute()
const router = useRouter()
const experimentStore = useExperimentStudentStore()
const { currentExperiment } = storeToRefs(experimentStore)

onMounted(async () => {
  await experimentStore.selectExperiment(String(route.params.id))
})
</script>

<style scoped>
.result-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}
</style>
