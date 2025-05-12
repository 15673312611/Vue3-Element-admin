<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    :width="width"
    :destroy-on-close="destroyOnClose"
    :close-on-click-modal="closeOnClickModal"
    :close-on-press-escape="closeOnPressEscape"
    :show-close="showClose"
    :draggable="draggable"
    :fullscreen="fullscreen"
    :top="top"
    :modal="modal"
    :append-to-body="appendToBody"
    custom-class="enhanced-dialog"
    @close="handleClose"
    @opened="$emit('opened')"
    @closed="$emit('closed')"
  >
    <template #header>
      <div class="dialog-header">
        <div class="dialog-title">
          <el-icon v-if="icon" class="dialog-icon"><component :is="icon" /></el-icon>
          <span>{{ title }}</span>
        </div>
        <div class="dialog-actions" v-if="showClose">
          <el-tooltip effect="dark" content="最大化" placement="top" v-if="!fullscreen">
            <el-icon class="action-icon" @click="toggleFullscreen"><FullScreen /></el-icon>
          </el-tooltip>
          <el-tooltip effect="dark" content="恢复" placement="top" v-else>
            <el-icon class="action-icon" @click="toggleFullscreen"><ScaleToOriginal /></el-icon>
          </el-tooltip>
          <el-tooltip effect="dark" content="关闭" placement="top">
            <el-icon class="action-icon" @click="handleClose"><Close /></el-icon>
          </el-tooltip>
        </div>
      </div>
    </template>
    
    <!-- 对话框内容 -->
    <div class="dialog-body" :class="{ 'is-scrollable': scrollable }">
      <slot></slot>
    </div>
    
    <!-- 底部按钮 -->
    <template #footer v-if="$slots.footer || showFooter">
      <div class="dialog-footer">
        <slot name="footer">
          <el-button @click="handleCancel" :disabled="loading">{{ cancelText }}</el-button>
          <el-button type="primary" @click="handleConfirm" :loading="loading">{{ confirmText }}</el-button>
        </slot>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '对话框'
  },
  icon: {
    type: String,
    default: ''
  },
  width: {
    type: String,
    default: '500px'
  },
  destroyOnClose: {
    type: Boolean,
    default: true
  },
  closeOnClickModal: {
    type: Boolean,
    default: false
  },
  closeOnPressEscape: {
    type: Boolean,
    default: true
  },
  showClose: {
    type: Boolean,
    default: true
  },
  draggable: {
    type: Boolean,
    default: false
  },
  top: {
    type: String,
    default: '15vh'
  },
  modal: {
    type: Boolean,
    default: true
  },
  appendToBody: {
    type: Boolean,
    default: true
  },
  scrollable: {
    type: Boolean,
    default: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  showFooter: {
    type: Boolean,
    default: true
  },
  confirmText: {
    type: String,
    default: '确定'
  },
  cancelText: {
    type: String,
    default: '取消'
  }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel', 'close', 'opened', 'closed'])

const dialogVisible = ref(props.modelValue)
const fullscreen = ref(props.fullscreen || false)

// 监听modelValue的变化
watch(() => props.modelValue, (val) => {
  dialogVisible.value = val
})

// 监听dialogVisible的变化
watch(() => dialogVisible.value, (val) => {
  emit('update:modelValue', val)
})

// 处理关闭
const handleClose = () => {
  dialogVisible.value = false
  emit('close')
}

// 处理取消
const handleCancel = () => {
  dialogVisible.value = false
  emit('cancel')
}

// 处理确认
const handleConfirm = () => {
  emit('confirm')
}

// 切换全屏
const toggleFullscreen = () => {
  fullscreen.value = !fullscreen.value
}

// 暴露方法
defineExpose({
  open: () => {
    dialogVisible.value = true
  },
  close: () => {
    dialogVisible.value = false
  }
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

:deep(.enhanced-dialog) {
  border-radius: $border-radius-lg;
  overflow: hidden;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15), 0 5px 15px rgba(0, 0, 0, 0.1);
  
  .el-dialog__header {
    margin: 0;
    padding: 0;
  }
  
  .dialog-header {
    padding: $spacing-md $spacing-lg;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
    background-color: #f9fafc;
    
    .dialog-title {
      display: flex;
      align-items: center;
      font-size: $font-size-lg;
      font-weight: $font-weight-semibold;
      color: $dark-text;
      
      .dialog-icon {
        margin-right: $spacing-sm;
        font-size: 1.1em;
        color: $primary-color;
      }
    }
    
    .dialog-actions {
      display: flex;
      align-items: center;
      
      .action-icon {
        cursor: pointer;
        font-size: 18px;
        padding: 8px;
        border-radius: $border-radius;
        color: $light-text;
        transition: all 0.3s;
        
        &:hover {
          color: $primary-color;
          background-color: rgba(0, 0, 0, 0.05);
        }
        
        &+.action-icon {
          margin-left: $spacing-xs;
        }
      }
    }
  }
  
  .el-dialog__body {
    padding: 0;
  }
  
  .dialog-body {
    padding: $spacing-xl;
    
    &.is-scrollable {
      max-height: 60vh;
      overflow-y: auto;
    }
  }
  
  .el-dialog__footer {
    padding: 0;
  }
  
  .dialog-footer {
    padding: $spacing-md $spacing-lg;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    border-top: 1px solid rgba(0, 0, 0, 0.05);
    background-color: #f9fafc;
    
    .el-button+.el-button {
      margin-left: $spacing-md;
    }
  }
}
</style> 