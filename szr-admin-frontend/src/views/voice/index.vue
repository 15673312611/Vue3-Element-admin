<template>
  <div class="voice-manager">
    <div class="voice-container">
      <!-- 顶部标题区 -->
      <div class="header-banner">
        <div class="left-content">
          <i class="el-icon-headset icon-large"></i>
          <div class="title-group">
            <h2 class="main-title">音色管理</h2>
            <p class="sub-title">管理系统中的音色配置</p>
          </div>
        </div>
        <el-button type="primary" @click="handleAdd" class="add-button">
          <i class="el-icon-plus"></i> 新增音色
        </el-button>
      </div>

      <!-- 搜索区域 -->
      <div class="search-section">
        <el-form :model="queryParams" ref="queryForm" :inline="true">
          <el-form-item>
            <el-input 
              v-model="queryParams.keyword" 
              placeholder="搜索音色名称" 
              clearable 
              prefix-icon="el-icon-search"
              @keyup.enter="handleQuery"
              class="keyword-input"
            />
          </el-form-item>
          <el-form-item>
            <el-select 
              v-model="queryParams.categoryId" 
              placeholder="选择分类" 
              clearable
              class="category-select"
            >
              <el-option 
                v-for="item in categoryOptions" 
                :key="item.id" 
                :label="item.name" 
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select 
              v-model="queryParams.status" 
              placeholder="状态筛选" 
              clearable
              class="status-select"
            >
              <el-option label="正常" value="completed" />
              <el-option label="关闭" value="failure" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select 
              v-model="queryParams.isHot" 
              placeholder="热门筛选" 
              clearable
              class="hot-select"
            >
              <el-option label="热门" :value="1" />
              <el-option label="普通" :value="0" />
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
            <i class="el-icon-notebook-1"></i> 音色列表
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
          :data="voiceList"
          border
          :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
          class="voice-table"
        >
          <el-table-column prop="id" label="ID" width="100" align="center" />
          
          <el-table-column prop="name" label="音色名称" width="200">
            <template #default="scope">
              <span class="name-text">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="categoryName" label="分类" width="140">
            <template #default="scope">
              <div class="category-cell">
                <i class="el-icon-collection-tag"></i>
                <span>{{ scope.row.categoryName || '未分类' }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="性别" width="120" align="center">
            <template #default="scope">
              <el-tag 
                :type="scope.row.gender === 1 ? 'primary' : 'success'" 
                size="mini"
                effect="dark"
              >
                {{ scope.row.gender === 1 ? '男' : '女' }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="状态" width="100" align="center">
            <template #default="scope">
              <div class="status-cell">
                <span 
                  :class="['status-dot', scope.row.status === 'completed' ? 'active-dot' : 'inactive-dot']"
                ></span>
                <span 
                  :class="['status-label', scope.row.status === 'completed' ? 'active-label' : 'inactive-label']"
                >
                  {{ scope.row.status === 'completed' ? '正常' : '关闭' }}
                </span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="热门" width="100" align="center">
            <template #default="scope">
              <el-tag 
                :type="scope.row.isHot === 1 ? 'warning' : 'info'" 
                size="mini"
                effect="plain"
              >
                {{ scope.row.isHot === 1 ? '热门' : '普通' }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="createTime" label="创建时间" min-width="160">
            <template #default="scope">
              <div class="time-cell">
                <i class="el-icon-time"></i>
                <span>{{ scope.row.createTime }}</span>
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
                  @click="handleUpdate(scope.row)"
                  plain
                  class="action-button"
                >
                  编辑
                </el-button>
                <el-button 
                  :type="scope.row.status === 'completed' ? 'warning' : 'success'" 
                  :icon="scope.row.status === 'completed' ? 'el-icon-close' : 'el-icon-check'" 
                  size="mini" 
                  @click="handleStatusChange(scope.row)"
                  plain
                  class="action-button"
                >
                  {{ scope.row.status === 'completed' ? '关闭' : '启用' }}
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
            :current-page="queryParams.page"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="queryParams.size"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
          />
        </div>
      </div>
    </div>

    <!-- 对话框 -->
    <el-dialog
      :title="form.id !== undefined ? '编辑音色' : '新增音色'"
      v-model="dialogVisible"
      width="650px"
      custom-class="voice-form-dialog"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="音色名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入音色名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option
                  v-for="item in categoryOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="热门状态" prop="isHot">
              <el-radio-group v-model="form.isHot">
                <el-radio :label="1">热门</el-radio>
                <el-radio :label="0">普通</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="音色ID" prop="voiceId">
              <el-input v-model="form.voiceId" placeholder="请输入音色ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="completed">正常</el-radio>
                <el-radio label="failure">关闭</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="声音URL" prop="voiceUrl">
          <el-input v-model="form.voiceUrl" placeholder="请输入声音URL" />
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入音色描述"
          />
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getVoiceList, getVoiceDetail, addVoice, updateVoice, deleteVoice } from '@/api/voice'
import { getVoiceCategoryList } from '@/api/voiceCategory'
import request from '@/utils/request'

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  keyword: '',
  categoryId: '',
  status: '',
  isHot: ''
})

// 表格数据
const voiceList = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const categoryOptions = ref([])

// 表单参数
const form = reactive({
  name: '',
  categoryId: '',
  gender: 1,
  voiceId: '',
  voiceUrl: '',
  description: '',
  isHot: 0,
  status: 'completed'
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入音色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择所属分类', trigger: 'change' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  voiceId: [
    { required: true, message: '请输入音色ID', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 引用
const queryForm = ref(null)
const formRef = ref(null)

// 初始化
onMounted(() => {
  getList()
  getCategoryOptions()
})

// 获取音色列表
const getList = async () => {
  try {
    loading.value = true
    const res = await getVoiceList(queryParams)
    voiceList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取音色列表失败：', error)
    ElMessage.error('获取音色列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类选项
const getCategoryOptions = async () => {
  try {
    const res = await getVoiceCategoryList({ status: '1' })
    categoryOptions.value = res.data.records
  } catch (error) {
    console.error('获取分类列表失败：', error)
    ElMessage.error('获取分类列表失败')
  }
}

// 查询
const handleQuery = () => {
  queryParams.page = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryForm.value.resetFields()
  handleQuery()
}

// 表格每页数量变化
const handleSizeChange = (size) => {
  queryParams.size = size
  getList()
}

// 表格页码变化
const handleCurrentChange = (page) => {
  queryParams.page = page
  getList()
}

// 新增音色
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 修改音色
const handleUpdate = async (row) => {
  resetForm()
  
  try {
    const res = await getVoiceDetail(row.id)
    if (res && res.data) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    } else {
      ElMessage.error('获取音色详情失败')
    }
  } catch (error) {
    console.error('获取音色详情失败：', error)
    ElMessage.error('获取音色详情失败')
  }
}

// 重置表单
const resetForm = () => {
  form.id = undefined
  form.name = ''
  form.categoryId = ''
  form.gender = 1
  form.voiceId = ''
  form.voiceUrl = ''
  form.description = ''
  form.isHot = 0
  form.status = 'completed'
  
  // 如果表单引用存在则重置
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 提交表单
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      if (form.id !== undefined) {
        // 更新
        await updateVoice(form)
        ElMessage.success('修改成功')
      } else {
        // 新增
        await addVoice(form)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      getList()
    } catch (error) {
      console.error('操作失败：', error)
      ElMessage.error('操作失败')
    }
  })
}

// 状态修改
const handleStatusChange = (row) => {
  const statusText = row.status === 'completed' ? '关闭' : '启用'
  ElMessageBox.confirm(`确认要${statusText}该音色吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const newStatus = row.status === 'completed' ? 'failure' : 'completed'
      await request({
        url: `/api/voice/${row.id}/status`,
        method: 'put',
        params: { status: newStatus }
      })
      ElMessage.success(`${statusText}成功`)
      getList()
    } catch (error) {
      console.error('状态修改失败：', error)
      ElMessage.error('状态修改失败')
    }
  }).catch(() => {})
}

// 删除音色
const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该音色吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteVoice(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败：', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.voice-manager {
  height: 100%;
  width: 100%;
  background-color: #f0f2f5;
  position: relative;
  overflow: auto;
  padding: 20px;
  box-sizing: border-box;
  
  .voice-container {
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
        
        .category-select, 
        .status-select, 
        .hot-select {
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
      
      .voice-table {
        width: 100%;
        
        // 表格单元格样式
        .name-text {
          font-weight: 500;
          color: #303133;
        }
        
        .category-cell {
          display: flex;
          align-items: center;
          
          i {
            color: #1890ff;
            margin-right: 6px;
            font-size: 14px;
          }
          
          span {
            color: #1890ff;
          }
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
        
        .time-cell {
          display: flex;
          align-items: center;
          
          i {
            color: #909399;
            margin-right: 5px;
            font-size: 14px;
          }
          
          span {
            color: #606266;
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
  .voice-form-dialog {
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
    
    .el-form {
      .el-row {
        margin-bottom: 15px;
      }
    }
    
    .el-dialog__footer {
      border-top: 1px solid #e4e7ed;
      padding: 15px 20px;
    }
  }
}
</style> 