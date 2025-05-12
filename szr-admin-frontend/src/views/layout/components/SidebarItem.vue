<template>
  <div v-if="!item.hidden">
    <!-- 如果没有子菜单并且不是嵌套路由 -->
    <template v-if="!hasOneShowingChild(item.children, item) && item.children">
      <el-sub-menu :index="resolvePath(item.path)" :popper-class="'sidebar-popper'">
        <template #title>
          <div class="menu-item-content">
            <div class="icon-wrapper" v-if="item.meta && item.meta.icon">
              <el-icon class="menu-item-icon">
                <component :is="item.meta.icon" />
              </el-icon>
            </div>
            <span class="menu-item-title">{{ item.meta.title }}</span>
          </div>
        </template>
        <sidebar-item
          v-for="child in item.children"
          :key="child.path"
          :item="child"
          :base-path="resolvePath(item.path)"
          :is-collapse="isCollapse"
        />
      </el-sub-menu>
    </template>
    
    <!-- 如果只有一个子菜单或没有子菜单 -->
    <template v-else>
      <el-menu-item :index="resolvePath(onlyOneChild.path)">
        <template #title>
          <div class="menu-item-content">
            <div class="icon-wrapper" v-if="onlyOneChild.meta && onlyOneChild.meta.icon">
              <el-icon class="menu-item-icon">
                <component :is="onlyOneChild.meta.icon" />
              </el-icon>
            </div>
            <span class="menu-item-title">{{ onlyOneChild.meta.title }}</span>
          </div>
        </template>
      </el-menu-item>
    </template>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { isExternal } from '@/utils/validate'

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  basePath: {
    type: String,
    default: ''
  },
  isCollapse: {
    type: Boolean,
    default: false
  }
})

const onlyOneChild = ref(null)

// 判断是否只有一个显示的子菜单
const hasOneShowingChild = (children = [], parent) => {
  if (!children) {
    children = []
  }
  
  const showingChildren = children.filter(item => {
    if (item.hidden) {
      return false
    } else {
      // 如果只有一个children，则递归判断
      onlyOneChild.value = item
      return true
    }
  })
  
  // 当只有一个子菜单时，默认显示子菜单
  if (showingChildren.length === 1) {
    return true
  }
  
  // 当没有子菜单时，显示父菜单
  if (showingChildren.length === 0) {
    onlyOneChild.value = { ...parent, path: '', noShowingChildren: true }
    return true
  }
  
  return false
}

// 解析路径
const resolvePath = (routePath) => {
  if (isExternal(routePath)) {
    return routePath
  }
  if (isExternal(props.basePath)) {
    return props.basePath
  }
  
  // 如果basePath为空，直接返回路由路径
  if (!props.basePath) {
    return routePath ? `/${routePath}` : '/'
  }
  
  // 如果路由路径为空，返回basePath
  if (!routePath) {
    return props.basePath
  }
  
  // 拼接路径，确保不会重复
  const path = `${props.basePath}/${routePath}`.replace(/\/+/g, '/')
  return path.startsWith('/') ? path : `/${path}`
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.menu-item-content {
  display: flex;
  align-items: center;
  width: 100%;
  height: 50px;
  padding: 0 10px;
  position: relative;
  transition: all 0.3s $transition-ease-out;
  border-radius: 8px;
  margin: 4px 0;
  
  .icon-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    margin-right: 12px;
    background: rgba(255, 255, 255, 0.05);
    position: relative;
    transition: all 0.3s $transition-ease-out;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      border-radius: 50%;
      background: transparent;
      opacity: 0;
      transform: scale(0.8);
      transition: all 0.3s $transition-ease-out;
    }
  }
  
  .menu-item-icon {
    font-size: 16px;
    position: relative;
    z-index: 1;
    transition: all 0.3s $transition-ease-out;
    color: rgba(255, 255, 255, 0.7);
  }
  
  .menu-item-title {
    flex: 1;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    font-size: 14px;
    transition: all 0.3s $transition-ease-out;
    font-weight: $font-weight-medium;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      bottom: -4px;
      left: 0;
      height: 2px;
      width: 0;
      background: linear-gradient(90deg, rgba(255, 255, 255, 0.7), transparent);
      transition: all 0.3s $transition-ease-out;
    }
  }
}

:deep(.el-menu-item) {
  margin: 4px 8px;
  border-radius: 8px;
  height: 50px;
  
  &.is-active {
    background: linear-gradient(90deg, $primary-color, rgba($primary-color, 0.7));
    box-shadow: 0 4px 15px rgba($primary-color, 0.3);
    
    .menu-item-content {
      .icon-wrapper {
        background: rgba(255, 255, 255, 0.2);
        
        &::before {
          background: rgba(255, 255, 255, 0.2);
          box-shadow: 0 0 12px rgba(255, 255, 255, 0.6);
          opacity: 1;
          transform: scale(1.2);
        }
      }
      
      .menu-item-icon {
        color: white;
        transform: scale(1.1);
      }
      
      .menu-item-title {
        color: white;
        font-weight: $font-weight-semibold;
        
        &::after {
          width: 60%;
        }
      }
    }
  }
  
  &:hover:not(.is-active) {
    background: rgba(255, 255, 255, 0.05);
    
    .menu-item-content {
      .icon-wrapper {
        background: rgba(255, 255, 255, 0.1);
        transform: translateX(3px);
      }
      
      .menu-item-icon {
        color: rgba(255, 255, 255, 0.9);
      }
      
      .menu-item-title {
        color: rgba(255, 255, 255, 0.9);
        transform: translateX(3px);
      }
    }
  }
}

:deep(.el-sub-menu__title) {
  margin: 4px 8px;
  border-radius: 8px;
  height: 50px;
  
  &:hover {
    background: rgba(255, 255, 255, 0.05);
    
    .menu-item-content {
      .icon-wrapper {
        background: rgba(255, 255, 255, 0.1);
      }
      
      .menu-item-icon {
        color: rgba(255, 255, 255, 0.9);
      }
      
      .menu-item-title {
        color: rgba(255, 255, 255, 0.9);
      }
    }
  }
}

:deep(.sidebar-popper) {
  .el-menu {
    background: $sidebar-dark !important;
    border: none;
    padding: 4px;
    border-radius: 8px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
    
    .el-menu-item {
      color: rgba(255, 255, 255, 0.7);
      background: transparent;
      margin: 5px 0;
      border-radius: 6px;
      height: 44px;
      
      &:hover {
        color: white;
        background-color: rgba(255, 255, 255, 0.08);
      }
      
      &.is-active {
        color: white;
        background: linear-gradient(90deg, $primary-color, rgba($primary-color, 0.7));
        box-shadow: 0 4px 10px rgba($primary-color, 0.3);
      }
    }
  }
}
</style> 