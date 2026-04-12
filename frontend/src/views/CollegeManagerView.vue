<template>
  <div class="college-management">
    <div class="page-header">
      <h2>学院管理</h2>
      <p class="tip">管理所有学院信息，支持名称/编码查询、新增与编辑，可查看学院下所有专业</p>
    </div>

    <!-- 查询表单 -->
    <el-card shadow="hover" class="search-card">
      <el-form :model="queryParams" inline @keyup.enter="getList">
        <el-form-item label="学院名称">
          <el-input v-model="queryParams.collegeName" placeholder="学院名称" clearable />
        </el-form-item>
        <el-form-item label="学院编码">
          <el-input v-model="queryParams.collegeCode" placeholder="2位数字编码" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="openAdd">新增学院</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="hover" class="table-card" v-loading="loading">
      <el-table :data="tableData" border stripe header-align="center" align="center">
        <el-table-column label="学院名称" prop="collegeName" min-width="200" />
        <el-table-column label="学院编码" prop="collegeCode" width="120" />
        <el-table-column label="创建时间" prop="createTime" width="200" :formatter="formatDateTime" />
        <el-table-column label="更新时间" prop="updateTime" width="200" :formatter="formatDateTime" />
        <el-table-column label="操作" width="240">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-button type="success" size="small" @click="openMajorList(scope.row)">查看专业</el-button>
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
    <el-dialog v-model="dialogVisible" title="学院信息" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="学院名称" prop="collegeName">
          <el-input v-model="form.collegeName" placeholder="请输入学院名称" />
        </el-form-item>

        <el-form-item label="学院编码" prop="collegeCode">
          <el-input v-model="form.collegeCode" placeholder="请输入2位数字编码" maxlength="2" @input="handleCodeInput" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认</el-button>
      </template>
    </el-dialog>

    <!-- 查看学院专业弹窗 -->
    <el-dialog v-model="majorDialogVisible" title="学院专业列表" width="800px">
      <div v-loading="majorLoading" class="major-list-box">
        <el-table :data="majorTableData" border stripe header-align="center" align="center" empty-text="该学院暂无专业">
          <el-table-column label="专业名称" prop="majorName" width="240" />
          <el-table-column label="专业编码" prop="majorCode" />
          <el-table-column label="创建时间" prop="createTime" width="200" :formatter="formatDateTime" />
          <el-table-column label="更新时间" prop="updateTime" width="200" :formatter="formatDateTime" />
        </el-table>
      </div>
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
const majorLoading = ref(false)

// 分页
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表格数据
const tableData = ref([])
const majorTableData = ref([])

// 查询条件
const queryParams = reactive({
  collegeName: '',
  collegeCode: '',
})

// 弹窗
const dialogVisible = ref(false)
const majorDialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

// 表单数据
const form = reactive({
  collegeId: '',
  collegeName: '',
  collegeCode: '',
})

// 校验规则
const rules = {
  collegeName: [{ required: true, message: '请输入学院名称', trigger: 'blur' }],
  collegeCode: [
    { required: true, message: '请输入学院编码', trigger: 'blur' },
    { pattern: /^\d{2}$/, message: '编码必须是2位数字', trigger: 'blur' },
  ],
}

// 限制只能输入数字
const handleCodeInput = () => {
  form.collegeCode = form.collegeCode.replace(/\D/g, '')
}

// 日期格式化
const formatDateTime = (row, column) => {
  return formatDate(row[column.property])
}

// 获取学院列表
const getList = async () => {
  const params = {
    collegeName: queryParams.collegeName?.trim() || null,
    collegeCode: queryParams.collegeCode?.trim() || null,
  }

  loading.value = true
  try {
    const res = await post('/college/getCollegesByConditionPage', params, {
      params: { pageNum: pageNum.value, pageSize: pageSize.value },
    })

    if (res.code === 200) {
      tableData.value = res.data.list || []
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
    collegeName: '',
    collegeCode: '',
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
const openEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 提交保存
const submitForm = async () => {
  await formRef.value.validate()
  try {
    let res
    if (isEdit.value) {
      res = await post('/college/updateCollege', form)
    } else {
      res = await post('/college/addCollege', form)
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
    collegeId: '',
    collegeName: '',
    collegeCode: '',
  })
  formRef.value?.clearValidate()
}

// 查看该学院的所有专业
const openMajorList = async (row) => {
  majorDialogVisible.value = true
  majorLoading.value = true
  try {
    const res = await post('/major/getMajorsByCollegeId', row.collegeId)
    if (res.code === 200) {
      majorTableData.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取专业失败')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('获取专业列表异常')
  } finally {
    majorLoading.value = false
  }
}

// 初始化
onMounted(() => {
  if (userStore.roleId !== 1) {
    ElMessage.warning('没有此界面的访问权限')
    router.push('/')
    return
  }
  getList()
})
</script>

<style scoped>
.college-management {
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

.major-list-box {
  max-height: 400px;
  overflow-y: auto;
}
</style>
