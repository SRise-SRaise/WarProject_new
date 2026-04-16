<template>
  <div class="exam-manage-page">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="page-header__content">
        <div class="page-header__info">
          <h1 class="page-header__title">考试管理</h1>
          <p class="page-header__desc">创建和管理考试，关联试卷并发布给学生</p>
        </div>
        <a-button type="primary" size="large" @click="openAddModal">
          <template #icon><PlusOutlined /></template>
          新建考试
        </a-button>
      </div>
    </header>

    <!-- 统计卡片 -->
    <section class="stats-section">
      <div class="stat-card">
        <div class="stat-card__icon stat-card__icon--primary">
          <FileTextOutlined />
        </div>
        <div class="stat-card__content">
          <span class="stat-card__value">{{ examStats.total }}</span>
          <span class="stat-card__label">考试总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-card__icon stat-card__icon--success">
          <CheckCircleOutlined />
        </div>
        <div class="stat-card__content">
          <span class="stat-card__value">{{ examStats.published }}</span>
          <span class="stat-card__label">已发布</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-card__icon stat-card__icon--warning">
          <EditOutlined />
        </div>
        <div class="stat-card__content">
          <span class="stat-card__value">{{ examStats.draft }}</span>
          <span class="stat-card__label">草稿</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-card__icon stat-card__icon--info">
          <PlayCircleOutlined />
        </div>
        <div class="stat-card__content">
          <span class="stat-card__value">{{ examStats.ongoing }}</span>
          <span class="stat-card__label">进行中</span>
        </div>
      </div>
    </section>

    <!-- 工具栏 -->
    <section class="content-section">
      <div class="toolbar">
        <div class="toolbar__search">
          <a-input-search
            v-model:value="searchKeyword"
            placeholder="搜索考试名称..."
            style="width: 300px"
            allow-clear
            @search="handleSearch"
            @change="handleSearchChange"
          />
        </div>
        <div class="toolbar__filters">
          <a-radio-group v-model:value="filterStatus" button-style="solid" @change="handleFilterChange">
            <a-radio-button value="all">全部</a-radio-button>
            <a-radio-button value="published">已发布</a-radio-button>
            <a-radio-button value="draft">草稿</a-radio-button>
          </a-radio-group>
        </div>
      </div>

      <!-- 考试表格 -->
      <a-table
        :columns="columns"
        :data-source="examList"
        :loading="examLoading"
        :pagination="false"
        row-key="id"
        class="exam-table"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'examName'">
            <div class="exam-name-cell">
              <span class="exam-name">{{ record.examName }}</span>
              <span v-if="record.paper" class="exam-paper">
                <FileTextOutlined /> {{ record.paper.paperName }}
              </span>
              <span v-else class="exam-paper exam-paper--empty">
                <FileTextOutlined /> 未关联试卷
              </span>
            </div>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record)">{{ getStatusText(record) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'duration'">
            <span v-if="record.durationMin">{{ record.durationMin }} 分钟</span>
            <span v-else class="empty-text">未设置</span>
          </template>
          <template v-else-if="column.key === 'startTime'">
            <span v-if="record.startTime">{{ formatDateTime(record.startTime) }}</span>
            <span v-else class="empty-text">未设置</span>
          </template>
          <template v-else-if="column.key === 'endTime'">
            <span v-if="record.endTime">{{ formatDateTime(record.endTime) }}</span>
            <span v-else class="empty-text">未设置</span>
          </template>
          <template v-else-if="column.key === 'paperInfo'">
            <div class="paper-info">
              <span>{{ record.paper?.questionCount ?? 0 }} 题</span>
              <span class="divider">|</span>
              <span>{{ record.paper?.totalScore ?? 0 }} 分</span>
            </div>
          </template>
          <template v-else-if="column.key === 'updatedAt'">
            <span class="time-text">{{ formatDate(record.updatedAt) }}</span>
          </template>
          <template v-else-if="column.key === 'actions'">
            <div class="action-buttons">
              <a-button type="link" size="small" @click="openEditModal(record)">
                <EditOutlined /> 编辑
              </a-button>
              <a-button 
                v-if="!record.isPublished && record.paperId" 
                type="link" 
                size="small"
                @click="handlePublish(record)"
              >
                <SendOutlined /> 发布
              </a-button>
              <a-button 
                v-if="record.isPublished" 
                type="link" 
                size="small" 
                danger
                @click="handleUnpublish(record)"
              >
                <StopOutlined /> 取消
              </a-button>
              <a-popconfirm
                title="确定要删除这个考试吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDelete(record.id)"
              >
                <a-button type="link" size="small" danger>
                  <DeleteOutlined /> 删除
                </a-button>
              </a-popconfirm>
            </div>
          </template>
        </template>
        <template #emptyText>
          <div class="empty-state">
            <FileSearchOutlined class="empty-icon" />
            <p>暂无考试数据</p>
            <a-button type="primary" @click="openAddModal">创建第一个考试</a-button>
          </div>
        </template>
      </a-table>

      <!-- 分页 -->
      <div v-if="examPagination.total > examPagination.pageSize" class="pagination-wrap">
        <a-pagination
          v-model:current="examPagination.current"
          :total="examPagination.total"
          :page-size="examPagination.pageSize"
          show-size-changer
          :page-size-options="['5', '10', '20']"
          @change="handlePageChange"
          @showSizeChange="handlePageSizeChange"
        />
      </div>
    </section>

    <!-- 新建/编辑考试弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEditing ? '编辑考试' : '新建考试'"
      :width="600"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
        class="exam-form"
      >
        <a-form-item label="考试名称" name="examName">
          <a-input 
            v-model:value="formData.examName" 
            placeholder="请输入考试名称"
            :maxlength="30"
            show-count
          />
        </a-form-item>

        <a-form-item label="关联试卷" name="paperId">
          <a-select
            v-model:value="formData.paperId"
            placeholder="请选择试卷"
            allow-clear
            :loading="papersLoading"
            @focus="loadPapersForSelect"
          >
            <a-select-option v-for="paper in allPapers" :key="paper.id" :value="paper.id">
              <div class="paper-option">
                <span>{{ paper.paperName }}</span>
                <span class="paper-option__meta">{{ paper.questionCount }}题 / {{ paper.totalScore }}分</span>
              </div>
            </a-select-option>
          </a-select>
          <template #extra>
            <span class="form-hint">选择已组卷完成的试卷</span>
          </template>
        </a-form-item>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="考试时长（分钟）" name="durationMin">
              <a-input-number
                v-model:value="formData.durationMin"
                placeholder="请输入"
                :min="1"
                :max="300"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="开始时间" name="startTime">
              <a-date-picker
                v-model:value="formData.startTimeValue"
                show-time
                placeholder="请选择开始时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="结束时间" name="endTime">
          <a-date-picker
            v-model:value="formData.endTimeValue"
            show-time
            placeholder="请选择结束时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
          <template #extra>
            <span class="form-hint">用于学生端识别考试是否过期，建议晚于开始时间</span>
          </template>
        </a-form-item>

        <!-- 试卷预览 -->
        <div v-if="selectedPaperDetail" class="paper-preview">
          <div class="paper-preview__header">
            <h4>试卷预览</h4>
          </div>
          <div class="paper-preview__content">
            <div class="paper-preview__info">
              <span><FileTextOutlined /> {{ selectedPaperDetail.paperName }}</span>
              <span><FormOutlined /> {{ selectedPaperDetail.questionCount }} 道题</span>
              <span><TrophyOutlined /> {{ selectedPaperDetail.totalScore }} 分</span>
            </div>
            <p v-if="selectedPaperDetail.description" class="paper-preview__desc">
              {{ selectedPaperDetail.description }}
            </p>
          </div>
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import type { TableColumnType } from 'ant-design-vue'
import {
  PlusOutlined,
  FileTextOutlined,
  CheckCircleOutlined,
  EditOutlined,
  PlayCircleOutlined,
  FormOutlined,
  TrophyOutlined,
  SendOutlined,
  StopOutlined,
  DeleteOutlined,
  FileSearchOutlined,
} from '@ant-design/icons-vue'
import { useExamAdminStore } from '@/stores/exam/admin'
import type { Exam, ExamAddRequest, ExamUpdateRequest } from '@/stores/exam/types'
import type { FormInstance } from 'ant-design-vue'
import dayjs from 'dayjs'

const examStore = useExamAdminStore()
const { examList, examPagination, examStats, allPapers, examLoading } = storeToRefs(examStore)

// 搜索和筛选
const searchKeyword = ref('')
const filterStatus = ref<'all' | 'published' | 'draft'>('all')

// 表格列定义
const columns: TableColumnType[] = [
  { title: '考试名称', key: 'examName', ellipsis: true },
  { title: '状态', key: 'status', width: 100 },
  { title: '时长', key: 'duration', width: 100 },
  { title: '开始时间', key: 'startTime', width: 150 },
  { title: '结束时间', key: 'endTime', width: 150 },
  { title: '试卷信息', key: 'paperInfo', width: 120 },
  { title: '更新时间', key: 'updatedAt', width: 160 },
  { title: '操作', key: 'actions', width: 200, fixed: 'right' }
]

// 弹窗状态
const modalVisible = ref(false)
const modalLoading = ref(false)
const isEditing = ref(false)
const editingExamId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const papersLoading = ref(false)

// 表单数据
const formData = reactive({
  examName: '',
  paperId: undefined as number | undefined,
  durationMin: undefined as number | undefined,
  startTimeValue: undefined as dayjs.Dayjs | undefined,
  endTimeValue: undefined as dayjs.Dayjs | undefined,
})

// 表单验证规则
const formRules = {
  examName: [{ required: true, message: '请输入考试名称' }],
  endTime: [
    {
      validator: async () => {
        if (formData.startTimeValue && formData.endTimeValue && formData.endTimeValue.isBefore(formData.startTimeValue)) {
          return Promise.reject('结束时间不能早于开始时间')
        }
        return Promise.resolve()
      },
      trigger: 'change',
    },
  ],
}

// 选中试卷的详情
const selectedPaperDetail = computed(() => {
  if (!formData.paperId) return null
  return allPapers.value.find(p => p.id === formData.paperId)
})

// 获取考试状态
function getExamStatusLocal(exam: Exam): string {
  if (!exam.isPublished) {
    return exam.paperId ? 'ready' : 'draft'
  }
  if (!exam.startTime) return 'published'

  const now = new Date()
  const start = new Date(exam.startTime)
  const end = exam.endTime
    ? new Date(exam.endTime)
    : exam.durationMin
      ? new Date(start.getTime() + exam.durationMin * 60 * 1000)
      : null

  if (now < start) return 'scheduled'
  if (!end || now <= end) return 'ongoing'
  return 'ended'
}

function getStatusColor(exam: Exam): string {
  const status = getExamStatusLocal(exam)
  const colorMap: Record<string, string> = {
    draft: 'default',
    ready: 'blue',
    published: 'cyan',
    scheduled: 'purple',
    ongoing: 'green',
    ended: 'gray'
  }
  return colorMap[status] || 'default'
}

function getStatusText(exam: Exam): string {
  const status = getExamStatusLocal(exam)
  const textMap: Record<string, string> = {
    draft: '草稿',
    ready: '待发布',
    published: '已发布',
    scheduled: '已排期',
    ongoing: '进行中',
    ended: '已结束'
  }
  return textMap[status] || '未知'
}

// 格式化日期
function formatDateTime(dateStr: string): string {
  return dayjs(dateStr).format('MM-DD HH:mm')
}

function formatDate(dateStr: string): string {
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

// 搜索处理
function handleSearch() {
  loadExams()
}

function handleSearchChange() {
  if (!searchKeyword.value) {
    loadExams()
  }
}

function handleFilterChange() {
  loadExams()
}

// 加载考试列表
async function loadExams() {
  const query: any = {
    current: 1,
    examName: searchKeyword.value || undefined,
  }
  
  if (filterStatus.value === 'published') {
    query.isPublished = true
  } else if (filterStatus.value === 'draft') {
    query.isPublished = false
  }
  
  await examStore.loadExams(query)
}

// 分页处理
function handlePageChange(page: number) {
  examStore.loadExams({ current: page })
}

function handlePageSizeChange(_current: number, size: number) {
  examPagination.value.pageSize = size
  examStore.loadExams({ current: 1, pageSize: size })
}

// 加载试卷列表
async function loadPapersForSelect() {
  if (allPapers.value.length === 0) {
    papersLoading.value = true
    await examStore.loadAllPapers()
    papersLoading.value = false
  }
}

// 打开新建弹窗
function openAddModal() {
  isEditing.value = false
  editingExamId.value = null
  formData.examName = ''
  formData.paperId = undefined
  formData.durationMin = 90
  formData.startTimeValue = undefined
  formData.endTimeValue = undefined
  modalVisible.value = true
}

// 打开编辑弹窗
function openEditModal(exam: Exam) {
  isEditing.value = true
  editingExamId.value = exam.id
  formData.examName = exam.examName
  formData.paperId = exam.paperId || undefined
  formData.durationMin = exam.durationMin || undefined
  formData.startTimeValue = exam.startTime ? dayjs(exam.startTime) : undefined
  formData.endTimeValue = exam.endTime ? dayjs(exam.endTime) : undefined
  modalVisible.value = true
  loadPapersForSelect()
}

// 弹窗确认
async function handleModalOk() {
  try {
    await formRef.value?.validate()
    modalLoading.value = true

    if (isEditing.value && editingExamId.value) {
      const request: ExamUpdateRequest = {
        id: editingExamId.value,
        examName: formData.examName,
        paperId: formData.paperId,
        durationMin: formData.durationMin,
        startTime: formData.startTimeValue?.format('YYYY-MM-DD HH:mm:ss'),
        endTime: formData.endTimeValue?.format('YYYY-MM-DD HH:mm:ss'),
      }
      const success = await examStore.updateExam(request)
      if (success) {
        message.success('考试更新成功')
        modalVisible.value = false
      } else {
        message.error('更新失败')
      }
    } else {
      const request: ExamAddRequest = {
        examName: formData.examName,
        paperId: formData.paperId,
        durationMin: formData.durationMin,
        startTime: formData.startTimeValue?.format('YYYY-MM-DD HH:mm:ss'),
        endTime: formData.endTimeValue?.format('YYYY-MM-DD HH:mm:ss'),
      }
      const id = await examStore.addExam(request)
      if (id) {
        message.success('考试创建成功')
        modalVisible.value = false
      } else {
        message.error('创建失败')
      }
    }
  } catch (e) {
    // 表单验证失败
  } finally {
    modalLoading.value = false
  }
}

function handleModalCancel() {
  modalVisible.value = false
}

// 发布考试
async function handlePublish(exam: Exam) {
  if (!exam.paperId) {
    message.warning('请先关联试卷')
    return
  }
  const success = await examStore.publishExam(exam.id)
  if (success) {
    message.success('考试已发布')
  } else {
    message.error('发布失败')
  }
}

// 取消发布
async function handleUnpublish(exam: Exam) {
  const success = await examStore.unpublishExam(exam.id)
  if (success) {
    message.success('已取消发布')
  } else {
    message.error('操作失败')
  }
}

// 删除考试
async function handleDelete(id: number) {
  const success = await examStore.deleteExam(id)
  if (success) {
    message.success('考试已删除')
  } else {
    message.error('删除失败')
  }
}

onMounted(async () => {
  await examStore.loadExams()
})
</script>

<style scoped>
.exam-manage-page {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 页面头部 */
.page-header {
  background: #fff;
  border-radius: 12px;
  padding: 28px 32px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.page-header__content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header__title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #1a1a1a;
}

.page-header__desc {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 统计卡片 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 10px;
  padding: 20px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.stat-card__icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.stat-card__icon--primary {
  background: #e6f7ff;
  color: #1890ff;
}

.stat-card__icon--success {
  background: #f6ffed;
  color: #52c41a;
}

.stat-card__icon--warning {
  background: #fff7e6;
  color: #fa8c16;
}

.stat-card__icon--info {
  background: #f0f5ff;
  color: #722ed1;
}

.stat-card__content {
  display: flex;
  flex-direction: column;
}

.stat-card__value {
  font-size: 28px;
  font-weight: 700;
  color: #262626;
  line-height: 1.2;
}

.stat-card__label {
  font-size: 13px;
  color: #8c8c8c;
  margin-top: 4px;
}

/* 内容区域 */
.content-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

/* 表格样式 */
.exam-table {
  margin-bottom: 20px;
}

.exam-name-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.exam-name {
  font-weight: 500;
  color: #262626;
}

.exam-paper {
  font-size: 12px;
  color: #8c8c8c;
  display: flex;
  align-items: center;
  gap: 4px;
}

.exam-paper--empty {
  color: #bfbfbf;
}

.paper-info {
  font-size: 13px;
  color: #595959;
}

.paper-info .divider {
  margin: 0 6px;
  color: #d9d9d9;
}

.time-text {
  color: #8c8c8c;
  font-size: 13px;
}

.empty-text {
  color: #bfbfbf;
}

.action-buttons {
  display: flex;
  gap: 4px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 48px;
  color: #d9d9d9;
  margin-bottom: 16px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

/* 表单样式 */
.exam-form {
  padding: 8px 0;
}

.paper-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.paper-option__meta {
  font-size: 12px;
  color: #8c8c8c;
}

.form-hint {
  font-size: 12px;
  color: #8c8c8c;
}

/* 试卷预览 */
.paper-preview {
  margin-top: 16px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
}

.paper-preview__header {
  background: #fafafa;
  padding: 12px 16px;
  border-bottom: 1px solid #e8e8e8;
}

.paper-preview__header h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
}

.paper-preview__content {
  padding: 16px;
}

.paper-preview__info {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #595959;
}

.paper-preview__info .anticon {
  margin-right: 4px;
  color: #1890ff;
}

.paper-preview__desc {
  margin: 12px 0 0;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
  font-size: 13px;
  color: #666;
}

@media (max-width: 1200px) {
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-section {
    grid-template-columns: 1fr;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
}
</style>
