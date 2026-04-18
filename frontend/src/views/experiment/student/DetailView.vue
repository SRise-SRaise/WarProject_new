<template>
  <div class="app-page-shell app-panel-grid">
    <!-- 加载状态 -->
    <section v-if="loading" class="loading-section">
      <a-spin size="large">
        <template #tip>正在加载实验详情...</template>
      </a-spin>
    </section>

    <!-- 错误状态 -->
    <section v-else-if="loadError" class="error-section app-surface-card">
      <a-result
        status="error"
        title="加载失败"
        :sub-title="loadError"
      >
        <template #extra>
          <a-space>
            <a-button @click="router.push('/experiments')">返回实验列表</a-button>
            <a-button type="primary" @click="retryLoad">重试</a-button>
          </a-space>
        </template>
      </a-result>
    </section>

    <!-- 实验不存在 -->
    <section v-else-if="!currentExperiment" class="error-section app-surface-card">
      <a-result
        status="404"
        title="实验不存在"
        sub-title="该实验可能已被删除或您没有访问权限。"
      >
        <template #extra>
          <a-button type="primary" @click="router.push('/experiments')">返回实验列表</a-button>
        </template>
      </a-result>
    </section>

    <!-- 正常内容 -->
    <template v-else>
      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="实验详情" :title="displayTitle" :description="displaySummary">
          <template #actions>
            <a-space :size="12" wrap>
              <a-button @click="router.push('/experiments')">返回实验列表</a-button>
              <a-button
                v-if="canStartExperiment"
                type="primary"
                @click="openPrimaryAction"
              >
                {{ primaryActionLabel }}
              </a-button>
              <a-button
                v-else-if="currentExperiment.status === 'completed' || currentExperiment.status === 'reviewed'"
                @click="openPrimaryAction"
              >
                查看结果
              </a-button>
            </a-space>
          </template>
        </SectionHeader>
        <ExperimentMetaGrid
          :topicLabel="displayTopicLabel"
          :teacher="displayTeacher"
          :schedule="displaySchedule"
          :objective="displayObjective"
        />
      </section>

      <section class="app-split-grid">
        <div class="app-panel-grid">
          <section class="app-surface-card app-section-card app-panel-grid">
            <SectionHeader
              eyebrow="实验步骤"
              title="处理链路"
              :description="stepsDescription"
              tight
            />
            <ExperimentStepList :steps="displaySteps" />
          </section>
        </div>

        <div class="app-panel-grid">
          <section class="app-surface-card app-section-card app-panel-grid">
            <SectionHeader
              eyebrow="实验材料"
              title="参考附件"
              description="先确认材料和样例，再进入实验处理页。"
              tight
            />
            <div v-if="displayMaterials.length > 0" class="app-list">
              <article v-for="item in displayMaterials" :key="item.name" class="app-list-card">
                <h3 class="app-list-card__title">{{ item.name }}</h3>
                <p class="app-list-card__meta">{{ item.kind }} · {{ item.size }}</p>
              </article>
            </div>
            <a-empty v-else description="暂无实验材料" />
          </section>

          <section class="app-surface-card app-section-card app-panel-grid">
            <SectionHeader
              eyebrow="当前进度"
              title="实验状态"
              description="进度和结果说明会随实验处理和教师反馈同步更新。"
              tight
            />
            <article class="app-list-card">
              <p class="app-list-card__meta">{{ displayWorkNote }}</p>
              <div class="detail-footer">
                <span class="app-inline-stat">{{ displayUpdatedAt }}</span>
                <StatusTag :type="statusTone" :label="statusLabel" />
              </div>
            </article>

            <!-- 分数和教师反馈（如果有） -->
            <template v-if="currentExperiment.work.score || currentExperiment.work.teacherFeedback">
              <a-divider />
              <div v-if="currentExperiment.work.score" class="score-section">
                <span class="score-label">得分：</span>
                <span class="score-value">{{ currentExperiment.work.score }}</span>
              </div>
              <div v-if="currentExperiment.work.teacherFeedback" class="feedback-section">
                <h4 class="feedback-title">教师反馈</h4>
                <p class="feedback-content">{{ currentExperiment.work.teacherFeedback }}</p>
              </div>
            </template>
          </section>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import ExperimentMetaGrid from '@/components/experiment/ExperimentMetaGrid.vue'
import ExperimentStepList from '@/components/experiment/ExperimentStepList.vue'
import { useExperimentStudentStore } from '@/stores/experiment/student'

const route = useRoute()
const router = useRouter()
const experimentStore = useExperimentStudentStore()
const { currentExperiment, loading } = storeToRefs(experimentStore)

const loadError = ref<string | null>(null)

// 显示相关字段，带默认值
const displayTitle = computed(() => currentExperiment.value?.title || '未命名实验')
const displaySummary = computed(() => currentExperiment.value?.summary || '暂无实验描述')
const displayTopicLabel = computed(() => currentExperiment.value?.topicLabel || '待定')
const displayTeacher = computed(() => currentExperiment.value?.teacher || '待定')
const displaySchedule = computed(() => currentExperiment.value?.schedule || '待定')
const displayObjective = computed(() => currentExperiment.value?.objective || '暂无实验目标')

const displayWorkNote = computed(() => {
  const work = currentExperiment.value?.work
  if (!work) return '暂无实验进度'
  return work.note || '暂无进度说明'
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
      { id: 'step-default-1', title: '实验准备', detail: '阅读实验要求和材料', deliverable: '无' },
      { id: 'step-default-2', title: '开始实验', detail: '进入答题页面完成实验题目', deliverable: '无' }
    ]
  }
  return steps
})

const stepsDescription = computed(() => {
  const steps = currentExperiment.value?.steps
  if (!steps || steps.length === 0) {
    return '暂无详细步骤，请进入实验页面查看'
  }
  return '从准备、执行到结果整理，按步骤逐步推进'
})

// 实验材料
const displayMaterials = computed(() => {
  const materials = currentExperiment.value?.materials
  if (!materials || materials.length === 0) {
    return []
  }
  return materials
})

// 是否可以开始实验
const canStartExperiment = computed(() => {
  const status = currentExperiment.value?.status
  return status === 'pending' || status === 'in_progress'
})

const primaryActionLabel = computed(() => {
  const status = currentExperiment.value?.status
  if (status === 'pending') return '开始实验'
  if (status === 'in_progress') return '继续实验'
  return '进入实验'
})

const statusTone = computed<'warning' | 'processing' | 'success'>(() => {
  const status = currentExperiment.value?.work?.status
  if (status === 'reviewed') return 'success'
  if (status === 'submitted') return 'processing'
  return 'warning'
})

const statusLabel = computed(() => {
  const status = currentExperiment.value?.work?.status
  if (status === 'reviewed') return '已处理'
  if (status === 'submitted') return '已提交'
  if (status === 'in_progress') return '进行中'
  return '待开始'
})

function openPrimaryAction(): void {
  if (!currentExperiment.value) return
  if (currentExperiment.value.status === 'completed' || currentExperiment.value.status === 'reviewed') {
    router.push(`/experiments/${currentExperiment.value.id}/result`)
    return
  }
  router.push(`/experiments/${currentExperiment.value.id}/answer`)
}

async function retryLoad(): Promise<void> {
  loadError.value = null
  await loadExperiment()
}

async function loadExperiment(): Promise<void> {
  loadError.value = null
  try {
    const result = await experimentStore.selectExperiment(String(route.params.id))
    if (!result) {
      // 实验不存在，但不要报错，因为 mock 数据可能没有
      console.log('实验详情可能为空，使用默认展示')
    }
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
.loading-section {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  padding: 60px 0;
}

.error-section {
  padding: 40px;
}

.detail-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 16px;
}

.score-section {
  padding: 12px 16px;
  background: #f6ffed;
  border-radius: 8px;
  border: 1px solid #b7eb8f;
  margin-bottom: 12px;
}

.score-label {
  color: #52c41a;
  font-weight: 500;
}

.score-value {
  color: #389e0d;
  font-weight: 600;
  font-size: 16px;
}

.feedback-section {
  padding: 12px 16px;
  background: #f0f7ff;
  border-radius: 8px;
  border: 1px solid #91caff;
}

.feedback-title {
  margin: 0 0 8px;
  font-size: 14px;
  font-weight: 500;
  color: #1890ff;
}

.feedback-content {
  margin: 0;
  font-size: 14px;
  color: #595959;
  line-height: 1.6;
}
</style>
