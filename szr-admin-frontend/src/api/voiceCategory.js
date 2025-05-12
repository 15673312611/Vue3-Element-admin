import request from '@/utils/request'

// 获取音色分类列表
export function getVoiceCategoryList(params) {
  return request({
    url: '/api/voice/category/list',
    method: 'get',
    params
  })
}

// 获取音色分类详情
export function getVoiceCategoryDetail(id) {
  return request({
    url: `/api/voice/category/${id}`,
    method: 'get'
  })
}

// 新增音色分类
export function addVoiceCategory(data) {
  return request({
    url: '/api/voice/category',
    method: 'post',
    data
  })
}

// 更新音色分类
export function updateVoiceCategory(id, data) {
  return request({
    url: `/api/voice/category/${id}`,
    method: 'put',
    data
  })
}

// 删除音色分类
export function deleteVoiceCategory(id) {
  return request({
    url: `/api/voice/category/${id}`,
    method: 'delete'
  })
}

// 修改音色分类状态
export function changeVoiceCategoryStatus(id, status) {
  return request({
    url: `/api/voice/category/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 为保持API调用一致性添加别名
export const getCategoryList = getVoiceCategoryList
export const getCategoryDetail = getVoiceCategoryDetail
export const addCategory = addVoiceCategory
export const updateCategory = updateVoiceCategory
export const deleteCategory = deleteVoiceCategory
export const changeCategoryStatus = changeVoiceCategoryStatus 