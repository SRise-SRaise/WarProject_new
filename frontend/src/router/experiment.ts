import type { RouteRecordRaw } from 'vue-router'
import BasicLayout from '@/layouts/BasicLayout.vue'
import { ExperimentOutlined, ToolOutlined, UnorderedListOutlined, FileTextOutlined, BarChartOutlined } from '@ant-design/icons-vue'
import type { MenuGroup } from './meta'

const ExperimentListView = () => import('@/views/experiment/student/ListView.vue')
const ExperimentDetailView = () => import('@/views/experiment/student/DetailView.vue')
const ExperimentWorkView = () => import('@/views/experiment/student/WorkView.vue')
const ExperimentResultView = () => import('@/views/experiment/student/ResultView.vue')
const ExperimentAnswerSheetView = () => import('@/views/experiment/student/AnswerSheetView.vue')
const StudentReportListView = () => import('@/views/experiment/student/ReportListView.vue')
const AdminExperimentListView = () => import('@/views/experiment/admin/ListView.vue')
const AdminExperimentEditView = () => import('@/views/experiment/admin/EditView.vue')
const AdminExperimentEditDetailView = () => import('@/views/experiment/admin/ExperimentEditDetailView.vue')
const AdminExperimentItemsView = () => import('@/views/experiment/admin/ItemsView.vue')
const AdminExperimentResultsView = () => import('@/views/experiment/admin/ResultsView.vue')
const AdminExperimentResultReviewView = () => import('@/views/experiment/admin/ResultReviewView.vue')
const AdminExperimentStepsEditorView = () => import('@/views/experiment/admin/StepsEditorView.vue')
const AdminExperimentReportView = () => import('@/views/experiment/admin/TeacherReportView.vue')
const StudentReportView = () => import('@/views/experiment/student/StudentReportView.vue')
const StudentAnalysisView = () => import('@/views/experiment/student/StudentAnalysisView.vue')
const TeacherAnalysisView = () => import('@/views/experiment/admin/TeacherAnalysisView.vue')

const experimentGroup: MenuGroup = { key: 'experiment', title: '实验管理', icon: ExperimentOutlined }

const experimentRoutes: RouteRecordRaw[] = [
  {
    path: '/experiments',
    component: BasicLayout,
    children: [
      { path: '', name: 'StudentExperimentList', component: ExperimentListView, meta: { title: '实验列表', requiresAuth: true, audience: 'student', shell: 'student', navLabel: '实验', summary: '查看实验开放安排和当前状态。' } },
      { path: 'reports', name: 'StudentReportList', component: StudentReportListView, meta: { title: '实验报告', icon: FileTextOutlined, requiresAuth: true, audience: 'student', shell: 'student', navLabel: '实验报告', summary: '查看所有实验的报告和批改结果。' } },
      { path: 'analysis', name: 'StudentExperimentAnalysis', component: StudentAnalysisView, meta: { title: '数据分析', requiresAuth: true, audience: 'student', shell: 'student', summary: '查看个人实验完成情况与各类型实验的得分分布。' } },
      { path: ':id', name: 'StudentExperimentDetail', component: ExperimentDetailView, meta: { title: '实验详情', requiresAuth: true, audience: 'student', shell: 'student', summary: '查看实验目标、步骤和附件资料。' } },
      { path: ':id/answer', name: 'StudentExperimentAnswer', component: ExperimentAnswerSheetView, meta: { title: '进行实验', requiresAuth: true, audience: 'student', shell: 'student', summary: '在 Word 风格页面中完成所有实验题目。' } },
      { path: ':id/work', name: 'StudentExperimentWork', component: ExperimentWorkView, meta: { title: '实验处理', requiresAuth: true, audience: 'student', shell: 'student', summary: '记录实验过程并提交结果。' } },
      { path: ':id/result', name: 'StudentExperimentResult', component: ExperimentResultView, meta: { title: '实验结果', requiresAuth: true, audience: 'student', shell: 'student', summary: '查看实验得分、说明和教师反馈。' } },
      { path: ':id/report', name: 'StudentExperimentReport', component: StudentReportView, meta: { title: '实验报告', requiresAuth: true, audience: 'student', shell: 'student', summary: '查看和打印实验报告。' } }
    ]
  }
]

export const adminExperimentRoutes: RouteRecordRaw[] = [
  // 还原 war-project 的实验列表页面
  {
    path: '/admin/experiments/list',
    name: 'AdminExperimentListFull',
    component: () => import('@/views/experiment/admin/ExperimentListView.vue'),
    meta: { title: '实验列表管理', icon: UnorderedListOutlined, group: experimentGroup, order: 40, requiresAuth: true, audience: 'admin', summary: '维护实验基本信息、步骤内容和评分标准。' }
  },
    {
    path: '/admin/experiments/reports',
    name: 'AdminReportList',
    component: () => import('@/views/experiment/admin/AdminReportListView.vue'),
    meta: { title: '实验报告', icon: FileTextOutlined, group: experimentGroup, order: 46, requiresAuth: true, audience: 'admin', summary: '选择实验和班级，查看学生实验报告并批改。' }
  },
  {
    path: '/admin/experiments/analysis',
    name: 'AdminExperimentAnalysis',
    component: TeacherAnalysisView,
    meta: { title: '数据分析', icon: BarChartOutlined, group: experimentGroup, order: 48, requiresAuth: true, audience: 'admin', summary: '查看单个实验的完成情况、得分分布，或统计所有实验的总体情况。' }
  },
  {
    path: '/admin/experiments/reports/:id/:studentId',
    name: 'AdminReportView',
    component: () => import('@/views/experiment/admin/TeacherReportView.vue'),
    meta: { title: '查看报告', icon: FileTextOutlined, group: experimentGroup, order: 47, requiresAuth: true, audience: 'admin', summary: '查看学生实验报告。', hideInMenu: true }
  },
  {
    path: '/admin/experiments/results/:id/:studentId',
    name: 'AdminExperimentReportGrade',
    component: () => import('@/views/experiment/admin/TeacherReportView.vue'),
    meta: { title: '报告批改', icon: ExperimentOutlined, group: experimentGroup, order: 45, requiresAuth: true, audience: 'admin', summary: '批改学生实验报告。', hideInMenu: true }
  },
  // 还原 war-project 的实验编辑页面
  {
    path: '/admin/experiments/edit',
    name: 'AdminExperimentEditFull',
    component: AdminExperimentEditDetailView,
    meta: { title: '新建实验', icon: ToolOutlined, group: experimentGroup, order: 41, requiresAuth: true, audience: 'admin', summary: '创建新实验并维护基本信息。', hideInMenu: true }
  },
  {
    path: '/admin/experiments/edit/:id',
    name: 'AdminExperimentEditDetail',
    component: AdminExperimentEditDetailView,
    meta: { title: '编辑实验', icon: ToolOutlined, group: experimentGroup, order: 42, requiresAuth: true, audience: 'admin', summary: '编辑实验信息、要求和内容。', hideInMenu: true }
  },
  {
    path: '/admin/experiments/steps/:id',
    name: 'AdminExperimentStepsEditor',
    component: AdminExperimentStepsEditorView,
    meta: { title: '步骤编辑', icon: ExperimentOutlined, group: experimentGroup, order: 44, requiresAuth: true, audience: 'admin', summary: '编辑实验步骤和题目内容。', hideInMenu: true }
  },
  // // 原有路由保留
  // { path: 'experiments', name: 'AdminExperimentList', component: AdminExperimentListView, meta: { title: '实验列表', icon: UnorderedListOutlined, group: experimentGroup, order: 40, requiresAuth: true, audience: 'admin', summary: '查看实验任务、范围和结果处理进度。' } },
  // { path: 'experiments/edit', name: 'AdminExperimentCreate', component: AdminExperimentEditView, meta: { title: '新建实验', icon: ToolOutlined, group: experimentGroup, order: 41, requiresAuth: true, audience: 'admin', summary: '创建实验草稿并维护基础信息。', hideInMenu: true } },
  // { path: 'experiments/edit/:id', name: 'AdminExperimentEdit', component: AdminExperimentEditView, meta: { title: '编辑实验', icon: ToolOutlined, group: experimentGroup, order: 42, requiresAuth: true, audience: 'admin', summary: '编辑实验摘要、安排和标签。', hideInMenu: true } },
  // { path: 'experiments/items/:id', name: 'AdminExperimentItems', component: AdminExperimentItemsView, meta: { title: '实验项管理', icon: ExperimentOutlined, group: experimentGroup, order: 43, requiresAuth: true, audience: 'admin', summary: '查看实验步骤和交付物要求。', hideInMenu: true } },
  // { path: 'experiments/steps/:id', name: 'AdminExperimentStepsEditor', component: AdminExperimentStepsEditorView, meta: { title: '步骤编辑', icon: ExperimentOutlined, group: experimentGroup, order: 44, requiresAuth: true, audience: 'admin', summary: '编辑实验步骤和题目内容。', hideInMenu: true } },
  // { path: 'experiments/results/:id', name: 'AdminExperimentResults', component: AdminExperimentResultsView, meta: { title: '实验结果', icon: ExperimentOutlined, group: experimentGroup, order: 44, requiresAuth: true, audience: 'admin', summary: '查看学生实验结果并进入处理。', hideInMenu: true } },
  // { path: 'experiments/results/:id/:studentId', name: 'AdminExperimentReportGrade', component: AdminExperimentReportView, meta: { title: '报告批改', icon: ExperimentOutlined, group: experimentGroup, order: 45, requiresAuth: true, audience: 'admin', summary: '批改学生实验报告。', hideInMenu: true } },
  // { path: 'experiments/results/:id/:resultId', name: 'AdminExperimentResultReview', component: AdminExperimentResultReviewView, meta: { title: '结果处理', icon: ExperimentOutlined, group: experimentGroup, order: 45, requiresAuth: true, audience: 'admin', summary: '完成实验结果评分和反馈。', hideInMenu: true } }
]

export default experimentRoutes
