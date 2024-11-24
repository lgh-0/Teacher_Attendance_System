<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h2>个人信息</h2>
        </div>
      </template>

      <el-descriptions v-if="teacherInfo" :column="1" border>

        <el-descriptions-item label="工号">
          {{ teacherInfo.teaId }}
        </el-descriptions-item>

        <el-descriptions-item label="姓名">
          {{ teacherInfo.teaName }}
        </el-descriptions-item>

        <el-descriptions-item label="邮箱">
          {{ teacherInfo.teaEmail }}
        </el-descriptions-item>

        <el-descriptions-item label="年龄">
          {{ teacherInfo.teaAge }}
        </el-descriptions-item>

        <el-descriptions-item label="性别">
          {{ teacherInfo.teaGender }}
        </el-descriptions-item>

        <el-descriptions-item label="教授班级">
          <el-tag v-if="teacherInfo.teaClass1" class="class-tag">
            {{ teacherInfo.teaClass1 }}
          </el-tag>

          <el-tag v-if="teacherInfo.teaClass2" class="class-tag" type="success">
            {{ teacherInfo.teaClass2 }}
          </el-tag>
          
        </el-descriptions-item>
      </el-descriptions>

      <div v-else class="no-data">
        <el-empty description="暂无个人信息" />
      </div>
    </el-card>
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const teacherInfo = ref(null)


const loadTeacherInfo = async () => {
  const storedTeacherInfo = localStorage.getItem('teacherInfo')
  if (storedTeacherInfo) {
    const teacher = JSON.parse(storedTeacherInfo)
    try {
      const response = await fetch(`http://localhost:8082/api/profile/${teacher.teaId}`)
      if (!response.ok) {
        throw new Error('获取教师信息失败')
      }
      const data = await response.json()
      teacherInfo.value = data // 将完整的教师信息存储
    } catch (error) {
      ElMessage.error('获取教师信息失败')
    }
  } else {
    ElMessage.warning('未找到本地存储的教师信息')
  }
}


onMounted(() => {
  loadTeacherInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.class-tag {
  margin-right: 10px;
}

.no-data {
  padding: 40px 0;
}

:deep(.el-descriptions__label) {
  width: 120px;
  justify-content: flex-end;
}
</style>