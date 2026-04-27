<template>
  <div class="app-page-shell">
    <!-- 面包屑导航 -->
    <div class="breadcrumb-nav">
      <a-breadcrumb>
        <a-breadcrumb-item>
          <a href="#" @click.prevent="router.push('/admin/experiments/list')">上机实验</a>
        </a-breadcrumb-item>
        <a-breadcrumb-item>{{ isEdit ? '编辑实验' : '新建实验' }}</a-breadcrumb-item>
      </a-breadcrumb>
    </div>

    <!-- 页面头部 -->
    <section class="app-surface-card page-header-card">
      <div class="page-header">
        <div class="page-header__main">
          <p class="page-header__eyebrow">上机实验</p>
          <h1 class="page-header__title">{{ pageTitle }}</h1>
          <p class="page-header__description">维护实验基本信息、步骤内容和评分标准，支持发布与撤回操作。</p>
        </div>
        <div class="page-header__actions">
          <a-space :size="12">
            <a-button @click="router.push('/admin/experiments/list')">
              <template #icon><RollbackOutlined /></template>
              返回列表
            </a-button>
          </a-space>
        </div>
      </div>
    </section>

    <!-- 编辑表单 -->
    <section class="app-surface-card form-card">
      <div class="form-container">
        <a-form
          ref="formRef"
          :model="formState"
          :rules="rules"
          layout="vertical"
          class="experiment-form"
          @finish="onFinish"
        >
          <!-- 实验ID（隐藏） -->
          <a-input v-model:value="formState.id" type="hidden" />

          <!-- 第一行：实验名称 -->
          <a-form-item label="实验名称" name="name">
            <a-input v-model:value="formState.name" size="large" placeholder="请输入实验名称" />
          </a-form-item>

          <!-- 第二行：实验次序 & 实验类型 -->
          <div class="form-row">
            <a-form-item label="实验次序" name="no" class="form-row__item form-row__item--small">
              <a-input-number v-model:value="formState.no" :min="1" :max="99" size="large" placeholder="序号" style="width: 100%" />
            </a-form-item>

            <a-form-item label="实验类型" name="type" class="form-row__item">
              <a-select v-model:value="formState.type" size="large" placeholder="请选择实验类型">
                <a-select-option v-for="(typeName, index) in experimentTypes" :key="index" :value="index + 1">
                  {{ typeName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </div>

          <!-- 第三行：实验要求 -->
          <a-form-item label="实验要求" name="requirement">
            <a-textarea v-model:value="formState.requirement" :rows="5" placeholder="请输入实验要求" />
          </a-form-item>

          <!-- 第四行：实验内容 -->
          <a-form-item label="实验内容" name="content">
            <a-textarea v-model:value="formState.content" :rows="5" placeholder="请输入实验内容" />
          </a-form-item>

          <!-- 第五行：实验指导（文件上传） -->
          <a-form-item label="实验指导">
            <div class="file-upload-row">
              <div class="file-info">
                <template v-if="formState.instructionType || formState.instructionFileName">
                  <a-tag color="blue">{{ formState.instructionType || 'FILE' }}</a-tag>
                  <span class="file-name">{{ formState.instructionFileName || '已上传文件' }}</span>
                  <a
                    v-if="formState.instructionUrl"
                    :href="formState.instructionUrl"
                    target="_blank"
                    class="view-link"
                  >查看</a>
                </template>
                <span v-else class="no-file">暂无指导文件</span>
              </div>
              <a-upload
                :before-upload="beforeUpload"
                :show-upload-list="false"
                accept=".pdf,.doc,.docx"
              >
                <a-button :loading="uploadingInstruction">
                  <template #icon><UploadOutlined /></template>
                  {{ uploadingInstruction ? '上传中...' : '选择文件' }}
                </a-button>
              </a-upload>
            </div>
            <p class="form-help-text">支持 PDF、DOC、DOCX 格式，最大 10MB。保存实验后文件将自动上传。</p>
          </a-form-item>

          <!-- 第六行：发布班级（多选） -->
          <a-form-item label="发布班级" name="classCodes">
            <div class="class-selector-tip">
              <span v-if="!allClasses.length && !loadingClasses" class="no-classes-hint">
                暂无可用班级，请先在班级管理中添加班级
              </span>
              <a-select
                v-model:value="formState.classCodes"
                mode="multiple"
                placeholder="请选择允许访问此实验的班级（必选，支持多选）"
                :loading="loadingClasses"
                :disabled="loadingClasses"
                allow-clear
                style="width: 100%"
                size="large"
              >
                <a-select-option v-for="cls in allClasses" :key="cls.classCode" :value="cls.classCode">
                  <div class="class-option">
                    <span class="class-code">{{ cls.classCode }}</span>
                    <span class="class-name">{{ cls.headmasterName || '暂无备注' }}</span>
                  </div>
                </a-select-option>
              </a-select>
            </div>
            <p class="form-help-text">选择允许访问此实验的班级，支持多选；不选择则实验仅创建者可见</p>
          </a-form-item>

          <!-- 提交按钮 -->
          <a-form-item class="form-actions">
            <a-space :size="16">
              <a-button type="primary" size="large" html-type="submit" :loading="saving">
                <template #icon><SaveOutlined /></template>
                保存实验
              </a-button>
              <a-button size="large" @click="router.push('/admin/experiments/list')">
                取消
              </a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </div>
    </section>

    <!-- 导入实验步骤区域（发布班级下方，独立卡片） -->
    <section class="app-surface-card import-card">
      <div class="import-card__header">
        <div class="import-card__title-group">
          <h2 class="import-card__title">导入实验步骤</h2>
          <p class="import-card__desc">
            通过上传 .docx 文件，自动解析并导入选择题、填空题、编程题、简答题等步骤内容。
            <template v-if="!isEdit">
              <br>
              <span class="hint-warning">请先保存实验基本信息，保存后即可在此处导入步骤。</span>
            </template>
          </p>
        </div>
        <!-- 下载模板按钮 -->
        <button type="button" class="template-download-btn" @click="handleTemplateDownload()">
          <DownloadOutlined />
          下载参考模板
        </button>
      </div>

      <!-- 格式说明 -->
      <div class="import-format-hint">
        <div class="format-hint__item">
          <span class="format-badge">1</span>选择题
        </div>
        <div class="format-hint__item">
          <span class="format-badge">2</span>填空题
        </div>
        <div class="format-hint__item">
          <span class="format-badge">3</span>编程题
        </div>
        <div class="format-hint__item">
          <span class="format-badge">4</span>简答题
        </div>
        <span class="format-hint__note">按照模板格式填写后上传，系统自动识别题型</span>
      </div>

      <!-- 上传区域 -->
      <div class="import-upload-area" :class="{ 'import-upload-area--disabled': !isEdit }">
        <a-upload-dragger
          v-if="isEdit"
          :before-upload="beforeImportUpload"
          :show-upload-list="false"
          accept=".docx"
          :disabled="importing"
        >
          <div class="upload-dragger-content">
            <InboxOutlined class="upload-dragger-icon" />
            <p class="upload-dragger-title">点击或拖拽 .docx 文件到此处上传</p>
            <p class="upload-dragger-hint">仅支持 .docx 格式，文件大小不超过 20MB</p>
          </div>
        </a-upload-dragger>
        <div v-else class="upload-disabled-placeholder">
          <LockOutlined class="upload-disabled-icon" />
          <p>请先保存实验基本信息后，再导入实验步骤</p>
        </div>
      </div>

      <!-- 导入进度/结果 -->
      <div v-if="importing" class="import-progress">
        <a-spin tip="正在解析文档并导入题目，请稍候..." />
      </div>

      <div v-if="importResult" class="import-result">
        <a-alert
          :type="importResult.success ? 'success' : 'error'"
          :message="importResult.success ? `导入完成：成功 ${importResult.successCount} 题` : '导入失败'"
          :description="importResult.message"
          show-icon
          class="import-result__alert"
        />
        <!-- 导入题目预览 -->
        <div v-if="importResult.questionPreviews && importResult.questionPreviews.length" class="import-preview">
          <h4 class="import-preview__title">已导入题目预览</h4>
          <div
            v-for="q in importResult.questionPreviews"
            :key="q.index"
            class="import-preview__item"
          >
            <span class="preview-index">{{ q.index }}</span>
            <span class="preview-type">{{ q.questionTypeName }}</span>
            <span class="preview-name">{{ q.questionName }}</span>
            <span class="preview-content">{{ q.contentSummary }}</span>
          </div>
        </div>
        <!-- 失败详情 -->
        <div v-if="importResult.failCount > 0" class="import-fail-detail">
          <p class="import-fail-detail__title">失败 {{ importResult.failCount }} 题：</p>
          <p
            v-for="(reason, idx) in importResult.failDetails"
            :key="idx"
            class="import-fail-detail__item"
          >
            第 {{ idx }} 题：{{ reason }}
          </p>
        </div>
        <a-button type="link" class="import-result__clear" @click="importResult = null; loadExperimentItems(formState.id)">清除结果</a-button>
      </div>

      <!-- 已有题目列表 -->
      <div v-if="!importResult && existingItems.length > 0" class="existing-items">
        <h4 class="existing-items__title">已导入的实验步骤（{{ existingItems.length }} 题）</h4>
        <a-spin :spinning="loadingItems">
          <div class="existing-items__list">
            <div
              v-for="(item, idx) in existingItems"
              :key="item.id"
              class="existing-item"
            >
              <span class="item-index">{{ idx + 1 }}</span>
              <span class="item-type" :class="`type-${item.questionType}`">
                {{ getQuestionTypeName(item.questionType) }}
              </span>
              <span class="item-name">{{ item.itemName || '未命名题目' }}</span>
              <span class="item-content">{{ truncateContent(item.questionContent) }}</span>
              <span class="item-score">{{ item.maxScore || 10 }} 分</span>
            </div>
          </div>
        </a-spin>
      </div>

      <!-- 无题目提示 -->
      <div v-if="!importResult && !loadingItems && isEdit && existingItems.length === 0" class="no-items-tip">
        <p>该实验暂无题目，请通过上方的导入功能添加实验步骤</p>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule } from 'ant-design-vue/es/form'
import {
  RollbackOutlined,
  UploadOutlined,
  SaveOutlined,
  DownloadOutlined,
  InboxOutlined,
  LockOutlined
} from '@ant-design/icons-vue'
import {
  addEduExperiment,
  updateEduExperiment,
  getEduExperimentVoById,
  uploadExperimentInstruction,
  importExperimentFromDocx,
  downloadExperimentTemplate
} from '@/api/eduExperimentController'
import { listEduExperimentItemVoByPage } from '@/api/eduExperimentItemController'

interface ExperimentFormState {
  id?: string
  name: string
  no: number
  type: number | null
  requirement: string
  content: string
  instructionType: string
  instructionFileName: string
  instructionFile: File | null
  instructionUrl: string
  classCodes: string[]
}

interface ClassOption {
  classCode: string
  headmasterName?: string
}

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const saving = ref(false)
const isEdit = ref(false)
const allClasses = ref<ClassOption[]>([])
const loadingClasses = ref(false)
const uploadingInstruction = ref(false)
const importing = ref(false)
const importResult = ref<any>(null)
const existingItems = ref<any[]>([])
const loadingItems = ref(false)

// 实验类型列表
const experimentTypes = [
  '编程实践',
  '设计实现',
  '数据库',
  '前端开发',
  '框架学习',
  '综合实验'
]

// 表单数据
const formState = reactive<ExperimentFormState>({
  id: undefined,
  name: '',
  no: 1,
  type: null,
  requirement: '',
  content: '',
  instructionType: '',
  instructionFileName: '',
  instructionFile: null,
  instructionUrl: '',
  classCodes: []
})

// 表单验证规则
const rules: Record<string, Rule[]> = {
  name: [{ required: true, message: '请输入实验名称', trigger: 'blur' }],
  no: [{ required: true, message: '请输入实验次序', trigger: 'blur' }],
  type: [{ required: true, message: '请选择实验类型', trigger: 'change' }],
  classCodes: [
    { required: true, message: '请至少选择一个发布班级', trigger: 'change', type: 'array', min: 1 }
  ]
}

// 页面标题
const pageTitle = computed(() => {
  return isEdit.value ? `编辑实验 - ${formState.name}` : '新建实验'
})

// 指导书文件选择（不立即上传，等保存成功后再上传）
function beforeUpload(file: File): boolean {
  const isValidType = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type)
  const suffix = file.name.split('.').pop()?.toLowerCase()
  if (!isValidType && !['pdf', 'doc', 'docx'].includes(suffix || '')) {
    message.error('只支持 PDF、DOC、DOCX 格式文件！')
    return false
  }
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    message.error('文件大小不能超过 10MB！')
    return false
  }
  formState.instructionFile = file
  formState.instructionType = file.name.split('.').pop()?.toUpperCase() || ''
  formState.instructionFileName = file.name
  message.info('文件已选择，保存实验后将自动上传')
  return false
}

// 导入步骤 docx 文件
async function beforeImportUpload(file: File): Promise<boolean> {
  if (!file.name.endsWith('.docx')) {
    message.error('仅支持 .docx 格式文件')
    return false
  }
  if (file.size > 20 * 1024 * 1024) {
    message.error('文件大小不能超过 20MB')
    return false
  }
  if (!formState.id) {
    message.warning('请先保存实验基本信息')
    return false
  }

  importing.value = true
  importResult.value = null
  try {
    const res: any = await importExperimentFromDocx(formState.id, file)
    const data = res?.data?.data ?? res?.data
    importResult.value = data
    if (data?.success) {
      message.success(`导入成功：${data.successCount} 道题目`)
    } else {
      message.error(data?.message || '导入失败，请检查文档格式')
    }
  } catch (err: any) {
    message.error(err?.message || '导入过程中发生错误')
  } finally {
    importing.value = false
  }
  return false
}

// 表单提交
async function onFinish(): Promise<void> {
  try {
    await formRef.value?.validate()
  } catch {
    message.error('请完整填写必填项')
    return
  }

  if (!formState.classCodes || formState.classCodes.length === 0) {
    message.error('请至少选择一个发布班级')
    return
  }

  saving.value = true
  try {
    const requestData: any = {
      sortOrder: formState.no,
      name: formState.name,
      categoryId: formState.type,
      requirement: formState.requirement,
      contentDesc: formState.content,
      fileType: formState.instructionType || null,
      classCodes: formState.classCodes
    }

    let savedId: string | undefined

    if (isEdit.value) {
      requestData.id = Number(formState.id)
      const res: any = await updateEduExperiment(requestData)
      if (res.data !== false) {
        savedId = formState.id
        message.success(`实验"${formState.name}"已更��`)
      }
    } else {
      const res: any = await addEduExperiment(requestData)
      const newId = res?.data?.data ?? res?.data
      if (newId) {
        savedId = String(newId)
        formState.id = savedId
        isEdit.value = true
        message.success(`实验"${formState.name}"已创建`)
      } else {
        // 部分后端成功时只返回 true
        message.success(`实验"${formState.name}"已创建，正在跳转...`)
        // 上传文件后跳列表
        await uploadInstructionIfNeeded(undefined)
        router.push('/admin/experiments/list')
        return
      }
    }

    // 若有待上传的指导书文件，立即上传
    if (savedId) {
      await uploadInstructionIfNeeded(savedId)
    }

    // 新建成功后跳转列表；编辑停留当前页
    if (!isEdit.value || !savedId) {
      router.push('/admin/experiments/list')
    } else {
      message.info('可继续在下方导入实验步骤')
    }
  } catch (error: any) {
    message.error(error.message || (isEdit.value ? '更新失败' : '创建失败'))
  } finally {
    saving.value = false
  }
}

// 上传指导书（如果用户选择了文件）
async function uploadInstructionIfNeeded(experimentId: string | undefined): Promise<void> {
  if (!formState.instructionFile) return
  const id = experimentId || formState.id
  if (!id) return

  uploadingInstruction.value = true
  try {
    const res: any = await uploadExperimentInstruction(id, formState.instructionFile)
    // attachment 接口返回 EduExperimentAttachmentVO，obsUrl 是访问地址
    const vo = res?.data?.data ?? res?.data
    const url = vo?.obsUrl ?? (typeof vo === 'string' ? vo : null)
    if (url) {
      formState.instructionUrl = url
      formState.instructionFileName = formState.instructionFile.name
      formState.instructionFile = null
      message.success('指导书上传成功')
    } else {
      message.warning('指导书上传失败，请稍后重试')
    }
  } catch (err: any) {
    message.warning('指导书上传失败，可稍后重新选择并保存')
  } finally {
    uploadingInstruction.value = false
  }
}

// 下载模板
async function handleTemplateDownload(): Promise<void> {
  try {
    await downloadExperimentTemplate()
  } catch (err: any) {
    message.error('模板下载失败，请稍后重试')
  }
}

// 获取题目类型名称
function getQuestionTypeName(type: number): string {
  const types: Record<number, string> = {
    1: '选择题',
    2: '填空题',
    3: '编程题',
    4: '简答题'
  }
  return types[type] || '未知类型'
}

// 截断内容显示
function truncateContent(content: string | null): string {
  if (!content) return '无内容'
  return content.length > 40 ? content.substring(0, 40) + '...' : content
}

// 加载实验数据（编辑模式）
async function loadExperimentData(id: string): Promise<void> {
  try {
    const res: any = await getEduExperimentVoById({ id } as any)
    const detail = res?.data?.data ?? res?.data
    if (detail && detail.id) {
      formState.id = String(detail.id)
      formState.no = detail.sortOrder || 1
      formState.name = detail.name || ''
      formState.type = detail.categoryId || null
      formState.requirement = detail.requirement || ''
      formState.content = detail.contentDesc || ''
      formState.instructionType = detail.fileType || ''
      formState.instructionFileName = detail.instructionUrl ? '已上传文件' : ''
      formState.instructionFile = null
      formState.instructionUrl = detail.instructionUrl || ''
      formState.classCodes = detail.classCodes || []
      // 加载关联的题目列表
      await loadExperimentItems(id)
    } else {
      message.error('未找到实验数据，请确认实验是否存在')
    }
  } catch (error) {
    message.error('加载实验数据失败')
  }
}

// 加载实验关联的题目列表
async function loadExperimentItems(experimentId: string): Promise<void> {
  loadingItems.value = true
  try {
    const res: any = await listEduExperimentItemVoByPage({
      experimentId: Number(experimentId),
      current: 1,
      pageSize: 100,
      sortField: 'sortOrder',
      sortOrder: 'ascend'
    })
    const data = res?.data?.data ?? res?.data
    existingItems.value = data?.records || []
  } catch (error) {
    console.error('加载题目列表失败:', error)
  } finally {
    loadingItems.value = false
  }
}

// 加载全部可选班级
async function loadAllClasses(): Promise<void> {
  loadingClasses.value = true
  try {
    const response = await fetch('/api/experiment/eduExperiment/all/classes', {
      credentials: 'include'
    })
    const text = await response.text()
    const data = JSON.parse(text)
    if (data?.code === 0) {
      allClasses.value = data.data || []
    }
  } catch (error) {
    console.error('加载班级列表失败:', error)
  } finally {
    loadingClasses.value = false
  }
}

onMounted(() => {
  loadAllClasses()
  const experimentId = route.params.id
  if (experimentId && experimentId !== '0') {
    isEdit.value = true
    loadExperimentData(String(experimentId))
  }
})
</script>

<style scoped>
.app-page-shell {
  padding: var(--space-5);
  min-height: 100%;
}

/* 面包屑导航 */
.breadcrumb-nav {
  margin-bottom: var(--space-5);
}
.breadcrumb-nav :deep(.ant-breadcrumb) { font-size: 14px; }
.breadcrumb-nav :deep(.ant-breadcrumb a) { color: var(--color-primary); transition: color 0.2s; }
.breadcrumb-nav :deep(.ant-breadcrumb a:hover) { color: var(--color-primary-hover); }

/* 页面头部卡片 */
.page-header-card {
  padding: var(--space-5);
  margin-bottom: var(--space-5);
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-5);
}
.page-header__eyebrow {
  margin: 0 0 8px;
  color: var(--color-primary);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}
.page-header__title {
  margin: 0;
  color: var(--color-text-main);
  font-size: 28px;
  font-weight: 700;
  line-height: 1.25;
}
.page-header__description {
  max-width: 600px;
  margin: 12px 0 0;
  color: var(--color-text-secondary);
  font-size: 14px;
  line-height: 1.65;
}
.page-header__actions { flex-shrink: 0; }

/* 表单卡片 */
.form-card {
  padding: var(--space-5);
  margin-bottom: var(--space-5);
}
.form-container { max-width: 800px; }
.experiment-form :deep(.ant-form-item-label > label) { font-weight: 600; color: var(--color-text-main); }
.experiment-form :deep(.ant-form-item) { margin-bottom: var(--space-5); }
.experiment-form :deep(.ant-input-textarea textarea) { font-size: 14px; line-height: 1.8; }

.form-row { display: flex; gap: var(--space-5); }
.form-row__item { flex: 1; }
.form-row__item--small { flex: 0 0 140px; }

/* 文件上传行 */
.file-upload-row { display: flex; align-items: center; gap: var(--space-4); }
.file-info { display: flex; align-items: center; gap: var(--space-3); }
.file-name { color: var(--color-text-secondary); font-size: 14px; }
.no-file { color: var(--color-text-tertiary); font-size: 14px; font-style: italic; }
.view-link {
  color: var(--color-primary);
  font-size: 13px;
  text-decoration: none;
}
.view-link:hover { text-decoration: underline; }

.form-help-text { margin-top: 8px; color: var(--color-text-tertiary); font-size: 12px; }
.class-selector-tip { width: 100%; }
.no-classes-hint { color: var(--color-text-tertiary); font-size: 14px; font-style: italic; }
.class-option { display: flex; align-items: center; gap: 8px; }
.class-code { font-weight: 600; color: var(--color-primary); }
.class-name { color: var(--color-text-secondary); font-size: 12px; }

.form-actions { margin-top: var(--space-6); margin-bottom: 0; }

/* ====== 导入步骤卡片 ====== */
.import-card {
  padding: var(--space-5);
  margin-bottom: var(--space-5);
}

.import-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-5);
  margin-bottom: var(--space-5);
}

.import-card__title-group { flex: 1; }

.import-card__title {
  margin: 0 0 8px;
  color: var(--color-text-main);
  font-size: 20px;
  font-weight: 700;
}

.import-card__desc {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 14px;
  line-height: 1.65;
}

.hint-warning {
  color: var(--color-warning, #fa8c16);
  font-size: 13px;
  font-weight: 500;
}

/* 模板下载按钮 */
.template-download-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  padding: 8px 18px;
  border: 1.5px solid var(--color-primary);
  border-radius: 6px;
  color: var(--color-primary);
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
  white-space: nowrap;
}
.template-download-btn:hover {
  background: var(--color-primary);
  color: #fff;
}

/* 格式说明条 */
.import-format-hint {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  padding: 12px 16px;
  background: var(--color-bg-muted);
  border-radius: 8px;
  margin-bottom: var(--space-5);
}
.format-hint__item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--color-text-secondary);
}
.format-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: var(--color-primary);
  color: #fff;
  font-size: 11px;
  font-weight: 700;
}
.format-hint__note {
  margin-left: auto;
  font-size: 12px;
  color: var(--color-text-tertiary);
  font-style: italic;
}

/* 上传区域 */
.import-upload-area {
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: var(--space-4);
}
.import-upload-area--disabled {
  opacity: 0.6;
  pointer-events: none;
}
.upload-dragger-content {
  padding: var(--space-6) var(--space-5);
  text-align: center;
}
.upload-dragger-icon {
  font-size: 40px;
  color: var(--color-primary);
  margin-bottom: var(--space-3);
}
.upload-dragger-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-main);
}
.upload-dragger-hint {
  margin: 0;
  font-size: 13px;
  color: var(--color-text-tertiary);
}

/* 禁用占位符 */
.upload-disabled-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: var(--space-6);
  border: 2px dashed var(--color-border);
  border-radius: 10px;
  color: var(--color-text-tertiary);
}
.upload-disabled-icon { font-size: 32px; }
.upload-disabled-placeholder p { margin: 0; font-size: 14px; }

/* 导入进度 */
.import-progress {
  display: flex;
  justify-content: center;
  padding: var(--space-5);
}

/* 导入结果 */
.import-result { margin-top: var(--space-4); }
.import-result__alert { margin-bottom: var(--space-4); }
.import-result__clear { padding: 0; font-size: 13px; }

.import-preview { margin-bottom: var(--space-4); }
.import-preview__title {
  margin: 0 0 12px;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-main);
}
.import-preview__item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 6px;
  background: var(--color-bg-muted);
  margin-bottom: 6px;
  font-size: 13px;
}
.preview-index {
  flex-shrink: 0;
  width: 22px;
  height: 22px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--color-primary);
  color: #fff;
  font-size: 11px;
  font-weight: 700;
}
.preview-type {
  flex-shrink: 0;
  padding: 2px 8px;
  border-radius: 4px;
  background: var(--color-primary-light, #e6f7ff);
  color: var(--color-primary);
  font-size: 12px;
  font-weight: 600;
}
.preview-name { font-weight: 600; color: var(--color-text-main); }
.preview-content { color: var(--color-text-tertiary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.import-fail-detail { padding: 12px 16px; background: #fff2f0; border-radius: 6px; }
.import-fail-detail__title { margin: 0 0 8px; color: var(--color-danger, #ff4d4f); font-weight: 600; font-size: 13px; }
.import-fail-detail__item { margin: 0 0 4px; font-size: 13px; color: var(--color-text-secondary); }

/* 已有题目列表 */
.existing-items { margin-top: var(--space-5); }
.existing-items__title {
  margin: 0 0 16px;
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-main);
  padding-bottom: 8px;
  border-bottom: 1px solid var(--color-border);
}
.existing-items__list { display: flex; flex-direction: column; gap: 8px; }
.existing-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  background: var(--color-bg-muted);
  transition: background 0.2s;
}
.existing-item:hover { background: var(--color-bg-hover, #f0f0f0); }
.item-index {
  flex-shrink: 0;
  width: 26px;
  height: 26px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--color-primary);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
}
.item-type {
  flex-shrink: 0;
  padding: 3px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}
.item-type.type-1 { background: #e6f7ff; color: #1890ff; }
.item-type.type-2 { background: #fff7e6; color: #fa8c16; }
.item-type.type-3 { background: #f6ffed; color: #52c41a; }
.item-type.type-4 { background: #f9f0ff; color: #722ed1; }
.item-name { font-weight: 600; color: var(--color-text-main); min-width: 80px; }
.item-content { flex: 1; color: var(--color-text-secondary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 13px; }
.item-score { flex-shrink: 0; padding: 2px 8px; background: #f5f5f5; border-radius: 4px; font-size: 12px; color: var(--color-text-tertiary); }

/* 无题目提示 */
.no-items-tip {
  padding: var(--space-5);
  text-align: center;
  color: var(--color-text-tertiary);
  background: var(--color-bg-muted);
  border-radius: 8px;
  margin-top: var(--space-4);
}
.no-items-tip p { margin: 0; font-size: 14px; }

/* 响应式 */
@media (max-width: 768px) {
  .app-page-shell { padding: var(--space-4); }
  .page-header { flex-direction: column; gap: var(--space-4); }
  .form-row { flex-direction: column; gap: 0; }
  .form-row__item--small { flex: 1; }
  .import-card__header { flex-direction: column; }
  .template-download-btn { width: 100%; justify-content: center; }
  .format-hint__note { display: none; }
}
</style>
