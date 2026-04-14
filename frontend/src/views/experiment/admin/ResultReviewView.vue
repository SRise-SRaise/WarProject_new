<template>
  <div class="app-panel-grid">
    <section v-if="currentResult" class="app-surface-card app-section-card">
      <SectionHeader eyebrow="结果处理" :title="`${currentResult.studentName} · 实验结果处理`" :description="currentResult.summary">
        <template #actions>
          <a-button @click="router.push(`/admin/experiments/results/${experimentId}`)">返回结果列表</a-button>
        </template>
      </SectionHeader>
    </section>

    <section v-if="currentResult" class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <article class="app-list-card">
          <h3 class="app-list-card__title">{{ currentResult.reportName ?? '未填写报告名' }}</h3>
          <p class="app-list-card__meta">{{ currentResult.summary }}</p>
        </article>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <a-form layout="vertical">
          <a-form-item label="得分"><a-input v-model:value="formState.score" size="large" /></a-form-item>
          <a-form-item label="反馈"><a-textarea v-model:value="formState.feedback" :rows="6" /></a-form-item>
          <a-button type="primary" size="large" :loading="saving" @click="reviewResult">保存结果</a-button>
        </a-form>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useExperimentAdminStore } from '@/stores/experiment/admin'

const route = useRoute()
const router = useRouter()
const experimentStore = useExperimentAdminStore()
const { currentResult } = storeToRefs(experimentStore)
const saving = ref(false)
const experimentId = computed(() => String(route.params.id))
const resultId = computed(() => String(route.params.resultId))
const formState = reactive({ score: '', feedback: '' })

async function reviewResult(): Promise<void> {
  saving.value = true
  try {
    await experimentStore.reviewResult(experimentId.value, resultId.value, { ...formState })
    message.success('实验结果已处理。')
    router.push(`/admin/experiments/results/${experimentId.value}`)
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const result = await experimentStore.selectResult(experimentId.value, resultId.value)
  if (!result) return
  formState.score = result.score ?? ''
  formState.feedback = result.feedback ?? ''
})
</script>
