<template>
  <div class="app-panel-grid">
    <section class="app-surface-card app-section-card">
      <SectionHeader
        eyebrow="教师管理层"
        :title="teacherDashboard.headline"
        :description="teacherDashboard.summary"
      >
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="router.push('/admin/users')">查看用户管理</a-button>
            <a-button type="primary" @click="router.push('/admin/classes')">查看班级管理</a-button>
          </a-space>
        </template>
      </SectionHeader>
    </section>

    <section class="app-kpi-grid">
      <MetricCard
        v-for="item in teacherDashboard.metrics"
        :key="item.label"
        :title="item.label"
        :value="item.value"
        :description="item.description"
        :trend="item.trend"
        :tone="item.tone"
      />
    </section>

    <section class="app-split-grid">
      <div class="app-panel-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="待处理队列" title="今天的教学重点" description="工作台只做汇总和分发，不把所有管理动作都堆到首页。" tight />
          <div class="app-list">
            <article v-for="item in teacherDashboard.queue" :key="item.id" class="app-list-card dashboard-queue-card">
              <div>
                <h3 class="app-list-card__title">{{ item.title }}</h3>
                <p class="app-list-card__meta">{{ item.meta }}</p>
              </div>
              <div class="dashboard-queue-card__footer">
                <StatusTag :type="queueTone(item.status)" :label="queueLabel(item.status)" />
                <a-button type="link" @click="openQuickLink(item.path)">{{ item.linkLabel }}</a-button>
              </div>
            </article>
          </div>
        </section>

        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="本周排期" title="近期教学节奏" description="帮助教师快速掌握本周重要节点与课堂安排。" tight />
          <a-timeline>
            <a-timeline-item v-for="item in teacherDashboard.schedule" :key="item.id">
              <h3 class="dashboard-schedule__title">{{ item.time }} · {{ item.title }}</h3>
              <p class="dashboard-schedule__detail">{{ item.detail }}</p>
            </a-timeline-item>
          </a-timeline>
        </section>
      </div>

      <div class="app-panel-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="快捷入口" title="立即处理" description="直接跳到已开放的后台页面，其余能力等待下一波扩展。" tight />
          <div class="app-list">
            <article v-for="item in teacherDashboard.quickLinks" :key="item.title" class="app-list-card dashboard-link-card">
              <div>
                <h3 class="app-list-card__title">{{ item.title }}</h3>
                <p class="app-list-card__meta">{{ item.description }}</p>
              </div>
              <div class="dashboard-link-card__footer">
                <a-tag :color="item.available ? 'blue' : 'default'">{{ item.badge }}</a-tag>
                <a-button type="link" @click="openQuickLink(item.path, item.available)">
                  {{ item.available ? '进入页面' : '稍后开放' }}
                </a-button>
              </div>
            </article>
          </div>
        </section>

        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="更新提醒" title="最近变化" description="资料、班级和协同提醒统一收束在工作台右侧。" tight />
          <div class="app-list">
            <article v-for="item in teacherDashboard.updates" :key="item.title" class="app-list-card">
              <h3 class="app-list-card__title">{{ item.title }}</h3>
              <p class="app-list-card__meta">{{ item.detail }}</p>
              <span class="app-inline-stat">{{ item.time }}</span>
            </article>
          </div>
        </section>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import MetricCard from '@/components/common/MetricCard.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { useAppStore } from '@/stores/common/app'

const router = useRouter()
const appStore = useAppStore()
const { teacherDashboard } = storeToRefs(appStore)

function queueTone(status: 'urgent' | 'processing' | 'planned'): 'warning' | 'processing' | 'default' {
  if (status === 'urgent') {
    return 'warning'
  }
  if (status === 'processing') {
    return 'processing'
  }
  return 'default'
}

function queueLabel(status: 'urgent' | 'processing' | 'planned'): string {
  if (status === 'urgent') {
    return '优先处理'
  }
  if (status === 'processing') {
    return '处理中'
  }
  return '待排期'
}

function openQuickLink(path?: string, available = true): void {
  if (!available || !path) {
    message.info('该功能将在后续波次继续接入。')
    return
  }

  router.push(path)
}

onMounted(async () => {
  await appStore.ensureReady()
})
</script>

<style scoped>
.dashboard-queue-card,
.dashboard-link-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.dashboard-queue-card__footer,
.dashboard-link-card__footer {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dashboard-schedule__title {
  margin: 0 0 6px;
  font-size: 16px;
}

.dashboard-schedule__detail {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.75;
}

@media (max-width: 720px) {
  .dashboard-queue-card,
  .dashboard-link-card,
  .dashboard-queue-card__footer,
  .dashboard-link-card__footer {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
