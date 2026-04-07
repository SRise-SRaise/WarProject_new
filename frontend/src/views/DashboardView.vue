<template>
  <div class="dashboard-view">
    <a-page-header title="工作台" sub-title="数据概览与系统运行状态" />

    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :sm="12" :lg="6">
        <StatCard
          title="总访问量"
          subtitle="指标说明"
          value="126560"
          :chart-data="[10, 20, 15, 30, 25, 40, 35]"
          chart-type="area"
          :daily-value="8846"
        />
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <StatCard
          title="总交易额"
          subtitle="指标说明"
          prefix="￥"
          value="258670"
          :trend="{ week: 12, day: 11 }"
        />
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <StatCard
          title="在线用户"
          subtitle="指标说明"
          value="1856"
          :chart-data="[5, 10, 8, 15, 12, 20, 18]"
          chart-type="bar"
          :progress="78"
        />
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <StatCard
          title="系统活跃度"
          subtitle="指标说明"
          value="95%"
          conversion="68%"
          :trend="{ week: 2, day: 5 }"
        />
      </a-col>
    </a-row>

    <a-row :gutter="[16, 16]" class="content-row">
      <a-col :xs="24" :lg="16">
        <a-card title="近七日访问趋势" :bordered="false" class="chart-card">
          <div class="bar-chart-container">
            <BarChart :data="barChartData" />
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="8">
        <a-card title="访问来源占比" :bordered="false" class="chart-card">
          <PieChart :data="pieChartData" />
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[16, 16]" class="content-row">
      <a-col :xs="24" :lg="24">
        <a-card title="数据趋势概览" :bordered="false" class="chart-card-large">
          <div class="line-chart-container">
            <LineChart :data="lineChartData" color="#52c41a" />
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[16, 16]" class="content-row">
      <a-col :xs="24" :lg="12">
        <a-card title="模块接入建议" :bordered="false">
          <a-steps direction="vertical" size="small" :current="1">
            <a-step title="新增业务路由" description="在 src/router/basic.ts 或 src/router/admin.ts 添加页面路由" />
            <a-step title="创建页面组件" description="在 src/views 下按业务域建立页面并复用现有布局" />
            <a-step title="接入 API 与状态" description="在 src/api 和 src/stores 中扩展接口与状态管理" />
          </a-steps>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="12">
        <a-card title="快捷入口" :bordered="false">
          <a-row :gutter="[16, 16]">
            <a-col :span="12" v-for="link in quickLinks" :key="link.name">
              <a-button block @click="router.push(link.path)">{{ link.name }}</a-button>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import StatCard from '@/components/dashboard/StatCard.vue'
import BarChart from '@/components/dashboard/BarChart.vue'
import PieChart from '@/components/dashboard/PieChart.vue'
import LineChart from '@/components/dashboard/LineChart.vue'

const router = useRouter()

const barChartData = [
  { month: '周一', value: 450 },
  { month: '周二', value: 620 },
  { month: '周三', value: 830 },
  { month: '周四', value: 540 },
  { month: '周五', value: 910 },
  { month: '周六', value: 780 },
  { month: '周日', value: 1020 }
]

const pieChartData = [
  { name: '直接访问', value: 4500 },
  { name: '邮件营销', value: 1200 },
  { name: '联盟广告', value: 1800 },
  { name: '视频广告', value: 2100 },
  { name: '搜索引擎', value: 3600 }
]

const lineChartData = [
  { month: '01', value: 120 },
  { month: '02', value: 132 },
  { month: '03', value: 101 },
  { month: '04', value: 134 },
  { month: '05', value: 90 },
  { month: '06', value: 230 },
  { month: '07', value: 210 },
  { month: '08', value: 180 },
  { month: '09', value: 250 },
  { month: '10', value: 320 },
  { month: '11', value: 280 },
  { month: '12', value: 350 }
]

const quickLinks = [
  { name: '前台首页', path: '/' },
  { name: '工具示例', path: '/utils-demo' },
  { name: '日期工具', path: '/date-util-demo' },
  { name: '登录页', path: '/user/login' }
]
</script>

<style scoped>
.dashboard-view {
  min-height: 100%;
}

.content-row {
  margin-top: 16px;
}

.chart-card {
  height: 400px;
}

.chart-card-large {
  height: 450px;
}

.bar-chart-container {
  height: 320px;
}

.line-chart-container {
  height: 370px;
}
</style>
