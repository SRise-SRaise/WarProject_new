<template>
  <div class="app-panel-grid">
    <div class="hw-page-header">
      <div class="hw-page-header__left">
        <h1 class="hw-page-header__title">{{ pageTitle }}</h1>
        <p class="hw-page-header__desc">维护作业基本信息并配置题目，保存草稿后可继续到布置页面发布。</p>
      </div>
      <div class="hw-page-header__actions">
        <a-button @click="router.push('/admin/homework')">返回作业列表</a-button>
      </div>
    </div>

    <section class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
          <a-form layout="vertical">
          <a-form-item label="作业标题" required>
            <a-input v-model:value="formState.title" size="large" placeholder="输入作业标题" />
          </a-form-item>
          <a-form-item label="关联实验">
            <a-input v-model:value="formState.relateExpId" size="large" placeholder="关联实验ID（可选）" />
          </a-form-item>
          <a-form-item label="开始时间">
            <a-date-picker
              v-model:value="formState.startTime"
              size="large"
              show-time
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm"
              placeholder="选择开始时间"
              style="width: 100%"
            />
          </a-form-item>
          <a-form-item label="截止时间" required>
            <a-date-picker
              v-model:value="formState.endTime"
              size="large"
              show-time
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm"
              placeholder="选择截止时间"
              style="width: 100%"
            />
          </a-form-item>
          <a-form-item label="作业描述">
            <a-textarea v-model:value="formState.description" :rows="4" placeholder="简要描述作业目标和要求" />
          </a-form-item>
            <a-space :size="10" wrap>
              <a-button size="large" :loading="saving" @click="saveHomework(false)">保存草稿</a-button>
              <a-button type="primary" size="large" :loading="saving" @click="saveHomework(true)">保存并去布置</a-button>
            </a-space>
          </a-form>
        </section>

      <div class="hw-side-column">
          <a-alert type="info" message="提示" description="创建完成后可在当前页面选择题目，布置页面支持再次微调。" show-icon />
          <div class="hw-tip-card">
            <p class="hw-tip-card__title">作业题目</p>
            <p class="hw-tip-card__text">请先保存草稿获取作业ID，再从题库加入题目。布置页面可继续微调后发布。</p>
            <p class="hw-tip-card__text" style="margin-top: 6px">当前已选：{{ questionStat.count }} 题，总分 {{ questionStat.totalScore }} 分。</p>
          </div>
        </div>
      </section>

      <QuestionPickerPanel :exercise-id="resolvedExerciseId" @change="onQuestionStatChange" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { addEduExercise, updateEduExercise, getEduExerciseVoById, listEduExerciseVoByPage } from '@/api/eduExerciseController'
import type { HomeworkQuestionStat } from '@/types/homework/assignment'
import QuestionPickerPanel from '@/components/homework/QuestionPickerPanel.vue'
import { useAuthStore } from '@/stores/user/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const homeworkId = computed(() => (typeof route.params.id === 'string' ? route.params.id : ''))
const pageTitle = computed(() => (homeworkId.value ? '编辑作业' : '新增作业'))
const resolvedExerciseId = computed(() => {
  return homeworkId.value || undefined
})
const saving = ref(false)
const questionStat = ref<HomeworkQuestionStat>({ count: 0, totalScore: 0 })

function onQuestionStatChange(payload: HomeworkQuestionStat): void {
  questionStat.value = payload
}

const formState = reactive({
  title: '',
  relateExpId: '',
  startTime: '',
  endTime: '',
  description: ''
})

function toFormDateTime(value: unknown): string {
  if (value === null || value === undefined || String(value).trim().length === 0) return ''
  const parsed = dayjs(String(value))
  return parsed.isValid() ? parsed.format('YYYY-MM-DD HH:mm') : String(value)
}

async function loadHomework() {
  if (!homeworkId.value) return

  try {
    const response = await getEduExerciseVoById({ id: homeworkId.value })
    const exercise = (response as any)?.data?.data ?? (response as any)?.data

    if (exercise) {
      formState.title = exercise.taskName || ''
      formState.relateExpId = exercise.relateExpId ? String(exercise.relateExpId) : ''
      formState.startTime = toFormDateTime(exercise.startTime)
      formState.endTime = toFormDateTime(exercise.endTime)
      formState.description = exercise.description || ''
    }
  } catch (error) {
    console.error('加载作业失败:', error)
    message.error('加载作业失败')
  }
}

async function resolveCreatedExerciseId(taskName: string): Promise<number | null> {
  try {
    const response = await listEduExerciseVoByPage({ current: 1, pageSize: 50 })
    const records = (response as any)?.data?.data?.records ?? (response as any)?.data?.records ?? []
    const teacherId = Number(authStore.session?.id) || 0
    const matched = (Array.isArray(records) ? records : [])
      .filter((item: any) => item?.taskName === taskName && (!teacherId || Number(item?.teacherId) === teacherId))
      .sort((a: any, b: any) => Number(b?.id || 0) - Number(a?.id || 0))[0]
    return matched?.id ? Number(matched.id) : null
  } catch (error) {
    console.error('查询新建作业ID失败:', error)
    return null
  }
}

async function saveHomework(goAssign: boolean) {
  if (formState.title.trim().length === 0) {
    message.error('请先填写标题')
    return
  }

  saving.value = true
  try {
    const teacherId = Number(authStore.session?.id) || 1

    if (homeworkId.value) {
      // 更新
      await updateEduExercise({
        id: Number(homeworkId.value),
        taskName: formState.title,
        relateExpId: formState.relateExpId ? Number(formState.relateExpId) : null,
        startTime: formState.startTime,
        endTime: formState.endTime,
        description: formState.description
      })
      message.success('作业已更新')
      if (goAssign) {
        router.push(`/admin/homework/assign/${homeworkId.value}`)
      }
    } else {
      // 新增
      await addEduExercise({
        taskName: formState.title,
        teacherId,
        relateExpId: formState.relateExpId ? Number(formState.relateExpId) : null,
        startTime: formState.startTime,
        endTime: formState.endTime,
        description: formState.description,
        publishStatus: 0
      })
      const createdId = await resolveCreatedExerciseId(formState.title)
      message.success('作业已创建')
      if (createdId) {
        if (goAssign) {
          router.push(`/admin/homework/assign/${createdId}`)
        } else {
          router.replace(`/admin/homework/edit/${createdId}`)
        }
        return
      }
      message.warning('作业已创建，但未能定位到作业ID，请在作业列表中重新进入编辑。')
    }
  } catch (error) {
    console.error('保存失败:', error)
    message.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadHomework()
})
</script>
