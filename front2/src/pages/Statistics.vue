<!-- 每班的考勤统计页面 -->
<template>
  <div class="statistics-container">
    <el-card class="statistics-card">
      <template #header>
        <div class="card-header">
          <h2>考勤统计</h2>
          <el-select v-model="selectedClass" placeholder="请选择班级" @change="loadStatistics">
            <el-option
              v-for="cls in teacherClasses"
              :key="cls"
              :label="cls"
              :value="cls"
            />
          </el-select>
        </div>
      </template>

      <div v-if="totalAttendances > 0" class="total-attendances">
        已进行 {{ totalAttendances }} 次考勤
      </div>
      
      <el-table :data="statisticsData" v-loading="loading">
        <el-table-column prop="stuId" label="学号" width="120" />
        <el-table-column prop="stuName" label="姓名" width="120" />
        <el-table-column prop="courseId" label="课程ID" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="120" />
        <el-table-column prop="teaId" label="教师ID" width="120" />
        <el-table-column prop="teaName" label="教师姓名" width="120" />
        <el-table-column label="考勤状态">
          <el-table-column prop="presentCount" label="出勤次数" width="100" />
          <el-table-column prop="absentCount" label="缺勤次数" width="100" />
          <el-table-column prop="leaveCount" label="请假次数" width="100" />
          <el-table-column prop="attendanceRate" label="出勤率 (%)" width="100" />   
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const selectedClass = ref('')  // 选中的班级

const statistics = ref({
  present: 0,
  absent: 0,
  leave: 0,
  total: 0
})
const teacherInfo = ref(null)
const teacherClasses = ref([])
let chart = null
const statisticsData = ref([])
const loading = ref(false)
const totalAttendances = ref(0)

onMounted(() => {
  const storedTeacherInfo = localStorage.getItem('teacherInfo')
  if (storedTeacherInfo) {
    teacherInfo.value = JSON.parse(storedTeacherInfo)
    // 直接使用数据库中的班级名称
    teacherClasses.value = [teacherInfo.value.teaClass1, teacherInfo.value.teaClass2]
      .filter(Boolean)
  }
})

const initChart = () => {
  if (chart) {
    chart.dispose()
  }
  
  const chartDom = document.getElementById('attendanceChart')
  if (!chartDom) return
  
  chart = echarts.init(chartDom)
  const option = {
    title: {
      text: `${selectedClass.value} 考勤统计`,
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 'bottom'
    },
    series: [
      {
        name: '考勤状态',
        type: 'pie',
        radius: '50%',
        data: [
          { 
            value: statistics.value.present, 
            name: '出勤',
            itemStyle: { color: '#67C23A' }
          },
          { 
            value: statistics.value.absent, 
            name: '缺勤',
            itemStyle: { color: '#F56C6C' }
          },
          { 
            value: statistics.value.leave, 
            name: '请假',
            itemStyle: { color: '#E6A23C' }
          }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  chart.setOption(option)
}

const loadStatistics = async () => {   // 获取考勤统计
  if (!selectedClass.value || !teacherInfo.value) return
  
  loading.value = true
  try {
    console.log('Fetching statistics for:', {
      teacherId: teacherInfo.value.teaId,
      class: selectedClass.value
    })
    
    const response = await fetch(
      `http://localhost:8082/api/statistics/class?teaId=${encodeURIComponent(teacherInfo.value.teaId)}&className=${encodeURIComponent(selectedClass.value)}`,
      {
        credentials: 'include'
      }
    )
    
    if (!response.ok) {
      const errorText = await response.text()
      console.error('Server response:', errorText)
      throw new Error('获取统计数据失败')
    }
    
    const data = await response.json()
    console.log('Received data:', data)
    
    statisticsData.value = data.statistics
    totalAttendances.value = data.totalAttendances
    
    // 更新统计数据用于图表
    statistics.value = {
      present: data.statistics.reduce((sum, item) => sum + item.presentCount, 0),
      absent: data.statistics.reduce((sum, item) => sum + item.absentCount, 0),
      leave: data.statistics.reduce((sum, item) => sum + item.leaveCount, 0)
    }
    
    // 初始化图表
    nextTick(() => {
      initChart()
    })
  } catch (error) {
    console.error('Error loading statistics:', error)
    ElMessage.error(error.message || '获取统计数据失败')
  } finally {
    loading.value = false
  }
}

// 监听班级选择变化
watch(selectedClass, (newValue) => {
  if (newValue) {
    loadStatistics()
  } else {
    statistics.value = {
      present: 0,
      absent: 0,
      leave: 0,
      total: 0
    }
    if (chart) {
      chart.dispose()
      chart = null
    }
    statisticsData.value = []
    totalAttendances.value = 0
  }
})

// 监听窗口大小变化，调整图表大小
const handleResize = () => {
  if (chart) {
    chart.resize()
  }
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (chart) {
    chart.dispose()
    chart = null
  }
})
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}

.statistics-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-attendances {
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: bold;
  color: #409EFF;
}

.statistics-content {
  margin-top: 20px;
}

.summary-section {
  margin-bottom: 30px;
}

.summary-card {
  text-align: center;
  padding: 20px;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.summary-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.chart-section {
  margin-top: 30px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  padding: 20px;
}

.empty-state {
  padding: 40px 0;
}
</style>