<template>
  <div class="simple-container">
    <h1>简单测试页面</h1>
    <p>当前时间: {{ currentTime }}</p>
    <el-button type="primary" @click="updateTime">刷新时间</el-button>
  </div>
</template>

<script>
export default {
  name: 'SimpleTest',
  data() {
    return {
      currentTime: new Date().toLocaleString()
    }
  },
  methods: {
    updateTime() {
      this.currentTime = new Date().toLocaleString()
    }
  },
  mounted() {
    console.log('Simple test page mounted')
  }
}
</script>

<style scoped>
.simple-container {
  padding: 20px;
}
</style> 