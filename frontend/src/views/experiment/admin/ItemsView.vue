<template>
  <div class="app-panel-grid">
    <section v-if="currentExperiment" class="app-surface-card app-section-card">
      <SectionHeader eyebrow="实验项管理" :title="`${currentExperiment.title} · 实验项`" description="实验项页聚焦步骤、交付物和执行提示，不再把基础信息堆在同一页。">
        <template #actions>
          <a-button @click="router.push(`/admin/experiments/edit/${currentExperiment.id}`)">返回编辑</a-button>
        </template>
      </SectionHeader>
    </section>

    <section v-if="studentExperiment" class="app-surface-card app-section-card app-panel-grid">
      <ExperimentStepList :steps="studentExperiment.steps" />
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import ExperimentStepList from '@/components/experiment/ExperimentStepList.vue'
import { useExperimentAdminStore } from '@/stores/experiment/admin'
import { useExperimentStudentStore } from '@/stores/experiment/student'

const route = useRoute()
const router = useRouter()
const adminStore = useExperimentAdminStore()
const studentStore = useExperimentStudentStore()
const { currentExperiment } = storeToRefs(adminStore)
const { experiments } = storeToRefs(studentStore)
const studentExperiment = computed(() => experiments.value.find((item) => item.id === String(route.params.id)) ?? null)

onMounted(async () => {
  await Promise.all([adminStore.selectExperiment(String(route.params.id)), studentStore.ensureLoaded()])
})
</script>
