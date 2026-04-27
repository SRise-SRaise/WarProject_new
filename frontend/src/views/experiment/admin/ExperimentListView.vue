<template>
  <div class="app-page-shell">
    <!-- 面包屑导航 -->
    <div class="breadcrumb-nav">
      <a-breadcrumb>
        <a-breadcrumb-item>
          <a href="#" @click.prevent="router.push('/learning')">上机实验</a>
        </a-breadcrumb-item>
        <a-breadcrumb-item>列表</a-breadcrumb-item>
      </a-breadcrumb>
    </div>

    <!-- 页面头部 -->
    <section class="app-surface-card page-header-card">
      <div class="page-header">
        <div class="page-header__main">
          <p class="page-header__eyebrow">上机实验</p>
          <h1 class="page-header__title">实验列表管理</h1>
          <p class="page-header__description">维护实验基本信息、步骤内容和评分标准，支持发布与撤回操作。</p>
        </div>
        <div class="page-header__actions">
          <a-space :size="12">
            <a-button @click="router.push('/admin/experiments/reports')">
              <template #icon><FileTextOutlined /></template>
              实验报告
            </a-button>
            <a-button type="primary" size="large" @click="router.push('/admin/experiments/edit')">
              <template #icon><PlusOutlined /></template>
              添加实验
            </a-button>
          </a-space>
        </div>
      </div>
    </section>

    <!-- 实验数据表格 -->
    <section class="app-surface-card table-card">
      <a-table :columns="columns" :data-source="experiments" :loading="loading" row-key="id" :pagination="paginationConfig" @change="handleTableChange">
        <!-- 实验信息 -->
        <template #bodyCell="{ column, record, index }">
          <!-- 序号：从 1 开始的行号 -->
          <template v-if="column.key === 'index'">
            <span class="row-index">{{ (paginationConfig.current - 1) * paginationConfig.pageSize + index + 1 }}</span>
          </template>

          <!-- 实验次序：创建时的 sortOrder，显示为"实验N" -->
          <template v-else-if="column.key === 'experimentNo'">
            <span class="experiment-order">实验{{ record.sortOrder }}</span>
          </template>

          <template v-else-if="column.key === 'experiment'">
            <div class="experiment-info">
              <div class="experiment-info__header">
                <span class="experiment-info__no">实验{{ record.sortOrder }}</span>
                <div class="experiment-info__tags">
                  <a-tag v-for="tag in record.tags" :key="tag" color="blue">{{ tag }}</a-tag>
                </div>
              </div>
              <h3 class="experiment-info__name">{{ record.name }}</h3>
              <p class="experiment-info__type">{{ record.categoryName }}</p>
            </div>
          </template>

          <!-- 指导书下载 -->
          <template v-else-if="column.key === 'instruction'">
            <a :href="record.instructionUrl" target="_blank" class="download-link" title="下载指导书">
              <DownloadOutlined />
            </a>
          </template>

          <!-- 详情 -->
          <template v-else-if="column.key === 'detail'">
            <a-button type="link" @click="router.push(`/admin/experiments/steps/${record.id}`)">
              <template #icon><EditOutlined /></template>
              编辑步骤
            </a-button>
          </template>

          <!-- 状态开关 -->
          <template v-else-if="column.key === 'status'">
            <a-switch
              :checked="record.publishStatus === 1"
              checked-children="开启"
              un-checked-children="关闭"
              @change="(checked: boolean) => toggleStatus(record, checked)"
            />
          </template>

          <!-- 操作按钮 -->
          <template v-else-if="column.key === 'action'">
            <a-space :size="8">
              <a-button type="link" @click="router.push(`/admin/experiments/edit/${record.id}`)">
                <template #icon><EditOutlined /></template>
                编辑
              </a-button>
              <a-popconfirm title="确定要删除这个实验吗？" ok-text="确认" cancel-text="取消" @confirm="deleteExperiment(record)">
                <a-button type="link" danger>
                  <template #icon><DeleteOutlined /></template>
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { TableProps } from 'ant-design-vue'
import {
  PlusOutlined,
  DownloadOutlined,
  EditOutlined,
  DeleteOutlined,
  FileTextOutlined
} from '@ant-design/icons-vue'
import { listEduExperimentVoByPage, deleteEduExperiment, updateEduExperiment } from '@/api/eduExperimentController'

interface ExperimentRecord {
  id: string
  sortOrder: number
  name: string
  categoryId: number
  categoryName: string
  fileType: string
  requirement: string
  contentDesc: string
  publishStatus: number
  instructionType: string
  instructionUrl: string
  tags: string[]
}

const router = useRouter()
const loading = ref(false)
const experiments = ref<ExperimentRecord[]>([])

const columns = [
  {
    title: '序号',
    key: 'index',
    width: 60,
    align: 'center' as const
  },
  {
    title: '实验次序',
    key: 'experimentNo',
    width: 100
  },
  {
    title: '实验名称 / 实验类型',
    key: 'experiment',
    minWidth: 280
  },
  {
    title: '指导书',
    key: 'instruction',
    width: 80,
    align: 'center' as const
  },
  {
    title: '详情',
    key: 'detail',
    width: 100,
    align: 'center' as const
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    align: 'center' as const
  },
  {
    title: '操作',
    key: 'action',
    width: 160,
    fixed: 'right' as const
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

const handleTableChange: TableProps['onChange'] = async (pag) => {
  paginationConfig.current = pag.current ?? 1
  paginationConfig.pageSize = pag.pageSize ?? 10
  await loadExperiments()
}

async function toggleStatus(record: ExperimentRecord, checked: boolean): Promise<void> {
  try {
    const state = checked ? 1 : 0
    await updateEduExperiment({
      id: Number(record.id),
      publishStatus: state
    } as any)
    record.publishStatus = state
    message.success(`实验"${record.name}"已${checked ? '开启' : '关闭'}`)
  } catch (error) {
    message.error('更新状态失败')
  }
}

async function deleteExperiment(record: ExperimentRecord): Promise<void> {
  try {
    await deleteEduExperiment({ id: record.id } as any)
    experiments.value = experiments.value.filter((item) => item.id !== record.id)
    paginationConfig.total -= 1
    message.success(`实验"${record.name}"已删除`)
  } catch (error) {
    message.error('删除失败')
  }
}

async function loadExperiments(): Promise<void> {
  loading.value = true
  try {
    const res: any = await listEduExperimentVoByPage({
      current: paginationConfig.current,
      pageSize: paginationConfig.pageSize,
      sortField: 'sortOrder',
      sortOrder: 'ascend'
    })

    // 处理后端返回的数据（兼容不同结构）
    const records = res.data?.records || res.data?.data?.records || []
    const total = res.data?.total || res.data?.data?.total || records.length

    experiments.value = records.map((item: any) => ({
      id: String(item.id),
      sortOrder: item.sortOrder,
      name: item.name,
      categoryId: item.categoryId,
      categoryName: item.categoryName || getCategoryName(item.categoryId),
      fileType: item.fileType,
      instructionType: item.fileType || 'PDF',
      instructionUrl: '#',
      publishStatus: item.publishStatus,
      requirement: item.requirement,
      contentDesc: item.contentDesc,
      tags: []
    }))
    paginationConfig.total = total
  } catch (error) {
    console.error('加载实验列表失败:', error)
    message.error('加载实验列表失败')
  } finally {
    loading.value = false
  }
}

function getCategoryName(categoryId: number | undefined): string {
  if (!categoryId) return '未知'
  const categoryMap: Record<number, string> = {
    1: '编程实践',
    2: '设计实现',
    3: '数据库',
    4: '前端开发',
    5: '框架学习',
    6: '综合实验'
  }
  return categoryMap[categoryId] || '未知'
}

onMounted(() => {
  loadExperiments()
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

/* 表格卡片 */
.table-card {
  padding: var(--space-4);
}

/* 实验信息单元格 */
.experiment-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.experiment-info__header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.experiment-info__no {
  padding: 2px 10px;
  background: var(--color-bg-muted);
  border-radius: 4px;
  color: var(--color-text-secondary);
  font-size: 12px;
  font-weight: 600;
}

.experiment-info__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.experiment-info__name {
  margin: 0;
  color: var(--color-text-main);
  font-size: 16px;
  font-weight: 600;
  line-height: 1.4;
}

.experiment-info__type {
  margin: 0;
  color: var(--color-text-tertiary);
  font-size: 13px;
}

/* 行序号 */
.row-index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--color-bg-muted);
  color: var(--color-text-secondary);
  font-size: 13px;
  font-weight: 600;
}

/* 实验次序 */
.experiment-order {
  display: inline-block;
  padding: 3px 10px;
  background: var(--color-primary-light, #e6f7ff);
  border-radius: 4px;
  color: var(--color-primary);
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
}

/* 下载链接 */
.download-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  color: var(--color-primary);
  background: var(--color-bg-muted);
  border-radius: 6px;
  transition: all 0.2s;
}

.download-link:hover {
  color: var(--color-primary-hover);
  background: var(--color-primary);
  color: white;
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

/* 开关样式 */
:deep(.ant-switch) {
  min-width: 64px;
}

:deep(.ant-switch-checked) {
  background: var(--color-success);
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
