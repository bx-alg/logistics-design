import request from '@/utils/request'

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: '/orders/page',
    method: 'get',
    params
  })
}

// 获取订单详情
export function getOrderDetail(id) {
  return request({
    url: `/orders/${id}`,
    method: 'get'
  })
}

// 创建订单
export function createOrder(data) {
  return request({
    url: '/orders',
    method: 'post',
    data
  })
}

// 更新订单
export function updateOrder(data) {
  return request({
    url: '/orders',
    method: 'put',
    data
  })
}

// 删除订单
export function deleteOrder(id) {
  return request({
    url: `/orders/${id}`,
    method: 'delete'
  })
}

// 更新订单状态
export function updateOrderStatus(id, status) {
  return request({
    url: `/orders/${id}/status/${status}`,
    method: 'put'
  })
} 