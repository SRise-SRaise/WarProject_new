<template>
  <div class="app-page-shell app-panel-grid">
    <section v-if="currentExperiment" class="app-surface-card app-section-card">
      <SectionHeader eyebrow="实验处理" :title="currentExperiment.title" description="记录实验过程摘要和报告名称，当前提交会同步到结果页与教师结果处理页。">
        <template #actions>
          <a-button @click="router.push(`/experiments/${currentExperiment.id}`)">返回详情</a-button>
        </template>
      </SectionHeader>
    </section>

    <section v-if="currentExperiment" class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <a-form layout="vertical">
          <a-form-item label="实验过程摘要"><a-textarea v-model:value="formState.note" :rows="8" placeholder="记录关键步骤、遇到的问题和当前结果" /></a-form-item>
          <a-form-item label="报告文件名"><a-input v-model:value="formState.reportName" size="large" placeholder="例如：experiment-report.pdf" /></a-form-item>
          <a-button type="primary" size="large" :loading="saving" @click="saveWork">提交实验结果</a-button>
        </a-form>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="实验步骤" title="当前要点" description="实验步骤与交付物会在处理页持续可见，减少来回跳转。" tight />
        <ExperimentStepList :steps="currentExperiment.steps" />
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import ExperimentStepList from '@/components/experiment/ExperimentStepList.vue'
import { useExperimentStudentStore } from '@/stores/experiment/student'

const route = useRoute()
const router = useRouter()
const experimentStore = useExperimentStudentStore()
const { currentExperiment } = storeToRefs(experimentStore)
const saving = ref(false)
const formState = reactive({ note: '', reportName: '' })

async function saveWork(): Promise<void> {
  if (!currentExperiment.value) return
  if (formState.note.trim().length === 0) {
    message.error('请先填写实验过程摘要。')
    return
  }
  saving.value = true
  try {
    await experimentStore.saveWork(formState.note, formState.reportName)
    message.success('实验结果已提交。')
    router.push(`/experiments/${currentExperiment.value.id}/result`)
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const experiment = await experimentStore.selectExperiment(String(route.params.id))
  if (!experiment) return
  formState.note = experiment.work.note
  formState.reportName = experiment.work.reportName ?? ''
})
</script>
