<template>
  <section class="auth-showcase app-surface-card">
    <span class="app-pill">{{ eyebrow }}</span>
    <h2 class="auth-showcase__title">{{ title }}</h2>
    <p class="auth-showcase__description">{{ description }}</p>

    <div class="auth-showcase__list">
      <div v-for="item in highlights" :key="item.title" class="auth-showcase__item">
        <h3>{{ item.title }}</h3>
        <p>{{ item.detail }}</p>
      </div>
    </div>

    <div v-if="$slots.footer" class="auth-showcase__footer">
      <slot name="footer" />
    </div>
  </section>
</template>

<script setup lang="ts">
interface HighlightItem {
  title: string
  detail: string
}

interface Props {
  eyebrow: string
  title: string
  description: string
  highlights: HighlightItem[]
}

defineProps<Props>()
</script>

<style scoped>
.auth-showcase {
  position: relative;
  overflow: hidden;
  padding: 32px;
  background: linear-gradient(160deg, rgba(17, 47, 87, 0.98) 0%, rgba(31, 95, 174, 0.92) 100%);
  color: var(--color-text-on-dark);
}

.auth-showcase::after {
  content: '';
  position: absolute;
  inset: auto -80px -120px auto;
  width: 240px;
  height: 240px;
  border-radius: 999px;
  background: radial-gradient(circle, rgba(216, 165, 69, 0.32) 0%, rgba(216, 165, 69, 0) 72%);
}

.auth-showcase :deep(.app-pill) {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.14);
  color: rgba(255, 255, 255, 0.88);
}

.auth-showcase__title {
  position: relative;
  z-index: 1;
  margin: 20px 0 14px;
  font-family: Georgia, 'Times New Roman', 'Songti SC', serif;
  font-size: clamp(30px, 3.8vw, 44px);
  line-height: 1.12;
}

.auth-showcase__description {
  position: relative;
  z-index: 1;
  margin: 0;
  color: rgba(247, 251, 255, 0.78);
  font-size: 15px;
  line-height: 1.8;
}

.auth-showcase__list {
  position: relative;
  z-index: 1;
  display: grid;
  gap: 14px;
  margin-top: 28px;
}

.auth-showcase__item {
  padding: 18px 18px 16px;
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.auth-showcase__item h3 {
  margin: 0 0 8px;
  font-size: 17px;
}

.auth-showcase__item p {
  margin: 0;
  color: rgba(247, 251, 255, 0.72);
  font-size: 13px;
  line-height: 1.7;
}

.auth-showcase__footer {
  position: relative;
  z-index: 1;
  margin-top: 28px;
}
</style>
