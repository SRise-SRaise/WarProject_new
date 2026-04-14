<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="题库管理" title="题库与题型管理" description="将共享题库与题型结构收拢到同一工作台，方便教师在考试后台连续维护题源和评分口径。" />
    </section>

    <section class="app-kpi-grid">
      <MetricCard title="题库数量" :value="String(questionBanks.length)" description="当前可管理的共享题库资产。" tone="primary" />
      <MetricCard title="题目总量" :value="String(totalQuestions)" description="作业与考试共用题目池规模。" tone="success" />
      <MetricCard title="题型数量" :value="String(questionTypeCount)" description="当前统一维护的题型模板。" tone="warning" />
      <MetricCard title="共享范围" value="作业 / 考试" description="同一题库与题型在两个流程模块中复用。" tone="accent" />
    </section>

    <section class="app-split-grid question-admin-layout">
      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="题库资产" title="共享题库列表" description="围绕题目规模、共享范围与最近维护时间查看题库资产。" tight />
        <div class="app-list">
          <article v-for="item in questionBanks" :key="item.id" class="app-list-card bank-card">
            <div class="bank-card__tags">
              <a-tag v-for="tag in item.tags" :key="tag">{{ tag }}</a-tag>
            </div>
            <div class="bank-card__head">
              <h2 class="bank-card__title">{{ item.name }}</h2>
              <span class="app-inline-stat">题目 {{ item.questionCount }}</span>
            </div>
            <p class="app-list-card__meta">{{ item.owner }} · 更新于 {{ item.updatedAt }}</p>
            <p class="bank-card__summary">{{ item.description }}</p>
            <div class="bank-card__stats">
              <span class="app-inline-stat">{{ item.sharedUsage }}</span>
            </div>
          </article>
        </div>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="题型结构" title="统一题型管理" description="在同一页面集中查看题型用途、题量分布和评分结构说明。" tight />
        <div class="app-list">
          <article v-for="item in questionTypes" :key="item.id" class="app-list-card type-card">
            <div class="type-card__head">
              <h2 class="type-card__title">{{ item.name }}</h2>
              <span class="app-inline-stat">题量 {{ item.questionCount }}</span>
            </div>
            <p class="app-list-card__meta">{{ item.usage }}</p>
            <p class="type-card__summary">{{ item.summary }}</p>
          </article>
        </div>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import MetricCard from '@/components/common/MetricCard.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useExamAdminStore } from '@/stores/exam/admin'

const examStore = useExamAdminStore()
const { questionBanks, questionTypes } = storeToRefs(examStore)
const totalQuestions = computed(() => questionBanks.value.reduce((sum, item) => sum + item.questionCount, 0))
const questionTypeCount = computed(() => questionTypes.value.length)

onMounted(async () => {
  await examStore.ensureLoaded()
})
</script>

<style scoped>
.question-admin-layout {
  align-items: start;
}

.bank-card,
.type-card {
  display: grid;
  gap: var(--space-3);
}

.bank-card__head,
.type-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-3);
}

.bank-card__tags,
.bank-card__stats {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.bank-card__title,
.type-card__title {
  margin: 0;
  color: var(--color-text-main);
  font-size: 20px;
}

.bank-card__summary,
.type-card__summary {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.75;
}

@media (max-width: 960px) {
  .bank-card__head,
  .type-card__head {
    flex-direction: column;
  }
}
</style>
