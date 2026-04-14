import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { examRepository } from './repository'
import type { ExamStudentItem } from './types'

export const useExamStudentStore = defineStore('exam-student', () => {
  const exams = ref<ExamStudentItem[]>([])
  const currentExam = ref<ExamStudentItem | null>(null)
  const loading = ref(false)
  const hydrated = ref(false)

  const readyExams = computed(() => exams.value.filter((item) => item.status === 'ready' || item.status === 'in_progress'))
  const completedExams = computed(() => exams.value.filter((item) => item.status === 'completed'))

  async function ensureLoaded(): Promise<void> {
    if (hydrated.value) return
    await refresh()
  }

  async function refresh(): Promise<void> {
    loading.value = true
    try {
      exams.value = await examRepository.listStudentExams()
      hydrated.value = true
    } finally {
      loading.value = false
    }
  }

  async function selectExam(id: string): Promise<ExamStudentItem | null> {
    await ensureLoaded()
    currentExam.value = await examRepository.getStudentExamById(id)
    return currentExam.value
  }

  async function submitExam(answerDraft: Record<string, string | string[]>): Promise<ExamStudentItem | null> {
    if (!currentExam.value) return null
    const updated = await examRepository.submitStudentExam(currentExam.value.id, answerDraft)
    await refresh()
    currentExam.value = updated
    return updated
  }

  return {
    exams,
    currentExam,
    loading,
    hydrated,
    readyExams,
    completedExams,
    ensureLoaded,
    refresh,
    selectExam,
    submitExam,
  }
})
