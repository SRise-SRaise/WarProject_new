import { CommonUtil } from '@/utils'
import {
  adminExamFixtures,
  examRecordFixtures,
  examStudentFixtures,
  paperFixtures,
  questionBankFixtures,
  questionTypeFixtures,
  scoreSummaryFixtures,
} from './fixtures'
import type {
  AdminExamItem,
  ExamQuestion,
  ExamRecordItem,
  ExamStudentItem,
  PaperItem,
  QuestionBankItem,
  QuestionTypeItem,
  ScoreSummaryItem,
} from './types'

let studentExams = CommonUtil.deepClone(examStudentFixtures)
let adminExams = CommonUtil.deepClone(adminExamFixtures)
let examRecords = CommonUtil.deepClone(examRecordFixtures)
let scoreSummary = CommonUtil.deepClone(scoreSummaryFixtures)
const questionBanks = CommonUtil.deepClone(questionBankFixtures)
const questionTypes = CommonUtil.deepClone(questionTypeFixtures)
const papers = CommonUtil.deepClone(paperFixtures)

function scoreQuestion(question: ExamQuestion, answer: string | string[] | undefined): number {
  if (question.kind === 'single') {
    return answer === question.correctAnswer ? question.score : 0
  }

  if (question.kind === 'multiple') {
    if (!Array.isArray(answer) || !Array.isArray(question.correctAnswer)) {
      return 0
    }
    const actual = [...answer].sort().join('|')
    const expected = [...question.correctAnswer].sort().join('|')
    return actual === expected ? question.score : 0
  }

  const text = typeof answer === 'string' ? answer.trim() : ''
  if (text.length >= 36) return question.score
  if (text.length >= 18) return Math.round(question.score * 0.75)
  if (text.length > 0) return Math.round(question.score * 0.4)
  return 0
}

function refreshSummaryForExam(examId: string): void {
  const related = examRecords.filter((item) => item.examId === examId && item.score)
  const adminExam = adminExams.find((item) => item.id === examId)
  if (!adminExam) return

  adminExam.submittedCount = examRecords.filter((item) => item.examId === examId && item.status !== 'not_started').length
  if (related.length > 0) {
    const avg = related.reduce((sum, item) => sum + Number(item.score ?? 0), 0) / related.length
    adminExam.averageScore = avg.toFixed(1)
  }

  const warningCount = examRecords.filter((item) => item.examId === examId && item.risk !== 'low').length
  const highScoreCount = examRecords.filter((item) => item.examId === examId && Number(item.score ?? 0) >= 90).length
  const relatedCount = Math.max(related.length, 1)

  scoreSummary = [
    { label: '平均分', value: adminExam.averageScore, detail: '进行中或已完成考试的实时平均分。', tone: 'primary' },
    { label: '优秀率', value: `${Math.round((highScoreCount / relatedCount) * 100)}%`, detail: '90 分及以上学生占比。', tone: 'success' },
    { label: '预警人数', value: String(warningCount), detail: '未开始、长时间停留或风险较高的记录数。', tone: 'warning' },
    { label: '共享题库复用率', value: '78%', detail: '作业与考试共用题目的近期复用情况。', tone: 'accent' },
  ]
}

export const examRepository = {
  async listStudentExams(): Promise<ExamStudentItem[]> {
    await CommonUtil.sleep(90)
    return CommonUtil.deepClone(studentExams)
  },
  async getStudentExamById(id: string): Promise<ExamStudentItem | null> {
    await CommonUtil.sleep(70)
    const matched = studentExams.find((item) => item.id === id)
    return matched ? CommonUtil.deepClone(matched) : null
  },
  async submitStudentExam(id: string, answers: Record<string, string | string[]>): Promise<ExamStudentItem | null> {
    await CommonUtil.sleep(140)
    const matched = studentExams.find((item) => item.id === id)
    if (!matched) return null

    matched.answerDraft = CommonUtil.deepClone(answers)
    const total = matched.questions.reduce((sum, question) => sum + scoreQuestion(question, answers[question.id]), 0)
    matched.status = 'completed'
    matched.result = {
      score: `${total} / 100`,
      rankHint: total >= 90 ? '班级前 15%' : total >= 80 ? '班级前 35%' : '建议继续复盘错题',
      submittedAt: '刚刚',
      durationUsed: '37 分钟',
      feedback: total >= 90 ? '整体表现稳定，答题结构清晰。' : '基础知识掌握较好，建议继续强化共享题库与模块边界相关题目。',
      highlights: ['已完成本次提交', '成绩已同步到教师记录与成绩汇总页', '题库资产仍统一由 exam 模块维护'],
    }

    const record = examRecords.find((item) => item.examId === id && item.studentName === '李明')
    if (record) {
      record.status = 'submitted'
      record.submittedAt = '刚刚'
      record.score = String(total)
      record.risk = total >= 85 ? 'low' : 'medium'
      record.note = '学生已完成提交，可进入成绩汇总复盘。'
    }

    refreshSummaryForExam(id)
    return CommonUtil.deepClone(matched)
  },
  async listQuestionBanks(): Promise<QuestionBankItem[]> {
    await CommonUtil.sleep(60)
    return CommonUtil.deepClone(questionBanks)
  },
  async listQuestionTypes(): Promise<QuestionTypeItem[]> {
    await CommonUtil.sleep(60)
    return CommonUtil.deepClone(questionTypes)
  },
  async listPapers(): Promise<PaperItem[]> {
    await CommonUtil.sleep(60)
    return CommonUtil.deepClone(papers)
  },
  async listAdminExams(): Promise<AdminExamItem[]> {
    await CommonUtil.sleep(80)
    return CommonUtil.deepClone(adminExams)
  },
  async getAdminExamById(id: string): Promise<AdminExamItem | null> {
    await CommonUtil.sleep(60)
    const matched = adminExams.find((item) => item.id === id)
    return matched ? CommonUtil.deepClone(matched) : null
  },
  async listExamRecords(examId: string): Promise<ExamRecordItem[]> {
    await CommonUtil.sleep(70)
    return CommonUtil.deepClone(examRecords.filter((item) => item.examId === examId))
  },
  async listScoreSummary(): Promise<ScoreSummaryItem[]> {
    await CommonUtil.sleep(50)
    return CommonUtil.deepClone(scoreSummary)
  },
}
