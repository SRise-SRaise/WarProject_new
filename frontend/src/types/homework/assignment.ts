export interface HomeworkAssignDraftRequest {
  exerciseId: number
  classCodes?: string[]
  startTime?: string
  endTime?: string
  allowLate?: boolean
}

export interface HomeworkQuestionStat {
  count: number
  totalScore: number
}
