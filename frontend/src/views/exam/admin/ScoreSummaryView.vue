<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="成绩汇总" title="考试结果看板" description="从平均分、优秀率、预警人数和题库复用率四个维度观察考试结果。" />
    </section>

    <section class="app-kpi-grid">
      <MetricCard v-for="item in scoreSummary" :key="item.label" :title="item.label" :value="item.value" :description="item.detail" :tone="item.tone" />
    </section>

    <section class="app-surface-card app-section-card app-panel-grid">
      <SectionHeader eyebrow="考试概览" title="当前考试清单" description="把考试状态、均分和共享题库使用情况统一收在成绩汇总页中。" tight />
      <div class="app-list">
        <article v-for="item in exams" :key="item.id" class="app-list-card summary-card">
          <div class="summary-card__head">
            <div>
              <h3 class="app-list-card__title">{{ item.title }}</h3>
              <p class="app-list-card__meta">{{ item.topicLabel }} · {{ item.schedule }} · {{ item.paperTitle }}</p>
            </div>
            <span class="app-inline-stat">均分 {{ item.averageScore }}</span>
          </div>
          <p class="app-list-card__meta">{{ item.summary }}</p>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import MetricCard from '@/components/common/MetricCard.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useExamAdminStore } from '@/stores/exam/admin'

const examStore = useExamAdminStore()
const { scoreSummary, exams } = storeToRefs(examStore)

onMounted(async () => {
  await examStore.ensureLoaded()
})
</script>

<style scoped>
.summary-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

@media (max-width: 720px) {
  .summary-card__head {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
