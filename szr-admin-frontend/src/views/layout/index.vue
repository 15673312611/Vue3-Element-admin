<template>
  <div class="app-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '240px'" class="sidebar-container">
        <div class="logo-container" :class="{ 'collapsed': isCollapse }">
          <img :src="logoPath" class="logo-image" alt="Logo" v-if="isCollapse" />
          <div class="logo-content" v-else>
            <img :src="logoPath" class="logo-image" alt="Logo" />
            <span class="logo-title">{{ systemName || '数字人后台' }}</span>
          </div>
        </div>
        <!-- 调试信息 -->
        <div v-if="isDebug" class="debug-info">
          <p>系统名称: {{ systemName }}</p>
          <p>Logo路径: {{ logoPath }}</p>
          <p>原始Logo: {{ systemLogo }}</p>
          <el-button size="small" type="primary" @click="refreshSystemConfig">刷新配置</el-button>
        </div>
        <el-scrollbar>
          <el-menu
            :default-active="activeMenu"
            :collapse="isCollapse"
            :unique-opened="true"
            :router="true"
            class="side-menu"
          >
            <sidebar-item
              v-for="route in routes"
              :key="route.path"
              :item="route"
              :base-path="route.path"
              :is-collapse="isCollapse"
            />
          </el-menu>
        </el-scrollbar>
      </el-aside>
      
      <el-container class="main-container">
        <!-- 头部 -->
        <el-header height="64px" class="app-header">
          <div class="header-left">
            <div class="collapse-btn" @click="toggleSidebar">
              <el-icon class="header-icon"><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
            </div>
            <el-breadcrumb separator="/" class="breadcrumb">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="route.meta && route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <div class="header-actions">
              <el-tooltip content="全屏" placement="bottom">
                <div class="action-item" @click="toggleFullScreen">
                  <el-icon><FullScreen /></el-icon>
                </div>
              </el-tooltip>
              
 

              <el-tooltip content="调试信息" placement="bottom">
                <div class="action-item" @click="toggleDebug">
                  <el-icon><InfoFilled /></el-icon>
                </div>
              </el-tooltip>
            </div>
            
            <el-dropdown trigger="click" class="user-dropdown">
              <div class="user-info">
                <el-avatar :size="32" class="user-avatar">{{ userInfo.username.slice(0, 1).toUpperCase() }}</el-avatar>
                <div class="user-details" v-if="!isCollapse">
                  <span class="username">{{ userInfo.username }}</span>
                  <span class="user-role">管理员</span>
                </div>
                <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="user-dropdown-menu">
                  <el-dropdown-item>
                    <el-icon><User /></el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <el-icon><Setting /></el-icon>系统设置
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <!-- 主内容区 -->
        <el-main class="app-main">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <keep-alive>
                <component :is="Component" />
              </keep-alive>
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getPublicConfig, testPublicConfig } from '@/api/system'
import SidebarItem from './components/SidebarItem.vue'
import defaultLogo from '@/assets/logo.svg'
import { InfoFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isCollapse = ref(false)
const systemName = ref('')
const systemLogo = ref('')
const logoPath = ref(defaultLogo)
const isDebug = ref(false)

const userInfo = computed(() => userStore.userInfo)

// 获取系统配置
const fetchSystemConfig = async () => {
  try {
    console.log('开始获取系统配置...')
    
    // 先尝试使用正常接口
    let response = null
    try {
      response = await getPublicConfig()
    } catch (error) {
      console.error('正常接口调用失败，尝试测试接口:', error)
      response = await testPublicConfig()
    }
    
    console.log('系统配置响应:', response)
    
    if (response.code === 0 || response.code === 200) {
      const data = response.data
      console.log('系统配置数据:', data)
      
      if (data) {
        systemName.value = data.system_name
        systemLogo.value = data.system_logo
        
        // 确保logo路径是绝对路径
        if (data.system_logo) {
          // 如果logo路径不是以http或/开头，则添加/
          if (!data.system_logo.startsWith('http') && !data.system_logo.startsWith('/')) {
            logoPath.value = '/' + data.system_logo
          } else {
            logoPath.value = data.system_logo
          }
          console.log('设置logo路径:', logoPath.value)
        } else {
          logoPath.value = defaultLogo
          console.log('使用默认logo路径:', logoPath.value)
        }
      }
    } else {
      console.error('获取系统配置失败:', response.message)
    }
  } catch (error) {
    console.error('获取系统配置失败:', error)
    // 使用默认值
    logoPath.value = defaultLogo
  }
}

// 页面加载时获取系统配置
onMounted(() => {
  fetchSystemConfig()
})

// 获取路由
const routes = computed(() => {
  return router.options.routes.find(route => route.path === '/').children
})

// 当前激活的菜单
const activeMenu = computed(() => {
  const { path } = route
  return path
})

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await userStore.logout()
    router.push('/login')
  }).catch(() => {})
}

// 切换侧边栏
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 全屏切换
const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    }
  }
}

// 刷新页面
const refreshPage = () => {
  const { path, query } = route
  router.replace({
    path: '/redirect' + path,
    query
  })
}

// 切换调试信息
const toggleDebug = () => {
  isDebug.value = !isDebug.value
}

// 手动刷新系统配置
const refreshSystemConfig = async () => {
  try {
    await fetchSystemConfig()
    ElMessage.success('刷新配置成功')
  } catch (error) {
    ElMessage.error('刷新配置失败: ' + error.message)
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.app-container {
  height: 100vh;
  width: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
}

// 侧边栏样式
.sidebar-container {
  height: 100vh;
  background-color: #1f2937; // 深灰色背景
  transition: width 0.3s ease;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  z-index: 1000;
  position: relative;
  
  .debug-info {
    padding: 10px;
    background-color: rgba(0, 0, 0, 0.5);
    color: white;
    font-size: 12px;
    
    p {
      margin: 5px 0;
      word-break: break-all;
    }
  }
  
  .logo-container {
    height: 70px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 16px;
    background-color: #111827; // 更深的灰色
    overflow: hidden;
    transition: all 0.3s;
    margin-bottom: 10px;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 10%;
      width: 80%;
      height: 1px;
      background: rgba(255, 255, 255, 0.1);
    }
    
    &.collapsed {
      padding: 0 8px;
    }
    
    .logo-image {
      height: 36px;
      width: 36px;
      object-fit: contain;
      transition: all 0.3s;
      filter: brightness(1.1);
    }
    
    .logo-content {
      display: flex;
      align-items: center;
      width: 100%;
      
      .logo-image {
        margin-right: 12px;
      }
    }
    
    .logo-title {
      color: white;
      font-size: 18px;
      font-weight: 500;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      letter-spacing: 0.5px;
    }
  }
  
  .side-menu {
    border-right: none;
    background: transparent;
    padding: 10px;
    
    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      height: 50px;
      line-height: 50px;
      color: rgba(255, 255, 255, 0.7);
      background: transparent;
      border-radius: 6px;
      margin: 4px 0;
      
      &:hover {
        color: white;
        background-color: rgba(255, 255, 255, 0.1);
      }
      
      &.is-active {
        color: white;
        background-color: rgba(255, 255, 255, 0.12);
        font-weight: 500;
        
        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 0;
          height: 100%;
          width: 3px;
          background-color: #10b981; // 使用绿色作为活跃元素的突出色
          border-radius: 0 4px 4px 0;
        }
      }
      
      .el-icon {
        font-size: 18px;
        margin-right: 12px;
      }
    }
    
    :deep(.el-sub-menu.is-active) {
      .el-sub-menu__title {
        color: white;
        
        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 0;
          height: 100%;
          width: 3px;
          background-color: #10b981; // 绿色突出色
          border-radius: 0 4px 4px 0;
        }
      }
    }
    
    :deep(.el-sub-menu) {
      .el-menu {
        background: rgba(0, 0, 0, 0.1) !important;
        border-radius: 6px;
        padding: 4px;
        margin-top: 4px;
      }
    }
  }
}

// 头部样式
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  padding: 0 25px;
  position: relative;
  z-index: 900;
  height: 64px;
  
  .header-left {
    display: flex;
    align-items: center;
    
    .collapse-btn {
      cursor: pointer;
      font-size: 20px;
      width: 40px;
      height: 40px;
      display: flex;
      justify-content: center;
      align-items: center;
      border-radius: 8px;
      margin-right: 16px;
      transition: all 0.2s;
      color: #64748b;
      
      &:hover {
        background-color: #f8fafc;
        color: #10b981; // 与侧边栏活跃色调一致
      }
    }
    
    .breadcrumb {
      margin-left: 8px;
      
      :deep(.el-breadcrumb__item) {
        .el-breadcrumb__inner {
          color: #64748b;
          font-weight: 500;
          
          &.is-link {
            color: #10b981;
            font-weight: 500;
            
            &:hover {
              color: #059669;
            }
          }
        }
        
        &:last-child {
          .el-breadcrumb__inner {
            color: #334155;
            font-weight: 600;
          }
        }
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    
    .header-actions {
      display: flex;
      margin-right: 16px;
      
      .action-item {
        width: 36px;
        height: 36px;
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
        border-radius: 8px;
        transition: all 0.2s;
        color: #64748b;
        margin: 0 5px;
        
        &:hover {
          background-color: #f8fafc;
          color: #10b981;
        }
      }
    }
    
    .user-dropdown {
      .user-info {
        display: flex;
        align-items: center;
        cursor: pointer;
        padding: 6px 14px;
        border-radius: 30px;
        transition: all 0.2s;
        border: 1px solid #f1f5f9;
        
        &:hover {
          background-color: #f8fafc;
          border-color: #e2e8f0;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
        }
        
        .user-avatar {
          background-color: #10b981;
          color: white;
          font-weight: bold;
        }
        
        .user-details {
          margin: 0 12px;
          line-height: 1.2;
          
          .username {
            display: block;
            font-size: 14px;
            color: #334155;
            font-weight: 600;
          }
          
          .user-role {
            display: block;
            font-size: 12px;
            color: #64748b;
          }
        }
        
        .dropdown-icon {
          font-size: 12px;
          color: #64748b;
          transition: transform 0.2s;
        }
        
        &:hover .dropdown-icon {
          transform: rotate(180deg);
        }
      }
    }
  }
}

// 下拉菜单样式
:deep(.user-dropdown-menu) {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  
  .el-dropdown-menu__item {
    display: flex;
    align-items: center;
    padding: 10px 16px;
    font-weight: 500;
    transition: all 0.2s;
    
    .el-icon {
      margin-right: 12px;
      font-size: 16px;
      transition: all 0.2s;
    }
    
    &:hover {
      background-color: #f1f5f9;
      color: #10b981;
      
      .el-icon {
        transform: translateX(3px);
        color: #10b981;
      }
    }
    
    &.is-divided {
      border-top: 1px solid #f1f5f9;
      margin-top: 4px;
      padding-top: 10px;
    }
  }
}

// 主内容区样式
.main-container {
  height: 100vh;
  transition: all 0.3s;
  background-color: #f9fafb;
  margin: 0;
  padding: 0;
  overflow: hidden;
  
  .app-main {
    padding: 20px;
    height: calc(100vh - 64px);
    overflow-y: auto;
    position: relative;
    box-sizing: border-box;
    
    // 确保所有模块容器都能正确填充
    :deep(.app-container) {
      height: 100%;
      width: 100%;
      box-sizing: border-box;
      overflow-x: hidden;
      
      // 为所有卡片添加样式
      .el-card {
        border-radius: 8px;
        margin-bottom: 16px;
        border: none;
        transition: box-shadow 0.2s;
        overflow: hidden;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
        
        &:hover {
          box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
        }
        
        .el-card__header {
          padding: 15px 20px;
          border-bottom: 1px solid #f1f5f9;
        }
        
        .el-card__body {
          padding: 20px;
        }
      }
      
      // 表格样式
      .el-table {
        border-radius: 8px;
        overflow: hidden;
        
        th {
          background-color: #f8fafc !important;
          color: #334155 !important;
          font-weight: 600;
          padding: 12px 0;
        }
        
        td {
          padding: 12px 0;
        }
      }
      
      // 分页样式
      .el-pagination {
        margin-top: 16px;
        justify-content: flex-end;
        
        .el-pagination__total {
          font-weight: 500;
        }
        
        .btn-prev, .btn-next {
          background-color: white;
          border-radius: 6px;
          margin: 0 5px;
          
          &:hover {
            color: #10b981;
          }
        }
        
        .el-pager li {
          border-radius: 6px;
          margin: 0 3px;
          
          &.is-active {
            background-color: #10b981;
            color: white;
          }
        }
      }
    }
  }
}

// 修复 Element Plus 容器样式
:deep(.el-container) {
  height: 100vh;
  margin: 0;
  padding: 0;
  overflow: hidden;
  
  .el-aside {
    margin: 0;
    padding: 0;
    overflow: hidden;
  }
  
  .el-main {
    margin: 0;
    padding: 0;
    overflow: hidden;
  }
}

// 页面切换动画
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.2s ease;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style> 