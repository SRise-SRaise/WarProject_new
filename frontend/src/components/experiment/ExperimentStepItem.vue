<template>
  <div class="experiment-step-item" :class="{ 'experiment-step-item--answered': isAnswered }">
    <div class="step-header">
      <div class="step-number">第 {{ stepNumber }} 步</div>
      <div class="step-type-badge">
        <span class="type-dot" :class="`type-dot--${typeClass}`"></span>
        {{ typeName }}
      </div>
      <div class="step-score">{{ score }}分</div>
    </div>
    
    <div class="step-title">{{ title }}</div>
    
    <div class="step-content">
      <slot name="question">
        <!-- 默认插槽用于放置题目组件 -->
      </slot>
    </div>
    
    <div v-if="isAnswered && lastSaved" class="step-status">
      <CheckCircleOutlined class="status-icon" />
      <span>已保存于 {{ lastSaved }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { CheckCircleOutlined } from '@ant-design/icons-vue'
import type { QuestionType } from '@/stores/experiment/types'
import { QUESTION_TYPE_NAMES } from '@/stores/experiment/types'

interface Props {
  stepNumber: number
  title: string
  type: QuestionType
  score: number
  isAnswered?: boolean
  lastSaved?: string
}

const props = withDefaults(defineProps<Props>(), {
  isAnswered: false,
  lastSaved: ''
})

const typeName = computed(() => QUESTION_TYPE_NAMES[props.type] || '未知题型')

const typeClass = computed(() => {
  const classMap: Record<number, string> = {
    1: 'fill',      // 填空
    2: 'single',    // 单选
    3: 'multiple',  // 多选
    4: 'judge',     // 判断
    5: 'short',     // 简答
    6: 'code',      // 编程
    7: 'design'     // 设计
  }
  return classMap[props.type] || 'default'
})
</script>

<style scoped>
.experiment-step-item {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e8e8e8;
  transition: all 0.2s ease;
}

.experiment-step-item:hover {
  border-color: var(--color-primary, #1890ff);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.08);
}

.experiment-step-item--answered {
  border-color: #b7eb8f;
  background: #fafff0;
}

.step-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.step-number {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-primary, #1890ff);
  padding: 4px 12px;
  background: #e6f7ff;
  border-radius: 20px;
}

.step-type-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--color-text-secondary, #8c8c8c);
}

.type-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #8c8c8c;
}

.type-dot--fill { background: #1890ff; }
.type-dot--single { background: #52c41a; }
.type-dot--multiple { background: #fa8c16; }
.type-dot--judge { background: #722ed1; }
.type-dot--short { background: #13c2c2; }
.type-dot--code { background: #fa541c; }
.type-dot--design { background: #eb2f96; }

.step-score {
  margin-left: auto;
  font-size: 14px;
  color: var(--color-text-secondary, #8c8c8c);
}

.step-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-main, #262626);
  margin-bottom: 20px;
}

.step-content {
  /* 内容区域样式由内部题目组件决定 */
}

.step-status {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px dashed #e8e8e8;
  font-size: 13px;
  color: #52c41a;
}

.status-icon {
  font-size: 14px;
}
</style>