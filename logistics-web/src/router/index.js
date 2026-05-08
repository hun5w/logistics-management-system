import { createRouter, createWebHistory } from 'vue-router'

// 导入视图组件
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import OrderCreate from '../views/OrderCreate.vue'
import OrderList from '../views/OrderList.vue'
import OrderSearch from '../views/OrderSearch.vue'
import UserProfile from '../views/UserProfile.vue'
import UserManage from '../views/UserManage.vue'
import AdminUserOverview from '../views/AdminUserOverview.vue'
import AdminUserCreate from '../views/AdminUserCreate.vue'
import AdminUserRoleStatus from '../views/AdminUserRoleStatus.vue'
import AdminUserSecurity from '../views/AdminUserSecurity.vue'
import AdminOpLogs from '../views/AdminOpLogs.vue'
import AdminDisabledUsers from '../views/AdminDisabledUsers.vue'
import AdminRoleChangeStats from '../views/AdminRoleChangeStats.vue'
import WarehouseWorktop from "../views/WarehouseWorktop.vue";
import CourierWorktop from "../views/CourierWorktop.vue";

const routes = [
    { path: '/login', name: 'Login', component: Login },
    {
        path: '/',
        redirect: '/search',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: Dashboard,
                meta: { roles: ['ADMIN'] }
            },
            {
                path: 'search',
                name: 'OrderSearch',
                component: OrderSearch
                // 公共页面，不设限
            },
            {
                path: 'create',
                name: 'OrderCreate',
                component: OrderCreate,
                meta: { roles: ['ADMIN', 'USER'] } // 🚩 允许普通用户下单
            },
            {
                path: 'profile',
                name: 'UserProfile',
                component: UserProfile,
                meta: { roles: ['USER'] }
            },
            {
                path: 'list',
                name: 'OrderList',
                component: OrderList,
                meta: { roles: ['ADMIN'] } // 仅限管理员看总表
            },
            {
                path: 'users',
                name: 'UserManage',
                component: UserManage,
                meta: { roles: ['ADMIN'] }
            },
            {
                path: 'admin/users-overview',
                name: 'AdminUserOverview',
                component: AdminUserOverview,
                meta: { roles: ['ADMIN'] }
            },
            {
                path: 'admin/users-create',
                name: 'AdminUserCreate',
                component: AdminUserCreate,
                meta: { roles: ['ADMIN'] }
            },
            {
                path: 'admin/users-role-status',
                name: 'AdminUserRoleStatus',
                component: AdminUserRoleStatus,
                meta: { roles: ['ADMIN'] }
            },
            {
                path: 'admin/users-security',
                name: 'AdminUserSecurity',
                component: AdminUserSecurity,
                meta: { roles: ['ADMIN'] }
            },
            {
                path: 'admin/op-logs',
                name: 'AdminOpLogs',
                component: AdminOpLogs,
                meta: { roles: ['ADMIN'] }
            },
            {
                path: 'admin/disabled-users',
                name: 'AdminDisabledUsers',
                component: AdminDisabledUsers,
                meta: { roles: ['ADMIN'] }
            },
            {
                path: 'admin/role-change-stats',
                name: 'AdminRoleChangeStats',
                component: AdminRoleChangeStats,
                meta: { roles: ['ADMIN'] }
            },
            {
                path: 'warehouse',
                name: 'Warehouse',
                component: WarehouseWorktop,
                meta: { roles: ['ADMIN', 'WAREHOUSE'] }
            },
            {
                path: 'courier',
                name: 'Courier',
                component: CourierWorktop,
                meta: { roles: ['ADMIN', 'COURIER'] }
            },
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

/**
 * 🚩 增强版路由守卫：登录拦截 + 角色鉴权
 */
router.beforeEach((to, from, next) => {
    const userInfoStr = localStorage.getItem('userInfo');
    const publicPaths = ['/', '/search'];

    // 1. 如果去登录页，直接放行
    if (to.path === '/login') {
        return next();
    }

    // 2. 公共路由放行（支持未登录查询）
    if (publicPaths.includes(to.path)) {
        return next();
    }

    // 3. 未登录拦截
    if (!userInfoStr) {
        return next('/login');
    }

    // 解析用户信息
    let user;
    try {
        user = JSON.parse(userInfoStr);
    } catch (e) {
        localStorage.removeItem('userInfo');
        return next('/login');
    }
    const userRole = user.role;

    // 4. 角色鉴权逻辑
    // 如果目标路由定义了 roles 要求，且当前用户角色不在其中
    if (to.meta.roles && !to.meta.roles.includes(userRole)) {
        console.warn(`用户 ${user.username} 尝试越权访问 ${to.path}`);
        // 可以在这里加一个 ElMessage.error('权限不足')
        return next('/search'); // 越权时重定向到公共查询页
    }

    // 5. 校验通过，放行
    next();
})

export default router