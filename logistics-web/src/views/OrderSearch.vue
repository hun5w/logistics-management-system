<template>
  <div class="search-container">
    <el-card class="search-card">
      <div style="display: flex; gap: 10px; margin-bottom: 30px;">
        <el-input v-model="searchNo" placeholder="请输入 LOG 开头的订单号" />
        <el-button type="primary" @click="handleSearch">搜索轨迹</el-button>
      </div>

      <el-empty v-if="!orderInfo.orderNo" description="暂无查询结果" />

      <div v-else>
        <el-descriptions title="订单基本信息" :column="2" border>
          <el-descriptions-item label="发件人">{{ orderInfo.senderName }}</el-descriptions-item>
          <el-descriptions-item label="收件人">{{ orderInfo.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="运费">￥{{ orderInfo.fee }}</el-descriptions-item>
        </el-descriptions>

        <el-divider>运输轨迹</el-divider>
        <el-timeline>
          <el-timeline-item timestamp="现在" placement="top" color="#409EFF">
            {{ statusTexts[orderInfo.status] }}
          </el-timeline-item>
          <el-timeline-item timestamp="下单时间" placement="top">
            订单已创建，等待揽件
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const searchNo = ref('')
const orderInfo = ref({})
const statusTexts = { 0: '待揽件', 1: '已揽件（处理中）', 2: '运输中', 3: '已签收' }

const handleSearch = async () => {
  if (!searchNo.value) return
  const res = await axios.get(`http://localhost:8080/api/orders/search?orderNo=${searchNo.value}`)
  if (res.data) {
    orderInfo.value = res.data
  } else {
    ElMessage.warning('未找到该订单')
  }
}
</script>