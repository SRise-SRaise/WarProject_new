<template>
  <div class="teacher-report-page">
    <div v-if="isLoading" class="loading-mask">
      <a-spin size="large" tip="正在加载报告..." />
    </div>

    <header class="page-header">
      <div>
        <h1 class="report-title">{{ mode === 'view' ? '查看实验报告' : '实验报告批改' }}</h1>
        <p class="report-meta">
          <span><UserOutlined /> {{ reportData.student.name }} ({{ reportData.student.no }})</span>
          <span><TeamOutlined /> {{ reportData.student.clazzNo }}</span>
          <span><BookOutlined /> {{ reportData.experiment.name }}</span>
        </p>
      </div>
      <a-space :size="12">
        <a-button @click="router.push('/admin/experiments/reports')"><LeftOutlined />返回列表</a-button>
        <template v-if="mode === 'grade'">
          <a-button @click="goToPrevStudent" :disabled="!hasPrevStudent"><UpOutlined />上一学生</a-button>
          <a-button @click="goToNextStudent" :disabled="!hasNextStudent"><DownOutlined />下一学生</a-button>
          <a-button type="primary" :loading="isSaving" @click="handleSaveGrade"><SaveOutlined />保存批改结果</a-button>
        </template>
      </a-space>
    </header>

    <div v-if="mode === 'grade'" class="grade-overview">
      <div>总分：{{ currentTotalScore }} / {{ total满分 }}</div>
      <div>已批改：{{ gradedCount }} / {{ reportData.steps.length }}</div>
      <a-progress :percent="gradeProgress" :show-info="false" style="width: 120px" />
    </div>

    <main class="report-content">
      <section class="report-card">
        <h2>{{ reportData.experiment.name || '实验报告' }}</h2>
        <p>课程：{{ reportData.experiment.courseName }}</p>
        <p>班级：{{ reportData.student.clazzNo }}</p>
        <p>学号：{{ reportData.student.no }}</p>
        <p>姓名：{{ reportData.student.name }}</p>
        <p>日期：{{ reportData.submittedAt }}</p>
      </section>

      <section v-if="reportData.objective" class="report-card">
        <h3>一、实验目的</h3>
        <ol><li v-for="(item, index) in parsedObjective" :key="index">{{ item }}</li></ol>
      </section>

      <section v-if="reportData.content" class="report-card">
        <h3>二、实验内容</h3>
        <ol><li v-for="(item, index) in parsedContent" :key="index">{{ item }}</li></ol>
      </section>

      <section class="report-card">
        <h3>三、实验步骤</h3>
        <div v-for="(step, index) in reportData.steps" :key="step.id" class="step-item">
          <div class="step-header">
            <strong>{{ index + 1 }}. {{ step.title }}</strong>
            <a-tag :color="getTypeColor(step.type)">{{ QUESTION_TYPE_NAMES[step.type] }}</a-tag>
            <span>满分：{{ step.score }}</span>
          </div>
          <div class="step-body">
            <div>
              <div class="label">题目内容</div>
              <div v-html="renderQuestionContent(step)"></div>
              <div v-if="step.correctAnswer" class="answer-box">
                <div class="label">标准答案</div>
                <div>{{ step.correctAnswer }}</div>
              </div>
            </div>
            <div>
              <div class="label">学生答案</div>
              <template v-if="step.type === 2 || step.type === 5">
                <a-tag :color="isCorrect(step) ? 'success' : 'error'">{{ step.studentAnswer || '（未作答）' }}</a-tag>
              </template>
              <template v-else>
                <pre class="code-answer">{{ step.studentAnswer || '（未作答）' }}</pre>
              </template>
            </div>
          </div>
          <div v-if="mode === 'grade'" class="grade-panel">
            <span>得分：</span>
            <a-input-number v-model:value="step.teacherScore" :min="0" :max="step.score" size="small" />
            <span>/ {{ step.score }}</span>
            <a-input v-model:value="step.teacherComment" placeholder="添加评语（可选）" size="small" style="width: 260px" />
          </div>
        </div>
      </section>

      <section class="report-card">
        <h3>四、实验小结</h3>
        <div class="summary-box">{{ reportData.summaryNote || '（未填写）' }}</div>
        <div v-if="mode === 'grade'" class="grade-panel">
          <span>小结得分：</span>
          <a-input-number v-model:value="summaryScore" :min="0" :max="10" size="small" />
          <span>/ 10</span>
        </div>
      </section>

      <section class="report-card">
        <h3>五、教师总评</h3>
        <a-textarea v-model:value="reportData.teacherFeedback" placeholder="请输入教师评语..." :rows="4" show-count />
        <div class="tips"><AlertOutlined /> 评语将显示在学生的实验报告中</div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { AlertOutlined, BookOutlined, DownOutlined, LeftOutlined, SaveOutlined, TeamOutlined, UpOutlined, UserOutlined } from '@ant-design/icons-vue'
import { gradeReport, getTeacherReportListPage } from '@/api/eduExperimentReportController'
import { experimentRepository } from '@/stores/experiment/repository'
import type { ExperimentReport, ReportQuestion } from '@/stores/experiment/types'
import { QUESTION_TYPE_NAMES } from '@/stores/experiment/types'

const route = useRoute()
const router = useRouter()
const reportList = ref<Array<{ studentId: string; studentNo: string; studentName: string; clazzNo: string }>>([])
const currentReportIndex = ref(-1)
const mode = computed((): 'view' | 'grade' => (route.path.includes('/results/') ? 'grade' : 'view'))
const isLoading = ref(false)
const isSaving = ref(false)
const summaryScore = ref(0)

const reportData = reactive<ExperimentReport>({
  student: { id: '', no: '', name: '', clazzNo: '' },
  experiment: { id: '', name: '', courseName: '', schedule: '' },
  summary: '',
  objective: '',
  content: '',
  steps: [],
  summaryNote: '',
  submittedAt: '',
  teacherFeedback: ''
})

const hasPrevStudent = computed(() => currentReportIndex.value > 0)
const hasNextStudent = computed(() => currentReportIndex.value < reportList.value.length - 1)
const total满分 = computed(() => reportData.steps.reduce((sum, s) => sum + s.score, 0) + 10)
const currentTotalScore = computed(() => reportData.steps.reduce((sum, s) => sum + (s.teacherScore || 0), 0) + summaryScore.value)
const gradedCount = computed(() => reportData.steps.filter((s) => s.teacherScore !== undefined).length)
const gradeProgress = computed(() => (reportData.steps.length === 0 ? 0 : Math.round((gradedCount.value / reportData.steps.length) * 100)))
const parsedObjective = computed(() => (reportData.objective ? reportData.objective.split(/\n|；|;/).filter(Boolean) : []))
const parsedContent = computed(() => (reportData.content ? reportData.content.split(/\n|；|;/).filter(Boolean) : []))

function resetReportData() {
  Object.assign(reportData, {
    student: { id: '', no: '', name: '', clazzNo: '' },
    experiment: { id: '', name: '', courseName: '', schedule: '' },
    summary: '',
    objective: '',
    content: '',
    steps: [],
    summaryNote: '',
    submittedAt: '',
    teacherFeedback: ''
  })
  summaryScore.value = 0
}

function getTypeColor(type: number): string {
  const colors: Record<number, string> = { 1: 'blue', 2: 'green', 3: 'cyan', 4: 'orange', 5: 'purple', 6: 'red', 7: 'magenta' }
  return colors[type] || 'default'
}

function isCorrect(step: ReportQuestion): boolean {
  if (step.teacherScore === undefined || step.correctAnswer === undefined) return false
  return step.teacherScore === step.score
}

function renderQuestionContent(step: ReportQuestion): string {
  return step.type === 1 ? step.content.replace(/____/g, '<span class="blank-line">________</span>') : step.content
}

function goToPrevStudent() {
  if (currentReportIndex.value > 0) {
    const prevReport = reportList.value[currentReportIndex.value - 1]
    router.push(`/admin/experiments/results/${route.params.id}/${prevReport.studentId}`)
  }
}

function goToNextStudent() {
  if (currentReportIndex.value < reportList.value.length - 1) {
    const nextReport = reportList.value[currentReportIndex.value + 1]
    router.push(`/admin/experiments/results/${route.params.id}/${nextReport.studentId}`)
  }
}

async function handleSaveGrade() {
  isSaving.value = true
  try {
    await gradeReport({
      experimentId: Number(route.params.id),
      studentId: Number(route.params.studentId),
      scores: reportData.steps.map((step) => ({
        itemId: Number(step.experimentItemId || step.id),
        score: step.teacherScore || 0,
        comment: step.teacherComment
      })),
      totalScore: currentTotalScore.value,
      feedback: reportData.teacherFeedback
    })

    message.success('批改结果已保存')
    await loadReport()
    await loadReportList()
  } catch (error) {
    message.error('保存失败')
    console.error(error)
  } finally {
    isSaving.value = false
  }
}

async function loadReport() {
  const experimentId = String(route.params.id)
  const studentId = String(route.params.studentId)

  resetReportData()
  isLoading.value = true
  try {
    const report = await experimentRepository.getStudentReport(experimentId, studentId)
    if (report) {
      Object.assign(reportData, report)
      if (report.summary) {
        const totalStepScore = reportData.steps.reduce((sum, s) => sum + (s.teacherScore || 0), 0)
        summaryScore.value = Math.max(0, Number(report.summary) - totalStepScore)
      }
    } else {
      message.warning('未获取到实验报告数据')
    }
  } catch (error) {
    console.error('加载报告失败:', error)
    message.error('加载报告失败')
  } finally {
    isLoading.value = false
  }
}

async function loadReportList() {
  try {
    const res = await getTeacherReportListPage({ experimentId: Number(route.params.id), current: 1, pageSize: 100 })
    const page = res.data?.data
    if (page?.records) {
      reportList.value = page.records.map((r: any) => ({
        studentId: String(r.studentId),
        studentNo: r.studentNo,
        studentName: r.studentName,
        clazzNo: r.clazzNo
      }))
      currentReportIndex.value = reportList.value.findIndex((r) => r.studentId === String(route.params.studentId))
    } else {
      reportList.value = []
      currentReportIndex.value = -1
    }
  } catch (error) {
    console.error('加载报告列表失败:', error)
    reportList.value = []
    currentReportIndex.value = -1
  }
}

watch(
  () => [route.params.id, route.params.studentId],
  async () => {
    await loadReportList()
    await loadReport()
  },
  { immediate: true }
)
</script>

<style scoped>
.teacher-report-page { min-height: 100vh; background: #f5f7fa; }
.loading-mask { position: fixed; inset: 0; display: flex; align-items: center; justify-content: center; background: rgba(255,255,255,.75); z-index: 1000; }
.page-header { position: sticky; top: 0; z-index: 10; display: flex; justify-content: space-between; gap: 16px; align-items: flex-start; padding: 16px 24px; background: #fff; border-bottom: 1px solid #eee; }
.report-title { margin: 0 0 8px; font-size: 20px; }
.report-meta { display: flex; flex-wrap: wrap; gap: 16px; margin: 0; color: #666; }
.grade-overview { display: flex; gap: 24px; align-items: center; padding: 12px 24px; background: #fff; border-bottom: 1px solid #eee; }
.report-content { max-width: 960px; margin: 24px auto; padding: 0 16px 40px; }
.report-card { margin-bottom: 16px; padding: 20px; background: #fff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,.04); }
.step-item { margin-top: 16px; border: 1px solid #e8e8e8; border-radius: 8px; }
.step-header { display: flex; gap: 12px; align-items: center; padding: 12px 16px; background: #fafafa; }
.step-body { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; padding: 16px; }
.label { margin-bottom: 8px; color: #666; font-weight: 600; }
.answer-box, .summary-box, .code-answer { padding: 12px; background: #fafafa; border-radius: 6px; }
.code-answer { margin: 0; white-space: pre-wrap; word-break: break-word; }
.grade-panel { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; padding: 12px 16px; border-top: 1px dashed #e8e8e8; }
.tips { margin-top: 8px; color: #888; }
:deep(.blank-line) { display: inline-block; min-width: 72px; border-bottom: 1px solid #888; }
@media (max-width: 900px) { .page-header { flex-direction: column; } .step-body { grid-template-columns: 1fr; } }
</style>
