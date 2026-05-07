<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration"></div>

    <div class="login-container">
      <!-- 左侧品牌区 -->
      <div class="brand-section hidden-mobile">
        <div class="brand-content">
          <div class="brand-icon">🚚</div>
          <h1 class="brand-title">物流智能<span class="gradient-text">管理</span></h1>
          <p class="brand-subtitle">实时追踪 · 智能分配 · 数据驱动</p>
          
          <div class="features-list">
            <div class="feature-item">
              <span class="feature-icon">✓</span>
              <span>端到端物流可视化</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">✓</span>
              <span>多角色权限管理</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">✓</span>
              <span>实时数据报表</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录卡片 -->
      <div class="form-section">
        <custom-card :variant="'elevated'" class="login-card" :title="isLogin ? '登录账户' : '创建账户'">
          <!-- Tab 选项卡 -->
          <div class="tab-switcher">
            <button 
              :class="['tab-item', { active: isLogin }]"
              @click="toggleTab(true)"
            >
              登录
            </button>
            <button 
              :class="['tab-item', { active: !isLogin }]"
              @click="toggleTab(false)"
            >
              注册
            </button>
          </div>

          <!-- 登录表单 -->
          <el-form v-if="isLogin" :model="form" @submit.prevent="handleSubmit">
            <el-form-item>
              <el-input
                v-model="form.username"
                placeholder="账号"
                size="large"
                clearable
                prefix-icon="User"
              />
            </el-form-item>

            <el-form-item>
              <el-input
                v-model="form.password"
                placeholder="密码"
                type="password"
                size="large"
                show-password
                prefix-icon="Lock"
              />
            </el-form-item>

            <custom-button
              variant="primary"
              size="lg"
              :loading="loading"
              class="full-width"
              @click="handleSubmit"
            >
              立即登录
            </custom-button>

            <custom-button
              variant="ghost"
              size="md"
              class="full-width mt-3"
              @click="goGuestSearch"
            >
              → 免登录查件
            </custom-button>
          </el-form>

          <!-- 注册表单 -->
          <el-form v-else :model="form" @submit.prevent="handleSubmit">
            <el-form-item>
              <el-input
                v-model="form.username"
                placeholder="账号"
                size="large"
                clearable
                prefix-icon="User"
              />
            </el-form-item>

            <el-form-item>
              <el-input
                v-model="form.nickname"
                placeholder="昵称（真实姓名）"
                size="large"
                clearable
                prefix-icon="Avatar"
              />
            </el-form-item>

            <el-form-item>
              <el-input
                v-model="form.phone"
                placeholder="手机号码"
                size="large"
                clearable
                prefix-icon="Phone"
              />
            </el-form-item>

            <el-form-item>
              <el-input
                v-model="form.password"
                placeholder="密码"
                type="password"
                size="large"
                show-password
                prefix-icon="Lock"
              />
            </el-form-item>

            <el-form-item>
              <el-select 
                v-model="form.role" 
                placeholder="选择身份"
                size="large"
                style="width: 100%"
              >
                <el-option label="👤 普通用户（发货/查询）" value="USER" />
                <el-option label="🚴 快递员（接单/派送）" value="COURIER" />
              </el-select>
              <div class="role-tip">* 管理员与仓库账号请联系系统内部分配</div>
            </el-form-item>

            <custom-button
              variant="primary"
              size="lg"
              :loading="loading"
              class="full-width"
              @click="handleSubmit"
            >
              提交注册
            </custom-button>
          </el-form>

          <!-- 底部提示 -->
          <div class="form-footer">
            <p class="text-muted text-sm">
              演示账号：admin/123456 (管理员) 
            </p>
          </div>
        </custom-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import CustomCard from '../components/CustomCard.vue'
import CustomButton from '../components/CustomButton.vue'
import { User, Lock, Avatar, Phone } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const isLogin = ref(true)

const form = ref({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  role: 'USER'
})

const toggleTab = (status) => {
  isLogin.value = status
  // 切换时清空部分表单
  form.value.nickname = ''
  form.value.phone = ''
}

const goGuestSearch = () => {
  router.push('/search')
}

const handleSubmit = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('账号和密码不能为空')
    return
  }

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
        ElMessage.success('欢迎回来，' + res.data.data.nickname)
        localStorage.setItem('userInfo', JSON.stringify(res.data.data))

        const role = res.data.data.role
        if (role === 'ADMIN') {
          router.push('/admin/users-overview')
        } else {
          router.push('/search')
        }
      } else {
        ElMessage.success('注册成功，请使用新账号登录')
        isLogin.value = true
        form.value = { username: '', password: '', nickname: '', phone: '', role: 'USER' }
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
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-background) 0%, #F0F4F8 100%);
  overflow: hidden;
  position: relative;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  inset: 0;
  opacity: 0.03;
  pointer-events: none;
  
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -50%;
    width: 100%;
    height: 100%;
    background: radial-gradient(circle, var(--color-accent), transparent);
    border-radius: 50%;
    filter: blur(150px);
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -50%;
    left: -50%;
    width: 100%;
    height: 100%;
    background: radial-gradient(circle, var(--color-accent), transparent);
    border-radius: 50%;
    filter: blur(150px);
  }
}

.login-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 3rem;
  width: 100%;
  max-width: 1200px;
  padding: 2rem;
  margin: 0 auto;
  position: relative;
  z-index: 1;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
    gap: 2rem;
    padding: 1rem;
  }
}

/* ============ 左侧品牌区 ============ */
.brand-section {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 3rem;
}

.brand-content {
  animation: fadeInUp 0.7s ease-out;
}

.brand-icon {
  font-size: 4rem;
  line-height: 1;
  margin-bottom: 1.5rem;
  display: inline-block;
}

.brand-title {
  font-family: var(--font-display);
  font-size: 3.5rem;
  line-height: 1.1;
  color: var(--color-foreground);
  margin-bottom: 1rem;

  .gradient-text {
    background: linear-gradient(to right, var(--color-accent), var(--color-accent-secondary));
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
  }
}

.brand-subtitle {
  font-size: 1.25rem;
  color: var(--color-muted-foreground);
  margin-bottom: 2.5rem;
  line-height: 1.5;
}

.features-list {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 1.125rem;
  color: var(--color-foreground);
  animation: fadeInUp 0.7s ease-out;
  animation-fill-mode: both;

  &:nth-child(1) { animation-delay: 0.1s; }
  &:nth-child(2) { animation-delay: 0.2s; }
  &:nth-child(3) { animation-delay: 0.3s; }
}

.feature-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 1.75rem;
  height: 1.75rem;
  background: linear-gradient(to right, var(--color-accent), var(--color-accent-secondary));
  color: white;
  border-radius: 50%;
  font-weight: bold;
  flex-shrink: 0;
  font-size: 0.875rem;
}

/* ============ 右侧表单区 ============ */
.form-section {
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.7s ease-out 0.2s both;

  @media (max-width: 768px) {
    animation: fadeInUp 0.7s ease-out;
  }
}

.login-card {
  width: 100%;
  max-width: 450px;
}

.tab-switcher {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 2rem;
  background-color: var(--color-muted);
  padding: 0.25rem;
  border-radius: var(--radius-lg);
}

.tab-item {
  flex: 1;
  padding: 0.75rem 1rem;
  background: transparent;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-weight: 500;
  color: var(--color-muted-foreground);
  transition: all var(--transition-normal);

  &.active {
    background: white;
    color: var(--color-accent);
    box-shadow: var(--shadow-sm);
    font-weight: 600;
  }

  &:hover:not(.active) {
    color: var(--color-foreground);
  }
}

.el-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;

  :deep(.el-form-item) {
    margin-bottom: 0;
  }

  :deep(.el-input) {
    --el-input-height: 3rem;
    --el-input-border-color: var(--color-border);
    --el-input-focus-border-color: var(--color-accent);
    border-radius: var(--radius-lg) !important;

    &.is-focus {
      box-shadow: 0 0 0 3px rgba(0, 82, 255, 0.1);
    }
  }

  :deep(.el-select) {
    --el-select-input-height: 3rem;
    --el-select-border-color: var(--color-border);

    .el-input__wrapper {
      border-radius: var(--radius-lg);
    }
  }
}

.role-tip {
  font-size: 0.875rem;
  color: var(--color-muted-foreground);
  margin-top: 0.5rem;
  line-height: 1.4;
}

.full-width {
  width: 100%;
}

.mt-3 {
  margin-top: 0.75rem;
}

.form-footer {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid var(--color-border);
  text-align: center;

  .text-muted {
    margin: 0;
    font-size: 0.875rem;
  }
}

/* ============ 响应式 ============ */
@media (max-width: 768px) {
  .brand-section {
    padding: 1.5rem;
  }

  .brand-title {
    font-size: 2.5rem;
  }

  .brand-subtitle {
    font-size: 1rem;
    margin-bottom: 1.5rem;
  }

  .feature-item {
    font-size: 1rem;
  }
}

/* ============ 动画关键帧 ============ */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(1.75rem);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.hidden-mobile {
  @media (max-width: 768px) {
    display: none;
  }
}
</style>