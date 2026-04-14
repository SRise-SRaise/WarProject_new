<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="作业编辑" :title="pageTitle" description="维护作业基本信息、说明和标签，发布动作单独放在下一页处理。">
        <template #actions>
          <a-button @click="router.push('/admin/homework')">返回作业列表</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <a-form layout="vertical">
          <a-form-item label="作业标题"><a-input v-model:value="formState.title" size="large" /></a-form-item>
          <a-form-item label="所属主题"><a-input v-model:value="formState.topicLabel" size="large" /></a-form-item>
          <a-form-item label="截止时间"><a-input v-model:value="formState.deadline" size="large" placeholder="例如：2026-04-20 20:00" /></a-form-item>
          <a-form-item label="作业摘要"><a-textarea v-model:value="formState.summary" :rows="5" /></a-form-item>
          <a-form-item label="标签（用 / 分隔）"><a-input v-model:value="formState.tagsText" size="large" /></a-form-item>
          <a-form-item label="作业要求（每行一条）"><a-textarea v-model:value="formState.instructionsText" :rows="6" /></a-form-item>
          <a-button type="primary" size="large" :loading="saving" @click="saveHomework">保存作业</a-button>
        </a-form>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="编辑说明" title="当前策略" description="首波后台页将编辑和发布拆开，以便教师先整理内容，再决定范围和截止规则。" tight />
        <div class="app-list">
          <article class="app-list-card"><p class="app-list-card__meta">可以从列表页进入已有作业编辑，也可以从这里新建草稿。</p></article>
          <article class="app-list-card"><p class="app-list-card__meta">保存后会跳转到发布页，继续处理班级范围和是否允许补交。</p></article>
        </div>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useHomeworkAdminStore } from '@/stores/homework/admin'

const route = useRoute()
const router = useRouter()
const homeworkStore = useHomeworkAdminStore()
const saving = ref(false)
const homeworkId = computed(() => (typeof route.params.id === 'string' ? route.params.id : ''))
const pageTitle = computed(() => (homeworkId.value ? '编辑现有作业' : '新建作业草稿'))

const formState = reactive({
  title: '',
  topicLabel: '',
  deadline: '',
  summary: '',
  tagsText: '',
  instructionsText: ''
})

function parseLines(value: string): string[] {
  return value.split(String.fromCharCode(10)).map((item) => item.trim()).filter((item) => item.length > 0)
}

function parseTags(value: string): string[] {
  return value.split('/').map((item) => item.trim()).filter((item) => item.length > 0)
}

async function saveHomework(): Promise<void> {
  if (formState.title.trim().length === 0 || formState.topicLabel.trim().length === 0) {
    message.error('请先填写标题和主题。')
    return
  }

  saving.value = true
  try {
    const saved = await homeworkStore.saveHomework({
      id: homeworkId.value || undefined,
      title: formState.title,
      topicLabel: formState.topicLabel,
      deadline: formState.deadline,
      summary: formState.summary,
      tags: parseTags(formState.tagsText),
      instructions: parseLines(formState.instructionsText)
    })
    message.success('作业已保存。')
    router.push(`/admin/homework/publish/${saved.id}`)
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await homeworkStore.ensureLoaded()
  if (!homeworkId.value) {
    return
  }

  const item = await homeworkStore.selectHomework(homeworkId.value)
  if (!item) {
    return
  }

  formState.title = item.title
  formState.topicLabel = item.topicLabel
  formState.deadline = item.deadline
  formState.summary = item.summary
  formState.tagsText = item.tags.join(' / ')
  formState.instructionsText = item.instructions.join(String.fromCharCode(10))
})
</script>
