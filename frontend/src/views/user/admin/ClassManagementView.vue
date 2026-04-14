<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader
        eyebrow="班级管理"
        title="班级结构与进度概览"
        description="在教师后台里，班级管理是教学对象组织的基础能力。首波页面优先呈现班级规模、完课状态和需要跟进的焦点。"
      >
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="router.push('/admin/users')">查看用户名册</a-button>
            <a-button type="primary" @click="router.push('/admin/profile')">教师个人设置</a-button>
          </a-space>
        </template>
      </SectionHeader>
    </section>

    <section class="app-kpi-grid">
      <MetricCard title="班级数" :value="String(classes.length)" description="当前纳入首波后台页面的班级总数。" tone="primary" />
      <MetricCard title="学生总数" :value="String(totalStudents)" description="汇总所有班级的人数规模。" tone="accent" />
      <MetricCard title="平均完成率" :value="`${averageCompletion}%`" description="按班级完成率估算当前整体学习进度。" tone="success" />
      <MetricCard title="需要跟进" :value="String(riskClasses.length)" description="完成率或活跃度偏低，需要优先关注的班级。" tone="warning" />
    </section>

    <section class="app-surface-card app-section-card app-panel-grid">
      <div class="class-toolbar">
        <a-input v-model:value="keyword" allow-clear size="large" placeholder="搜索班级名称、专业、负责人或关注重点" />
        <span class="app-inline-stat">当前显示 {{ visibleClasses.length }} 个班级</span>
      </div>

      <div class="class-grid">
        <article v-for="item in visibleClasses" :key="item.id" class="class-card app-surface-card">
          <div class="class-card__head">
            <div>
              <h2 class="class-card__title">{{ item.name }}</h2>
              <p class="app-list-card__meta">{{ item.major }} · {{ item.term }} · {{ item.advisor }}</p>
            </div>
            <span class="app-inline-stat">{{ item.studentCount }} 人</span>
          </div>
          <p class="class-card__focus">当前重点：{{ item.focus }}</p>
          <a-progress :percent="item.completionRate" size="small" />
          <div class="class-card__footer">
            <span class="app-subtle-text">班长：{{ item.monitor }}</span>
            <StatusTag :type="item.completionRate >= 85 ? 'success' : item.completionRate >= 78 ? 'warning' : 'default'" :label="completionLabel(item.completionRate)" />
          </div>
        </article>
      </div>
    </section>

    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="班级清单" title="详细视图" description="保留一张清晰的表格，方便后续接真实发布范围、课堂分组和统计维度。" tight />
      <a-table :columns="classColumns" :data-source="visibleClasses" :loading="loading" row-key="id" :pagination="false" class="class-table">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'completionRate'">
            <a-progress :percent="record.completionRate" size="small" />
          </template>
        </template>
      </a-table>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import MetricCard from '@/components/common/MetricCard.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { useUserAdminStore } from '@/stores/user/admin'

const router = useRouter()
const userAdminStore = useUserAdminStore()
const { classes, loading } = storeToRefs(userAdminStore)
const keyword = ref('')

const totalStudents = computed(() => classes.value.reduce((sum, item) => sum + item.studentCount, 0))
const averageCompletion = computed(() => {
  if (classes.value.length === 0) {
    return 0
  }
  return Math.round(classes.value.reduce((sum, item) => sum + item.completionRate, 0) / classes.value.length)
})
const riskClasses = computed(() => classes.value.filter((item) => item.completionRate < 82))
const visibleClasses = computed(() => {
  const term = keyword.value.trim().toLowerCase()
  return classes.value.filter((item) => {
    return (
      term.length === 0 ||
      item.name.toLowerCase().includes(term) ||
      item.major.toLowerCase().includes(term) ||
      item.advisor.toLowerCase().includes(term) ||
      item.focus.toLowerCase().includes(term)
    )
  })
})

const classColumns = [
  { title: '班级', dataIndex: 'name', key: 'name' },
  { title: '专业', dataIndex: 'major', key: 'major' },
  { title: '学期', dataIndex: 'term', key: 'term' },
  { title: '负责人', dataIndex: 'advisor', key: 'advisor' },
  { title: '人数', dataIndex: 'studentCount', key: 'studentCount' },
  { title: '完成率', dataIndex: 'completionRate', key: 'completionRate' },
  { title: '当前重点', dataIndex: 'focus', key: 'focus' },
  { title: '班长', dataIndex: 'monitor', key: 'monitor' }
]

function completionLabel(percent: number): string {
  if (percent >= 85) {
    return '稳定推进'
  }
  if (percent >= 78) {
    return '需要跟进'
  }
  return '优先处理'
}

onMounted(async () => {
  await userAdminStore.ensureReady()
})
</script>

<style scoped>
.class-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.class-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: var(--space-5);
}

.class-card {
  padding: 22px;
}

.class-card__head,
.class-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.class-card__title {
  margin: 0;
  font-size: 22px;
}

.class-card__focus {
  margin: 18px 0 16px;
  color: var(--color-text-secondary);
  line-height: 1.75;
}

.class-card__footer {
  margin-top: 18px;
}

.class-table {
  margin-top: 18px;
}

@media (max-width: 960px) {
  .class-toolbar,
  .class-card__head,
  .class-card__footer {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
