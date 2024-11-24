<template>
  <div class="logout-container">
    <el-card class="logout-card">
      <template #header>
        <div class="card-header">
          <h3>退出登录</h3>
        </div>
      </template>
      
      <div class="logout-content">
        <el-button type="primary" @click="showLogoutConfirm">
          退出登录
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const showLogoutConfirm = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      // 清除登录信息
      localStorage.removeItem('token')
      // 跳转到登录页
      router.push('/login')
    })
    .catch(() => {
      // 用户点击取消，不做任何操作
    })
}
</script>

<style scoped>
.logout-container {
  padding: 20px;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.logout-card {
  width: 100%;
  max-width: 400px;
}

.card-header {
  text-align: center;
}

.card-header h3 {
  margin: 0;
  color: #303133;
}

.logout-content {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.logout-content .el-button {
  width: 120px;
  height: 40px;
}
</style>
