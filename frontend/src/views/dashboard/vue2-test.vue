<template>
  <div>
    <h1>Vue 2 测试页面</h1>
    <p>这是一个使用 Vue 2 语法的测试页面</p>
    <p>当前计数: {{ count }}</p>
    <button @click="increment">增加</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      count: 0
    }
  },
  methods: {
    increment() {
      this.count++
    }
  }
}
</script> 