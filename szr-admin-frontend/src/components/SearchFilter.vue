<template>
  <el-card class="search-filter-card" shadow="hover">
    <template #header>
      <div class="filter-header">
        <div class="filter-title">
          <el-icon class="filter-icon"><Search /></el-icon>
          <span>{{ title }}</span>
        </div>
        <div class="filter-actions">
          <el-button type="primary" link @click="$emit('reset')">
            <el-icon class="mr-xs"><RefreshRight /></el-icon>重置
          </el-button>
          <el-button type="primary" link @click="$emit('fold')">
            <el-icon class="mr-xs">
              <component :is="fold ? 'ArrowDown' : 'ArrowUp'" />
            </el-icon>
            {{ fold ? '展开' : '收起' }}
          </el-button>
        </div>
      </div>
    </template>
    
    <el-form ref="formRef" :model="model" :inline="true" class="filter-form" :class="{ 'is-collapsed': fold }">
      <slot></slot>
      <el-form-item class="filter-buttons">
        <el-button type="primary" :loading="loading" @click="$emit('search')" class="search-button">
          <el-icon class="mr-xs"><Search /></el-icon>查询
        </el-button>
        <el-button @click="$emit('reset')" :disabled="loading">
          <el-icon class="mr-xs"><Delete /></el-icon>清空
        </el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  model: {
    type: Object,
    required: true
  },
  title: {
    type: String,
    default: '筛选条件'
  },
  loading: {
    type: Boolean,
    default: false
  },
  fold: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['search', 'reset', 'fold'])

// 表单引用
const formRef = ref(null)

// 暴露方法和属性给父组件
defineExpose({
  form: formRef
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.search-filter-card {
  margin-bottom: $spacing-lg;
  transition: all 0.3s;
  border-radius: $border-radius-lg;
  overflow: hidden;
  box-shadow: $card-shadow;
  
  &:hover {
    box-shadow: $hover-shadow;
  }
  
  .filter-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .filter-title {
      display: flex;
      align-items: center;
      font-size: $font-size-md;
      font-weight: $font-weight-medium;
      
      .filter-icon {
        margin-right: $spacing-xs;
        color: $primary-color;
        font-size: 18px;
      }
    }
    
    .filter-actions {
      display: flex;
      align-items: center;
      
      .el-button + .el-button {
        margin-left: $spacing-sm;
      }
    }
  }
  
  .filter-form {
    display: flex;
    flex-wrap: wrap;
    gap: $spacing-sm;
    padding: $spacing-xs 0;
    
    &.is-collapsed {
      max-height: 70px;
      overflow: hidden;
    }
    
    :deep(.el-form-item) {
      margin-bottom: $spacing-sm;
      margin-right: $spacing-md;
      
      .el-form-item__label {
        font-weight: $font-weight-medium;
        color: $light-text;
      }
      
      .el-input__wrapper,
      .el-textarea__wrapper,
      .el-select__wrapper {
        box-shadow: 0 0 0 1px $border-color !important;
        transition: all 0.3s ease;
        
        &:hover {
          box-shadow: 0 0 0 1px darken($border-color, 15%) !important;
        }
        
        &.is-focus {
          box-shadow: 0 0 0 2px rgba($primary-color, 0.3) !important;
        }
      }
      
      .el-input,
      .el-select,
      .el-date-editor {
        width: 220px;
      }
      
      .el-range-editor {
        width: 380px;
      }
    }
    
    .filter-buttons {
      display: flex;
      align-items: center;
      margin-left: auto;
      
      .search-button {
        min-width: 100px;
      }
    }
  }
}

@media (max-width: 768px) {
  .search-filter-card {
    .filter-form {
      :deep(.el-form-item) {
        margin-right: 0;
        width: 100%;
        
        .el-input,
        .el-select,
        .el-date-editor,
        .el-range-editor {
          width: 100%;
        }
      }
      
      .filter-buttons {
        width: 100%;
        justify-content: center;
        margin-top: $spacing-sm;
        
        .el-button {
          flex: 1;
        }
      }
    }
  }
}
</style> 