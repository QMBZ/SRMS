<template>
  <div class="major-management">
    <div class="page-header">
      <h2>专业管理</h2>
      <p class="tip">管理所有专业信息，支持多条件查询、新增与编辑，专业编码关联学院编码</p>
    </div>

    <!-- 查询表单 -->
    <el-card shadow="hover" class="search-card">
      <el-form :model="queryParams" inline @keyup.enter="getList">
        <el-form-item label="专业名称">
          <el-input v-model="queryParams.majorName" placeholder="专业名称" clearable />
        </el-form-item>
        <el-form-item label="专业编码">
          <el-input v-model="queryParams.majorCode" placeholder="专业编码" clearable />
        </el-form-item>
        <el-form-item label="所属学院">
          <el-select v-model="queryParams.collegeId" placeholder="请选择学院" clearable style="width: 160px">
            <el-option
              v-for="item in collegeList"
              :key="item.collegeId"
              :label="item.collegeName"
              :value="item.collegeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="openAdd">新增专业</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="hover" class="table-card" v-loading="loading">
      <el-table :data="tableData" border stripe header-align="center" align="center">
        <el-table-column label="专业名称" prop="majorName" min-width="200" />
        <el-table-column label="专业编码" prop="majorCode" width="160" />
        <el-table-column label="所属学院" prop="collegeName" width="200" />
        <el-table-column label="学院编码" prop="collegeCode" width="120" />
        <el-table-column label="更新时间" prop="updateTime" width="200" :formatter="formatDateTime" />
        <el-table-column label="操作" width="150">
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
    <el-dialog v-model="dialogVisible" title="专业信息" width="520px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
        <!-- 专业名称 -->
        <el-form-item label="专业名称" prop="majorName">
          <el-input v-model="form.majorName" placeholder="请输入专业名称" />
        </el-form-item>

        <!-- 所属学院（必选） -->
        <el-form-item label="所属学院" prop="collegeId">
          <el-select
            v-model="form.collegeId"
            placeholder="请选择学院"
            clearable
            style="width: 100%"
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

        <!-- 专业编码：学院编码固定 + 自定义后缀 -->
        <el-form-item label="专业编码">
          <div class="code-input-box">
            <el-input
              :model-value="collegeCodePrefix"
              placeholder="学院编码"
              readonly
              style="width: 80px; margin-right: 8px"
            />
            <span class="split">-</span>
            <el-input
              v-model="form.majorSuffix"
              placeholder="后缀"
              maxlength="2"
              style="width: 100px; margin-left: 8px"
              @input="autoGenerateCode"
            />
            <el-input v-model="form.majorCode" placeholder="自动生成" readonly style="flex: 1; margin-left: 12px" />
          </div>
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
import { ElMessage } from 'element-plus'
import { formatDate } from '@/utils/date'
import { useApi } from '@/composables/useApi'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

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

// 查询条件
const queryParams = reactive({
  majorName: '',
  majorCode: '',
  collegeId: '',
})

// 学院列表
const collegeList = ref([])

// 弹窗
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

// 表单数据
const form = reactive({
  majorId: '',
  majorName: '',
  majorCode: '',
  collegeId: '',
  majorSuffix: '', // 专业编码后缀（可编辑）
})

// 学院编码前缀（只读）
const collegeCodePrefix = ref('')

// 校验规则
const rules = {
  majorName: [{ required: true, message: '请输入专业名称', trigger: 'blur' }],
  collegeId: [{ required: true, message: '请选择所属学院', trigger: 'change' }],
}

// ======================
// 自动生成专业编码
// 规则：学院编码 + 自定义后缀（2位）
// ======================
const autoGenerateCode = () => {
  if (!collegeCodePrefix.value || !form.majorSuffix) {
    form.majorCode = ''
    return
  }
  // 自动拼接：学院编码 + 后缀
  form.majorCode = `${collegeCodePrefix.value}${form.majorSuffix}`
}

// ======================
// 切换学院 → 自动加载学院编码
// ======================
const handleCollegeChange = (collegeId) => {
  if (!collegeId) {
    collegeCodePrefix.value = ''
    form.majorSuffix = ''
    form.majorCode = ''
    return
  }

  // 找到选中学院的编码
  const college = collegeList.value.find((item) => item.collegeId === collegeId)
  if (college) {
    collegeCodePrefix.value = college.collegeCode || ''
  } else {
    collegeCodePrefix.value = ''
  }

  // 清空后缀与编码
  form.majorSuffix = ''
  form.majorCode = ''
}

// ======================
// 获取所有学院
// ======================
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

// ======================
// 日期格式化
// ======================
const formatDateTime = (row, column) => {
  return formatDate(row.updateTime)
}

// ======================
// 获取专业列表
// ======================
const getList = async () => {
  const params = {
    majorName: queryParams.majorName?.trim() || null,
    majorCode: queryParams.majorCode?.trim() || null,
    collegeId: queryParams.collegeId ? Number(queryParams.collegeId) : null,
  }

  loading.value = true
  try {
    const res = await post('/major/getMajorsByConditionPage', params, {
      params: { pageNum: pageNum.value, pageSize: pageSize.value },
    })

    if (res.code === 200) {
      const list = res.data.list || []
      // 匹配学院名称与编码
      list.forEach((item) => {
        const college = collegeList.value.find((c) => c.collegeId === item.collegeId)
        item.collegeName = college ? college.collegeName : '未知学院'
        item.collegeCode = college ? college.collegeCode : ''
      })
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
    majorName: '',
    majorCode: '',
    collegeId: '',
  })
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

  // 自动解析专业编码：学院编码固定，后缀可编辑
  const college = collegeList.value.find((item) => item.collegeId === row.collegeId)
  if (college) {
    collegeCodePrefix.value = college.collegeCode || ''
    // 截取后缀：专业编码 - 学院编码长度
    if (row.majorCode && college.collegeCode) {
      form.majorSuffix = row.majorCode.slice(college.collegeCode.length)
    }
  }

  autoGenerateCode()
  dialogVisible.value = true
}

// 提交保存
const submitForm = async () => {
  await formRef.value.validate()
  try {
    let res
    if (isEdit.value) {
      res = await post('/major/updateMajor', form)
    } else {
      res = await post('/major/addMajor', form)
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
    majorId: '',
    majorName: '',
    majorCode: '',
    collegeId: '',
    majorSuffix: '',
  })
  collegeCodePrefix.value = ''
  formRef.value?.clearValidate()
}

// 初始化
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
.major-management {
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

.pagination {
  margin-top: 12px;
  text-align: right;
  flex-shrink: 0;
}

.code-input-box {
  display: flex;
  align-items: center;
}

.split {
  color: #999;
}
</style>
