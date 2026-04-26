/**
 * 题型组件工厂（Factory Method 模式）
 *
 * <p>通过工厂方法根据题目类型动态获取对应的 Vue 组件，替代模板中冗长的 v-if 链。
 * 新增题型只需在此注册，无需修改视图层代码。</p>
 *
 * <p>使用方式：
 * <pre>
 * import { questionComponentFactory } from '@/stores/experiment/QuestionComponentFactory'
 * const component = questionComponentFactory.getComponent(question.type)
 * // 在模板中：&lt;component :is="component" v-bind="getProps(question)" /&gt;
 * </pre>
 * </p>
 */
import { defineAsyncComponent, type Component } from 'vue'
import type { QuestionType, ExperimentQuestion } from './types'

// 题型组件按类型分组注册
// 类型值与 ExperimentQuestionTypeEnum 保持一致
const QUESTION_COMPONENTS: Record<QuestionType, Component> = {
  // 选择题（单选）
  1: defineAsyncComponent(() => import('@/components/experiment/question/SingleChoiceQuestion.vue')),
  // 填空题
  2: defineAsyncComponent(() => import('@/components/experiment/question/FillBlankQuestion.vue')),
  // 编程题
  3: defineAsyncComponent(() => import('@/components/experiment/question/CodeQuestion.vue')),
  // 简答题
  4: defineAsyncComponent(() => import('@/components/experiment/question/ShortAnswerQuestion.vue')),
  // 多选题
  5: defineAsyncComponent(() => import('@/components/experiment/question/MultipleChoiceQuestion.vue')),
  // 判断题
  6: defineAsyncComponent(() => import('@/components/experiment/question/JudgmentQuestion.vue')),
  // 设计题（兜底到简答题）
  7: defineAsyncComponent(() => import('@/components/experiment/question/ShortAnswerQuestion.vue'))
}

export class QuestionComponentFactory {
  /**
   * 根据题目类型获取对应的组件。
   * 未知类型默认返回简答题组件。
   */
  getComponent(type: number | QuestionType): Component {
    return QUESTION_COMPONENTS[type as QuestionType] ?? QUESTION_COMPONENTS[4]
  }

  /**
   * 获取组件的 display name（用于调试）。
   */
  getComponentName(type: number | QuestionType): string {
    const names: Record<QuestionType, string> = {
      1: 'SingleChoiceQuestion',
      2: 'FillBlankQuestion',
      3: 'CodeQuestion',
      4: 'ShortAnswerQuestion',
      5: 'MultipleChoiceQuestion',
      6: 'JudgmentQuestion',
      7: 'ShortAnswerQuestion'
    }
    return names[type as QuestionType] ?? 'ShortAnswerQuestion'
  }

  /**
   * 获取题目类型的展示名称。
   */
  getTypeName(type: number | QuestionType): string {
    const names: Record<QuestionType, string> = {
      1: '选择题', 2: '填空题', 3: '编程题',
      4: '简答题', 5: '多选题', 6: '判断题', 7: '设计题'
    }
    return names[type as QuestionType] ?? '未知题型'
  }

  /**
   * 检查某题型是否需要选项列表。
   */
  hasOptions(type: number | QuestionType): boolean {
    return type === 1 || type === 5
  }

  /**
   * 检查某题型是否需要代码编辑器。
   */
  isCodeQuestion(type: number | QuestionType): boolean {
    return type === 3
  }

  /**
   * 获取题目类型的图标（Ant Design 图标名）。
   */
  getTypeIcon(type: number | QuestionType): string {
    const icons: Record<QuestionType, string> = {
      1: 'CheckSquareOutlined',
      2: 'EditOutlined',
      3: 'CodeOutlined',
      4: 'FileTextOutlined',
      5: 'AccountBookOutlined',
      6: 'ExclamationCircleOutlined',
      7: 'BulbOutlined'
    }
    return icons[type as QuestionType] ?? 'FileTextOutlined'
  }

  /**
   * 根据题目生成组件所需的 props 对象。
   *
   * <p>返回的 props 键名与各题型组件的 API 对齐，
   * 支持直接通过 v-bind 绑定到 &lt;component :is&gt; 上。</p>
   */
  buildComponentProps(question: ExperimentQuestion, answerGetters: {
    getSimpleAnswer: (id: string) => string
    getMultipleAnswer: (id: string) => string[]
    getFillBlanksAnswer: (id: string) => string[]
    handleSimpleAnswerChange: (id: string, val: unknown) => void
    handleMultipleAnswerChange: (id: string, val: unknown) => void
    handleFillBlanksChange: (id: string, val: unknown) => void
  }): Record<string, unknown> {
    const { getSimpleAnswer, getMultipleAnswer, getFillBlanksAnswer,
            handleSimpleAnswerChange, handleMultipleAnswerChange,
            handleFillBlanksChange } = answerGetters
    const qid = question.id

    switch (question.type) {
      case 1: // 单选
        return {
          'question-content': question.content,
          options: question.options ?? [],
          'model-value': getSimpleAnswer(qid),
          'onUpdate:modelValue': (val: unknown) => handleSimpleAnswerChange(qid, val)
        }
      case 5: // 多选
        return {
          'question-content': question.content,
          options: question.options ?? [],
          'model-value': getMultipleAnswer(qid),
          'onUpdate:modelValue': (val: unknown) => handleMultipleAnswerChange(qid, val)
        }
      case 2: // 填空
        return {
          content: question.content,
          'model-value': getFillBlanksAnswer(qid),
          'onUpdate:modelValue': (val: unknown) => handleFillBlanksChange(qid, val)
        }
      case 3: // 编程
        return {
          'question-content': question.content,
          language: question.language ?? 'text',
          'allow-paste': question.allowPaste ?? true,
          'model-value': getSimpleAnswer(qid),
          'onUpdate:modelValue': (val: unknown) => handleSimpleAnswerChange(qid, val)
        }
      case 4: // 简答
      case 6: // 判断
      case 7: // 设计
      default:
        return {
          'question-content': question.content,
          'model-value': getSimpleAnswer(qid),
          'onUpdate:modelValue': (val: unknown) => handleSimpleAnswerChange(qid, val)
        }
    }
  }
}

/** 导出工厂单例 */
export const questionComponentFactory = new QuestionComponentFactory()

/** 便利导出 */
export const getQuestionComponent = (type: number | QuestionType) =>
  questionComponentFactory.getComponent(type)
export const getQuestionTypeName = (type: number | QuestionType) =>
  questionComponentFactory.getTypeName(type)
