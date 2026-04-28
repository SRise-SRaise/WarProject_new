import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { experimentRepository } from './repository'
import { userContextFactory } from './UserContextFactory'
import type { ExperimentStudentItem, ExperimentQuestion, AnswerSaveRequest, ExperimentReport } from './types'

export const useExperimentStudentStore = defineStore('experiment-student', () => {
  const experiments = ref<ExperimentStudentItem[]>([])
  const currentExperiment = ref<ExperimentStudentItem | null>(null)
  const currentQuestions = ref<ExperimentQuestion[]>([])
  const currentReport = ref<ExperimentReport | null>(null)
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

  async function loadExperimentQuestions(experimentId: string): Promise<ExperimentQuestion[]> {
    loading.value = true
    try {
      currentQuestions.value = await experimentRepository.getExperimentQuestions(experimentId)
      return currentQuestions.value
    } finally {
      loading.value = false
    }
  }

  async function saveAnswers(request: AnswerSaveRequest): Promise<boolean> {
    return await experimentRepository.saveAnswers(request)
  }

  async function submitAnswers(request: AnswerSaveRequest): Promise<boolean> {
    const user = userContextFactory.getCurrent()
    console.log('[StudentStore] submitAnswers - user:', user)

    const success = await experimentRepository.submitReport({
      experimentId: request.experimentId,
      studentId: String(user.id || 1),
      answers: request.answers.map(a => ({
        itemId: a.questionId,
        answer: a.answer,
        filledBlanks: a.filledBlanks
      }))
    })

    console.log('[StudentStore] submitReport 结果:', success)
    return success
  }

  async function getSavedAnswers(experimentId: string) {
    return await experimentRepository.getSavedAnswers(experimentId)
  }

  async function saveWork(note: string, reportName: string): Promise<ExperimentStudentItem | null> {
    if (!currentExperiment.value) return null
    const updated = await experimentRepository.saveStudentWork(currentExperiment.value.id, note, reportName)
    await refresh()
    currentExperiment.value = updated
    return updated
  }

  async function loadStudentReport(experimentId: string): Promise<ExperimentReport | null> {
    loading.value = true
    try {
      const studentId = userContextFactory.getUserIdStr()
      currentReport.value = await experimentRepository.getStudentReport(experimentId, studentId)
      return currentReport.value
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取当前用户信息（用于日志记录）
   */
  function getCurrentUserInfo(): { account: string; studentName: string; clazzNo: string } | null {
    const user = userContextFactory.getCurrent()
    if (!user) return null
    return {
      account: user.account || String(user.id || ''),
      studentName: user.name || user.account || '',
      clazzNo: user.classNo || ''
    }
  }

  return {
    experiments,
    currentExperiment,
    currentQuestions,
    currentReport,
    loading,
    hydrated,
    visibleExperiments,
    ensureLoaded,
    refresh,
    selectExperiment,
    loadExperimentQuestions,
    saveAnswers,
    submitAnswers,
    getSavedAnswers,
    saveWork,
    loadStudentReport,
    getCurrentUserInfo
  }
})
