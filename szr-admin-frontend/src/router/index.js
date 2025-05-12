import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import Layout from '@/views/layout/index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '控制台', icon: 'Odometer', requiresAuth: true }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', icon: 'User', requiresAuth: true, permission: 'user:list' }
      },
      {
        path: 'template',
        name: 'Template',
        component: () => import('@/views/template/index.vue'),
        meta: { title: '形象管理', icon: 'PictureFilled', requiresAuth: true, permission: 'template:list' }
      },
      {
        path: 'template-category',
        name: 'TemplateCategory',
        component: () => import('@/views/template-category/index.vue'),
        meta: { title: '形象分类', icon: 'Menu', requiresAuth: true, permission: 'template:category:list' }
      },
      {
        path: 'voice',
        name: 'Voice',
        component: () => import('@/views/voice/index.vue'),
        meta: { title: '音色管理', icon: 'Microphone', requiresAuth: true, permission: 'voice:list' }
      },
      {
        path: 'voice-category',
        name: 'VoiceCategory',
        component: () => import('@/views/voice-category/index.vue'),
        meta: { title: '音色分类', icon: 'List', requiresAuth: true, permission: 'voice:category:list' }
      },
      {
        path: 'digital-human',
        name: 'DigitalHuman',
        component: () => import('@/views/digitalHuman/index.vue'),
        meta: { title: '数字人管理', icon: 'Avatar', requiresAuth: true, permission: 'digital:human:list' }
      },
      {
        path: 'task',
        name: 'Task',
        component: () => import('@/views/task/index.vue'),
        meta: { title: '任务管理', icon: 'Document', requiresAuth: true, permission: 'task:list' }
      },
      {
        path: 'card-key',
        name: 'CardKey',
        component: () => import('@/views/cardKey/index.vue'),
        meta: { title: '卡密管理', icon: 'Key', requiresAuth: true, permission: 'card:key:list' }
      },
      {
        path: 'system',
        name: 'System',
        component: () => import('@/views/system/index.vue'),
        meta: { title: '系统设置', icon: 'Setting', requiresAuth: true, permission: 'system:manage' }
      },
      {
        path: 'system-config',
        name: 'SystemConfig',
        component: () => import('@/views/system/config/index.vue'),
        meta: { title: '参数配置', icon: 'List', requiresAuth: true, permission: 'system:config' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 数字人后台管理系统` : '数字人后台管理系统'
  
  const userStore = useUserStore()
  
  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    // 检查是否有 token
    if (!userStore.token) {
      // 没有 token，重定向到登录页
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
    
    // 如果没有用户信息，先获取用户信息
    if (!userStore.userInfo.id) {
      try {
        // 获取用户信息和权限
        await userStore.getInfo()
      } catch (error) {
        // 获取用户信息失败，可能是 token 失效
        ElMessage.error('获取用户信息失败，请重新登录')
        userStore.resetState()
        next({ name: 'Login', query: { redirect: to.fullPath } })
        return
      }
    }
    
    // 检查用户是否有权限访问该路由
    if (userStore.hasRoutePermission(to)) {
      next()
    } else {
      ElMessage.error('您没有权限访问该页面')
      next('/')
    }
  } else {
    next()
  }
})

export default router 