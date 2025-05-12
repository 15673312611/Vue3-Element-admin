import request from '@/utils/request'

/**
 * 获取用户列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getUserList(params) {
  return request({
    url: '/api/user/list',
    method: 'get',
    params
  })
}

/**
 * 获取用户详情
 * @param {number} id 用户ID
 * @returns {Promise}
 */
export function getUserDetail(id) {
  return request({
    url: `/api/user/${id}`,
    method: 'get'
  })
}

/**
 * 添加用户
 * @param {Object} data 用户数据
 * @returns {Promise}
 */
export function addUser(data) {
  return request({
    url: '/api/user',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 * @param {Object} data 用户数据
 * @returns {Promise}
 */
export function updateUser(data) {
  return request({
    url: '/api/user/' + data.id,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 * @param {number} id 用户ID
 * @returns {Promise}
 */
export function deleteUser(id) {
  return request({
    url: `/api/user/${id}`,
    method: 'delete'
  })
}

// 修改用户状态
export function changeUserStatus(id, status) {
  return request({
    url: `/api/user/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 重置用户密码
export function resetUserPassword(id) {
  return request({
    url: `/api/user/${id}/password/reset`,
    method: 'put'
  })
} 