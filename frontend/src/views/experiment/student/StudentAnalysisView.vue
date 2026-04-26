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
        <a-breadcrumb-item>数据分析</a-breadcrumb-item>
      </a-breadcrumb>
    </div>

    <!-- 页面头部 -->
    <section class="app-surface-card page-header-card">
      <div class="page-header">
        <div class="page-header__main">
          <p class="page-header__eyebrow">上机实验</p>
          <h1 class="page-header__title">我的实验数据分析</h1>
          <p class="page-header__description">查看个人实验完成情况与各类型实验的得分分布。</p>
        </div>
        <div class="page-header__actions">
          <a-button @click="router.push('/experiments')">
            <template #icon><ArrowLeftOutlined /></template>
            返回实验列表
          </a-button>
        </div>
      </div>
    </section>

    <!-- 加载状态 -->
    <section v-if="loading" class="loading-section">
      <a-spin size="large">
        <template #tip>正在加载数据...</template>
      </a-spin>
    </section>

    <!-- 错误状态 -->
    <section v-else-if="error" class="error-section">
      <a-result status="error" title="加载失败" :sub-title="error">
        <template #extra>
          <a-space>
            <a-button @click="router.push('/experiments')">返回</a-button>
            <a-button type="primary" @click="loadData">重试</a-button>
          </a-space>
        </template>
      </a-result>
    </section>

    <!-- 数据展示 -->
    <template v-else-if="analysisData">
      <!-- 完成情况统计 -->
      <section class="app-surface-card stats-section">
        <h2 class="section-title">实验完成情况</h2>
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon" style="background: #e6f4ff;">
              <ExperimentOutlined style="font-size: 24px; color: #1677ff;" />
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ analysisData.totalPublishedExperiments }}</p>
              <p class="stat-label">应完成实验</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: #f6ffed;">
              <CheckCircleOutlined style="font-size: 24px; color: #52c41a;" />
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ analysisData.completedExperiments }}</p>
              <p class="stat-label">已完成实验</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: #fff7e6;">
              <ClockCircleOutlined style="font-size: 24px; color: #fa8c16;" />
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ analysisData.gradedExperiments }}</p>
              <p class="stat-label">已批改实验</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: #f9f0ff;">
              <ProjectOutlined style="font-size: 24px; color: #722ed1;" />
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ analysisData.completionRate }}<span class="stat-unit">%</span></p>
              <p class="stat-label">完成率</p>
            </div>
          </div>
        </div>

        <!-- 完成率进度条 -->
        <div class="completion-progress">
          <div class="progress-label">
            <span>整体完成进度</span>
            <span>{{ analysisData.completedExperiments }} / {{ analysisData.totalPublishedExperiments }}</span>
          </div>
          <a-progress
            :percent="analysisData.completionRate"
            :stroke-color="{ '0%': '#1677ff', '100%': '#52c41a' }"
            :show-info="false"
          />
        </div>
      </section>

      <!-- 成绩统计 -->
      <section class="app-surface-card score-section">
        <h2 class="section-title">成绩统计</h2>
        <div class="score-grid">
          <div class="score-card score-card--primary">
            <div class="score-main">
              <span class="score-big">{{ analysisData.totalScore }}</span>
              <span class="score-total"> / {{ analysisData.totalMaxScore }} 分</span>
            </div>
            <p class="score-desc">累计得分</p>
          </div>
          <div class="score-stat-card">
            <div class="score-stat-icon" style="color: #52c41a;">
              <ArrowUpOutlined />
            </div>
            <div class="score-stat-info">
              <p class="score-stat-value">{{ analysisData.maxScore }}</p>
              <p class="score-stat-label">最高分</p>
            </div>
          </div>
          <div class="score-stat-card">
            <div class="score-stat-icon" style="color: #fa8c16;">
              <ArrowDownOutlined />
            </div>
            <div class="score-stat-info">
              <p class="score-stat-value">{{ analysisData.minScore }}</p>
              <p class="score-stat-label">最低分</p>
            </div>
          </div>
          <div class="score-stat-card">
            <div class="score-stat-icon" style="color: #1677ff;">
              <LineChartOutlined />
            </div>
            <div class="score-stat-info">
              <p class="score-stat-value">{{ analysisData.averageScore }}</p>
              <p class="score-stat-label">平均分</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 实验类型维度得分分布 -->
      <section class="app-surface-card chart-section">
        <h2 class="section-title">各类型实验得分分布</h2>
        <p class="section-desc">按实验类型（编程实践、设计实现、数据库、前端开发、框架学习、综合实验）统计得分情况。</p>

        <div class="type-chart-grid">
          <div
            v-for="typeItem in analysisData.experimentTypeScores"
            :key="typeItem.typeCode"
            class="type-chart-card"
            :class="{ 'type-chart-card--active': typeItem.experimentCount > 0 }"
          >
            <div class="type-chart-header">
              <a-tag :color="getTypeColor(typeItem.typeCode)">{{ typeItem.typeName }}</a-tag>
              <span class="type-count">×{{ typeItem.experimentCount }} 个实验</span>
            </div>
            <div class="type-chart-score">
              <span class="type-score-value">{{ typeItem.totalScore }}</span>
              <span class="type-score-label">分</span>
            </div>
            <div class="type-chart-avg">
              平均 {{ typeItem.averageScore }} 分 / 实验
            </div>
            <!-- 得分条形图 -->
            <div class="type-bar-wrapper">
              <a-progress
                :percent="getBarPercent(typeItem)"
                :stroke-color="getBarColor(typeItem.typeCode)"
                :show-info="false"
                :success="{ percent: 0 }"
                size="small"
              />
            </div>
          </div>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowLeftOutlined,
  ExperimentOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  ProjectOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  LineChartOutlined
} from '@ant-design/icons-vue'
import { experimentRepository } from '@/stores/experiment/repository'
import { userContextFactory } from '@/stores/experiment/UserContextFactory'
import type { StudentExperimentAnalysisVO } from '@/stores/experiment/types'

const router = useRouter()

const loading = ref(false)
const error = ref<string | null>(null)
const analysisData = ref<StudentExperimentAnalysisVO | null>(null)

// ==================== 用户信息已迁移至 userContextFactory ====================
// 旧 getCurrentUser() / getLoginUser() 已移除

async function loadData() {
  loading.value = true
  error.value = null

  try {
    const studentId = userContextFactory.getUserIdStr()
    const data = await experimentRepository.getStudentExperimentAnalysis(studentId)

    if (data) {
      analysisData.value = data as StudentExperimentAnalysisVO
    } else {
      error.value = '暂无数据，请先完成实验后再试。'
    }
  } catch (err) {
    console.error('加载数据分析失败:', err)
    error.value = '加载失败，请稍后重试。'
  } finally {
    loading.value = false
  }
}

const TYPE_COLORS: Record<number, string> = {
  1: 'blue',
  2: 'cyan',
  3: 'green',
  4: 'orange',
  5: 'purple',
  6: 'magenta'
}

const BAR_COLORS: Record<number, string> = {
  1: '#1677ff',
  2: '#13c2c2',
  3: '#52c41a',
  4: '#fa8c16',
  5: '#722ed1',
  6: '#eb2f96'
}

function getTypeColor(typeCode: number): string {
  return TYPE_COLORS[typeCode] || 'default'
}

function getBarColor(typeCode: number): string {
  return BAR_COLORS[typeCode] || '#1677ff'
}

function getBarPercent(typeItem: { totalScore: number; experimentCount: number; averageScore: number }): number {
  if (typeItem.experimentCount === 0) return 0
  const maxPossible = typeItem.experimentCount * 100
  return Math.round((typeItem.totalScore / maxPossible) * 100)
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

.loading-section {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  padding: 60px 0;
}

.error-section {
  padding: 40px;
}

.section-title {
  margin: 0 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-main);
}

.section-desc {
  margin: -8px 0 20px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

/* 完成情况统计 */
.stats-section {
  padding: var(--space-5);
  margin-bottom: var(--space-5);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  background: var(--color-bg-muted);
  border-radius: 10px;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  flex-shrink: 0;
}

.stat-value {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-main);
  line-height: 1.2;
}

.stat-unit {
  font-size: 14px;
  font-weight: 400;
}

.stat-label {
  margin: 4px 0 0;
  font-size: 12px;
  color: var(--color-text-secondary);
}

.completion-progress {
  padding: 16px;
  background: var(--color-bg-muted);
  border-radius: 8px;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

/* 成绩统计 */
.score-section {
  padding: var(--space-5);
  margin-bottom: var(--space-5);
}

.score-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 16px;
  align-items: stretch;
}

.score-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px;
  border-radius: 10px;
  text-align: center;
}

.score-card--primary {
  background: linear-gradient(135deg, #e6f4ff 0%, #bae0ff 100%);
}

.score-main {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.score-big {
  font-size: 36px;
  font-weight: 800;
  color: #1677ff;
  line-height: 1;
}

.score-total {
  font-size: 14px;
  color: #8c929a;
}

.score-desc {
  margin: 8px 0 0;
  font-size: 13px;
  color: #595959;
}

.score-stat-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px;
  background: var(--color-bg-muted);
  border-radius: 10px;
}

.score-stat-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.score-stat-value {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text-main);
  line-height: 1.2;
}

.score-stat-label {
  margin: 2px 0 0;
  font-size: 12px;
  color: var(--color-text-secondary);
}

/* 类型维度图表 */
.chart-section {
  padding: var(--space-5);
  margin-bottom: var(--space-5);
}

.type-chart-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.type-chart-card {
  padding: 16px;
  background: var(--color-bg-muted);
  border-radius: 10px;
  border: 1px solid transparent;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.type-chart-card--active {
  background: #fff;
  border-color: var(--color-border);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.type-chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.type-count {
  font-size: 12px;
  color: var(--color-text-tertiary);
}

.type-chart-score {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 4px;
}

.type-score-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-main);
}

.type-score-label {
  font-size: 14px;
  color: #8c929a;
}

.type-chart-avg {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin-bottom: 12px;
}

.type-bar-wrapper {
  margin-top: 8px;
}

/* 响应式 */
@media (max-width: 1100px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .score-grid {
    grid-template-columns: 1fr 1fr;
  }

  .type-chart-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .app-page-shell {
    padding: var(--space-4);
  }

  .page-header {
    flex-direction: column;
    gap: var(--space-4);
  }

  .stats-grid {
    grid-template-columns: 1fr 1fr;
  }

  .score-grid {
    grid-template-columns: 1fr;
  }

  .type-chart-grid {
    grid-template-columns: 1fr;
  }
}
</style>
