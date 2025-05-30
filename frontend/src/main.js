import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './styles/index.css'
import axios from 'axios'
import VueCompositionAPI from '@vue/composition-api'
import { createPinia, PiniaVuePlugin } from 'pinia'
import * as echarts from 'echarts'

Vue.use(VueCompositionAPI)
Vue.use(PiniaVuePlugin)
Vue.use(ElementUI, { size: 'medium' })

// 将echarts挂载到Vue原型上
Vue.prototype.$echarts = echarts

Vue.config.productionTip = false

// 配置axios
axios.defaults.timeout = 10000
Vue.prototype.$http = axios

const pinia = createPinia()

new Vue({
  router,
  pinia: pinia,
  render: h => h(App)
}).$mount('#app')