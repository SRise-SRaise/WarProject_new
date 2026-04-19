<template>
  <div class="app-panel-grid" v-if="submission">
    <div class="hw-page-header">
      <div class="hw-page-header__left">
        <h1 class="hw-page-header__title">{{ submission.studentName }} · 作业批改</h1>
        <p class="hw-page-header__desc">{{ submission.exerciseName }}</p>
      </div>
      <div class="hw-page-header__actions">
        <a-button @click="router.push(`/admin/homework/submissions/${homeworkId}`)">返回提交记录</a-button>
      </div>
    </div>

    <a-spin :spinning="loading">
      <section class="app-split-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <h2 style="margin:0 0 16px;font-size:16px;font-weight:700;color:var(--color-text-main)">答题详情</h2>
          <article v-for="(answer, idx) in submission.answers" :key="answer.formKey" class="app-list-card">
            <div class="review-answer-header">
              <h3 class="app-list-card__title">题目 {{ idx + 1 }}：{{ typeLabel(answer.questionType) }}</h3>
              <span class="app-inline-stat">{{ answer.score || 0 }} / {{ answer.maxScore }}</span>
            </div>
            <p class="app-list-card__meta" style="margin-top:8px">{{ answer.question }}</p>
            <div class="review-answer-status" :class="{ 'review-answer-status--empty': !answer.recordId }">
              <span class="review-answer-status__label">学生答案</span>
              <span class="review-answer-status__value">{{ answer.studentAnswer || '未作答' }}</span>
            </div>
            <p class="app-list-card__meta">标准答案：{{ answer.standardAnswer }}</p>

            <a-form layout="inline" style="margin-top:12px" v-if="answer.gradingStatus !== 1 && answer.recordId">
              <a-form-item label="评分">
                <a-input-number v-model:value="answerScores[answer.formKey]" :min="0" :max="answer.maxScore" size="small" />
              </a-form-item>
              <a-form-item label="评语">
                <a-input v-model:value="answerComments[answer.formKey]" size="small" placeholder="评语" />
              </a-form-item>
              <a-button size="small" @click="reviewItem(answer)">批阅此题</a-button>
            </a-form>
            <p
              v-else-if="answer.gradingStatus !== 1"
              class="app-list-card__meta"
              style="margin-top:12px;color:var(--color-text-secondary)"
            >
              该题未提交，无法单独批阅。
            </p>
            <p v-if="answer.comment" class="app-list-card__meta" style="margin-top:8px;color:var(--color-primary)">
              教师评语：{{ answer.comment }}
            </p>
          </article>

          <h2 style="margin:24px 0 16px;font-size:16px;font-weight:700;color:var(--color-text-main)">成绩汇总</h2>
          <div class="score-summary">
            <span class="app-inline-stat">总分：{{ submission.totalScore || 0 }}</span>
          </div>
        </section>

        <div class="hw-side-column">
          <a-alert type="info" message="批改说明" description="客观题（单选/判断）已自动评分，主观题需人工批阅。" show-icon />
        </div>
      </section>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { getSubmissionDetail, reviewExerciseItem } from '@/api/eduExerciseSubmissionController'

interface SubmissionData {
  exerciseId: number
  exerciseName: string
  studentId: number
  studentName: string
  className: string
  submittedAt: string
  totalScore: number
  answers: Array<{
    formKey: string
    recordId?: number
    itemId: number
    question: string
    questionType: number
    optionsText?: string
    standardAnswer: string
    maxScore: number
    studentAnswer: string
    score: number
    gradingStatus: number
    comment?: string
  }>
}

const route = useRoute()
const router = useRouter()

const homeworkId = String(route.params.homeworkId)
const studentId = Number(route.params.submissionId)

const loading = ref(true)
const submission = ref<SubmissionData | null>(null)
const answerScores = reactive<Record<string, number>>({})
const answerComments = reactive<Record<string, string>>({})

function typeLabel(questionType: number): string {
  const typeMap: Record<number, string> = {
    1: '填空题',
    2: '单选题',
    3: '多选题',
    4: '判断题',
    5: '简答题'
  }
  return typeMap[questionType] || '未知'
}

async function loadData() {
  if (!homeworkId || !studentId) {
    loading.value = false
    return
  }

  try {
    const response = await getSubmissionDetail({
      exerciseId: Number(homeworkId),
      studentId
    })
    const data = response.data?.data

    if (data) {
      submission.value = {
        exerciseId: data.exerciseId || 0,
        exerciseName: data.exerciseName || '',
        studentId: data.studentId || 0,
        studentName: data.studentName || '',
        className: data.className || '',
        submittedAt: data.submittedAt || '',
        totalScore: data.totalScore || 0,
        answers: (data.answers || []).map((item, index) => {
          const recordId = item.recordId || undefined
          const itemId = item.itemId || 0
          return {
            formKey: recordId ? `record-${recordId}` : `item-${itemId || index + 1}`,
            recordId,
            itemId,
            question: item.question || '',
            questionType: item.questionType || 0,
            optionsText: item.optionsText,
            standardAnswer: item.standardAnswer || '',
            maxScore: item.maxScore || 0,
            studentAnswer: item.studentAnswer || '',
            score: item.score || 0,
            gradingStatus: item.gradingStatus || 0,
            comment: item.comment
          }
        })
      }

      // 初始化评分和评语
      Object.keys(answerScores).forEach((key) => delete answerScores[key])
      Object.keys(answerComments).forEach((key) => delete answerComments[key])
      for (const answer of submission.value.answers) {
        answerScores[answer.formKey] = answer.score || 0
        if (answer.comment) {
          answerComments[answer.formKey] = answer.comment
        }
      }
    }
  } catch (error) {
    console.error('加载提交详情失败:', error)
    message.error('加载提交详情失败')
  } finally {
    loading.value = false
  }
}

async function reviewItem(answer: SubmissionData['answers'][number]) {
  try {
    await reviewExerciseItem({
      recordId: answer.recordId,
      score: answerScores[answer.formKey],
      comment: answerComments[answer.formKey] || '',
      gradingStatus: 2
    })
    message.success('批阅成功')
    await loadData() // 刷新数据
  } catch (error) {
    console.error('批阅失败:', error)
    message.error('批阅失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.review-answer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.score-summary {
  display: flex;
  gap: 12px;
}

.review-answer-status {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  margin-top: 12px;
  padding: 10px 14px;
  border: 1px solid var(--color-border-default);
  border-radius: 10px;
  background: var(--color-bg-page);
}

.review-answer-status__label {
  font-size: 13px;
  font-weight: 700;
  color: var(--color-text-secondary);
}

.review-answer-status__value {
  font-size: 15px;
  font-weight: 700;
  color: var(--color-text-main);
}

.review-answer-status--empty {
  border-color: var(--color-warning);
  background: var(--color-warning-bg);
}

.review-answer-status--empty .review-answer-status__label,
.review-answer-status--empty .review-answer-status__value {
  color: var(--color-warning);
}
</style>
