<template>
  <div class="app-page-shell app-panel-grid">
    <section v-if="currentHomework" class="app-surface-card app-section-card app-panel-grid">
      <SectionHeader eyebrow="作业详情" :title="currentHomework.title" :description="currentHomework.summary">
        <template #actions>
          <a-space :size="12" wrap>
            <a-button @click="router.push('/homework')">返回作业列表</a-button>
            <a-button type="primary" @click="openPrimaryAction">{{ primaryActionLabel }}</a-button>
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
      <div class="app-panel-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="任务要求" title="提交说明" description="按下面的结构组织作业内容，避免提交前再反复补材料。" tight />
          <HomeworkInstructionList :items="currentHomework.requirementSections" />
        </section>

        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="提交提示" title="开始之前" description="这些提示会直接影响后续提交页的填写效率。" tight />
          <div class="app-list">
            <article v-for="tip in currentHomework.submitTips" :key="tip" class="app-list-card">
              <p class="app-list-card__meta">{{ tip }}</p>
            </article>
          </div>
        </section>
      </div>

      <div class="app-panel-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="参考资料" title="附件清单" description="建议先通读资料，再进入提交页。" tight />
          <div class="app-list">
            <article v-for="resource in currentHomework.resources" :key="resource.name" class="app-list-card">
              <h3 class="app-list-card__title">{{ resource.name }}</h3>
              <p class="app-list-card__meta">{{ resource.kind }} · {{ resource.size }}</p>
            </article>
          </div>
        </section>

        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="当前状态" title="提交进度" description="这里直接反映当前草稿或批阅状态。" tight />
          <div class="app-list-card">
            <h3 class="app-list-card__title">{{ submissionLabel }}</h3>
            <p class="app-list-card__meta">{{ currentHomework.submission.content }}</p>
            <div class="detail-state__footer">
              <StatusTag :type="submissionTone" :label="submissionLabel" />
              <span class="app-subtle-text">最近更新：{{ currentHomework.submission.updatedAt }}</span>
            </div>
          </div>
        </section>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import HomeworkInstructionList from '@/components/homework/HomeworkInstructionList.vue'
import HomeworkMetaGrid from '@/components/homework/HomeworkMetaGrid.vue'
import { useHomeworkStudentStore } from '@/stores/homework/student'

const route = useRoute()
const router = useRouter()
const homeworkStore = useHomeworkStudentStore()
const { currentHomework } = storeToRefs(homeworkStore)

const primaryActionLabel = computed(() => (currentHomework.value?.status === 'reviewed' || currentHomework.value?.status === 'submitted' ? '查看结果' : '去提交'))
const submissionLabel = computed(() => {
  const status = currentHomework.value?.submission.status
  if (status === 'reviewed') return '已批阅'
  if (status === 'submitted') return '已提交'
  if (status === 'late') return '逾期提交'
  return '草稿待提交'
})
const submissionTone = computed<'success' | 'processing' | 'warning'>(() => {
  const status = currentHomework.value?.submission.status
  if (status === 'reviewed') return 'success'
  if (status === 'submitted') return 'processing'
  return 'warning'
})

function openPrimaryAction(): void {
  if (!currentHomework.value) return
  if (currentHomework.value.status === 'submitted' || currentHomework.value.status === 'reviewed') {
    router.push(`/homework/${currentHomework.value.id}/result`)
    return
  }
  router.push(`/homework/${currentHomework.value.id}/submit`)
}

onMounted(async () => {
  await homeworkStore.selectHomework(String(route.params.id))
})
</script>

<style scoped>
.detail-state__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 16px;
}

@media (max-width: 720px) {
  .detail-state__footer {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
