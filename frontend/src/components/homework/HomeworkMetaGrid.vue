<template>
  <div class="app-key-value">
    <div class="app-key-value__item">
      <p class="app-key-value__label">主题</p>
      <p class="app-key-value__value">{{ topicLabel }}</p>
    </div>
    <div class="app-key-value__item">
      <p class="app-key-value__label">授课教师</p>
      <p class="app-key-value__value">{{ teacher }}</p>
    </div>
    <div class="app-key-value__item">
      <p class="app-key-value__label">开放状态</p>
      <p class="app-key-value__value">{{ openTime }}</p>
    </div>
    <div class="app-key-value__item">
      <p class="app-key-value__label">截止时间</p>
      <p class="app-key-value__value">{{ deadline }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  topicLabel: string
  teacher: string
  openTime: string
  deadline: string
}

defineProps<Props>()
</script>
