<template>
  <div class="app-container">
    <!-- 顶部标题区 -->
    <div class="header-banner">
      <div class="left-content">
        <div class="icon-glow"><el-icon><Picture /></el-icon></div>
        <div class="title-group">
          <h2 class="main-title">形象管理中心</h2>
          <p class="sub-title">高级形象管理与控制系统</p>
        </div>
      </div>
      <el-button type="primary" @click="handleAdd" class="add-button">
        <el-icon><Plus /></el-icon> 新增形象
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-container" shadow="hover">
      <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="70px">
        <el-form-item label="名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入形象名称"
            clearable
            :prefix-icon="Search"
            @keyup.enter="handleQuery"
            class="keyword-input"
          />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="queryParams.categoryId" placeholder="请选择分类" clearable class="status-select">
            <el-option
              v-for="item in categoryOptions"
              :key="item?.id || ''"
              :label="item?.name || ''"
              :value="item?.id || ''"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="status-select">
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item class="button-group">
          <el-button type="primary" @click="handleQuery" class="query-btn">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="resetQuery" class="reset-btn">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-container" shadow="hover">
      <template #header>
        <div class="table-header">
          <h3 class="table-title">
            <el-icon><Picture /></el-icon> 形象数据
            <span class="record-count" v-if="total > 0">{{ total }} 条记录</span>
          </h3>
          <el-button 
            type="text" 
            @click="getList"
            class="refresh-btn"
          >
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
        </div>
      </template>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="templateList"
        border
        stripe
        highlight-current-row
        style="width: 100%;"
        row-key="id"
        :header-cell-style="{ background: '#f8fafc', color: '#334155', borderColor: '#e2e8f0' }"
        :cell-style="{ background: '#ffffff', color: '#334155', borderColor: '#e2e8f0' }"
        class="cyber-table"
      >
        <el-table-column type="index" label="#" width="60" align="center" />
        
        <el-table-column prop="name" label="形象名称" show-overflow-tooltip align="center" min-width="120">
          <template #default="scope">
            <div class="name-cell">
              <span class="name-text">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="分类" width="120" align="center">
          <template #default="scope">
            <el-tag type="info" class="category-tag">
              {{ getCategoryName(scope.row.categoryId) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip align="center" />
        
        <el-table-column label="预览图" width="140" align="center">
          <template #default="scope">
            <div class="preview-wrapper" @click="handlePreview(scope.row)">
              <el-image
                class="preview-image"
                :src="getImageUrl(scope.row.coverUrl)"
                fit="cover"
                :preview-src-list="[getImageUrl(scope.row.coverUrl)]"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="preview-overlay">
                <el-icon class="preview-icon"><VideoPlay /></el-icon>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="120" align="center">
          <template #default="scope">
            <div class="status-cell">
              <div :class="['status-badge', scope.row.status === '1' ? 'status-active' : 'status-banned']">
                {{ scope.row.status === '1' ? '启用' : '禁用' }}
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="创建时间" min-width="160" align="center">
          <template #default="scope">
            <span class="create-time">{{ formatDateTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" min-width="140" align="center">
          <template #default="scope">
            <div class="action-group">
              <el-button 
                type="primary" 
                size="small"
                @click="handleEdit(scope.row)"
                class="cyber-button edit-button"
              >
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button 
                :type="scope.row.status === '1' ? 'danger' : 'success'" 
                size="small"
                @click="handleStatusChange(scope.row)"
                class="cyber-button"
                :class="scope.row.status === '1' ? 'ban-button' : 'unban-button'"
              >
                <el-icon v-if="scope.row.status === '1'"><Lock /></el-icon>
                <el-icon v-else><Unlock /></el-icon>
                {{ scope.row.status === '1' ? '禁用' : '启用' }}
              </el-button>
              <el-button 
                type="danger" 
                size="small"
                @click="handleDelete(scope.row)"
                class="cyber-button ban-button"
              >
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryParams.page"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="queryParams.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          background
        />
      </div>
    </el-card>

    <!-- 视频预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="视频预览"
      width="800px"
      class="preview-dialog"
      :close-on-click-modal="false"
    >
      <div class="video-preview-container">
        <video
          v-if="currentPreview.videoUrl"
          :src="currentPreview.videoUrl"
          controls
          class="preview-video"
        ></video>
        <div v-else class="no-video">
          <el-empty description="暂无视频" />
        </div>
      </div>
    </el-dialog>

    <!-- 添加或编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      append-to-body
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="100px"
      >
        <el-form-item label="形象名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入形象名称" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option
              v-for="item in categoryOptions"
              :key="item?.id || ''"
              :label="item?.name || ''"
              :value="item?.id || ''"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="形象视频" prop="videoUrl">
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
              <el-button type="primary">选择视频</el-button>
            </template>
            <template #tip>
              <div class="upload-tip">
                支持mp4、mov等格式，文件大小不超过100MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="uploading">
            {{ uploading ? '上传中...' : '确定' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, VideoPlay, Picture, Search, Refresh, Edit, Lock, Unlock, Delete } from '@element-plus/icons-vue'
import { getTemplateList, getTemplateDetail, addTemplate, updateTemplate, deleteTemplate, changeTemplateStatus } from '@/api/template'
import { getCategoryList } from '@/api/templateCategory'
import { getToken } from '@/utils/auth'

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  name: '',
  categoryId: '',
  status: ''
})

// 表格数据
const templateList = ref([])
const total = ref(0)
const loading = ref(false)

// 分类选项
const categoryOptions = ref([])

// 表单参数
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? '编辑形象' : '新增形象')
const form = reactive({
  id: undefined,
  name: '',
  categoryId: '',
  description: '',
  videoUrl: '',
  coverUrl: '',
  status: '1'
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入形象名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
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
        if (!form.id && !value && videoFileList.value.length === 0) {
          callback(new Error('请上传视频'))
        } else {
          callback()
        }
      }
    }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 引用
const queryForm = ref(null)
const formRef = ref(null)

// 预览相关
const previewDialogVisible = ref(false)
const currentPreview = ref({})

// 上传相关
const videoFileList = ref([])
const uploading = ref(false)

// 初始化
onMounted(() => {
  console.log('形象管理页面已加载，开始获取数据')
  getList()
  getCategories()
})

// 处理图片URL
const getImageUrl = (url) => {
  if (!url) return ''
  // 如果是完整的URL，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  // 如果是相对路径，添加基础URL
  return import.meta.env.VITE_APP_BASE_API + url
}

// 获取形象列表
const getList = async () => {
  console.log('开始获取形象列表')
  try {
    loading.value = true
    const res = await getTemplateList(queryParams)
    console.log('获取到形象列表数据:', res)
    if (res?.data?.records) {
      templateList.value = res.data.records.map(item => ({
        ...item,
        coverUrl: getImageUrl(item.coverUrl)
      }))
      total.value = res.data.total
    } else {
      templateList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取形象列表失败:', error)
    templateList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const getCategories = async () => {
  console.log('开始获取分类列表')
  try {
    const res = await getCategoryList()
    console.log('获取到分类列表数据:', res)
    if (res?.data?.records) {
      categoryOptions.value = res.data.records.filter(item => item && item.id)
    } else {
      categoryOptions.value = []
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    categoryOptions.value = []
  }
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

// 新增形象
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑形象
const handleEdit = async (row) => {
  if (!row || !row.id) {
    ElMessage.error('无效的形象数据')
    return
  }
  
  resetForm()
  try {
    const res = await getTemplateDetail(row.id)
    if (res && res.data) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    } else {
      ElMessage.error('获取形象详情失败：数据为空')
    }
  } catch (error) {
    console.error('获取形象详情失败：', error)
    ElMessage.error('获取形象详情失败')
  }
}

// 重置表单
const resetForm = () => {
  form.id = undefined
  form.name = ''
  form.categoryId = ''
  form.description = ''
  form.videoUrl = ''
  form.coverUrl = ''
  form.status = '1'
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
    form.videoUrl = ''
    return
  }
  if (!isLt100M) {
    ElMessage.error('视频大小不能超过 100MB!')
    videoFileList.value = []
    form.videoUrl = ''
    return
  }

  // 关键：同步 fileList
  videoFileList.value = fileList

  // 设置临时URL
  form.videoUrl = URL.createObjectURL(file.raw)
}

// 处理视频移除
const handleVideoRemove = () => {
  videoFileList.value = []
  form.videoUrl = ''
  form.coverUrl = ''
}

// 处理视频URL，生成封面URL
const processVideoUrl = (url) => {
  if (!url) return ''
  // 移除URL中的查询参数
  const baseUrl = url.split('?')[0]
  // 添加视频截图参数
  return `${baseUrl}?x-oss-process=video/snapshot,t_0`
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (videoFileList.value.length === 0 && !form.id) {
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
    
    // 添加模板数据
    formData.append('template', JSON.stringify(form))

    if (form.id) {
      // 编辑
      await updateTemplate(form.id, formData)
      ElMessage.success('修改成功')
    } else {
      // 新增
      await addTemplate(formData)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('操作失败：', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    uploading.value = false
  }
}

// 修改状态
const handleStatusChange = (row) => {
  const status = row.status === '1' ? '0' : '1'
  const statusText = status === '1' ? '启用' : '禁用'
  
  ElMessageBox.confirm(`确认要${statusText}该形象吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await changeTemplateStatus(row.id, status)
      ElMessage.success(`${statusText}成功`)
      getList()
    } catch (error) {
      console.error('修改状态失败：', error)
    }
  }).catch(() => {})
}

// 删除形象
const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该形象吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteTemplate(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败：', error)
    }
  }).catch(() => {})
}

// 每页条数改变
const handleSizeChange = (size) => {
  queryParams.size = size
  getList()
}

// 当前页改变
const handleCurrentChange = (page) => {
  queryParams.page = page
  getList()
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  const category = categoryOptions.value.find(item => item.id === categoryId)
  return category ? category.name : '未分类'
}

// 处理预览
const handlePreview = (row) => {
  currentPreview.value = row
  previewDialogVisible.value = true
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 16px;
  background-color: #f1f5f9;
  min-height: auto;
  height: auto;
  overflow: visible;
}

.header-banner {
  background: linear-gradient(135deg, #334155, #475569);
  border-radius: 8px;
  padding: 12px 20px;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  .left-content {
    display: flex;
    align-items: center;
    z-index: 1;
  }

  .icon-glow {
    background: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
    box-shadow: 0 0 15px rgba(255, 255, 255, 0.5);
    
    .el-icon {
      font-size: 24px;
      color: white;
    }
  }

  .title-group {
    .main-title {
      font-size: 20px;
      font-weight: 600;
      margin: 0;
      display: flex;
      align-items: center;
    }

    .title-accent {
      background: linear-gradient(45deg, #ffd700, #ff8c00);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      font-weight: 700;
      margin-left: 4px;
    }

    .sub-title {
      font-size: 12px;
      margin: 2px 0 0;
      opacity: 0.8;
    }
  }

  .add-button {
    background: rgba(255, 255, 255, 0.2);
    border: none;
    color: white;
    font-weight: 600;
    transition: all 0.3s;
    z-index: 1;

    &:hover {
      background: rgba(255, 255, 255, 0.3);
      transform: translateY(-2px);
    }
  }

  &::before {
    content: '';
    position: absolute;
    width: 150px;
    height: 150px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    top: -75px;
    right: -75px;
  }

  &::after {
    content: '';
    position: absolute;
    width: 100px;
    height: 100px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    bottom: -50px;
    left: 10%;
  }
}

.search-container {
  margin-bottom: 12px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  :deep(.el-card__body) {
    padding: 10px 16px;
  }

  .el-form {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;
  }

  .el-form-item {
    margin-bottom: 0;
    margin-right: 0;
  }

  .keyword-input {
    width: 220px;
  }

  .status-select {
    width: 120px;
  }

  .button-group {
    .el-button {
      margin-left: 0;
      display: flex;
      align-items: center;
      
      .el-icon {
        margin-right: 4px;
      }
    }

    .query-btn {
      background: linear-gradient(135deg, #0284c7, #0ea5e9);
      border: none;
      box-shadow: 0 2px 5px rgba(14, 165, 233, 0.2);
      transition: all 0.3s;

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 8px rgba(14, 165, 233, 0.3);
      }
    }

    .reset-btn {
      border: 1px solid #cbd5e1;
      color: #64748b;
      background: white;
      transition: all 0.3s;

      &:hover {
        color: #0ea5e9;
        border-color: #0ea5e9;
      }
    }
  }
}

.table-container {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  display: flex;
  flex-direction: column;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
  background-color: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  
  .table-title {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    display: flex;
    align-items: center;
    color: #334155;
    
    .el-icon {
      color: #0ea5e9;
      margin-right: 8px;
      font-size: 18px;
    }
    
    .record-count {
      font-size: 13px;
      color: #64748b;
      margin-left: 10px;
      font-weight: 500;
      background: rgba(14, 165, 233, 0.1);
      padding: 4px 12px;
      border-radius: 12px;
      border: 1px solid rgba(14, 165, 233, 0.3);
    }
  }
  
  .refresh-btn {
    border-radius: 8px;
    padding: 8px 16px;
    transition: all 0.3s;
    color: #64748b;
    display: flex;
    align-items: center;
    
    .el-icon {
      margin-right: 4px;
    }
    
    &:hover {
      color: #0ea5e9;
      background-color: rgba(14, 165, 233, 0.1);
    }
  }
}

.cyber-table {
  margin-top: 0;
  border-color: #e2e8f0;
  width: 100% !important;
  
  :deep(.el-table__inner-wrapper),
  :deep(.el-scrollbar__wrap),
  :deep(.el-scrollbar__view) {
    width: 100% !important;
  }
  
  :deep(.el-table__header),
  :deep(.el-table__body) {
    width: 100% !important;
  }
  
  :deep(.el-table-column--selection .cell),
  :deep(.el-table-column) {
    padding: 0 5px;
  }
  
  :deep(.el-table__row) {
    background-color: #ffffff !important;
    
    &:hover td {
      background-color: #f0f9ff !important;
    }
    
    &.current-row td {
      background-color: rgba(14, 165, 233, 0.1) !important;
      border-right-color: #e2e8f0;
    }
    
    &:nth-child(even) {
      background-color: #f8fafc !important;
    }
  }
  
  :deep(.el-table__header-wrapper) th {
    background-color: #f8fafc !important;
    color: #334155 !important;
    border-color: #e2e8f0 !important;
  }
  
  :deep(.el-table__cell) {
    background-color: #ffffff;
    color: #334155;
    border-color: #e2e8f0;
  }
  
  .name-cell {
    display: flex;
    align-items: center;
    justify-content: center;
    
    .name-text {
      font-weight: 600;
      color: #334155;
      position: relative;
      
      &::after {
        content: '';
        position: absolute;
        width: 0;
        height: 1px;
        bottom: -2px;
        left: 50%;
        background: linear-gradient(90deg, transparent, #0ea5e9, transparent);
        transform: translateX(-50%);
        transition: width 0.3s;
      }
      
      &:hover::after {
        width: 100%;
      }
    }
  }
  
  .status-cell {
    display: flex;
    align-items: center;
    justify-content: center;
    
    .status-badge {
      padding: 6px 20px;
      border-radius: 8px;
      font-weight: 600;
      position: relative;
      display: inline-block;
      min-width: 110px;
      text-align: center;
      height: 32px;
      line-height: 20px;
      overflow: visible;
      font-size: 15px;
      
      &.status-active {
        background-color: #10b981;
        color: white;
        box-shadow: 0 0 8px rgba(16, 185, 129, 0.3);
      }
      
      &.status-banned {
        background-color: #ef4444;
        color: white;
        box-shadow: 0 0 8px rgba(239, 68, 68, 0.3);
      }
    }
  }
  
  .create-time {
    font-size: 13px;
    color: #334155;
    font-family: 'Courier New', monospace;
    background-color: rgba(14, 165, 233, 0.08);
    padding: 4px 8px;
    border-radius: 4px;
    border-left: 2px solid #0ea5e9;
    display: inline-block;
    white-space: nowrap;
    margin: 0;
    max-width: 160px;
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 1.4;
  }
}

.pagination-container {
  padding: 16px 24px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-top: 1px solid #e2e8f0;
  background-color: #f8fafc;
  
  :deep(.el-pagination) {
    display: flex;
    align-items: center;
    justify-content: center;
    
    .el-pagination__total {
      margin-right: 16px;
      color: #64748b;
    }
    
    .el-pagination__sizes {
      margin-right: 16px;
      
      .el-select .el-input {
        width: 110px;
        
        .el-input__wrapper {
          background-color: #ffffff;
          box-shadow: none;
          border: 1px solid #e2e8f0;
          
          &:hover {
            border-color: #0ea5e9;
          }
        }
      }
    }
    
    .btn-prev,
    .btn-next {
      min-width: 32px;
      height: 32px;
      background-color: #ffffff;
      border: 1px solid #e2e8f0;
      color: #64748b;
      font-weight: 500;
      transition: all 0.3s;
      
      &:hover:not(:disabled) {
        color: #0ea5e9;
        border-color: #0ea5e9;
        background-color: rgba(14, 165, 233, 0.05);
      }
    }
    
    .el-pager {
      display: flex;
      align-items: center;
      margin: 0 8px;
      
      li {
        min-width: 32px;
        height: 32px;
        line-height: 32px;
        background-color: #ffffff;
        border: 1px solid #e2e8f0;
        color: #64748b;
        font-weight: 500;
        transition: all 0.3s;
        margin: 0 2px;
        
        &:hover {
          color: #0ea5e9;
          border-color: #0ea5e9;
          background-color: rgba(14, 165, 233, 0.05);
        }
        
        &.is-active {
          background: linear-gradient(135deg, #0284c7, #0ea5e9);
          color: white;
          border-color: #0284c7;
          font-weight: 600;
          box-shadow: 0 2px 8px rgba(14, 165, 233, 0.3);
        }
      }
    }
    
    .el-pagination__jump {
      margin-left: 16px;
      color: #64748b;
      
      .el-input {
        width: 70px;
        margin: 0 8px;
        
        .el-input__wrapper {
          background-color: #ffffff;
          box-shadow: none;
          border: 1px solid #e2e8f0;
          
          &:hover {
            border-color: #0ea5e9;
          }
        }
      }
    }
  }
}

.action-group {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 2px;
  width: 100%;
  
  .cyber-button {
    position: relative;
    border: none;
    border-radius: 3px;
    font-weight: 600;
    padding: 1px 3px;
    margin: 1px;
    overflow: hidden;
    transition: all 0.3s;
    font-size: 10px;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.5), transparent);
      transition: 0.5s;
    }
    
    &:hover {
      transform: translateY(-1px);
      
      &::before {
        left: 100%;
      }
    }
    
    &.edit-button {
      background: linear-gradient(135deg, #0284c7, #0ea5e9);
      color: white;
      box-shadow: 0 0 6px rgba(14, 165, 233, 0.2);
      min-width: 28px;
      
      &:hover {
        box-shadow: 0 0 12px rgba(14, 165, 233, 0.3);
      }
    }
    
    &.ban-button {
      background: linear-gradient(135deg, #dc2626, #ef4444);
      color: white;
      box-shadow: 0 0 6px rgba(239, 68, 68, 0.2);
      
      &:hover {
        box-shadow: 0 0 12px rgba(239, 68, 68, 0.3);
      }
    }
    
    &.unban-button {
      background: linear-gradient(135deg, #059669, #10b981);
      color: white;
      box-shadow: 0 0 6px rgba(16, 185, 129, 0.2);
      
      &:hover {
        box-shadow: 0 0 12px rgba(16, 185, 129, 0.3);
      }
    }
    
    .el-icon {
      margin-right: 2px;
      vertical-align: middle;
    }
  }
}

.preview-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  margin: 0 auto;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  
  &:hover {
    transform: scale(1.05);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
    
    .preview-overlay {
      opacity: 1;
    }
  }
  
  .preview-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .preview-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s;
    
    .preview-icon {
      color: white;
      font-size: 24px;
      filter: drop-shadow(0 0 3px rgba(255, 255, 255, 0.7));
    }
  }
  
  .image-error {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f1f5f9;
    color: #94a3b8;
    font-size: 24px;
  }
}

.category-tag {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  background-color: rgba(100, 116, 139, 0.1);
  color: #475569;
  border: 1px solid rgba(100, 116, 139, 0.2);
}

/* 预览对话框 */
:deep(.preview-dialog) {
  .el-dialog__header {
    background: linear-gradient(135deg, #0284c7, #0ea5e9);
    padding: 12px 20px;
    
    .el-dialog__title {
      color: white;
      font-weight: 600;
    }
    
    .el-dialog__close {
      color: white;
      
      &:hover {
        background-color: rgba(255, 255, 255, 0.1);
      }
    }
  }
  
  .el-dialog__body {
    padding: 24px;
  }
}

.video-preview-container {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 10px 0;
  
  .preview-video {
    max-width: 100%;
    max-height: 500px;
    border-radius: 8px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  }
  
  .no-video {
    width: 100%;
    min-height: 200px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

/* 表单样式 */
:deep(.el-form-item__label) {
  color: #475569;
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: none;
  border: 1px solid #cbd5e1;
  transition: all 0.3s;
  
  &:hover {
    border-color: #0ea5e9;
  }
  
  &.is-focus {
    border-color: #0ea5e9;
    box-shadow: 0 0 0 2px rgba(14, 165, 233, 0.2);
  }
}

:deep(.el-textarea__inner) {
  border-radius: 8px;
  border-color: #cbd5e1;
  
  &:hover {
    border-color: #0ea5e9;
  }
  
  &:focus {
    border-color: #0ea5e9;
    box-shadow: 0 0 0 2px rgba(14, 165, 233, 0.2);
  }
}

/* 上传组件样式 */
.video-uploader {
  :deep(.el-upload--picture-card),
  :deep(.el-upload-dragger) {
    width: 100%;
    border-radius: 8px;
    border-color: #cbd5e1;
    background-color: #f8fafc;
    transition: all 0.3s;
    
    &:hover {
      border-color: #0ea5e9;
      background-color: rgba(14, 165, 233, 0.05);
    }
  }
  
  .upload-tip {
    font-size: 12px;
    color: #64748b;
    margin-top: 8px;
  }
}

/* 按钮样式调整 */
:deep(.el-dialog__footer) {
  .el-button--primary {
    background: linear-gradient(135deg, #0284c7, #0ea5e9);
    border: none;
    
    &:hover {
      box-shadow: 0 4px 12px rgba(14, 165, 233, 0.3);
      transform: translateY(-1px);
    }
  }
}

@keyframes shine {
  0% { left: -100%; }
  20% { left: 100%; }
  100% { left: 100%; }
}
</style> 