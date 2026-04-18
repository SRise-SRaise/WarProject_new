<template>
  <div class="exam-result-page">
    <header class="page-header">
      <div>
        <h1 class="page-title">{{ submission?.examName || '考试结果' }}</h1>
        <p class="page-desc">{{ submission?.submittedAt ? `提交时间：${submission.submittedAt}` : '查看考试成绩与批改状态' }}</p>
      </div>
      <a-space>
        <a-button @click="router.push('/exams')">返回考试列表</a-button>
      </a-space>
    </header>

    <div v-if="!submission" class="empty-state">
      <a-empty description="当前浏览器没有找到该考试的提交记录">
        <a-button type="primary" @click="router.push('/exams')">返回考试列表</a-button>
      </a-empty>
    </div>

    <template v-else>
      <section v-if="submission.releaseMode === 'immediate'" class="result-panel result-panel--success">
        <div class="result-summary">
          <div class="result-score">
            <span class="score-earned">{{ submission.finalScore }}</span>
            <span class="score-total">/ {{ submission.totalScore }} 分</span>
          </div>
          <div class="result-copy">
            <h2>自动判分已完成</h2>
            <p>这场试卷全部为无需人工批改的题型，提交后已立即生成成绩明细。</p>
          </div>
        </div>
      </section>

      <section v-else class="result-panel result-panel--pending">
        <div class="result-summary">
          <div class="pending-badge">待教师批改</div>
          <div class="result-copy">
            <h2>试卷已提交，正在等待教师批改</h2>
            <p>当前试卷包含简答、编程或综合题。系统已记录客观题作答，但最终成绩和答案需要待教师完成批改后再开放。</p>
          </div>
        </div>
      </section>

      <section class="meta-grid">
        <article class="meta-card">
          <span class="meta-label">试卷名称</span>
          <span class="meta-value">{{ submission.paper.paperName }}</span>
        </article>
        <article class="meta-card">
          <span class="meta-label">题目数量</span>
          <span class="meta-value">{{ submission.paper.questionCount }} 题</span>
        </article>
        <article class="meta-card">
          <span class="meta-label">总分</span>
          <span class="meta-value">{{ submission.totalScore }} 分</span>
        </article>
        <article class="meta-card">
          <span class="meta-label">成绩状态</span>
          <span class="meta-value">{{ submission.releaseMode === 'immediate' ? '已发布' : '待教师批改' }}</span>
        </article>
      </section>

      <section v-if="submission.releaseMode === 'immediate'" class="detail-section">
        <h2 class="detail-title">成绩明细</h2>
        <div class="detail-list">
          <article v-for="question in submission.paper.questions" :key="question.id" class="detail-card">
            <div class="detail-card__header">
              <div>
                <h3 class="detail-card__title">第 {{ question.id }} 题</h3>
                <p class="detail-card__prompt">{{ question.questionContent }}</p>
              </div>
              <a-tag :color="isQuestionCorrect(question.questionId) ? 'green' : 'orange'">
                {{ isQuestionCorrect(question.questionId) ? '判定正确' : '未得分' }}
              </a-tag>
            </div>
            <div class="detail-card__body">
              <div>
                <span class="body-label">你的答案</span>
                <p class="body-value">{{ formatAnswer(submission.answers[question.questionId]) }}</p>
              </div>
              <div>
                <span class="body-label">本题得分</span>
                <p class="body-value score">{{ submission.questionScores[question.questionId]?.earned ?? 0 }} / {{ question.score }}</p>
              </div>
            </div>
          </article>
        </div>
      </section>

      <section v-else class="detail-section pending-detail">
        <h2 class="detail-title">当前状态</h2>
        <p class="pending-copy">你已经完成试卷提交，系统将保留当前作答。待教师完成主观题批改后，再通过考试列表查看最终成绩和状态。</p>
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useExamStudentStore } from '@/stores/exam/student'

const route = useRoute()
const router = useRouter()
const examStore = useExamStudentStore()
const examId = computed(() => Number(route.params.id))
const submission = computed(() => examStore.currentSubmission)

function isQuestionCorrect(questionId: number): boolean {
  const detail = submission.value?.questionScores[questionId]
  if (!detail) return false
  return detail.earned === detail.max && detail.max > 0
}

function formatAnswer(answer: string | string[] | undefined): string {
  if (!answer) return '未作答'
  if (Array.isArray(answer)) return answer.join('，')
  return answer
}

onMounted(() => {
  examStore.loadSubmissionResult(examId.value)
})
</script>

<style scoped>
.exam-result-page { max-width: 1080px; margin: 0 auto; padding: 24px; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; gap: 16px; margin-bottom: 24px; }
.page-title { margin: 0 0 8px; font-size: 28px; font-weight: 700; }
.page-desc { margin: 0; color: #8c8c8c; }
.result-panel { margin-bottom: 24px; border-radius: 16px; padding: 24px 28px; }
.result-panel--success { background: linear-gradient(135deg, #f6ffed, #ffffff); border: 1px solid #b7eb8f; }
.result-panel--pending { background: linear-gradient(135deg, #fff7e6, #ffffff); border: 1px solid #ffd591; }
.result-summary { display: flex; align-items: center; gap: 24px; }
.result-score { display: flex; align-items: baseline; gap: 8px; }
.score-earned { font-size: 42px; font-weight: 700; color: #1677ff; }
.score-total { font-size: 20px; color: #595959; }
.pending-badge { display: inline-flex; align-items: center; justify-content: center; min-width: 112px; padding: 10px 14px; border-radius: 999px; background: #fa8c16; color: #fff; font-weight: 600; }
.meta-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 16px; margin-bottom: 24px; }
.meta-card { display: flex; flex-direction: column; gap: 10px; padding: 18px 20px; border: 1px solid #f0f0f0; border-radius: 12px; background: #fff; }
.meta-label { color: #8c8c8c; font-size: 13px; }
.meta-value { color: #1f1f1f; font-size: 18px; font-weight: 600; }
.detail-title { margin: 0 0 16px; font-size: 20px; font-weight: 600; }
.detail-list { display: flex; flex-direction: column; gap: 16px; }
.detail-card { border: 1px solid #f0f0f0; border-radius: 12px; background: #fff; padding: 20px; }
.detail-card__header { display: flex; justify-content: space-between; gap: 16px; margin-bottom: 16px; }
.detail-card__title { margin: 0 0 8px; font-size: 16px; font-weight: 600; }
.detail-card__prompt { margin: 0; color: #595959; line-height: 1.7; white-space: pre-wrap; }
.detail-card__body { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; }
.body-label { display: block; margin-bottom: 8px; color: #8c8c8c; font-size: 13px; }
.body-value { margin: 0; color: #1f1f1f; font-weight: 500; white-space: pre-wrap; }
.body-value.score { color: #1677ff; }
.pending-detail { border: 1px dashed #ffd591; border-radius: 12px; background: #fffdf8; padding: 20px; }
.pending-copy { margin: 0; color: #595959; line-height: 1.8; }
.empty-state { padding: 80px 24px; }
@media (max-width: 960px) { .meta-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } .detail-card__header, .result-summary { flex-direction: column; align-items: flex-start; } .detail-card__body { grid-template-columns: 1fr; } }
</style>
