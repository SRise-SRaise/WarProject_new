<template>
  <div class="experiment-detail-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <a-spin size="large">
        <template #tip>正在加载实验详情...</template>
      </a-spin>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="loadError" class="error-container">
      <a-result status="error" title="加载失败" :sub-title="loadError">
        <template #extra>
          <a-space>
            <a-button @click="router.push('/experiments')">返回列表</a-button>
            <a-button type="primary" @click="retryLoad">重试</a-button>
          </a-space>
        </template>
      </a-result>
    </div>

    <!-- 实验不存在 -->
    <div v-else-if="!currentExperiment" class="error-container">
      <a-result status="404" title="实验不存在" sub-title="该实验可能已被删除或您没有访问权限。">
        <template #extra>
          <a-button type="primary" @click="router.push('/experiments')">返回列表</a-button>
        </template>
      </a-result>
    </div>

    <!-- 正常内容 -->
    <template v-else>
      <!-- 顶部横幅区域 -->
      <header class="detail-hero">
        <div class="hero-background"></div>
        <div class="hero-content">
          <div class="hero-breadcrumb">
            <a-breadcrumb>
              <a-breadcrumb-item>
                <router-link to="/experiments">实验列表</router-link>
              </a-breadcrumb-item>
              <a-breadcrumb-item>实验详情</a-breadcrumb-item>
            </a-breadcrumb>
          </div>
          
          <div class="hero-main">
            <div class="hero-info">
              <div class="hero-tags">
                <a-tag v-for="tag in (currentExperiment.tags || [])" :key="tag" color="blue">{{ tag }}</a-tag>
                <StatusTag :type="statusTone" :label="statusLabel" />
              </div>
              <h1 class="hero-title">{{ displayTitle }}</h1>
              <p class="hero-description">{{ displaySummary }}</p>
            </div>
            
            <div class="hero-actions">
              <a-button size="large" @click="router.push('/experiments')">
                <LeftOutlined />
                返回列表
              </a-button>
              <a-button
                v-if="canStartExperiment"
                type="primary"
                size="large"
                @click="startExperiment"
              >
                <PlayCircleOutlined />
                {{ primaryActionLabel }}
              </a-button>
              <a-button
                v-else-if="isCompleted"
                size="large"
                @click="viewResult"
              >
                <EyeOutlined />
                查看结果
              </a-button>
            </div>
          </div>
        </div>
      </header>

      <!-- 主体内容 -->
      <main class="detail-content">
        <!-- 实验元信息卡片 -->
        <section class="meta-cards">
          <div class="meta-card">
            <div class="meta-card__icon">
              <BookOutlined />
            </div>
            <div class="meta-card__content">
              <span class="meta-card__label">实验类型</span>
              <span class="meta-card__value">{{ displayTopicLabel }}</span>
            </div>
          </div>
          <div class="meta-card">
            <div class="meta-card__icon">
              <UserOutlined />
            </div>
            <div class="meta-card__content">
              <span class="meta-card__label">指导教师</span>
              <span class="meta-card__value">{{ displayTeacher }}</span>
            </div>
          </div>
          <div class="meta-card">
            <div class="meta-card__icon">
              <CalendarOutlined />
            </div>
            <div class="meta-card__content">
              <span class="meta-card__label">实验安排</span>
              <span class="meta-card__value">{{ displaySchedule }}</span>
            </div>
          </div>
          <div class="meta-card">
            <div class="meta-card__icon">
              <ClockCircleOutlined />
            </div>
            <div class="meta-card__content">
              <span class="meta-card__label">当前状态</span>
              <span class="meta-card__value">{{ statusLabel }}</span>
            </div>
          </div>
        </section>

        <!-- 两栏布局 -->
        <div class="detail-grid">
          <!-- 左侧：实验目标和步骤 -->
          <div class="detail-left">
            <!-- 实验目标 -->
            <section class="detail-section">
              <div class="section-header">
                <AimOutlined class="section-icon" />
                <h2 class="section-title">实验目标</h2>
              </div>
              <div class="section-content objective-content">
                {{ displayObjective }}
              </div>
            </section>

            <!-- 实验步骤 -->
            <section class="detail-section">
              <div class="section-header">
                <OrderedListOutlined class="section-icon" />
                <h2 class="section-title">实验步骤</h2>
                <span class="section-badge">{{ displaySteps.length }} 个步骤</span>
              </div>
              <div class="steps-list">
                <div v-for="(step, index) in displaySteps" :key="step.id" class="step-item">
                  <div class="step-number">{{ index + 1 }}</div>
                  <div class="step-content">
                    <h4 class="step-title">{{ step.title }}</h4>
                    <p class="step-detail">{{ step.detail }}</p>
                    <div v-if="step.deliverable && step.deliverable !== '无'" class="step-deliverable">
                      <FileTextOutlined />
                      <span>交付物：{{ step.deliverable }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </section>
          </div>

          <!-- 右侧：材料和进度 -->
          <div class="detail-right">
            <!-- 实验材料 -->
            <section class="detail-section">
              <div class="section-header">
                <FolderOpenOutlined class="section-icon" />
                <h2 class="section-title">实验材料</h2>
              </div>
              <div v-if="displayMaterials.length > 0" class="materials-list">
                <div v-for="item in displayMaterials" :key="item.name" class="material-item">
                  <div class="material-icon">
                    <FileOutlined />
                  </div>
                  <div class="material-info">
                    <span class="material-name">{{ item.name }}</span>
                    <span class="material-meta">{{ item.kind }} · {{ item.size }}</span>
                  </div>
                  <a
                    v-if="item.url"
                    :href="item.url"
                    target="_blank"
                    class="download-link"
                    title="下载附件"
                  >
                    <DownloadOutlined />
                  </a>
                  <span v-else class="no-download" title="暂无下载链接">
                    <DownloadOutlined style="color: #bfbfbf" />
                  </span>
                </div>
              </div>
              <a-empty v-else description="暂无实验材料" :image="Empty.PRESENTED_IMAGE_SIMPLE" />
            </section>

            <!-- 当前进度 -->
            <section class="detail-section progress-section">
              <div class="section-header">
                <DashboardOutlined class="section-icon" />
                <h2 class="section-title">当前进度</h2>
              </div>
              <div class="progress-content">
                <div class="progress-status">
                  <StatusTag :type="statusTone" :label="statusLabel" size="large" />
                </div>
                <p class="progress-note">{{ displayWorkNote }}</p>
                <div v-if="displayUpdatedAt" class="progress-time">
                  <ClockCircleOutlined />
                  <span>更新于 {{ displayUpdatedAt }}</span>
                </div>
              </div>

              <!-- 分数和反馈 -->
              <template v-if="currentExperiment.work?.score || currentExperiment.work?.teacherFeedback">
                <a-divider />
                <div v-if="currentExperiment.work?.score" class="score-display">
                  <span class="score-label">实验得分</span>
                  <span class="score-value">{{ currentExperiment.work.score }}</span>
                </div>
                <div v-if="currentExperiment.work?.teacherFeedback" class="feedback-display">
                  <h4 class="feedback-label">教师反馈</h4>
                  <p class="feedback-content">{{ currentExperiment.work.teacherFeedback }}</p>
                </div>
              </template>
            </section>

            <!-- 快速操作 -->
            <section class="detail-section action-section">
              <a-button
                v-if="canStartExperiment"
                type="primary"
                block
                size="large"
                @click="startExperiment"
              >
                <PlayCircleOutlined />
                {{ primaryActionLabel }}
              </a-button>
              <a-button
                v-else-if="isCompleted"
                block
                size="large"
                @click="viewResult"
              >
                <EyeOutlined />
                查看实验结果
              </a-button>
            </section>
          </div>
        </div>
      </main>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoute, useRouter } from 'vue-router'
import { Empty } from 'ant-design-vue'
import {
  LeftOutlined,
  PlayCircleOutlined,
  EyeOutlined,
  BookOutlined,
  UserOutlined,
  CalendarOutlined,
  ClockCircleOutlined,
  AimOutlined,
  OrderedListOutlined,
  FileTextOutlined,
  FolderOpenOutlined,
  FileOutlined,
  DownloadOutlined,
  DashboardOutlined
} from '@ant-design/icons-vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { useExperimentStudentStore } from '@/stores/experiment/student'

const route = useRoute()
const router = useRouter()
const experimentStore = useExperimentStudentStore()
const { currentExperiment, loading } = storeToRefs(experimentStore)

const loadError = ref<string | null>(null)

// 显示字段
const displayTitle = computed(() => currentExperiment.value?.title || '未命名实验')
const displaySummary = computed(() => currentExperiment.value?.summary || '暂无实验描述')
const displayTopicLabel = computed(() => currentExperiment.value?.topicLabel || '待定')
const displayTeacher = computed(() => currentExperiment.value?.teacher || '待定')
const displaySchedule = computed(() => currentExperiment.value?.schedule || '待定')
const displayObjective = computed(() => currentExperiment.value?.objective || '暂无实验目标说明，请进入实验后查看具体要求。')

const displayWorkNote = computed(() => {
  const work = currentExperiment.value?.work
  if (!work) return '暂无实验进度，点击开始实验开始答题'
  return work.note || '实验进行中'
})

const displayUpdatedAt = computed(() => {
  const work = currentExperiment.value?.work
  if (!work?.updatedAt) return ''
  return work.updatedAt
})

// 实验步骤
const displaySteps = computed(() => {
  const steps = currentExperiment.value?.steps
  if (!steps || steps.length === 0) {
    return [
      { id: 'step-1', title: '阅读实验说明', detail: '仔细阅读实验目标和要求，了解实验内容', deliverable: '无' },
      { id: 'step-2', title: '完成实验题目', detail: '按顺序完成所有实验题目，可随时保存进度', deliverable: '答案' },
      { id: 'step-3', title: '提交实验报告', detail: '确认答案无误后提交实验报告', deliverable: '实验报告' }
    ]
  }
  return steps
})

// 实验材料
const displayMaterials = computed(() => currentExperiment.value?.materials || [])

// 状态判断
const canStartExperiment = computed(() => {
  const status = currentExperiment.value?.status
  return status === 'pending' || status === 'in_progress'
})

const isCompleted = computed(() => {
  const status = currentExperiment.value?.status
  return status === 'completed' || status === 'reviewed'
})

const primaryActionLabel = computed(() => {
  const status = currentExperiment.value?.status
  if (status === 'pending') return '开始实验'
  if (status === 'in_progress') return '继续实验'
  return '进入实验'
})

const statusTone = computed<'warning' | 'processing' | 'success' | 'default'>(() => {
  const status = currentExperiment.value?.status || currentExperiment.value?.work?.status
  if (status === 'reviewed') return 'success'
  if (status === 'completed' || status === 'submitted') return 'processing'
  if (status === 'in_progress') return 'processing'
  return 'warning'
})

const statusLabel = computed(() => {
  const status = currentExperiment.value?.status || currentExperiment.value?.work?.status
  if (status === 'reviewed') return '已批阅'
  if (status === 'completed' || status === 'submitted') return '已提交'
  if (status === 'in_progress') return '进行中'
  return '待开始'
})

// 操作方法
function startExperiment(): void {
  if (!currentExperiment.value) return
  router.push(`/experiments/${currentExperiment.value.id}/answer`)
}

function viewResult(): void {
  if (!currentExperiment.value) return
  router.push(`/experiments/${currentExperiment.value.id}/result`)
}

async function retryLoad(): Promise<void> {
  loadError.value = null
  await loadExperiment()
}

async function loadExperiment(): Promise<void> {
  loadError.value = null
  try {
    await experimentStore.selectExperiment(String(route.params.id))
  } catch (error) {
    console.error('加载实验失败:', error)
    loadError.value = '加载实验详情时发生错误，请稍后重试'
  }
}

onMounted(async () => {
  await loadExperiment()
})
</script>

<style scoped>
.experiment-detail-page {
  min-height: 100vh;
  background: var(--color-bg-base, #f5f7fa);
}

.loading-container,
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  padding: 60px 20px;
}

/* Hero 区域 */
.detail-hero {
  position: relative;
  padding: 32px 40px 48px;
  overflow: hidden;
}

.hero-background {
  position: absolute;
  inset: 0;
  background: #f8f9fb;
  border-bottom: 1px solid #e8e8e8;
  z-index: 0;
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
}

.hero-breadcrumb {
  margin-bottom: 24px;
}

.hero-breadcrumb :deep(.ant-breadcrumb) {
  color: var(--color-text-secondary, #8c8c8c);
}

.hero-breadcrumb :deep(.ant-breadcrumb a) {
  color: var(--color-text-secondary, #8c8c8c);
}

.hero-breadcrumb :deep(.ant-breadcrumb a:hover) {
  color: var(--color-primary, #1890ff);
}

.hero-breadcrumb :deep(.ant-breadcrumb-separator) {
  color: var(--color-text-tertiary, #bfbfbf);
}

.hero-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 40px;
}

.hero-info {
  flex: 1;
}

.hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.hero-title {
  margin: 0 0 12px;
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-main, #262626);
  line-height: 1.3;
}

.hero-description {
  margin: 0;
  font-size: 15px;
  color: var(--color-text-secondary, #595959);
  line-height: 1.6;
  max-width: 600px;
}

.hero-actions {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.hero-actions .ant-btn {
  height: 42px;
  padding: 0 20px;
  font-size: 14px;
  border-radius: 8px;
}

.hero-actions .ant-btn-default {
  background: #fff;
  border-color: #d9d9d9;
  color: var(--color-text-main, #262626);
}

.hero-actions .ant-btn-default:hover {
  background: #fff;
  border-color: #4f6ef7;
  color: #4f6ef7;
}

.hero-actions .ant-btn-primary {
  background: #4f6ef7;
  border-color: #4f6ef7;
}

.hero-actions .ant-btn-primary:hover {
  background: #3d5bd9;
  border-color: #3d5bd9;
}

/* 主内容区 */
.detail-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 40px 60px;
  position: relative;
  z-index: 2;
}

/* 元信息卡片 */
.meta-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.meta-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}

.meta-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.meta-card__icon {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f5ff;
  border-radius: 10px;
  color: #4f6ef7;
  font-size: 18px;
}

.meta-card__content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-card__label {
  font-size: 13px;
  color: var(--color-text-tertiary, #8c8c8c);
}

.meta-card__value {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-main, #262626);
}

/* 两栏布局 */
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
}

.detail-left,
.detail-right {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 通用 Section 样式 */
.detail-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--color-border, #f0f0f0);
}

.section-icon {
  font-size: 17px;
  color: #4f6ef7;
}

.section-title {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: var(--color-text-main, #262626);
}

.section-badge {
  margin-left: auto;
  padding: 4px 12px;
  background: #f0f5ff;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  color: #4f6ef7;
}

/* 实验目标 */
.objective-content {
  font-size: 15px;
  line-height: 1.8;
  color: var(--color-text-secondary, #595959);
}

/* 实验步骤 */
.steps-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.step-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: var(--color-bg-muted, #fafafa);
  border-radius: 10px;
  transition: background 0.2s;
}

.step-item:hover {
  background: #f0f5ff;
}

.step-number {
  flex-shrink: 0;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #4f6ef7;
  color: #fff;
  border-radius: 50%;
  font-size: 13px;
  font-weight: 600;
}

.step-content {
  flex: 1;
}

.step-title {
  margin: 0 0 6px;
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-main, #262626);
}

.step-detail {
  margin: 0 0 8px;
  font-size: 14px;
  color: var(--color-text-secondary, #595959);
  line-height: 1.6;
}

.step-deliverable {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: #fff;
  border-radius: 6px;
  font-size: 12px;
  color: var(--color-text-tertiary, #8c8c8c);
}

/* 实验材料 */
.materials-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.material-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: var(--color-bg-muted, #fafafa);
  border-radius: 8px;
  transition: background 0.2s;
}

.material-item:hover {
  background: #f0f5ff;
}

.material-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 8px;
  color: #4f6ef7;
  font-size: 16px;
}

.material-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.material-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-main, #262626);
}

.material-meta {
  font-size: 12px;
  color: var(--color-text-tertiary, #8c8c8c);
}

.download-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  color: var(--color-primary, #4f6ef7);
  background: var(--color-bg-muted, #fafafa);
  border-radius: 6px;
  transition: all 0.2s;
}

.download-link:hover {
  background: var(--color-primary, #4f6ef7);
  color: #fff;
}

.no-download {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
}

/* 当前进度 */
.progress-section {
  background: #fff;
  border: 1px solid #e8e8e8;
}

.progress-content {
  text-align: center;
  padding: 8px 0;
}

.progress-status {
  margin-bottom: 12px;
  display: flex;
  justify-content: center;
}

.progress-note {
  margin: 0 0 10px;
  font-size: 14px;
  color: var(--color-text-secondary, #595959);
  line-height: 1.5;
}

.progress-time {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--color-text-tertiary, #8c8c8c);
  padding: 4px 10px;
  background: #f5f5f5;
  border-radius: 4px;
}

.score-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: rgba(82, 196, 26, 0.1);
  border-radius: 8px;
  margin-bottom: 12px;
}

.score-label {
  font-size: 14px;
  color: var(--color-text-secondary, #595959);
}

.score-value {
  font-size: 28px;
  font-weight: 700;
  color: #52c41a;
}

.feedback-display {
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
  border-left: 3px solid #4f6ef7;
}

.feedback-label {
  margin: 0 0 8px;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-primary, #1890ff);
}

.feedback-content {
  margin: 0;
  font-size: 14px;
  color: var(--color-text-secondary, #595959);
  line-height: 1.6;
}

/* 快速操作 */
.action-section {
  padding: 20px 24px;
  background: #fff;
}

.action-section .ant-btn {
  height: 46px;
  font-size: 15px;
  border-radius: 8px;
}

.action-section .ant-btn-primary {
  background: #4f6ef7;
  border-color: #4f6ef7;
}

.action-section .ant-btn-primary:hover {
  background: #3d5bd9;
  border-color: #3d5bd9;
}

/* 响应式 */
@media (max-width: 1024px) {
  .detail-hero {
    padding: 24px 20px 40px;
  }
  
  .detail-content {
    padding: 0 20px 40px;
  }
  
  .meta-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .hero-main {
    flex-direction: column;
    gap: 24px;
  }
  
  .hero-actions {
    width: 100%;
  }
  
  .hero-actions .ant-btn {
    flex: 1;
  }
}

@media (max-width: 640px) {
  .hero-title {
    font-size: 24px;
  }
  
  .meta-cards {
    grid-template-columns: 1fr;
  }
  
  .hero-actions {
    flex-direction: column;
  }
}
</style>
