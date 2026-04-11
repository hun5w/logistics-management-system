<template>
  <div class="user-manage-page">
    <el-card>
      <template #header>
        <div class="header-row">
          <span>👤 用户与角色管理（仅管理员）</span>
          <div class="actions">
            <el-button type="primary" @click="openCreateDialog">新增内部账号</el-button>
            <el-button @click="fetchUsers" :loading="loading">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="users" stripe v-loading="loading" style="width: 100%" class="data-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="账号" width="150" />
        <el-table-column prop="nickname" label="昵称" width="140" />
        <el-table-column prop="phone" label="手机号" width="140" />

        <el-table-column label="角色" width="170">
          <template #default="scope">
            <el-select
              v-model="scope.row.role"
              size="small"
              style="width: 120px"
              @change="(val) => handleRoleChange(scope.row, val)"
            >
              <el-option label="管理员" value="ADMIN" />
              <el-option label="仓库员" value="WAREHOUSE" />
              <el-option label="快递员" value="COURIER" />
              <el-option label="普通用户" value="USER" />
            </el-select>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="150">
          <template #default="scope">
            <el-switch
              :model-value="scope.row.status === 1"
              active-text="正常"
              inactive-text="禁用"
              @change="(val) => handleStatusChange(scope.row, val)"
            />
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="warning" size="small" plain @click="handleResetPassword(scope.row)">重置密码</el-button>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" min-width="160" />
      </el-table>
    </el-card>

    <el-card style="margin-top: 14px;">
      <template #header>
        <div class="header-row">
          <span>🧾 管理员操作日志（最近 100 条）</span>
          <div class="actions">
            <el-button size="small" @click="fetchOpLogs">刷新日志</el-button>
            <el-button size="small" type="success" plain @click="handleExportLogs">导出 CSV</el-button>
          </div>
        </div>
      </template>

      <el-table :data="opLogs" stripe size="small" style="width: 100%" max-height="320" class="data-table">
        <el-table-column prop="createTime" label="时间" width="170" />
        <el-table-column prop="operatorUsername" label="操作人" width="130" />
        <el-table-column prop="action" label="动作" width="140" />
        <el-table-column prop="targetUserId" label="目标用户ID" width="120" />
        <el-table-column prop="detail" label="详情" min-width="220" />
      </el-table>
    </el-card>

    <el-dialog v-model="createDialogVisible" title="新增内部账号" width="520px">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="账号">
          <el-input v-model.trim="createForm.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="createForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model.trim="createForm.nickname" placeholder="可选" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model.trim="createForm.phone" placeholder="可选" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="createForm.role" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="仓库员" value="WAREHOUSE" />
            <el-option label="快递员" value="COURIER" />
            <el-option label="普通用户" value="USER" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreateUser">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const opLogs = ref([])
const loading = ref(false)
const creating = ref(false)
const createDialogVisible = ref(false)

const createForm = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  role: 'WAREHOUSE'
})

const getRoleHeader = () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return userInfo.role || ''
}

const getUsernameHeader = () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return userInfo.username || ''
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/users/all', {
      headers: { 'User-Role': getRoleHeader(), 'User-Name': getUsernameHeader() }
    })
    if (res.data.code === 200) {
      users.value = res.data.data || []
    } else {
      ElMessage.error(res.data.msg || '加载用户失败')
    }
  } catch (error) {
    ElMessage.error('加载用户失败，请检查后端服务')
  } finally {
    loading.value = false
  }
}

const handleRoleChange = async (row, targetRole) => {
  try {
    const res = await axios.put(
      `http://localhost:8080/api/users/role?id=${row.id}&targetRole=${targetRole}`,
      null,
      { headers: { 'User-Role': getRoleHeader(), 'User-Name': getUsernameHeader() } }
    )
    if (res.data.code === 200) {
      ElMessage.success('角色更新成功')
      fetchUsers()
      fetchOpLogs()
    } else {
      ElMessage.error(res.data.msg || '角色更新失败')
      fetchUsers()
    }
  } catch (error) {
    ElMessage.error('角色更新失败')
    fetchUsers()
  }
}

const handleStatusChange = async (row, enabled) => {
  const targetStatus = enabled ? 1 : 0
  try {
    const res = await axios.put(
      `http://localhost:8080/api/users/status?id=${row.id}&status=${targetStatus}`,
      null,
      { headers: { 'User-Role': getRoleHeader(), 'User-Name': getUsernameHeader() } }
    )
    if (res.data.code === 200) {
      ElMessage.success('账号状态更新成功')
      fetchUsers()
      fetchOpLogs()
    } else {
      ElMessage.error(res.data.msg || '状态更新失败')
      fetchUsers()
    }
  } catch (error) {
    ElMessage.error('状态更新失败')
    fetchUsers()
  }
}

const openCreateDialog = () => {
  createForm.username = ''
  createForm.password = ''
  createForm.nickname = ''
  createForm.phone = ''
  createForm.role = 'WAREHOUSE'
  createDialogVisible.value = true
}

const handleCreateUser = async () => {
  if (!createForm.username || !createForm.password) {
    ElMessage.warning('账号和密码不能为空')
    return
  }
  creating.value = true
  try {
    const payload = {
      username: createForm.username,
      password: createForm.password,
      nickname: createForm.nickname,
      phone: createForm.phone,
      role: createForm.role,
      status: 1
    }
    const res = await axios.post('http://localhost:8080/api/users/internal-create', payload, {
      headers: { 'User-Role': getRoleHeader(), 'User-Name': getUsernameHeader() }
    })
    if (res.data.code === 200) {
      ElMessage.success('内部账号创建成功')
      createDialogVisible.value = false
      fetchUsers()
      fetchOpLogs()
    } else {
      ElMessage.error(res.data.msg || '创建失败')
    }
  } catch (error) {
    ElMessage.error('创建失败')
  } finally {
    creating.value = false
  }
}

const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认将账号 ${row.username} 的密码重置为 123456 吗？`,
      '重置密码确认',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
    )
    const res = await axios.put(
      `http://localhost:8080/api/users/reset-password?id=${row.id}`,
      null,
      { headers: { 'User-Role': getRoleHeader(), 'User-Name': getUsernameHeader() } }
    )
    if (res.data.code === 200) {
      ElMessage.success('密码已重置为 123456')
      fetchOpLogs()
    } else {
      ElMessage.error(res.data.msg || '密码重置失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('密码重置失败')
    }
  }
}

const handleExportLogs = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/users/op-logs/export', {
      responseType: 'blob',
      headers: { 'User-Role': getRoleHeader(), 'User-Name': getUsernameHeader() }
    })
    const blob = new Blob([res.data], { type: 'text/csv;charset=utf-8;' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'admin-op-logs.csv')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('日志导出成功')
  } catch (error) {
    ElMessage.error('日志导出失败')
  }
}

const fetchOpLogs = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/users/op-logs', {
      headers: { 'User-Role': getRoleHeader(), 'User-Name': getUsernameHeader() }
    })
    if (res.data.code === 200) {
      opLogs.value = res.data.data || []
    } else {
      ElMessage.error(res.data.msg || '加载日志失败')
    }
  } catch (error) {
    ElMessage.error('加载日志失败')
  }
}

onMounted(() => {
  fetchUsers()
  fetchOpLogs()
})
</script>

<style scoped>
.user-manage-page {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.actions {
  display: flex;
  gap: 10px;
}

.data-table {
  border-radius: 8px;
  overflow: hidden;
}

@media (max-width: 768px) {
  .header-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>