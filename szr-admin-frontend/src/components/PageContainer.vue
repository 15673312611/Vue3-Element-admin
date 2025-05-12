<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header" v-if="title">
      <div class="title-section">
        <h1 class="page-title">
          <el-icon v-if="icon" class="page-icon"><component :is="icon" /></el-icon>
          <span>{{ title }}</span>
        </h1>
        <p class="page-subtitle" v-if="subtitle">{{ subtitle }}</p>
      </div>
      <div class="header-actions">
        <slot name="actions"></slot>
      </div>
    </div>
    
    <!-- 页面内容 -->
    <div class="page-content" :class="{ 'has-header': title }">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: ''
  },
  subtitle: {
    type: String,
    default: ''
  },
  icon: {
    type: String,
    default: ''
  }
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.page-container {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: $spacing-lg;
    padding-bottom: $spacing-md;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      height: 2px;
      width: 80px;
      background: linear-gradient(90deg, $primary-color, rgba($primary-color, 0.2));
      border-radius: $border-radius-pill;
    }
    
    .title-section {
      flex: 1;
      
      .page-title {
        font-size: $font-size-heading;
        font-weight: $font-weight-semibold;
        color: $dark-text;
        margin: 0 0 $spacing-xs 0;
        display: flex;
        align-items: center;
        
        .page-icon {
          margin-right: $spacing-sm;
          font-size: 1.3em;
          color: $primary-color;
        }
      }
      
      .page-subtitle {
        font-size: $font-size-md;
        color: $light-text;
        margin: 0;
        max-width: 600px;
      }
    }
    
    .header-actions {
      display: flex;
      align-items: center;
      
      :deep(.el-button) {
        margin-left: $spacing-sm;
      }
    }
  }
  
  .page-content {
    flex: 1;
    position: relative;
    
    &.has-header {
      padding-top: $spacing-sm;
    }
  }
}

@media (max-width: 768px) {
  .page-container {
    .page-header {
      flex-direction: column;
      
      .header-actions {
        margin-top: $spacing-md;
      }
    }
  }
}
</style> 