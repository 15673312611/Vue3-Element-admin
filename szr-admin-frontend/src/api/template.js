import request from '@/utils/request'

// 获取形象列表
export function getTemplateList(params) {
  return request({
    url: '/api/template/list',
    method: 'get',
    params
  })
}

// 获取形象详情
export function getTemplateDetail(id) {
  return request({
    url: `/api/template/${id}`,
    method: 'get'
  })
}

// 添加形象
export function addTemplate(data) {
  return request({
    url: '/api/template',
    method: 'post',
    data
  })
}

// 更新形象
export function updateTemplate(id, data) {
  return request({
    url: `/api/template/${id}`,
    method: 'put',
    data
  })
}

// 删除形象
export function deleteTemplate(id) {
  return request({
    url: `/api/template/${id}`,
    method: 'delete'
  })
}

// 修改形象状态
export function changeTemplateStatus(id, status) {
  return request({
    url: `/api/template/${id}/status`,
    method: 'put',
    params: { status }
  })
} 