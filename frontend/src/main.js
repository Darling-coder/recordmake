import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { ElMessage } from 'element-plus'
import router from './router/index.js'

const app = createApp(App)

// 注册路由和UI组件库
app.use(router)
app.use(ElementPlus)

// 可选：全局挂载消息（模板里this.$message用，setup语法糖用不到）
app.config.globalProperties.$message = ElMessage

app.mount('#app')