<template>
  <div class="page">
    <div class="page-header">
      <h2>学生管理</h2>
      <el-input v-model="className" placeholder="按班级筛选" clearable style="width: 200px" @change="fetch" />
    </div>
    <el-table :data="students" stripe style="width: 100%" class="apple-table">
      <el-table-column prop="studentNo" label="学号" width="140" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="className" label="班级" width="160" />
      <el-table-column prop="createTime" label="注册时间" min-width="180" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleReset(row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import teacherRequest from '../../api/teacherRequest'

const students = ref([])
const className = ref('')

const fetch = async () => {
  try {
    const params = {}
    if (className.value) params.className = className.value
    const res = await teacherRequest.get('/api/teacher/students', { params })
    students.value = res.data
  } catch (e) {}
}

const handleReset = async (row) => {
  try {
    await ElMessageBox.confirm(`确定将 ${row.name} 的密码重置为 123456？`, '确认', { type: 'warning' })
  } catch { return }
  try {
    await teacherRequest.post(`/api/teacher/students/reset-password/${row.id}`)
    ElMessage.success('密码已重置')
  } catch (e) {}
}

onMounted(fetch)
</script>

<style scoped>
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
h2 { font-size: 22px; font-weight: 700; color: #1d1d1f; margin: 0; letter-spacing: -0.02em; }
</style>
