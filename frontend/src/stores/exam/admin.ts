import { ref, reactive } from 'vue'
import { defineStore } from 'pinia'
import { examRepository } from './repository'
import type {
  AdminExamItem,
  Exam,
  ExamAddRequest,
  ExamQueryRequest,
  ExamRecordItem,
  ExamUpdateRequest,
  GradeAnswerRequest,
  PageResult,
  Paper,
  PaperAddRequest,
  PaperDetail,
  PaperItem,
  PaperQuestionAddRequest,
  PaperQueryRequest,
  PaperUpdateRequest,
  QuestionAddRequest,
  QuestionBankItem,
  QuestionItem,
  QuestionQueryRequest,
  QuestionTypeItem,
  QuestionUpdateRequest,
  ScoreSummaryItem,
  StudentAnswerRecord,
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

  // 试卷相关状态
  const paperList = ref<Paper[]>([])
  const paperPagination = reactive({
    current: 1,
    pageSize: 10,
    total: 0
  })
  const currentPaper = ref<PaperDetail | null>(null)
  const allQuestions = ref<QuestionItem[]>([])
  const paperLoading = ref(false)

  // 考试相关状态
  const examList = ref<Exam[]>([])
  const examPagination = reactive({
    current: 1,
    pageSize: 10,
    total: 0
  })
  const examStats = reactive({
    total: 0,
    published: 0,
    draft: 0,
    ongoing: 0
  })
  const allPapers = ref<Paper[]>([])
  const examLoading = ref(false)

  // 批改相关状态
  const gradingExams = ref<Array<Exam & { submittedCount: number; gradedCount: number; pendingCount: number }>>([])
  const studentRecords = ref<StudentAnswerRecord[]>([])
  const currentRecord = ref<{
    record: StudentAnswerRecord
    exam: Exam
    paper: PaperDetail
  } | null>(null)
  const scoreStats = reactive({
    totalStudents: 0,
    submittedCount: 0,
    gradedCount: 0,
    pendingCount: 0,
    averageScore: 0,
    highestScore: 0,
    lowestScore: 0,
    passRate: 0
  })
  const gradingLoading = ref(false)

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

  // 试卷管理方法
  async function loadPapers(query: Partial<PaperQueryRequest> = {}): Promise<void> {
    paperLoading.value = true
    try {
      const request: PaperQueryRequest = {
        current: query.current ?? paperPagination.current,
        pageSize: query.pageSize ?? paperPagination.pageSize,
        paperName: query.paperName
      }
      const result = await examRepository.listPapersNew(request)
      paperList.value = result.records
      paperPagination.current = result.current
      paperPagination.total = result.total
    } finally {
      paperLoading.value = false
    }
  }

  async function loadPaperDetail(id: number): Promise<PaperDetail | null> {
    paperLoading.value = true
    try {
      currentPaper.value = await examRepository.getPaperById(id)
      return currentPaper.value
    } finally {
      paperLoading.value = false
    }
  }

  async function loadAllQuestions(): Promise<void> {
    allQuestions.value = await examRepository.getAllQuestions()
  }

  async function addPaper(request: PaperAddRequest): Promise<number> {
    const id = await examRepository.addPaper(request)
    if (id) {
      await loadPapers()
    }
    return id
  }

  async function updatePaper(request: PaperUpdateRequest): Promise<boolean> {
    const result = await examRepository.updatePaper(request)
    if (result) {
      await loadPapers()
      if (currentPaper.value?.id === request.id) {
        await loadPaperDetail(request.id)
      }
    }
    return result
  }

  async function deletePaper(id: number): Promise<boolean> {
    const result = await examRepository.deletePaper(id)
    if (result) {
      await loadPapers()
      if (currentPaper.value?.id === id) {
        currentPaper.value = null
      }
    }
    return result
  }

  async function addQuestionToPaper(request: PaperQuestionAddRequest): Promise<boolean> {
    const result = await examRepository.addPaperQuestion(request)
    if (result && currentPaper.value?.id === request.paperId) {
      await loadPaperDetail(request.paperId)
    }
    return result
  }

  async function updatePaperQuestionScore(id: number, score: number, sectionName?: string): Promise<boolean> {
    const result = await examRepository.updatePaperQuestion(id, score, sectionName)
    if (result && currentPaper.value) {
      await loadPaperDetail(currentPaper.value.id)
    }
    return result
  }

  async function removeQuestionFromPaper(id: number): Promise<boolean> {
    const result = await examRepository.removePaperQuestion(id)
    if (result && currentPaper.value) {
      await loadPaperDetail(currentPaper.value.id)
    }
    return result
  }

  async function reorderPaperQuestions(paperId: number, questionIds: number[]): Promise<boolean> {
    const result = await examRepository.reorderPaperQuestions(paperId, questionIds)
    if (result && currentPaper.value?.id === paperId) {
      await loadPaperDetail(paperId)
    }
    return result
  }

  // 考试管理方法
  async function loadExams(query: Partial<ExamQueryRequest> = {}): Promise<void> {
    examLoading.value = true
    try {
      const request: ExamQueryRequest = {
        current: query.current ?? examPagination.current,
        pageSize: query.pageSize ?? examPagination.pageSize,
        examName: query.examName,
        isPublished: query.isPublished
      }
      const result = await examRepository.listExams(request)
      examList.value = result.records
      examPagination.current = result.current
      examPagination.total = result.total
      
      // 同时更新统计
      const stats = await examRepository.getExamStats()
      examStats.total = stats.total
      examStats.published = stats.published
      examStats.draft = stats.draft
      examStats.ongoing = stats.ongoing
    } finally {
      examLoading.value = false
    }
  }

  async function loadAllPapers(): Promise<void> {
    allPapers.value = await examRepository.getAllPapers()
  }

  async function addExam(request: ExamAddRequest): Promise<number> {
    const id = await examRepository.addExam(request)
    if (id) {
      await loadExams()
    }
    return id
  }

  async function updateExam(request: ExamUpdateRequest): Promise<boolean> {
    const result = await examRepository.updateExam(request)
    if (result) {
      await loadExams()
    }
    return result
  }

  async function deleteExam(id: number): Promise<boolean> {
    const result = await examRepository.deleteExam(id)
    if (result) {
      await loadExams()
    }
    return result
  }

  async function publishExam(id: number): Promise<boolean> {
    const result = await examRepository.publishExam(id)
    if (result) {
      await loadExams()
    }
    return result
  }

  async function unpublishExam(id: number): Promise<boolean> {
    const result = await examRepository.unpublishExam(id)
    if (result) {
      await loadExams()
    }
    return result
  }

  // 批改管理方法
  async function loadGradingExams(): Promise<void> {
    gradingLoading.value = true
    try {
      gradingExams.value = await examRepository.getPublishedExamsForGrading()
    } finally {
      gradingLoading.value = false
    }
  }

  async function loadStudentRecords(examId: number): Promise<void> {
    gradingLoading.value = true
    try {
      studentRecords.value = await examRepository.getStudentAnswerRecords(examId)
      const stats = await examRepository.getScoreStatistics(examId)
      Object.assign(scoreStats, stats)
    } finally {
      gradingLoading.value = false
    }
  }

  async function loadRecordDetail(recordId: number): Promise<void> {
    gradingLoading.value = true
    try {
      currentRecord.value = await examRepository.getStudentAnswerRecordById(recordId)
    } finally {
      gradingLoading.value = false
    }
  }

  async function gradeAnswer(request: GradeAnswerRequest): Promise<boolean> {
    const result = await examRepository.gradeAnswer(request)
    if (result && currentRecord.value) {
      // 重新加载当前记录
      await loadRecordDetail(request.recordId)
      // 更新统计
      if (currentRecord.value) {
        const stats = await examRepository.getScoreStatistics(currentRecord.value.exam.id)
        Object.assign(scoreStats, stats)
      }
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
    // 试卷相关
    paperList,
    paperPagination,
    currentPaper,
    allQuestions,
    paperLoading,
    loadPapers,
    loadPaperDetail,
    loadAllQuestions,
    addPaper,
    updatePaper,
    deletePaper,
    addQuestionToPaper,
    updatePaperQuestionScore,
    removeQuestionFromPaper,
    reorderPaperQuestions,
    // 考试相关
    examList,
    examPagination,
    examStats,
    allPapers,
    examLoading,
    loadExams,
    loadAllPapers,
    addExam,
    updateExam,
    deleteExam,
    publishExam,
    unpublishExam,
    // 批改相关
    gradingExams,
    studentRecords,
    currentRecord,
    scoreStats,
    gradingLoading,
    loadGradingExams,
    loadStudentRecords,
    loadRecordDetail,
    gradeAnswer,
  }
})
