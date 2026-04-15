import type { RouteRecordRaw } from 'vue-router'
import BasicLayout from '@/layouts/BasicLayout.vue'
import {
  EditOutlined,
  FileDoneOutlined,
  FileSearchOutlined,
  FileTextOutlined,
  ScheduleOutlined,
  SolutionOutlined
} from '@ant-design/icons-vue'
import type { MenuGroup } from './meta'

const HomeworkListView = () => import('@/views/homework/student/ListView.vue')
const HomeworkDoView = () => import('@/views/homework/student/DetailView.vue')
const HomeworkSubmitView = () => import('@/views/homework/student/SubmitView.vue')
const HomeworkScoreView = () => import('@/views/homework/student/ResultView.vue')
const AdminHomeworkListView = () => import('@/views/homework/admin/ListView.vue')
const AdminHomeworkEditView = () => import('@/views/homework/admin/EditView.vue')
const AdminHomeworkAssignView = () => import('@/views/homework/admin/PublishView.vue')
const AdminHomeworkSubmissionsView = () => import('@/views/homework/admin/SubmissionsView.vue')
const AdminHomeworkReviewView = () => import('@/views/homework/admin/ReviewView.vue')
const AdminQuestionListView = () => import('@/views/homework/admin/QuestionListView.vue')
const AdminQuestionEditView = () => import('@/views/homework/admin/QuestionEditView.vue')

const homeworkGroup: MenuGroup = { key: 'homework', title: '作业管理', icon: FileTextOutlined }

const homeworkRoutes: RouteRecordRaw[] = [
  {
    path: '/homework',
    component: BasicLayout,
    children: [
      { path: '', name: 'StudentHomeworkList', component: HomeworkListView, meta: { title: '作业列表', requiresAuth: true, audience: 'student', shell: 'student', navLabel: '作业', summary: '显示全部作业并按状态筛选。' } },
      { path: ':id/do', name: 'StudentHomeworkDo', component: HomeworkDoView, meta: { title: '做作业', requiresAuth: true, audience: 'student', shell: 'student', summary: '进入答题页并完成选择题与简答提交。' } },
      { path: ':id/score', name: 'StudentHomeworkScore', component: HomeworkScoreView, meta: { title: '作业成绩', requiresAuth: true, audience: 'student', shell: 'student', summary: '查看得分、反馈和提交记录。' } },
      { path: ':id', redirect: (to) => ({ name: 'StudentHomeworkDo', params: to.params }) },
      { path: ':id/submit', name: 'StudentHomeworkSubmit', component: HomeworkSubmitView, meta: { title: '提交作业', requiresAuth: true, audience: 'student', shell: 'student', summary: '兼容旧入口，后续统一回收到做作业页。', hideInMenu: true } },
      { path: ':id/result', redirect: (to) => ({ name: 'StudentHomeworkScore', params: to.params }) }
    ]
  }
]

export const adminHomeworkRoutes: RouteRecordRaw[] = [
  { path: 'homework', name: 'AdminHomeworkList', component: AdminHomeworkListView, meta: { title: '作业列表', icon: FileTextOutlined, group: homeworkGroup, order: 30, requiresAuth: true, audience: 'admin', summary: '查看作业状态和教师侧管理动作。' } },
  { path: 'homework/edit', name: 'AdminHomeworkCreate', component: AdminHomeworkEditView, meta: { title: '新增作业', icon: EditOutlined, group: homeworkGroup, order: 31, requiresAuth: true, audience: 'admin', summary: '创建作业并配置基础信息。' } },
  { path: 'homework/edit/:id', name: 'AdminHomeworkEdit', component: AdminHomeworkEditView, meta: { title: '编辑作业', icon: EditOutlined, group: homeworkGroup, order: 32, requiresAuth: true, audience: 'admin', summary: '维护作业内容与结构。', hideInMenu: true } },
  { path: 'homework/assign', name: 'AdminHomeworkAssignHub', component: AdminHomeworkAssignView, meta: { title: '布置作业', icon: ScheduleOutlined, group: homeworkGroup, order: 33, requiresAuth: true, audience: 'admin', summary: '按班级范围与截止时间布置作业。' } },
  { path: 'homework/assign/:id', name: 'AdminHomeworkAssign', component: AdminHomeworkAssignView, meta: { title: '布置作业', icon: ScheduleOutlined, group: homeworkGroup, order: 33, requiresAuth: true, audience: 'admin', summary: '设置发布范围、截止时间和补交策略。', hideInMenu: true } },
  { path: 'homework/submissions', name: 'AdminHomeworkSubmissionsHub', component: AdminHomeworkSubmissionsView, meta: { title: '提交记录', icon: FileDoneOutlined, group: homeworkGroup, order: 34, requiresAuth: true, audience: 'admin', summary: '查看各作业提交情况并进入批改。' } },
  { path: 'homework/submissions/:id', name: 'AdminHomeworkSubmissions', component: AdminHomeworkSubmissionsView, meta: { title: '提交记录', icon: FileDoneOutlined, group: homeworkGroup, order: 38, requiresAuth: true, audience: 'admin', summary: '次级页面，查看学生提交与批改入口。', hideInMenu: true } },
  { path: 'homework/review/:homeworkId/:submissionId', name: 'AdminHomeworkReview', component: AdminHomeworkReviewView, meta: { title: '作业批改', icon: FileDoneOutlined, group: homeworkGroup, order: 39, requiresAuth: true, audience: 'admin', summary: '次级页面，完成分数与反馈录入。', hideInMenu: true } },
  { path: 'questions', name: 'AdminQuestionList', component: AdminQuestionListView, meta: { title: '题目列表', icon: FileSearchOutlined, group: homeworkGroup, order: 35, requiresAuth: true, audience: 'admin', summary: '管理作业题库并支持筛选。' } },
  { path: 'questions/add', name: 'AdminQuestionAdd', component: AdminQuestionEditView, meta: { title: '新增题目', icon: SolutionOutlined, group: homeworkGroup, order: 36, requiresAuth: true, audience: 'admin', summary: '创建题目并设置题型答案。', hideInMenu: true } },
  { path: 'questions/edit/:id', name: 'AdminQuestionEdit', component: AdminQuestionEditView, meta: { title: '编辑题目', icon: SolutionOutlined, group: homeworkGroup, order: 37, requiresAuth: true, audience: 'admin', summary: '修改题干、选项和标准答案。', hideInMenu: true } }
]

export default homeworkRoutes
