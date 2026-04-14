export interface MetricItem {
  label: string
  value: string
  description: string
  trend?: string
  tone?: 'primary' | 'accent' | 'success' | 'warning'
}

export interface PublicFeature {
  title: string
  description: string
  tags: string[]
}

export interface NoticeItem {
  title: string
  detail: string
  time: string
}

export interface TimelineItem {
  title: string
  description: string
}

export interface LearningTask {
  id: string
  moduleLabel: string
  title: string
  topicLabel: string
  dueLabel: string
  status: 'urgent' | 'processing' | 'planned'
  progress: string
  available: boolean
  path?: string
}

export interface LearningResult {
  id: string
  title: string
  moduleLabel: string
  score: string
  feedback: string
  updatedAt: string
}

export interface QuickLinkItem {
  title: string
  description: string
  badge?: string
  available: boolean
  path?: string
}

export interface PublicSnapshot {
  heroMetrics: MetricItem[]
  highlights: PublicFeature[]
  timeline: TimelineItem[]
  notices: NoticeItem[]
}

export interface StudentOverviewData {
  headline: string
  summary: string
  metrics: MetricItem[]
  tasks: LearningTask[]
  results: LearningResult[]
  notices: NoticeItem[]
  quickLinks: QuickLinkItem[]
}

export interface MaterialAttachment {
  name: string
  size: string
  kind: string
}

export interface MaterialSection {
  heading: string
  content: string
}

export interface MaterialItem {
  id: string
  title: string
  topicLabel: string
  type: '讲义' | '案例' | '录播' | '实验指导'
  level: '基础' | '进阶' | '专题'
  summary: string
  teacher: string
  updatedAt: string
  audience: string
  tags: string[]
  downloads: number
  duration: string
  coverNote: string
  sections: MaterialSection[]
  attachments: MaterialAttachment[]
  availability: 'open' | 'limited'
}

export interface TeacherQueueItem {
  id: string
  title: string
  meta: string
  status: 'urgent' | 'processing' | 'planned'
  path?: string
  linkLabel: string
}

export interface ScheduleItem {
  id: string
  time: string
  title: string
  detail: string
}

export interface TeacherDashboardData {
  headline: string
  summary: string
  metrics: MetricItem[]
  queue: TeacherQueueItem[]
  updates: NoticeItem[]
  schedule: ScheduleItem[]
  quickLinks: QuickLinkItem[]
}

export interface MaterialFilters {
  keyword: string
  topicLabel: string
  type: string
}

export interface SelectOption {
  label: string
  value: string
}
