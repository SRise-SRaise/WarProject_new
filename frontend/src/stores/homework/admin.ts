import { ref } from 'vue'
import { defineStore } from 'pinia'
import { homeworkRepository } from './repository'
import type {
  HomeworkAdminItem,
  HomeworkEditPayload,
  HomeworkPublishPayload,
  HomeworkReviewPayload,
  HomeworkSubmissionItem
} from './types'

export const useHomeworkAdminStore = defineStore('homework-admin', () => {
  const homeworks = ref<HomeworkAdminItem[]>([])
  const currentHomework = ref<HomeworkAdminItem | null>(null)
  const submissions = ref<HomeworkSubmissionItem[]>([])
  const currentSubmission = ref<HomeworkSubmissionItem | null>(null)
  const loading = ref(false)
  const hydrated = ref(false)

  async function ensureLoaded(): Promise<void> {
    if (hydrated.value) return
    await refresh()
  }

  async function refresh(): Promise<void> {
    loading.value = true
    try {
      homeworks.value = await homeworkRepository.listAdminHomeworks()
      hydrated.value = true
    } finally {
      loading.value = false
    }
  }

  async function selectHomework(id: string): Promise<HomeworkAdminItem | null> {
    await ensureLoaded()
    currentHomework.value = await homeworkRepository.getAdminHomeworkById(id)
    return currentHomework.value
  }

  async function saveHomework(payload: HomeworkEditPayload): Promise<HomeworkAdminItem> {
    const saved = await homeworkRepository.saveHomework(payload)
    await refresh()
    currentHomework.value = saved
    return saved
  }

  async function publishHomework(id: string, payload: HomeworkPublishPayload): Promise<HomeworkAdminItem | null> {
    const published = await homeworkRepository.publishHomework(id, payload)
    await refresh()
    currentHomework.value = published
    return published
  }

  async function loadSubmissions(homeworkId: string): Promise<HomeworkSubmissionItem[]> {
    submissions.value = await homeworkRepository.listHomeworkSubmissions(homeworkId)
    return submissions.value
  }

  async function selectSubmission(homeworkId: string, submissionId: string): Promise<HomeworkSubmissionItem | null> {
    currentSubmission.value = await homeworkRepository.getSubmissionById(homeworkId, submissionId)
    return currentSubmission.value
  }

  async function reviewSubmission(homeworkId: string, submissionId: string, payload: HomeworkReviewPayload): Promise<HomeworkSubmissionItem | null> {
    const reviewed = await homeworkRepository.reviewSubmission(homeworkId, submissionId, payload)
    await refresh()
    submissions.value = await homeworkRepository.listHomeworkSubmissions(homeworkId)
    currentSubmission.value = reviewed
    return reviewed
  }

  return {
    homeworks,
    currentHomework,
    submissions,
    currentSubmission,
    loading,
    hydrated,
    ensureLoaded,
    refresh,
    selectHomework,
    saveHomework,
    publishHomework,
    loadSubmissions,
    selectSubmission,
    reviewSubmission
  }
})
