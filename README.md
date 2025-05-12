# 数字人后台管理系统

这是一个基于Vue3和Spring Boot的后台管理系统，主要用于管理数字人相关资源和用户。适合小型项目作为后台管理系统使用。

演示站:https://admincs.chataot.cn/

联系作者,接网站开发 白菜价(微信号：soe303)
![微信图片_20241105222850](https://github.com/user-attachments/assets/57d247f8-0db1-4231-bd2b-b4111bf00f47)


![image](https://github.com/user-attachments/assets/b5b69337-32b8-4fd9-8ab1-88bf758c3e53)
![image](https://github.com/user-attachments/assets/5096b15d-ff16-4ddb-b56b-3e828c398d55)
![image](https://github.com/user-attachments/assets/4b028e9f-56c8-4e3d-9309-cba6dc9c44d6)

## 项目结构

```
szrConsole/
├── szr-admin-frontend/     # 前端项目，基于Vue3 + Element Plus
└── szr-admin-backend/      # 后端项目，基于Spring Boot
```

## 技术栈

### 前端
- Vue 3
- Element Plus
- Axios
- Vue Router
- Pinia

### 后端
- Spring Boot 2.7.x
- Spring Security + JWT
- MyBatis Plus
- MySQL8.0

## 功能模块

### 核心功能
1. 登录认证
   - 用户登录（用户名/密码）
   - Token生成与存储（JWT）
   - 登录状态校验
   - 401/403错误处理

2. 基础CRUD模块
   - 数据表格（分页、筛选、排序）
   - 新增/编辑/删除操作
   - 表单验证
   - 数据详情查看

3. 权限管理
   - 角色分配
   - 按钮级权限控制
   - 菜单权限动态加载

### 业务模块
1. 用户管理
2. 形象管理
3. 音色管理
4. 数字人管理
5. 卡密管理
6. 系统设置

## 快速启动

### 前端

```bash
cd szr-admin-frontend
npm install
npm run dev
```

### 后端

```bash
cd szr-admin-backend
mvn spring-boot:run
```

## 配置说明

### 前端配置
前端配置文件位于 `szr-admin-frontend/vite.config.js`，主要配置代理等信息。

### 后端配置
后端配置文件位于 `szr-admin-backend/src/main/resources/application.yml`，主要配置数据库连接、JWT等信息。

## 部署说明

### 前端部署
```bash
cd szr-admin-frontend
npm run build
```
将生成的 `dist` 目录部署到Web服务器。

### 后端部署
```bash
cd szr-admin-backend
mvn clean package
```
将生成的 jar 文件部署到服务器，通过 `java -jar` 命令启动。

# 系统名称和Logo配置实现文档

## 功能描述

将系统名称和Logo设置为可配置项，允许管理员在系统设置中修改，并在前端页面显示。

## 实现步骤

### 1. 后端实现

1. 创建SQL迁移脚本 `szr-admin-backend/src/main/resources/db/migration/V1.1.1__add_system_name_logo_config.sql`，添加系统名称和Logo配置项：
   ```sql
   INSERT INTO system_config (config_key, config_value, config_name, config_type, remark, create_time, update_time)
   VALUES 
     ('system_name', '数字人后台', '系统名称', 'basic', '系统名称配置', NOW(), NOW()),
     ('system_logo', '/static/logo.svg', '系统Logo', 'basic', '系统Logo配置', NOW(), NOW())
   ON DUPLICATE KEY UPDATE 
     config_value = VALUES(config_value),
     update_time = NOW();
   ```

2. 在 `SystemConfigController` 中添加获取公共配置的接口（无需权限验证）：
   ```java
   @GetMapping("/public")
   public Result<Map<String, String>> getPublicConfig() {
       List<String> keys = Arrays.asList("system_name", "system_logo");
       Map<String, String> configMap = systemConfigService.getConfigByKeys(keys);
       return Result.success(configMap);
   }
   ```

### 2. 前端实现

1. 在 `szr-admin-frontend/src/api/system.js` 中添加获取公共配置的API：
   ```javascript
   /**
    * 获取公共配置（系统名称、Logo等）
    * @returns {Promise} Promise对象
    */
   export function getPublicConfig() {
     return request({
       url: '/api/system/config/public',
       method: 'get'
     })
   }
   ```

2. 修改布局组件 `szr-admin-frontend/src/views/layout/index.vue`，使用配置的系统名称和Logo：
   - 添加响应式变量存储配置值：
     ```javascript
     const systemName = ref('')
     const systemLogo = ref('')
     ```
   - 添加获取配置的方法：
     ```javascript
     const fetchSystemConfig = async () => {
       try {
         const { code, data } = await getPublicConfig()
         if (code === 0 && data) {
           systemName.value = data.system_name
           systemLogo.value = data.system_logo
         }
       } catch (error) {
         console.error('获取系统配置失败:', error)
       }
     }
     
     onMounted(() => {
       fetchSystemConfig()
     })
     ```
   - 在模板中使用配置值：
     ```html
     <img :src="systemLogo || '@/assets/logo.svg'" class="logo-image" alt="Logo" />
     <span class="logo-title">{{ systemName || '数字人后台' }}</span>
     ```

## 配置管理

管理员可以在系统设置页面的"基本设置"选项卡中修改系统名称和Logo配置。修改后，前端页面将自动显示新的系统名称和Logo。

## 注意事项

1. Logo图片应上传到服务器的静态资源目录，确保路径正确。
2. 如果配置获取失败，将使用默认的系统名称和Logo。
3. 系统名称和Logo配置变更后，可能需要刷新页面才能看到效果。 
