import { createRouter, createWebHistory } from 'vue-router'

// 导入视图组件
import Login from '../views/Login.vue'
import OrderCreate from '../views/OrderCreate.vue'
import OrderList from '../views/OrderList.vue'
import OrderSearch from '../views/OrderSearch.vue'
import WarehouseWorktop from "../views/WarehouseWorktop.vue";
import CourierWorktop from "../views/CourierWorktop.vue";

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/',
        redirect: '/search', // 登录后默认去查询页，比较通用
        children: [
            {
                path: 'create',
                name: 'OrderCreate',
                component: OrderCreate
            },
            {
                path: 'list',
                name: 'OrderList',
                component: OrderList
            },
            {
                path: 'search',
                name: 'OrderSearch',
                component: OrderSearch
            },
            {
                path: 'warehouse',
                name: 'Warehouse',
                component: WarehouseWorktop
            },
            {
                path: 'courier',
                name: 'Courier',
                component: CourierWorktop
            },
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

/**
 * 🚩 路由守卫：论文中的“访问控制逻辑”实现
 */
router.beforeEach((to, from, next) => {
    // 从本地存储获取用户信息
    const userInfo = localStorage.getItem('userInfo');

    if (to.path === '/login') {
        // 如果要去登录页，直接放行
        next();
    } else if (!userInfo) {
        // 如果没有登录信息，强制跳转到登录页
        next('/login');
    } else {
        // 已登录，允许访问
        next();
    }
})

export default router