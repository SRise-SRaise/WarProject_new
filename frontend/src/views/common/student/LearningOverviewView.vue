<template>
  <div class="app-page-shell app-panel-grid">
    <section class="overview-hero app-surface-card app-section-card">
      <SectionHeader
        eyebrow="学生学习层"
        :title="`${authStore.session?.name ?? '同学'}，${studentOverview.headline}`"
        :description="studentOverview.summary"
      >
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="router.push('/materials')">查看资料</a-button>
            <a-button type="primary" @click="router.push('/profile')">更新个人中心</a-button>
          </a-space>
        </template>
      </SectionHeader>
    </section>

    <section class="app-kpi-grid">
      <MetricCard
        v-for="item in studentOverview.metrics"
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
          <SectionHeader
            eyebrow="待完成事项"
            title="今天先做什么"
            description="首波页面先把资料域打通，其余流程型模块先以稳定的占位提醒呈现。"
            tight
          />

          <div class="app-list">
            <article v-for="task in studentOverview.tasks" :key="task.id" class="app-list-card overview-task-card">
              <div class="overview-task-card__head">
                <div>
                  <h3 class="app-list-card__title">{{ task.title }}</h3>
                  <p class="app-list-card__meta">{{ task.moduleLabel }} · {{ task.topicLabel }} · {{ task.dueLabel }}</p>
                </div>
                <StatusTag :type="statusTone(task.status)" :label="task.progress" />
              </div>
              <div class="overview-task-card__footer">
                <span class="app-inline-stat">{{ task.progress }}</span>
                <a-button type="link" @click="openTask(task.path, task.available)">
                  {{ task.available ? '进入' : '查看规划' }}
                </a-button>
              </div>
            </article>
          </div>
        </section>

        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="最近结果" title="最近一次反馈" description="把测验成绩、资料确认和教师反馈收束到同一个列表里。" tight />
          <div class="app-list">
            <article v-for="result in studentOverview.results" :key="result.id" class="app-list-card">
              <h3 class="app-list-card__title">{{ result.title }}</h3>
              <p class="app-list-card__meta">{{ result.moduleLabel }} · {{ result.feedback }}</p>
              <div class="overview-result__footer">
                <span class="app-inline-stat">{{ result.score }}</span>
                <span class="app-subtle-text">{{ result.updatedAt }}</span>
              </div>
            </article>
          </div>
        </section>
      </div>

      <div class="app-panel-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="快捷入口" title="从这里继续前进" description="首波开放的页面直接可进，未开放的流程模块保留统一提示。" tight />
          <div class="app-list">
            <article v-for="link in studentOverview.quickLinks" :key="link.title" class="app-list-card overview-link-card">
              <div>
                <h3 class="app-list-card__title">{{ link.title }}</h3>
                <p class="app-list-card__meta">{{ link.description }}</p>
              </div>
              <div class="overview-link-card__footer">
                <a-tag :color="link.available ? 'blue' : 'default'">{{ link.badge }}</a-tag>
                <a-button type="link" @click="openTask(link.path, link.available)">
                  {{ link.available ? '立即进入' : '稍后开放' }}
                </a-button>
              </div>
            </article>
          </div>
        </section>

        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="提醒" title="系统提示" description="账户安全和资料更新会优先在这里展示。" tight />
          <div class="app-list">
            <article v-for="notice in studentOverview.notices" :key="notice.title" class="app-list-card">
              <h3 class="app-list-card__title">{{ notice.title }}</h3>
              <p class="app-list-card__meta">{{ notice.detail }}</p>
              <span class="app-inline-stat">{{ notice.time }}</span>
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
import { useAuthStore } from '@/stores/user/auth'

const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()
const { studentOverview } = storeToRefs(appStore)

function statusTone(status: 'urgent' | 'processing' | 'planned'): 'warning' | 'processing' | 'default' {
  if (status === 'urgent') {
    return 'warning'
  }
  if (status === 'processing') {
    return 'processing'
  }
  return 'default'
}

function openTask(path: string | undefined, available: boolean): void {
  if (!available || !path) {
    message.info('该流程模块将在下一波继续接入。')
    return
  }

  router.push(path)
}

onMounted(async () => {
  await appStore.ensureReady()
})
</script>

<style scoped>
.overview-hero {
  padding-top: 30px;
}

.overview-task-card__head,
.overview-link-card__footer,
.overview-task-card__footer,
.overview-result__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.overview-task-card__footer,
.overview-link-card__footer,
.overview-result__footer {
  margin-top: 16px;
}

@media (max-width: 720px) {
  .overview-task-card__head,
  .overview-link-card__footer,
  .overview-task-card__footer,
  .overview-result__footer {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
