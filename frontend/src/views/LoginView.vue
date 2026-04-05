<template>
  <div class="login-page">
    <div class="login-container">
      <h2 class="login-title">学生学籍管理系统</h2>

      <!-- 登录表单 -->
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter.native="handleLogin"
      >
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入账号" :prefix-icon="User" clearable />
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="loginForm.password" placeholder="请输入密码" :prefix-icon="Lock" show-password clearable />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin"> 登 录 </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useApi } from '@/composables/useApi'
import { useUserStore } from '@/stores/user' // pinia
import { getUserId, getRoleId } from '@/utils/jwt'

const router = useRouter()
const { post } = useApi()
const userStore = useUserStore()

// 加载状态
const loading = ref(false)

// 表单
const loginFormRef = ref(null)
const loginForm = reactive({
  username: '',
  password: '',
})

// 校验规则
const loginRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

// =============== 登录提交 ===============
const handleLogin = async () => {
  // 表单校验
  const valid = await loginFormRef.value?.validate()
  if (!valid) return

  loading.value = true
  try {
    // 调用后端登录接口
    const res = await post('/auth/login', {
      username: loginForm.username,
      password: loginForm.password,
    })

    // 接口返回成功（code=200）
    if (res.code === 200) {
      ElMessage.success('登录成功')

      // 保存 token 到 pinia
      const token = res.data
      const userId = getUserId(token)

      userStore.setLoginInfo({
        token: token,
      })

      // 获取用户信息（这个接口要token）
      const userRes = await post('/user/getUserById', userId)
      const userData = userRes.data
      userStore.setLoginInfo({
        token: token,
        ...userData,
      })

      console.log('token', token)
      console.log('user', userData)
      console.log('username', userData.username)

      // 跳转到主页
      router.replace('/')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (err) {
    console.error('登录异常', err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #f0f5ff 0%, #e6edf9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-container {
  width: 400px;
  padding: 32px 32px;
  background: #ffffff;
  border-radius: 8px;
  /* 柔和阴影 */
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
}

.login-title {
  text-align: center;
  margin-bottom: 35px;
  color: #1f2937;
  font-size: 24px;
  font-weight: 500;
  letter-spacing: 1px;
}

.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  margin-top: 10px;
}

/* 输入框统一样式优化 */
:deep(.el-input) {
  --el-input-height: 44px;
}

/* 表单间距优化 */
.el-form-item {
  margin-bottom: 22px;
}
</style>
