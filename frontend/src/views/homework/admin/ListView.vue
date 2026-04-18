<template>
  <div class="app-panel-grid">
    <section class="page-header-row">
      <h1 class="page-title">教师作业台账</h1>
      <a-space :size="10" wrap>
        <a-button @click="router.push('/admin/questions')">题目管理</a-button>
        <a-button type="primary" @click="router.push('/admin/homework/edit')">新增作业</a-button>
      </a-space>
    </section>

    <section class="app-kpi-grid">
      <MetricCard title="作业总数" :value="String(homeworks.length)" description="当前学期全部作业条目。" tone="primary" />
      <MetricCard title="已发布" :value="String(publishedCount)" description="学生可见且可提交。" tone="success" />
      <MetricCard title="批阅中" :value="String(reviewingCount)" description="存在未完成批阅记录。" tone="warning" />
      <MetricCard title="草稿" :value="String(draftCount)" description="尚未布置到班级。" tone="accent" />
    </section>

    <section class="app-surface-card app-section-card app-panel-grid">
      <a-spin :spinning="loading">
        <a-table :columns="columns" :data-source="homeworks" row-key="id" :pagination="false">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'title'">
              <div class="row-title-cell">
                <h3 class="row-title">{{ record.title }}</h3>
                <p class="row-subtitle">{{ record.summary }}</p>
              </div>
            </template>

            <template v-else-if="column.key === 'status'">
              <StatusTag :type="statusTone(record.status)" :label="statusLabel(record.status)" />
            </template>

            <template v-else-if="column.key === 'progress'">
              <span class="app-inline-stat">{{ record.reviewedCount }} / {{ record.submissionCount }}</span>
            </template>

            <template v-else-if="column.key === 'action'">
              <a-space :size="8" wrap>
                <a-button size="small" @click="router.push(`/admin/homework/edit/${record.id}`)">编辑</a-button>
                <a-button type="primary" size="small" @click="router.push(`/admin/homework/assign/${record.id}`)">布置</a-button>
                <a-popconfirm title="确认删除该作业？" ok-text="删除" cancel-text="取消" @confirm="deleteHomework(record.id)">
                  <a-button size="small" danger>删除</a-button>
                </a-popconfirm>
                <a-button size="small" @click="router.push(`/admin/homework/submissions/${record.id}`)">提交记录</a-button>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-spin>
      <p class="weak-tip">提交记录与作业批改为次级入口，默认不占用主导航层级。</p>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import MetricCard from '@/components/common/MetricCard.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { listEduExerciseVoByPage, deleteEduExercise } from '@/api/eduExerciseController'

type HomeworkAdminStatus = 'draft' | 'published' | 'reviewing' | 'closed'

interface HomeworkAdminItem {
  id: string
  title: string
  topicLabel: string
  status: HomeworkAdminStatus
  summary: string
  publishScope: string
  deadline: string
  updatedAt: string
  submissionCount: number
  reviewedCount: number
}

const router = useRouter()
const loading = ref(false)
const homeworks = ref<HomeworkAdminItem[]>([])
const EMPTY_TEXT = '--'

const publishedCount = computed(() => homeworks.value.filter((item) => item.status === 'published').length)
const reviewingCount = computed(() => homeworks.value.filter((item) => item.status === 'reviewing').length)
const draftCount = computed(() => homeworks.value.filter((item) => item.status === 'draft').length)

const columns = [
  { title: '作业', dataIndex: 'title', key: 'title' },
  { title: '主题', dataIndex: 'topicLabel', key: 'topicLabel', width: 130 },
  { title: '发布范围', dataIndex: 'publishScope', key: 'publishScope', width: 180 },
  { title: '截止时间', dataIndex: 'deadline', key: 'deadline', width: 170 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 110 },
  { title: '批阅进度', key: 'progress', width: 120 },
  { title: '操作', key: 'action', width: 300 }
]

function statusTone(status: HomeworkAdminStatus): 'success' | 'processing' | 'warning' | 'default' {
  if (status === 'published') return 'success'
  if (status === 'reviewing') return 'processing'
  if (status === 'closed') return 'default'
  return 'warning'
}

function statusLabel(status: HomeworkAdminStatus): string {
  if (status === 'published') return '已发布'
  if (status === 'reviewing') return '批阅中'
  if (status === 'closed') return '已结束'
  return '草稿'
}

function toDisplayText(value: unknown, fallback = EMPTY_TEXT): string {
  if (value === null || value === undefined) return fallback
  const text = String(value).trim()
  return text.length > 0 ? text : fallback
}

function formatDeadline(value: unknown): string {
  if (value === null || value === undefined || String(value).trim().length === 0) return EMPTY_TEXT
  const text = String(value)
  const parsed = dayjs(text)
  return parsed.isValid() ? parsed.format('YYYY-MM-DD HH:mm') : toDisplayText(text)
}

function formatPublishScope(item: any): string {
  const rawScope = item.publishScope ?? item.classNames ?? item.className ?? item.classCodes ?? item.classCode
  if (Array.isArray(rawScope)) {
    return rawScope.length > 0 ? rawScope.join(' / ') : EMPTY_TEXT
  }
  return toDisplayText(rawScope)
}

function formatTopicLabel(item: any): string {
  return toDisplayText(item.topicLabel ?? item.topicName ?? item.relateExpName)
}

function mapStatus(rawStatus: unknown): HomeworkAdminStatus {
  const status = Number(rawStatus)
  if (status === 1) return 'published'
  if (status === 2) return 'closed'
  return 'draft'
}

function toCount(value: unknown): number {
  const count = Number(value)
  return Number.isFinite(count) && count > 0 ? count : 0
}

async function loadHomeworks() {
  loading.value = true
  try {
    const response = await listEduExerciseVoByPage({ current: 1, pageSize: 50 })

    const records = (response as any)?.data?.data?.records ?? (response as any)?.data?.records ?? []
    if (Array.isArray(records)) {
      homeworks.value = records.map((item: any) => ({
        id: String(item.id),
        title: toDisplayText(item.taskName),
        topicLabel: formatTopicLabel(item),
        status: mapStatus(item.publishStatus),
        summary: toDisplayText(item.description),
        publishScope: formatPublishScope(item),
        deadline: formatDeadline(item.endTime ?? item.deadline),
        updatedAt: toDisplayText(item.updatedAt),
        submissionCount: toCount(item.submissionCount ?? item.submitCount),
        reviewedCount: toCount(item.reviewedCount ?? item.reviewCount)
      }))
    } else {
      homeworks.value = []
    }
  } catch (error) {
    console.error('加载作业列表失败:', error)
    message.error('加载作业列表失败')
  } finally {
    loading.value = false
  }
}

async function deleteHomework(homeworkId: string) {
  try {
    await deleteEduExercise({ id: homeworkId })
    message.success('作业已删除')
    await loadHomeworks()
  } catch (error) {
    console.error('删除失败:', error)
    message.error('删除失败')
  }
}

onMounted(() => {
  loadHomeworks()
})
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

.weak-tip {
  margin: 0;
  font-size: 12px;
  color: var(--color-text-tertiary);
}

@media (max-width: 960px) {
  .page-header-row {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
