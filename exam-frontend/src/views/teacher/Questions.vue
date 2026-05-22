<template>
  <div class="page">
    <div class="page-header">
      <h2>题库管理</h2>
      <el-button type="primary" @click="openAdd">添加题目</el-button>
    </div>
    <div class="filters">
      <el-select v-model="filterSubject" placeholder="科目" clearable style="width: 140px" @change="fetch">
        <el-option v-for="s in subjects" :key="s" :label="s" :value="s" />
      </el-select>
      <el-select v-model="filterType" placeholder="题型" clearable style="width: 140px" @change="fetch">
        <el-option label="单选题" value="CHOICE" />
        <el-option label="多选题" value="MULTI_CHOICE" />
        <el-option label="判断题" value="JUDGE" />
        <el-option label="填空题" value="BLANK" />
        <el-option label="简答题" value="ESSAY" />
      </el-select>
    </div>
    <el-table :data="questions" stripe style="width: 100%" class="apple-table">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="subject" label="科目" width="100" />
      <el-table-column label="题型" width="110">
        <template #default="{ row }">
          <el-tag :type="typeColor(row.type)" size="small">{{ typeLabel(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="题目内容" min-width="280" show-overflow-tooltip />
      <el-table-column prop="score" label="分值" width="70" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑题目' : '添加题目'" width="600px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="科目">
          <el-select v-model="form.subject" style="width: 100%">
            <el-option v-for="s in subjects" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="单选题" value="CHOICE" />
            <el-option label="多选题" value="MULTI_CHOICE" />
            <el-option label="判断题" value="JUDGE" />
            <el-option label="填空题" value="BLANK" />
            <el-option label="简答题" value="ESSAY" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目内容">
          <el-input v-model="form.content" type="textarea" :rows="3" />
        </el-form-item>
        <template v-if="hasOptions(form.type)">
          <el-form-item label="选项A"><el-input v-model="form.optionA" /></el-form-item>
          <el-form-item label="选项B"><el-input v-model="form.optionB" /></el-form-item>
          <el-form-item label="选项C"><el-input v-model="form.optionC" /></el-form-item>
          <el-form-item label="选项D"><el-input v-model="form.optionD" /></el-form-item>
        </template>
        <el-form-item label="正确答案">
          <el-input v-model="form.answer" placeholder="单选题填字母，多选题填字母逗号分隔如A,C" />
        </el-form-item>
        <el-form-item label="分值">
          <el-input-number v-model="form.score" :min="1" :max="30" />
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="form.explanation" type="textarea" :rows="3" placeholder="错题解析，供学生查看" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import teacherRequest from '../../api/teacherRequest'

const questions = ref([])
const subjects = ['Java', '高数', '英语', '数据结构']
const filterSubject = ref('')
const filterType = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ subject: 'Java', type: 'CHOICE', content: '', optionA: '', optionB: '', optionC: '', optionD: '', answer: '', score: 5, explanation: '' })

const hasOptions = (type) => ['CHOICE', 'MULTI_CHOICE'].includes(type)
const typeLabel = (t) => ({ CHOICE: '单选题', MULTI_CHOICE: '多选题', JUDGE: '判断题', BLANK: '填空题', ESSAY: '简答题' }[t] || t)
const typeColor = (t) => ({ CHOICE: 'primary', MULTI_CHOICE: 'success', JUDGE: 'warning', BLANK: 'info', ESSAY: 'danger' }[t] || 'info')

const fetch = async () => {
  try {
    const params = {}
    if (filterSubject.value) params.subject = filterSubject.value
    if (filterType.value) params.type = filterType.value
    const res = await teacherRequest.get('/api/teacher/questions', { params })
    questions.value = res.data
  } catch (e) {}
}

const resetForm = () => {
  form.value = { subject: 'Java', type: 'CHOICE', content: '', optionA: '', optionB: '', optionC: '', optionD: '', answer: '', score: 5, explanation: '' }
}

const openAdd = () => {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  form.value = {
    id: row.id, subject: row.subject, type: row.type, content: row.content,
    optionA: row.optionA || '', optionB: row.optionB || '', optionC: row.optionC || '', optionD: row.optionD || '',
    answer: row.answer, score: row.score, explanation: row.explanation || ''
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    if (isEdit.value) {
      await teacherRequest.put(`/api/teacher/questions/${form.value.id}`, form.value)
    } else {
      await teacherRequest.post('/api/teacher/questions', form.value)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    dialogVisible.value = false
    fetch()
  } catch (e) {}
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该题目？', '确认', { type: 'warning' })
  } catch { return }
  try {
    await teacherRequest.delete(`/api/teacher/questions/${row.id}`)
    ElMessage.success('删除成功')
    fetch()
  } catch (e) {}
}

onMounted(fetch)
</script>

<style scoped>
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
h2 { font-size: 22px; font-weight: 700; color: #1d1d1f; margin: 0; letter-spacing: -0.02em; }
.filters { display: flex; gap: 12px; margin-bottom: 16px; }
</style>
