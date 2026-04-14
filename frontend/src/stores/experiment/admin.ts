import { ref } from 'vue'
import { defineStore } from 'pinia'
import { experimentRepository } from './repository'
import type {
  ExperimentAdminItem,
  ExperimentEditPayload,
  ExperimentResultItem,
  ExperimentResultPayload
} from './types'

export const useExperimentAdminStore = defineStore('experiment-admin', () => {
  const experiments = ref<ExperimentAdminItem[]>([])
  const currentExperiment = ref<ExperimentAdminItem | null>(null)
  const results = ref<ExperimentResultItem[]>([])
  const currentResult = ref<ExperimentResultItem | null>(null)
  const loading = ref(false)
  const hydrated = ref(false)

  async function ensureLoaded(): Promise<void> {
    if (hydrated.value) return
    await refresh()
  }

  async function refresh(): Promise<void> {
    loading.value = true
    try {
      experiments.value = await experimentRepository.listAdminExperiments()
      hydrated.value = true
    } finally {
      loading.value = false
    }
  }

  async function selectExperiment(id: string): Promise<ExperimentAdminItem | null> {
    await ensureLoaded()
    currentExperiment.value = await experimentRepository.getAdminExperimentById(id)
    return currentExperiment.value
  }

  async function saveExperiment(payload: ExperimentEditPayload): Promise<ExperimentAdminItem> {
    const saved = await experimentRepository.saveExperiment(payload)
    await refresh()
    currentExperiment.value = saved
    return saved
  }

  async function loadResults(experimentId: string): Promise<ExperimentResultItem[]> {
    results.value = await experimentRepository.listExperimentResults(experimentId)
    return results.value
  }

  async function selectResult(experimentId: string, resultId: string): Promise<ExperimentResultItem | null> {
    currentResult.value = await experimentRepository.getExperimentResult(experimentId, resultId)
    return currentResult.value
  }

  async function reviewResult(experimentId: string, resultId: string, payload: ExperimentResultPayload): Promise<ExperimentResultItem | null> {
    const reviewed = await experimentRepository.reviewExperimentResult(experimentId, resultId, payload)
    await refresh()
    results.value = await experimentRepository.listExperimentResults(experimentId)
    currentResult.value = reviewed
    return reviewed
  }

  return { experiments, currentExperiment, results, currentResult, loading, hydrated, ensureLoaded, refresh, selectExperiment, saveExperiment, loadResults, selectResult, reviewResult }
})
