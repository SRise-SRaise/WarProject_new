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
