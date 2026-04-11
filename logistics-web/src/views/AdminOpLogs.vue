<template>
  <div class="admin-page">
    <el-card>
      <template #header>
        <div class="header-row">
          <span>🧾 管理员操作日志</span>
          <div class="actions">
            <el-button @click="fetchLogs" :loading="loading">刷新</el-button>
            <el-button type="success" plain @click="exportLogs">导出 CSV</el-button>
          </div>
        </div>
      </template>

      <el-table :data="logs" stripe v-loading="loading" class="data-table" max-height="520">
        <el-table-column prop="createTime" label="时间" width="170" />
        <el-table-column prop="operatorUsername" label="操作人" width="130" />
        <el-table-column prop="action" label="动作" width="160" />
        <el-table-column prop="targetUserId" label="目标用户ID" width="120" />
        <el-table-column prop="detail" label="详情" min-width="260" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const logs = ref([])
const loading = ref(false)

const headers = () => {
  const info = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return { 'User-Role': info.role || '', 'User-Name': info.username || '' }
}

const fetchLogs = async () => {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/users/op-logs', { headers: headers() })
    if (res.data.code === 200) logs.value = res.data.data || []
    else ElMessage.error(res.data.msg || '加载失败')
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const exportLogs = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/users/op-logs/export', {
      responseType: 'blob',
      headers: headers()
    })
    const blob = new Blob([res.data], { type: 'text/csv;charset=utf-8;' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.setAttribute('download', 'admin-op-logs.csv')
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

onMounted(fetchLogs)
</script>

<style scoped>
.admin-page { display: flex; flex-direction: column; gap: 14px; }
.header-row { display: flex; align-items: center; justify-content: space-between; font-size: 16px; font-weight: 600; }
.actions { display: flex; gap: 10px; }
.data-table { border-radius: 8px; overflow: hidden; }
</style>
