<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="实验编辑" :title="pageTitle" description="先维护实验基本信息，再到实验项页完善具体步骤。">
        <template #actions>
          <a-button @click="router.push('/admin/experiments')">返回实验列表</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <a-form layout="vertical">
          <a-form-item label="实验标题"><a-input v-model:value="formState.title" size="large" /></a-form-item>
          <a-form-item label="所属主题"><a-input v-model:value="formState.topicLabel" size="large" /></a-form-item>
          <a-form-item label="实验安排"><a-input v-model:value="formState.schedule" size="large" placeholder="例如：2026-04-20 18:00" /></a-form-item>
          <a-form-item label="实验摘要"><a-textarea v-model:value="formState.summary" :rows="5" /></a-form-item>
          <a-form-item label="标签（用 / 分隔）"><a-input v-model:value="formState.tagsText" size="large" /></a-form-item>
          <a-button type="primary" size="large" :loading="saving" @click="saveExperiment">保存实验</a-button>
        </a-form>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="编辑说明" title="当前策略" description="实验项和结果处理分到后续页面，让编辑页只负责实验基础信息。" tight />
        <div class="app-list">
          <article class="app-list-card"><p class="app-list-card__meta">保存后会跳转到实验项页继续完善步骤、交付物和提示说明。</p></article>
        </div>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useExperimentAdminStore } from '@/stores/experiment/admin'

const route = useRoute()
const router = useRouter()
const experimentStore = useExperimentAdminStore()
const saving = ref(false)
const experimentId = computed(() => (typeof route.params.id === 'string' ? route.params.id : ''))
const pageTitle = computed(() => (experimentId.value ? '编辑现有实验' : '新建实验草稿'))
const formState = reactive({ title: '', topicLabel: '', schedule: '', summary: '', tagsText: '' })

function parseTags(value: string): string[] {
  return value.split('/').map((item) => item.trim()).filter((item) => item.length > 0)
}

async function saveExperiment(): Promise<void> {
  if (formState.title.trim().length === 0 || formState.topicLabel.trim().length === 0) {
    message.error('请先填写标题和主题。')
    return
  }
  saving.value = true
  try {
    const saved = await experimentStore.saveExperiment({
      id: experimentId.value || undefined,
      title: formState.title,
      topicLabel: formState.topicLabel,
      schedule: formState.schedule,
      summary: formState.summary,
      tags: parseTags(formState.tagsText)
    })
    message.success('实验已保存。')
    router.push(`/admin/experiments/items/${saved.id}`)
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await experimentStore.ensureLoaded()
  if (!experimentId.value) return
  const item = await experimentStore.selectExperiment(experimentId.value)
  if (!item) return
  formState.title = item.title
  formState.topicLabel = item.topicLabel
  formState.schedule = item.schedule
  formState.summary = item.summary
  formState.tagsText = item.tags.join(' / ')
})
</script>
