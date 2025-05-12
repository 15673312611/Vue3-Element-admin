import { defineStore } from 'pinia'
import { login, getInfo, logout } from '@/api/auth'
import { removeToken, setToken, getToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    userInfo: {},
    roles: [],
    permissions: []
  }),
  
  getters: {
    // 判断是否为超级管理员
    isAdmin: (state) => {
      return state.roles.includes('admin') || state.userInfo.username === 'admin';
    },
    
    // 检查是否有特定角色
    hasRole: (state) => (role) => {
      if (state.isAdmin) return true;
      return state.roles.includes(role);
    },
    
    // 检查是否有特定权限
    hasPermission: (state) => (permission) => {
      // 超级管理员拥有所有权限
      if (state.roles.includes('admin') || state.userInfo.username === 'admin') {
        return true;
      }
      return state.permissions.includes(permission);
    },
    
    // 检查是否有模块权限
    hasModulePermission: (state) => (module) => {
      // 超级管理员拥有所有模块权限
      if (state.roles.includes('admin') || state.userInfo.username === 'admin') {
        return true;
      }
      
      // 检查权限是否是数组
      if (!Array.isArray(state.permissions)) {
        console.error('权限不是数组格式');
        return false;
      }
      
      // 检查每个权限项是否是字符串并且以模块名开头
      return state.permissions.some(perm => {
        if (typeof perm !== 'string') {
          console.log('权限项不是字符串:', perm);
          return false;
        }
        return perm.startsWith(`${module}:`);
      });
    }
  },
  
  actions: {
    async login(username, password) {
      try {
        const { data } = await login(username, password)
        console.log('登录响应:', data)
        setToken(data.token)
        this.token = data.token
        return Promise.resolve()
      } catch (error) {
        console.error('登录错误:', error)
        return Promise.reject(error)
      }
    },
    
    async getInfo() {
      try {
        const { data } = await getInfo()
        console.log('获取到的用户信息:', data)
        this.userInfo = data.user || {}
        this.roles = data.roles || []
        
        // 确保 permissions 是字符串数组
        if (data.permissions && Array.isArray(data.permissions)) {
          this.permissions = data.permissions.filter(p => typeof p === 'string');
        } else {
          this.permissions = [];
        }
        
        console.log('解析后的角色:', this.roles)
        console.log('解析后的权限:', this.permissions)
        
        return Promise.resolve(data)
      } catch (error) {
        console.error('获取用户信息错误:', error)
        return Promise.reject(error)
      }
    },
    
    hasRoutePermission(route) {
      // 如果是登录页或错误页，不需要权限
      if (route.path === '/login' || route.path.startsWith('/error') || !route.meta?.requiresAuth) {
        return true;
      }
      
      // 超级管理员拥有所有页面的访问权限
      if (this.roles.includes('admin') || this.userInfo.username === 'admin') {
        return true;
      }
      
      // 检查路由中的permission字段
      if (route.meta?.permission && this.hasPermission(route.meta.permission)) {
        return true;
      }
      
      // 根据路径的第一段判断模块
      const moduleName = route.path.split('/')[1];
      if (moduleName && this.hasModulePermission(moduleName)) {
        return true;
      }
      
      return false;
    },
    
    async logout() {
      try {
        await logout()
        this.resetState()
        return Promise.resolve()
      } catch (error) {
        console.error('登出错误:', error)
        this.resetState()
        return Promise.reject(error)
      }
    },
    
    resetState() {
      this.token = ''
      this.userInfo = {}
      this.roles = []
      this.permissions = []
      removeToken()
    }
  }
}) 