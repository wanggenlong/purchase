<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <div class="logo">
          <span class="logo-icon">📦</span>
        </div>
        <h1 class="title">采购管理系统</h1>
        <p class="subtitle">Sign in to continue</p>
      </div>
      
      <el-form
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.native.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="Username"
            prefix-icon="el-icon-user"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="Password"
            prefix-icon="el-icon-lock"
            show-password
            clearable
            @keyup.enter.native="handleLogin"
          />
        </el-form-item>
        
        <el-button
          type="primary"
          :loading="loading"
          class="login-button"
          @click="handleLogin"
        >
          {{ loading ? 'Signing in...' : 'Sign in' }}
        </el-button>
      </el-form>
      
      <div class="login-footer">
        <p class="footer-text">Use your corporate account</p>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/auth';

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, message: 'Please enter username', trigger: 'blur' }
        ],
        password: [
          { required: true, message: 'Please enter password', trigger: 'blur' },
          { min: 6, message: 'Password must be at least 6 characters', trigger: 'blur' }
        ]
      },
      loading: false
    };
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) return;
        
        this.loading = true;
        login(this.loginForm)
          .then(res => {
            if (res.code === 0) {
              localStorage.setItem('token', res.data.token);
              localStorage.setItem('username', res.data.username);
              localStorage.setItem('realName', res.data.realName);
              this.$message.success('Login successful');
              this.$router.replace('/');
            } else {
              this.$message.error(res.msg || 'Login failed');
            }
          })
          .catch(err => {
            this.$message.error('Login failed: ' + (err.message || 'Unknown error'));
          })
          .finally(() => {
            this.loading = false;
          });
      });
    }
  }
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7eb 100%);
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 400px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  margin-bottom: 16px;
}

.logo-icon {
  font-size: 48px;
}

.title {
  font-size: 24px;
  font-weight: 500;
  color: #202124;
  margin: 0 0 8px 0;
  font-family: 'Product Sans', Arial, sans-serif;
}

.subtitle {
  font-size: 14px;
  color: #5f6368;
  margin: 0;
}

.login-form {
  margin-bottom: 24px;
}

.login-form >>> .el-input__inner {
  height: 44px;
  border-radius: 4px;
  border: 1px solid #dadce0;
  padding: 0 12px 0 40px;
}

.login-form >>> .el-input__inner:focus {
  border-color: #1a73e8;
  box-shadow: 0 0 0 1px #1a73e8;
}

.login-form >>> .el-input__prefix {
  left: 10px;
  color: #5f6368;
}

.login-form >>> .el-input--prefix .el-input__inner {
  padding-left: 40px;
}

.login-button {
  width: 100%;
  height: 44px;
  background: #1a73e8;
  border-color: #1a73e8;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  margin-top: 8px;
}

.login-button:hover {
  background: #1765cc;
  border-color: #1765cc;
}

.login-button:focus {
  background: #1765cc;
  border-color: #1765cc;
}

.login-footer {
  text-align: center;
}

.footer-text {
  font-size: 12px;
  color: #5f6368;
  margin: 0;
}
</style>