<template>
  <div class="single-choice-question">
    <div class="question-content">
      {{ questionContent }}
    </div>

    <div class="options-group">
      <div
        v-for="option in options"
        :key="option.key"
        class="option-item"
        :class="{ 'option-item--selected': selectedAnswer === option.key }"
        @click="selectOption(option.key)"
      >
        <div class="option-radio">
          <div class="radio-circle" :class="{ 'radio-circle--selected': selectedAnswer === option.key }">
            <div v-if="selectedAnswer === option.key" class="radio-inner"></div>
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
import type { QuestionOption } from '@/stores/experiment/types'

interface Props {
  questionContent: string
  options: QuestionOption[]
  correctAnswer?: string
  modelValue?: string
  readOnly?: boolean
  showAnswer?: boolean
}

interface Emits {
  (e: 'update:modelValue', value: string): void
  (e: 'answerChange', questionId: string, answer: string): void
}

const props = withDefaults(defineProps<Props>(), {
  correctAnswer: '',
  modelValue: '',
  readOnly: false,
  showAnswer: false
})

const emit = defineEmits<Emits>()

const selectedAnswer = ref(props.modelValue || '')

watch(() => props.modelValue, (newVal) => {
  selectedAnswer.value = newVal || ''
})

function selectOption(key: string) {
  if (props.readOnly) return
  selectedAnswer.value = key
  emit('update:modelValue', key)
  emit('answerChange', '', key)
}
</script>

<style scoped>
.single-choice-question {
  padding: 16px 0;
}

.question-content {
  font-size: 16px;
  line-height: 1.8;
  color: var(--color-text-main, #262626);
  margin-bottom: 20px;
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

.option-radio {
  margin-right: 12px;
}

.radio-circle {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  border: 2px solid #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.radio-circle--selected {
  border-color: var(--color-primary, #1890ff);
}

.radio-inner {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: var(--color-primary, #1890ff);
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
