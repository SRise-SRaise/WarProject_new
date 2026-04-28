<template>
  <div class="app-page-shell app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="实验模块" title="学生实验列表" description="先看开放安排与当前状态，再进入实验详情、处理页或结果页。">
        <template #actions>
          <a-space :size="12">
            <a-button type="primary" @click="router.push('/experiments/reports')">
              <template #icon><FileTextOutlined /></template>
              我的报告
            </a-button>
            <a-button @click="router.push('/experiments/analysis')">
              <template #icon><BarChartOutlined /></template>
              数据分析
            </a-button>
            <a-button @click="router.push('/learning')">返回学习概览</a-button>
          </a-space>
        </template>
      </SectionHeader>
    </section>

    <!-- 加载状态 -->
    <section v-if="loading" class="loading-section">
      <a-spin size="large">
        <template #tip>正在加载实验列表...</template>
      </a-spin>
    </section>

    <!-- 错误状态 -->
    <section v-else-if="loadError" class="error-section">
      <a-result
        status="error"
        title="加载失败"
        :sub-title="loadError"
      >
        <template #extra>
          <a-space>
            <a-button @click="router.push('/')">返回首页</a-button>
            <a-button type="primary" @click="retryLoad">重试</a-button>
          </a-space>
        </template>
      </a-result>
    </section>

    <!-- 空状态 -->
    <section v-else-if="visibleExperiments.length === 0" class="empty-section">
      <a-result
        status="info"
        title="暂无实验"
        sub-title="当前没有可参加的实验，请耐心等待教师发布。"
      >
        <template #extra>
          <a-button type="primary" @click="router.push('/')">返回首页</a-button>
        </template>
      </a-result>
    </section>

    <!-- 实验列表 -->
    <section v-else class="experiment-grid">
      <article v-for="item in visibleExperiments" :key="item.id" class="app-surface-card experiment-card">
        <div class="experiment-card__head">
          <div>
            <div class="experiment-card__tags">
              <a-tag v-for="tag in (item.tags || [])" :key="tag">{{ tag }}</a-tag>
            </div>
            <h2 class="experiment-card__title">{{ item.title || '未命名实验' }}</h2>
            <p class="app-list-card__meta">{{ item.topicLabel || '待定' }} · {{ item.teacher || '待定' }} · {{ item.schedule || '待定' }}</p>
          </div>
          <StatusTag :type="statusTone(item.status)" :label="statusLabel(item.status)" />
        </div>
        <p class="experiment-card__summary">{{ item.summary || '暂无实验描述' }}</p>
        <a-space :size="10" wrap>
          <a-button v-if="item.status === 'pending' || item.status === 'in_progress'" type="primary" @click="router.push(`/experiments/${item.id}`)">进行实验</a-button>
          <a-button v-else type="primary" @click="router.push(`/experiments/${item.id}/result`)">查看结果</a-button>
        </a-space>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import { FileTextOutlined, BarChartOutlined } from '@ant-design/icons-vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { useExperimentStudentStore } from '@/stores/experiment/student'
import type { ExperimentStudentStatus } from '@/stores/experiment/types'

const router = useRouter()
const experimentStore = useExperimentStudentStore()
const { visibleExperiments, loading } = storeToRefs(experimentStore)

const loadError = ref<string | null>(null)

function statusTone(status: ExperimentStudentStatus): 'warning' | 'processing' | 'success' | 'default' {
  if (status === 'pending') return 'warning'
  if (status === 'in_progress') return 'processing'
  if (status === 'reviewed') return 'success'
  return 'default'
}

function statusLabel(status: ExperimentStudentStatus): string {
  if (status === 'pending') return '待开始'
  if (status === 'in_progress') return '进行中'
  if (status === 'completed') return '已提交'
  return '已批阅'
}

async function retryLoad(): Promise<void> {
  loadError.value = null
  await experimentStore.refresh()
}

onMounted(async () => {
  try {
    await experimentStore.ensureLoaded()
  } catch (error) {
    console.error('加载实验列表失败:', error)
    loadError.value = '加载实验列表时发生错误，请稍后重试'
  }
})
</script>

<style scoped>
.loading-section {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  padding: 60px 0;
}

.error-section,
.empty-section {
  padding: 40px;
}

.experiment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: var(--space-5);
}

.experiment-card {
  padding: 24px;
}

.experiment-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.experiment-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 14px;
}

.experiment-card__title {
  margin: 0;
  font-size: 24px;
}

.experiment-card__summary {
  margin: 18px 0;
  color: var(--color-text-secondary);
  line-height: 1.8;
}
</style>
