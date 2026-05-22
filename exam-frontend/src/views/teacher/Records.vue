<template>
  <div class="page">
    <div class="page-header">
      <h2>成绩总览</h2>
    </div>
    <div class="filters">
      <el-select v-model="filterSubject" placeholder="科目" clearable style="width: 140px" @change="fetch">
        <el-option v-for="s in subjects" :key="s" :label="s" :value="s" />
      </el-select>
      <el-input v-model="filterClass" placeholder="班级" clearable style="width: 160px" @change="fetch" />
      <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 140px" @change="fetch">
        <el-option label="已提交" value="SUBMITTED" />
        <el-option label="进行中" value="IN_PROGRESS" />
      </el-select>
    </div>
    <el-table :data="records" stripe style="width: 100%" class="apple-table">
      <el-table-column prop="studentName" label="学生" width="100" />
      <el-table-column prop="studentNo" label="学号" width="130" />
      <el-table-column prop="className" label="班级" width="120" />
      <el-table-column prop="paperTitle" label="试卷" min-width="180" />
      <el-table-column prop="subject" label="科目" width="100" />
      <el-table-column label="成绩" width="120">
        <template #default="{ row }">
          {{ row.score }} / {{ row.totalScore }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'SUBMITTED' ? 'success' : 'warning'" size="small">
            {{ row.status === 'SUBMITTED' ? '已提交' : '进行中' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submitTime" label="提交时间" min-width="170" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="$router.push(`/teacher/grade/${row.recordId}`)">
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import teacherRequest from '../../api/teacherRequest'

const records = ref([])
const subjects = ['Java', '高数', '英语', '数据结构']
const filterSubject = ref('')
const filterClass = ref('')
const filterStatus = ref('')

const fetch = async () => {
  try {
    const params = {}
    if (filterSubject.value) params.subject = filterSubject.value
    if (filterClass.value) params.className = filterClass.value
    if (filterStatus.value) params.status = filterStatus.value
    const res = await teacherRequest.get('/api/teacher/records', { params })
    records.value = res.data
  } catch (e) {}
}

onMounted(fetch)
</script>

<style scoped>
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
h2 { font-size: 22px; font-weight: 700; color: #1d1d1f; margin: 0; letter-spacing: -0.02em; }
.filters { display: flex; gap: 12px; margin-bottom: 16px; }
</style>
