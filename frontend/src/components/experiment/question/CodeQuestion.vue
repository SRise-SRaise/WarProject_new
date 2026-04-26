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

      <div class="editor-container" ref="containerRef" :style="{ height: editorHeight + 'px' }" />
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
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import { LockOutlined, UnlockOutlined } from '@ant-design/icons-vue'
import type { ProgrammingLanguage } from '@/stores/experiment/types'
import { LANGUAGE_NAMES } from '@/stores/experiment/types'
import { createCodeEditor, type CodeEditorInstance } from './CodeEditor'

interface Props {
  questionContent: string
  language?: ProgrammingLanguage
  correctAnswer?: string
  modelValue?: string
  readOnly?: boolean
  allowPaste?: boolean
  showAnswer?: boolean
  editorHeight?: number
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
  language: 'text'
})

const emit = defineEmits<Emits>()

const containerRef = ref<HTMLElement | null>(null)
let editorInstance: CodeEditorInstance | null = null

function initEditor() {
  if (!containerRef.value) return
  // 销毁旧实例（热更新场景）
  if (editorInstance) {
    editorInstance.destroy()
    editorInstance = null
  }
  editorInstance = createCodeEditor({
    container: containerRef.value,
    value: props.modelValue || '',
    language: props.language || 'text',
    readOnly: props.readOnly || false,
    onChange: (value: string) => {
      emit('update:modelValue', value)
      emit('answerChange', '', value)
    }
  })
  if (!props.readOnly) {
    editorInstance.focus()
  }
}

watch(() => props.modelValue, (newVal) => {
  if (editorInstance && newVal !== editorInstance.getValue()) {
    editorInstance.setValue(newVal || '')
  }
})

watch(() => props.language, (newLang) => {
  if (editorInstance && newLang) {
    editorInstance.setLanguage(newLang)
  }
})

function resetCode() {
  if (editorInstance) {
    editorInstance.setValue('')
  }
  emit('update:modelValue', '')
  emit('answerChange', '', '')
}

onMounted(() => {
  initEditor()
})

onBeforeUnmount(() => {
  if (editorInstance) {
    editorInstance.destroy()
    editorInstance = null
  }
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
  border: 1px solid #3c3c3c;
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
}

.editor-container :deep(.cm-editor) {
  height: 100%;
}

.editor-container :deep(.cm-scroller) {
  overflow: auto;
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
