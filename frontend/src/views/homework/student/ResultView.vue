<template>
  <div class="app-page-shell app-panel-grid" v-if="scoreData">
    <section class="app-surface-card app-section-card app-panel-grid">
      <SectionHeader eyebrow="作业成绩" :title="`${scoreData.title} · 成绩反馈`" :description="scoreData.summary">
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="router.push(`/homework/${scoreData.id}/do`)">回到做作业</a-button>
            <a-button type="primary" @click="router.push('/homework')">返回作业列表</a-button>
          </a-space>
        </template>
      </SectionHeader>
      <HomeworkMetaGrid
        :topicLabel="scoreData.topicLabel"
        :teacher="scoreData.teacher"
        :open-time="scoreData.openTime"
        :deadline="scoreData.deadline"
      />
    </section>

    <section class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="成绩摘要" title="本次作业成绩" description="当前为页面 Mock 数据，后续将由真实成绩接口替换。" tight />
        <div class="score-grid">
          <span class="app-inline-stat">状态：{{ scoreData.status }}</span>
          <span class="app-inline-stat">总分：{{ scoreData.score }}</span>
          <span class="app-inline-stat">提交时间：{{ scoreData.submittedAt }}</span>
          <span class="app-inline-stat">批阅时间：{{ scoreData.reviewedAt }}</span>
        </div>
        <article class="app-list-card">
          <h3 class="app-list-card__title">教师反馈</h3>
          <p class="app-list-card__meta">{{ scoreData.feedback }}</p>
        </article>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="评分明细" title="题目得分构成" description="按题型展示当前得分构成，方便学生定位改进点。" tight />
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
import SectionHeader from '@/components/common/SectionHeader.vue'
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

// 作业模块Mock数据占位符，后续需替换到真实后端接口：GET /s_excercise_score.do
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
