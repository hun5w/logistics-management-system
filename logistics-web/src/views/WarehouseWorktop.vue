<template>
  <div class="warehouse-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>🏭 仓储中转流水线 (模拟站点：上海分拨中心)</span>
        </div>
      </template>

      <el-table :data="transportingOrders" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="单号" width="200" />
        <el-table-column prop="receiverAddress" label="最终目的地" />

        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" type="success" @click="doArrive(scope.row)">
              扫描入库
            </el-button>
            <el-button size="small" type="warning" @click="doDepart(scope.row)">
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
// 过滤逻辑：只展示 status 为 2 的订单
const transportingOrders = computed(() => orders.value.filter(o => o.status === 2))

const fetchOrders = async () => {
  const res = await axios.get('http://localhost:8080/api/orders/all')
  orders.value = res.data
}

const doArrive = async (row) => {
  await axios.put(`http://localhost:8080/api/orders/arrive?id=${row.id}&location=上海分拨中心`)
  ElMessage.success('入库成功')
  fetchOrders()
}

const doDepart = async (row) => {
  ElMessageBox.prompt('请输入下一站目的地', '发货确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: '例如：杭州分拨中心'
  }).then(async ({ value }) => {
    await axios.put(`http://localhost:8080/api/orders/depart?id=${row.id}&nextStop=${value}`)
    ElMessage.success('已发货至：' + value)
    fetchOrders()
  })
}

onMounted(fetchOrders)
</script>