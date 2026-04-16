<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader
        eyebrow="用户管理"
        title="学生名册检索"
        description="根据后端学生表数据，支持教师按学号、姓名、班级、账号状态进行查询与分页浏览。"
      />
    </section>

    <section class="app-kpi-grid">
      <MetricCard title="查询总数" :value="String(total)" description="满足当前筛选条件的学生总数。" tone="primary" />
      <MetricCard title="当前页数据" :value="String(studentRows.length)" description="当前分页已加载学生条数。" tone="accent" />
      <MetricCard title="正常账号" :value="String(normalCount)" description="当前页账号状态为正常（0）的学生数量。" tone="success" />
      <MetricCard title="异常账号" :value="String(abnormalCount)" description="当前页账号状态非 0 的学生数量。" tone="warning" />
    </section>

    <section class="app-surface-card app-section-card app-panel-grid">
      <div class="user-toolbar">
        <a-input
          v-model:value="queryForm.studentCode"
          allow-clear
          size="large"
          placeholder="按学号精确查询"
          class="toolbar-item toolbar-input"
        />
        <a-input
          v-model:value="queryForm.studentName"
          allow-clear
          size="large"
          placeholder="按姓名模糊查询"
          class="toolbar-item toolbar-input"
        />
        <a-select
          v-model:value="queryForm.classCode"
          size="large"
          :options="classOptions"
          :loading="classLoading"
          show-search
          option-filter-prop="label"
          class="toolbar-item toolbar-select"
        />
        <a-select v-model:value="queryForm.accountStatus" size="large" :options="statusOptions" class="toolbar-item toolbar-select" />
        <a-button size="large" class="toolbar-item toolbar-btn" @click="resetFilters">重置筛选</a-button>
        <a-button type="primary" size="large" class="toolbar-item toolbar-btn" @click="openAddModal">新增学生</a-button>
        <a-button size="large" class="toolbar-item toolbar-btn" @click="openFileSelector">选择文件</a-button>
        <a-button
          type="dashed"
          size="large"
          class="toolbar-item toolbar-btn"
          @click="downloadImportTemplate"
        >
          下载模板
        </a-button>
      </div>
      <input
        ref="fileInputRef"
        type="file"
        accept=".xlsx,.xls,.csv"
        class="hidden-file-input"
        @change="handleFileSelected"
      />
      <div v-if="selectedFileName" class="selected-file-tip selected-file-tip-row">
        <span>已选择文件：{{ selectedFileName }}</span>
        <a-button type="primary" size="small" @click="handleUploadSelectedFile">
          上传
        </a-button>
      </div>
      <div v-if="queryForm.classCode" class="class-batch-toolbar">
        <a-button
          type="primary"
          :loading="batchUpdating"
          @click="confirmBatchUpdate(0)"
        >
          启用全班所有账号
        </a-button>
        <a-button
          danger
          :loading="batchUpdating"
          @click="confirmBatchUpdate(1)"
        >
          禁用全班账号
        </a-button>
      </div>

      <a-table
        :columns="studentColumns"
        :data-source="studentRows"
        :loading="loading"
        row-key="id"
        :pagination="{
          current,
          pageSize,
          total,
          showSizeChanger: true,
          showTotal: (t: number) => `共 ${t} 条`
        }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'accountStatus'">
            <StatusTag :type="record.accountStatus === 0 ? 'success' : 'warning'" :label="record.accountStatus === 0 ? '正常' : '禁用'" />
          </template>
          <template v-else-if="column.key === 'lastLoginIp'">
            <span>{{ record.lastLoginIp || '--' }}</span>
          </template>
          <template v-else-if="column.key === 'remark'">
            <span>{{ record.remark || '--' }}</span>
          </template>
          <template v-else-if="column.key === 'actions'">
            <div class="row-actions">
              <a-tooltip title="重置密码为 12345678">
                <a-button type="text" size="small" @click="handleResetPassword(record)">
                  <template #icon>
                    <RedoOutlined />
                  </template>
                </a-button>
              </a-tooltip>
              <a-tooltip title="编辑账号">
                <a-button type="text" size="small" @click="openEditModal(record)">
                  <template #icon>
                    <EditOutlined />
                  </template>
                </a-button>
              </a-tooltip>
              <a-tooltip title="删除账号">
                <a-button danger type="text" size="small" @click="handleDeleteStudent(record)">
                  <template #icon>
                    <DeleteOutlined />
                  </template>
                </a-button>
              </a-tooltip>
            </div>
          </template>
        </template>
      </a-table>
    </section>
    <a-modal
      v-model:open="editModalVisible"
      title="编辑学生账号"
      :confirm-loading="editSubmitting"
      ok-text="保存"
      cancel-text="取消"
      @ok="submitEditStudent"
      @cancel="closeEditModal"
    >
      <a-form layout="vertical">
        <a-form-item label="学号">
          <a-input v-model:value="editForm.studentCode" placeholder="请输入学号" />
        </a-form-item>
        <a-form-item label="姓名">
          <a-input v-model:value="editForm.studentName" placeholder="请输入学生姓名" />
        </a-form-item>
        <a-form-item label="密码（留空不修改）">
          <a-input-password v-model:value="editForm.password" placeholder="请输入新密码（至少 8 位）" />
        </a-form-item>
        <a-form-item label="班级">
          <a-select
            v-model:value="editForm.classCode"
            :options="classOptions.filter((item) => item.value)"
            placeholder="请选择班级"
          />
        </a-form-item>
        <a-form-item label="专业/备注">
          <a-input v-model:value="editForm.remark" placeholder="请输入专业或备注" />
        </a-form-item>
        <a-form-item label="账号状态">
          <a-select
            v-model:value="editForm.accountStatus"
            :options="[
              { label: '正常', value: 0 },
              { label: '异常', value: 1 }
            ]"
          />
        </a-form-item>
      </a-form>
    </a-modal>
    <a-modal
      v-model:open="addModalVisible"
      title="新增学生"
      :confirm-loading="addSubmitting"
      ok-text="新增"
      cancel-text="取消"
      @ok="submitAddStudent"
      @cancel="closeAddModal"
    >
      <a-form layout="vertical">
        <a-form-item label="学号">
          <a-input v-model:value="addForm.studentCode" placeholder="请输入学号" />
        </a-form-item>
        <a-form-item label="姓名">
          <a-input v-model:value="addForm.studentName" placeholder="请输入学生姓名" />
        </a-form-item>
        <a-form-item label="初始密码">
          <a-input-password v-model:value="addForm.password" placeholder="请输入初始密码（至少 8 位）" />
        </a-form-item>
        <a-form-item label="班级">
          <a-select
            v-model:value="addForm.classCode"
            :options="classOptions.filter((item) => item.value)"
            placeholder="请选择班级"
          />
        </a-form-item>
        <a-form-item label="专业/备注">
          <a-input v-model:value="addForm.remark" placeholder="请输入专业或备注（选填）" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import MetricCard from '@/components/common/MetricCard.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import {
  batchUpdateAccountStatusByClass,
  addAuthStudent,
  deleteAuthStudent,
  importAuthStudents,
  listAuthStudentVoByPage,
  resetAuthStudentPassword,
  updateAuthStudent
} from '@/api/authStudentController'
import { listAuthClassVoByPage } from '@/api/authClassController'
import { message, Modal } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/user/auth'
import { DeleteOutlined, EditOutlined, RedoOutlined } from '@ant-design/icons-vue'
import ExcelUtil from '@/utils/excel'

interface StudentRow {
  id: number
  studentCode: string
  studentName: string
  classCode?: string
  remark?: string
  accountStatus?: number
  loginFailCount?: number
  lastLoginIp?: string
  updatedAt?: string
}

interface PaginationConfig {
  current?: number
  pageSize?: number
}

interface AuthClassRow {
  classCode?: string
}

const loading = ref(false)
const studentRows = ref<StudentRow[]>([])
const current = ref(1)
const pageSize = ref(10)
const total = ref(0)
const classLoading = ref(false)
const batchUpdating = ref(false)
const editModalVisible = ref(false)
const editSubmitting = ref(false)
const addModalVisible = ref(false)
const addSubmitting = ref(false)
const classOptions = ref<{ label: string; value: string }[]>([{ label: '全部班级', value: '' }])
const fileInputRef = ref<HTMLInputElement | null>(null)
const selectedFileName = ref('')
const selectedFile = ref<File | null>(null)
const uploadSubmitting = ref(false)
const router = useRouter()
const authStore = useAuthStore()
let searchTimer: ReturnType<typeof setTimeout> | null = null
const editingStudentId = ref<number | null>(null)

const queryForm = reactive({
  studentCode: '',
  studentName: '',
  classCode: '',
  accountStatus: 'all'
})

const editForm = reactive({
  studentCode: '',
  studentName: '',
  password: '',
  classCode: '',
  remark: '',
  accountStatus: 0
})

const addForm = reactive({
  studentCode: '',
  studentName: '',
  password: '',
  classCode: '',
  remark: ''
})

const statusOptions = [
  { label: '全部状态', value: 'all' },
  { label: '正常', value: '0' },
  { label: '异常', value: '1' }
]

const normalCount = computed(() => studentRows.value.filter((item) => item.accountStatus === 0).length)
const abnormalCount = computed(() => studentRows.value.filter((item) => item.accountStatus !== 0).length)

const studentColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 90 },
  { title: '学号', dataIndex: 'studentCode', key: 'studentCode', width: 130 },
  { title: '姓名', dataIndex: 'studentName', key: 'studentName', width: 120 },
  { title: '班级', dataIndex: 'classCode', key: 'classCode', width: 120 },
  { title: '专业/备注', dataIndex: 'remark', key: 'remark', width: 160 },
  { title: '状态', dataIndex: 'accountStatus', key: 'accountStatus', width: 110 },
  { title: '失败次数', dataIndex: 'loginFailCount', key: 'loginFailCount', width: 110 },
  { title: '最后登录IP', dataIndex: 'lastLoginIp', key: 'lastLoginIp', width: 160 },
  { title: '操作', key: 'actions', width: 120, fixed: 'right' as const }
]

function updateClassOptionsFromRows(rows: StudentRow[]): void {
  const existing = new Set(classOptions.value.map((item) => item.value))
  const next = [...classOptions.value]
  for (const row of rows) {
    const code = row.classCode?.trim()
    if (!code || existing.has(code)) {
      continue
    }
    existing.add(code)
    next.push({ label: code, value: code })
  }
  classOptions.value = next
}

async function fetchStudents(): Promise<void> {
  if (!(await ensureTeacherSession())) {
    return
  }
  loading.value = true
  try {
    const response = await listAuthStudentVoByPage({
      current: current.value,
      pageSize: pageSize.value,
      studentCode: queryForm.studentCode.trim() || undefined,
      studentName: queryForm.studentName.trim() || undefined,
      classCode: queryForm.classCode.trim() || undefined,
      accountStatus: queryForm.accountStatus === 'all' ? undefined : Number(queryForm.accountStatus)
    })
    if (response.data?.code !== 0 || !response.data.data) {
      throw new Error(response.data?.message || '查询学生失败')
    }
    const pageData = response.data.data
    studentRows.value = (pageData.records || []) as StudentRow[]
    total.value = Number(pageData.total || 0)
    updateClassOptionsFromRows(studentRows.value)
  } catch (error) {
    const err = error as Error
    const msg = err.message || '查询学生失败'
    if (msg.includes('未登录')) {
      message.warning('登录状态已失效，请重新登录后查看学生列表')
      authStore.logout()
      router.replace('/user/login')
      return
    }
    message.error(msg)
    studentRows.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

async function fetchClasses(): Promise<void> {
  classLoading.value = true
  try {
    const response = await listAuthClassVoByPage({
      current: 1,
      pageSize: 50
    })
    if (response.data?.code !== 0 || !response.data.data) {
      throw new Error(response.data?.message || '查询班级失败')
    }
    const records = (response.data.data.records || []) as AuthClassRow[]
    classOptions.value = [
      { label: '全部班级', value: '' },
      ...records.map((item: AuthClassRow) => ({
        label: item.classCode || '',
        value: item.classCode || ''
      })).filter((item: { label: string; value: string }) => item.value)
    ]
  } catch (error) {
    const err = error as Error
    // 班级接口异常时，后续会用学生列表里的 classCode 自动补全下拉选项
    message.warning(err.message || '查询班级失败，已使用学生列表班级作为下拉选项')
    classOptions.value = [{ label: '全部班级', value: '' }]
  } finally {
    classLoading.value = false
  }
}

function resetFilters(): void {
  queryForm.studentCode = ''
  queryForm.studentName = ''
  queryForm.classCode = ''
  queryForm.accountStatus = 'all'
  current.value = 1
  fetchStudents()
}

function handleTableChange(pagination: PaginationConfig): void {
  current.value = pagination.current ?? 1
  pageSize.value = pagination.pageSize ?? 10
  fetchStudents()
}

function handleResetPassword(record: StudentRow): void {
  Modal.confirm({
    title: '重置密码确认',
    content: `确定要重置学号 ${record.studentCode} 的密码吗？重置后默认为 12345678。`,
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      if (!(await ensureTeacherSession())) {
        return
      }
      const response = await resetAuthStudentPassword({ id: record.id })
      if (response.data?.code !== 0 || !response.data?.data) {
        throw new Error(response.data?.message || '重置密码失败')
      }
      message.success('重置密码成功，默认密码为 12345678')
    }
  })
}

function handleDeleteStudent(record: StudentRow): void {
  Modal.confirm({
    title: '删除账号确认',
    content: `确定要删除学生 ${record.studentName}（${record.studentCode}）吗？`,
    okText: '确定',
    cancelText: '取消',
    okButtonProps: { danger: true },
    onOk: async () => {
      if (!(await ensureTeacherSession())) {
        return
      }
      const response = await deleteAuthStudent({ id: String(record.id) })
      if (response.data?.code !== 0 || !response.data?.data) {
        throw new Error(response.data?.message || '删除账号失败')
      }
      message.success('删除账号成功')
      await fetchStudents()
    }
  })
}

function openEditModal(record: StudentRow): void {
  editingStudentId.value = record.id
  editForm.studentCode = record.studentCode || ''
  editForm.studentName = record.studentName || ''
  editForm.password = ''
  editForm.classCode = record.classCode || ''
  editForm.remark = record.remark || ''
  editForm.accountStatus = record.accountStatus ?? 0
  editModalVisible.value = true
}

function closeEditModal(): void {
  editModalVisible.value = false
  editingStudentId.value = null
}

function openAddModal(): void {
  addForm.studentCode = ''
  addForm.studentName = ''
  addForm.password = ''
  addForm.classCode = ''
  addForm.remark = ''
  addModalVisible.value = true
}

function closeAddModal(): void {
  addModalVisible.value = false
}

function openFileSelector(): void {
  fileInputRef.value?.click()
}

function handleFileSelected(event: Event): void {
  const target = event.target as HTMLInputElement | null
  const file = target?.files?.[0]
  if (!file) {
    return
  }
  selectedFile.value = file
  selectedFileName.value = file.name
  message.success(`已选择文件：${file.name}`)
}

async function handleUploadSelectedFile(): Promise<void> {
  if (!(await ensureTeacherSession())) {
    return
  }
  if (!selectedFile.value) {
    message.warning('请先选择文件')
    return
  }
  uploadSubmitting.value = true
  try {
    const validation = ExcelUtil.validateFile(selectedFile.value, {
      allowedExtensions: ['.xlsx', '.xls', '.csv'],
      maxSize: 10 * 1024 * 1024
    })
    if (!validation.valid) {
      message.warning(validation.message)
      return
    }

    const importResult = await ExcelUtil.importExcel(selectedFile.value, {
      sheetIndex: 0,
      header: true
    })

    if (!importResult.success) {
      throw new Error('解析失败')
    }

    const rows = importResult.data as Record<string, unknown>[]
    const toStr = (v: unknown): string => {
      if (v === null || v === undefined) return ''
      return String(v).trim()
    }

    const payload: Array<{
      studentCode: string
      studentName: string
      password: string
      classCode?: string
      remark?: string
    }> = []

    rows.forEach((row, index) => {
      const lineNo = index + 2 // 表头是第 1 行
      const studentCode = toStr(row['学号'])
      const studentName = toStr(row['姓名'])
      const password = toStr(row['密码'])
      const classCodeRaw = toStr(row['班级'])
      const remarkRaw = toStr(row['专业/备注'])

      // 空行跳过
      if (!studentCode && !studentName && !password && !classCodeRaw && !remarkRaw) {
        return
      }

      if (!studentCode || !studentName || !password) {
        throw new Error(`导入数据第 ${lineNo} 行缺少必填字段（学号/姓名/密码）`)
      }

      const item: {
        studentCode: string
        studentName: string
        password: string
        classCode?: string
        remark?: string
      } = {
        studentCode,
        studentName,
        password
      }

      if (classCodeRaw) {
        item.classCode = classCodeRaw
      }
      if (remarkRaw) {
        item.remark = remarkRaw
      }
      payload.push(item)
    })

    if (payload.length === 0) {
      message.warning('文件中没有有效数据')
      return
    }

    const response = await importAuthStudents(payload)
    if (response.data?.code !== 0 || !response.data?.data) {
      throw new Error(response.data?.message || '导入失败')
    }

    message.success(`导入成功，共 ${payload.length} 条`)
    selectedFile.value = null
    selectedFileName.value = ''
    if (fileInputRef.value) {
      fileInputRef.value.value = ''
    }
    current.value = 1
    await fetchStudents()
  } catch (error) {
    const err = error as Error
    message.error(err.message || '导入失败')
  } finally {
    uploadSubmitting.value = false
  }
}

function downloadImportTemplate(): void {
  const headers = ['学号', '姓名', '密码', '班级', '专业/备注']
  const sampleData: (string | number)[][] = [
    ['23201324', '小钱', '12345678', '232013', '软件工程'],
    ['23201325', '小张', '12345678', '232013', '软件工程'],
    ['23201326', '小李', '12345678', '232013', '自动化'],
    ['', '', '', '', '']
  ]

  const result = ExcelUtil.downloadTemplate(headers, '学生导入模板', sampleData)
  if (!result.success) {
    message.error(result.message || '模板下载失败')
  }
}

async function submitAddStudent(): Promise<void> {
  if (!(await ensureTeacherSession())) {
    return
  }
  if (!addForm.studentCode.trim()) {
    message.warning('学号不能为空')
    return
  }
  if (!addForm.studentName.trim()) {
    message.warning('姓名不能为空')
    return
  }
  if (!addForm.password.trim()) {
    message.warning('初始密码不能为空')
    return
  }
  if (addForm.password.trim().length < 8) {
    message.warning('初始密码长度不能小于 8 位')
    return
  }
  if (!addForm.classCode.trim()) {
    message.warning('班级不能为空')
    return
  }
  addSubmitting.value = true
  try {
    const response = await addAuthStudent({
      studentCode: addForm.studentCode.trim(),
      studentName: addForm.studentName.trim(),
      passwordMd5: addForm.password.trim(),
      classCode: addForm.classCode.trim(),
      remark: addForm.remark.trim() || undefined,
      accountStatus: 0,
      loginFailCount: 0
    })
    if (response.data?.code !== 0 || !response.data?.data) {
      throw new Error(response.data?.message || '新增学生失败')
    }
    message.success('新增学生成功')
    closeAddModal()
    current.value = 1
    await fetchStudents()
  } catch (error) {
    const err = error as Error
    message.error(err.message || '新增学生失败')
  } finally {
    addSubmitting.value = false
  }
}

async function submitEditStudent(): Promise<void> {
  if (!(await ensureTeacherSession())) {
    return
  }
  if (!editingStudentId.value) {
    message.error('未找到待编辑学生')
    return
  }
  if (!editForm.studentCode.trim()) {
    message.warning('学号不能为空')
    return
  }
  if (!editForm.studentName.trim()) {
    message.warning('姓名不能为空')
    return
  }
  if (!editForm.classCode.trim()) {
    message.warning('班级不能为空')
    return
  }
  editSubmitting.value = true
  try {
    const response = await updateAuthStudent({
      id: editingStudentId.value,
      studentCode: editForm.studentCode.trim(),
      studentName: editForm.studentName.trim(),
      passwordMd5: editForm.password.trim() || undefined,
      classCode: editForm.classCode.trim(),
      remark: editForm.remark.trim() || undefined,
      accountStatus: editForm.accountStatus
    })
    if (response.data?.code !== 0 || !response.data?.data) {
      throw new Error(response.data?.message || '编辑账号失败')
    }
    message.success('编辑账号成功')
    closeEditModal()
    await fetchStudents()
  } catch (error) {
    const err = error as Error
    message.error(err.message || '编辑账号失败')
  } finally {
    editSubmitting.value = false
  }
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

function confirmBatchUpdate(accountStatus: 0 | 1): void {
  if (!queryForm.classCode) {
    message.warning('请先选择班级')
    return
  }
  const actionText = accountStatus === 0 ? '启用' : '禁用'
  Modal.confirm({
    title: `${actionText}班级账号确认`,
    content: `确定要${actionText}班级 ${queryForm.classCode} 的所有学生账号吗？`,
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      if (!(await ensureTeacherSession())) {
        return
      }
      batchUpdating.value = true
      try {
        const response = await batchUpdateAccountStatusByClass({
          classCode: queryForm.classCode,
          accountStatus
        })
        if (response.data?.code !== 0 || !response.data?.data) {
          throw new Error(response.data?.message || `${actionText}失败`)
        }
        message.success(`${actionText}成功`)
        await fetchStudents()
      } catch (error) {
        const err = error as Error
        message.error(err.message || `${actionText}失败`)
      } finally {
        batchUpdating.value = false
      }
    }
  })
}

watch(
  () => [queryForm.studentCode, queryForm.studentName, queryForm.classCode, queryForm.accountStatus],
  () => {
    if (searchTimer) {
      clearTimeout(searchTimer)
    }
    searchTimer = setTimeout(() => {
      current.value = 1
      fetchStudents()
    }, 300)
  }
)

onBeforeUnmount(() => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
})

onMounted(async () => {
  if (!(await ensureTeacherSession())) {
    return
  }
  await fetchClasses()
  await fetchStudents()
})
</script>

<style scoped>
.user-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.toolbar-item {
  min-width: 0;
}

.toolbar-input {
  flex: 1 1 180px;
  min-width: 140px;
}

.toolbar-select {
  flex: 1 1 160px;
  min-width: 120px;
}

.toolbar-btn {
  flex: 0 0 auto;
  min-width: 100px;
  white-space: nowrap;
}

.hidden-file-input {
  display: none;
}

.selected-file-tip {
  margin-top: 8px;
  color: #64748b;
  font-size: 13px;
}

.selected-file-tip-row {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 12px;
}

.class-batch-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.row-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

@media (max-width: 960px) {
  .user-toolbar {
    gap: 10px;
  }
}
</style>
