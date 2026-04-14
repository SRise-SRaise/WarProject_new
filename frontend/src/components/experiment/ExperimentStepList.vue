<template>
  <div class="step-list">
    <article v-for="step in steps" :key="step.id" class="app-list-card">
      <h3 class="app-list-card__title">{{ step.title }}</h3>
      <p class="app-list-card__meta">{{ step.detail }}</p>
      <span class="app-inline-stat">交付物：{{ step.deliverable }}</span>
    </article>
  </div>
</template>

<script setup lang="ts">
import type { ExperimentStep } from '@/stores/experiment/types'

interface Props {
  steps: ExperimentStep[]
}

defineProps<Props>()
</script>

<style scoped>
.step-list {
  display: grid;
  gap: 12px;
}
</style>
