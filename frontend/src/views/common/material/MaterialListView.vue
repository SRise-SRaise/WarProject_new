<template>
  <div class="app-page-shell app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader
        eyebrow="资料"
        title="资料列表"
        description="按主题、类型和关键词快速定位当前首波开放的学习资源。资料页同时承担学生侧公共资源入口的职责。"
      >
        <template #actions>
          <a-button type="primary" @click="router.push('/learning')">返回学习概览</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="app-surface-card app-section-card app-panel-grid">
      <div class="materials-toolbar">
        <a-input
          v-model:value="materialStore.filters.keyword"
          allow-clear
          size="large"
          placeholder="搜索资料标题、摘要或标签"
        />
        <a-select v-model:value="materialStore.filters.topicLabel" size="large" :options="topicLabelOptions" />
        <a-select v-model:value="materialStore.filters.type" size="large" :options="typeOptions" />
        <a-button size="large" @click="materialStore.resetFilters()">重置筛选</a-button>
      </div>

      <div class="materials-overview">
        <span class="app-inline-stat">当前共 {{ filteredMaterials.length }} 份资料</span>
        <span class="app-inline-stat">覆盖 {{ visibleTopicCount }} 个主题</span>
        <span class="app-inline-stat">累计 {{ visibleDownloads }} 次查看</span>
      </div>
    </section>

    <section v-if="filteredMaterials.length > 0" class="app-surface-card app-section-card app-panel-grid">
      <div class="materials-list-head">
        <div>
          <p class="materials-list-head__eyebrow">资料清单</p>
          <h2 class="materials-list-head__title">纵向列表浏览</h2>
        </div>
        <p class="materials-list-head__description">每条资料保持关键信息、主题标签与详情入口，便于快速比对和连续阅读。</p>
      </div>

      <div class="materials-list">
        <article v-for="material in filteredMaterials" :key="material.id" class="materials-row">
          <div class="materials-row__main">
            <div class="materials-row__tags">
              <a-tag color="blue">{{ material.type }}</a-tag>
              <a-tag>{{ material.level }}</a-tag>
              <a-tag :color="material.availability === 'open' ? 'green' : 'gold'">
                {{ material.availability === 'open' ? '已开放' : '限范围开放' }}
              </a-tag>
            </div>

            <h2 class="materials-row__title">{{ material.title }}</h2>
            <p class="materials-row__summary">{{ material.summary }}</p>

            <div class="materials-row__chips">
              <a-tag v-for="tag in material.tags" :key="tag">{{ tag }}</a-tag>
            </div>
          </div>

          <div class="materials-row__meta">
            <div class="materials-row__meta-item">
              <p class="materials-row__meta-label">主题</p>
              <p class="materials-row__meta-value">{{ material.topicLabel }}</p>
            </div>
            <div class="materials-row__meta-item">
              <p class="materials-row__meta-label">维护教师</p>
              <p class="materials-row__meta-value">{{ material.teacher }}</p>
            </div>
            <div class="materials-row__meta-item">
              <p class="materials-row__meta-label">开放范围</p>
              <p class="materials-row__meta-value">{{ material.audience }}</p>
            </div>
            <div class="materials-row__meta-item">
              <p class="materials-row__meta-label">建议时长</p>
              <p class="materials-row__meta-value">{{ material.duration }}</p>
            </div>
            <div class="materials-row__meta-item">
              <p class="materials-row__meta-label">更新时间</p>
              <p class="materials-row__meta-value">{{ material.updatedAt }}</p>
            </div>
          </div>

          <div class="materials-row__aside">
            <span class="app-inline-stat">{{ material.downloads }} 次查看</span>
            <p class="materials-row__id">资料编号 {{ material.id }}</p>
            <a-button type="primary" @click="router.push(`/materials/${material.id}`)">查看详情</a-button>
          </div>
        </article>
      </div>
    </section>

    <EmptyState v-else description="未找到符合条件的资料，可尝试调整筛选条件。" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import EmptyState from '@/components/common/EmptyState.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useMaterialStore } from '@/stores/common/material'

const router = useRouter()
const materialStore = useMaterialStore()
const { filteredMaterials, topicLabelOptions, typeOptions } = storeToRefs(materialStore)

const visibleDownloads = computed(() =>
  filteredMaterials.value.reduce((sum, material) => sum + material.downloads, 0)
)

const visibleTopicCount = computed(() => new Set(filteredMaterials.value.map((material) => material.topicLabel)).size)

onMounted(async () => {
  materialStore.resetFilters()
  await materialStore.ensureLoaded()
})
</script>

<style scoped>
.materials-toolbar {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) 220px 180px auto;
  gap: 12px;
}

.materials-overview {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.materials-list-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(194, 206, 222, 0.55);
}

.materials-list-head__eyebrow {
  margin: 0 0 8px;
  color: var(--color-primary);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.materials-list-head__title {
  margin: 0;
  color: var(--color-text-main);
  font-family: Georgia, 'Times New Roman', 'Songti SC', serif;
  font-size: clamp(22px, 2.4vw, 30px);
  line-height: 1.1;
}

.materials-list-head__description {
  max-width: 520px;
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.75;
  text-align: right;
}

.materials-list {
  display: grid;
  gap: 18px;
}

.materials-row {
  display: grid;
  grid-template-columns: minmax(0, 1.7fr) minmax(280px, 1.15fr) 180px;
  gap: 20px;
  padding: 22px 24px;
  border: 1px solid rgba(194, 206, 222, 0.55);
  border-radius: var(--radius-md);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(246, 248, 251, 0.96) 100%);
  box-shadow: var(--shadow-inset);
}

.materials-row__main,
.materials-row__aside {
  display: grid;
  align-content: start;
  gap: 14px;
}

.materials-row__tags,
.materials-row__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.materials-row__title {
  margin: 0;
  color: var(--color-text-main);
  font-size: 24px;
  line-height: 1.2;
}

.materials-row__summary {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.8;
}

.materials-row__meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.materials-row__meta-item {
  padding: 14px 16px;
  border-radius: var(--radius-sm);
  background: rgba(255, 255, 255, 0.84);
  border: 1px solid rgba(194, 206, 222, 0.45);
}

.materials-row__meta-label {
  margin: 0 0 6px;
  color: var(--color-text-tertiary);
  font-size: 12px;
}

.materials-row__meta-value {
  margin: 0;
  color: var(--color-text-main);
  font-size: 14px;
  font-weight: 600;
  line-height: 1.6;
}

.materials-row__aside {
  justify-items: flex-end;
}

.materials-row__id {
  margin: 0;
  color: var(--color-text-tertiary);
  font-size: 13px;
}

@media (max-width: 1100px) {
  .materials-row {
    grid-template-columns: minmax(0, 1fr);
  }

  .materials-row__aside {
    justify-items: flex-start;
  }
}

@media (max-width: 980px) {
  .materials-toolbar {
    grid-template-columns: 1fr;
  }

  .materials-list-head {
    align-items: flex-start;
    flex-direction: column;
  }

  .materials-list-head__description {
    text-align: left;
  }
}

@media (max-width: 720px) {
  .materials-row {
    padding: 18px;
  }

  .materials-row__title {
    font-size: 21px;
  }

  .materials-row__meta {
    grid-template-columns: 1fr;
  }
}
</style>
