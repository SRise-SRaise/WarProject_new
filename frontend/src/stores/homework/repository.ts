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
  HomeworkSubmissionItem,
  HomeworkStudentItem
} from './types'

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
      if (response.data?.data) {
        return response.data.data.map((item) => ({
          id: String(item.id),
          title: item.taskName || '',
          topicLabel: '',
          teacher: item.teacherName || '',
          deadline: formatDateTime(item.endTime),
          openTime: formatDateTime(item.startTime),
          summary: item.description || '',
          status: mapStatus(item.status || 'pending'),
          tags: [],
          totalScore: item.totalScore || 0,
          studentScore: item.studentScore || 0,
          itemCount: item.itemCount || 0
        }))
      }
      return []
    } catch (error) {
      console.error('获取作业列表失败:', error)
      return []
    }
  },

  // 学生端：获取单个作业详情
  async getStudentHomeworkById(id: string): Promise<HomeworkStudentItem | null> {
    try {
      const response = await getEduExerciseVoById({ id })
      const exercise = response.data?.data
      if (exercise) {
        return {
          id: String(exercise.id),
          title: exercise.taskName || '',
          topicLabel: '',
          teacher: '',
          deadline: formatDateTime(exercise.endTime),
          openTime: formatDateTime(exercise.startTime),
          summary: exercise.description || '',
          status: 'pending',
          tags: [],
          totalScore: 0,
          studentScore: 0,
          itemCount: 0
        }
      }
      return null
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
      const submitResult = response.data?.data
      if (submitResult?.success) {
        return {
          id,
          title: '',
          topicLabel: '',
          teacher: '',
          deadline: '',
          openTime: '',
          summary: '',
          status: 'submitted',
          tags: [],
          submission: {
            status: 'submitted',
            submittedAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            content,
            fileName,
            highlights: ['已正式提交', '等待教师批阅']
          }
        }
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
      if (response.data) {
        return {
          answeredCount: response.data.answeredCount || 0,
          totalCount: response.data.totalCount || 0,
          isSubmitted: response.data.isSubmitted || false,
          items: (response.data.items || []).map((item) => ({
            itemId: item.itemId || 0,
            isAnswered: item.isAnswered || false,
            choiceAnswer: item.choiceAnswer,
            textContent: item.textContent
          }))
        }
      }
      return null
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
      if (response.data) {
        return {
          totalScore: response.data.totalScore || 0,
          maxScore: response.data.maxScore || 0,
          status: response.data.status || 'submitted',
          items: (response.data.items || []).map((item) => ({
            itemId: item.itemId || 0,
            question: item.question || '',
            maxScore: item.maxScore || 0,
            studentScore: item.studentScore || 0,
            studentAnswer: item.studentAnswer || '',
            gradingStatus: item.gradingStatus || 0,
            comment: item.comment
          }))
        }
      }
      return null
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
      if (response.data) {
        return {
          id: String(response.data.id),
          title: response.data.taskName || '',
          topicLabel: '',
          status: response.data.publishStatus === 1 ? 'published' : 'draft',
          summary: response.data.description || '',
          publishScope: '',
          deadline: response.data.endTime || '',
          updatedAt: '',
          submissionCount: 0,
          reviewedCount: 0,
          tags: [],
          instructions: [],
          resources: []
        }
      }
      return null
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
      if (response.data) {
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
      if (response.data) {
        return {
          exerciseName: response.data.exerciseName || '',
          studentName: response.data.studentName || '',
          className: response.data.className || '',
          submittedAt: response.data.submittedAt || '',
          totalScore: response.data.totalScore || 0,
          answers: (response.data.answers || []).map((item) => ({
            recordId: item.recordId || 0,
            itemId: item.itemId || 0,
            question: item.question || '',
            questionType: item.questionType || 0,
            optionsText: item.optionsText,
            standardAnswer: item.standardAnswer || '',
            maxScore: item.maxScore || 0,
            studentAnswer: item.studentAnswer || '',
            score: item.score || 0,
            gradingStatus: item.gradingStatus || 0,
            comment: item.comment
          }))
        }
      }
      return null
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
      return response.data || false
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
      return response.data || 0
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
