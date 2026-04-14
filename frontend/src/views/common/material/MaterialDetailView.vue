<template>
  <div class="app-page-shell app-panel-grid">
    <section v-if="currentMaterial" class="app-surface-card app-section-card app-panel-grid">
      <SectionHeader
        eyebrow="资料详情"
        :title="currentMaterial.title"
        :description="currentMaterial.summary"
      >
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="copyMaterialId">复制资料编号</a-button>
            <a-button @click="router.push('/materials')">返回资料列表</a-button>
            <a-button type="primary" @click="router.push('/learning')">回到学习概览</a-button>
          </a-space>
        </template>
      </SectionHeader>

      <div class="materials-detail__tags">
        <a-tag color="blue">{{ currentMaterial.type }}</a-tag>
        <a-tag>{{ currentMaterial.level }}</a-tag>
        <a-tag>{{ currentMaterial.availability === 'open' ? '已开放' : '限范围开放' }}</a-tag>
      </div>

      <div class="app-key-value">
        <div class="app-key-value__item">
          <p class="app-key-value__label">主题</p>
          <p class="app-key-value__value">{{ currentMaterial.topicLabel }}</p>
        </div>
        <div class="app-key-value__item">
          <p class="app-key-value__label">维护教师</p>
          <p class="app-key-value__value">{{ currentMaterial.teacher }}</p>
        </div>
        <div class="app-key-value__item">
          <p class="app-key-value__label">开放范围</p>
          <p class="app-key-value__value">{{ currentMaterial.audience }}</p>
        </div>
        <div class="app-key-value__item">
          <p class="app-key-value__label">更新时间</p>
          <p class="app-key-value__value">{{ currentMaterial.updatedAt }}</p>
        </div>
        <div class="app-key-value__item">
          <p class="app-key-value__label">建议时长</p>
          <p class="app-key-value__value">{{ currentMaterial.duration }}</p>
        </div>
        <div class="app-key-value__item">
          <p class="app-key-value__label">资料编号</p>
          <p class="app-key-value__value">{{ currentMaterial.id }}</p>
        </div>
      </div>
    </section>

    <section v-if="currentMaterial" class="app-split-grid">
      <div class="app-panel-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="阅读建议" title="使用说明" :description="currentMaterial.coverNote" tight />
          <div class="app-list">
            <article v-for="section in currentMaterial.sections" :key="section.heading" class="app-list-card">
              <h3 class="app-list-card__title">{{ section.heading }}</h3>
              <p class="app-list-card__meta">{{ section.content }}</p>
            </article>
          </div>
        </section>
      </div>

      <div class="app-panel-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="附件信息" title="资料清单" description="这里展示当前资料对应的附件与文件形态。" tight />
          <div class="app-list">
            <article v-for="attachment in currentMaterial.attachments" :key="attachment.name" class="app-list-card">
              <h3 class="app-list-card__title">{{ attachment.name }}</h3>
              <p class="app-list-card__meta">{{ attachment.kind }} · {{ attachment.size }}</p>
            </article>
          </div>
        </section>

        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="继续阅读" title="相关资料" description="从同一资料域中继续扩展阅读，保持学生侧信息架构连贯。" tight />
          <div class="app-list">
            <article v-for="material in relatedMaterials" :key="material.id" class="app-list-card related-material-card">
              <div>
                <h3 class="app-list-card__title">{{ material.title }}</h3>
                <p class="app-list-card__meta">{{ material.topicLabel }} · {{ material.teacher }}</p>
              </div>
              <a-button type="link" @click="router.push(`/materials/${material.id}`)">查看</a-button>
            </article>
          </div>
        </section>
      </div>
    </section>

    <a-result
      v-else-if="!loading"
      status="404"
      title="资料不存在"
      sub-title="没有找到对应资料，请返回资料列表重新选择。"
    >
      <template #extra>
        <a-button type="primary" @click="router.push('/materials')">返回资料列表</a-button>
      </template>
    </a-result>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useMaterialStore } from '@/stores/common/material'
import { CommonUtil } from '@/utils'

const route = useRoute()
const router = useRouter()
const materialStore = useMaterialStore()
const { currentMaterial, loading, materials } = storeToRefs(materialStore)

const materialId = computed(() => String(route.params.id ?? ''))
const relatedMaterials = computed(() =>
  materials.value.filter((item) => item.id !== currentMaterial.value?.id).slice(0, 3)
)

async function loadMaterial(): Promise<void> {
  if (!materialId.value) {
    return
  }
  await materialStore.selectMaterial(materialId.value)
}

async function copyMaterialId(): Promise<void> {
  if (!currentMaterial.value) {
    return
  }

  const result = await CommonUtil.copyToClipboard(currentMaterial.value.id)
  if (result.success) {
    message.success(result.message)
    return
  }
  message.error(result.message)
}

watch(materialId, () => {
  void loadMaterial()
})

onMounted(async () => {
  await materialStore.ensureLoaded()
  await loadMaterial()
})
</script>

<style scoped>
.materials-detail__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.related-material-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

@media (max-width: 720px) {
  .related-material-card {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
