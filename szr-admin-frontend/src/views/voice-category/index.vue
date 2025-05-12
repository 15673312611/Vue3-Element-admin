<template>
  <div class="category-manager">
    <div class="category-container">
      <!-- 顶部标题区 -->
      <div class="header-banner">
        <div class="left-content">
          <i class="el-icon-folder icon-large"></i>
          <div class="title-group">
            <h2 class="main-title">音色分类管理</h2>
            <p class="sub-title">管理系统中的不同音色分类</p>
          </div>
        </div>
        <el-button type="primary" @click="handleAdd" class="add-button">
          <i class="el-icon-plus"></i> 新增分类
        </el-button>
      </div>

      <!-- 搜索区域 -->
      <div class="search-section">
        <el-form :model="queryParams" ref="queryForm" :inline="true">
          <el-form-item>
            <el-input 
              v-model="queryParams.name" 
              placeholder="搜索分类名称" 
              clearable 
              prefix-icon="el-icon-search"
              @keyup.enter="handleQuery"
              class="keyword-input"
            />
          </el-form-item>
          <el-form-item>
            <el-select 
              v-model="queryParams.status" 
              placeholder="状态筛选" 
              clearable
              class="status-select"
            >
              <el-option label="启用" value="1" />
              <el-option label="禁用" value="0" />
            </el-select>
          </el-form-item>
          <el-form-item class="button-group">
            <el-button type="primary" @click="handleQuery" class="query-btn">
              <i class="el-icon-search"></i> 搜索
            </el-button>
            <el-button @click="resetQuery" class="reset-btn">
              <i class="el-icon-refresh"></i> 重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格区域 -->
      <div class="table-area">
        <div class="table-header">
          <h3 class="table-title">
            <i class="el-icon-folder-opened"></i> 分类列表
            <span class="record-count" v-if="total > 0">共 {{ total }} 条</span>
          </h3>
          <el-button 
            type="text" 
            icon="el-icon-refresh" 
            @click="getList"
            class="refresh-btn"
          >
            刷新
          </el-button>
        </div>

        <el-table
          v-loading="loading"
          :data="categoryList"
          border
          :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
          class="category-table"
        >
          <el-table-column prop="id" label="ID" width="100" align="center" />
          
          <el-table-column prop="name" label="分类名称" width="200">
            <template #default="scope">
              <span class="name-text">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="description" label="描述" min-width="300">
            <template #default="scope">
              <span class="description-text">{{ scope.row.description || '暂无描述' }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="状态" width="120" align="center">
            <template #default="scope">
              <div class="status-cell">
                <span 
                  :class="['status-dot', scope.row.status === '1' || scope.row.status === 1 ? 'active-dot' : 'inactive-dot']"
                ></span>
                <span 
                  :class="['status-label', scope.row.status === '1' || scope.row.status === 1 ? 'active-label' : 'inactive-label']"
                >
                  {{ scope.row.status === '1' || scope.row.status === 1 ? '启用' : '禁用' }}
                </span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="320" fixed="right">
            <template #default="scope">
              <div class="action-group">
                <el-button 
                  type="primary" 
                  icon="el-icon-edit" 
                  size="mini" 
                  @click="handleEdit(scope.row)"
                  plain
                  class="action-button"
                >
                  编辑
                </el-button>
                <el-button 
                  :type="scope.row.status === '1' || scope.row.status === 1 ? 'warning' : 'success'" 
                  :icon="scope.row.status === '1' || scope.row.status === 1 ? 'el-icon-close' : 'el-icon-check'" 
                  size="mini" 
                  @click="handleStatusChange(scope.row)"
                  plain
                  class="action-button"
                >
                  {{ scope.row.status === '1' || scope.row.status === 1 ? '禁用' : '启用' }}
                </el-button>
                <el-button 
                  type="danger" 
                  icon="el-icon-delete" 
                  size="mini" 
                  @click="handleDelete(scope.row)"
                  plain
                  class="action-button"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-box">
          <el-pagination
            background
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="page"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="size"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
          />
        </div>
      </div>
    </div>

    <!-- 对话框 -->
    <el-dialog
      :title="form.id ? '编辑分类' : '新增分类'"
      v-model="dialogVisible"
      width="550px"
      destroy-on-close
      custom-class="category-form-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
          <div class="form-tips">
            <i class="el-icon-info"></i>
            <span>简要描述该分类的用途和特点</span>
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1" class="status-radio">
              <div class="status-option">
                <span class="status-dot active-dot"></span>
                <span>启用</span>
              </div>
            </el-radio>
            <el-radio label="0" class="status-radio">
              <div class="status-option">
                <span class="status-dot inactive-dot"></span>
                <span>禁用</span>
              </div>
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm" :loading="formLoading">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getVoiceCategoryList, getVoiceCategoryDetail, addVoiceCategory, updateVoiceCategory, deleteVoiceCategory, changeVoiceCategoryStatus } from '@/api/voiceCategory'

// 查询参数
const queryParams = reactive({
  name: '',
  status: ''
})

// 数据列表
const categoryList = ref([])
const total = ref(0)
const loading = ref(false)
const formLoading = ref(false)
const page = ref(1)
const size = ref(10)

// 对话框
const dialogVisible = ref(false)
const form = reactive({
  id: '',
  name: '',
  description: '',
  status: '1'
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '长度不能超过 200 个字符', trigger: 'blur' }
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
    const params = {
      page: page.value,
      size: size.value,
      name: queryParams.name,
      status: queryParams.status
    }
    const res = await getVoiceCategoryList(params)
    categoryList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取分类列表失败：', error)
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  page.value = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  if (queryForm.value) {
    queryForm.value.resetFields()
  } else {
    queryParams.name = ''
    queryParams.status = ''
  }
  handleQuery()
}

// 处理新增
const handleAdd = () => {
  form.id = ''
  form.name = ''
  form.description = ''
  form.status = '1'
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = async (row) => {
  if (!row || !row.id) {
    ElMessage.error('无效的分类数据')
    return
  }
  
  try {
    formLoading.value = true
    const res = await getVoiceCategoryDetail(row.id)
    if (res && res.data) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    } else {
      ElMessage.error('获取分类详情失败')
    }
  } catch (error) {
    console.error('获取分类详情失败：', error)
    ElMessage.error('获取分类详情失败')
  } finally {
    formLoading.value = false
  }
}

// 处理状态变更
const handleStatusChange = (row) => {
  // 直接判断当前状态是不是1
  const currentStatus = Number(row.status);
  const newStatus = currentStatus === 1 ? 2 : 1;
  const statusText = currentStatus === 1 ? '禁用' : '启用';
  
  ElMessageBox.confirm(`确定要${statusText}该分类吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await changeVoiceCategoryStatus(row.id, newStatus)
      ElMessage.success(`${statusText}成功`)
      getList()
    } catch (error) {
      console.error(`${statusText}失败：`, error)
      ElMessage.error(`${statusText}失败`)
    }
  }).catch(() => {})
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该分类吗？删除后不可恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteVoiceCategory(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败：', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 提交表单
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        formLoading.value = true
        if (form.id) {
          await updateVoiceCategory(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await addVoiceCategory(form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('保存失败：', error)
        ElMessage.error('保存失败')
      } finally {
        formLoading.value = false
      }
    }
  })
}

// 分页处理
const handleSizeChange = (val) => {
  size.value = val
  getList()
}

const handleCurrentChange = (val) => {
  page.value = val
  getList()
}
</script>

<style lang="scss" scoped>
.category-manager {
  height: 100%;
  width: 100%;
  background-color: #f0f2f5;
  position: relative;
  overflow: auto;
  padding: 20px;
  box-sizing: border-box;
  
  .category-container {
    height: auto;
    max-width: 1400px;
    margin: 0 auto;
    
    // 头部横幅
    .header-banner {
      display: flex;
      justify-content: space-between;
      align-items: center;
      background: linear-gradient(135deg, #334155, #475569);
      padding: 20px 30px;
      border-radius: 8px;
      color: white;
      margin-bottom: 20px;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
      
      .left-content {
        display: flex;
        align-items: center;
        
        .icon-large {
          font-size: 36px;
          background: rgba(255, 255, 255, 0.2);
          border-radius: 50%;
          padding: 12px;
          margin-right: 16px;
        }
        
        .title-group {
          .main-title {
            font-size: 24px;
            margin: 0 0 5px 0;
            font-weight: 600;
          }
          
          .sub-title {
            margin: 0;
            font-size: 14px;
            opacity: 0.85;
          }
        }
      }
      
      .add-button {
        background: rgba(255, 255, 255, 0.25);
        border: none;
        color: white;
        font-weight: 500;
        padding: 10px 20px;
        border-radius: 20px;
        transition: all 0.3s;
        
        &:hover {
          background: rgba(255, 255, 255, 0.35);
          transform: translateY(-2px);
        }
        
        i {
          margin-right: 4px;
        }
      }
    }
    
    // 搜索区域
    .search-section {
      background: white;
      padding: 16px 20px;
      border-radius: 8px;
      margin-bottom: 20px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
      
      .el-form {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        
        .el-form-item {
          margin-right: 15px;
          margin-bottom: 0;
        }
        
        .keyword-input {
          width: 240px;
        }
        
        .status-select {
          width: 150px;
        }
        
        .button-group {
          margin-left: auto;
          
          .query-btn, 
          .reset-btn {
            padding: 9px 16px;
          }
          
          .query-btn {
            margin-right: 10px;
          }
        }
      }
    }
    
    // 表格区域
    .table-area {
      background: white;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
      
      .table-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 20px;
        border-bottom: 1px solid #ebeef5;
        
        .table-title {
          margin: 0;
          font-size: 16px;
          font-weight: 600;
          display: flex;
          align-items: center;
          
          i {
            color: #1890ff;
            margin-right: 8px;
            font-size: 18px;
          }
          
          .record-count {
            font-size: 13px;
            color: #909399;
            margin-left: 10px;
            font-weight: normal;
            background: #f5f7fa;
            padding: 2px 8px;
            border-radius: 10px;
          }
        }
        
        .refresh-btn {
          &:hover {
            color: #1890ff;
          }
        }
      }
      
      .category-table {
        width: 100%;
        
        // 表格单元格样式
        .name-cell {
          display: flex;
          align-items: center;
          
          .name-text {
            font-weight: 500;
            color: #303133;
          }
        }
        
        .description-text {
          color: #606266;
          font-size: 13px;
        }
        
        .status-cell {
          display: flex;
          align-items: center;
          justify-content: center;
          
          .status-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            margin-right: 5px;
            
            &.active-dot {
              background-color: #67c23a;
            }
            
            &.inactive-dot {
              background-color: #f56c6c;
            }
          }
          
          .status-label {
            &.active-label {
              color: #67c23a;
            }
            
            &.inactive-label {
              color: #f56c6c;
            }
          }
        }
        
        .action-group {
          display: flex;
          justify-content: space-around;
          
          .el-button {
            padding: 5px 12px;
            margin: 0 6px;
          }
          
          .action-button {
            color: #fff;
            
            &.el-button--primary {
              background-color: #409EFF;
              border-color: #409EFF;
            }
            
            &.el-button--success {
              background-color: #67C23A;
              border-color: #67C23A;
            }
            
            &.el-button--warning {
              background-color: #E6A23C;
              border-color: #E6A23C;
            }
            
            &.el-button--danger {
              background-color: #F56C6C;
              border-color: #F56C6C;
            }
          }
        }
      }
      
      .pagination-box {
        padding: 15px 20px;
        display: flex;
        justify-content: flex-end;
        background: white;
        border-top: 1px solid #ebeef5;
      }
    }
  }
  
  // 表单对话框
  .category-form-dialog {
    .el-dialog__header {
      background: #f5f7fa;
      padding: 15px 20px;
      border-bottom: 1px solid #e4e7ed;
      
      .el-dialog__title {
        font-weight: 600;
        color: #303133;
      }
    }
    
    .el-dialog__body {
      padding: 20px;
    }
    
    .form-tips {
      margin-top: 5px;
      font-size: 12px;
      color: #909399;
      display: flex;
      align-items: center;
      
      i {
        color: #909399;
        margin-right: 5px;
        font-size: 14px;
      }
    }
    
    .status-radio {
      margin-right: 20px;
      
      .status-option {
        display: flex;
        align-items: center;
        
        .status-dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          margin-right: 5px;
          
          &.active-dot {
            background-color: #67c23a;
          }
          
          &.inactive-dot {
            background-color: #f56c6c;
          }
        }
      }
    }
    
    .el-dialog__footer {
      border-top: 1px solid #e4e7ed;
      padding: 15px 20px;
    }
  }
}
</style> 