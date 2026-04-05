import { createRouter, createWebHistory } from 'vue-router'

import { useUserStore } from '@/stores/user' // pinia

import LoginView from '@/views/LoginView.vue'
import MainView from '@/views/MainView.vue'
import RecordView from '@/views/RecordView.vue'
import AdminCollege from '@/views/AdminCollege.vue'
import HomeView from '@/views/HomeView.vue'
import ProfileView from '@/views/ProfileView.vue'
import ClassesManagerView from '@/views/ClassesManagerView.vue'

const routes = [
  {
    path: '/',
    component: MainView,
    children: [
      { path: '', component: HomeView },
      { path: 'record', component: RecordView },
      { path: 'admin-college', component: AdminCollege },
      { path: 'class-manager', component: ClassesManagerView },
      { path: 'profile', component: ProfileView },
    ],
    meta: { requiresAuth: true },
  },

  {
    path: '/login',
    component: LoginView,
    meta: { requiresAuth: false }, // 不需要登录就能访问
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// =============== 路由守卫：判断是否登录 ===============
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  console.log(userStore)
  const hasToken = !!userStore.token
  console.log('hasToken', hasToken)

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
