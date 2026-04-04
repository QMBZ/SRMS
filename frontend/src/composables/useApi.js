import axios from 'axios'
import router from '@/router/index.js'
import { useUserStore } from '@/stores/user' // pinia

// 创建实例
const instance = axios.create({
  baseURL: '/api',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器 → 自动加 Token
instance.interceptors.request.use((config) => {
  const userStore = useUserStore()
  const token = userStore.token

  // 白名单：不需要token
  const whiteList = ['/auth/login']
  const isWhite = whiteList.some((u) => config.url.includes(u))

  // 有token + 不是白名单 → 自动带上
  if (token && !isWhite) {
    config.headers.Authorization = token // 设置 token 字段
  }

  return config
})

// 响应拦截器 → 自动拆data + 401自动退出
instance.interceptors.response.use(
  (response) => {
    // 自动拆 data
    return response.data
  },

  (error) => {
    // 401：token失效/未登录 → 自动清空 + 跳登录
    if (error.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout() // 清空pinia + 持久化
      ElMessage.error('登录已过期，请重新登录')
      router.replace('/login')
      return new Promise(() => {}) // 中断Promise，不触发上层catch
    }

    // 其他错误统一提示
    const msg = error.message || '请求失败'
    ElMessage.error(msg)

    return Promise.reject(error)
  },
)

// 导出：只暴露 post（你说接口全是POST）
export function useApi() {
  return {
    post: (url, data = {}) => instance.post(url, data),
  }
}

export default instance
