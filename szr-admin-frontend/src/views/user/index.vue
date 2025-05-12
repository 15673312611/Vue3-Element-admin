<template>
  <div class="app-container">
    <!-- 顶部标题区 -->
    <div class="header-banner">
      <div class="left-content">
        <div class="icon-glow"><el-icon><user /></el-icon></div>
        <div class="title-group">
          <h2 class="main-title">用户管理中心</h2>
          <p class="sub-title">高级用户管理与控制系统</p>
        </div>
      </div>
      <el-button type="primary" @click="handleAdd" class="add-button">
        <el-icon><plus /></el-icon> 创建新用户
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-container" shadow="hover">
      <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="70px">
        <el-form-item label="关键词" prop="keyword">
          <el-input
            v-model="queryParams.keyword"
            placeholder="请输入用户名或邮箱"
            clearable
            :prefix-icon="Search"
            @keyup.enter="handleQuery"
            class="keyword-input"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="用户状态" clearable class="status-select">
            <el-option label="正常" value="1" />
            <el-option label="封禁" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item class="button-group">
          <el-button type="primary" @click="handleQuery" class="query-btn">
            <el-icon><search /></el-icon> 搜索
          </el-button>
          <el-button @click="resetQuery" class="reset-btn">
            <el-icon><refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-container" shadow="hover">
      <template #header>
        <div class="table-header">
          <h3 class="table-title">
            <el-icon><user /></el-icon> 用户数据 
            <span class="record-count" v-if="total > 0">{{ total }} 条记录</span>
          </h3>
          <el-button 
            type="text" 
            @click="getList"
            class="refresh-btn"
          >
            <el-icon><refresh /></el-icon> 刷新
          </el-button>
        </div>
      </template>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="userList"
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
        
        <el-table-column prop="username" label="用户名" show-overflow-tooltip align="center" min-width="120">
          <template #default="scope">
            <div class="name-cell">
              <span class="name-text">{{ scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="email" label="邮箱" show-overflow-tooltip align="center" min-width="140" />
        
        <el-table-column prop="power" label="算力" align="center" width="180">
          <template #default="scope">
            <div class="power-badge">{{ scope.row.power }}</div>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="160" align="center">
          <template #default="scope">
            <div class="status-cell">
              <div :class="['status-badge', scope.row.status === '1' ? 'status-active' : 'status-banned']">
                {{ scope.row.status === '1' ? '正常' : '封禁' }}
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="注册时间" min-width="160" align="center">
          <template #default="scope">
            <span class="create-time">{{ formatDateTime(scope.row.createTime || scope.row.create_time || scope.row.createtime) }}</span>
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
                <el-icon><edit /></el-icon> 编辑
              </el-button>
              <el-button 
                type="warning" 
                size="small"
                @click="handleResetPwd(scope.row)"
                class="cyber-button reset-button"
              >
                <el-icon><key /></el-icon> 重置密码
              </el-button>
              <el-button 
                :type="scope.row.status === '1' ? 'danger' : 'success'" 
                size="small"
                @click="handleStatusChange(scope.row)"
                class="cyber-button"
                :class="scope.row.status === '1' ? 'ban-button' : 'unban-button'"
              >
                <el-icon v-if="scope.row.status === '1'"><lock /></el-icon>
                <el-icon v-else><unlock /></el-icon>
                {{ scope.row.status === '1' ? '封禁' : '解封' }}
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 新分页组件 -->
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

    <!-- 添加或编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      append-to-body
      destroy-on-close
      center
      custom-class="cyber-dialog"
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="80px"
        class="cyber-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="form.id !== undefined" class="cyber-input" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" class="cyber-input" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="form.id === undefined">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password class="cyber-input" />
        </el-form-item>
        <el-form-item label="算力" prop="power">
          <el-input-number v-model="form.power" :min="0" :step="10" style="width: 100%" class="cyber-input-number" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status" class="cyber-radio-group">
            <el-radio label="1" class="cyber-radio">
              <div class="status-option active-option">
                <span class="status-glow active-glow"></span>
                <span>正常</span>
              </div>
            </el-radio>
            <el-radio label="2" class="cyber-radio">
              <div class="status-option inactive-option">
                <span class="status-glow inactive-glow"></span>
                <span>封禁</span>
              </div>
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="submitForm" class="submit-btn">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, getUserDetail, addUser, updateUser, changeUserStatus, resetUserPassword } from '@/api/user'
import { validUsername, validEmail } from '@/utils/validate'
import { 
  DArrowLeft, 
  DArrowRight, 
  User, 
  Plus, 
  Search, 
  Refresh, 
  Edit, 
  Key, 
  Lock, 
  Unlock 
} from '@element-plus/icons-vue'

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 20, // 增加默认每页数量
  keyword: '',
  status: ''
})

// 表格数据
const userList = ref([])
const total = ref(0)
const loading = ref(false)
const hasMoreData = computed(() => {
  return total.value > queryParams.page * queryParams.size
})

// 表单参数
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? '编辑用户' : '新增用户')
const form = reactive({
  id: undefined,
  username: '',
  email: '',
  password: '',
  power: 0,
  status: '1'
})

// 表单校验规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error('用户名只能包含字母、数字、下划线和连字符，长度3-16位'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (!validEmail(value)) {
        callback(new Error('邮箱格式不正确'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  power: [
    { required: true, message: '请输入算力', trigger: 'blur' }
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
})

// 获取用户列表
const getList = async () => {
  try {
    loading.value = true
    console.log('查询参数:', queryParams)
    const res = await getUserList(queryParams)
    userList.value = res.data.records
    total.value = res.data.total
    
    // 详细调试输出
    if (res.data.records && res.data.records.length > 0) {
      console.log('==== 用户数据示例 ====')
      console.log(JSON.stringify(res.data.records[0], null, 2))
      console.log('==== 字段列表 ====')
      Object.keys(res.data.records[0]).forEach(key => {
        console.log(`${key}: ${res.data.records[0][key]}`)
      })
    }
  } catch (error) {
    console.error('获取用户列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '暂无数据';
  
  // 调试输出时间字段
  console.log('时间字段值:', dateTimeStr, typeof dateTimeStr);
  
  try {
    // 处理不同格式的时间
    let dateValue = dateTimeStr;
    if (typeof dateTimeStr === 'number') {
      // 如果是时间戳则直接使用
      dateValue = new Date(dateTimeStr);
    } else if (typeof dateTimeStr === 'string') {
      // 如果是字符串，尝试解析
      dateValue = new Date(dateTimeStr);
    }
    
    if (isNaN(dateValue.getTime())) {
      console.warn('无效的时间值:', dateTimeStr);
      return '时间格式错误';
    }
    
    const year = dateValue.getFullYear();
    const month = String(dateValue.getMonth() + 1).padStart(2, '0');
    const day = String(dateValue.getDate()).padStart(2, '0');
    const hours = String(dateValue.getHours()).padStart(2, '0');
    const minutes = String(dateValue.getMinutes()).padStart(2, '0');
    const seconds = String(dateValue.getSeconds()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  } catch (error) {
    console.error('日期格式化错误:', error);
    return '日期错误';
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
  queryParams.keyword = ''
  queryParams.status = ''
  handleQuery()
}

// 新增用户
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = async (row) => {
  resetForm()
  try {
    const res = await getUserDetail(row.id)
    Object.assign(form, res.data)
    dialogVisible.value = true
  } catch (error) {
    console.error('获取用户详情失败：', error)
  }
}

// 重置表单
const resetForm = () => {
  form.id = undefined
  form.username = ''
  form.email = ''
  form.password = ''
  form.power = 0
  form.status = '1'
  
  // 防止表单校验残留
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
        // 编辑
        await updateUser({ ...form, id: form.id })
        ElMessage.success('修改成功')
      } else {
        // 新增
        await addUser(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      getList()
    } catch (error) {
      console.error('操作失败：', error)
    }
  })
}

// 修改用户状态
const handleStatusChange = (row) => {
  const status = row.status === '1' ? '2' : '1'
  const statusText = status === '1' ? '解封' : '封禁'
  
  ElMessageBox.confirm(`确认要${statusText}该用户吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await changeUserStatus(row.id, status)
      ElMessage.success(`${statusText}成功`)
      getList()
    } catch (error) {
      console.error('修改状态失败：', error)
      ElMessage.error('操作失败：' + (error.message || '未知错误'))
    }
  }).catch(() => {})
}

// 重置密码
const handleResetPwd = (row) => {
  ElMessageBox.confirm('确认要重置该用户的密码吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await resetUserPassword(row.id)
      ElMessage.success('密码重置成功')
    } catch (error) {
      console.error('重置密码失败：', error)
    }
  }).catch(() => {})
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  queryParams.size = size
  queryParams.page = 1
  getList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  queryParams.page = page
  getList()
}

// 加载更多数据
const loadMore = () => {
  if (hasMoreData.value) {
    queryParams.page += 1
    getMoreData()
  }
}

// 获取更多数据
const getMoreData = async () => {
  try {
    loading.value = true
    const res = await getUserList(queryParams)
    // 追加数据而不是替换
    userList.value = [...userList.value, ...res.data.records]
    total.value = res.data.total
  } catch (error) {
    console.error('获取更多用户数据失败：', error)
  } finally {
    loading.value = false
  }
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
  
  .new-pagination-container {
    display: none; /* 隐藏旧的分页容器 */
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
  
  .cyber-pagination-container {
    padding: 16px 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px solid #e2e8f0;
    background-color: #f8fafc;
    flex-wrap: wrap;
    gap: 12px;
    
    @media (max-width: 768px) {
      flex-direction: column;
      align-items: flex-start;
      padding: 12px 16px;
    }
    
    .pagination-info {
      color: #64748b;
      font-size: 14px;
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 16px;
      
      @media (max-width: 576px) {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
        width: 100%;
        
        .page-info {
          width: 100%;
          text-align: center;
        }
        
        .quick-jump {
          margin: 8px auto 0;
        }
      }
      
      .page-info {
        background: rgba(14, 165, 233, 0.1);
        padding: 6px 12px;
        border-radius: 8px;
        border: 1px solid rgba(14, 165, 233, 0.2);
      }
      
      .quick-jump {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .jump-input {
          width: 70px;
          margin: 0 4px;
          
          :deep(.el-input-number__decrease),
          :deep(.el-input-number__increase) {
            background-color: #f8fafc;
            border-color: #cbd5e1;
            color: #64748b;
          }
          
          :deep(.el-input__wrapper) {
            background-color: #ffffff;
            box-shadow: none;
            border: 1px solid #cbd5e1;
            
            &:hover {
              border-color: #0ea5e9;
            }
          }
        }
      }
    }
    
    .pagination-controls {
      display: flex;
      align-items: center;
      gap: 8px;
      flex-wrap: wrap;
      justify-content: center;
      width: 100%;
      
      @media (max-width: 768px) {
        width: 100%;
        justify-content: center;
        margin-top: 8px;
      }
      
      @media (max-width: 576px) {
        flex-wrap: wrap;
        
        .page-buttons {
          width: 100%;
          justify-content: space-between;
          margin-top: 8px;
        }
        
        .page-nav-btn {
          flex: 0 0 auto;
          min-width: 80px;
        }
        
        .page-size-select {
          width: 100%;
          justify-content: center;
          margin-bottom: 8px;
        }
      }
      
      .page-buttons {
        display: flex;
        align-items: center;
        gap: 8px;
      }
      
      .page-size-select {
        display: flex;
        align-items: center;
        margin-right: 8px;
        
        span {
          color: #64748b;
          margin-right: 4px;
        }
        
        .size-select {
          width: 80px;
          
          :deep(.el-input__wrapper) {
            background-color: #ffffff;
            box-shadow: none;
            border: 1px solid #e2e8f0;
            
            &:hover {
              border-color: #0ea5e9;
            }
          }
          
          :deep(.el-select__caret) {
            color: #64748b;
          }
        }
      }
    }
    
    .cyber-pagination {
      margin: 0 8px;
      height: 32px;
      
      :deep(.el-pagination) {
        display: flex;
        height: 32px;
      }
      
      :deep(.el-pagination__total) {
        margin-right: 16px;
      }
      
      :deep(.el-pagination__sizes) {
        margin-right: 16px;
      }
      
      :deep(.el-pagination__jump) {
        margin-left: 16px;
      }
      
      :deep(.btn-prev),
      :deep(.btn-next) {
        min-width: 32px;
        height: 32px;
        background-color: #ffffff;
        border: 1px solid #e2e8f0;
        color: #64748b;
        font-weight: 500;
        transition: all 0.3s;
        
        &:hover {
          color: #0ea5e9;
          border-color: #0ea5e9;
          background-color: rgba(14, 165, 233, 0.05);
        }
      }
      
      :deep(.el-pager) {
        display: flex;
        align-items: center;
      }
      
      :deep(.el-pager li) {
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
      
      // 确保分页组件可见
      display: flex !important;
      align-items: center !important;
    }
  }
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
  
  .power-badge {
    font-weight: 600;
    border-radius: 8px;
    padding: 6px 20px;
    background: linear-gradient(135deg, #2563eb, #3b82f6);
    display: inline-block;
    color: white;
    position: relative;
    overflow: hidden;
    box-shadow: 0 0 8px rgba(59, 130, 246, 0.3);
    min-width: 110px;
    font-size: 15px;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 70%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.5), transparent);
      transform: skewX(-30deg);
      animation: shine 3s infinite;
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

.cyber-dialog {
  :deep(.el-dialog) {
    background-color: #ffffff;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 0 30px rgba(14, 165, 233, 0.15);
    border: 1px solid #e2e8f0;
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 1px;
      background: linear-gradient(90deg, transparent, #0ea5e9, transparent);
    }
    
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      width: 100%;
      height: 1px;
      background: linear-gradient(90deg, transparent, rgba(14, 165, 233, 0.5), transparent);
    }
  }
  
  :deep(.el-dialog__header) {
    padding: 20px 24px;
    background-color: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    margin-right: 0;
    color: #334155;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 20px;
      right: 20px;
      height: 1px;
      background: linear-gradient(90deg, transparent, rgba(14, 165, 233, 0.3), transparent);
    }
    
    .el-dialog__title {
      color: #334155;
      font-weight: 600;
      font-size: 18px;
      position: relative;
      display: inline-block;
      
      &::before {
        content: '';
        position: absolute;
        width: 4px;
        height: 100%;
        left: -12px;
        top: 0;
        background: linear-gradient(to bottom, #0ea5e9, transparent);
      }
    }
    
    .el-dialog__close {
      color: #64748b;
      
      &:hover {
        color: #0ea5e9;
        background-color: rgba(14, 165, 233, 0.1);
        border-radius: 50%;
      }
    }
  }
  
  :deep(.el-dialog__body) {
    padding: 30px 24px;
    color: #334155;
  }
  
  .cyber-form {
    :deep(.el-form-item__label) {
      color: #475569;
      font-weight: 500;
    }
    
    .cyber-input {
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
      
      :deep(.el-input__inner) {
        color: #334155;
        
        &::placeholder {
          color: #94a3b8;
        }
      }
    }
    
    .cyber-input-number {
      :deep(.el-input-number__decrease),
      :deep(.el-input-number__increase) {
        background-color: #f8fafc;
        color: #64748b;
        border-color: #cbd5e1;
        
        &:hover {
          color: #0ea5e9;
        }
      }
      
      :deep(.el-input__wrapper) {
        background-color: #ffffff;
        box-shadow: none;
        border: 1px solid #cbd5e1;
        
        &:hover {
          border-color: #0ea5e9;
        }
        
        .el-input__inner {
          color: #334155;
        }
      }
    }
    
    .cyber-radio-group {
      display: flex;
      gap: 20px;
      
      .cyber-radio {
        margin-right: 0;
        
        :deep(.el-radio__input) {
          display: none;
        }
        
        :deep(.el-radio__label) {
          padding-left: 0;
          color: #334155;
        }
        
        .status-option {
          display: flex;
          align-items: center;
          padding: 10px 16px;
          border-radius: 8px;
          transition: all 0.3s;
          position: relative;
          border: 1px solid #cbd5e1;
          background-color: #ffffff;
          
          .status-glow {
            width: 10px;
            height: 10px;
            border-radius: 50%;
            margin-right: 8px;
            position: relative;
            
            &.active-glow {
              background-color: #10b981;
              box-shadow: 0 0 10px #10b981;
            }
            
            &.inactive-glow {
              background-color: #ef4444;
              box-shadow: 0 0 10px #ef4444;
            }
          }
          
          &.active-option {
            background-color: rgba(16, 185, 129, 0.05);
          }
          
          &.inactive-option {
            background-color: rgba(239, 68, 68, 0.05);
          }
        }
        
        :deep(.is-checked) {
          .status-option {
            &.active-option {
              background-color: rgba(16, 185, 129, 0.1);
              border-color: #10b981;
              box-shadow: 0 0 15px rgba(16, 185, 129, 0.15);
            }
            
            &.inactive-option {
              background-color: rgba(239, 68, 68, 0.1);
              border-color: #ef4444;
              box-shadow: 0 0 15px rgba(239, 68, 68, 0.15);
            }
          }
        }
      }
    }
  }
  
  .dialog-footer {
    padding: 20px 24px;
    display: flex;
    justify-content: flex-end;
    gap: 16px;
    border-top: 1px solid #e2e8f0;
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 20px;
      right: 20px;
      height: 1px;
      background: linear-gradient(90deg, transparent, rgba(14, 165, 233, 0.3), transparent);
    }
    
    .cancel-btn,
    .submit-btn {
      border-radius: 8px;
      padding: 10px 24px;
      font-weight: 600;
      transition: all 0.3s;
    }
    
    .cancel-btn {
      background-color: transparent;
      border: 1px solid #cbd5e1;
      color: #64748b;
      
      &:hover {
        border-color: #0ea5e9;
        color: #0ea5e9;
        background-color: rgba(14, 165, 233, 0.05);
      }
    }
    
    .submit-btn {
      background: linear-gradient(45deg, #0ea5e9, #38bdf8);
      border: none;
      position: relative;
      overflow: hidden;
      color: white;
      
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
        box-shadow: 0 0 20px rgba(14, 165, 233, 0.3);
        transform: translateY(-2px);
        
        &::before {
          left: 100%;
        }
      }
    }
  }
}

.pagination-card {
  display: none;
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
    
    &.reset-button {
      background: linear-gradient(135deg, #8b5cf6, #a855f7);
      color: white;
      box-shadow: 0 0 6px rgba(168, 85, 247, 0.2);
      min-width: 55px;
      
      &:hover {
        box-shadow: 0 0 12px rgba(168, 85, 247, 0.3);
      }
    }
    
    &.ban-button, &.unban-button {
      min-width: 28px;
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

@keyframes shine {
  0% { left: -100%; }
  20% { left: 100%; }
  100% { left: 100%; }
}

:deep(.el-table__row) {
  transition: all 0.3s;
}

:deep(.el-input-number .el-input__wrapper) {
  width: 100%;
}

:deep(.el-form-item__error) {
  color: #ef4444;
}

:deep(.el-select-dropdown) {
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  
  .el-select-dropdown__item {
    color: #334155;
    
    &.hover, &:hover {
      background-color: rgba(14, 165, 233, 0.1);
    }
    
    &.selected {
      color: #0ea5e9;
      font-weight: 600;
    }
  }
}

:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.9);
  
  .el-loading-spinner {
    .path {
      stroke: #0ea5e9;
    }
    
    .el-loading-text {
      color: #0ea5e9;
    }
  }
}

:deep(.el-message) {
  background-color: #ffffff;
  border-color: #e2e8f0;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  
  .el-message__content {
    color: #334155;
  }
  
  &.el-message--success {
    border-color: #10b981;
    
    .el-message__icon {
      color: #10b981;
    }
  }
  
  &.el-message--error {
    border-color: #ef4444;
    
    .el-message__icon {
      color: #ef4444;
    }
  }
  
  &.el-message--warning {
    border-color: #f59e0b;
    
    .el-message__icon {
      color: #f59e0b;
    }
  }
  
  &.el-message--info {
    border-color: #0ea5e9;
    
    .el-message__icon {
      color: #0ea5e9;
    }
  }
}

:deep(.el-message-box) {
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  box-shadow: 0 0 30px rgba(0, 0, 0, 0.1);
  
  .el-message-box__title {
    color: #334155;
  }
  
  .el-message-box__content {
    color: #475569;
  }
  
  .el-message-box__btns {
    .el-button {
      border-radius: 8px;
      
      &--default {
        background-color: transparent;
        border: 1px solid #cbd5e1;
        color: #64748b;
        
        &:hover {
          border-color: #0ea5e9;
          color: #0ea5e9;
          background-color: rgba(14, 165, 233, 0.05);
        }
      }
      
      &--primary {
        background: linear-gradient(45deg, #0ea5e9, #38bdf8);
        border: none;
        color: white;
        
        &:hover {
          box-shadow: 0 0 15px rgba(14, 165, 233, 0.3);
          transform: translateY(-2px);
        }
      }
    }
  }
}

.data-summary {
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #e2e8f0;
  background-color: #f8fafc;
  
  .total-record, .no-data {
    color: #64748b;
    font-size: 14px;
    background: rgba(14, 165, 233, 0.1);
    padding: 6px 12px;
    border-radius: 8px;
    border: 1px solid rgba(14, 165, 233, 0.2);
  }
  
  .no-data {
    color: #94a3b8;
    background: rgba(203, 213, 225, 0.2);
    border-color: #e2e8f0;
  }
  
  .load-more-btn {
    background: linear-gradient(135deg, #0284c7, #0ea5e9);
    border: none;
    color: white;
    font-weight: 600;
    transition: all 0.3s;
    border-radius: 8px;
    
    &:hover:not(:disabled) {
      transform: translateY(-1px);
      box-shadow: 0 4px 8px rgba(14, 165, 233, 0.3);
    }
    
    &:disabled {
      background: #cbd5e1;
      opacity: 0.7;
      color: #f8fafc;
    }
  }
}

.page-nav-btn {
  display: none;
}
</style> 