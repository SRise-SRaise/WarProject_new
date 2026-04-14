<template>
  <div style="padding: 40px;">
    <h1 style="margin: 0 0 20px; color: #1a1a2e;">教学工作台</h1>
    <p style="color: #595959; margin: 0 0 30px;">欢迎回来，{{ authStore.session?.name }}</p>
    
    <div style="background: #fff; padding: 20px; border-radius: 8px; border: 1px solid #e8eaed;">
      <h2 style="margin: 0 0 15px;">功能导航</h2>
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 12px;">
        <button @click="navigate('/admin/users')" style="padding: 12px; border: 1px solid #e8eaed; border-radius: 6px; background: #fff; cursor: pointer; text-align: left;">
          <strong>用户管理</strong>
          <p style="margin: 4px 0 0; font-size: 12px; color: #8c929a;">管理教师和学生账号</p>
        </button>
        <button @click="navigate('/admin/materials')" style="padding: 12px; border: 1px solid #e8eaed; border-radius: 6px; background: #fff; cursor: pointer; text-align: left;">
          <strong>资料管理</strong>
          <p style="margin: 4px 0 0; font-size: 12px; color: #8c929a;">查看和管理教学资料</p>
        </button>
        <button @click="navigate('/admin/homework')" style="padding: 12px; border: 1px solid #e8eaed; border-radius: 6px; background: #fff; cursor: pointer; text-align: left;">
          <strong>作业管理</strong>
          <p style="margin: 4px 0 0; font-size: 12px; color: #8c929a;">发布和批改学生作业</p>
        </button>
        <button @click="navigate('/admin/exams')" style="padding: 12px; border: 1px solid #e8eaed; border-radius: 6px; background: #fff; cursor: pointer; text-align: left;">
          <strong>考试管理</strong>
          <p style="margin: 4px 0 0; font-size: 12px; color: #8c929a;">组织和管理学生考试</p>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/user/auth'

const router = useRouter()
const authStore = useAuthStore()

const navigate = (path: string) => {
  router.push(path)
}
</script>
