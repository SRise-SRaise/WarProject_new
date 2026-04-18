<template>
  <div class="fill-blank-question">
    <div class="question-content" v-html="renderedContent"></div>

    <div class="answer-section">
      <div class="blank-hint">请在下方填写答案：</div>
      <div class="blanks-inputs">
        <div class="blank-item" v-if="blankCount >= 1">
          <span class="blank-number">1</span>
          <input
            type="text"
            class="blank-input"
            v-model="answer1"
            placeholder="第 1 空答案"
            :disabled="readOnly"
          />
        </div>
        <div class="blank-item" v-if="blankCount >= 2">
          <span class="blank-number">2</span>
          <input
            type="text"
            class="blank-input"
            v-model="answer2"
            placeholder="第 2 空答案"
            :disabled="readOnly"
          />
        </div>
        <div class="blank-item" v-if="blankCount >= 3">
          <span class="blank-number">3</span>
          <input
            type="text"
            class="blank-input"
            v-model="answer3"
            placeholder="第 3 空答案"
            :disabled="readOnly"
          />
        </div>
        <div class="blank-item" v-if="blankCount >= 4">
          <span class="blank-number">4</span>
          <input
            type="text"
            class="blank-input"
            v-model="answer4"
            placeholder="第 4 空答案"
            :disabled="readOnly"
          />
        </div>
        <div class="blank-item" v-if="blankCount >= 5">
          <span class="blank-number">5</span>
          <input
            type="text"
            class="blank-input"
            v-model="answer5"
            placeholder="第 5 空答案"
            :disabled="readOnly"
          />
        </div>
      </div>
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

// 使用独立的 ref 变量
const answer1 = ref('')
const answer2 = ref('')
const answer3 = ref('')
const answer4 = ref('')
const answer5 = ref('')

// 计算填空数量
const blankCount = computed(() => {
  if (!props.content) return 0
  const matches = props.content.match(/____/g)
  return matches ? matches.length : 0
})

// 渲染后的内容
const renderedContent = computed(() => {
  if (!props.content) return ''
  let index = 0
  return props.content.replace(/____/g, () => {
    index++
    return `<span class="blank-marker">
      <span class="blank-circle">${index}</span>
      <span class="blank-underline"></span>
    </span>`
  })
})

// 获取所有答案
function getAnswers(): string[] {
  const answers: string[] = []
  answers.push(answer1.value)
  answers.push(answer2.value)
  answers.push(answer3.value)
  answers.push(answer4.value)
  answers.push(answer5.value)
  return answers.slice(0, blankCount.value)
}

// 监听每个答案的变化
function onAnswerChange() {
  const answers = getAnswers()
  emit('update:modelValue', answers)
  emit('answerChange', '', answers.join(','))
}

watch(answer1, onAnswerChange)
watch(answer2, onAnswerChange)
watch(answer3, onAnswerChange)
watch(answer4, onAnswerChange)
watch(answer5, onAnswerChange)

// 监听 props.modelValue 变化，初始化答案
watch(() => props.modelValue, (newVal) => {
  if (newVal && newVal.length > 0) {
    answer1.value = newVal[0] || ''
    answer2.value = newVal[1] || ''
    answer3.value = newVal[2] || ''
    answer4.value = newVal[3] || ''
    answer5.value = newVal[4] || ''
  }
}, { immediate: true, deep: true })
</script>

<style scoped>
.fill-blank-question {
  padding: 16px 0;
}

.question-content {
  font-size: 16px;
  line-height: 2;
  color: var(--color-text-main, #262626);
  margin-bottom: 20px;
}

:deep(.blank-marker) {
  display: inline-flex;
  align-items: center;
  margin: 0 4px;
  vertical-align: baseline;
}

:deep(.blank-circle) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: var(--color-primary, #1890ff);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  margin-right: 6px;
}

:deep(.blank-underline) {
  display: inline-block;
  width: 80px;
  border-bottom: 2px solid var(--color-primary, #1890ff);
  color: transparent;
  letter-spacing: 2px;
}

.answer-section {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
}

.blank-hint {
  font-size: 14px;
  color: var(--color-text-secondary, #8c8c8c);
  margin-bottom: 12px;
}

.blanks-inputs {
  display: grid;
  gap: 12px;
}

.blank-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.blank-number {
  flex-shrink: 0;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--color-primary-light, #e6f7ff);
  color: var(--color-primary, #1890ff);
  font-size: 13px;
  font-weight: 600;
}

.blank-input {
  flex: 1;
  padding: 8px 12px;
  font-size: 15px;
  line-height: 1.5;
  color: #262626;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  outline: none;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.blank-input:focus {
  border-color: var(--color-primary, #1890ff);
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.blank-input:disabled {
  background: #f5f5f5;
  color: #bfbfbf;
  cursor: not-allowed;
}

.blank-input::placeholder {
  color: #bfbfbf;
}

@media (max-width: 576px) {
  :deep(.blank-underline) {
    width: 60px;
  }

  .blank-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }
}
</style>
