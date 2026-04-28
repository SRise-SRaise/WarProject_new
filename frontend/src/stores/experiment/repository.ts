import { CommonUtil } from '@/utils'
import {
  experimentAdminFixtures,
  experimentResultFixtures,
  experimentStudentFixtures
} from './fixtures'
import {
  dataTransformerFactory
} from './DataTransformerFactory'
import type {
  ExperimentAdminItem,
  ExperimentEditPayload,
  ExperimentResultItem,
  ExperimentResultPayload,
  ExperimentStudentItem,
  ExperimentQuestion,
  AnswerSaveRequest,
  ExperimentReport,
  ExperimentStep
} from './types'
import {
  addResExperimentResult
} from '@/api/resExperimentResultController'

/**
 * 实验模块数据仓库
 *
 * <p>单例模式（标准实现）：通过私有的静态实例字段 + 公开的 getInstance() 方法，
 * 保证全局只有一个 {@code ExperimentRepository} 实例，所有 store 与 view
 * 共享同一份数据缓存。</p>
 *
 * <p>使用时直接引用导出的 {@code experimentRepository} 对象，无需调用 getInstance()。</p>
 */
class ExperimentRepository {
  // ==================== 私有实例字段（全局唯一状态） ====================
  private static instance: ExperimentRepository | null = null

  private readonly studentExperiments = CommonUtil.deepClone(experimentStudentFixtures)
  private readonly adminExperiments = CommonUtil.deepClone(experimentAdminFixtures)
  private readonly results = CommonUtil.deepClone(experimentResultFixtures)
  private useRealApi = true

  private constructor() {}

  static getInstance(): ExperimentRepository {
    if (!ExperimentRepository.instance) {
      ExperimentRepository.instance = new ExperimentRepository()
    }
    return ExperimentRepository.instance
  }

  // ==================== 学生端接口 ====================

  async listStudentExperiments(): Promise<ExperimentStudentItem[]> {
    try {
      // 优先尝试从后端获取数据
      if (this.useRealApi) {
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

          return publishedExperiments.map((item: any) => dataTransformerFactory.toStudentItem(item))
        }

        console.log('[Repository] 后端返回错误:', backendData?.message || '未知错误')
        return []
      }

      return []
    } catch (error: any) {
      console.error('[Repository] 从后端获取失败:', error?.message || error)
      return []
    }
  }

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
  }

  async getStudentExperimentById(id: string): Promise<ExperimentStudentItem | null> {
    try {
      if (this.useRealApi) {
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
          const experiment = dataTransformerFactory.toStudentItem(backendData.data)
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
      const matched = this.studentExperiments.find((item) => item.id === id)
      return matched ? CommonUtil.deepClone(matched) : null
    } catch (error: any) {
      console.error('[Repository] 从后端获取详情失败:', error?.message || error)
      await CommonUtil.sleep(70)
      const matched = this.studentExperiments.find((item) => item.id === id)
      return matched ? CommonUtil.deepClone(matched) : null
    }
  }

  async saveStudentWork(id: string, note: string, reportName: string): Promise<ExperimentStudentItem | null> {
    await CommonUtil.sleep(110)
      const matched = this.studentExperiments.find((item) => item.id === id)
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
    const existing = this.results.find((item) => item.experimentId === id && item.studentName === '李明')
    if (existing) {
      existing.status = 'submitted'
      existing.submittedAt = '刚刚'
      existing.summary = note
      existing.reportName = reportName
    } else {
      this.results.unshift({ id: CommonUtil.generateId('exp-result'), experimentId: id, studentName: '李明', className: '前端 2401', status: 'submitted', submittedAt: '刚刚', summary: note, reportName })
    }
    return CommonUtil.deepClone(matched)
  }

  async listAdminExperiments(): Promise<ExperimentAdminItem[]> {
    try {
      const requestData = {
        current: 1,
        pageSize: 200
      }

      const response = await fetch('/api/experiment/eduExperiment/list/page/vo', {
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

      if (backendData?.code === 0) {
        const records = Array.isArray(backendData?.data?.records) ? backendData.data.records : []
        console.log('[Repository] 教师端获取实验列表:', records.length, '条')
        return records.map((item: any) => dataTransformerFactory.toAdminItem(item))
      }

      console.error('[Repository] 教师端获取实验列表失败:', backendData?.message || '未知错误')
      return []
    } catch (error: any) {
      console.error('[Repository] 教师端获取实验列表失败:', error?.message || error)
      return []
    }
  }

  async getAdminExperimentById(id: string): Promise<ExperimentAdminItem | null> {
    try {
      const response = await fetch(`/api/experiment/eduExperiment/get/vo?id=${encodeURIComponent(id)}`, {
        method: 'GET',
        credentials: 'include'
      })

      const responseText = await response.text()
      let backendData
      try {
        backendData = JSON.parse(responseText)
      } catch (e) {
        console.error('[Repository] JSON 解析失败:', e)
        return null
      }

      if (backendData?.code === 0 && backendData?.data) {
        return dataTransformerFactory.toAdminItem(backendData.data)
      }

      console.error('[Repository] 获取实验详情失败:', backendData?.message || '未知错误')
      return null
    } catch (error: any) {
      console.error('[Repository] 获取实验详情失败:', error?.message || error)
      return null
    }
  }

  async saveExperiment(payload: ExperimentEditPayload): Promise<ExperimentAdminItem> {
    await CommonUtil.sleep(120)
    if (payload.id) {
      const matched = this.adminExperiments.find((item) => item.id === payload.id)
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
    this.adminExperiments.unshift(next)
    return CommonUtil.deepClone(next)
  }

  async listExperimentResults(experimentId: string): Promise<ExperimentResultItem[]> {
    await CommonUtil.sleep(90)
    return CommonUtil.deepClone(this.results.filter((item) => item.experimentId === experimentId))
  }

  async getExperimentResult(experimentId: string, resultId: string): Promise<ExperimentResultItem | null> {
    await CommonUtil.sleep(70)
    const matched = this.results.find((item) => item.experimentId === experimentId && item.id === resultId)
    return matched ? CommonUtil.deepClone(matched) : null
  }

  async reviewExperimentResult(experimentId: string, resultId: string, payload: ExperimentResultPayload): Promise<ExperimentResultItem | null> {
    await CommonUtil.sleep(120)
    const matched = this.results.find((item) => item.experimentId === experimentId && item.id === resultId)
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
              options = dataTransformerFactory.parseOptionsText(item.optionsText)
            }

            // 编程题：从 content 末尾识别 [LANG:xxx] 并剥离
            let language: string = 'text'
            if (item.questionType === 3 || item.itemType === 3) {
              const langMatch = content.match(/\[LANG:(\w+)\]\s*$/)
              if (langMatch) {
                language = langMatch[1]
                content = content.substring(0, langMatch.index!).trimEnd()
              } else if (item.language) {
                language = item.language
              }
            } else if (item.language) {
              language = item.language
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
              language: language,
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
  }

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
  }

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
  }

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
  }

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
        return dataTransformerFactory.toExperimentReport(backendData.data)
      }

      console.log('[Repository] 获取报告失败:', backendData?.message || '未知错误')
      return null
    } catch (error) {
      console.error('获取实验报告失败:', error)
      return null
    }
  }

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
  }

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
  }

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
        return backendData.data.map((item: any) => dataTransformerFactory.toExperimentReport(item))
      }

      return []
    } catch (error) {
      console.error('获取学生报告列表失败:', error)
      return []
    }
  }

  // ==================== 学生实验数据分析 ====================

  /**
   * 获取学生实验数据分析
   */
  async getStudentExperimentAnalysis(studentId: string): Promise<any> {
    try {
      console.log('[Repository] 获取学生实验数据分析:', studentId)

      const response = await fetch(
        `/api/experiment/analysis/student?studentId=${encodeURIComponent(studentId)}`,
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
        return null
      }

      if (backendData?.code === 0 && backendData?.data) {
        console.log('[Repository] 获取数据分析成功:', backendData.data)
        return backendData.data
      }

      console.log('[Repository] 获取数据分析失败:', backendData?.message || '未知错误')
      return null
    } catch (error) {
      console.error('获取学生实验数据分析失败:', error)
      return null
    }
  }

  // ==================== 教师实验数据分析 ====================

  /**
   * 获取指定实验关联的班级列表
   * @param experimentId 实验ID
   */
  async getExperimentClasses(experimentId: string): Promise<string[]> {
    try {
      const response = await fetch(
        `/api/experiment/eduExperiment/classes?experimentId=${encodeURIComponent(experimentId)}`,
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
        console.log('[Repository] 获取实验班级列表:', backendData.data)
        return backendData.data
      }

      console.log('[Repository] 获取班级列表失败:', backendData?.message || '未知错误')
      return []
    } catch (error) {
      console.error('获取实验班级列表失败:', error)
      return []
    }
  }

  /**
   * 获取教师端实验数据分析
   * @param experimentId 可选，传入则返回单个实验分析，不传则返回全局统计
   * @param clazzNo 可选，班级编号
   */
  async getExperimentAnalysis(experimentId?: string, clazzNo?: string): Promise<any> {
    try {
      let url = '/api/experiment/analysis/experiment'
      const params: string[] = []
      if (experimentId) params.push(`experimentId=${encodeURIComponent(experimentId)}`)
      if (clazzNo) params.push(`clazzNo=${encodeURIComponent(clazzNo)}`)
      if (params.length > 0) url += '?' + params.join('&')

      console.log('[Repository] 获取教师实验数据分析:', url)

      const response = await fetch(url, {
        method: 'GET',
        credentials: 'include'
      })

      const responseText = await response.text()
      let backendData
      try {
        backendData = JSON.parse(responseText)
      } catch (e) {
        console.error('[Repository] JSON 解析失败:', e)
        return null
      }

      if (backendData?.code === 0 && backendData?.data) {
        console.log('[Repository] 获取教师数据分析成功:', backendData.data)
        return backendData.data
      }

      console.log('[Repository] 获取教师数据分析失败:', backendData?.message || '未知错误')
      return null
    } catch (error) {
      console.error('获取教师实验数据分析失败:', error)
      return null
    }
  }
}

// ==================== 以下函数已迁移至 DataTransformerFactory ====================
// parseOptionsText / transformToStudentItem / formatDate / formatSchedule
// getWorkNote / getHighlights / transformToAdminItem / transformToExperimentReport
// 如需使用，请从 './DataTransformerFactory' 导入 dataTransformerFactory

/** 单例导出（供外部使用，与原 API 保持兼容） */
export const experimentRepository = ExperimentRepository.getInstance()
