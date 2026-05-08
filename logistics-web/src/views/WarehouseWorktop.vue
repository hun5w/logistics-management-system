<template>
  <div class="warehouse-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>🏭 仓储中转流水线 (模拟站点)</span>
        </div>
      </template>

      <el-table :data="transportingOrders" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="单号" width="200" />
        <el-table-column prop="receiverAddress" label="最终目的地" />
        <el-table-column label="当前状态" width="120">
          <template #default="scope">
            <el-tag type="info">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作">
          <template #default="scope">
            <el-button
                v-if="scope.row.status === 0"
                size="small"
                type="success"
                @click="doArrive(scope.row)">
              揽件入库
            </el-button>

            <el-button
                v-else-if="scope.row.status === 1"
                size="small"
                type="warning"
                @click="doDepart(scope.row)">
              装车出库
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
// 仓库可处理订单：待揽件(0)、已揽件(1) 或 运输中(2)
const transportingOrders = computed(() => orders.value.filter(o => o.status === 0 || o.status === 1 || o.status === 2))

const getStatusLabel = (status) => {
  const map = {
    0: '待揽件',
    1: '已揽件',
    2: '运输中'
  }
  return map[status] || '其他状态'
}

const getAuthHeaders = () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return {
    'User-Role': userInfo.role || '',
    'User-Name': userInfo.username || ''
  }
}

const fetchOrders = async () => {
  const res = await axios.get('http://localhost:8080/api/orders/all')
  orders.value = res.data
}

const doArrive = async (row) => {
  try {
    await axios.put(
      `http://localhost:8080/api/orders/arrive?id=${row.id}&location=上海分拨中心`,
      null,
      { headers: getAuthHeaders() }
    )
    ElMessage.success('入库成功')
    fetchOrders()
  } catch (e) {
    ElMessage.error(e?.response?.data?.msg || '入库失败')
  }
}

const doDepart = async (row) => {
  ElMessageBox.prompt('请输入下一站目的地', '发货确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: '例如：杭州分拨中心'
  }).then(async ({ value }) => {
    try {
      await axios.put(
        `http://localhost:8080/api/orders/depart?id=${row.id}&nextStop=${value}`,
        null,
        { headers: getAuthHeaders() }
      )
      ElMessage.success('已发货至：' + value)
      fetchOrders()
    } catch (e) {
      ElMessage.error(e?.response?.data?.msg || '出库失败')
    }
  })
}

onMounted(fetchOrders)
</script>