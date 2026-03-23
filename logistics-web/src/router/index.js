import { createRouter, createWebHistory } from 'vue-router'

import OrderCreate from '../views/OrderCreate.vue'
import OrderList from '../views/OrderList.vue'
import OrderSearch from '../views/OrderSearch.vue'

const routes = [
    {
        path: '/',
        redirect: '/create',
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
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router