<template>
  <div class="judgment-question">
    <div class="question-content">
      {{ questionContent }}
    </div>

    <a-radio-group
      :value="selectedAnswer"
      :disabled="readOnly"
      class="options-group"
      @change="handleSelect"
    >
      <div
        class="option-item"
        :class="{ 'option-item--selected': selectedAnswer === '正确' }"
      >
        <a-radio value="正确">
          <span class="option-icon correct-icon">√</span>
          <span class="option-label">正确</span>
        </a-radio>
      </div>
      <div
        class="option-item"
        :class="{ 'option-item--selected': selectedAnswer === '错误' }"
      >
        <a-radio value="错误">
          <span class="option-icon wrong-icon">×</span>
          <span class="option-label">错误</span>
        </a-radio>
      </div>
    </a-radio-group>

    <div v-if="showAnswer && correctAnswer" class="answer-info">
      <span class="correct-label">正确答案：</span>
      <span class="correct-value">{{ correctAnswer }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

interface Props {
  questionContent: string
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

function handleSelect(e: Event) {
  const value = (e.target as HTMLInputElement).value
  selectedAnswer.value = value
  emit('update:modelValue', value)
  emit('answerChange', '', value)
}
</script>

<style scoped>
.judgment-question {
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
  gap: 16px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 14px 24px;
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

.option-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  font-size: 16px;
  font-weight: bold;
  margin-right: 12px;
}

.correct-icon {
  background: #52c41a;
  color: #fff;
}

.wrong-icon {
  background: #ff4d4f;
  color: #fff;
}

.option-label {
  font-size: 15px;
  color: var(--color-text-main, #262626);
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
