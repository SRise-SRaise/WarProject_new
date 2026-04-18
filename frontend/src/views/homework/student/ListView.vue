<template>
  <div class="app-page-shell app-panel-grid">
    <section class="page-header-row">
      <div>
        <h1 class="page-title">学生作业列表</h1>
      </div>
      <a-button @click="router.push('/learning')">返回学习概览</a-button>
    </section>

    <section class="app-kpi-grid">
      <MetricCard title="作业总数" :value="String(homeworks.length)" description="当前学期所有已发布作业。" tone="primary" />
      <MetricCard title="待完成" :value="String(pendingCount)" description="建议优先处理临近截止任务。" tone="warning" />
      <MetricCard title="已提交" :value="String(submittedCount)" description="等待教师批阅的作业数量。" tone="success" />
      <MetricCard title="已批阅" :value="String(reviewedCount)" description="已可查看得分和反馈。" tone="accent" />
    </section>

    <section class="app-surface-card app-section-card app-panel-grid">
      <div class="homework-toolbar">
        <a-input v-model:value="keyword" allow-clear size="large" placeholder="搜索作业标题或主题" />
        <a-select v-model:value="statusFilter" size="large" :options="statusOptions" />
        <a-button size="large" @click="resetFilters">重置筛选</a-button>
      </div>

      <a-spin :spinning="loading">
        <a-table :columns="columns" :data-source="filteredHomeworks" row-key="id" :pagination="false">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <StatusTag :type="statusTone(record.status)" :label="statusLabel(record.status)" />
            </template>
            <template v-else-if="column.key === 'title'">
              <div class="row-title-cell">
                <h3 class="row-title">{{ record.title }}</h3>
                <p class="row-subtitle">{{ record.summary }}</p>
              </div>
            </template>
            <template v-else-if="column.key === 'action'">
              <a-space :size="8" wrap>
                <a-button type="primary" size="small" @click="router.push(`/homework/${record.id}/do`)">做作业</a-button>
                <a-button size="small" @click="router.push(`/homework/${record.id}/score`)">查看成绩</a-button>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-spin>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useHomeworkStudentStore } from '@/stores/homework/student'
import MetricCard from '@/components/common/MetricCard.vue'
import StatusTag from '@/components/common/StatusTag.vue'

type HomeworkStatus = 'pending' | 'submitted' | 'reviewed' | 'overdue'

interface HomeworkListItem {
  id: string
  title: string
  topicLabel: string
  teacher: string
  deadline: string
  openTime: string
  summary: string
  status: HomeworkStatus
  tags: string[]
}

const router = useRouter()
const route = useRoute()
const homeworkStore = useHomeworkStudentStore()

const keyword = ref('')
const statusFilter = ref<'all' | HomeworkStatus>('all')

const statusOptions = [
  { label: '全部状态', value: 'all' },
  { label: '待完成', value: 'pending' },
  { label: '已提交', value: 'submitted' },
  { label: '已批阅', value: 'reviewed' },
  { label: '已逾期', value: 'overdue' }
]

// 从Store获取数据
const homeworks = computed<HomeworkListItem[]>(() => homeworkStore.homeworks)
const loading = computed(() => homeworkStore.loading)

const filteredHomeworks = computed(() => {
  const lowerKeyword = keyword.value.trim().toLowerCase()
  return homeworks.value.filter((item) => {
    const byKeyword = lowerKeyword.length === 0
      || item.title.toLowerCase().includes(lowerKeyword)
      || item.topicLabel.toLowerCase().includes(lowerKeyword)
    const byStatus = statusFilter.value === 'all' || item.status === statusFilter.value
    return byKeyword && byStatus
  })
})

const pendingCount = computed(() => homeworks.value.filter((item) => item.status === 'pending').length)
const submittedCount = computed(() => homeworks.value.filter((item) => item.status === 'submitted').length)
const reviewedCount = computed(() => homeworks.value.filter((item) => item.status === 'reviewed').length)

const columns = [
  { title: '作业', dataIndex: 'title', key: 'title' },
  { title: '主题', dataIndex: 'topicLabel', key: 'topicLabel', width: 150 },
  { title: '教师', dataIndex: 'teacher', key: 'teacher', width: 110 },
  { title: '截止时间', dataIndex: 'deadline', key: 'deadline', width: 170 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 120 },
  { title: '操作', key: 'action', width: 180 }
]

function resetFilters(): void {
  keyword.value = ''
  statusFilter.value = 'all'
  router.replace({ path: '/homework' })
}

function statusTone(status: HomeworkStatus): 'warning' | 'processing' | 'success' | 'default' {
  if (status === 'pending' || status === 'overdue') return 'warning'
  if (status === 'submitted') return 'processing'
  if (status === 'reviewed') return 'success'
  return 'default'
}

function statusLabel(status: HomeworkStatus): string {
  if (status === 'pending') return '待完成'
  if (status === 'submitted') return '已提交'
  if (status === 'reviewed') return '已批阅'
  return '已逾期'
}

// 初始化时加载数据
onMounted(async () => {
  await homeworkStore.refresh()
})

watch(
  () => route.query.status,
  (value) => {
    if (value === 'pending' || value === 'submitted' || value === 'reviewed' || value === 'overdue') {
      statusFilter.value = value
      return
    }
    statusFilter.value = 'all'
  },
  { immediate: true }
)
</script>

<style scoped>
.page-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.page-title {
  margin: 0;
  font-size: 28px;
  line-height: 1.2;
  font-weight: 700;
  color: var(--color-text-main);
}

.homework-toolbar {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) 220px auto;
  gap: 12px;
}

.row-title-cell {
  min-width: 0;
}

.row-title {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  color: var(--color-text-main);
}

.row-subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--color-text-secondary);
  line-height: 1.5;
}

@media (max-width: 960px) {
  .page-header-row {
    align-items: flex-start;
    flex-direction: column;
  }

  .homework-toolbar {
    grid-template-columns: 1fr;
  }
}
</style>
