<template>
  <div v-if="route.path === '/login'" class="login-wrapper">
    <router-view />
  </div>

  <el-container v-else class="main-layout">
    <el-aside width="240px">
      <div class="logo-area">🚚 智能物流系统</div>

      <el-menu
          router
          :default-active="route.path"
          class="side-menu"
      >
        <el-menu-item index="/search">
          <el-icon><Search /></el-icon>
          <span>物流轨迹查询</span>
        </el-menu-item>

        <template v-if="userRole === 'ADMIN'">
          <el-menu-item index="/create">
            <el-icon><Plus /></el-icon>
            <span>快速下单</span>
          </el-menu-item>
          <el-menu-item index="/list">
            <el-icon><Memo /></el-icon>
            <span>订单管理总览</span>
          </el-menu-item>
        </template>

        <el-menu-item v-if="['ADMIN', 'WAREHOUSE'].includes(userRole)" index="/warehouse">
          <el-icon><Box /></el-icon>
          <span>仓库中转作业</span>
        </el-menu-item>

        <el-menu-item v-if="['ADMIN', 'COURIER'].includes(userRole)" index="/courier">
          <el-icon><Bicycle /></el-icon>
          <span>末端派送签收</span>
        </el-menu-item>

        <el-divider />

        <el-menu-item @click="handleLogout" class="logout-btn">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录 ({{ nickname }})</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, Plus, Memo, Box, Bicycle, SwitchButton } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userRole = ref('')
const nickname = ref('')

// 定义一个更新权限的方法
const updateUserInfo = () => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    const user = JSON.parse(info)
    userRole.value = user.role
    nickname.value = user.nickname
  } else {
    userRole.value = ''
    nickname.value = ''
  }
}

// 🚩 核心逻辑：监听路由变化。每次页面跳转都重新检查一次权限
watch(() => route.path, () => {
  updateUserInfo()
})

// 初始化执行一次
onMounted(() => {
  updateUserInfo()
})

const handleLogout = () => {
  ElMessageBox.confirm('确认退出系统吗？', '提示').then(() => {
    localStorage.removeItem('userInfo')
    router.push('/login')
  })
}
</script>

<style scoped>
.main-layout { height: 100vh; }
.el-aside { background-color: #304156; color: white; }
.side-menu { border-right: none; background-color: #304156; }
/* Element Plus 菜单深色模式适配 */
:deep(.el-menu) { border-right: none; --el-menu-bg-color: #304156; --el-menu-text-color: #bfcbd9; --el-menu-active-color: #409eff; }
.logo-area { height: 60px; line-height: 60px; text-align: center; font-weight: bold; font-size: 18px; color: #fff; background: #2b2f3a; }
.logout-btn { color: #f56c6c !important; }
</style>