/**
 * 数据转换器工厂（Factory Method 模式）
 *
 * <p>集中管理所有后端 VO → 前端模型的转换逻辑，包括：
 * <ul>
 *   <li>EduExperimentVO → ExperimentStudentItem</li>
 *   <li>EduExperimentVO → ExperimentAdminItem</li>
 *   <li>后端报告数据 → ExperimentReport</li>
 *   <li>后端 Question 记录 → ExperimentQuestion</li>
 *   <li>后端 QuestionItem 记录 → ExperimentQuestion</li>
 * </ul>
 * 同时提供日期格式化、工作状态描述等辅助工具方法。</p>
 */
import type {
  ExperimentStudentItem,
  ExperimentAdminItem,
  ExperimentQuestion,
  ExperimentReport,
  ExperimentStep,
  QuestionOption,
  ProgrammingLanguage
} from './types'

// ==================== 日期工具 ====================

export class DateFormatterFactory {
  /**
   * 格式化日期为"月日 时:分"格式
   * 示例：2024-03-15T10:30:00 → "3月15日 10:30"
   */
  formatDate(date: string | Date | undefined): string {
    if (!date) return ''
    const d = new Date(date)
    return `${d.getMonth() + 1}月${d.getDate()}日 ${d.getHours()}:${String(d.getMinutes()).padStart(2, '0')}`
  }

  /**
   * 格式化日程安排为"yyyy-MM-dd 截止"格式
   * 示例：2024-03-15T10:30:00 → "2024-03-15 截止"
   */
  formatSchedule(date: string | Date | undefined): string {
    if (!date) return '待定'
    const d = new Date(date)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} 截止`
  }
}

export const dateFormatter = new DateFormatterFactory()

// ==================== 状态标签工厂 ====================

export class StatusLabelFactory {
  /**
   * 根据学生实验状态获取工作说明文字
   */
  getWorkNote(status: string): string {
    switch (status) {
      case 'pending': return '实验尚未开始'
      case 'in_progress': return '实验进行中'
      case 'completed': return '已完成，等待批阅'
      case 'reviewed': return '已批阅完成'
      default: return '未知状态'
    }
  }

  /**
   * 根据学生实验状态获取高亮标签列表
   */
  getHighlights(status: string): string[] {
    switch (status) {
      case 'pending': return ['未开始']
      case 'in_progress': return ['进行中']
      case 'completed': return ['已完成', '等待批阅']
      case 'reviewed': return ['已批阅']
      default: return []
    }
  }

  /**
   * 主题标签映射（categoryId → 显示文本）
   */
  getTopicLabel(categoryId: number | string | undefined): string {
    const labels: Record<string, string> = {
      1: '编程实践', 2: '设计实现', 3: '数据库',
      4: '前端开发', 5: '框架学习', 6: '综合实验'
    }
    return labels[String(categoryId)] ?? '待定'
  }
}

export const statusLabelFactory = new StatusLabelFactory()

// ==================== 转换器工厂 ====================

export class DataTransformerFactory {
  /**
   * EduExperimentVO → ExperimentStudentItem（学生端视图）
   */
  toStudentItem(vo: any): ExperimentStudentItem {
    const publishStatus = vo.publishStatus ?? vo.state
    const status = publishStatus === 1 ? 'in_progress' : 'pending'

    return {
      id: String(vo.id),
      title: vo.name || '未命名实验',
      topicLabel: vo.categoryName || statusLabelFactory.getTopicLabel(vo.categoryId),
      teacher: vo.teacherName || vo.teacher || '待定教师',
      status,
      schedule: vo.schedule || dateFormatter.formatSchedule(vo.createdAt),
      summary: vo.requirement || vo.contentDesc || '暂无实验描述',
      objective: vo.contentDesc || vo.requirement || '暂无实验目标',
      tags: vo.tags || [vo.categoryName || '实验'],
      materials: this.parseMaterials(vo.materials, vo.instructionUrl),
      steps: this.parseSteps(vo.steps),
      work: {
        status: status === 'in_progress' ? 'pending' : status,
        startedAt: dateFormatter.formatDate(vo.createdAt) || '待开始',
        updatedAt: dateFormatter.formatDate(vo.updatedAt) || '待更新',
        note: statusLabelFactory.getWorkNote(status),
        highlights: statusLabelFactory.getHighlights(status)
      }
    }
  }

  /**
   * EduExperimentVO[] → ExperimentStudentItem[]（批量）
   */
  toStudentItems(records: any[]): ExperimentStudentItem[] {
    if (!Array.isArray(records)) return []
    return records.map((vo) => this.toStudentItem(vo))
  }

  /**
   * EduExperimentVO → ExperimentAdminItem（教师端视图）
   */
  toAdminItem(vo: any): ExperimentAdminItem {
    const statusMap: Record<number, string> = {
      0: 'draft',
      1: 'published',
      2: 'running',
      3: 'closed'
    }
    return {
      id: String(vo.id),
      sortOrder: vo.sortOrder,
      title: vo.name || '未命名实验',
      topicLabel: vo.categoryName || '待定',
      status: (statusMap[vo.publishStatus] ?? 'draft') as any,
      summary: vo.requirement || vo.contentDesc || '暂无描述',
      schedule: vo.createdAt ? dateFormatter.formatSchedule(vo.createdAt) : '待定',
      scope: vo.scope || '待设置',
      updatedAt: dateFormatter.formatDate(vo.updatedAt) || '未知',
      itemCount: vo.itemCount ?? 0,
      resultCount: vo.resultCount ?? 0,
      tags: [vo.categoryName || '实验'],
      classCodes: vo.classCodes,
      classNames: vo.classNames,
      classCount: vo.classCount ?? 0
    }
  }

  /**
   * EduExperimentVO[] → ExperimentAdminItem[]（批量）
   */
  toAdminItems(records: any[]): ExperimentAdminItem[] {
    if (!Array.isArray(records)) return []
    return records.map((vo) => this.toAdminItem(vo))
  }

  /**
   * 后端报告记录 → ExperimentReport
   */
  toExperimentReport(data: any): ExperimentReport {
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
      steps: this.toReportQuestions(data.questions),
      summaryNote: data.summaryNote || '',
      teacherFeedback: data.teacherFeedback || '',
      submittedAt: data.submittedAt || '',
      reviewedAt: data.reviewedAt || '',
      status: data.status || 'pending'
    }
  }

  /**
   * 后端报告记录[] → ExperimentReport[]（批量）
   */
  toExperimentReports(records: any[]): ExperimentReport[] {
    if (!Array.isArray(records)) return []
    return records.map((r) => this.toExperimentReport(r))
  }

  /**
   * 后端 QuestionItem 记录 → ExperimentQuestion
   * 用于从 eduExperimentItem 接口获取题目
   */
  toQuestionFromItem(item: any): ExperimentQuestion {
    let content = item.questionContent || item.itemContent || item.content || ''
    let options: QuestionOption[] | undefined = undefined

    // 尝试从 content 中解析选项（格式：题目内容 + \n\n + A. xxx\nB. xxx\n...）
    const optionsMatch = content.match(/\n\n([A-Z]\.\s*.+(?:\n[A-Z]\.\s*.+)*)$/)
    if (optionsMatch) {
      content = content.substring(0, optionsMatch.index)
      const optionLines = optionsMatch[1].split('\n')
      options = optionLines
        .map((line: string) => {
          const match = line.match(/^([A-Z])[.、:：]\s*(.+)$/)
          return match ? { key: match[1], label: match[2].trim() } : null
        })
        .filter(Boolean) as QuestionOption[]
    }

    // 备用：从 optionsText 解析
    if (!options && item.optionsText) {
      options = this.parseOptionsText(item.optionsText)
    }

    // 编程题：从 content 末尾识别 [LANG:xxx] 并剥离，或根据题目标题/内容自动推断语言
    const type = item.questionType || item.itemType || 1
    let language: ProgrammingLanguage = 'text'
    if (type === 3) {
      const langMatch = content.match(/\[LANG:(\w+)\]\s*$/)
      if (langMatch) {
        language = langMatch[1] as ProgrammingLanguage
        content = content.substring(0, langMatch.index!).trimEnd()
      } else if (item.language) {
        language = item.language as ProgrammingLanguage
      } else {
        // 智能语言检测：根据题目标题或内容推断编程语言
        language = this.detectProgrammingLanguage(item.itemName || item.name || '', content)
      }
    }

    return {
      id: String(item.id),
      experimentItemId: String(item.experimentId || item.id),
      title: item.itemName || item.name || '未命名题目',
      type,
      content,
      options,
      correctAnswer: item.standardAnswer || '',
      score: item.maxScore || item.itemScore || item.score || 10,
      allowPaste: item.allowPaste !== false,
      language
    }
  }

  /**
   * 后端 QuestionItem 记录[] → ExperimentQuestion[]（批量）
   */
  toQuestionsFromItems(items: any[]): ExperimentQuestion[] {
    if (!Array.isArray(items)) return []
    return items.map((item) => this.toQuestionFromItem(item))
  }

  // ==================== 内部辅助方法 ====================

  /**
   * 根据题目标题和内容智能检测编程语言
   */
  private detectProgrammingLanguage(title: string, content: string): ProgrammingLanguage {
    const text = (title + ' ' + content).toLowerCase()
    
    // 优先匹配明确的语言关键字（标题中）
    const titleLower = title.toLowerCase()
    if (/\bjava\b/.test(titleLower) && !/\bjavascript\b/.test(titleLower)) return 'java'
    if (/\bpython\b/.test(titleLower)) return 'python'
    if (/\bsql\b/.test(titleLower)) return 'sql'
    if (/\bjavascript\b|\bjs\b/.test(titleLower)) return 'javascript'
    if (/\bhtml\b/.test(titleLower)) return 'html'
    if (/\bcss\b/.test(titleLower)) return 'css'
    if (/\bjsp\b/.test(titleLower)) return 'jsp'
    
    // 根据代码特征检测
    // Java 特征：public class, public static void main, System.out, import java.
    if (/public\s+class\s+\w+|public\s+static\s+void\s+main|System\.out\.|import\s+java\./.test(content)) {
      return 'java'
    }
    
    // Python 特征：def xxx():, import xxx, print(, if __name__
    if (/def\s+\w+\s*\(|^import\s+\w+|print\s*\(|if\s+__name__\s*==/.test(content)) {
      return 'python'
    }
    
    // SQL 特征：SELECT, INSERT, UPDATE, DELETE, CREATE TABLE
    if (/\b(SELECT|INSERT|UPDATE|DELETE|CREATE\s+TABLE|ALTER\s+TABLE|DROP\s+TABLE)\b/i.test(content)) {
      return 'sql'
    }
    
    // JavaScript 特征：function, const/let/var, console.log, =>
    if (/function\s+\w+|const\s+\w+\s*=|let\s+\w+\s*=|console\.log|=>\s*{/.test(content)) {
      return 'javascript'
    }
    
    // HTML 特征：<html>, <head>, <body>, <!DOCTYPE
    if (/<html|<head|<body|<!DOCTYPE/i.test(content)) {
      return 'html'
    }
    
    // CSS 特征：选择器 { 属性: 值 }
    if (/\w+\s*{\s*[\w-]+\s*:\s*[^}]+}/.test(content)) {
      return 'css'
    }
    
    // JSP 特征：<%@, <%=, <jsp:
    if (/<%[@=]|<jsp:/.test(content)) {
      return 'jsp'
    }
    
    // 内容关键字匹配（较宽松）
    if (/\bjava\b/.test(text) && !/\bjavascript\b/.test(text)) return 'java'
    if (/\bpython\b/.test(text)) return 'python'
    if (/\bsql\b|\bmysql\b|\boracle\b/.test(text)) return 'sql'
    
    return 'text'
  }

  private parseMaterials(materials: any, instructionUrl?: string): ExperimentStudentItem['materials'] {
    const result: ExperimentStudentItem['materials'] = []
    
    // 如果有 instructionUrl，作为指导书添加到材料列表
    if (instructionUrl && typeof instructionUrl === 'string' && instructionUrl.trim()) {
      const url = instructionUrl.trim()
      // 从 URL 中提取文件名
      const fileName = url.split('/').pop() || '实验指导书'
      // 根据扩展名判断类型
      const ext = fileName.split('.').pop()?.toLowerCase() || ''
      const kindMap: Record<string, string> = {
        'pdf': 'PDF文档',
        'doc': 'Word文档',
        'docx': 'Word文档',
        'ppt': 'PPT',
        'pptx': 'PPT',
        'xls': 'Excel',
        'xlsx': 'Excel',
        'zip': '压缩包',
        'rar': '压缩包',
        'txt': '文本文件'
      }
      result.push({
        name: decodeURIComponent(fileName),
        size: '—',
        kind: kindMap[ext] || '指导书',
        url: url
      })
    }
    
    // 合并已有的 materials 数组
    if (Array.isArray(materials)) {
      result.push(...materials)
    }
    
    return result
  }

  private parseSteps(steps: any): ExperimentStep[] {
    if (Array.isArray(steps)) return steps
    return []
  }

  private toReportQuestions(questions: any[]): ExperimentReport['steps'] {
    if (!Array.isArray(questions)) return []
    return questions.map((q, index) => {
      let content = q.content || ''
      let language: ProgrammingLanguage | undefined = undefined

      // 编程题：从 content 末尾识别 [LANG:xxx] 并剥离
      if (q.type === 3) {
        const langMatch = content.match(/\[LANG:(\w+)\]\s*$/)
        if (langMatch) {
          language = langMatch[1] as ProgrammingLanguage
          content = content.substring(0, langMatch.index!).trimEnd()
        }
      }

      return {
        id: q.id || `q-${index}`,
        experimentItemId: q.experimentItemId || q.id || '',
        stepNo: q.stepNo || index + 1,
        title: q.title || '',
        type: q.type || 1,
        content,
        score: q.score || 0,
        options: this.parseOptions(q.options),
        correctAnswer: q.correctAnswer || q.standardAnswer || '',
        studentAnswer: q.studentAnswer || '',
        filledBlanks: q.filledBlanks ? (Array.isArray(q.filledBlanks) ? q.filledBlanks : q.filledBlanks.split(',')) : undefined,
        teacherScore: q.teacherScore,
        teacherComment: q.teacherComment,
        language
      }
    })
  }

  private parseOptions(options: any): QuestionOption[] | undefined {
    if (Array.isArray(options)) return options
    if (typeof options === 'string' && options) {
      return this.parseOptionsText(options)
    }
    return undefined
  }

  /**
   * 解析选项文本（JSON 格式或换行分隔格式）
   */
  parseOptionsText(optionsText: string): QuestionOption[] {
    if (!optionsText) return []
    try {
      return JSON.parse(optionsText)
    } catch {
      const options: QuestionOption[] = []
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
}

export const dataTransformerFactory = new DataTransformerFactory()

/** 兼容旧代码的别名 */
export const transformToStudentItem = (vo: any) => dataTransformerFactory.toStudentItem(vo)
export const transformToAdminItem = (vo: any) => dataTransformerFactory.toAdminItem(vo)
export const transformToExperimentReport = (data: any) => dataTransformerFactory.toExperimentReport(data)
