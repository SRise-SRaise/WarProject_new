<template>
  <div class="app-page-shell app-panel-grid">
    <section v-if="currentHomework" class="app-surface-card app-section-card app-panel-grid">
      <SectionHeader eyebrow="作业结果" :title="`${currentHomework.title} · 结果反馈`" :description="currentHomework.summary">
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="router.push(`/homework/${currentHomework.id}`)">回到详情</a-button>
            <a-button type="primary" @click="router.push('/homework')">查看其他作业</a-button>
          </a-space>
        </template>
      </SectionHeader>
      <HomeworkMetaGrid
        :topicLabel="currentHomework.topicLabel"
        :teacher="currentHomework.teacher"
        :open-time="currentHomework.openTime"
        :deadline="currentHomework.deadline"
      />
    </section>

    <section v-if="currentHomework" class="app-split-grid">
      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="提交记录" title="我的提交" description="这里聚合当前提交状态、附件信息和最近更新时间。" tight />
        <div class="app-list-card">
          <h3 class="app-list-card__title">{{ currentHomework.submission.fileName || '未提供附件名称' }}</h3>
          <p class="app-list-card__meta">{{ currentHomework.submission.content }}</p>
          <div class="result-grid">
            <span class="app-inline-stat">状态：{{ submissionLabel }}</span>
            <span class="app-inline-stat">更新时间：{{ currentHomework.submission.updatedAt }}</span>
            <span class="app-inline-stat">得分：{{ currentHomework.submission.score ?? '待批阅' }}</span>
          </div>
        </div>
      </section>

      <section class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader eyebrow="教师反馈" title="批阅意见" description="批阅完成后，结果页承担学生查看得分与反馈的唯一入口。" tight />
        <div class="app-list">
          <article v-for="item in currentHomework.submission.highlights" :key="item" class="app-list-card">
            <p class="app-list-card__meta">{{ item }}</p>
          </article>
          <article class="app-list-card">
            <h3 class="app-list-card__title">教师说明</h3>
            <p class="app-list-card__meta">{{ currentHomework.submission.teacherFeedback ?? '教师尚未填写反馈，结果会在批阅后更新。' }}</p>
          </article>
        </div>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import HomeworkMetaGrid from '@/components/homework/HomeworkMetaGrid.vue'
import { useHomeworkStudentStore } from '@/stores/homework/student'

const route = useRoute()
const router = useRouter()
const homeworkStore = useHomeworkStudentStore()
const { currentHomework } = storeToRefs(homeworkStore)

const submissionLabel = computed(() => {
  const status = currentHomework.value?.submission.status
  if (status === 'reviewed') return '已批阅'
  if (status === 'submitted') return '已提交'
  if (status === 'late') return '逾期提交'
  return '草稿'
})

onMounted(async () => {
  await homeworkStore.selectHomework(String(route.params.id))
})
</script>

<style scoped>
.result-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}
</style>
