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

// 题目类型枚举
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
