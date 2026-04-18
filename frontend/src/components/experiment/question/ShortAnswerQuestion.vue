<template>
  <div class="short-answer-question">
    <div class="question-content">
      {{ questionContent }}
    </div>

    <div class="answer-section">
      <a-textarea
        v-model:value="answerText"
        :placeholder="'请输入你的答案...'"
        :disabled="readOnly"
        :rows="6"
        show-count
        class="answer-textarea"
        @change="emitAnswer"
      />
    </div>

    <div v-if="showAnswer && correctAnswer" class="answer-info">
      <span class="correct-label">参考答案：</span>
      <pre class="correct-text">{{ correctAnswer }}</pre>
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

const answerText = ref(props.modelValue || '')

watch(() => props.modelValue, (newVal) => {
  answerText.value = newVal || ''
})

function emitAnswer() {
  emit('update:modelValue', answerText.value)
  emit('answerChange', '', answerText.value)
}
</script>

<style scoped>
.short-answer-question {
  padding: 16px 0;
}

.question-content {
  font-size: 16px;
  line-height: 1.8;
  color: var(--color-text-main, #262626);
  margin-bottom: 20px;
}

.answer-section {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
}

.answer-textarea {
  font-size: 15px;
  line-height: 1.8;
}

.answer-textarea :deep(.ant-input) {
  font-size: 15px;
  line-height: 1.8;
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
  display: block;
  margin-bottom: 8px;
}

.correct-text {
  margin: 0;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #262626;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
