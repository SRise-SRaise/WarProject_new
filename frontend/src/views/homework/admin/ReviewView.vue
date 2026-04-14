<template>
  <div class="app-panel-grid">
    <section v-if="currentSubmission" class="app-surface-card app-section-card">
      <SectionHeader eyebrow="作业批改" :title="`${currentSubmission.studentName} · 作业批改`" :description="currentSubmission.summary">
        <template #actions>
          <a-button @click="router.push(`/admin/homework/submissions/${homeworkId}`)">返回提交记录</a-button>
        </template>
      </SectionHeader>
    </section>

    <section v-if="currentSubmission" class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="学生提交" title="内容预览" description="先阅读摘要和附件信息，再填写评分与反馈。" tight />
        <article class="app-list-card">
          <p class="app-list-card__meta">{{ currentSubmission.answerPreview }}</p>
          <div class="review-attachments">
            <span v-for="item in currentSubmission.attachments" :key="item.name" class="app-inline-stat">{{ item.name }}</span>
          </div>
        </article>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="评分反馈" title="填写批阅结果" description="结果会同步回学生结果页。" tight />
        <a-form layout="vertical">
          <a-form-item label="得分"><a-input v-model:value="formState.score" size="large" placeholder="例如：89 分" /></a-form-item>
          <a-form-item label="教师反馈"><a-textarea v-model:value="formState.feedback" :rows="6" /></a-form-item>
          <a-button type="primary" size="large" :loading="saving" @click="submitReview">保存批改</a-button>
        </a-form>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useHomeworkAdminStore } from '@/stores/homework/admin'

const route = useRoute()
const router = useRouter()
const homeworkStore = useHomeworkAdminStore()
const { currentSubmission } = storeToRefs(homeworkStore)
const saving = ref(false)
const homeworkId = computed(() => String(route.params.homeworkId))
const submissionId = computed(() => String(route.params.submissionId))
const formState = reactive({ score: '', feedback: '' })

async function submitReview(): Promise<void> {
  saving.value = true
  try {
    await homeworkStore.reviewSubmission(homeworkId.value, submissionId.value, { ...formState })
    message.success('批阅结果已保存。')
    router.push(`/admin/homework/submissions/${homeworkId.value}`)
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const submission = await homeworkStore.selectSubmission(homeworkId.value, submissionId.value)
  if (!submission) return
  formState.score = submission.score ?? ''
  formState.feedback = submission.feedback ?? ''
})
</script>

<style scoped>
.review-attachments {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}
</style>
