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
  ExperimentStudentItem,
  ExperimentQuestion,
  AnswerSaveRequest,
  ExperimentReport
} from './types'
import {
  listEduExperimentByPage,
  listEduExperimentVoByPage,
  getEduExperimentVoById
} from '@/api/eduExperimentController'
import {
  listEduExperimentItemByPage,
  listEduExperimentItemVoByPage
} from '@/api/eduExperimentItemController'
import {
  addResExperimentResult,
  updateResExperimentResult
} from '@/api/resExperimentResultController'

// 本地 mock 数据缓存，用于降级展示
let studentExperiments = CommonUtil.deepClone(experimentStudentFixtures)
let adminExperiments = CommonUtil.deepClone(experimentAdminFixtures)
let results = CommonUtil.deepClone(experimentResultFixtures)

// 后端 API 状态标记
let useRealApi = true // 启用真实 API

export const experimentRepository = {
  async listStudentExperiments(): Promise<ExperimentStudentItem[]> {
    try {
      // 优先尝试从后端获取数据
      if (useRealApi) {
        // 直接使用 fetch 调试 API 调用
        const requestData = {
          current: 1,
          pageSize: 50 // 后端限制最大为 50
        }

        console.log('[Repository] 发送 POST 请求到: /api/experiment/eduExperiment/list/page/vo')
        console.log('[Repository] 请求数据:', JSON.stringify(requestData))

        const response = await fetch('/api/experiment/eduExperiment/list/page/vo', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(requestData),
          credentials: 'include'
        })

        console.log('[Repository] 响应状态:', response.status)
        const responseText = await response.text()
        console.log('[Repository] 响应内容:', responseText)

        let backendData
        try {
          backendData = JSON.parse(responseText)
        } catch (e) {
          console.error('[Repository] JSON 解析失败:', e)
        }

        console.log('[Repository] 解析后的数据:', backendData)

        if (backendData?.code === 0) {
          const records = Array.isArray(backendData?.data?.records) ? backendData.data.records : []
          console.log('[Repository] 成功获取数据，共', records.length, '条')

          // 过滤出已发布的实验
          const publishedExperiments = records.filter(
            (item: any) => item.state === 1 || item.publishStatus === 1
          )
          console.log('[Repository] 已发布实验:', publishedExperiments.length, '条')

          return publishedExperiments.map((item: any) => transformToStudentItem(item))
        }

        console.log('[Repository] 后端返回错误:', backendData?.message || '未知错误')
        return []
      }

      return []
    } catch (error: any) {
      console.error('[Repository] 从后端获取失败:', error?.message || error)
      return []
    }
  },

  // 获取实验子项目并转换为步骤格式
  async getExperimentItemsAsSteps(experimentId: string): Promise<ExperimentStep[]> {
    try {
      const requestData = {
        experimentId: parseInt(experimentId),
        current: 1,
        pageSize: 50
      }

      const response = await fetch('/api/experiment/eduExperimentItem/list/page/vo', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData),
        credentials: 'include'
      })

      const responseText = await response.text()
      let backendData
      try {
        backendData = JSON.parse(responseText)
      } catch (e) {
        console.error('[Repository] JSON 解析失败:', e)
        return []
      }

      if (backendData?.code === 0 && backendData?.data?.records) {
        const items = backendData.data.records
        console.log('[Repository] 获取到子项目:', items.length, '条')

        return items.map((item: any, index: number) => ({
          id: `step-${item.id || index}`,
          title: item.itemName || `步骤 ${index + 1}`,
          detail: item.questionContent || item.itemContent || '暂无内容',
          deliverable: '无'
        }))
      }
    } catch (error) {
      console.error('[Repository] 获取子项目失败:', error)
    }
    return []
  },

  async getStudentExperimentById(id: string): Promise<ExperimentStudentItem | null> {
    try {
      if (useRealApi) {
        console.log('[Repository] 发送 GET 请求到: /api/experiment/eduExperiment/get/vo?id=' + id)

        const response = await fetch(`/api/experiment/eduExperiment/get/vo?id=${encodeURIComponent(id)}`, {
          method: 'GET',
          credentials: 'include'
        })

        console.log('[Repository] 响应状态:', response.status)
        const responseText = await response.text()
        console.log('[Repository] 响应内容:', responseText)

        let backendData
        try {
          backendData = JSON.parse(responseText)
        } catch (e) {
          console.error('[Repository] JSON 解析失败:', e)
        }

        if (backendData?.code === 0 && backendData?.data) {
          // 同时获取子项目作为步骤
          const steps = await this.getExperimentItemsAsSteps(id)
          const experiment = transformToStudentItem(backendData.data)
          // 如果后端没有 steps，则使用子项目
          if ((!experiment.steps || experiment.steps.length === 0) && steps.length > 0) {
            experiment.steps = steps
          }
          return experiment
        } else if (backendData?.code !== 0) {
          console.log('[Repository] 后端返回错误:', backendData?.message || '未知错误')
        }
      }

      await CommonUtil.sleep(70)
      const matched = studentExperiments.find((item) => item.id === id)
      return matched ? CommonUtil.deepClone(matched) : null
    } catch (error: any) {
      console.error('[Repository] 从后端获取详情失败:', error?.message || error)
      await CommonUtil.sleep(70)
      const matched = studentExperiments.find((item) => item.id === id)
      return matched ? CommonUtil.deepClone(matched) : null
    }
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
  },

  async getExperimentQuestions(experimentId: string): Promise<ExperimentQuestion[]> {
    try {
      // 使用 fetch 直接调用，与 getExperimentItemsAsSteps 保持一致
      const requestData = {
        experimentId: parseInt(experimentId),
        current: 1,
        pageSize: 100
      }

      console.log('[Repository] 获取题目，发送请求到: /api/experiment/eduExperimentItem/list/page/vo')
      console.log('[Repository] 请求参数:', requestData)

      const response = await fetch('/api/experiment/eduExperimentItem/list/page/vo', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData),
        credentials: 'include'
      })

      const responseText = await response.text()
      console.log('[Repository] 获取题目响应:', responseText)

      let backendData
      try {
        backendData = JSON.parse(responseText)
      } catch (e) {
        console.error('[Repository] JSON 解析失败:', e)
        return []
      }

      // 检查响应结构
      if (backendData?.code === 0) {
        const records = backendData?.data?.records || backendData?.data || []
        console.log('[Repository] 题目记录数:', records.length)

        if (records.length > 0) {
          const questions = records.map((item: any) => {
            // 从 questionContent 中解析选项内容（格式：题目内容 + \n\n + A. xxx\nB. xxx\n...）
            let content = item.questionContent || item.itemContent || item.content || ''
            let options: Array<{ key: string; label: string }> | undefined = undefined

            // 尝试从内容中解析选项
            const optionsMatch = content.match(/\n\n([A-Z]\.\s*.+(?:\n[A-Z]\.\s*.+)*)$/)
            if (optionsMatch) {
              // 分离题目内容和选项
              content = content.substring(0, optionsMatch.index)
              const optionLines = optionsMatch[1].split('\n')
              options = optionLines
                .map((line: string) => {
                  const match = line.match(/^([A-Z])[.、:：]\s*(.+)$/)
                  if (match) {
                    return { key: match[1], label: match[2].trim() }
                  }
                  return null
                })
                .filter(Boolean) as Array<{ key: string; label: string }>
            }

            // 如果 optionsText 存在，也尝试合并
            if (item.optionsText && !options) {
              options = parseOptionsText(item.optionsText)
            }

            return {
              id: String(item.id),
              experimentItemId: String(item.experimentId || item.id),
              title: item.itemName || item.name || '未命名题目',
              type: item.questionType || item.itemType || 1,
              content: content,
              options: options,
              score: item.maxScore || item.itemScore || item.score || 10,
              allowPaste: item.allowPaste !== false,
              language: item.language || 'text',
              standardAnswer: item.standardAnswer || ''
            }
          })
          console.log('[Repository] 转换后的题目:', questions)
          return questions
        }
      } else {
        console.log('[Repository] 获取题目失败:', backendData?.message || '未知错误')
      }
      return []
    } catch (error) {
      console.error('获取实验题目失败:', error)
      return []
    }
  },

  async saveAnswers(request: AnswerSaveRequest): Promise<boolean> {
    try {
      // 优先从 eduhub.auth.session 获取用户信息
      let studentId = 1
      try {
        const sessionStr = localStorage.getItem('eduhub.auth.session')
        if (sessionStr) {
          const session = JSON.parse(sessionStr)
          studentId = session.id || session.userId || 1
        }
      } catch (e) {
        console.error('获取 session 失败:', e)
      }

      // 为每个题目单独保存一条记录
      for (const answer of request.answers) {
        const resultPayload = {
          studentId: studentId,
          itemId: parseInt(answer.questionId),
          subContent: answer.answer,
          gradingStatus: request.isSubmit ? 1 : 0,
          fillTime: new Date() // 重要：设置 fillTime 避免数据库约束错误
        }

        console.log('[Repository] 保存答案:', resultPayload)
        await addResExperimentResult(resultPayload as any)
      }

      return true
    } catch (error) {
      console.error('保存答题记录失败:', error)
      return false
    }
  },

  async submitAnswers(request: AnswerSaveRequest): Promise<boolean> {
    try {
      // 优先从 eduhub.auth.session 获取用户信息
      let studentId = 1
      try {
        const sessionStr = localStorage.getItem('eduhub.auth.session')
        if (sessionStr) {
          const session = JSON.parse(sessionStr)
          studentId = session.id || session.userId || 1
        }
      } catch (e) {
        console.error('获取 session 失败:', e)
      }

      // 为每个题目单独保存一条记录，标记为已提交
      for (const answer of request.answers) {
        const resultPayload = {
          studentId: studentId,
          itemId: parseInt(answer.questionId),
          subContent: answer.answer,
          gradingStatus: 1, // 已提交
          fillTime: new Date() // 重要：设置 fillTime 避免数据库约束错误
        }

        console.log('[Repository] 提交答案:', resultPayload)
        await addResExperimentResult(resultPayload as any)
      }

      return true
    } catch (error) {
      console.error('提交答题记录失败:', error)
      return false
    }
  },

  async getSavedAnswers(experimentId: string): Promise<Map<string, { questionId: string; answer: string; filledBlanks?: string[] }>> {
    const answersMap = new Map<string, { questionId: string; answer: string; filledBlanks?: string[] }>()
    try {
      const localDraft = localStorage.getItem(`experiment-draft-${experimentId}`)
      if (localDraft) {
        const draft = JSON.parse(localDraft)
        draft.answers?.forEach((a: { questionId: string; answer: string; filledBlanks?: string[] }) => {
          answersMap.set(a.questionId, a)
        })
      }
    } catch (error) {
      console.error('获取已保存答题记录失败:', error)
    }
    return answersMap
  },

  // ==================== 实验报告相关 ====================

  /**
   * 获取学生的实验报告
   */
  async getStudentReport(experimentId: string, studentId: string): Promise<ExperimentReport | null> {
    try {
      console.log('[Repository] 获取实验报告:', { experimentId, studentId })

      const response = await fetch(
        `/api/experiment/report/get?experimentId=${encodeURIComponent(experimentId)}&studentId=${encodeURIComponent(studentId)}`,
        {
          method: 'GET',
          credentials: 'include'
        }
      )

      const responseText = await response.text()
      console.log('[Repository] 报告响应:', responseText)

      let backendData
      try {
        backendData = JSON.parse(responseText)
      } catch (e) {
        console.error('[Repository] JSON 解析失败:', e)
        return null
      }

      if (backendData?.code === 0 && backendData?.data) {
        return transformToExperimentReport(backendData.data)
      }

      console.log('[Repository] 获取报告失败:', backendData?.message || '未知错误')
      return null
    } catch (error) {
      console.error('获取实验报告失败:', error)
      return null
    }
  },

  /**
   * 提交实验报告
   */
  async submitReport(request: {
    experimentId: string
    studentId: string
    summaryNote?: string
    answers: Array<{ itemId: string; answer: string; filledBlanks?: string[] }>
  }): Promise<boolean> {
    try {
      const payload = {
        experimentId: parseInt(request.experimentId),
        studentId: parseInt(request.studentId),
        summaryNote: request.summaryNote || '',
        answers: request.answers.map(a => ({
          itemId: parseInt(a.itemId),
          answer: a.answer,
          filledBlanks: a.filledBlanks
        }))
      }

      console.log('[Repository] 提交报告:', payload)

      // 打印每个答案的详细信息，便于调试
      payload.answers.forEach((a: any, index: number) => {
        console.log(`[Repository] 答案 ${index + 1}: itemId=${a.itemId}, answer=${a.answer ? a.answer.substring(0, Math.min(50, a.answer.length)) : 'null'}`)
      })

      const response = await fetch('/api/experiment/report/submit', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload),
        credentials: 'include'
      })

      const responseText = await response.text()
      let backendData
      try {
        backendData = JSON.parse(responseText)
      } catch (e) {
        console.error('[Repository] JSON 解析失败:', e)
        return false
      }

      if (backendData?.code === 0) {
        console.log('[Repository] 提交成功')
        return true
      }

      console.log('[Repository] 提交失败:', backendData?.message || '未知错误')
      return false
    } catch (error) {
      console.error('提交实验报告失败:', error)
      return false
    }
  },

  /**
   * 保存实验报告草稿
   */
  async saveReportDraft(request: {
    experimentId: string
    studentId: string
    summaryNote?: string
    answers: Array<{ itemId: string; answer: string; filledBlanks?: string[] }>
  }): Promise<boolean> {
    try {
      const payload = {
        experimentId: parseInt(request.experimentId),
        studentId: parseInt(request.studentId),
        summaryNote: request.summaryNote || '',
        answers: request.answers.map(a => ({
          itemId: parseInt(a.itemId),
          answer: a.answer,
          filledBlanks: a.filledBlanks
        }))
      }

      console.log('[Repository] 保存草稿:', payload)

      const response = await fetch('/api/experiment/report/save', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload),
        credentials: 'include'
      })

      const responseText = await response.text()
      let backendData
      try {
        backendData = JSON.parse(responseText)
      } catch (e) {
        console.error('[Repository] JSON 解析失败:', e)
        return false
      }

      return backendData?.code === 0
    } catch (error) {
      console.error('保存实验报告草稿失败:', error)
      return false
    }
  },

  /**
   * 获取学生的报告列表
   */
  async getStudentReportList(studentId: string): Promise<ExperimentReport[]> {
    try {
      console.log('[Repository] 获取学生报告列表:', studentId)

      const response = await fetch(
        `/api/experiment/report/list/student?studentId=${encodeURIComponent(studentId)}`,
        {
          method: 'GET',
          credentials: 'include'
        }
      )

      const responseText = await response.text()
      let backendData
      try {
        backendData = JSON.parse(responseText)
      } catch (e) {
        console.error('[Repository] JSON 解析失败:', e)
        return []
      }

      if (backendData?.code === 0 && Array.isArray(backendData?.data)) {
        return backendData.data.map((item: any) => transformToExperimentReport(item))
      }

      return []
    } catch (error) {
      console.error('获取学生报告列表失败:', error)
      return []
    }
  }
}

function parseOptionsText(optionsText: string): Array<{ key: string; label: string }> {
  if (!optionsText) return []
  try {
    return JSON.parse(optionsText)
  } catch {
    const options: Array<{ key: string; label: string }> = []
    const lines = optionsText.split(/[;\n]/)
    lines.forEach((line, index) => {
      const match = line.match(/^([A-Z])[.、:：]\s*(.+)$/)
      if (match) {
        options.push({ key: match[1], label: match[2].trim() })
      } else if (line.trim()) {
        options.push({ key: String.fromCharCode(65 + index), label: line.trim() })
      }
    })
    return options
  }
}

/**
 * 将后端 EduExperimentVO 转换为前端 ExperimentStudentItem 格式
 */
function transformToStudentItem(vo: any): ExperimentStudentItem {
  // 根据发布状态判断实验状态
  let status: 'pending' | 'in_progress' | 'completed' | 'reviewed' = 'pending'
  if (vo.publishStatus === 1) {
    status = 'in_progress'
  }

  // 根据 categoryName 映射主题标签
  const topicLabels: Record<string, string> = {
    1: '编程实践',
    2: '设计实现',
    3: '数据库',
    4: '前端开发',
    5: '框架学习',
    6: '综合实验'
  }

  return {
    id: String(vo.id),
    title: vo.name || '未命名实验',
    topicLabel: vo.categoryName || topicLabels[vo.categoryId] || '待定',
    teacher: vo.teacherName || vo.teacher || '待定教师',
    status: status,
    schedule: vo.schedule || formatSchedule(vo.createdAt),
    summary: vo.requirement || vo.contentDesc || '暂无实验描述',
    objective: vo.contentDesc || vo.requirement || '暂无实验目标',
    tags: vo.tags || [vo.categoryName || '实验'],
    materials: vo.materials || [],
    steps: vo.steps || [],
    work: {
      status: status === 'in_progress' ? 'pending' : status,
      startedAt: vo.createdAt ? formatDate(vo.createdAt) : '待开始',
      updatedAt: vo.updatedAt ? formatDate(vo.updatedAt) : '待更新',
      note: getWorkNote(status),
      highlights: getHighlights(status)
    }
  }
}

/**
 * 格式化日期
 */
function formatDate(date: string | Date): string {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getMonth() + 1}月${d.getDate()}日 ${d.getHours()}:${String(d.getMinutes()).padStart(2, '0')}`
}

/**
 * 格式化日程安排
 */
function formatSchedule(date: string | Date | undefined): string {
  if (!date) return '待定'
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} 截止`
}

/**
 * 根据状态获取工作说明
 */
function getWorkNote(status: string): string {
  switch (status) {
    case 'pending': return '实验尚未开始'
    case 'in_progress': return '实验进行中'
    case 'completed': return '已完成，等待批阅'
    case 'reviewed': return '已批阅完成'
    default: return '未知状态'
  }
}

/**
 * 根据状态获取高亮信息
 */
function getHighlights(status: string): string[] {
  switch (status) {
    case 'pending': return ['未开始']
    case 'in_progress': return ['进行中']
    case 'completed': return ['已完成', '等待批阅']
    case 'reviewed': return ['已批阅']
    default: return []
  }
}

/**
 * 将后端报告数据转换为前端 ExperimentReport 格式
 */
function transformToExperimentReport(data: any): ExperimentReport {
  return {
    student: {
      id: String(data.studentId || ''),
      no: data.studentNo || '',
      name: data.studentName || '',
      clazzNo: data.clazzNo || ''
    },
    experiment: {
      id: String(data.experimentId || ''),
      name: data.experimentName || '',
      courseName: data.courseName || '',
      schedule: data.schedule || ''
    },
    summary: data.teacherScore || '',
    objective: data.objective || '',
    content: data.content || '',
    steps: (data.questions || []).map((q: any, index: number) => ({
      id: q.id || `q-${index}`,
      experimentItemId: q.experimentItemId || q.id || '',
      stepNo: q.stepNo || index + 1,
      title: q.title || '',
      type: q.type || 1,
      content: q.content || '',
      score: q.score || 0,
      options: Array.isArray(q.options) ? q.options : (q.options ? parseOptionsText(q.options) : undefined),
      correctAnswer: q.correctAnswer || q.standardAnswer || '',
      studentAnswer: q.studentAnswer || '',
      filledBlanks: q.filledBlanks ? q.filledBlanks.split(',') : undefined,
      teacherScore: q.teacherScore,
      teacherComment: q.teacherComment
    })),
    summaryNote: data.summaryNote || '',
    teacherFeedback: data.teacherFeedback || '',
    submittedAt: data.submittedAt || '',
    reviewedAt: data.reviewedAt || '',
    status: data.status || 'pending'
  }
}
