<template>
  <div class="empty-state app-surface-card">
    <a-empty :description="description">
      <template #image>
        <slot name="icon">
          <InboxOutlined class="empty-state__icon" />
        </slot>
      </template>
      <slot />
    </a-empty>
  </div>
</template>

<script setup lang="ts">
import { InboxOutlined } from '@ant-design/icons-vue'

interface Props {
  description?: string
}

withDefaults(defineProps<Props>(), {
  description: '暂无数据'
})
</script>

<style scoped>
.empty-state {
  padding: 12px;
  text-align: center;
}

.empty-state__icon {
  font-size: 52px;
  color: var(--color-primary);
  opacity: 0.2;
}
</style>
