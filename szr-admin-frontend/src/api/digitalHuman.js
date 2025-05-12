import request from '@/utils/request'

// 获取数字人列表
export function getDigitalHumanList(params) {
  return request({
    url: '/api/digital-human/list',
    method: 'get',
    params
  })
}

// 获取数字人详情
export function getDigitalHumanDetail(id) {
  return request({
    url: `/api/digital-human/${id}`,
    method: 'get'
  })
}

// 添加数字人
export function addDigitalHuman(data) {
  return request({
    url: '/api/digital-human',
    method: 'post',
    data
  })
}

// 更新数字人
export function updateDigitalHuman(id, data) {
  return request({
    url: `/api/digital-human/${id}`,
    method: 'put',
    data
  })
}

// 删除数字人
export function deleteDigitalHuman(id) {
  return request({
    url: `/api/digital-human/${id}`,
    method: 'delete'
  })
}

// 修改数字人状态
export function changeDigitalHumanStatus(id, status) {
  return request({
    url: `/api/digital-human/${id}/status`,
    method: 'put',
    params: { status }
  })
} 