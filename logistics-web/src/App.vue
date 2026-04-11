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
          :unique-opened="true"
      >
        <el-menu-item v-if="userRole === 'ADMIN'" index="/dashboard">
          <span>总览看板</span>
        </el-menu-item>

        <el-menu-item index="/search">
          <el-icon><Search /></el-icon>
          <span>物流轨迹查询</span>
        </el-menu-item>

        <el-menu-item v-if="['ADMIN', 'USER'].includes(userRole)" index="/create">
          <el-icon><Plus /></el-icon>
          <span>快速下单</span>
        </el-menu-item>

        <el-menu-item v-if="userRole === 'ADMIN'" index="/list">
          <el-icon><Memo /></el-icon>
          <span>订单管理总览</span>
        </el-menu-item>

        <el-menu-item v-if="['ADMIN', 'WAREHOUSE'].includes(userRole)" index="/warehouse">
          <el-icon><Box /></el-icon>
          <span>仓库中转作业</span>
        </el-menu-item>

        <el-menu-item v-if="['ADMIN', 'COURIER'].includes(userRole)" index="/courier">
          <el-icon><Bicycle /></el-icon>
          <span>末端派送签收</span>
        </el-menu-item>

        <el-sub-menu v-if="userRole === 'ADMIN'" index="/admin-group">
          <template #title>
            <span>管理员账号中心</span>
          </template>
          <el-menu-item index="/admin/users-overview">账号总览</el-menu-item>
          <el-menu-item index="/admin/users-create">创建内部账号</el-menu-item>
          <el-menu-item index="/admin/users-role-status">角色与状态维护</el-menu-item>
          <el-menu-item index="/admin/users-security">密码重置中心</el-menu-item>
          <el-menu-item index="/admin/op-logs">管理员操作日志</el-menu-item>
          <el-menu-item index="/admin/disabled-users">禁用账号管理</el-menu-item>
          <el-menu-item index="/admin/role-change-stats">角色变更统计</el-menu-item>
        </el-sub-menu>
        
        <el-divider />

        <el-menu-item v-if="userRole" @click="handleLogout" class="logout-btn">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录 ({{ nickname || '未登录' }})</span>
        </el-menu-item>

        <el-menu-item v-else @click="goLogin" class="logout-btn">
          <el-icon><SwitchButton /></el-icon>
          <span>去登录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-main class="content-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, Plus, Memo, Box, Bicycle, SwitchButton } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userRole = ref('')
const nickname = ref('')
const publicPaths = ['/search']

/**
 * 更新用户信息逻辑
 * 从 localStorage 获取登录时存入的 userInfo
 */
const updateUserInfo = () => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    try {
      const user = JSON.parse(info)
      userRole.value = user.role
      nickname.value = user.nickname
    } catch (e) {
      console.error("用户信息解析失败", e)
      clearAuth()
    }
  } else {
    clearAuth()
  }
}

const clearAuth = () => {
  userRole.value = ''
  nickname.value = ''
  localStorage.removeItem('userInfo')
}

/**
 * 🚩 核心：监听路由变化
 * 1. 每次跳转重新拉取权限状态
 * 2. 增加基础的登录拦截（防绕过）
 */
watch(
    () => route.path,
    (newPath) => {
      updateUserInfo()

      // 如果不是去登录页，且没有角色信息，强制回弹
      if (newPath !== '/login' && !publicPaths.includes(newPath) && !userRole.value) {
        ElMessage.warning('请先登录系统')
        router.push('/login')
      }
    },
    { immediate: true }
)

onMounted(() => {
  updateUserInfo()
})

/**
 * 退出登录逻辑
 */
const handleLogout = () => {
  ElMessageBox.confirm('确认退出智能物流管理系统吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    clearAuth()
    router.push('/login')
    ElMessage.success('已安全退出')
  }).catch(() => {})
}

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: white;
  transition: width 0.3s;
  overflow-x: hidden;
}

.side-menu {
  border-right: none;
  background-color: #304156;
}

/* 侧边栏 Logo 区域 */
.logo-area {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-weight: bold;
  font-size: 18px;
  color: #fff;
  background: #2b2f3a;
  letter-spacing: 1px;
}

/* 主内容区域背景 */
.content-main {
  background-color: #f0f2f5;
  padding: 20px;
}

/* Element Plus 菜单深色模式覆盖 */
:deep(.el-menu) {
  --el-menu-bg-color: #304156;
  --el-menu-text-color: #bfcbd9;
  --el-menu-active-color: #409eff;
  --el-menu-hover-bg-color: #263445;
}

:deep(.el-menu-item.is-active) {
  background-color: #263445 !important;
}

.logout-btn {
  color: #f56c6c !important;
  margin-top: 20px;
}

.login-wrapper {
  width: 100%;
  height: 100vh;
}

/* 隐藏分割线边距 */
.el-divider--horizontal {
  margin: 12px 0;
  background-color: #4a5a6a;
}
</style>