<template>
  <div class="code-question">
    <div class="question-content">
      {{ questionContent }}
    </div>
    
    <div class="code-section">
      <div class="code-header">
        <div class="code-info">
          <span v-if="language" class="language-tag">{{ LANGUAGE_NAMES[language] || language }}</span>
          <span class="paste-hint" :class="{ 'paste-hint--disabled': !allowPaste }">
            <LockOutlined v-if="!allowPaste" />
            <UnlockOutlined v-else />
            {{ allowPaste ? '允许粘贴' : '禁止粘贴' }}
          </span>
        </div>
        <div class="code-actions">
          <a-button size="small" @click="resetCode">重置</a-button>
        </div>
      </div>
      
      <div class="editor-container" :style="{ height: editorHeight + 'px' }">
        <textarea
          v-if="!useMonaco"
          ref="textareaRef"
          v-model="codeContent"
          class="code-textarea"
          :placeholder="`在此输入 ${LANGUAGE_NAMES[language] || '代码'}...`"
          :disabled="readOnly"
          :style="{ imeMode: allowPaste ? 'auto' : 'disabled' }"
          @input="handleInput"
          @keydown="handleKeyDown"
          @paste="handlePaste"
        ></textarea>
        <!-- 预留 Monaco Editor 集成位置 -->
        <div v-else class="monaco-placeholder">
          <span>Monaco Editor 集成区域</span>
        </div>
      </div>
    </div>
    
    <div v-if="showAnswer && correctAnswer" class="answer-info">
      <div class="answer-header">
        <span class="correct-label">参考代码：</span>
      </div>
      <pre class="correct-code"><code>{{ correctAnswer }}</code></pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { LockOutlined, UnlockOutlined } from '@ant-design/icons-vue'
import type { ProgrammingLanguage } from '@/stores/experiment/types'
import { LANGUAGE_NAMES } from '@/stores/experiment/types'

interface Props {
  questionContent: string
  language?: ProgrammingLanguage
  correctAnswer?: string
  modelValue?: string
  readOnly?: boolean
  allowPaste?: boolean
  showAnswer?: boolean
  editorHeight?: number
  useMonaco?: boolean   // 预留：是否使用 Monaco Editor
}

interface Emits {
  (e: 'update:modelValue', value: string): void
  (e: 'answerChange', questionId: string, answer: string): void
}

const props = withDefaults(defineProps<Props>(), {
  correctAnswer: '',
  modelValue: '',
  readOnly: false,
  allowPaste: true,
  showAnswer: false,
  editorHeight: 400,
  useMonaco: false,
  language: 'text'
})

const emit = defineEmits<Emits>()

const textareaRef = ref<HTMLTextAreaElement | null>(null)
const codeContent = ref(props.modelValue || '')

watch(() => props.modelValue, (newVal) => {
  codeContent.value = newVal || ''
})

function handleInput(e: Event) {
  const value = (e.target as HTMLTextAreaElement).value
  codeContent.value = value
  emit('update:modelValue', value)
  emit('answerChange', '', value)
}

function handleKeyDown(e: KeyboardEvent) {
  // 禁用粘贴快捷键（当禁止粘贴时）
  if (!props.allowPaste && (e.ctrlKey || e.metaKey) && e.key === 'v') {
    e.preventDefault()
  }
  
  // Tab 键处理
  if (e.key === 'Tab') {
    e.preventDefault()
    const target = e.target as HTMLTextAreaElement
    const start = target.selectionStart
    const end = target.selectionEnd
    const value = target.value
    codeContent.value = value.substring(0, start) + '  ' + value.substring(end)
    emit('update:modelValue', codeContent.value)
    emit('answerChange', '', codeContent.value)
    // 恢复光标位置
    setTimeout(() => {
      target.selectionStart = target.selectionEnd = start + 2
    }, 0)
  }
}

function handlePaste(e: ClipboardEvent) {
  if (!props.allowPaste) {
    e.preventDefault()
    return
  }
  // 允许粘贴，正常的 paste 事件会处理
}

function resetCode() {
  codeContent.value = ''
  emit('update:modelValue', '')
  emit('answerChange', '', '')
}

onMounted(() => {
  // 聚焦到编辑器
  textareaRef.value?.focus()
})
</script>

<style scoped>
.code-question {
  padding: 16px 0;
}

.question-content {
  font-size: 16px;
  line-height: 1.8;
  color: var(--color-text-main, #262626);
  margin-bottom: 20px;
}

.code-section {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
  background: #1e1e1e;
}

.code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  background: #252526;
  border-bottom: 1px solid #3c3c3c;
}

.code-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.language-tag {
  padding: 4px 10px;
  background: #0066cc;
  color: #fff;
  font-size: 12px;
  border-radius: 4px;
  font-weight: 500;
}

.paste-hint {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #52c41a;
}

.paste-hint--disabled {
  color: #ff4d4f;
}

.code-actions {
  display: flex;
  gap: 8px;
}

.editor-container {
  width: 100%;
  position: relative;
}

.code-textarea {
  width: 100%;
  height: 100%;
  min-height: 300px;
  padding: 16px;
  background: #1e1e1e;
  color: #d4d4d4;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  border: none;
  resize: none;
  outline: none;
}

.code-textarea::placeholder {
  color: #6a6a6a;
}

.monaco-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #2d2d2d;
  color: #888;
  font-size: 14px;
}

.answer-info {
  margin-top: 16px;
  padding: 12px 16px;
  background: #f6ffed;
  border-radius: 8px;
  border: 1px solid #b7eb8f;
}

.answer-header {
  margin-bottom: 8px;
}

.correct-label {
  color: #52c41a;
  font-weight: 500;
}

.correct-code {
  margin: 0;
  padding: 12px;
  background: #1e1e1e;
  color: #d4d4d4;
  border-radius: 6px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  overflow-x: auto;
}

.correct-code code {
  white-space: pre;
}
</style>