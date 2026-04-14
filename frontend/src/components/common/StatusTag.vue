<template>
  <a-tag :color="resolvedColor" class="status-tag">
    <slot>{{ label }}</slot>
  </a-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  type?: 'success' | 'warning' | 'error' | 'processing' | 'default'
  label?: string
  color?: string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'default',
  label: '',
  color: ''
})

const resolvedColor = computed(() => {
  if (props.color) {
    return props.color
  }

  const palette = {
    success: 'success',
    warning: 'warning',
    error: 'error',
    processing: 'processing',
    default: 'default'
  }

  return palette[props.type]
})
</script>

<style scoped>
.status-tag {
  margin-inline-end: 0;
  padding: 4px 10px;
  border-radius: 999px;
  font-weight: 600;
}
</style>
