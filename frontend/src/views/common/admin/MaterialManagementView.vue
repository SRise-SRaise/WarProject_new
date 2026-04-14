<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader
        eyebrow="资料管理"
        title="共享资料管理台账"
        description="沿用学生侧的共享资料数据源，在教师后台统一查看资料覆盖范围、开放策略与最近更新。本波次保持只读，不在这里直接新增或编辑内容。"
      >
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="router.push('/materials')">查看学生资料页</a-button>
            <a-button type="primary" :loading="loading" @click="refreshMaterials">刷新资料</a-button>
          </a-space>
        </template>
      </SectionHeader>
    </section>

    <section class="app-kpi-grid">
      <MetricCard title="资料总数" :value="String(materials.length)" description="当前已经接入后台台账的共享资料数量。" tone="primary" />
      <MetricCard title="覆盖主题" :value="String(topicCoverage)" description="按当前资料数据源计算出的主题域数量。" tone="accent" />
      <MetricCard title="限范围开放" :value="String(limitedCount)" description="仅面向指定班级或方向开放的资料数量。" tone="warning" />
      <MetricCard title="累计查看" :value="totalDownloadsLabel" description="用于判断资料热度的当前查看量累计。" tone="success" />
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
      <div class="materials-toolbar__summary">
        <span class="app-inline-stat">当前显示 {{ filteredMaterials.length }} 份资料</span>
        <span class="app-subtle-text">覆盖 {{ teacherCoverage }} 位维护教师，最近更新 {{ latestUpdatedAt || '暂无记录' }}</span>
      </div>
    </section>

    <section v-if="filteredMaterials.length > 0" class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader
          eyebrow="资料总表"
          title="当前资料清单"
          description="点击资料名称即可在右侧查看摘要、附件和章节信息。当前页面只承担查看与筛选，不接入编辑动作。"
          tight
        />
        <a-table
          :columns="materialColumns"
          :data-source="filteredMaterials"
          :loading="loading"
          row-key="id"
          :pagination="{ pageSize: 6, hideOnSinglePage: true }"
          :scroll="{ x: 1100 }"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'title'">
              <button class="material-row-trigger" type="button" @click="selectMaterial(record.id)">
                <span class="material-row-trigger__head">
                  <span class="material-row-trigger__title">{{ record.title }}</span>
                  <span
                    class="material-row-trigger__hint"
                    :class="{ 'material-row-trigger__hint--active': selectedMaterial?.id === record.id }"
                  >
                    {{ selectedMaterial?.id === record.id ? '当前侧览' : '查看侧览' }}
                  </span>
                </span>
                <span class="material-row-trigger__summary">{{ record.summary }}</span>
              </button>
            </template>
            <template v-else-if="column.key === 'type'">
              <a-tag color="blue">{{ record.type }}</a-tag>
            </template>
            <template v-else-if="column.key === 'availability'">
              <StatusTag :type="availabilityTone(record.availability)" :label="availabilityLabel(record.availability)" />
            </template>
            <template v-else-if="column.key === 'downloads'">
              <span class="app-inline-stat">{{ record.downloads }} 次</span>
            </template>
          </template>
        </a-table>
      </section>

      <div class="app-panel-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader
            eyebrow="资料侧览"
            :title="selectedMaterial?.title ?? '暂无资料'"
            :description="selectedMaterial?.summary ?? '筛选结果为空时不会显示资料侧览。'"
            tight
          >
            <template #actions>
              <a-button
                v-if="selectedMaterial"
                type="link"
                @click="router.push(`/materials/${selectedMaterial.id}`)"
              >
                查看公共详情页
              </a-button>
            </template>
          </SectionHeader>

          <template v-if="selectedMaterial">
            <div class="materials-preview__tags">
              <a-tag color="blue">{{ selectedMaterial.type }}</a-tag>
              <a-tag>{{ selectedMaterial.level }}</a-tag>
              <StatusTag
                :type="availabilityTone(selectedMaterial.availability)"
                :label="availabilityLabel(selectedMaterial.availability)"
              />
            </div>

            <div class="app-key-value">
              <div class="app-key-value__item">
                <p class="app-key-value__label">主题</p>
                <p class="app-key-value__value">{{ selectedMaterial.topicLabel }}</p>
              </div>
              <div class="app-key-value__item">
                <p class="app-key-value__label">维护教师</p>
                <p class="app-key-value__value">{{ selectedMaterial.teacher }}</p>
              </div>
              <div class="app-key-value__item">
                <p class="app-key-value__label">开放范围</p>
                <p class="app-key-value__value">{{ selectedMaterial.audience }}</p>
              </div>
              <div class="app-key-value__item">
                <p class="app-key-value__label">建议时长</p>
                <p class="app-key-value__value">{{ selectedMaterial.duration }}</p>
              </div>
              <div class="app-key-value__item">
                <p class="app-key-value__label">最近更新</p>
                <p class="app-key-value__value">{{ selectedMaterial.updatedAt }}</p>
              </div>
              <div class="app-key-value__item">
                <p class="app-key-value__label">资料编号</p>
                <p class="app-key-value__value">{{ selectedMaterial.id }}</p>
              </div>
            </div>

            <section class="materials-preview__section">
              <h3 class="materials-preview__title">资料封面说明</h3>
              <p class="materials-preview__paragraph">{{ selectedMaterial.coverNote }}</p>
            </section>

            <section class="materials-preview__section">
              <h3 class="materials-preview__title">附件清单</h3>
              <div class="app-list">
                <article
                  v-for="attachment in selectedMaterial.attachments"
                  :key="attachment.name"
                  class="app-list-card preview-item-card"
                >
                  <div>
                    <h3 class="app-list-card__title">{{ attachment.name }}</h3>
                    <p class="app-list-card__meta">{{ attachment.kind }}</p>
                  </div>
                  <span class="app-inline-stat">{{ attachment.size }}</span>
                </article>
              </div>
            </section>

            <section class="materials-preview__section">
              <h3 class="materials-preview__title">章节摘要</h3>
              <div class="app-list">
                <article v-for="section in selectedMaterial.sections" :key="section.heading" class="app-list-card">
                  <h3 class="app-list-card__title">{{ section.heading }}</h3>
                  <p class="app-list-card__meta">{{ section.content }}</p>
                </article>
              </div>
            </section>
          </template>
        </section>

        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader
            eyebrow="覆盖结构"
            title="主题分布"
            description="帮助教师快速判断当前资料集中在哪些主题域，以及主题对应的维护教师。"
            tight
          />
          <div class="app-list">
            <article v-for="item in topicBreakdown" :key="item.label" class="app-list-card topic-card">
              <div>
                <h3 class="app-list-card__title">{{ item.label }}</h3>
                <p class="app-list-card__meta">{{ item.teachers.join(' / ') }}</p>
              </div>
              <span class="app-inline-stat">{{ item.count }} 份</span>
            </article>
          </div>
        </section>
      </div>
    </section>

    <EmptyState v-else description="未找到符合条件的资料，可尝试调整筛选条件。" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import EmptyState from '@/components/common/EmptyState.vue'
import MetricCard from '@/components/common/MetricCard.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { useMaterialStore } from '@/stores/common/material'

const router = useRouter()
const materialStore = useMaterialStore()
const { filteredMaterials, loading, materials, topicLabelOptions, typeOptions } = storeToRefs(materialStore)

const selectedMaterialId = ref('')

const limitedCount = computed(() => materials.value.filter((item) => item.availability === 'limited').length)
const topicCoverage = computed(() => topicLabelOptions.value.filter((item) => item.value !== 'all').length)
const totalDownloadsLabel = computed(() => materials.value.reduce((sum, item) => sum + item.downloads, 0).toLocaleString('zh-CN'))
const teacherCoverage = computed(() => new Set(materials.value.map((item) => item.teacher)).size)
const latestUpdatedAt = computed(() => materials.value.map((item) => item.updatedAt).sort((left, right) => right.localeCompare(left))[0] ?? '')

const selectedMaterial = computed(() => {
  return filteredMaterials.value.find((item) => item.id === selectedMaterialId.value) ?? filteredMaterials.value[0] ?? null
})

const topicBreakdown = computed(() => {
  const summary = new Map<string, { label: string; count: number; teachers: Set<string> }>()

  for (const item of filteredMaterials.value) {
    const existing = summary.get(item.topicLabel)
    if (existing) {
      existing.count += 1
      existing.teachers.add(item.teacher)
      continue
    }

    summary.set(item.topicLabel, {
      label: item.topicLabel,
      count: 1,
      teachers: new Set([item.teacher])
    })
  }

  return [...summary.values()]
    .map((item) => ({
      label: item.label,
      count: item.count,
      teachers: [...item.teachers]
    }))
    .sort((left, right) => right.count - left.count || left.label.localeCompare(right.label))
})

const materialColumns = [
  { title: '资料', dataIndex: 'title', key: 'title', width: 320 },
  { title: '主题', dataIndex: 'topicLabel', key: 'topicLabel', width: 180 },
  { title: '类型', dataIndex: 'type', key: 'type', width: 110 },
  { title: '维护教师', dataIndex: 'teacher', key: 'teacher', width: 120 },
  { title: '开放状态', dataIndex: 'availability', key: 'availability', width: 120 },
  { title: '查看量', dataIndex: 'downloads', key: 'downloads', width: 110 },
  { title: '最近更新', dataIndex: 'updatedAt', key: 'updatedAt', width: 170 }
]

function selectMaterial(id: string): void {
  selectedMaterialId.value = id
}

function availabilityTone(availability: 'open' | 'limited'): 'success' | 'warning' {
  return availability === 'open' ? 'success' : 'warning'
}

function availabilityLabel(availability: 'open' | 'limited'): string {
  return availability === 'open' ? '已开放' : '限范围开放'
}

async function refreshMaterials(): Promise<void> {
  await materialStore.refresh()
  message.success('资料台账已刷新')
}

watch(
  filteredMaterials,
  (nextList) => {
    if (nextList.some((item) => item.id === selectedMaterialId.value)) {
      return
    }

    selectedMaterialId.value = nextList[0]?.id ?? ''
  },
  { immediate: true }
)

onMounted(async () => {
  materialStore.resetFilters()
  await materialStore.ensureLoaded()
})
</script>

<style scoped>
.materials-toolbar {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) 220px 180px auto;
  gap: var(--space-3);
}

.materials-toolbar__summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
}

.material-row-trigger {
  display: block;
  width: 100%;
  padding: 0;
  border: none;
  background: transparent;
  color: inherit;
  text-align: left;
  cursor: pointer;
}

.material-row-trigger__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-3);
}

.material-row-trigger__title {
  display: block;
  color: var(--color-text-main);
  font-size: 15px;
  font-weight: 700;
  line-height: 1.5;
}

.material-row-trigger__summary {
  display: block;
  margin-top: var(--space-2);
  color: var(--color-text-secondary);
  font-size: 13px;
  line-height: 1.75;
}

.material-row-trigger__hint {
  color: var(--color-text-tertiary);
  font-size: 12px;
  font-weight: 700;
  white-space: nowrap;
}

.material-row-trigger__hint--active {
  color: var(--color-primary);
}

.materials-preview__tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.materials-preview__section {
  display: grid;
  gap: var(--space-3);
}

.materials-preview__title {
  margin: 0;
  color: var(--color-text-main);
  font-size: 16px;
  font-weight: 700;
}

.materials-preview__paragraph {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.75;
}

.preview-item-card,
.topic-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
}

@media (max-width: 980px) {
  .materials-toolbar {
    grid-template-columns: 1fr;
  }

  .materials-toolbar__summary,
  .preview-item-card,
  .topic-card {
    align-items: flex-start;
    flex-direction: column;
  }
}

@media (max-width: 720px) {
  .material-row-trigger__head {
    flex-direction: column;
  }
}
</style>
