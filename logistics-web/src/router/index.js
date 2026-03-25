import { createRouter, createWebHistory } from 'vue-router'

import OrderCreate from '../views/OrderCreate.vue'
import OrderList from '../views/OrderList.vue'
import OrderSearch from '../views/OrderSearch.vue'
import WarehouseWorktop from "../views/WarehouseWorktop.vue";
import CourierWorktop from "../views/CourierWorktop.vue";

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

export default router