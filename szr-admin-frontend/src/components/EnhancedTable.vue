<template>
  <div class="enhanced-table">
    <!-- 表格卡片容器 -->
    <el-card class="table-card" shadow="hover" :body-style="{ padding: 0 }">
      <!-- 卡片标题 -->
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon v-if="icon" class="mr-sm text-primary"><component :is="icon" /></el-icon>
            <span>{{ title }}</span>
            <el-tag v-if="showTotal && total > 0" type="info" effect="plain" class="ml-sm">总计 {{ total }} 项</el-tag>
          </div>
          <div class="header-actions">
            <slot name="actions"></slot>
            <el-tooltip v-if="showRefresh" effect="dark" content="刷新数据" placement="top">
              <el-button type="primary" circle plain size="small" @click="$emit('refresh')">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </el-tooltip>
          </div>
        </div>
      </template>
      
      <!-- 工具栏 -->
      <div class="table-toolbar" v-if="$slots.toolbar">
        <slot name="toolbar"></slot>
      </div>
      
      <!-- 数据表格 -->
      <el-table
        ref="tableRef"
        v-bind="$attrs"
        :data="data"
        v-loading="loading"
        :max-height="maxHeight"
        :header-cell-style="{ background: '#f8f9fa' }"
        :row-class-name="rowClassName"
        stripe
        highlight-current-row
        @selection-change="$emit('selection-change', $event)"
        @row-click="handleRowClick"
      >
        <el-table-column v-if="showSelection" type="selection" width="50" align="center" />
        <el-table-column v-if="showIndex" type="index" label="#" width="60" align="center" />
        
        <!-- 表格列 -->
        <slot></slot>
        
        <!-- 操作列 -->
        <el-table-column v-if="$slots.operation" :label="operationLabel" :width="operationWidth" :fixed="operationFixed" align="center">
          <template #default="scope">
            <div class="table-operations">
              <slot name="operation" :row="scope.row" :index="scope.$index"></slot>
            </div>
          </template>
        </el-table-column>
        
        <!-- 无数据 -->
        <template #empty>
          <el-empty description="暂无数据" :image-size="100">
            <template #description>
              <p>{{ emptyText }}</p>
            </template>
            <slot name="empty-action"></slot>
          </el-empty>
        </template>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container" v-if="showPagination && total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="pageSizes"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="$emit('size-change', pageSize)"
          @current-change="$emit('current-change', currentPage)"
          background
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '数据列表'
  },
  icon: {
    type: String,
    default: 'List'
  },
  total: {
    type: Number,
    default: 0
  },
  page: {
    type: Number,
    default: 1
  },
  size: {
    type: Number,
    default: 10
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  },
  maxHeight: {
    type: [String, Number],
    default: null
  },
  showIndex: {
    type: Boolean,
    default: true
  },
  showSelection: {
    type: Boolean,
    default: false
  },
  showPagination: {
    type: Boolean,
    default: true
  },
  showTotal: {
    type: Boolean,
    default: true
  },
  showRefresh: {
    type: Boolean,
    default: true
  },
  operationLabel: {
    type: String,
    default: '操作'
  },
  operationWidth: {
    type: [String, Number],
    default: 'auto'
  },
  operationFixed: {
    type: String,
    default: 'right'
  },
  emptyText: {
    type: String,
    default: '暂无数据'
  },
  rowClickHighlight: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits([
  'refresh',
  'selection-change',
  'row-click',
  'size-change',
  'current-change'
])

const tableRef = ref(null)
const currentPage = ref(props.page)
const pageSize = ref(props.size)

// 当外部页码变化时，更新内部页码
watch(() => props.page, (newVal) => {
  currentPage.value = newVal
})

// 当外部每页条数变化时，更新内部每页条数
watch(() => props.size, (newVal) => {
  pageSize.value = newVal
})

// 处理行点击事件
const handleRowClick = (row, column, event) => {
  emit('row-click', row, column, event)
  if (props.rowClickHighlight && tableRef.value) {
    tableRef.value.setCurrentRow(row)
  }
}

// 自定义行类名
const rowClassName = ({ row, rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 对外暴露方法
defineExpose({
  refreshTable: () => {
    emit('refresh')
  },
  clearSelection: () => {
    if (tableRef.value) {
      tableRef.value.clearSelection()
    }
  },
  toggleRowSelection: (row, selected) => {
    if (tableRef.value) {
      tableRef.value.toggleRowSelection(row, selected)
    }
  },
  setCurrentRow: (row) => {
    if (tableRef.value) {
      tableRef.value.setCurrentRow(row)
    }
  }
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.enhanced-table {
  width: 100%;
  
  .table-card {
    box-shadow: $card-shadow;
    transition: all 0.3s;
    border-radius: $border-radius-lg;
    overflow: hidden;
    margin-bottom: $spacing-lg;
    
    &:hover {
      box-shadow: $hover-shadow;
    }
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .card-title {
        display: flex;
        align-items: center;
        font-size: $font-size-md;
        font-weight: $font-weight-medium;
        color: $dark-text;
        
        .el-icon {
          font-size: 18px;
          margin-right: $spacing-xs;
        }
      }
      
      .header-actions {
        display: flex;
        align-items: center;
        
        .el-button {
          margin-left: $spacing-xs;
        }
      }
    }
    
    .table-toolbar {
      padding: $spacing-md;
      background-color: rgba(249, 250, 252, 0.5);
      border-bottom: 1px solid rgba(0, 0, 0, 0.03);
    }
    
    :deep(.el-table) {
      border: none;
      width: 100%;
      
      &::before, &::after {
        display: none;
      }
      
      .el-table__header-wrapper {
        .el-table__header {
          th.el-table__cell {
            background-color: #f8fafc;
            color: $dark-text;
            font-weight: $font-weight-medium;
            font-size: $font-size-sm;
            height: 50px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            border-bottom: 1px solid rgba(0, 0, 0, 0.05);
          }
        }
      }
      
      .el-table__body-wrapper {
        .el-table__row {
          transition: all 0.2s;
          height: 60px;
          
          &:hover {
            background-color: $hover-color !important;
          }
          
          &.current-row {
            background-color: rgba($primary-color, 0.05) !important;
            td.el-table__cell {
              background-color: rgba($primary-color, 0.05) !important;
            }
          }
          
          &.even-row {
            background-color: transparent;
          }
          
          &.odd-row {
            background-color: rgba(0, 0, 0, 0.015);
          }
          
          td.el-table__cell {
            color: $light-text;
            border-bottom: 1px solid rgba(0, 0, 0, 0.03);
          }
        }
      }
      
      .table-operations {
        display: flex;
        justify-content: center;
        align-items: center;
        
        .el-button {
          font-size: $font-size-sm;
          padding: 8px;
          
          &+.el-button {
            margin-left: $spacing-xs;
          }
        }
      }
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: $spacing-md $spacing-lg;
    border-top: 1px solid rgba(0, 0, 0, 0.05);
    background-color: rgba(249, 250, 252, 0.5);
  }
}
</style> 