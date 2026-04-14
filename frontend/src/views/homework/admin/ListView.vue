<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader eyebrow="作业管理" title="教师作业列表" description="作业后台页按发布状态组织编辑、发布、提交记录和批改入口。">
        <template #actions>
          <a-button type="primary" @click="router.push('/admin/homework/edit')">新建作业</a-button>
        </template>
      </SectionHeader>
    </section>

    <section class="app-kpi-grid">
      <MetricCard title="作业总数" :value="String(homeworks.length)" description="当前已创建的作业数量。" tone="primary" />
      <MetricCard title="已发布" :value="String(publishedCount)" description="学生可见且处于开放状态的作业。" tone="success" />
      <MetricCard title="待批阅" :value="String(reviewingCount)" description="已有提交但尚未全部完成批改。" tone="warning" />
      <MetricCard title="草稿" :value="String(draftCount)" description="还未进入发布阶段的作业。" tone="accent" />
    </section>

    <section class="homework-admin-grid">
      <article v-for="item in homeworks" :key="item.id" class="app-surface-card homework-admin-card">
        <div class="homework-admin-card__head">
          <div>
            <div class="homework-admin-card__tags">
              <a-tag v-for="tag in item.tags" :key="tag">{{ tag }}</a-tag>
            </div>
            <h2 class="homework-admin-card__title">{{ item.title }}</h2>
            <p class="app-list-card__meta">{{ item.topicLabel }} · {{ item.publishScope }} · 截止 {{ item.deadline }}</p>
          </div>
          <StatusTag :type="statusTone(item.status)" :label="statusLabel(item.status)" />
        </div>
        <p class="homework-admin-card__summary">{{ item.summary }}</p>
        <div class="homework-admin-card__stats">
          <span class="app-inline-stat">提交 {{ item.submissionCount }}</span>
          <span class="app-inline-stat">已批阅 {{ item.reviewedCount }}</span>
          <span class="app-inline-stat">更新 {{ item.updatedAt }}</span>
        </div>
        <a-space :size="10" wrap>
          <a-button @click="router.push(`/admin/homework/edit/${item.id}`)">编辑</a-button>
          <a-button @click="router.push(`/admin/homework/publish/${item.id}`)">发布</a-button>
          <a-button type="primary" @click="router.push(`/admin/homework/submissions/${item.id}`)">提交记录</a-button>
        </a-space>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import MetricCard from '@/components/common/MetricCard.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { useHomeworkAdminStore } from '@/stores/homework/admin'
import type { HomeworkAdminStatus } from '@/stores/homework/types'

const router = useRouter()
const homeworkStore = useHomeworkAdminStore()
const { homeworks } = storeToRefs(homeworkStore)

const publishedCount = computed(() => homeworks.value.filter((item) => item.status === 'published').length)
const reviewingCount = computed(() => homeworks.value.filter((item) => item.status === 'reviewing').length)
const draftCount = computed(() => homeworks.value.filter((item) => item.status === 'draft').length)

function statusTone(status: HomeworkAdminStatus): 'success' | 'processing' | 'warning' | 'default' {
  if (status === 'published') return 'success'
  if (status === 'reviewing') return 'processing'
  if (status === 'closed') return 'default'
  return 'warning'
}

function statusLabel(status: HomeworkAdminStatus): string {
  if (status === 'published') return '已发布'
  if (status === 'reviewing') return '批阅中'
  if (status === 'closed') return '已结束'
  return '草稿'
}

onMounted(async () => {
  await homeworkStore.ensureLoaded()
})
</script>

<style scoped>
.homework-admin-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: var(--space-5);
}

.homework-admin-card {
  padding: 24px;
}

.homework-admin-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.homework-admin-card__tags,
.homework-admin-card__stats {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.homework-admin-card__tags {
  margin-bottom: 14px;
}

.homework-admin-card__title {
  margin: 0;
  font-size: 24px;
}

.homework-admin-card__summary {
  margin: 18px 0;
  color: var(--color-text-secondary);
  line-height: 1.8;
}

.homework-admin-card__stats {
  margin-bottom: 18px;
}
</style>
