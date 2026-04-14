<template>
  <div class="app-page-shell app-panel-grid">
    <section v-if="currentHomework" class="app-surface-card app-section-card">
      <SectionHeader eyebrow="作业提交" :title="currentHomework.title" description="先确认要求，再提交文本摘要和附件名称，当前提交会同步到结果页和教师提交记录。">
        <template #actions>
          <a-button @click="router.push(`/homework/${currentHomework.id}`)">返回详情</a-button>
        </template>
      </SectionHeader>
    </section>

    <section v-if="currentHomework" class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="提交内容" title="填写作业摘要" description="先提交结构化摘要，后续真实接口接入时可替换为更完整的表单或上传链路。" tight />
        <a-form layout="vertical">
          <a-form-item label="作业摘要">
            <a-textarea v-model:value="formState.content" :rows="8" placeholder="概述你的完成情况、关键分析和待补充项" />
          </a-form-item>
          <a-form-item label="附件名称">
            <a-input v-model:value="formState.fileName" size="large" placeholder="例如：analysis-report.pdf" />
          </a-form-item>
          <a-button type="primary" size="large" :loading="submitting" @click="submitHomework">确认提交</a-button>
        </a-form>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="提交提醒" title="提交前检查" description="这些提示来自作业详情页，帮助学生避免在最后时刻反复修改。" tight />
        <div class="app-list">
          <article v-for="tip in currentHomework.submitTips" :key="tip" class="app-list-card">
            <p class="app-list-card__meta">{{ tip }}</p>
          </article>
        </div>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useHomeworkStudentStore } from '@/stores/homework/student'

const route = useRoute()
const router = useRouter()
const homeworkStore = useHomeworkStudentStore()
const { currentHomework } = storeToRefs(homeworkStore)
const submitting = ref(false)

const formState = reactive({
  content: '',
  fileName: ''
})

async function submitHomework(): Promise<void> {
  if (!currentHomework.value) return
  if (formState.content.trim().length === 0) {
    message.error('请先填写作业摘要。')
    return
  }
  submitting.value = true
  try {
    await homeworkStore.submitCurrentHomework(formState.content, formState.fileName)
    message.success('作业已提交。')
    router.push(`/homework/${currentHomework.value.id}/result`)
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  const homework = await homeworkStore.selectHomework(String(route.params.id))
  if (!homework) return
  formState.content = homework.submission.content
  formState.fileName = homework.submission.fileName ?? ''
})
</script>
