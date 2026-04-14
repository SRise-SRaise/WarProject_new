<template>
  <div class="app-page-shell app-panel-grid">
    <section v-if="currentExperiment" class="app-surface-card app-section-card app-panel-grid">
      <SectionHeader eyebrow="实验详情" :title="currentExperiment.title" :description="currentExperiment.summary">
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="router.push('/experiments')">返回实验列表</a-button>
            <a-button type="primary" @click="openPrimaryAction">{{ primaryActionLabel }}</a-button>
          </a-space>
        </template>
      </SectionHeader>
      <ExperimentMetaGrid
        :topicLabel="currentExperiment.topicLabel"
        :teacher="currentExperiment.teacher"
        :schedule="currentExperiment.schedule"
        :objective="currentExperiment.objective"
      />
    </section>

    <section v-if="currentExperiment" class="app-split-grid">
      <div class="app-panel-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="实验步骤" title="处理链路" description="从准备、执行到结果整理，按同一条实验链路逐步推进。" tight />
          <ExperimentStepList :steps="currentExperiment.steps" />
        </section>
      </div>

      <div class="app-panel-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="实验材料" title="参考附件" description="先确认材料和样例，再进入实验处理页。" tight />
          <div class="app-list">
            <article v-for="item in currentExperiment.materials" :key="item.name" class="app-list-card">
              <h3 class="app-list-card__title">{{ item.name }}</h3>
              <p class="app-list-card__meta">{{ item.kind }} · {{ item.size }}</p>
            </article>
          </div>
        </section>

        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="当前进度" title="实验状态" description="进度和结果说明会随实验处理和教师反馈同步更新。" tight />
          <article class="app-list-card">
            <p class="app-list-card__meta">{{ currentExperiment.work.note }}</p>
            <div class="detail-footer">
              <span class="app-inline-stat">{{ currentExperiment.work.updatedAt }}</span>
              <StatusTag :type="statusTone" :label="statusLabel" />
            </div>
          </article>
        </section>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import ExperimentMetaGrid from '@/components/experiment/ExperimentMetaGrid.vue'
import ExperimentStepList from '@/components/experiment/ExperimentStepList.vue'
import { useExperimentStudentStore } from '@/stores/experiment/student'

const route = useRoute()
const router = useRouter()
const experimentStore = useExperimentStudentStore()
const { currentExperiment } = storeToRefs(experimentStore)

const primaryActionLabel = computed(() => (currentExperiment.value?.status === 'completed' || currentExperiment.value?.status === 'reviewed' ? '查看结果' : '进入实验'))
const statusTone = computed<'warning' | 'processing' | 'success'>(() => {
  const status = currentExperiment.value?.work.status
  if (status === 'reviewed') return 'success'
  if (status === 'submitted') return 'processing'
  return 'warning'
})
const statusLabel = computed(() => {
  const status = currentExperiment.value?.work.status
  if (status === 'reviewed') return '已处理'
  if (status === 'submitted') return '已提交'
  return '待开始'
})

function openPrimaryAction(): void {
  if (!currentExperiment.value) return
  if (currentExperiment.value.status === 'completed' || currentExperiment.value.status === 'reviewed') {
    router.push(`/experiments/${currentExperiment.value.id}/result`)
    return
  }
  router.push(`/experiments/${currentExperiment.value.id}/work`)
}

onMounted(async () => {
  await experimentStore.selectExperiment(String(route.params.id))
})
</script>

<style scoped>
.detail-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 16px;
}
</style>
