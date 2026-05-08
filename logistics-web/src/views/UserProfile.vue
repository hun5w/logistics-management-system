<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="header-copy">
        <div class="badge">个人中心</div>
        <h2>我的订单记录</h2>
        <p>仅显示当前登录普通用户的发件订单，确保数据隔离与安全。</p>
      </div>
      <custom-button variant="primary" size="md" @click="goCreate">
        继续下单
      </custom-button>
    </div>

    <custom-card class="profile-card">
      <div class="summary-row">
        <div class="summary-item">
          <div class="summary-label">订单总数</div>
          <div class="summary-value">{{ orders.length }}</div>
        </div>
        <div class="summary-item">
          <div class="summary-label">当前用户名</div>
          <div class="summary-value">{{ userInfo.username || '未知用户' }}</div>
        </div>
      </div>

      <div v-if="orders.length" class="orders-table">
        <div class="table-head">
          <span>订单号</span>
          <span>状态</span>
          <span>创建时间</span>
        </div>
        <div class="table-body">
          <div
            v-for="order in orders"
            :key="order.id"
            class="table-row"
          >
            <span class="order-no">{{ order.orderNo }}</span>
            <el-tag :type="statusMap[order.status]?.type || 'info'">
              {{ statusMap[order.status]?.label || '未知状态' }}
            </el-tag>
            <span>{{ formatDate(order.createTime) }}</span>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-icon">📦</div>
        <div class="empty-title">您还没有个人订单记录</div>
        <div class="empty-desc">下单后，订单将会出现在这里，方便随时查看物流状态。</div>
        <custom-button variant="outline" size="lg" @click="goCreate">
          立即下单
        </custom-button>
      </div>
    </custom-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import CustomCard from '../components/CustomCard.vue'
import CustomButton from '../components/CustomButton.vue'

const router = useRouter()
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const orders = ref([])
const loading = ref(false)

const statusMap = {
  0: { label: '待揽件', type: 'info' },
  1: { label: '已揽件', type: 'success' },
  2: { label: '运输中', type: 'warning' },
  3: { label: '派送中', type: 'info' },
  4: { label: '已签收', type: 'success' }
}

const formatDate = (value) => {
  if (!value) return ''
  const date = new Date(value)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const headers = {
      'User-Role': userInfo.role || '',
      'User-Name': userInfo.username || '',
      'User-Phone': userInfo.phone || ''
    }
    const res = await axios.get('http://localhost:8080/api/orders/mine', { headers })
    if (res.data?.code === 200) {
      orders.value = res.data.data || []
    } else {
      orders.value = []
      ElMessage.error(res.data?.msg || '获取订单失败')
    }
  } catch (error) {
    console.error(error)
    orders.value = []
    ElMessage.error('无法连接后端，请检查服务是否可用')
  } finally {
    loading.value = false
  }
}

const goCreate = () => {
  router.push('/create')
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  padding: 24px;
  background: #f5f7fb;
}

.profile-header {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.header-copy {
  max-width: 660px;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  border-radius: 999px;
  background: rgba(24, 144, 255, 0.12);
  color: #096dd9;
  font-size: 0.88rem;
  font-weight: 600;
}

.header-copy h2 {
  margin: 14px 0 8px;
  font-size: 2rem;
  color: #102a43;
}

.header-copy p {
  margin: 0;
  color: #5f6f84;
  line-height: 1.7;
}

.profile-card {
  padding: 24px;
}

.summary-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.summary-item {
  padding: 18px 20px;
  background: #ffffff;
  border: 1px solid rgba(15, 23, 42, 0.06);
  border-radius: 16px;
}

.summary-label {
  color: #7f8fa4;
  font-size: 0.88rem;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 1.9rem;
  font-weight: 700;
  color: #1f2a37;
}

.orders-table {
  background: #ffffff;
  border: 1px solid rgba(15, 23, 42, 0.06);
  border-radius: 16px;
  overflow: hidden;
}

.table-head,
.table-row {
  display: grid;
  grid-template-columns: 2.2fr 1fr 1.4fr;
  gap: 16px;
  align-items: center;
  padding: 16px 24px;
}

.table-head {
  background: #f0f5ff;
  color: #364a63;
  font-size: 0.95rem;
  font-weight: 600;
}

.table-row {
  border-top: 1px solid rgba(15, 23, 42, 0.06);
}

.order-no {
  color: #102a43;
  font-weight: 600;
}

.empty-state {
  text-align: center;
  padding: 48px 28px;
}

.empty-icon {
  font-size: 2.5rem;
  margin-bottom: 16px;
}

.empty-title {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 8px;
}

.empty-desc {
  color: #637381;
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .profile-header,
  .summary-row {
    grid-template-columns: 1fr;
  }

  .table-head,
  .table-row {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .table-head span,
  .table-row span,
  .table-row .el-tag {
    display: block;
  }
}
</style>
