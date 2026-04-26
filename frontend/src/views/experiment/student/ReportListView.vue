<template>
  <div class="app-page-shell">
    <!-- 面包屑导航 -->
    <div class="breadcrumb-nav">
      <a-breadcrumb>
        <a-breadcrumb-item>
          <a href="#" @click.prevent="router.push('/learning')">学习概览</a>
        </a-breadcrumb-item>
        <a-breadcrumb-item>
          <a href="#" @click.prevent="router.push('/experiments')">上机实验</a>
        </a-breadcrumb-item>
        <a-breadcrumb-item>实验报告</a-breadcrumb-item>
      </a-breadcrumb>
    </div>

    <!-- 页面头部 -->
    <section class="app-surface-card page-header-card">
      <div class="page-header">
        <div class="page-header__main">
          <p class="page-header__eyebrow">上机实验</p>
          <h1 class="page-header__title">我的实验报告</h1>
          <p class="page-header__description">查看所有已完成实验的报告，支持查看详情和打印。</p>
        </div>
        <div class="page-header__actions">
          <a-button @click="router.push('/experiments')">
            <template #icon><ArrowLeftOutlined /></template>
            返回实验列表
          </a-button>
        </div>
      </div>
    </section>

    <!-- 筛选栏 -->
    <section class="app-surface-card filter-card">
      <a-space :size="16" wrap>
        <div class="filter-item">
          <span class="filter-label">选择实验：</span>
          <a-select v-model:value="selectedExperiment" placeholder="全部实验" style="width: 200px" allow-clear>
            <a-select-option value="">全部实验</a-select-option>
            <a-select-option v-for="exp in experiments" :key="exp.id" :value="exp.id">
              {{ exp.title }}
            </a-select-option>
          </a-select>
        </div>
        <div class="filter-item">
          <span class="filter-label">批改状态：</span>
          <a-select v-model:value="selectedStatus" placeholder="全部状态" style="width: 140px" allow-clear>
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="reviewed">已批改</a-select-option>
            <a-select-option value="submitted">待批改</a-select-option>
          </a-select>
        </div>
      </a-space>
    </section>

    <!-- 报告列表 -->
    <section class="app-surface-card table-card">
      <a-table :columns="columns" :data-source="filteredReports" :loading="loading" row-key="id" :pagination="paginationConfig" @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'experiment'">
            <div class="experiment-info">
              <span class="experiment-no">实验{{ record.experimentNo }}</span>
              <span class="experiment-name">{{ record.experimentName }}</span>
            </div>
          </template>

          <template v-else-if="column.key === 'submitTime'">
            <span class="time-text">{{ record.submittedAt }}</span>
          </template>

          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 'reviewed' ? 'success' : 'processing'">
              {{ record.status === 'reviewed' ? '已批改' : '待批改' }}
            </a-tag>
          </template>

          <template v-else-if="column.key === 'score'">
            <span v-if="record.teacherScore !== undefined" class="score-value">
              {{ record.teacherScore }} 分
            </span>
            <span v-else class="score-pending">待评分</span>
          </template>

          <template v-else-if="column.key === 'action'">
            <a-space :size="8">
              <a-button type="link" @click="router.push(`/experiments/${record.experimentId}/report`)">
                <template #icon><FileTextOutlined /></template>
                查看报告
              </a-button>
              <a-button v-if="record.status === 'reviewed'" type="link" @click="showFeedbackModal(record)">
                <template #icon><MessageOutlined /></template>
                查看反馈
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </section>

    <!-- 教师反馈弹窗 -->
    <a-modal
      v-model:open="feedbackModalVisible"
      title="教师反馈"
      :width="500"
      :footer="null"
    >
      <div v-if="currentFeedback" class="feedback-modal">
        <div class="feedback-header">
          <h3>{{ currentFeedback.experimentName }}</h3>
          <span class="feedback-date">{{ currentFeedback.reviewedAt }}</span>
        </div>
        <div class="feedback-score">
          <span class="score-label">得分：</span>
          <span class="score-value">{{ currentFeedback.teacherScore }} / {{ currentFeedback.totalScore }}</span>
        </div>
        <div class="feedback-content">
          <h4>教师评语：</h4>
          <p>{{ currentFeedback.teacherFeedback || '暂无评语' }}</p>
        </div>
        <div v-if="currentFeedback.stepFeedbacks?.length" class="step-feedbacks">
          <h4>各题反馈：</h4>
          <div v-for="(fb, index) in currentFeedback.stepFeedbacks" :key="index" class="step-feedback-item">
            <span class="step-label">第{{ index + 1 }}步：</span>
            <span class="step-score">{{ fb.score }}分</span>
            <span v-if="fb.comment" class="step-comment">- {{ fb.comment }}</span>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowLeftOutlined,
  FileTextOutlined,
  MessageOutlined
} from '@ant-design/icons-vue'
import type { StudentReportInfo } from '@/stores/experiment/types'
import { experimentRepository } from '@/stores/experiment/repository'
import { userContextFactory } from '@/stores/experiment/UserContextFactory'

const router = useRouter()

// 页面状态
const loading = ref(false)
const selectedExperiment = ref('')
const selectedStatus = ref('')
const feedbackModalVisible = ref(false)
const currentFeedback = ref<any>(null)

// 实验列表
const experiments = ref<Array<{ id: string; title: string }>>([])

// 报告列表
const reports = ref<StudentReportInfo[]>([])

const columns = [
  {
    title: '序号',
    key: 'index',
    width: 60,
    align: 'center' as const
  },
  {
    title: '实验信息',
    key: 'experiment',
    minWidth: 200
  },
  {
    title: '提交时间',
    key: 'submitTime',
    width: 160
  },
  {
    title: '批改状态',
    key: 'status',
    width: 100,
    align: 'center' as const
  },
  {
    title: '得分',
    key: 'score',
    width: 100,
    align: 'center' as const
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    align: 'center' as const
  }
]

const paginationConfig = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`
})

// 筛选后的报告
const filteredReports = computed(() => {
  return reports.value.filter(report => {
    if (selectedExperiment.value && report.experimentId !== selectedExperiment.value) {
      return false
    }
    if (selectedStatus.value && report.status !== selectedStatus.value) {
      return false
    }
    return true
  })
})

function handleTableChange(pag: { current?: number; pageSize?: number }) {
  paginationConfig.current = pag.current ?? 1
  paginationConfig.pageSize = pag.pageSize ?? 10
}

function showFeedbackModal(record: StudentReportInfo) {
  // 从报告数据中获取反馈信息
  currentFeedback.value = {
    experimentName: record.experimentName || '实验报告',
    teacherScore: record.teacherScore || 0,
    totalScore: record.totalScore || 100,
    teacherFeedback: record.teacherFeedback || '暂无评语',
    reviewedAt: record.submittedAt
  }
  feedbackModalVisible.value = true
}

// ==================== 用户信息已迁移至 userContextFactory ====================
// 旧 getCurrentUser() / getLoginUser() 已移除

// 加载数据
async function loadData() {
  loading.value = true
  try {
    // 获取实验列表
    const experimentList = await experimentRepository.listStudentExperiments()
    experiments.value = experimentList.map(exp => ({
      id: exp.id,
      title: exp.title
    }))

    // 获取学生报告列表
    const studentId = userContextFactory.getUserIdStr()
    const currentUser = userContextFactory.getCurrent()

    const reportList = await experimentRepository.getStudentReportList(studentId)

    // 转换为 StudentReportInfo 格式
    reports.value = reportList.map((report: any) => {
      const teacherScore = report.summary
      const status = teacherScore !== undefined && teacherScore !== null && teacherScore !== ''
        ? 'reviewed'
        : (report.submittedAt ? 'submitted' : 'pending')

      return {
        studentId: report.student?.id || studentId,
        studentNo: report.student?.no || currentUser.account,
        studentName: report.student?.name || currentUser.name,
        clazzNo: report.student?.clazzNo || currentUser.classCode,
        experimentId: String(report.experiment?.id || ''),
        experimentName: report.experiment?.name || '',
        submittedAt: report.submittedAt || '',
        totalScore: 100,
        teacherScore: teacherScore ? parseInt(teacherScore) : undefined,
        teacherFeedback: report.teacherFeedback,
        status: status
      }
    })

    paginationConfig.total = reports.value.length
  } catch (error) {
    console.error('加载报告数据失败:', error)
    reports.value = []
    experiments.value = []
    paginationConfig.total = 0
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.app-page-shell {
  padding: var(--space-5);
  min-height: 100%;
}

/* 面包屑导航 */
.breadcrumb-nav {
  margin-bottom: var(--space-5);
}

.breadcrumb-nav :deep(.ant-breadcrumb) {
  font-size: 14px;
}

.breadcrumb-nav :deep(.ant-breadcrumb a) {
  color: var(--color-primary);
  transition: color 0.2s;
}

.breadcrumb-nav :deep(.ant-breadcrumb a:hover) {
  color: var(--color-primary-hover);
}

/* 页面头部卡片 */
.page-header-card {
  padding: var(--space-5);
  margin-bottom: var(--space-5);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-5);
}

.page-header__eyebrow {
  margin: 0 0 8px;
  color: var(--color-primary);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.page-header__title {
  margin: 0;
  color: var(--color-text-main);
  font-size: 28px;
  font-weight: 700;
  line-height: 1.25;
}

.page-header__description {
  max-width: 600px;
  margin: 12px 0 0;
  color: var(--color-text-secondary);
  font-size: 14px;
  line-height: 1.65;
}

.page-header__actions {
  flex-shrink: 0;
}

/* 筛选栏 */
.filter-card {
  padding: var(--space-4);
  margin-bottom: var(--space-5);
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: var(--color-text-secondary);
}

/* 表格卡片 */
.table-card {
  padding: var(--space-4);
}

/* 实验信息单元格 */
.experiment-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.experiment-no {
  font-size: 12px;
  color: var(--color-text-tertiary);
}

.experiment-name {
  font-size: 15px;
  font-weight: 500;
  color: var(--color-text-main);
}

.time-text {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.score-value {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-primary);
}

.score-pending {
  font-size: 13px;
  color: var(--color-text-tertiary);
}

/* 反馈弹窗 */
.feedback-modal {
  padding: 8px 0;
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.feedback-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.feedback-date {
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.feedback-score {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: var(--color-bg-muted);
  border-radius: 8px;
  margin-bottom: 16px;
}

.feedback-score .score-label {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.feedback-score .score-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-primary);
}

.feedback-content h4 {
  margin: 0 0 8px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.feedback-content p {
  margin: 0;
  font-size: 15px;
  line-height: 1.7;
  color: var(--color-text-main);
}

.step-feedbacks {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

.step-feedbacks h4 {
  margin: 0 0 12px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.step-feedback-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  border-bottom: 1px dashed var(--color-border);
}

.step-label {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.step-score {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-primary);
}

.step-comment {
  font-size: 13px;
  color: var(--color-text-tertiary);
}

/* 表格样式覆盖 */
:deep(.ant-table) {
  font-size: 14px;
}

:deep(.ant-table-thead > tr > th) {
  padding: var(--space-4);
  background: var(--color-bg-muted);
  border-bottom: 1px solid var(--color-border);
  font-weight: 600;
  color: var(--color-text-main);
}

:deep(.ant-table-tbody > tr > td) {
  padding: var(--space-4);
  border-bottom: 1px solid var(--color-border);
  vertical-align: middle;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: var(--color-bg-spotlight);
}

/* 响应式 */
@media (max-width: 768px) {
  .app-page-shell {
    padding: var(--space-4);
  }

  .page-header {
    flex-direction: column;
    gap: var(--space-4);
  }

  .page-header__actions {
    width: 100%;
  }

  .page-header__actions :deep(.ant-btn) {
    width: 100%;
  }
}
</style>
