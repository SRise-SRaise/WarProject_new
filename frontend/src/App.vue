<template>
  <a-config-provider :locale="zhCN" :theme="antdTheme">
    <router-view />
  </a-config-provider>
</template>

<script setup lang="ts">
import zhCN from 'ant-design-vue/es/locale/zh_CN'

const antdTheme = {
  token: {
    colorPrimary: '#1f5fae',
    colorInfo: '#1f5fae',
    colorSuccess: '#2f8f4e',
    colorWarning: '#b7791f',
    colorError: '#b42318',
    borderRadius: 16,
    fontSize: 14,
    colorText: '#182433',
    colorTextSecondary: '#4d5a6b',
    colorBgLayout: '#eef3f8',
    colorBorder: '#d7deea',
    fontFamily: '"Segoe UI", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif'
  }
}
</script>
