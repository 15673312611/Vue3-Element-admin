import request from '@/utils/request'

/**
 * 获取仪表盘统计数据
 * 注意：此API仅用于兼容性，实际使用task API
 * @returns {Promise}
 */
export function getDashboardStatistics() {
  return request({
    url: '/api/task/list',
    method: 'get'
  })
} 