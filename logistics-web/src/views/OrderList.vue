<template>
  <div class="list-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>📋 订单管理全流程监控</span>
          <el-button type="primary" size="small" @click="fetchOrders">刷新数据</el-button>
        </div>
      </template>

      <el-table :data="orders" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="senderName" label="发件人" width="100" />
        <el-table-column prop="receiverName" label="收件人" width="100" />
        <el-table-column prop="fee" label="运费(元)" width="100" />

        <el-table-column label="当前状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusInfo(scope.row.status).type">
              {{ getStatusInfo(scope.row.status).label }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="物流操作">
          <template #default="scope">
            <el-button
                v-if="scope.row.status === 0"
                size="small" type="primary"
                @click="updateStatus(scope.row, 1)">确认揽件</el-button>

            <el-button
                v-if="scope.row.status === 1"
                size="small" type="warning"
                @click="updateStatus(scope.row, 2)">开始运输</el-button>

            <el-button
                v-if="scope.row.status === 2"
                size="small" type="success"
                @click="updateStatus(scope.row, 3)">设为派送中</el-button>

            <el-button
                v-if="scope.row.status === 3"
                size="small" type="danger"
                @click="updateStatus(scope.row, 4)">确认签收</el-button>

            <span v-if="scope.row.status === 4" style="color: #909399; font-size: 12px;">流程已结束</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const orders = ref([])
const loading = ref(false)

// 状态字典配置
const getStatusInfo = (status) => {
  const map = {
    0: { label: '待揽件', type: 'info' },
    1: { label: '已揽件', type: 'success' },
    2: { label: '运输中', type: 'warning' },
    3: { label: '派送中', type: '' },
    4: { label: '已签收', type: 'danger' }
  }
  return map[status] || { label: '未知', type: 'info' }
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/orders/all')
    orders.value = res.data
  } catch (error) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const updateStatus = async (row, nextStatus) => {
  try {
    // 调起后端现有的更新接口
    await axios.put(`http://localhost:8080/api/orders/status?id=${row.id}&status=${nextStatus}`)
    ElMessage.success('物流状态已更新')
    fetchOrders() // 刷新列表，确保页面不消失
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

onMounted(fetchOrders)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>