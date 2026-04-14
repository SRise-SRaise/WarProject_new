<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader
        eyebrow="用户管理"
        title="学生与教师名册"
        description="首波页面把用户和班级组织收回到 user 模块，由教师工作台统一跳转进入，避免后台菜单再维护一套单独配置。"
      />
    </section>

    <section class="app-kpi-grid">
      <MetricCard title="在册学生" :value="String(students.length)" description="当前纳入本轮首波管理视图的学生数量。" tone="primary" />
      <MetricCard title="协同教师" :value="String(teachers.length)" description="当前参与资料维护与教学协同的教师数量。" tone="accent" />
      <MetricCard title="重点跟进" :value="String(attentionCount)" description="根据活跃度与任务完成情况识别的关注对象。" tone="warning" />
      <MetricCard title="覆盖班级" :value="String(classes.length)" description="当前工作台已纳入的班级范围。" tone="success" />
    </section>

    <section class="app-surface-card app-section-card app-panel-grid">
      <div class="user-toolbar">
        <a-input v-model:value="keyword" allow-clear size="large" placeholder="搜索姓名、账号、方向或联系信息" />
        <a-select v-model:value="selectedClass" size="large" :options="classOptions" />
        <a-button size="large" @click="resetFilters">重置筛选</a-button>
      </div>

      <a-tabs v-model:activeKey="activeTab">
        <a-tab-pane key="students" tab="学生名册">
          <a-table :columns="studentColumns" :data-source="filteredStudents" :loading="loading" row-key="id" :pagination="{ pageSize: 5 }">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'progress'">
                <a-progress :percent="record.progress" size="small" />
              </template>
              <template v-else-if="column.key === 'pendingTasks'">
                <span class="app-inline-stat">{{ record.pendingTasks }} 项</span>
              </template>
              <template v-else-if="column.key === 'status'">
                <StatusTag :type="studentTone(record.status)" :label="studentStatusLabel(record.status)" />
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="teachers" tab="协同教师">
          <a-table :columns="teacherColumns" :data-source="filteredTeachers" :loading="loading" row-key="id" :pagination="false">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'classCoverage'">
                <span class="app-inline-stat">{{ record.classCoverage }} 个班</span>
              </template>
              <template v-else-if="column.key === 'openTasks'">
                <span class="app-inline-stat">{{ record.openTasks }} 项</span>
              </template>
              <template v-else-if="column.key === 'status'">
                <StatusTag :type="teacherTone(record.status)" :label="teacherStatusLabel(record.status)" />
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import MetricCard from '@/components/common/MetricCard.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { useUserAdminStore } from '@/stores/user/admin'

const userAdminStore = useUserAdminStore()
const { classes, loading, students, teachers } = storeToRefs(userAdminStore)

const keyword = ref('')
const selectedClass = ref('all')
const activeTab = ref('students')

const attentionCount = computed(() => students.value.filter((item) => item.status !== 'active').length)
const classOptions = computed(() => [
  { label: '全部班级', value: 'all' },
  ...classes.value.map((item) => ({ label: item.name, value: item.name }))
])

const filteredStudents = computed(() => {
  const term = keyword.value.trim().toLowerCase()
  return students.value.filter((item) => {
    const matchesKeyword =
      term.length === 0 ||
      item.name.toLowerCase().includes(term) ||
      item.account.toLowerCase().includes(term) ||
      item.email.toLowerCase().includes(term)
    const matchesClass = selectedClass.value === 'all' || item.className === selectedClass.value
    return matchesKeyword && matchesClass
  })
})

const filteredTeachers = computed(() => {
  const term = keyword.value.trim().toLowerCase()
  return teachers.value.filter((item) => {
    return (
      term.length === 0 ||
      item.name.toLowerCase().includes(term) ||
      item.account.toLowerCase().includes(term) ||
      item.topicLabel.toLowerCase().includes(term) ||
      item.contact.toLowerCase().includes(term)
    )
  })
})

const studentColumns = [
  { title: '学生', dataIndex: 'name', key: 'name' },
  { title: '班级', dataIndex: 'className', key: 'className' },
  { title: '专业', dataIndex: 'major', key: 'major' },
  { title: '完成率', dataIndex: 'progress', key: 'progress' },
  { title: '待办', dataIndex: 'pendingTasks', key: 'pendingTasks' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '最近活跃', dataIndex: 'lastActive', key: 'lastActive' }
]

const teacherColumns = [
  { title: '教师', dataIndex: 'name', key: 'name' },
  { title: '负责方向', dataIndex: 'topicLabel', key: 'topicLabel' },
  { title: '覆盖班级', dataIndex: 'classCoverage', key: 'classCoverage' },
  { title: '待处理事项', dataIndex: 'openTasks', key: 'openTasks' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '联系信息', dataIndex: 'contact', key: 'contact' }
]

function resetFilters(): void {
  keyword.value = ''
  selectedClass.value = 'all'
}

function studentTone(status: 'active' | 'attention' | 'inactive'): 'success' | 'warning' | 'default' {
  if (status === 'active') {
    return 'success'
  }
  if (status === 'attention') {
    return 'warning'
  }
  return 'default'
}

function studentStatusLabel(status: 'active' | 'attention' | 'inactive'): string {
  if (status === 'active') {
    return '稳定'
  }
  if (status === 'attention') {
    return '需关注'
  }
  return '低活跃'
}

function teacherTone(status: 'active' | 'support' | 'leave'): 'success' | 'processing' | 'default' {
  if (status === 'active') {
    return 'success'
  }
  if (status === 'support') {
    return 'processing'
  }
  return 'default'
}

function teacherStatusLabel(status: 'active' | 'support' | 'leave'): string {
  if (status === 'active') {
    return '在岗'
  }
  if (status === 'support') {
    return '协同'
  }
  return '休整'
}

onMounted(async () => {
  await userAdminStore.ensureReady()
})
</script>

<style scoped>
.user-toolbar {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) 220px auto;
  gap: 12px;
}

@media (max-width: 960px) {
  .user-toolbar {
    grid-template-columns: 1fr;
  }
}
</style>
