<script setup>
import { reactive, ref, computed } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Phone, Location } from '@element-plus/icons-vue'
import CustomCard from '../components/CustomCard.vue'
import CustomButton from '../components/CustomButton.vue'

const orderForm = reactive({
  senderName: '',
  senderPhone: '',
  senderAddress: '',
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  weight: 1.0,
  distance: 10.0
})

const loading = ref(false)

const previewFee = computed(() => {
  const basePrice = 10.00
  const weightPrice = orderForm.weight * 2.0
  let distancePrice = 0
  if (orderForm.distance > 100) {
    distancePrice = (orderForm.distance - 100) * 0.5
  }
  return (basePrice + weightPrice + distancePrice).toFixed(2)
})

const submitOrder = async () => {
  if (!orderForm.senderName || !orderForm.receiverName) {
    ElMessage.error('请填写完整的收发件人信息')
    return
  }

  loading.value = true
  try {
    const response = await axios.post('http://localhost:8080/api/orders/create', orderForm)

    ElMessageBox.alert('订单已成功创建！进入待揽件状态。\n\n' + (response.data || ''), '下单成功', {
      confirmButtonText: '确定',
      type: 'success'
    })

    Object.assign(orderForm, {
      senderName: '', senderPhone: '', senderAddress: '',
      receiverName: '', receiverPhone: '', receiverAddress: '',
      weight: 1.0, distance: 10.0
    })
  } catch (error) {
    console.error(error)
    ElMessage.error('连接后端服务失败，请检查后端是否启动')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="create-page">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-content container-sm">
        <h1 class="hero-title">
          快速
          <span class="gradient-text">下单</span>
        </h1>
        <p class="hero-subtitle">填写信息，一键发起物流需求</p>
      </div>
    </div>

    <!-- 表单卡片 -->
    <div class="form-container container-sm">
      <custom-card variant="elevated">
        <el-form :model="orderForm" @submit.prevent="submitOrder">
          <!-- 发件人信息 -->
          <div class="form-section">
            <h3 class="section-title">📤 发件人信息</h3>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item>
                  <el-input
                    v-model="orderForm.senderName"
                    placeholder="发件人姓名"
                    size="large"
                    clearable
                    prefix-icon="User"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item>
                  <el-input
                    v-model="orderForm.senderPhone"
                    placeholder="联系电话"
                    size="large"
                    clearable
                    prefix-icon="Phone"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item>
              <el-input
                v-model="orderForm.senderAddress"
                placeholder="详细地址 (从哪里出发？)"
                size="large"
                clearable
                rows="2"
                type="textarea"
                prefix-icon="Location"
              />
            </el-form-item>
          </div>

          <el-divider class="form-divider" />

          <!-- 收件人信息 -->
          <div class="form-section">
            <h3 class="section-title">📥 收件人信息</h3>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item>
                  <el-input
                    v-model="orderForm.receiverName"
                    placeholder="收件人姓名"
                    size="large"
                    clearable
                    prefix-icon="User"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item>
                  <el-input
                    v-model="orderForm.receiverPhone"
                    placeholder="联系电话"
                    size="large"
                    clearable
                    prefix-icon="Phone"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item>
              <el-input
                v-model="orderForm.receiverAddress"
                placeholder="详细地址 (送到哪里去？)"
                size="large"
                clearable
                rows="2"
                type="textarea"
                prefix-icon="Location"
              />
            </el-form-item>
          </div>

          <el-divider class="form-divider" />

          <!-- 货物与运输信息 -->
          <div class="form-section">
            <h3 class="section-title">📦 货物与运输信息</h3>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item>
                  <label class="input-label">货物重量 (kg)</label>
                  <el-input-number
                    v-model="orderForm.weight"
                    :min="0.1"
                    :precision="2"
                    :step="0.5"
                    size="large"
                    controls-position="right"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item>
                  <label class="input-label">预计距离 (km)</label>
                  <el-input-number
                    v-model="orderForm.distance"
                    :min="1"
                    :precision="1"
                    :step="10"
                    size="large"
                    controls-position="right"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <el-divider class="form-divider" />

          <!-- 费用预估 -->
          <div class="fee-estimate">
            <div class="fee-label">预估运费</div>
            <div class="fee-amount">
              <span class="currency">¥</span>
              <span class="value">{{ previewFee }}</span>
              <span class="unit">元</span>
            </div>
            <div class="fee-breakdown">
              <p>
                <span class="breakdown-item">基础费用: ¥10.00</span>
                <span class="breakdown-item">重量费: ¥{{ (orderForm.weight * 2.0).toFixed(2) }}</span>
                <span class="breakdown-item" v-if="orderForm.distance > 100">
                  距离费: ¥{{ ((orderForm.distance - 100) * 0.5).toFixed(2) }}
                </span>
              </p>
            </div>
          </div>

          <el-divider class="form-divider" />

          <!-- 提交按钮 -->
          <div class="form-actions">
            <custom-button
              variant="primary"
              size="lg"
              :loading="loading"
              :show-arrow="!loading"
              class="full-width"
              @click="submitOrder"
            >
              提交订单
            </custom-button>
            <p class="form-hint">✓ 订单提交后，进入「待揽件」状态</p>
          </div>
        </el-form>
      </custom-card>
    </div>
  </div>
</template>

<style scoped>
/* ============ 页面布局 ============ */
.create-page {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--color-background) 0%, #F0F4F8 100%);
  padding: 2rem 1rem;
}

/* ============ Hero Section ============ */
.hero-section {
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

/* ============ 表单容器 ============ */
.form-container {
  animation: fadeInUp 0.7s ease-out 0.2s both;

  :deep(.el-form) {
    display: flex;
    flex-direction: column;
    gap: 0;
  }

  :deep(.el-form-item) {
    margin-bottom: 0;
  }

  :deep(.el-input) {
    --el-input-height: 3rem;
    --el-input-border-color: var(--color-border);
    --el-input-focus-border-color: var(--color-accent);
    border-radius: var(--radius-lg) !important;
  }

  :deep(.el-textarea__inner) {
    border-radius: var(--radius-lg) !important;
  }

  :deep(.el-input.is-focus .el-input__wrapper,
    .el-textarea.is-focus .el-textarea__inner) {
    box-shadow: 0 0 0 3px rgba(0, 82, 255, 0.1) !important;
  }

  :deep(.el-input-number) {
    width: 100%;
    --el-input-height: 3rem;

    .el-input__wrapper {
      border-radius: var(--radius-lg) !important;
    }
  }
}

/* ============ 表单部分 ============ */
.form-section {
  padding: var(--spacing-lg) 0;

  &:not(:last-child) {
    border-bottom: 1px solid var(--color-border);
  }
}

.section-title {
  font-family: var(--font-display);
  font-size: 1.5rem;
  color: var(--color-foreground);
  margin-bottom: var(--spacing-lg);
}

.input-label {
  display: block;
  font-size: 0.875rem;
  color: var(--color-muted-foreground);
  font-weight: 500;
  margin-bottom: 0.5rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-family: var(--font-mono);
}

.form-divider {
  margin: 0;
  border-color: var(--color-border);

  :deep(.el-divider__text) {
    display: none;
  }
}

/* ============ 费用估算卡片 ============ */
.fee-estimate {
  padding: var(--spacing-lg) 0;
  border-bottom: 1px solid var(--color-border);
  background: linear-gradient(135deg, rgba(0, 82, 255, 0.03), rgba(77, 124, 255, 0.02));
  padding: var(--spacing-xl);
  border-radius: var(--radius-lg);
  margin: var(--spacing-lg) 0;
}

.fee-label {
  font-size: 0.875rem;
  color: var(--color-muted-foreground);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.75rem;
  font-family: var(--font-mono);
}

.fee-amount {
  display: flex;
  align-items: baseline;
  gap: 0.25rem;
  margin-bottom: 1rem;
}

.currency {
  font-size: 1.5rem;
  color: var(--color-accent);
  font-weight: 600;
}

.value {
  font-family: var(--font-display);
  font-size: 3rem;
  color: var(--color-accent);
  font-weight: 400;
  line-height: 1;
}

.unit {
  font-size: 1.125rem;
  color: var(--color-foreground);
  font-weight: 500;
}

.fee-breakdown {
  font-size: 0.875rem;
  color: var(--color-muted-foreground);

  p {
    margin: 0;
    display: flex;
    gap: 1.5rem;
    flex-wrap: wrap;
  }

  .breakdown-item {
    display: inline-block;
    padding: 0.25rem 0;
  }
}

/* ============ 表单操作 ============ */
.form-actions {
  padding: var(--spacing-lg) 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  align-items: stretch;
}

.full-width {
  width: 100%;
}

.form-hint {
  text-align: center;
  font-size: 0.875rem;
  color: var(--color-muted-foreground);
  margin: 0;
  line-height: 1.4;
}

/* ============ 容器类 ============ */
.container-sm {
  width: 100%;
  max-width: 48rem;
  margin: 0 auto;
  padding: 0 var(--spacing-md);
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

/* ============ 响应式 ============ */
@media (max-width: 640px) {
  .form-section {
    padding: var(--spacing-md) 0;
  }

  .section-title {
    font-size: 1.25rem;
  }

  .value {
    font-size: 2rem;
  }
}
</style>