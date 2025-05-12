import request from '@/utils/request'

// 获取音色列表
export function getVoiceList(params) {
  return request({
    url: '/api/voice/list',
    method: 'get',
    params
  })
}

// 获取音色详情
export function getVoiceDetail(id) {
  return request({
    url: `/api/voice/${id}`,
    method: 'get'
  })
}

// 添加音色
export function addVoice(data) {
  return request({
    url: '/api/voice',
    method: 'post',
    data
  })
}

// 更新音色
export function updateVoice(data) {
  return request({
    url: `/api/voice/${data.id}`,
    method: 'put',
    data
  })
}

// 删除音色
export function deleteVoice(id) {
  return request({
    url: `/api/voice/${id}`,
    method: 'delete'
  })
}

// 修改音色状态
export function changeVoiceStatus(id, status) {
  return request({
    url: `/api/voice/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 更新音色状态（适配前端组件使用）
export function updateVoiceStatus(data) {
  return request({
    url: `/api/voice/${data.id}/status`,
    method: 'put',
    data: { status: data.status }
  })
} 