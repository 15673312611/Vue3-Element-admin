import request from '@/utils/request'

/**
 * 更新单个系统配置
 * @param {Object} config 配置对象
 * @returns {Promise} Promise对象
 */
export function updateConfig(config) {
  return request({
    url: `/api/system/config/${config.id}`,
    method: 'put',
    data: config
  })
}

/**
 * 更新或创建单个系统配置
 * @param {Object} config 配置对象
 * @returns {Promise} Promise对象
 */
export function updateOrCreateConfig(config) {
  if (config.id) {
    return updateConfig(config)
  } else {
    return createConfig(config)
  }
}

/**
 * 批量创建或更新配置
 * @param {Array} configs 配置数组
 * @returns {Promise} Promise对象
 */
export function batchCreateOrUpdateConfig(configs) {
  return request({
    url: '/api/system/config/batch',
    method: 'post',
    data: configs
  })
}

/**
 * 更新基本配置
 * @param {Object} data 基本配置数据
 * @returns {Promise} Promise对象
 */
export function updateBasicConfig(data) {
  return batchCreateOrUpdateConfig(data.configs)
}

/**
 * 更新安全配置
 * @param {Object} data 安全配置数据
 * @returns {Promise} Promise对象
 */
export function updateSecurityConfig(data) {
  return batchCreateOrUpdateConfig(data.configs)
}

/**
 * 更新存储配置
 * @param {Object} data 存储配置数据
 * @returns {Promise} Promise对象
 */
export function updateStorageConfig(data) {
  return batchCreateOrUpdateConfig(data.configs)
}

/**
 * 测试存储连接
 * @param {Object} data 存储配置数据
 * @returns {Promise} Promise对象
 */
export function testStorageConnection(data) {
  return request({
    url: '/api/system/config/storage/test',
    method: 'post',
    data
  })
}

/**
 * 获取所有系统配置(列表)
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
 * 获取公共配置（系统名称、Logo等）
 * @returns {Promise} Promise对象
 */
export function getPublicConfig() {
  console.log('调用getPublicConfig API')
  return request({
    url: '/api/system/config/public',
    method: 'get'
  }).then(response => {
    console.log('getPublicConfig API响应:', response)
    return response
  }).catch(error => {
    console.error('getPublicConfig API错误:', error)
    throw error
  })
}

/**
 * 创建系统配置
 * @param {Object} config 配置对象
 * @returns {Promise} Promise对象
 */
export function createConfig(config) {
  return request({
    url: '/api/system/config',
    method: 'post',
    data: config
  })
}

/**
 * 测试公共配置接口
 * @returns {Promise} Promise对象
 */
export function testPublicConfig() {
  console.log('调用testPublicConfig API')
  return request({
    url: '/api/system/config/test-public',
    method: 'get'
  }).then(response => {
    console.log('testPublicConfig API响应:', response)
    return response
  }).catch(error => {
    console.error('testPublicConfig API错误:', error)
    throw error
  })
} 