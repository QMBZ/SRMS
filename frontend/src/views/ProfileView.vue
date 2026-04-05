<template>
  <div class="personal-center">
    <div class="page-header">
      <h2>个人中心</h2>
      <p class="tip">管理个人账号信息及安全设置</p>
    </div>

    <div v-loading="loading" element-loading-text="加载个人信息中...">
      <el-card shadow="hover" class="info-card">
        <div class="info-content">
          <!-- 头像 -->
          <div class="avatar-box">
            <el-avatar :size="120" :src="photoUrl"/>
            <p class="status-badge" :class="getRoleClass(userInfo.roleId)">
              {{ mapRoleIdToName(userInfo.roleId) }}
            </p>
          </div>

          <div class="function-area">
            <el-tabs v-model="activeTab">
              <el-tab-pane label="基本资料" name="basic">
                <div class="tab-pane-content">
                  <el-descriptions :column="1" border size="large" label-style="width:140px; font-weight:bold;">
                    <el-descriptions-item label="登录账号">{{ userInfo.username || '暂无' }}</el-descriptions-item>
                    <el-descriptions-item label="真实姓名">{{ userInfo.realName || '暂无' }}</el-descriptions-item>
                    <el-descriptions-item label="所属学院">{{ collegeName || '暂无' }}</el-descriptions-item>
                    <el-descriptions-item label="手机号码">{{ phone || '未填写' }}</el-descriptions-item>
                    <el-descriptions-item label="电子邮箱">{{ email || '未填写' }}</el-descriptions-item>
                    <el-descriptions-item label="账号状态">
                      <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'" size="small">
                        {{ userInfo.status === 1 ? '正常' : '禁用' }}
                      </el-tag>
                    </el-descriptions-item>
                  </el-descriptions>
                  <p class="note-text">* 如需修改联系方式或基本学籍信息，请联系系统管理员。</p>
                </div>
              </el-tab-pane>

              <el-tab-pane label="安全设置" name="security">
                <div class="security-form">
                  <el-form
                      :model="pwdForm"
                      :rules="rules"
                      ref="pwdFormRef"
                      label-position="top"
                  >
                    <el-form-item label="当前密码" prop="oldPassword">
                      <el-input
                          v-model="pwdForm.oldPassword"
                          type="password"
                          show-password
                          placeholder="请输入当前登录密码"
                      />
                    </el-form-item>
                    <el-form-item label="新密码" prop="newPassword">
                      <el-input
                          v-model="pwdForm.newPassword"
                          type="password"
                          show-password
                          placeholder="请输入新密码（6-20位）"
                      />
                    </el-form-item>
                    <el-form-item label="确认新密码" prop="confirmPassword">
                      <el-input
                          v-model="pwdForm.confirmPassword"
                          type="password"
                          show-password
                          placeholder="请再次输入新密码"
                      />
                    </el-form-item>
                    <el-form-item style="margin-top: 30px;">
                      <el-button type="primary" @click="handleUpdatePassword" :loading="submitting">
                        保存修改
                      </el-button>
                      <el-button @click="resetPwdForm">重置</el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {useApi} from '@/composables/useApi'
import {useUserStore} from '@/stores/user'
// 获取roleId
import {getRoleId} from "@/utils/jwt.js";

// ======================
// 1. 初始化数据与 Store
// ======================
const userStore = useUserStore()
const {post} = useApi()
const loading = ref(false)
const submitting = ref(false)
const activeTab = ref('basic')

// 用户综合信息
const userInfo = ref({
  roleId: null,
  username: '',
  realName: '',
  status: 0,
})
const photoUrl = ref('https://api.dicebear.com/9.x/shapes/svg')
const collegeName = ref('暂无')
const phone = ref('暂无')
const email = ref('暂无')
const collegeId = ref()

// 密码表单
const pwdFormRef = ref(null)
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// ======================
// 2. 校验规则
// ======================
const validateConfirm = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [{required: true, message: '请输入当前密码', trigger: 'blur'}],
  newPassword: [
    {required: true, message: '请输入新密码', trigger: 'blur'},
    {min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur'}
  ],
  confirmPassword: [
    {required: true, message: '请确认新密码', trigger: 'blur'},
    {validator: validateConfirm, trigger: 'blur'}
  ]
}

// ======================
// 新增：角色ID映射角色名称的工具函数
// ======================
const mapRoleIdToName = (roleId) => {
  // 定义角色映射关系：1=超级管理员，2=学院管理员，3=学生
  const roleMap = {
    1: '超级管理员',
    2: '学院管理员',
    3: '学生'
  }
  // 匹配不到则返回'暂无'
  return roleMap[roleId] || '暂无'
}

// ======================
// 新增：角色ID映射样式类（对应不同颜色）
// ======================
const getRoleClass = (roleId) => {
  const roleClassMap = {
    1: 'status-super',    // 超级管理员-紫色
    2: 'status-college',  // 学院管理员-橙色
    3: 'status-student'   // 学生-蓝色
  }
  return roleClassMap[roleId] || 'status-student' // 默认学生样式
}

// ======================
// 3. 获取用户信息
// ======================
const getUserProfile = async () => {
  const userNo = userStore.username
  if (!userNo) {
    ElMessage.error('获取用户名失败，请重新登录')
    return
  }

  loading.value = true

  // 从小菠萝中获取用户信息
  userInfo.value = {
    roleId: userStore.roleId,
    username: userStore.username,
    realName: userStore.realName,
    status: userStore.status,
  }

  // 剩下字段通过查询获取

  /*
  * 如果该用户为学生，获取照片，否则使用默认照片
  * 学院，学生通过studentInfo查，系管理为对应学院，校管理直接为学校
  *  */

  try {
    // 当用户不为学生时，只设置学院名称
    // 假设：userInfo是reactive对象，collegeName是ref变量
    // 核心：先转字符串，再替换，空值兜底为''
    collegeName.value = (userInfo.value.realName || '').toString().replace(/管理员$/, '')

    // 超级管理员特殊处理：学院名称直接赋值为学校（补充逻辑）
    if (userInfo.value.roleId === 1) {
      collegeName.value = '本校' // 可根据需求修改为具体学校名，比如'XX大学'
    }

    if (userInfo.value.roleId === 3) {
      // 当访问对象为学生时
      const res = await post('/student/getStudentInfoByStudentNo', userNo)
      if (res.code === 200) {
        photoUrl.value = res.data.photoUrl
        collegeId.value = res.data.collegeId
        phone.value = res.data.phone
        email.value = res.data.email

        // 拿着collegeId去查collegeName
        await getCollegeName(collegeId.value)

        console.log('用户信息', userInfo.value, collegeName.value, phone.value, email.value)
      } else {
        ElMessage.error(res.msg || '获取个人信息失败')
      }
    }

    ElMessage.success('个人信息加载成功')
  } catch (err) {
    console.error('加载失败', err)
  } finally {
    loading.value = false
  }
}

const getCollegeName = async (collegeId) => {
  try {
    // 查学院
    const college = await post('/college/getCollegeById', collegeId)
    if (college.code === 200) collegeName.value = college.data.collegeName
  } catch (e) {
    ElMessage.error('查询名称失败', e)
  }
}

// ======================
// 4. 修改密码逻辑
// ======================
const handleUpdatePassword = () => {
  pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await post('/user/updatePassword', {
          username: userStore.username,
          ...pwdForm
        })
        if (res.code === 200) {
          ElMessage.success('密码修改成功')
          resetPwdForm()
        } else {
          ElMessage.error(res.msg || '修改失败')
        }
      } catch (err) {
        ElMessage.error('网络错误')
      } finally {
        submitting.value = false
      }
    }
  })
}

const resetPwdForm = () => {
  if (pwdFormRef.value) pwdFormRef.value.resetFields()
}

onMounted(() => getUserProfile())
</script>

<style scoped>
/* 深度复用 RecordView.vue 的样式 */
.personal-center {
  padding: 24px;
  background-color: #f5f7fa;
  box-sizing: border-box;
}

.page-header {
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2329;
}

.tip {
  color: #909399;
  margin: 6px 0 0;
  font-size: 14px;
}

.info-card {
  max-width: 1100px;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.info-content {
  display: flex;
  gap: 48px;
  align-items: flex-start;
  padding: 20px 12px;
}

.avatar-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  margin-top: 12px;
  flex-shrink: 0;
}

.status-badge {
  padding: 5px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.status-student {
  background: #e6f7ff;
  color: #1890ff;
}

.status-college {
  background: #fff7e6;
  color: #fa8c16;
}

.status-super {
  background: #e6fffb;
  color: #13c2c2;
}

/* 右侧功能区布局 */
.function-area {
  flex: 1;
  min-width: 0;
}

.tab-pane-content {
  padding-top: 16px;
}

.security-form {
  max-width: 480px;
  padding-top: 16px;
}

.note-text {
  color: #909399;
  font-size: 13px;
  margin-top: 20px;
}

/* 覆盖 ElementPlus 表格样式使其与 RecordView 一致 */
:deep(.el-descriptions-item__cell) {
  padding: 14px 16px !important;
}

:deep(.el-descriptions-item__label) {
  background-color: #fafafa !important;
  color: #4e5969 !important;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  height: 50px;
  line-height: 50px;
}
</style>