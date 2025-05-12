import request from '@/utils/request'

/**
 * 获取系统配置列表
 * @param {Object} params 查询参数
 * @returns {Promise} Promise对象
 */
export function getConfigList(params) {
  return request({
    url: '/api/system/config/list',
    method: 'get',
    params
  })
}

/**
 * 获取系统配置详情
 * @param {Number} id 配置ID
 * @returns {Promise} Promise对象
 */
export function getConfigDetail(id) {
  return request({
    url: `/api/system/config/${id}`,
    method: 'get'
  })
}

/**
 * 根据key获取系统配置
 * @param {String} key 配置key
 * @returns {Promise} Promise对象
 */
export function getConfigByKey(key) {
  return request({
    url: `/api/system/config/key/${key}`,
    method: 'get'
  })
}

/**
 * 添加系统配置
 * @param {Object} data 配置信息
 * @returns {Promise} Promise对象
 */
export function addConfig(data) {
  return request({
    url: '/api/system/config',
    method: 'post',
    data
  })
}

/**
 * 更新系统配置
 * @param {Number} id 配置ID
 * @param {Object} data 配置信息
 * @returns {Promise} Promise对象
 */
export function updateConfig(id, data) {
  return request({
    url: `/api/system/config/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除系统配置
 * @param {Number} id 配置ID
 * @returns {Promise} Promise对象
 */
export function deleteConfig(id) {
  return request({
    url: `/api/system/config/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除系统配置
 * @param {Array} ids 配置ID数组
 * @returns {Promise} Promise对象
 */
export function batchDeleteConfig(ids) {
  return request({
    url: '/api/system/config/batch',
    method: 'delete',
    data: { ids }
  })
} 