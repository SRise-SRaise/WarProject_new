import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { examRepository } from './repository'
import type { Exam, PaperDetail, StudentExamResult } from './types'

export const useExamStudentStore = defineStore('exam-student', () => {
  // 已发布的考试列表
  const publishedExams = ref<Exam[]>([])
  // 当前选中的考试
  const currentExam = ref<Exam | null>(null)
  // 当前考试的试卷详情（含题目）
  const currentPaper = ref<PaperDetail | null>(null)
  // 学生答案
  const answers = ref<Record<number, string | string[]>>({})
  // 提交结果
  const examResult = ref<StudentExamResult | null>(null)
  // 加载状态
  const loading = ref(false)
  const hydrated = ref(false)

  // 可参加的考试数量
  const availableCount = computed(() => publishedExams.value.length)

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
      // 清空之前的答案
      answers.value = {}
      examResult.value = null
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

  // 设置填空题的某个空的答案
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
    // 确保数组足够长
    while (arr.length <= blankIndex) {
      arr.push('')
    }
    arr[blankIndex] = value
    answers.value[questionId] = arr.join(',')
  }

  async function submitExam(): Promise<StudentExamResult | null> {
    if (!currentExam.value) return null
    loading.value = true
    try {
      const result = await examRepository.submitStudentAnswers(currentExam.value.id, answers.value)
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
  }

  return {
    publishedExams,
    currentExam,
    currentPaper,
    answers,
    examResult,
    loading,
    hydrated,
    availableCount,
    ensureLoaded,
    refresh,
    loadExamDetail,
    setAnswer,
    getAnswer,
    setFillBlankAnswer,
    submitExam,
    resetExam,
  }
})
