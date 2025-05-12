<template>
  <div class="error-page">
    <div class="error-code">403</div>
    <div class="error-desc">抱歉，您没有权限访问该页面</div>
    <div class="error-tips">
      <p>如果您是管理员，请确保您已正确登录系统</p>
      <p>如需获取访问权限，请联系系统超级管理员</p>
    </div>
    <div class="error-actions">
      <el-button type="primary" @click="goHome">返回首页</el-button>
      <el-button @click="goBack">返回上一页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.error-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  color: #F56C6C;
}

.error-desc {
  font-size: 24px;
  margin: 20px 0 30px;
  color: #606266;
}

.error-tips {
  font-size: 16px;
  margin-bottom: 30px;
  color: #909399;
  text-align: center;
  line-height: 1.5;
}

.error-actions {
  display: flex;
  gap: 15px;
}
</style> 