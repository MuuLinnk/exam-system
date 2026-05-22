<template>
  <div class="page">
    <div class="page-header">
      <h2>试卷管理</h2>
      <el-button type="primary" @click="openAdd">创建试卷</el-button>
    </div>
    <el-table :data="papers" stripe style="width: 100%" class="apple-table">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="试卷名称" min-width="200" />
      <el-table-column prop="subject" label="科目" width="120" />
      <el-table-column prop="duration" label="时长(分钟)" width="110" />
      <el-table-column prop="totalScore" label="总分" width="80" />
      <el-table-column label="抽题配置" min-width="240">
        <template #default="{ row }">
          <span class="cfg">单选:{{ row.choiceCount }} 多选:{{ row.multiChoiceCount }} 判断:{{ row.judgeCount }} 填空:{{ row.blankCount }} 简答:{{ row.essayCount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑试卷' : '创建试卷'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="120px">
        <el-form-item label="试卷名称">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="form.subject" style="width: 100%">
            <el-option v-for="s in subjects" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试时长(分钟)">
          <el-input-number v-model="form.duration" :min="10" :max="180" />
        </el-form-item>
        <el-form-item label="总分">
          <el-input-number v-model="form.totalScore" :min="10" :max="200" />
        </el-form-item>
        <el-form-item label="单选题数量">
          <el-input-number v-model="form.choiceCount" :min="0" :max="50" />
        </el-form-item>
        <el-form-item label="多选题数量">
          <el-input-number v-model="form.multiChoiceCount" :min="0" :max="50" />
        </el-form-item>
        <el-form-item label="判断题数量">
          <el-input-number v-model="form.judgeCount" :min="0" :max="50" />
        </el-form-item>
        <el-form-item label="填空题数量">
          <el-input-number v-model="form.blankCount" :min="0" :max="50" />
        </el-form-item>
        <el-form-item label="简答题数量">
          <el-input-number v-model="form.essayCount" :min="0" :max="20" />
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

const papers = ref([])
const subjects = ['Java', '高数', '英语', '数据结构']
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ title: '', subject: 'Java', duration: 60, totalScore: 100, choiceCount: 10, multiChoiceCount: 5, judgeCount: 5, blankCount: 5, essayCount: 2 })

const fetch = async () => {
  try {
    const res = await teacherRequest.get('/api/teacher/papers')
    papers.value = res.data
  } catch (e) {}
}

const resetForm = () => {
  form.value = { title: '', subject: 'Java', duration: 60, totalScore: 100, choiceCount: 10, multiChoiceCount: 5, judgeCount: 5, blankCount: 5, essayCount: 2 }
}

const openAdd = () => {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    if (isEdit.value) {
      await teacherRequest.put(`/api/teacher/papers/${form.value.id}`, form.value)
    } else {
      await teacherRequest.post('/api/teacher/papers', form.value)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    fetch()
  } catch (e) {}
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该试卷？', '确认', { type: 'warning' })
  } catch { return }
  try {
    await teacherRequest.delete(`/api/teacher/papers/${row.id}`)
    ElMessage.success('删除成功')
    fetch()
  } catch (e) {}
}

onMounted(fetch)
</script>

<style scoped>
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
h2 { font-size: 22px; font-weight: 700; color: #1d1d1f; margin: 0; letter-spacing: -0.02em; }
.cfg { font-size: 12px; color: #86868b; }
</style>
