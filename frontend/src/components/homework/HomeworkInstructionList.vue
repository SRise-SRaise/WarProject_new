<template>
  <div class="instruction-list">
    <article v-for="item in items" :key="item.title" class="app-list-card">
      <h3 class="app-list-card__title">{{ item.title }}</h3>
      <p class="app-list-card__meta">{{ item.content }}</p>
    </article>
  </div>
</template>

<script setup lang="ts">
import type { HomeworkSection } from '@/stores/homework/types'

interface Props {
  items: HomeworkSection[]
}

defineProps<Props>()
</script>

<style scoped>
.instruction-list {
  display: grid;
  gap: 12px;
}
</style>
