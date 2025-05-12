import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
// 先导入自定义ElementPlus样式，再导入全局样式，确保自定义样式优先级高于全局样式
import './styles/variables.scss'
import './styles/elementplus-custom.scss'
import './styles/index.scss'
import './styles/global.scss'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus)
app.use(createPinia())
app.use(router)

app.mount('#app') 