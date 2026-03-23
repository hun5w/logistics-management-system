import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        redirect: '/create',
        children: [
            { path: 'create', component: () => import('../views/OrderCreate.vue') },
            { path: 'list', component: () => import('../views/OrderList.vue') },
            { path: 'search', component: () => import('../views/OrderSearch.vue') }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router