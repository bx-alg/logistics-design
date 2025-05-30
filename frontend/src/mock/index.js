// 客户数据
export const customers = [
  {
    id: 1,
    customerCode: 'CUS001',
    customerName: '上海童泰童装有限公司',
    contactName: '张明',
    contactPhone: '13800138001',
    email: 'zhangming@tongtai.com',
    address: '上海市闵行区七莘路1885号',
    status: 1,
    createTime: '2024-01-01 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  },
  {
    id: 2,
    customerCode: 'CUS002',
    customerName: '杭州巴拉巴拉服饰有限公司',
    contactName: '李强',
    contactPhone: '13800138002',
    email: 'liqiang@balabala.com',
    address: '杭州市滨江区江南大道3588号',
    status: 1,
    createTime: '2024-01-02 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  },
  {
    id: 3,
    customerCode: 'CUS003',
    customerName: '广州小猪班纳童装有限公司',
    contactName: '王晓',
    contactPhone: '13800138003',
    email: 'wangxiao@pigbanner.com',
    address: '广州市天河区珠江新城88号',
    status: 1,
    createTime: '2024-01-03 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  },
  {
    id: 4,
    customerCode: 'CUS004',
    customerName: '北京安奈儿童装股份有限公司',
    contactName: '赵伟',
    contactPhone: '13800138004',
    email: 'zhaowei@annil.com',
    address: '北京市朝阳区朝阳门外大街甲6号',
    status: 1,
    createTime: '2024-01-04 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  },
  {
    id: 5,
    customerCode: 'CUS005',
    customerName: '深圳市红黄蓝儿童用品有限公司',
    contactName: '陈明',
    contactPhone: '13800138005',
    email: 'chenming@ryl.com',
    address: '深圳市南山区科技园南区W1栋5层',
    status: 1,
    createTime: '2024-01-05 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  }
]

// 产品数据
export const products = [
  {
    id: 1,
    productCode: 'PRD001',
    productName: '儿童夏季短袖T恤',
    categoryId: 1,
    categoryName: '上衣',
    unit: '件',
    price: 59.99,
    description: '纯棉儿童短袖T恤，适合3-8岁儿童夏季穿着',
    imageUrl: 'https://example.com/tshirt.jpg',
    status: 1,
    createTime: '2024-01-01 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  },
  {
    id: 2,
    productCode: 'PRD002',
    productName: '儿童牛仔裤',
    categoryId: 2,
    categoryName: '裤子',
    unit: '条',
    price: 89.99,
    description: '弹力儿童牛仔裤，舒适耐穿，适合4-10岁儿童',
    imageUrl: 'https://example.com/jeans.jpg',
    status: 1,
    createTime: '2024-01-02 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  },
  {
    id: 3,
    productCode: 'PRD003',
    productName: '女童公主连衣裙',
    categoryId: 3,
    categoryName: '裙装',
    unit: '件',
    price: 129.98,
    description: '夏季女童公主风连衣裙，蓬蓬纱设计，适合3-7岁女童',
    imageUrl: 'https://example.com/dress.jpg',
    status: 1,
    createTime: '2024-01-03 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  },
  {
    id: 4,
    productCode: 'PRD004',
    productName: '儿童运动套装',
    categoryId: 4,
    categoryName: '套装',
    unit: '套',
    price: 149.99,
    description: '儿童春秋运动套装，舒适透气，适合5-12岁儿童',
    imageUrl: 'https://example.com/sportswear.jpg',
    status: 1,
    createTime: '2024-01-04 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  },
  {
    id: 5,
    productCode: 'PRD005',
    productName: '婴儿连体衣',
    categoryId: 5,
    categoryName: '婴儿装',
    unit: '件',
    price: 79.99,
    description: '纯棉婴儿连体衣，柔软舒适，适合0-2岁婴幼儿',
    imageUrl: 'https://example.com/babysuit.jpg',
    status: 1,
    createTime: '2024-01-05 09:00:00',
    updateTime: '2024-03-01 10:00:00'
  }
]

// 订单数据
export const orders = [
  {
    id: 1,
    orderNo: 'ORD20240301001',
    customerId: 1,
    customerName: '上海童泰童装有限公司',
    totalAmount: 59990.00,
    status: 0,
    progress: 0,
    address: '上海市闵行区七莘路1885号',
    contactName: '张明',
    contactPhone: '13800138001',
    remark: '夏季新款，要求6月1日前交货',
    expectedDeliveryDate: '2024-06-01',
    createTime: '2024-03-01 09:00:00',
    updateTime: '2024-03-01 09:00:00',
    orderItems: [
      {
        id: 1,
        orderId: 1,
        productId: 1,
        productName: '儿童夏季短袖T恤',
        productCode: 'PRD001',
        quantity: 1000,
        unit: '件',
        unitPrice: 59.99,
        totalPrice: 59990.00
      }
    ]
  },
  {
    id: 2,
    orderNo: 'ORD20240301002',
    customerId: 2,
    customerName: '杭州巴拉巴拉服饰有限公司',
    totalAmount: 89990.00,
    status: 1,
    progress: 30,
    address: '杭州市滨江区江南大道3588号',
    contactName: '李强',
    contactPhone: '13800138002',
    remark: '春季新款，需要提供详细的尺码表',
    expectedDeliveryDate: '2024-04-15',
    createTime: '2024-03-01 10:30:00',
    updateTime: '2024-03-02 14:20:00',
    orderItems: [
      {
        id: 2,
        orderId: 2,
        productId: 2,
        productName: '儿童牛仔裤',
        productCode: 'PRD002',
        quantity: 1000,
        unit: '条',
        unitPrice: 89.99,
        totalPrice: 89990.00
      }
    ]
  },
  {
    id: 3,
    orderNo: 'ORD20240301003',
    customerId: 3,
    customerName: '广州小猪班纳童装有限公司',
    totalAmount: 129980.00,
    status: 2,
    progress: 100,
    address: '广州市天河区珠江新城88号',
    contactName: '王晓',
    contactPhone: '13800138003',
    remark: '夏季新款连衣裙，面料要求透气舒适',
    expectedDeliveryDate: '2024-04-20',
    createTime: '2024-03-01 11:00:00',
    updateTime: '2024-03-05 16:30:00',
    orderItems: [
      {
        id: 3,
        orderId: 3,
        productId: 3,
        productName: '女童公主连衣裙',
        productCode: 'PRD003',
        quantity: 1000,
        unit: '件',
        unitPrice: 129.98,
        totalPrice: 129980.00
      }
    ]
  },
  {
    id: 4,
    orderNo: 'ORD20240301004',
    customerId: 4,
    customerName: '北京安奈儿童装股份有限公司',
    totalAmount: 149990.00,
    status: 3,
    progress: 100,
    address: '北京市朝阳区朝阳门外大街甲6号',
    contactName: '赵伟',
    contactPhone: '13800138004',
    remark: '春秋季运动套装，已按期完成，客户已确认验收',
    expectedDeliveryDate: '2024-03-10',
    actualDeliveryDate: '2024-03-08',
    createTime: '2024-03-01 14:00:00',
    updateTime: '2024-03-08 11:20:00',
    orderItems: [
      {
        id: 4,
        orderId: 4,
        productId: 4,
        productName: '儿童运动套装',
        productCode: 'PRD004',
        quantity: 1000,
        unit: '套',
        unitPrice: 149.99,
        totalPrice: 149990.00
      }
    ]
  },
  {
    id: 5,
    orderNo: 'ORD20240301005',
    customerId: 5,
    customerName: '深圳市红黄蓝儿童用品有限公司',
    totalAmount: 79990.00,
    status: 1,
    progress: 45,
    address: '深圳市南山区科技园南区W1栋5层',
    contactName: '陈明',
    contactPhone: '13800138005',
    remark: '要求提供完整的测试报告',
    expectedDeliveryDate: '2024-03-30',
    createTime: '2024-03-01 15:30:00',
    updateTime: '2024-03-03 09:40:00',
    orderItems: [
      {
        id: 5,
        orderId: 5,
        productId: 5,
        productName: '婴儿连体衣',
        productCode: 'PRD005',
        quantity: 1000,
        unit: '件',
        unitPrice: 79.99,
        totalPrice: 79990.00
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
      customerName: '上海童泰童装有限公司',
      productName: '儿童夏季短袖T恤',
      status: 0,
      progress: 0,
      startTime: '2024-03-01 09:00:00',
      estimatedEndTime: '2024-06-01',
      description: '夏季新款，要求6月1日前交货'
    },
    {
      id: 2,
      orderNo: 'ORD20240301002',
      customerName: '杭州巴拉巴拉服饰有限公司',
      productName: '儿童牛仔裤',
      status: 1,
      progress: 30,
      startTime: '2024-03-01 10:30:00',
      estimatedEndTime: '2024-04-15',
      description: '春季新款，需要提供详细的尺码表'
    },
    {
      id: 3,
      orderNo: 'ORD20240301003',
      customerName: '广州小猪班纳童装有限公司',
      productName: '女童公主连衣裙',
      status: 2,
      progress: 100,
      startTime: '2024-03-01 11:00:00',
      estimatedEndTime: '2024-04-20',
      description: '夏季新款连衣裙，面料要求透气舒适'
    },
    {
      id: 4,
      orderNo: 'ORD20240301004',
      customerName: '北京安奈儿童装股份有限公司',
      productName: '儿童运动套装',
      status: 3,
      progress: 100,
      startTime: '2024-03-01 14:00:00',
      estimatedEndTime: '2024-03-10',
      description: '春秋季运动套装，已按期完成，客户已确认验收'
    },
    {
      id: 5,
      orderNo: 'ORD20240301005',
      customerName: '深圳市红黄蓝儿童用品有限公司',
      productName: '婴儿连体衣',
      status: 1,
      progress: 45,
      startTime: '2024-03-01 15:30:00',
      estimatedEndTime: '2024-03-30',
      description: '要求提供完整的测试报告'
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
      { productName: '儿童夏季短袖T恤', sales: 1000, amount: 59990.00 },
      { productName: '儿童牛仔裤', sales: 1000, amount: 89990.00 },
      { productName: '女童公主连衣裙', sales: 1000, amount: 129980.00 }
    ]
  },
  // 客户统计
  customerStats: {
    total: 3,
    topCustomers: [
      { customerName: '上海童泰童装有限公司', orderCount: 20, amount: 399998.00 },
      { customerName: '杭州巴拉巴拉服饰有限公司', orderCount: 15, amount: 299997.50 },
      { customerName: '广州小猪班纳童装有限公司', orderCount: 10, amount: 199998.00 }
    ]
  }
}

// 系统常量
export const constants = {
  // 订单状态
  orderStatus: [
    { value: 0, label: '待生产' },
    { value: 1, label: '生产中' },
    { value: 2, label: '已发货' },
    { value: 3, label: '已完成' },
    { value: 4, label: '已取消' }
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
    { value: 1, label: '上衣' },
    { value: 2, label: '裤子' },
    { value: 3, label: '裙装' },
    { value: 4, label: '套装' },
    { value: 5, label: '婴儿装' }
  ]
} 