<script setup>
import { reactive, ref, computed } from 'vue' // 引入 computed 用于计算预览运费
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// 1. 定义表单数据对象，增加 distance 字段
const orderForm = reactive({
  senderName: '',
  senderPhone: '',
  senderAddress: '',
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  weight: 1.0,
  distance: 10.0 // 默认距离 10km
})

const loading = ref(false)

// 2. 前端预览运费计算（需与后端逻辑保持一致，提升用户体验）
const previewFee = computed(() => {
  const basePrice = 10.00
  const weightPrice = orderForm.weight * 2.0
  let distancePrice = 0
  if (orderForm.distance > 100) {
    distancePrice = (orderForm.distance - 100) * 0.5
  }
  return (basePrice + weightPrice + distancePrice).toFixed(2)
})

// 3. 提交下单函数
const submitOrder = async () => {
  if (!orderForm.senderName || !orderForm.receiverName) {
    ElMessage.error('请填写完整的收发件人信息')
    return
  }

  loading.value = true
  try {
    // 此时提交的 orderForm 包含了 distance
    const response = await axios.post('http://localhost:8080/api/orders/create', orderForm)

    ElMessageBox.alert(response.data, '下单成功', {
      confirmButtonText: '确定',
      type: 'success'
    })

    // 重置表单
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
  <div class="order-container">
    <el-card class="order-card">
      <template #header>
        <div class="card-header">
          <span>🚚 物流管理系统 - 快速下单</span>
        </div>
      </template>

      <el-form :model="orderForm" label-width="110px">
        <el-divider content-position="left">发件人信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发件姓名">
              <el-input v-model="orderForm.senderName" placeholder="发件人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="orderForm.senderPhone" placeholder="联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址">
          <el-input v-model="orderForm.senderAddress" placeholder="从哪里出发？" />
        </el-form-item>

        <el-divider content-position="left">收件人信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收件姓名">
              <el-input v-model="orderForm.receiverName" placeholder="收件人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="orderForm.receiverPhone" placeholder="联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址">
          <el-input v-model="orderForm.receiverAddress" placeholder="送到哪里去？" />
        </el-form-item>

        <el-divider content-position="left">货物与运输信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货物重量(kg)">
              <el-input-number v-model="orderForm.weight" :min="0.1" :precision="2" :step="0.5" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计距离(km)">
              <el-input-number v-model="orderForm.distance" :min="1" :precision="1" :step="10" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="预估费用">
          <span class="fee-tip">￥ {{ previewFee }} 元</span>
          <span class="fee-note">(起步价10元 + 2元/kg + 超100km加价)</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitOrder" :loading="loading" size="large" style="width: 100%">
            提交订单 (进入待揽件状态)
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style>
.order-container {
  display: flex;
  justify-content: center;
  padding: 40px;
  background-color: #f5f7fa;
  min-height: 100vh;
}
.order-card {
  width: 100%;
  max-width: 850px;
}
.card-header {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}
.fee-tip {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}
.fee-note {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
.el-divider__text {
  font-weight: bold;
  color: #606266;
}
</style>