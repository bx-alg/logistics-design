import request from '@/utils/request'

export function getOrderStatusList() {
  return request({
    url: '/order-status/list',
    method: 'get'
  })
}

export function getOrderStatusByCode(code) {
  return request({
    url: `/order-status/${code}`,
    method: 'get'
  })
} 