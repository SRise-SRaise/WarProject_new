<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2 class="title">通用管理系统</h2>
        <p class="subtitle">欢迎回到您的工作空间</p>
      </div>
      <a-form
        :model="formState"
        name="login"
        class="login-form"
        @finish="onFinish"
      >
        <a-form-item
          name="userAccount"
          :rules="[{ required: true, message: '请输入账号' }]"
        >
          <a-input v-model:value="formState.userAccount" placeholder="账号">
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item
          name="userPassword"
          :rules="[{ required: true, message: '请输入密码' }]"
        >
          <a-input-password v-model:value="formState.userPassword" placeholder="密码">
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <div class="form-options">
          <a-checkbox v-model:checked="formState.remember">记住我</a-checkbox>
          <a-button type="link" class="forgot-btn">忘记密码？</a-button>
        </div>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            class="login-button"
            :loading="loading"
          >
            登录
          </a-button>
        </a-form-item>

        <div class="register-link">
          还没有账号？<a @click="goToRegister">立即注册</a>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';

const router = useRouter();
const loading = ref(false);

const formState = reactive({
  userAccount: '',
  userPassword: '',
  remember: true,
});

const onFinish = async (values: any) => {
  loading.value = true;
  try {
    await Promise.resolve(values);
    message.success('登录示例：未启用真实鉴权');
    router.push('/admin/dashboard');
  } catch (error) {
    message.error('登录示例异常');
  } finally {
    loading.value = false;
  }
};

const goToRegister = () => {
  router.push('/user/register');
};
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 64px);
  background: var(--color-bg-page);
}

.login-card {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid var(--color-border);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text-main);
  margin-bottom: 8px;
}

.subtitle {
  color: var(--color-text-secondary);
  font-size: 14px;
}

.login-form :deep(.ant-input-affix-wrapper) {
  padding: 8px 11px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.login-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
  background-color: var(--color-primary);
  border-color: var(--color-primary);
}

.login-button:hover {
  background-color: var(--color-primary-hover);
}

.register-link {
  text-align: center;
  margin-top: 16px;
  color: var(--color-text-secondary);
}

.register-link a {
  color: var(--color-primary);
  cursor: pointer;
}
</style>
