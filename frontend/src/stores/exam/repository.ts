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
import { QUESTION_TYPE_MAP } from './types'

let studentExams = CommonUtil.deepClone(examStudentFixtures)
let adminExams = CommonUtil.deepClone(adminExamFixtures)
let examRecords = CommonUtil.deepClone(examRecordFixtures)
let scoreSummary = CommonUtil.deepClone(scoreSummaryFixtures)
const questionBanks = CommonUtil.deepClone(questionBankFixtures)
const questionTypes = CommonUtil.deepClone(questionTypeFixtures)
const papers = CommonUtil.deepClone(paperFixtures)

// 模拟题目数据
let questionIdCounter = 100
let questions: QuestionItem[] = [
  {
    id: 1,
    questionContent: '在分层架构中，最适合承载业务规则聚合与流程协调的是哪一层？',
    questionType: 2,
    optionsText: JSON.stringify([
      { key: 'A', label: '表现层' },
      { key: 'B', label: '服务层' },
      { key: 'C', label: '数据源层' },
      { key: 'D', label: '工具层' }
    ]),
    standardAnswer: 'B',
    analysis: '服务层负责业务规则的聚合和流程协调，是应用程序的核心业务逻辑所在。',
    difficulty: 2,
    creatorTeacherId: 1,
    createdAt: '2026-04-10 14:30:00',
    updatedAt: '2026-04-10 14:30:00'
  },
  {
    id: 2,
    questionContent: '以下哪些内容属于题库资产在作业与考试之间共享的直接收益？',
    questionType: 3,
    optionsText: JSON.stringify([
      { key: 'A', label: '题目复用' },
      { key: 'B', label: '评分口径统一' },
      { key: 'C', label: '减少页面数量' },
      { key: 'D', label: '降低出题重复劳动' }
    ]),
    standardAnswer: 'A,B,D',
    analysis: '题库共享可以实现题目复用、统一评分标准以及减少重复出题的工作量。',
    difficulty: 3,
    creatorTeacherId: 1,
    createdAt: '2026-04-10 15:00:00',
    updatedAt: '2026-04-10 15:00:00'
  },
  {
    id: 3,
    questionContent: '请简要说明为什么题库管理入口建议放在 exam 模块，而不是放到 homework 模块。',
    questionType: 5,
    optionsText: null,
    standardAnswer: '题库作为共享资产，主要服务于考试场景，由exam模块统一管理可以保证题目质量和评分口径的一致性。',
    analysis: '可以围绕"共享资产"、"模块所有权"、"考试主场景"三个关键词组织答案。',
    difficulty: 4,
    creatorTeacherId: 1,
    createdAt: '2026-04-11 09:15:00',
    updatedAt: '2026-04-11 09:15:00'
  },
  {
    id: 4,
    questionContent: 'JavaScript 中 null 和 undefined 的区别是什么？',
    questionType: 1,
    optionsText: null,
    standardAnswer: 'null 表示空值，undefined 表示未定义',
    analysis: 'null 是一个表示"无"的对象，undefined 是一个原始值，表示变量已声明但未赋值。',
    difficulty: 2,
    creatorTeacherId: 2,
    createdAt: '2026-04-11 10:30:00',
    updatedAt: '2026-04-11 10:30:00'
  },
  {
    id: 5,
    questionContent: 'React 组件的生命周期方法 componentDidMount 在什么时候调用？',
    questionType: 2,
    optionsText: JSON.stringify([
      { key: 'A', label: '组件渲染之前' },
      { key: 'B', label: '组件挂载到DOM后' },
      { key: 'C', label: '组件更新时' },
      { key: 'D', label: '组件卸载时' }
    ]),
    standardAnswer: 'B',
    analysis: 'componentDidMount 在组件第一次渲染完成并挂载到 DOM 后立即调用。',
    difficulty: 2,
    creatorTeacherId: 2,
    createdAt: '2026-04-12 08:45:00',
    updatedAt: '2026-04-12 08:45:00'
  },
  {
    id: 6,
    questionContent: '编写一个函数，实现数组去重功能。',
    questionType: 6,
    optionsText: null,
    standardAnswer: 'function unique(arr) { return [...new Set(arr)]; }',
    analysis: '可以使用 Set 数据结构、filter + indexOf、或者 reduce 方法实现数组去重。',
    difficulty: 3,
    creatorTeacherId: 2,
    createdAt: '2026-04-12 14:20:00',
    updatedAt: '2026-04-12 14:20:00'
  },
  {
    id: 7,
    questionContent: 'HTTP 状态码 404 表示什么？',
    questionType: 4,
    optionsText: JSON.stringify([
      { key: 'A', label: '请求成功' },
      { key: 'B', label: '资源未找到' }
    ]),
    standardAnswer: 'B',
    analysis: 'HTTP 404 状态码表示服务器无法找到请求的资源。',
    difficulty: 1,
    creatorTeacherId: 1,
    createdAt: '2026-04-13 11:00:00',
    updatedAt: '2026-04-13 11:00:00'
  },
  {
    id: 8,
    questionContent: '请描述 MVC 架构模式中 Model、View、Controller 各自的职责。',
    questionType: 5,
    optionsText: null,
    standardAnswer: 'Model 负责数据和业务逻辑，View 负责展示，Controller 负责接收用户输入并调度 Model 和 View。',
    analysis: 'MVC 是一种经典的软件架构模式，将应用程序分为三个核心组件。',
    difficulty: 3,
    creatorTeacherId: 1,
    createdAt: '2026-04-14 09:30:00',
    updatedAt: '2026-04-14 09:30:00'
  }
]

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

  // 题目 CRUD 方法
  async listQuestions(query: QuestionQueryRequest): Promise<PageResult<QuestionItem>> {
    await CommonUtil.sleep(80)
    let filtered = [...questions]
    
    // 按题目内容搜索
    if (query.questionContent) {
      const keyword = query.questionContent.toLowerCase()
      filtered = filtered.filter(q => q.questionContent.toLowerCase().includes(keyword))
    }
    
    // 按题型筛选
    if (query.questionType) {
      filtered = filtered.filter(q => q.questionType === query.questionType)
    }
    
    // 按难度筛选
    if (query.difficulty) {
      filtered = filtered.filter(q => q.difficulty === query.difficulty)
    }
    
    // 按更新时间倒序
    filtered.sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime())
    
    // 分页
    const start = (query.current - 1) * query.pageSize
    const records = filtered.slice(start, start + query.pageSize)
    
    return {
      records: CommonUtil.deepClone(records),
      total: filtered.length,
      current: query.current,
      size: query.pageSize
    }
  },

  async getQuestionById(id: number): Promise<QuestionItem | null> {
    await CommonUtil.sleep(50)
    const question = questions.find(q => q.id === id)
    return question ? CommonUtil.deepClone(question) : null
  },

  async addQuestion(request: QuestionAddRequest): Promise<boolean> {
    await CommonUtil.sleep(100)
    const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
    const newQuestion: QuestionItem = {
      id: ++questionIdCounter,
      questionContent: request.questionContent,
      questionType: request.questionType,
      optionsText: request.optionsText || null,
      standardAnswer: request.standardAnswer,
      analysis: request.analysis || null,
      difficulty: request.difficulty,
      creatorTeacherId: request.creatorTeacherId || null,
      createdAt: now,
      updatedAt: now
    }
    questions.unshift(newQuestion)
    // 更新题型统计
    const typeItem = questionTypes.find(t => t.name === QUESTION_TYPE_MAP[request.questionType])
    if (typeItem) {
      typeItem.questionCount++
    }
    return true
  },

  async updateQuestion(request: QuestionUpdateRequest): Promise<boolean> {
    await CommonUtil.sleep(100)
    const index = questions.findIndex(q => q.id === request.id)
    if (index === -1) return false
    
    const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
    questions[index] = {
      ...questions[index],
      ...request,
      updatedAt: now
    }
    return true
  },

  async deleteQuestion(id: number): Promise<boolean> {
    await CommonUtil.sleep(80)
    const index = questions.findIndex(q => q.id === id)
    if (index === -1) return false
    
    const deleted = questions[index]
    questions.splice(index, 1)
    
    // 更新题型统计
    const typeItem = questionTypes.find(t => t.name === QUESTION_TYPE_MAP[deleted.questionType])
    if (typeItem && typeItem.questionCount > 0) {
      typeItem.questionCount--
    }
    return true
  },

  // 获取题目统计
  async getQuestionStats(): Promise<{ total: number; byType: Record<number, number>; byDifficulty: Record<number, number> }> {
    await CommonUtil.sleep(40)
    const byType: Record<number, number> = {}
    const byDifficulty: Record<number, number> = {}
    
    questions.forEach(q => {
      byType[q.questionType] = (byType[q.questionType] || 0) + 1
      byDifficulty[q.difficulty] = (byDifficulty[q.difficulty] || 0) + 1
    })
    
    return {
      total: questions.length,
      byType,
      byDifficulty
    }
  },
}
