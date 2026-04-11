<template>
  <div class="student-management">
    <div class="page-header">
      <h2>学生学籍管理</h2>
      <p class="tip">管理学生学籍信息，支持多条件查询、新增、编辑，学号自动按规则生成</p>
    </div>

    <!-- 查询表单 -->
    <el-card shadow="hover" class="search-card">
      <el-form :model="queryParams" inline @keyup.enter="getList">
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
        <el-form-item label="学籍状态">
          <el-select v-model="queryParams.studentStatus" placeholder="选择状态" clearable style="width: 140px">
            <el-option v-for="item in statusList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="openAdd">新增学生</el-button>
          <el-button type="warning" @click="handleExport">导出学生数据</el-button>
          <el-button type="primary" @click="handleImport">导入学生数据</el-button>
          <el-button type="info" @click="handleExportTemplate">导出模板</el-button>
          <el-button type="success" @click="handleImportAdd">批量添加学生</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="hover" class="table-card" v-loading="loading">
      <el-table :data="tableData" border stripe header-align="center" align="center">
        <el-table-column label="学号" prop="studentNo" min-width="160" />
        <el-table-column label="姓名" prop="realName" min-width="100" />
        <el-table-column label="性别" prop="gender" width="80" />
        <el-table-column label="学院" prop="collegeName" min-width="150" />
        <el-table-column label="专业" prop="majorName" min-width="150" />
        <el-table-column label="班级" prop="className" min-width="160" />
        <el-table-column label="学籍状态" prop="studentStatus" width="110">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.studentStatus)">
              {{ scope.row.studentStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="入学时间" prop="enrollmentTime" width="120" />
        <el-table-column label="操作" width="160">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEdit(scope.row)">编辑</el-button>
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
    <el-dialog v-model="dialogVisible" title="学生学籍信息" width="780px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="自动生成" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="选择性别" style="width: 100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="民族" prop="nation">
              <el-input v-model="form.nation" placeholder="请输入民族" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="籍贯" prop="nativePlace">
              <el-input v-model="form.nativePlace" placeholder="请输入籍贯" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学院" prop="collegeId">
              <el-select
                v-model="form.collegeId"
                placeholder="选择学院"
                style="width: 100%"
                :disabled="isCollegeAdmin"
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
          </el-col>
          <el-col :span="12">
            <el-form-item label="专业" prop="majorId">
              <el-select
                v-model="form.majorId"
                placeholder="选择专业"
                style="width: 100%"
                :disabled="!form.collegeId"
                @change="handleMajorChangeForForm"
              >
                <el-option
                  v-for="item in formMajorList"
                  :key="item.majorId"
                  :label="item.majorName"
                  :value="item.majorId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班级" prop="classId">
              <el-select
                v-model="form.classId"
                placeholder="选择班级"
                style="width: 100%"
                :disabled="!form.majorId"
                @change="handleClassChange"
              >
                <el-option
                  v-for="item in classList"
                  :key="item.classId"
                  :label="item.className"
                  :value="item.classId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班内序号(2位)" prop="studentSeq">
              <el-input
                v-model.number="form.studentSeq"
                placeholder="如30"
                maxlength="2"
                @input="!isEdit && autoGenerateStudentNo()"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 自动生成的年级（只读） -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年级">
              <el-input v-model="form.grade" placeholder="自动获取" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学籍状态" prop="studentStatus">
              <el-select v-model="form.studentStatus" placeholder="选择状态" style="width: 100%">
                <el-option v-for="item in statusList" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入学时间" prop="enrollmentTime">
              <el-date-picker
                v-model="form.enrollmentTime"
                type="date"
                placeholder="选择入学时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="毕业时间">
              <el-date-picker
                v-model="form.graduationTime"
                type="date"
                placeholder="选择毕业时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDate } from '@/utils/date'
import { useApi } from '@/composables/useApi'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()
const { post } = useApi()

// 加载状态
const loading = ref(false)

// 分页
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表格数据
const tableData = ref([])

// 字典数据
const collegeList = ref([])
const majorList = ref([])
const formMajorList = ref([])
const classList = ref([])
const statusList = ref([])

// 角色标记 + 学院管理员管理的学院ID
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

// 弹窗
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

// 学生表单
const form = reactive({
  studentId: '',
  studentNo: '',
  realName: '',
  gender: '',
  idCard: '',
  nation: '',
  nativePlace: '',
  phone: '',
  email: '',
  collegeId: '',
  majorId: '',
  classId: '',
  studentSeq: '',
  grade: '', // 自动生成
  enrollmentTime: '',
  graduationTime: '',
  studentStatus: '在读',
})

// 校验规则
const rules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  collegeId: [{ required: true, message: '请选择学院', trigger: 'change' }],
  majorId: [{ required: true, message: '请选择专业', trigger: 'change' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  studentSeq: [
    { required: true, message: '请输入班内序号', trigger: 'blur' },
    { type: 'number', min: 1, max: 99, message: '序号必须1-99', trigger: 'blur' },
  ],
  studentStatus: [{ required: true, message: '请选择学籍状态', trigger: 'change' }],
  enrollmentTime: [{ required: true, message: '请选择入学时间', trigger: 'change' }],
}

// ====================== 核心接口 ======================
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

const getClassById = async (classId) => {
  try {
    const res = await post('/classes/getClassById', classId)
    return res.code === 200 ? res.data?.className || '未知班级' : '未知班级'
  } catch (e) {
    return '未知班级'
  }
}

// ====================== 新增：获取学院管理员管理的学院 ======================
const getCollegeAdminCollege = async () => {
  try {
    const res = await post('/adminCollege/getByUserId', userStore.userId)
    if (res.code === 200 && res.data) {
      collegeAdminCollegeId.value = res.data.collegeId
      // 自动赋值给查询条件和表单
      queryParams.collegeId = collegeAdminCollegeId.value
      form.collegeId = collegeAdminCollegeId.value
      // 加载该学院下的专业
      getMajorsByCollegeId(collegeAdminCollegeId.value)
    }
  } catch (err) {
    console.error('获取管理员学院失败', err)
    ElMessage.error('获取管理学院信息失败')
  }
}

// ====================== 基础数据获取 ======================
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
    formMajorList.value = []
    return
  }
  try {
    const res = await post('/major/getAllMajors')
    if (res.code === 200) {
      const filtered = res.data.filter((i) => i.collegeId === collegeId)
      majorList.value = filtered
      formMajorList.value = filtered
    }
  } catch (err) {
    console.error('获取专业失败', err)
  }
}

const getClassesByMajorId = async (majorId) => {
  if (!majorId) {
    classList.value = []
    return
  }
  try {
    const res = await post('/classes/getClassesByConditionPage', { majorId }, { params: { pageNum: 1, pageSize: 100 } })
    if (res.code === 200) classList.value = res.data.list || []
  } catch (err) {
    console.error('获取班级失败', err)
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

// ====================== 级联切换 ======================
const handleCollegeChangeForQuery = (val) => {
  queryParams.majorId = ''
  getMajorsByCollegeId(val)
}

const handleCollegeChangeForForm = (val) => {
  form.majorId = ''
  form.classId = ''
  form.studentSeq = ''
  form.studentNo = ''
  form.grade = ''
  classList.value = []
  getMajorsByCollegeId(val)
}

const handleMajorChangeForForm = (val) => {
  form.classId = ''
  form.studentSeq = ''
  form.studentNo = ''
  form.grade = ''
  getClassesByMajorId(val)
  autoGenerateStudentNo()
}

// 班级切换 → 自动生成学号 + 自动设置年级
const handleClassChange = () => {
  // 编辑状态下，不自动生成学号
  if (!isEdit.value) {
    autoGenerateStudentNo()
  }
  autoSetGradeFromClass()
}

// ====================== 自动设置年级（从classCode前4位） ======================
const autoSetGradeFromClass = () => {
  const { classId } = form
  if (!classId) return

  const currentClass = classList.value.find((c) => c.classId === classId)
  if (!currentClass || !currentClass.classCode) return

  // 取班级编码前4位作为年级
  form.grade = currentClass.classCode.substring(0, 4)
}

// ====================== 学号自动生成（班级code + 序号） ======================
const autoGenerateStudentNo = () => {
  const { classId, studentSeq } = form
  if (!classId || !studentSeq) return

  const currentClass = classList.value.find((c) => c.classId === classId)
  if (!currentClass || !currentClass.classCode) return

  const seqStr = String(studentSeq).padStart(2, '0')
  form.studentNo = `${currentClass.classCode}${seqStr}`
}

// ====================== 表格 ======================
const getStatusTagType = (status) => {
  const map = {
    在读: 'success',
    休学: 'warning',
    复学: 'primary',
    保留: 'info',
    毕业: 'success',
    结业: '',
    退学: 'danger',
    开除: 'danger',
  }
  return map[status] || ''
}

const formatDateTime = (row) => formatDate(row.updateTime)

// 获取学生列表
const getList = async () => {
  const params = {
    studentNo: queryParams.studentNo?.trim() || null,
    realName: queryParams.realName?.trim() || null,
    collegeId: queryParams.collegeId ? Number(queryParams.collegeId) : null,
    majorId: queryParams.majorId ? Number(queryParams.majorId) : null,
    studentStatus: queryParams.studentStatus || null,
  }

  loading.value = true
  try {
    const res = await post('/student/getStudentInfoByConditionPage', params, {
      params: { pageNum: pageNum.value, pageSize: pageSize.value },
    })

    if (res.code === 200) {
      let list = res.data.list || []
      tableData.value = await Promise.all(
        list.map(async (item) => ({
          ...item,
          collegeName: await getCollegeById(item.collegeId),
          majorName: await getMajorById(item.majorId),
          className: await getClassById(item.classId),
        })),
      )
      total.value = res.data.total
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
    studentNo: '',
    realName: '',
    majorId: '',
    studentStatus: '',
  })
  // 学院管理员：重置保留自己的学院
  if (isCollegeAdmin.value) {
    queryParams.collegeId = collegeAdminCollegeId.value
  } else {
    queryParams.collegeId = ''
  }
  majorList.value = []
  pageNum.value = 1
  getList()
}

// ====================== 新增/编辑 ======================
const openAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  // 学院管理员：新增自动绑定自己的学院
  if (isCollegeAdmin.value) {
    form.collegeId = collegeAdminCollegeId.value
    getMajorsByCollegeId(collegeAdminCollegeId.value)
  }
}

const openEdit = async (row) => {
  isEdit.value = true
  Object.assign(form, row)
  if (row.studentNo) form.studentSeq = parseInt(row.studentNo.slice(-2))
  await getMajorsByCollegeId(row.collegeId)
  await getClassesByMajorId(row.majorId)
  autoSetGradeFromClass()
  dialogVisible.value = true
}

// 提交
const submitForm = async () => {
  await formRef.value.validate()
  try {
    let res
    if (isEdit.value) {
      res = await post('/student/updateStudentInfo', form)
    } else {
      res = await post('/student/addStudentInfo', form)
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
    studentId: '',
    studentNo: '',
    realName: '',
    gender: '',
    idCard: '',
    nation: '',
    nativePlace: '',
    phone: '',
    email: '',
    collegeId: isCollegeAdmin.value ? collegeAdminCollegeId.value : '', // 学院管理员重置保留学院
    majorId: '',
    classId: '',
    studentSeq: '',
    grade: '',
    enrollmentTime: '',
    graduationTime: '',
    studentStatus: '在读',
  })
  formRef.value?.clearValidate()
}

// 导出学生数据（按当前查询条件）
const handleExport = async () => {
  try {
    loading.value = true
    // 构造和查询一致的条件
    const params = {
      studentNo: queryParams.studentNo?.trim() || null,
      realName: queryParams.realName?.trim() || null,
      collegeId: queryParams.collegeId ? Number(queryParams.collegeId) : null,
      majorId: queryParams.majorId ? Number(queryParams.majorId) : null,
      studentStatus: queryParams.studentStatus || null,
    }

    // 调用后端导出接口（文件流下载）
    const res = await post('/student/export/condition', params, {
      responseType: 'blob', // 必须加，文件下载
    })

    // 下载文件
    const blob = new Blob([res], { type: 'application/vnd.ms-excel' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `学生学籍信息_${new Date().getTime()}.xlsx`
    a.click()
    URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (err) {
    console.error('导出失败', err)
    ElMessage.error('导出失败')
  } finally {
    loading.value = false
  }
}

// 导入学生数据
const handleImport = () => {
  // 创建隐藏文件选择框
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.xlsx,.xls'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (!file) return

    // FormData 包装文件
    const formData = new FormData()
    formData.append('file', file)

    try {
      loading.value = true
      const res = await post('/student/import', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      if (res.code === 200) {
        ElMessage.success(res.msg || '导入成功')
        getList() // 刷新表格
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

// 导出学生新增模板
const handleExportTemplate = async () => {
  try {
    loading.value = true
    const res = await post('/student/export/add/template', {}, { responseType: 'blob' })
    const blob = new Blob([res], { type: 'application/vnd.ms-excel' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `学生信息导入模板.xlsx`
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

// 批量添加学生（导入新增专用）
const handleImportAdd = () => {
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
      const res = await post('/student/import/add', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      if (res.code === 200) {
        ElMessage.success(res.msg || '批量添加成功')
        getList()
      } else {
        ElMessage.error(res.msg || '批量添加失败')
      }
    } catch (err) {
      console.error('批量添加失败', err)
      ElMessage.error('批量添加失败，请检查文件格式')
    } finally {
      loading.value = false
      input.value = ''
    }
  }
  input.click()
}

// ====================== 初始化 ======================
onMounted(async () => {
  // 学生角色拦截
  if (userStore.roleId === 3) {
    ElMessage.warning('无访问权限')
    router.push('/')
    return
  }

  // 标记是否为学院管理员
  isCollegeAdmin.value = userStore.roleId === 2

  // 加载基础数据
  await getAllColleges()
  await getAllStudentStatus()

  // 学院管理员：自动获取并绑定自己的学院
  if (isCollegeAdmin.value) {
    await getCollegeAdminCollege()
  }

  getList()
})
</script>

<style scoped>
.student-management {
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
