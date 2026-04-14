<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="题型管理" title="题型结构总览" description="在共享题库之上统一维护单选、多选与简答题型，保持作业与考试评分口径一致。" />
    </section>

    <section class="type-grid">
      <article v-for="item in questionTypes" :key="item.id" class="app-surface-card type-card">
        <h2 class="type-card__title">{{ item.name }}</h2>
        <p class="app-list-card__meta">{{ item.usage }}</p>
        <p class="type-card__summary">{{ item.summary }}</p>
        <span class="app-inline-stat">题量 {{ item.questionCount }}</span>
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
const { questionTypes } = storeToRefs(examStore)

onMounted(async () => {
  await examStore.ensureLoaded()
})
</script>

<style scoped>
.type-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: var(--space-5); }
.type-card { padding: 24px; }
.type-card__title { margin: 0; font-size: 24px; }
.type-card__summary { margin: 18px 0; color: var(--color-text-secondary); line-height: 1.8; }
</style>
