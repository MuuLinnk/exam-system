<template>
  <div class="dash">
    <div class="welcome apple-card">
      <h2>{{ studentName }}，欢迎回来</h2>
      <p>每一天的努力都是未来的基石</p>
    </div>

    <div class="stats">
      <div class="stat-card apple-card">
        <div class="stat-num">{{ stats.total }}</div>
        <div class="stat-label">可选考试</div>
      </div>
      <div class="stat-card apple-card">
        <div class="stat-num">{{ stats.done }}</div>
        <div class="stat-label">已完成</div>
      </div>
      <div class="stat-card apple-card">
        <div class="stat-num">{{ stats.avgScore }}</div>
        <div class="stat-label">平均分</div>
      </div>
    </div>

    <div class="quick apple-card">
      <h3>快捷操作</h3>
      <div class="actions">
        <el-button type="primary" @click="$router.push('/exams')">参加考试</el-button>
        <el-button @click="$router.push('/history')" class="secondary-btn">查看历史</el-button>
      </div>
    </div>

    <div class="chart apple-card" v-if="subjectStats.length > 0">
      <h3>各科成绩</h3>
      <div v-for="s in subjectStats" :key="s.subject" class="chart-row">
        <span class="chart-label">{{ s.subject }}</span>
        <div class="chart-bar-bg">
          <div class="chart-bar" :style="{ width: Math.min(s.avg / 100 * 100, 100) + '%', background: barColor(s.avg) }"></div>
        </div>
        <span class="chart-val">{{ s.avg }}分</span>
      </div>
    </div>

    <div class="recent apple-card" v-if="recent.length > 0">
      <h3>最近考试</h3>
      <div v-for="r in recent" :key="r.recordId" class="recent-item">
        <span class="recent-title">{{ r.title }}</span>
        <div class="recent-right">
          <el-tag v-if="r.status==='SUBMITTED'" type="success" size="small">{{ r.score }}分</el-tag>
          <el-tag v-else type="warning" size="small">进行中</el-tag>
          <el-button size="small" @click="r.status==='SUBMITTED'?$router.push('/result/'+r.recordId):$router.push('/exam/'+r.paperId)">
            {{ r.status === 'SUBMITTED' ? '查看' : '继续' }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../api/request'

const studentName = ref(localStorage.getItem('studentName') || '')
const stats = ref({ total: 0, done: 0, avgScore: 0 })
const recent = ref([])
const subjectStats = ref([])

const barColor = (score) => {
  if (score >= 80) return 'var(--success)'
  if (score >= 60) return 'var(--warning)'
  return 'var(--danger)'
}

onMounted(async () => {
  try {
    const res = await request.get('/api/exam/list')
    const list = res.data
    stats.value.total = list.length
    stats.value.done = list.filter(e => e.status === 'SUBMITTED').length
    const scores = list.filter(e => e.score != null).map(e => e.score)
    stats.value.avgScore = scores.length ? Math.round(scores.reduce((a,b)=>a+b,0)/scores.length) : 0
    recent.value = list.filter(e => e.status !== 'NOT_STARTED').slice(0, 3)

    const statsRes = await request.get('/api/exam/stats')
    subjectStats.value = statsRes.data
  } catch (e) {}
})
</script>

<style scoped>
.dash { max-width: 800px; }
.welcome { margin-bottom: 24px; }
.welcome h2 { font-size: 24px; font-weight: 700; letter-spacing: -0.02em; margin-bottom: 4px; }
.welcome p { color: var(--text-secondary); font-size: 15px; }
.stats { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; margin-bottom: 24px; }
.stat-card { text-align: center; padding: 20px; }
.stat-num { font-size: 36px; font-weight: 700; color: var(--primary); letter-spacing: -0.02em; }
.stat-label { margin-top: 4px; color: var(--text-secondary); font-size: 14px; }
.quick { margin-bottom: 24px; }
.quick h3 { font-size: 16px; font-weight: 600; margin-bottom: 14px; }
.actions { display: flex; gap: 10px; }
.secondary-btn { background: var(--border-light); border: none; border-radius: 10px; color: var(--text); }
.chart { margin-bottom: 24px; }
.chart h3 { font-size: 16px; font-weight: 600; margin-bottom: 16px; }
.chart-row { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.chart-label { width: 80px; font-size: 14px; font-weight: 500; }
.chart-bar-bg { flex: 1; height: 16px; background: var(--border-light); border-radius: 8px; overflow: hidden; }
.chart-bar { height: 100%; border-radius: 8px; transition: width 0.5s ease; }
.chart-val { width: 42px; font-size: 14px; font-weight: 600; color: var(--text); text-align: right; }
.recent h3 { font-size: 16px; font-weight: 600; margin-bottom: 12px; }
.recent-item { display: flex; align-items: center; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid var(--border-light); }
.recent-item:last-child { border-bottom: none; }
.recent-title { font-size: 15px; font-weight: 500; }
.recent-right { display: flex; align-items: center; gap: 10px; }
</style>
