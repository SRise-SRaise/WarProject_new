export type UserRole = 'student' | 'teacher'

export interface NotificationPreference {
  key: string
  label: string
  enabled: boolean
}

export interface UserSession {
  id: string
  account: string
  name: string
  role: UserRole
  title: string
  department: string
  major?: string
  classCode?: string
  lastLoginIp?: string
  email: string
  phone: string
  location: string
  signature: string
  avatar: string
  lastLogin: string
  focusAreas: string[]
  preferences: NotificationPreference[]
}

export interface RegisteredAccount {
  id: string
  account: string
  password: string
  role: UserRole
  name: string
}

export interface AuthCredentials {
  account: string
  password: string
  role?: UserRole
}

export interface StudentRosterItem {
  id: string
  name: string
  account: string
  className: string
  major: string
  progress: number
  pendingTasks: number
  status: 'active' | 'attention' | 'inactive'
  email: string
  lastActive: string
}

export interface TeacherRosterItem {
  id: string
  name: string
  account: string
  topicLabel: string
  classCoverage: number
  openTasks: number
  status: 'active' | 'support' | 'leave'
  contact: string
}

export interface ClassRecord {
  id: string
  name: string
  major: string
  term: string
  advisor: string
  studentCount: number
  completionRate: number
  focus: string
  monitor: string
}
