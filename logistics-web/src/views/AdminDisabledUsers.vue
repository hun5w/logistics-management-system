<template>
  <div class="admin-page">
    <el-card>
      <template #header>
        <div class="header-row">
          <span>🚫 禁用账号管理</span>
          <el-button @click="fetchDisabledUsers" :loading="loading">刷新</el-button>
        </div>
      </template>

      <el-table :data="users" stripe v-loading="loading" class="data-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="账号" width="150" />
        <el-table-column prop="nickname" label="昵称" width="140" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="role" label="角色" width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />

        <el-table-column label="操作" width="140">
          <template #default="scope">
            <el-button type="success" size="small" plain @click="enableUser(scope.row)">恢复启用</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && users.length === 0" description="暂无禁用账号" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const users = ref([])
const loading = ref(false)

const headers = () => {
  const info = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return { 'User-Role': info.role || '', 'User-Name': info.username || '' }
}

const fetchDisabledUsers = async () => {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/users/disabled', { headers: headers() })
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

const enableUser = async (row) => {
  try {
    const res = await axios.put(`http://localhost:8080/api/users/status?id=${row.id}&status=1`, null, { headers: headers() })
    if (res.data.code === 200) {
      ElMessage.success('账号已恢复')
      fetchDisabledUsers()
    } else {
      ElMessage.error(res.data.msg || '恢复失败')
    }
  } catch (e) {
    ElMessage.error('恢复失败')
  }
}

onMounted(fetchDisabledUsers)
</script>

<style scoped>
.admin-page { display: flex; flex-direction: column; gap: 14px; }
.header-row { display: flex; align-items: center; justify-content: space-between; font-size: 16px; font-weight: 600; color: #303133; }
.data-table { border-radius: 8px; overflow: hidden; }
</style>
