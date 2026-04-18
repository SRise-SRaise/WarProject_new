import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { experimentRepository } from './repository'
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
    // 使用 submitReport 接口保存答案（已包含学生信息）
    const currentUser = getCurrentUserFromStore()
    console.log('[StudentStore] submitAnswers - currentUser:', currentUser)

    const success = await experimentRepository.submitReport({
      experimentId: request.experimentId,
      studentId: String(currentUser.id || 1),
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
      const currentUser = getCurrentUserFromStore()
      const studentId = String(currentUser.id || 1)
      currentReport.value = await experimentRepository.getStudentReport(experimentId, studentId)
      return currentReport.value
    } finally {
      loading.value = false
    }
  }

  function getCurrentUserFromStore() {
    // 优先从 eduhub.auth.session 获取（与 StudentReportView.vue 保持一致）
    try {
      const sessionStr = localStorage.getItem('eduhub.auth.session')
      if (sessionStr) {
        console.log('[StudentStore] session原始数据:', sessionStr.substring(0, 200))
        const session = JSON.parse(sessionStr)
        console.log('[StudentStore] session解析后:', { id: session?.id, userId: session?.userId, account: session?.account, name: session?.name })
        if (session && (session.id || session.userId)) {
          return {
            id: session.id || session.userId,
            studentCode: session.account || '',
            studentName: session.name || '',
            classCode: session.classCode || session.className || ''
          }
        }
      }
    } catch (e) {
      console.error('获取 eduhub.auth.session 失败:', e)
    }

    // 备用：尝试从 user 获取
    try {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        const user = JSON.parse(userStr)
        console.log('[StudentStore] user数据:', user)
        if (user && user.id) {
          return {
            id: user.id,
            studentCode: user.studentCode || user.account || '',
            studentName: user.studentName || user.name || '',
            classCode: user.classCode || ''
          }
        }
      }
    } catch (e) {
      console.error('获取 user 失败:', e)
    }

    // 默认值
    console.log('[StudentStore] 无法获取用户信息，使用默认值')
    return { id: 1, studentCode: '2023001234', studentName: '张三', classCode: 'CS2301' }
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
    loadStudentReport
  }
})
