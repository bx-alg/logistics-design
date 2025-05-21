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
    return filterAndPaginate(mockData.productionTracking.orders, params.filters, params.pagination)
  },
  
  async summary() {
    await delay()
    return mockData.productionTracking.summary
  },
  
  async updateProgress(id, progress, description) {
    await delay()
    const order = mockData.productionTracking.orders.find(o => o.id === id)
    if (order) {
      order.progress = progress
      order.description = description
      if (progress === 100) {
        order.status = 3 // 已发货
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