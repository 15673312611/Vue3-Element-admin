<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">数字人后台管理系统</div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            :loading="loading"
            type="primary"
            class="login-button"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  console.log('开始登录流程...')
  try {
    await loginFormRef.value.validate()
    loading.value = true
    console.log('表单验证通过，发送登录请求...')
    
    await userStore.login(loginForm.username, loginForm.password)
    console.log('登录成功，获取用户信息...')
    
    await userStore.getInfo()
    console.log('用户信息获取成功，准备跳转...')
    
    const redirect = route.query.redirect || '/'
    console.log('跳转到', redirect)
    router.push(redirect)
    
    ElMessage.success('登录成功')
  } catch (error) {
    console.error('登录错误详情:', error)
    
    // 显示详细错误信息
    if (error.response) {
      const { status, data } = error.response
      console.error('HTTP错误:', status, data)
      ElMessage.error(data?.message || `服务器错误(${status})`)
    } else if (error.request) {
      console.error('请求未收到响应:', error.request)
      ElMessage.error('服务器无响应，请检查网络连接')
    } else {
      console.error('请求配置错误:', error.message)
      ElMessage.error(error.message || '登录失败')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-box {
  width: 400px;
  padding: 40px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 30px;
  color: #303133;
}

.login-form {
  margin-top: 30px;
}

.login-button {
  width: 100%;
}
</style> 