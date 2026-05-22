<template>
  <div class="history">
    <h2>考试历史</h2>
    <div v-if="records.length === 0" class="empty">还没有考试记录，去参加一场吧</div>
    <div v-for="r in records" :key="r.recordId" class="record-card apple-card">
      <div>
        <h4>{{ r.title }} <el-tag size="small" effect="plain" style="margin-left:6px">{{ r.subject }}</el-tag></h4>
        <p>提交时间：{{ r.submitTime }}</p>
      </div>
      <div class="score-col">
        <span class="score">{{ r.score }} 分</span>
        <el-button @click="$router.push('/result/'+r.recordId)" class="secondary-btn" size="small">
          查看详情
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../api/request'

const records = ref([])

onMounted(async () => {
  try {
    const res = await request.get('/api/exam/list')
    const submitted = res.data.filter(e => e.status === 'SUBMITTED')
    for (const item of submitted) {
      try {
        const detail = await request.get(`/api/exam/record/${item.recordId}`)
        records.value.push({
          recordId: item.recordId,
          title: item.title,
          subject: item.subject || '',
          score: item.score,
          submitTime: detail.data?.submitTime || '未知'
        })
      } catch (e) {}
    }
  } catch (e) {}
})
</script>

<style scoped>
.history { max-width: 800px; }
h2 { font-size: 24px; font-weight: 700; letter-spacing: -0.02em; margin-bottom: 20px; }
.empty { text-align: center; color: var(--text-secondary); padding: 60px; font-size: 15px; }
.record-card {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 10px; padding: 18px 24px;
}
.record-card h4 { font-size: 16px; font-weight: 600; margin-bottom: 4px; }
.record-card p { color: var(--text-secondary); font-size: 13px; }
.score-col { text-align: right; display: flex; align-items: center; gap: 12px; }
.score { font-size: 24px; font-weight: 700; color: var(--primary); }
.secondary-btn { background: var(--border-light); border: none; border-radius: 10px; color: var(--text); }
</style>
