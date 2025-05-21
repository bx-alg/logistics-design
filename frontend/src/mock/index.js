// 客户数据
export const customers = [
  {
    id: 1,
    customerCode: 'CUS001',
    customerName: '上海科技有限公司',
    contactName: '张三',
    contactPhone: '13800138001',
    email: 'zhangsan@tech.com',
    address: '上海市浦东新区张江高科技园区',
    status: 1,
    createTime: '2024-01-01 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  },
  {
    id: 2,
    customerCode: 'CUS002',
    customerName: '北京智能科技公司',
    contactName: '李四',
    contactPhone: '13800138002',
    email: 'lisi@smart.com',
    address: '北京市海淀区中关村科技园',
    status: 1,
    createTime: '2024-01-02 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  },
  {
    id: 3,
    customerCode: 'CUS003',
    customerName: '广州电子有限公司',
    contactName: '王五',
    contactPhone: '13800138003',
    email: 'wangwu@electronics.com',
    address: '广州市天河区科技园',
    status: 1,
    createTime: '2024-01-03 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  }
]

// 产品数据
export const products = [
  {
    id: 1,
    productCode: 'PRD001',
    productName: '高精度传感器',
    categoryId: 1,
    categoryName: '传感器',
    unit: '个',
    price: 1999.99,
    description: '工业级高精度传感器',
    imageUrl: 'https://example.com/sensor.jpg',
    status: 1,
    createTime: '2024-01-01 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  },
  {
    id: 2,
    productCode: 'PRD002',
    productName: '工业控制器',
    categoryId: 2,
    categoryName: '控制器',
    unit: '台',
    price: 4999.99,
    description: '智能工业控制器',
    imageUrl: 'https://example.com/controller.jpg',
    status: 1,
    createTime: '2024-01-02 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  },
  {
    id: 3,
    productCode: 'PRD003',
    productName: '电路板组件',
    categoryId: 3,
    categoryName: '电子组件',
    unit: '套',
    price: 299.99,
    description: '高质量电路板组件',
    imageUrl: 'https://example.com/pcb.jpg',
    status: 1,
    createTime: '2024-01-03 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  }
]

// 订单数据
export const orders = [
  {
    id: 1,
    orderNo: 'ORD20240301001',
    customerId: 1,
    customerName: '上海科技有限公司',
    totalAmount: 19999.90,
    status: 0,
    progress: 0,
    address: '上海市浦东新区张江高科技园区',
    contactName: '张三',
    contactPhone: '13800138001',
    remark: '待生产',
    expectedDeliveryDate: '2024-03-10 18:00:00',
    createTime: '2024-03-01 09:00:00',
    updateTime: '2024-03-01 09:00:00',
    orderItems: [
      {
        id: 1,
        orderId: 1,
        productId: 1,
        productName: '高精度传感器',
        quantity: 10,
        unitPrice: 1999.99,
        totalPrice: 19999.90
      }
    ]
  },
  {
    id: 2,
    orderNo: 'ORD20240301002',
    customerId: 2,
    customerName: '北京智能科技公司',
    totalAmount: 14999.97,
    status: 1,
    progress: 20,
    address: '北京市海淀区中关村科技园',
    contactName: '李四',
    contactPhone: '13800138002',
    remark: '生产准备中',
    expectedDeliveryDate: '2024-03-12 18:00:00',
    createTime: '2024-03-01 10:30:00',
    updateTime: '2024-03-01 10:30:00',
    orderItems: [
      {
        id: 2,
        orderId: 2,
        productId: 2,
        productName: '工业控制器',
        quantity: 3,
        unitPrice: 4999.99,
        totalPrice: 14999.97
      }
    ]
  }
]

// 生产跟踪数据
export const productionTracking = {
  orders: [
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
    }
  ],
  summary: {
    waiting: 10,
    processing: 15,
    shipped: 8,
    completed: 12
  }
}

// 统计分析数据
export const statistics = {
  // 订单统计
  orderStats: {
    total: 45,
    today: 5,
    thisWeek: 25,
    thisMonth: 45,
    status: {
      waiting: 10,
      processing: 15,
      shipped: 8,
      completed: 12
    }
  },
  // 销售统计
  salesStats: {
    total: 299999.80,
    today: 29999.98,
    thisWeek: 149999.90,
    thisMonth: 299999.80,
    trend: [
      { date: '2024-03-01', amount: 29999.98 },
      { date: '2024-03-02', amount: 39999.97 },
      { date: '2024-03-03', amount: 19999.99 },
      { date: '2024-03-04', amount: 49999.96 },
      { date: '2024-03-05', amount: 59999.95 }
    ]
  },
  // 产品统计
  productStats: {
    total: 3,
    topSelling: [
      { productName: '高精度传感器', sales: 100, amount: 199999.00 },
      { productName: '工业控制器', sales: 50, amount: 249999.50 },
      { productName: '电路板组件', sales: 200, amount: 59998.00 }
    ]
  },
  // 客户统计
  customerStats: {
    total: 3,
    topCustomers: [
      { customerName: '上海科技有限公司', orderCount: 20, amount: 399998.00 },
      { customerName: '北京智能科技公司', orderCount: 15, amount: 299997.50 },
      { customerName: '广州电子有限公司', orderCount: 10, amount: 199998.00 }
    ]
  }
}

// 系统常量
export const constants = {
  // 订单状态
  orderStatus: [
    { value: 0, label: '待生产' },
    { value: 1, label: '生产准备中' },
    { value: 2, label: '生产中' },
    { value: 3, label: '已发货' },
    { value: 4, label: '已完成' }
  ],
  // 客户状态
  customerStatus: [
    { value: 0, label: '禁用' },
    { value: 1, label: '启用' }
  ],
  // 产品状态
  productStatus: [
    { value: 0, label: '下架' },
    { value: 1, label: '上架' }
  ],
  // 产品分类
  productCategories: [
    { id: 1, name: '传感器' },
    { id: 2, name: '控制器' },
    { id: 3, name: '电子组件' }
  ]
} 