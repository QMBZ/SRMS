<template>
  <div class="graduate-management">
    <div class="page-header">
      <h2>毕业资格审核管理</h2>
      <p class="tip">根据成绩自动统计毕业资格，支持学院/专业筛选，一键准予毕业</p>
    </div>

    <!-- 查询表单 -->
    <el-card shadow="hover" class="search-card">
      <el-form :model="queryParams" inline @keyup.enter="getList">
        <el-form-item label="学院">
          <el-select
            v-model="queryParams.collegeId"
            placeholder="选择学院"
            clearable
            style="width: 180px"
            :disabled="isCollegeAdmin"
            @change="handleCollegeChange"
          >
            <el-option
              v-for="item in collegeList"
              :key="item.collegeId"
              :label="item.collegeName"
              :value="item.collegeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="专业">
          <el-select
            v-model="queryParams.majorId"
            placeholder="选择专业"
            clearable
            style="width: 180px"
            :disabled="!queryParams.collegeId"
          >
            <el-option v-for="item in majorList" :key="item.majorId" :label="item.majorName" :value="item.majorId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 + 纯前端分页 -->
    <el-card shadow="hover" class="table-card" v-loading="loading">
      <el-table :data="pagedTableData" border stripe header-align="center" align="center">
        <el-table-column label="学号" prop="studentNo" min-width="160" />
        <el-table-column label="姓名" prop="realName" min-width="100" />
        <el-table-column label="学院" prop="collegeName" min-width="150" />
        <el-table-column label="专业" prop="majorName" min-width="150" />
        <el-table-column label="未通过数量" width="120">
          <template #default="scope">
            <span :style="{ color: scope.row.failCount > 0 ? '#f56c6c' : '#333' }">
              {{ scope.row.failCount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="scope">
            <el-button
              type="success"
              size="small"
              @click="handleGraduate(scope.row)"
              :disabled="!scope.row.graduateStatus"
            >
              {{ scope.row.graduateStatus ? '准予毕业' : '不予毕业' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 纯前端分页组件 -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleFrontPageChange"
        @current-change="handleFrontPageChange"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useApi } from '@/composables/useApi'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()
const { post } = useApi()

// 加载状态
const loading = ref(false)

// 全部原始数据（存储后端返回的所有数据）
const allTableData = ref([])

// 字典数据
const collegeList = ref([])
const majorList = ref([])

// 角色标记 + 学院管理员管理的学院ID
const isCollegeAdmin = ref(false)
const collegeAdminCollegeId = ref('')

// 查询条件
const queryParams = reactive({
  collegeId: '',
  majorId: '',
})

// ====================== 纯前端分页参数 ======================
const pageNum = ref(1) // 当前页
const pageSize = ref(10) // 每页条数
const total = computed(() => allTableData.value.length) // 总条数

// 前端计算属性：自动分页切片
const pagedTableData = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value
  const end = pageNum.value * pageSize.value
  return allTableData.value.slice(start, end)
})

// ====================== 核心接口（完全没改参数！） ======================
/**
 * 获取毕业资格列表（原样不动，只拿全部数据）
 */
const getList = async () => {
  const params = {
    collegeId: queryParams.collegeId ? Number(queryParams.collegeId) : null,
    majorId: queryParams.majorId ? Number(queryParams.majorId) : null,
  }

  loading.value = true
  try {
    const res = await post('/score/getStudentScoreGraduateList', params)
    if (res.code === 200) {
      allTableData.value = res.data || [] // 存全部数据
      pageNum.value = 1 // 查询/切换条件 → 回到第一页
    } else {
      ElMessage.error(res.msg || '查询失败')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('查询异常')
  } finally {
    loading.value = false
  }
}

/**
 * 准予毕业 → 操作后刷新列表
 */
const handleGraduate = async (row) => {
  try {
    const res = await post('/student/updateStudentInfo', {
      studentId: row.studentId,
      studentStatus: '毕业',
    })
    if (res.code === 200) {
      ElMessage.success('已成功准予毕业！')
      getList()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('操作异常')
  }
}

// ====================== 前端分页切换事件 ======================
const handleFrontPageChange = () => {}

// ====================== 基础数据 ======================
const getAllColleges = async () => {
  try {
    const res = await post('/college/getAllColleges')
    if (res.code === 200) collegeList.value = res.data || []
  } catch (err) {
    console.error('获取学院失败', err)
  }
}

const getMajorsByCollegeId = async (collegeId) => {
  if (!collegeId) {
    majorList.value = []
    return
  }
  try {
    const res = await post('/major/getAllMajors')
    if (res.code === 200) {
      const filtered = res.data.filter((i) => i.collegeId === collegeId)
      majorList.value = filtered
    }
  } catch (err) {
    console.error('获取专业失败', err)
  }
}

// ====================== 级联切换 ======================
const handleCollegeChange = (val) => {
  queryParams.majorId = ''
  getMajorsByCollegeId(val)
}

// ====================== 重置查询 ======================
const resetQuery = () => {
  queryParams.majorId = ''
  if (isCollegeAdmin.value) {
    queryParams.collegeId = collegeAdminCollegeId.value
  } else {
    queryParams.collegeId = ''
  }
  majorList.value = []
  getList()
}

// ====================== 学院管理员自动绑定学院 ======================
const getCollegeAdminCollege = async () => {
  try {
    const res = await post('/adminCollege/getByUserId', userStore.userId)
    if (res.code === 200 && res.data) {
      collegeAdminCollegeId.value = res.data.collegeId
      queryParams.collegeId = collegeAdminCollegeId.value
      getMajorsByCollegeId(collegeAdminCollegeId.value)
    }
  } catch (err) {
    console.error('获取管理员学院失败', err)
  }
}

// ====================== 初始化 ======================
onMounted(async () => {
  // 学生无权限
  if (userStore.roleId === 3) {
    ElMessage.warning('无访问权限')
    router.push('/')
    return
  }

  // 标记是否为学院管理员
  isCollegeAdmin.value = userStore.roleId === 2

  // 加载基础数据
  await getAllColleges()

  // 学院管理员自动绑定
  if (isCollegeAdmin.value) {
    await getCollegeAdminCollege()
  }

  // 加载列表
  getList()
})
</script>

<style scoped>
.graduate-management {
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
