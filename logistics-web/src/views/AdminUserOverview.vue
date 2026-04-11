<template>
  <div class="admin-page">
    <el-card>
      <template #header>
        <div class="header-row">
          <span>👥 账号总览</span>
          <el-button @click="fetchUsers" :loading="loading">刷新</el-button>
        </div>
      </template>

      <el-row :gutter="14" class="summary-grid">
        <el-col :xs="12" :sm="12" :md="8" :lg="6" v-for="card in cards" :key="card.key">
          <el-card shadow="hover" class="summary-card">
            <div class="label">{{ card.label }}</div>
            <div class="value">{{ card.value }}</div>
          </el-card>
        </el-col>
      </el-row>

      <el-divider />

      <el-table :data="latestUsers" stripe v-loading="loading" class="data-table">
        <el-table-column prop="username" label="账号" width="150" />
        <el-table-column prop="nickname" label="昵称" width="140" />
        <el-table-column prop="role" label="角色" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const users = ref([])
const loading = ref(false)

const headers = () => {
  const info = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return { 'User-Role': info.role || '', 'User-Name': info.username || '' }
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/users/all', { headers: headers() })
    if (res.data.code === 200) {
      users.value = res.data.data || []
    } else {
      ElMessage.error(res.data.msg || '加载失败')
    }
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const cards = computed(() => {
  const total = users.value.length
  const admin = users.value.filter(u => u.role === 'ADMIN').length
  const wh = users.value.filter(u => u.role === 'WAREHOUSE').length
  const courier = users.value.filter(u => u.role === 'COURIER').length
  const user = users.value.filter(u => u.role === 'USER').length
  const disabled = users.value.filter(u => u.status === 0).length
  return [
    { key: 'total', label: '账号总数', value: total },
    { key: 'admin', label: '管理员', value: admin },
    { key: 'wh', label: '仓库员', value: wh },
    { key: 'courier', label: '快递员', value: courier },
    { key: 'user', label: '普通用户', value: user },
    { key: 'disabled', label: '禁用账号', value: disabled }
  ]
})

const latestUsers = computed(() => users.value.slice(0, 10))

onMounted(fetchUsers)
</script>

<style scoped>
.admin-page { display: flex; flex-direction: column; gap: 14px; }
.header-row { display: flex; align-items: center; justify-content: space-between; font-size: 16px; font-weight: 600; }
.summary-grid { margin-bottom: 10px; }
.summary-card { min-height: 94px; }
.label { color: #909399; font-size: 13px; margin-bottom: 8px; }
.value { color: #409eff; font-size: 28px; font-weight: 700; }
.data-table { border-radius: 8px; overflow: hidden; }
</style>
