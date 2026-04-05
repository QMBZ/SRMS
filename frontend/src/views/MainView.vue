<template>
  <div class="srms-layout">
    <el-container style="height: 100vh">
      <!-- 侧边栏 -->
      <el-aside width="220px" class="aside-container">
        <div class="logo-box">
          <h1 class="logo-text">SRMS</h1>
        </div>

        <el-menu
          :default-active="activeMenu"
          mode="vertical"
          background-color="#2f4050"
          text-color="#fff"
          active-text-color="#409eff"
          unique-opened
          class="sidebar-menu"
        >
          <!-- ====================== 超级管理员 roleId = 1 ====================== -->
          <el-sub-menu index="1" v-if="userStore.roleId === 1">
            <template #title>
              <el-icon><Menu /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="1-1">用户管理</el-menu-item>
            <el-menu-item index="1-2" @click="toAdminCollege">学院管理员管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="2" v-if="userStore.roleId === 1">
            <template #title>
              <el-icon><School /></el-icon>
              <span>基础数据</span>
            </template>
            <el-menu-item index="2-1">学院管理</el-menu-item>
            <el-menu-item index="2-2">专业管理</el-menu-item>
            <el-menu-item index="2-3">班级管理</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="3" v-if="userStore.roleId === 1">
            <el-icon><User /></el-icon>
            <span>学生管理</span>
          </el-menu-item>

          <!-- ====================== 学院管理员 roleId = 2 ====================== -->
          <el-menu-item index="4" v-if="userStore.roleId === 2">
            <el-icon><User /></el-icon>
            <span>学生管理</span>
          </el-menu-item>

          <el-sub-menu index="5" v-if="userStore.roleId === 2">
            <template #title>
              <el-icon><School /></el-icon>
              <span>教学管理</span>
            </template>
            <el-menu-item index="5-1">专业管理</el-menu-item>
            <el-menu-item index="5-2">班级管理</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="6" v-if="userStore.roleId === 2">
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>

          <!-- ====================== 学生 roleId = 3 ====================== -->
          <el-menu-item index="7" v-if="userStore.roleId === 3" @click="toProfile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>

          <el-menu-item index="8" v-if="userStore.roleId === 3" @click="toRecord">
            <el-icon><Document /></el-icon>
            <span>我的学籍信息</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 右侧容器 -->
      <el-container>
        <!-- 顶部 -->
        <el-header class="header-container">
          <div class="header-left">
            <span class="page-title">学生学籍管理系统</span>
          </div>
          <div class="header-right">
            <!-- <el-avatar :size="40" src="https://picsum.photos/200" /> -->
            <el-avatar :size="40" src="https://api.dicebear.com/9.x/shapes/svg" />
            <span class="username">{{ userStore.realName || userStore.username }}</span>
            <el-button type="primary" link class="logout-btn" @click="handleLogout">退出登录</el-button>
          </div>
        </el-header>

        <!-- 主内容区 -->
        <el-main class="main-container">
          <div class="content-box">
            <!-- 页面内容 -->
            <RouterView />
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Menu, School, User, UserFilled, Document } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 当前激活菜单（可根据路由自动设置，这里先保留默认）
const activeMenu = ref('1')

/**
 * 退出登录
 * 1. 清空 userStore
 * 2. 跳转到登录页
 */
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
  ElMessage.success('退出成功')
}

// 跳转到学院管理员管理界面
const toAdminCollege = () => {
  activeMenu.value = '1-2'
  router.push('/admin-college')
}

// 跳转到个人信息
const toProfile = () => {
  activeMenu.value = '7'
  router.push('/profile')
}

// 跳转到学籍信息
const toRecord = () => {
  activeMenu.value = '8'
  router.push('/record')
}
</script>

<style scoped>
.srms-layout {
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏 */
.aside-container {
  background-color: #2f4050;
  height: 100%;
}

.logo-box {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #394757;
}

.logo-text {
  color: #fff;
  font-size: 22px;
  font-weight: bold;
  margin: 0;
  letter-spacing: 2px;
}

.sidebar-menu {
  border-right: none;
}

/* 顶部 */
.header-container {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}

.header-left .page-title {
  font-size: 18px;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  font-size: 14px;
  color: #333;
}

.logout-btn {
  color: #f56c6c;
}

/* 主内容 */
.main-container {
  background-color: #f3f3f4;
  padding: 20px;
  overflow: auto;
}

.content-box {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  /* min-height: calc(100% - 40px); */
}
</style>

<style>
/* 全局侧边栏样式 */
.el-menu--vertical {
  border-right: none !important;
}
.el-menu-item {
  color: #fff !important;
}
.el-sub-menu__title {
  color: #fff !important;
}
</style>
