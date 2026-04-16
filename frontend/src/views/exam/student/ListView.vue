<template>
  <div class="student-exam-page">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="header-content">
        <h1 class="page-title">我的考试</h1>
        <p class="page-desc">查看已发布的考试，点击进入开始答题</p>
      </div>
      <a-button @click="router.push('/learning')">返回学习概览</a-button>
    </header>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon blue">
          <FileTextOutlined />
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ publishedExams.length }}</span>
          <span class="stat-label">可参加考试</span>
        </div>
      </div>
    </div>

    <!-- 考试列表 -->
    <div class="exam-list-section">
      <div class="section-header">
        <h2 class="section-title">考试列表</h2>
      </div>

      <a-spin :spinning="loading">
        <div v-if="publishedExams.length === 0" class="empty-state">
          <InboxOutlined class="empty-icon" />
          <p class="empty-text">暂无可参加的考试</p>
        </div>

        <a-table
          v-else
          :dataSource="publishedExams"
          :columns="columns"
          :pagination="false"
          rowKey="id"
          class="exam-table"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'examName'">
              <div class="exam-name-cell">
                <span class="exam-name">{{ record.examName }}</span>
                <span v-if="record.paper" class="paper-name">试卷：{{ record.paper.paperName }}</span>
              </div>
            </template>
            <template v-else-if="column.key === 'info'">
              <div class="exam-info-cell">
                <span v-if="record.paper" class="info-item">
                  <FileTextOutlined /> {{ record.paper.questionCount }} 题
                </span>
                <span v-if="record.paper" class="info-item">
                  <TrophyOutlined /> {{ record.paper.totalScore }} 分
                </span>
                <span v-if="record.durationMin" class="info-item">
                  <ClockCircleOutlined /> {{ record.durationMin }} 分钟
                </span>
              </div>
            </template>
            <template v-else-if="column.key === 'startTime'">
              <span v-if="record.startTime">{{ formatDateTime(record.startTime) }}</span>
              <span v-else class="text-muted">随时可考</span>
            </template>
            <template v-else-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record)">{{ getStatusText(record) }}</a-tag>
            </template>
            <template v-else-if="column.key === 'action'">
              <a-button type="primary" size="small" @click="enterExam(record)">
                进入考试
              </a-button>
            </template>
          </template>
        </a-table>
      </a-spin>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import { FileTextOutlined, ClockCircleOutlined, TrophyOutlined, InboxOutlined } from '@ant-design/icons-vue'
import { useExamStudentStore } from '@/stores/exam/student'
import type { Exam } from '@/stores/exam/types'

const router = useRouter()
const examStore = useExamStudentStore()
const { publishedExams, loading } = storeToRefs(examStore)

const columns = [
  { title: '考试名称', key: 'examName', width: '30%' },
  { title: '考试信息', key: 'info', width: '25%' },
  { title: '开始时间', key: 'startTime', width: '20%' },
  { title: '状态', key: 'status', width: '12%' },
  { title: '操作', key: 'action', width: '13%' },
]

function formatDateTime(dateStr: string): string {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function getStatusColor(exam: Exam): string {
  if (!exam.startTime) return 'green'
  const now = new Date()
  const start = new Date(exam.startTime)
  const end = new Date(start.getTime() + (exam.durationMin || 0) * 60 * 1000)
  
  if (now < start) return 'blue'
  if (now >= start && now <= end) return 'green'
  return 'default'
}

function getStatusText(exam: Exam): string {
  if (!exam.startTime) return '可开始'
  const now = new Date()
  const start = new Date(exam.startTime)
  const end = new Date(start.getTime() + (exam.durationMin || 0) * 60 * 1000)
  
  if (now < start) return '未开始'
  if (now >= start && now <= end) return '进行中'
  return '已结束'
}

function enterExam(exam: Exam): void {
  router.push(`/exams/${exam.id}/take`)
}

onMounted(async () => {
  await examStore.ensureLoaded()
})
</script>

<style scoped>
.student-exam-page {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px 0;
}

.page-desc {
  color: #666;
  margin: 0;
}

.stats-row {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 24px;
}

.stat-icon.blue {
  background: #e6f4ff;
  color: #1677ff;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.exam-list-section {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 20px;
}

.section-header {
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.exam-table :deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
}

.exam-name-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.exam-name {
  font-weight: 500;
  color: #1a1a1a;
}

.paper-name {
  font-size: 12px;
  color: #999;
}

.exam-info-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.info-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #666;
}

.text-muted {
  color: #999;
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  color: #d9d9d9;
}

.empty-text {
  margin-top: 16px;
  color: #999;
}
</style>
