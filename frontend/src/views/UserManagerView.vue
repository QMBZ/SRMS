<template>
  <div class="user-management">
    <div class="page-header">
      <h2>用户管理</h2>
      <p class="tip">管理所有用户信息，支持多条件查询、新增、编辑与状态切换</p>
    </div>

    <!-- 查询表单 -->
    <el-card shadow="hover" class="search-card">
      <el-form :model="queryParams" inline @keyup.enter="getList">
        <el-form-item label="学号/工号">
          <el-input v-model="queryParams.username" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="queryParams.realName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="角色类型">
          <el-select v-model="queryParams.roleId" placeholder="选择角色" clearable style="width: 160px">
            <el-option v-for="item in roleList" :key="item.roleId" :label="item.roleName" :value="item.roleId" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号状态">
          <el-select v-model="queryParams.status" placeholder="选择状态" clearable style="width: 140px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="openAdd">新增用户</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="hover" class="table-card" v-loading="loading">
      <el-table :data="tableData" border stripe header-align="center" align="center">
        <el-table-column label="用户ID" prop="userId" width="100" />
        <el-table-column label="学号/工号" prop="username" min-width="160" />
        <el-table-column label="真实姓名" prop="realName" min-width="120" />
        <el-table-column label="角色类型" prop="roleId" width="120">
          <template #default="scope">
            <el-tag type="primary">{{ getRoleName(scope.row.roleId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="账号状态" prop="status" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" prop="updateTime" width="200" :formatter="formatDateTime" />
        <el-table-column label="操作" width="320">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEdit(scope.row)">编辑</el-button>
            <!-- 重置密码按钮：红色 -->
            <el-button type="danger" size="small" @click="resetPwd(scope.row)">重置密码</el-button>
            <!-- 状态按钮颜色修改：启用=success绿色，禁用=warning黄色 -->
            <el-button
              size="small"
              @click="changeStatus(scope.row)"
              :type="scope.row.status === 1 ? 'warning' : 'success'"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="用户信息" width="520px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
        <el-form-item label="学号/工号" prop="username">
          <el-input v-model="form.username" placeholder="请输入" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="角色类型" prop="roleId">
          <el-select v-model="form.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option v-for="item in roleList" :key="item.roleId" :label="item.roleName" :value="item.roleId" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate } from '@/utils/date'
import { useApi } from '@/composables/useApi'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const { post } = useApi()

// 加载状态
const loading = ref(false)

// 分页
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表格数据
const tableData = ref([])

// 角色列表
const roleList = ref([])

// 查询条件
const queryParams = reactive({
  username: '',
  realName: '',
  roleId: '',
  status: '',
})

// 弹窗
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

// 表单数据
const form = reactive({
  userId: '',
  username: '',
  password: '',
  realName: '',
  roleId: '',
  status: 1,
})

// 校验规则：编辑时密码非必填，新增时必填
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

// 获取所有角色
const getAllRoles = async () => {
  try {
    const res = await post('/role/getAllRoles')
    if (res.code === 200) {
      roleList.value = res.data || []
    }
  } catch (err) {
    console.error('获取角色失败：', err)
  }
}

// 根据角色ID获取名称
const getRoleName = (roleId) => {
  const item = roleList.value.find((r) => r.roleId === roleId)
  return item ? item.roleName : '未知角色'
}

// 日期格式化
const formatDateTime = (row) => {
  return formatDate(row.updateTime || row.createTime)
}

// 获取用户列表
const getList = async () => {
  const userEntity = {
    username: queryParams.username?.trim() || null,
    realName: queryParams.realName?.trim() || null,
    roleId: queryParams.roleId ? Number(queryParams.roleId) : null,
    status: queryParams.status !== '' ? Number(queryParams.status) : null,
  }

  loading.value = true
  try {
    const res = await post('/user/getUserByConditionPage', userEntity, {
      params: { pageNum: pageNum.value, pageSize: pageSize.value },
    })

    if (res.code === 200) {
      tableData.value = res.data.list || []
      total.value = res.data.total
    } else {
      console.log(res)
      ElMessage.error(res.msg || '查询失败')
    }
  } catch (err) {
    console.error('查询错误：', err)
    ElMessage.error('网络或接口异常')
  } finally {
    loading.value = false
  }
}

// 分页事件
const handleSizeChange = (val) => {
  pageSize.value = val
  pageNum.value = 1
  getList()
}
const handleCurrentChange = (val) => {
  pageNum.value = val
  getList()
}

// 重置查询
const resetQuery = () => {
  Object.assign(queryParams, {
    username: '',
    realName: '',
    roleId: '',
    status: '',
  })
  pageNum.value = 1
  getList()
}

// 新增/编辑
const openAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const openEdit = async (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  await formRef.value.validate()
  try {
    let res
    if (isEdit.value) {
      res = await post('/user/updateUser', form)
    } else {
      res = await post('/user/addUser', form)
    }
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      getList()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    console.error(e)
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    userId: '',
    username: '',
    password: '',
    realName: '',
    roleId: '',
    status: 1,
  })
  formRef.value?.clearValidate()
}

// 重置密码
const resetPwd = async (row) => {
  try {
    await ElMessageBox.confirm('确定要重置该用户的密码吗？重置后将恢复为默认密码！', '温馨提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    // 只传userId
    const res = await post('/user/resetPassword', row.userId)
    if (res.code === 200) {
      ElMessage.success('密码重置成功')
    } else {
      ElMessage.error(res.msg || '密码重置失败')
    }
  } catch {
    ElMessage.info('已取消重置')
  }
}

// 切换状态（启用/禁用）
const changeStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const text = newStatus === 1 ? '启用' : '禁用'

  try {
    const updateUser = {
      userId: row.userId,
      status: newStatus,
    }
    const res = await post('/user/updateUser', updateUser)
    if (res.code === 200) {
      ElMessage.success(`${text}成功`)
      getList()
    } else {
      ElMessage.error(res.msg || `${text}失败`)
    }
  } catch (err) {
    ElMessage.error('网络异常')
  }
}

// 初始化
onMounted(() => {
  // 权限判断（根据你的业务调整）
  if (userStore.roleId !== 1) {
    ElMessage.warning('没有访问权限')
    return
  }
  getAllRoles()
  getList()
})
</script>

<style scoped>
.user-management {
  width: 100%;
  height: 100%;
  padding: 20px;
  background-color: #f5f7fa;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.page-header {
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
}

.page-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.tip {
  color: #909399;
  margin: 4px 0 0;
  font-size: 13px;
}

.search-card {
  margin-bottom: 16px;
  flex-shrink: 0;
}

.table-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

:deep(.el-table) {
  flex: 1;
}

.pagination {
  margin-top: 12px;
  text-align: right;
  flex-shrink: 0;
}
</style>
