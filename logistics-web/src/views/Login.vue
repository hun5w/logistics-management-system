<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>🚚 智能物流管理系统</h2>
        <div class="tab-header">
          <span :class="{ active: isLogin }" @click="toggleTab(true)">登录</span>
          <span :class="{ active: !isLogin }" @click="toggleTab(false)">注册</span>
        </div>
      </template>

      <el-form :model="form" label-width="70px" label-position="left">
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>

        <template v-if="!isLogin">
          <el-form-item label="昵称">
            <el-input v-model="form.nickname" placeholder="建议使用真实姓名" />
          </el-form-item>

          <el-form-item label="手机号">
            <el-input v-model="form.phone" placeholder="用于接收物流信息" />
          </el-form-item>

          <el-form-item label="身份">
            <el-select v-model="form.role" placeholder="请选择您的初始身份" style="width: 100%">
              <el-option label="普通用户 (发货/查询)" value="USER" />
              <el-option label="快递员 (接单/派送)" value="COURIER" />
            </el-select>
            <div class="role-tip">* 管理员与仓库账号请联系系统内部分配</div>
          </el-form-item>
        </template>

        <el-button type="primary" @click="handleSubmit" :loading="loading" style="width: 100%; margin-top: 10px;">
          {{ isLogin ? '立即登录' : '提交规范注册' }}
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
const isLogin = ref(true)

const form = ref({
  username: '',
  password: '',
  nickname: '',
  phone: '',   // 新增：手机号
  role: 'USER' // 默认注册为普通用户
})

// 切换标签页时重置部分数据
const toggleTab = (status) => {
  isLogin.value = status
}

const handleSubmit = async () => {
  // 1. 基础非空校验
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('账号和密码不能为空')
    return
  }

  // 2. 注册时的额外校验
  if (!isLogin.value) {
    if (!form.value.phone) {
      ElMessage.warning('手机号是物流追踪的关键，请填写')
      return
    }
  }

  loading.value = true
  const url = isLogin.value ? '/api/auth/login' : '/api/auth/register'

  try {
    const res = await axios.post(`http://localhost:8080${url}`, form.value)

    if (res.data.code === 200) {
      if (isLogin.value) {
        // 登录成功
        ElMessage.success('欢迎回来，' + res.data.data.nickname)
        localStorage.setItem('userInfo', JSON.stringify(res.data.data))

        // 角色分流跳转
        const role = res.data.data.role
        if (role === 'ADMIN') {
          router.push('/list') // 管理员去总表
        } else if (role === 'WAREHOUSE') {
          router.push('/warehouse')
        } else if (role === 'COURIER') {
          router.push('/courier')
        } else {
          router.push('/search') // USER 去查询页
        }
      } else {
        // 注册成功处理
        ElMessage.success('注册成功，请使用新账号登录')
        isLogin.value = true
      }
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (err) {
    ElMessage.error('连接服务器失败，请检查后端服务是否启动')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container { height: 100vh; display: flex; justify-content: center; align-items: center; background: #f5f7fa; }
.login-card { width: 420px; }
h2 { text-align: center; margin-bottom: 20px; color: #409EFF; letter-spacing: 1px; }
.tab-header { display: flex; justify-content: space-around; margin-bottom: 20px; cursor: pointer; color: #909399; font-size: 16px; }
.tab-header .active { color: #409EFF; font-weight: bold; border-bottom: 2px solid #409EFF; padding-bottom: 5px; }
.role-tip { font-size: 12px; color: #999; margin-top: 5px; line-height: 1.4; }
</style>