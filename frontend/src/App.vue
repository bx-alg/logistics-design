<template>
  <div id="app">
    <div class="app-debug" v-if="showDebug">
      <p>App.vue 已加载</p>
      <p>当前路由: {{ $route.path }}</p>
    </div>
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      showDebug: false
    }
  },
  created() {
    console.log('App created')
  },
  mounted() {
    console.log('App mounted')
  }
}
</script>

<style>
html, body {
  height: 100%;
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  height: 100%;
}

/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 全局过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

/* 修复 Element UI 的一些样式问题 */
.el-card {
  overflow: visible !important;
}

.el-table {
  width: 100%;
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}

.app-debug {
  position: fixed;
  top: 0;
  left: 0;
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  padding: 10px;
  z-index: 10000;
  font-family: monospace;
  font-size: 12px;
}
</style> 