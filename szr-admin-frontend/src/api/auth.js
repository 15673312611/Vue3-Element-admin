import request from '@/utils/request'

// 登录
export function login(username, password) {
  console.log('调用登录接口，参数:', { username, password })
  return request({
    url: '/api/auth/login',
    method: 'post',
    data: {
      username,
      password
    }
  })
}

// 获取用户信息
export function getInfo() {
  console.log('获取用户信息')
  return request({
    url: '/api/auth/info',
    method: 'get'
  })
}

// 退出登录
export function logout() {
  console.log('退出登录')
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
} 