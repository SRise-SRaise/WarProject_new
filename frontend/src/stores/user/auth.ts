import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { getLoginUser1, login, register } from '@/api/authController'
import { getAuthStudentVoById } from '@/api/authStudentController'
import { CommonUtil } from '@/utils'
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
    lastLoginIp: principal.lastLoginIp || fallback?.lastLoginIp || '',
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

function extractPayload(value: unknown): Record<string, unknown> | null {
  if (!isRecord(value)) {
    return null
  }

  if ('data' in value && isRecord(value.data)) {
    return value.data
  }

  return value
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

async function tryApiLogin(credentials: AuthCredentials): Promise<UserSession | null> {
  try {
    const role: UserRole = credentials.role ?? (credentials.account.toLowerCase().includes('teacher') ? 'teacher' : 'student')
    const roleType = role === 'teacher' ? '教师' : '学生'
    const response = await login({