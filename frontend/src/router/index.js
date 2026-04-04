import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import MainView from '@/views/MainView.vue'

const routes = [
  {
    path: '/login',
    component: LoginView,
    meta: { requiresAuth: false }, // 不需要登录就能访问
  },
  {
    path: '/',
    component: MainView,
    meta: { requiresAuth: true }, // 需要登录才能访问
    children: [
      // 你其他子页面写在这里
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// =============== 路由守卫：判断是否登录 ===============
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const hasToken = !!userStore.token

  // 需要登录的页面
  if (to.meta.requiresAuth) {
    if (hasToken) {
      next() // 已登录，放行
    } else {
      next('/login') // 未登录，跳登录
    }
  } else {
    next() // 不需要登录，直接放行
  }
})

export default router
