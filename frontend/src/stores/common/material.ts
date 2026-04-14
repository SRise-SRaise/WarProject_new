import { computed, reactive, ref } from 'vue'
import { defineStore } from 'pinia'
import { CommonUtil } from '@/utils'
import { materialFixtures } from './fixtures'
import type {
  MaterialFilters,
  MaterialItem,
  SelectOption
} from './types'

const materialRepository = {
  async list(): Promise<MaterialItem[]> {
    await CommonUtil.sleep(100)
    return CommonUtil.deepClone(materialFixtures)
  },
  async getById(id: string): Promise<MaterialItem | null> {
    await CommonUtil.sleep(80)
    const matched = materialFixtures.find((item) => item.id === id)
    return matched ? CommonUtil.deepClone(matched) : null
  }
}

export const useMaterialStore = defineStore('common-material', () => {
  const materials = ref<MaterialItem[]>([])
  const currentMaterial = ref<MaterialItem | null>(null)
  const loading = ref(false)
  const hydrated = ref(false)
  const filters = reactive<MaterialFilters>({
    keyword: '',
    topicLabel: 'all',
    type: 'all'
  })

  const topicLabelOptions = computed<SelectOption[]>(() => {
    const topicLabels = CommonUtil.unique(materials.value.map((item) => item.topicLabel))
    return [
      { label: '全部主题', value: 'all' },
      ...topicLabels.map((topicLabel) => ({ label: topicLabel, value: topicLabel }))
    ]
  })

  const typeOptions = computed<SelectOption[]>(() => {
    const types = CommonUtil.unique(materials.value.map((item) => item.type))
    return [
      { label: '全部类型', value: 'all' },
      ...types.map((type) => ({ label: type, value: type }))
    ]
  })

  const filteredMaterials = computed(() => {
    const keyword = filters.keyword.trim().toLowerCase()
    return materials.value.filter((item) => {
      const matchesKeyword =
        keyword.length === 0 ||
        item.title.toLowerCase().includes(keyword) ||
        item.summary.toLowerCase().includes(keyword) ||
        item.tags.some((tag) => tag.toLowerCase().includes(keyword))
      const matchesTopicLabel = filters.topicLabel === 'all' || item.topicLabel === filters.topicLabel
      const matchesType = filters.type === 'all' || item.type === filters.type
      return matchesKeyword && matchesTopicLabel && matchesType
    })
  })

  const featuredMaterials = computed(() => materials.value.slice(0, 3))

  async function ensureLoaded(): Promise<void> {
    if (hydrated.value) {
      return
    }

    await refresh()
  }

  async function refresh(): Promise<void> {
    loading.value = true
    try {
      materials.value = await materialRepository.list()
      hydrated.value = true
    } finally {
      loading.value = false
    }
  }

  async function selectMaterial(id: string): Promise<MaterialItem | null> {
    await ensureLoaded()
    const cached = materials.value.find((item) => item.id === id)
    currentMaterial.value = cached ? CommonUtil.deepClone(cached) : await materialRepository.getById(id)
    return currentMaterial.value
  }

  function resetFilters(): void {
    filters.keyword = ''
    filters.topicLabel = 'all'
    filters.type = 'all'
  }

  return {
    materials,
    currentMaterial,
    loading,
    hydrated,
    filters,
    topicLabelOptions,
    typeOptions,
    filteredMaterials,
    featuredMaterials,
    ensureLoaded,
    refresh,
    selectMaterial,
    resetFilters
  }
})
