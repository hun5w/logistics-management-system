<template>
  <div class="dashboard-page">
    <el-card class="header-card">
      <template #header>
        <div class="page-header">
          <div>
            <div class="page-title">📊 物流数据总览看板</div>
            <div class="page-subtitle">展示订单规模、状态分布、趋势变化、目的地热度和异常预警</div>
          </div>
          <el-button type="primary" :loading="loading" @click="fetchOverview">刷新数据</el-button>
        </div>
      </template>

      <el-row :gutter="16" class="summary-grid">
        <el-col :xs="12" :sm="12" :md="8" :lg="4" v-for="card in summaryCards" :key="card.key">
          <el-card class="summary-card" shadow="hover">
            <div class="summary-label">{{ card.label }}</div>
            <div class="summary-value">{{ card.value }}</div>
            <div class="summary-note">{{ card.note }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="16" class="chart-grid">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="section-title">状态分布</div>
          </template>

          <div v-if="statusDistribution.length" class="bar-list">
            <div v-for="item in statusDistribution" :key="item.status" class="bar-row">
              <div class="bar-head">
                <span>{{ item.label }}</span>
                <strong>{{ item.count }}</strong>
              </div>
              <div class="bar-track">
                <div class="bar-fill status-fill" :style="{ width: statusPercent(item.count) + '%' }"></div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无状态数据" />
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="section-title">近 7 天订单趋势</div>
          </template>

          <div v-if="orderTrend.length" class="trend-chart">
            <div v-for="item in orderTrend" :key="item.date" class="trend-item">
              <div class="trend-bar-wrap">
                <div class="trend-bar" :style="{ height: trendHeight(item.count) + '%' }"></div>
              </div>
              <div class="trend-count">{{ item.count }}</div>
              <div class="trend-date">{{ item.date }}</div>
            </div>
          </div>
          <el-empty v-else description="暂无趋势数据" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-grid">
      <el-col :xs="24" :lg="10">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="section-title">目的地热度</div>
          </template>

          <div v-if="destinationRanking.length" class="bar-list">
            <div v-for="item in destinationRanking" :key="item.name" class="bar-row">
              <div class="bar-head">
                <span>{{ item.name }}</span>
                <strong>{{ item.count }}</strong>
              </div>
              <div class="bar-track">
                <div class="bar-fill destination-fill" :style="{ width: destinationPercent(item.count) + '%' }"></div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无目的地数据" />
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="14">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="section-title">异常订单预警</div>
          </template>

          <el-table :data="abnormalOrders" stripe v-loading="loading" size="small" height="360">
            <el-table-column prop="orderNo" label="单号" width="170" />
            <el-table-column prop="receiverName" label="收件人" width="100" />
            <el-table-column prop="statusLabel" label="状态" width="90" />
            <el-table-column prop="reason" label="异常原因" min-width="180" />
            <el-table-column prop="lastUpdate" label="最后更新时间" width="150" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const overview = ref({
  summary: {},
  statusDistribution: [],
  orderTrend: [],
  destinationRanking: [],
  abnormalOrders: []
})

const fetchOverview = async () => {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/dashboard/overview')
    if (res.data && res.data.code === 200) {
      overview.value = res.data.data || overview.value
    } else {
      ElMessage.error(res.data?.msg || '加载看板失败')
    }
  } catch (error) {
    ElMessage.error('看板数据加载失败，请检查后端服务')
  } finally {
    loading.value = false
  }
}

const summaryCards = computed(() => {
  const summary = overview.value.summary || {}
  return [
    { key: 'totalOrders', label: '订单总量', value: summary.totalOrders ?? 0, note: '系统累计订单数' },
    { key: 'todayOrders', label: '今日新增', value: summary.todayOrders ?? 0, note: '当天创建订单' },
    { key: 'pendingOrders', label: '待揽件', value: summary.pendingOrders ?? 0, note: '等待仓库处理' },
    { key: 'inTransitOrders', label: '在途订单', value: summary.inTransitOrders ?? 0, note: '运输 / 派送中' },
    { key: 'finishedOrders', label: '已签收', value: summary.finishedOrders ?? 0, note: '完成闭环订单' },
    { key: 'abnormalOrders', label: '异常预警', value: summary.abnormalOrders ?? 0, note: '24 小时未更新' }
  ]
})

const statusDistribution = computed(() => overview.value.statusDistribution || [])
const orderTrend = computed(() => overview.value.orderTrend || [])
const destinationRanking = computed(() => overview.value.destinationRanking || [])
const abnormalOrders = computed(() => overview.value.abnormalOrders || [])

const maxStatusCount = computed(() => Math.max(1, ...statusDistribution.value.map(item => item.count || 0)))
const maxTrendCount = computed(() => Math.max(1, ...orderTrend.value.map(item => item.count || 0)))
const maxDestinationCount = computed(() => Math.max(1, ...destinationRanking.value.map(item => item.count || 0)))

const statusPercent = (count) => Math.round((count / maxStatusCount.value) * 100)
const trendHeight = (count) => Math.max(8, Math.round((count / maxTrendCount.value) * 100))
const destinationPercent = (count) => Math.round((count / maxDestinationCount.value) * 100)

onMounted(fetchOverview)
</script>

<style scoped>
.dashboard-page { display: flex; flex-direction: column; gap: 16px; }
.header-card { margin-bottom: 0; }
.page-header { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.page-title { font-size: 22px; font-weight: 700; color: #303133; }
.page-subtitle { margin-top: 6px; color: #909399; font-size: 13px; }
.summary-grid, .chart-grid { margin-top: 4px; }
.summary-card { text-align: left; min-height: 110px; }
.summary-label { font-size: 13px; color: #909399; margin-bottom: 8px; }
.summary-value { font-size: 30px; font-weight: 700; color: #409eff; line-height: 1.2; }
.summary-note { margin-top: 8px; font-size: 12px; color: #c0c4cc; }
.chart-card { height: 100%; }
.section-title { font-size: 16px; font-weight: 700; color: #303133; }
.bar-list { display: flex; flex-direction: column; gap: 14px; }
.bar-row { display: flex; flex-direction: column; gap: 8px; }
.bar-head { display: flex; justify-content: space-between; font-size: 13px; color: #606266; }
.bar-track { width: 100%; height: 10px; border-radius: 999px; background: #ebeef5; overflow: hidden; }
.bar-fill { height: 100%; border-radius: 999px; }
.status-fill { background: linear-gradient(90deg, #909399, #67c23a); }
.destination-fill { background: linear-gradient(90deg, #409eff, #79bbff); }
.trend-chart { display: flex; align-items: flex-end; gap: 12px; min-height: 240px; padding: 8px 0; }
.trend-item { flex: 1; display: flex; flex-direction: column; align-items: center; gap: 8px; min-width: 0; }
.trend-bar-wrap { width: 100%; height: 200px; display: flex; align-items: flex-end; justify-content: center; background: linear-gradient(180deg, rgba(64,158,255,0.06), rgba(64,158,255,0.02)); border-radius: 14px; padding: 12px 0; }
.trend-bar { width: 60%; min-height: 8px; border-radius: 12px 12px 4px 4px; background: linear-gradient(180deg, #67c23a, #409eff); transition: height 0.3s ease; }
.trend-count { font-weight: 700; color: #303133; }
.trend-date { font-size: 12px; color: #909399; }
@media (max-width: 768px) {
  .page-header { flex-direction: column; align-items: flex-start; }
  .trend-chart { gap: 8px; }
}
</style>