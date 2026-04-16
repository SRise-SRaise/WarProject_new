import type { RouteRecordRaw } from 'vue-router'
import BasicLayout from '@/layouts/BasicLayout.vue'
import { BarChartOutlined, ContainerOutlined, FileSearchOutlined, ScheduleOutlined } from '@ant-design/icons-vue'
import type { MenuGroup } from './meta'

const ExamListView = () => import('@/views/exam/student/ListView.vue')
const ExamTakeView = () => import('@/views/exam/student/TakeView.vue')
const AdminQuestionBankView = () => import('@/views/exam/admin/QuestionBankView.vue')
const AdminPaperListView = () => import('@/views/exam/admin/PaperListView.vue')
const AdminExamListView = () => import('@/views/exam/admin/ExamListView.vue')
const AdminExamRecordsView = () => import('@/views/exam/admin/ExamRecordsView.vue')
const AdminScoreSummaryView = () => import('@/views/exam/admin/ScoreSummaryView.vue')

const examGroup: MenuGroup = { key: 'exam', title: '考试与题库', icon: ScheduleOutlined }

const examRoutes: RouteRecordRaw[] = [
  {
    path: '/exams',
    component: BasicLayout,
    children: [
      { path: '', name: 'StudentExamList', component: ExamListView, meta: { title: '考试列表', requiresAuth: true, audience: 'student', shell: 'student', navLabel: '考试', summary: '查看可参加的考试，点击进入答题。' } },
      { path: ':id/take', name: 'StudentExamTake', component: ExamTakeView, meta: { title: '开始答题', requiresAuth: true, audience: 'student', shell: 'student', summary: '在统一答题页中完成本场考试，支持倒计时和答题卡。' } },
    ],
  },
]

export const adminExamRoutes: RouteRecordRaw[] = [
  { path: 'question-bank', name: 'AdminQuestionBank', component: AdminQuestionBankView, meta: { title: '题库管理', icon: FileSearchOutlined, group: examGroup, order: 50, requiresAuth: true, audience: 'admin', summary: '集中维护所有题目资源，支持多种题型的增删改查操作。' } },
  { path: 'papers', name: 'AdminPaperList', component: AdminPaperListView, meta: { title: '试卷管理', icon: ContainerOutlined, group: examGroup, order: 52, requiresAuth: true, audience: 'admin', summary: '围绕共享题库组织试卷草稿与待发布卷。' } },
  { path: 'exams', name: 'AdminExamList', component: AdminExamListView, meta: { title: '考试管理', icon: ScheduleOutlined, group: examGroup, order: 53, requiresAuth: true, audience: 'admin', summary: '查看考试安排、状态、参与人数与提交进度。' } },
  { path: 'exams/records/:id', name: 'AdminExamRecords', component: AdminExamRecordsView, meta: { title: '考试记录', icon: ScheduleOutlined, group: examGroup, order: 54, requiresAuth: true, audience: 'admin', summary: '查看单场考试的学生记录与风险提示。', hideInMenu: true } },
  { path: 'scores/summary', name: 'AdminScoreSummary', component: AdminScoreSummaryView, meta: { title: '成绩分析与批改', icon: BarChartOutlined, group: examGroup, order: 55, requiresAuth: true, audience: 'admin', summary: '查看考试成绩统计，批改简答题和编程题。' } },
]

export default examRoutes
