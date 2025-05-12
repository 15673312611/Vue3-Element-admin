<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-card class="search-container">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="分类名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入分类名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作区域 -->
    <el-card class="table-container">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">新增分类</el-button>
      </div>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="categoryList"
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
              {{ scope.row.status === '1' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button 
              type="primary" 
              link
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button 
              :type="scope.row.status === '1' ? 'danger' : 'success'" 
              link
              @click="handleStatusChange(scope.row)"
            >
              {{ scope.row.status === '1' ? '禁用' : '启用' }}
            </el-button>
            <el-button 
              type="danger" 
              link
              @click="handleDelete(scope.row)"
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
      />
    </el-card>

    <!-- 添加或编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      append-to-body
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="80px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
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
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategoryList, getCategoryDetail, addCategory, updateCategory, deleteCategory, changeCategoryStatus } from '@/api/voiceCategory'

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  name: '',
  status: ''
})

// 表格数据
const categoryList = ref([])
const total = ref(0)
const loading = ref(false)

// 表单参数
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? '编辑分类' : '新增分类')
const form = reactive({
  id: undefined,
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

// 获取分类列表
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

// 新增分类
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = async (row) => {
  resetForm()
  try {
    const res = await getCategoryDetail(row.id)
    Object.assign(form, res.data)
    dialogVisible.value = true
  } catch (error) {
    console.error('获取分类详情失败：', error)
  }
}

// 重置表单
const resetForm = () => {
  form.id = undefined
  form.name = ''
  form.description = ''
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
        await updateCategory(form.id, form)
        ElMessage.success('修改成功')
      } else {
        // 新增
        await addCategory(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      getList()
    } catch (error) {
      console.error('操作失败：', error)
    }
  })
}

// 修改状态
const handleStatusChange = (row) => {
  const status = row.status === '1' ? '0' : '1'
  const statusText = status === '1' ? '启用' : '禁用'
  
  ElMessageBox.confirm(`确认要${statusText}该分类吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await changeCategoryStatus(row.id, status)
      ElMessage.success(`${statusText}成功`)
      getList()
    } catch (error) {
      console.error('修改状态失败：', error)
    }
  }).catch(() => {})
}

// 删除分类
const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该分类吗?', '提示', {
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
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.search-container,
.table-container {
  margin-bottom: 20px;
}

.table-header {
  margin-bottom: 20px;
}
</style> 