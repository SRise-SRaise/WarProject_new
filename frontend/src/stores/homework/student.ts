import { computed, reactive, ref } from 'vue'
import { defineStore } from 'pinia'
import { homeworkRepository } from './repository'
import type {
  HomeworkFilters,
  HomeworkSelectOption,
  HomeworkStudentItem
} from './types'

export const useHomeworkStudentStore = defineStore('homework-student', () => {
  const homeworks = ref<HomeworkStudentItem[]>([])
  const currentHomework = ref<HomeworkStudentItem | null>(null)
  const loading = ref(false)
  const hydrated = ref(false)
  const filters = reactive<HomeworkFilters>({ keyword: '', status: 'all', topicLabel: 'all' })

  const topicLabelOptions = computed<HomeworkSelectOption[]>(() => [
    { label: '全部主题', value: 'all' },
    ...Array.from(new Set(homeworks.value.map((item) => item.topicLabel))).map((topicLabel) => ({ label: topicLabel, value: topicLabel }))
  ])
  const statusOptions = computed<HomeworkSelectOption[]>(() => [
    { label: '全部状态', value: 'all' },
    { label: '待完成', value: 'pending' },
    { label: '已提交', value: 'submitted' },
    { label: '已批阅', value: 'reviewed' },
    { label: '已逾期', value: 'overdue' }
  ])

  const filteredHomeworks = computed(() => {
    const keyword = filters.keyword.trim().toLowerCase()
    return homeworks.value.filter((item) => {
      const matchesKeyword = keyword.length === 0 || item.title.toLowerCase().includes(keyword) || item.summary.toLowerCase().includes(keyword)
      const matchesStatus = filters.status === 'all' || item.status === filters.status
      const matchesTopicLabel = filters.topicLabel === 'all' || item.topicLabel === filters.topicLabel
      return matchesKeyword && matchesStatus && matchesTopicLabel
    })
  })

  async function ensureLoaded(): Promise<void> {
    if (hydrated.value) return
    await refresh()
  }

  async function refresh(): Promise<void> {
    loading.value = true
    try {
      homeworks.value = await homeworkRepository.listStudentHomeworks()
      hydrated.value = true
    } finally {
      loading.value = false
    }
  }

  async function selectHomework(id: string): Promise<HomeworkStudentItem | null> {
    await ensureLoaded()
    currentHomework.value = await homeworkRepository.getStudentHomeworkById(id)
    return currentHomework.value
  }

  async function submitCurrentHomework(content: string, fileName: string): Promise<HomeworkStudentItem | null> {
    if (!currentHomework.value) return null
    const updated = await homeworkRepository.submitHomework(currentHomework.value.id, content, fileName)
    await refresh()
    currentHomework.value = updated
    return updated
  }

  function resetFilters(): void {
    filters.keyword = ''
    filters.status = 'all'
    filters.topicLabel = 'all'
  }

  return {
    homeworks,
    currentHomework,
    loading,
    hydrated,
    filters,
    topicLabelOptions,
    statusOptions,
    filteredHomeworks,
    ensureLoaded,
    refresh,
    selectHomework,
    submitCurrentHomework,
    resetFilters
  }
})
