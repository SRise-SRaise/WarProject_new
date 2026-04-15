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
      <p class="weak-tip">提交记录与作业批改为次级入口，默认不占用主导航层级。</p>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import MetricCard from '@/components/common/MetricCard.vue'
import StatusTag from '@/components/common/StatusTag.vue'

type HomeworkAdminStatus = 'draft' | 'published' | 'reviewing' | 'closed'

interface HomeworkAdminMock {
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

// 作业模块Mock数据占位符，后续需替换到真实后端接口：GET /t_excercise_list.do
const homeworks = ref<HomeworkAdminMock[]>([
  {
    id: 'hw-101',
    title: '需求分析作业一：角色旅程拆解',
    topicLabel: '需求分析专题',
    status: 'published',
    summary: '组织学生输出角色旅程与验收边界分析。',
    publishScope: '软工 2401 / 2402',
    deadline: '2026-04-20 20:00',
    updatedAt: '今天 10:25',
    submissionCount: 31,
    reviewedCount: 18
  },
  {
    id: 'hw-102',
    title: '界面结构复盘报告',
    topicLabel: '界面实现专题',
    status: 'reviewing',
    summary: '要求学生复盘页面结构拆分与状态归属。',
    publishScope: '前端 2401',
    deadline: '2026-04-18 18:00',
    updatedAt: '昨天 19:12',
    submissionCount: 26,
    reviewedCount: 9
  },
  {
    id: 'hw-103',
    title: '课堂展示结构草案',
    topicLabel: '公共资料区',
    status: 'draft',
    summary: '用于下周课堂展示的结构草案，尚未布置。',
    publishScope: '待布置',
    deadline: '2026-04-24 17:30',
    updatedAt: '今天 09:03',
    submissionCount: 0,
    reviewedCount: 0
  }
])

const router = useRouter()

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

function deleteHomework(homeworkId: string): void {
  // 作业模块Mock数据占位符，后续需替换到真实后端接口：POST /t_excercise_delete.do
  homeworks.value = homeworks.value.filter((item) => item.id !== homeworkId)
  message.success('作业已删除（Mock）。')
}
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
