<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-card class="search-container">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="配置键名" prop="configKey">
          <el-input v-model="queryParams.configKey" placeholder="请输入配置键名" clearable style="width: 240px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作工具栏 -->
    <el-card class="table-container">
      <template #header>
        <div class="card-header">
          <span>系统配置列表</span>
          <div class="card-header-operations">
            <el-button type="primary" icon="Plus" @click="handleAdd">新增</el-button>
          </div>
        </div>
      </template>

      <!-- 表格区域 -->
      <el-table v-loading="loading" :data="configList" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="configKey" label="配置键名" min-width="180" show-overflow-tooltip />
        <el-table-column prop="configValue" label="配置值" min-width="180" show-overflow-tooltip />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" link icon="Edit" @click="handleUpdate(scope.row)">编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="form.id ? '编辑配置' : '新增配置'"
      v-model="dialog.visible"
      width="500px"
      append-to-body
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="配置键名" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入配置键名" :disabled="form.id !== undefined" />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="form.configValue" type="textarea" :rows="4" placeholder="请输入配置值" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getConfigList, addConfig, updateConfig, deleteConfig } from '@/api/systemConfig'

// 加载状态
const loading = ref(false)
// 列表数据
const configList = ref([])
// 总条数
const total = ref(0)
// 查询参数
const queryParams = reactive({
  configKey: '',
  page: 1,
  size: 10
})

// 对话框相关
const dialog = reactive({
  visible: false
})

// 表单对象
const form = reactive({
  id: undefined,
  configKey: '',
  configValue: '',
  description: ''
})

// 表单校验规则
const rules = {
  configKey: [
    { required: true, message: '配置键名不能为空', trigger: 'blur' },
    { max: 100, message: '配置键名长度不能超过100个字符', trigger: 'blur' }
  ],
  configValue: [
    { required: true, message: '配置值不能为空', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '描述长度不能超过500个字符', trigger: 'blur' }
  ]
}

// 表单引用
const formRef = ref(null)
// 查询表单引用
const queryForm = ref(null)

// 查询列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getConfigList(queryParams)
    configList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取配置列表失败', error)
    ElMessage.error('获取配置列表失败')
  } finally {
    loading.value = false
  }
}

// 查询操作
const handleQuery = () => {
  queryParams.page = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryForm.value?.resetFields()
  handleQuery()
}

// 分页大小变化
const handleSizeChange = (size) => {
  queryParams.size = size
  getList()
}

// 分页页码变化
const handleCurrentChange = (page) => {
  queryParams.page = page
  getList()
}

// 重置表单
const resetForm = () => {
  form.id = undefined
  form.configKey = ''
  form.configValue = ''
  form.description = ''
  formRef.value?.resetFields()
}

// 新增配置
const handleAdd = () => {
  resetForm()
  dialog.visible = true
}

// 修改配置
const handleUpdate = (row) => {
  resetForm()
  // 直接使用行数据，不再请求详情
  console.log('编辑配置行数据:', row)
  Object.assign(form, {
    id: row.id,
    configKey: row.configKey,
    configValue: row.configValue,
    description: row.description
  })
  dialog.visible = true
}

// 提交表单
const submitForm = async () => {
  formRef.value?.validate(async (valid) => {
    if (!valid) return
    
    try {
      if (form.id) {
        // 更新
        await updateConfig(form.id, form)
        ElMessage.success('修改成功')
      } else {
        // 新增
        await addConfig(form)
        ElMessage.success('新增成功')
      }
      dialog.visible = false
      getList()
    } catch (error) {
      console.error('保存失败', error)
      ElMessage.error('保存失败')
    }
  })
}

// 删除配置
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该配置吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteConfig(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 页面加载时获取列表
onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.search-container {
  margin-bottom: 20px;
}

.table-container {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style> 