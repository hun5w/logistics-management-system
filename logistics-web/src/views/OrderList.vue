<template>
  <div class="list-container">
    <el-card>
      <template #header><span>📋 订单管理后台</span></template>
      <el-table :data="orders" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="senderName" label="发件人" />
        <el-table-column prop="receiverName" label="收件人" />
        <el-table-column prop="fee" label="运费(元)" />
        <el-table-column label="状态">
          <template #default="scope">
            <el-tag :type="statusMap[scope.row.status].type">
              {{ statusMap[scope.row.status].label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button
                v-if="scope.row.status === 0"
                size="small"
                type="primary"
                @click="handlePickup(scope.row)">确认揽件</el-button>
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
const statusMap = {
  0: { label: '待揽件', type: 'info' },
  1: { label: '已揽件', type: 'success' }
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/orders/all')
    orders.value = res.data
  } finally {
    loading.value = false
  }
}

const handlePickup = async (row) => {
  try {
    await axios.put(`http://localhost:8080/api/orders/status?id=${row.id}&status=1`)
    ElMessage.success('揽件成功！')
    fetchOrders() // 刷新列表
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(fetchOrders)
</script>