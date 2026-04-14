import { ref } from 'vue'
import { defineStore } from 'pinia'
import { examRepository } from './repository'
import type {
  AdminExamItem,
  ExamRecordItem,
  PaperItem,
  QuestionBankItem,
  QuestionTypeItem,
  ScoreSummaryItem,
} from './types'

export const useExamAdminStore = defineStore('exam-admin', () => {
  const questionBanks = ref<QuestionBankItem[]>([])
  const questionTypes = ref<QuestionTypeItem[]>([])
  const papers = ref<PaperItem[]>([])
  const exams = ref<AdminExamItem[]>([])
  const currentExam = ref<AdminExamItem | null>(null)
  const records = ref<ExamRecordItem[]>([])
  const scoreSummary = ref<ScoreSummaryItem[]>([])
  const loading = ref(false)
  const hydrated = ref(false)

  async function ensureLoaded(): Promise<void> {
    if (hydrated.value) return
    await refresh()
  }

  async function refresh(): Promise<void> {
    loading.value = true
    try {
      const [banks, types, paperItems, examItems, summaryItems] = await Promise.all([
        examRepository.listQuestionBanks(),
        examRepository.listQuestionTypes(),
        examRepository.listPapers(),
        examRepository.listAdminExams(),
        examRepository.listScoreSummary(),
      ])
      questionBanks.value = banks
      questionTypes.value = types
      papers.value = paperItems
      exams.value = examItems
      scoreSummary.value = summaryItems
      hydrated.value = true
    } finally {
      loading.value = false
    }
  }

  async function selectExam(id: string): Promise<AdminExamItem | null> {
    await ensureLoaded()
    currentExam.value = await examRepository.getAdminExamById(id)
    return currentExam.value
  }

  async function loadRecords(examId: string): Promise<ExamRecordItem[]> {
    records.value = await examRepository.listExamRecords(examId)
    return records.value
  }

  return {
    questionBanks,
    questionTypes,
    papers,
    exams,
    currentExam,
    records,
    scoreSummary,
    loading,
    hydrated,
    ensureLoaded,
    refresh,
    selectExam,
    loadRecords,
  }
})
