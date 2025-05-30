<template>
  <div class="simple-container">
    <h1>组合式 API 测试页面</h1>
    <p>当前时间: {{ currentTime }}</p>
    <el-button type="primary" @click="updateTime">刷新时间</el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const currentTime = ref(new Date().toLocaleString())

function updateTime() {
  currentTime.value = new Date().toLocaleString()
}
</script>

<style scoped>
.simple-container {
  padding: 20px;
}
</style> 