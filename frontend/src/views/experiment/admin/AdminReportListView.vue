<template>
  <div class="app-page-shell">
    <div class="breadcrumb-nav">
      <a-breadcrumb>
        <a-breadcrumb-item>
          <a href="#" @click.prevent="router.push('/admin/experiments/list')">实验管理</a>
        </a-breadcrumb-item>
        <a-breadcrumb-item>实验报告</a-breadcrumb-item>
      </a-breadcrumb>
    </div>

    <section class="app-surface-card page-header-card">
      <div class="page-header">
        <div class="page-header__main">
          <p class="page-header__eyebrow">实验管理</p>
          <h1 class="page-header__title">实验报告管理</h1>
          <p class="page-header__description">选择实验和班级，查看学生实验报告并进入批改。</p>
        </div>
        <div class="page-header__actions">
          <a-button type="primary" @click="router.push('/admin/experiments/list')">
            <template #icon><ExperimentOutlined /></template>
            回到实验列表
          </a-button>
        </div>
      </div>
    </section>

    <section class="app-surface-card filter-card">
      <a-form layout="inline" :model="filterForm">
        <a-form-item label="选择实验">
          <a-select v-model:value="filterForm.experimentId" placeholder="选择实验" style="width: 220px" @change="handleExperimentChange">
            <a-select-option value="">全部实验</a-select-option>
            <a-select-option v-for="exp in experiments" :key="exp.id" :value="exp.id">
              实验{{ exp.experimentNo }} - {{ exp.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="选择班级">
          <a-select v-model:value="filterForm.clazzNo" placeholder="选择班级" style="width: 180px" @change="handleSearch">
            <a-select-option value="">全部班级</a-select-option>
            <a-select-option v-for="clazz in classes" :key="clazz.no" :value="clazz.no">{{ clazz.no }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space :size="12">
            <a-button type="primary" @click="handleSearch">
              <template #icon><SearchOutlined /></template>
              查询
            </a-button>
            <a-button @click="handlePrintByClass" :disabled="!filterForm.experimentId">
              <template #icon><PrinterOutlined /></template>
              按班打印
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </section>

    <section class="app-surface-card table-card">
      <a-table :columns="columns" :data-source="reportList" :loading="loading" row-key="id" :pagination="paginationConfig" @change="handleTableChange">
        <template #bodyCell="{ column, record, index }">
          <template v-if="column.key === 'index'">
            {{ getRowIndex(index) }}
          </template>
          <template v-else-if="column.key === 'studentNo'">
            <span class="student-no">{{ record.studentNo || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'studentName'">
            <span class="student-name">{{ record.studentName || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'clazzNo'">
            <span class="clazz-no">{{ record.clazzNo || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'score'">
            <span v-if="record.score !== undefined && record.score !== null" class="score-value">{{ record.score }}</span>
            <span v-else class="score-pending">-</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space :size="8">
              <a-button type="link" @click="router.push(`/admin/experiments/reports/${record.experimentId}/${record.studentId}`)">
                <template #icon><FileTextOutlined /></template>
                查看报告
              </a-button>
              <a-button type="primary" ghost @click="router.push(`/admin/experiments/results/${record.experimentId}/${record.studentId}`)">
                <template #icon><EditOutlined /></template>
                批改
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </section>

    <section v-if="reportList.length > 0" class="stats-section">
      <div class="stats-card">
        <div class="stat-item"><span class="stat-label">当前页记录</span><span class="stat-value">{{ reportList.length }}</span></div>
        <div class="stat-item"><span class="stat-label">已批改</span><span class="stat-value success">{{ reviewedCount }}</span></div>
        <div class="stat-item"><span class="stat-label">待批改</span><span class="stat-value warning">{{ submittedCount }}</span></div>
        <div class="stat-item"><span class="stat-label">平均分</span><span class="stat-value">{{ averageScore }}</span></div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { EditOutlined, ExperimentOutlined, FileTextOutlined, PrinterOutlined, SearchOutlined } from '@ant-design/icons-vue'
import { getExperimentClasses, getTeacherReportListPage } from '@/api/eduExperimentReportController'
import { listEduExperimentVoByPage } from '@/api/eduExperimentController'

const STORAGE_KEY = 'admin-experiment-report-filters'
const router = useRouter()
const loading = ref(false)
const filterForm = reactive({ experimentId: '', clazzNo: '' })
const experiments = ref<Array<{ id: string; experimentNo: number; name: string }>>([])
const classes = ref<Array<{ no: string }>>([])
const reportList = ref<any[]>([])

const columns = [
  { title: '序号', key: 'index', width: 70, align: 'center' as const },
  { title: '学号', dataIndex: 'studentNo', key: 'studentNo', width: 140 },
  { title: '姓名', dataIndex: 'studentName', key: 'studentName', width: 100 },
  { title: '班级', dataIndex: 'clazzNo', key: 'clazzNo', width: 160 },
  { title: '成绩', dataIndex: 'score', key: 'score', width: 90, align: 'center' as const },
  { title: '操作', key: 'action', width: 220, align: 'center' as const }
]

const paginationConfig = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (t: number) => `共 ${t} 条`
})

const reviewedCount = computed(() => reportList.value.filter((r) => r.status === 'reviewed').length)
const submittedCount = computed(() => reportList.value.filter((r) => r.status === 'submitted').length)
const averageScore = computed(() => {
  const scored = reportList.value.filter((r) => r.score !== undefined && r.score !== null)
  if (scored.length === 0) return '-'
  return (scored.reduce((acc, r) => acc + Number(r.score || 0), 0) / scored.length).toFixed(1)
})

function getRowIndex(index: number) {
  return (paginationConfig.current - 1) * paginationConfig.pageSize + index + 1
}

function saveFilters() {
  sessionStorage.setItem(
    STORAGE_KEY,
    JSON.stringify({
      experimentId: filterForm.experimentId,
      clazzNo: filterForm.clazzNo,
      current: paginationConfig.current,
      pageSize: paginationConfig.pageSize
    })
  )
}

function restoreFilters() {
  const raw = sessionStorage.getItem(STORAGE_KEY)
  if (!raw) return
  try {
    const saved = JSON.parse(raw)
    filterForm.experimentId = saved.experimentId || ''
    filterForm.clazzNo = saved.clazzNo || ''
    paginationConfig.current = Number(saved.current || 1)
    paginationConfig.pageSize = Number(saved.pageSize || 10)
  } catch {
    sessionStorage.removeItem(STORAGE_KEY)
  }
}

function handleExperimentChange() {
  paginationConfig.current = 1
  filterForm.clazzNo = ''
  saveFilters()
  void loadClasses()
  void loadReports()
}

function handleSearch() {
  paginationConfig.current = 1
  saveFilters()
  void loadReports()
}

function handleTableChange(pag: { current?: number; pageSize?: number }) {
  paginationConfig.current = pag.current ?? 1
  paginationConfig.pageSize = pag.pageSize ?? 10
  saveFilters()
  void loadReports()
}

function handlePrintByClass() {
  if (!filterForm.experimentId) return
  window.open(`/admin/experiments/${filterForm.experimentId}/reports/print?clazz=${filterForm.clazzNo}`, '_blank')
}

async function loadExperiments() {
  try {
    const res = await listEduExperimentVoByPage({ current: 1, pageSize: 100 } as any)
    const page = res.data?.data
    experiments.value = page?.records
      ? page.records.map((exp: any) => ({ id: String(exp.id), experimentNo: exp.sortOrder ?? exp.experimentNo ?? 0, name: exp.name }))
      : []
  } catch (error) {
    console.error('加载实验列表失败:', error)
    experiments.value = []
  }
}

async function loadClasses() {
  if (!filterForm.experimentId) {
    classes.value = []
    return
  }
  try {
    const res = await getExperimentClasses(filterForm.experimentId)
    classes.value = res.data?.data ? res.data.data.map((no: string) => ({ no })) : []
  } catch (error) {
    console.error('加载班级列表失败:', error)
    classes.value = []
  }
}

async function loadReports() {
  loading.value = true
  try {
    const res = await getTeacherReportListPage({
      experimentId: filterForm.experimentId || undefined,
      clazzNo: filterForm.clazzNo || undefined,
      current: paginationConfig.current,
      pageSize: paginationConfig.pageSize
    })
    const page = res.data?.data
    if (page?.records) {
      const submittedReports = page.records.filter((report: any) => ['submitted', 'reviewed'].includes(report.status))
      reportList.value = submittedReports.map((report: any) => ({
        id: report.id ?? `${report.experimentId}-${report.studentId}`,
        experimentId: String(report.experimentId ?? ''),
        studentId: String(report.studentId ?? ''),
        studentNo: report.studentNo ?? '-',
        studentName: report.studentName ?? '-',
        clazzNo: report.clazzNo ?? '-',
        status: report.status ?? '',
        score: report.totalScore ?? null
      }))
      paginationConfig.total = submittedReports.length
    } else {
      reportList.value = []
      paginationConfig.total = 0
    }
    saveFilters()
  } catch (error) {
    console.error('加载报告列表失败:', error)
    reportList.value = []
    paginationConfig.total = 0
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  restoreFilters()
  await loadExperiments()
  if (filterForm.experimentId && !experiments.value.some((item) => item.id === filterForm.experimentId)) {
    filterForm.experimentId = ''
    filterForm.clazzNo = ''
  }
  await loadClasses()
  await loadReports()
})
</script>

<style scoped>
.app-page-shell{padding:var(--space-5);min-height:100%}.breadcrumb-nav{margin-bottom:var(--space-5)}.page-header-card,.filter-card,.table-card{margin-bottom:var(--space-5)}.page-header{display:flex;justify-content:space-between;gap:var(--space-5)}.page-header__eyebrow{margin:0 0 8px;color:var(--color-primary);font-size:12px;font-weight:600;text-transform:uppercase}.page-header__title{margin:0;font-size:28px}.page-header__description{max-width:600px;margin:12px 0 0;color:var(--color-text-secondary)}.student-no,.student-name{color:var(--color-text-main)}.student-name{font-weight:500}.clazz-no,.score-value{color:var(--color-primary)}.score-value{font-weight:600}.score-pending{color:var(--color-text-tertiary)}.stats-section{margin-top:var(--space-5)}.stats-card{display:flex;gap:var(--space-5);padding:var(--space-4);background:var(--color-bg-muted);border-radius:8px}.stat-item{display:flex;flex-direction:column;gap:4px;min-width:100px}.stat-label{font-size:13px;color:var(--color-text-tertiary)}.stat-value{font-size:24px;font-weight:700}.stat-value.success{color:var(--color-success)}.stat-value.warning{color:var(--color-warning)}@media (max-width:768px){.app-page-shell{padding:var(--space-4)}.page-header{flex-direction:column}.stats-card{flex-wrap:wrap}}
</style>
