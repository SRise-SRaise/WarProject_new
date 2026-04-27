<template>
  <div class="app-page-shell">
    <!-- 面包屑导航 -->
    <div class="breadcrumb-nav">
      <a-breadcrumb>
        <a-breadcrumb-item>
          <a href="#" @click.prevent="router.push('/admin/experiments/list')">上机实验</a>
        </a-breadcrumb-item>
        <a-breadcrumb-item>修改</a-breadcrumb-item>
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
                <template v-if="formState.instructionType">
                  <a-tag color="blue">{{ formState.instructionType }}</a-tag>
                  <span class="file-name">{{ formState.instructionFileName }}</span>
                </template>
                <span v-else class="no-file">暂无指导文件</span>
              </div>
              <a-upload
                :before-upload="beforeUpload"
                :show-upload-list="false"
                accept=".pdf,.doc,.docx"
              >
                <a-button>
                  <template #icon><UploadOutlined /></template>
                  选择文件
                </a-button>
              </a-upload>
            </div>
            <p class="form-help-text">支持 PDF、DOC、DOCX 格式文件上传</p>
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
  SaveOutlined
} from '@ant-design/icons-vue'
import { addEduExperiment, updateEduExperiment, getEduExperimentVoById } from '@/api/eduExperimentController'

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
  /** 发布的班级编号列表 */
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

// 文件上传前处理
function beforeUpload(file: File): boolean {
  const isValidType = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type)
  if (!isValidType) {
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

  return false // 阻止自动上传
}

// 表单提交
async function onFinish(): Promise<void> {
  try {
    await formRef.value?.validate()
  } catch {
    message.error('请完整填写必填项')
    return
  }

  saving.value = true
  try {
    // 必填校验：班级必须选
    if (!formState.classCodes || formState.classCodes.length === 0) {
      message.error('请至少选择一个发布班级')
      saving.value = false
      return
    }

    // 构建请求数据
    const requestData: any = {
      sortOrder: formState.no,
      name: formState.name,
      categoryId: formState.type,
      requirement: formState.requirement,
      contentDesc: formState.content,
      fileType: formState.instructionType || null,
      classCodes: formState.classCodes
    }

    if (isEdit.value) {
      // 更新实验
      requestData.id = Number(formState.id)
      const res: any = await updateEduExperiment(requestData)
      if (res.data) {
        message.success(`实验"${formState.name}"已更新`)
        router.push('/admin/experiments/list')
      }
    } else {
      // 新建实验
      const res: any = await addEduExperiment(requestData)
      if (res.data) {
        message.success(`实验"${formState.name}"已创建`)
        router.push('/admin/experiments/list')
      }
    }
  } catch (error: any) {
    message.error(error.message || (isEdit.value ? '更新失败' : '创建失败'))
  } finally {
    saving.value = false
  }
}

// 加载实验数据（编辑模式）
async function loadExperimentData(id: string): Promise<void> {
  try {
    const res: any = await getEduExperimentVoById({ id } as any)
    // 兼容 res.data 和 res.data.data 两种后端返回结构
    const detail = res?.data?.data ?? res?.data
    if (detail && detail.id) {
      formState.id = String(detail.id)
      formState.no = detail.sortOrder || 1
      formState.name = detail.name || ''
      formState.type = detail.categoryId || null
      formState.requirement = detail.requirement || ''
      formState.content = detail.contentDesc || ''
      formState.instructionType = detail.fileType || ''
      formState.instructionFileName = ''
      formState.instructionFile = null
      // 回显已绑定的班级
      formState.classCodes = detail.classCodes || []
    } else {
      message.error('未找到实验数据，请确认实验是否存在')
    }
  } catch (error) {
    message.error('加载实验数据失败')
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
  // 加载全部班级供选择
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

.breadcrumb-nav :deep(.ant-breadcrumb) {
  font-size: 14px;
}

.breadcrumb-nav :deep(.ant-breadcrumb a) {
  color: var(--color-primary);
  transition: color 0.2s;
}

.breadcrumb-nav :deep(.ant-breadcrumb a:hover) {
  color: var(--color-primary-hover);
}

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

.page-header__actions {
  flex-shrink: 0;
}

/* 表单卡片 */
.form-card {
  padding: var(--space-5);
}

.form-container {
  max-width: 800px;
}

/* 表单样式 */
.experiment-form :deep(.ant-form-item-label > label) {
  font-weight: 600;
  color: var(--color-text-main);
}

.experiment-form :deep(.ant-form-item) {
  margin-bottom: var(--space-5);
}

.experiment-form :deep(.ant-input-textarea textarea) {
  font-size: 14px;
  line-height: 1.8;
}

/* 表单行布局 */
.form-row {
  display: flex;
  gap: var(--space-5);
}

.form-row__item {
  flex: 1;
}

.form-row__item--small {
  flex: 0 0 140px;
}

/* 文件上传行 */
.file-upload-row {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.file-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.file-name {
  color: var(--color-text-secondary);
  font-size: 14px;
}

.no-file {
  color: var(--color-text-tertiary);
  font-size: 14px;
  font-style: italic;
}

.form-help-text {
  margin-top: 8px;
  color: var(--color-text-tertiary);
  font-size: 12px;
}

.class-selector-tip {
  width: 100%;
}

.no-classes-hint {
  color: var(--color-text-tertiary);
  font-size: 14px;
  font-style: italic;
}

.class-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.class-code {
  font-weight: 600;
  color: var(--color-primary);
}

.class-name {
  color: var(--color-text-secondary);
  font-size: 12px;
}

/* 提交按钮 */
.form-actions {
  margin-top: var(--space-6);
  margin-bottom: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .app-page-shell {
    padding: var(--space-4);
  }

  .page-header {
    flex-direction: column;
    gap: var(--space-4);
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .form-row__item--small {
    flex: 1;
  }
}
</style>
