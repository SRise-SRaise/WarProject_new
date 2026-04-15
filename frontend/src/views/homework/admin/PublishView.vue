<template>
  <div class="app-panel-grid" v-if="homework">
    <div class="hw-page-header">
      <div class="hw-page-header__left">
        <h1 class="hw-page-header__title">布置作业：{{ homework.title }}</h1>
        <p class="hw-page-header__desc">配置发布班级和补交策略，完成布置后作业对学生可见。</p>
      </div>
      <div class="hw-page-header__actions">
        <a-button @click="router.push(`/admin/homework/edit/${homework.id}`)">返回编辑</a-button>
      </div>
    </div>

    <section class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <a-form layout="vertical">
          <a-form-item label="发布班级" required>
            <a-select v-model:value="assignForm.classCodes" mode="multiple" size="large" placeholder="选择发布班级" :options="classOptions" />
          </a-form-item>
          <a-form-item label="截止时间" required>
            <a-input v-model:value="assignForm.deadline" size="large" placeholder="例如：2026-04-20 20:00" />
          </a-form-item>
          <a-form-item label="允许补交">
            <a-switch v-model:checked="assignForm.allowLate" />
          </a-form-item>
          <a-button type="primary" size="large" @click="assignHomework">确认布置</a-button>
        </a-form>
      </section>

      <div class="hw-side-column">
        <a-alert type="info" message="提示" description="确认布置后作业将对所选班级学生可见，截止前可修改布置范围。" show-icon />
        <div v-if="homework.instructions.length > 0" class="hw-tip-card">
          <p class="hw-tip-card__title">作业要求</p>
          <ul class="hw-tip-list">
            <li v-for="item in homework.instructions" :key="item">{{ item }}</li>
          </ul>
        </div>
        <div v-else class="hw-tip-card">
          <p class="hw-tip-card__text">暂无作业要求说明。</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'

interface HomeworkAssignMock {
  id: string
  title: string
  summary: string
  instructions: string[]
  deadline: string
}

const assignMock: HomeworkAssignMock[] = [
  {
    id: 'hw-101',
    title: '需求分析作业一：角色旅程拆解',
    summary: '围绕教学平台案例输出角色旅程和验收边界。',
    instructions: ['说明三类角色任务链路。', '补充至少两条异常流。', '给出验收通过/失败口径。'],
    deadline: '2026-04-20 20:00'
  }
]

const classOptions = [
  { label: '软工 2401', value: '2401' },
  { label: '软工 2402', value: '2402' },
  { label: '前端 2401', value: 'F2401' }
]

const route = useRoute()
const router = useRouter()
const homework = computed(() => assignMock.find((item) => item.id === String(route.params.id)) ?? assignMock[0])

const assignForm = reactive({
  classCodes: [] as string[],
  deadline: homework.value.deadline,
  allowLate: true
})

function assignHomework(): void {
  if (assignForm.classCodes.length === 0) {
    message.error('请至少选择一个班级。')
    return
  }
  message.success('作业布置成功（Mock）。')
  router.push('/admin/homework')
}
</script>