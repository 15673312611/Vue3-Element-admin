<template>
  <div class="app-container">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h2 class="welcome-title">
          欢迎使用<span class="gradient-text">形象分类</span>管理
        </h2>
        <p class="welcome-subtitle">在这里您可以管理所有的形象分类信息</p>
      </div>

      <div class="welcome-decoration">
        <div class="circle circle-1"></div>
        <div class="circle circle-2"></div>
      </div>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="分类名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入分类名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增分类
          </el-button>
        </div>
      </template>

      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="categoryList"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="分类名称" min-width="120" show-overflow-tooltip>
          <template #default="scope">
            <div class="category-name">
              <el-icon><Folder /></el-icon>
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip>
          <template #default="scope">
            <div class="category-description">
              {{ scope.row.description || '暂无描述' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 1 ? 'success' : 'danger'"
              effect="light"
              size="small"
            >
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center">
          <template #default="scope">
            <div class="create-time">
              <el-icon><Clock /></el-icon>
              <span>{{ formatDateTime(scope.row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <div class="table-actions">
              <el-button type="primary" link @click="handleUpdate(scope.row)">
                <el-icon><Edit /></el-icon>编辑
              </el-button>
              <el-button type="danger" link @click="handleDelete(scope.row)">
                <el-icon><Delete /></el-icon>删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加或修改对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="500px"
      append-to-body
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入描述"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategoryList, getCategoryDetail, addCategory, updateCategory, deleteCategory, changeCategoryStatus } from '@/api/templateCategory'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  status: undefined
})

// 数据列表
const categoryList = ref([])
const total = ref(0)
const loading = ref(false)
const open = ref(false)
const title = ref('')

// 表单对象
const form = reactive({
  id: undefined,
  name: '',
  description: '',
  status: 1
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

// 表单引用
const formRef = ref(null)
const queryForm = ref(null)

// 初始化
onMounted(() => {
  getList()
})

// 获取列表
const getList = async () => {
  try {
    loading.value = true
    const res = await getCategoryList(queryParams)
    categoryList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取分类列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  queryForm.value?.resetFields()
  handleQuery()
}

// 新增按钮操作
const handleAdd = () => {
  reset()
  open.value = true
  title.value = '添加分类'
}

// 修改按钮操作
const handleUpdate = async (row) => {
  try {
    const res = await getCategoryDetail(row.id)
    Object.assign(form, res.data)
    open.value = true
    title.value = '修改分类'
  } catch (error) {
    console.error('获取分类详情失败：', error)
    ElMessage.error('获取分类详情失败')
  }
}

// 删除按钮操作
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该分类吗？删除后不可恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCategory(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败：', error)
    }
  }).catch(() => {})
}

// 表单重置
const reset = () => {
  form.id = undefined
  form.name = ''
  form.description = ''
  form.status = 1
  formRef.value?.resetFields()
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}

// 提交按钮
const submitForm = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateCategory(form.id, form)
          ElMessage.success('修改成功')
        } else {
          await addCategory(form)
          ElMessage.success('新增成功')
        }
        open.value = false
        getList()
      } catch (error) {
        console.error('保存失败：', error)
      }
    }
  })
}

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

// 页码改变
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 格式化日期时间
const formatDateTime = (date) => {
  if (!date) return '暂无数据'
  return new Date(date).toLocaleString()
}
</script>

<style lang="scss" scoped>
// 定义变量
$primary-color: #409eff;
$success-color: #67c23a;
$warning-color: #e6a23c;
$danger-color: #f56c6c;
$info-color: #909399;
$background-color: #f5f7fa;
$border-color: #e5e7eb;
$text-color: #334155;
$text-color-light: #64748b;

.app-container {
  padding: 20px;
  background-color: $background-color;
  min-height: calc(100vh - 84px);

  .welcome-banner {
    position: relative;
    background: linear-gradient(135deg, #334155, #475569);
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 20px;
    color: white;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

    .welcome-content {
      position: relative;
      z-index: 1;

      .welcome-title {
        font-size: 24px;
        font-weight: 600;
        margin: 0 0 8px 0;

        .gradient-text {
          background: linear-gradient(45deg, #ffd700, #ffa500);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          font-weight: 700;
        }
      }

      .welcome-subtitle {
        font-size: 14px;
        opacity: 0.9;
        margin: 0;
      }
    }

    .welcome-actions {
      position: relative;
      z-index: 1;
      margin-top: 16px;
    }

    .welcome-decoration {
      position: absolute;
      top: 0;
      right: 0;
      bottom: 0;
      left: 0;
      overflow: hidden;

      .circle {
        position: absolute;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.1);
      }

      .circle-1 {
        width: 160px;
        height: 160px;
        top: -80px;
        right: -80px;
      }

      .circle-2 {
        width: 200px;
        height: 200px;
        bottom: -100px;
        left: -100px;
      }
    }
  }

  .search-card {
    margin-bottom: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    
    :deep(.el-card__body) {
      padding: 20px;
    }
    
    .el-form {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
      
      .el-form-item {
        margin-bottom: 0;
        margin-right: 0;
      }
    }
  }

  .table-card {
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;
    }
    
    :deep(.el-card__body) {
      padding: 20px;
    }
    
    :deep(.el-table) {
      border-radius: 8px;
      overflow: hidden;
      
      .el-table__header-wrapper {
        th {
          background-color: #f8fafc !important;
          color: $text-color !important;
          font-weight: 600;
          padding: 12px 0;
        }
      }
      
      .el-table__row {
        td {
          padding: 12px 0;
        }
        
        &:hover {
          background-color: rgba($primary-color, 0.05) !important;
        }
      }
    }
  }

  .category-name {
    display: flex;
    align-items: center;
    gap: 8px;
    color: $text-color;
    
    .el-icon {
      color: $primary-color;
      font-size: 16px;
    }
  }

  .category-description {
    color: $text-color-light;
    font-size: 14px;
  }

  .create-time {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    color: $text-color-light;
    font-size: 13px;
    
    .el-icon {
      font-size: 14px;
    }
  }

  .table-actions {
    display: flex;
    justify-content: center;
    gap: 12px;
    
    .el-button {
      padding: 4px 8px;
      
      .el-icon {
        margin-right: 4px;
      }
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

:deep(.el-dialog) {
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  
  .el-dialog__header {
    margin: 0;
    padding: 20px;
    border-bottom: 1px solid $border-color;
    
    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: $text-color;
    }
  }
  
  .el-dialog__body {
    padding: 20px;
  }
  
  .el-dialog__footer {
    padding: 20px;
    border-top: 1px solid $border-color;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-tag) {
  border-radius: 4px;
  padding: 0 8px;
  height: 24px;
  line-height: 22px;
  font-size: 12px;
  
  &.el-tag--success {
    background-color: rgba($success-color, 0.1);
    border-color: rgba($success-color, 0.2);
    color: $success-color;
  }
  
  &.el-tag--danger {
    background-color: rgba($danger-color, 0.1);
    border-color: rgba($danger-color, 0.2);
    color: $danger-color;
  }
}
</style> 