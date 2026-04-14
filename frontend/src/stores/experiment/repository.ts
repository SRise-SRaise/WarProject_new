import { CommonUtil } from '@/utils'
import {
  experimentAdminFixtures,
  experimentResultFixtures,
  experimentStudentFixtures
} from './fixtures'
import type {
  ExperimentAdminItem,
  ExperimentEditPayload,
  ExperimentResultItem,
  ExperimentResultPayload,
  ExperimentStudentItem
} from './types'

let studentExperiments = CommonUtil.deepClone(experimentStudentFixtures)
let adminExperiments = CommonUtil.deepClone(experimentAdminFixtures)
let results = CommonUtil.deepClone(experimentResultFixtures)

export const experimentRepository = {
  async listStudentExperiments(): Promise<ExperimentStudentItem[]> {
    await CommonUtil.sleep(90)
    return CommonUtil.deepClone(studentExperiments)
  },
  async getStudentExperimentById(id: string): Promise<ExperimentStudentItem | null> {
    await CommonUtil.sleep(70)
    const matched = studentExperiments.find((item) => item.id === id)
    return matched ? CommonUtil.deepClone(matched) : null
  },
  async saveStudentWork(id: string, note: string, reportName: string): Promise<ExperimentStudentItem | null> {
    await CommonUtil.sleep(110)
    const matched = studentExperiments.find((item) => item.id === id)
    if (!matched) return null
    matched.status = 'completed'
    matched.work = {
      ...matched.work,
      status: 'submitted',
      updatedAt: '刚刚',
      note,
      reportName,
      highlights: ['实验结果已提交', '等待教师处理']
    }
    const existing = results.find((item) => item.experimentId === id && item.studentName === '李明')
    if (existing) {
      existing.status = 'submitted'
      existing.submittedAt = '刚刚'
      existing.summary = note
      existing.reportName = reportName
    } else {
      results.unshift({ id: CommonUtil.generateId('exp-result'), experimentId: id, studentName: '李明', className: '前端 2401', status: 'submitted', submittedAt: '刚刚', summary: note, reportName })
    }
    return CommonUtil.deepClone(matched)
  },
  async listAdminExperiments(): Promise<ExperimentAdminItem[]> {
    await CommonUtil.sleep(100)
    return CommonUtil.deepClone(adminExperiments)
  },
  async getAdminExperimentById(id: string): Promise<ExperimentAdminItem | null> {
    await CommonUtil.sleep(80)
    const matched = adminExperiments.find((item) => item.id === id)
    return matched ? CommonUtil.deepClone(matched) : null
  },
  async saveExperiment(payload: ExperimentEditPayload): Promise<ExperimentAdminItem> {
    await CommonUtil.sleep(120)
    if (payload.id) {
      const matched = adminExperiments.find((item) => item.id === payload.id)
      if (matched) {
        matched.title = payload.title
        matched.topicLabel = payload.topicLabel
        matched.summary = payload.summary
        matched.schedule = payload.schedule
        matched.tags = [...payload.tags]
        matched.updatedAt = '刚刚'
        return CommonUtil.deepClone(matched)
      }
    }
    const next: ExperimentAdminItem = {
      id: CommonUtil.generateId('exp'),
      title: payload.title,
      topicLabel: payload.topicLabel,
      status: 'draft',
      summary: payload.summary,
      schedule: payload.schedule,
      scope: '待设置',
      updatedAt: '刚刚',
      itemCount: 3,
      resultCount: 0,
      tags: [...payload.tags]
    }
    adminExperiments.unshift(next)
    return CommonUtil.deepClone(next)
  },
  async listExperimentResults(experimentId: string): Promise<ExperimentResultItem[]> {
    await CommonUtil.sleep(90)
    return CommonUtil.deepClone(results.filter((item) => item.experimentId === experimentId))
  },
  async getExperimentResult(experimentId: string, resultId: string): Promise<ExperimentResultItem | null> {
    await CommonUtil.sleep(70)
    const matched = results.find((item) => item.experimentId === experimentId && item.id === resultId)
    return matched ? CommonUtil.deepClone(matched) : null
  },
  async reviewExperimentResult(experimentId: string, resultId: string, payload: ExperimentResultPayload): Promise<ExperimentResultItem | null> {
    await CommonUtil.sleep(120)
    const matched = results.find((item) => item.experimentId === experimentId && item.id === resultId)
    if (!matched) return null
    matched.status = 'reviewed'
    matched.score = payload.score
    matched.feedback = payload.feedback
    const studentMatched = studentExperiments.find((item) => item.id === experimentId)
    if (studentMatched && matched.studentName === '李明') {
      studentMatched.status = 'reviewed'
      studentMatched.work = {
        ...studentMatched.work,
        status: 'reviewed',
        score: payload.score,
        teacherFeedback: payload.feedback,
        updatedAt: '刚刚',
        highlights: ['实验结果已处理', '可回看教师反馈']
      }
    }
    return CommonUtil.deepClone(matched)
  }
}
