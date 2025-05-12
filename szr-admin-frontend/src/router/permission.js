import router from './index'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false })

// 白名单路由（不需要登录即可访问）
const whiteList = ['/login', '/register', '/forget-password']

router.beforeEach(async (to, from, next) => {
  NProgress.start()
  
  const userStore = useUserStore()
  const token = userStore.token
  const hasGetUserInfo = userStore.userInfo && Object.keys(userStore.userInfo).length > 0
  
  document.title = to.meta.title ? `${to.meta.title} - SZR管理系统` : 'SZR管理系统'
  
  // 有token表示已登录
  if (token) {
    if (to.path === '/login') {
      // 已登录状态下访问登录页，重定向到首页
      next({ path: '/' })
      NProgress.done()
    } else {
      // 已登录，检查用户信息是否已获取
      if (hasGetUserInfo) {
        // 检查用户是否有权限访问该路由
        if (userStore.hasRoutePermission(to)) {
          next()
        } else {
          next('/403')
          ElMessage.error('您没有权限访问该页面')
          NProgress.done()
        }
      } else {
        try {
          // 获取用户信息
          await userStore.getInfo()
          
          // 重新检查权限
          if (userStore.hasRoutePermission(to)) {
            next({ ...to, replace: true })
          } else {
            next('/403')
            ElMessage.error('您没有权限访问该页面')
          }
        } catch (error) {
          // 获取用户信息失败，清除token并跳转到登录页
          console.error('获取用户信息失败:', error)
          await userStore.logout()
          ElMessage.error(error.message || '获取用户信息失败，请重新登录')
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path)) {
      // 在白名单中，直接进入
      next()
    } else {
      // 不在白名单中，重定向到登录页
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router 