<template>
  <div class="app-panel-grid" v-if="submission">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="次级页面" :title="`${submission.studentName} · 作业批改`" :description="submission.summary">
        <template #actions>
          <a-button @click="router.push(`/admin/homework/submissions/${submission.homeworkId}`)">返回提交记录</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="学生提交" title="内容预览" description="先审阅摘要和附件，再填写评分。" tight />
        <article class="app-list-card">
          <p class="app-list-card__meta">{{ submission.answerPreview }}</p>
          <div class="review-attachments">
            <span v-for="item in submission.attachments" :key="item" class="app-inline-stat">{{ item }}</span>
          </div>
        </article>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="评分反馈" title="填写批改结果" description="保存后可在学生成绩页查看反馈。" tight />
        <a-form layout="vertical">
          <a-form-item label="得分">
            <a-input v-model:value="reviewForm.score" size="large" placeholder="例如：89 分" />
          </a-form-item>
          <a-form-item label="教师反馈">
            <a-textarea v-model:value="reviewForm.feedback" :rows="6" />
          </a-form-item>
          <a-button type="primary" size="large" @click="submitReview">保存批改</a-button>
        </a-form>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'

interface ReviewMock {
  id: string
  homeworkId: string
  studentName: string
  summary: string
  answerPreview: string
  attachments: string[]
  score?: string
  feedback?: string
}

// 作业模块Mock数据占位符，后续需替换到真实后端接口：GET /t_excercise_list.do
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
  score: submission.value.score ?? '',
  feedback: submission.value.feedback ?? ''
})

function submitReview(): void {
  if (reviewForm.score.trim().length === 0) {
    message.error('请先填写得分。')
    return
  }
  // 作业模块Mock数据占位符，后续需替换到真实后端接口：POST /t_excercise_save.do
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
