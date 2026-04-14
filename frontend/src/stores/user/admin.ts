import { ref } from 'vue'
import { defineStore } from 'pinia'
import { CommonUtil } from '@/utils'
import {
  classFixtures,
  studentRosterFixture,
  teacherRosterFixture
} from './fixtures'
import type {
  ClassRecord,
  StudentRosterItem,
  TeacherRosterItem
} from './types'

const userAdminRepository = {
  async fetchStudents(): Promise<StudentRosterItem[]> {
    await CommonUtil.sleep(80)
    return CommonUtil.deepClone(studentRosterFixture)
  },
  async fetchTeachers(): Promise<TeacherRosterItem[]> {
    await CommonUtil.sleep(90)
    return CommonUtil.deepClone(teacherRosterFixture)
  },
  async fetchClasses(): Promise<ClassRecord[]> {
    await CommonUtil.sleep(100)
    return CommonUtil.deepClone(classFixtures)
  }
}

export const useUserAdminStore = defineStore('user-admin', () => {
  const students = ref<StudentRosterItem[]>(CommonUtil.deepClone(studentRosterFixture))
  const teachers = ref<TeacherRosterItem[]>(CommonUtil.deepClone(teacherRosterFixture))
  const classes = ref<ClassRecord[]>(CommonUtil.deepClone(classFixtures))
  const loading = ref(false)
  const hydrated = ref(false)

  async function ensureReady(): Promise<void> {
    if (hydrated.value) {
      return
    }

    loading.value = true
    try {
      const [nextStudents, nextTeachers, nextClasses] = await Promise.all([
        userAdminRepository.fetchStudents(),
        userAdminRepository.fetchTeachers(),
        userAdminRepository.fetchClasses()
      ])
      students.value = nextStudents
      teachers.value = nextTeachers
      classes.value = nextClasses
      hydrated.value = true
    } finally {
      loading.value = false
    }
  }

  return {
    students,
    teachers,
    classes,
    loading,
    hydrated,
    ensureReady
  }
})
