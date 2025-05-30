<template>
  <div class="test-container">
    <h1>测试页面</h1>
    <el-button type="primary" @click="testClick">点击测试</el-button>
  </div>
</template>

<script>
export default {
  name: 'Test',
  data() {
    return {
      message: 'Hello'
    }
  },
  created() {
    console.log('Test component created')
  },
  mounted() {
    console.log('Test component mounted')
  },
  methods: {
    testClick() {
      this.$message.success('按钮点击成功')
    }
  }
}
</script>

<style scoped>
.test-container {
  padding: 20px;
}
</style> 