<template>
  <div class="app-panel-grid" v-if="submission">
    <div class="hw-page-header">
      <div class="hw-page-header__left">
        <h1 class="hw-page-header__title">{{ submission.studentName }} · 作业批改</h1>
        <p class="hw-page-header__desc">{{ submission.summary }}</p>
      </div>
      <div class="hw-page-header__actions">
        <a-button @click="router.push(`/admin/homework/submissions/${submission.homeworkId}`)">返回提交记录</a-button>
      </div>
    </div>

    <section class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <h2 style="margin:0 0 16px;font-size:16px;font-weight:700;color:var(--color-text-main)">学生提交</h2>
        <article class="app-list-card">
          <p class="app-list-card__meta">{{ submission.answerPreview }}</p>
          <div class="review-attachments">
            <span v-for="item in submission.attachments" :key="item" class="app-inline-stat">{{ item }}</span>
          </div>
        </article>

        <h2 style="margin:24px 0 16px;font-size:16px;font-weight:700;color:var(--color-text-main)">评分反馈</h2>
        <a-form layout="vertical">
          <a-form-item label="得分">
            <a-input v-model:value="reviewForm.score" size="large" placeholder="例如：89 分" />
          </a-form-item>
          <a-form-item label="教师反馈">
            <a-textarea v-model:value="reviewForm.feedback" :rows="4" placeholder="请输入批改意见" />
          </a-form-item>
          <a-button type="primary" size="large" @click="submitReview">保存批改</a-button>
        </a-form>
      </section>

      <div class="hw-side-column">
        <a-alert type="info" message="批改说明" description="先审阅学生内容，再填写评分。后续接入后端后，将支持逐题评分。" show-icon />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'

interface ReviewMock {
  id: string
  homeworkId: string
  studentName: string
  summary: string
  answerPreview: string
  attachments: string[]
}

const reviewMock: ReviewMock[] = [
  {
    id: 'sub-1',
    homeworkId: 'hw-101',
    studentName: '李明',
    summary: '已完成角色旅程与异常流梳理。',
    answerPreview: '本次提交补充了学生与教师双视角流程，并标注了登录失败和重复提交两条异常流。',
    attachments: ['journey-analysis.pdf']
  }
]

const route = useRoute()
const router = useRouter()
const submission = computed(() => {
  const homeworkId = String(route.params.homeworkId)
  const submissionId = String(route.params.submissionId)
  return reviewMock.find((item) => item.homeworkId === homeworkId && item.id === submissionId) ?? reviewMock[0]
})

const reviewForm = reactive({
  score: '',
  feedback: ''
})

function submitReview(): void {
  if (reviewForm.score.trim().length === 0) {
    message.error('请先填写得分。')
    return
  }
  message.success('批改结果已保存（Mock）。')
  router.push(`/admin/homework/submissions/${submission.value.homeworkId}`)
}
</script>

<style scoped>
.review-attachments {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}
</style>