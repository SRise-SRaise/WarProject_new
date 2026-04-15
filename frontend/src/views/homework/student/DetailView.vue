<template>
  <div class="app-page-shell app-panel-grid" v-if="homework">
    <section class="app-surface-card app-section-card app-panel-grid">
      <SectionHeader eyebrow="做作业" :title="homework.title" :description="homework.summary">
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="router.push('/homework')">返回作业列表</a-button>
            <a-button @click="router.push(`/homework/${homework.id}/score`)">查看成绩</a-button>
          </a-space>
        </template>
      </SectionHeader>
      <HomeworkMetaGrid
        :topicLabel="homework.topicLabel"
        :teacher="homework.teacher"
        :open-time="homework.openTime"
        :deadline="homework.deadline"
      />
    </section>

    <section class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="选择题" title="提交单选/多选答案" description="对应作业模块提交答案能力，当前页面仅做 Mock 演示。" tight />
        <div class="app-list">
          <article v-for="question in choiceQuestions" :key="question.id" class="app-list-card">
            <h3 class="app-list-card__title">{{ question.title }}</h3>
            <a-radio-group v-model:value="choiceAnswers[question.id]" class="question-options">
              <a-radio v-for="option in question.options" :key="option.value" :value="option.value">{{ option.label }}</a-radio>
            </a-radio-group>
          </article>
        </div>
        <a-button type="primary" size="large" @click="submitChoiceAnswers">提交选择题答案</a-button>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="填空与简答" title="提交内容题答案" description="对应填空/简答提交能力，后续替换为真实接口调用。" tight />
        <a-form layout="vertical">
          <a-form-item label="填空题答案">
            <a-input v-model:value="contentAnswers.fillBlank" size="large" placeholder="请输入关键术语或短语" />
          </a-form-item>
          <a-form-item label="简答题答案">
            <a-textarea v-model:value="contentAnswers.shortAnswer" :rows="6" placeholder="请输入简答分析内容" />
          </a-form-item>
          <a-button type="primary" size="large" @click="submitContentAnswers">提交填空/简答答案</a-button>
        </a-form>
        <div class="app-list">
          <article v-for="tip in homework.submitTips" :key="tip" class="app-list-card">
            <p class="app-list-card__meta">{{ tip }}</p>
          </article>
        </div>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
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

interface ChoiceQuestion {
  id: string
  title: string
  options: { label: string; value: string }[]
}

// 作业模块Mock数据占位符，后续需替换到真实后端接口：GET /s_excercise_do.do
const homeworkDetailMock: HomeworkDetailMock[] = [
  {
    id: 'hw-101',
    title: '需求分析作业一：角色旅程拆解',
    topicLabel: '需求分析专题',
    teacher: '周老师',
    openTime: '2026-04-14 08:00',
    deadline: '2026-04-20 20:00',
    summary: '围绕教学平台案例完成角色旅程和验收边界分析。',
    submitTips: ['先完成选择题再提交内容题。', '提交后可在截止前继续更新。', '简答题建议用条目结构表达。']
  },
  {
    id: 'hw-102',
    title: '界面结构复盘报告',
    topicLabel: '界面实现专题',
    teacher: '林老师',
    openTime: '2026-04-10 09:00',
    deadline: '2026-04-18 18:00',
    summary: '对壳层、页面层、组件层做结构决策复盘。',
    submitTips: ['结合实际页面给出结构依据。', '避免只描述视觉表现。']
  }
]

// 作业模块Mock数据占位符，后续需替换到真实后端接口：GET /s_excercise_do.do
const choiceQuestions: ChoiceQuestion[] = [
  {
    id: 'q-1',
    title: '以下哪项最适合作为角色旅程的首层分段？',
    options: [
      { label: '按数据库表名分段', value: 'A' },
      { label: '按用户任务阶段分段', value: 'B' },
      { label: '按代码文件目录分段', value: 'C' }
    ]
  },
  {
    id: 'q-2',
    title: '验收边界描述中最关键的内容是？',
    options: [
      { label: '界面颜色与字号', value: 'A' },
      { label: '通过条件与失败条件', value: 'B' },
      { label: '开发者个人偏好', value: 'C' }
    ]
  }
]

const route = useRoute()
const router = useRouter()

const homework = computed(() => homeworkDetailMock.find((item) => item.id === String(route.params.id)) ?? homeworkDetailMock[0])
const choiceAnswers = reactive<Record<string, string>>({ 'q-1': '', 'q-2': '' })
const contentAnswers = reactive({ fillBlank: '', shortAnswer: '' })

async function submitChoiceAnswers(): Promise<void> {
  if (!choiceAnswers['q-1'] || !choiceAnswers['q-2']) {
    message.error('请先完成全部选择题。')
    return
  }
  // 作业模块Mock数据占位符，后续需替换到真实后端接口：POST /s_excercise_submit.do
  await Promise.resolve()
  message.success('选择题答案已提交（Mock）。')
}

async function submitContentAnswers(): Promise<void> {
  if (contentAnswers.fillBlank.trim().length === 0 || contentAnswers.shortAnswer.trim().length === 0) {
    message.error('请填写填空与简答答案。')
    return
  }
  // 作业模块Mock数据占位符，后续需替换到真实后端接口：POST /s_excercise_content_submit.do
  await Promise.resolve()
  message.success('填空/简答答案已提交（Mock）。')
}
</script>

<style scoped>
.question-options {
  display: grid;
  gap: 10px;
  margin-top: 14px;
}
</style>
