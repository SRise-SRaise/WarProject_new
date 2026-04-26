<template>
  <div class="step-editor-page">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="header-info">
        <h1 class="experiment-title">{{ experimentTitle }}</h1>
        <p class="experiment-meta">
          <span class="meta-item">
            <FileTextOutlined />
            共 {{ totalSteps }} 个步骤
          </span>
          <span class="meta-item">
            <StarOutlined />
            满分 {{ totalScore }} 分
          </span>
        </p>
      </div>
      <div class="header-actions">
        <a-button @click="handlePreview" :disabled="totalSteps === 0">
          <EyeOutlined />
          预览效果
        </a-button>
        <a-button @click="handleSave" :loading="isSaving">
          <SaveOutlined />
          保存步骤
        </a-button>
      </div>
    </header>

    <!-- 步骤编辑区域 -->
    <main class="step-editor-content">
      <div class="steps-container">
        <!-- 加载状态 -->
        <div v-if="isLoading" class="loading-state">
          <a-spin size="large" />
          <p>正在加载实验步骤...</p>
        </div>

        <!-- 空状态提示 -->
        <div v-else-if="steps.length === 0" class="empty-state">
          <div class="empty-icon">
            <PlusSquareOutlined />
          </div>
          <h3>暂无实验步骤</h3>
          <p>点击下方按钮添加第一个实验步骤</p>
          <a-button type="primary" size="large" @click="addStep">
            <PlusOutlined />
            添加步骤 1
          </a-button>
        </div>

        <!-- 步骤卡片列表 -->
        <TransitionGroup name="step-list" tag="div" class="steps-list">
          <div
            v-for="(step, index) in steps"
            :key="step.id"
            class="step-card"
            :class="{ 'step-card--active': activeStepIndex === index }"
            @click="setActiveStep(index)"
          >
            <!-- 步骤头部 -->
            <div class="step-header">
              <div class="step-number">步骤 {{ index + 1 }}</div>
              <div class="step-actions">
                <a-button type="text" size="small" @click.stop="moveStepUp(index)" :disabled="index === 0">
                  <UpOutlined />
                </a-button>
                <a-button type="text" size="small" @click.stop="moveStepDown(index)" :disabled="index === steps.length - 1">
                  <DownOutlined />
                </a-button>
                <a-popconfirm
                  title="确定删除此步骤吗？"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm.stop="deleteStep(index)"
                >
                  <a-button type="text" size="small" danger>
                    <DeleteOutlined />
                  </a-button>
                </a-popconfirm>
              </div>
            </div>

            <!-- 步骤内容编辑器 -->
            <div class="step-content-editor">
              <!-- 步骤标题 -->
              <div class="form-row">
                <label class="form-label">步骤标题</label>
                <a-input
                  v-model:value="step.title"
                  placeholder="请输入步骤标题，如：变量声明与赋值"
                  size="large"
                  @click.stop
                />
              </div>

              <!-- 题目类型选择 -->
              <div class="form-row">
                <label class="form-label">题目类型</label>
                <a-select
                  v-model:value="step.type"
                  placeholder="请选择题目类型"
                  style="width: 200px"
                  @click.stop
                >
                  <a-select-option v-for="(name, key) in QUESTION_TYPE_NAMES" :key="key" :value="Number(key)">
                    {{ name }}
                  </a-select-option>
                </a-select>
                <span class="form-hint">
                  <TagOutlined />
                  分值：
                </span>
                <a-input-number
                  v-model:value="step.score"
                  :min="0"
                  :max="100"
                  style="width: 100px"
                  @click.stop
                />
                <span class="unit">分</span>
              </div>

              <!-- 题目内容（自适应宽度） -->
              <div class="form-row form-row--vertical form-row--wide">
                <label class="form-label">
                  题目内容
                  <span v-if="step.type === 2" class="label-tip">（使用 ____ 标记填空位置）</span>
                </label>
                <a-textarea
                  v-model:value="step.content"
                  :placeholder="getContentPlaceholder(step.type)"
                  :rows="4"
                  show-count
                  @click.stop
                />
                <!-- 填空题预览 -->
                <div v-if="step.type === 2 && step.content" class="fill-preview">
                  <span class="preview-label">预览：</span>
                  <span class="preview-content" v-html="CommonUtil.renderFillBlankPreview(step.content)"></span>
                </div>
              </div>

              <!-- 选项编辑（单选/多选） -->
              <div v-if="[1, 5].includes(step.type)" class="form-row form-row--vertical">
                <label class="form-label">选项设置</label>
                <div class="options-editor">
                  <div v-for="(opt, optIndex) in step.options" :key="optIndex" class="option-item">
                    <span class="option-key">{{ opt.key }}</span>
                    <a-input
                      v-model:value="opt.label"
                      placeholder="选项内容"
                      style="flex: 1"
                      @click.stop
                    />
                    <a-button
                      v-if="step.options.length > 2"
                      type="text"
                      danger
                      @click.stop="removeOption(step, optIndex)"
                    >
                      <MinusCircleOutlined />
                    </a-button>
                  </div>
                  <a-button type="dashed" block @click.stop="addOption(step)">
                    <PlusOutlined />
                    添加选项
                  </a-button>
                </div>
              </div>

              <!-- 代码语言选择（编程题） -->
              <div v-if="step.type === 3" class="form-row">
                <label class="form-label">代码语言</label>
                <a-select
                  v-model:value="step.language"
                  placeholder="选择语言"
                  style="width: 150px"
                  @click.stop
                >
                  <a-select-option v-for="(name, key) in LANGUAGE_NAMES" :key="key" :value="key">
                    {{ name }}
                  </a-select-option>
                </a-select>
                <a-checkbox
                  v-model:checked="step.allowPaste"
                  class="paste-checkbox"
                  @click.stop
                >
                  允许学生粘贴
                </a-checkbox>
                <span class="form-hint--warning">
                  <LockOutlined v-if="!step.allowPaste" />
                  <UnlockOutlined v-else />
                  {{ step.allowPaste ? '学生可以粘贴代码' : '禁止学生粘贴（防作弊）' }}
                </span>
              </div>

              <!-- 正确答案 -->
              <div class="form-row form-row--vertical">
                <label class="form-label">标准答案</label>

                <!-- 单选题答案（类型1） -->
                <a-radio-group v-if="step.type === 1" v-model:value="step.correctAnswer">
                  <a-radio v-for="opt in step.options" :key="opt.key" :value="opt.key">
                    {{ opt.key }}
                  </a-radio>
                </a-radio-group>

                <!-- 多选题答案（类型5） -->
                <a-checkbox-group v-else-if="step.type === 5" v-model:value="multiAnswersMap[step.id]">
                  <a-checkbox v-for="opt in step.options" :key="opt.key" :value="opt.key">
                    {{ opt.key }}
                  </a-checkbox>
                </a-checkbox-group>

                <!-- 判断题答案（类型6） -->
                <a-radio-group v-else-if="step.type === 6" v-model:value="step.correctAnswer">
                  <a-radio value="正确">正确</a-radio>
                  <a-radio value="错误">错误</a-radio>
                </a-radio-group>

                <!-- 填空题答案（类型2）：动态多个输入框 -->
                <div v-else-if="step.type === 2" class="fill-blank-answers-editor">
                  <div v-if="getFillBlankCount(step) === 0" class="fill-blank-hint">
                    <InfoCircleOutlined />
                    请先在"题目内容"中输入 ____ 标记填空位置
                  </div>
                  <div v-else class="fill-blank-inputs-list">
                    <div class="fill-blank-input-header">
                      <span class="fill-blank-label">请为每个空填写标准答案</span>
                      <span class="fill-blank-count">共 {{ getFillBlankCount(step) }} 个空</span>
                    </div>
                    <div
                      v-for="n in getFillBlankCount(step)"
                      :key="n"
                      class="fill-blank-input-row"
                    >
                      <span class="fill-blank-num">{{ n }}</span>
                      <a-input
                        :value="(fillBlankAnswerMap[step.id] || [])[n - 1] || ''"
                        :placeholder="`第 ${n} 空标准答案`"
                        @update:value="(val: string) => {
                          if (!fillBlankAnswerMap[step.id]) {
                            fillBlankAnswerMap[step.id] = Array(getFillBlankCount(step)).fill('')
                          }
                          fillBlankAnswerMap[step.id][n - 1] = val
                          fillBlankAnswerMap[step.id] = [...fillBlankAnswerMap[step.id]]
                        }"
                        @click.stop
                      />
                    </div>
                    <div class="fill-blank-preview-box">
                      <span class="fill-blank-preview-label">预览效果：</span>
                      <span class="fill-blank-preview-content" v-html="CommonUtil.renderFillBlankPreview(step.content)"></span>
                    </div>
                  </div>
                </div>

                <!-- 编程题答案（类型3）：代码编辑器样式 -->
                <div v-else-if="step.type === 3" class="code-editor-block">
                  <div class="code-editor-header">
                    <span class="language-tag-sm">{{ getLanguageLabel(step.language) }}</span>
                    <span class="editor-hint-sm">标准答案代码</span>
                  </div>
                  <textarea
                    v-model="step.correctAnswer"
                    class="code-editor-textarea"
                    :placeholder="`在此输入 ${getLanguageLabel(step.language)} 参考代码...`"
                    @click.stop
                  ></textarea>
                </div>

                <!-- 其他题型答案 -->
                <a-textarea
                  v-else
                  v-model:value="step.correctAnswer"
                  placeholder="请输入标准答案"
                  :rows="3"
                  @click.stop
                />
              </div>
            </div>

            <!-- 添加新步骤按钮（类似 Word 添加页面） -->
            <div class="add-step-divider" @click.stop>
              <div class="divider-line"></div>
              <a-button type="primary" ghost size="small" class="add-step-btn" @click="addStepAfter(index)">
                <PlusOutlined />
                添加步骤 {{ index + 2 }}
              </a-button>
              <div class="divider-line"></div>
            </div>
          </div>
        </TransitionGroup>

        <!-- 底部添加步骤按钮 -->
        <div v-if="steps.length > 0" class="add-step-footer">
          <a-button type="dashed" size="large" block @click="addStep">
            <PlusOutlined />
            添加步骤 {{ steps.length + 1 }}
          </a-button>
        </div>
      </div>

      <!-- 侧边栏：步骤概览 -->
      <aside class="steps-sidebar">
        <div class="sidebar-card">
          <h3 class="sidebar-title">
            <UnorderedListOutlined />
            步骤概览
          </h3>
          <div class="steps-summary">
            <div
              v-for="(step, index) in steps"
              :key="step.id"
              class="summary-item"
              :class="{ 'summary-item--active': activeStepIndex === index }"
              @click="setActiveStep(index)"
            >
              <span class="summary-number">{{ index + 1 }}</span>
              <div class="summary-info">
                <span class="summary-title">{{ step.title || '未命名步骤' }}</span>
                <span class="summary-type">{{ QUESTION_TYPE_NAMES[step.type] || '请选择类型' }}</span>
              </div>
              <span class="summary-score">{{ step.score }}分</span>
            </div>
            <div v-if="steps.length === 0" class="summary-empty">
              暂无步骤
            </div>
          </div>
        </div>

        <div class="sidebar-card">
          <h3 class="sidebar-title">
            <BarChartOutlined />
            题型分布
          </h3>
          <div class="type-distribution">
            <div v-for="(count, type) in typeDistribution" :key="type" class="type-bar">
              <span class="type-name">{{ QUESTION_TYPE_NAMES[type as unknown as QuestionType] || type }}</span>
              <div class="bar-track">
                <div class="bar-fill" :style="{ width: getBarWidth(count) + '%' }"></div>
              </div>
              <span class="type-count">{{ count }}</span>
            </div>
          </div>
        </div>
      </aside>
    </main>

    <!-- 预览弹窗 -->
    <a-modal
      v-model:open="previewVisible"
      title="学生端预览效果"
      :width="900"
      :footer="null"
    >
      <div class="preview-content">
        <p class="preview-tip">以下是学生在答题页面看到的效果（仅供参考）</p>
        <div class="preview-steps">
          <div v-for="(step, index) in steps" :key="step.id" class="preview-step">
            <div class="preview-step-header">
              <span class="preview-step-number">第 {{ index + 1 }} 步</span>
              <a-tag>{{ QUESTION_TYPE_NAMES[step.type] }}</a-tag>
              <span class="preview-step-score">{{ step.score }}分</span>
            </div>
            <h4 class="preview-step-title">{{ step.title }}</h4>
            <p class="preview-step-content">{{ step.content || '(未填写题目内容)' }}</p>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  FileTextOutlined,
  StarOutlined,
  EyeOutlined,
  SaveOutlined,
  PlusOutlined,
  PlusSquareOutlined,
  UpOutlined,
  DownOutlined,
  DeleteOutlined,
  MinusCircleOutlined,
  TagOutlined,
  InfoCircleOutlined,
  LockOutlined,
  UnlockOutlined,
  UnorderedListOutlined,
  BarChartOutlined
} from '@ant-design/icons-vue'
import { CommonUtil } from '@/utils'
import type {
  ExperimentStepEditItem,
  QuestionType,
  QuestionOption,
  ProgrammingLanguage,
  StepEditSaveRequest
} from '@/stores/experiment/types'
import { QUESTION_TYPE_NAMES, LANGUAGE_NAMES } from '@/stores/experiment/types'
import { getEduExperimentVoById } from '@/api/eduExperimentController'
import { listEduExperimentItemVoByPage, addEduExperimentItem, updateEduExperimentItem, deleteEduExperimentItem } from '@/api/eduExperimentItemController'

const route = useRoute()
const router = useRouter()

// 页面状态
const experimentId = computed(() => String(route.params.id))
const experimentTitle = ref('')
const isSaving = ref(false)
const activeStepIndex = ref(0)
const previewVisible = ref(false)
const isLoading = ref(false)

// 步骤列表
const steps = ref<ExperimentStepEditItem[]>([])

// 多选题答案映射
const multiAnswersMap = reactive<Record<string, string[]>>({})

// 填空题答案映射（每个步骤的答案数组，key 为 step.id）
const fillBlankAnswerMap = reactive<Record<string, string[]>>({})

// 填空题最大数量（与 FillBlankQuestion.vue 保持一致）
const FILL_BLANK_MAX = 5

// 获取步骤的填空数量
function getFillBlankCount(step: ExperimentStepEditItem): number {
  if (!step.content) return 0
  const matches = step.content.match(/____/g)
  return matches ? Math.min(matches.length, FILL_BLANK_MAX) : 0
}

// 获取步骤的标准答案数组（用于渲染）
function getStepAnswerList(step: ExperimentStepEditItem): string[] {
  if (fillBlankAnswerMap[step.id]) return fillBlankAnswerMap[step.id]
  // 兼容旧数据：从 correctAnswer 解析（按逗号分隔，与学生端 getAnswers().join(',') 对应）
  if (step.correctAnswer) {
    return step.correctAnswer.split(',')
  }
  return []
}

// 同步填空答案到 step.correctAnswer
function syncFillBlankAnswer(step: ExperimentStepEditItem) {
  const list = fillBlankAnswerMap[step.id] || []
  step.correctAnswer = list.join(',')
}

// 计算属性
const totalSteps = computed(() => steps.value.length)

const totalScore = computed(() => {
  return steps.value.reduce((sum, step) => sum + step.score, 0)
})

const typeDistribution = computed(() => {
  const dist: Record<number, number> = {}
  steps.value.forEach(step => {
    dist[step.type] = (dist[step.type] || 0) + 1
  })
  return dist
})

// 方法
function setActiveStep(index: number) {
  activeStepIndex.value = index
  // 滚动到对应步骤
  setTimeout(() => {
    const cards = document.querySelectorAll('.step-card')
    if (cards[index]) {
      cards[index].scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  }, 100)
}

function getContentPlaceholder(type: QuestionType): string {
  if (type === 1) {
    return '请输入题目内容'
  }
  if (type === 2) {
    return '请输入题目内容，使用 ____ 标记填空位置。例如：声明一个整型变量 x = ____'
  }
  if (type === 3) {
    return '请输入题目内容'
  }
  if (type === 4) {
    return '请输入判断题内容（正确/错误）'
  }
  return '请输入题目内容'
}

function getLanguageLabel(lang: string | undefined): string {
  const labels: Record<string, string> = {
    java: 'Java',
    javascript: 'JavaScript',
    python: 'Python',
    sql: 'SQL',
    html: 'HTML',
    css: 'CSS',
    text: '纯文本'
  }
  return labels[lang || 'java'] || lang || 'Java'
}

function addOption(step: ExperimentStepEditItem) {
  const nextKey = String.fromCharCode(65 + step.options!.length)
  step.options!.push({ key: nextKey, label: '' })
}

function removeOption(step: ExperimentStepEditItem, index: number) {
  step.options!.splice(index, 1)
  // 重新排列选项字母
  step.options!.forEach((opt, i) => {
    opt.key = String.fromCharCode(65 + i)
  })
}

function addStep() {
  const newStep = createEmptyStep(steps.value.length + 1)
  steps.value.push(newStep)
  multiAnswersMap[newStep.id] = []
  fillBlankAnswerMap[newStep.id] = []
  activeStepIndex.value = steps.value.length - 1
  scrollToBottom()
}

function addStepAfter(index: number) {
  const newStep = createEmptyStep(index + 2)
  // 重新编号后续步骤
  steps.value.splice(index + 1, 0, newStep)
  reindexSteps()
  multiAnswersMap[newStep.id] = []
  fillBlankAnswerMap[newStep.id] = []
  activeStepIndex.value = index + 1
  scrollToBottom()
}

async function deleteStep(index: number) {
  const step = steps.value[index]
  delete multiAnswersMap[step.id]
  delete fillBlankAnswerMap[step.id]

  // 如果步骤已保存到后端，则删除
  if (step.id && !step.id.startsWith('step-')) {
    try {
      await deleteEduExperimentItem({ id: step.id } as any)
    } catch (error) {
      console.error('删除步骤失败:', error)
    }
  }

  steps.value.splice(index, 1)
  reindexSteps()
  if (activeStepIndex.value >= steps.value.length) {
    activeStepIndex.value = Math.max(0, steps.value.length - 1)
  }
}

function moveStepUp(index: number) {
  if (index > 0) {
    [steps.value[index], steps.value[index - 1]] = [steps.value[index - 1], steps.value[index]]
    reindexSteps()
    activeStepIndex.value = index - 1
  }
}

function moveStepDown(index: number) {
  if (index < steps.value.length - 1) {
    [steps.value[index], steps.value[index + 1]] = [steps.value[index + 1], steps.value[index]]
    reindexSteps()
    activeStepIndex.value = index + 1
  }
}

function reindexSteps() {
  steps.value.forEach((step, index) => {
    step.stepNo = index + 1
  })
}

function scrollToBottom() {
  setTimeout(() => {
    window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' })
  }, 100)
}

function createEmptyStep(stepNo: number): ExperimentStepEditItem {
  return {
    id: CommonUtil.generateId('step'),
    stepNo,
    title: '',
    type: 1,
    content: '',
    options: [
      { key: 'A', label: '' },
      { key: 'B', label: '' }
    ],
    correctAnswer: '',
    score: 10,
    allowPaste: true,
    language: 'java'
  }
}

function getBarWidth(count: number): number {
  if (totalSteps.value === 0) return 0
  return Math.round((count / totalSteps.value) * 100)
}

// 监听多选题答案变化
watch(multiAnswersMap, (newMap) => {
  steps.value.forEach(step => {
    if (step.type === 5 && newMap[step.id]) {
      step.correctAnswer = newMap[step.id].sort().join(',')
    }
  })
}, { deep: true })

// 监听填空题答案变化，同步到 step.correctAnswer
watch(fillBlankAnswerMap, () => {
  steps.value.forEach(step => {
    if (step.type === 2) {
      syncFillBlankAnswer(step)
    }
  })
}, { deep: true })

// 加载实验数据
async function loadExperiment() {
  isLoading.value = true
  try {
    const expId = experimentId.value
    console.log('加载实验 ID:', expId)

    // 加载实验基本信息
    const expRes: any = await getEduExperimentVoById({ id: expId } as any)
    console.log('实验信息响应:', expRes)
    // 兼容不同数据格式
    const expData = expRes?.data?.data || expRes?.data
    if (expData) {
      experimentTitle.value = expData.name || '未知实验'
    } else {
      experimentTitle.value = '未知实验'
    }

    // 加载实验步骤（pageSize 设为足够大的值以加载所有步骤）
    const itemRes: any = await listEduExperimentItemVoByPage({
      current: 1,
      pageSize: 1000,
      sortField: 'sortOrder',
      sortOrder: 'ascend',
      experimentId: Number(expId)
    } as any)
    console.log('实验步骤响应:', itemRes)

    // 过滤出当前实验的步骤（使用更稳健的比较方式）
    const currentExpId = Number(expId)
    console.log('currentExpId type:', typeof currentExpId, 'value:', currentExpId)

    // 尝试多种方式获取 records
    let allRecords = []
    if (itemRes?.data?.records) {
      allRecords = itemRes.data.records
    } else if (itemRes?.data?.data?.records) {
      allRecords = itemRes.data.data.records
    } else if (Array.isArray(itemRes?.data)) {
      allRecords = itemRes.data
    }
    console.log('所有步骤记录:', allRecords)
    console.log('allRecords length:', allRecords.length)

    // 避免 NaN 比较，使用 String 比较
    const currentExpIdStr = String(currentExpId)
    const records = allRecords.filter((item: any) => {
      const itemExpId = item.experimentId
      const match = String(itemExpId) === currentExpIdStr || Number(itemExpId) === currentExpId
      console.log(`过滤: item.experimentId=${itemExpId}, currentExpId=${currentExpId}, match=${match}`)
      return match
    })
    console.log(`过滤后的步骤记录数 (experimentId=${currentExpId}):`, records.length)

    steps.value = records.map((item: any) => {
      // 从 questionContent 中解析选项内容（格式：题目内容 + \n\n + A. xxx\nB. xxx\n...）
      let content = item.questionContent || ''
      let options: any[] = []

      // 尝试从内容中解析选项
      const optionsMatch = content.match(/\n\n([A-Z]\.\s*.+(?:\n[A-Z]\.\s*.+)*)$/)
      if (optionsMatch) {
        // 分离题目内容和选项
        content = content.substring(0, optionsMatch.index)
        const optionLines = optionsMatch[1].split('\n')
        options = optionLines
          .map(line => {
            const match = line.match(/^([A-Z])\.\s*(.+)$/)
            if (match) {
              return { key: match[1], label: match[2] }
            }
            return null
          })
          .filter(Boolean)
      }

      // 如果没有解析到选项，提供默认选项（根据题目类型）
      const questionType = item.questionType || 1
      if (options.length === 0 && [1, 5].includes(questionType)) {
        options = [
          { key: 'A', label: '' },
          { key: 'B', label: '' }
        ]
      }

      const step: ExperimentStepEditItem = {
        id: String(item.id),
        stepNo: item.sortOrder || 1,
        title: item.itemName || '',
        type: (item.questionType || 1) as QuestionType,
        content: content,
        correctAnswer: item.standardAnswer || '',
        score: item.maxScore || 10,
        allowPaste: true,
        language: 'java',
        options: options
      }

      // 多选题答案初始化
      if (step.type === 5 && step.correctAnswer) {
        multiAnswersMap[step.id] = step.correctAnswer.split(',').filter(Boolean)
      }

      // 填空题答案初始化（兼容旧数据）
      if (step.type === 2 && step.correctAnswer) {
        fillBlankAnswerMap[step.id] = step.correctAnswer.split(',')
      } else if (step.type === 2) {
        fillBlankAnswerMap[step.id] = []
      }

      return step
    })

    // 更新步骤序号
    reindexSteps()
  } catch (error: any) {
    console.error('加载实验步骤失败:', error)
    // 显示具体的错误信息
    const errorMsg = error?.response?.data?.message || error?.message || '加载失败，请检查后端服务'
    message.error(errorMsg)
  } finally {
    isLoading.value = false
  }
}

// 保存步骤
async function handleSave() {
  // 调试：打印所有步骤状态
  console.log('=== 开始保存 ===')
  console.log('steps.value:', JSON.parse(JSON.stringify(steps.value)))
  console.log('steps.value.map(s => ({id: s.id, title: s.title})):', steps.value.map((s: any) => ({id: s.id, title: s.title})))

  // 校验
  for (let i = 0; i < steps.value.length; i++) {
    const step = steps.value[i]
    if (!step.title.trim()) {
      message.error(`请填写第 ${i + 1} 步的标题`)
      activeStepIndex.value = i
      return
    }
    if (!step.content.trim()) {
      message.error(`请填写第 ${i + 1} 步的题目内容`)
      activeStepIndex.value = i
      return
    }
  }

  isSaving.value = true
  try {
    const savedIds: string[] = []

    // 保存每个步骤
    for (const step of steps.value) {
      console.log(`处理步骤: id="${step.id}", typeof id=${typeof step.id}`)

      // 将选项内容拼接到题目内容中（单选/多选题）
      let questionContent = step.content
      if ([1, 5].includes(step.type) && step.options && step.options.length > 0) {
        const optionsStr = step.options
          .filter(opt => opt.label.trim())
          .map(opt => `${opt.key}. ${opt.label}`)
          .join('\n')
        if (optionsStr) {
          questionContent = step.content + '\n\n' + optionsStr
        }
      }
      // 编程题：将语言标识追加到末尾
      if (step.type === 3 && step.language) {
        questionContent = questionContent + '\n[LANG:' + step.language + ']'
      }

      const requestData: any = {
        experimentId: Number(experimentId.value),
        sortOrder: step.stepNo,
        itemName: step.title,
        questionType: step.type,
        questionContent: questionContent,
        standardAnswer: step.correctAnswer || '',
        maxScore: step.score,
        itemStatus: 1
      }

      // 判断是新增还是更新（临时ID以 step_ 或 step- 开头）
      const isNewStep = !step.id || step.id.startsWith('step_') || step.id.startsWith('step-')
      console.log(`保存步骤 ${step.title}, step.id="${step.id}", isNewStep=${isNewStep}`)

      if (!isNewStep) {
        // 更新现有步骤
        const updateData = {
          ...requestData,
          id: Number(step.id)
        }
        console.log('更新步骤:', updateData)
        const res: any = await updateEduExperimentItem(updateData)
        console.log('更新结果:', res)
        if (!res.data) {
          throw new Error('更新步骤失败，记录可能不存在')
        }
        savedIds.push(step.id)
      } else {
        // 新增步骤
        console.log('新增步骤:', requestData)
        const res: any = await addEduExperimentItem(requestData)
        console.log('新增结果:', res)
        // 更新本地 ID（如果是临时 ID）
        if (res.data) {
          const newId = String(res.data)
          if (step.id.startsWith('step_') || step.id.startsWith('step-')) {
            step.id = newId
          }
          savedIds.push(newId)
        }
      }
    }

    console.log('保存成功，步骤 IDs:', savedIds)
    message.success('保存成功')

    // 保存后重新加载数据以确保一致性
    await loadExperiment()
  } catch (error: any) {
    console.error('保存失败:', error)
    const errorMsg = error?.response?.data?.message || error?.message || '保存失败'
    message.error(errorMsg)
  } finally {
    isSaving.value = false
  }
}

// 预览
function handlePreview() {
  previewVisible.value = true
}

// 初始化
onMounted(() => {
  loadExperiment()
})
</script>

<style scoped>
.step-editor-page {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 页面头部 */
.page-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.experiment-title {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
}

.experiment-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  margin: 0;
  font-size: 14px;
  color: #8c8c8c;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 内容区域 */
.step-editor-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 空状态 */
.loading-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 40px;
  background: #fff;
  border-radius: 12px;
  text-align: center;
}

.loading-state p {
  margin-top: 16px;
  color: #8c8c8c;
  font-size: 14px;
}

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 40px;
  background: #fff;
  border-radius: 12px;
  border: 2px dashed #d9d9d9;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  color: #d9d9d9;
  margin-bottom: 16px;
}

.empty-state h3 {
  margin: 0 0 8px;
  font-size: 18px;
  color: #595959;
}

.empty-state p {
  margin: 0 0 24px;
  color: #8c8c8c;
}

/* 步骤列表 */
.steps-container {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.steps-list {
  display: flex;
  flex-direction: column;
}

/* 步骤卡片 */
.step-card {
  position: relative;
  margin-bottom: 0;
  background: #fff;
  border-radius: 12px;
  border: 2px solid transparent;
  transition: all 0.2s ease;
  cursor: pointer;
}

.step-card:hover {
  border-color: #d9d9d9;
}

.step-card--active {
  border-color: var(--color-primary, #1890ff);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.step-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
  border-radius: 10px 10px 0 0;
}

.step-number {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-primary, #1890ff);
}

.step-actions {
  display: flex;
  gap: 4px;
}

/* 步骤内容编辑器 */
.step-content-editor {
  padding: 20px;
}

.form-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.form-row--vertical {
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}

.form-row--wide {
  width: 100%;
}

.form-row--wide :deep(.ant-input-textarea),
.form-row--wide :deep(.ant-input-textarea textarea) {
  width: 100%;
  max-width: 100%;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
  min-width: 80px;
}

.label-tip {
  font-weight: 400;
  color: #8c8c8c;
  font-size: 12px;
}

.form-hint {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #8c8c8c;
}

.form-hint--warning {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #fa8c16;
}

.paste-checkbox {
  margin-left: 16px;
}

.unit {
  color: #8c8c8c;
}

/* 填空预览 */
.fill-preview {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px 16px;
  background: #f6f8fa;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.8;
}

.preview-label {
  color: #8c8c8c;
  flex-shrink: 0;
}

.preview-content {
  flex: 1;
}

:deep(.preview-blank) {
  display: inline-flex;
  align-items: center;
  margin: 0 4px;
}

:deep(.preview-blank-num) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #1890ff;
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  margin-right: 4px;
}

:deep(.preview-blank-line) {
  border-bottom: 2px solid #1890ff;
  color: transparent;
  letter-spacing: 2px;
}

/* 填空题答案编辑器 */
.fill-blank-answers-editor {
  width: 100%;
}

.fill-blank-hint {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fffbe6;
  border: 1px solid #ffe58f;
  border-radius: 6px;
  font-size: 14px;
  color: #fa8c16;
}

.fill-blank-inputs-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.fill-blank-input-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.fill-blank-label {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
}

.fill-blank-count {
  font-size: 13px;
  color: #8c8c8c;
}

.fill-blank-input-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.fill-blank-num {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #e6f7ff;
  color: #1890ff;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.fill-blank-preview-box {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px 16px;
  background: #f6f8fa;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.8;
  margin-top: 4px;
}

.fill-blank-preview-label {
  color: #8c8c8c;
  flex-shrink: 0;
}

.fill-blank-preview-content {
  flex: 1;
}

/* 选项编辑器 */
.options-editor {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.option-key {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--color-primary, #1890ff);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

/* 添加步骤分隔线 */
.add-step-divider {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  gap: 16px;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: #d9d9d9;
}

.add-step-btn {
  flex-shrink: 0;
}

/* 底部添加按钮 */
.add-step-footer {
  padding: 16px 0;
}

/* 侧边栏 */
.steps-sidebar {
  position: sticky;
  top: 88px;
  height: fit-content;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sidebar-card {
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e8e8e8;
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

/* 步骤概览 */
.steps-summary {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  cursor: pointer;
  transition: all 0.2s ease;
}

.summary-item:hover {
  border-color: var(--color-primary, #1890ff);
  background: #f0f7ff;
}

.summary-item--active {
  border-color: var(--color-primary, #1890ff);
  background: #e6f7ff;
}

.summary-number {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #e6f7ff;
  color: var(--color-primary, #1890ff);
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.summary-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.summary-title {
  font-size: 13px;
  color: #262626;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.summary-type {
  font-size: 11px;
  color: #8c8c8c;
}

.summary-score {
  font-size: 12px;
  color: #8c8c8c;
  flex-shrink: 0;
}

.summary-empty {
  padding: 20px;
  text-align: center;
  color: #bfbfbf;
  font-size: 14px;
}

/* 题型分布 */
.type-distribution {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.type-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.type-name {
  width: 60px;
  font-size: 12px;
  color: #595959;
  flex-shrink: 0;
}

.bar-track {
  flex: 1;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: var(--color-primary, #1890ff);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.type-count {
  width: 20px;
  font-size: 12px;
  color: #8c8c8c;
  text-align: right;
  flex-shrink: 0;
}

/* 预览弹窗 */
.preview-content {
  padding: 8px 0;
}

.preview-tip {
  margin: 0 0 16px;
  color: #8c8c8c;
  font-size: 14px;
}

.preview-steps {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.preview-step {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
}

.preview-step-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.preview-step-number {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-primary, #1890ff);
}

.preview-step-score {
  margin-left: auto;
  font-size: 13px;
  color: #8c8c8c;
}

.preview-step-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.preview-step-content {
  margin: 0;
  font-size: 14px;
  color: #595959;
  line-height: 1.6;
}

/* 过渡动画 */
.step-list-enter-active,
.step-list-leave-active {
  transition: all 0.3s ease;
}

.step-list-enter-from,
.step-list-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 响应式 */
@media (max-width: 1024px) {
  .step-editor-content {
    grid-template-columns: 1fr;
  }
  
  .steps-sidebar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    flex-direction: row;
    padding: 12px;
    background: #fff;
    border-top: 1px solid #e8e8e8;
    box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
    z-index: 99;
    overflow-x: auto;
  }
  
  .sidebar-card {
    min-width: 200px;
  }
}

/* 代码编辑器样式（教师端编程题题目内容） */
.code-label-tip {
  color: #fa541c;
  font-size: 12px;
  margin-left: 8px;
}

.code-editor-block {
  width: 100%;
  border: 1px solid #3c3c3c;
  border-radius: 8px;
  overflow: hidden;
  background: #1e1e1e;
}

.code-editor-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 14px;
  background: #252526;
  border-bottom: 1px solid #3c3c3c;
}

.language-tag-sm {
  padding: 3px 8px;
  background: #0066cc;
  color: #fff;
  font-size: 12px;
  border-radius: 4px;
  font-weight: 500;
  flex-shrink: 0;
}

.editor-hint-sm {
  font-size: 12px;
  color: #888;
}

.code-editor-textarea {
  width: 100%;
  min-height: 200px;
  padding: 16px;
  background: #1e1e1e;
  color: #d4d4d4;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  border: none;
  resize: vertical;
  outline: none;
  display: block;
}

.code-editor-textarea::placeholder {
  color: #6a6a6a;
}
</style>
