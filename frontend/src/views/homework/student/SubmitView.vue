<template>
  <div class="app-page-shell app-panel-grid">
    <section class="app-surface-card app-section-card app-panel-grid">
      <SectionHeader
        eyebrow="提交作业"
        title="兼容入口页"
        description="该页面仅用于兼容旧路由，建议后续统一从“做作业”页面提交答案。"
      >
        <template #actions>
          <a-button type="primary" @click="router.push(`/homework/${homeworkId}/do`)">进入做作业页</a-button>
        </template>
      </SectionHeader>
      <article class="app-list-card">
        <h3 class="app-list-card__title">当前路由状态</h3>
        <p class="app-list-card__meta">你访问的是历史兼容路由：`/homework/:id/submit`。</p>
        <p class="app-list-card__meta">为保持作业流程一致，系统建议直接进入做作业页完成选择题与简答提交。</p>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import SectionHeader from '@/components/common/SectionHeader.vue'

const route = useRoute()
const router = useRouter()
// 作业模块Mock数据占位符，后续需替换到真实后端接口：GET /s_excercise_submit.do
const homeworkId = computed(() => String(route.params.id || 'hw-101'))
</script>
