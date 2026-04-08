import { createRouter, createWebHistory } from 'vue-router'

// 导入视图组件
import Login from '../views/Login.vue'
import OrderCreate from '../views/OrderCreate.vue'
import OrderList from '../views/OrderList.vue'
import OrderSearch from '../views/OrderSearch.vue'
import WarehouseWorktop from "../views/WarehouseWorktop.vue";
import CourierWorktop from "../views/CourierWorktop.vue";

const routes = [
    { path: '/login', name: 'Login', component: Login },
    {
        path: '/',
        redirect: '/search',
        children: [
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
                path: 'list',
                name: 'OrderList',
                component: OrderList,
                meta: { roles: ['ADMIN'] } // 仅限管理员看总表
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

    // 1. 如果去登录页，直接放行
    if (to.path === '/login') {
        return next();
    }

    // 2. 未登录拦截
    if (!userInfoStr) {
        return next('/login');
    }

    // 解析用户信息
    const user = JSON.parse(userInfoStr);
    const userRole = user.role;

    // 3. 角色鉴权逻辑
    // 如果目标路由定义了 roles 要求，且当前用户角色不在其中
    if (to.meta.roles && !to.meta.roles.includes(userRole)) {
        console.warn(`用户 ${user.username} 尝试越权访问 ${to.path}`);
        // 可以在这里加一个 ElMessage.error('权限不足')
        return next('/search'); // 越权时重定向到公共查询页
    }

    // 4. 校验通过，放行
    next();
})

export default router