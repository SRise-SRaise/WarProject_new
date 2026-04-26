/**
 * CodeMirror 6 编辑器封装
 *
 * <p>统一管理 CodeMirror 实例的创建与销毁，提供语法高亮、自动补全（可选）、主题支持。
 * 相比 textarea，提供：
 * <ul>
 *   <li>语言语法高亮（Java / Python / SQL / JS / HTML / CSS）</li>
 *   <li>代码折叠、行号、匹配括号等编辑器特性</li>
 *   <li>深色主题（VSCode Dark+ 风格）</li>
 * </ul>
 */
import { EditorState, Compartment } from '@codemirror/state'
import {
  EditorView,
  keymap,
  lineNumbers,
  highlightActiveLineGutter,
  highlightSpecialChars,
  drawSelection,
  dropCursor,
  rectangularSelection,
  crosshairCursor,
  highlightActiveLine
} from '@codemirror/view'
import {
  defaultKeymap,
  history,
  historyKeymap,
  indentWithTab
} from '@codemirror/commands'
import {
  foldGutter,
  indentOnInput,
  syntaxHighlighting,
  defaultHighlightStyle,
  bracketMatching,
  foldKeymap
} from '@codemirror/language'
import { closeBrackets, autocompletion, closeBracketsKeymap, completionKeymap } from '@codemirror/autocomplete'
import { searchKeymap, highlightSelectionMatches as searchHighlight } from '@codemirror/search'
import { oneDark } from '@codemirror/theme-one-dark'
import { java } from '@codemirror/lang-java'
import { python } from '@codemirror/lang-python'
import { sql } from '@codemirror/lang-sql'
import { javascript } from '@codemirror/lang-javascript'
import { html } from '@codemirror/lang-html'
import { css } from '@codemirror/lang-css'
import { xml } from '@codemirror/lang-xml'
import type { ProgrammingLanguage } from '@/stores/experiment/types'

/** 语言 → CodeMirror LanguageSupport */
function getLanguageExtension(language: ProgrammingLanguage) {
  switch (language) {
    case 'java':     return java()
    case 'python':   return python()
    case 'sql':      return sql()
    case 'javascript': return javascript()
    case 'html':     return html()
    case 'css':      return css()
    case 'jsp':      return xml()
    default:         return []
  }
}

const languageCompartment = new Compartment()

export interface CodeEditorOptions {
  container: HTMLElement
  value: string
  language: ProgrammingLanguage
  readOnly?: boolean
  onChange?: (value: string) => void
}

export interface CodeEditorInstance {
  destroy: () => void
  getValue: () => string
  setValue: (value: string) => void
  setLanguage: (language: ProgrammingLanguage) => void
  focus: () => void
}

export function createCodeEditor(options: CodeEditorOptions): CodeEditorInstance {
  const { container, value, language, readOnly = false, onChange } = options

  const updateListener = EditorView.updateListener.of((update) => {
    if (update.docChanged && onChange) {
      onChange(update.state.doc.toString())
    }
  })

  const state = EditorState.create({
    doc: value,
    extensions: [
      lineNumbers(),
      highlightActiveLineGutter(),
      highlightSpecialChars(),
      history(),
      foldGutter(),
      drawSelection(),
      dropCursor(),
      EditorState.allowMultipleSelections.of(true),
      indentOnInput(),
      syntaxHighlighting(defaultHighlightStyle, { fallback: true }),
      bracketMatching(),
      closeBrackets(),
      autocompletion(),
      rectangularSelection(),
      crosshairCursor(),
      highlightActiveLine(),
      searchHighlight(),
      keymap.of([
        ...closeBracketsKeymap,
        ...defaultKeymap,
        ...searchKeymap,
        ...historyKeymap,
        ...foldKeymap,
        ...completionKeymap,
        indentWithTab
      ]),
      oneDark,
      languageCompartment.of(getLanguageExtension(language)),
      EditorState.readOnly.of(readOnly),
      updateListener,
      EditorView.theme({
        '&': {
          height: '100%',
          fontSize: '14px'
        },
        '.cm-scroller': {
          fontFamily: "'Consolas', 'Monaco', 'Courier New', monospace",
          lineHeight: '1.6'
        },
        '.cm-content': {
          padding: '12px 0'
        },
        '.cm-gutters': {
          backgroundColor: '#1e1e1e',
          color: '#6e7681',
          border: 'none',
          paddingRight: '8px'
        },
        '.cm-activeLineGutter': {
          backgroundColor: '#2d2d2d'
        }
      })
    ]
  })

  const view = new EditorView({ state, parent: container })

  return {
    destroy() {
      view.destroy()
    },
    getValue() {
      return view.state.doc.toString()
    },
    setValue(value: string) {
      view.dispatch({
        changes: { from: 0, to: view.state.doc.length, insert: value }
      })
    },
    setLanguage(language: ProgrammingLanguage) {
      view.dispatch({
        effects: languageCompartment.reconfigure(getLanguageExtension(language))
      })
    },
    focus() {
      view.focus()
    }
  }
}
