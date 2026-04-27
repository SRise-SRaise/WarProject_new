<template>
  <div class="fill-blank-question">
    <!-- 题目内容：将 ____ 替换为内联输入框 -->
    <div class="question-content">
      <template v-for="(part, i) in contentParts" :key="i">
        <span v-if="part.type === 'text'" class="content-text" v-html="part.value"></span>
        <span v-else class="blank-wrapper">
          <span class="blank-index">{{ part.index }}</span>
          <input
            class="blank-inline-input"
            type="text"
            :value="answers[part.index! - 1]"
            :placeholder="`第 ${part.index} 空`"
            :disabled="readOnly"
            @input="onInput(part.index! - 1, ($event.target as HTMLInputElement).value)"
          />
        </span>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

interface Props {
  content: string
  correctAnswer?: string
  modelValue?: string[]
  readOnly?: boolean
}

interface Emits {
  (e: 'update:modelValue', value: string[]): void
  (e: 'answerChange', questionId: string, answer: string): void
}

const props = withDefaults(defineProps<Props>(), {
  correctAnswer: '',
  modelValue: () => [],
  readOnly: false
})

const emit = defineEmits<Emits>()

// 内部答案数组
const answers = ref<string[]>([])

// 将题目内容按 ____ 分割为文字段和填空段
interface ContentPart {
  type: 'text' | 'blank'
  value?: string
  index?: number
}

const contentParts = computed((): ContentPart[] => {
  if (!props.content) return []
  const segments = props.content.split(/____/)
  const parts: ContentPart[] = []
  segments.forEach((seg, i) => {
    if (seg) {
      parts.push({ type: 'text', value: seg })
    }
    if (i < segments.length - 1) {
      parts.push({ type: 'blank', index: i + 1 })
    }
  })
  return parts
})

// 填空数量
const blankCount = computed(() => {
  if (!props.content) return 0
  const matches = props.content.match(/____/g)
  return matches ? matches.length : 0
})

// 初始化答案数组长度
watch(blankCount, (n) => {
  while (answers.value.length < n) answers.value.push('')
}, { immediate: true })

// 当外部传入 modelValue 时同步
watch(() => props.modelValue, (val) => {
  if (val && val.length > 0) {
    answers.value = [...val]
    while (answers.value.length < blankCount.value) answers.value.push('')
  }
}, { immediate: true, deep: true })

function onInput(index: number, value: string) {
  answers.value[index] = value
  emit('update:modelValue', [...answers.value])
  emit('answerChange', '', answers.value.join(','))
}
</script>

<style scoped>
.fill-blank-question {
  padding: 12px 0;
}

.question-content {
  font-size: 16px;
  line-height: 2.4;
  color: var(--color-text-main, #262626);
  word-break: break-word;
}

.content-text {
  white-space: pre-wrap;
}

/* 填空单元：序号 + 下划线输入框，全部内联 */
.blank-wrapper {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin: 0 4px;
  vertical-align: middle;
}

.blank-index {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: var(--color-primary, #1890ff);
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  line-height: 1;
}

.blank-inline-input {
  display: inline-block;
  width: 140px;
  padding: 0 6px 2px;
  font-size: 15px;
  color: var(--color-text-main, #262626);
  background: transparent;
  border: none;
  border-bottom: 2px solid var(--color-primary, #1890ff);
  border-radius: 0;
  outline: none;
  vertical-align: middle;
  transition: border-color 0.25s, box-shadow 0.25s;
  line-height: 1.6;
}

.blank-inline-input:focus {
  border-bottom-color: var(--color-primary-hover, #096dd9);
  box-shadow: 0 2px 0 0 rgba(24, 144, 255, 0.2);
}

.blank-inline-input:disabled {
  color: var(--color-text-tertiary, #bfbfbf);
  border-bottom-color: #d9d9d9;
  cursor: not-allowed;
}

.blank-inline-input::placeholder {
  color: #bfbfbf;
  font-size: 13px;
}

@media (max-width: 576px) {
  .blank-inline-input {
    width: 100px;
  }
}
</style>
