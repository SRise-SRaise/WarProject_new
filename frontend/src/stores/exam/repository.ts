import type { AxiosResponse } from 'axios'
import {
  addExamCustom,
  deleteEduExam,
  getAdminExamCard,
  getExamStats as getExamStatsApi,
  getStudentExamDetail,
  listExamPageFull,
  listGradingExams,
  listPaperOptions,
  listPublishedExamsForStudent as listPublishedExamsForStudentApi,
  publishExam as publishExamApi,
  unpublishExam as unpublishExamApi,
  updateExamCustom,
} from '@/api/eduExamController'
import {
  addEduPaper,
  deleteEduPaper,
  getPaperDetail,
  listAllPapers as listAllPaperSummaries,
  listPaperPageFull,
  updateEduPaper,
} from '@/api/eduPaperController'
import {
  addEduQuestionBank,
  deleteEduQuestionBank,
  getQuestionStats as getQuestionStatsApi,
  listAllQuestions as listAllQuestionEntities,
  listEduQuestionBankByPage,
  updateEduQuestionBank,
} from '@/api/eduQuestionBankController'
import { listAllEduQuestionTypes } from '@/api/eduQuestionTypeController'
import {
  addQuestionToPaper as addQuestionToPaperApi,
  removeQuestionFromPaper as removeQuestionFromPaperApi,
  reorderPaperQuestions as reorderPaperQuestionsApi,
  updatePaperQuestion as updatePaperQuestionApi,
} from '@/api/relPaperQuestionController'
import {
  getScoreStatistics as getScoreStatisticsApi,
  getStudentAnswerRecordDetail,
  gradeAnswer as gradeAnswerApi,
  listExamRecordCards,
  listStudentAnswerRecords as listStudentAnswerRecordsApi,
  submitExam as submitExamApi,
} from '@/api/resExamRecordController'
import type {
  AdminExamItem,
  Exam,
  ExamAddRequest,
  ExamQueryRequest,
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
  QuestionType,
  QuestionTypeItem,
  QuestionUpdateRequest,
  ScoreSummaryItem,
  StudentAnswerRecord,
  StudentExamDetail,
  StudentExamResult,
} from './types'

type ApiEnvelope<T> = {
  code?: number
  data?: T
  message?: string
}

type ApiResponse<T> = AxiosResponse<ApiEnvelope<T>>
type UnknownMap = Record<string, unknown>

const SUCCESS_CODES = new Set([0, 200])

const QUESTION_TYPE_META: Record<number, { usage: string; summary: string }> = {
  1: { usage: '客观题 / 自动判分', summary: '适合结构化空值补全与关键术语识别。' },
  2: { usage: '客观题 / 自动判分', summary: '适合单点判断与概念辨析。' },
  3: { usage: '客观题 / 自动判分', summary: '适合组合性判断和多条件筛查。' },
  4: { usage: '客观题 / 自动判分', summary: '适合真假判断与规则校验。' },
  5: { usage: '主观题 / 教师批改', summary: '适合结构化表达与概念说明。' },
  6: { usage: '主观题 / 教师批改', summary: '适合代码实现、算法和调试类考查。' },
  7: { usage: '主观题 / 教师批改', summary: '适合综合设计与跨知识点分析。' },
}

function isRecord(value: unknown): value is UnknownMap {
  return typeof value === 'object' && value !== null
}

function asArray(value: unknown): unknown[] {
  return Array.isArray(value) ? value : []
}

function toNumber(value: unknown, fallback = 0): number {
  if (typeof value === 'number' && Number.isFinite(value)) return value
  if (typeof value === 'string' && value.trim() !== '') {
    const parsed = Number(value)
    return Number.isFinite(parsed) ? parsed : fallback
  }
  return fallback
}

function toOptionalNumber(value: unknown): number | null {
  if (value === null || value === undefined || value === '') return null
  return toNumber(value)
}

function toStringValue(value: unknown, fallback = ''): string {
  if (typeof value === 'string') return value
  if (value === null || value === undefined) return fallback
  return String(value)
}

function toOptionalString(value: unknown): string | null {
  const text = toStringValue(value).trim()
  return text ? text : null
}

function toBooleanValue(value: unknown): boolean {
  if (typeof value === 'boolean') return value
  if (typeof value === 'number') return value !== 0
  if (typeof value === 'string') return value === '1' || value.toLowerCase() === 'true'
  return false
}

function parseQuestionType(value: unknown): QuestionType {
  const type = toNumber(value, 2)
  return type >= 1 && type <= 7 ? (type as QuestionType) : 2
}

function parseTags(value: unknown): string[] {
  return asArray(value).map((item) => toStringValue(item)).filter(Boolean)
}

function unwrap<T>(response: ApiResponse<T>, label: string): T {
  const envelope = response.data
  if (!envelope) throw new Error(`${label}响应为空`)
  if (envelope.code !== undefined && !SUCCESS_CODES.has(envelope.code)) {
    throw new Error(envelope.message || `${label}失败`)
  }
  if (envelope.data === undefined || envelope.data === null) {
    throw new Error(envelope.message || `${label}返回为空`)
  }
  return envelope.data
}

function unwrapListMap(response: ApiResponse<Record<string, any>[]>, label: string): UnknownMap[] {
  return unwrap(response, label).filter((item) => isRecord(item))
}

function unwrapMap(response: ApiResponse<Record<string, any>>, label: string): UnknownMap {
  const data = unwrap(response, label)
  if (!isRecord(data)) throw new Error(`${label}返回格式错误`)
  return data
}

function mapQuestion(raw: API.EduQuestionBank | UnknownMap): QuestionItem {
  return {
    id: toNumber(raw.id),
    questionContent: toStringValue(raw.questionContent),
    questionType: parseQuestionType(raw.questionType),
    optionsText: toOptionalString(raw.optionsText),
    standardAnswer: toStringValue(raw.standardAnswer),
    analysis: toOptionalString(raw.analysis),
    difficulty: toNumber(raw.difficulty, 3),
    creatorTeacherId: toOptionalNumber(raw.creatorTeacherId),
    createdAt: toStringValue(raw.createdAt),
    updatedAt: toStringValue(raw.updatedAt),
  }
}

function mapPaperSummary(raw: UnknownMap): Paper {
  return {
    id: toNumber(raw.id),
    paperCode: toNumber(raw.paperCode),
    paperName: toStringValue(raw.paperName),
    description: toOptionalString(raw.description),
    generationTime: toOptionalString(raw.generationTime),
    totalScore: toNumber(raw.totalScore),
    questionCount: toNumber(raw.questionCount),
    createdAt: toStringValue(raw.createdAt),
    updatedAt: toStringValue(raw.updatedAt),
  }
}

function mapPaperQuestion(raw: UnknownMap): PaperQuestion {
  const question = isRecord(raw.question) ? mapQuestion(raw.question) : undefined
  return {
    id: toNumber(raw.id),
    paperId: toNumber(raw.paperId),
    questionId: toNumber(raw.questionId),
    score: toNumber(raw.score),
    questionOrder: toNumber(raw.questionOrder),
    sectionName: toOptionalString(raw.sectionName),
    question,
  }
}

function mapPaperDetail(raw: UnknownMap): PaperDetail {
  return {
    ...mapPaperSummary(raw),
    questions: asArray(raw.questions).filter(isRecord).map(mapPaperQuestion),
  }
}

function mapExam(raw: UnknownMap): Exam {
  const paper = isRecord(raw.paper) ? mapPaperSummary(raw.paper) : undefined
  return {
    id: toNumber(raw.id),
    examName: toStringValue(raw.examName || raw.title),
    paperId: toOptionalNumber(raw.paperId),
    durationMin: toOptionalNumber(raw.durationMin),
    startTime: toOptionalString(raw.startTime),
    endTime: toOptionalString(raw.endTime),
    isPublished: toBooleanValue(raw.isPublished),
    createdAt: toStringValue(raw.createdAt),
    updatedAt: toStringValue(raw.updatedAt),
    paper,
  }
}

function mapAdminExam(raw: UnknownMap): AdminExamItem {
  return {
    id: toStringValue(raw.id),
    title: toStringValue(raw.title || raw.examName),
    topicLabel: toStringValue(raw.topicLabel || raw.paperTitle || '考试安排'),
    status: (toStringValue(raw.status) as AdminExamItem['status']) || 'draft',
    scope: toStringValue(raw.scope || '全体可见'),
    schedule: toStringValue(raw.schedule || ''),
    paperTitle: toStringValue(raw.paperTitle || ''),
    candidateCount: toNumber(raw.candidateCount ?? raw.submittedCount),
    submittedCount: toNumber(raw.submittedCount),
    averageScore: toStringValue(raw.averageScore ?? '0'),
    summary: toStringValue(raw.summary || ''),
    tags: parseTags(raw.tags),
  }
}

function mapExamRecord(raw: UnknownMap): ExamRecordItem {
  const status = toStringValue(raw.status)
  return {
    id: toStringValue(raw.id),
    examId: toStringValue(raw.examId),
    studentName: toStringValue(raw.studentName),
    className: toStringValue(raw.className),
    status: status === 'graded' ? 'reviewed' : status === 'grading' ? 'in_progress' : status === 'submitted' ? 'submitted' : 'not_started',
    startedAt: toStringValue(raw.startedAt || raw.submittedAt || '--'),
    submittedAt: toOptionalString(raw.submittedAt) ?? undefined,
    score: toOptionalString(raw.score) ?? undefined,
    risk: (toStringValue(raw.risk || 'low') as ExamRecordItem['risk']) || 'low',
    note: toStringValue(raw.note || ''),
  }
}

function parseAnswerValue(value: unknown): string | string[] {
  if (Array.isArray(value)) {
    return value.map((item) => toStringValue(item))
  }
  return toStringValue(value)
}

function mapStudentAnswerRecord(raw: UnknownMap): StudentAnswerRecord {
  const answers: StudentAnswerRecord['answers'] = {}
  if (isRecord(raw.answers)) {
    for (const [questionId, answerRaw] of Object.entries(raw.answers)) {
      if (!isRecord(answerRaw)) continue
      answers[toNumber(questionId)] = {
        answer: parseAnswerValue(answerRaw.answer),
        autoScore: answerRaw.autoScore === null || answerRaw.autoScore === undefined ? null : toNumber(answerRaw.autoScore),
        manualScore: answerRaw.manualScore === null || answerRaw.manualScore === undefined ? null : toNumber(answerRaw.manualScore),
        maxScore: toNumber(answerRaw.maxScore),
        comment: toOptionalString(answerRaw.comment) ?? undefined,
      }
    }
  }
  const status = toStringValue(raw.status)
  return {
    id: toNumber(raw.id),
    examId: toNumber(raw.examId),
    studentId: toNumber(raw.studentId),
    studentName: toStringValue(raw.studentName),
    studentNo: toStringValue(raw.studentNo),
    className: toStringValue(raw.className),
    submittedAt: toOptionalString(raw.submittedAt) ?? '--',
    totalScore: toNumber(raw.totalScore),
    earnedScore: toNumber(raw.earnedScore),
    status: status === 'graded' ? 'graded' : status === 'grading' ? 'grading' : 'submitted',
    answers,
  }
}

function mapQuestionTypeItem(raw: UnknownMap, typeCount: Record<number, number>): QuestionTypeItem {
  const typeId = toNumber(raw.typeId)
  const meta = QUESTION_TYPE_META[typeId] || { usage: '题型维护', summary: '题型字典项。' }
  return {
    id: toStringValue(raw.typeId),
    name: toStringValue(raw.typeName),
    questionCount: typeCount[typeId] || 0,
    usage: meta.usage,
    summary: meta.summary,
  }
}

function mapQuestionBankCards(questions: QuestionItem[], stats: { total: number }): QuestionBankItem[] {
  const latest = questions.map((item) => item.updatedAt).sort().reverse()[0] || ''
  return [{
    id: 'shared-bank',
    name: '共享题库',
    questionCount: stats.total,
    sharedUsage: '服务考试组卷与教师批改流程',
    owner: '考试模块',
    updatedAt: latest,
    description: '当前系统中的考试题目资源集合。',
    tags: ['真实数据', '后端同步'],
  }]
}

function mapPaperItem(raw: UnknownMap): PaperItem {
  const questionCount = toNumber(raw.questionCount)
  return {
    id: toStringValue(raw.id),
    title: toStringValue(raw.paperName),
    topicLabel: '试卷资源',
    questionCount,
    duration: `${questionCount} 题`,
    status: questionCount > 0 ? 'ready' : 'draft',
    questionBankName: '共享题库',
    updatedAt: toStringValue(raw.updatedAt),
    summary: toStringValue(raw.description || ''),
    tags: questionCount > 0 ? ['已组卷'] : ['待组卷'],
  }
}

function mapScoreSummaryItems(stats: { total: number; published: number; draft: number; ongoing: number }): ScoreSummaryItem[] {
  return [
    { label: '考试总数', value: String(stats.total), detail: '当前考试模块下已创建的考试数量。', tone: 'primary' },
    { label: '已发布', value: String(stats.published), detail: '已经开放给学生的考试数量。', tone: 'success' },
    { label: '草稿数', value: String(stats.draft), detail: '仍可继续编辑的考试草稿。', tone: 'warning' },
    { label: '进行中', value: String(stats.ongoing), detail: '当前在时间窗口内的考试。', tone: 'accent' },
  ]
}

function mapPageResult<T>(raw: UnknownMap, mapper: (item: UnknownMap) => T): PageResult<T> {
  const records = asArray(raw.records).filter(isRecord).map(mapper)
  return {
    records,
    total: toNumber(raw.total),
    current: toNumber(raw.current, 1),
    size: toNumber(raw.size, records.length || 10),
  }
}

async function loadQuestionStatsInternal(): Promise<{ total: number; byType: Record<number, number>; byDifficulty: Record<number, number> }> {
  const raw = unwrapMap(await getQuestionStatsApi(), '获取题目统计')
  return {
    total: toNumber(raw.total),
    byType: isRecord(raw.byType) ? Object.fromEntries(Object.entries(raw.byType).map(([key, value]) => [Number(key), toNumber(value)])) : {},
    byDifficulty: isRecord(raw.byDifficulty) ? Object.fromEntries(Object.entries(raw.byDifficulty).map(([key, value]) => [Number(key), toNumber(value)])) : {},
  }
}

async function loadAllQuestionEntities(): Promise<QuestionItem[]> {
  const raw = unwrap(await listAllQuestionEntities(), '获取全部题目')
  return raw.map((item) => mapQuestion(item))
}

export const examRepository = {
  async listStudentExams(): Promise<ExamStudentItem[]> {
    const exams = await this.getPublishedExamsForStudent()
    return exams.map((exam) => ({
      id: String(exam.id),
      title: exam.examName,
      topicLabel: exam.paper?.paperName || '考试安排',
      teacher: '教师端发布',
      status: exam.isPublished ? 'ready' : 'upcoming',
      schedule: exam.startTime || '开放后可参加',
      duration: exam.durationMin ? `${exam.durationMin} 分钟` : '未设置',
      questionCount: exam.paper?.questionCount ?? 0,
      summary: exam.paper?.description || '查看考试详情并开始作答。',
      tags: [exam.isPublished ? '已发布' : '未发布'],
      questionBankNote: '题库由考试模块统一管理。',
      rules: [], readyChecklist: [], questions: [], answerDraft: {},
      result: { score: '', rankHint: '', submittedAt: '', durationUsed: '', feedback: '', highlights: [] },
    }))
  },

  async getStudentExamById(id: string): Promise<ExamStudentItem | null> {
    const detail = await this.getExamDetailForStudent(Number(id))
    if (!detail) return null
    return {
      id: String(detail.exam.id),
      title: detail.exam.examName,
      topicLabel: detail.exam.paper?.paperName || '考试安排',
      teacher: '教师端发布',
      status: detail.exam.isPublished ? 'ready' : 'upcoming',
      schedule: detail.exam.startTime || '开放后可参加',
      duration: detail.exam.durationMin ? `${detail.exam.durationMin} 分钟` : '未设置',
      questionCount: detail.paper.questionCount,
      summary: detail.paper.description || '查看考试详情并开始作答。',
      tags: [detail.exam.isPublished ? '已发布' : '未发布'],
      questionBankNote: '题库由考试模块统一管理。',
      rules: [], readyChecklist: [], questions: [], answerDraft: {},
      result: { score: '', rankHint: '', submittedAt: '', durationUsed: '', feedback: '', highlights: [] },
    }
  },

  async submitStudentExam(id: string, answers: Record<string, string | string[]>): Promise<ExamStudentItem | null> {
    const result = await this.submitStudentAnswers(Number(id), Object.fromEntries(Object.entries(answers).map(([key, value]) => [Number(key), value])))
    return {
      id,
      title: '', topicLabel: '', teacher: '', status: 'completed', schedule: '', duration: '',
      questionCount: Object.keys(result.questionScores).length,
      summary: '', tags: ['已提交'], questionBankNote: '', rules: [], readyChecklist: [], questions: [],
      answerDraft: answers,
      result: { score: `${result.earnedScore} / ${result.totalScore}`, rankHint: '', submittedAt: '', durationUsed: '', feedback: '', highlights: [] },
    }
  },

  async listQuestionBanks(): Promise<QuestionBankItem[]> {
    const [questions, stats] = await Promise.all([loadAllQuestionEntities(), loadQuestionStatsInternal()])
    return mapQuestionBankCards(questions, stats)
  },

  async listQuestionTypes(): Promise<QuestionTypeItem[]> {
    const [types, stats] = await Promise.all([
      unwrapListMap(await listAllEduQuestionTypes(), '获取题型列表'),
      loadQuestionStatsInternal(),
    ])
    return types.map((item) => mapQuestionTypeItem(item, stats.byType))
  },

  async listPapers(): Promise<PaperItem[]> {
    const raw = unwrapListMap(await listAllPaperSummaries(), '获取试卷列表')
    return raw.map(mapPaperItem)
  },

  async listAdminExams(): Promise<AdminExamItem[]> {
    const page = unwrapMap(await listExamPageFull({ current: 1, pageSize: 100 }), '获取考试列表')
    return asArray(page.records).filter(isRecord).map(mapAdminExam)
  },

  async getAdminExamById(id: string): Promise<AdminExamItem | null> {
    return mapAdminExam(unwrapMap(await getAdminExamCard({ id: Number(id) }), '获取考试详情'))
  },

  async listExamRecords(examId: string): Promise<ExamRecordItem[]> {
    return unwrapListMap(await listExamRecordCards({ examId: Number(examId) }), '获取考试记录卡片').map(mapExamRecord)
  },

  async listScoreSummary(): Promise<ScoreSummaryItem[]> {
    return mapScoreSummaryItems(await this.getExamStats())
  },

  async listQuestions(query: QuestionQueryRequest): Promise<PageResult<QuestionItem>> {
    const page = unwrap(await listEduQuestionBankByPage({
      current: query.current,
      pageSize: query.pageSize,
      questionContent: query.questionContent,
      questionType: query.questionType,
      difficulty: query.difficulty,
    }), '获取题目分页')
    return mapPageResult(isRecord(page) ? page : {}, mapQuestion)
  },

  async getQuestionById(id: number): Promise<QuestionItem | null> {
    const questions = await loadAllQuestionEntities()
    return questions.find((item) => item.id === id) ?? null
  },

  async addQuestion(request: QuestionAddRequest): Promise<boolean> {
    return unwrap(await addEduQuestionBank({
      questionContent: request.questionContent,
      questionType: request.questionType,
      optionsText: request.optionsText,
      standardAnswer: request.standardAnswer,
      analysis: request.analysis,
      difficulty: request.difficulty,
      creatorTeacherId: request.creatorTeacherId,
    }), '新增题目')
  },

  async updateQuestion(request: QuestionUpdateRequest): Promise<boolean> {
    return unwrap(await updateEduQuestionBank({
      id: request.id,
      questionContent: request.questionContent,
      questionType: request.questionType,
      optionsText: request.optionsText,
      standardAnswer: request.standardAnswer,
      analysis: request.analysis,
      difficulty: request.difficulty,
    }), '更新题目')
  },

  async deleteQuestion(id: number): Promise<boolean> {
    return unwrap(await deleteEduQuestionBank({ id: String(id) }), '删除题目')
  },

  async getQuestionStats(): Promise<{ total: number; byType: Record<number, number>; byDifficulty: Record<number, number> }> {
    return loadQuestionStatsInternal()
  },

  async listPapersNew(query: PaperQueryRequest): Promise<PageResult<Paper>> {
    const page = unwrapMap(await listPaperPageFull({ current: query.current, pageSize: query.pageSize, paperName: query.paperName }), '获取试卷分页')
    return mapPageResult(page, mapPaperSummary)
  },

  async getPaperById(id: number): Promise<PaperDetail | null> {
    return mapPaperDetail(unwrapMap(await getPaperDetail({ id }), '获取试卷详情'))
  },

  async addPaper(request: PaperAddRequest): Promise<number> {
    const ok = unwrap(await addEduPaper({
      paperCode: request.paperCode,
      paperName: request.paperName,
      description: request.description,
    }), '新建试卷')
    if (!ok) return 0
    const papers = unwrapListMap(await listAllPaperSummaries(), '获取试卷列表')
      .map(mapPaperSummary)
      .filter((item) => item.paperCode === request.paperCode && item.paperName === request.paperName)
      .sort((a, b) => b.id - a.id)
    return papers[0]?.id ?? 0
  },

  async updatePaper(request: PaperUpdateRequest): Promise<boolean> {
    return unwrap(await updateEduPaper({
      id: request.id,
      paperCode: request.paperCode,
      paperName: request.paperName,
      description: request.description,
    }), '更新试卷')
  },

  async deletePaper(id: number): Promise<boolean> {
    return unwrap(await deleteEduPaper({ id: String(id) }), '删除试卷')
  },
  async addPaperQuestion(request: PaperQuestionAddRequest): Promise<boolean> {
    return unwrap(await addQuestionToPaperApi({
      paperId: request.paperId,
      questionId: request.questionId,
      score: request.score,
      questionOrder: request.questionOrder,
      sectionName: request.sectionName,
    }), '试卷添加题目')
  },

  async updatePaperQuestion(id: number, score: number, sectionName?: string): Promise<boolean> {
    return unwrap(await updatePaperQuestionApi({ id, score, sectionName }), '更新试卷题目')
  },

  async removePaperQuestion(id: number): Promise<boolean> {
    return unwrap(await removeQuestionFromPaperApi({ id }), '移除试卷题目')
  },

  async reorderPaperQuestions(paperId: number, questionIds: number[]): Promise<boolean> {
    return unwrap(await reorderPaperQuestionsApi({ paperId, questionIds }), '试卷题目重排')
  },

  async getAllQuestions(): Promise<QuestionItem[]> {
    return loadAllQuestionEntities()
  },

  async listExams(query: ExamQueryRequest): Promise<PageResult<Exam>> {
    const page = unwrapMap(await listExamPageFull({ current: query.current, pageSize: query.pageSize, examName: query.examName, isPublished: query.isPublished }), '获取考试分页')
    return mapPageResult(page, mapExam)
  },

  async getExamById(id: number): Promise<Exam | null> {
    return mapExam(unwrapMap(await getAdminExamCard({ id }), '获取考试详情'))
  },

  async addExam(request: ExamAddRequest): Promise<number> {
    return unwrap(await addExamCustom({
      examName: request.examName,
      paperId: request.paperId,
      durationMin: request.durationMin,
      startTime: request.startTime,
      endTime: request.endTime,
      isPublished: false,
    }), '新增考试')
  },

  async updateExam(request: ExamUpdateRequest): Promise<boolean> {
    return unwrap(await updateExamCustom({
      id: request.id,
      examName: request.examName,
      paperId: request.paperId,
      durationMin: request.durationMin,
      startTime: request.startTime,
      endTime: request.endTime,
      isPublished: request.isPublished,
    }), '更新考试')
  },

  async deleteExam(id: number): Promise<boolean> {
    return unwrap(await deleteEduExam({ id: String(id) }), '删除考试')
  },

  async publishExam(id: number): Promise<boolean> {
    return unwrap(await publishExamApi({ id }), '发布考试')
  },

  async unpublishExam(id: number): Promise<boolean> {
    return unwrap(await unpublishExamApi({ id }), '取消发布考试')
  },

  async getExamStats(): Promise<{ total: number; published: number; draft: number; ongoing: number }> {
    const raw = unwrapMap(await getExamStatsApi(), '获取考试统计')
    return {
      total: toNumber(raw.total),
      published: toNumber(raw.published),
      draft: toNumber(raw.draft),
      ongoing: toNumber(raw.ongoing),
    }
  },

  async getAllPapers(): Promise<Paper[]> {
    const raw = unwrapListMap(await listPaperOptions(), '获取试卷选项')
    return raw.map(mapPaperSummary)
  },

  async getPublishedExamsForStudent(): Promise<Exam[]> {
    return unwrapListMap(await listPublishedExamsForStudentApi(), '获取学生考试列表').map(mapExam)
  },

  async getExamDetailForStudent(examId: number): Promise<StudentExamDetail | null> {
    const raw = unwrapMap(await getStudentExamDetail({ id: examId }), '获取学生考试详情')
    const exam = isRecord(raw.exam) ? mapExam(raw.exam) : null
    const paper = isRecord(raw.paper) ? mapPaperDetail(raw.paper) : null
    if (!exam || !paper) return null
    return { exam, paper }
  },

  async submitStudentAnswers(examId: number, answers: Record<number, string | string[]>): Promise<StudentExamResult> {
    const raw = unwrapMap(await submitExamApi({ examId, answers }), '提交考试')
    const questionScores: StudentExamResult['questionScores'] = {}
    if (isRecord(raw.questionScores)) {
      for (const [questionId, item] of Object.entries(raw.questionScores)) {
        if (!isRecord(item)) continue
        questionScores[Number(questionId)] = {
          earned: toNumber(item.earned),
          max: toNumber(item.max),
        }
      }
    }
    return {
      totalScore: toNumber(raw.totalScore),
      earnedScore: toNumber(raw.earnedScore),
      questionScores,
    }
  },

  async getStudentAnswerRecords(examId: number): Promise<StudentAnswerRecord[]> {
    return unwrapListMap(await listStudentAnswerRecordsApi({ examId }), '获取学生答卷记录').map(mapStudentAnswerRecord)
  },

  async getStudentAnswerRecordById(recordId: number): Promise<{ record: StudentAnswerRecord; exam: Exam; paper: PaperDetail } | null> {
    const raw = unwrapMap(await getStudentAnswerRecordDetail({ recordId }), '获取学生答卷详情')
    const record = isRecord(raw.record) ? mapStudentAnswerRecord(raw.record) : null
    const exam = isRecord(raw.exam) ? mapExam(raw.exam) : null
    const paper = isRecord(raw.paper) ? mapPaperDetail(raw.paper) : null
    if (!record || !exam || !paper) return null
    return { record, exam, paper }
  },

  async gradeAnswer(request: GradeAnswerRequest): Promise<boolean> {
    return unwrap(await gradeAnswerApi({
      recordId: request.recordId,
      questionId: request.questionId,
      score: request.score,
      comment: request.comment,
    }), '提交批改结果')
  },

  async getScoreStatistics(examId: number): Promise<{ totalStudents: number; submittedCount: number; gradedCount: number; pendingCount: number; averageScore: number; highestScore: number; lowestScore: number; passRate: number }> {
    const raw = unwrapMap(await getScoreStatisticsApi({ examId }), '获取成绩统计')
    return {
      totalStudents: toNumber(raw.totalStudents),
      submittedCount: toNumber(raw.submittedCount),
      gradedCount: toNumber(raw.gradedCount),
      pendingCount: toNumber(raw.pendingCount),
      averageScore: toNumber(raw.averageScore),
      highestScore: toNumber(raw.highestScore),
      lowestScore: toNumber(raw.lowestScore),
      passRate: toNumber(raw.passRate),
    }
  },

  async getPublishedExamsForGrading(): Promise<Array<Exam & { submittedCount: number; gradedCount: number; pendingCount: number }>> {
    const raw = unwrapListMap(await listGradingExams(), '获取可批改考试列表')
    return raw.map((item) => ({
      ...mapExam(item),
      submittedCount: toNumber(item.submittedCount),
      gradedCount: toNumber(item.gradedCount),
      pendingCount: toNumber(item.pendingCount),
    }))
  },
}
