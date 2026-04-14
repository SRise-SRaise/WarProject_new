<template>
  <div class="app-key-value exam-meta-grid">
    <article v-for="item in items" :key="item.label" class="app-key-value__item">
      <p class="app-key-value__label">{{ item.label }}</p>
      <p class="app-key-value__value">{{ item.value }}</p>
    </article>
  </div>
</template>

<script setup lang="ts">
interface MetaItem {
  label: string
  value: string
}

defineProps<{ items: MetaItem[] }>()
</script>

<style scoped>
.exam-meta-grid {
  gap: var(--space-3);
}
</style>
