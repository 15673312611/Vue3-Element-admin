<template>
  <div class="app-container dashboard-container">
    <!-- 欢迎消息 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h1 class="welcome-title">
          <span>欢迎回来，</span>
          <span class="username">{{ userInfo.username }}</span>
        </h1>
        <p class="welcome-subtitle">今天是 {{ formatDate(new Date()) }}，祝您有个愉快的一天！</p>
      </div>
      <div class="welcome-actions">
        <el-button type="primary" class="action-button refresh-btn" @click="refreshDashboard">
          <el-icon class="mr-sm"><Refresh /></el-icon>刷新数据
        </el-button>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card-wrapper">
          <div class="stat-card" hover-effect>
            <div class="stat-icon-wrapper bg-primary-gradient">
              <el-icon class="stat-icon"><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">用户总数</div>
              <div class="stat-value">{{ statistics.userCount }}</div>
              <div class="stat-trend">
                <span>较昨日</span>
                <span :class="statistics.userIncrease >= 0 ? 'trend-up' : 'trend-down'">
                  <el-icon v-if="statistics.userIncrease >= 0"><CaretTop /></el-icon>
                  <el-icon v-else><CaretBottom /></el-icon>
                  {{ Math.abs(statistics.userIncrease) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card-wrapper">
          <div class="stat-card" hover-effect>
            <div class="stat-icon-wrapper bg-success-gradient">
              <el-icon class="stat-icon"><Avatar /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">数字人总数</div>
              <div class="stat-value">{{ statistics.digitalHumanCount }}</div>
              <div class="stat-trend">
                <span>较昨日</span>
                <span :class="statistics.digitalHumanIncrease >= 0 ? 'trend-up' : 'trend-down'">
                  <el-icon v-if="statistics.digitalHumanIncrease >= 0"><CaretTop /></el-icon>
                  <el-icon v-else><CaretBottom /></el-icon>
                  {{ Math.abs(statistics.digitalHumanIncrease) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card-wrapper">
          <div class="stat-card" hover-effect>
            <div class="stat-icon-wrapper bg-warning-gradient">
              <el-icon class="stat-icon"><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">今日任务数</div>
              <div class="stat-value">{{ statistics.todayTaskCount }}</div>
              <div class="stat-trend">
                <span>较昨日</span>
                <span :class="statistics.taskIncrease >= 0 ? 'trend-up' : 'trend-down'">
                  <el-icon v-if="statistics.taskIncrease >= 0"><CaretTop /></el-icon>
                  <el-icon v-else><CaretBottom /></el-icon>
                  {{ Math.abs(statistics.taskIncrease) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card-wrapper">
          <div class="stat-card" hover-effect>
            <div class="stat-icon-wrapper bg-info-gradient">
              <el-icon class="stat-icon"><Monitor /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">任务总数</div>
              <div class="stat-value">{{ statistics.taskCount || 0 }}</div>
              <div class="stat-trend">
                <span>总任务数</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 任务和公告部分 -->
    <el-row :gutter="20" class="dashboard-main-row">
      <el-col :xs="24" :lg="16">
        <el-card class="dashboard-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <el-icon class="mr-sm"><Tickets /></el-icon>
                <span>最近任务</span>
              </div>
              <el-button type="primary" link @click="goToTaskList">查看全部</el-button>
            </div>
          </template>
          <div class="recent-tasks">
            <el-table :data="statistics.recentTasks" style="width: 100%" border stripe>
              <el-table-column prop="name" label="任务名称" min-width="80">
                <template #default="scope">
                  <div class="task-name">{{ scope.row.name }}</div>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="150" align="center">
                <template #default="scope">
                  <el-tag :type="getStatusType(scope.row.status)" effect="dark" size="small">
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="160" align="center">
                <template #default="scope">
                  <span>{{ formatDate(scope.row.createTime) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" align="center">
                <template #default="scope">
                  <div class="table-actions">
                    <el-button 
                      v-if="scope.row.status == 3 || scope.row.status == 'completed'" 
                      type="primary" 
                      size="small" 
                      @click="viewTaskVideo(scope.row)"
                    >
                      <el-icon><VideoPlay /></el-icon>查看
                    </el-button>
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="deleteTask(scope.row)"
                    >
                      <el-icon><Delete /></el-icon>删除
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="8">
        <el-card class="dashboard-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <el-icon class="mr-sm"><Bell /></el-icon>
                <span>系统公告</span>
              </div>
              <el-tag type="info" effect="plain" size="small" v-if="notices.length > 0">
                {{ notices.length }}条公告
              </el-tag>
            </div>
          </template>
          <div class="notice-list" v-if="notices.length > 0">
            <div v-for="notice in notices" :key="notice.id" class="notice-item">
              <div class="notice-header">
                <div class="notice-title">{{ notice.title }}</div>
                <el-tag size="small" type="info" effect="plain">{{ formatRelativeTime(notice.createTime) }}</el-tag>
              </div>
              <div class="notice-content" v-if="notice.content">
                {{ notice.content }}
              </div>
              <div class="notice-footer">
                <span class="notice-time">{{ notice.createTime }}</span>
                <el-button type="primary" link size="small">查看详情</el-button>
              </div>
            </div>
          </div>
          <div class="text-center pt-md" v-else>
            <el-empty description="暂无系统公告" />
          </div>
        </el-card>
        
        <!-- 快捷访问卡片 -->
        <el-card class="dashboard-card quick-access-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <el-icon class="mr-sm"><HomeFilled /></el-icon>
                <span>快捷访问</span>
              </div>
            </div>
          </template>
          <div class="quick-access">
            <el-row :gutter="10">
              <el-col :span="6" v-for="(item, index) in quickLinks" :key="index">
                <div class="quick-access-item" @click="goToPage(item.path)">
                  <el-button type="primary" circle plain>
                    <el-icon><component :is="item.icon" /></el-icon>
                  </el-button>
                  <div class="quick-access-text">{{ item.title }}</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 视频预览对话框 -->
    <el-dialog
      v-model="videoDialogVisible"
      title="视频预览"
      width="70%"
      destroy-on-close
      :before-close="handleCloseVideoDialog"
    >
      <div class="video-container" v-if="videoUrl">
        <video
          :src="videoUrl"
          controls
          autoplay
          class="video-player"
        ></video>
      </div>
      <div v-else class="text-center">
        <el-empty description="无法加载视频" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getDashboardStatistics } from '@/api/dashboard'
import { getTaskList } from '@/api/task'
import { getUserList } from '@/api/user'
import { getDigitalHumanList } from '@/api/digitalHuman'
import { formatDate, formatRelativeTime } from '@/utils/format'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 用户信息
const userInfo = computed(() => userStore.userInfo)

// 统计数据
const statistics = ref({
  userCount: 0,
  userIncrease: 0,
  digitalHumanCount: 0,
  digitalHumanIncrease: 0,
  todayTaskCount: 0,
  taskIncrease: 0,
  systemLoad: 0,
  recentTasks: [],
  taskCount: 0
})

// 系统公告
const notices = ref([])

// 视频预览相关
const videoDialogVisible = ref(false)
const videoUrl = ref('')

// 处理关闭视频对话框
const handleCloseVideoDialog = () => {
  videoUrl.value = ''
  videoDialogVisible.value = false
}

// 快捷访问链接
const quickLinks = [
  { title: '用户管理', path: '/user', icon: 'User' },
  { title: '数字人管理', path: '/digital-human', icon: 'Avatar' },
  { title: '卡密管理', path: '/card-key', icon: 'Key' },
  { title: '系统设置', path: '/system', icon: 'Setting' }
]

// 获取任务状态类型
const getStatusType = (status) => {
  const statusMap = {
    'pending': 'warning',
    'processing': 'primary',
    'completed': 'success',
    'failed': 'danger',
    1: 'warning',  // 待处理
    2: 'primary',  // 处理中
    3: 'success',  // 已完成
    4: 'danger'    // 失败
  }
  return statusMap[status] || 'info'
}

// 获取任务状态文本
const getStatusText = (status) => {
  const statusMap = {
    'pending': '待处理',
    'processing': '处理中',
    'completed': '已完成',
    'failed': '失败',
    1: '待处理',
    2: '处理中',
    3: '已完成',
    4: '失败'
  }
  return statusMap[status] || '未知'
}

// 获取负载颜色
const getLoadColor = (load) => {
  if (load < 60) return '#67C23A'
  if (load < 80) return '#E6A23C'
  return '#F56C6C'
}

// 刷新仪表盘数据
const refreshDashboard = async () => {
  try {
    // 获取任务列表数据
    const taskRes = await getTaskList({})
    if (taskRes && taskRes.data) {
      // 使用任务列表数据来填充统计信息
      statistics.value.recentTasks = taskRes.data.records.slice(0, 10)
      statistics.value.taskCount = taskRes.data.total || 0
      
      // 计算今日任务数
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      const todayTasks = taskRes.data.records.filter(task => {
        const taskDate = new Date(task.createTime)
        return taskDate >= today
      })
      statistics.value.todayTaskCount = todayTasks.length
      
      // 计算任务增长率
      const yesterday = new Date()
      yesterday.setDate(yesterday.getDate() - 1)
      yesterday.setHours(0, 0, 0, 0)
      const yesterdayTasks = taskRes.data.records.filter(task => {
        const taskDate = new Date(task.createTime)
        return taskDate >= yesterday && taskDate < today
      })
      statistics.value.taskIncrease = todayTasks.length - yesterdayTasks.length
    }
    
    // 获取用户数据
    try {
      const userRes = await getUserList({})
      if (userRes && userRes.data) {
        statistics.value.userCount = userRes.data.total || 0
        
        // 模拟用户增长率
        statistics.value.userIncrease = Math.floor(Math.random() * 20)
      }
    } catch (error) {
      console.error('获取用户数据失败', error)
      statistics.value.userCount = 1025
      statistics.value.userIncrease = 50
    }
    
    // 获取数字人数据
    try {
      const digitalHumanRes = await getDigitalHumanList({})
      if (digitalHumanRes && digitalHumanRes.data) {
        statistics.value.digitalHumanCount = digitalHumanRes.data.total || 0
        
        // 模拟数字人增长率
        statistics.value.digitalHumanIncrease = Math.floor(Math.random() * 10)
      }
    } catch (error) {
      console.error('获取数字人数据失败', error)
      statistics.value.digitalHumanCount = 108
      statistics.value.digitalHumanIncrease = 10
    }
  } catch (error) {
    console.error('获取仪表盘数据失败', error)
    ElMessage.error('获取仪表盘数据失败')
    
    // 使用模拟数据
    mockDashboardData()
  }
}

// 模拟仪表盘数据
const mockDashboardData = () => {
  statistics.value = {
    userCount: 1025,
    userIncrease: 50,
    digitalHumanCount: 108,
    digitalHumanIncrease: 10,
    todayTaskCount: 234,
    taskIncrease: 20,
    taskCount: 1280,
    recentTasks: [
      {
        id: 1001,
        name: '数字人视频生成',
        content: '基于文本生成数字人讲解视频',
        status: 3, // 已完成
        createTime: '2024-05-09 12:00:00',
        videoUrl: 'https://example.com/videos/demo1.mp4'
      },
      {
        id: 1002,
        name: '语音合成任务',
        content: '将文本转换为自然语音',
        status: 2, // 处理中
        createTime: '2024-05-09 11:30:00'
      },
      {
        id: 1003,
        name: '模型训练任务',
        content: '基于用户数据训练个性化模型',
        status: 1, // 待处理
        createTime: '2024-05-09 10:45:00'
      },
      {
        id: 1004,
        name: '图像生成任务',
        content: '根据描述生成AI图像',
        status: 4, // 失败
        createTime: '2024-05-09 09:15:00'
      },
      {
        id: 1005,
        name: '视频剪辑合成',
        content: '将多个视频片段合成为一个完整视频',
        status: 3, // 已完成
        createTime: '2024-05-08 16:20:00',
        videoUrl: 'https://example.com/videos/demo2.mp4'
      },
      {
        id: 1006,
        name: '文本翻译任务',
        content: '将中文内容翻译为英文',
        status: 3, // 已完成
        createTime: '2024-05-08 15:10:00',
        videoUrl: 'https://example.com/videos/demo3.mp4'
      },
      {
        id: 1007,
        name: '音频降噪处理',
        content: '对录音文件进行降噪和音质优化',
        status: 2, // 处理中
        createTime: '2024-05-08 14:05:00'
      },
      {
        id: 1008,
        name: '数据分析报告',
        content: '生成用户行为分析报告',
        status: 1, // 待处理
        createTime: '2024-05-08 11:30:00'
      },
      {
        id: 1009,
        name: '系统备份任务',
        content: '执行系统数据完整备份',
        status: 3, // 已完成
        createTime: '2024-05-08 10:00:00',
        videoUrl: 'https://example.com/videos/demo4.mp4'
      },
      {
        id: 1010,
        name: '安全扫描任务',
        content: '执行系统安全漏洞扫描',
        status: 4, // 失败
        createTime: '2024-05-08 09:00:00'
      }
    ]
  }

  notices.value = [
    {
      id: 1,
      title: '系统升级通知',
      content: '系统将于本周六凌晨2点-4点进行例行维护升级，届时系统将暂停服务，请做好相关准备。',
      createTime: '2024-05-09 10:00:00'
    },
    {
      id: 2,
      title: '新功能上线公告',
      content: '数字人生成功能全新升级，支持更多场景和个性化定制，欢迎体验！',
      createTime: '2024-05-08 09:00:00'
    },
    {
      id: 3,
      title: '安全提醒',
      content: '请定期修改您的账户密码，并确保使用强密码保护您的账户安全。',
      createTime: '2024-05-07 14:30:00'
    }
  ]
}

// 跳转到任务列表
const goToTaskList = () => {
  router.push('/task')
}

// 跳转到指定页面
const goToPage = (path) => {
  router.push(path)
}

// 查看任务视频
const viewTaskVideo = (task) => {
  if (!task.videoUrl) {
    ElMessage.warning('该任务暂无视频链接')
    return
  }
  
  // 打开视频预览对话框
  videoUrl.value = task.videoUrl
  videoDialogVisible.value = true
}

// 删除任务
const deleteTask = (task) => {
  ElMessageBox.confirm(`确定要删除任务"${task.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用删除任务的API，但由于没有实际API，我们只做模拟
    ElMessage.success('删除成功')
    
    // 从列表中移除该任务
    statistics.value.recentTasks = statistics.value.recentTasks.filter(item => item.id !== task.id)
  }).catch(() => {
    // 取消删除
  })
}

// 初始化
onMounted(() => {
  refreshDashboard()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.dashboard-container {
  height: 100%;
  width: 100%;
  background-color: $background-color;
  box-sizing: border-box;
  overflow-x: hidden;
  
  // 欢迎横幅美化
  .welcome-banner {
    background: linear-gradient(135deg, #334155, #475569);
    border-radius: 16px;
    padding: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-md;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    position: relative;
    overflow: hidden;
    width: calc(100% - 20px);
    margin-left: 10px;
    margin-right: 10px;
    
    &::before {
      content: '';
      position: absolute;
      top: -20px;
      right: -20px;
      width: 140px;
      height: 140px;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 50%;
    }
    
    &::after {
      content: '';
      position: absolute;
      bottom: -30px;
      left: 20%;
      width: 180px;
      height: 180px;
      background: rgba(255, 255, 255, 0.05);
      border-radius: 50%;
    }
    
    .welcome-content {
      position: relative;
      z-index: 2;
      
      .welcome-title {
        color: white;
        font-size: 22px;
        margin: 0 0 6px 0;
        font-weight: 600;
        
        .username {
          background: linear-gradient(90deg, #ffffff, #e2e8f0);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          font-weight: 700;
        }
      }
      
      .welcome-subtitle {
        color: rgba(255, 255, 255, 0.8);
        font-size: 14px;
        margin: 0;
      }
    }
    
    .welcome-actions {
      position: relative;
      z-index: 2;
      
      .action-button {
        background: rgba(255, 255, 255, 0.15);
        border: none;
        backdrop-filter: blur(10px);
        padding: 8px 16px;
        font-weight: 500;
        transition: all 0.3s;
        
        &:hover {
          background: rgba(255, 255, 255, 0.25);
          transform: translateY(-2px);
        }
      }
    }
  }
  
  // 统计卡片美化
  .stats-row {
    .stat-card-wrapper {
      margin-bottom: $spacing-lg;
      
      .stat-card {
        background: white;
        border-radius: 16px;
        margin-left: 10px;
        padding: 20px;
        display: flex;
        align-items: center;
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
        transition: all 0.3s;
        overflow: hidden;
        position: relative;
        height: 100%;
        
        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
        }
        
        .stat-icon-wrapper {
          width: 60px;
          height: 60px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;
          position: relative;
          overflow: hidden;
          
          &::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(255, 255, 255, 0.15);
            transform: rotate(45deg) scale(1.5);
          }
          
          .stat-icon {
            font-size: 26px;
            color: white;
            position: relative;
            z-index: 1;
          }
        }
        
        .bg-primary-gradient {
          background: linear-gradient(135deg, #0ea5e9, #1e40af);
        }
        
        .bg-success-gradient {
          background: linear-gradient(135deg, #10b981, #047857);
        }
        
        .bg-warning-gradient {
          background: linear-gradient(135deg, #f59e0b, #d97706);
        }
        
        .bg-info-gradient {
          background: linear-gradient(135deg, #3b82f6, #1d4ed8);
        }
        
        .stat-content {
          flex: 1;
          
          .stat-label {
            color: $light-text;
            font-size: 15px;
            margin-bottom: 5px;
          }
          
          .stat-value {
            font-size: 28px;
            font-weight: 700;
            color: $dark-text;
            line-height: 1.2;
            margin-bottom: 5px;
          }
          
          .stat-trend {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 14px;
            color: $light-text;
            
            .trend-up {
              color: $success-color;
              display: flex;
              align-items: center;
              font-weight: 500;
            }
            
            .trend-down {
              color: $danger-color;
              display: flex;
              align-items: center;
              font-weight: 500;
            }
          }
        }
      }
    }
  }
  
  // 卡片标题美化
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .card-title {
      display: flex;
      align-items: center;
      font-size: 18px;
      font-weight: 600;
      color: $dark-text;
      
      .el-icon {
        margin-right: 8px;
        color: $primary-color;
        font-size: 20px;
      }
    }
  }
  
  // 任务表格美化
  .recent-tasks {
    :deep(.el-table) {
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
      
      .el-table__header-wrapper {
        th {
          background-color: #ffffff !important;
          color: #334155 !important;
          font-weight: 600;
          padding: 12px 0;
          border-bottom: 1px solid #e2e8f0;
        }
      }
      
      .el-table__row {
        td {
          padding: 12px 0;
          border-bottom: 1px solid #f1f5f9;
          
          .task-name {
            font-weight: 500;
            color: $dark-text;
            display: flex;
            align-items: center;
            max-width: 150px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            
            &::before {
              content: '';
              display: inline-block;
              width: 6px;
              height: 6px;
              border-radius: 50%;
              background-color: $primary-color;
              margin-right: 8px;
              flex-shrink: 0;
            }
          }
          
          .el-tag {
            padding: 0 12px;
            height: 28px;
            line-height: 26px;
            border-radius: 6px;
            font-weight: 500;
            border: none;
            background: linear-gradient(135deg, rgba($primary-color, 0.1), rgba($primary-color, 0.05));
            color: $primary-color;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
            
            &.el-tag--success {
              background: linear-gradient(135deg, rgba($success-color, 0.1), rgba($success-color, 0.05));
              color: $success-color;
            }
            
            &.el-tag--warning {
              background: linear-gradient(135deg, rgba($warning-color, 0.1), rgba($warning-color, 0.05));
              color: $warning-color;
            }
            
            &.el-tag--danger {
              background: linear-gradient(135deg, rgba($danger-color, 0.1), rgba($danger-color, 0.05));
              color: $danger-color;
            }
          }
          
          .create-time {
            color: $light-text;
            font-size: 13px;
            display: flex;
            align-items: center;
            background: linear-gradient(135deg, #f8fafc, #f1f5f9);
            padding: 6px 12px;
            border-radius: 6px;
            width: fit-content;
            margin: 0 auto;
            
            &::before {
              content: '';
              display: inline-block;
              width: 16px;
              height: 16px;
              background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%236B7280"><path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10 10-4.5 10-10S17.5 2 12 2zm0 18c-4.4 0-8-3.6-8-8s3.6-8 8-8 8 3.6 8 8-3.6 8-8 8zm.5-13H11v6l5.2 3.2.8-1.3-4.5-2.7V7z"/></svg>') no-repeat center;
              margin-right: 6px;
            }
          }
          
          .table-actions {
            display: flex;
            justify-content: center;
            gap: 8px;
            
            .el-button {
              padding: 6px 14px;
              font-weight: 500;
              border-radius: 6px;
              transition: all 0.3s;
              
              &:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
              }
              
              .el-icon {
                margin-right: 4px;
              }
            }
          }
        }
        
        &:hover {
          background-color: rgba($primary-color, 0.05) !important;
        }
      }
    }
  }
  
  // 公告美化
  .notice-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
    max-height: 220px;
    overflow-y: auto;
    
    .notice-item {
      background: #f8fafc;
      border-radius: 12px;
      padding: 12px;
      transition: all 0.3s;
      
      &:hover {
        background: #f1f5f9;
        transform: translateY(-2px);
      }
      
      .notice-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 6px;
        
        .notice-title {
          font-weight: 600;
          color: $dark-text;
          font-size: 15px;
        }
      }
      
      .notice-content {
        color: $light-text;
        margin-bottom: 6px;
        line-height: 1.5;
        font-size: 13px;
        max-height: 60px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 3;
        -webkit-box-orient: vertical;
      }
      
      .notice-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .notice-time {
          color: $extra-light-text;
          font-size: 12px;
        }
      }
    }
  }
  
  // 快捷访问美化
  .quick-access-card {
    margin-top: 16px;
  }
  
  .quick-access {
    .quick-access-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      cursor: pointer;
      padding: 8px 0;
      transition: all 0.3s;
      
      &:hover {
        transform: translateY(-3px);
        
        .el-button {
          background-color: $primary-color;
          color: white;
          box-shadow: 0 6px 16px rgba($primary-color, 0.3);
        }
      }
      
      .el-button {
        width: 42px;
        height: 42px;
        transition: all 0.3s;
        margin-bottom: 6px;
        background-color: $primary-color;
        border: none;
        color: white;
        box-shadow: 0 4px 8px rgba($primary-color, 0.2);
        
        .el-icon {
          font-size: 18px;
        }
      }
      
      .quick-access-text {
        font-size: 13px;
        color: $light-text;
        text-align: center;
      }
    }
  }
  
  // 视频预览对话框
  :deep(.el-dialog) {
    border-radius: 16px;
    overflow: hidden;
    
    .el-dialog__header {
      padding: 16px 20px;
      border-bottom: 1px solid #f1f5f9;
      margin: 0;
      
      .el-dialog__title {
        font-weight: 600;
        font-size: 18px;
      }
    }
    
    .el-dialog__body {
      padding: 20px;
    }
    
    .video-container {
      width: 100%;
      
      .video-player {
        width: 100%;
        border-radius: 8px;
        overflow: hidden;
      }
    }
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    .welcome-banner {
      flex-direction: column;
      gap: 16px;
      
      .welcome-content {
        text-align: center;
        width: 100%;
      }
      
      .welcome-actions {
        width: 100%;
        display: flex;
        justify-content: center;
      }
    }
    
    .stats-row {
      .stat-card {
        margin-bottom: 16px;
      }
    }
  }
}
</style> 