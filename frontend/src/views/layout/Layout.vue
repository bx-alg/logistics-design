<template>
  <div class="app-wrapper">
    <div class="sidebar-container">
      <div class="logo">
        <h1>数智化供应链平台</h1>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <template v-for="item in routes">
          <el-submenu v-if="item.children && item.children.length > 1" :key="item.path" :index="item.path">
            <template slot="title">
              <i :class="item.meta.icon"></i>
              <span>{{ item.meta.title }}</span>
            </template>
            <el-menu-item v-for="child in item.children" :key="child.path" :index="item.path + '/' + child.path">
              {{ child.meta.title }}
            </el-menu-item>
          </el-submenu>
          <el-menu-item v-else-if="!item.hidden" :key="item.path + '-menu'" :index="item.path">
            <i :class="item.children[0].meta.icon || item.meta.icon"></i>
            <span slot="title">{{ item.children[0].meta.title || item.meta.title }}</span>
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
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Layout',
  computed: {
    routes() {
      return this.$router.options.routes.filter(item => !item.hidden)
    },
    activeMenu() {
      const route = this.$route
      return (route.matched && route.matched.length > 1 && route.matched[1].path) || route.path
    }
  }
}
</script>

<style scoped>
.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;
  display: flex;
}

.sidebar-container {
  width: 220px;
  height: 100%;
  background: #304156;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #263445;
}

.logo h1 {
  color: #fff;
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.sidebar-menu {
  border-right: none;
  height: calc(100% - 60px);
}

.main-container {
  flex: 1;
  overflow: auto;
  display: flex;
  flex-direction: column;
}

.header {
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 20px;
}

.user-info {
  cursor: pointer;
  color: #666;
}

.app-main {
  flex: 1;
  padding: 20px;
  overflow: auto;
}
</style>