import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken } from '@/utils/auth'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: '', // 不添加前缀，因为后端已经包含/api前缀
  timeout: 10000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 打印请求信息，便于调试
    console.log('发送请求:', config.method.toUpperCase(), config.url, config)
    
    const token = getToken()
    if (token) {
      // 确保 token 格式正确
      config.headers['Authorization'] = token
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果是文件下载类型的响应，直接返回
    if (response.request.responseType === 'blob') {
      return response
    }
    
    // 如果响应成功
    if (res.code === 200 || res.code === 0) {
      return res
    }
    
    // 处理错误情况
    console.error('API返回错误:', res)
    
    // 401: 未登录或Token过期
    if (res.code === 401) {
      handleUnauthorized()
      return Promise.reject(new Error('登录已过期，请重新登录'))
    }
    
    // 403: 没有权限
    if (res.code === 403) {
      const errMsg = '您没有访问该资源的权限，请联系管理员'
      ElMessage.error(errMsg)
      return Promise.reject(new Error(errMsg))
    }
    
    // 其他错误
    ElMessage({
      message: res.message || '请求失败',
      type: 'error',
      duration: 5 * 1000
    })
    
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  error => {
    console.error('请求发生错误:', error)
    
    if (error.response) {
      const { status, data } = error.response
      
      console.error(`HTTP错误 ${status}:`, data)
      
      switch (status) {
        case 401:
          handleUnauthorized()
          return Promise.reject(new Error('登录已过期，请重新登录'))
        case 403:
          const errMsg = data?.message || '您没有访问该资源的权限，请联系管理员'
          ElMessage.error(errMsg)
          return Promise.reject(new Error(errMsg))
        case 404:
          ElMessage.error('请求的资源不存在')
          return Promise.reject(new Error('请求的资源不存在'))
        case 500:
          ElMessage.error('服务器内部错误，请联系管理员')
          return Promise.reject(new Error('服务器内部错误'))
        default:
          ElMessage.error(data?.message || `未知错误(${status})`)
          return Promise.reject(new Error(data?.message || `未知错误(${status})`))
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      console.error('没有收到响应:', error.request)
      const errMsg = '服务器未响应，请检查网络连接或联系管理员'
      ElMessage.error(errMsg)
      return Promise.reject(new Error(errMsg))
    } else {
      // 设置请求时发生错误
      console.error('请求配置错误:', error.message)
      const errMsg = '请求配置错误: ' + error.message
      ElMessage.error(errMsg)
      return Promise.reject(new Error(errMsg))
    }
  }
)

function handleUnauthorized() {
  ElMessageBox.confirm(
    '登录状态已过期，请重新登录',
    '系统提示',
    {
      confirmButtonText: '重新登录',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 清除本地存储的 token
    localStorage.removeItem('admin_token')
    // 跳转到登录页
    router.push('/login')
  }).catch(() => {})
}

export default service 