import {
  listExerciseForStudent,
  publishExercise,
  getEduExerciseVoById
} from '@/api/eduExerciseController'
import dayjs from 'dayjs'
import {
  submitExerciseAnswers,
  getExerciseProgress,
  getMyExerciseScore,
  getSubmissionDetail,
  reviewExerciseItem,
  autoGradeExercise
} from '@/api/eduExerciseSubmissionController'
import { getLoginUser1 } from '@/api/authController'
import { useAuthStore } from '@/stores/user/auth'
import type {
  HomeworkAdminItem,
  HomeworkEditPayload,
  HomeworkPublishPayload,
  HomeworkReviewPayload,
  HomeworkSubmissionRecord,
  HomeworkSubmissionItem,
  HomeworkStudentItem
} from './types'

type ApiEnvelope<T> = {
  code?: number
  data?: T
  message?: string
}

const SUCCESS_CODES = new Set([0, 200])

// 辅助函数：从AuthStore获取当前用户ID
function getCurrentStudentId(): number {
  const authStore = useAuthStore()
  return Number(authStore.session?.id) || 0
}

async function resolveCurrentStudentId(): Promise<number> {
  const authStore = useAuthStore()
  const localId = Number(authStore.session?.id)
  if (Number.isFinite(localId) && localId > 0) {
    return localId
  }

  try {
    const response = await getLoginUser1()
    const principal = (response as any)?.data?.data
    const userIdRaw = principal?.userId
    const userId = Number(userIdRaw)
    if (Number.isFinite(userId) && userId > 0) {
      if (authStore.session) {
        authStore.updateProfile({ id: String(userId) })
      }
      return userId
    }
  } catch (error) {
    console.error('获取当前登录学生ID失败:', error)
  }

  return 0
}

function getCurrentTeacherId(): number {
  const authStore = useAuthStore()
  return Number(authStore.session?.id) || 0
}

// 辅助函数：状态映射
function mapStatus(status: string): 'pending' | 'submitted' | 'reviewed' | 'overdue' {
  const statusMap: Record<string, 'pending' | 'submitted' | 'reviewed' | 'overdue'> = {
    pending: 'pending',
    submitted: 'submitted',
    reviewed: 'reviewed',
    overdue: 'overdue'
  }
  return statusMap[status] || 'pending'
}

function formatDateTime(value: unknown): string {
  if (value === null || value === undefined || String(value).trim().length === 0) {
    return ''
  }
  const parsed = dayjs(String(value))
  if (!parsed.isValid()) {
    return String(value)
  }
  return parsed.format('YYYY-MM-DD HH:mm')
}

function toNumber(value: unknown, fallback = 0): number {
  if (typeof value === 'number' && Number.isFinite(value)) return value
  if (typeof value === 'string' && value.trim().length > 0) {
    const parsed = Number(value)
    return Number.isFinite(parsed) ? parsed : fallback
  }
  return fallback
}

function toStringValue(value: unknown, fallback = ''): string {
  if (typeof value === 'string') return value
  if (value === null || value === undefined) return fallback
  return String(value)
}

function unwrapData<T>(response: unknown, label: string): T {
  const envelope = (response as { data?: ApiEnvelope<T> })?.data
  if (!envelope) {
    throw new Error(`${label}响应为空`)
  }
  if (envelope.code !== undefined && !SUCCESS_CODES.has(envelope.code)) {
    throw new Error(envelope.message || `${label}失败`)
  }
  if (envelope.data === undefined || envelope.data === null) {
    throw new Error(envelope.message || `${label}返回为空`)
  }
  return envelope.data
}

function createDefaultSubmission(content = '', fileName = ''): HomeworkSubmissionRecord {
  const now = new Date().toISOString()
  return {
    status: 'draft',
    submittedAt: undefined,
    updatedAt: now,
    content,
    fileName: fileName || undefined,
    highlights: []
  }
}

function createStudentItemBase(partial: Partial<HomeworkStudentItem>): HomeworkStudentItem {
  return {
    id: partial.id || '',
    title: partial.title || '',
    topicLabel: partial.topicLabel || '',
    teacher: partial.teacher || '',
    status: partial.status || 'pending',
    deadline: partial.deadline || '',
    openTime: partial.openTime || '',
    summary: partial.summary || '',
    tags: partial.tags || [],
    requirementSections: partial.requirementSections || [],
    resources: partial.resources || [],
    submitTips: partial.submitTips || [],
    submission: partial.submission || createDefaultSubmission()
  }
}

export const homeworkRepository = {
  // 学生端：获取作业列表
  async listStudentHomeworks(): Promise<HomeworkStudentItem[]> {
    const studentId = await resolveCurrentStudentId()
    try {
      if (studentId <= 0) {
        console.error('学生ID无效，当前会话信息:', useAuthStore().session)
        return []
      }
      const response = await listExerciseForStudent({
        studentId,
        current: 1,
        pageSize: 50
      })
      const list = unwrapData<API.StudentExerciseVO[]>(response, '获取作业列表')
      return list.map((item) =>
        createStudentItemBase({
          id: toStringValue(item.id),
          title: toStringValue(item.taskName),
          topicLabel: '',
          teacher: toStringValue(item.teacherName),
          deadline: formatDateTime(item.endTime),
          openTime: formatDateTime(item.startTime),
          summary: toStringValue(item.description),
          status: mapStatus(toStringValue(item.status, 'pending')),
          submitTips: ['按时提交', '提交前检查答案完整性']
        })
      )
    } catch (error) {
      console.error('获取作业列表失败:', error)
      return []
    }
  },

  // 学生端：获取单个作业详情
  async getStudentHomeworkById(id: string): Promise<HomeworkStudentItem | null> {
    try {
      const response = await getEduExerciseVoById({ id })
      const exercise = unwrapData<API.EduExerciseVO>(response, '获取作业详情')
      return createStudentItemBase({
        id: toStringValue(exercise.id),
        title: toStringValue(exercise.taskName),
        topicLabel: '',
        teacher: '',
        deadline: formatDateTime(exercise.endTime),
        openTime: formatDateTime(exercise.startTime),
        summary: toStringValue(exercise.description),
        status: 'pending',
        submitTips: ['支持先暂存再提交', '提交后可查看评分进度']
      })
    } catch (error) {
      console.error('获取作业详情失败:', error)
      return null
    }
  },

  // 学生端：提交作业
  async submitHomework(
    id: string,
    content: string,
    fileName: string
  ): Promise<HomeworkStudentItem | null> {
    const studentId = await resolveCurrentStudentId()
    try {
      // 构造提交请求
      const response = await submitExerciseAnswers({
        exerciseId: Number(id),
        studentId,
        answers: [
          {
            itemId: 1,
            questionType: 5, // 简答题
            textContent: content
          }
        ]
      })
      const submitResult = unwrapData<API.SubmissionResultVO>(response, '提交作业')
      if (submitResult?.success) {
        return createStudentItemBase({
          id,
          status: 'submitted',
          submission: {
            status: 'submitted',
            submittedAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            content,
            fileName,
            highlights: ['已正式提交', '等待教师批阅']
          }
        })
      }
      return null
    } catch (error) {
      console.error('提交作业失败:', error)
      return null
    }
  },

  // 学生端：获取答题进度
  async getStudentProgress(exerciseId: string): Promise<{
    answeredCount: number
    totalCount: number
    isSubmitted: boolean
    items: Array<{
      itemId: number
      isAnswered: boolean
      choiceAnswer?: string
      textContent?: string
    }>
  } | null> {
    const studentId = await resolveCurrentStudentId()
    try {
      const response = await getExerciseProgress({
        exerciseId: Number(exerciseId),
        studentId
      })
      const progress = unwrapData<API.StudentProgressVO>(response, '获取答题进度')
      return {
        answeredCount: toNumber(progress.answeredCount),
        totalCount: toNumber(progress.totalCount),
        isSubmitted: Boolean(progress.isSubmitted),
        items: (progress.items || []).map((item) => ({
          itemId: toNumber(item.itemId),
          isAnswered: Boolean(item.isAnswered),
          choiceAnswer: item.choiceAnswer,
          textContent: item.textContent
        }))
      }
    } catch (error) {
      console.error('获取答题进度失败:', error)
      return null
    }
  },

  // 学生端：获取成绩
  async getStudentScore(exerciseId: string): Promise<{
    totalScore: number
    maxScore: number
    status: string
    items: Array<{
      itemId: number
      question: string
      maxScore: number
      studentScore: number
      studentAnswer: string
      gradingStatus: number
      comment?: string
    }>
  } | null> {
    const studentId = await resolveCurrentStudentId()
    try {
      const response = await getMyExerciseScore({
        exerciseId: Number(exerciseId),
        studentId
      })
      const score = unwrapData<API.StudentScoreVO>(response, '获取成绩')
      return {
        totalScore: toNumber(score.totalScore),
        maxScore: toNumber(score.maxScore),
        status: toStringValue(score.status, 'submitted'),
        items: (score.items || []).map((item) => ({
          itemId: toNumber(item.itemId),
          question: toStringValue(item.question),
          maxScore: toNumber(item.maxScore),
          studentScore: toNumber(item.studentScore),
          studentAnswer: toStringValue(item.studentAnswer),
          gradingStatus: toNumber(item.gradingStatus),
          comment: item.comment
        }))
      }
    } catch (error) {
      console.error('获取成绩失败:', error)
      return null
    }
  },

  // 教师端：获取作业列表
  async listAdminHomeworks(): Promise<HomeworkAdminItem[]> {
    // 教师端暂时使用Mock数据，后续对接真实API
    return []
  },

  // 教师端：获取单个作业详情
  async getAdminHomeworkById(id: string): Promise<HomeworkAdminItem | null> {
    try {
      const response = await getEduExerciseVoById({ id })
      const exercise = unwrapData<API.EduExerciseVO>(response, '获取教师作业详情')
      return {
        id: toStringValue(exercise.id),
        title: toStringValue(exercise.taskName),
        topicLabel: '',
        status: toNumber(exercise.publishStatus) === 1 ? 'published' : 'draft',
        summary: toStringValue(exercise.description),
        publishScope: '',
        deadline: formatDateTime(exercise.endTime),
        updatedAt: formatDateTime(exercise.updatedAt),
        submissionCount: toNumber(exercise.submissionCount),
        reviewedCount: toNumber(exercise.reviewedCount),
        tags: [],
        instructions: [],
        resources: []
      }
    } catch (error) {
      console.error('获取作业详情失败:', error)
      return null
    }
  },

  // 教师端：保存作业
  async saveHomework(payload: HomeworkEditPayload): Promise<HomeworkAdminItem> {
    // 暂时返回Mock数据
    return {
      id: payload.id || 'hw-new',
      title: payload.title,
      topicLabel: payload.topicLabel,
      status: 'draft',
      summary: payload.summary,
      publishScope: '待发布',
      deadline: payload.deadline,
      updatedAt: '刚刚',
      submissionCount: 0,
      reviewedCount: 0,
      tags: payload.tags,
      instructions: payload.instructions,
      resources: []
    }
  },

  // 教师端：发布作业
  async publishHomework(
    id: string,
    payload: HomeworkPublishPayload
  ): Promise<HomeworkAdminItem | null> {
    try {
      const response = await publishExercise({
        exerciseId: Number(id),
        classCodes: payload.classCodes || [],
        startTime: new Date().toISOString(),
        endTime: payload.deadline,
        allowLate: payload.allowLate
      })
      if (unwrapData<boolean>(response, '发布作业')) {
        return {
          id,
          title: '',
          topicLabel: '',
          status: 'published',
          summary: '',
          publishScope: payload.publishScope,
          deadline: payload.deadline,
          updatedAt: '刚刚',
          submissionCount: 0,
          reviewedCount: 0,
          tags: [],
          instructions: [],
          resources: []
        }
      }
      return null
    } catch (error) {
      console.error('发布作业失败:', error)
      return null
    }
  },

  // 教师端：获取提交列表
  async listHomeworkSubmissions(homeworkId: string): Promise<HomeworkSubmissionItem[]> {
    // 暂时返回空数组，后续对接真实API
    return []
  },

  // 教师端：获取提交详情
  async getSubmissionDetail(
    exerciseId: string,
    studentId: number
  ): Promise<{
    exerciseName: string
    studentName: string
    className: string
    submittedAt: string
    totalScore: number
    answers: Array<{
      recordId: number
      itemId: number
      question: string
      questionType: number
      optionsText?: string
      standardAnswer: string
      maxScore: number
      studentAnswer: string
      score: number
      gradingStatus: number
      comment?: string
    }>
  } | null> {
    try {
      const response = await getSubmissionDetail({
        exerciseId: Number(exerciseId),
        studentId
      })
      const detail = unwrapData<API.SubmissionDetailVO>(response, '获取提交详情')
      return {
        exerciseName: toStringValue(detail.exerciseName),
        studentName: toStringValue(detail.studentName),
        className: toStringValue(detail.className),
        submittedAt: formatDateTime(detail.submittedAt),
        totalScore: toNumber(detail.totalScore),
        answers: (detail.answers || []).map((item) => ({
          recordId: toNumber(item.recordId),
          itemId: toNumber(item.itemId),
          question: toStringValue(item.question),
          questionType: toNumber(item.questionType),
          optionsText: item.optionsText,
          standardAnswer: toStringValue(item.standardAnswer),
          maxScore: toNumber(item.maxScore),
          studentAnswer: toStringValue(item.studentAnswer),
          score: toNumber(item.score),
          gradingStatus: toNumber(item.gradingStatus),
          comment: item.comment
        }))
      }
    } catch (error) {
      console.error('获取提交详情失败:', error)
      return null
    }
  },

  // 教师端：批阅单题
  async reviewItem(
    recordId: number,
    score: number,
    comment: string
  ): Promise<boolean> {
    try {
      const response = await reviewExerciseItem({
        recordId,
        score,
        comment,
        gradingStatus: 2
      })
      return unwrapData<boolean>(response, '批阅单题')
    } catch (error) {
      console.error('批阅失败:', error)
      return false
    }
  },

  // 教师端：自动评分
  async autoGrade(exerciseId: string, studentId: number): Promise<number> {
    try {
      const response = await autoGradeExercise({
        exerciseId: Number(exerciseId),
        studentId
      })
      return unwrapData<number>(response, '自动评分')
    } catch (error) {
      console.error('自动评分失败:', error)
      return 0
    }
  },

  // 教师端：批阅提交
  async reviewSubmission(
    homeworkId: string,
    submissionId: string,
    payload: HomeworkReviewPayload
  ): Promise<HomeworkSubmissionItem | null> {
    // 暂时返回Mock数据
    return {
      id: submissionId,
      homeworkId,
      studentName: '学生',
      className: '',
      status: 'reviewed',
      submittedAt: '',
      summary: '',
      answerPreview: '',
      score: payload.score,
      feedback: payload.feedback,
      attachments: []
    }
  }
}
