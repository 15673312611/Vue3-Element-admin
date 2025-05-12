import request from '@/utils/request'

// 获取卡密列表
export function getCardKeyList(params) {
  return request({
    url: '/api/card-key/list',
    method: 'get',
    params
  })
}

// 获取卡密详情
export function getCardKeyDetail(id) {
  return request({
    url: `/api/card-key/${id}`,
    method: 'get'
  })
}

// 添加卡密
export function addCardKey(data) {
  return request({
    url: '/api/card-key',
    method: 'post',
    data
  })
}

// 批量生成卡密
export function generateCardKeys(data) {
  return request({
    url: '/api/card-key/batch',
    method: 'post',
    data
  })
}

// 更新卡密
export function updateCardKey(id, data) {
  return request({
    url: `/api/card-key/${id}`,
    method: 'put',
    data
  })
}

// 删除卡密
export function deleteCardKey(id) {
  return request({
    url: `/api/card-key/${id}`,
    method: 'delete'
  })
}

// 批量删除卡密
export function batchDeleteCardKey(ids) {
  return request({
    url: '/api/card-key/batch',
    method: 'delete',
    data: ids
  })
}

// 修改卡密状态
export function changeCardKeyStatus(id, status) {
  return request({
    url: `/api/card-key/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 导出卡密
export function exportCardKey(batchNo) {
  return request({
    url: '/api/card-key/export',
    method: 'get',
    params: { batchNo }
  })
} 