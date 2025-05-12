const TOKEN_KEY = 'admin_token'

export function getToken() {
  const token = localStorage.getItem(TOKEN_KEY)
  return token ? `Bearer ${token}` : ''
}

export function setToken(token) {
  // 确保存储的是纯 token，不包含 Bearer 前缀
  const cleanToken = token.replace(/^Bearer\s+/i, '')
  return localStorage.setItem(TOKEN_KEY, cleanToken)
}

export function removeToken() {
  return localStorage.removeItem(TOKEN_KEY)
} 