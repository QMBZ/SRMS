<template>
  <div class="login-page">
    <div class="login-container">
      <h2 class="login-title">学生学籍管理系统</h2>

      <!-- 登录表单 -->
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入账号" prefix-icon="User" clearable />
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="loginForm.password" placeholder="请输入密码" prefix-icon="Lock" show-password clearable />
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
import { useApi } from '@/utils/request' // 你的封装axios
import { useUserStore } from '@/stores/useUserStore' // pinia

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
      userStore.setLoginInfo({
        token: res.data,
        username: loginForm.username,
      })

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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-container {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}

.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
}
</style>
