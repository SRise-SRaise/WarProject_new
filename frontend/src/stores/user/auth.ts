import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { getLoginUser1, login, logout as apiLogout, register } from '@/api/authController'
import { getAuthStudentVoById } from '@/api/authStudentController'
import { CommonUtil } from '@/utils'
import {
  buildDisplayName,
  buildSessionFromAccount,
  defaultUserAccounts
} from './fixtures'
import type {
  AuthCredentials,
  NotificationPreference,
  RegisteredAccount,
  UserRole,
  UserSession
} from './types'

const SESSION_KEY = 'eduhub.auth.session'
const ACCOUNTS_KEY = 'eduhub.auth.accounts'

const studentPreferences: NotificationPreference[] = [
  { key: 'notice-material', label: '资料更新提醒', enabled: true },
  { key: 'notice-deadline', label: '截止时间提醒', enabled: true },
  { key: 'notice-feedback', label: '反馈结果提醒', enabled: true }
]

const teacherPreferences: NotificationPreference[] = [
  { key: 'notice-class', label: '班级风险提醒', enabled: true },
  { key: 'notice-material', label: '资料发布提醒', enabled: true },
  { key: 'notice-collab', label: '教研协同提醒', enabled: false }
]

const defaultUserAccounts: RegisteredAccount[] = [
  {
    id: 'student-001',
    account: 'student',
    password: '123456',
    role: 'student',
    name: '学生用户'
  },
  {
    id: 'teacher-001',
    account: 'teacher',
    password: '123456',
    role: 'teacher',
    name: '教师用户'
  }
]

function buildAvatar(name: string): string {
  return name.slice(0, 1).toUpperCase()
}

function buildDisplayName(account: string, role: UserRole): string {
  const suffix = account.slice(-4)
  return role === 'teacher' ? `教师 ${suffix}` : `同学 ${suffix}`
}

function buildSessionFromAccount(account: RegisteredAccount): UserSession {
  const now = new Date().toLocaleString('zh-CN', { hour12: false })
  if (account.role === 'teacher') {
    return {
      id: account.id,
      account: account.account,
      name: account.name,
      role: 'teacher',
      title: '教师',
      department: '',
      classCode: '',
      lastLoginIp: '',
      email: '',
      phone: '',
      location: '',
      signature: '',
      avatar: buildAvatar(account.name),
      lastLogin: now,
      focusAreas: [],
      preferences: teacherPreferences.map((item) => ({ ...item }))
    }
  }

  return {
    id: account.id,
    account: account.account,
    name: account.name,
    role: 'student',
    title: '学生',
    department: '',
    major: '',
    classCode: '',
    className: '',
    lastLoginIp: '',
    email: '',
    phone: '',
    location: '',
    signature: '',
    avatar: buildAvatar(account.name),
    lastLogin: now,
    focusAreas: [],
    preferences: studentPreferences.map((item) => ({ ...item }))
  }
}

function buildSessionFromPrincipal(
  principal: {
    userId?: number
    roleType?: string
    loginAccount?: string
    displayName?: string
    major?: string
    classCode?: string
    lastLoginIp?: string
  },
  fallback?: UserSession | null
): UserSession {
  const roleRaw = (principal.roleType || '').toLowerCase()
  const role: UserRole = roleRaw.includes('teacher') || principal.roleType === '教师' ? 'teacher' : 'student'
  const now = new Date().toLocaleString('zh-CN', { hour12: false })
  const baseName = principal.displayName || fallback?.name || (role === 'teacher' ? '教师用户' : '学生用户')
  return {
    id: principal.userId ? String(principal.userId) : (fallback?.id ?? CommonUtil.generateId(role)),
    account: principal.loginAccount || fallback?.account || '',
    name: baseName,
    role,
    title: role === 'teacher' ? '教师' : '学生',
    department: fallback?.department || '',
    major: principal.major || fallback?.major || '',
    classCode: principal.classCode || fallback?.classCode || '',
    className: principal.classCode || fallback?.className || fallback?.classCode || '',
    // IP 必须以后端返回（数据库值）为准，不使用本地旧缓存回退
    lastLoginIp: principal.lastLoginIp ?? '',
    email: fallback?.email || '',
    phone: fallback?.phone || '',
    location: fallback?.location || '',
    signature: fallback?.signature || '',
    avatar: fallback?.avatar || buildAvatar(baseName),
    lastLogin: now,
    focusAreas: fallback?.focusAreas || [],
    preferences:
      fallback?.preferences || (role === 'teacher'
        ? teacherPreferences.map((item) => ({ ...item }))
        : studentPreferences.map((item) => ({ ...item }))
      )
  }
}

function isBrowser(): boolean {
  return typeof window !== 'undefined'
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null
}

function parseRole(value: unknown): UserRole | null {
  if (value === 'teacher' || value === 'admin') {
    return 'teacher'
  }

  if (value === 'student') {
    return 'student'
  }

  return null
}

function parseStringArray(value: unknown): string[] {
  if (!Array.isArray(value)) {
    return []
  }

  return value.filter((item): item is string => typeof item === 'string')
}

function parsePreferences(value: unknown): NotificationPreference[] {
  if (!Array.isArray(value)) {
    return []
  }

  return value.flatMap((item) => {
    if (!isRecord(item) || typeof item.key !== 'string' || typeof item.label !== 'string') {
      return []
    }

    return [{ key: item.key, label: item.label, enabled: item.enabled === true }]
  })
}

function parseStoredAccount(value: unknown): RegisteredAccount | null {
  if (!isRecord(value)) {
    return null
  }

  const role = parseRole(value.role)
  if (!role || typeof value.account !== 'string' || typeof value.password !== 'string' || typeof value.name !== 'string') {
    return null
  }

  return {
    id: typeof value.id === 'string' ? value.id : CommonUtil.generateId(role),
    account: value.account,
    password: value.password,
    role,
    name: value.name
  }
}

function readStoredAccounts(): RegisteredAccount[] {
  if (!isBrowser()) {
    return []
  }

  const raw = window.localStorage.getItem(ACCOUNTS_KEY)
  if (!raw) {
    return []
  }

  try {
    const parsed: unknown = JSON.parse(raw)
    if (!Array.isArray(parsed)) {
      return []
    }
    return parsed.flatMap((item) => {
      const account = parseStoredAccount(item)
      return account ? [account] : []
    })
  } catch {
    return []
  }
}

function writeStoredAccounts(accounts: RegisteredAccount[]): void {
  if (!isBrowser()) {
    return
  }

  window.localStorage.setItem(ACCOUNTS_KEY, JSON.stringify(accounts))
}

function mergeAccounts(): RegisteredAccount[] {
  const merged = new Map<string, RegisteredAccount>()
  for (const account of defaultUserAccounts) {
    merged.set(account.account, account)
  }
  for (const account of readStoredAccounts()) {
    merged.set(account.account, account)
  }
  return [...merged.values()]
}

function inferRoleFromPayload(payload: Record<string, unknown>, account: string): UserRole {
  const candidates = [payload.role, payload.userRole, payload.identity, payload.accountType]
  for (const candidate of candidates) {
    const role = parseRole(candidate)
    if (role) {
      return role
    }
  }

  return account.toLowerCase().includes('teacher') ? 'teacher' : 'student'
}

function inferNameFromPayload(payload: Record<string, unknown>, account: string, role: UserRole): string {
  const candidates = [payload.name, payload.userName, payload.username, payload.displayName]
  for (const candidate of candidates) {
    if (typeof candidate === 'string' && candidate.trim().length > 0) {
      return candidate
    }
  }

  return buildDisplayName(account, role)
}

function parseSession(value: unknown): UserSession | null {
  if (!isRecord(value) || typeof value.account !== 'string' || typeof value.name !== 'string') {
    return null
  }

  const role = parseRole(value.role)
  if (!role) {
    return null
  }

  const base = buildSessionFromAccount({
    id: typeof value.id === 'string' ? value.id : CommonUtil.generateId(role),
    account: value.account,
    password: '',
    role,
    name: value.name
  })

  const preferences = parsePreferences(value.preferences)
  const focusAreas = parseStringArray(value.focusAreas)

  return {
    ...base,
    title: typeof value.title === 'string' ? value.title : base.title,
    department: typeof value.department === 'string' ? value.department : base.department,
    major: typeof value.major === 'string' ? value.major : base.major,
    classCode: typeof value.classCode === 'string' ? value.classCode : base.classCode,
    lastLoginIp: typeof value.lastLoginIp === 'string' ? value.lastLoginIp : base.lastLoginIp,
    email: typeof value.email === 'string' ? value.email : base.email,
    phone: typeof value.phone === 'string' ? value.phone : base.phone,
    location: typeof value.location === 'string' ? value.location : base.location,
    signature: typeof value.signature === 'string' ? value.signature : base.signature,
    avatar: typeof value.avatar === 'string' ? value.avatar : base.avatar,
    lastLogin: typeof value.lastLogin === 'string' ? value.lastLogin : base.lastLogin,
    focusAreas: focusAreas.length > 0 ? focusAreas : base.focusAreas,
    preferences: preferences.length > 0 ? preferences : base.preferences
  }
}

function readStoredSession(): UserSession | null {
  if (!isBrowser()) {
    return null
  }

  const raw = window.localStorage.getItem(SESSION_KEY)
  if (!raw) {
    return null
  }

  try {
    const parsed: unknown = JSON.parse(raw)
    return parseSession(parsed)
  } catch {
    return null
  }
}

function writeStoredSession(session: UserSession | null): void {
  if (!isBrowser()) {
    return
  }

  if (!session) {
    window.localStorage.removeItem(SESSION_KEY)
    return
  }

  window.localStorage.setItem(SESSION_KEY, JSON.stringify(session))
}

function getErrorMessage(error: unknown): string {
  const fallbackMessage = '登录失败：账号或密码错误，或后端会话未建立。'
  let message = ''
  if (error instanceof Error && error.message.trim().length > 0) {
    message = error.message.trim()
  }
  if (isRecord(error)) {
    const maybeResponse = error.response
    if (isRecord(maybeResponse)) {
      const maybeData = maybeResponse.data
      if (isRecord(maybeData) && typeof maybeData.message === 'string' && maybeData.message.trim().length > 0) {
        message = maybeData.message.trim()
      }
    }
  }
  const isSameAccountMultiLogin =
    message.includes('该账号已在其他浏览器登录')
    || ((message.includes('账号') || message.includes('同一账号')) && message.includes('登录') && message.includes('其他浏览器'))
    || (message.includes('账号') && message.includes('已登录') && (message.includes('其他端') || message.includes('其他设备')))
  if (isSameAccountMultiLogin) {
    return message
  }
  return fallbackMessage
}

async function tryApiLogin(credentials: AuthCredentials): Promise<UserSession | null> {
  try {
    const role: UserRole = credentials.role ?? (credentials.account.toLowerCase().includes('teacher') ? 'teacher' : 'student')
    const roleType = role === 'teacher' ? '教师' : '学生'
    const doLogin = () => login({
      roleType,
      loginAccount: credentials.account,
      loginPassword: credentials.password
    })
    let response = await doLogin()
    const payload = response.data?.data?.loginPrincipal
    if (!payload) {
      return null
    }
    const payloadUserId = typeof payload.userId === 'number'
      ? payload.userId
      : (typeof payload.userId === 'string' ? Number(payload.userId) : undefined)

    const inferPayload = payload as unknown as Record<string, unknown>
    const parsedRole = inferRoleFromPayload(inferPayload, credentials.account)
    const matchedAccount = mergeAccounts().find((item) => item.account === credentials.account)
    const account = matchedAccount ?? {
      id: payloadUserId && !Number.isNaN(payloadUserId) ? String(payloadUserId) : CommonUtil.generateId(parsedRole),
      account: credentials.account,
      password: credentials.password,
      role: parsedRole,
      name: payload.displayName || inferNameFromPayload(inferPayload, credentials.account, parsedRole)
    }
    const baseSession = buildSessionFromAccount(account)
    const session = buildSessionFromPrincipal(
      {
        userId: payloadUserId && !Number.isNaN(payloadUserId) ? payloadUserId : undefined,
        roleType: payload.roleType,
        loginAccount: payload.loginAccount,
        displayName: payload.displayName,
        major: payload.major,
        classCode: payload.classCode,
        lastLoginIp: (payload as { lastLoginIp?: string }).lastLoginIp
      },
      baseSession
    )
    if (payloadUserId && !Number.isNaN(payloadUserId)) {
      session.id = String(payloadUserId)
    }
    return session
  } catch (error) {
    const messageText = getErrorMessage(error)
    const isSameAccountMultiLogin =
      messageText.includes('该账号已在其他浏览器登录')
      || ((messageText.includes('账号') || messageText.includes('同一账号')) && messageText.includes('登录') && messageText.includes('其他浏览器'))
      || (messageText.includes('账号') && messageText.includes('已登录') && (messageText.includes('其他端') || messageText.includes('其他设备')))
    if (isSameAccountMultiLogin) {
      try {
        // 尝试清理同浏览器残留的后端会话（若已退出/无会话，该请求会失败但可忽略）
        await apiLogout()
        const role: UserRole = credentials.role ?? (credentials.account.toLowerCase().includes('teacher') ? 'teacher' : 'student')
        const roleType = role === 'teacher' ? '教师' : '学生'
        const retry = await login({
          roleType,
          loginAccount: credentials.account,
          loginPassword: credentials.password
        })
        const payload = retry.data?.data?.loginPrincipal
        if (!payload) {
          throw new Error(messageText)
        }
        const payloadUserId = typeof payload.userId === 'number'
          ? payload.userId
          : (typeof payload.userId === 'string' ? Number(payload.userId) : undefined)
        const inferPayload = payload as unknown as Record<string, unknown>
        const parsedRole = inferRoleFromPayload(inferPayload, credentials.account)
        const matchedAccount = mergeAccounts().find((item) => item.account === credentials.account)
        const account = matchedAccount ?? {
          id: payloadUserId && !Number.isNaN(payloadUserId) ? String(payloadUserId) : CommonUtil.generateId(parsedRole),
          account: credentials.account,
          password: credentials.password,
          role: parsedRole,
          name: payload.displayName || inferNameFromPayload(inferPayload, credentials.account, parsedRole)
        }
        const baseSession = buildSessionFromAccount(account)
        const session = buildSessionFromPrincipal(
          {
            userId: payloadUserId && !Number.isNaN(payloadUserId) ? payloadUserId : undefined,
            roleType: payload.roleType,
            loginAccount: payload.loginAccount,
            displayName: payload.displayName,
            major: payload.major,
            classCode: payload.classCode,
            lastLoginIp: (payload as { lastLoginIp?: string }).lastLoginIp
          },
          baseSession
        )
        if (payloadUserId && !Number.isNaN(payloadUserId)) {
          session.id = String(payloadUserId)
        }
        return session
      } catch {
        throw new Error(messageText)
      }
    }
    throw new Error(messageText)
  }
}

async function tryApiRegister(credentials: AuthCredentials): Promise<void> {
  try {
    await register({
      roleType: '学生',
      loginAccount: credentials.account,
      displayName: buildDisplayName(credentials.account, 'student'),
      classCode: '',
      loginPassword: credentials.password,
      checkPassword: credentials.password
    })
  } catch {
    return
  }
}

export const useAuthStore = defineStore('user-auth', () => {
  const session = ref<UserSession | null>(readStoredSession())
  const loading = ref(false)

  const isAuthenticated = computed(() => session.value !== null)
  const isStudent = computed(() => session.value?.role === 'student')
  const isTeacher = computed(() => session.value?.role === 'teacher')
  const role = computed<UserRole | null>(() => session.value?.role ?? null)
  const defaultRoute = computed(() => (session.value?.role === 'teacher' ? '/admin/dashboard' : '/learning'))

  function restoreSession(): void {
    session.value = readStoredSession()
  }

  async function login(credentials: AuthCredentials): Promise<UserSession> {
    loading.value = true
    try {
      const apiSession = await tryApiLogin(credentials)
      if (!apiSession) {
        throw new Error('登录失败：账号或密码错误，或后端会话未建立。')
      }

      session.value = apiSession
      writeStoredSession(apiSession)
      return apiSession
    } finally {
      loading.value = false
    }
  }

  async function register(credentials: AuthCredentials): Promise<void> {
    loading.value = true
    try {
      if (mergeAccounts().some((account) => account.account === credentials.account)) {
        throw new Error('该账号已存在，请直接登录。')
      }

      await tryApiRegister(credentials)

      const nextAccount: RegisteredAccount = {
        id: CommonUtil.generateId('student'),
        account: credentials.account,
        password: credentials.password,
        role: 'student',
        name: buildDisplayName(credentials.account, 'student')
      }

      writeStoredAccounts([...readStoredAccounts(), nextAccount])
    } finally {
      loading.value = false
    }
  }

  function logout(): void {
    // 尽力清理后端会话，避免同浏览器再次登录被“单点登录”拦截
    void apiLogout().catch(() => null)
    session.value = null
    writeStoredSession(null)
  }

  function setSession(nextSession: UserSession): void {
    session.value = nextSession
    writeStoredSession(nextSession)
  }

  function updateProfile(patch: Partial<UserSession>): void {
    if (!session.value) {
      return
    }

    session.value = {
      ...session.value,
      ...patch,
      focusAreas: patch.focusAreas ?? session.value.focusAreas,
      preferences: patch.preferences ?? session.value.preferences
    }
    writeStoredSession(session.value)
  }

  async function refreshSessionFromServer(): Promise<void> {
    if (!session.value) {
      return
    }
    try {
      const response = await getLoginUser1()
      const data = response.data?.data as
        | ({
            userId?: number
            roleType?: string
            loginAccount?: string
            displayName?: string
            major?: string
            classCode?: string
            lastLoginIp?: string
          } & { loginPrincipal?: {
            userId?: number
            roleType?: string
            loginAccount?: string
            displayName?: string
            major?: string
            classCode?: string
            lastLoginIp?: string
          } })
        | undefined
      const principal = data?.loginPrincipal ?? data
      if (!principal) {
        return
      }
      const nextSession = buildSessionFromPrincipal(
        principal as {
          userId?: number
          roleType?: string
          loginAccount?: string
          displayName?: string
          major?: string
          classCode?: string
          lastLoginIp?: string
        },
        session.value
      )
      if (!nextSession.classCode && nextSession.role === 'student') {
        const studentId = Number(nextSession.id)
        if (!Number.isNaN(studentId) && studentId > 0) {
          const studentResp = await getAuthStudentVoById({ id: String(studentId) })
          const studentVo = studentResp.data?.data
          if (studentVo?.classCode) {
            nextSession.classCode = studentVo.classCode
            nextSession.className = studentVo.classCode
          }
          if (studentVo?.remark && !nextSession.major) {
            nextSession.major = studentVo.remark
          }
        }
      }
      session.value = nextSession
      writeStoredSession(nextSession)
    } catch {
      // Ignore refresh errors and keep local session.
    }
  }

  return {
    session,
    loading,
    role,
    isAuthenticated,
    isStudent,
    isTeacher,
    defaultRoute,
    restoreSession,
    login,
    register,
    logout,
    setSession,
    updateProfile,
    refreshSessionFromServer
  }
})
