<!-- 提交考勤页面 -->
<template>
  <div class="attendance-container">
    <el-card class="attendance-card">
      
      <template #header>
        <div class="card-header">
          <h2>考勤管理</h2>
          <!-- selectClass是传给后端，让后端通过selectClass查询班级返回班级学生 -->
          <el-select v-model="selectedClass" placeholder="请选择班级" @change="loadStudents">
            <el-option
              v-for="cls in teacherClasses"
              :key="cls"
              :label="cls"
              :value="cls"
            />
          </el-select>
        </div>
      </template>

      <div v-if="selectedClass" class="total-count">
        班级总人数：{{ totalStudents }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;已经考勤次数：{{ totalAttendances }}
      </div>

      <div v-if="selectedClass" class="attendance-input">
        <el-input 
          v-model.number="currentAttendanceId"
          placeholder="请输入本次考勤的次数"
          type="number"
          :min="1"
        />
      </div>

      <el-table v-if="students.length > 0" :data="students" border style="width: 100%">
        <el-table-column prop="stuId" label="学号" width="100" />
        <el-table-column prop="stuName" label="姓名" width="100" />
        <el-table-column prop="courseId" label="课程号" width="100" />
        <el-table-column prop="courseName" label="课程名" width="120" />
        <el-table-column prop="courseTeaId" label="教师ID" width="100" />
        <el-table-column prop="courseTeaName" label="教师姓名" width="100" />
        <el-table-column prop="courseClass" label="班级" width="100" />
        
        <el-table-column label="考勤状态" width="300">
          <template #default="scope">
            <el-radio-group v-model="scope.row.status">
              <el-radio :label="1">出勤</el-radio>
              <el-radio :label="2">缺勤</el-radio>
              <el-radio :label="3">请假</el-radio>
            </el-radio-group>
          </template>
        </el-table-column>
      </el-table>

      <div class="submit-section" v-if="students.length > 0">
        <el-button type="primary" :disabled="!canSubmit" @click="submitAttendance">
          提交考勤
        </el-button>
      </div>

    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'

const selectedClass = ref('')//selectClass是传给后端，让后端通过selectClass查询班级返回班级学生
const students = ref([])
const totalStudents = ref(0)      // 班级总人数
const totalAttendances = ref(0)
const currentAttendanceId = ref('')
const teacherInfo = ref(null)
const teacherClasses = ref([])  // 教师的班级
const currentTime = ref('')

// 更新当前时间
const updateCurrentTime = () => {
  const now = new Date()
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  currentTime.value = `${hours}:${minutes}:${seconds}`
}

// 启动时间更新定时器
let timeInterval
onMounted(() => {
  updateCurrentTime() // 立即更新一次
  timeInterval = setInterval(updateCurrentTime, 1000) // 每秒更新一次
  
  const storedTeacherInfo = localStorage.getItem('teacherInfo')
  if (storedTeacherInfo) {
    teacherInfo.value = JSON.parse(storedTeacherInfo)
  }
  loadTeacherClasses() // 加载教师班级
})

// 清理定时器
onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})

// 加载教师的班级列表
const loadTeacherClasses = async () => {
  try {
    const storedTeacherInfo = localStorage.getItem('teacherInfo')
    if (!storedTeacherInfo) {
      ElMessage.error('未找到教师信息')
      return
    }
    
    const teacherInfo = JSON.parse(storedTeacherInfo)
    const teacherId = teacherInfo.teaId
    if (!teacherId) {
      ElMessage.error('未找到教师ID')
      return
    }
    
    const response = await fetch(`http://localhost:8082/api/teacher/classes/${teacherId}`)
    if (!response.ok) {
      throw new Error('Failed to load teacher classes')
    }
    const data = await response.json()
    teacherClasses.value = data
  } catch (error) {
    ElMessage.error('加载班级列表失败')
    console.error(error)
  }
}



// 加载学生列表和考勤次数
const loadStudents = async () => {
  if (!selectedClass.value) return
  
  try {
    // 从teacherInfo对象中获取teacherId
    const teacherId = teacherInfo.value?.teaId
    if (!teacherId) {
      ElMessage.error('未找到教师信息')
      return
    }

    // 加载学生列表
    const studentsResponse = await fetch(`http://localhost:8082/api/students/class/${teacherId}/${encodeURIComponent(selectedClass.value)}`)
    if (!studentsResponse.ok) {
      const errorText = await studentsResponse.text()
      throw new Error(`Failed to load students: ${errorText}`)
    }
    
    const studentsData = await studentsResponse.json()
    totalStudents.value = studentsData.totalStudents
    students.value = studentsData.students.map(student => ({
      ...student,
      status: null
    }))



    // 加载考勤次数
    if (selectedClass.value) {
      const attendanceResponse = await fetch(
        `http://localhost:8082/attendance/count/${teacherId}/${encodeURIComponent(selectedClass.value)}`
      )
      if (!attendanceResponse.ok) {
        const errorText = await attendanceResponse.text()
        throw new Error(`Failed to load attendance count: ${errorText}`)
      }
      
      totalAttendances.value = await attendanceResponse.json()
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
    console.error('Error loading data:', error)
  }
}







// 检查是否可以提交考勤
const canSubmit = computed(() => {
  return students.value.length > 0 && 
         students.value.every(student => student.status !== null) && 
         currentAttendanceId.value !== ''
})




const submitAttendance = async () => {    // 增加提交  本次考勤是第几次 入数据库sign2中的attendance表
  if (!selectedClass.value || !currentAttendanceId.value) return

  try {
    const now = new Date()
    // 使用本地时间而不是 UTC 时间
    const year = now.getFullYear()
    const month = String(now.getMonth() + 1).padStart(2, '0')
    const day = String(now.getDate()).padStart(2, '0')
    const hours = String(now.getHours()).padStart(2, '0')
    const minutes = String(now.getMinutes()).padStart(2, '0')
    const seconds = String(now.getSeconds()).padStart(2, '0')
    const milliseconds = String(now.getMilliseconds()).padStart(3, '0')
    
    const localDateTime = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}.${milliseconds}`
    
    const attendances = students.value.map(student => ({
      stuId: student.stuId,
      stuName: student.stuName,
      stuClass: selectedClass.value,
      courseId: student.courseId || '',
      courseName: student.courseName || '',
      teaId: teacherInfo.value.teaId || '',
      teaName: teacherInfo.value.teaName,
      stuStatue: student.status,
      attendanceTime: localDateTime.split('.')[0],
      attendanceId: parseInt(currentAttendanceId.value)
    }))
    
    const response = await fetch(`http://localhost:8082/attendance/submit/${teacherInfo.value.teaName}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(attendances)
    })

    if (!response.ok) {
      const errorData = await response.json()
      throw new Error(errorData.message || '考勤提交失败')
    }

    ElMessage.success('考勤提交成功')
    // 重置状态
    students.value = students.value.map(student => ({
      ...student,
      status: null
    }))
  } catch (error) {
    console.error('Error submitting attendance:', error)
    ElMessage.error(error.message || '考勤提交失败')
  }
}
</script>


<style scoped>
.attendance-container {
  padding: 20px;
}

.attendance-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-count {
  margin: 20px 0;
  font-size: 16px;
  font-weight: bold;
}

.attendance-input {
  margin: 20px 0;
}

.submit-section {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.current-time {
  font-size: 16px;
  font-weight: bold;
  color: #409EFF;
}

:deep(.el-radio-group) {
  display: flex;
  justify-content: space-around;
}
</style>