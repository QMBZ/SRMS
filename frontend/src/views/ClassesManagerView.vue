<template>
  <div class="class-management">
    <div class="page-header">
      <h2>班级管理</h2>
      <p class="tip">管理所有班级信息，支持多条件查询、新增与编辑</p>
    </div>

    <!-- 查询表单 -->
    <el-card shadow="hover" class="search-card">
      <el-form :model="queryParams" inline @keyup.enter="getList">
        <el-form-item label="班级名称">
          <el-input v-model="queryParams.className" placeholder="班级名称" clearable />
        </el-form-item>
        <el-form-item label="班级编码">
          <el-input v-model="queryParams.classCode" placeholder="班级编码" clearable />
        </el-form-item>
        <el-form-item label="年级">
          <el-select v-model="queryParams.grade" placeholder="选择年级" clearable style="width: 140px">
            <el-option v-for="year in yearList" :key="year" :label="`${year}级`" :value="year" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属学院">
          <el-select
            v-model="queryParams.collegeId"
            placeholder="请选择学院"
            clearable
            style="width: 160px"
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
        <el-form-item label="所属专业">
          <el-select
            v-model="queryParams.majorId"
            placeholder="请选择专业"
            clearable
            style="width: 160px"
            :disabled="!queryParams.collegeId"
          >
            <el-option v-for="item in majorList" :key="item.majorId" :label="item.majorName" :value="item.majorId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="openAdd">新增班级</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="hover" class="table-card" v-loading="loading">
      <el-table :data="tableData" border stripe header-align="center" align="center">
        <el-table-column label="班级名称" prop="className" min-width="200" />
        <el-table-column label="班级编码" prop="classCode" width="160" />
        <el-table-column label="所属学院" prop="collegeName" width="200" />
        <el-table-column label="所属专业" prop="majorName" width="200" />
        <!-- <el-table-column label="年级" prop="grade" width="100" /> -->
        <el-table-column label="更新时间" prop="updateTime" width="200" :formatter="formatDateTime" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-button type="success" size="small" @click="openStudentList(scope.row)">查看学生</el-button>
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

    <!-- 弹窗 -->
    <el-dialog v-model="dialogVisible" title="班级信息" width="520px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
        <!-- 班级名称：自动生成 只读 -->
        <el-form-item label="班级名称">
          <el-input v-model="form.className" placeholder="自动生成" readonly />
        </el-form-item>

        <!-- 年级选择 -->
        <el-form-item label="年级" prop="grade">
          <el-select
            v-model="form.grade"
            placeholder="请选择年级"
            clearable
            style="width: 100%"
            @change="autoGenerateAll"
          >
            <el-option v-for="year in yearList" :key="year" :label="`${year}级`" :value="year" />
          </el-select>
        </el-form-item>

        <!-- 学院 -->
        <el-form-item label="所属学院" prop="collegeId">
          <el-select
            v-model="form.collegeId"
            placeholder="请选择学院"
            clearable
            style="width: 100%"
            @change="handleCollegeChangeForForm"
          >
            <el-option
              v-for="item in collegeList"
              :key="item.collegeId"
              :label="item.collegeName"
              :value="item.collegeId"
            />
          </el-select>
        </el-form-item>

        <!-- 专业 -->
        <el-form-item label="所属专业" prop="majorId">
          <el-select
            v-model="form.majorId"
            placeholder="请选择专业"
            clearable
            style="width: 100%"
            :disabled="!form.collegeId"
            @change="autoGenerateAll"
          >
            <el-option
              v-for="item in formMajorList"
              :key="item.majorId"
              :label="item.majorName"
              :value="item.majorId"
            />
          </el-select>
        </el-form-item>

        <!-- 班号（只需输入 1-99） -->
        <el-form-item label="班号（1-99）" prop="classNo">
          <el-input
            v-model.number="form.classNo"
            placeholder="请输入班号，如 6"
            maxlength="2"
            @input="autoGenerateAll"
          />
        </el-form-item>

        <!-- 自动生成的班级编码 只读 -->
        <el-form-item label="班级编码">
          <el-input v-model="form.classCode" placeholder="自动生成" readonly />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认</el-button>
      </template>
    </el-dialog>

    <!-- 学生列表弹窗 -->
    <el-dialog v-model="studentDialogVisible" title="班级学生列表" width="700px" @close="resetStudentForm">
      <div v-loading="studentLoading" class="student-table-box">
        <el-table :data="studentList" border stripe header-align="center" align="center" empty-text="该班级暂无学生">
          <el-table-column label="姓名" prop="realName" min-width="100" />
          <el-table-column label="学号" prop="studentNo" min-width="120" />
          <el-table-column label="性别" prop="gender" width="80" />
          <el-table-column label="手机号" prop="phone" min-width="130" />
        </el-table>
      </div>

      <template #footer>
        <el-button @click="studentDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDate } from '@/utils/date'
import { useApi } from '@/composables/useApi'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user' // pinia

const userStore = useUserStore()
const router = useRouter()

const { post } = useApi()

// 加载
const loading = ref(false)

// 分页
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表格
const tableData = ref([])

// 查询条件
const queryParams = reactive({
  className: '',
  classCode: '',
  grade: '',
  collegeId: '',
  majorId: '',
})

// 年级 2000-2050
const yearList = ref([])
for (let i = 2000; i <= 2050; i++) {
  yearList.value.push(i)
}

// 学院、专业列表
const collegeList = ref([])
const majorList = ref([])
const formMajorList = ref([])

// 弹窗
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

// 学生列表相关
const studentDialogVisible = ref(false)
const studentLoading = ref(false)
const studentList = ref([])
const currentClassId = ref('')

const openStudentList = async (row) => {
  if (!row.classId) {
    ElMessage.warning('班级ID不存在')
    return
  }
  currentClassId.value = row.classId
  studentDialogVisible.value = true
  await getStudentList()
}

const getStudentList = async () => {
  if (!currentClassId.value) return
  studentLoading.value = true
  studentList.value = []
  try {
    const res = await post('/student/getStudentInfosByClassId', currentClassId.value)
    if (res.code === 200) {
      studentList.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取学生失败')
    }
  } catch (err) {
    console.error('获取学生错误：', err)
    ElMessage.error('网络异常')
  } finally {
    studentLoading.value = false
  }
}

const resetStudentForm = () => {
  studentList.value = []
  currentClassId.value = ''
}

// 表单数据 自动生成
const form = reactive({
  classId: '',
  className: '', // 专业+年级+班号
  grade: '',
  collegeId: '',
  majorId: '',
  classNo: '', // 1-99
  classCode: '', // 年级+专业编码+班号
})

// 校验规则
const rules = {
  grade: [{ required: true, message: '请选择年级', trigger: 'change' }],
  collegeId: [{ required: true, message: '请选择学院', trigger: 'change' }],
  majorId: [{ required: true, message: '请选择专业', trigger: 'change' }],
  classNo: [
    { required: true, message: '请输入班号', trigger: 'blur' },
    { type: 'number', min: 1, max: 99, message: '班号必须 1-99', trigger: 'blur' },
  ],
}

// 自动生成：班级名称 + 班级编码
const autoGenerateAll = () => {
  const { grade, majorId, classNo } = form
  if (!grade || !majorId || !classNo) return

  // 找到当前专业
  const major = formMajorList.value.find((m) => m.majorId === majorId)
  if (!major) return

  // 生成班级名称：专业名称 + 年级 + 级 + 班号 + 班
  form.className = `${major.majorName}${grade}级${classNo}班`

  // 生成班级编码：年级 + 专业编码 + 两位班号
  const classNoStr = String(classNo).padStart(2, '0')
  form.classCode = `${grade}${major.majorCode}${classNoStr}`
}

// 获取所有学院
const getAllColleges = async () => {
  try {
    const res = await post('/college/getAllColleges')
    if (res.code === 200) {
      collegeList.value = res.data || []
    }
  } catch (err) {
    console.error('获取学院失败：', err)
  }
}

// 根据学院ID获取专业
const getMajorsByCollegeId = async (collegeId) => {
  if (!collegeId) {
    majorList.value = []
    formMajorList.value = []
    return
  }
  try {
    const res = await post('/major/getAllMajors')
    if (res.code === 200) {
      const list = res.data || []
      const filtered = list.filter((item) => item.collegeId === collegeId)
      majorList.value = filtered
      formMajorList.value = filtered
      autoGenerateAll()
    }
  } catch (err) {
    console.error('获取专业失败：', err)
  }
}

// 查询区域学院切换
const handleCollegeChangeForQuery = (val) => {
  queryParams.majorId = ''
  getMajorsByCollegeId(val)
}

// 弹窗学院切换
const handleCollegeChangeForForm = (val) => {
  form.majorId = ''
  form.classNo = ''
  form.className = ''
  form.classCode = ''
  getMajorsByCollegeId(val)
}

// 日期格式化
const formatDateTime = (row, column) => {
  return formatDate(row.updateTime)
}

// 获取列表
const getList = async () => {
  const classesEntity = {
    className: queryParams.className?.trim() || null,
    classCode: queryParams.classCode?.trim() || null,
    grade: queryParams.grade || null,
    collegeId: queryParams.collegeId ? Number(queryParams.collegeId) : null,
    majorId: queryParams.majorId ? Number(queryParams.majorId) : null,
  }

  loading.value = true
  try {
    const res = await post('/classes/getClassesByConditionPage', classesEntity, {
      params: { pageNum: pageNum.value, pageSize: pageSize.value },
    })

    if (res.code === 200) {
      const list = res.data.list
      for (let item of list) {
        const college = collegeList.value.find((c) => c.collegeId === item.collegeId)
        item.collegeName = college ? college.collegeName : '未知学院'

        const major = await getMajorInfo(item.majorId)
        item.majorName = major ? major.majorName : '未知专业'
      }
      tableData.value = list
      total.value = res.data.total
    } else {
      ElMessage.error(res.msg || '查询失败')
    }
  } catch (err) {
    console.error('查询错误：', err)
    ElMessage.error('网络或接口异常')
  } finally {
    loading.value = false
  }
}

const getMajorInfo = async (majorId) => {
  if (!majorId) return null
  try {
    const res = await post('/major/getAllMajors')
    if (res.code === 200) {
      return res.data.find((item) => item.majorId === majorId)
    }
  } catch {}
  return null
}

// 分页
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
    className: '',
    classCode: '',
    grade: '',
    collegeId: '',
    majorId: '',
  })
  majorList.value = []
  pageNum.value = 1
  getList()
}

// 新增
const openAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑回显
const openEdit = async (row) => {
  isEdit.value = true
  Object.assign(form, row)

  // 从编码解析班号
  if (row.classCode) {
    form.classNo = parseInt(row.classCode.slice(-2))
  }

  await getMajorsByCollegeId(row.collegeId)
  autoGenerateAll() // 编辑时也自动刷新名称和编码
  dialogVisible.value = true
}

// 提交
const submitForm = async () => {
  await formRef.value.validate()
  try {
    let res
    if (isEdit.value) {
      res = await post('/classes/updateClass', form)
    } else {
      res = await post('/classes/addClass', form)
    }
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      getList()
    }
  } catch (e) {
    console.error(e)
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    classId: '',
    className: '',
    grade: '',
    collegeId: '',
    majorId: '',
    classNo: '',
    classCode: '',
  })
  formMajorList.value = []
  formRef.value?.clearValidate()
}

onMounted(() => {
  if (userStore.roleId !== 1) {
    ElMessage.warning('没有此界面的访问权限')
    router.push('/')
    return
  }

  getAllColleges()
  getList()
})
</script>

<style scoped>
.class-management {
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

.student-table-box {
  min-height: 300px;
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
