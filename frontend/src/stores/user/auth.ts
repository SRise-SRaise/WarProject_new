import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { postAuthLogin, postAuthRegister } from '@/api/auth'
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
    className: typeof value.className === 'string' ? value.className : base.className,
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
    const response = await postAuthLogin({
      account: credentials.account,
      password: credentials.password
    })
    const data: unknown = response.data
    const payload = extractPayload(data)
    if (!payload) {
      return null
    }

    const role = inferRoleFromPayload(payload, credentials.account)
    const matchedAccount = mergeAccounts().find((item) => item.account === credentials.account)
    const account = matchedAccount ?? {
      id: CommonUtil.generateId(role),
      account: credentials.account,
      password: credentials.password,
      role,
      name: inferNameFromPayload(payload, credentials.account, role)
    }
    return buildSessionFromAccount(account)
  } catch {
    return null
  }
}

async function tryApiRegister(credentials: AuthCredentials): Promise<void> {
  try {
    await postAuthRegister({
      account: credentials.account,
      password: credentials.password
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
      if (apiSession) {
        session.value = apiSession
        writeStoredSession(apiSession)
        return apiSession
      }

      const matchedAccount = mergeAccounts().find(
        (account) => account.account === credentials.account && account.password === credentials.password
      )
      if (!matchedAccount) {
        throw new Error('账号或密码不正确，请使用示例账号或已注册账号登录。')
      }

      const nextSession = buildSessionFromAccount(matchedAccount)
      session.value = nextSession
      writeStoredSession(nextSession)
      return nextSession
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
    session.value = null
    writeStoredSession(null)
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
    updateProfile
  }
})
