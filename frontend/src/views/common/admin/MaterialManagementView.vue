<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader
        eyebrow="资料管理"
        title="共享资料管理"
        description="集中维护资料名称、类型与附件文件，支持上传、下载、编辑和删除。"
      >
        <template #actions>
          <a-space :size="12" wrap>
            <a-button type="primary" :loading="loading" @click="refresh">刷新</a-button>
            <a-button size="large" @click="openUploadModal">上传资料</a-button>
          </a-space>
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
          placeholder="资料类型（讲义/代码/软件/课件/参考资料 或自定义输入）"
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
            <div class="material-name-cell">
              <span class="material-name">{{ record.lectureName || '--' }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'categoryId'">
            <span>{{ formatCategoryLabel(record.categoryId) }}</span>
          </template>
          <template v-else-if="column.key === 'fileExtension'">
            <span>{{ record.fileExtension ? `.${record.fileExtension}` : '--' }}</span>
          </template>
          <template v-else-if="column.key === 'createdAt'">
            <span>{{ record.createdAt ? CommonUtil.formatDate(record.createdAt) : '--' }}</span>
          </template>
          <template v-else-if="column.key === 'updatedAt'">
            <span>{{ record.updatedAt ? CommonUtil.formatDate(record.updatedAt) : '--' }}</span>
          </template>
          <template v-else-if="column.key === 'actions'">
            <div class="row-actions">
              <a-tooltip title="下载资料">
                <a-button type="text" size="small" @click="handleDownload(record)">
                  <template #icon>
                    <DownloadOutlined />
                  </template>
                </a-button>
              </a-tooltip>
              <a-tooltip title="编辑资料">
                <a-button type="text" size="small" @click="openEditModal(record)">
                  <template #icon>
                    <EditOutlined />
                  </template>
                </a-button>
              </a-tooltip>
              <a-tooltip title="删除资料">
                <a-button danger type="text" size="small" @click="handleDelete(record)">
                  <template #icon>
                    <DeleteOutlined />
                  </template>
                </a-button>
              </a-tooltip>
            </div>
          </template>
        </template>
      </a-table>

      <EmptyState v-if="!loading && rows.length === 0" description="暂无资料，请先上传。" />
    </section>

    <a-modal
      v-model:open="uploadModalVisible"
      title="上传资料"
      ok-text="上传"
      cancel-text="取消"
      :confirm-loading="uploadSubmitting"
      @ok="handleUpload"
      @cancel="closeUploadModal"
    >
      <a-form layout="vertical" :model="uploadForm">
        <a-form-item label="资料名称" required>
          <a-input v-model:value="uploadForm.lectureName" placeholder="请输入资料名称" size="large" />
        </a-form-item>
        <a-form-item label="资料类型" required>
          <a-auto-complete
            v-model:value="uploadForm.categoryInput"
            size="large"
            style="width: 100%"
            allow-clear
            :options="categoryAutoCompleteOptions"
            placeholder="请选择或输入资料类型"
            :filter-option="false"
          />
          <p class="hint-text">可选：讲义、代码、软件、课件、参考资料；也可直接输入自定义类型编号（数字）。</p>
        </a-form-item>
        <a-form-item label="选择文件" required>
          <div class="file-row">
            <a-button size="large" @click="fileInputRef?.click()">选择文件</a-button>
            <span class="file-name">{{ selectedFile?.name ?? '未选择文件' }}</span>
          </div>
          <input
            ref="fileInputRef"
            type="file"
            style="display: none"
            @change="handleFileSelected"
          />
          <p class="hint-text">支持上传任意资料文件，资料类型请与后端分类编号保持一致。</p>
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="editModalVisible"
      title="编辑资料"
      ok-text="保存"
      cancel-text="取消"
      :confirm-loading="editSubmitting"
      @ok="submitEdit"
      @cancel="closeEditModal"
    >
      <a-form layout="vertical" :model="editForm">
        <a-form-item label="资料名称" required>
          <a-input v-model:value="editForm.lectureName" placeholder="请输入资料名称" size="large" />
        </a-form-item>
        <a-form-item label="资料类型" required>
          <a-auto-complete
            v-model:value="editForm.categoryInput"
            size="large"
            style="width: 100%"
            allow-clear
            :options="categoryAutoCompleteOptions"
            placeholder="请选择或输入资料类型"
            :filter-option="false"
          />
          <p class="hint-text">可选：讲义、代码、软件、课件、参考资料；也可直接输入自定义类型编号（数字）。</p>
        </a-form-item>
        <a-form-item label="重新上传文件">
          <div class="file-row">
            <a-button size="large" @click="editFileInputRef?.click()">选择新文件</a-button>
            <span class="file-name">{{ editSelectedFileName }}</span>
          </div>
          <input
            ref="editFileInputRef"
            type="file"
            style="display: none"
            @change="handleEditFileSelected"
          />
          <p class="hint-text">不选择新文件时，仅更新资料名称与资料类型。</p>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { DeleteOutlined, DownloadOutlined, EditOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import MetricCard from '@/components/common/MetricCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { addEduLecture, deleteEduLecture, listEduLectureVoByPage, updateEduLecture } from '@/api/eduLectureController'
import { uploadFile } from '@/api/fileController'
import { useAuthStore } from '@/stores/user/auth'
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

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const current = ref(1)
const pageSize = ref(10)
const total = ref(0)
const rows = ref<LectureRow[]>([])

const filters = reactive({
  lectureName: '',
  categoryInput: '' as string
})

const uploadForm = reactive({
  lectureName: '',
  categoryInput: '' as string
})

const editForm = reactive({
  id: null as number | null,
  lectureName: '',
  categoryInput: '' as string,
  fileExtension: '',
  filePath: ''
})

const fileInputRef = ref<HTMLInputElement | null>(null)
const editFileInputRef = ref<HTMLInputElement | null>(null)
const selectedFile = ref<File | null>(null)
const editNewFile = ref<File | null>(null)
const editSelectedFileName = computed(() => {
  if (editNewFile.value) {
    return editNewFile.value.name
  }
  if (editForm.filePath) {
    const name = extractOriginalFileName(editForm.filePath)
    return name || editForm.filePath
  }
  return '未选择文件'
})
const uploadSubmitting = ref(false)
const editSubmitting = ref(false)
const uploadModalVisible = ref(false)
const editModalVisible = ref(false)

const categoryCount = computed(() => new Set(rows.value.map((item) => item.categoryId).filter((item) => item != null)).size)
const downloadableCount = computed(() => rows.value.filter((item) => !!item.filePath).length)

const CATEGORY_OPTIONS = [
  { label: '讲义', value: 1 },
  { label: '代码', value: 2 },
  { label: '软件', value: 3 },
  { label: '课件', value: 4 },
  { label: '参考资料', value: 5 }
] as const

const categoryAutoCompleteOptions = CATEGORY_OPTIONS.map((item) => ({ value: item.label }))

const columns = [
  { title: '资料名称', dataIndex: 'lectureName', key: 'lectureName', width: 280 },
  { title: '资料类型', dataIndex: 'categoryId', key: 'categoryId', width: 120 },
  { title: '文件后缀', dataIndex: 'fileExtension', key: 'fileExtension', width: 120 },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 180 },
  { title: '更新时间', dataIndex: 'updatedAt', key: 'updatedAt', width: 180 },
  { title: '操作', key: 'actions', width: 140, fixed: 'right' as const }
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

function resetUploadForm(): void {
  uploadForm.lectureName = ''
  uploadForm.categoryInput = ''
  selectedFile.value = null
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

function resetEditForm(): void {
  editForm.id = null
  editForm.lectureName = ''
  editForm.categoryInput = ''
  editForm.fileExtension = ''
  editForm.filePath = ''
  editNewFile.value = null
  if (editFileInputRef.value) {
    editFileInputRef.value.value = ''
  }
}

function getFileExtension(fileName: string): string {
  const index = fileName.lastIndexOf('.')
  if (index < 0 || index === fileName.length - 1) {
    return ''
  }
  return fileName.slice(index + 1).toLowerCase()
}

function extractOriginalFileName(pathOrUrl: string): string {
  const rawName = pathOrUrl.split('/').pop()?.trim() || ''
  // 后端上传默认会生成：8位随机串 + '-' + 原文件名
  // 例如：Lkha1rZu-技术文档.docx → 技术文档.docx
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

async function ensureTeacherSession(): Promise<boolean> {
  if (!authStore.isAuthenticated) {
    message.warning('请先登录教师账号')
    router.replace('/user/login')
    return false
  }
  await authStore.refreshSessionFromServer()
  if (!authStore.isTeacher) {
    message.warning('当前会话不是教师/管理员，请重新登录教师账号后再操作')
    authStore.logout()
    router.replace('/user/login')
    return false
  }
  return true
}

async function uploadLectureFile(file: File): Promise<{ filePath: string; fileExtension: string }> {
  const fileExtension = getFileExtension(file.name)
  if (!fileExtension) {
    throw new Error('无法识别文件后缀')
  }

  const response = await uploadFile({ biz: 'lecture_material' }, file)
  const filePath = response.data?.data
  if (!filePath) {
    throw new Error(response.data?.message || '文件上传失败')
  }

  return {
    filePath,
    fileExtension
  }
}

async function refresh(): Promise<void> {
  if (!(await ensureTeacherSession())) {
    return
  }

  loading.value = true
  try {
    const categoryId = parseCategoryId(filters.categoryInput)
    const response = await listEduLectureVoByPage({
      current: current.value,
      pageSize: pageSize.value,
      lectureName: filters.lectureName.trim() || undefined,
      categoryId: categoryId ?? undefined
    })

    if (response.data?.code !== 0 || !response.data?.data) {
      throw new Error(response.data?.message || '查询资料失败')
    }

    const pageData = response.data.data
    rows.value = (pageData.records || []) as LectureRow[]
    total.value = Number(pageData.total || 0)
  } catch (error) {
    const err = error as Error
    const msg = err.message || '查询资料失败'
    if (msg.includes('未登录')) {
      message.warning('登录状态已失效，请重新登录后查看资料管理')
      authStore.logout()
      router.replace('/user/login')
      return
    }
    message.error(msg)
    rows.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleTableChange(pagination: PaginationConfig): void {
  current.value = pagination.current ?? 1
  pageSize.value = pagination.pageSize ?? 10
  void refresh()
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

function handleFileSelected(event: Event): void {
  const target = event.target as HTMLInputElement | null
  selectedFile.value = target?.files?.[0] ?? null
}

function handleEditFileSelected(event: Event): void {
  const target = event.target as HTMLInputElement | null
  editNewFile.value = target?.files?.[0] ?? null
}

function openUploadModal(): void {
  resetUploadForm()
  editModalVisible.value = false
  uploadModalVisible.value = true
}

function closeUploadModal(): void {
  uploadModalVisible.value = false
  uploadSubmitting.value = false
  resetUploadForm()
}

function openEditModal(row: LectureRow): void {
  editForm.id = row.id
  editForm.lectureName = row.lectureName || ''
  editForm.categoryInput = row.categoryId != null ? formatCategoryLabel(row.categoryId) : ''
  editForm.fileExtension = row.fileExtension || ''
  editForm.filePath = row.filePath || ''
  editNewFile.value = null
  if (editFileInputRef.value) {
    editFileInputRef.value.value = ''
  }
  uploadModalVisible.value = false
  editModalVisible.value = true
}

function closeEditModal(): void {
  editModalVisible.value = false
  editSubmitting.value = false
  resetEditForm()
}

async function handleUpload(): Promise<void> {
  if (!(await ensureTeacherSession())) {
    return
  }
  if (!uploadForm.lectureName.trim()) {
    message.warning('请填写资料名称')
    return
  }
  const categoryId = parseCategoryId(uploadForm.categoryInput)
  if (!categoryId) {
    message.warning('请选择资料类型，或输入自定义类型编号（数字）')
    return
  }
  if (!selectedFile.value) {
    message.warning('请先选择文件')
    return
  }

  uploadSubmitting.value = true
  try {
    const { filePath, fileExtension } = await uploadLectureFile(selectedFile.value)
    const response = await addEduLecture({
      lectureName: uploadForm.lectureName.trim(),
      categoryId,
      fileExtension,
      filePath
    } as API.EduLectureAddRequest & { filePath: string })

    if (response.data?.code !== 0 || !response.data?.data) {
      throw new Error(response.data?.message || '上传资料失败')
    }

    message.success('资料上传成功')
    closeUploadModal()
    current.value = 1
    await refresh()
  } catch (error) {
    const err = error as Error
    message.error(err.message || '上传资料失败')
  } finally {
    uploadSubmitting.value = false
  }
}

async function submitEdit(): Promise<void> {
  if (!(await ensureTeacherSession())) {
    return
  }
  if (!editForm.id) {
    message.error('未找到待编辑资料')
    return
  }
  if (!editForm.lectureName.trim()) {
    message.warning('请填写资料名称')
    return
  }
  const categoryId = parseCategoryId(editForm.categoryInput)
  if (!categoryId) {
    message.warning('请选择资料类型，或输入自定义类型编号（数字）')
    return
  }

  editSubmitting.value = true
  try {
    let filePath = editForm.filePath
    let fileExtension = editForm.fileExtension

    if (editNewFile.value) {
      const uploaded = await uploadLectureFile(editNewFile.value)
      filePath = uploaded.filePath
      fileExtension = uploaded.fileExtension
    }

    const response = await updateEduLecture({
      id: editForm.id,
      lectureName: editForm.lectureName.trim(),
      categoryId,
      fileExtension,
      filePath
    } as API.EduLectureUpdateRequest & { filePath?: string })

    if (response.data?.code !== 0 || !response.data?.data) {
      throw new Error(response.data?.message || '保存资料失败')
    }

    message.success('资料更新成功')
    closeEditModal()
    await refresh()
  } catch (error) {
    const err = error as Error
    message.error(err.message || '保存资料失败')
  } finally {
    editSubmitting.value = false
  }
}

function handleDownload(row: LectureRow): void {
  if (!row.filePath) {
    message.warning('该资料缺少文件地址，无法下载')
    return
  }
  CommonUtil.downloadFile(row.filePath, buildDownloadName(row))
}

function handleDelete(row: LectureRow): void {
  Modal.confirm({
    title: '删除资料确认',
    content: `确定要删除资料「${row.lectureName || '--'}」吗？`,
    okText: '删除',
    cancelText: '取消',
    okButtonProps: { danger: true },
    onOk: async () => {
      if (!(await ensureTeacherSession())) {
        return
      }
      const response = await deleteEduLecture({ id: String(row.id) })
      if (response.data?.code !== 0 || !response.data?.data) {
        throw new Error(response.data?.message || '删除资料失败')
      }
      message.success('资料删除成功')
      await refresh()
    }
  })
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
  flex: 0 0 180px;
  min-width: 160px;
}

.toolbar-btn {
  flex: 0 0 auto;
  white-space: nowrap;
}

.material-name-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.material-name {
  font-weight: 500;
  color: var(--color-text-primary);
}

.material-subtext {
  font-size: 12px;
  color: var(--color-text-tertiary);
}

.row-actions {
  display: flex;
  gap: 4px;
  align-items: center;
}

.file-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.file-name {
  color: var(--color-text-secondary);
  font-size: 13px;
  max-width: 420px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hidden-file-input {
  display: none;
}

.hint-text {
  margin: 8px 0 0;
  color: var(--color-text-tertiary);
  font-size: 12px;
}

@media (max-width: 960px) {
  .toolbar-select {
    flex: 1 1 180px;
  }
}
</style>