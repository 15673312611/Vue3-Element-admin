<template>
  <div class="app-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="main-title">卡密管理</h2>
      <div class="page-desc">轻松管理系统卡密，支持批量生成和导出</div>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-container" shadow="hover">
      <div class="search-header">
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
      </div>
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="卡密" prop="cardKey">
          <el-input
            v-model="queryParams.cardKey"
            placeholder="请输入卡密"
            clearable
            prefix-icon="Search"
            class="custom-input"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select 
            v-model="queryParams.status" 
            placeholder="请选择状态" 
            clearable 
            class="custom-select"
            style="width: 200px;">
            <el-option label="未使用" value="unused" />
            <el-option label="未使用" value="UNUSED" />
            <el-option label="已使用" value="used" />
            <el-option label="已使用" value="USED" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间" prop="createTime">
          <el-date-picker
            v-model="queryParams.createTime"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            class="custom-date-picker"
            style="width: 320px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery" class="custom-button search-btn">
            <i class="el-icon-search"></i> 查询
          </el-button>
          <el-button @click="resetQuery" class="custom-button reset-btn">
            <i class="el-icon-refresh"></i> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作区域 -->
    <el-card class="table-container" shadow="hover">
      <div class="table-header">
        <div class="left-actions">
          <el-button type="primary" @click="handleGenerate" class="custom-button generate-btn">
            <i class="el-icon-plus"></i> 生成卡密
          </el-button>
          <el-button type="success" @click="handleExport" class="custom-button export-btn">
            <i class="el-icon-download"></i> 导出卡密
          </el-button>
          <el-button type="danger" @click="handleBatchDelete" class="custom-button delete-batch-btn" :disabled="selectedRows.length === 0">
            <i class="el-icon-delete"></i> 批量删除
          </el-button>
        </div>
        <div class="right-actions">
          <el-button type="info" @click="getList" class="custom-button refresh-btn">
            <i class="el-icon-refresh"></i> 刷新
          </el-button>
        </div>
      </div>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="cardKeyList"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
        header-row-class-name="table-header-row"
        row-class-name="table-row"
        class="custom-table"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="cardNo" label="卡密" min-width="90" show-overflow-tooltip />
        <el-table-column prop="amount" label="算力" width="150" align="center">
          <template #default="scope">
            <span class="amount-tag">{{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="140" align="center">
          <template #default="scope">
            <el-tag
              :type="getStatusType(scope.row.status)"
              effect="dark"
              round
              class="status-tag"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="usedBy" label="使用者" width="150" align="center" show-overflow-tooltip />
        <el-table-column label="使用时间" width="220" align="center">
          <template #default="scope">
            {{ formatDateTime(scope.row.usedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="220" align="center">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button 
              type="danger" 
              link
              v-if="scope.row.status === 'unused' || scope.row.status === 'UNUSED'"
              @click="handleDelete(scope.row)"
              class="delete-btn"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryParams.page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="queryParams.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        background
        class="pagination"
      />
    </el-card>

    <!-- 生成卡密对话框 -->
    <el-dialog
      title="生成卡密"
      v-model="dialogVisible"
      width="500px"
      append-to-body
      destroy-on-close
      class="custom-dialog"
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="100px"
        class="dialog-form"
      >
        <el-form-item label="生成数量" prop="count">
          <el-input-number 
            v-model="form.count" 
            :min="1" 
            :max="100"
            placeholder="请输入生成数量"
            class="custom-input-number"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="算力" prop="amount">
          <el-input-number 
            v-model="form.amount" 
            :min="1" 
            :max="1000"
            placeholder="请输入算力值"
            class="custom-input-number"
            style="width: 100%"
          />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCardKeyList, generateCardKeys, deleteCardKey, exportCardKey } from '@/api/cardKey'

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  cardKey: '',
  status: '',
  createTime: []
})

// 表格数据
const cardKeyList = ref([])
const total = ref(0)
const loading = ref(false)
const selectedRows = ref([])

// 表单参数
const dialogVisible = ref(false)
const form = reactive({
  count: 1,
  amount: 100
})

// 表单校验规则
const rules = {
  count: [
    { required: true, message: '请输入生成数量', trigger: 'blur' },
    { type: 'number', min: 1, max: 100, message: '数量在 1 到 100 之间', trigger: 'blur' }
  ],
  amount: [
    { required: true, message: '请输入算力值', trigger: 'blur' },
    { type: 'number', min: 1, max: 1000, message: '算力在 1 到 1000 之间', trigger: 'blur' }
  ]
}

// 引用
const queryForm = ref(null)
const formRef = ref(null)

// 初始化
onMounted(() => {
  getList()
})

// 获取卡密列表
const getList = async () => {
  try {
    loading.value = true
    const res = await getCardKeyList(queryParams)
    cardKeyList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取卡密列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    'UNUSED': 'info',    // 未使用
    'unused': 'info',    // 未使用
    'USED': 'success',   // 已使用
    'used': 'success'    // 已使用
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'UNUSED': '未使用',
    'unused': '未使用',
    'USED': '已使用',
    'used': '已使用'
  }
  return statusMap[status] || '未知'
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

// 生成卡密
const handleGenerate = () => {
  form.count = 1
  form.amount = 100
  dialogVisible.value = true
}

// 导出卡密
const handleExport = async () => {
  try {
    const res = await exportCardKey("current")
    // 处理导出结果...
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出卡密失败：', error)
    ElMessage.error('导出失败')
  }
}

// 删除卡密
const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该卡密吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCardKey(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败：', error)
    }
  }).catch(() => {})
}

// 批量删除卡密
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一项进行删除')
    return
  }
  
  ElMessageBox.confirm(`确认要删除选中的 ${selectedRows.value.length} 个卡密吗?`, '批量删除提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const deletePromises = selectedRows.value.map(row => deleteCardKey(row.id))
      await Promise.all(deletePromises)
      ElMessage.success(`成功删除 ${selectedRows.value.length} 个卡密`)
      getList()
      selectedRows.value = []
    } catch (error) {
      console.error('批量删除失败：', error)
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

// 提交表单
const submitForm = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await generateCardKeys({
          type: 1,
          value: form.amount,
          count: form.count
        })
        ElMessage.success(`成功生成 ${res.data.count} 个卡密`)
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('生成卡密失败：', error)
        ElMessage.error('生成失败')
      }
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

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
  height: auto;
  overflow-y: auto;
}

.page-header {
  margin-bottom: 20px;
  padding: 20px;
  background: linear-gradient(120deg, #334155, #475569);
  border-radius: 12px;
  color: #fff;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  animation: fadeIn 0.6s ease-in-out;

  .main-title {
            font-size: 24px;
            margin: 0 0 5px 0;
            font-weight: 600;
          }
  .page-title {
    font-size: 28px;
    margin: 0;
    margin-bottom: 10px;
    font-weight: 600;
    letter-spacing: 1px;
    text-shadow: 2px 2px 4px rgba(255, 255, 255, 0.2);
  }
  
  .page-desc {
    font-size: 16px;
    opacity: 0.9;
  }
}

.search-container,
.table-container {
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  background-color: #fff;
  animation: slideUp 0.4s ease-out;
  
  &:hover {
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }
}

.search-header {
  margin-bottom: 15px;
  font-size: 18px;
  font-weight: bold;
  color: #00BCD4;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding-bottom: 12px;
  
  i {
    margin-right: 8px;
  }
}

.search-form {
  margin-top: 15px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  
  .left-actions {
    display: flex;
    gap: 12px;
  }
  
  .right-actions {
    display: flex;
    gap: 12px;
  }
}

.custom-button {
  transition: all 0.3s;
  border-radius: 6px;
  font-weight: 500;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
  }
  
  &:active {
    transform: translateY(1px);
  }
}

.generate-btn {
  background: linear-gradient(to right, #00BCD4, #00838F);
  border: none;
  
  &:hover {
    background: linear-gradient(to right, #00ACC1, #006064);
  }
}

.export-btn {
  background: linear-gradient(to right, #4DB6AC, #009688);
  border: none;
  
  &:hover {
    background: linear-gradient(to right, #26A69A, #00796B);
  }
}

.delete-batch-btn {
  background: linear-gradient(to right, #FF5252, #FF1744);
  border: none;
  
  &:hover {
    background: linear-gradient(to right, #FF1744, #D50000);
  }
  
  &:disabled {
    background: #BDBDBD;
    cursor: not-allowed;
    transform: none;
    
    &:hover {
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    }
  }
}

.refresh-btn {
  background: linear-gradient(to right, #26C6DA, #00ACC1);
  border: none;
  color: white;
  
  &:hover {
    background: linear-gradient(to right, #00ACC1, #0097A7);
  }
}

.search-btn {
  background: linear-gradient(to right, #00BCD4, #0097A7);
  border: none;
  
  &:hover {
    background: linear-gradient(to right, #00ACC1, #00838F);
  }
}

.reset-btn {
  background: linear-gradient(to right, #80DEEA, #26C6DA);
  color: white;
  border: none;
  
  &:hover {
    background: linear-gradient(to right, #4DD0E1, #00ACC1);
  }
}

.custom-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
  
  .el-table__header-wrapper {
    .el-table__header {
      th {
        background-color: #E0F7FA !important;
      }
    }
  }
}

.table-header-row {
  background-color: #E0F7FA !important;
  color: #333;
  font-weight: 600;
  font-size: 14px;
  height: 54px;
}

.table-row {
  transition: all 0.3s;
  
  &:hover {
    background-color: #E0F7FA !important;
  }
}

.pagination {
  margin-top: 20px;
  text-align: right;
  padding-bottom: 20px;
}

.amount-tag {
  background: linear-gradient(to right, #00BCD4, #00838F);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-weight: 500;
  box-shadow: 0 2px 6px rgba(0, 188, 212, 0.2);
  display: inline-block;
}

.status-tag {
  padding: 6px 14px;
  font-weight: 500;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  
  &.el-tag--info {
    background: linear-gradient(to right, #80DEEA, #26C6DA);
    border: none;
  }
  
  &.el-tag--success {
    background: linear-gradient(to right, #4DB6AC, #009688);
    border: none;
  }
}

.custom-input,
.custom-select,
.custom-date-picker,
.custom-input-number {
  border-radius: 6px;
  transition: all 0.3s;
  
  &:focus, &:hover {
    box-shadow: 0 0 8px rgba(0, 188, 212, 0.2);
  }
}

.custom-dialog {
  border-radius: 12px;
  overflow: hidden;
  
  .el-dialog__header {
    background: linear-gradient(to right, #00BCD4, #00838F);
    color: white;
    padding: 16px 20px;
    
    .el-dialog__title {
      color: white;
      font-weight: 600;
    }
  }
  
  .el-dialog__body {
    padding: 24px;
  }
  
  .el-dialog__footer {
    padding: 16px 20px;
    border-top: 1px solid #eee;
  }
}

.dialog-form {
  .el-form-item__label {
    font-weight: 500;
  }
}

.submit-btn {
  background: linear-gradient(to right, #00BCD4, #00838F);
  border: none;
  
  &:hover {
    background: linear-gradient(to right, #00ACC1, #006064);
  }
}

.delete-btn {
  &:hover {
    color: #f56c6c;
    text-decoration: underline;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style> 