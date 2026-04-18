import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { examRepository } from './repository'
import type { Exam, PaperDetail, QuestionType, StudentExamResult } from './types'

const EXAM_SUBMISSION_STORAGE_KEY = 'eduhub.exam.student.submissions'
const AUTH_SESSION_STORAGE_KEY = 'eduhub.auth.session'

type ReleaseMode = 'immediate' | 'pending_teacher'

interface SubmissionQuestionSnapshot {
  id: number
  questionId: number
  questionType?: QuestionType
  questionContent: string
  score: number
}

interface StoredExamSubmission {
  examId: number
  examName: string
  submittedAt: string
  totalScore: number
  objectiveScore: number
  finalScore: number | null
  releaseMode: ReleaseMode
  answers: Record<number, string | string[]>
  questionScores: Record<number, { earned: number; max: number }>
  paper: {
    id: number
    paperName: string
    questionCount: number
    totalScore: number
    questions: SubmissionQuestionSnapshot[]
  }
}

type SubmissionLedger = Record<string, StoredExamSubmission>

function isBrowser(): boolean {
  return typeof window !== 'undefined'
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null
}

function readLedger(): SubmissionLedger {
  if (!isBrowser()) return {}
  try {
    const raw = window.localStorage.getItem(EXAM_SUBMISSION_STORAGE_KEY)
    if (!raw) return {}
    const parsed: unknown = JSON.parse(raw)
    if (!isRecord(parsed)) return {}
    return parsed as SubmissionLedger
  } catch {
    return {}
  }
}

function writeLedger(ledger: SubmissionLedger): void {
  if (!isBrowser()) return
  window.localStorage.setItem(EXAM_SUBMISSION_STORAGE_KEY, JSON.stringify(ledger))
}

function getCurrentStudentScope(): string | null {
  if (!isBrowser()) return null
  try {
    const raw = window.localStorage.getItem(AUTH_SESSION_STORAGE_KEY)
    if (!raw) return null
    const parsed: unknown = JSON.parse(raw)
    if (!isRecord(parsed)) return null
    const role = typeof parsed.role === 'string' ? parsed.role : ''
    if (role !== 'student') return null
    const account = typeof parsed.account === 'string' ? parsed.account : ''
    const id = typeof parsed.id === 'string' ? parsed.id : ''
    if (!account && !id) return null
    return `${id}::${account}`
  } catch {
    return null
  }
}

function buildLedgerKey(scope: string, examId: number): string {
  return `${scope}::${examId}`
}

function nowLabel(): string {
  return new Date().toLocaleString('zh-CN', { hour12: false })
}

function cloneAnswers(value: Record<number, string | string[]>): Record<number, string | string[]> {
  return Object.fromEntries(Object.entries(value).map(([key, answer]) => [Number(key), Array.isArray(answer) ? [...answer] : answer]))
}

function resolveExamEnd(exam: Exam): Date | null {
  if (exam.endTime) return new Date(exam.endTime)
  if (exam.startTime && exam.durationMin) {
    return new Date(new Date(exam.startTime).getTime() + exam.durationMin * 60 * 1000)
  }
  return null
}

export const useExamStudentStore = defineStore('exam-student', () => {
  const publishedExams = ref<Exam[]>([])
  const currentExam = ref<Exam | null>(null)
  const currentPaper = ref<PaperDetail | null>(null)
  const answers = ref<Record<number, string | string[]>>({})
  const examResult = ref<StudentExamResult | null>(null)
  const currentSubmission = ref<StoredExamSubmission | null>(null)
  const loading = ref(false)
  const hydrated = ref(false)
  const submissionLedger = ref<SubmissionLedger>(readLedger())

  const availableCount = computed(() => publishedExams.value.filter((exam) => canEnterExam(exam)).length)

  function getSubmission(examId: number): StoredExamSubmission | null {
    const scope = getCurrentStudentScope()
    if (!scope) return null
    return submissionLedger.value[buildLedgerKey(scope, examId)] ?? null
  }

  function hasSubmittedExam(examId: number): boolean {
    return getSubmission(examId) !== null
  }

  function getSubmissionState(examId: number): 'none' | 'immediate' | 'pending_teacher' {
    const submission = getSubmission(examId)
    if (!submission) return 'none'
    return submission.releaseMode
  }

  function canEnterExam(exam: Exam): boolean {
    if (hasSubmittedExam(exam.id)) return false
    if (!exam.isPublished) return false
    if (exam.startTime && new Date() < new Date(exam.startTime)) return false
    const end = resolveExamEnd(exam)
    if (end && new Date() > end) return false
    return true
  }

  function loadSubmissionResult(examId: number): StoredExamSubmission | null {
    currentSubmission.value = getSubmission(examId)
    return currentSubmission.value
  }

  async function ensureLoaded(): Promise<void> {
    if (hydrated.value) return
    await refresh()
  }

  async function refresh(): Promise<void> {
    loading.value = true
    try {
      publishedExams.value = await examRepository.getPublishedExamsForStudent()
      hydrated.value = true
    } finally {
      loading.value = false
    }
  }

  function getExamFromList(examId: number): Exam | null {
    return publishedExams.value.find((exam) => exam.id === examId) ?? null
  }

  async function loadExamDetail(examId: number): Promise<boolean> {
    loading.value = true
    try {
      const detail = await examRepository.getExamDetailForStudent(examId)
      if (!detail) {
        currentExam.value = null
        currentPaper.value = null
        return false
      }
      currentExam.value = detail.exam
      currentPaper.value = detail.paper
      answers.value = {}
      examResult.value = null
      currentSubmission.value = null
      return true
    } finally {
      loading.value = false
    }
  }

  function setAnswer(questionId: number, answer: string | string[]): void {
    answers.value[questionId] = answer
  }

  function getAnswer(questionId: number): string | string[] | undefined {
    return answers.value[questionId]
  }

  function setFillBlankAnswer(questionId: number, blankIndex: number, value: string): void {
    const current = answers.value[questionId]
    let arr: string[]
    if (typeof current === 'string') {
      arr = current ? current.split(',') : []
    } else if (Array.isArray(current)) {
      arr = [...current]
    } else {
      arr = []
    }
    while (arr.length <= blankIndex) {
      arr.push('')
    }
    arr[blankIndex] = value
    answers.value[questionId] = arr.join(',')
  }

  function persistSubmission(submission: StoredExamSubmission): void {
    const scope = getCurrentStudentScope()
    if (!scope) return
    submissionLedger.value = {
      ...submissionLedger.value,
      [buildLedgerKey(scope, submission.examId)]: submission,
    }
    writeLedger(submissionLedger.value)
  }

  async function submitExam(): Promise<StudentExamResult | null> {
    if (!currentExam.value || !currentPaper.value) return null
    loading.value = true
    try {
      const result = await examRepository.submitStudentAnswers(currentExam.value.id, answers.value)
      const hasManualQuestions = currentPaper.value.questions.some((pq) => {
        const type = pq.question?.questionType
        return type === 5 || type === 6 || type === 7
      })
      const submission: StoredExamSubmission = {
        examId: currentExam.value.id,
        examName: currentExam.value.examName,
        submittedAt: nowLabel(),
        totalScore: result.totalScore,
        objectiveScore: result.earnedScore,
        finalScore: hasManualQuestions ? null : result.earnedScore,
        releaseMode: hasManualQuestions ? 'pending_teacher' : 'immediate',
        answers: cloneAnswers(answers.value),
        questionScores: result.questionScores,
        paper: {
          id: currentPaper.value.id,
          paperName: currentPaper.value.paperName,
          questionCount: currentPaper.value.questionCount,
          totalScore: currentPaper.value.totalScore,
          questions: currentPaper.value.questions.map((pq) => ({
            id: pq.id,
            questionId: pq.questionId,
            questionType: pq.question?.questionType,
            questionContent: pq.question?.questionContent || '',
            score: pq.score,
          })),
        },
      }
      persistSubmission(submission)
      currentSubmission.value = submission
      examResult.value = result
      return result
    } finally {
      loading.value = false
    }
  }

  function resetExam(): void {
    currentExam.value = null
    currentPaper.value = null
    answers.value = {}
    examResult.value = null
    currentSubmission.value = null
  }

  return {
    publishedExams,
    currentExam,
    currentPaper,
    answers,
    examResult,
    currentSubmission,
    loading,
    hydrated,
    availableCount,
    ensureLoaded,
    refresh,
    getExamFromList,
    getSubmission,
    hasSubmittedExam,
    getSubmissionState,
    canEnterExam,
    loadSubmissionResult,
    loadExamDetail,
    setAnswer,
    getAnswer,
    setFillBlankAnswer,
    submitExam,
    resetExam,
  }
})
