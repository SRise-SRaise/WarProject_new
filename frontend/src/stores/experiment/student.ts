import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { experimentRepository } from './repository'
import type { ExperimentStudentItem } from './types'

export const useExperimentStudentStore = defineStore('experiment-student', () => {
  const experiments = ref<ExperimentStudentItem[]>([])
  const currentExperiment = ref<ExperimentStudentItem | null>(null)
  const loading = ref(false)
  const hydrated = ref(false)

  const visibleExperiments = computed(() => experiments.value)

  async function ensureLoaded(): Promise<void> {
    if (hydrated.value) return
    await refresh()
  }

  async function refresh(): Promise<void> {
    loading.value = true
    try {
      experiments.value = await experimentRepository.listStudentExperiments()
      hydrated.value = true
    } finally {
      loading.value = false
    }
  }

  async function selectExperiment(id: string): Promise<ExperimentStudentItem | null> {
    await ensureLoaded()
    currentExperiment.value = await experimentRepository.getStudentExperimentById(id)
    return currentExperiment.value
  }

  async function saveWork(note: string, reportName: string): Promise<ExperimentStudentItem | null> {
    if (!currentExperiment.value) return null
    const updated = await experimentRepository.saveStudentWork(currentExperiment.value.id, note, reportName)
    await refresh()
    currentExperiment.value = updated
    return updated
  }

  return { experiments, currentExperiment, loading, hydrated, visibleExperiments, ensureLoaded, refresh, selectExperiment, saveWork }
})
