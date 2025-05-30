<template>
  <div class="app-wrapper">
    <div class="sidebar-container">
      <div class="logo">
        <h1>智慧物流平台</h1>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <!-- 添加数据总览菜单 -->
        <el-menu-item index="/data-overview">
          <i class="el-icon-s-data"></i>
          <span slot="title">数据总览</span>
        </el-menu-item>
        
        <!-- 其他菜单项 -->
        <template v-for="item in routes.filter(route => route.path !== '/')">
          <el-submenu v-if="item.children && item.children.length > 1" :key="item.path" :index="item.path">
            <template slot="title">
              <i v-if="item.meta && item.meta.icon" :class="item.meta.icon"></i>
              <span>{{ item.meta ? item.meta.title : '' }}</span>
            </template>
            <el-menu-item 
              v-for="child in item.children.filter(c => !c.hidden && !(c.meta && c.meta.hidden))" 
              :key="child.path" 
              :index="resolvePath(item.path, child.path)"
            >
              {{ child.meta ? child.meta.title : '' }}
            </el-menu-item>
          </el-submenu>
          <el-menu-item v-else-if="!item.hidden" :key="item.path + '-menu'" :index="item.path">
            <i v-if="getMenuIcon(item)" :class="getMenuIcon(item)"></i>
            <span slot="title">{{ getMenuTitle(item) }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </div>
    <div class="main-container">
      <div class="header">
        <div class="right-menu">
          <el-dropdown trigger="click">
            <span class="user-info">
              管理员 <i class="el-icon-caret-bottom"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>个人中心</el-dropdown-item>
              <el-dropdown-item divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <div class="app-main">
        <div class="debug-info" v-if="showDebug">
          <p>当前路由: {{ $route.path }}</p>
          <p>可用路由: {{ JSON.stringify(routes, null, 2) }}</p>
        </div>
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Layout',
  data() {
    return {
      showDebug: false // 关闭调试信息
    }
  },
  computed: {
    routes() {
      return this.$router.options.routes.filter(item => !item.hidden)
    },
    activeMenu() {
      const route = this.$route
      return (route.matched && route.matched.length > 1 && route.matched[1].path) || route.path
    }
  },
  methods: {
    resolvePath(basePath, routePath) {
      // 确保路径格式正确
      if (basePath.endsWith('/') && routePath.startsWith('/')) {
        return basePath + routePath.substring(1)
      } else if (!basePath.endsWith('/') && !routePath.startsWith('/')) {
        return basePath + '/' + routePath
      } else {
        return basePath + routePath
      }
    },
    getMenuIcon(item) {
      if (item.children && item.children[0] && item.children[0].meta && item.children[0].meta.icon) {
        return item.children[0].meta.icon
      }
      return item.meta && item.meta.icon ? item.meta.icon : ''
    },
    getMenuTitle(item) {
      if (item.children && item.children[0] && item.children[0].meta && item.children[0].meta.title) {
        return item.children[0].meta.title
      }
      return item.meta && item.meta.title ? item.meta.title : ''
    }
  },
  created() {
    console.log('Layout created, routes:', this.routes)
  },
  mounted() {
    console.log('Layout mounted, current route:', this.$route.path)
  }
}
</script>

<style scoped>
.app-wrapper {
  position: relative;
  height: 100vh;
  width: 100%;
  display: flex;
  overflow: hidden;
}

.sidebar-container {
  width: 220px;
  height: 100vh;
  background: #304156;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  overflow-y: auto;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #2b3b4e;
}

.logo h1 {
  color: #fff;
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 1px;
}

.sidebar-menu {
  border-right: none;
  height: calc(100% - 60px);
}

.main-container {
  flex: 1;
  margin-left: 220px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: relative;
  background-color: #f5f7fa;
  z-index: 1;
}

.header {
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 20px;
  position: fixed;
  top: 0;
  right: 0;
  left: 220px;
  z-index: 1000;
}

.user-info {
  cursor: pointer;
  color: #303133;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.user-info i {
  margin-left: 5px;
  font-size: 12px;
}

.app-main {
  flex: 1;
  padding: 20px;
  margin-top: 60px;
  overflow-y: auto;
  box-sizing: border-box;
}

.debug-info {
  margin-bottom: 20px;
  padding: 10px;
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 4px;
}
</style>