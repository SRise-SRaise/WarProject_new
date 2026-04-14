<template>
  <div class="app-key-value">
    <div class="app-key-value__item">
      <p class="app-key-value__label">主题</p>
      <p class="app-key-value__value">{{ topicLabel }}</p>
    </div>
    <div class="app-key-value__item">
      <p class="app-key-value__label">教师</p>
      <p class="app-key-value__value">{{ teacher }}</p>
    </div>
    <div class="app-key-value__item">
      <p class="app-key-value__label">实验安排</p>
      <p class="app-key-value__value">{{ schedule }}</p>
    </div>
    <div class="app-key-value__item">
      <p class="app-key-value__label">实验目标</p>
      <p class="app-key-value__value">{{ objective }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  topicLabel: string
  teacher: string
  schedule: string
  objective: string
}

defineProps<Props>()
</script>
