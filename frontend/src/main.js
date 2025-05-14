import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './styles/index.css'
import axios from 'axios'

Vue.config.productionTip = false
Vue.use(ElementUI, { size: 'medium' })

// 配置axios
axios.defaults.baseURL = '/api'
axios.defaults.timeout = 10000
Vue.prototype.$http = axios

new Vue({
  router,
  render: h => h(App)
}).$mount('#app') 