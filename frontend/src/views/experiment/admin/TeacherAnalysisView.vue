<template>
  <div class="app-page-shell">
    <!-- 面包屑导航 -->
    <div class="breadcrumb-nav">
      <a-breadcrumb>
        <a-breadcrumb-item>
          <a href="#" @click.prevent="router.push('/admin/experiments/list')">实验管理</a>
        </a-breadcrumb-item>
        <a-breadcrumb-item>数据分析</a-breadcrumb-item>
      </a-breadcrumb>
    </div>

    <!-- 页面头部 -->
    <section class="app-surface-card page-header-card">
      <div class="page-header">
        <div class="page-header__main">
          <p class="page-header__eyebrow">实验管理</p>
          <h1 class="page-header__title">实验数据分析</h1>
          <p class="page-header__description">查看单个实验的完成情况、得分分布，或统计所有实验的总体情况。</p>
        </div>
        <div class="page-header__actions">
          <a-button @click="router.push('/admin/experiments/list')">
            <template #icon><ArrowLeftOutlined /></template>
            返回实验列表
          </a-button>
        </div>
      </div>
    </section>

    <!-- 筛选栏 -->
    <section class="app-surface-card filter-card">
      <a-form layout="inline" :model="filterForm">
        <a-form-item label="选择实验">
          <a-select
            v-model:value="filterForm.experimentId"
            placeholder="全部实验（全局统计）"
            style="width: 280px"
            allow-clear
            :loading="adminStore.loading"
            :not-found-content="adminStore.loading ? '加载中...' : '暂无实验数据'"
            @change="handleExperimentChange"
          >
            <a-select-option value="">全部实验（全局统计）</a-select-option>
            <a-select-option v-for="exp in experiments" :key="exp.id" :value="exp.id">
              {{ exp.sortOrder ? `步骤${exp.sortOrder} - ` : '' }}{{ exp.title }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item v-if="filterForm.experimentId" label="选择班级">
          <a-select v-model:value="filterForm.clazzNo" placeholder="全部班级" style="width: 180px" allow-clear>
            <a-select-option value="">全部班级</a-select-option>
            <a-select-option v-for="clazz in classes" :key="clazz" :value="clazz">{{ clazz }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space :size="12">
            <a-button type="primary" @click="loadData" :loading="loading">
              <template #icon><SearchOutlined /></template>
              查询
            </a-button>
            <a-button v-if="filterForm.experimentId" @click="loadData">
              <template #icon><ReloadOutlined /></template>
              刷新
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </section>

    <!-- 加载状态 -->
    <section v-if="loading" class="loading-section">
      <a-spin size="large">
        <template #tip>正在加载数据...</template>
      </a-spin>
    </section>

    <!-- 查询出错 -->
    <section v-else-if="queryError" class="empty-section">
      <a-result status="error" title="查询失败" :sub-title="queryError">
        <template #extra>
          <a-button type="primary" @click="loadData">重试</a-button>
        </template>
      </a-result>
    </section>

    <!-- 未查询过（初始状态） -->
    <section v-else-if="!queried" class="empty-section">
      <a-result status="info" title="请选择筛选条件并查询" sub-title="选择具体实验可查看该实验的完成情况和步骤得分分析；不选择实验则显示全局统计。">
        <template #extra>
          <a-button type="primary" @click="loadData" :loading="loading">立即查询</a-button>
        </template>
      </a-result>
    </section>

    <!-- 查询无数据 -->
    <section v-else-if="queryEmpty" class="empty-section">
      <a-result status="info" title="暂无数据" sub-title="该实验暂无学生提交记录，或实验尚未开始。">
        <template #extra>
          <a-button @click="loadData">刷新</a-button>
        </template>
      </a-result>
    </section>

    <!-- 单实验分析 -->
    <template v-else-if="filterForm.experimentId && analysisData">
      <!-- 实验信息卡片 -->
      <section class="app-surface-card info-card">
        <div class="experiment-info">
          <div class="exp-info-left">
            <div class="exp-badge">
              <a-tag :color="getTypeColor(analysisData.experimentTypeName)">{{ analysisData.experimentTypeName || '未分类' }}</a-tag>
              <span class="exp-id">ID: {{ analysisData.experimentId }}</span>
            </div>
            <h2 class="exp-name">{{ analysisData.experimentName }}</h2>
          </div>
          <div class="exp-info-right">
            <a-tag v-if="analysisData.clazzNo" color="blue">班级：{{ analysisData.clazzNo }}</a-tag>
            <a-tag v-else color="default">全部班级</a-tag>
          </div>
        </div>
      </section>

      <!-- 完成情况统计 -->
      <section class="app-surface-card stats-section">
        <h3 class="section-title">完成情况</h3>
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon" style="background: #e6f4ff;">
              <UserOutlined style="font-size: 22px; color: #1677ff;" />
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ analysisData.totalStudents ?? 0 }}</p>
              <p class="stat-label">应交人数</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: #f6ffed;">
              <CheckCircleOutlined style="font-size: 22px; color: #52c41a;" />
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ analysisData.submittedStudents ?? 0 }}</p>
              <p class="stat-label">已提交</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: #fff7e6;">
              <EditOutlined style="font-size: 22px; color: #fa8c16;" />
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ analysisData.gradedStudents ?? 0 }}</p>
              <p class="stat-label">已批改</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: #f9f0ff;">
              <PercentageOutlined style="font-size: 22px; color: #722ed1;" />
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ analysisData.submissionRate ?? 0 }}<span class="stat-unit">%</span></p>
              <p class="stat-label">提交率</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 成绩统计 -->
      <section class="app-surface-card score-section">
        <h3 class="section-title">成绩统计</h3>
        <div class="score-grid">
          <div class="score-card score-card--primary">
            <div class="score-main">
              <span class="score-big">{{ analysisData.classAverageScore ?? 0 }}</span>
              <span class="score-total">分</span>
            </div>
            <p class="score-desc">班级平均分</p>
          </div>
          <div class="score-stat-card">
            <div class="score-stat-icon" style="color: #52c41a;">
              <ArrowUpOutlined />
            </div>
            <div class="score-stat-info">
              <p class="score-stat-value">{{ analysisData.classMaxScore ?? 0 }}</p>
              <p class="score-stat-label">最高分</p>
            </div>
          </div>
          <div class="score-stat-card">
            <div class="score-stat-icon" style="color: #fa8c16;">
              <ArrowDownOutlined />
            </div>
            <div class="score-stat-info">
              <p class="score-stat-value">{{ analysisData.classMinScore ?? 0 }}</p>
              <p class="score-stat-label">最低分</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 得分分布图 -->
      <section class="app-surface-card chart-section">
        <h3 class="section-title">得分分布</h3>
        <div class="chart-wrapper">
          <div ref="barChartRef" class="chart-container"></div>
        </div>
        <div v-if="!analysisData.scoreDistribution || analysisData.scoreDistribution.length === 0" class="chart-empty">
          <a-empty description="暂无批改数据" />
        </div>
      </section>

      <!-- 步骤得分分析 -->
      <section class="app-surface-card step-score-section">
        <div class="step-score-header">
          <h3 class="section-title" style="margin-bottom:0">各步骤得分分析</h3>
          <span class="step-score-subtitle">按题目统计平均分、得分率及作答情况</span>
        </div>

        <div v-if="!analysisData.stepScoreAnalysis || analysisData.stepScoreAnalysis.length === 0" class="chart-empty">
          <a-empty description="暂无步骤批改数据" />
        </div>

        <template v-else>
          <!-- 水平条形图 -->
          <div
            ref="stepChartRef"
            class="step-chart-container"
            :style="{ height: Math.max(260, (analysisData.stepScoreAnalysis?.length ?? 0) * 42 + 60) + 'px' }"
          ></div>

          <!-- 明细表格 -->
          <a-table
            :columns="stepColumns"
            :data-source="analysisData.stepScoreAnalysis"
            :pagination="false"
            row-key="sortOrder"
            class="step-table"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'itemName'">
                <div class="step-name-cell">
                  <span class="step-index">{{ record.sortOrder }}</span>
                  <span class="step-name">{{ record.itemName || '未命名步骤' }}</span>
                  <a-tag v-if="record.questionType === 7" color="purple" style="margin-left:4px;font-size:11px">小结</a-tag>
                </div>
              </template>
              <template v-else-if="column.key === 'avgScore'">
                <span :class="getStepScoreClass(record.scoreRate)">
                  {{ record.avgScore ?? 0 }} / {{ record.maxScore ?? 0 }}
                </span>
              </template>
              <template v-else-if="column.key === 'scoreRate'">
                <div class="rate-cell">
                  <a-progress
                    :percent="record.scoreRate ?? 0"
                    :stroke-color="getStepRateColor(record.scoreRate)"
                    size="small"
                    :show-info="false"
                    style="flex:1;min-width:80px"
                  />
                  <span class="rate-text" :class="getStepScoreClass(record.scoreRate)">
                    {{ record.scoreRate ?? 0 }}%
                  </span>
                </div>
              </template>
            </template>
          </a-table>
        </template>
      </section>
    </template>

    <!-- 全局统计 -->
    <template v-else-if="!filterForm.experimentId && analysisData">
      <!-- 全局概览 -->
      <section class="app-surface-card overview-section">
        <h3 class="section-title">全局概览</h3>
        <div class="overview-grid">
          <div class="overview-card">
            <div class="overview-icon" style="background: #e6f4ff;">
              <ExperimentOutlined style="font-size: 24px; color: #1677ff;" />
            </div>
            <div class="overview-info">
              <p class="overview-value">{{ analysisData.totalExperiments ?? 0 }}</p>
              <p class="overview-label">已发布实验</p>
            </div>
          </div>
          <div class="overview-card">
            <div class="overview-icon" style="background: #f6ffed;">
              <CheckCircleOutlined style="font-size: 24px; color: #52c41a;" />
            </div>
            <div class="overview-info">
              <p class="overview-value">{{ analysisData.activeExperiments ?? 0 }}</p>
              <p class="overview-label">有学生提交</p>
            </div>
          </div>
          <div class="overview-card">
            <div class="overview-icon" style="background: #fff7e6;">
              <EditOutlined style="font-size: 24px; color: #fa8c16;" />
            </div>
            <div class="overview-info">
              <p class="overview-value">{{ analysisData.gradedExperiments ?? 0 }}</p>
              <p class="overview-label">已批改</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 各类型实验统计 -->
      <section class="app-surface-card type-section">
        <h3 class="section-title">各类型实验统计</h3>
        <p class="section-desc">按实验类型（编程实践、设计实现、数据库、前端开发、框架学习、综合实验）统计数量与批改情况。</p>
        <a-table
          :columns="typeColumns"
          :data-source="analysisData.experimentTypeAnalysis || []"
          :pagination="false"
          row-key="typeCode"
          class="type-table"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'typeCode'">
              <a-tag :color="getTypeColor(record.typeName)">{{ record.typeName }}</a-tag>
            </template>
            <template v-else-if="column.key === 'avgScore'">
              <span :class="getScoreClass(record.averageScore)">{{ record.averageScore }} 分</span>
            </template>
            <template v-else-if="column.key === 'progress'">
              <div class="progress-cell">
                <a-progress
                  :percent="getSubmitProgress(record)"
                  :stroke-color="getProgressColor(record)"
                  size="small"
                  :show-info="false"
                />
                <span class="progress-text">{{ record.submittedCount }} / {{ record.experimentCount }}</span>
              </div>
            </template>
          </template>
        </a-table>
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import {
  ArrowLeftOutlined,
  SearchOutlined,
  ReloadOutlined,
  UserOutlined,
  CheckCircleOutlined,
  EditOutlined,
  PercentageOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  ExperimentOutlined
} from '@ant-design/icons-vue'
import type { ECharts } from 'echarts'
import * as echarts from 'echarts'
import { useExperimentAdminStore } from '@/stores/experiment/admin'
import { experimentRepository } from '@/stores/experiment/repository'

const router = useRouter()
const adminStore = useExperimentAdminStore()
const { experiments } = storeToRefs(adminStore)

const loading = ref(false)
const queryError = ref('')
const filterForm = ref({
  experimentId: '' as string,
  clazzNo: '' as string
})
const analysisData = ref<any>(null)
// 是否已进行过至少一次查询
const queried = ref(false)
const experimentClasses = ref<string[]>([])

const barChartRef = ref<HTMLElement>()
let barChart: ECharts | null = null

const stepChartRef = ref<HTMLElement>()
let stepChart: ECharts | null = null

const stepColumns = [
  { title: '步骤', key: 'itemName', ellipsis: true },
  { title: '平均分 / 满分', key: 'avgScore', width: 130, align: 'center' as const },
  { title: '最高分', dataIndex: 'highScore', key: 'highScore', width: 90, align: 'center' as const },
  { title: '最低分', dataIndex: 'lowScore', key: 'lowScore', width: 90, align: 'center' as const },
  { title: '作答人数', dataIndex: 'answeredCount', key: 'answeredCount', width: 100, align: 'center' as const },
  { title: '得分率', key: 'scoreRate', width: 180 }
]

const classes = computed(() => experimentClasses.value)

// 已查询过且有数据
const hasData = computed(() => queried.value && analysisData.value !== null)
// 已查询过但无数据
const queryEmpty = computed(() => queried.value && analysisData.value === null)

const typeColumns = [
  { title: '实验类型', key: 'typeCode', width: 140 },
  { title: '实验数量', dataIndex: 'experimentCount', key: 'experimentCount', width: 100, align: 'center' as const },
  { title: '已提交', dataIndex: 'submittedCount', key: 'submittedCount', width: 100, align: 'center' as const },
  { title: '已批改', dataIndex: 'gradedCount', key: 'gradedCount', width: 100, align: 'center' as const },
  { title: '总提交人次', dataIndex: 'totalSubmissions', key: 'totalSubmissions', width: 120, align: 'center' as const },
  { title: '提交进度', key: 'progress', width: 200 },
  { title: '平均分', key: 'avgScore', width: 120, align: 'center' as const }
]

function getTypeColor(typeName: string): string {
  const colorMap: Record<string, string> = {
    '编程实践': 'blue',
    '设计实现': 'cyan',
    '数据库': 'green',
    '前端开发': 'orange',
    '框架学习': 'purple',
    '综合实验': 'magenta'
  }
  return colorMap[typeName] || 'default'
}

function getScoreClass(score: number): string {
  if (score >= 90) return 'score-excellent'
  if (score >= 75) return 'score-good'
  if (score >= 60) return 'score-ok'
  return 'score-bad'
}

function getSubmitProgress(record: any): number {
  if (!record.experimentCount || record.experimentCount === 0) return 0
  return Math.round((record.submittedCount / record.experimentCount) * 100)
}

function getProgressColor(record: any): string {
  const p = getSubmitProgress(record)
  if (p >= 80) return '#52c41a'
  if (p >= 50) return '#fa8c16'
  return '#ff4d4f'
}

async function loadData() {
  loading.value = true
  queryError.value = ''
  try {
    await adminStore.ensureLoaded()
    const experimentId = filterForm.value.experimentId || undefined
    const clazzNo = filterForm.value.clazzNo || undefined
    const data = await experimentRepository.getExperimentAnalysis(experimentId, clazzNo)
    analysisData.value = data
    queried.value = true
    if (data && experimentId) {
      await nextTick()
      renderBarChart()
      renderStepChart()
    }
  } catch (e: any) {
    console.error('加载数据分析失败:', e)
    queryError.value = e?.message || '查询失败，请重试'
    analysisData.value = null
    queried.value = true
  } finally {
    loading.value = false
  }
}

function handleExperimentChange() {
  filterForm.value.clazzNo = ''
  experimentClasses.value = []
  // 切换实验时重置数据，让用户点击"查询"再加载，避免显示旧实验的数据
  analysisData.value = null
  queried.value = false
  queryError.value = ''
  // 销毁旧图表，切换实验后图表需要重新初始化
  barChart?.dispose()
  barChart = null
  stepChart?.dispose()
  stepChart = null
  if (filterForm.value.experimentId) {
    loadExperimentClasses()
  }
}

async function loadExperimentClasses() {
  if (!filterForm.value.experimentId) return
  try {
    const classes = await experimentRepository.getExperimentClasses(filterForm.value.experimentId)
    experimentClasses.value = classes
  } catch (e) {
    console.error('加载班级列表失败:', e)
    experimentClasses.value = []
  }
}

function renderBarChart() {
  if (!barChartRef.value || !analysisData.value?.scoreDistribution) return

  if (barChart) {
    barChart.dispose()
    barChart = null
  }
  barChart = echarts.init(barChartRef.value)

  const dist = analysisData.value.scoreDistribution
  const labels = dist.map((d: any) => d.label)
  const counts = dist.map((d: any) => d.count)
  const percentages = dist.map((d: any) => d.percentage)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params: any) => {
        const idx = params[0].dataIndex
        const item = dist[idx]
        return `<b>${item.label}</b><br/>人数：${item.count} 人<br/>占比：${item.percentage}%`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: labels,
      axisLabel: { fontSize: 12, interval: 0, rotate: 15 },
      axisLine: { lineStyle: { color: '#e8eaed' } }
    },
    yAxis: {
      type: 'value',
      name: '人数',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f0f0f0' } }
    },
    series: [
      {
        name: '人数',
        type: 'bar',
        data: counts,
        barWidth: '55%',
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          color: (params: any) => {
            const colors = ['#ff4d4f', '#fa8c16', '#faad14', '#52c41a', '#1677ff']
            return colors[params.dataIndex] || '#1677ff'
          }
        },
        label: {
          show: true,
          position: 'top',
          formatter: (params: any) => {
            const p = percentages[params.dataIndex]
            return `${params.value}人\n(${p}%)`
          },
          fontSize: 11,
          color: '#595959'
        }
      }
    ]
  }

  barChart.setOption(option)
}

function getStepScoreClass(rate: number): string {
  if (rate >= 85) return 'score-excellent'
  if (rate >= 70) return 'score-good'
  if (rate >= 55) return 'score-ok'
  return 'score-bad'
}

function getStepRateColor(rate: number): string {
  if (rate >= 85) return '#52c41a'
  if (rate >= 70) return '#4f6ef7'
  if (rate >= 55) return '#fa8c16'
  return '#ff4d4f'
}

function renderStepChart() {
  const steps = analysisData.value?.stepScoreAnalysis
  if (!stepChartRef.value || !steps || steps.length === 0) return

  // 若已有实例先销毁，确保容器尺寸改变后重新初始化
  if (stepChart) {
    stepChart.dispose()
    stepChart = null
  }
  stepChart = echarts.init(stepChartRef.value)

  const names = steps.map((s: any) => `步骤${s.sortOrder} ${s.itemName?.length > 8 ? s.itemName.slice(0, 8) + '…' : (s.itemName || '')}`)
  const avgScores = steps.map((s: any) => s.avgScore ?? 0)
  const maxScores = steps.map((s: any) => s.maxScore ?? 0)
  const rates = steps.map((s: any) => s.scoreRate ?? 0)

  stepChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params: any) => {
        const idx = params[0].dataIndex
        const s = steps[idx]
        return `<b>步骤${s.sortOrder}：${s.itemName || '未命名'}</b><br/>
                平均分：${s.avgScore ?? 0} / ${s.maxScore ?? 0}<br/>
                得分率：${s.scoreRate ?? 0}%<br/>
                作答人数：${s.answeredCount ?? 0} 人`
      }
    },
    grid: { left: '2%', right: '8%', top: '8%', bottom: '4%', containLabel: true },
    xAxis: {
      type: 'value',
      max: (value: any) => Math.max(value.max, 10),
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f0f0f0' } },
      axisLabel: { formatter: '{value}分' }
    },
    yAxis: {
      type: 'category',
      data: names,
      inverse: true,
      axisLabel: { fontSize: 12, width: 140, overflow: 'truncate' },
      axisLine: { lineStyle: { color: '#e8eaed' } }
    },
    series: [
      {
        name: '满分',
        type: 'bar',
        data: maxScores,
        barWidth: 14,
        barGap: '-100%',
        itemStyle: { color: '#f0f0f0', borderRadius: [0, 4, 4, 0] },
        z: 1,
        label: { show: false }
      },
      {
        name: '平均分',
        type: 'bar',
        data: avgScores,
        barWidth: 14,
        z: 2,
        itemStyle: {
          borderRadius: [0, 4, 4, 0],
          color: (params: any) => {
            const r = rates[params.dataIndex]
            return getStepRateColor(r)
          }
        },
        label: {
          show: true,
          position: 'right',
          formatter: (params: any) => {
            const r = rates[params.dataIndex]
            return `${params.value}分 (${r}%)`
          },
          fontSize: 11,
          color: '#595959'
        }
      }
    ]
  })
}

function handleResize() {
  barChart?.resize()
  stepChart?.resize()
}

onMounted(async () => {
  // 强制刷新实验列表（不走 hydrated 缓存），确保下拉显示真实数据库数据
  await adminStore.refresh()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  barChart?.dispose()
  stepChart?.dispose()
})


</script>

<style scoped>
.app-page-shell {
  padding: var(--space-5);
  min-height: 100%;
}

.breadcrumb-nav { margin-bottom: var(--space-5); }
.breadcrumb-nav :deep(.ant-breadcrumb) { font-size: 14px; }
.breadcrumb-nav :deep(.ant-breadcrumb a) { color: var(--color-primary); }

.page-header-card { padding: var(--space-5); margin-bottom: var(--space-5); }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-5); }
.page-header__eyebrow { margin: 0 0 8px; color: var(--color-primary); font-size: 12px; font-weight: 600; letter-spacing: 0.04em; }
.page-header__title { margin: 0; color: var(--color-text-main); font-size: 28px; font-weight: 700; }
.page-header__description { max-width: 600px; margin: 12px 0 0; color: var(--color-text-secondary); font-size: 14px; }
.page-header__actions { flex-shrink: 0; }

.filter-card { padding: var(--space-4); margin-bottom: var(--space-5); }

.loading-section { display: flex; justify-content: center; align-items: center; min-height: 200px; }
.empty-section { padding: 40px; }

/* 实验信息卡片 */
.info-card { padding: var(--space-5); margin-bottom: var(--space-5); }
.experiment-info { display: flex; justify-content: space-between; align-items: center; }
.exp-info-left { display: flex; flex-direction: column; gap: 8px; }
.exp-badge { display: flex; align-items: center; gap: 8px; }
.exp-id { font-size: 12px; color: var(--color-text-tertiary); }
.exp-name { margin: 0; font-size: 22px; font-weight: 700; color: var(--color-text-main); }

/* 统计卡片 */
.stats-section { padding: var(--space-5); margin-bottom: var(--space-5); }
.section-title { margin: 0 0 16px; font-size: 16px; font-weight: 600; color: var(--color-text-main); }
.section-desc { margin: -8px 0 20px; font-size: 13px; color: var(--color-text-secondary); }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.stat-card { display: flex; align-items: center; gap: 14px; padding: 16px; background: var(--color-bg-muted); border-radius: 10px; }
.stat-icon { display: flex; align-items: center; justify-content: center; width: 44px; height: 44px; border-radius: 10px; flex-shrink: 0; }
.stat-value { margin: 0; font-size: 22px; font-weight: 700; color: var(--color-text-main); }
.stat-unit { font-size: 14px; font-weight: 400; }
.stat-label { margin: 4px 0 0; font-size: 12px; color: var(--color-text-secondary); }

/* 成绩统计 */
.score-section { padding: var(--space-5); margin-bottom: var(--space-5); }
.score-grid { display: grid; grid-template-columns: 2fr 1fr 1fr; gap: 16px; align-items: stretch; }
.score-card { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 24px; border-radius: 10px; text-align: center; }
.score-card--primary { background: linear-gradient(135deg, #e6f4ff 0%, #bae0ff 100%); }
.score-main { display: flex; align-items: baseline; gap: 4px; }
.score-big { font-size: 36px; font-weight: 800; color: #1677ff; }
.score-total { font-size: 14px; color: #8c929a; }
.score-desc { margin: 8px 0 0; font-size: 13px; color: #595959; }
.score-stat-card { display: flex; align-items: center; gap: 10px; padding: 16px; background: var(--color-bg-muted); border-radius: 10px; }
.score-stat-icon { font-size: 18px; flex-shrink: 0; }
.score-stat-value { margin: 0; font-size: 20px; font-weight: 700; color: var(--color-text-main); }
.score-stat-label { margin: 2px 0 0; font-size: 12px; color: var(--color-text-secondary); }

/* 得分分布图 */
.chart-section { padding: var(--space-5); margin-bottom: var(--space-5); }
.chart-wrapper { width: 100%; }
.chart-container { width: 100%; height: 360px; }
.chart-empty { display: flex; justify-content: center; padding: 40px 0; }

/* 步骤得分分析 */
.step-score-section { padding: var(--space-5); margin-bottom: var(--space-5); }
.step-score-header { display: flex; align-items: baseline; gap: 12px; margin-bottom: 20px; }
.step-score-subtitle { font-size: 13px; color: var(--color-text-secondary); }
.step-chart-container { width: 100%; margin-bottom: 20px; }
.step-table :deep(.ant-table-thead > tr > th) { font-weight: 600; color: var(--color-text-main); background: var(--color-bg-muted); }
.step-table :deep(.ant-table-tbody > tr > td) { vertical-align: middle; }
.step-name-cell { display: flex; align-items: center; gap: 8px; }
.step-index { display: inline-flex; align-items: center; justify-content: center; width: 22px; height: 22px; border-radius: 50%; background: var(--color-primary); color: #fff; font-size: 11px; font-weight: 700; flex-shrink: 0; }
.step-name { color: var(--color-text-main); font-size: 13px; }
.rate-cell { display: flex; align-items: center; gap: 8px; }
.rate-text { font-size: 12px; font-weight: 600; white-space: nowrap; min-width: 42px; text-align: right; }

/* 全局概览 */
.overview-section { padding: var(--space-5); margin-bottom: var(--space-5); }
.overview-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.overview-card { display: flex; align-items: center; gap: 14px; padding: 20px; background: var(--color-bg-muted); border-radius: 10px; }
.overview-icon { display: flex; align-items: center; justify-content: center; width: 48px; height: 48px; border-radius: 12px; flex-shrink: 0; }
.overview-value { margin: 0; font-size: 26px; font-weight: 700; color: var(--color-text-main); }
.overview-label { margin: 4px 0 0; font-size: 13px; color: var(--color-text-secondary); }

/* 类型统计表 */
.type-section { padding: var(--space-5); margin-bottom: var(--space-5); }
.type-table :deep(.ant-table-thead > tr > th) { font-weight: 600; color: var(--color-text-main); background: var(--color-bg-muted); }
.type-table :deep(.ant-table-tbody > tr > td) { vertical-align: middle; }
.progress-cell { display: flex; align-items: center; gap: 8px; }
.progress-text { font-size: 12px; color: var(--color-text-secondary); white-space: nowrap; }
.score-excellent { color: #52c41a; font-weight: 600; }
.score-good { color: #1677ff; font-weight: 600; }
.score-ok { color: #fa8c16; font-weight: 600; }
.score-bad { color: #ff4d4f; font-weight: 600; }

@media (max-width: 1100px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .score-grid { grid-template-columns: 1fr 1fr; }
  .overview-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .app-page-shell { padding: var(--space-4); }
  .stats-grid { grid-template-columns: 1fr 1fr; }
  .score-grid { grid-template-columns: 1fr; }
  .overview-grid { grid-template-columns: 1fr; }
  .chart-container { height: 280px; }
}
</style>
