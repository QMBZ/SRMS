// 存储用户信息，不需要频繁请求
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  // 状态
  state: () => ({
    token: '', // JWT token
    userId: 0, // 用户ID user_id
    roleId: 1, // 角色ID role_id

    // 用户表其他信息
    username: '', // 登录账号
    realName: '', // 真实姓名
    status: 1, // 账号状态 1正常 0禁用
  }),

  // 方法：修改状态
  actions: {
    // 1. 登录成功后保存所有信息（接口返回数据直接传入）
    setLoginInfo(data) {
      this.token = data.token
      this.userId = data.userId
      this.roleId = data.roleId

      // 用户表信息
      this.username = data.username
      this.realName = data.realName
      this.status = data.status
    },

    // 2. 退出登录：清空所有信息
    logout() {
      this.$reset()
    },

    // // 3. 单独更新用户信息（比如修改资料后）
    // updateUserInfo(data) {
    //   this.realName = data.realName || this.realName
    //   this.username = data.username || this.username
    // },
  },

  // 持久化：刷新页面不丢失
  persist: true,
})

/**
 * 使用样例
 * const userStore = useUserStore()
 * userStore.setLoginInfo(res.data)
 * if (userStore.roleId === 1)
 */
