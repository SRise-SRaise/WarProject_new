import request from '@/request'
import type { HomeworkAssignDraftRequest } from '@/types/homework/assignment'

export async function saveHomeworkAssignDraft(body: HomeworkAssignDraftRequest) {
  return request.post('/homework/eduExercise/saveDraftAssign', body)
}
