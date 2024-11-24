<template>
  <div class="dashboard">
    <el-container class="dashboard-container">
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
        <div class="logo-container">
          <h3 class="logo-title" v-show="!isCollapse">考勤管理系统</h3>
          <el-icon v-show="isCollapse"><School /></el-icon>
        </div>
        
        <el-menu
          :default-active="$route.path"
          class="el-menu-vertical"
          :collapse="isCollapse"
          background-color="#304156"
          text-color="#fff"
          active-text-color="#409EFF"
          router
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          
          <el-menu-item index="/attendance">
            <el-icon><Calendar /></el-icon>
            <template #title>考勤管理</template>
          </el-menu-item>
          
          <el-menu-item index="/statistics">
            <el-icon><PieChart /></el-icon>
            <template #title>考勤统计</template>
          </el-menu-item>
          
          <el-menu-item index="/profile">
            <el-icon><User /></el-icon>
            <template #title>个人信息</template>
          </el-menu-item>
          
          <el-menu-item @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            <template #title>退出登录</template>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- 主要内容区 -->
      <el-container class="main-container">
        <el-header class="header">
          <div class="header-left">
            <el-icon 
              class="collapse-btn"
              @click="toggleCollapse"
            >
              <Fold v-if="!isCollapse"/>
              <Expand v-else/>
            </el-icon>
            
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="welcome-text">
            欢迎使用教师考勤管理系统
          </div>
        </el-header>
        
        <el-main class="main">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  HomeFilled,
  Calendar,
  PieChart,
  User,
  SwitchButton,
  Fold,
  Expand,
  School
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const currentRoute = computed(() => {
  switch (route.path) {
    case '/':
      return '首页'
    case '/attendance':
      return '考勤管理'
    case '/statistics':
      return '考勤统计'
    case '/profile':
      return '个人信息'
    default:
      return ''
  }
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = () => {
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
      localStorage.removeItem('token')
      router.push('/login')
    })
    .catch(() => {
      // 用户点击取消，不做任何操作
    })
}
</script>

<style scoped>
.dashboard {
  height: 100vh;
  width: 100%;
  overflow: hidden;
}

.dashboard-container {
  height: 100%;
  width: 100%;
}

.aside {
  height: 100%;
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3648;
  color: #fff;
}

.logo-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.el-menu-vertical {
  border-right: none;
  height: calc(100% - 60px);
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
}

.main-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.header {
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #303133;
}

.collapse-btn:hover {
  color: #409EFF;
}

.welcome-text {
  font-size: 16px;
  color: #606266;
}

.main {
  height: calc(100% - 60px);
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
  box-sizing: border-box;
}

/* 路由切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>