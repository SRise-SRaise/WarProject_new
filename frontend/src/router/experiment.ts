import type { RouteRecordRaw } from 'vue-router'
import BasicLayout from '@/layouts/BasicLayout.vue'
import { ExperimentOutlined, ToolOutlined, UnorderedListOutlined } from '@ant-design/icons-vue'
import type { MenuGroup } from './meta'

const ExperimentListView = () => import('@/views/experiment/student/ListView.vue')
const ExperimentDetailView = () => import('@/views/experiment/student/DetailView.vue')
const ExperimentWorkView = () => import('@/views/experiment/student/WorkView.vue')
const ExperimentResultView = () => import('@/views/experiment/student/ResultView.vue')
const AdminExperimentListView = () => import('@/views/experiment/admin/ListView.vue')
const AdminExperimentEditView = () => import('@/views/experiment/admin/EditView.vue')
const AdminExperimentItemsView = () => import('@/views/experiment/admin/ItemsView.vue')
const AdminExperimentResultsView = () => import('@/views/experiment/admin/ResultsView.vue')
const AdminExperimentResultReviewView = () => import('@/views/experiment/admin/ResultReviewView.vue')

const experimentGroup: MenuGroup = { key: 'experiment', title: '实验管理', icon: ExperimentOutlined }

const experimentRoutes: RouteRecordRaw[] = [
  {
    path: '/experiments',
    component: BasicLayout,
    children: [
      { path: '', name: 'StudentExperimentList', component: ExperimentListView, meta: { title: '实验列表', requiresAuth: true, audience: 'student', shell: 'student', navLabel: '实验', summary: '查看实验开放安排和当前状态。' } },
      { path: ':id', name: 'StudentExperimentDetail', component: ExperimentDetailView, meta: { title: '实验详情', requiresAuth: true, audience: 'student', shell: 'student', summary: '查看实验目标、步骤和附件资料。' } },
      { path: ':id/work', name: 'StudentExperimentWork', component: ExperimentWorkView, meta: { title: '实验处理', requiresAuth: true, audience: 'student', shell: 'student', summary: '记录实验过程并提交结果。' } },
      { path: ':id/result', name: 'StudentExperimentResult', component: ExperimentResultView, meta: { title: '实验结果', requiresAuth: true, audience: 'student', shell: 'student', summary: '查看实验得分、说明和教师反馈。' } }
    ]
  }
]

export const adminExperimentRoutes: RouteRecordRaw[] = [
  { path: 'experiments', name: 'AdminExperimentList', component: AdminExperimentListView, meta: { title: '实验列表', icon: UnorderedListOutlined, group: experimentGroup, order: 40, requiresAuth: true, audience: 'admin', summary: '查看实验任务、范围和结果处理进度。' } },
  { path: 'experiments/edit', name: 'AdminExperimentCreate', component: AdminExperimentEditView, meta: { title: '新建实验', icon: ToolOutlined, group: experimentGroup, order: 41, requiresAuth: true, audience: 'admin', summary: '创建实验草稿并维护基础信息。', hideInMenu: true } },
  { path: 'experiments/edit/:id', name: 'AdminExperimentEdit', component: AdminExperimentEditView, meta: { title: '编辑实验', icon: ToolOutlined, group: experimentGroup, order: 42, requiresAuth: true, audience: 'admin', summary: '编辑实验摘要、安排和标签。', hideInMenu: true } },
  { path: 'experiments/items/:id', name: 'AdminExperimentItems', component: AdminExperimentItemsView, meta: { title: '实验项管理', icon: ExperimentOutlined, group: experimentGroup, order: 43, requiresAuth: true, audience: 'admin', summary: '查看实验步骤和交付物要求。', hideInMenu: true } },
  { path: 'experiments/results/:id', name: 'AdminExperimentResults', component: AdminExperimentResultsView, meta: { title: '实验结果', icon: ExperimentOutlined, group: experimentGroup, order: 44, requiresAuth: true, audience: 'admin', summary: '查看学生实验结果并进入处理。', hideInMenu: true } },
  { path: 'experiments/results/:id/:resultId', name: 'AdminExperimentResultReview', component: AdminExperimentResultReviewView, meta: { title: '结果处理', icon: ExperimentOutlined, group: experimentGroup, order: 45, requiresAuth: true, audience: 'admin', summary: '完成实验结果评分和反馈。', hideInMenu: true } }
]

export default experimentRoutes
