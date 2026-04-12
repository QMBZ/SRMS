<template>
  <div class="score-management">
    <div class="page-header">
      <h2>学生成绩管理</h2>
      <p class="tip">通过条件筛选学生，点击查看成绩按钮查看该生所有课程成绩，仅查看不可修改</p>
    </div>

    <!-- 查询表单 -->
    <el-card shadow="hover" class="search-card">
      <el-form :model="queryParams" inline @keyup.enter="handleQuery">
        <el-form-item label="学号">
          <el-input v-model="queryParams.studentNo" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.realName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="学院">
          <el-select
            v-model="queryParams.collegeId"
            placeholder="选择学院"
            clearable
            style="width: 160px"
            :disabled="isCollegeAdmin"
            @change="handleCollegeChangeForQuery"
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
            style="width: 160px"
            :disabled="!queryParams.collegeId"
          >
            <el-option v-for="item in majorList" :key="item.majorId" :label="item.majorName" :value="item.majorId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="info" @click="handleExportScoreTemplate">导出模板</el-button>
          <el-button type="success" @click="handleImportScore">导入成绩</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 学生列表表格（后端分页） -->
    <el-card shadow="hover" class="table-card" v-loading="loading">
      <el-table :data="studentList" border stripe header-align="center" align="center" empty-text="暂无学生数据">
        <el-table-column label="学号" prop="studentNo" min-width="160" />
        <el-table-column label="姓名" prop="realName" min-width="100" />
        <el-table-column label="学院" prop="collegeName" min-width="150" />
        <el-table-column label="专业" prop="majorName" min-width="150" />
        <el-table-column label="操作" width="140">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openScoreDialog(scope.row)"> 查看成绩 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 后端分页 -->
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        class="pagination"
      />
    </el-card>

    <!-- 成绩弹窗 -->
    <el-dialog
      v-model="scoreDialogVisible"
      :title="`${scoreDialogTitle} - 课程成绩列表`"
      width="560px"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-table :data="studentScoreList" border stripe header-align="center" align="center" empty-text="该生暂无成绩">
        <el-table-column label="课程名称" prop="courseName" min-width="280" />
        <el-table-column label="成绩" prop="score" min-width="280">
          <template #default="scope">
            <span>{{ parseFloat(scope.row.score).toFixed(2) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
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

// 学生列表（后端分页）
const studentList = ref([])
const total = ref(0)

// 字典数据
const collegeList = ref([])
const majorList = ref([])

// 角色标记
const isCollegeAdmin = ref(false)
const collegeAdminCollegeId = ref('')

// 查询条件 + 分页参数
const queryParams = reactive({
  studentNo: '',
  realName: '',
  collegeId: '',
  majorId: '',
  studentStatus: '在读',
  pageNum: 1,
  pageSize: 10,
})

// ================== 成绩弹窗相关 ==================
const scoreDialogVisible = ref(false)
const scoreDialogTitle = ref('')
const studentScoreList = ref([])

// 打开成绩弹窗
const openScoreDialog = async (row) => {
  scoreDialogVisible.value = true
  scoreDialogTitle.value = `${row.realName} (${row.studentNo})`
  studentScoreList.value = []

  try {
    const res = await post('/score/getScoresByStudentNo', row.studentNo)
    if (res.code === 200 && res.data) {
      studentScoreList.value = res.data
    }
  } catch (err) {
    console.error('获取成绩失败', err)
    ElMessage.error('获取成绩失败')
  }
}

// ================== 基础数据 ==================
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

// 根据ID获取名称
const getCollegeById = async (collegeId) => {
  try {
    const res = await post('/college/getCollegeById', collegeId)
    return res.code === 200 ? res.data?.collegeName || '未知学院' : '未知学院'
  } catch (e) {
    return '未知学院'
  }
}
const getMajorById = async (majorId) => {
  try {
    const res = await post('/major/getMajorById', majorId)
    return res.code === 200 ? res.data?.majorName || '未知专业' : '未知专业'
  } catch (e) {
    return '未知专业'
  }
}

// 学院切换
const handleCollegeChangeForQuery = (val) => {
  queryParams.majorId = ''
  getMajorsByCollegeId(val)
}

// ================== 后端分页查询学生 ==================
const handleQuery = async () => {
  const params = {
    studentNo: queryParams.studentNo?.trim() || null,
    realName: queryParams.realName?.trim() || null,
    collegeId: queryParams.collegeId ? Number(queryParams.collegeId) : null,
    majorId: queryParams.majorId ? Number(queryParams.majorId) : null,
    studentStatus: queryParams.studentStatus,
  }

  loading.value = true
  try {
    const res = await post('/student/getStudentInfoByConditionPage', params, {
      params: {
        pageNum: queryParams.pageNum,
        pageSize: queryParams.pageSize,
      },
    })
    if (res.code === 200) {
      let list = res.data.list || []
      // 修复：给每个学生赋值学院名称、专业名称
      for (let item of list) {
        item.collegeName = await getCollegeById(item.collegeId)
        item.majorName = await getMajorById(item.majorId)
      }
      studentList.value = list
      total.value = res.data.total || 0
    }
  } catch (err) {
    console.error('查询学生失败', err)
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// ================== 重置查询 ==================
const resetQuery = () => {
  Object.assign(queryParams, {
    studentNo: '',
    realName: '',
    majorId: '',
    studentStatus: '在读',
    pageNum: 1,
    pageSize: 10,
  })
  if (isCollegeAdmin.value) {
    queryParams.collegeId = collegeAdminCollegeId.value
  } else {
    queryParams.collegeId = ''
  }
  majorList.value = []
  studentList.value = []
  total.value = 0
}

// ================== 学院管理员 ==================
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

// ================== 导出/导入 ==================
const handleExportScoreTemplate = async () => {
  try {
    loading.value = true
    const res = await post('/score/exportScoreTemplate', {}, { responseType: 'blob' })
    const blob = new Blob([res], { type: 'application/vnd.ms-excel' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `成绩导入模板.xlsx`
    a.click()
    URL.revokeObjectURL(url)
    ElMessage.success('模板导出成功')
  } catch (err) {
    console.error('导出模板失败', err)
    ElMessage.error('导出模板失败')
  } finally {
    loading.value = false
  }
}

const handleImportScore = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.xlsx,.xls'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (!file) return

    const formData = new FormData()
    formData.append('file', file)

    try {
      loading.value = true
      const res = await post('/score/importScore', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      if (res.code === 200) {
        ElMessage.success(res.msg || '成绩导入成功')
        handleQuery()
      } else {
        ElMessage.error(res.msg || '导入失败')
      }
    } catch (err) {
      console.error('导入失败', err)
      ElMessage.error('导入失败，请检查文件格式')
    } finally {
      loading.value = false
      input.value = ''
    }
  }
  input.click()
}

// ================== 初始化 ==================
onMounted(async () => {
  if (userStore.roleId === 3) {
    ElMessage.warning('无访问权限')
    router.push('/')
    return
  }

  isCollegeAdmin.value = userStore.roleId === 2

  await getAllColleges()

  if (isCollegeAdmin.value) {
    await getCollegeAdminCollege()
  }

  handleQuery()
})
</script>

<style scoped>
.score-management {
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
  overflow: hidden;
}

:deep(.el-table) {
  height: 100%;
}
</style>
