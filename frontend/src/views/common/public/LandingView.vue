<template>
  <div class="landing-page app-page-shell app-page-shell--wide app-panel-grid">
    <section class="landing-hero app-surface-card">
      <div class="landing-hero__content">
        <div class="landing-hero__copy">
          <span class="app-pill">教学协同 / 学习空间</span>
          <h1 class="landing-hero__title">把资料、学习提醒与教学管理收进同一套校内协作界面。</h1>
          <p class="landing-hero__description">
            第一波界面聚焦公共入口、学生学习概览、资料详情和教师管理工作台，
            让新系统先具备稳定、清晰、可继续扩展的产品骨架。
          </p>

          <a-space :size="12" wrap>
            <a-button type="primary" size="large" @click="goPrimary">{{ primaryActionLabel }}</a-button>
            <a-button size="large" @click="router.push('/admin/dashboard')">教师工作台</a-button>
          </a-space>
        </div>

        <div class="landing-hero__metrics app-kpi-grid">
          <MetricCard
            v-for="item in publicSnapshot.heroMetrics"
            :key="item.label"
            :title="item.label"
            :value="item.value"
            :description="item.description"
            :trend="item.trend"
            :tone="item.tone"
          />
        </div>
      </div>
    </section>

    <section class="app-surface-card app-section-card app-panel-grid">
      <SectionHeader
        eyebrow="首波范围"
        title="把公共入口、学习视图和教学视图组织成一致的导航语言。"
        description="页面不再沿用模板站点式布局，而是让首页、学习概览、教师工作台和个人设置都围绕同一套主题变量与卡片层级展开。"
      />

      <div class="app-card-grid landing-feature-grid">
        <article v-for="feature in publicSnapshot.highlights" :key="feature.title" class="landing-feature-card">
          <h3>{{ feature.title }}</h3>
          <p>{{ feature.description }}</p>
          <div class="landing-feature-card__tags">
            <a-tag v-for="tag in feature.tags" :key="tag">{{ tag }}</a-tag>
          </div>
        </article>
      </div>
    </section>

    <section class="app-split-grid">
      <div class="app-surface-card app-section-card app-panel-grid">
        <SectionHeader
          eyebrow="落地节奏"
          title="先把关键路径做好，再继续接入流程型业务模块。"
          description="当前页面已按模块目录和双壳层分清职责，后续作业、实验与考试模块可以延续相同路由与页面结构接入。"
        />

        <div class="landing-timeline">
          <div v-for="item in publicSnapshot.timeline" :key="item.title" class="landing-timeline__item">
            <span class="landing-timeline__dot"></span>
            <div>
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="app-panel-grid">
        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader
            eyebrow="资料预览"
            title="先把资料域做完整。"
            description="资料列表和详情页是学生学习入口的核心锚点，也是后续流程模块最稳定的公共资源底座。"
          />

          <div class="app-list">
            <article v-for="material in featuredMaterials" :key="material.id" class="app-list-card landing-material-card">
              <div class="landing-material-card__head">
                <div>
                  <h3 class="app-list-card__title">{{ material.title }}</h3>
                  <p class="app-list-card__meta">{{ material.topicLabel }} · {{ material.teacher }} · {{ material.updatedAt }}</p>
                </div>
                <a-tag color="blue">{{ material.type }}</a-tag>
              </div>
              <p class="landing-material-card__summary">{{ material.summary }}</p>
              <a-button type="link" @click="openMaterial(material.id)">查看资料详情</a-button>
            </article>
          </div>
        </section>

        <section class="app-surface-card app-section-card app-panel-grid">
          <SectionHeader eyebrow="实施提醒" title="当前实现说明" description="下面两项是当前首波界面的关键使用说明。" tight />
          <div class="app-list">
            <article v-for="notice in publicSnapshot.notices" :key="notice.title" class="app-list-card">
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
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import MetricCard from '@/components/common/MetricCard.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import { useAppStore } from '@/stores/common/app'
import { useMaterialStore } from '@/stores/common/material'
import { useAuthStore } from '@/stores/user/auth'

const router = useRouter()
const appStore = useAppStore()
const materialStore = useMaterialStore()
const authStore = useAuthStore()

const { publicSnapshot } = storeToRefs(appStore)
const { featuredMaterials } = storeToRefs(materialStore)

const primaryActionLabel = computed(() => {
  if (authStore.isTeacher) {
    return '进入教师工作台'
  }
  if (authStore.isStudent) {
    return '继续学习概览'
  }
  return '立即登录'
})

function goPrimary(): void {
  if (authStore.isTeacher) {
    router.push('/admin/dashboard')
    return
  }

  if (authStore.isStudent) {
    router.push('/learning')
    return
  }

  router.push('/user/login')
}

function openMaterial(id: string): void {
  router.push(`/materials/${id}`)
}

onMounted(async () => {
  await Promise.all([appStore.ensureReady(), materialStore.ensureLoaded()])
})
</script>

<style scoped>
.landing-hero {
  padding: 32px;
}

.landing-hero__content {
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(320px, 0.92fr);
  gap: var(--space-6);
  align-items: start;
}

.landing-hero__title {
  margin: 20px 0 16px;
  color: var(--color-text-main);
  font-family: Georgia, 'Times New Roman', 'Songti SC', serif;
  font-size: clamp(34px, 4vw, 54px);
  line-height: 1.08;
}

.landing-hero__description {
  margin: 0 0 26px;
  max-width: 620px;
  color: var(--color-text-secondary);
  font-size: 16px;
  line-height: 1.85;
}

.landing-feature-grid {
  align-items: stretch;
}

.landing-feature-card {
  padding: 22px;
  border-radius: var(--radius-md);
  border: 1px solid rgba(194, 206, 222, 0.55);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(246, 248, 251, 0.96) 100%);
}

.landing-feature-card h3 {
  margin: 0 0 10px;
  font-size: 19px;
}

.landing-feature-card p {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.8;
}

.landing-feature-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 18px;
}

.landing-timeline {
  display: grid;
  gap: 18px;
}

.landing-timeline__item {
  display: grid;
  grid-template-columns: 20px minmax(0, 1fr);
  gap: 16px;
}

.landing-timeline__dot {
  position: relative;
  top: 8px;
  width: 12px;
  height: 12px;
  border-radius: 999px;
  background: var(--color-accent);
  box-shadow: 0 0 0 6px rgba(216, 165, 69, 0.16);
}

.landing-timeline__item h3 {
  margin: 0 0 8px;
  font-size: 18px;
}

.landing-timeline__item p {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.8;
}

.landing-material-card__head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.landing-material-card__summary {
  margin: 14px 0 0;
  color: var(--color-text-secondary);
  line-height: 1.75;
}

@media (max-width: 980px) {
  .landing-hero {
    padding: 24px;
  }

  .landing-hero__content {
    grid-template-columns: 1fr;
  }
}
</style>
