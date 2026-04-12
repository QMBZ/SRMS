<template>
  <div class="score-management">
    <div class="page-header">
      <h2>学生成绩管理</h2>
      <p class="tip">通过条件筛选学生，查看对应学生所有课程成绩，仅查看不可修改</p>
    </div>

    <!-- 查询表单 -->
    <el-card shadow="hover" class="search-card">
      <el-form :model="queryParams" inline @keyup.enter="getAllStudentThenScore">
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
        <!-- <el-form-item label="学籍状态">
          <el-select v-model="queryParams.studentStatus" placeholder="选择状态" clearable style="width: 140px">
            <el-option v-for="item in statusList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item> -->
        <el-form-item>
          <el-button type="primary" @click="getAllStudentThenScore">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="info" @click="handleExportScoreTemplate">导出模板</el-button>
          <el-button type="success" @click="handleImportScore">导入成绩</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 成绩表格：学号、姓名、学院、专业、课程、成绩 -->
    <el-card shadow="hover" class="table-card" v-loading="loading">
      <el-table :data="scoreListPage" border stripe header-align="center" align="center" empty-text="暂无成绩数据">
        <el-table-column label="学号" prop="studentNo" min-width="160" />
        <el-table-column label="姓名" prop="realName" min-width="100" />
        <el-table-column label="学院" prop="collegeName" min-width="150" />
        <el-table-column label="专业" prop="majorName" min-width="150" />
        <el-table-column label="课程" prop="courseName" min-width="200" />
        <el-table-column label="成绩" prop="score" width="120">
          <template #default="scope">
            <span>{{ parseFloat(scope.row.score).toFixed(2) }}</span>
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

// 全部成绩列表（原始数据）
const scoreListAll = ref([])
// 分页后的成绩列表（展示用）
const scoreListPage = ref([])

// 字典数据
const collegeList = ref([])
const majorList = ref([])
const statusList = ref([])

// 角色标记
const isCollegeAdmin = ref(false)
const collegeAdminCollegeId = ref('')

// 查询条件
const queryParams = reactive({
  studentNo: '',
  realName: '',
  collegeId: '',
  majorId: '',
  studentStatus: '',
})

// 分页参数（现在只控制前端成绩展示分页）
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 基础数据获取
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

const getAllStudentStatus = async () => {
  try {
    const res = await post('/student/getAllStudentStatus')
    if (res.code === 200) statusList.value = res.data || []
  } catch (err) {
    console.error('获取学籍状态失败', err)
  }
}

// 学院切换
const handleCollegeChangeForQuery = (val) => {
  queryParams.majorId = ''
  getMajorsByCollegeId(val)
}

// 查询 所有 学生 → 查成绩 → 前端分页
const getAllStudentThenScore = async () => {
  const params = {
    studentNo: queryParams.studentNo?.trim() || null,
    realName: queryParams.realName?.trim() || null,
    collegeId: queryParams.collegeId ? Number(queryParams.collegeId) : null,
    majorId: queryParams.majorId ? Number(queryParams.majorId) : null,
    studentStatus: '在读',
  }

  loading.value = true
  scoreListAll.value = []
  pageNum.value = 1 // 查询重置到第一页

  try {
    // 一次性查 所有符合条件的学生
    const studentRes = await post('/student/getStudentInfoByConditionPage', params, {
      params: { pageNum: 1, pageSize: 9999 }, // 一次性拉取所有学生
    })

    if (studentRes.code !== 200 || !studentRes.data.list.length) {
      ElMessage.warning('未查询到符合条件的学生')
      scoreListPage.value = []
      total.value = 0
      loading.value = false
      return
    }

    const studentList = studentRes.data.list

    // 批量获取所有学生的成绩
    let finalScoreList = []
    for (let student of studentList) {
      const collegeName = await getCollegeById(student.collegeId)
      const majorName = await getMajorById(student.majorId)

      const scoreRes = await post('/score/getScoresByStudentNo', student.studentNo)
      if (scoreRes.code === 200 && scoreRes.data?.length) {
        const scores = scoreRes.data.map((item) => ({
          ...item,
          studentNo: student.studentNo,
          realName: student.realName,
          collegeName: collegeName,
          majorName: majorName,
        }))
        finalScoreList = [...finalScoreList, ...scores]
      }
    }

    // 保存全部成绩
    scoreListAll.value = finalScoreList
    total.value = finalScoreList.length
    ElMessage.success(`查询到 ${finalScoreList.length} 条成绩`)

    // 前端分页刷新
    refreshPageData()
  } catch (err) {
    console.error('查询异常', err)
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 前端分页刷新
const refreshPageData = () => {
  const start = (pageNum.value - 1) * pageSize.value
  const end = start + pageSize.value
  scoreListPage.value = scoreListAll.value.slice(start, end)
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

// 重置查询
const resetQuery = () => {
  Object.assign(queryParams, {
    studentNo: '',
    realName: '',
    majorId: '',
    studentStatus: '',
  })
  if (isCollegeAdmin.value) {
    queryParams.collegeId = collegeAdminCollegeId.value
  } else {
    queryParams.collegeId = ''
  }
  majorList.value = []
  scoreListAll.value = []
  scoreListPage.value = []
  total.value = 0
}

// 学院管理员数据
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

// 分页切换
const handleSizeChange = (val) => {
  pageSize.value = val
  refreshPageData()
}
const handleCurrentChange = (val) => {
  pageNum.value = val
  refreshPageData()
}

// 导出成绩模板
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

// 导入成绩
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
        getAllStudentThenScore() // 导入后刷新成绩列表
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

// 初始化
onMounted(async () => {
  if (userStore.roleId === 3) {
    ElMessage.warning('无访问权限')
    router.push('/')
    return
  }

  isCollegeAdmin.value = userStore.roleId === 2

  await getAllColleges()
  await getAllStudentStatus()

  if (isCollegeAdmin.value) {
    await getCollegeAdminCollege()
  }

  // getAllStudentThenScore()
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
