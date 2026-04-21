export interface ExperimentAsset {
  name: string
  size: string
  kind: string
}

export interface ExperimentStep {
  id: string
  title: string
  detail: string
  deliverable: string
}

export type ExperimentStudentStatus = 'pending' | 'in_progress' | 'completed' | 'reviewed'
export type ExperimentAdminStatus = 'draft' | 'published' | 'running' | 'closed'
export type ExperimentResultStatus = 'pending' | 'submitted' | 'reviewed'

export interface ExperimentWorkRecord {
  status: ExperimentResultStatus
  startedAt: string
  updatedAt: string
  note: string
  reportName?: string
  score?: string
  teacherFeedback?: string
  highlights: string[]
}

export interface ExperimentStudentItem {
  id: string
  title: string
  topicLabel: string
  teacher: string
  status: ExperimentStudentStatus
  schedule: string
  summary: string
  objective: string
  tags: string[]
  materials: ExperimentAsset[]
  steps: ExperimentStep[]
  work: ExperimentWorkRecord
}

export interface ExperimentAdminItem {
  id: string
  sortOrder?: number
  title: string
  topicLabel: string
  status: ExperimentAdminStatus
  summary: string
  schedule: string
  scope: string
  updatedAt: string
  itemCount: number
  resultCount: number
  tags: string[]
}

export interface ExperimentResultItem {
  id: string
  experimentId: string
  studentName: string
  className: string
  status: ExperimentResultStatus
  submittedAt: string
  score?: string
  summary: string
  reportName?: string
  feedback?: string
}

export interface ExperimentEditPayload {
  id?: string
  title: string
  topicLabel: string
  summary: string
  schedule: string
  tags: string[]
}

export interface ExperimentResultPayload {
  score: string
  feedback: string
}

// ==================== 题型相关类型 ====================

/** 题目类型枚举 */
export type QuestionType = 1 | 2 | 3 | 4 | 5 | 6 | 7

/** 题目类型映射（与后端 ExperimentQuestionTypeEnum 保持一致） */
export const QUESTION_TYPE_NAMES: Record<QuestionType, string> = {
  1: '选择题',
  2: '填空题',
  3: '编程题',
  4: '简答题',
  5: '多选题',
  6: '判断题',
  7: '设计题'
}

/** 编程语言类型 */
export type ProgrammingLanguage = 'text' | 'css' | 'javascript' | 'html' | 'jsp' | 'java' | 'python' | 'sql'

/** 编程语言显示名称映射 */
export const LANGUAGE_NAMES: Record<ProgrammingLanguage, string> = {
  text: '纯文本',
  css: 'CSS',
  javascript: 'JavaScript',
  html: 'HTML',
  jsp: 'JSP',
  java: 'Java',
  python: 'Python',
  sql: 'SQL'
}

/** 选项结构 */
export interface QuestionOption {
  key: string      // 选项标识，如 'A', 'B', 'C', 'D'
  label: string    // 选项内容
}

/** 单个题目 */
export interface ExperimentQuestion {
  id: string
  experimentItemId: string          // 对应原 war-project 的 experiment_item_id
  title: string                      // 题目名称/标题
  type: QuestionType                 // 题目类型
  content: string                    // 题目内容（填空题为带 ____ 的模板）
  options?: QuestionOption[]         // 选项（单选/多选时有效）
  correctAnswer?: string             // 正确答案（仅教师端可见）
  score: number                     // 分值
  allowPaste: boolean               // 是否允许粘贴（防作弊）
  language?: ProgrammingLanguage    // 代码题的语言类型
}

/** 学生答题记录 */
export interface StudentAnswer {
  questionId: string
  answer: string                    // 答案内容
  filledBlanks?: string[]            // 填空题的每个空答案
  answeredAt?: string                // 答题时间
}

/** 答题卡整体状态 */
export interface AnswerSheetState {
  experimentId: string
  totalScore: number
  answeredCount: number
  questions: ExperimentQuestion[]
  answers: Map<string, StudentAnswer>
  lastSavedAt?: string
  isSubmitting: boolean
  isAutoSaving: boolean
}

/** 答题卡保存请求 */
export interface AnswerSaveRequest {
  experimentId: string
  answers: Array<{
    questionId: string
    answer: string
    filledBlanks?: string[]
  }>
  isSubmit: boolean
}

// ==================== 教师端实验步骤编辑类型 ====================

/** 实验步骤编辑项 */
export interface ExperimentStepEditItem {
  id: string
  stepNo: number              // 步骤序号
  title: string               // 步骤标题
  type: QuestionType          // 题目类型
  content: string             // 题目内容
  options?: QuestionOption[]  // 选项
  correctAnswer: string       // 正确答案
  score: number               // 分值
  allowPaste: boolean         // 是否允许粘贴
  language?: ProgrammingLanguage // 代码语言
}

/** 步骤编辑保存请求 */
export interface StepEditSaveRequest {
  experimentId: string
  steps: ExperimentStepEditItem[]
}

// ==================== 实验报告相关类型 ====================

/** 学生实验报告信息 */
export interface StudentReportInfo {
  studentId: string
  studentNo: string
  studentName: string
  clazzNo: string
  experimentId: string
  experimentName: string
  submittedAt: string
  totalScore: number
  teacherScore?: number
  teacherFeedback?: string
  status: 'pending' | 'submitted' | 'reviewed'
}

/** 报告题目答案 */
export interface ReportQuestion {
  id: string
  experimentItemId: string
  stepNo: number
  title: string
  type: QuestionType
  content: string
  score: number
  options?: QuestionOption[]
  studentAnswer: string
  filledBlanks?: string[]
  teacherScore?: number
  teacherComment?: string
}

/** 实验报告完整数据 */
export interface ExperimentReport {
  student: {
    id: string
    no: string
    name: string
    clazzNo: string
  }
  experiment: {
    id: string
    name: string
    courseName: string
    schedule: string
  }
  summary: string          // 得分汇总
  objective: string        // 实验目的
  content: string          // 实验内容
  steps: ReportQuestion[]
  summaryNote: string      // 实验小结
  teacherFeedback?: string
  teacherSign?: string
  submittedAt: string
  reviewedAt?: string
  status?: 'pending' | 'submitted' | 'reviewed'
}

/** 批改请求 */
export interface GradeSubmitRequest {
  experimentId: string
  studentId: string
  scores: Array<{
    questionId: string
    score: number
    comment?: string
  }>
  totalScore?: number
  feedback?: string
}

// ==================== 学生实验数据分析类型 ====================

export interface StudentExperimentAnalysisVO {
  totalPublishedExperiments: number
  completedExperiments: number
  completionRate: number
  gradedExperiments: number
  totalScore: number
  totalMaxScore: number
  averageScore: number
  maxScore: number
  minScore: number
  experimentTypeScores: ExperimentTypeScoreVO[]
}

export interface ExperimentTypeScoreVO {
  typeCode: number
  typeName: string
  experimentCount: number
  totalScore: number
  averageScore: number
}

export interface StudentAnalysisState {
  loading: boolean
  data: StudentExperimentAnalysisVO | null
  error: string | null
}
