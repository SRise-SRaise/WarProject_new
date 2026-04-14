<template>
  <div style="padding: 40px;">
    <h1 style="margin: 0 0 20px; color: #1a1a2e;">学习概览</h1>
    <p style="color: #595959; margin: 0 0 30px;">欢迎回来，{{ authStore.session?.name }}</p>
    
    <div style="background: #fff; padding: 20px; border-radius: 8px; border: 1px solid #e8eaed; margin-bottom: 20px;">
      <h2 style="margin: 0 0 15px;">快速导航</h2>
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 12px;">
        <button @click="navigate('/learning')" style="padding: 12px; border: 1px solid #e8eaed; border-radius: 6px; background: #fff; cursor: pointer; text-align: left; font-weight: 600;">
          <strong>📚 学习概览</strong>
          <p style="margin: 4px 0 0; font-size: 12px; color: #8c929a;">查看学习进度</p>
        </button>
        <button @click="navigate('/materials')" style="padding: 12px; border: 1px solid #e8eaed; border-radius: 6px; background: #fff; cursor: pointer; text-align: left; font-weight: 600;">
          <strong>📄 资料</strong>
          <p style="margin: 4px 0 0; font-size: 12px; color: #8c929a;">查看教学资料</p>
        </button>
        <button @click="navigate('/homework')" style="padding: 12px; border: 1px solid #e8eaed; border-radius: 6px; background: #fff; cursor: pointer; text-align: left; font-weight: 600;">
          <strong>✏️ 作业</strong>
          <p style="margin: 4px 0 0; font-size: 12px; color: #8c929a;">完成学习作业</p>
        </button>
        <button @click="navigate('/experiments')" style="padding: 12px; border: 1px solid #e8eaed; border-radius: 6px; background: #fff; cursor: pointer; text-align: left; font-weight: 600;">
          <strong>🔬 实验</strong>
          <p style="margin: 4px 0 0; font-size: 12px; color: #8c929a;">进行实验操作</p>
        </button>
        <button @click="navigate('/exams')" style="padding: 12px; border: 1px solid #e8eaed; border-radius: 6px; background: #fff; cursor: pointer; text-align: left; font-weight: 600;">
          <strong>📝 考试</strong>
          <p style="margin: 4px 0 0; font-size: 12px; color: #8c929a;">参加学业考试</p>
        </button>
        <button @click="navigate('/profile')" style="padding: 12px; border: 1px solid #e8eaed; border-radius: 6px; background: #fff; cursor: pointer; text-align: left; font-weight: 600;">
          <strong>👤 个人中心</strong>
          <p style="margin: 4px 0 0; font-size: 12px; color: #8c929a;">管理个人信息</p>
        </button>
      </div>
    </div>

    <div style="background: #f0f5ff; padding: 20px; border-radius: 8px; border-left: 4px solid #1677ff;">
      <h3 style="margin: 0 0 10px; color: #1a1a2e;">系统提示</h3>
      <p style="margin: 0; color: #595959;">所有功能正常运行。使用顶部导航菜单快速访问各个功能模块。</p>
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
