<template>
  <div class="app-panel-grid">
    <!-- 页面头部 -->
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="考试管理" title="题库管理" description="集中维护所有题目资源，支持多种题型的增删改查操作。">
        <template #actions>
          <a-button type="primary" @click="handleAdd">
            <template #icon><PlusOutlined /></template>
            新增题目
          </a-button>
        </template>
      </SectionHeader>
    </section>

    <!-- 统计卡片 -->
    <section class="app-kpi-grid">
      <MetricCard title="题目总数" :value="String(questionStats.total)" description="题库中所有可用题目。" tone="primary" />
      <MetricCard title="单选题" :value="String(questionStats.byType[2] || 0)" description="客观题，自动评分。" tone="success" />
      <MetricCard title="多选题" :value="String(questionStats.byType[3] || 0)" description="多项选择题型。" tone="warning" />
      <MetricCard title="主观题" :value="String((questionStats.byType[5] || 0) + (questionStats.byType[6] || 0))" description="简答与编程题。" tone="accent" />
    </section>

    <!-- 筛选和列表区域 -->
    <section class="app-surface-card app-section-card question-list-section">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索题目内容..."
          style="width: 280px"
          allow-clear
          @search="handleSearch"
          @pressEnter="handleSearch"
        />
        <a-select
          v-model:value="filterType"
          placeholder="题型筛选"
          style="width: 140px"
          allow-clear
          @change="handleSearch"
        >
          <a-select-option v-for="(name, key) in QUESTION_TYPE_MAP" :key="key" :value="Number(key)">
            {{ name }}
          </a-select-option>
        </a-select>
        <a-select
          v-model:value="filterDifficulty"
          placeholder="难度筛选"
          style="width: 140px"
          allow-clear
          @change="handleSearch"
        >
          <a-select-option v-for="(name, key) in DIFFICULTY_MAP" :key="key" :value="Number(key)">
            {{ name }}
          </a-select-option>
        </a-select>
        <a-button @click="handleReset">重置</a-button>
      </div>

      <!-- 题目列表 -->
      <a-spin :spinning="questionLoading">
        <div class="question-list">
          <div v-if="questions.length === 0" class="empty-state">
            <FileSearchOutlined class="empty-icon" />
            <p>暂无题目数据</p>
          </div>
          <article v-for="item in questions" :key="item.id" class="question-card">
            <div class="question-card__header">
              <div class="question-card__tags">
                <a-tag :color="getTypeColor(item.questionType)">{{ QUESTION_TYPE_MAP[item.questionType] }}</a-tag>
                <a-tag>{{ DIFFICULTY_MAP[item.difficulty] }}</a-tag>
              </div>
              <div class="question-card__actions">
                <a-button type="link" size="small" @click="handleView(item)">
                  <template #icon><EyeOutlined /></template>
                  查看
                </a-button>
                <a-button type="link" size="small" @click="handleEdit(item)">
                  <template #icon><EditOutlined /></template>
                  编辑
                </a-button>
                <a-popconfirm title="确定删除这道题目吗？" ok-text="确定" cancel-text="取消" @confirm="handleDelete(item.id)">
                  <a-button type="link" size="small" danger>
                    <template #icon><DeleteOutlined /></template>
                    删除
                  </a-button>
                </a-popconfirm>
              </div>
            </div>
            <div class="question-card__content">
              <p class="question-card__text">{{ item.questionContent }}</p>
              <!-- 选项展示 -->
              <div v-if="item.optionsText" class="question-card__options">
                <div v-for="opt in parseOptions(item.optionsText)" :key="opt.key" class="option-item">
                  <span class="option-key">{{ opt.key }}</span>
                  <span class="option-label">{{ opt.label }}</span>
                </div>
              </div>
            </div>
            <div class="question-card__footer">
              <span class="question-card__meta">
                <span class="meta-label">答案:</span>
                <span class="meta-value answer-text">{{ item.standardAnswer }}</span>
              </span>
              <span class="question-card__meta">
                <span class="meta-label">更新时间:</span>
                <span class="meta-value">{{ formatDate(item.updatedAt) }}</span>
              </span>
            </div>
          </article>
        </div>
      </a-spin>

      <!-- 分页 -->
      <div v-if="questionPagination.total > 0" class="pagination-wrapper">
        <a-pagination
          v-model:current="questionPagination.current"
          :total="questionPagination.total"
          :page-size="questionPagination.pageSize"
          show-size-changer
          :show-total="(total: number) => `共 ${total} 道题目`"
          @change="handlePageChange"
          @show-size-change="handleSizeChange"
        />
      </div>
    </section>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalMode === 'add' ? '新增题目' : modalMode === 'edit' ? '编辑题目' : '查看题目'"
      :width="720"
      :footer="modalMode === 'view' ? null : undefined"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
        :disabled="modalMode === 'view'"
      >
        <a-form-item label="题目类型" name="questionType">
          <a-select v-model:value="formData.questionType" placeholder="请选择题目类型" @change="handleTypeChange">
            <a-select-option v-for="(name, key) in QUESTION_TYPE_MAP" :key="key" :value="Number(key)">
              {{ name }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="难度等级" name="difficulty">
          <a-rate v-model:value="formData.difficulty" :count="5" allow-half />
          <span class="difficulty-text">{{ DIFFICULTY_MAP[formData.difficulty] || '请选择' }}</span>
        </a-form-item>

        <a-form-item label="题目内容" name="questionContent">
          <a-textarea
            v-model:value="formData.questionContent"
            placeholder="请输入题目内容"
            :rows="4"
            show-count
            :maxlength="500"
          />
        </a-form-item>

        <!-- 选项编辑（单选/多选/判断） -->
        <a-form-item v-if="needOptions" label="选项设置">
          <div class="options-editor">
            <div v-for="(opt, index) in formOptions" :key="index" class="option-editor-item">
              <a-input
                v-model:value="opt.key"
                style="width: 60px"
                placeholder="选项"
                :maxlength="1"
              />
              <a-input
                v-model:value="opt.label"
                style="flex: 1"
                placeholder="选项内容"
              />
              <a-button v-if="formOptions.length > 2" type="text" danger @click="removeOption(index)">
                <template #icon><MinusCircleOutlined /></template>
              </a-button>
            </div>
            <a-button type="dashed" block @click="addOption">
              <template #icon><PlusOutlined /></template>
              添加选项
            </a-button>
          </div>
        </a-form-item>

        <a-form-item label="标准答案" name="standardAnswer">
          <template v-if="formData.questionType === 2">
            <!-- 单选答案 -->
            <a-radio-group v-model:value="formData.standardAnswer">
              <a-radio v-for="opt in formOptions" :key="opt.key" :value="opt.key">{{ opt.key }}</a-radio>
            </a-radio-group>
          </template>
          <template v-else-if="formData.questionType === 3">
            <!-- 多选答案 -->
            <a-checkbox-group v-model:value="multiAnswers">
              <a-checkbox v-for="opt in formOptions" :key="opt.key" :value="opt.key">{{ opt.key }}</a-checkbox>
            </a-checkbox-group>
          </template>
          <template v-else-if="formData.questionType === 4">
            <!-- 判断答案 -->
            <a-radio-group v-model:value="formData.standardAnswer">
              <a-radio v-for="opt in formOptions" :key="opt.key" :value="opt.key">{{ opt.label }}</a-radio>
            </a-radio-group>
          </template>
          <template v-else>
            <!-- 其他题型 -->
            <a-textarea
              v-model:value="formData.standardAnswer"
              placeholder="请输入标准答案"
              :rows="3"
            />
          </template>
        </a-form-item>

        <a-form-item label="题目解析" name="analysis">
          <a-textarea
            v-model:value="formData.analysis"
            placeholder="请输入题目解析（可选）"
            :rows="3"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule } from 'ant-design-vue/es/form'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  EyeOutlined,
  FileSearchOutlined,
  MinusCircleOutlined
} from '@ant-design/icons-vue'
import MetricCard from '@/components/common/MetricCard.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useExamAdminStore } from '@/stores/exam/admin'
import type { QuestionItem, QuestionType, QuestionAddRequest, QuestionUpdateRequest } from '@/stores/exam/types'
import { QUESTION_TYPE_MAP, DIFFICULTY_MAP } from '@/stores/exam/types'

interface OptionItem {
  key: string
  label: string
}

const examStore = useExamAdminStore()
const { questions, questionPagination, questionStats, questionLoading } = storeToRefs(examStore)

// 筛选状态
const searchKeyword = ref('')
const filterType = ref<QuestionType | undefined>()
const filterDifficulty = ref<number | undefined>()

// 弹窗状态
const modalVisible = ref(false)
const modalMode = ref<'add' | 'edit' | 'view'>('add')
const formRef = ref<FormInstance>()
const currentEditId = ref<number | null>(null)

// 表单数据
const formData = reactive({
  questionType: undefined as QuestionType | undefined,
  difficulty: 3,
  questionContent: '',
  standardAnswer: '',
  analysis: ''
})

// 选项数据
const formOptions = ref<OptionItem[]>([
  { key: 'A', label: '' },
  { key: 'B', label: '' }
])

// 多选答案
const multiAnswers = ref<string[]>([])

// 是否需要选项
const needOptions = computed(() => {
  return [2, 3, 4].includes(formData.questionType as number)
})

// 表单校验规则
const formRules: Record<string, Rule[]> = {
  questionType: [{ required: true, message: '请选择题目类型' }],
  difficulty: [{ required: true, message: '请选择难度等级' }],
  questionContent: [{ required: true, message: '请输入题目内容' }],
  standardAnswer: [{ required: true, message: '请输入标准答案' }]
}

// 获取题型颜色
function getTypeColor(type: QuestionType): string {
  const colors: Record<number, string> = {
    1: 'blue',
    2: 'green',
    3: 'orange',
    4: 'cyan',
    5: 'purple',
    6: 'red',
    7: 'gold'
  }
  return colors[type] || 'default'
}

// 解析选项
function parseOptions(optionsText: string): OptionItem[] {
  try {
    return JSON.parse(optionsText)
  } catch {
    return []
  }
}

// 格式化日期
function formatDate(dateStr: string): string {
  if (!dateStr) return '--'
  return dateStr.substring(0, 16)
}

// 搜索
function handleSearch() {
  examStore.loadQuestions({
    current: 1,
    questionContent: searchKeyword.value || undefined,
    questionType: filterType.value,
    difficulty: filterDifficulty.value
  })
}

// 重置筛选
function handleReset() {
  searchKeyword.value = ''
  filterType.value = undefined
  filterDifficulty.value = undefined
  examStore.loadQuestions({ current: 1 })
}

// 分页变化
function handlePageChange(page: number) {
  examStore.loadQuestions({
    current: page,
    questionContent: searchKeyword.value || undefined,
    questionType: filterType.value,
    difficulty: filterDifficulty.value
  })
}

// 每页数量变化
function handleSizeChange(_current: number, size: number) {
  questionPagination.value.pageSize = size
  examStore.loadQuestions({
    current: 1,
    pageSize: size,
    questionContent: searchKeyword.value || undefined,
    questionType: filterType.value,
    difficulty: filterDifficulty.value
  })
}

// 重置表单
function resetForm() {
  formData.questionType = undefined
  formData.difficulty = 3
  formData.questionContent = ''
  formData.standardAnswer = ''
  formData.analysis = ''
  formOptions.value = [
    { key: 'A', label: '' },
    { key: 'B', label: '' }
  ]
  multiAnswers.value = []
  currentEditId.value = null
}

// 新增
function handleAdd() {
  resetForm()
  modalMode.value = 'add'
  modalVisible.value = true
}

// 查看
function handleView(item: QuestionItem) {
  fillForm(item)
  modalMode.value = 'view'
  modalVisible.value = true
}

// 编辑
function handleEdit(item: QuestionItem) {
  fillForm(item)
  modalMode.value = 'edit'
  modalVisible.value = true
}

// 填充表单
function fillForm(item: QuestionItem) {
  currentEditId.value = item.id
  formData.questionType = item.questionType
  formData.difficulty = item.difficulty
  formData.questionContent = item.questionContent
  formData.standardAnswer = item.standardAnswer
  formData.analysis = item.analysis || ''
  
  if (item.optionsText) {
    formOptions.value = parseOptions(item.optionsText)
  } else {
    formOptions.value = [
      { key: 'A', label: '' },
      { key: 'B', label: '' }
    ]
  }
  
  if (item.questionType === 3) {
    multiAnswers.value = item.standardAnswer.split(',')
  }
}

// 删除
async function handleDelete(id: number) {
  const success = await examStore.deleteQuestion(id)
  if (success) {
    message.success('删除成功')
  } else {
    message.error('删除失败')
  }
}

// 题型变化
function handleTypeChange(type: QuestionType) {
  if (type === 4) {
    // 判断题默认选项
    formOptions.value = [
      { key: 'A', label: '正确' },
      { key: 'B', label: '错误' }
    ]
  } else if ([2, 3].includes(type)) {
    // 单选/多选默认4个选项
    formOptions.value = [
      { key: 'A', label: '' },
      { key: 'B', label: '' },
      { key: 'C', label: '' },
      { key: 'D', label: '' }
    ]
  }
  formData.standardAnswer = ''
  multiAnswers.value = []
}

// 添加选项
function addOption() {
  const nextKey = String.fromCharCode(65 + formOptions.value.length)
  formOptions.value.push({ key: nextKey, label: '' })
}

// 删除选项
function removeOption(index: number) {
  formOptions.value.splice(index, 1)
}

// 监听多选答案变化
watch(multiAnswers, (val) => {
  if (formData.questionType === 3) {
    formData.standardAnswer = val.sort().join(',')
  }
})

// 弹窗确认
async function handleModalOk() {
  try {
    await formRef.value?.validate()
    
    if (modalMode.value === 'add') {
      const request: QuestionAddRequest = {
        questionType: formData.questionType!,
        difficulty: formData.difficulty,
        questionContent: formData.questionContent,
        standardAnswer: formData.standardAnswer,
        analysis: formData.analysis || undefined,
        optionsText: needOptions.value ? JSON.stringify(formOptions.value) : undefined
      }
      const success = await examStore.addQuestion(request)
      if (success) {
        message.success('新增成功')
        modalVisible.value = false
      } else {
        message.error('新增失败')
      }
    } else if (modalMode.value === 'edit' && currentEditId.value) {
      const request: QuestionUpdateRequest = {
        id: currentEditId.value,
        questionType: formData.questionType,
        difficulty: formData.difficulty,
        questionContent: formData.questionContent,
        standardAnswer: formData.standardAnswer,
        analysis: formData.analysis || undefined,
        optionsText: needOptions.value ? JSON.stringify(formOptions.value) : undefined
      }
      const success = await examStore.updateQuestion(request)
      if (success) {
        message.success('更新成功')
        modalVisible.value = false
      } else {
        message.error('更新失败')
      }
    }
  } catch {
    // 校验失败
  }
}

// 弹窗取消
function handleModalCancel() {
  modalVisible.value = false
}

onMounted(async () => {
  await examStore.loadQuestions()
})
</script>

<style scoped>
.question-list-section {
  display: grid;
  gap: var(--space-5);
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: var(--space-3);
}

.question-list {
  display: grid;
  gap: var(--space-4);
  min-height: 200px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--color-text-tertiary);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.question-card {
  padding: 20px;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  background: #ffffff;
  transition: box-shadow var(--transition-base), border-color var(--transition-base);
}

.question-card:hover {
  border-color: var(--color-primary);
  box-shadow: 0 4px 12px rgba(31, 95, 174, 0.08);
}

.question-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.question-card__tags {
  display: flex;
  gap: 8px;
}

.question-card__actions {
  display: flex;
  gap: 4px;
}

.question-card__content {
  margin-bottom: 16px;
}

.question-card__text {
  margin: 0 0 12px;
  color: var(--color-text-main);
  font-size: 15px;
  line-height: 1.7;
}

.question-card__options {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 8px;
}

.option-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px 12px;
  border-radius: var(--radius-xs);
  background: var(--color-bg-muted);
}

.option-key {
  flex-shrink: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--color-primary);
  color: white;
  font-size: 12px;
  font-weight: 600;
}

.option-label {
  color: var(--color-text-secondary);
  font-size: 14px;
  line-height: 1.5;
}

.question-card__footer {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
}

.question-card__meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.meta-label {
  color: var(--color-text-tertiary);
}

.meta-value {
  color: var(--color-text-secondary);
}

.answer-text {
  color: var(--color-success);
  font-weight: 600;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding-top: var(--space-3);
}

/* 选项编辑器 */
.options-editor {
  display: grid;
  gap: 12px;
}

.option-editor-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.difficulty-text {
  margin-left: 12px;
  color: var(--color-text-secondary);
  font-size: 14px;
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-bar > * {
    width: 100% !important;
  }

  .question-card__header {
    flex-direction: column;
    gap: 12px;
  }

  .question-card__options {
    grid-template-columns: 1fr;
  }
}
</style>
