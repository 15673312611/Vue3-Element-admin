/**
 * 判断是否为外部链接
 * @param {string} path 路径
 * @returns {boolean}
 */
export function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

/**
 * 判断是否为空值
 * @param {*} value 值
 * @returns {boolean}
 */
export function isEmpty(value) {
  return value === undefined || value === null || value === ''
}

/**
 * 验证用户名
 * @param {string} str 用户名
 * @returns {boolean}
 */
export function validUsername(str) {
  const pattern = /^[a-zA-Z0-9_-]{3,16}$/
  return pattern.test(str)
}

/**
 * 验证手机号
 * @param {string} str 手机号
 * @returns {boolean}
 */
export function validMobile(str) {
  const pattern = /^1[3-9]\d{9}$/
  return pattern.test(str)
}

/**
 * 判断是否为有效的URL
 * @param {string} url
 * @returns {boolean}
 */
export function validURL(url) {
  const reg = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/
  return reg.test(url)
}

/**
 * 判断是否为有效的邮箱
 * @param {string} email
 * @returns {boolean}
 */
export function validEmail(email) {
  const reg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
  return reg.test(email)
}

/**
 * 判断是否为有效的手机号
 * @param {string} phone
 * @returns {boolean}
 */
export function validPhone(phone) {
  const reg = /^1[3-9]\d{9}$/
  return reg.test(phone)
}

/**
 * 判断是否为有效的密码
 * @param {string} password
 * @returns {boolean}
 */
export function validPassword(password) {
  const reg = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/
  return reg.test(password)
} 