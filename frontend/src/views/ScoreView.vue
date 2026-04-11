<template>
  <div class="score-page">
    <div class="page-header">
      <h2>我的成绩</h2>
      <p class="tip">查看本人所有课程成绩信息</p>
    </div>

    <!-- 成绩表格（无筛选、无操作，纯展示） -->
    <el-card shadow="hover" v-loading="loading">
      <el-table :data="scoreList" border stripe header-align="center" align="center" empty-text="暂无成绩数据">
        <el-table-column label="序号" type="index" width="80" />
        <el-table-column label="课程名称" prop="courseName" min-width="200" />
        <el-table-column label="成绩" prop="score" width="120">
          <template #default="scope">
            <!-- 成绩保留两位小数展示 -->
            <span>{{ parseFloat(scope.row.score).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="录入时间" prop="createTime" width="220" :formatter="formatDateTime" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDate } from '@/utils/date'
import { useApi } from '@/composables/useApi'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 工具和状态引入
const userStore = useUserStore()
const router = useRouter()
const { post } = useApi()

// 加载状态
const loading = ref(false)

// 成绩列表数据
const scoreList = ref([])

// ======================
// 日期格式化（复用项目工具）
// ======================
const formatDateTime = (row, column) => {
  return formatDate(row[column.property])
}

// ======================
// 获取当前学生的成绩
// ======================
const getMyScores = async () => {
  // 从用户信息中获取当前登录学生的学号
  const studentNo = userStore.username
  if (!studentNo) {
    ElMessage.error('未获取到学号信息')
    return
  }

  loading.value = true
  try {
    // 调用你提供的后端接口
    const res = await post('/score/getScoresByStudentNo', studentNo)

    if (res.code === 200) {
      scoreList.value = res.data || []
      ElMessage.success('成绩查询成功')
    } else {
      ElMessage.error(res.msg || '成绩查询失败')
    }
  } catch (err) {
    console.error('获取成绩异常：', err)
    ElMessage.error('网络或接口异常')
  } finally {
    loading.value = false
  }
}

// ======================
// 页面初始化 + 权限校验
// ======================
onMounted(() => {
  // 权限校验：仅 roleId == 3 的学生可访问
  if (userStore.roleId !== 3) {
    ElMessage.warning('无成绩页面访问权限')
    router.push('/')
    return
  }

  // 有权限则加载成绩
  getMyScores()
})
</script>

<style scoped>
.score-page {
  width: 100%;
  height: 100%;
  padding: 20px;
  background-color: #f5f7fa;
  box-sizing: border-box;
}

.page-header {
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e8e8e8;
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

/* 表格卡片样式 */
.el-card {
  flex: 1;
}
</style>
