<template>
  <div class="dashboard">
    <h2>概览</h2>
    <div class="stats-grid">
      <div class="stat-card"><span class="stat-num">{{ stats.studentCount }}</span><span class="stat-label">学生总数</span></div>
      <div class="stat-card"><span class="stat-num">{{ stats.examCount }}</span><span class="stat-label">考试次数</span></div>
      <div class="stat-card"><span class="stat-num">{{ stats.avgScore }}</span><span class="stat-label">平均分</span></div>
      <div class="stat-card"><span class="stat-num">{{ stats.pendingGrade }}</span><span class="stat-label">待批改</span></div>
      <div class="stat-card"><span class="stat-num">{{ stats.paperCount }}</span><span class="stat-label">试卷数</span></div>
      <div class="stat-card"><span class="stat-num">{{ stats.questionCount }}</span><span class="stat-label">题目总数</span></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import teacherRequest from '../../api/teacherRequest'

const stats = ref({ studentCount: 0, examCount: 0, avgScore: 0, pendingGrade: 0, paperCount: 0, questionCount: 0 })

onMounted(async () => {
  try {
    const res = await teacherRequest.get('/api/teacher/stats')
    stats.value = res.data
  } catch (e) {}
})
</script>

<style scoped>
h2 { font-size: 22px; font-weight: 700; color: #1d1d1f; margin: 0 0 24px; letter-spacing: -0.02em; }
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.stat-card {
  background: #fff; border-radius: 16px; padding: 28px 24px;
  box-shadow: 0 1px 8px rgba(0,0,0,0.06); display: flex; flex-direction: column; gap: 8px;
}
.stat-num { font-size: 32px; font-weight: 700; color: var(--primary); letter-spacing: -0.02em; }
.stat-label { font-size: 14px; color: #86868b; }
</style>
