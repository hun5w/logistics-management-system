<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>🚚 物流管理系统登录</h2>
      </template>
      <el-form :model="loginForm" label-width="60px">
        <el-form-item label="账号">
          <el-input v-model="loginForm.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">
          立即登录
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const loginForm = ref({ username: '', password: '' })

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  loading.value = true
  try {
    const res = await axios.post('http://localhost:8080/api/auth/login', loginForm.value)
    if (res.data.code === 200) {
      ElMessage.success('欢迎回来，' + res.data.data.nickname)
      // 🚩 关键：将用户信息持久化存储
      localStorage.setItem('userInfo', JSON.stringify(res.data.data))
      // 跳转到默认首页（查询页或列表页）
      router.push('/search')
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (err) {
    ElMessage.error('服务器连接失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container { height: 100vh; display: flex; justify-content: center; align-items: center; background: #f5f7fa; }
.login-card { width: 400px; }
h2 { text-align: center; margin: 0; color: #409EFF; }
</style>