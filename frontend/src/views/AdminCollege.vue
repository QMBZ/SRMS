<template>
  <div class="college-admin-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>学院管理员管理</h2>
      <p class="tip">管理学院与管理员的绑定关系，支持绑定、解绑操作</p>
    </div>

    <!-- 功能区 -->
    <div class="tool-box">
      <!-- 学院筛选 + 前置文字 -->
      <div class="search-row">
        <span class="label-text">选择学院：</span>
        <el-select
          v-model="queryCollegeId"
          placeholder="请选择学院筛选"
          clearable
          style="width: 240px"
          @change="getAdminList"
        >
          <el-option
            v-for="item in collegeList"
            :key="item.collegeId"
            :label="item.collegeName"
            :value="item.collegeId"
          />
        </el-select>
      </div>

      <!-- 新增绑定按钮 -->
      <el-button type="primary" @click="openBindDialog">
        <el-icon><Plus /></el-icon>
        新增学院管理员绑定
      </el-button>
    </div>

    <!-- 数据表格 -->
    <div v-loading="loading" element-loading-text="加载中..." class="table-box">
      <el-table :data="adminList" border stripe size="default" header-align="center" align="center">
        <el-table-column label="绑定ID" prop="id" width="100" />
        <el-table-column label="管理员ID" prop="userId" width="120" />
        <el-table-column label="管理员姓名" prop="realName" min-width="120" />
        <el-table-column label="工号" prop="username" min-width="140" />
        <el-table-column label="管理学院" prop="collegeName" min-width="160" />
        <el-table-column label="更新时间" prop="updateTime" width="180" :formatter="formatDateTime" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="danger" link @click="handleUnbind(scope.row)"> 解绑 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空数据提示 -->
      <el-empty v-if="adminList.length === 0 && !loading" description="暂无学院管理员数据" style="margin: 40px 0" />
    </div>

    <!-- 新增绑定弹窗 -->
    <el-dialog v-model="bindDialogVisible" title="新增学院管理员绑定" width="500px" :close-on-click-modal="false">
      <el-form ref="bindFormRef" :model="bindForm" :rules="bindRules" label-width="100px" size="default">
        <el-form-item label="选择学院" prop="collegeId" required>
          <el-select v-model="bindForm.collegeId" placeholder="请选择学院" style="width: 100%" clearable>
            <el-option
              v-for="item in collegeList"
              :key="item.collegeId"
              :label="item.collegeName"
              :value="item.collegeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="工号" prop="username" required>
          <el-input v-model="bindForm.username" placeholder="请输入用户工号/登录账号" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <div class="tip-text">绑定后，该用户角色将自动设置为【学院管理员】(role_id=2)</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="bindDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="btnLoading" @click="handleBind"> 确认绑定 </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useApi } from '@/composables/useApi'
import { formatDate } from '@/utils/date'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user' // pinia

const userStore = useUserStore()
const router = useRouter()

// 请求工具
const { post } = useApi()

// 加载状态
const loading = ref(false)
const btnLoading = ref(false)

// 数据列表
const adminList = ref([])
const collegeList = ref([])

// 查询条件
const queryCollegeId = ref('')

// 弹窗
const bindDialogVisible = ref(false)
const bindFormRef = ref()
const bindForm = reactive({
  username: '',
  collegeId: '',
})

// 表单校验规则
const bindRules = {
  collegeId: [{ required: true, message: '请选择学院', trigger: 'change' }],
  username: [{ required: true, message: '请输入工号/账号', trigger: 'blur' }],
}

// ======================
// 日期格式化
// ======================
const formatDateTime = (row, column) => {
  return formatDate(row.createTime)
}

// ======================
// 1. 页面加载
// ======================
onMounted(() => {
  if (userStore.roleId !== 1) {
    ElMessage.warning('没有此界面的访问权限')
    router.push('/')
    return
  }

  getAllCollegeList()
  getAdminList()
})

// ======================
// 2. 获取所有学院列表
// ======================
const getAllCollegeList = async () => {
  try {
    const res = await post('/college/getAllColleges')
    if (res.code === 200) {
      collegeList.value = res.data || []
    }
  } catch (e) {
    ElMessage.error('获取学院列表失败')
    console.error(e)
  }
}

// ======================
// 3. 获取学院管理员列表
// ======================
const getAdminList = async () => {
  loading.value = true
  try {
    let res
    if (queryCollegeId.value) {
      res = await post('/adminCollege/getByCollegeId', queryCollegeId.value)
    } else {
      res = await post('/adminCollege/getAll')
    }

    if (res.code === 200) {
      const list = res.data || []
      await loadUserNameAndCollegeName(list)
    }
  } catch (e) {
    ElMessage.error('获取管理员列表失败')
    console.error(e)
  } finally {
    loading.value = false
  }
}

// ======================
// 4. 加载用户+学院名称
// ======================
const loadUserNameAndCollegeName = async (list) => {
  try {
    const result = []
    for (let item of list) {
      const userRes = await post('/user/getUserById', item.userId)
      const collegeRes = await post('/college/getCollegeById', item.collegeId)

      result.push({
        ...item,
        realName: userRes.data?.realName || '未知',
        username: userRes.data?.username || '未知',
        collegeName: collegeRes.data?.collegeName || '未知',
      })
    }
    adminList.value = result
  } catch (e) {
    ElMessage.warning('部分名称加载失败')
    adminList.value = list
  }
}

// ======================
// 5. 打开绑定弹窗
// ======================
const openBindDialog = () => {
  // 重置表单
  bindForm.username = ''
  bindForm.collegeId = ''
  bindDialogVisible.value = true
}

// ======================
// 6. 执行绑定
// ======================
const handleBind = async () => {
  const valid = await bindFormRef.value?.validate()
  if (!valid) return

  btnLoading.value = true
  try {
    // 1. 根据用户名/工号查询用户信息
    const userRes = await post('/user/getUserByUsername', bindForm.username)
    if (!userRes.data) {
      ElMessage.error('未找到该用户，请检查工号')
      return
    }
    const user = userRes.data

    // 2. 执行学院管理员绑定
    const bindRes = await post('/adminCollege/add', {
      userId: user.userId,
      collegeId: bindForm.collegeId,
    })

    if (bindRes.code !== 200) {
      ElMessage.error(bindRes.msg || '绑定失败')
      return
    }

    // 3. 更新角色为学院管理员(2) + 启用用户
    user.roleId = 2
    user.status = 1 // 启动
    await post('/user/updateUser', user)

    ElMessage.success('绑定成功！用户已启用并设置为学院管理员')
    bindDialogVisible.value = false
    getAdminList()
  } catch (e) {
    ElMessage.error('绑定操作失败，请检查用户是否存在')
    console.error(e)
  } finally {
    btnLoading.value = false
  }
}

// ======================
// 7. 解绑操作
// ======================
const handleUnbind = async (row) => {
  try {
    await ElMessageBox.confirm('解绑后该用户将被禁用，确定继续吗？', '提示', { type: 'warning' })

    loading.value = true
    // 1. 解绑
    const res = await post('/adminCollege/unbind', {
      userId: row.userId,
      collegeId: row.collegeId,
    })

    if (res.code === 200) {
      // 2. 禁用用户
      const userRes = await post('/user/getUserById', { userId: row.userId })
      if (userRes.data) {
        const user = userRes.data
        user.status = 0 // 禁用
        await post('/user/updateUser', user)
      }

      ElMessage.success('解绑成功！用户已禁用')
      getAdminList()
    } else {
      ElMessage.error(res.msg || '解绑失败')
    }
  } catch (e) {
    ElMessage.info('已取消')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 页面容器 - 子页面样式，不占全屏 */
.college-admin-page {
  padding: 24px;
  background-color: #f5f7fa;
  box-sizing: border-box;
  height: 100%;
}

/* 标题区域 */
.page-header {
  margin-bottom: 20px;
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

/* 功能按钮区 */
.tool-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

/* 搜索行 */
.search-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.label-text {
  font-size: 14px;
  color: #333;
}

/* 表格容器 */
.table-box {
  background: #fff;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 弹窗底部 */
.dialog-footer {
  text-align: right;
}

/* 提示文字 */
.tip-text {
  color: #fa8c16;
  font-size: 13px;
  padding: 8px 12px;
  background: #fff7e6;
  border-radius: 4px;
}
</style>
