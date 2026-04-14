import { defineStore } from 'pinia'
import { ref } from 'vue'
import { CommonUtil } from '@/utils'
import {
  publicSnapshotFixture,
  studentOverviewFixture,
  teacherDashboardFixture
} from './fixtures'
import type {
  PublicSnapshot,
  StudentOverviewData,
  TeacherDashboardData
} from './types'

const appRepository = {
  async fetchPublicSnapshot(): Promise<PublicSnapshot> {
    await CommonUtil.sleep(80)
    return CommonUtil.deepClone(publicSnapshotFixture)
  },
  async fetchStudentOverview(): Promise<StudentOverviewData> {
    await CommonUtil.sleep(100)
    return CommonUtil.deepClone(studentOverviewFixture)
  },
  async fetchTeacherDashboard(): Promise<TeacherDashboardData> {
    await CommonUtil.sleep(120)
    return CommonUtil.deepClone(teacherDashboardFixture)
  }
}

export const useAppStore = defineStore('common-app', () => {
  const publicSnapshot = ref<PublicSnapshot>(CommonUtil.deepClone(publicSnapshotFixture))
  const studentOverview = ref<StudentOverviewData>(CommonUtil.deepClone(studentOverviewFixture))
  const teacherDashboard = ref<TeacherDashboardData>(CommonUtil.deepClone(teacherDashboardFixture))
  const backendCollapsed = ref(false)
  const hydrated = ref(false)
  const loading = ref(false)

  async function ensureReady(): Promise<void> {
    if (hydrated.value) {
      return
    }

    loading.value = true
    try {
      const [nextPublicSnapshot, nextStudentOverview, nextTeacherDashboard] = await Promise.all([
        appRepository.fetchPublicSnapshot(),
        appRepository.fetchStudentOverview(),
        appRepository.fetchTeacherDashboard()
      ])

      publicSnapshot.value = nextPublicSnapshot
      studentOverview.value = nextStudentOverview
      teacherDashboard.value = nextTeacherDashboard
      hydrated.value = true
    } finally {
      loading.value = false
    }
  }

  function setBackendCollapsed(value: boolean): void {
    backendCollapsed.value = value
  }

  return {
    publicSnapshot,
    studentOverview,
    teacherDashboard,
    backendCollapsed,
    hydrated,
    loading,
    ensureReady,
    setBackendCollapsed
  }
})
