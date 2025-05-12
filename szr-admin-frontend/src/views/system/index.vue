<template>
  <div class="app-container">
    <el-tabs v-model="activeTab">
      <!-- 基本设置 -->
      <el-tab-pane label="基本设置" name="basic">
        <el-card>
          <el-form
            :model="basicForm"
            :rules="basicRules"
            ref="basicFormRef"
            label-width="120px"
          >
            <el-form-item label="系统名称" prop="systemName">
              <el-input v-model="basicForm.systemName" placeholder="请输入系统名称" />
            </el-form-item>
            <el-form-item label="系统Logo" prop="systemLogo">
              <el-upload
                class="logo-uploader"
                action="/api/upload"
                :show-file-list="false"
                :on-success="handleLogoSuccess"
                :before-upload="beforeLogoUpload"
              >
                <img v-if="basicForm.systemLogo" :src="basicForm.systemLogo" class="logo" />
                <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item label="系统描述" prop="systemDesc">
              <el-input
                v-model="basicForm.systemDesc"
                type="textarea"
                :rows="3"
                placeholder="请输入系统描述"
              />
            </el-form-item>
            <el-form-item label="版权信息" prop="copyright">
              <el-input v-model="basicForm.copyright" placeholder="请输入版权信息" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleBasicSubmit">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 安全设置 -->
      <el-tab-pane label="安全设置" name="security">
        <el-card>
          <el-form
            :model="securityForm"
            :rules="securityRules"
            ref="securityFormRef"
            label-width="120px"
          >
            <el-form-item label="登录验证码" prop="loginCaptcha">
              <el-switch v-model="securityForm.loginCaptcha" />
            </el-form-item>
            <el-form-item label="密码最小长度" prop="passwordMinLength">
              <el-input-number 
                v-model="securityForm.passwordMinLength" 
                :min="6" 
                :max="20"
              />
            </el-form-item>
            <el-form-item label="密码复杂度" prop="passwordComplexity">
              <el-checkbox-group v-model="securityForm.passwordComplexity">
                <el-checkbox label="number">必须包含数字</el-checkbox>
                <el-checkbox label="letter">必须包含字母</el-checkbox>
                <el-checkbox label="special">必须包含特殊字符</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="Token有效期" prop="tokenExpireTime">
              <el-input-number 
                v-model="securityForm.tokenExpireTime" 
                :min="1" 
                :max="24"
              />
              <span class="unit">小时</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSecuritySubmit">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 存储设置 -->
      <el-tab-pane label="存储设置" name="storage">
        <el-card>
          <el-form
            :model="storageForm"
            :rules="storageRules"
            ref="storageFormRef"
            label-width="120px"
          >
            <el-form-item label="存储方式" prop="storageType">
              <el-radio-group v-model="storageForm.storageType">
                <el-radio label="local">本地存储</el-radio>
                <el-radio label="oss">阿里云OSS</el-radio>
                <el-radio label="cos">腾讯云COS</el-radio>
              </el-radio-group>
            </el-form-item>

            <!-- 本地存储设置 -->
            <template v-if="storageForm.storageType === 'local'">
              <el-form-item label="存储路径" prop="localPath">
                <el-input v-model="storageForm.localPath" placeholder="请输入存储路径" />
              </el-form-item>
              <el-form-item label="访问域名" prop="localDomain">
                <el-input v-model="storageForm.localDomain" placeholder="请输入访问域名" />
              </el-form-item>
            </template>

            <!-- 阿里云OSS设置 -->
            <template v-if="storageForm.storageType === 'oss'">
              <el-form-item label="AccessKey" prop="ossAccessKey">
                <el-input v-model="storageForm.ossAccessKey" placeholder="请输入AccessKey" />
              </el-form-item>
              <el-form-item label="AccessSecret" prop="ossAccessSecret">
                <el-input 
                  v-model="storageForm.ossAccessSecret" 
                  type="password" 
                  placeholder="请输入AccessSecret"
                  show-password
                />
              </el-form-item>
              <el-form-item label="Endpoint" prop="ossEndpoint">
                <el-input v-model="storageForm.ossEndpoint" placeholder="请输入Endpoint" />
              </el-form-item>
              <el-form-item label="Bucket" prop="ossBucket">
                <el-input v-model="storageForm.ossBucket" placeholder="请输入Bucket" />
              </el-form-item>
            </template>

            <!-- 腾讯云COS设置 -->
            <template v-if="storageForm.storageType === 'cos'">
              <el-form-item label="SecretId" prop="cosSecretId">
                <el-input v-model="storageForm.cosSecretId" placeholder="请输入SecretId" />
              </el-form-item>
              <el-form-item label="SecretKey" prop="cosSecretKey">
                <el-input 
                  v-model="storageForm.cosSecretKey" 
                  type="password" 
                  placeholder="请输入SecretKey"
                  show-password
                />
              </el-form-item>
              <el-form-item label="Region" prop="cosRegion">
                <el-input v-model="storageForm.cosRegion" placeholder="请输入Region" />
              </el-form-item>
              <el-form-item label="Bucket" prop="cosBucket">
                <el-input v-model="storageForm.cosBucket" placeholder="请输入Bucket" />
              </el-form-item>
            </template>

            <el-form-item>
              <el-button type="primary" @click="handleStorageSubmit">保存设置</el-button>
              <el-button @click="handleTestConnection">测试连接</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { 
  getConfigList, 
  updateBasicConfig, 
  updateSecurityConfig, 
  updateStorageConfig,
  testStorageConnection
} from '@/api/system'

// 当前激活的标签页
const activeTab = ref('basic')

// 基本设置表单
const basicFormRef = ref(null)
const basicForm = reactive({
  systemName: '',
  systemLogo: '',
  systemDesc: '',
  copyright: ''
})

// 存储配置项ID
const configIds = reactive({
  system_name: null,
  system_logo: null,
  system_desc: null,
  copyright: null,
  security_login_captcha: null,
  security_password_min_length: null,
  security_password_complexity: null,
  security_token_expire_time: null,
  storage_type: null,
  storage_local_path: null,
  storage_local_domain: null,
  storage_oss_access_key: null,
  storage_oss_access_secret: null,
  storage_oss_endpoint: null,
  storage_oss_bucket: null,
  storage_cos_secret_id: null,
  storage_cos_secret_key: null,
  storage_cos_region: null,
  storage_cos_bucket: null
})

// 基本设置校验规则
const basicRules = {
  systemName: [
    { required: true, message: '请输入系统名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  systemDesc: [
    { max: 200, message: '长度不能超过 200 个字符', trigger: 'blur' }
  ],
  copyright: [
    { max: 100, message: '长度不能超过 100 个字符', trigger: 'blur' }
  ]
}

// 安全设置表单
const securityFormRef = ref(null)
const securityForm = reactive({
  loginCaptcha: true,
  passwordMinLength: 8,
  passwordComplexity: ['number', 'letter'],
  tokenExpireTime: 12
})

// 安全设置校验规则
const securityRules = {
  passwordMinLength: [
    { required: true, message: '请输入密码最小长度', trigger: 'blur' }
  ],
  tokenExpireTime: [
    { required: true, message: '请输入Token有效期', trigger: 'blur' }
  ]
}

// 存储设置表单
const storageFormRef = ref(null)
const storageForm = reactive({
  storageType: 'local',
  localPath: '',
  localDomain: '',
  ossAccessKey: '',
  ossAccessSecret: '',
  ossEndpoint: '',
  ossBucket: '',
  cosSecretId: '',
  cosSecretKey: '',
  cosRegion: '',
  cosBucket: ''
})

// 存储设置校验规则
const storageRules = {
  storageType: [
    { required: true, message: '请选择存储方式', trigger: 'change' }
  ],
  localPath: [
    { required: true, message: '请输入存储路径', trigger: 'blur' }
  ],
  localDomain: [
    { required: true, message: '请输入访问域名', trigger: 'blur' }
  ],
  ossAccessKey: [
    { required: true, message: '请输入AccessKey', trigger: 'blur' }
  ],
  ossAccessSecret: [
    { required: true, message: '请输入AccessSecret', trigger: 'blur' }
  ],
  ossEndpoint: [
    { required: true, message: '请输入Endpoint', trigger: 'blur' }
  ],
  ossBucket: [
    { required: true, message: '请输入Bucket', trigger: 'blur' }
  ],
  cosSecretId: [
    { required: true, message: '请输入SecretId', trigger: 'blur' }
  ],
  cosSecretKey: [
    { required: true, message: '请输入SecretKey', trigger: 'blur' }
  ],
  cosRegion: [
    { required: true, message: '请输入Region', trigger: 'blur' }
  ],
  cosBucket: [
    { required: true, message: '请输入Bucket', trigger: 'blur' }
  ]
}

// 初始化
onMounted(async () => {
  try {
    // 获取所有配置
    const res = await getConfigList({ page: 1, size: 100 })
    const configList = res.data.records || []
    
    // 处理配置数据
    configList.forEach(item => {
      const key = item.configKey
      const value = item.configValue
      
      // 保存配置项ID
      if (configIds.hasOwnProperty(key)) {
        configIds[key] = item.id
      }
      
      // 根据配置键前缀分组
      if (key.startsWith('system_')) {
        // 系统名称
        if (key === 'system_name') {
          basicForm.systemName = value
        }
        // 系统Logo
        else if (key === 'system_logo') {
          basicForm.systemLogo = value
        }
        // 系统描述
        else if (key === 'system_desc') {
          basicForm.systemDesc = value
        }
        // 版权信息
        else if (key === 'copyright') {
          basicForm.copyright = value
        }
      }
      else if (key.startsWith('security_')) {
        // 登录验证码
        if (key === 'security_login_captcha') {
          securityForm.loginCaptcha = value === 'true'
        }
        // 密码最小长度
        else if (key === 'security_password_min_length') {
          securityForm.passwordMinLength = parseInt(value) || 8
        }
        // 密码复杂度
        else if (key === 'security_password_complexity') {
          securityForm.passwordComplexity = value ? value.split(',') : ['number', 'letter']
        }
        // Token有效期
        else if (key === 'security_token_expire_time') {
          securityForm.tokenExpireTime = parseInt(value) || 12
        }
      }
      else if (key.startsWith('storage_')) {
        // 存储方式
        if (key === 'storage_type') {
          storageForm.storageType = value || 'local'
        }
        // 本地存储路径
        else if (key === 'storage_local_path') {
          storageForm.localPath = value
        }
        // 本地访问域名
        else if (key === 'storage_local_domain') {
          storageForm.localDomain = value
        }
        // OSS AccessKey
        else if (key === 'storage_oss_access_key') {
          storageForm.ossAccessKey = value
        }
        // OSS AccessSecret
        else if (key === 'storage_oss_access_secret') {
          storageForm.ossAccessSecret = value
        }
        // OSS Endpoint
        else if (key === 'storage_oss_endpoint') {
          storageForm.ossEndpoint = value
        }
        // OSS Bucket
        else if (key === 'storage_oss_bucket') {
          storageForm.ossBucket = value
        }
        // COS SecretId
        else if (key === 'storage_cos_secret_id') {
          storageForm.cosSecretId = value
        }
        // COS SecretKey
        else if (key === 'storage_cos_secret_key') {
          storageForm.cosSecretKey = value
        }
        // COS Region
        else if (key === 'storage_cos_region') {
          storageForm.cosRegion = value
        }
        // COS Bucket
        else if (key === 'storage_cos_bucket') {
          storageForm.cosBucket = value
        }
      }
    })
  } catch (error) {
    console.error('获取系统配置失败：', error)
  }
})

// Logo上传前校验
const beforeLogoUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// Logo上传成功回调
const handleLogoSuccess = (res) => {
  basicForm.systemLogo = res.data
}

// 保存基本设置
const handleBasicSubmit = () => {
  basicFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      // 构建配置列表
      const configs = [
        { id: configIds.system_name, configKey: 'system_name', configValue: basicForm.systemName },
        { id: configIds.system_logo, configKey: 'system_logo', configValue: basicForm.systemLogo },
        { id: configIds.system_desc, configKey: 'system_desc', configValue: basicForm.systemDesc || '' },
        { id: configIds.copyright, configKey: 'copyright', configValue: basicForm.copyright || '' }
      ]
      
      // 批量更新配置
      await updateBasicConfig({ configs })
      ElMessage.success('保存成功')
    } catch (error) {
      console.error('保存失败：', error)
      ElMessage.error('保存失败，请稍后重试')
    }
  })
}

// 保存安全设置
const handleSecuritySubmit = () => {
  securityFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      // 构建配置列表
      const configs = [
        { id: configIds.security_login_captcha, configKey: 'security_login_captcha', configValue: securityForm.loginCaptcha.toString() },
        { id: configIds.security_password_min_length, configKey: 'security_password_min_length', configValue: securityForm.passwordMinLength.toString() },
        { id: configIds.security_password_complexity, configKey: 'security_password_complexity', configValue: securityForm.passwordComplexity.join(',') },
        { id: configIds.security_token_expire_time, configKey: 'security_token_expire_time', configValue: securityForm.tokenExpireTime.toString() }
      ]
      
      // 批量更新配置
      await updateSecurityConfig({ configs })
      ElMessage.success('保存成功')
    } catch (error) {
      console.error('保存失败：', error)
      ElMessage.error('保存失败，请稍后重试')
    }
  })
}

// 保存存储设置
const handleStorageSubmit = () => {
  storageFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      // 构建配置列表
      const configs = [
        { id: configIds.storage_type, configKey: 'storage_type', configValue: storageForm.storageType }
      ]
      
      // 根据存储类型添加不同的配置
      if (storageForm.storageType === 'local') {
        configs.push(
          { id: configIds.storage_local_path, configKey: 'storage_local_path', configValue: storageForm.localPath },
          { id: configIds.storage_local_domain, configKey: 'storage_local_domain', configValue: storageForm.localDomain }
        )
      } else if (storageForm.storageType === 'oss') {
        configs.push(
          { id: configIds.storage_oss_access_key, configKey: 'storage_oss_access_key', configValue: storageForm.ossAccessKey },
          { id: configIds.storage_oss_access_secret, configKey: 'storage_oss_access_secret', configValue: storageForm.ossAccessSecret },
          { id: configIds.storage_oss_endpoint, configKey: 'storage_oss_endpoint', configValue: storageForm.ossEndpoint },
          { id: configIds.storage_oss_bucket, configKey: 'storage_oss_bucket', configValue: storageForm.ossBucket }
        )
      } else if (storageForm.storageType === 'cos') {
        configs.push(
          { id: configIds.storage_cos_secret_id, configKey: 'storage_cos_secret_id', configValue: storageForm.cosSecretId },
          { id: configIds.storage_cos_secret_key, configKey: 'storage_cos_secret_key', configValue: storageForm.cosSecretKey },
          { id: configIds.storage_cos_region, configKey: 'storage_cos_region', configValue: storageForm.cosRegion },
          { id: configIds.storage_cos_bucket, configKey: 'storage_cos_bucket', configValue: storageForm.cosBucket }
        )
      }
      
      // 批量更新配置
      await updateStorageConfig({ configs })
      ElMessage.success('保存成功')
    } catch (error) {
      console.error('保存失败：', error)
      ElMessage.error('保存失败，请稍后重试')
    }
  })
}

// 测试存储连接
const handleTestConnection = async () => {
  try {
    await testStorageConnection(storageForm)
    ElMessage.success('连接成功')
  } catch (error) {
    console.error('连接失败：', error)
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.logo-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);

    &:hover {
      border-color: var(--el-color-primary);
    }
  }
}

.logo-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  line-height: 120px;
}

.logo {
  width: 120px;
  height: 120px;
  display: block;
}

.unit {
  margin-left: 10px;
  color: var(--el-text-color-secondary);
}
</style> 