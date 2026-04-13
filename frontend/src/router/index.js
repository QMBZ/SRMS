import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('@/views/MainView.vue'), // 动态导入
      children: [
        { path: '', component: () => import('@/views/HomeView.vue') },
        { path: 'record', component: () => import('@/views/RecordView.vue') },
        { path: 'admin-college', component: () => import('@/views/AdminCollege.vue') },
        { path: 'class-manager', component: () => import('@/views/ClassesManagerView.vue') },
        { path: 'major-manager', component: () => import('@/views/MajorManagerView.vue') },
        { path: 'college-manager', component: () => import('@/views/CollegeManagerView.vue') },
        { path: 'user-manager', component: () => import('@/views/UserManagerView.vue') },
        { path: 'student-manager', component: () => import('@/views/StudentManagerView.vue') },
        { path: 'profile', component: () => import('@/views/ProfileView.vue') },
        { path: 'score', component: () => import('@/views/ScoreView.vue') },
        { path: 'score-manager', component: () => import('@/views/ScoreManagerView.vue') },
        { path: 'graduate-manager', component: () => import('@/views/GraduateManagerView.vue') },
      ],
      meta: { requiresAuth: true },
    },

    {
      path: '/login',
      component: () => import('@/views/LoginView.vue'), // 动态导入
      meta: { requiresAuth: false },
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const hasToken = !!userStore.token

  if (to.meta.requiresAuth) {
    if (hasToken) {
      next()
    } else {
      next('/login')
    }
  } else {
    next()
  }
})

export default router