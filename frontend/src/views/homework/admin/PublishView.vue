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
            <a-date-picker
              v-model:value="assignForm.deadline"
              size="large"
              show-time
              format="YYYY-MM-DD HH:mm"
              placeholder="选择截止时间"
              style="width: 100%"
            />
          </a-form-item>
          <a-form-item label="允许补交">
            <a-switch v-model:checked="assignForm.allowLate" />
          </a-form-item>
          <p class="hw-tip-card__text" style="margin-bottom: 12px">当前题目：{{ questionStat.count }} 题，总分 {{ questionStat.totalScore }} 分。</p>
          <a-space :size="10" wrap>
            <a-button size="large" :loading="draftSaving" @click="saveDraft">保存并退出</a-button>
            <a-button type="primary" size="large" :loading="publishing" @click="assignHomework">确认布置</a-button>
          </a-space>
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

    <QuestionPickerPanel :exercise-id="resolvedExerciseId" @change="onQuestionStatChange" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/user/auth'
import { getEduExerciseVoById, publishExercise } from '@/api/eduExerciseController'
import { saveHomeworkAssignDraft } from '@/api/homeworkAssignmentController'
import { listAuthClassVoByPage } from '@/api/authClassController'
import type { HomeworkQuestionStat } from '@/types/homework/assignment'
import QuestionPickerPanel from '@/components/homework/QuestionPickerPanel.vue'

interface HomeworkAssignData {
  id: string
  title: string
  summary: string
  instructions: string[]
  deadline: string
}

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const homework = ref<HomeworkAssignData | null>(null)
const publishing = ref(false)
const draftSaving = ref(false)
const classOptions = ref<{ label: string; value: string }[]>([])
const questionStat = ref<HomeworkQuestionStat>({ count: 0, totalScore: 0 })
const resolvedExerciseId = computed(() => {
  const id = String(route.params.id || '').trim()
  return id.length > 0 ? id : undefined
})

const assignForm = reactive({
  classCodes: [] as string[],
  deadline: null as dayjs.Dayjs | null,
  allowLate: true
})

async function loadData() {
  const homeworkId = route.params.id as string

  if (!homeworkId) return

  try {
    // 加载作业信息
    const response = await getEduExerciseVoById({ id: homeworkId })
    const exercise = response.data?.data

    if (exercise) {
      homework.value = {
        id: homeworkId,
        title: exercise.taskName || '',
        summary: exercise.description || '',
        instructions: [],
        deadline: exercise.endTime || ''
      }
      if (exercise.endTime) {
        assignForm.deadline = dayjs(exercise.endTime)
      }
    }

    // 加载班级列表
    const classResponse = await listAuthClassVoByPage({ current: 1, pageSize: 50 })
    if (classResponse.data?.data?.records) {
      classOptions.value = classResponse.data.data.records.map((cls) => ({
        label: cls.classCode || '',
        value: cls.classCode || ''
      }))
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

async function assignHomework() {
  if (assignForm.classCodes.length === 0) {
    message.error('请至少选择一个班级')
    return
  }

if (!assignForm.deadline) {
    message.error('请选择截止时间')
    return
  }

  if (questionStat.value.count <= 0) {
    message.error('请至少添加 1 道题目')
    return
  }

  const homeworkId = route.params.id as string
  if (!homeworkId) return

publishing.value = true
  try {
    const endTimeValue = assignForm.deadline.format('YYYY-MM-DDTHH:mm:ss')
    await publishExercise({
      exerciseId: Number(homeworkId),
      classCodes: assignForm.classCodes,
      endTime: endTimeValue,
      allowLate: assignForm.allowLate
    })
    message.success('作业布置成功')
    router.push('/admin/homework')
  } catch (error) {
    console.error('布置失败:', error)
    message.error('布置失败')
  } finally {
    publishing.value = false
  }
}

function onQuestionStatChange(payload: HomeworkQuestionStat): void {
  questionStat.value = payload
}

async function saveDraft() {
  const homeworkId = route.params.id as string
  if (!homeworkId) return

draftSaving.value = true
  try {
    const endTimeValue = assignForm.deadline.format('YYYY-MM-DDTHH:mm:ss')
    await saveHomeworkAssignDraft({
      exerciseId: Number(homeworkId),
      classCodes: assignForm.classCodes,
      endTime: endTimeValue,
      allowLate: assignForm.allowLate
    })
    message.success('布置草稿已保存')
    router.push('/admin/homework')
  } catch (error) {
    console.error('保存草稿失败:', error)
    message.error('保存草稿失败')
  } finally {
    draftSaving.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
