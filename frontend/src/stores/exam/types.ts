export type ExamStudentStatus = 'upcoming' | 'ready' | 'in_progress' | 'completed'
export type ExamAdminStatus = 'draft' | 'scheduled' | 'running' | 'closed'
export type ExamRecordStatus = 'not_started' | 'in_progress' | 'submitted' | 'reviewed'
export type ExamQuestionKind = 'single' | 'multiple' | 'short'

export interface ExamQuestionOption {
  key: string
  label: string
}

export interface ExamQuestion {
  id: string
  title: string
  prompt: string
  kind: ExamQuestionKind
  score: number
  options?: ExamQuestionOption[]
  correctAnswer: string | string[]
  answerTip: string
}

export interface ExamRule {
  label: string
  content: string
}

export interface ExamStudentResult {
  score: string
  rankHint: string
  submittedAt: string
  durationUsed: string
  feedback: string
  highlights: string[]
}

export interface ExamStudentItem {
  id: string
  title: string
  topicLabel: string
  teacher: string
  status: ExamStudentStatus
  schedule: string
  duration: string
  questionCount: number
  summary: string
  tags: string[]
  questionBankNote: string
  rules: ExamRule[]
  readyChecklist: string[]
  questions: ExamQuestion[]
  answerDraft: Record<string, string | string[]>
  result: ExamStudentResult
}

export interface QuestionBankItem {
  id: string
  name: string
  questionCount: number
  sharedUsage: string
  owner: string
  updatedAt: string
  description: string
  tags: string[]
}

export interface QuestionTypeItem {
  id: string
  name: string
  questionCount: number
  usage: string
  summary: string
}

// 题目类型枚举（1-填空 2-单选 3-多选 4-判断 5-简答 6-编程 7-综合）
// 注：填空题使用 ____ 标记填空位置，答案用逗号分隔对应多个空
// 注：判断题答案使用 1 表示正确，0 表示错误
export type QuestionType = 1 | 2 | 3 | 4 | 5 | 6 | 7

export const QUESTION_TYPE_MAP: Record<QuestionType, string> = {
  1: '填空',
  2: '单选',
  3: '多选',
  4: '判断',
  5: '简答',
  6: '编程',
  7: '综合'
}

// 判断题答案映射
export const JUDGE_ANSWER_MAP: Record<string, string> = {
  '1': '正确',
  '0': '错误'
}

export const DIFFICULTY_MAP: Record<number, string> = {
  1: '简单',
  2: '较易',
  3: '中等',
  4: '较难',
  5: '困难'
}

// 题目实体（对应数据库 edu_question_bank）
export interface QuestionItem {
  id: number
  questionContent: string
  questionType: QuestionType
  optionsText: string | null
  standardAnswer: string
  analysis: string | null
  difficulty: number
  creatorTeacherId: number | null
  createdAt: string
  updatedAt: string
}

// 题目添加请求
export interface QuestionAddRequest {
  questionContent: string
  questionType: QuestionType
  optionsText?: string
  standardAnswer: string
  analysis?: string
  difficulty: number
  creatorTeacherId?: number
}

// 题目更新请求
export interface QuestionUpdateRequest {
  id: number
  questionContent?: string
  questionType?: QuestionType
  optionsText?: string
  standardAnswer?: string
  analysis?: string
  difficulty?: number
}

// 题目查询请求
export interface QuestionQueryRequest {
  current: number
  pageSize: number
  questionContent?: string
  questionType?: QuestionType
  difficulty?: number
}

// 分页结果
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
}

export interface PaperItem {
  id: string
  title: string
  topicLabel: string
  questionCount: number
  duration: string
  status: 'draft' | 'ready' | 'used'
  questionBankName: string
  updatedAt: string
  summary: string
  tags: string[]
}

// 试卷实体（对应数据库 edu_paper）
export interface Paper {
  id: number
  paperCode: number
  paperName: string
  description: string | null
  generationTime: string | null
  totalScore: number
  questionCount: number
  createdAt: string
  updatedAt: string
}

// 试卷题目关联（对应数据库 rel_paper_question）
export interface PaperQuestion {
  id: number
  paperId: number
  questionId: number
  score: number
  questionOrder: number
  sectionName: string | null
  // 关联的题目详情
  question?: QuestionItem
}

// 试卷添加请求
export interface PaperAddRequest {
  paperCode: number
  paperName: string
  description?: string
}

// 试卷更新请求
export interface PaperUpdateRequest {
  id: number
  paperCode?: number
  paperName?: string
  description?: string
}

// 试卷题目添加请求
export interface PaperQuestionAddRequest {
  paperId: number
  questionId: number
  score: number
  questionOrder: number
  sectionName?: string
}

// 试卷查询请求
export interface PaperQueryRequest {
  current: number
  pageSize: number
  paperName?: string
}

// 试卷详情（包含题目列表）
export interface PaperDetail extends Paper {
  questions: PaperQuestion[]
}

export interface AdminExamItem {
  id: string
  title: string
  topicLabel: string
  status: ExamAdminStatus
  scope: string
  schedule: string
  paperTitle: string
  candidateCount: number
  submittedCount: number
  averageScore: string
  summary: string
  tags: string[]
}

export interface ExamRecordItem {
  id: string
  examId: string
  studentName: string
  className: string
  status: ExamRecordStatus
  startedAt: string
  submittedAt?: string
  score?: string
  risk: 'low' | 'medium' | 'high'
  note: string
}

export interface ScoreSummaryItem {
  label: string
  value: string
  detail: string
  tone: 'primary' | 'success' | 'warning' | 'accent'
}

// 考试实体（对应数据库 edu_exam）
export interface Exam {
  id: number
  examName: string
  paperId: number | null
  durationMin: number | null
  startTime: string | null
  isPublished: boolean
  createdAt: string
  updatedAt: string
  // 关联的试卷信息
  paper?: Paper
}

// 考试添加请求
export interface ExamAddRequest {
  examName: string
  paperId?: number
  durationMin?: number
  startTime?: string
}

// 考试更新请求
export interface ExamUpdateRequest {
  id: number
  examName?: string
  paperId?: number
  durationMin?: number
  startTime?: string
  isPublished?: boolean
}

// 考试查询请求
export interface ExamQueryRequest {
  current: number
  pageSize: number
  examName?: string
  isPublished?: boolean
}

// 考试状态类型
export type ExamStatus = 'draft' | 'ready' | 'published' | 'ongoing' | 'ended'

// 计算考试状态
export function getExamStatus(exam: Exam): ExamStatus {
  if (!exam.isPublished) {
    return exam.paperId ? 'ready' : 'draft'
  }
  if (!exam.startTime) return 'published'
  
  const now = new Date()
  const start = new Date(exam.startTime)
  const end = new Date(start.getTime() + (exam.durationMin || 0) * 60 * 1000)
  
  if (now < start) return 'published'
  if (now >= start && now <= end) return 'ongoing'
  return 'ended'
}

// 学生答题结果
export interface StudentExamResult {
  totalScore: number
  earnedScore: number
  questionScores: Record<number, { earned: number; max: number }>
}

// 学生考试详情（含试卷和题目）
export interface StudentExamDetail {
  exam: Exam
  paper: PaperDetail
}

// 学生答题记录（用于教师批改）
export interface StudentAnswerRecord {
  id: number
  examId: number
  studentId: number
  studentName: string
  studentNo: string
  className: string
  submittedAt: string
  totalScore: number
  earnedScore: number
  status: 'submitted' | 'grading' | 'graded'
  answers: Record<number, {
    answer: string | string[]
    autoScore: number | null  // 系统自动评分（填空/选择题）
    manualScore: number | null  // 教师手动评分（简答/编程题）
    maxScore: number
    comment?: string  // 教师评语
  }>
}

// 批改请求
export interface GradeAnswerRequest {
  recordId: number
  questionId: number
  score: number
  comment?: string
}
