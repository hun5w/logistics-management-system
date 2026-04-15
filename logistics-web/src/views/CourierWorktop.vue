<template>
  <div class="courier-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>🛵 快递员末端派送系统</span>
        </div>
      </template>

      <el-table :data="courierOrders" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="单号" width="180" />
        <el-table-column prop="receiverName" label="收件人" width="100" />
        <el-table-column prop="receiverAddress" label="配送地址" />

        <el-table-column label="配送操作">
          <template #default="scope">
            <el-button
                v-if="scope.row.status === 2"
                size="small" type="primary"
                @click="handleAction(scope.row, 3)">开始派送</el-button>

            <el-button
                v-if="scope.row.status === 3"
                size="small" type="success"
                @click="handleAction(scope.row, 4)">确认签收</el-button>

            <el-tag v-if="scope.row.status === 4" type="info">已完结</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const allOrders = ref([])
// 过滤出需要快递员处理的订单（状态2运输中 或 状态3派送中）
const courierOrders = computed(() =>
    allOrders.value.filter(o => o.status === 2 || o.status === 3)
)

const getAuthHeaders = () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return {
    'User-Role': userInfo.role || '',
    'User-Name': userInfo.username || ''
  }
}

const fetchOrders = async () => {
  const res = await axios.get('http://localhost:8080/api/orders/all')
  allOrders.value = res.data
}

const handleAction = async (row, nextStatus) => {
  try {
    await axios.put(
      `http://localhost:8080/api/orders/status?id=${row.id}&status=${nextStatus}`,
      null,
      { headers: getAuthHeaders() }
    )
    ElMessage.success(nextStatus === 3 ? '开始派送中...' : '订单已签收！')
    fetchOrders()
  } catch (e) {
    ElMessage.error(e?.response?.data?.msg || '操作失败')
  }
}

onMounted(fetchOrders)
</script>