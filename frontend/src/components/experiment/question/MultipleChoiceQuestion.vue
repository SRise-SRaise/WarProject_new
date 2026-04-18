<template>
  <div class="multiple-choice-question">
    <div class="question-content">
      {{ questionContent }}
      <span class="multi-hint">（多选）</span>
    </div>

    <div class="options-group">
      <div
        v-for="option in options"
        :key="option.key"
        class="option-item"
        :class="{ 'option-item--selected': selectedAnswers.includes(option.key) }"
        @click="toggleOption(option.key)"
      >
        <div class="option-checkbox">
          <div class="checkbox-box" :class="{ 'checkbox-box--selected': selectedAnswers.includes(option.key) }">
            <CheckOutlined v-if="selectedAnswers.includes(option.key)" class="check-icon" />
          </div>
        </div>
        <div class="option-content">
          <span class="option-key">{{ option.key }}</span>
          <span class="option-label">{{ option.label }}</span>
        </div>
      </div>
    </div>

    <div v-if="showAnswer && correctAnswer" class="answer-info">
      <span class="correct-label">正确答案：</span>
      <span class="correct-value">{{ correctAnswer }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { CheckOutlined } from '@ant-design/icons-vue'
import type { QuestionOption } from '@/stores/experiment/types'

interface Props {
  questionContent: string
  options: QuestionOption[]
  correctAnswer?: string
  modelValue?: string[]
  readOnly?: boolean
  showAnswer?: boolean
}

interface Emits {
  (e: 'update:modelValue', value: string[]): void
  (e: 'answerChange', questionId: string, answer: string): void
}

const props = withDefaults(defineProps<Props>(), {
  correctAnswer: '',
  modelValue: () => [],
  readOnly: false,
  showAnswer: false
})

const emit = defineEmits<Emits>()

const selectedAnswers = ref<string[]>([...(props.modelValue || [])])

watch(() => props.modelValue, (newVal) => {
  selectedAnswers.value = newVal ? [...newVal] : []
}, { deep: true })

function toggleOption(key: string) {
  if (props.readOnly) return

  const index = selectedAnswers.value.indexOf(key)
  if (index === -1) {
    selectedAnswers.value.push(key)
  } else {
    selectedAnswers.value.splice(index, 1)
  }

  const sortedAnswers = [...selectedAnswers.value].sort()
  emit('update:modelValue', sortedAnswers)
  emit('answerChange', '', sortedAnswers.join(','))
}
</script>

<style scoped>
.multiple-choice-question {
  padding: 16px 0;
}

.question-content {
  font-size: 16px;
  line-height: 1.8;
  color: var(--color-text-main, #262626);
  margin-bottom: 20px;
}

.multi-hint {
  color: var(--color-warning, #fa8c16);
  font-size: 13px;
  margin-left: 8px;
}

.options-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 14px 18px;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  background: #fff;
  transition: all 0.2s ease;
  cursor: pointer;
}

.option-item:hover {
  border-color: var(--color-primary, #1890ff);
  background: #f0f7ff;
}

.option-item--selected {
  border-color: var(--color-primary, #1890ff);
  background: #e6f7ff;
}

.option-checkbox {
  margin-right: 12px;
}

.checkbox-box {
  width: 22px;
  height: 22px;
  border-radius: 4px;
  border: 2px solid #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.checkbox-box--selected {
  border-color: var(--color-primary, #1890ff);
  background: var(--color-primary, #1890ff);
}

.check-icon {
  color: #fff;
  font-size: 14px;
  font-weight: bold;
}

.option-content {
  display: flex;
  align-items: center;
  flex: 1;
}

.option-key {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 26px;
  height: 26px;
  border-radius: 50%;
  background: var(--color-primary, #1890ff);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  margin-right: 12px;
}

.option-label {
  font-size: 15px;
  color: var(--color-text-main, #262626);
  line-height: 1.6;
}

.answer-info {
  margin-top: 16px;
  padding: 12px 16px;
  background: #f6ffed;
  border-radius: 8px;
  border: 1px solid #b7eb8f;
}

.correct-label {
  color: #52c41a;
  font-weight: 500;
}

.correct-value {
  color: #389e0d;
  font-weight: 600;
}
</style>