<template>
  <div class="my-student-info">
    <div class="page-header">
      <h2>我的学籍信息</h2>
      <p class="tip">仅可查看本人学籍信息，不可修改</p>
    </div>

    <div v-loading="loading" element-loading-text="加载学籍信息中...">
      <el-card shadow="hover" class="info-card">
        <div class="info-content">
          <!-- 头像 -->
          <div class="avatar-box">
            <el-avatar :size="120" :src="studentInfo.photoUrl" />
            <p class="status-badge" :class="getStatusClass(studentInfo.studentStatus)">
              {{ studentInfo.studentStatus }}
            </p>
          </div>

          <!-- 信息列表 -->
          <div class="info-form">
            <el-descriptions :column="2" border size="large" label-style="width:140px; font-weight:bold;">
              <el-descriptions-item label="学号">{{ studentInfo.studentNo || '暂无' }}</el-descriptions-item>
              <el-descriptions-item label="姓名">{{ studentInfo.realName || '暂无' }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ studentInfo.gender || '未知' }}</el-descriptions-item>

              <!-- 身份证号 + 隐藏/显示开关 -->
              <el-descriptions-item label="身份证号">
                <div class="id-card-row">
                  <span>{{ showIdCard ? studentInfo.idCard : maskIdCard(studentInfo.idCard) }}</span>
                  <el-icon class="icon-btn" @click="showIdCard = !showIdCard" v-if="studentInfo.idCard">
                    <View v-if="!showIdCard" />
                    <Hide v-if="showIdCard" />
                  </el-icon>
                </div>
              </el-descriptions-item>

              <el-descriptions-item label="民族">{{ studentInfo.nation || '汉族' }}</el-descriptions-item>
              <el-descriptions-item label="籍贯">{{ studentInfo.nativePlace || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="手机号">{{ studentInfo.phone || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ studentInfo.email || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="学院">{{ collegeName || '加载中...' }}</el-descriptions-item>
              <el-descriptions-item label="专业">{{ majorName || '加载中...' }}</el-descriptions-item>
              <el-descriptions-item label="班级">{{ className || '加载中...' }}</el-descriptions-item>
              <el-descriptions-item label="入学时间">{{
                formatDate(studentInfo.enrollmentTime) || '暂无'
              }}</el-descriptions-item>
              <el-descriptions-item label="毕业时间">{{
                formatDate(studentInfo.graduationTime) || '未毕业'
              }}</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{
                formatDate(studentInfo.updateTime) || '暂无'
              }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { View, Hide } from '@element-plus/icons-vue'
import { useApi } from '@/composables/useApi'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { formatDate } from '@/utils/date'

const userStore = useUserStore()
const router = useRouter()
const loading = ref(false)
const { post } = useApi()

// 学生信息
const studentInfo = ref({})
// 学院/专业/班级名称
const collegeName = ref('暂无')
const majorName = ref('暂无')
const className = ref('暂无')

// 身份证显示/隐藏控制（默认隐藏）
const showIdCard = ref(false)

// 身份证脱敏格式化（前6后4，中间****）
const maskIdCard = (idCard) => {
  if (!idCard) return '未填写'
  if (idCard.length !== 18) return idCard
  return idCard.slice(0, 6) + '**********' + idCard.slice(-4)
}

// 获取学生本人信息
const getMyInfo = async () => {
  const studentNo = userStore.username
  if (!studentNo) {
    ElMessage.error('获取学号失败，请重新登录')
    return
  }

  loading.value = true
  try {
    const res = await post('/student/getStudentInfoByStudentNo', studentNo)
    if (res.code === 200) {
      studentInfo.value = res.data
      console.log('学生信息:', studentInfo.value)

      // 拿到 ID 后，立即查询 学院/专业/班级 名称
      await getAllNames()
      ElMessage.success('学籍信息加载成功')
    } else {
      ElMessage.error(res.msg || '获取学籍信息失败')
    }
  } catch (err) {
    ElMessage.error('加载失败')
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 批量查询学院、专业、班级名称
const getAllNames = async () => {
  const { collegeId, majorId, classId } = studentInfo.value

  try {
    // 查询学院
    const college = await post('/college/getCollegeById', collegeId)
    if (college.code === 200) collegeName.value = college.data.collegeName

    // 查询专业
    const major = await post('/major/getMajorById', majorId)
    if (major.code === 200) majorName.value = major.data.majorName

    // 查询班级
    const cls = await post('/classes/getClassById', classId)
    if (cls.code === 200) className.value = cls.data.className
  } catch (e) {
    ElMessage.error('查询名称失败', e)
  }
}

// 学籍状态样式
const getStatusClass = (status) => {
  const map = {
    在读: 'status-normal',
    休学: 'status-halt',
    复学: 'status-normal',
    保留: 'status-keep',
    毕业: 'status-graduate',
    结业: 'status-graduate',
    退学: 'status-danger',
    开除: 'status-danger',
  }
  return map[status] || 'status-normal'
}

onMounted(() => {
  if (userStore.roleId !== 3) {
    ElMessage.warning('没有此界面的访问权限')
    router.push('/')
    return
  }

  getMyInfo()
})
</script>

<style scoped>
/* 页面整体容器 */
.my-student-info {
  padding: 24px;
  background-color: #f5f7fa;
  box-sizing: border-box;
}

/* 标题区域 */
.page-header {
  margin-bottom: 24px;
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

/* 卡片主体 */
.info-card {
  max-width: 1100px;
  margin: 0 auto;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

/* 内容布局 */
.info-content {
  display: flex;
  gap: 48px;
  align-items: flex-start;
  padding: 20px 12px;
}

/* 头像区域 */
.avatar-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  margin-top: 12px;
  flex-shrink: 0; /* 防止头像被挤压变形 */
}

.status-badge {
  padding: 5px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.status-normal {
  background: #e6f7ff;
  color: #1890ff;
}
.status-halt {
  background: #fff7e6;
  color: #fa8c16;
}
.status-keep {
  background: #f7f8fa;
  color: #666;
}
.status-graduate {
  background: #e6fffb;
  color: #13c2c2;
}
.status-danger {
  background: #fff2f0;
  color: #ff4d4f;
}

/* 信息表格区域 */
.info-form {
  flex: 1;
  min-width: 0;
}

:deep(.el-descriptions) {
  --el-descriptions-item-label-width: 150px !important;
}

:deep(.el-descriptions-item__cell) {
  padding: 14px 16px !important;
}

:deep(.el-descriptions-item__label) {
  font-weight: 600 !important;
  color: #4e5969 !important;
}

:deep(.el-descriptions-item__content) {
  color: #1f2329 !important;
}

/* 身份证号行样式 */
.id-card-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

/* 图标按钮 */
.icon-btn {
  font-size: 16px;
  color: #1890ff;
  cursor: pointer;
  transition: color 0.2s;
}
.icon-btn:hover {
  color: #40a9ff;
}
</style>
