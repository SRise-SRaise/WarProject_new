export interface HomeworkAttachment {
  name: string
  size: string
  kind: string
}

export interface HomeworkSection {
  title: string
  content: string
}

export type HomeworkStudentStatus = 'pending' | 'submitted' | 'reviewed' | 'overdue'
export type HomeworkAdminStatus = 'draft' | 'published' | 'reviewing' | 'closed'
export type HomeworkSubmissionStatus = 'draft' | 'submitted' | 'reviewed' | 'late'

export interface HomeworkSubmissionRecord {
  status: HomeworkSubmissionStatus
  submittedAt?: string
  updatedAt: string
  content: string
  fileName?: string
  score?: string
  teacherFeedback?: string
  reviewer?: string
  highlights: string[]
}

export interface HomeworkStudentItem {
  id: string
  title: string
  topicLabel: string
  teacher: string
  status: HomeworkStudentStatus
  deadline: string
  openTime: string
  summary: string
  tags: string[]
  requirementSections: HomeworkSection[]
  resources: HomeworkAttachment[]
  submitTips: string[]
  submission: HomeworkSubmissionRecord
}

export interface HomeworkAdminItem {
  id: string
  title: string
  topicLabel: string
  status: HomeworkAdminStatus
  summary: string
  publishScope: string
  deadline: string
  updatedAt: string
  submissionCount: number
  reviewedCount: number
  tags: string[]
  instructions: string[]
  resources: HomeworkAttachment[]
}

export interface HomeworkSubmissionItem {
  id: string
  homeworkId: string
  studentName: string
  className: string
  status: HomeworkSubmissionStatus
  submittedAt: string
  score?: string
  summary: string
  answerPreview: string
  attachments: HomeworkAttachment[]
  feedback?: string
}

export interface HomeworkFilters {
  keyword: string
  status: string
  topicLabel: string
}

export interface HomeworkSelectOption {
  label: string
  value: string
}

export interface HomeworkEditPayload {
  id?: string
  title: string
  topicLabel: string
  summary: string
  deadline: string
  tags: string[]
  instructions: string[]
}

export interface HomeworkPublishPayload {
  publishScope: string
  deadline: string
  allowLate: boolean
}

export interface HomeworkReviewPayload {
  score: string
  feedback: string
}
