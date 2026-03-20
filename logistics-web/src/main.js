import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
// 导入 Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)

// 使用插件
app.use(ElementPlus)
app.mount('#app')