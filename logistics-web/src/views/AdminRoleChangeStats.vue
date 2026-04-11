<template>
  <div class="admin-page">
    <el-card>
      <template #header>
        <div class="header-row">
          <span>📈 角色变更统计</span>
          <el-button @click="fetchStats" :loading="loading">刷新</el-button>
        </div>
      </template>

      <el-row :gutter="14" class="summary-grid">
        <el-col :xs="12" :sm="12" :md="8" :lg="6">
          <el-card shadow="hover" class="summary-card">
            <div class="label">角色变更总次数</div>
            <div class="value">{{ totalRoleChanges }}</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="8" :lg="6" v-for="item in distribution" :key="item.role">
          <el-card shadow="hover" class="summary-card">
            <div class="label">变更为 {{ item.role }}</div>
            <div class="value">{{ item.count }}</div>
          </el-card>
        </el-col>
      </el-row>

      <el-divider />

      <el-table :data="recentLogs" stripe v-loading="loading" class="data-table">
        <el-table-column prop="createTime" label="时间" width="170" />
        <el-table-column prop="operatorUsername" label="操作人" width="130" />
        <el-table-column prop="targetUserId" label="目标用户ID" width="120" />
        <el-table-column prop="detail" label="变更详情" min-width="260" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const totalRoleChanges = ref(0)
const distribution = ref([])
const recentLogs = ref([])

const headers = () => {
  const info = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return { 'User-Role': info.role || '', 'User-Name': info.username || '' }
}

const fetchStats = async () => {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/users/role-change-stats', { headers: headers() })
    if (res.data.code === 200) {
      const data = res.data.data || {}
      totalRoleChanges.value = data.totalRoleChanges || 0
      distribution.value = data.distribution || []
      recentLogs.value = data.recent || []
    } else {
      ElMessage.error(res.data.msg || '加载失败')
    }
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(fetchStats)
</script>

<style scoped>
.admin-page { display: flex; flex-direction: column; gap: 14px; }
.header-row { display: flex; align-items: center; justify-content: space-between; font-size: 16px; font-weight: 600; color: #303133; }
.summary-grid { margin-bottom: 10px; }
.summary-card { min-height: 94px; }
.label { color: #909399; font-size: 13px; margin-bottom: 8px; }
.value { color: #409eff; font-size: 26px; font-weight: 700; }
.data-table { border-radius: 8px; overflow: hidden; }
</style>
