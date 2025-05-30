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
    redirect: '/data-overview',
    children: [
      {
        path: 'data-overview',
        name: 'DataOverview',
        component: () => import('@/views/dashboard/dataOverview'),
        meta: { title: '数据总览', icon: 'el-icon-s-data' }
      }
      // 大屏展示路由已删除
    ]
  },
  {
    path: '/device',
    component: Layout,
    meta: { title: '设备管理', icon: 'el-icon-s-platform' },
    children: [
      {
        path: 'list',
        name: 'DeviceList',
        component: () => import('@/views/device/index'),
        meta: { title: '设备管理' }
      },
      {
        path: 'rgv-monitor',
        name: 'RgvMonitor',
        component: () => import('@/views/device/monitor/rgv'),
        meta: { title: 'RGV设备监控' }
      }
    ]
  },
  {
    path: '/order',
    component: Layout,
    redirect: '/order/orders',
    meta: { title: '订单管理', icon: 'el-icon-s-order' },
    children: [
      {
        path: 'orders',
        name: 'OrderList',
        component: () => import('@/views/order/list'),
        meta: { title: '订单列表' }
      },
      {
        path: 'create',
        name: 'OrderCreate',
        component: () => import('@/views/order/create'),
        meta: { title: '创建订单', activeMenu: '/order/orders' },
        hidden: false
      },
      {
        path: 'forecast',
        name: 'OrderForecast',
        component: () => import('@/views/order/forecast'),
        meta: { title: '销量预测' }
      },
      {
        path: 'distribution',
        name: 'OrderDistribution',
        component: () => import('@/views/order/distribution'),
        meta: { title: '订单配送地图', hidden: true }
      }
    ]
  },
  {
    path: '/production',
    component: Layout,
    redirect: '/production/plan',
    meta: { title: '排产计划管理', icon: 'el-icon-s-management' },
    children: [
      {
        path: 'plan',
        name: 'ProductionPlan',
        component: () => import('@/views/production/productionPlan'),
        meta: { title: '排产计划' }
      },
      {
        path: 'plan-detail/:id',
        name: 'ProductionPlanDetail',
        component: () => import('@/views/production/productionPlanDetail'),
        meta: { 
          title: '计划详情', 
          activeMenu: '/production/plan',
          hidden: true 
        },
        hidden: true
      },
      {
        path: 'tracking',
        name: 'ProductionTracking',
        component: () => import('@/views/production/tracking'),
        meta: { title: '生产进度跟踪' }
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
      },
      {
        path: 'warehouse',
        name: 'WarehouseList',
        component: () => import('@/views/inventory/warehouse'),
        meta: { title: '仓库列表' }
      },
      {
        path: 'warehouse-detail/:id',
        name: 'WarehouseDetail',
        component: () => import('@/views/inventory/warehouseDetail'),
        meta: { 
          title: '仓库详情', 
          activeMenu: '/inventory/warehouse',
          hidden: true 
        },
        hidden: true
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
  mode: 'history',  // 使用history模式，去掉URL中的#
  base: process.env.BASE_URL,
  routes
})

// 全局前置守卫，用于处理路由跳转前的逻辑
router.beforeEach((to, from, next) => {
  console.log('路由跳转:', from.path, '->', to.path)
  next()
})

export default router