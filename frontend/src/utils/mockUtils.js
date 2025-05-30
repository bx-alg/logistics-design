import * as mockData from '@/mock'
import moment from 'moment'

// 通用的分页和过滤方法
export function filterAndPaginate(data, filters, pagination) {
  // 过滤
  let filteredData = data.filter(item => {
    for (let key in filters) {
      if (!filters[key]) continue
      
      // 处理日期范围
      if (Array.isArray(filters[key])) {
        const [startDate, endDate] = filters[key]
        if (startDate && moment(item.createTime).isBefore(startDate)) return false
        if (endDate && moment(item.createTime).isAfter(endDate)) return false
        continue
      }
      
      // 处理字符串模糊匹配
      if (typeof item[key] === 'string' && typeof filters[key] === 'string') {
        if (!item[key].includes(filters[key])) return false
        continue
      }
      
      // 处理精确匹配
      if (item[key] !== filters[key]) return false
    }
    return true
  })

  // 分页
  const { pageNum = 1, pageSize = 10 } = pagination
  const start = (pageNum - 1) * pageSize
  const end = start + pageSize

  return {
    records: filteredData.slice(start, end),
    total: filteredData.length
  }
}

// 模拟API延迟
export function delay(ms = 500) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

// 订单相关方法
export const orderApi = {
  async list(params) {
    await delay()
    return filterAndPaginate(mockData.orders, params.filters, params.pagination)
  },
  
  async detail(id) {
    await delay()
    return mockData.orders.find(order => order.id === id)
  },
  
  async create(order) {
    await delay()
    const newOrder = {
      ...order,
      id: mockData.orders.length + 1,
      createTime: moment().format('YYYY-MM-DD HH:mm:ss'),
      updateTime: moment().format('YYYY-MM-DD HH:mm:ss')
    }
    mockData.orders.push(newOrder)
    return newOrder
  },
  
  async update(order) {
    await delay()
    const index = mockData.orders.findIndex(o => o.id === order.id)
    if (index > -1) {
      mockData.orders[index] = {
        ...mockData.orders[index],
        ...order,
        updateTime: moment().format('YYYY-MM-DD HH:mm:ss')
      }
      return mockData.orders[index]
    }
    throw new Error('订单不存在')
  }
}

// 客户相关方法
export const customerApi = {
  async list(params) {
    await delay()
    return filterAndPaginate(mockData.customers, params.filters, params.pagination)
  },
  
  async detail(id) {
    await delay()
    return mockData.customers.find(customer => customer.id === id)
  },
  
  async create(customer) {
    await delay()
    const newCustomer = {
      ...customer,
      id: mockData.customers.length + 1,
      createTime: moment().format('YYYY-MM-DD HH:mm:ss'),
      updateTime: moment().format('YYYY-MM-DD HH:mm:ss')
    }
    mockData.customers.push(newCustomer)
    return newCustomer
  },
  
  async update(customer) {
    await delay()
    const index = mockData.customers.findIndex(c => c.id === customer.id)
    if (index > -1) {
      mockData.customers[index] = {
        ...mockData.customers[index],
        ...customer,
        updateTime: moment().format('YYYY-MM-DD HH:mm:ss')
      }
      return mockData.customers[index]
    }
    throw new Error('客户不存在')
  }
}

// 产品相关方法
export const productApi = {
  async list(params) {
    await delay()
    return filterAndPaginate(mockData.products, params.filters, params.pagination)
  },
  
  async detail(id) {
    await delay()
    return mockData.products.find(product => product.id === id)
  },
  
  async create(product) {
    await delay()
    const newProduct = {
      ...product,
      id: mockData.products.length + 1,
      createTime: moment().format('YYYY-MM-DD HH:mm:ss'),
      updateTime: moment().format('YYYY-MM-DD HH:mm:ss')
    }
    mockData.products.push(newProduct)
    return newProduct
  },
  
  async update(product) {
    await delay()
    const index = mockData.products.findIndex(p => p.id === product.id)
    if (index > -1) {
      mockData.products[index] = {
        ...mockData.products[index],
        ...product,
        updateTime: moment().format('YYYY-MM-DD HH:mm:ss')
      }
      return mockData.products[index]
    }
    throw new Error('产品不存在')
  }
}

// 生产跟踪相关方法
export const productionApi = {
  async list(params) {
    await delay()
    // 生成更多的模拟数据
    const allOrders = [
      {
        id: 1,
        orderNo: 'ORD20240301001',
        customerName: '上海科技有限公司',
        productName: '高精度传感器',
        status: 0,
        progress: 0,
        startTime: '2024-03-01 09:00:00',
        estimatedEndTime: '2024-03-10 18:00:00',
        description: '待生产'
      },
      {
        id: 2,
        orderNo: 'ORD20240301002',
        customerName: '北京智能科技公司',
        productName: '工业控制器',
        status: 1,
        progress: 20,
        startTime: '2024-03-01 10:30:00',
        estimatedEndTime: '2024-03-12 18:00:00',
        description: '生产准备中'
      },
      // 添加更多模拟数据
      {
        id: 3,
        orderNo: 'ORD20240301003',
        customerName: '广州电子科技公司',
        productName: '智能传感器',
        status: 1,
        progress: 45,
        startTime: '2024-03-02 09:00:00',
        estimatedEndTime: '2024-03-15 18:00:00',
        description: '生产中'
      },
      // ... 继续添加更多数据，确保数量与概览一致
      // 待生产的订单
      ...Array.from({ length: 8 }, (_, i) => ({
        id: i + 4,
        orderNo: `ORD202403${String(i + 4).padStart(2, '0')}`,
        customerName: [
          '深圳微芯科技有限公司',
          '杭州智联电子有限公司',
          '南京创芯半导体有限公司',
          '武汉光电科技股份有限公司',
          '成都智能设备有限公司',
          '苏州精密仪器有限公司',
          '天津新能源科技有限公司',
          '重庆智能制造有限公司'
        ][i],
        productName: [
          '高精度温度传感器',
          '工业控制主板',
          '智能监控系统',
          '光电转换器',
          '自动化控制器',
          '精密测量仪',
          '储能控制系统',
          '智能生产线控制器'
        ][i],
        status: 0,
        progress: 0,
        startTime: null,
        estimatedEndTime: '2024-03-20 18:00:00',
        description: '待生产'
      })),
      // 生产中的订单
      ...Array.from({ length: 13 }, (_, i) => ({
        id: i + 12,
        orderNo: `ORD202403${String(i + 12).padStart(2, '0')}`,
        customerName: [
          '青岛海洋科技有限公司',
          '西安航空电子有限公司',
          '厦门智能装备有限公司',
          '长沙工业自动化有限公司',
          '济南精工机械有限公司',
          '合肥新能源设备有限公司',
          '大连海洋仪器有限公司',
          '郑州智能科技有限公司',
          '无锡精密制造有限公司',
          '东莞电子科技有限公司',
          '宁波工业自动化有限公司',
          '福州智能系统有限公司',
          '昆明工业设备有限公司'
        ][i],
        productName: [
          '海洋监测设备',
          '航空电子系统',
          '工业机器人控制器',
          '自动化生产线',
          '精密加工设备',
          '新能源管理系统',
          '海洋数据采集器',
          '智能物流系统',
          '精密检测设备',
          '电子元件测试仪',
          '工业控制终端',
          '智能监控主机',
          '工业传感网关'
        ][i],
        status: 1,
        progress: Math.floor(Math.random() * 60) + 20,
        startTime: '2024-03-01 09:00:00',
        estimatedEndTime: '2024-03-25 18:00:00',
        description: '生产进行中'
      })),
      // 已发货的订单
      ...Array.from({ length: 8 }, (_, i) => ({
        id: i + 25,
        orderNo: `ORD202403${String(i + 25).padStart(2, '0')}`,
        customerName: [
          '广州智能装备股份有限公司',
          '北京航天科技有限公司',
          '上海工业自动化有限公司',
          '深圳电子技术有限公司',
          '南京智能系统有限公司',
          '杭州数控设备有限公司',
          '武汉光电科技有限公司',
          '成都工业科技有限公司'
        ][i],
        productName: [
          '智能装配系统',
          '航天控制器',
          '工业自动化主机',
          '电子测试平台',
          '智能控制系统',
          '数控加工设备',
          '光电传感器',
          '工业物联网网关'
        ][i],
        status: 2,
        progress: 100,
        startTime: '2024-03-01 09:00:00',
        estimatedEndTime: '2024-03-15 18:00:00',
        description: '已发货'
      })),
      // 已完成的订单
      ...Array.from({ length: 12 }, (_, i) => ({
        id: i + 33,
        orderNo: `ORD202403${String(i + 33).padStart(2, '0')}`,
        customerName: [
          '天津工业装备有限公司',
          '重庆智能制造有限公司',
          '西安航空仪器有限公司',
          '苏州精密机械有限公司',
          '长春汽车电子有限公司',
          '青岛海洋装备有限公司',
          '大连船舶电子有限公司',
          '厦门自动化设备有限公司',
          '济南机床制造有限公司',
          '合肥新能源科技有限公司',
          '昆山精密仪器有限公司',
          '宁波工控系统有限公司'
        ][i],
        productName: [
          '工业机器人',
          '智能生产线',
          '航空仪表系统',
          '精密加工中心',
          '汽车电子控制器',
          '海洋监测系统',
          '船舶导航设备',
          '自动化控制柜',
          '数控机床系统',
          '储能控制器',
          '精密测量设备',
          '工业控制系统'
        ][i],
        status: 3,
        progress: 100,
        startTime: '2024-03-01 09:00:00',
        estimatedEndTime: '2024-03-10 18:00:00',
        description: '已完成'
      }))
    ];

    // 应用过滤条件
    let filteredData = allOrders;
    if (params.filters) {
      const { orderNo, customerName, status, dateRange } = params.filters;
      filteredData = allOrders.filter(order => {
        if (orderNo && !order.orderNo.includes(orderNo)) return false;
        if (customerName && !order.customerName.includes(customerName)) return false;
        if (status !== undefined && status !== '' && order.status !== status) return false;
        if (dateRange && dateRange.length === 2) {
          const [start, end] = dateRange;
          const orderDate = new Date(order.startTime || order.estimatedEndTime);
          if (start && orderDate < new Date(start)) return false;
          if (end && orderDate > new Date(end)) return false;
        }
        return true;
      });
    }

    // 分页处理
    const { pageNum = 1, pageSize = 10 } = params.pagination || {};
    const start = (pageNum - 1) * pageSize;
    const end = start + pageSize;

    return {
      records: filteredData.slice(start, end),
      total: filteredData.length
    };
  },

  async summary() {
    await delay()
    return {
      waiting: 10,    // 待生产
      processing: 15, // 生产中
      shipped: 8,     // 已发货
      completed: 12   // 已完成
    }
  },

  async updateProgress(id, progress, description) {
    await delay()
    const order = mockData.productionTracking.orders.find(o => o.id === id)
    if (order) {
      order.progress = progress
      order.description = description
      if (progress === 100) {
        order.status = 3 // 已完成
      } else if (progress >= 1) {
        order.status = 2 // 生产中
      }
      return order
    }
    throw new Error('订单不存在')
  }
}

// 统计分析相关方法
export const statisticsApi = {
  async getOrderStats() {
    await delay()
    return mockData.statistics.orderStats
  },
  
  async getSalesStats() {
    await delay()
    return mockData.statistics.salesStats
  },
  
  async getProductStats() {
    await delay()
    return mockData.statistics.productStats
  },
  
  async getCustomerStats() {
    await delay()
    return mockData.statistics.customerStats
  }
}

// 系统常量
export const getConstants = () => mockData.constants 