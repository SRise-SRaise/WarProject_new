import type { RouteRecordRaw } from 'vue-router'
import BasicLayout from '@/layouts/BasicLayout.vue'
import { BarChartOutlined, ContainerOutlined, FileSearchOutlined, ScheduleOutlined } from '@ant-design/icons-vue'
import type { MenuGroup } from './meta'

const ExamListView = () => import('@/views/exam/student/ListView.vue')
const ExamConfirmView = () => import('@/views/exam/student/ConfirmView.vue')
const ExamTakeView = () => import('@/views/exam/student/TakeView.vue')
const ExamResultView = () => import('@/views/exam/student/ResultView.vue')
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
      { path: '', name: 'StudentExamList', component: ExamListView, meta: { title: '考试列表', requiresAuth: true, audience: 'student', shell: 'student', navLabel: '考试', summary: '查看待参加、已完成和即将开放的考试。' } },
      { path: ':id/confirm', name: 'StudentExamConfirm', component: ExamConfirmView, meta: { title: '考试确认', requiresAuth: true, audience: 'student', shell: 'student', summary: '确认考试规则、题目数量和共享题库说明。' } },
      { path: ':id/take', name: 'StudentExamTake', component: ExamTakeView, meta: { title: '开始答题', requiresAuth: true, audience: 'student', shell: 'student', summary: '在统一答题页中完成本场考试。' } },
      { path: ':id/result', name: 'StudentExamResult', component: ExamResultView, meta: { title: '考试结果', requiresAuth: true, audience: 'student', shell: 'student', summary: '查看成绩摘要、复盘建议和共享题库说明。' } },
    ],
  },
]

export const adminExamRoutes: RouteRecordRaw[] = [
  { path: 'question-bank', name: 'AdminQuestionBank', component: AdminQuestionBankView, meta: { title: '题库与题型管理', icon: FileSearchOutlined, group: examGroup, order: 50, requiresAuth: true, audience: 'admin', summary: '在同一页面统一维护共享题库资产与题型结构。' } },
  { path: 'question-types', name: 'AdminQuestionTypes', redirect: { name: 'AdminQuestionBank' }, meta: { title: '题型管理', icon: FileSearchOutlined, group: examGroup, order: 51, requiresAuth: true, audience: 'admin', summary: '题型管理已合并到题库与题型管理页面。', hideInMenu: true } },
  { path: 'papers', name: 'AdminPaperList', component: AdminPaperListView, meta: { title: '试卷管理', icon: ContainerOutlined, group: examGroup, order: 52, requiresAuth: true, audience: 'admin', summary: '围绕共享题库组织试卷草稿与待发布卷。' } },
  { path: 'exams', name: 'AdminExamList', component: AdminExamListView, meta: { title: '考试管理', icon: ScheduleOutlined, group: examGroup, order: 53, requiresAuth: true, audience: 'admin', summary: '查看考试安排、状态、参与人数与提交进度。' } },
  { path: 'exams/records/:id', name: 'AdminExamRecords', component: AdminExamRecordsView, meta: { title: '考试记录', icon: ScheduleOutlined, group: examGroup, order: 54, requiresAuth: true, audience: 'admin', summary: '查看单场考试的学生记录与风险提示。', hideInMenu: true } },
  { path: 'scores/summary', name: 'AdminScoreSummary', component: AdminScoreSummaryView, meta: { title: '成绩汇总', icon: BarChartOutlined, group: examGroup, order: 55, requiresAuth: true, audience: 'admin', summary: '从共享题库、提交记录和平均分角度查看考试结果。' } },
]

export default examRoutes
