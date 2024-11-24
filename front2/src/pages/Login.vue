<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>教师考勤系统</h2>
        </div>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginForm" label-position="top">
        <el-form-item label="工号" prop="username">
          <el-input v-model="username" placeholder="请输入工号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" style="width: 100%">
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="login-footer">
      <p> 2024 教师考勤管理系统 版权所有</p>
    </div>
    
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const username = ref('')
const password = ref('')

const loginForm = {
  username,
  password
}

const rules = {
  username: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  try {
    console.log('Attempting login with:', { username: username.value, password: password.value })
    
    loading.value = true
    const response = await fetch('http://localhost:8082/api/login', {
     
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: username.value,
        password: password.value
      })
    })
    
    console.log('Response status:', response.status)
    const data = await response.json()
    console.log('Response data:', data)
    
    if (!response.ok) {
      throw new Error(data.message || '登录失败')
    }
    
    if (data) {
      console.log('Login successful, teacher data:', data)
      // 存储教师信息
      localStorage.setItem('teacherInfo', JSON.stringify(data))
      localStorage.setItem('token', 'logged-in')
      ElMessage.success('登录成功')
       //router.push('/dashboard') //  router.push('/profile')
       router.push('/') //跳转到主页 /
    }
  } catch (error) {
    console.error('Login error:', error)
    ElMessage.error('登录失败：工号或密码错误')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
}

.login-card {
  width: 400px;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.card-header {
  text-align: center;
  padding: 20px 0;
}

.card-header h2 {
  margin: 0;
  color: #1e88e5;
}

:deep(.el-form) {
  padding: 20px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #409eff inset;
}

:deep(.el-input__inner) {
  height: 40px;
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #409eff 0%, #1e80ff 100%);
  border: none;
}

.login-button:hover {
  background: linear-gradient(135deg, #66b1ff 0%, #409eff 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.login-footer {
  margin-top: 30px;
  text-align: center;
}

.login-footer p {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  margin: 0;
}

@media (max-width: 480px) {
  .card-header h2 {
    font-size: 20px;
  }
}
</style>