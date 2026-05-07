<template>
  <div class="search-page">
    <div class="search-hero">
      <div class="hero-content container-sm">
        <h1 class="hero-title">
          查询物流
          <span class="gradient-text">轨迹</span>
        </h1>
        <p class="hero-subtitle">输入订单号，实时追踪您的包裹位置</p>
      </div>
    </div>

    <div class="search-section container-sm">
      <custom-card variant="elevated" class="search-card">
        <!-- 搜索框 -->
        <div class="search-box-wrapper">
          <el-input
            v-model.trim="searchNo"
            placeholder="请输入 LOG 开头的订单号"
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <span class="search-icon">🔍</span>
            </template>
          </el-input>
          <custom-button
            variant="primary"
            size="lg"
            :loading="loading"
            :show-arrow="!loading"
            @click="handleSearch"
          >
            查询追踪
          </custom-button>
        </div>

        <!-- 查询结果区域 -->
        <div v-if="orderInfo.orderNo" class="result-content">
          <!-- 订单信息卡片 -->
          <div class="order-info-card">
            <div class="info-header">
              <span class="order-no">{{ orderInfo.orderNo }}</span>
              <el-tag
                :type="statusMap[orderInfo.status]?.type || 'info'"
                class="status-badge"
              >
                {{ statusMap[orderInfo.status]?.label || '未知状态' }}
              </el-tag>
            </div>

            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">📤 发件人</span>
                <span class="info-value">{{ orderInfo.senderName }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">📥 收件人</span>
                <span class="info-value">{{ orderInfo.receiverName }}</span>
              </div>
            </div>
          </div>

          <!-- 轨迹时间线 -->
          <div class="timeline-wrapper">
            <h3 class="section-heading">📍 运输轨迹</h3>

            <el-empty v-if="!orderInfo.tracks || orderInfo.tracks.length === 0" description="暂无轨迹记录" />

            <div v-else class="timeline-container">
              <div
                v-for="(track, index) in orderInfo.tracks"
                :key="index"
                :class="['timeline-item', { 'is-latest': index === 0 }]"
              >
                <div class="timeline-marker">
                  <span v-if="index === 0" class="marker-icon">✓</span>
                  <span v-else class="marker-dot"></span>
                </div>

                <div class="timeline-content">
                  <div class="track-time">{{ formatDate(track.createTime) }}</div>
                  <div class="track-text">{{ track.content }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 未搜索或无结果 -->
        <div v-else-if="hasSearched" class="empty-state">
          <div class="empty-icon">❌</div>
          <p class="empty-text">未找到相关订单，请检查单号是否正确</p>
          <custom-button
            variant="outline"
            size="md"
            @click="resetSearch"
          >
            重新查询
          </custom-button>
        </div>

        <!-- 提示文案 -->
        <div v-if="!hasSearched" class="hint-box">
          <p>💡 <strong>提示：</strong> 订单号以 LOG 开头，例如 LOG20250507001</p>
          <p>没有账号？ 可直接在此查询。想要完整功能？ <router-link to="/login">注册登录</router-link></p>
        </div>
      </custom-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import CustomCard from '../components/CustomCard.vue'
import CustomButton from '../components/CustomButton.vue'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
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

const formatDate = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
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

const resetSearch = () => {
  searchNo.value = ''
  orderInfo.value = {}
  hasSearched.value = false
}
</script>

<style scoped>
/* ============ 页面布局 ============ */
.search-page {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--color-background) 0%, #F0F4F8 100%);
  padding: 2rem 1rem;
}

/* ============ Hero Section ============ */
.search-hero {
  margin-bottom: 3rem;
  animation: fadeInUp 0.7s ease-out;
}

.hero-content {
  text-align: center;
}

.hero-title {
  font-family: var(--font-display);
  font-size: 3.5rem;
  line-height: 1.1;
  color: var(--color-foreground);
  margin-bottom: 1rem;

  @media (max-width: 640px) {
    font-size: 2.5rem;
  }

  .gradient-text {
    background: linear-gradient(to right, var(--color-accent), var(--color-accent-secondary));
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
  }
}

.hero-subtitle {
  font-size: 1.25rem;
  color: var(--color-muted-foreground);
  line-height: 1.5;
  margin: 0;

  @media (max-width: 640px) {
    font-size: 1rem;
  }
}

/* ============ 搜索卡片 ============ */
.search-section {
  animation: fadeInUp 0.7s ease-out 0.2s both;
}

.search-card {
  padding: var(--spacing-xl);

  @media (max-width: 640px) {
    padding: var(--spacing-lg);
  }
}

/* ============ 搜索框 ============ */
.search-box-wrapper {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 1rem;
  margin-bottom: 2rem;
  align-items: flex-end;

  @media (max-width: 640px) {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }

  :deep(.el-input) {
    --el-input-height: 3rem;
    --el-input-border-color: var(--color-border);
    --el-input-focus-border-color: var(--color-accent);
    border-radius: var(--radius-lg) !important;
  }

  :deep(.el-input.is-focus .el-input__wrapper) {
    box-shadow: 0 0 0 3px rgba(0, 82, 255, 0.1) !important;
  }
}

.search-icon {
  font-size: 1.25rem;
}

/* ============ 结果内容区 ============ */
.result-content {
  animation: fadeInUp 0.5s ease-out;
}

/* ============ 订单信息卡片 ============ */
.order-info-card {
  background: linear-gradient(135deg, rgba(0, 82, 255, 0.03), rgba(77, 124, 255, 0.02));
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-2xl);
}

.info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-lg);
  gap: var(--spacing-md);

  @media (max-width: 640px) {
    flex-direction: column;
    align-items: flex-start;
  }
}

.order-no {
  font-family: var(--font-mono);
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-accent);
  letter-spacing: 0.05em;
}

.status-badge {
  font-size: 0.875rem;
  font-weight: 600;
  padding: 0.5rem 1rem;
  border-radius: var(--radius-lg);
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-lg);

  @media (max-width: 640px) {
    grid-template-columns: 1fr;
  }
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.info-label {
  font-size: 0.875rem;
  color: var(--color-muted-foreground);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-family: var(--font-mono);
}

.info-value {
  font-size: 1.125rem;
  color: var(--color-foreground);
  font-weight: 500;
}

/* ============ 时间线 ============ */
.timeline-wrapper {
  margin-top: 2rem;
}

.section-heading {
  font-family: var(--font-display);
  font-size: 1.5rem;
  color: var(--color-foreground);
  margin-bottom: var(--spacing-lg);
}

.timeline-container {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  position: relative;

  /* 竖线连接 */
  &::before {
    content: '';
    position: absolute;
    left: 16px;
    top: 32px;
    bottom: 0;
    width: 2px;
    background: var(--color-border);
  }
}

.timeline-item {
  display: flex;
  gap: var(--spacing-lg);
  padding: var(--spacing-md) 0;
  position: relative;
  animation: fadeInUp 0.5s ease-out both;

  &:nth-child(1) { animation-delay: 0.1s; }
  &:nth-child(2) { animation-delay: 0.2s; }
  &:nth-child(3) { animation-delay: 0.3s; }
  &:nth-child(n+4) { animation-delay: 0.4s; }

  &.is-latest {
    .track-text {
      font-weight: 600;
      color: var(--color-accent);
    }
  }
}

.timeline-marker {
  flex-shrink: 0;
  width: 3rem;
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 2;
}

.marker-icon {
  width: 2rem;
  height: 2rem;
  background: linear-gradient(to right, var(--color-accent), var(--color-accent-secondary));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-weight: bold;
  font-size: 1rem;
  box-shadow: var(--shadow-accent-md);
  animation: pulse 2s ease-in-out infinite;
}

.marker-dot {
  width: 1rem;
  height: 1rem;
  background: var(--color-accent);
  border-radius: 50%;
  box-shadow: 0 0 0 4px rgba(0, 82, 255, 0.1);
}

.timeline-content {
  padding-top: 0.25rem;
}

.track-time {
  font-size: 0.875rem;
  color: var(--color-muted-foreground);
  font-family: var(--font-mono);
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.track-text {
  font-size: 1rem;
  color: var(--color-foreground);
  line-height: 1.5;
  transition: color var(--transition-normal);
}

/* ============ 空状态 ============ */
.empty-state {
  text-align: center;
  padding: var(--spacing-2xl) var(--spacing-lg);
  animation: fadeInUp 0.5s ease-out;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: var(--spacing-lg);
}

.empty-text {
  font-size: 1.125rem;
  color: var(--color-muted-foreground);
  margin-bottom: var(--spacing-lg);
}

/* ============ 提示框 ============ */
.hint-box {
  margin-top: 2rem;
  padding: var(--spacing-lg);
  background: rgba(0, 82, 255, 0.03);
  border-left: 4px solid var(--color-accent);
  border-radius: var(--radius-lg);

  p {
    margin: 0.5rem 0;
    font-size: 0.95rem;
    color: var(--color-muted-foreground);

    &:last-child {
      margin-bottom: 0;
    }

    strong {
      color: var(--color-foreground);
    }

    a {
      color: var(--color-accent);
      font-weight: 600;
      text-decoration: underline;
    }
  }
}

/* ============ 动画 ============ */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(1.75rem);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    box-shadow: var(--shadow-accent-md), 0 0 0 0 rgba(0, 82, 255, 0.7);
  }
  70% {
    box-shadow: var(--shadow-accent-md), 0 0 0 8px rgba(0, 82, 255, 0);
  }
}

/* ============ 容器类 ============ */
.container-sm {
  width: 100%;
  max-width: 48rem;
  margin: 0 auto;
  padding: 0 var(--spacing-md);
}
</style>