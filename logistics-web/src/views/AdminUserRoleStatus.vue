<template>
  <div class="admin-page">
    <el-card>
      <template #header>
        <div class="header-row">
          <span>🛠️ 角色与状态维护</span>
          <el-button @click="fetchUsers" :loading="loading">刷新</el-button>
        </div>
      </template>

      <el-table :data="users" stripe v-loading="loading" class="data-table">
        <el-table-column prop="username" label="账号" width="150" />
        <el-table-column prop="nickname" label="昵称" width="140" />
        <el-table-column prop="phone" label="手机号" width="150" />

        <el-table-column label="角色" width="170">
          <template #default="scope">
            <el-select
              v-model="scope.row.role"
              size="small"
              style="width: 120px"
              @change="(val) => updateRole(scope.row, val)"
            >
              <el-option label="管理员" value="ADMIN" />
              <el-option label="仓库员" value="WAREHOUSE" />
              <el-option label="快递员" value="COURIER" />
              <el-option label="普通用户" value="USER" />
            </el-select>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="160">
          <template #default="scope">
            <el-switch
              :model-value="scope.row.status === 1"
              active-text="正常"
              inactive-text="禁用"
              @change="(val) => updateStatus(scope.row, val)"
            />
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

const updateRole = async (row, role) => {
  try {
    const res = await axios.put(`http://localhost:8080/api/users/role?id=${row.id}&targetRole=${role}`, null, { headers: headers() })
    if (res.data.code === 200) {
      ElMessage.success('角色更新成功')
    } else {
      ElMessage.error(res.data.msg || '角色更新失败')
      fetchUsers()
    }
  } catch (e) {
    ElMessage.error('角色更新失败')
    fetchUsers()
  }
}

const updateStatus = async (row, enabled) => {
  try {
    const status = enabled ? 1 : 0
    const res = await axios.put(`http://localhost:8080/api/users/status?id=${row.id}&status=${status}`, null, { headers: headers() })
    if (res.data.code === 200) {
      ElMessage.success('状态更新成功')
    } else {
      ElMessage.error(res.data.msg || '状态更新失败')
      fetchUsers()
    }
  } catch (e) {
    ElMessage.error('状态更新失败')
    fetchUsers()
  }
}

onMounted(fetchUsers)
</script>

<style scoped>
.admin-page { display: flex; flex-direction: column; gap: 14px; }
.header-row { display: flex; align-items: center; justify-content: space-between; font-size: 16px; font-weight: 600; }
.data-table { border-radius: 8px; overflow: hidden; }
</style>
