<template>
  <div class="paper-manage-page">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="page-header__content">
        <div class="page-header__info">
          <h1 class="page-header__title">试卷管理</h1>
          <p class="page-header__desc">创建和管理试卷，支持从题库拖拽添加题目，灵活组卷</p>
        </div>
        <a-button type="primary" size="large" @click="handleCreatePaper">
          <template #icon><PlusOutlined /></template>
          新建试卷
        </a-button>
      </div>
    </header>

    <!-- 统计卡片 -->
    <section class="stats-section">
      <div class="stat-card">
        <div class="stat-card__value">{{ paperList.length }}</div>
        <div class="stat-card__label">试卷总数</div>
      </div>
      <div class="stat-card stat-card--success">
        <div class="stat-card__value">{{ paperList.filter(p => p.questionCount > 0).length }}</div>
        <div class="stat-card__label">已组卷</div>
      </div>
      <div class="stat-card stat-card--warning">
        <div class="stat-card__value">{{ paperList.filter(p => p.questionCount === 0).length }}</div>
        <div class="stat-card__label">待组卷</div>
      </div>
    </section>

    <!-- 搜索栏和表格 -->
    <section class="content-section">
      <div class="toolbar">
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索试卷名称..."
          style="width: 300px"
          allow-clear
          @search="handleSearch"
          @change="handleSearchChange"
        />
      </div>

      <!-- 试卷表格 -->
      <a-table
        :columns="columns"
        :data-source="paperList"
        :loading="paperLoading"
        :pagination="false"
        row-key="id"
        class="paper-table"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'paperCode'">
            <span class="paper-code">No.{{ record.paperCode }}</span>
          </template>
          <template v-else-if="column.key === 'paperName'">
            <div class="paper-name-cell">
              <span class="paper-name">{{ record.paperName }}</span>
              <span v-if="record.description" class="paper-desc">{{ record.description }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.questionCount > 0 ? 'green' : 'orange'">
              {{ record.questionCount > 0 ? '已组卷' : '待组卷' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'questionCount'">
            <span>{{ record.questionCount }} 题</span>
          </template>
          <template v-else-if="column.key === 'totalScore'">
            <span class="score-text">{{ record.totalScore }} 分</span>
          </template>
          <template v-else-if="column.key === 'updatedAt'">
            <span class="time-text">{{ formatTime(record.updatedAt) }}</span>
          </template>
          <template v-else-if="column.key === 'actions'">
            <div class="action-buttons">
              <a-button type="link" size="small" @click="handleEditPaper(record)">
                <EditOutlined /> 组卷
              </a-button>
              <a-button type="link" size="small" @click="handleEditInfo(record)">
                <FormOutlined /> 编辑
              </a-button>
              <a-popconfirm
                title="确定删除此试卷吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDeletePaper(record.id)"
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
            <p>暂无试卷</p>
            <a-button type="primary" @click="handleCreatePaper">创建第一份试卷</a-button>
          </div>
        </template>
      </a-table>

      <!-- 分页 -->
      <div v-if="paperPagination.total > paperPagination.pageSize" class="pagination-wrap">
        <a-pagination
          v-model:current="paperPagination.current"
          :total="paperPagination.total"
          :page-size="paperPagination.pageSize"
          show-quick-jumper
          @change="handlePageChange"
        />
      </div>
    </section>

    <!-- 新建/编辑试卷弹窗 -->
    <a-modal
      v-model:open="paperModalVisible"
      :title="isEditMode ? '编辑试卷' : '新建试卷'"
      :width="500"
      @ok="handleSavePaper"
      @cancel="paperModalVisible = false"
    >
      <a-form :model="paperForm" layout="vertical" class="paper-form">
        <a-form-item label="试卷编号" required>
          <a-input-number
            v-model:value="paperForm.paperCode"
            :min="1"
            style="width: 100%"
            placeholder="请输入试卷编号"
          />
        </a-form-item>
        <a-form-item label="试卷名称" required>
          <a-input
            v-model:value="paperForm.paperName"
            placeholder="请输入试卷名称"
            :maxlength="50"
            show-count
          />
        </a-form-item>
        <a-form-item label="试卷描述">
          <a-textarea
            v-model:value="paperForm.description"
            placeholder="请输入试卷描述"
            :rows="3"
            :maxlength="200"
            show-count
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 组卷弹窗 -->
    <a-modal
      v-model:open="composeModalVisible"
      title="组卷"
      :width="1200"
      :footer="null"
      class="compose-modal"
      @cancel="composeModalVisible = false"
    >
      <div class="compose-container">
        <!-- 左侧：试卷内容 -->
        <div class="compose-left">
          <div class="compose-panel-header">
            <h3>试卷内容</h3>
            <div class="compose-info">
              <span>共 {{ currentPaper?.questions.length || 0 }} 题</span>
              <span class="divider">|</span>
              <span>总分 {{ currentPaper?.totalScore || 0 }} 分</span>
            </div>
          </div>
          
          <div class="paper-content" ref="paperContentRef">
            <draggable
              v-if="currentPaper"
              v-model="paperQuestionsList"
              group="questions"
              item-key="id"
              handle=".drag-handle"
              @end="handleDragEnd"
              class="question-list"
            >
              <template #item="{ element, index }">
                <div class="question-item">
                  <div class="drag-handle">
                    <HolderOutlined />
                  </div>
                  <div class="question-order">{{ index + 1 }}</div>
                  <div class="question-main">
                    <div class="question-header">
                      <a-tag :color="getTypeColor(element.question?.questionType)">
                        {{ getTypeName(element.question?.questionType) }}
                      </a-tag>
                      <a-tag v-if="element.sectionName">{{ element.sectionName }}</a-tag>
                    </div>
                    <div class="question-text">{{ element.question?.questionContent }}</div>
                    <div class="question-meta">
                      <span>难度：{{ getDifficultyName(element.question?.difficulty) }}</span>
                    </div>
                  </div>
                  <div class="question-score">
                    <a-input-number
                      v-model:value="element.score"
                      :min="1"
                      :max="100"
                      size="small"
                      @change="(val: number) => handleScoreChange(element.id, val)"
                    />
                    <span class="score-label">分</span>
                  </div>
                  <a-button
                    type="text"
                    danger
                    size="small"
                    @click="handleRemoveQuestion(element.id)"
                  >
                    <DeleteOutlined />
                  </a-button>
                </div>
              </template>
            </draggable>

            <!-- 空状态 -->
            <div v-if="!currentPaper?.questions.length" class="empty-paper">
              <InboxOutlined class="empty-icon" />
              <p>从右侧题库拖拽题目到这里</p>
              <p class="empty-hint">或点击题目右侧的添加按钮</p>
            </div>
          </div>
        </div>

        <!-- 右侧：题库 -->
        <div class="compose-right">
          <div class="compose-panel-header">
            <h3>题库</h3>
            <a-input-search
              v-model:value="questionSearchKeyword"
              placeholder="搜索题目"
              style="width: 180px"
              size="small"
              allow-clear
            />
          </div>

          <!-- 题型筛选 -->
          <div class="filter-row">
            <a-select
              v-model:value="questionTypeFilter"
              placeholder="题型"
              style="width: 100px"
              size="small"
              allow-clear
            >
              <a-select-option :value="1">填空</a-select-option>
              <a-select-option :value="2">单选</a-select-option>
              <a-select-option :value="3">多选</a-select-option>
              <a-select-option :value="5">简答</a-select-option>
              <a-select-option :value="6">编程</a-select-option>
              <a-select-option :value="7">综合</a-select-option>
            </a-select>
            <a-select
              v-model:value="questionDifficultyFilter"
              placeholder="难度"
              style="width: 100px"
              size="small"
              allow-clear
            >
              <a-select-option :value="1">简单</a-select-option>
              <a-select-option :value="2">较易</a-select-option>
              <a-select-option :value="3">中等</a-select-option>
              <a-select-option :value="4">较难</a-select-option>
              <a-select-option :value="5">困难</a-select-option>
            </a-select>
          </div>

          <div class="question-bank-list">
            <draggable
              v-model="filteredQuestions"
              :group="{ name: 'questions', pull: 'clone', put: false }"
              item-key="id"
              :clone="cloneQuestion"
              :sort="false"
              class="bank-list"
            >
              <template #item="{ element }">
                <div 
                  class="bank-item"
                  :class="{ 'is-added': isQuestionAdded(element.id) }"
                >
                  <div class="bank-item-main">
                    <div class="bank-item-header">
                      <a-tag :color="getTypeColor(element.questionType)" size="small">
                        {{ getTypeName(element.questionType) }}
                      </a-tag>
                      <span class="difficulty-badge">{{ getDifficultyName(element.difficulty) }}</span>
                    </div>
                    <div class="bank-item-text">{{ element.questionContent }}</div>
                  </div>
                  <a-button
                    type="primary"
                    size="small"
                    :disabled="isQuestionAdded(element.id)"
                    @click="handleAddQuestion(element)"
                  >
                    <template v-if="isQuestionAdded(element.id)">
                      <CheckOutlined /> 已添加
                    </template>
                    <template v-else>
                      <PlusOutlined /> 添加
                    </template>
                  </a-button>
                </div>
              </template>
            </draggable>

            <div v-if="filteredQuestions.length === 0" class="empty-bank">
              <p>暂无匹配的题目</p>
            </div>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import type { TableColumnType } from 'ant-design-vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  FormOutlined,
  FileSearchOutlined,
  HolderOutlined,
  InboxOutlined,
  CheckOutlined
} from '@ant-design/icons-vue'
import draggable from 'vuedraggable'
import { useExamAdminStore } from '@/stores/exam/admin'
import type { Paper, QuestionItem, PaperQuestion } from '@/stores/exam/types'
import { QUESTION_TYPE_MAP, DIFFICULTY_MAP } from '@/stores/exam/types'

const examStore = useExamAdminStore()
const { paperList, paperPagination, currentPaper, allQuestions, paperLoading } = storeToRefs(examStore)

// 搜索
const searchKeyword = ref('')

// 表格列定义
const columns: TableColumnType[] = [
  { title: '编号', key: 'paperCode', width: 80 },
  { title: '试卷名称', key: 'paperName', ellipsis: true },
  { title: '状态', key: 'status', width: 100 },
  { title: '题目数', key: 'questionCount', width: 100 },
  { title: '总分', key: 'totalScore', width: 100 },
  { title: '更新时间', key: 'updatedAt', width: 160 },
  { title: '操作', key: 'actions', width: 200, fixed: 'right' }
]

// 新建/编辑弹窗
const paperModalVisible = ref(false)
const isEditMode = ref(false)
const editingPaperId = ref<number | null>(null)
const paperForm = ref({
  paperCode: 1,
  paperName: '',
  description: ''
})

// 组卷弹窗
const composeModalVisible = ref(false)
const questionSearchKeyword = ref('')
const questionTypeFilter = ref<number | undefined>()
const questionDifficultyFilter = ref<number | undefined>()

// 试卷题目列表（用于拖拽）
const paperQuestionsList = ref<PaperQuestion[]>([])

// 监听当前试卷变化，同步题目列表
watch(() => currentPaper.value, (newVal) => {
  if (newVal) {
    paperQuestionsList.value = [...newVal.questions]
  } else {
    paperQuestionsList.value = []
  }
}, { immediate: true })

// 筛选后的题库题目
const filteredQuestions = computed(() => {
  let list = [...allQuestions.value]
  
  if (questionSearchKeyword.value) {
    const keyword = questionSearchKeyword.value.toLowerCase()
    list = list.filter(q => q.questionContent.toLowerCase().includes(keyword))
  }
  
  if (questionTypeFilter.value) {
    list = list.filter(q => q.questionType === questionTypeFilter.value)
  }
  
  if (questionDifficultyFilter.value) {
    list = list.filter(q => q.difficulty === questionDifficultyFilter.value)
  }
  
  return list
})

// 检查题目是否已添加到试卷
function isQuestionAdded(questionId: number): boolean {
  return paperQuestionsList.value.some(pq => pq.questionId === questionId)
}

// 工具函数
function getTypeName(type?: number): string {
  return type ? QUESTION_TYPE_MAP[type as keyof typeof QUESTION_TYPE_MAP] || '未知' : '未知'
}

function getDifficultyName(difficulty?: number): string {
  return difficulty ? DIFFICULTY_MAP[difficulty] || '未知' : '未知'
}

function getTypeColor(type?: number): string {
  const colors: Record<number, string> = {
    1: 'blue',
    2: 'green',
    3: 'orange',
    5: 'purple',
    6: 'red',
    7: 'magenta'
  }
  return type ? colors[type] || 'default' : 'default'
}

function formatTime(time: string): string {
  if (!time) return '-'
  return time.substring(0, 16).replace('T', ' ')
}

// 搜索处理
function handleSearch() {
  examStore.loadPapers({ current: 1, paperName: searchKeyword.value || undefined })
}

function handleSearchChange() {
  if (!searchKeyword.value) {
    examStore.loadPapers({ current: 1 })
  }
}

// 分页
function handlePageChange(page: number) {
  examStore.loadPapers({ current: page, paperName: searchKeyword.value || undefined })
}

// 新建试卷
function handleCreatePaper() {
  isEditMode.value = false
  editingPaperId.value = null
  paperForm.value = {
    paperCode: paperList.value.length + 1,
    paperName: '',
    description: ''
  }
  paperModalVisible.value = true
}

// 编辑试卷信息
function handleEditInfo(paper: Paper) {
  isEditMode.value = true
  editingPaperId.value = paper.id
  paperForm.value = {
    paperCode: paper.paperCode,
    paperName: paper.paperName,
    description: paper.description || ''
  }
  paperModalVisible.value = true
}

// 编辑试卷（进入组卷页面）
async function handleEditPaper(paper: Paper) {
  await examStore.loadPaperDetail(paper.id)
  await examStore.loadAllQuestions()
  composeModalVisible.value = true
}

// 保存试卷基本信息
async function handleSavePaper() {
  if (!paperForm.value.paperName.trim()) {
    message.warning('请输入试卷名称')
    return
  }
  
  if (isEditMode.value && editingPaperId.value) {
    await examStore.updatePaper({
      id: editingPaperId.value,
      ...paperForm.value
    })
    message.success('试卷更新成功')
  } else {
    const newId = await examStore.addPaper(paperForm.value)
    if (newId) {
      message.success('试卷创建成功')
      // 自动进入组卷
      await examStore.loadPaperDetail(newId)
      await examStore.loadAllQuestions()
      paperModalVisible.value = false
      composeModalVisible.value = true
      return
    }
  }
  
  paperModalVisible.value = false
}

// 删除试卷
async function handleDeletePaper(id: number) {
  await examStore.deletePaper(id)
  message.success('试卷删除成功')
}

// 克隆题目（拖拽时创建新的题目关联）
function cloneQuestion(question: QuestionItem): PaperQuestion {
  return {
    id: Date.now(),
    paperId: currentPaper.value?.id || 0,
    questionId: question.id,
    score: 10,
    questionOrder: paperQuestionsList.value.length + 1,
    sectionName: getTypeName(question.questionType) + '题',
    question
  }
}

// 拖拽结束
async function handleDragEnd() {
  if (!currentPaper.value) return
  
  // 检查是否有新添加的题目
  const existingIds = new Set(currentPaper.value.questions.map(q => q.questionId))
  const newQuestions = paperQuestionsList.value.filter(pq => !existingIds.has(pq.questionId))
  
  // 添加新题目
  for (const pq of newQuestions) {
    await examStore.addQuestionToPaper({
      paperId: currentPaper.value.id,
      questionId: pq.questionId,
      score: pq.score,
      questionOrder: paperQuestionsList.value.indexOf(pq) + 1,
      sectionName: pq.sectionName || undefined
    })
  }
  
  // 更新顺序
  const questionIds = paperQuestionsList.value.map(pq => pq.questionId)
  await examStore.reorderPaperQuestions(currentPaper.value.id, questionIds)
}

// 添加题目
async function handleAddQuestion(question: QuestionItem) {
  if (!currentPaper.value) return
  
  await examStore.addQuestionToPaper({
    paperId: currentPaper.value.id,
    questionId: question.id,
    score: 10,
    questionOrder: (currentPaper.value.questions.length || 0) + 1,
    sectionName: getTypeName(question.questionType) + '题'
  })
  
  message.success('题目添加成功')
}

// 移除题目
async function handleRemoveQuestion(id: number) {
  await examStore.removeQuestionFromPaper(id)
  message.success('题目移除成功')
}

// 修改分数
async function handleScoreChange(id: number, score: number) {
  if (score && score > 0) {
    await examStore.updatePaperQuestionScore(id, score)
  }
}

// 初始化
onMounted(async () => {
  await examStore.loadPapers()
})
</script>

<style scoped>
.paper-manage-page {
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
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 10px;
  padding: 20px 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.stat-card__value {
  font-size: 28px;
  font-weight: 700;
  color: #1890ff;
  line-height: 1.2;
}

.stat-card--success .stat-card__value {
  color: #52c41a;
}

.stat-card--warning .stat-card__value {
  color: #fa8c16;
}

.stat-card__label {
  font-size: 13px;
  color: #8c8c8c;
  margin-top: 6px;
}

/* 内容区域 */
.content-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.toolbar {
  margin-bottom: 20px;
}

/* 表格样式 */
.paper-table {
  margin-bottom: 20px;
}

.paper-code {
  font-weight: 500;
  color: #8c8c8c;
}

.paper-name-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.paper-name {
  font-weight: 500;
  color: #262626;
}

.paper-desc {
  font-size: 12px;
  color: #8c8c8c;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.score-text {
  font-weight: 500;
  color: #1890ff;
}

.time-text {
  color: #8c8c8c;
  font-size: 13px;
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

/* 组卷弹窗 */
.compose-container {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 20px;
  height: 600px;
}

.compose-left,
.compose-right {
  display: flex;
  flex-direction: column;
  background: #fafafa;
  border-radius: 8px;
  overflow: hidden;
}

.compose-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.compose-panel-header h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
}

.compose-info {
  font-size: 13px;
  color: #666;
}

.compose-info .divider {
  margin: 0 8px;
  color: #d9d9d9;
}

.paper-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.question-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
}

.drag-handle {
  cursor: grab;
  color: #bfbfbf;
  padding: 4px;
}

.drag-handle:hover {
  color: #1890ff;
}

.question-order {
  flex-shrink: 0;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #1890ff;
  color: #fff;
  border-radius: 50%;
  font-size: 13px;
  font-weight: 600;
}

.question-main {
  flex: 1;
  min-width: 0;
}

.question-header {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.question-text {
  font-size: 14px;
  color: #262626;
  line-height: 1.6;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.question-meta {
  font-size: 12px;
  color: #8c8c8c;
}

.question-score {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}

.score-label {
  font-size: 13px;
  color: #8c8c8c;
}

.empty-paper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #bfbfbf;
}

.empty-paper .empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-hint {
  font-size: 12px;
  margin-top: 4px;
}

/* 题库列表 */
.filter-row {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.question-bank-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.bank-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.bank-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: #fff;
  border-radius: 6px;
  border: 1px solid #e8e8e8;
  transition: all 0.2s;
}

.bank-item:hover {
  border-color: #1890ff;
}

.bank-item.is-added {
  opacity: 0.6;
  background: #f5f5f5;
}

.bank-item-main {
  flex: 1;
  min-width: 0;
}

.bank-item-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.difficulty-badge {
  font-size: 11px;
  color: #8c8c8c;
}

.bank-item-text {
  font-size: 13px;
  color: #262626;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.empty-bank {
  text-align: center;
  padding: 40px 20px;
  color: #bfbfbf;
}

@media (max-width: 768px) {
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .compose-container {
    grid-template-columns: 1fr;
    height: auto;
  }
  
  .compose-left,
  .compose-right {
    height: 400px;
  }
}
</style>
