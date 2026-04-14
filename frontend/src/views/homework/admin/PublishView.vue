<template>
  <div class="app-panel-grid">
    <section v-if="currentHomework" class="app-surface-card app-section-card">
      <SectionHeader eyebrow="作业发布" :title="currentHomework.title" description="完成内容编辑后，再决定覆盖班级、截止时间和是否允许补交。">
        <template #actions>
          <a-button @click="router.push(`/admin/homework/edit/${currentHomework.id}`)">返回编辑</a-button>
        </template>
      </SectionHeader>
    </section>

    <section v-if="currentHomework" class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <a-form layout="vertical">
          <a-form-item label="发布范围"><a-input v-model:value="formState.publishScope" size="large" placeholder="例如：软工 2401 / 2402" /></a-form-item>
          <a-form-item label="截止时间"><a-input v-model:value="formState.deadline" size="large" /></a-form-item>
          <a-form-item label="允许补交"><a-switch v-model:checked="formState.allowLate" /></a-form-item>
          <a-button type="primary" size="large" :loading="publishing" @click="publishHomework">确认发布</a-button>
        </a-form>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="发布摘要" title="当前内容概览" :description="currentHomework.summary" tight />
        <div class="app-list">
          <article v-for="item in currentHomework.instructions" :key="item" class="app-list-card"><p class="app-list-card__meta">{{ item }}</p></article>
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
import { useHomeworkAdminStore } from '@/stores/homework/admin'

const route = useRoute()
const router = useRouter()
const homeworkStore = useHomeworkAdminStore()
const { currentHomework } = storeToRefs(homeworkStore)
const publishing = ref(false)
const formState = reactive({ publishScope: '', deadline: '', allowLate: true })

async function publishHomework(): Promise<void> {
  if (!currentHomework.value) return
  publishing.value = true
  try {
    await homeworkStore.publishHomework(currentHomework.value.id, { ...formState })
    message.success('作业已发布。')
    router.push(`/admin/homework/submissions/${currentHomework.value.id}`)
  } finally {
    publishing.value = false
  }
}

onMounted(async () => {
  const item = await homeworkStore.selectHomework(String(route.params.id))
  if (!item) return
  formState.publishScope = item.publishScope
  formState.deadline = item.deadline
})
</script>
