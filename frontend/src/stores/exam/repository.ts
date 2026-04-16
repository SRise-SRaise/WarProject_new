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
  Exam,
  ExamAddRequest,
  ExamQueryRequest,
  ExamQuestion,
  ExamRecordItem,
  ExamStudentItem,
  ExamUpdateRequest,
  GradeAnswerRequest,
  PageResult,
  Paper,
  PaperAddRequest,
  PaperDetail,
  PaperItem,
  PaperQuestion,
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
import { QUESTION_TYPE_MAP } from './types'

let studentExams = CommonUtil.deepClone(examStudentFixtures)
let adminExams = CommonUtil.deepClone(adminExamFixtures)
let examRecords = CommonUtil.deepClone(examRecordFixtures)
let scoreSummary = CommonUtil.deepClone(scoreSummaryFixtures)
const questionBanks = CommonUtil.deepClone(questionBankFixtures)
const questionTypes = CommonUtil.deepClone(questionTypeFixtures)
const papers = CommonUtil.deepClone(paperFixtures)

// 模拟学生答题记录（用于教师批改）
let studentAnswerRecords: StudentAnswerRecord[] = [
  {
    id: 1,
    examId: 1,
    studentId: 1001,
    studentName: '张三',
    studentNo: '2024001',
    className: '软件工程2024级1班',
    submittedAt: '2026-04-20 10:25:00',
    totalScore: 100,
    earnedScore: 0,
    status: 'submitted',
    answers: {
      1: { answer: 'B', autoScore: 15, manualScore: null, maxScore: 15 },
      2: { answer: ['A', 'B', 'D'], autoScore: 15, manualScore: null, maxScore: 15 },
      9: { answer: '0', autoScore: 10, manualScore: null, maxScore: 10 },
      10: { answer: '1', autoScore: 10, manualScore: null, maxScore: 10 },
      3: { answer: '题库作为共享资产，放在exam模块可以统一管理题目质量，保证评分口径一致性，方便考试和作业共用题目资源。', autoScore: null, manualScore: null, maxScore: 25 },
      8: { answer: 'Model负责数据存储和业务逻辑处理；View负责界面展示；Controller负责接收用户输入，协调Model和View之间的交互。', autoScore: null, manualScore: null, maxScore: 25 },
    }
  },
  {
    id: 2,
    examId: 1,
    studentId: 1002,
    studentName: '李四',
    studentNo: '2024002',
    className: '软件工程2024级1班',
    submittedAt: '2026-04-20 10:28:00',
    totalScore: 100,
    earnedScore: 85,
    status: 'graded',
    answers: {
      1: { answer: 'B', autoScore: 15, manualScore: null, maxScore: 15 },
      2: { answer: ['A', 'B', 'D'], autoScore: 15, manualScore: null, maxScore: 15 },
      9: { answer: '0', autoScore: 10, manualScore: null, maxScore: 10 },
      10: { answer: '0', autoScore: 0, manualScore: null, maxScore: 10 },
      3: { answer: '因为考试是题库的主要使用场景，统一管理有利于质量控制。', autoScore: null, manualScore: 20, maxScore: 25, comment: '回答基本正确，但可以更详细说明共享资产的概念' },
      8: { answer: 'MVC是软件设计模式，M是模型层，V是视图层，C是控制层，三者分工明确。', autoScore: null, manualScore: 25, maxScore: 25, comment: '理解正确，描述较为简洁' },
    }
  },
  {
    id: 3,
    examId: 1,
    studentId: 1003,
    studentName: '王五',
    studentNo: '2024003',
    className: '软件工程2024级1班',
    submittedAt: '2026-04-20 10:30:00',
    totalScore: 100,
    earnedScore: 0,
    status: 'submitted',
    answers: {
      1: { answer: 'C', autoScore: 0, manualScore: null, maxScore: 15 },
      2: { answer: ['A', 'C'], autoScore: 7, manualScore: null, maxScore: 15 },
      9: { answer: '1', autoScore: 0, manualScore: null, maxScore: 10 },
      10: { answer: '1', autoScore: 10, manualScore: null, maxScore: 10 },
      3: { answer: '题库放在exam模块更合理。', autoScore: null, manualScore: null, maxScore: 25 },
      8: { answer: 'MVC就是把代码分成三部分。', autoScore: null, manualScore: null, maxScore: 25 },
    }
  },
  {
    id: 4,
    examId: 2,
    studentId: 1001,
    studentName: '张三',
    studentNo: '2024001',
    className: '软件工程2024级1班',
    submittedAt: '2026-04-18 14:40:00',
    totalScore: 100,
    earnedScore: 0,
    status: 'submitted',
    answers: {
      4: { answer: 'null,undefined', autoScore: 15, manualScore: null, maxScore: 15 },
      5: { answer: 'B', autoScore: 15, manualScore: null, maxScore: 15 },
      11: { answer: '0', autoScore: 10, manualScore: null, maxScore: 10 },
      6: { answer: 'function unique(arr) { return Array.from(new Set(arr)); }', autoScore: null, manualScore: null, maxScore: 40 },
      7: { answer: '200,404,500', autoScore: 20, manualScore: null, maxScore: 20 },
    }
  },
]

// 模拟考试数据
let examIdCounter = 10
let examsList: Exam[] = [
  {
    id: 1,
    examName: '软件工程期中考试',
    paperId: 1,
    durationMin: 90,
    startTime: '2026-04-20 09:00:00',
    isPublished: true,
    createdAt: '2026-04-10 10:00:00',
    updatedAt: '2026-04-15 14:30:00'
  },
  {
    id: 2,
    examName: 'Web前端技术随堂测验',
    paperId: 2,
    durationMin: 45,
    startTime: '2026-04-18 14:00:00',
    isPublished: true,
    createdAt: '2026-04-12 09:00:00',
    updatedAt: '2026-04-14 16:20:00'
  },
  {
    id: 3,
    examName: '计算机网络期末考试',
    paperId: null,
    durationMin: 120,
    startTime: null,
    isPublished: false,
    createdAt: '2026-04-14 11:00:00',
    updatedAt: '2026-04-14 11:00:00'
  },
  {
    id: 4,
    examName: '数据结构第三次测验',
    paperId: 3,
    durationMin: 60,
    startTime: null,
    isPublished: false,
    createdAt: '2026-04-15 08:30:00',
    updatedAt: '2026-04-15 08:30:00'
  }
]

// 模拟试卷数据
let paperIdCounter = 10
let papersList: Paper[] = [
  {
    id: 1,
    paperCode: 1,
    paperName: '软件工程期中测验',
    description: '涵盖软件工程基础、需求分析、架构设计等内容',
    generationTime: '2026-04-10 10:00:00',
    totalScore: 100,
    questionCount: 5,
    createdAt: '2026-04-10 10:00:00',
    updatedAt: '2026-04-12 15:30:00'
  },
  {
    id: 2,
    paperCode: 2,
    paperName: 'Web前端技术测验',
    description: 'JavaScript、React、CSS等前端技术综合测验',
    generationTime: '2026-04-11 14:00:00',
    totalScore: 100,
    questionCount: 4,
    createdAt: '2026-04-11 14:00:00',
    updatedAt: '2026-04-13 09:20:00'
  },
  {
    id: 3,
    paperCode: 3,
    paperName: '计算机网络基础测验',
    description: 'HTTP协议、网络模型、TCP/IP等基础知识',
    generationTime: null,
    totalScore: 0,
    questionCount: 0,
    createdAt: '2026-04-14 08:30:00',
    updatedAt: '2026-04-14 08:30:00'
  }
]

// 试卷题目关联
let paperQuestionIdCounter = 100
let paperQuestions: PaperQuestion[] = [
  // 试卷1的题目
  { id: 1, paperId: 1, questionId: 1, score: 15, questionOrder: 1, sectionName: '单选题' },
  { id: 2, paperId: 1, questionId: 2, score: 15, questionOrder: 2, sectionName: '多选题' },
  { id: 3, paperId: 1, questionId: 9, score: 10, questionOrder: 3, sectionName: '判断题' },
  { id: 4, paperId: 1, questionId: 10, score: 10, questionOrder: 4, sectionName: '判断题' },
  { id: 5, paperId: 1, questionId: 3, score: 25, questionOrder: 5, sectionName: '简答题' },
  { id: 6, paperId: 1, questionId: 8, score: 25, questionOrder: 6, sectionName: '简答题' },
  // 试卷2的题目
  { id: 7, paperId: 2, questionId: 4, score: 15, questionOrder: 1, sectionName: '填空题' },
  { id: 8, paperId: 2, questionId: 5, score: 15, questionOrder: 2, sectionName: '单选题' },
  { id: 9, paperId: 2, questionId: 11, score: 10, questionOrder: 3, sectionName: '判断题' },
  { id: 10, paperId: 2, questionId: 6, score: 40, questionOrder: 4, sectionName: '编程题' },
  { id: 11, paperId: 2, questionId: 7, score: 20, questionOrder: 5, sectionName: '填空题' },
]

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
    questionContent: 'JavaScript 中，____ 表示空值，____ 表示变量已声明但未赋值。',
    questionType: 1,
    optionsText: null,
    standardAnswer: 'null,undefined',
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
    questionContent: 'HTTP 状态码 ____ 表示请求成功，状态码 ____ 表示资源未找到，状态码 ____ 表示服务器内部错误。',
    questionType: 1,
    optionsText: null,
    standardAnswer: '200,404,500',
    analysis: 'HTTP 200表示请求成功，404表示资源未找到，500表示服务器内部错误。',
    difficulty: 2,
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
  },
  {
    id: 9,
    questionContent: 'JavaScript 是一种强类型语言。',
    questionType: 4,
    optionsText: null,
    standardAnswer: '0',
    analysis: 'JavaScript 是一种弱类型（动态类型）语言，变量类型可以在运行时改变。',
    difficulty: 1,
    creatorTeacherId: 2,
    createdAt: '2026-04-14 10:00:00',
    updatedAt: '2026-04-14 10:00:00'
  },
  {
    id: 10,
    questionContent: 'HTTP 协议是一种无状态协议。',
    questionType: 4,
    optionsText: null,
    standardAnswer: '1',
    analysis: 'HTTP 是无状态协议，服务器不会保存客户端的任何状态信息，每次请求都是独立的。',
    difficulty: 1,
    creatorTeacherId: 1,
    createdAt: '2026-04-14 10:15:00',
    updatedAt: '2026-04-14 10:15:00'
  },
  {
    id: 11,
    questionContent: 'RESTful API 中，POST 方法通常用于获取资源。',
    questionType: 4,
    optionsText: null,
    standardAnswer: '0',
    analysis: 'RESTful API 中，GET 用于获取资源，POST 用于创建资源。',
    difficulty: 2,
    creatorTeacherId: 1,
    createdAt: '2026-04-14 10:30:00',
    updatedAt: '2026-04-14 10:30:00'
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
      rankHint: total >= 90 ? '班级前 15%' : total >= 80 ? '班级前 35%' : '建议继续复���错题',
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

  // ========== 试卷 CRUD 方法 ==========
  async listPapersNew(query: PaperQueryRequest): Promise<PageResult<Paper>> {
    await CommonUtil.sleep(80)
    let filtered = [...papersList]
    
    if (query.paperName) {
      const keyword = query.paperName.toLowerCase()
      filtered = filtered.filter(p => p.paperName.toLowerCase().includes(keyword))
    }
    
    filtered.sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime())
    
    const start = (query.current - 1) * query.pageSize
    const records = filtered.slice(start, start + query.pageSize)
    
    return {
      records: CommonUtil.deepClone(records),
      total: filtered.length,
      current: query.current,
      size: query.pageSize
    }
  },

  async getPaperById(id: number): Promise<PaperDetail | null> {
    await CommonUtil.sleep(60)
    const paper = papersList.find(p => p.id === id)
    if (!paper) return null
    
    // 获取试卷的题目列表
    const pqs = paperQuestions
      .filter(pq => pq.paperId === id)
      .sort((a, b) => a.questionOrder - b.questionOrder)
      .map(pq => ({
        ...pq,
        question: questions.find(q => q.id === pq.questionId)
      }))
    
    return CommonUtil.deepClone({
      ...paper,
      questions: pqs
    })
  },

  async addPaper(request: PaperAddRequest): Promise<number> {
    await CommonUtil.sleep(100)
    const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
    const newPaper: Paper = {
      id: ++paperIdCounter,
      paperCode: request.paperCode,
      paperName: request.paperName,
      description: request.description || null,
      generationTime: null,
      totalScore: 0,
      questionCount: 0,
      createdAt: now,
      updatedAt: now
    }
    papersList.unshift(newPaper)
    return newPaper.id
  },

  async updatePaper(request: PaperUpdateRequest): Promise<boolean> {
    await CommonUtil.sleep(100)
    const index = papersList.findIndex(p => p.id === request.id)
    if (index === -1) return false
    
    const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
    papersList[index] = {
      ...papersList[index],
      ...request,
      updatedAt: now
    }
    return true
  },

  async deletePaper(id: number): Promise<boolean> {
    await CommonUtil.sleep(80)
    const index = papersList.findIndex(p => p.id === id)
    if (index === -1) return false
    
    papersList.splice(index, 1)
    // 同时删除关联的题目
    paperQuestions = paperQuestions.filter(pq => pq.paperId !== id)
    return true
  },

  // ========== 试卷题目关联方法 ==========
  async addPaperQuestion(request: PaperQuestionAddRequest): Promise<boolean> {
    await CommonUtil.sleep(60)
    const newPq: PaperQuestion = {
      id: ++paperQuestionIdCounter,
      paperId: request.paperId,
      questionId: request.questionId,
      score: request.score,
      questionOrder: request.questionOrder,
      sectionName: request.sectionName || null
    }
    paperQuestions.push(newPq)
    
    // 更新试卷统计
    const paper = papersList.find(p => p.id === request.paperId)
    if (paper) {
      paper.questionCount++
      paper.totalScore += request.score
      paper.generationTime = new Date().toISOString().replace('T', ' ').substring(0, 19)
      paper.updatedAt = paper.generationTime
    }
    return true
  },

  async updatePaperQuestion(id: number, score: number, sectionName?: string): Promise<boolean> {
    await CommonUtil.sleep(60)
    const pq = paperQuestions.find(p => p.id === id)
    if (!pq) return false
    
    const oldScore = pq.score
    pq.score = score
    if (sectionName !== undefined) pq.sectionName = sectionName
    
    // 更新试卷总分
    const paper = papersList.find(p => p.id === pq.paperId)
    if (paper) {
      paper.totalScore = paper.totalScore - oldScore + score
      paper.updatedAt = new Date().toISOString().replace('T', ' ').substring(0, 19)
    }
    return true
  },

  async removePaperQuestion(id: number): Promise<boolean> {
    await CommonUtil.sleep(60)
    const index = paperQuestions.findIndex(p => p.id === id)
    if (index === -1) return false
    
    const pq = paperQuestions[index]
    paperQuestions.splice(index, 1)
    
    // 更新试卷统计
    const paper = papersList.find(p => p.id === pq.paperId)
    if (paper) {
      paper.questionCount--
      paper.totalScore -= pq.score
      paper.updatedAt = new Date().toISOString().replace('T', ' ').substring(0, 19)
    }
    
    // 重新排序
    paperQuestions
      .filter(p => p.paperId === pq.paperId && p.questionOrder > pq.questionOrder)
      .forEach(p => p.questionOrder--)
    
    return true
  },

  async reorderPaperQuestions(paperId: number, questionIds: number[]): Promise<boolean> {
    await CommonUtil.sleep(80)
    questionIds.forEach((qId, idx) => {
      const pq = paperQuestions.find(p => p.paperId === paperId && p.questionId === qId)
      if (pq) pq.questionOrder = idx + 1
    })
    
    const paper = papersList.find(p => p.id === paperId)
    if (paper) {
      paper.updatedAt = new Date().toISOString().replace('T', ' ').substring(0, 19)
    }
    return true
  },

  // 获取所有题目（用于组卷时选择）
  async getAllQuestions(): Promise<QuestionItem[]> {
    await CommonUtil.sleep(50)
    return CommonUtil.deepClone(questions)
  },

  // ========== 考试 CRUD 方法 ==========
  async listExams(query: ExamQueryRequest): Promise<PageResult<Exam>> {
    await CommonUtil.sleep(80)
    let filtered = [...examsList]
    
    if (query.examName) {
      const keyword = query.examName.toLowerCase()
      filtered = filtered.filter(e => e.examName.toLowerCase().includes(keyword))
    }
    
    if (query.isPublished !== undefined) {
      filtered = filtered.filter(e => e.isPublished === query.isPublished)
    }
    
    // 按更新时间倒序
    filtered.sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime())
    
    // 关联试卷信息
    const records = filtered.map(e => ({
      ...e,
      paper: e.paperId ? papersList.find(p => p.id === e.paperId) : undefined
    }))
    
    const start = (query.current - 1) * query.pageSize
    const paged = records.slice(start, start + query.pageSize)
    
    return {
      records: CommonUtil.deepClone(paged),
      total: filtered.length,
      current: query.current,
      size: query.pageSize
    }
  },

  async getExamById(id: number): Promise<Exam | null> {
    await CommonUtil.sleep(60)
    const exam = examsList.find(e => e.id === id)
    if (!exam) return null
    
    return CommonUtil.deepClone({
      ...exam,
      paper: exam.paperId ? papersList.find(p => p.id === exam.paperId) : undefined
    })
  },

  async addExam(request: ExamAddRequest): Promise<number> {
    await CommonUtil.sleep(100)
    const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
    const newExam: Exam = {
      id: ++examIdCounter,
      examName: request.examName,
      paperId: request.paperId || null,
      durationMin: request.durationMin || null,
      startTime: request.startTime || null,
      isPublished: false,
      createdAt: now,
      updatedAt: now
    }
    examsList.unshift(newExam)
    return newExam.id
  },

  async updateExam(request: ExamUpdateRequest): Promise<boolean> {
    await CommonUtil.sleep(100)
    const index = examsList.findIndex(e => e.id === request.id)
    if (index === -1) return false
    
    const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
    const updated = { ...examsList[index] }
    
    if (request.examName !== undefined) updated.examName = request.examName
    if (request.paperId !== undefined) updated.paperId = request.paperId
    if (request.durationMin !== undefined) updated.durationMin = request.durationMin
    if (request.startTime !== undefined) updated.startTime = request.startTime
    if (request.isPublished !== undefined) updated.isPublished = request.isPublished
    updated.updatedAt = now
    
    examsList[index] = updated
    return true
  },

  async deleteExam(id: number): Promise<boolean> {
    await CommonUtil.sleep(80)
    const index = examsList.findIndex(e => e.id === id)
    if (index === -1) return false
    
    examsList.splice(index, 1)
    return true
  },

  async publishExam(id: number): Promise<boolean> {
    await CommonUtil.sleep(80)
    const exam = examsList.find(e => e.id === id)
    if (!exam) return false
    
    // 检查是否关联了试卷
    if (!exam.paperId) return false
    
    exam.isPublished = true
    exam.updatedAt = new Date().toISOString().replace('T', ' ').substring(0, 19)
    return true
  },

  async unpublishExam(id: number): Promise<boolean> {
    await CommonUtil.sleep(80)
    const exam = examsList.find(e => e.id === id)
    if (!exam) return false
    
    exam.isPublished = false
    exam.updatedAt = new Date().toISOString().replace('T', ' ').substring(0, 19)
    return true
  },

  // 获取考试统计
  async getExamStats(): Promise<{ total: number; published: number; draft: number; ongoing: number }> {
    await CommonUtil.sleep(40)
    const now = new Date()
    let ongoing = 0
    
    examsList.forEach(e => {
      if (e.isPublished && e.startTime && e.durationMin) {
        const start = new Date(e.startTime)
        const end = new Date(start.getTime() + e.durationMin * 60 * 1000)
        if (now >= start && now <= end) ongoing++
      }
    })
    
    return {
      total: examsList.length,
      published: examsList.filter(e => e.isPublished).length,
      draft: examsList.filter(e => !e.isPublished).length,
      ongoing
    }
  },

  // 获取所有试卷（用于考试关联选择）
  async getAllPapers(): Promise<Paper[]> {
    await CommonUtil.sleep(50)
    return CommonUtil.deepClone(papersList.filter(p => p.questionCount > 0))
  },

  // ========== 学生端考试方法 ==========
  // 获取学生可参加的考试（已发布且有试卷的考试）
  async getPublishedExamsForStudent(): Promise<Exam[]> {
    await CommonUtil.sleep(80)
    const published = examsList.filter(e => e.isPublished && e.paperId)
    
    return CommonUtil.deepClone(published.map(e => ({
      ...e,
      paper: e.paperId ? papersList.find(p => p.id === e.paperId) : undefined
    })))
  },

  // 获取考试详情（含完整试卷和题目，用于学生答题）
  async getExamDetailForStudent(examId: number): Promise<{
    exam: Exam
    paper: PaperDetail
  } | null> {
    await CommonUtil.sleep(100)
    const exam = examsList.find(e => e.id === examId)
    if (!exam || !exam.isPublished || !exam.paperId) return null
    
    const paper = papersList.find(p => p.id === exam.paperId)
    if (!paper) return null
    
    // 获取试卷的题目列表
    const pqs = paperQuestions
      .filter(pq => pq.paperId === paper.id)
      .sort((a, b) => a.questionOrder - b.questionOrder)
      .map(pq => ({
        ...pq,
        question: questions.find(q => q.id === pq.questionId)
      }))
    
    return CommonUtil.deepClone({
      exam: {
        ...exam,
        paper
      },
      paper: {
        ...paper,
        questions: pqs
      }
    })
  },

  // 提交学生答案
  async submitStudentAnswers(examId: number, answers: Record<number, string | string[]>): Promise<{
    totalScore: number
    earnedScore: number
    questionScores: Record<number, { earned: number; max: number }>
  }> {
    await CommonUtil.sleep(150)
    const exam = examsList.find(e => e.id === examId)
    if (!exam || !exam.paperId) {
      return { totalScore: 0, earnedScore: 0, questionScores: {} }
    }
    
    const pqs = paperQuestions.filter(pq => pq.paperId === exam.paperId)
    let totalScore = 0
    let earnedScore = 0
    const questionScores: Record<number, { earned: number; max: number }> = {}
    
    pqs.forEach(pq => {
      const question = questions.find(q => q.id === pq.questionId)
      if (!question) return
      
      totalScore += pq.score
      const answer = answers[pq.questionId]
      let earned = 0
      
      // 评分逻辑
      if (question.questionType === 1) {
        // 填空题：答案用逗号分隔，逐个比对
        const correctAnswers = question.standardAnswer.split(',').map(a => a.trim().toLowerCase())
        const studentAnswers = (typeof answer === 'string' ? answer : '').split(',').map(a => a.trim().toLowerCase())
        let correctCount = 0
        correctAnswers.forEach((ca, idx) => {
          if (studentAnswers[idx] === ca) correctCount++
        })
        earned = Math.round(pq.score * (correctCount / correctAnswers.length))
      } else if (question.questionType === 2) {
        // 单选题
        if (answer === question.standardAnswer) {
          earned = pq.score
        }
      } else if (question.questionType === 3) {
        // 多选题
        const correctArr = question.standardAnswer.split(',').sort()
        const answerArr = (Array.isArray(answer) ? answer : []).sort()
        if (correctArr.join(',') === answerArr.join(',')) {
          earned = pq.score
        } else {
          // 部分正确得一半分
          const correctSet = new Set(correctArr)
          const answerSet = new Set(answerArr)
          let correct = 0
          answerArr.forEach(a => { if (correctSet.has(a)) correct++ })
          const hasWrong = answerArr.some(a => !correctSet.has(a))
          if (!hasWrong && correct > 0) {
            earned = Math.round(pq.score * (correct / correctArr.length))
          }
        }
      } else if (question.questionType === 4) {
        // 判断题：1=正确，0=错误
        if (answer === question.standardAnswer) {
          earned = pq.score
        }
      } else if (question.questionType === 5 || question.questionType === 6 || question.questionType === 7) {
        // 简答/编程/综合题：根据答案长度给分（简化逻辑）
        const text = typeof answer === 'string' ? answer.trim() : ''
        if (text.length >= 50) earned = pq.score
        else if (text.length >= 30) earned = Math.round(pq.score * 0.8)
        else if (text.length >= 15) earned = Math.round(pq.score * 0.6)
        else if (text.length > 0) earned = Math.round(pq.score * 0.3)
      }
      
      earnedScore += earned
      questionScores[pq.questionId] = { earned, max: pq.score }
    })
    
    return { totalScore, earnedScore, questionScores }
  },

  // ========== 教师批改方法 ==========
  // 获取某考试的所有学生答题记录
  async getStudentAnswerRecords(examId: number): Promise<StudentAnswerRecord[]> {
    await CommonUtil.sleep(80)
    return CommonUtil.deepClone(studentAnswerRecords.filter(r => r.examId === examId))
  },

  // 获取单个学生答题记录详情
  async getStudentAnswerRecordById(recordId: number): Promise<{
    record: StudentAnswerRecord
    exam: Exam
    paper: PaperDetail
  } | null> {
    await CommonUtil.sleep(60)
    const record = studentAnswerRecords.find(r => r.id === recordId)
    if (!record) return null
    
    const exam = examsList.find(e => e.id === record.examId)
    if (!exam || !exam.paperId) return null
    
    const paper = papersList.find(p => p.id === exam.paperId)
    if (!paper) return null
    
    const pqs = paperQuestions
      .filter(pq => pq.paperId === paper.id)
      .sort((a, b) => a.questionOrder - b.questionOrder)
      .map(pq => ({
        ...pq,
        question: questions.find(q => q.id === pq.questionId)
      }))
    
    return CommonUtil.deepClone({
      record,
      exam: { ...exam, paper },
      paper: { ...paper, questions: pqs }
    })
  },

  // 教师批改答案
  async gradeAnswer(request: GradeAnswerRequest): Promise<boolean> {
    await CommonUtil.sleep(100)
    const record = studentAnswerRecords.find(r => r.id === request.recordId)
    if (!record) return false
    
    const answerInfo = record.answers[request.questionId]
    if (!answerInfo) return false
    
    answerInfo.manualScore = request.score
    if (request.comment) answerInfo.comment = request.comment
    
    // 重新计算总得分
    let totalEarned = 0
    Object.values(record.answers).forEach(a => {
      if (a.autoScore !== null) totalEarned += a.autoScore
      if (a.manualScore !== null) totalEarned += a.manualScore
    })
    record.earnedScore = totalEarned
    
    // 检查是否所有需要手动批改的题目都已批改
    const allGraded = Object.values(record.answers).every(a => 
      a.autoScore !== null || a.manualScore !== null
    )
    if (allGraded) {
      record.status = 'graded'
    } else {
      record.status = 'grading'
    }
    
    return true
  },

  // 获取成绩统计数据
  async getScoreStatistics(examId: number): Promise<{
    totalStudents: number
    submittedCount: number
    gradedCount: number
    pendingCount: number
    averageScore: number
    highestScore: number
    lowestScore: number
    passRate: number
  }> {
    await CommonUtil.sleep(60)
    const records = studentAnswerRecords.filter(r => r.examId === examId)
    const gradedRecords = records.filter(r => r.status === 'graded')
    
    const scores = gradedRecords.map(r => r.earnedScore)
    const passScore = 60  // 及格分数线
    
    return {
      totalStudents: records.length,
      submittedCount: records.length,
      gradedCount: gradedRecords.length,
      pendingCount: records.filter(r => r.status !== 'graded').length,
      averageScore: scores.length > 0 ? Math.round(scores.reduce((a, b) => a + b, 0) / scores.length) : 0,
      highestScore: scores.length > 0 ? Math.max(...scores) : 0,
      lowestScore: scores.length > 0 ? Math.min(...scores) : 0,
      passRate: scores.length > 0 ? Math.round(scores.filter(s => s >= passScore).length / scores.length * 100) : 0
    }
  },

  // 获取所有已发布考试（用于成绩分析页面）
  async getPublishedExamsForGrading(): Promise<Array<Exam & { 
    submittedCount: number
    gradedCount: number 
    pendingCount: number
  }>> {
    await CommonUtil.sleep(80)
    const published = examsList.filter(e => e.isPublished && e.paperId)
    
    return CommonUtil.deepClone(published.map(e => {
      const records = studentAnswerRecords.filter(r => r.examId === e.id)
      return {
        ...e,
        paper: e.paperId ? papersList.find(p => p.id === e.paperId) : undefined,
        submittedCount: records.length,
        gradedCount: records.filter(r => r.status === 'graded').length,
        pendingCount: records.filter(r => r.status !== 'graded').length
      }
    }))
  },
}
