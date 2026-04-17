<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="班级管理" title="班级基础信息" description="维护班级编号、负责人及班级状态。">
        <template #actions>
          <a-space :size="12" wrap>
            <a-button type="primary" @click="openAddModal">新增班级</a-button>
          </a-space>
        </template>
      </SectionHeader>
    </section>

    <section class="app-kpi-grid">
      <MetricCard title="班级数" :value="String(total)" description="当前页加载到的班级总数。" tone="primary" />
      <MetricCard title="正常班级" :value="String(activeCount)" description="classStatus=0 的班级数量。" tone="success" />
      <MetricCard title="归档班级" :value="String(archivedCount)" description="classStatus=1 的班级数量。" tone="warning" />
    </section>

    <section class="app-surface-card app-section-card app-panel-grid">
      <div class="class-toolbar">
        <a-input
          v-model:value="queryForm.classCode"
          allow-clear
          size="large"
          placeholder="按班级编号精确查询"
          class="toolbar-input"
        />
        <a-input
          v-model:value="queryForm.headmasterName"
          allow-clear
          size="large"
          placeholder="按负责人模糊查询"
          class="toolbar-input"
        />
        <a-select v-model:value="queryForm.classStatus" size="large" :options="statusOptions" class="toolbar-select" />

        <a-button size="large" class="toolbar-btn" @click="resetFilters">重置筛选</a-button>
      </div>

      <a-table
        :columns="classColumns"
        :data-source="classRows"
        :loading="loading"
        row-key="classCode"
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
          <template v-if="column.key === 'classStatus'">
            <StatusTag
              :type="record.classStatus === 0 ? 'success' : 'warning'"
              :label="record.classStatus === 0 ? 'ip未限制' : 'ip限制'"
            />
          </template>
          <template v-else-if="column.key === 'studentCount'">
            <span>{{ record.studentCount ?? 0 }}</span>
          </template>
          <template v-else-if="column.key === 'createdAt'">
            <span>{{ record.createdAt ? CommonUtil.formatDate(record.createdAt) : '--' }}</span>
          </template>
          <template v-else-if="column.key === 'updatedAt'">
            <span>{{ record.updatedAt ? CommonUtil.formatDate(record.updatedAt) : '--' }}</span>
          </template>
          <template v-else-if="column.key === 'actions'">
            <div class="row-actions">
              <a-tooltip title="编辑">
                <a-button type="text" size="small" @click="openEditModal(record)">
                  <template #icon>
                    <EditOutlined />
                  </template>
                </a-button>
              </a-tooltip>
              <a-tooltip title="删除">
                <a-button danger type="text" size="small" @click="handleDeleteClass(record)">
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
      :title="editingClassCode ? '编辑班级' : '新增班级'"
      :confirm-loading="editSubmitting"
      ok-text="保存"
      cancel-text="取消"
      @ok="submitEditClass"
      @cancel="closeEditModal"
    >
      <a-form layout="vertical">
        <a-form-item label="班级编号">
          <a-input v-model:value="editForm.classCode" :disabled="!!editingClassCode" placeholder="例如：232013" />
        </a-form-item>
        <a-form-item label="负责人">
          <a-input v-model:value="editForm.headmasterName" placeholder="例如：张老师" />
        </a-form-item>
        <a-form-item label="班级状态">
          <a-select v-model:value="editForm.classStatus" :options="[{ label: '关闭ip限制', value: 0 }, { label: 'ip限制', value: 1 }]" />
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
import { CommonUtil } from '@/utils'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { DeleteOutlined, EditOutlined } from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/user/auth'
import { addAuthClass, deleteAuthClass, listAuthClassVoByPage, updateAuthClass } from '@/api/authClassController'
import { listAuthStudentVoByPage } from '@/api/authStudentController'

interface ClassRow {
  classCode: string
  headmasterName: string
  classStatus: number
  studentCount?: number
  createdAt?: string | Date
  updatedAt?: string | Date
}

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const classRows = ref<ClassRow[]>([])
const current = ref(1)
const pageSize = ref(10)
const total = ref(0)
let searchTimer: ReturnType<typeof setTimeout> | null = null

const queryForm = reactive({
  classCode: '',
  headmasterName: '',
  classStatus: 'all' as 'all' | 0 | 1
})

const statusOptions = [
  { label: '全部状态', value: 'all' },
  { label: '正常', value: 0 },
  { label: '归档', value: 1 }
]

const activeCount = computed(() => classRows.value.filter((it) => it.classStatus === 0).length)
const archivedCount = computed(() => classRows.value.filter((it) => it.classStatus === 1).length)

const classColumns = [
  { title: '班级编号', dataIndex: 'classCode', key: 'classCode', width: 150 },
  { title: '负责人', dataIndex: 'headmasterName', key: 'headmasterName', width: 200 },
  { title: '班级状态', dataIndex: 'classStatus', key: 'classStatus', width: 120 },
  { title: '人数', dataIndex: 'studentCount', key: 'studentCount', width: 120 },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 180 },
  { title: '更新时间', dataIndex: 'updatedAt', key: 'updatedAt', width: 180 },
  { title: '操作', key: 'actions', width: 140, fixed: 'right' as const }
]

const editModalVisible = ref(false)
const editSubmitting = ref(false)
const editingClassCode = ref<string | null>(null)

const editForm = reactive({
  classCode: '',
  headmasterName: '',
  classStatus: 0 as 0 | 1
})

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

async function fetchClasses(): Promise<void> {
  if (!(await ensureTeacherSession())) {
    return
  }

  loading.value = true
  try {
    const response = await listAuthClassVoByPage({
      current: current.value,
      pageSize: pageSize.value,
      classCode: queryForm.classCode.trim() || undefined,
      headmasterName: queryForm.headmasterName.trim() || undefined,
      classStatus: queryForm.classStatus === 'all' ? undefined : Number(queryForm.classStatus)
    })

    if (response.data?.code !== 0 || !response.data?.data) {
      throw new Error(response.data?.message || '查询班级失败')
    }

    const pageData = response.data.data
    classRows.value = (pageData.records || []) as ClassRow[]
    total.value = Number(pageData.total || 0)

    await fetchStudentCountsForClasses(classRows.value)
  } catch (error) {
    const err = error as Error
    const msg = err.message || '查询班级失败'
    if (msg.includes('未登录')) {
      message.warning('登录状态已失效，请重新登录后查看班级')
      authStore.logout()
      router.replace('/user/login')
      return
    }
    message.error(msg)
    classRows.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

async function fetchStudentCountsForClasses(rows: ClassRow[]): Promise<void> {
  const classCodes = rows
    .map((it) => it.classCode)
    .map((it) => it?.trim())
    .filter((it): it is string => !!it)

  if (classCodes.length === 0) {
    return
  }

  // 直接按 classCode 分别取 student 名册 total（避免要求后端改接口）。
  // 当前是 N+1 请求；通常班级数量较少可接受。
  const results = await Promise.allSettled(
    classCodes.map(async (classCode) => {
      const resp = await listAuthStudentVoByPage({
        current: 1,
        pageSize: 1,
        classCode
      })
      const totalCount = Number(resp.data?.data?.total ?? 0)
      return { classCode, totalCount }
    })
  )

  const totalByClassCode = new Map<string, number>()
  for (const r of results) {
    if (r.status === 'fulfilled') {
      totalByClassCode.set(r.value.classCode, r.value.totalCount)
    }
  }

  rows.forEach((row) => {
    row.studentCount = totalByClassCode.get(row.classCode) ?? 0
  })
}

function resetFilters(): void {
  queryForm.classCode = ''
  queryForm.headmasterName = ''
  queryForm.classStatus = 'all'
  current.value = 1
  fetchClasses()
}

function handleTableChange(next: { current?: number; pageSize?: number }): void {
  current.value = next.current ?? 1
  pageSize.value = next.pageSize ?? 10
  fetchClasses()
}

watch(
  () => [queryForm.classCode, queryForm.headmasterName, queryForm.classStatus],
  () => {
    if (searchTimer) {
      clearTimeout(searchTimer)
    }
    searchTimer = setTimeout(() => {
      current.value = 1
      fetchClasses()
    }, 300)
  }
)

onBeforeUnmount(() => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
})

function openAddModal(): void {
  editingClassCode.value = null
  editForm.classCode = ''
  editForm.headmasterName = ''
  editForm.classStatus = 0
  editModalVisible.value = true
}

function openEditModal(record: ClassRow): void {
  editingClassCode.value = record.classCode
  editForm.classCode = record.classCode
  editForm.headmasterName = record.headmasterName || ''
  editForm.classStatus = (record.classStatus as 0 | 1) ?? 0
  editModalVisible.value = true
}

function closeEditModal(): void {
  editModalVisible.value = false
  editingClassCode.value = null
}

async function submitEditClass(): Promise<void> {
  if (!(await ensureTeacherSession())) {
    return
  }

  if (!editForm.classCode.trim()) {
    message.warning('班级编号不能为空')
    return
  }
  if (!editForm.headmasterName.trim()) {
    message.warning('负责人不能为空')
    return
  }

  editSubmitting.value = true
  try {
    const req = {
      classCode: editForm.classCode.trim(),
      headmasterName: editForm.headmasterName.trim(),
      classStatus: editForm.classStatus
    }

    const response = editingClassCode.value
      ? await updateAuthClass(req)
      : await addAuthClass(req)

    if (response.data?.code !== 0 || !response.data?.data) {
      throw new Error(response.data?.message || '保存失败')
    }

    message.success('保存成功')
    closeEditModal()
    await fetchClasses()
  } catch (error) {
    const err = error as Error
    message.error(err.message || '保存失败')
  } finally {
    editSubmitting.value = false
  }
}

function handleDeleteClass(record: ClassRow): void {
  if (!record.classCode) {
    return
  }
  Modal.confirm({
    title: '删除班级确认',
    content: `确定要删除班级 ${record.classCode} 吗？`,
    okText: '确定',
    cancelText: '取消',
    okButtonProps: { danger: true },
    onOk: async () => {
      if (!(await ensureTeacherSession())) {
        return
      }
      const response = await deleteAuthClass({ id: record.classCode })
      if (response.data?.code !== 0 || !response.data?.data) {
        message.error(response.data?.message || '删除失败')
        return
      }
      message.success('删除成功')
      await fetchClasses()
    }
  })
}

onMounted(async () => {
  await fetchClasses()
})
</script>

<style scoped>
.class-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.toolbar-input {
  flex: 1 1 220px;
  min-width: 160px;
}

.toolbar-select {
  flex: 0 0 160px;
  min-width: 140px;
}

.toolbar-btn {
  flex: 0 0 auto;
  white-space: nowrap;
}

.row-actions {
  display: flex;
  gap: 6px;
  align-items: center;
}

@media (max-width: 960px) {
  .toolbar-select {
    flex: 1 1 160px;
  }
}
</style>

