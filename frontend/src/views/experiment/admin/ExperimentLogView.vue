<template>
  <div class="experiment-log-page">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="header-info">
        <h1 class="page-title">日志监控</h1>
        <p class="page-subtitle">查看学生实验操作日志，检测异常答题行为</p>
      </div>
    </header>

    <!-- 筛选栏 -->
    <section class="filter-section app-surface-card">
      <a-form layout="inline" :model="filterForm">
        <a-form-item label="选择实验">
          <a-select
            v-model:value="filterForm.experimentId"
            placeholder="请选择实验"
            style="width: 280px"
            :loading="experimentsLoading"
            @change="handleExperimentChange"
          >
            <a-select-option v-for="exp in experiments" :key="exp.id" :value="exp.id">
              {{ exp.title }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="筛选班级">
          <a-select
            v-model:value="filterForm.clazzNo"
            placeholder="全部班级"
            style="width: 180px"
            allow-clear
            :disabled="!filterForm.experimentId"
          >
            <a-select-option v-for="clazz in classes" :key="clazz" :value="clazz">
              {{ clazz }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="风险筛选">
          <a-select
            v-model:value="filterForm.riskLevel"
            placeholder="全部风险"
            style="width: 140px"
            allow-clear
          >
            <a-select-option value="high">高风险</a-select-option>
            <a-select-option value="medium">中风险</a-select-option>
            <a-select-option value="low">低风险</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" :loading="loading" @click="loadData">
            <SearchOutlined />
            查询
          </a-button>
        </a-form-item>
      </a-form>
    </section>

    <!-- 统计概览 -->
    <section v-if="statistics" class="statistics-section">
      <div class="stat-card">
        <div class="stat-value">{{ statistics.total }}</div>
        <div class="stat-label">学生总数</div>
      </div>
      <div class="stat-card stat-card--high">
        <div class="stat-value">{{ statistics.highRisk }}</div>
        <div class="stat-label">高风险</div>
      </div>
      <div class="stat-card stat-card--medium">
        <div class="stat-value">{{ statistics.mediumRisk }}</div>
        <div class="stat-label">中风险</div>
      </div>
      <div class="stat-card stat-card--low">
        <div class="stat-value">{{ statistics.lowRisk }}</div>
        <div class="stat-label">低风险</div>
      </div>
    </section>

    <!-- 学生列表 -->
    <section class="list-section app-surface-card">
      <a-spin :spinning="loading">
        <a-table
          :columns="columns"
          :data-source="riskList"
          :pagination="pagination"
          row-key="account"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'studentInfo'">
              <div class="student-info">
                <span class="student-name">{{ record.studentName || record.account }}</span>
                <span class="student-account">{{ record.account }}</span>
              </div>
            </template>
            <template v-else-if="column.key === 'riskLevel'">
              <a-tag :color="getRiskColor(record.riskLevel)">
                {{ getRiskLabel(record.riskLevel) }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'totalDuration'">
              {{ formatDuration(record.totalDuration) }}
            </template>
            <template v-else-if="column.key === 'avgInterval'">
              {{ record.avgInterval }}秒
            </template>
            <template v-else-if="column.key === 'riskReasons'">
              <div class="risk-reasons">
                <span v-for="(reason, idx) in record.riskReasons?.slice(0, 2)" :key="idx" class="reason-tag">
                  {{ reason }}
                </span>
                <span v-if="record.riskReasons?.length > 2" class="reason-more">
                  +{{ record.riskReasons.length - 2 }}
                </span>
              </div>
            </template>
            <template v-else-if="column.key === 'action'">
              <a-button type="link" size="small" @click="viewDetail(record)">
                <EyeOutlined />
                查看详情
              </a-button>
            </template>
          </template>
        </a-table>
      </a-spin>
    </section>

    <!-- 详情抽屉 -->
    <a-drawer
      v-model:open="detailDrawerVisible"
      :title="`操作日志详情 - ${currentStudent?.studentName || currentStudent?.account}`"
      width="720"
      placement="right"
    >
      <template v-if="detailLoading">
        <div class="detail-loading">
          <a-spin size="large" />
        </div>
      </template>
      <template v-else-if="logDetail">
        <!-- 风险摘要 -->
        <div class="detail-summary">
          <div class="summary-header">
            <a-tag :color="getRiskColor(logDetail.riskLevel)" size="large">
              {{ getRiskLabel(logDetail.riskLevel) }}
            </a-tag>
            <span class="summary-account">{{ logDetail.account }}</span>
          </div>
          <div class="summary-stats">
            <div class="summary-stat">
              <span class="stat-label">总用时</span>
              <span class="stat-value">{{ formatDuration(logDetail.totalDuration) }}</span>
            </div>
            <div class="summary-stat">
              <span class="stat-label">答题数</span>
              <span class="stat-value">{{ logDetail.questionCount }}题</span>
            </div>
            <div class="summary-stat">
              <span class="stat-label">平均间隔</span>
              <span class="stat-value">{{ logDetail.avgInterval }}秒</span>
            </div>
            <div class="summary-stat">
              <span class="stat-label">最短间隔</span>
              <span class="stat-value" :class="{ 'stat-value--warning': logDetail.minInterval < 10 }">
                {{ logDetail.minInterval }}秒
              </span>
            </div>
            <div class="summary-stat">
              <span class="stat-label">快速答题</span>
              <span class="stat-value" :class="{ 'stat-value--warning': logDetail.rapidAnswerCount >= 3 }">
                {{ logDetail.rapidAnswerCount }}次
              </span>
            </div>
            <div class="summary-stat">
              <span class="stat-label">粘贴次数</span>
              <span class="stat-value" :class="{ 'stat-value--warning': logDetail.pasteCount > 3 }">
                {{ logDetail.pasteCount }}次
              </span>
            </div>
          </div>
          <div class="summary-reasons">
            <span class="reasons-label">风险原因：</span>
            <a-tag v-for="(reason, idx) in logDetail.riskReasons" :key="idx" :color="getRiskColor(logDetail.riskLevel)">
              {{ reason }}
            </a-tag>
          </div>
        </div>

        <!-- 操作时间线 -->
        <div class="detail-timeline">
          <h3 class="timeline-title">操作时间线</h3>
          <a-timeline>
            <a-timeline-item
              v-for="(log, idx) in logDetail.logs"
              :key="idx"
              :color="getTimelineColor(log.actionType)"
            >
              <div class="timeline-item">
                <div class="timeline-header">
                  <span class="timeline-action" :class="`action-type-${log.actionType}`">
                    {{ log.actionName }}
                  </span>
                  <span class="timeline-time">{{ formatTime(log.opTime) }}</span>
                </div>
                <div v-if="log.questionName" class="timeline-question">
                  题目：{{ log.questionName }}
                </div>
                <div v-if="log.clientIp" class="timeline-ip">
                  IP: {{ log.clientIp }}
                </div>
              </div>
            </a-timeline-item>
          </a-timeline>
        </div>
      </template>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { SearchOutlined, EyeOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { useExperimentAdminStore } from '@/stores/experiment/admin'
import { experimentRepository } from '@/stores/experiment/repository'
import { storeToRefs } from 'pinia'

const adminStore = useExperimentAdminStore()
const { experiments } = storeToRefs(adminStore)

// 筛选表单
const filterForm = reactive({
  experimentId: '' as string,
  clazzNo: '' as string,
  riskLevel: '' as string
})

// 状态
const loading = ref(false)
const experimentsLoading = ref(false)
const detailLoading = ref(false)
const detailDrawerVisible = ref(false)

// 数据
const classes = ref<string[]>([])
const riskList = ref<any[]>([])
const currentStudent = ref<any>(null)
const logDetail = ref<any>(null)

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`
})

// 统计
const statistics = computed(() => {
  if (riskList.value.length === 0) return null
  return {
    total: riskList.value.length,
    highRisk: riskList.value.filter(r => r.riskLevel === 'high').length,
    mediumRisk: riskList.value.filter(r => r.riskLevel === 'medium').length,
    lowRisk: riskList.value.filter(r => r.riskLevel === 'low').length
  }
})

// 表格列配置
const columns = [
  { title: '学生', key: 'studentInfo', width: 180 },
  { title: '班级', dataIndex: 'clazzNo', key: 'clazzNo', width: 120 },
  { title: '风险等级', key: 'riskLevel', width: 100, align: 'center' as const },
  { title: '总用时', key: 'totalDuration', width: 100, align: 'center' as const },
  { title: '答题数', dataIndex: 'questionCount', key: 'questionCount', width: 80, align: 'center' as const },
  { title: '平均间隔', key: 'avgInterval', width: 100, align: 'center' as const },
  { title: '风险原因', key: 'riskReasons', ellipsis: true },
  { title: '操作', key: 'action', width: 100, align: 'center' as const, fixed: 'right' as const }
]

// 方法
function getRiskColor(level: string): string {
  switch (level) {
    case 'high': return 'red'
    case 'medium': return 'orange'
    case 'low': return 'green'
    default: return 'default'
  }
}

function getRiskLabel(level: string): string {
  switch (level) {
    case 'high': return '高风险'
    case 'medium': return '中风险'
    case 'low': return '低风险'
    default: return '未知'
  }
}

function formatDuration(seconds: number): string {
  if (!seconds || seconds <= 0) return '—'
  if (seconds < 60) return `${seconds}秒`
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  if (minutes < 60) return `${minutes}分${secs}秒`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return `${hours}小时${mins}分`
}

function formatTime(dateStr: string): string {
  if (!dateStr) return '—'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

function getTimelineColor(actionType: number): string {
  switch (actionType) {
    case 100: return 'green'   // 进入实验
    case 103: return 'blue'    // 提交实验
    case 102: return 'gray'    // 保存答案
    case 106:
    case 107: return 'orange'  // 复制/粘贴
    case 104:
    case 108: return 'red'     // 离开/失焦
    default: return 'gray'
  }
}

async function handleExperimentChange() {
  filterForm.clazzNo = ''
  classes.value = []
  riskList.value = []
  
  if (filterForm.experimentId) {
    // 加载班级列表
    try {
      classes.value = await experimentRepository.getExperimentClasses(filterForm.experimentId)
    } catch (e) {
      console.error('加载班级失败:', e)
    }
  }
}

async function loadData() {
  if (!filterForm.experimentId) {
    message.warning('请先选择实验')
    return
  }

  loading.value = true
  try {
    const response = await fetch(
      `/api/experiment/log/risk/list?experimentId=${filterForm.experimentId}` +
      `&clazzNo=${filterForm.clazzNo || ''}` +
      `&current=${pagination.current}` +
      `&pageSize=${pagination.pageSize}`,
      { credentials: 'include' }
    )
    const data = await response.json()
    
    if (data.code === 0 && data.data) {
      let list = data.data.records || []
      // 前端过滤风险等级
      if (filterForm.riskLevel) {
        list = list.filter((r: any) => r.riskLevel === filterForm.riskLevel)
      }
      riskList.value = list
      pagination.total = data.data.total || list.length
    } else {
      message.error(data.message || '加载失败')
    }
  } catch (e) {
    console.error('加载风险列表失败:', e)
    message.error('加载失败')
  } finally {
    loading.value = false
  }
}

function handleTableChange(pag: any) {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadData()
}

async function viewDetail(record: any) {
  currentStudent.value = record
  detailDrawerVisible.value = true
  detailLoading.value = true
  
  try {
    const response = await fetch(
      `/api/experiment/log/detail?experimentId=${filterForm.experimentId}&account=${encodeURIComponent(record.account)}`,
      { credentials: 'include' }
    )
    const data = await response.json()
    
    if (data.code === 0 && data.data) {
      logDetail.value = data.data
    } else {
      message.error(data.message || '加载详情失败')
    }
  } catch (e) {
    console.error('加载日志详情失败:', e)
    message.error('加载详情失败')
  } finally {
    detailLoading.value = false
  }
}

onMounted(async () => {
  experimentsLoading.value = true
  try {
    await adminStore.ensureLoaded()
  } finally {
    experimentsLoading.value = false
  }
})
</script>

<style scoped>
.experiment-log-page {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text-primary, #1f2937);
  margin: 0 0 4px 0;
}

.page-subtitle {
  font-size: 14px;
  color: var(--color-text-secondary, #6b7280);
  margin: 0;
}

.filter-section {
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 12px;
}

.statistics-section {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  flex: 1;
  background: var(--color-bg-surface, #fff);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  border: 1px solid var(--color-border, #e5e7eb);
}

.stat-card--high {
  border-color: #fecaca;
  background: #fef2f2;
}

.stat-card--medium {
  border-color: #fed7aa;
  background: #fffbeb;
}

.stat-card--low {
  border-color: #bbf7d0;
  background: #f0fdf4;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-text-primary, #1f2937);
}

.stat-card--high .stat-value { color: #dc2626; }
.stat-card--medium .stat-value { color: #ea580c; }
.stat-card--low .stat-value { color: #16a34a; }

.stat-label {
  font-size: 14px;
  color: var(--color-text-secondary, #6b7280);
  margin-top: 4px;
}

.list-section {
  padding: 20px;
  border-radius: 12px;
}

.student-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.student-name {
  font-weight: 500;
  color: var(--color-text-primary, #1f2937);
}

.student-account {
  font-size: 12px;
  color: var(--color-text-tertiary, #9ca3af);
}

.risk-reasons {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.reason-tag {
  font-size: 12px;
  padding: 2px 8px;
  background: var(--color-bg-muted, #f3f4f6);
  border-radius: 4px;
  color: var(--color-text-secondary, #6b7280);
}

.reason-more {
  font-size: 12px;
  color: var(--color-text-tertiary, #9ca3af);
}

/* 详情抽屉样式 */
.detail-loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.detail-summary {
  background: var(--color-bg-muted, #f9fafb);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
}

.summary-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.summary-account {
  font-size: 16px;
  font-weight: 500;
  color: var(--color-text-primary, #1f2937);
}

.summary-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.summary-stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.summary-stat .stat-label {
  font-size: 12px;
  color: var(--color-text-tertiary, #9ca3af);
}

.summary-stat .stat-value {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary, #1f2937);
}

.summary-stat .stat-value--warning {
  color: #dc2626;
}

.summary-reasons {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.reasons-label {
  font-size: 14px;
  color: var(--color-text-secondary, #6b7280);
}

.detail-timeline {
  padding: 0 4px;
}

.timeline-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary, #1f2937);
  margin: 0 0 16px 0;
}

.timeline-item {
  padding: 4px 0;
}

.timeline-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.timeline-action {
  font-weight: 500;
  color: var(--color-text-primary, #1f2937);
}

.action-type-100 { color: #16a34a; }
.action-type-103 { color: #2563eb; }
.action-type-106,
.action-type-107 { color: #ea580c; }
.action-type-104,
.action-type-108 { color: #dc2626; }

.timeline-time {
  font-size: 12px;
  color: var(--color-text-tertiary, #9ca3af);
}

.timeline-question {
  font-size: 13px;
  color: var(--color-text-secondary, #6b7280);
  margin-top: 4px;
}

.timeline-ip {
  font-size: 12px;
  color: var(--color-text-tertiary, #9ca3af);
  margin-top: 2px;
}
</style>
