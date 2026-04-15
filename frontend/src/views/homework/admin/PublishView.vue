<template>
  <div class="app-panel-grid" v-if="homework">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="布置作业" :title="homework.title" description="配置发布班级、截止时间和补交策略，完成教师端布置动作。">
        <template #actions>
          <a-button @click="router.push(`/admin/homework/edit/${homework.id}`)">返回编辑</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <a-form layout="vertical">
          <a-form-item label="发布范围">
            <a-input v-model:value="assignForm.publishScope" size="large" placeholder="例如：软工 2401 / 2402" />
          </a-form-item>
          <a-form-item label="截止时间">
            <a-input v-model:value="assignForm.deadline" size="large" />
          </a-form-item>
          <a-form-item label="允许补交">
            <a-switch v-model:checked="assignForm.allowLate" />
          </a-form-item>
          <a-button type="primary" size="large" @click="assignHomework">确认布置</a-button>
        </a-form>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="作业摘要" title="当前作业说明" :description="homework.summary" tight />
        <div class="app-list">
          <article v-for="item in homework.instructions" :key="item" class="app-list-card">
            <p class="app-list-card__meta">{{ item }}</p>
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

interface HomeworkAssignMock {
  id: string
  title: string
  summary: string
  instructions: string[]
  publishScope: string
  deadline: string
}

// 作业模块Mock数据占位符，后续需替换到真实后端接口：GET /t_excercise_edit.do
const assignMock: HomeworkAssignMock[] = [
  {
    id: 'hw-101',
    title: '需求分析作业一：角色旅程拆解',
    summary: '围绕教学平台案例输出角色旅程和验收边界。',
    instructions: ['说明三类角色任务链路。', '补充至少两条异常流。', '给出验收通过/失败口径。'],
    publishScope: '软工 2401 / 2402',
    deadline: '2026-04-20 20:00'
  }
]

const route = useRoute()
const router = useRouter()
const homework = computed(() => assignMock.find((item) => item.id === String(route.params.id)) ?? assignMock[0])

const assignForm = reactive({
  publishScope: homework.value.publishScope,
  deadline: homework.value.deadline,
  allowLate: true
})

function assignHomework(): void {
  // 作业模块Mock数据占位符，后续需替换到真实后端接口：POST /t_excercise_assign.do
  message.success('作业布置成功（Mock）。')
  router.push('/admin/homework')
}
</script>
