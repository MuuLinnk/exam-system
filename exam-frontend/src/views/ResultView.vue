<template>
  <div class="result">
    <div class="result-header apple-card" style="text-align:center">
      <h2>考试提交成功</h2>
      <p v-if="result">试卷总分：{{ result.totalScore }} 分</p>
      <p v-if="result">客观题得分：<span class="big-score">{{ result.objectiveScore }}</span> 分</p>
      <p style="color:var(--text-secondary)">简答题：待批改后更新</p>
    </div>

    <div class="details" v-if="result">
      <div v-for="(d, idx) in result.details" :key="d.questionId" class="detail-item apple-card">
        <div class="d-header">
          <div class="d-header-left">
            <span class="d-num">{{ idx + 1 }}</span>
            <span class="d-type">{{ typeLabel(d.type) }}</span>
            <span class="d-score">{{ d.score }}分</span>
          </div>
          <el-tag v-if="d.correct===true" type="success" size="small">正确</el-tag>
          <el-tag v-else-if="d.correct===false" type="danger" size="small">错误</el-tag>
          <el-tag v-else type="info" size="small">待批改</el-tag>
        </div>

        <div class="q-content">{{ d.content }}</div>

        <!-- 选项 -->
        <div v-if="d.options" class="options-list">
          <div v-for="(label, key) in d.options" :key="key" class="opt-row"
            :class="{
              'opt-correct': isOptCorrect(d, key),
              'opt-wrong': isOptWrong(d, key),
              'opt-missed': isOptMissed(d, key)
            }">
            <span class="opt-badge">{{ key }}</span>
            <span class="opt-label">{{ label }}</span>
          </div>
        </div>

        <div class="answer-section">
          <div class="answer-row">
            <span class="label">你的答案</span>
            <span :class="d.correct===false ? 'ans-wrong' : 'ans-ok'">{{ formatAnswer(d.yourAnswer) || '（未作答）' }}</span>
          </div>
          <div class="answer-row" v-if="d.correct===false || d.correct===null">
            <span class="label">正确答案</span>
            <span class="ans-correct">{{ formatAnswer(d.correctAnswer) }}</span>
          </div>
          <div class="answer-row" v-if="d.remark">
            <span class="label"></span>
            <span class="ans-remark">{{ d.remark }}</span>
          </div>
        </div>
      </div>
    </div>

    <div style="text-align:center;margin-top:24px">
      <el-button type="primary" @click="$router.push('/exams')">返回考试列表</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '../api/request'

const route = useRoute()
const result = ref(null)

const typeLabel = (t) => {
  const map = { CHOICE:'单选题', MULTI_CHOICE:'多选题', JUDGE:'判断题', BLANK:'填空题', ESSAY:'简答题' }
  return map[t] || t
}

const formatAnswer = (ans) => {
  if (!ans) return ''
  if (ans === 'T') return '正确'
  if (ans === 'F') return '错误'
  return ans
}

const isOptCorrect = (d, key) => {
  if (d.type === 'JUDGE') return false
  if (!d.correctAnswer) return false
  const correctSet = new Set(d.correctAnswer.split(','))
  return correctSet.has(key)
}
const isOptWrong = (d, key) => {
  if (d.type === 'JUDGE') return false
  if (d.correct === true) return false
  if (!d.yourAnswer) return false
  const yourSet = new Set(d.yourAnswer.split(','))
  const correctSet = new Set(d.correctAnswer.split(','))
  return yourSet.has(key) && !correctSet.has(key)
}
const isOptMissed = (d, key) => {
  if (d.type === 'JUDGE') return false
  if (d.correct === true) return false
  const correctSet = new Set(d.correctAnswer.split(','))
  const yourSet = new Set(d.yourAnswer ? d.yourAnswer.split(',') : [])
  return correctSet.has(key) && !yourSet.has(key)
}

onMounted(async () => {
  const cached = localStorage.getItem('resultData')
  if (cached) {
    try { result.value = JSON.parse(cached); localStorage.removeItem('resultData'); return } catch (e) {}
  }
  try { const res = await request.get(`/api/exam/record/${route.params.recordId}`); result.value = res.data } catch (e) {}
})
</script>

<style scoped>
.result { max-width: 800px; }
.result-header { margin-bottom: 20px; }
.result-header p { margin: 6px 0; font-size: 15px; }
.big-score { font-size: 36px; font-weight: 700; color: var(--primary); }

.detail-item { margin-bottom: 12px; padding: 20px 24px; }
.d-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 12px;
}
.d-header-left { display: flex; align-items: center; gap: 10px; }
.d-num {
  width: 24px; height: 24px; border-radius: 50%;
  background: rgba(0,122,255,0.1); color: var(--primary);
  display: flex; align-items: center; justify-content: center;
  font-size: 13px; font-weight: 600;
}
.d-type { font-size: 12px; color: var(--text-secondary); }
.d-score { font-size: 12px; color: var(--warning); }

.q-content { font-size: 16px; font-weight: 500; margin-bottom: 12px; line-height: 1.5; }

.options-list { display: flex; flex-direction: column; gap: 4px; margin-bottom: 12px; }
.opt-row {
  display: flex; align-items: baseline; gap: 8px;
  padding: 6px 12px; border-radius: 8px;
  background: var(--border-light); color: var(--text-secondary);
}
.opt-row.opt-correct {
  background: rgba(52,199,89,0.12); color: var(--text);
}
.opt-row.opt-correct .opt-badge {
  background: var(--success); color: #fff;
}
.opt-row.opt-wrong {
  background: rgba(255,59,48,0.1); color: var(--text);
}
.opt-row.opt-wrong .opt-badge {
  background: var(--danger); color: #fff;
}
.opt-row.opt-missed {
  background: rgba(255,149,0,0.1); color: var(--text);
  border: 1px dashed var(--warning);
}
.opt-row.opt-missed .opt-badge {
  background: var(--warning); color: #fff;
}
.opt-badge {
  width: 20px; height: 20px; border-radius: 50%;
  background: var(--text-tertiary); color: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 11px; font-weight: 600; flex-shrink: 0;
}
.opt-label { font-size: 14px; line-height: 1.4; }

.answer-section { border-top: 1px solid var(--border-light); padding-top: 10px; }
.answer-row { margin-bottom: 4px; font-size: 14px; display: flex; gap: 8px; }
.label { color: var(--text-secondary); flex-shrink: 0; }
.ans-ok { color: var(--success); font-weight: 500; }
.ans-wrong { color: var(--danger); font-weight: 500; text-decoration: line-through; }
.ans-correct { color: var(--success); font-weight: 600; }
.ans-remark { color: var(--warning); font-style: italic; }
</style>
