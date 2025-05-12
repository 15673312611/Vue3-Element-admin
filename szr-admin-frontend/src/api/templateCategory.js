import request from '@/utils/request'

// 获取分类列表
export function getCategoryList(params) {
  return request({
    url: '/api/template/category/list',
    method: 'get',
    params
  })
}

// 获取分类详情
export function getCategoryDetail(id) {
  return request({
    url: `/api/template/category/${id}`,
    method: 'get'
  })
}

// 添加分类
export function addCategory(data) {
  return request({
    url: '/api/template/category',
    method: 'post',
    data
  })
}

// 更新分类
export function updateCategory(id, data) {
  return request({
    url: `/api/template/category/${id}`,
    method: 'put',
    data
  })
}

// 删除分类
export function deleteCategory(id) {
  return request({
    url: `/api/template/category/${id}`,
    method: 'delete'
  })
}

// 修改分类状态
export function changeCategoryStatus(id, status) {
  return request({
    url: `/api/template/category/${id}/status`,
    method: 'put',
    data: { status }
  })
} 