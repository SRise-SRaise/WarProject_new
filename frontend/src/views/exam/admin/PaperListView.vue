<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="试卷管理" title="试卷与共享题库联动" description="试卷围绕共享题库生成，既保证考试组卷效率，也方便作业复用相同题源。" />
    </section>

    <section class="paper-grid">
      <article v-for="item in papers" :key="item.id" class="app-surface-card paper-card">
        <div class="paper-card__tags"><a-tag v-for="tag in item.tags" :key="tag">{{ tag }}</a-tag></div>
        <h2 class="paper-card__title">{{ item.title }}</h2>
        <p class="app-list-card__meta">{{ item.topicLabel }} · {{ item.questionBankName }} · 更新于 {{ item.updatedAt }}</p>
        <p class="paper-card__summary">{{ item.summary }}</p>
        <div class="paper-card__stats">
          <span class="app-inline-stat">题目 {{ item.questionCount }}</span>
          <span class="app-inline-stat">时长 {{ item.duration }}</span>
          <span class="app-inline-stat">状态 {{ item.status }}</span>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useExamAdminStore } from '@/stores/exam/admin'

const examStore = useExamAdminStore()
const { papers } = storeToRefs(examStore)

onMounted(async () => {
  await examStore.ensureLoaded()
})
</script>

<style scoped>
.paper-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: var(--space-5); }
.paper-card { padding: 24px; }
.paper-card__tags,.paper-card__stats { display: flex; flex-wrap: wrap; gap: 8px; }
.paper-card__tags { margin-bottom: 14px; }
.paper-card__title { margin: 0; font-size: 24px; }
.paper-card__summary { margin: 18px 0; color: var(--color-text-secondary); line-height: 1.8; }
</style>
