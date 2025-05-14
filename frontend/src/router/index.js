import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/views/layout/Layout'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: { title: '数据总览', icon: 'el-icon-s-data' }
    }]
  },
  {
    path: '/order',
    component: Layout,
    meta: { title: '订单管理', icon: 'el-icon-s-order' },
    children: [
      {
        path: '',
        name: 'OrderList',
        component: () => import('@/views/order/index'),
        meta: { title: '订单列表' }
      },
      {
        path: 'create',
        name: 'OrderCreate',
        component: () => import('@/views/order/detail'),
        meta: { title: '创建订单', activeMenu: '/order' },
        hidden: true
      },
      {
        path: ':id',
        name: 'OrderDetail',
        component: () => import('@/views/order/detail'),
        meta: { title: '订单详情', activeMenu: '/order' },
        hidden: true
      },
      {
        path: 'forecast',
        name: 'OrderForecast',
        component: () => import('@/views/order/forecast'),
        meta: { title: '销量预测' }
      }
    ]
  },
  {
    path: '/shipment',
    component: Layout,
    meta: { title: '运输管理', icon: 'el-icon-truck' },
    children: [
      {
        path: 'list',
        name: 'ShipmentList',
        component: () => import('@/views/shipment/index'),
        meta: { title: '运输单列表' }
      },
      {
        path: 'tracking',
        name: 'ShipmentTracking',
        component: () => import('@/views/shipment/tracking'),
        meta: { title: '实时追踪' }
      }
    ]
  },
  {
    path: '/inventory',
    component: Layout,
    meta: { title: '库存管理', icon: 'el-icon-s-goods' },
    children: [
      {
        path: 'list',
        name: 'InventoryList',
        component: () => import('@/views/inventory/index'),
        meta: { title: '库存列表' }
      },
      {
        path: 'analysis',
        name: 'InventoryAnalysis',
        component: () => import('@/views/inventory/analysis'),
        meta: { title: '库存分析' }
      }
    ]
  },
  {
    path: '/supplier',
    component: Layout,
    meta: { title: '供应商管理', icon: 'el-icon-s-custom' },
    children: [
      {
        path: 'list',
        name: 'SupplierList',
        component: () => import('@/views/supplier/index'),
        meta: { title: '供应商列表' }
      },
      {
        path: 'performance',
        name: 'SupplierPerformance',
        component: () => import('@/views/supplier/performance'),
        meta: { title: '绩效评估' }
      }
    ]
  }
]

const router = new VueRouter({
  routes
})

export default router 