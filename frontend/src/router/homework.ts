import type { RouteRecordRaw } from 'vue-router'
import BasicLayout from '@/layouts/BasicLayout.vue'
import { EditOutlined, FileTextOutlined } from '@ant-design/icons-vue'
import type { MenuGroup } from './meta'

const HomeworkListView = () => import('@/views/homework/student/ListView.vue')
const HomeworkDetailView = () => import('@/views/homework/student/DetailView.vue')
const HomeworkSubmitView = () => import('@/views/homework/student/SubmitView.vue')
const HomeworkResultView = () => import('@/views/homework/student/ResultView.vue')
const AdminHomeworkListView = () => import('@/views/homework/admin/ListView.vue')
const AdminHomeworkEditView = () => import('@/views/homework/admin/EditView.vue')
const AdminHomeworkPublishView = () => import('@/views/homework/admin/PublishView.vue')
const AdminHomeworkSubmissionsView = () => import('@/views/homework/admin/SubmissionsView.vue')
const AdminHomeworkReviewView = () => import('@/views/homework/admin/ReviewView.vue')

const homeworkGroup: MenuGroup = { key: 'homework', title: '作业管理', icon: FileTextOutlined }

const homeworkRoutes: RouteRecordRaw[] = [
  {
    path: '/homework',
    component: BasicLayout,
    children: [
      { path: '', name: 'StudentHomeworkList', component: HomeworkListView, meta: { title: '作业列表', requiresAuth: true, audience: 'student', shell: 'student', navLabel: '作业', summary: '查看待完成、已提交和已批阅作业。' } },
      { path: ':id', name: 'StudentHomeworkDetail', component: HomeworkDetailView, meta: { title: '作业详情', requiresAuth: true, audience: 'student', shell: 'student', summary: '查看单个作业说明、资料与当前提交状态。' } },
      { path: ':id/submit', name: 'StudentHomeworkSubmit', component: HomeworkSubmitView, meta: { title: '提交作业', requiresAuth: true, audience: 'student', shell: 'student', summary: '填写作业摘要并完成本次提交。' } },
      { path: ':id/result', name: 'StudentHomeworkResult', component: HomeworkResultView, meta: { title: '作业结果', requiresAuth: true, audience: 'student', shell: 'student', summary: '查看提交状态、得分和教师反馈。' } }
    ]
  }
]

export const adminHomeworkRoutes: RouteRecordRaw[] = [
  { path: 'homework', name: 'AdminHomeworkList', component: AdminHomeworkListView, meta: { title: '作业列表', icon: FileTextOutlined, group: homeworkGroup, order: 30, requiresAuth: true, audience: 'admin', summary: '查看作业状态、范围和批改进度。' } },
  { path: 'homework/edit', name: 'AdminHomeworkCreate', component: AdminHomeworkEditView, meta: { title: '新建作业', icon: EditOutlined, group: homeworkGroup, order: 31, requiresAuth: true, audience: 'admin', summary: '新建作业草稿并维护基本信息。', hideInMenu: true } },
  { path: 'homework/edit/:id', name: 'AdminHomeworkEdit', component: AdminHomeworkEditView, meta: { title: '编辑作业', icon: EditOutlined, group: homeworkGroup, order: 32, requiresAuth: true, audience: 'admin', summary: '编辑已有作业内容与要求。', hideInMenu: true } },
  { path: 'homework/publish/:id', name: 'AdminHomeworkPublish', component: AdminHomeworkPublishView, meta: { title: '发布作业', icon: FileTextOutlined, group: homeworkGroup, order: 33, requiresAuth: true, audience: 'admin', summary: '设置发布范围、截止时间和补交规则。', hideInMenu: true } },
  { path: 'homework/submissions/:id', name: 'AdminHomeworkSubmissions', component: AdminHomeworkSubmissionsView, meta: { title: '提交记录', icon: FileTextOutlined, group: homeworkGroup, order: 34, requiresAuth: true, audience: 'admin', summary: '查看学生提交情况并进入批改。', hideInMenu: true } },
  { path: 'homework/review/:homeworkId/:submissionId', name: 'AdminHomeworkReview', component: AdminHomeworkReviewView, meta: { title: '作业批改', icon: FileTextOutlined, group: homeworkGroup, order: 35, requiresAuth: true, audience: 'admin', summary: '完成作业评分与教师反馈。', hideInMenu: true } }
]

export default homeworkRoutes
