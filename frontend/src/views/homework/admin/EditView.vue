<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="作业编辑" :title="pageTitle" description="维护作业基本信息和作业说明，保存后进入布置作业页面。">
        <template #actions>
          <a-button @click="router.push('/admin/homework')">返回作业列表</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <a-form layout="vertical">
          <a-form-item label="作业标题">
            <a-input v-model:value="formState.title" size="large" />
          </a-form-item>
          <a-form-item label="所属主题">
            <a-input v-model:value="formState.topicLabel" size="large" />
          </a-form-item>
          <a-form-item label="截止时间">
            <a-input v-model:value="formState.deadline" size="large" placeholder="例如：2026-04-20 20:00" />
          </a-form-item>
          <a-form-item label="作业摘要">
            <a-textarea v-model:value="formState.summary" :rows="4" />
          </a-form-item>
          <a-form-item label="标签（用 / 分隔）">
            <a-input v-model:value="formState.tagsText" size="large" />
          </a-form-item>
          <a-form-item label="作业要求（每行一条）">
            <a-textarea v-model:value="formState.instructionsText" :rows="6" />
          </a-form-item>
          <a-button type="primary" size="large" @click="saveHomework">保存作业</a-button>
        </a-form>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="说明" title="当前波次规则" description="先编辑再布置，减少教师在同一页面处理过多配置。" tight />
        <div class="app-list">
          <article class="app-list-card">
            <p class="app-list-card__meta">保存动作对应创建/更新作业基础信息。</p>
          </article>
          <article class="app-list-card">
            <p class="app-list-card__meta">布置范围、是否允许补交在下一页完成。</p>
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

interface HomeworkEditMock {
  id: string
  title: string
  topicLabel: string
  deadline: string
  summary: string
  tags: string[]
  instructions: string[]
}

// 作业模块Mock数据占位符，后续需替换到真实后端接口：GET /t_excercise_edit.do
const editMockData: HomeworkEditMock[] = [
  {
    id: 'hw-101',
    title: '需求分析作业一：角色旅程拆解',
    topicLabel: '需求分析专题',
    deadline: '2026-04-20 20:00',
    summary: '围绕教学平台案例输出角色旅程和验收边界。',
    tags: ['需求分析', '角色旅程'],
    instructions: ['说明三类角色任务链路。', '补充至少两条异常流。', '给出验收通过/失败口径。']
  }
]

const route = useRoute()
const router = useRouter()
const homeworkId = computed(() => (typeof route.params.id === 'string' ? route.params.id : ''))
const selected = computed(() => editMockData.find((item) => item.id === homeworkId.value))
const pageTitle = computed(() => (homeworkId.value ? '编辑作业' : '新增作业'))

const formState = reactive({
  title: selected.value?.title ?? '',
  topicLabel: selected.value?.topicLabel ?? '',
  deadline: selected.value?.deadline ?? '',
  summary: selected.value?.summary ?? '',
  tagsText: selected.value?.tags.join(' / ') ?? '',
  instructionsText: selected.value?.instructions.join('\n') ?? ''
})

function saveHomework(): void {
  if (formState.title.trim().length === 0 || formState.topicLabel.trim().length === 0) {
    message.error('请先填写标题和主题。')
    return
  }
  // 作业模块Mock数据占位符，后续需替换到真实后端接口：POST /t_excercise_save.do
  message.success('作业已保存（Mock）。')
  const targetId = homeworkId.value || 'hw-new'
  router.push(`/admin/homework/assign/${targetId}`)
}
</script>
