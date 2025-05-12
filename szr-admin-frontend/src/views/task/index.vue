<template>
  <div class="task-manager">
    <div class="task-container">
      <!-- 顶部标题区 -->
      <div class="header-banner">
        <div class="left-content">
          <i class="el-icon-video-play icon-large"></i>
          <div class="title-group">
            <h2 class="main-title">视频合成任务</h2>
            <p class="sub-title">管理系统中的视频合成任务</p>
          </div>
        </div>
      </div>

      <!-- 搜索区域 -->
      <div class="search-section">
        <div class="search-header">
          <i class="el-icon-search"></i>
          <span>搜索筛选</span>
        </div>
        <el-form :model="queryParams" ref="queryForm" :inline="true">
          <el-form-item>
            <el-input 
              v-model="queryParams.name" 
              placeholder="搜索任务名称" 
              clearable 
              prefix-icon="el-icon-search"
              @keyup.enter="handleQuery"
              class="keyword-input"
            />
          </el-form-item>
          <el-form-item>
            <el-select 
              v-model="queryParams.voiceId" 
              placeholder="音色筛选" 
              clearable
              class="voice-select"
            >
              <el-option
                v-for="item in voiceOptions"
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
              <el-option label="待处理" value="1">
                <div class="status-option">
                  <span class="status-dot status-pending"></span>
                  <span>待处理</span>
                </div>
              </el-option>
              <el-option label="处理中" value="2">
                <div class="status-option">
                  <span class="status-dot status-processing"></span>
                  <span>处理中</span>
                </div>
              </el-option>
              <el-option label="已完成" value="3">
                <div class="status-option">
                  <span class="status-dot status-active"></span>
                  <span>已完成</span>
                </div>
              </el-option>
              <el-option label="失败" value="4">
                <div class="status-option">
                  <span class="status-dot status-inactive"></span>
                  <span>失败</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-date-picker
              v-model="queryParams.createTime"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              class="date-picker"
            />
          </el-form-item>
          <el-form-item class="button-group">
            <el-button type="primary" @click="handleQuery" class="query-btn" size="small">
              <i class="el-icon-search"></i> 搜索
            </el-button>
            <el-button @click="resetQuery" class="reset-btn" size="small">
              <i class="el-icon-refresh"></i> 重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 任务卡片区域 -->
      <div class="task-card-container">
        <div class="task-header">
          <div class="task-title">
            <i class="el-icon-notebook-1"></i> 
            <span>任务列表</span>
            <span class="record-count" v-if="total > 0">共 {{ total }} 条</span>
          </div>
          <div class="task-actions">
            <el-button 
              type="primary" 
              icon="el-icon-refresh" 
              @click="getList"
              class="refresh-btn"
              size="small"
              plain
            >
              刷新
            </el-button>
          </div>
        </div>

        <!-- 卡片列表 -->
        <div v-loading="loading" class="task-cards">
          <transition-group name="task-list" tag="div" class="card-list-container">
            <div v-for="item in taskList" :key="item.id" class="task-card" :class="getCardClass(item)">
              <div class="card-header">
                <div class="task-name">{{ item.name }}</div>
                <div class="task-status">
                  <span :class="['status-badge', `status-${item.status}`]">
                    {{ getStatusText(item.status) }}
                  </span>
                </div>
              </div>
              
              <div class="card-body">
                <div class="task-content">
                  <i class="el-icon-document-copy"></i>
                  <el-tooltip effect="dark" :content="item.text || '暂无内容'" placement="top">
                    <span class="text-content ellipsis">{{ item.text || '暂无内容' }}</span>
                  </el-tooltip>
                </div>
                
                <div class="task-details">
                  <div class="detail-item">
                    <i class="el-icon-microphone"></i>
                    <span>音色ID: </span>
                    <el-tag size="small" type="info" effect="plain" class="voice-tag">{{ item.voiceId }}</el-tag>
                  </div>
                  
                  <div class="detail-item">
                    <i class="el-icon-time"></i>
                    <span>{{ formatDateTime(item.createTime) }}</span>
                  </div>
                </div>
                
                <div class="video-preview-container" v-if="item.result_url || item.resultUrl || item.videoUrl">
                  <div class="preview-title">视频预览</div>
                  <div class="video-preview-wrapper" @click="playVideo(item)">
                    <el-image 
                      :src="getVideoCover(item.result_url || item.resultUrl || item.videoUrl)" 
                      fit="cover"
                      class="video-preview-image"
                      :preview-src-list="[]"
                    >
                      <template #error>
                        <div class="image-error">
                          <i class="el-icon-video-camera"></i>
                        </div>
                      </template>
                    </el-image>
                    <div class="play-icon-overlay">
                      <i class="el-icon-video-play"></i>
                    </div>
                  </div>
                </div>
                <div class="no-video-preview" v-else>
                  <i class="el-icon-picture"></i>
                  <span>暂无视频</span>
                </div>
              </div>
              
              <div class="card-footer">
                <el-button 
                  type="primary" 
                  icon="el-icon-edit" 
                  size="small" 
                  @click="handleChangeStatus(item)"
                  class="action-button"
                >
                  修改状态
                </el-button>
                <el-button 
                  type="danger" 
                  icon="el-icon-delete" 
                  size="small" 
                  @click="handleDelete(item)"
                  class="action-button"
                >
                  删除
                </el-button>
                <el-button 
                  type="warning" 
                  icon="el-icon-close" 
                  size="small" 
                  v-if="['1', '2'].includes(item.status)"
                  @click="handleCancel(item)"
                  class="action-button"
                >
                  取消
                </el-button>
              </div>
            </div>
          </transition-group>
          
          <!-- 无数据展示 -->
          <div v-if="taskList.length === 0 && !loading" class="no-data">
            <i class="el-icon-document"></i>
            <p>暂无任务数据</p>
          </div>
        </div>

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

    <!-- 视频预览对话框 -->
    <el-dialog
      title="视频预览"
      v-model="videoPreviewVisible"
      :width="dialogWidth"
      destroy-on-close
      top="5vh"
    >
      <template #default>
        <div class="video-preview-box">
          <div v-if="previewVideoUrl" class="video-box">
            <video 
              ref="videoPlayer"
              :src="previewVideoUrl" 
              controls 
              preload="auto"
              style="width: 100%;"
            ></video>
          </div>
          <div v-else class="empty-video">
            <i class="el-icon-video-camera"></i>
            <p>暂无可预览的视频</p>
          </div>
          
          <div v-if="currentTask.text" class="video-text">
            <h4><i class="el-icon-document-copy"></i> 视频文本内容</h4>
            <p>{{ currentTask.text }}</p>
          </div>
        </div>
      </template>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeVideoPreview">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 修改状态对话框 -->
    <el-dialog
      title="修改任务状态"
      v-model="changeStatusDialogVisible"
      width="500px"
      destroy-on-close
      custom-class="status-change-dialog"
    >
      <div class="status-change-content">
        <p class="task-name">任务：{{ currentTask.name }}</p>
        <el-form :model="statusForm" label-width="100px">
          <el-form-item label="当前状态">
            <div class="current-status">
              <span :class="['status-dot', getStatusBadgeClass(currentTask.status)]"></span>
              <span :class="['status-text', `status-${currentTask.status}`]">
                {{ getStatusNumber(currentTask.status) }}-{{ getStatusText(currentTask.status) }}
              </span>
            </div>
          </el-form-item>
          <el-form-item label="新状态">
            <el-select v-model="statusForm.status" placeholder="请选择新状态" class="status-select">
              <el-option label="1-待处理" value="1">
                <div class="status-option">
                  <span class="status-dot status-pending"></span>
                  <span>1-待处理</span>
                </div>
              </el-option>
              <el-option label="2-处理中" value="2">
                <div class="status-option">
                  <span class="status-dot status-processing"></span>
                  <span>2-处理中</span>
                </div>
              </el-option>
              <el-option label="3-已完成" value="3">
                <div class="status-option">
                  <span class="status-dot status-active"></span>
                  <span>3-已完成</span>
                </div>
              </el-option>
              <el-option label="4-失败" value="4">
                <div class="status-option">
                  <span class="status-dot status-inactive"></span>
                  <span>4-失败</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="small" @click="changeStatusDialogVisible = false">取 消</el-button>
          <el-button type="primary" size="small" @click="submitStatusChange">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTaskList, getTaskDetail, addTask, cancelTask, updateTaskStatus } from '@/api/task'
import { getVoiceList } from '@/api/voice'

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  name: '',
  voiceId: '',
  status: '',
  createTime: []
})

// 表格数据
const taskList = ref([])
const total = ref(0)
const loading = ref(false)

// 音色选项
const voiceOptions = ref([])

// 表单参数
const dialogVisible = ref(false)
const form = reactive({
  name: '',
  userId: 1, // 默认用户ID，可根据实际情况调整
  voiceId: '',
  text: ''
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入任务名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  voiceId: [
    { required: true, message: '请选择音色', trigger: 'change' }
  ],
  text: [
    { required: true, message: '请输入内容', trigger: 'blur' },
    { min: 1, max: 1000, message: '长度在 1 到 1000 个字符', trigger: 'blur' }
  ]
}

// 查看任务相关
const viewDialogVisible = ref(false)
const currentTask = ref({})

// 引用
const queryForm = ref(null)
const formRef = ref(null)
const videoPlayer = ref(null)

// 默认视频封面图
const videoPlaceholderImage = 'https://img.alicdn.com/imgextra/i4/O1CN01EX9LB51UyKJKcwI3p_!!6000000002594-2-tps-1024-1024.png'

// 视频预览相关
const videoPreviewVisible = ref(false)
const previewVideoUrl = ref('')
const dialogWidth = ref('70%')

// 初始化
onMounted(() => {
  getList()
  getVoices()
})

// 获取任务列表
const getList = async () => {
  try {
    loading.value = true
    const res = await getTaskList(queryParams)
    taskList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取任务列表失败：', error)
    ElMessage.error('获取任务列表失败')
  } finally {
    loading.value = false
  }
}

// 获取音色列表
const getVoices = async () => {
  try {
    const res = await getVoiceList({ status: '1' })
    voiceOptions.value = res.data.records
  } catch (error) {
    console.error('获取音色列表失败：', error)
    ElMessage.error('获取音色列表失败')
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    '1': 'info',    // 待处理
    '2': 'warning', // 处理中
    '3': 'success', // 已完成
    '4': 'danger'   // 失败
  }
  return statusMap[status] || 'info'
}

// 获取状态数字
const getStatusNumber = (status) => {
  const statusMap = {
    '1': '1',
    '2': '2',
    '3': '3',
    '4': '4'
  }
  return statusMap[status] || '0'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    '1': '待处理',
    '2': '处理中',
    '3': '已完成',
    '4': '失败'
  }
  return statusMap[status] || '未知'
}

// 获取状态徽标类名
const getStatusBadgeClass = (status) => {
  switch (status) {
    case '1':
      return 'status-pending'
    case '2':
      return 'status-processing'
    case '3':
      return 'status-active'
    case '4':
      return 'status-inactive'
    default:
      return 'status-pending'
  }
}

// 获取进度状态
const getProgressStatus = (status) => {
  if (status === '4') return 'exception'
  if (status === '3') return 'success'
  return ''
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '-'
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

// 新建任务
const handleAdd = () => {
  form.name = ''
  form.userId = 1
  form.voiceId = ''
  form.text = ''
  dialogVisible.value = true
}

// 查看任务
const handleView = async (row) => {
  try {
    const res = await getTaskDetail(row.id)
    currentTask.value = res.data
    viewDialogVisible.value = true
  } catch (error) {
    console.error('获取任务详情失败：', error)
    ElMessage.error('获取任务详情失败')
  }
}

// 下载视频
const handleDownload = (row) => {
  const videoUrl = row.videoUrl || row.resultUrl
  if (!videoUrl) {
    ElMessage.warning('视频文件不存在')
    return
  }
  window.open(videoUrl)
  ElMessage.success('视频下载已开始')
}

// 取消任务
const handleCancel = (row) => {
  ElMessageBox.confirm('确认要取消该任务吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelTask(row.id)
      ElMessage.success('取消成功')
      getList()
    } catch (error) {
      console.error('取消失败：', error)
      ElMessage.error('取消失败')
    }
  }).catch(() => {})
}

// 提交表单
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      await addTask(form)
      ElMessage.success('任务创建成功')
      dialogVisible.value = false
      getList()
    } catch (error) {
      console.error('创建失败：', error)
      ElMessage.error('创建失败')
    }
  })
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

// 视频预览
const playVideo = (row) => {
  if (!row) return
  
  // 使用result_url字段作为视频源
  const videoUrl = row.result_url || row.resultUrl || row.videoUrl
  console.log('准备播放视频:', videoUrl)
  
  if (videoUrl) {
    currentTask.value = { ...row }
    previewVideoUrl.value = videoUrl
    videoPreviewVisible.value = true
    
    // 根据屏幕宽度设置对话框宽度
    const screenWidth = window.innerWidth
    if (screenWidth < 768) {
      dialogWidth.value = '95%'
    } else if (screenWidth < 1200) {
      dialogWidth.value = '80%'
    } else {
      dialogWidth.value = '70%'
    }
  } else {
    ElMessage.warning('视频文件不存在')
  }
}

// 关闭视频预览
const closeVideoPreview = () => {
  videoPreviewVisible.value = false
  if (videoPlayer.value) {
    videoPlayer.value.pause()
  }
  setTimeout(() => {
    previewVideoUrl.value = ''
  }, 300)
}

// 修改状态
const changeStatusDialogVisible = ref(false)
const statusForm = reactive({
  status: ''
})

const handleChangeStatus = (row) => {
  currentTask.value = { ...row }
  statusForm.status = row.status
  changeStatusDialogVisible.value = true
}

const submitStatusChange = async () => {
  try {
    // 使用updateTaskStatus API更新任务状态
    await updateTaskStatus(currentTask.value.id, statusForm.status)
    ElMessage.success('任务状态修改成功')
    changeStatusDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('修改状态失败：', error)
    ElMessage.error('修改状态失败')
  }
}

// 获取视频封面
const getVideoCover = (videoUrl) => {
  if (!videoUrl) return videoPlaceholderImage
  
  console.log('获取视频封面URL:', videoUrl)
  
  // 移除所有现有的URL参数，然后添加截图参数
  const baseUrl = videoUrl.split('?')[0]; // 获取URL的基本部分，去掉所有参数
  const coverUrl = baseUrl + '?x-oss-process=video/snapshot,t_0,f_jpg';
  
  console.log('生成的封面URL:', coverUrl)
  return coverUrl;
}

// 删除任务
const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该任务吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 这里可以调用删除API，如果没有专门的删除API，也可以复用取消API
      await cancelTask(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败：', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 获取卡片类名
const getCardClass = (item) => {
  if (item.status === '3') {
    return 'success-card';
  } else if (item.status === '4') {
    return 'error-card';
  } else if (item.status === '2') {
    return 'processing-card';
  }
  return 'pending-card';
}
</script>

<style lang="scss" scoped>
.task-manager {
  height: 100%;
  width: 100%;
  background-color: #f5f7fa;
  position: relative;
  overflow: auto;
  padding: 20px;
  box-sizing: border-box;
  
  .task-container {
    height: auto;
    max-width: 1400px;
    margin: 0 auto;
    
    // 头部横幅
    .header-banner {
      display: flex;
      justify-content: space-between;
      align-items: center;
      background: linear-gradient(120deg, #334155, #475569, #64748B);
      padding: 24px 30px;
      border-radius: 16px;
      color: white;
      margin-bottom: 24px;
      box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
      position: relative;
      overflow: hidden;
      
      &:before {
        content: '';
        position: absolute;
        top: -50%;
        right: -50%;
        width: 100%;
        height: 200%;
        background: rgba(255, 255, 255, 0.1);
        transform: rotate(30deg);
      }
      
      .left-content {
        display: flex;
        align-items: center;
        position: relative;
        z-index: 2;
        
        .icon-large {
          font-size: 40px;
          background: rgba(255, 255, 255, 0.2);
          border-radius: 50%;
          padding: 16px;
          margin-right: 20px;
          box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
          transition: all 0.3s ease;
          
          &:hover {
            transform: translateY(-5px);
            box-shadow: 0 12px 20px rgba(0, 0, 0, 0.3);
          }
        }
        
        .title-group {
          .main-title {
            font-size: 28px;
            margin: 0 0 8px 0;
            font-weight: 700;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
            letter-spacing: 1px;
          }
          
          .sub-title {
            margin: 0;
            font-size: 16px;
            opacity: 0.9;
            letter-spacing: 0.5px;
          }
        }
      }
    }
    
    // 搜索区域
    .search-section {
      background: white;
      padding: 20px;
      border-radius: 16px;
      margin-bottom: 24px;
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.06);
      transition: all 0.3s ease;
      
      &:hover {
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
      }
      
      .search-header {
        margin-bottom: 16px;
        padding-bottom: 12px;
        border-bottom: 1px solid #ebeef5;
        color: #1a237e;
        font-size: 16px;
        font-weight: 600;
        
        i {
          margin-right: 8px;
          font-size: 18px;
        }
      }
      
      .el-form {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        
        .el-form-item {
          margin-right: 15px;
          margin-bottom: 10px;
        }
        
        .keyword-input {
          width: 220px;
        }
        
        .voice-select, .status-select {
          width: 150px;
        }
        
        .date-picker {
          width: 240px;
        }
        
        .button-group {
          margin-left: auto;
          
          .query-btn, 
          .reset-btn {
            padding: 9px 20px;
            border-radius: 8px;
          }
          
          .query-btn {
            margin-right: 10px;
            background: linear-gradient(45deg, #3949ab, #5c6bc0);
            border: none;
            box-shadow: 0 4px 8px rgba(63, 81, 181, 0.3);
            transition: all 0.3s ease;
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 6px 12px rgba(63, 81, 181, 0.4);
              background: linear-gradient(45deg, #3949ab, #7986cb);
            }
            
            &:active {
              transform: translateY(0);
            }
          }
          
          .reset-btn {
            border: 1px solid #d9d9d9;
            background: #fff;
            color: #606266;
            transition: all 0.3s ease;
            
            &:hover {
              border-color: #3949ab;
              color: #3949ab;
              transform: translateY(-2px);
              box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
            }
            
            &:active {
              transform: translateY(0);
            }
          }
        }
      }
      
      .status-option {
        display: flex;
        align-items: center;
        
        .status-dot {
          display: inline-block;
          width: 8px;
          height: 8px;
          border-radius: 50%;
          margin-right: 8px;
        }
      }
    }
    
    // 任务卡片区域
    .task-card-container {
      background: white;
      border-radius: 16px;
      overflow: hidden;
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.06);
      
      .task-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px 24px;
        border-bottom: 1px solid #ebeef5;
        
        .task-title {
          font-size: 18px;
          font-weight: 600;
          display: flex;
          align-items: center;
          color: #1a237e;
          
          i {
            color: #3949ab;
            margin-right: 10px;
            font-size: 20px;
          }
          
          .record-count {
            font-size: 14px;
            color: #fff;
            margin-left: 12px;
            font-weight: normal;
            background: #3949ab;
            padding: 3px 10px;
            border-radius: 30px;
          }
        }
        
        .task-actions {
          .refresh-btn {
            border-radius: 8px;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
            transition: all 0.3s ease;
            
            &:hover {
              transform: rotate(180deg);
              color: #3949ab;
              box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            }
          }
        }
      }
      
      .task-cards {
        padding: 24px;
        min-height: 300px;
        position: relative;
        
        .card-list-container {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
          gap: 24px;
        }
        
        .task-card {
          border-radius: 12px;
          overflow: hidden;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
          transition: all 0.3s ease;
          background: white;
          border: 1px solid #ebeef5;
          display: flex;
          flex-direction: column;
          
          &:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
          }
          
          &.success-card {
            border-top: 4px solid #66bb6a;
          }
          
          &.error-card {
            border-top: 4px solid #ef5350;
          }
          
          &.processing-card {
            border-top: 4px solid #ffb74d;
          }
          
          &.pending-card {
            border-top: 4px solid #90a4ae;
          }
          
          .card-header {
            padding: 16px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid #f0f0f0;
            background: #fafafa;
            
            .task-name {
              font-weight: 600;
              color: #1a237e;
              font-size: 16px;
              width: 70%;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
            
            .task-status {
              .status-badge {
                display: inline-block;
                padding: 4px 12px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 500;
                
                &.status-1 {
                  background-color: #eceff1;
                  color: #607d8b;
                  border: 1px solid #cfd8dc;
                }
                
                &.status-2 {
                  background-color: #fff8e1;
                  color: #ff8f00;
                  border: 1px solid #ffe082;
                }
                
                &.status-3 {
                  background-color: #e8f5e9;
                  color: #43a047;
                  border: 1px solid #c8e6c9;
                }
                
                &.status-4 {
                  background-color: #ffebee;
                  color: #e53935;
                  border: 1px solid #ffcdd2;
                }
              }
            }
          }
          
          .card-body {
            padding: 16px;
            flex: 1;
            
            .task-content {
              display: flex;
              align-items: flex-start;
              margin-bottom: 16px;
              
              i {
                color: #3949ab;
                margin-right: 8px;
                font-size: 16px;
                margin-top: 2px;
              }
              
              .text-content {
                color: #37474f;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
                line-height: 1.5;
                max-height: 3em;
                width: calc(100% - 24px);
              }
            }
            
            .task-details {
              margin-bottom: 16px;
              
              .detail-item {
                display: flex;
                align-items: center;
                margin-bottom: 8px;
                color: #607d8b;
                font-size: 14px;
                
                i {
                  color: #3949ab;
                  margin-right: 8px;
                  font-size: 14px;
                }
                
                .voice-tag {
                  margin-left: 5px;
                  background-color: #e8eaf6;
                  border-radius: 4px;
                  padding: 2px 8px;
                  color: #3949ab;
                  border: 1px solid #c5cae9;
                  font-weight: 500;
                }
              }
            }
            
            .video-preview-container {
              margin-top: 16px;
              
              .preview-title {
                font-size: 14px;
                color: #1a237e;
                margin-bottom: 8px;
                font-weight: 500;
              }
              
              .video-preview-wrapper {
                width: 100%;
                height: 140px;
                border-radius: 8px;
                overflow: hidden;
                position: relative;
                cursor: pointer;
                transition: all 0.3s ease;
                
                &:hover {
                  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
                  
                  .play-icon-overlay {
                    background-color: rgba(0, 0, 0, 0.4);
                    
                    i {
                      transform: scale(1.2);
                    }
                  }
                }
                
                .video-preview-image {
                  width: 100%;
                  height: 100%;
                  object-fit: cover;
                }
                
                .play-icon-overlay {
                  position: absolute;
                  top: 0;
                  left: 0;
                  width: 100%;
                  height: 100%;
                  background-color: rgba(0, 0, 0, 0.3);
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  transition: all 0.3s ease;
                  
                  i {
                    color: #fff;
                    font-size: 40px;
                    transition: all 0.3s ease;
                    filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.5));
                  }
                }
              }
            }
            
            .no-video-preview {
              margin-top: 16px;
              padding: 24px;
              background-color: #f5f5f5;
              border-radius: 8px;
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              color: #9e9e9e;
              
              i {
                font-size: 32px;
                margin-bottom: 8px;
              }
              
              span {
                font-size: 14px;
              }
            }
          }
          
          .card-footer {
            padding: 16px;
            display: flex;
            justify-content: space-around;
            border-top: 1px solid #f0f0f0;
            background: #fafafa;
            
            .action-button {
              flex: 1;
              margin: 0 6px;
              border-radius: 8px;
              transition: all 0.3s ease;
              box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
              
              &:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
              }
              
              &:active {
                transform: translateY(0);
                box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
              }
            }
          }
        }
        
        .no-data {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          height: 300px;
          color: #9e9e9e;
          
          i {
            font-size: 48px;
            margin-bottom: 16px;
          }
          
          p {
            font-size: 16px;
          }
        }
      }
      
      .pagination-box {
        padding: 20px;
        display: flex;
        justify-content: center;
        background: white;
        border-top: 1px solid #ebeef5;
      }
    }
  }
  
  // 视频预览样式（新版本）
  :deep(.el-dialog) {
    border-radius: 8px;
    overflow: hidden;
    
    .el-dialog__header {
      background: #f0f2f5;
      margin: 0;
      padding: 15px 20px;
      border-bottom: 1px solid #e6e6e6;
      
      .el-dialog__title {
        font-weight: 600;
        color: #303133;
      }
    }
    
    .el-dialog__body {
      padding: 0;
    }
    
    .el-dialog__footer {
      border-top: 1px solid #e6e6e6;
      padding: 10px 20px;
    }
  }
  
  .video-preview-box {
    .video-box {
      width: 100%;
      background: #000;
      position: relative;
      
      video {
        display: block;
        margin: 0 auto;
        max-height: 70vh;
      }
    }
    
    .empty-video {
      height: 300px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background: #f5f5f5;
      color: #909399;
      
      i {
        font-size: 48px;
        margin-bottom: 10px;
      }
    }
    
    .video-text {
      padding: 16px 20px;
      background: #fff;
      
      h4 {
        margin: 0 0 10px 0;
        font-size: 16px;
        color: #303133;
        display: flex;
        align-items: center;
        
        i {
          margin-right: 6px;
          color: #409eff;
        }
      }
      
      p {
        margin: 0;
        padding: 10px;
        background: #f5f7fa;
        border-radius: 4px;
        color: #606266;
        font-size: 14px;
        line-height: 1.6;
        max-height: 150px;
        overflow-y: auto;
      }
    }
  }

  .ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    display: inline-block;
    max-width: 100%;
    vertical-align: bottom;
  }
  
  // 过渡动画
  .task-list-enter-active, .task-list-leave-active {
    transition: all 0.5s ease;
  }
  .task-list-enter-from, .task-list-leave-to {
    opacity: 0;
    transform: translateY(30px);
  }
  .task-list-move {
    transition: transform 0.5s ease;
  }

  @keyframes shine {
    0% {
      left: -100%;
    }
    100% {
      left: 100%;
    }
  }
}
</style> 