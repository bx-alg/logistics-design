import request from '@/utils/request'

// 获取生产跟踪列表
export function getProductionList(params) {
  return request({
    url: '/production-tracking/list',
    method: 'get',
    params
  })
}

// 获取生产统计数据
export function getProductionSummary() {
  return request({
    url: '/production-tracking/summary',
    method: 'get'
  })
}

// 更新生产进度
export function updateProgress(id, data) {
  return request({
    url: `/production-tracking/${id}/progress`,
    method: 'put',
    params: data
  })
} 