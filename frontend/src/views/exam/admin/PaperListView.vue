<template>
  <div class="paper-manage">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="header-content">
        <div class="header-info">
          <span class="header-eyebrow">试卷管理</span>
          <h1 class="header-title">组卷与试卷维护</h1>
          <p class="header-desc">创建和管理试卷，支持从题库拖拽添加题目，灵活组卷</p>
        </div>
        <a-button type="primary" size="large" @click="handleCreatePaper">
          <template #icon><PlusOutlined /></template>
          新建试卷
        </a-button>
      </div>
      <!-- 统计卡片 -->
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-value">{{ paperList.length }}</div>
          <div class="stat-label">试卷总数</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ paperList.filter(p => p.questionCount > 0).length }}</div>
          <div class="stat-label">已组卷</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ paperList.filter(p => p.questionCount === 0).length }}</div>
          <div class="stat-label">待组卷</div>
        </div>
      </div>
    </header>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <a-input-search
        v-model:value="searchKeyword"
        placeholder="搜索试卷名称..."
        style="width: 320px"
        allow-clear
        @search="handleSearch"
        @change="handleSearchChange"
      />
    </div>

    <!-- 试卷列表 -->
    <div class="paper-list" v-if="!paperLoading">
      <div
        v-for="paper in paperList"
        :key="paper.id"
        class="paper-card"
        @click="handleEditPaper(paper)"
      >
        <div class="paper-card-header">
          <div class="paper-code">No.{{ paper.paperCode }}</div>
          <a-tag :color="paper.questionCount > 0 ? 'green' : 'orange'">
            {{ paper.questionCount > 0 ? '已组卷' : '待组卷' }}
          </a-tag>
        </div>
        <h3 class="paper-name">{{ paper.paperName }}</h3>
        <p class="paper-desc">{{ paper.description || '暂无描述' }}</p>
        <div class="paper-stats">
          <div class="paper-stat">
            <FileTextOutlined />
            <span>{{ paper.questionCount }} 道题</span>
          </div>
          <div class="paper-stat">
            <TrophyOutlined />
            <span>{{ paper.totalScore }} 分</span>
          </div>
        </div>
        <div class="paper-footer">
          <span class="paper-time">更新于 {{ formatTime(paper.updatedAt) }}</span>
          <div class="paper-actions" @click.stop>
            <a-button type="link" size="small" @click="handleEditPaper(paper)">
              <EditOutlined /> 编辑
            </a-button>
            <a-popconfirm
              title="确定删除此试卷吗？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleDeletePaper(paper.id)"
            >
              <a-button type="link" size="small" danger>
                <DeleteOutlined /> 删除
              </a-button>
            </a-popconfirm>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="paperList.length === 0" class="empty-state">
        <FileSearchOutlined class="empty-icon" />
        <p>暂无试卷</p>
        <a-button type="primary" @click="handleCreatePaper">创建第一份试卷</a-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else class="loading-state">
      <a-spin size="large" />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="paperPagination.total > paperPagination.pageSize">
      <a-pagination
        v-model:current="paperPagination.current"
        :total="paperPagination.total"
        :page-size="paperPagination.pageSize"
        show-quick-jumper
        @change="handlePageChange"
      />
    </div>

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
              <a-select-option :value="4">判断</a-select-option>
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
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  FileTextOutlined,
  TrophyOutlined,
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
    4: 'cyan',
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
.paper-manage {
  padding: 24px;
  min-height: 100vh;
  background: var(--color-bg-container, #f5f7fa);
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  color: #fff;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-eyebrow {
  font-size: 13px;
  opacity: 0.8;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.header-title {
  font-size: 28px;
  font-weight: 600;
  margin: 8px 0;
}

.header-desc {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.stats-row {
  display: flex;
  gap: 16px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 16px 24px;
  min-width: 120px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  font-size: 13px;
  opacity: 0.8;
  margin-top: 4px;
}

.search-bar {
  margin-bottom: 20px;
}

.paper-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.paper-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e8e8e8;
}

.paper-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.paper-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.paper-code {
  font-size: 13px;
  color: #8c8c8c;
  font-weight: 500;
}

.paper-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #262626;
}

.paper-desc {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0 0 16px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.paper-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.paper-stat {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #595959;
}

.paper-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.paper-time {
  font-size: 12px;
  color: #bfbfbf;
}

.paper-actions {
  display: flex;
  gap: 4px;
}

.empty-state,
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  grid-column: 1 / -1;
}

.empty-icon {
  font-size: 48px;
  color: #d9d9d9;
  margin-bottom: 16px;
}

.empty-state p {
  color: #8c8c8c;
  margin-bottom: 16px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 组卷弹窗样式 */
.compose-container {
  display: flex;
  gap: 20px;
  height: 70vh;
}

.compose-left,
.compose-right {
  display: flex;
  flex-direction: column;
  background: #fafafa;
  border-radius: 8px;
  overflow: hidden;
}

.compose-left {
  flex: 1.2;
}

.compose-right {
  flex: 1;
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
  font-size: 16px;
  font-weight: 600;
}

.compose-info {
  font-size: 13px;
  color: #8c8c8c;
}

.compose-info .divider {
  margin: 0 8px;
  color: #d9d9d9;
}

.filter-row {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.paper-content,
.question-bank-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.question-list,
.bank-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-height: 100%;
}

.question-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background: #fff;
  border-radius: 8px;
  padding: 12px;
  border: 1px solid #e8e8e8;
}

.drag-handle {
  cursor: grab;
  color: #bfbfbf;
  padding: 4px;
}

.drag-handle:hover {
  color: #595959;
}

.question-order {
  width: 28px;
  height: 28px;
  background: #667eea;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
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
  color: #8c8c8c;
}

.empty-paper .empty-icon {
  font-size: 48px;
  color: #d9d9d9;
  margin-bottom: 16px;
}

.empty-hint {
  font-size: 12px;
  color: #bfbfbf;
}

.bank-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background: #fff;
  border-radius: 8px;
  padding: 12px;
  border: 1px solid #e8e8e8;
  transition: all 0.2s;
}

.bank-item:hover {
  border-color: #667eea;
}

.bank-item.is-added {
  opacity: 0.6;
  background: #fafafa;
}

.bank-item-main {
  flex: 1;
  min-width: 0;
}

.bank-item-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.difficulty-badge {
  font-size: 12px;
  color: #8c8c8c;
}

.bank-item-text {
  font-size: 13px;
  color: #595959;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.empty-bank {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100px;
  color: #8c8c8c;
}

/* 新建试卷表单 */
.paper-form {
  padding: 16px 0;
}

/* 拖拽样式 */
.sortable-ghost {
  opacity: 0.5;
  background: #e6f7ff;
}

.sortable-chosen {
  background: #f0f5ff;
}
</style>
