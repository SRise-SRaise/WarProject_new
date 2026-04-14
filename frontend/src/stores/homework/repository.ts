import { CommonUtil } from '@/utils'
import {
  homeworkAdminFixtures,
  homeworkSubmissionFixtures,
  homeworkStudentFixtures
} from './fixtures'
import type {
  HomeworkAdminItem,
  HomeworkEditPayload,
  HomeworkPublishPayload,
  HomeworkReviewPayload,
  HomeworkSubmissionItem,
  HomeworkStudentItem
} from './types'

let studentHomeworks = CommonUtil.deepClone(homeworkStudentFixtures)
let adminHomeworks = CommonUtil.deepClone(homeworkAdminFixtures)
let submissions = CommonUtil.deepClone(homeworkSubmissionFixtures)

function syncAdminCounts(homeworkId: string): void {
  const matched = adminHomeworks.find((item) => item.id === homeworkId)
  if (!matched) {
    return
  }
  const relevant = submissions.filter((item) => item.homeworkId === homeworkId)
  matched.submissionCount = relevant.length
  matched.reviewedCount = relevant.filter((item) => item.status === 'reviewed').length
}

export const homeworkRepository = {
  async listStudentHomeworks(): Promise<HomeworkStudentItem[]> {
    await CommonUtil.sleep(90)
    return CommonUtil.deepClone(studentHomeworks)
  },
  async getStudentHomeworkById(id: string): Promise<HomeworkStudentItem | null> {
    await CommonUtil.sleep(70)
    const matched = studentHomeworks.find((item) => item.id === id)
    return matched ? CommonUtil.deepClone(matched) : null
  },
  async submitHomework(id: string, content: string, fileName: string): Promise<HomeworkStudentItem | null> {
    await CommonUtil.sleep(120)
    const matched = studentHomeworks.find((item) => item.id === id)
    if (!matched) {
      return null
    }

    matched.status = 'submitted'
    matched.submission = {
      ...matched.submission,
      status: 'submitted',
      submittedAt: '刚刚',
      updatedAt: '刚刚',
      content,
      fileName,
      highlights: ['已正式提交', '等待教师批阅']
    }

    const existingSubmission = submissions.find((item) => item.homeworkId === id && item.studentName === '李明')
    if (existingSubmission) {
      existingSubmission.status = 'submitted'
      existingSubmission.submittedAt = '刚刚'
      existingSubmission.answerPreview = content
      existingSubmission.summary = '已提交最新版作业内容。'
      existingSubmission.attachments = fileName ? [{ name: fileName, size: '自填文件', kind: '附件' }] : []
    } else {
      submissions.unshift({
        id: CommonUtil.generateId('sub'),
        homeworkId: id,
        studentName: '李明',
        className: '软工 2402',
        status: 'submitted',
        submittedAt: '刚刚',
        summary: '已提交最新版作业内容。',
        answerPreview: content,
        attachments: fileName ? [{ name: fileName, size: '自填文件', kind: '附件' }] : []
      })
    }

    syncAdminCounts(id)
    return CommonUtil.deepClone(matched)
  },
  async listAdminHomeworks(): Promise<HomeworkAdminItem[]> {
    await CommonUtil.sleep(100)
    return CommonUtil.deepClone(adminHomeworks)
  },
  async getAdminHomeworkById(id: string): Promise<HomeworkAdminItem | null> {
    await CommonUtil.sleep(80)
    const matched = adminHomeworks.find((item) => item.id === id)
    return matched ? CommonUtil.deepClone(matched) : null
  },
  async saveHomework(payload: HomeworkEditPayload): Promise<HomeworkAdminItem> {
    await CommonUtil.sleep(120)
    if (payload.id) {
      const matched = adminHomeworks.find((item) => item.id === payload.id)
      if (matched) {
        matched.title = payload.title
        matched.topicLabel = payload.topicLabel
        matched.summary = payload.summary
        matched.deadline = payload.deadline
        matched.tags = [...payload.tags]
        matched.instructions = [...payload.instructions]
        matched.updatedAt = '刚刚'
        return CommonUtil.deepClone(matched)
      }
    }

    const nextHomework: HomeworkAdminItem = {
      id: CommonUtil.generateId('hw'),
      title: payload.title,
      topicLabel: payload.topicLabel,
      status: 'draft',
      summary: payload.summary,
      publishScope: '待发布',
      deadline: payload.deadline,
      updatedAt: '刚刚',
      submissionCount: 0,
      reviewedCount: 0,
      tags: [...payload.tags],
      instructions: [...payload.instructions],
      resources: []
    }
    adminHomeworks.unshift(nextHomework)
    return CommonUtil.deepClone(nextHomework)
  },
  async publishHomework(id: string, payload: HomeworkPublishPayload): Promise<HomeworkAdminItem | null> {
    await CommonUtil.sleep(100)
    const matched = adminHomeworks.find((item) => item.id === id)
    if (!matched) {
      return null
    }
    matched.status = 'published'
    matched.publishScope = payload.publishScope
    matched.deadline = payload.deadline
    matched.updatedAt = payload.allowLate ? '刚刚（允许补交）' : '刚刚'
    return CommonUtil.deepClone(matched)
  },
  async listHomeworkSubmissions(homeworkId: string): Promise<HomeworkSubmissionItem[]> {
    await CommonUtil.sleep(90)
    return CommonUtil.deepClone(submissions.filter((item) => item.homeworkId === homeworkId))
  },
  async getSubmissionById(homeworkId: string, submissionId: string): Promise<HomeworkSubmissionItem | null> {
    await CommonUtil.sleep(70)
    const matched = submissions.find((item) => item.homeworkId === homeworkId && item.id === submissionId)
    return matched ? CommonUtil.deepClone(matched) : null
  },
  async reviewSubmission(homeworkId: string, submissionId: string, payload: HomeworkReviewPayload): Promise<HomeworkSubmissionItem | null> {
    await CommonUtil.sleep(120)
    const matched = submissions.find((item) => item.homeworkId === homeworkId && item.id === submissionId)
    if (!matched) {
      return null
    }
    matched.status = 'reviewed'
    matched.score = payload.score
    matched.feedback = payload.feedback
    syncAdminCounts(homeworkId)
    const studentMatched = studentHomeworks.find((item) => item.id === homeworkId)
    if (studentMatched && matched.studentName === '李明') {
      studentMatched.status = 'reviewed'
      studentMatched.submission = {
        ...studentMatched.submission,
        status: 'reviewed',
        score: payload.score,
        teacherFeedback: payload.feedback,
        reviewer: '周老师',
        updatedAt: '刚刚',
        highlights: ['已完成批阅', '可查看得分与反馈']
      }
    }
    return CommonUtil.deepClone(matched)
  }
}
