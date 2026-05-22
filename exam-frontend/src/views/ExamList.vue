<template>
  <div class="exams">
    <h2>我的考试</h2>
    <div v-if="exams.length === 0" class="empty">暂无考试，休息一下吧</div>
    <div v-for="exam in exams" :key="exam.paperId" class="exam-card apple-card">
      <div class="exam-info">
        <h3>
          {{ exam.title }}
          <el-tag size="small" effect="plain" style="margin-left:8px">{{ exam.subject }}</el-tag>
        </h3>
        <p>时长 {{ exam.duration }} 分钟 &nbsp;·&nbsp; 总分 {{ exam.totalScore }} 分</p>
        <p class="status-line">
          <el-tag v-if="exam.status==='NOT_STARTED'" type="info" effect="plain" size="small">未开始</el-tag>
          <el-tag v-else-if="exam.status==='DOING'" type="warning" effect="plain" size="small">进行中</el-tag>
          <el-tag v-else type="success" effect="plain" size="small">已提交 · {{ exam.score }} 分</el-tag>
        </p>
      </div>
      <div class="exam-actions">
        <el-button v-if="exam.status==='NOT_STARTED'" type="primary" @click="$router.push('/exam/'+exam.paperId)">
          开始考试
        </el-button>
        <el-button v-if="exam.status==='DOING'" type="warning" @click="$router.push('/exam/'+exam.paperId)">
          继续考试
        </el-button>
        <el-button v-if="exam.status==='SUBMITTED'" @click="$router.push('/result/'+exam.recordId)"
          class="secondary-btn">
          查看成绩
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../api/request'

const exams = ref([])
onMounted(async () => {
  try { const res = await request.get('/api/exam/list'); exams.value = res.data } catch (e) {}
})
</script>

<style scoped>
.exams { max-width: 800px; }
h2 { font-size: 24px; font-weight: 700; letter-spacing: -0.02em; margin-bottom: 20px; }
.empty { text-align: center; color: var(--text-secondary); padding: 60px; font-size: 15px; }
.exam-card { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; padding: 20px 24px; }
.exam-info h3 { font-size: 16px; font-weight: 600; margin-bottom: 4px; }
.exam-info p { color: var(--text-secondary); font-size: 13px; }
.status-line { margin-top: 6px !important; }
.secondary-btn { background: var(--border-light); border: none; border-radius: 10px; color: var(--text); }
</style>
