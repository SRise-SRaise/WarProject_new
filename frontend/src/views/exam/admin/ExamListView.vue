<template>
  <div class="exam-manage-container">
    <!-- 页面头部 -->
    <header class="exam-header">
      <div class="exam-header__content">
        <div class="exam-header__info">
          <h1 class="exam-header__title">考试管理</h1>
          <p class="exam-header__desc">创建和管理考试，关联试卷并发布给学生</p>
        </div>
        <a-button type="primary" size="large" @click="openAddModal">
          <template #icon><PlusOutlined /></template>
          新建考试
        </a-button>
      </div>
      
      <!-- 统计卡片 -->
      <div class="exam-stats">
        <div class="stat-card stat-card--total">
          <div class="stat-card__icon">
            <FileTextOutlined />
          </div>
          <div class="stat-card__content">
            <span class="stat-card__value">{{ examStats.total }}</span>
            <span class="stat-card__label">考试总数</span>
          </div>
        </div>
        <div class="stat-card stat-card--published">
          <div class="stat-card__icon">
            <CheckCircleOutlined />
          </div>
          <div class="stat-card__content">
            <span class="stat-card__value">{{ examStats.published }}</span>
            <span class="stat-card__label">已发布</span>
          </div>
        </div>
        <div class="stat-card stat-card--draft">
          <div class="stat-card__icon">
            <EditOutlined />
          </div>
          <div class="stat-card__content">
            <span class="stat-card__value">{{ examStats.draft }}</span>
            <span class="stat-card__label">草稿</span>
          </div>
        </div>
        <div class="stat-card stat-card--ongoing">
          <div class="stat-card__icon">
            <PlayCircleOutlined />
          </div>
          <div class="stat-card__content">
            <span class="stat-card__value">{{ examStats.ongoing }}</span>
            <span class="stat-card__label">进行中</span>
          </div>
        </div>
      </div>
    </header>

    <!-- 搜索和筛选 -->
    <div class="exam-toolbar">
      <div class="exam-toolbar__search">
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索考试名称..."
          style="width: 300px"
          allow-clear
          @search="handleSearch"
          @change="handleSearchChange"
        />
      </div>
      <div class="exam-toolbar__filters">
        <a-radio-group v-model:value="filterStatus" button-style="solid" @change="handleFilterChange">
          <a-radio-button value="all">全部</a-radio-button>
          <a-radio-button value="published">已发布</a-radio-button>
          <a-radio-button value="draft">草稿</a-radio-button>
        </a-radio-group>
      </div>
    </div>

    <!-- 考试列表 -->
    <a-spin :spinning="examLoading">
      <div v-if="examList.length === 0" class="exam-empty">
        <a-empty description="暂无考试数据">
          <a-button type="primary" @click="openAddModal">创建第一个考试</a-button>
        </a-empty>
      </div>
      
      <div v-else class="exam-list">
        <div v-for="exam in examList" :key="exam.id" class="exam-card" :class="getExamCardClass(exam)">
          <div class="exam-card__header">
            <div class="exam-card__title-row">
              <h3 class="exam-card__title">{{ exam.examName }}</h3>
              <a-tag :color="getStatusColor(exam)">{{ getStatusText(exam) }}</a-tag>
            </div>
            <div class="exam-card__meta">
              <span v-if="exam.paper" class="exam-card__paper">
                <FileTextOutlined /> {{ exam.paper.paperName }}
              </span>
              <span v-else class="exam-card__paper exam-card__paper--empty">
                <FileTextOutlined /> 未关联试卷
              </span>
            </div>
          </div>

          <div class="exam-card__body">
            <div class="exam-card__info-grid">
              <div class="exam-card__info-item">
                <ClockCircleOutlined />
                <span>考试时长</span>
                <strong>{{ exam.durationMin ? `${exam.durationMin} 分钟` : '未设置' }}</strong>
              </div>
              <div class="exam-card__info-item">
                <CalendarOutlined />
                <span>开始时间</span>
                <strong>{{ exam.startTime ? formatDateTime(exam.startTime) : '未设置' }}</strong>
              </div>
              <div class="exam-card__info-item">
                <FormOutlined />
                <span>题目数量</span>
                <strong>{{ exam.paper?.questionCount ?? 0 }} 题</strong>
              </div>
              <div class="exam-card__info-item">
                <TrophyOutlined />
                <span>总分</span>
                <strong>{{ exam.paper?.totalScore ?? 0 }} 分</strong>
              </div>
            </div>
          </div>

          <div class="exam-card__footer">
            <div class="exam-card__time">
              <span>更新于 {{ formatDate(exam.updatedAt) }}</span>
            </div>
            <div class="exam-card__actions">
              <a-button size="small" @click="openEditModal(exam)">
                <EditOutlined /> 编辑
              </a-button>
              <a-button 
                v-if="!exam.isPublished && exam.paperId" 
                size="small" 
                type="primary"
                @click="handlePublish(exam)"
              >
                <SendOutlined /> 发布
              </a-button>
              <a-button 
                v-if="exam.isPublished" 
                size="small" 
                danger
                @click="handleUnpublish(exam)"
              >
                <StopOutlined /> 取消发布
              </a-button>
              <a-popconfirm
                title="确定要删除这个考试吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDelete(exam.id)"
              >
                <a-button size="small" danger>
                  <DeleteOutlined /> 删除
                </a-button>
              </a-popconfirm>
            </div>
          </div>
        </div>
      </div>
    </a-spin>

    <!-- 分页 -->
    <div v-if="examPagination.total > examPagination.pageSize" class="exam-pagination">
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

    <!-- 新建/编辑考试弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEditing ? '编辑考试' : '新建考试'"
      :width="640"
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
                <span class="paper-option__meta">{{ paper.questionCount }}题 · {{ paper.totalScore }}分</span>
              </div>
            </a-select-option>
          </a-select>
          <template #extra>
            <span class="form-item-hint">选择已组卷完成的试卷</span>
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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  FileTextOutlined,
  CheckCircleOutlined,
  EditOutlined,
  PlayCircleOutlined,
  ClockCircleOutlined,
  CalendarOutlined,
  FormOutlined,
  TrophyOutlined,
  SendOutlined,
  StopOutlined,
  DeleteOutlined,
} from '@ant-design/icons-vue'
import { useExamAdminStore } from '@/stores/exam/admin'
import type { Exam, ExamAddRequest, ExamUpdateRequest, Paper } from '@/stores/exam/types'
import { getExamStatus } from '@/stores/exam/types'
import type { FormInstance } from 'ant-design-vue'
import dayjs from 'dayjs'

const examStore = useExamAdminStore()
const { examList, examPagination, examStats, allPapers, examLoading } = storeToRefs(examStore)

// 搜索和筛选
const searchKeyword = ref('')
const filterStatus = ref<'all' | 'published' | 'draft'>('all')

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
})

// 表单验证规则
const formRules = {
  examName: [{ required: true, message: '请输入考试名称' }],
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
  const end = new Date(start.getTime() + (exam.durationMin || 0) * 60 * 1000)
  
  if (now < start) return 'scheduled'
  if (now >= start && now <= end) return 'ongoing'
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

function getExamCardClass(exam: Exam): string {
  const status = getExamStatusLocal(exam)
  return `exam-card--${status}`
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
.exam-manage-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

/* 头部样式 */
.exam-header {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  color: #fff;
}

.exam-header__content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 28px;
}

.exam-header__title {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.exam-header__desc {
  margin: 0;
  opacity: 0.85;
  font-size: 15px;
}

/* 统计卡片 */
.exam-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-card__icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.stat-card__content {
  display: flex;
  flex-direction: column;
}

.stat-card__value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-card__label {
  font-size: 13px;
  opacity: 0.85;
}

/* 工具栏 */
.exam-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

/* 考试列表 */
.exam-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

.exam-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  transition: all 0.2s ease;
  border: 1px solid #f0f0f0;
}

.exam-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.exam-card--ongoing {
  border-color: #52c41a;
  border-width: 2px;
}

.exam-card__header {
  padding: 20px 20px 12px;
  border-bottom: 1px solid #f5f5f5;
}

.exam-card__title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 8px;
}

.exam-card__title {
  font-size: 17px;
  font-weight: 600;
  margin: 0;
  color: #1a1a1a;
  flex: 1;
  line-height: 1.4;
}

.exam-card__meta {
  font-size: 13px;
  color: #666;
}

.exam-card__paper {
  display: flex;
  align-items: center;
  gap: 6px;
}

.exam-card__paper--empty {
  color: #999;
}

.exam-card__body {
  padding: 16px 20px;
}

.exam-card__info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.exam-card__info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;
  color: #666;
}

.exam-card__info-item .anticon {
  color: #1890ff;
  margin-bottom: 2px;
}

.exam-card__info-item strong {
  font-size: 14px;
  color: #1a1a1a;
  font-weight: 500;
}

.exam-card__footer {
  padding: 12px 20px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fafafa;
}

.exam-card__time {
  font-size: 12px;
  color: #999;
}

.exam-card__actions {
  display: flex;
  gap: 8px;
}

/* 空状态 */
.exam-empty {
  padding: 80px 0;
  background: #fff;
  border-radius: 12px;
}

/* 分页 */
.exam-pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
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
  color: #999;
}

.form-item-hint {
  font-size: 12px;
  color: #999;
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
  color: #666;
}

.paper-preview__info .anticon {
  margin-right: 6px;
  color: #1890ff;
}

.paper-preview__desc {
  margin: 12px 0 0;
  font-size: 13px;
  color: #999;
}

/* 响应式 */
@media (max-width: 768px) {
  .exam-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .exam-toolbar {
    flex-direction: column;
    gap: 12px;
  }
  
  .exam-list {
    grid-template-columns: 1fr;
  }
}
</style>
