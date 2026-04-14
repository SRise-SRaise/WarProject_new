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
