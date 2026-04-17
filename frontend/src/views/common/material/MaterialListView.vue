<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader
        eyebrow="资料中心"
        title="学习资料列表"
        description="学生端支持按名称和资料类型检索，并直接下载资料文件。"
      >
        <template #actions>
          <a-button type="primary" :loading="loading" @click="refresh">刷新</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="app-kpi-grid">
      <MetricCard title="资料总数" :value="String(total)" description="满足当前筛选条件的资料数量。" tone="primary" />
      <MetricCard title="当前页数量" :value="String(rows.length)" description="当前分页已加载的资料条数。" tone="accent" />
      <MetricCard title="资料类型数" :value="String(categoryCount)" description="当前页涉及的资料类型数量。" tone="success" />
      <MetricCard title="可下载资料" :value="String(downloadableCount)" description="当前页存在文件地址的资料数量。" tone="warning" />
    </section>

    <section class="app-surface-card app-section-card app-panel-grid">
      <div class="materials-toolbar">
        <a-input
          v-model:value="filters.lectureName"
          allow-clear
          size="large"
          placeholder="按资料名称搜索"
          class="toolbar-input"
        />
        <a-auto-complete
          v-model:value="filters.categoryInput"
          class="toolbar-select"
          size="large"
          allow-clear
          :options="categoryAutoCompleteOptions"
          placeholder="资料类型（讲义/代码/软件/课件/参考资料 或自定义编号）"
          :filter-option="false"
        />
        <a-button size="large" class="toolbar-btn" @click="resetFilters">重置筛选</a-button>
        <a-button type="primary" size="large" class="toolbar-btn" @click="handleSearch">查询资料</a-button>
      </div>

      <a-table
        :columns="columns"
        :data-source="rows"
        :loading="loading"
        row-key="id"
        :pagination="{
          current,
          pageSize,
          total,
          showSizeChanger: true,
          showTotal: (count: number) => `共 ${count} 条`
        }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'lectureName'">
            <span>{{ record.lectureName || '--' }}</span>
          </template>
          <template v-else-if="column.key === 'categoryId'">
            <span>{{ formatCategoryLabel(record.categoryId) }}</span>
          </template>
          <template v-else-if="column.key === 'fileExtension'">
            <span>{{ record.fileExtension ? `.${record.fileExtension}` : '--' }}</span>
          </template>
          <template v-else-if="column.key === 'fileSize'">
            <span>{{ formatFileSize(fileSizeMap[record.id]) }}</span>
          </template>
          <template v-else-if="column.key === 'createdAt'">
            <span>{{ record.createdAt ? CommonUtil.formatDate(record.createdAt) : '--' }}</span>
          </template>
          <template v-else-if="column.key === 'updatedAt'">
            <span>{{ record.updatedAt ? CommonUtil.formatDate(record.updatedAt) : '--' }}</span>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-button type="link" @click="handleDownload(record)">下载</a-button>
          </template>
        </template>
      </a-table>

      <EmptyState v-if="!loading && rows.length === 0" description="暂无资料，请稍后再试。" />
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import EmptyState from '@/components/common/EmptyState.vue'
import MetricCard from '@/components/common/MetricCard.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { listEduLectureVoByPage } from '@/api/eduLectureController'
import { CommonUtil } from '@/utils'

interface LectureRow {
  id: number
  lectureName?: string
  categoryId?: number
  fileExtension?: string
  filePath?: string
  createdAt?: string | Date
  updatedAt?: string | Date
}

interface PaginationConfig {
  current?: number
  pageSize?: number
}

const loading = ref(false)
const current = ref(1)
const pageSize = ref(10)
const total = ref(0)
const rows = ref<LectureRow[]>([])
const fileSizeMap = ref<Record<number, number>>({})

const filters = reactive({
  lectureName: '',
  categoryInput: '' as string
})

const CATEGORY_OPTIONS = [
  { label: '讲义', value: 1 },
  { label: '代码', value: 2 },
  { label: '软件', value: 3 },
  { label: '课件', value: 4 },
  { label: '参考资料', value: 5 }
] as const

const categoryAutoCompleteOptions = CATEGORY_OPTIONS.map((item) => ({ value: item.label }))
const categoryCount = computed(() => new Set(rows.value.map((item) => item.categoryId).filter((item) => item != null)).size)
const downloadableCount = computed(() => rows.value.filter((item) => !!item.filePath).length)

const columns = [
  { title: '资料名称', dataIndex: 'lectureName', key: 'lectureName', width: 280 },
  { title: '资料类型', dataIndex: 'categoryId', key: 'categoryId', width: 130 },
  { title: '文件后缀', dataIndex: 'fileExtension', key: 'fileExtension', width: 130 },
  { title: '文件大小', key: 'fileSize', width: 130 },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 180 },
  { title: '更新时间', dataIndex: 'updatedAt', key: 'updatedAt', width: 180 },
  { title: '操作', key: 'actions', width: 120, fixed: 'right' as const }
]

function parseCategoryId(input: string): number | null {
  const normalized = (input || '').trim()
  if (!normalized) return null
  const matched = CATEGORY_OPTIONS.find((item) => item.label === normalized)
  if (matched) return matched.value
  const asNumber = Number(normalized)
  if (Number.isInteger(asNumber) && asNumber > 0) return asNumber
  return null
}

function formatCategoryLabel(categoryId?: number): string {
  if (categoryId == null) return '--'
  const matched = CATEGORY_OPTIONS.find((item) => item.value === categoryId)
  return matched ? matched.label : String(categoryId)
}

function extractOriginalFileName(pathOrUrl: string): string {
  const rawName = pathOrUrl.split('/').pop()?.trim() || ''
  if (/^[A-Za-z0-9]{8}-/.test(rawName)) {
    return rawName.substring(9)
  }
  return rawName
}

function buildDownloadName(row: LectureRow): string {
  const pathName = row.filePath?.split('/').pop()?.trim()
  if (pathName) {
    return extractOriginalFileName(pathName) || pathName
  }
  const extension = row.fileExtension ? `.${row.fileExtension}` : ''
  return `${row.lectureName || '资料'}${extension}`
}

function formatFileSize(sizeBytes?: number): string {
  if (sizeBytes == null || sizeBytes < 0) {
    return '--'
  }
  if (sizeBytes < 1024) {
    return `${sizeBytes} B`
  }
  if (sizeBytes < 1024 * 1024) {
    return `${(sizeBytes / 1024).toFixed(1)} KB`
  }
  if (sizeBytes < 1024 * 1024 * 1024) {
    return `${(sizeBytes / (1024 * 1024)).toFixed(1)} MB`
  }
  return `${(sizeBytes / (1024 * 1024 * 1024)).toFixed(1)} GB`
}

async function fetchSizeForRow(row: LectureRow): Promise<void> {
  if (!row.id || !row.filePath || fileSizeMap.value[row.id] != null) {
    return
  }
  try {
    const response = await fetch(row.filePath, {
      method: 'HEAD',
      credentials: 'include'
    })
    if (!response.ok) {
      return
    }
    const sizeText = response.headers.get('content-length')
    const size = sizeText ? Number(sizeText) : NaN
    if (!Number.isNaN(size) && size >= 0) {
      fileSizeMap.value = {
        ...fileSizeMap.value,
        [row.id]: size
      }
    }
  } catch {
    // ignore size probe errors
  }
}

function hydrateRowFileSizes(list: LectureRow[]): void {
  for (const row of list) {
    void fetchSizeForRow(row)
  }
}

async function refresh(): Promise<void> {
  loading.value = true
  try {
    const response = await listEduLectureVoByPage({
      current: current.value,
      pageSize: pageSize.value,
      lectureName: filters.lectureName.trim() || undefined,
      categoryId: parseCategoryId(filters.categoryInput) ?? undefined
    })
    if (response.data?.code !== 0 || !response.data?.data) {
      throw new Error(response.data?.message || '查询资料失败')
    }
    const pageData = response.data.data
    rows.value = (pageData.records || []) as LectureRow[]
    total.value = Number(pageData.total || 0)
    hydrateRowFileSizes(rows.value)
  } catch (error) {
    const err = error as Error
    message.error(err.message || '查询资料失败')
    rows.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch(): void {
  current.value = 1
  void refresh()
}

function resetFilters(): void {
  filters.lectureName = ''
  filters.categoryInput = ''
  current.value = 1
  void refresh()
}

function handleTableChange(pagination: PaginationConfig): void {
  current.value = pagination.current ?? 1
  pageSize.value = pagination.pageSize ?? 10
  void refresh()
}

function handleDownload(row: LectureRow): void {
  if (!row.filePath) {
    message.warning('该资料缺少文件地址，无法下载')
    return
  }
  CommonUtil.downloadFile(row.filePath, buildDownloadName(row))
}

onMounted(async () => {
  await refresh()
})
</script>

<style scoped>
.materials-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.toolbar-input {
  flex: 1 1 220px;
  min-width: 180px;
}

.toolbar-select {
  flex: 0 0 220px;
  min-width: 180px;
}

.toolbar-btn {
  flex: 0 0 auto;
  white-space: nowrap;
}

@media (max-width: 960px) {
  .toolbar-select {
    flex: 1 1 220px;
  }
}
</style>
