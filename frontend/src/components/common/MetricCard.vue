<template>
  <div class="metric-card" :class="`metric-card--${tone}`">
    <p class="metric-card__label">{{ title }}</p>
    <div class="metric-card__value-row">
      <strong class="metric-card__value">{{ value }}</strong>
      <span v-if="trend" class="metric-card__trend">{{ trend }}</span>
    </div>
    <p v-if="description" class="metric-card__description">{{ description }}</p>
  </div>
</template>

<script setup lang="ts">
interface Props {
  title: string
  value: string
  description?: string
  trend?: string
  tone?: 'primary' | 'accent' | 'success' | 'warning'
}

withDefaults(defineProps<Props>(), {
  description: '',
  trend: '',
  tone: 'primary'
})
</script>

<style scoped>
.metric-card {
  padding: 22px 20px;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  background: #ffffff;
}

.metric-card--primary {
  --metric-tone: var(--color-primary);
}

.metric-card--accent {
  --metric-tone: var(--color-accent);
}

.metric-card--success {
  --metric-tone: var(--color-success);
}

.metric-card--warning {
  --metric-tone: var(--color-warning);
}

.metric-card__label {
  margin: 0 0 14px;
  color: var(--metric-tone);
  font-size: 13px;
  font-weight: 600;
}

.metric-card__value-row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: var(--space-3);
}

.metric-card__value {
  color: var(--color-text-main);
  font-size: clamp(28px, 3vw, 38px);
  line-height: 1;
}

.metric-card__trend {
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(17, 47, 87, 0.08);
  color: var(--color-primary-deep);
  font-size: 12px;
  font-weight: 700;
}

.metric-card__description {
  margin: 14px 0 0;
  color: var(--color-text-tertiary);
  font-size: 13px;
  line-height: 1.6;
}
</style>
