<template>
  <div class="admin-page">
    <el-card>
      <template #header>
        <div class="header-row">
          <span>➕ 创建内部账号</span>
        </div>
      </template>

      <el-form :model="form" label-width="110px" class="form-wrap">
        <el-form-item label="账号">
          <el-input v-model.trim="form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model.trim="form.nickname" placeholder="可选" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model.trim="form.phone" placeholder="可选" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="仓库员" value="WAREHOUSE" />
            <el-option label="快递员" value="COURIER" />
            <el-option label="普通用户" value="USER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="submit">创建账号</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  role: 'WAREHOUSE'
})

const headers = () => {
  const info = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return { 'User-Role': info.role || '', 'User-Name': info.username || '' }
}

const submit = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('账号和密码不能为空')
    return
  }
  loading.value = true
  try {
    const res = await axios.post('http://localhost:8080/api/users/internal-create', {
      ...form,
      status: 1
    }, { headers: headers() })

    if (res.data.code === 200) {
      ElMessage.success('创建成功')
      resetForm()
    } else {
      ElMessage.error(res.data.msg || '创建失败')
    }
  } catch (e) {
    ElMessage.error('创建失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.username = ''
  form.password = ''
  form.nickname = ''
  form.phone = ''
  form.role = 'WAREHOUSE'
}
</script>

<style scoped>
.admin-page { display: flex; flex-direction: column; gap: 14px; }
.header-row { display: flex; align-items: center; justify-content: space-between; font-size: 16px; font-weight: 600; }
.form-wrap { max-width: 620px; }
</style>
