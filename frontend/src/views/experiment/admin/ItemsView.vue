<template>
  <div class="app-panel-grid">
    <section v-if="currentExperiment" class="app-surface-card app-section-card">
      <SectionHeader eyebrow="实验项管理" :title="`${currentExperiment.title} · 实验项`" description="编辑实验步骤题目，类似 Word 页面风格，可自由添加和管理题目。">
        <template #actions>
          <a-space :size="12">
            <a-button @click="router.push(`/admin/experiments/edit/${currentExperiment.id}`)">返回编辑</a-button>
            <a-button type="primary" @click="router.push(`/admin/experiments/steps/${currentExperiment.id}`)">
              <template #icon><EditOutlined /></template>
              编辑步骤题目
            </a-button>
          </a-space>
        </template>
      </SectionHeader>
    </section>

    <section v-if="studentExperiment" class="app-surface-card app-section-card app-panel-grid">
      <SectionHeader eyebrow="实验步骤" title="学生端预览" description="学生答题时将看到以下步骤列表。" tight />
      <ExperimentStepList :steps="studentExperiment.steps" />
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { EditOutlined } from '@ant-design/icons-vue'
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
