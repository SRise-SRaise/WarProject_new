import { ref, reactive } from 'vue'
import { defineStore } from 'pinia'
import { examRepository } from './repository'
import type {
  AdminExamItem,
  ExamRecordItem,
  PageResult,
  PaperItem,
  QuestionAddRequest,
  QuestionBankItem,
  QuestionItem,
  QuestionQueryRequest,
  QuestionTypeItem,
  QuestionUpdateRequest,
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

  // 题目相关状态
  const questions = ref<QuestionItem[]>([])
  const questionPagination = reactive({
    current: 1,
    pageSize: 10,
    total: 0
  })
  const questionStats = reactive({
    total: 0,
    byType: {} as Record<number, number>,
    byDifficulty: {} as Record<number, number>
  })
  const questionLoading = ref(false)

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

  // 题目管理方法
  async function loadQuestions(query: Partial<QuestionQueryRequest> = {}): Promise<void> {
    questionLoading.value = true
    try {
      const request: QuestionQueryRequest = {
        current: query.current ?? questionPagination.current,
        pageSize: query.pageSize ?? questionPagination.pageSize,
        questionContent: query.questionContent,
        questionType: query.questionType,
        difficulty: query.difficulty
      }
      const result = await examRepository.listQuestions(request)
      questions.value = result.records
      questionPagination.current = result.current
      questionPagination.total = result.total
      
      // 同时更新统计
      const stats = await examRepository.getQuestionStats()
      questionStats.total = stats.total
      questionStats.byType = stats.byType
      questionStats.byDifficulty = stats.byDifficulty
    } finally {
      questionLoading.value = false
    }
  }

  async function addQuestion(request: QuestionAddRequest): Promise<boolean> {
    const result = await examRepository.addQuestion(request)
    if (result) {
      await loadQuestions()
    }
    return result
  }

  async function updateQuestion(request: QuestionUpdateRequest): Promise<boolean> {
    const result = await examRepository.updateQuestion(request)
    if (result) {
      await loadQuestions()
    }
    return result
  }

  async function deleteQuestion(id: number): Promise<boolean> {
    const result = await examRepository.deleteQuestion(id)
    if (result) {
      await loadQuestions()
    }
    return result
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
    // 题目相关
    questions,
    questionPagination,
    questionStats,
    questionLoading,
    loadQuestions,
    addQuestion,
    updateQuestion,
    deleteQuestion,
  }
})
