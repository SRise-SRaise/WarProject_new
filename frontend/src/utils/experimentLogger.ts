/**
 * 实验操作日志记录工具
 * 用于记录学生在实验过程中的操作行为，支持风险检测分析
 */

// 操作类型常量（与后端保持一致）
export const ACTION_TYPES = {
  ENTER_EXPERIMENT: 100,      // 进入实验
  SWITCH_QUESTION: 101,       // 切换题目
  SAVE_ANSWER: 102,           // 保存答案
  SUBMIT_EXPERIMENT: 103,     // 提交实验
  LEAVE_PAGE: 104,            // 离开页面
  RETURN_PAGE: 105,           // 返回页面
  COPY: 106,                  // 复制操作
  PASTE: 107,                 // 粘贴操作
  FOCUS_LOST: 108             // 失去焦点
} as const

export type ActionType = typeof ACTION_TYPES[keyof typeof ACTION_TYPES]

interface LogEntry {
  account: string
  studentName?: string
  actionType: ActionType
  experimentId: string
  experimentTitle?: string
  clazzNo?: string
  questionId?: string
  questionName?: string
  timestamp: number
  extra?: Record<string, unknown>
}

// 日志缓冲区（用于批量上传）
let logBuffer: LogEntry[] = []
let flushTimer: ReturnType<typeof setTimeout> | null = null
const FLUSH_INTERVAL = 5000 // 5秒自动上传一次
const MAX_BUFFER_SIZE = 20  // 缓冲区最大条数

// 当前用户信息缓存
let currentUserInfo: { account: string; studentName: string; clazzNo: string } | null = null

/**
 * 设置当前用户信息
 */
export function setUserInfo(account: string, studentName: string, clazzNo: string) {
  currentUserInfo = { account, studentName, clazzNo }
}

/**
 * 记录操作日志
 */
export function logAction(
  actionType: ActionType,
  experimentId: string,
  options: {
    experimentTitle?: string
    questionId?: string
    questionName?: string
    extra?: Record<string, unknown>
  } = {}
) {
  if (!currentUserInfo) {
    console.warn('[ExperimentLogger] 用户信息未设置，无法记录日志')
    return
  }

  const entry: LogEntry = {
    account: currentUserInfo.account,
    studentName: currentUserInfo.studentName,
    actionType,
    experimentId,
    experimentTitle: options.experimentTitle,
    clazzNo: currentUserInfo.clazzNo,
    questionId: options.questionId,
    questionName: options.questionName,
    timestamp: Date.now(),
    extra: options.extra
  }

  logBuffer.push(entry)

  // 如果缓冲区已满，立即上传
  if (logBuffer.length >= MAX_BUFFER_SIZE) {
    flushLogs()
  } else {
    // 否则启动定时上传
    scheduleFlush()
  }
}

/**
 * 调度定时上传
 */
function scheduleFlush() {
  if (flushTimer) return
  flushTimer = setTimeout(() => {
    flushTimer = null
    flushLogs()
  }, FLUSH_INTERVAL)
}

/**
 * 立即上传日志
 */
export async function flushLogs(): Promise<void> {
  if (logBuffer.length === 0) return

  const logsToSend = [...logBuffer]
  logBuffer = []

  if (flushTimer) {
    clearTimeout(flushTimer)
    flushTimer = null
  }

  try {
    const response = await fetch('/api/experiment/log/record/batch', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(logsToSend),
      credentials: 'include'
    })

    if (!response.ok) {
      // 上传失败，将日志放回缓冲区
      logBuffer = [...logsToSend, ...logBuffer]
      console.error('[ExperimentLogger] 日志上传失败:', response.statusText)
    }
  } catch (error) {
    // 网络错误，将日志放回缓冲区
    logBuffer = [...logsToSend, ...logBuffer]
    console.error('[ExperimentLogger] 日志上传异常:', error)
  }
}

/**
 * 记录进入实验
 */
export function logEnterExperiment(experimentId: string, experimentTitle?: string) {
  logAction(ACTION_TYPES.ENTER_EXPERIMENT, experimentId, { experimentTitle })
}

/**
 * 记录切换题目
 */
export function logSwitchQuestion(experimentId: string, questionId: string, questionName?: string) {
  logAction(ACTION_TYPES.SWITCH_QUESTION, experimentId, { questionId, questionName })
}

/**
 * 记录保存答案
 */
export function logSaveAnswer(experimentId: string, questionId: string, questionName?: string) {
  logAction(ACTION_TYPES.SAVE_ANSWER, experimentId, { questionId, questionName })
}

/**
 * 记录提交实验
 */
export function logSubmitExperiment(experimentId: string) {
  logAction(ACTION_TYPES.SUBMIT_EXPERIMENT, experimentId)
}

/**
 * 记录离开页面
 */
export function logLeavePage(experimentId: string) {
  logAction(ACTION_TYPES.LEAVE_PAGE, experimentId)
}

/**
 * 记录返回页面
 */
export function logReturnPage(experimentId: string) {
  logAction(ACTION_TYPES.RETURN_PAGE, experimentId)
}

/**
 * 记录复制操作
 */
export function logCopy(experimentId: string, questionId?: string, questionName?: string) {
  logAction(ACTION_TYPES.COPY, experimentId, { questionId, questionName })
}

/**
 * 记录粘贴操作
 */
export function logPaste(experimentId: string, questionId?: string, questionName?: string) {
  logAction(ACTION_TYPES.PASTE, experimentId, { questionId, questionName })
}

/**
 * 记录失去焦点
 */
export function logFocusLost(experimentId: string) {
  logAction(ACTION_TYPES.FOCUS_LOST, experimentId)
}

/**
 * 页面卸载前上传剩余日志（使用 sendBeacon）
 */
export function flushLogsBeforeUnload(): void {
  if (logBuffer.length === 0) return

  const data = JSON.stringify(logBuffer)
  logBuffer = []

  // 使用 sendBeacon 确保页面关闭前数据能发送出去
  const success = navigator.sendBeacon('/api/experiment/log/record/batch', new Blob([data], { type: 'application/json' }))
  if (!success) {
    console.warn('[ExperimentLogger] sendBeacon 发送失败')
  }
}

/**
 * 清理日志记录器
 */
export function cleanupLogger() {
  if (flushTimer) {
    clearTimeout(flushTimer)
    flushTimer = null
  }
  logBuffer = []
  currentUserInfo = null
}
