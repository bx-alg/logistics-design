<template>
  <div class="login-container">
    <div class="login-form-container">
      <h2 class="title">数智化供应链平台</h2>
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" prefix-icon="el-icon-user" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" prefix-icon="el-icon-lock" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button :loading="loading" type="primary" class="login-button" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: 'admin',
        password: '123456'
      },
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          // 模拟登录，实际项目中应该调用接口
          setTimeout(() => {
            this.loading = false
            this.$router.push({ path: '/' })
          }, 1000)
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  height: 100%;
  width: 100%;
  background-color: #2d3a4b;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-form-container {
  padding: 35px;
  width: 350px;
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.login-button {
  width: 100%;
}
</style> 