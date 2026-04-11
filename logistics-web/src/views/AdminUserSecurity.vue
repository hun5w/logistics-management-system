<template>
  <div class="admin-page">
    <el-card>
      <template #header>
        <div class="header-row">
          <span>🔐 密码重置中心</span>
          <el-button @click="fetchUsers" :loading="loading">刷新</el-button>
        </div>
      </template>

      <el-table :data="users" stripe v-loading="loading" class="data-table">
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
        <el-table-column label="安全操作" width="150">
          <template #default="scope">
            <el-button type="warning" size="small" plain @click="resetPassword(scope.row)">重置为123456</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

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
    if (res.data.code === 200) users.value = res.data.data || []
    else ElMessage.error(res.data.msg || '加载失败')
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const resetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(`确认重置账号 ${row.username} 的密码为 123456 吗？`, '重置确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await axios.put(`http://localhost:8080/api/users/reset-password?id=${row.id}`, null, { headers: headers() })
    if (res.data.code === 200) ElMessage.success('重置成功')
    else ElMessage.error(res.data.msg || '重置失败')
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('重置失败')
  }
}

onMounted(fetchUsers)
</script>

<style scoped>
.admin-page { display: flex; flex-direction: column; gap: 14px; }
.header-row { display: flex; align-items: center; justify-content: space-between; font-size: 16px; font-weight: 600; }
.data-table { border-radius: 8px; overflow: hidden; }
</style>
