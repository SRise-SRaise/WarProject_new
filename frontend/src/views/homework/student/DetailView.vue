<template>
  <div class="app-page-shell app-panel-grid" v-if="homework">
    <div class="hw-page-header">
      <div class="hw-page-header__left">
        <h1 class="hw-page-header__title">{{ homework.title }}</h1>
        <p class="hw-page-header__desc">{{ homework.summary }}</p>
      </div>
      <div class="hw-page-header__actions">
        <a-button @click="router.push('/homework')">返回作业列表</a-button>
        <a-button @click="router.push(`/homework/${homework.id}/score`)">查看成绩</a-button>
      </div>
    </div>

    <HomeworkMetaGrid
      :topicLabel="homework.topicLabel"
      :teacher="homework.teacher"
      :open-time="homework.openTime"
      :deadline="homework.deadline"
    />

    <a-alert type="info" show-icon style="margin-bottom: var(--space-5)">
      <template #message>提交提示</template>
      <template #description>
        <ul class="hw-tip-list" style="margin:0;padding-left:18px">
          <li v-for="tip in homework.submitTips" :key="tip">{{ tip }}</li>
        </ul>
      </template>
    </a-alert>

    <section v-for="(q, idx) in questions" :key="q.id" class="app-surface-card app-section-card hw-question-block">
      <div class="hw-question-header">
        <span class="hw-question-number">{{ idx + 1 }}</span>
        <span class="hw-question-type-tag">{{ typeLabel(q.type) }}</span>
        <span v-if="q.maxScore" class="hw-question-score">（{{ q.maxScore }}分）</span>
      </div>

      <div class="hw-question-stem">
        <template v-if="q.type === 'fill_blank'">
          <span v-for="(segment, sIdx) in parseFillBlank(q.stem)" :key="sIdx">
            <template v-if="segment.type === 'text'">{{ segment.content }}</template>
            <template v-else>
              <a-input
                v-model:value="fillAnswers[segment.blankId]"
                class="hw-fill-input"
                placeholder="填写答案"
                :style="{ width: Math.max(segment.content.length * 16, 80) + 'px' }"
              />
            </template>
          </span>
        </template>
        <template v-else>
          {{ q.stem }}
        </template>
      </div>

      <div v-if="q.type === 'single_choice' || q.type === 'multiple_choice'" class="hw-question-options">
        <a-radio-group v-if="q.type === 'single_choice'" v-model:value="choiceAnswers[q.id]" class="hw-options-list">
          <a-radio v-for="opt in q.options" :key="opt.value" :value="opt.value" class="hw-option-item">{{ opt.label }}</a-radio>
        </a-radio-group>
        <a-checkbox-group v-else v-model:value="multipleAnswers[q.id]" class="hw-options-list">
          <a-checkbox v-for="opt in q.options" :key="opt.value" :value="opt.value" class="hw-option-item">{{ opt.label }}</a-checkbox>
        </a-checkbox-group>
      </div>

      <div v-if="q.type === 'true_false'" class="hw-question-options">
        <a-radio-group v-model:value="choiceAnswers[q.id]" class="hw-options-list">
          <a-radio value="T" class="hw-option-item">正确（T）</a-radio>
          <a-radio value="F" class="hw-option-item">错误（F）</a-radio>
        </a-radio-group>
      </div>

      <div v-if="q.type === 'short_answer'" class="hw-question-answer-area">
        <a-textarea v-model:value="shortAnswers[q.id]" :rows="4" placeholder="请输入你的答案" />
      </div>
    </section>

    <section class="app-surface-card app-section-card" style="display:flex;justify-content:flex-end;gap:12px;">
      <a-button size="large" @click="saveDraft">暂存答案</a-button>
      <a-button type="primary" size="large" @click="submitAll">提交全部答案</a-button>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import HomeworkMetaGrid from '@/components/homework/HomeworkMetaGrid.vue'

interface HomeworkDetailMock {
  id: string
  title: string
  topicLabel: string
  teacher: string
  openTime: string
  deadline: string
  summary: string
  submitTips: string[]
}

type QuestionType = 'single_choice' | 'multiple_choice' | 'fill_blank' | 'true_false' | 'short_answer'

interface QuestionItem {
  id: string
  type: QuestionType
  stem: string
  options?: { label: string; value: string }[]
  maxScore: number
}

interface FillSegment {
  type: 'text' | 'blank'
  content: string
  blankId: string
}

const homeworkDetailMock: HomeworkDetailMock[] = [
  {
    id: 'hw-101',
    title: '需求分析作业一：角色旅程拆解',
    topicLabel: '需求分析专题',
    teacher: '周老师',
    openTime: '2026-04-14 08:00',
    deadline: '2026-04-20 20:00',
    summary: '围绕教学平台案例完成角色旅程和验收边界分析。',
    submitTips: ['选择题点击选项即可作答', '填空题点击横线处输入答案', '简答题请在文本框中作答', '截止前可多次提交更新']
  }
]

const questions: QuestionItem[] = [
  {
    id: 'q-1',
    type: 'single_choice',
    stem: '以下哪项最适合作为角色旅程的首层分段？',
    options: [
      { label: 'A. 按数据库表名分段', value: 'A' },
      { label: 'B. 按用户任务阶段分段', value: 'B' },
      { label: 'C. 按代码文件目录分段', value: 'C' }
    ],
    maxScore: 5
  },
  {
    id: 'q-2',
    type: 'multiple_choice',
    stem: '验收边界描述中以下哪些属于关键内容？（多选）',
    options: [
      { label: 'A. 通过条件', value: 'A' },
      { label: 'B. 失败条件', value: 'B' },
      { label: 'C. 界面颜色与字号', value: 'C' },
      { label: 'D. 开发者个人偏好', value: 'D' }
    ],
    maxScore: 5
  },
  {
    id: 'q-3',
    type: 'true_false',
    stem: '角色旅程图只需要描述主流程，不需要描述异常流。',
    maxScore: 3
  },
  {
    id: 'q-4',
    type: 'fill_blank',
    stem: '在需求分析中，___是用来描述用户与系统交互过程的工具，而___则定义了系统在何种条件下可以通过验收。',
    maxScore: 6
  },
  {
    id: 'q-5',
    type: 'fill_blank',
    stem: 'HTML中用于插入图片的标签是___，其src属性用于指定___。',
    maxScore: 4
  },
  {
    id: 'q-6',
    type: 'short_answer',
    stem: '请简述角色旅程图的核心价值，并结合教学平台案例说明其在需求分析中的作用。',
    maxScore: 10
  },
  {
    id: 'q-7',
    type: 'short_answer',
    stem: '请描述"验收边界"的概念，并给出至少两条具体的通过/失败口径示例。',
    maxScore: 10
  }
]

function typeLabel(type: QuestionType): string {
  const map: Record<QuestionType, string> = {
    single_choice: '单选题',
    multiple_choice: '多选题',
    fill_blank: '填空题',
    true_false: '判断题',
    short_answer: '简答题'
  }
  return map[type] ?? '未知'
}

let blankCounter = 0
function parseFillBlank(stem: string): FillSegment[] {
  blankCounter = 0
  const segments = stem.split('___')
  const result: FillSegment[] = []
  for (let i = 0; i < segments.length; i++) {
    if (segments[i]) {
      result.push({ type: 'text', content: segments[i], blankId: '' })
    }
    if (i < segments.length - 1) {
      blankCounter++
      result.push({ type: 'blank', content: '答案', blankId: `blank_${blankCounter}` })
    }
  }
  return result
}

const route = useRoute()
const router = useRouter()

const homework = computed(() => homeworkDetailMock.find((item) => item.id === String(route.params.id)) ?? homeworkDetailMock[0])
const choiceAnswers = reactive<Record<string, string>>({ 'q-3': '' })
const multipleAnswers = reactive<Record<string, string[]>>({ 'q-2': [] })
const fillAnswers = reactive<Record<string, string>>({})
const shortAnswers = reactive<Record<string, string>>({})

// 预初始化选择题的第一个选项
choiceAnswers['q-1'] = ''

for (const q of questions) {
  if (q.type === 'fill_blank') {
    const segments = parseFillBlank(q.stem)
    for (const seg of segments) {
      if (seg.type === 'blank') {
        fillAnswers[seg.blankId] = ''
      }
    }
  }
  if (q.type === 'short_answer') {
    shortAnswers[q.id] = ''
  }
}

function saveDraft(): void {
  message.success('答案已暂存（Mock）。')
}

function submitAll(): void {
  message.success('全部答案已提交（Mock）。')
  router.push(`/homework/${homework.value.id}/score`)
}
</script>

<style scoped>
.hw-question-block {
  margin-bottom: var(--space-4);
}

.hw-question-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.hw-question-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--color-primary);
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  flex-shrink: 0;
}

.hw-question-type-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 10px;
  border-radius: 4px;
  background: var(--color-bg-muted);
  border: 1px solid var(--color-border);
  font-size: 12px;
  color: var(--color-text-secondary);
}

.hw-question-score {
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.hw-question-stem {
  font-size: 15px;
  line-height: 1.8;
  color: var(--color-text-main);
  margin-bottom: 16px;
}

.hw-question-options {
  margin-bottom: 4px;
}

.hw-options-list {
  display: grid;
  gap: 10px;
}

.hw-option-item {
  display: flex;
  align-items: flex-start;
  padding: 10px 14px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  background: var(--color-bg-muted);
  transition: border-color 0.15s, background 0.15s;
  cursor: pointer;
  line-height: 1.6;
}

.hw-option-item:hover {
  border-color: var(--color-primary);
  background: rgba(31, 95, 174, 0.04);
}

.hw-question-answer-area {
  margin-bottom: 4px;
}

.hw-fill-input {
  display: inline-block;
  border-bottom: 2px solid var(--color-primary);
  border-radius: 0;
  text-align: center;
  font-weight: 600;
}

.hw-fill-input :deep(.ant-input) {
  border: none;
  text-align: center;
  font-weight: 600;
}

.hw-fill-input :deep(.ant-input:focus) {
  box-shadow: none;
  border-bottom: 2px solid var(--color-primary);
}
</style>