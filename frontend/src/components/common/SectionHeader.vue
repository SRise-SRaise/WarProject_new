<template>
  <div class="section-header" :class="{ 'section-header--tight': tight }">
    <div class="section-header__main">
      <p v-if="eyebrow" class="section-header__eyebrow">{{ eyebrow }}</p>
      <div class="section-header__title-row">
        <h2 class="section-header__title">{{ title }}</h2>
        <slot name="suffix" />
      </div>
      <p v-if="description" class="section-header__description">{{ description }}</p>
    </div>
    <div v-if="$slots.actions" class="section-header__actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  title: string
  eyebrow?: string
  description?: string
  tight?: boolean
}

defineProps<Props>()
</script>

<style scoped>
.section-header {
  display: flex;
  justify-content: space-between;
  gap: var(--space-4);
  align-items: flex-start;
}

.section-header--tight {
  gap: var(--space-3);
}

.section-header__main {
  min-width: 0;
}

.section-header__eyebrow {
  margin: 0 0 10px;
  color: var(--color-primary);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.section-header__title-row {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.section-header__title {
  margin: 0;
  color: var(--color-text-main);
  font-family: Georgia, 'Times New Roman', 'Songti SC', serif;
  font-size: clamp(24px, 2.6vw, 34px);
  line-height: 1.1;
}

.section-header__description {
  max-width: 720px;
  margin: 14px 0 0;
  color: var(--color-text-secondary);
  font-size: 15px;
  line-height: 1.75;
}

.section-header__actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
  }

  .section-header__actions {
    width: 100%;
  }
}
</style>
