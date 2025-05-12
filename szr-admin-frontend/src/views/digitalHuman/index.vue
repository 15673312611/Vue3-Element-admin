<template>
  <div class="digital-human-manager">
    <div class="digital-human-container">
      <!-- 顶部标题区 -->
      <div class="header-banner">
        <div class="left-content">
          <i class="el-icon-user icon-large"></i>
          <div class="title-group">
            <h2 class="main-title">数字人管理</h2>
            <p class="sub-title">管理系统中的数字人形象</p>
          </div>
        </div>
      </div>

      <!-- 搜索区域 -->
      <div class="search-section">
        <el-form :model="queryParams" ref="queryForm" :inline="true">
          <el-form-item>
            <el-input 
              v-model="queryParams.name" 
              placeholder="搜索数字人名称" 
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
            <i class="el-icon-notebook-1"></i> 数字人列表
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

        <!-- 表格 -->
        <el-table
          v-loading="loading"
          :data="digitalHumanList"
          border
          :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
          class="digital-human-table"
        >
          <el-table-column prop="id" label="ID" width="100" align="center" />
          
          <el-table-column prop="name" label="数字人名称" width="220">
            <template #default="scope">
              <span class="name-text">
                <template v-if="scope.row.username">
                  <span class="username-tag">{{ scope.row.username }}</span>
                </template>
                {{ scope.row.name }}
              </span>
            </template>
          </el-table-column>
          
          <el-table-column prop="description" label="描述" min-width="300">
            <template #default="scope">
              <span class="description-text">{{ scope.row.description || '暂无描述' }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="预览" width="130">
            <template #default="scope">
              <el-image
                style="width: 80px; height: 80px; cursor: pointer;"
                :src="scope.row.coverUrl"
                fit="cover"
                @click="playVideo(scope.row.videoUrl)"
              />
            </template>
          </el-table-column>
          
          <el-table-column label="状态" width="120" align="center">
            <template #default="scope">
              <div class="status-cell">
                <span 
                  :class="['status-dot', scope.row.status === '1' ? 'active-dot' : 'inactive-dot']"
                ></span>
                <span 
                  :class="['status-label', scope.row.status === '1' ? 'active-label' : 'inactive-label']"
                >
                  {{ scope.row.status === '1' ? '启用' : '禁用' }}
                </span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="createTime" label="创建时间" min-width="160">
            <template #default="scope">
              <div class="time-cell">
                <i class="el-icon-time"></i>
                <span>{{ formatDateTime(scope.row.createTime) }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="scope">
              <div class="action-group">
                <el-button 
                  :type="scope.row.status === '1' ? 'warning' : 'success'" 
                  :icon="scope.row.status === '1' ? 'el-icon-close' : 'el-icon-check'" 
                  size="mini" 
                  @click="handleStatusChange(scope.row)"
                  plain
                  class="action-button"
                >
                  {{ scope.row.status === '1' ? '禁用' : '启用' }}
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
      :title="form.id ? '编辑数字人' : '新增数字人'"
      v-model="dialogVisible"
      width="550px"
      destroy-on-close
      custom-class="digital-human-form-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="数字人名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入数字人名称" />
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        
        <el-form-item label="视频文件" prop="videoUrl">
          <el-upload
            class="video-uploader"
            :action="null"
            :auto-upload="false"
            :show-file-list="true"
            :limit="1"
            :on-change="handleVideoChange"
            :on-remove="handleVideoRemove"
            :file-list="videoFileList"
            accept="video/*"
          >
            <template #trigger>
              <el-button type="primary">
                <i class="el-icon-upload"></i>
                选择视频
              </el-button>
            </template>
            <template #tip>
              <div class="form-tips">
                <i class="el-icon-info"></i>
                <span>支持mp4、mov等视频格式，文件大小不超过100MB</span>
              </div>
            </template>
          </el-upload>
          <div v-if="form.videoUrl && !videoFileList.length" class="video-preview">
            <video :src="form.videoUrl" style="width: 200px; height: 150px" />
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
        <el-button type="primary" @click="submitForm" :loading="uploading">
          {{ uploading ? '上传中...' : '确 定' }}
        </el-button>
      </div>
    </el-dialog>

    <!-- 视频播放对话框 -->
    <el-dialog
      title="视频预览"
      v-model="videoDialogVisible"
      width="640px"
      destroy-on-close
      center
      class="video-dialog"
    >
      <div class="video-container">
        <video
          v-if="currentVideoUrl"
          :src="currentVideoUrl"
          controls
          autoplay
          class="video-player"
        ></video>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload } from '@element-plus/icons-vue'
import { getDigitalHumanList, getDigitalHumanDetail, addDigitalHuman, updateDigitalHuman, deleteDigitalHuman, changeDigitalHumanStatus } from '@/api/digitalHuman'

// 查询参数
const queryParams = ref({
  page: 1,
  size: 10,
  name: '',
  status: ''
})

// 表格数据
const digitalHumanList = ref([])
const total = ref(0)
const loading = ref(false)

// 视频播放相关
const videoDialogVisible = ref(false)
const currentVideoUrl = ref('')

// 表单参数
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? '编辑数字人' : '新增数字人')
const form = ref({
  id: '',
  name: '',
  description: '',
  videoUrl: '',
  coverUrl: '',
  status: '1'
})

// 视频上传相关
const videoFileList = ref([])
const uploading = ref(false)

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入数字人名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '长度不能超过 200 个字符', trigger: 'blur' }
  ],
  videoUrl: [
    { 
      required: true, 
      message: '请上传视频', 
      trigger: 'change',
      validator: (rule, value, callback) => {
        if (!form.value.id && !value && videoFileList.value.length === 0) {
          callback(new Error('请上传视频'))
        } else {
          callback()
        }
      }
    }
  ]
}

// 引用
const queryForm = ref(null)
const formRef = ref(null)

// 初始化
onMounted(async () => {
  try {
    loading.value = true
    await getList()
  } catch (error) {
    console.error('初始化数据失败：', error)
  } finally {
    loading.value = false
  }
})

// 获取数字人列表
const getList = async () => {
  try {
    const res = await getDigitalHumanList(queryParams.value)
    digitalHumanList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取数字人列表失败：', error)
  }
}

// 重置查询
const resetQuery = () => {
  queryParams.value = {
    page: 1,
    size: 10,
    name: '',
    status: ''
  }
  handleQuery()
}

// 查询
const handleQuery = () => {
  queryParams.value.page = 1
  getList()
}

// 处理新增
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = async (row) => {
  resetForm()
  try {
    const res = await getDigitalHumanDetail(row.id)
    if (res && res.data) {
      form.value = {
        ...res.data,
        status: res.data.status || '1'
      }
      dialogVisible.value = true
    } else {
      ElMessage.error('获取数字人详情失败：数据为空')
    }
  } catch (error) {
    console.error('获取数字人详情失败：', error)
    ElMessage.error('获取数字人详情失败')
  }
}

// 重置表单
const resetForm = () => {
  form.value = {
    id: '',
    name: '',
    description: '',
    videoUrl: '',
    coverUrl: '',
    status: '1'
  }
  videoFileList.value = []
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 处理视频选择
const handleVideoChange = (file, fileList) => {
  const isVideo = file.raw.type.startsWith('video/')
  const isLt100M = file.raw.size / 1024 / 1024 < 100

  if (!isVideo) {
    ElMessage.error('只能上传视频文件!')
    videoFileList.value = []
    form.value.videoUrl = ''
    return
  }
  if (!isLt100M) {
    ElMessage.error('视频大小不能超过 100MB!')
    videoFileList.value = []
    form.value.videoUrl = ''
    return
  }

  // 同步 fileList
  videoFileList.value = fileList

  // 设置临时URL用于预览
  form.value.videoUrl = URL.createObjectURL(file.raw)
}

// 处理视频移除
const handleVideoRemove = () => {
  videoFileList.value = []
  form.value.videoUrl = ''
  form.value.coverUrl = ''
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (videoFileList.value.length === 0 && !form.value.id) {
      ElMessage.error('请选择视频文件')
      return
    }

    uploading.value = true
    
    // 创建FormData对象
    const formData = new FormData()
    
    // 如果有视频文件，添加到FormData
    if (videoFileList.value.length > 0) {
      formData.append('file', videoFileList.value[0].raw)
    }
    
    // 添加数字人数据
    formData.append('digitalHuman', JSON.stringify(form.value))

    if (form.value.id) {
      // 编辑
      await updateDigitalHuman(form.value.id, formData)
      ElMessage.success('修改成功')
    } else {
      // 新增
      await addDigitalHuman(formData)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交表单失败：', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    uploading.value = false
  }
}

// 处理状态变更
const handleStatusChange = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认要${row.status === '1' ? '禁用' : '启用'}该数字人吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    loading.value = true
    const newStatus = row.status === '1' ? '0' : '1'
    await changeDigitalHumanStatus(row.id, newStatus)
    ElMessage.success('操作成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('状态变更失败：', error)
      ElMessage.error('操作失败')
    }
  } finally {
    loading.value = false
  }
}

// 处理删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认要删除该数字人吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    loading.value = true
    await deleteDigitalHuman(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败：', error)
      ElMessage.error('删除失败')
    }
  } finally {
    loading.value = false
  }
}

// 每页条数改变
const handleSizeChange = (size) => {
  queryParams.value.size = size
  getList()
}

// 当前页改变
const handleCurrentChange = (page) => {
  queryParams.value.page = page
  getList()
}

// 播放视频
const playVideo = (videoUrl) => {
  if (!videoUrl) {
    ElMessage.warning('视频链接不存在')
    return
  }
  currentVideoUrl.value = videoUrl
  videoDialogVisible.value = true
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}
</script>

<style lang="scss" scoped>
.digital-human-manager {
  height: 100%;
  width: 100%;
  background-color: #f0f2f5;
  position: relative;
  overflow: auto;
  padding: 20px;
  box-sizing: border-box;
  
  .digital-human-container {
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
      
      .digital-human-table {
        width: 100%;
        
        // 表格单元格样式
        .name-text {
          font-weight: 500;
          color: #303133;
          display: flex;
          align-items: center;
          flex-wrap: wrap;
          gap: 4px;
          
          .username-tag {
            display: inline-block;
            background-color: #e6f7ff;
            color: #1890ff;
            font-size: 12px;
            padding: 2px 6px;
            border-radius: 4px;
            margin-right: 6px;
            border: 1px solid #91d5ff;
            font-weight: normal;
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
  .digital-human-form-dialog {
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
    
    .video-preview {
      width: 200px;
      height: 150px;
      display: flex;
      justify-content: center;
      align-items: center;
      background-color: #f5f7fa;
      border-radius: 6px;
      overflow: hidden;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      
      video {
        max-width: 100%;
        max-height: 100%;
        object-fit: contain;
      }
    }
    
    .preview-tip {
      font-size: 12px;
      color: #909399;
      text-align: center;
      line-height: 150px;
      width: 200px;
      height: 150px;
      background-color: #f5f7fa;
      border-radius: 6px;
      border: 1px dashed #d9d9d9;
    }
    
    .el-dialog__footer {
      border-top: 1px solid #e4e7ed;
      padding: 15px 20px;
    }
  }
}

// 视频对话框样式
:deep(.video-dialog) {
  .el-dialog__header {
    text-align: center;
    padding: 15px 0;
    background: #f5f7fa;
    margin-right: 0;
    border-bottom: 1px solid #ebeef5;
  }
  
  .el-dialog__body {
    padding: 20px;
  }
  
  .video-container {
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    
    .video-player {
      width: 100%;
      max-height: 400px;
      border-radius: 4px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
  }
}
</style> 