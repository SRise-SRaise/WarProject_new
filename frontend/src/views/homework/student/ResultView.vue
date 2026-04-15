<template>
  <div class="app-page-shell app-panel-grid" v-if="scoreData">
    <div class="hw-page-header">
      <div class="hw-page-header__left">
        <h1 class="hw-page-header__title">{{ scoreData.title }} · 成绩反馈</h1>
        <p class="hw-page-header__desc">{{ scoreData.summary }}</p>
      </div>
      <div class="hw-page-header__actions">
        <a-button @click="router.push(`/homework/${scoreData.id}/do`)">回到做作业</a-button>
        <a-button type="primary" @click="router.push('/homework')">返回作业列表</a-button>
      </div>
    </div>

    <HomeworkMetaGrid
      :topicLabel="scoreData.topicLabel"
      :teacher="scoreData.teacher"
      :open-time="scoreData.openTime"
      :deadline="scoreData.deadline"
    />

    <section class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <h2 style="margin:0 0 16px;font-size:16px;font-weight:700;color:var(--color-text-main)">成绩摘要</h2>
        <div class="score-grid">
          <span class="app-inline-stat">状态：{{ scoreData.status }}</span>
          <span class="app-inline-stat">总分：{{ scoreData.score }}</span>
          <span class="app-inline-stat">提交时间：{{ scoreData.submittedAt }}</span>
          <span class="app-inline-stat">批阅时间：{{ scoreData.reviewedAt }}</span>
        </div>
        <a-alert v-if="scoreData.feedback" type="info" :message="`教师反馈：${scoreData.feedback}`" show-icon style="margin-top:16px" />
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <h2 style="margin:0 0 16px;font-size:16px;font-weight:700;color:var(--color-text-main)">评分明细</h2>
        <div class="app-list">
          <article v-for="item in scoreData.breakdown" :key="item.name" class="app-list-card">
            <div class="breakdown-row">
              <h3 class="app-list-card__title">{{ item.name }}</h3>
              <span class="app-inline-stat">{{ item.score }}</span>
            </div>
            <p class="app-list-card__meta">{{ item.comment }}</p>
          </article>
        </div>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import HomeworkMetaGrid from '@/components/homework/HomeworkMetaGrid.vue'

interface ScoreBreakdownItem {
  name: string
  score: string
  comment: string
}

interface HomeworkScoreItem {
  id: string
  title: string
  topicLabel: string
  teacher: string
  openTime: string
  deadline: string
  summary: string
  status: string
  score: string
  submittedAt: string
  reviewedAt: string
  feedback: string
  breakdown: ScoreBreakdownItem[]
}

const scoreMock: HomeworkScoreItem[] = [
  {
    id: 'hw-101',
    title: '需求分析作业一：角色旅程拆解',
    topicLabel: '需求分析专题',
    teacher: '周老师',
    openTime: '2026-04-14 08:00',
    deadline: '2026-04-20 20:00',
    summary: '围绕教学平台案例完成角色旅程和验收边界分析。',
    status: '已批阅',
    score: '89 分',
    submittedAt: '2026-04-18 21:32',
    reviewedAt: '2026-04-19 10:05',
    feedback: '角色旅程主链路完整，异常流描述还可增加触发条件。',
    breakdown: [
      { name: '选择题', score: '27 / 30', comment: '基础概念掌握较好。' },
      { name: '填空题', score: '18 / 20', comment: '术语准确，个别细节可再规范。' },
      { name: '简答题', score: '44 / 50', comment: '分析结构清晰，建议补充验收失败示例。' }
    ]
  }
]

const route = useRoute()
const router = useRouter()
const scoreData = computed(() => scoreMock.find((item) => item.id === String(route.params.id)) ?? scoreMock[0])
</script>

<style scoped>
.score-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.breakdown-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
</style>