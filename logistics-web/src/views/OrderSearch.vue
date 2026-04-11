<template>
  <div class="search-container">
    <el-card class="search-card">
      <template #header>
        <div class="card-header">
          <span>🔍 订单物流轨迹查询</span>
        </div>
      </template>

      <div class="search-box">
        <el-input
            v-model.trim="searchNo"
            placeholder="请输入 LOG 开头的订单号"
            clearable
            @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" :loading="loading" @click="handleSearch">立即查询</el-button>
      </div>

      <div v-if="orderInfo.orderNo" class="result-content">
        <el-descriptions title="📦 订单基本信息" :column="2" border class="info-table">
          <el-descriptions-item label="订单编号">{{ orderInfo.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="statusMap[orderInfo.status]?.type || 'info'">
              {{ statusMap[orderInfo.status]?.label || '未知状态' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发件人">{{ orderInfo.senderName }}</el-descriptions-item>
          <el-descriptions-item label="收件人">{{ orderInfo.receiverName }}</el-descriptions-item>
        </el-descriptions>

        <div class="timeline-section">
          <h3 class="section-title">🕒 运输轨迹</h3>
          <el-empty v-if="!orderInfo.tracks || orderInfo.tracks.length === 0" description="暂无轨迹记录" />

          <el-timeline v-else>
            <el-timeline-item
                v-for="(track, index) in orderInfo.tracks"
                :key="index"
                :timestamp="formatDate(track.createTime)"
                :type="index === 0 ? 'primary' : ''"
                :hollow="index !== 0"
                size="large"
            >
              <span :class="{ 'latest-track': index === 0 }">
                {{ track.content }}
              </span>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>

      <el-empty v-else-if="hasSearched" description="未找到相关订单信息，请检查单号" />
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const searchNo = ref('')
const orderInfo = ref({})
const loading = ref(false)
const hasSearched = ref(false)

const statusMap = {
  0: { label: '待揽件', type: 'info' },
  1: { label: '已揽件', type: 'success' },
  2: { label: '运输中', type: 'warning' },
  3: { label: '派送中', type: '' },
  4: { label: '已签收', type: 'danger' }
}

// 时间格式化小工具
const formatDate = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString()
}

const handleSearch = async () => {
  const cleanNo = searchNo.value.trim()
  if (!cleanNo) {
    ElMessage.warning('请输入单号')
    return
  }

  loading.value = true
  hasSearched.value = true
  try {
    const res = await axios.get(`http://localhost:8080/api/orders/search?orderNo=${cleanNo}`)
    if (res.data && res.data.code === 200 && res.data.data) {
      orderInfo.value = res.data.data
    } else {
      orderInfo.value = {}
      ElMessage.error(res.data?.msg || '订单号不存在')
    }
  } catch (e) {
    ElMessage.error('查询失败，请检查网络')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.search-container { padding: 20px; max-width: 900px; margin: 0 auto; }
.search-box { display: flex; gap: 15px; margin-bottom: 30px; }
.info-table { margin-bottom: 30px; }
.section-title { margin-bottom: 20px; font-size: 16px; color: #303133; }
.timeline-section { padding: 20px; background: #f8f9fa; border-radius: 8px; }
.latest-track { font-weight: bold; color: #409EFF; }
</style>