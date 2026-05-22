<template>
  <div class="wrong-book">
    <h2>错题本</h2>
    <div v-if="list.length === 0" class="empty">没有错题，太棒了！</div>

    <div v-for="(item, idx) in list" :key="idx" class="wrong-item apple-card">
      <div class="subject-tag">
        <el-tag size="small" effect="plain" type="info">{{ item.subject }}</el-tag>
        <el-tag size="small" effect="plain" style="margin-left:6px">{{ item.paperTitle }}</el-tag>
        <el-tag size="small" effect="plain" type="warning" style="margin-left:6px">{{ typeLabel(item.type) }}</el-tag>
      </div>
      <div class="content">{{ item.content }}</div>
      <div v-if="item.options" class="options-display">
        <span v-for="(label, key) in item.options" :key="key" class="opt-item" :class="{ correct: isCorrectOpt(item, key) }">
          {{ key }}. {{ label }}
        </span>
      </div>
      <div class="answers">
        <div class="answer-row">你的答案：<span class="wrong-ans">{{ formatAnswer(item.yourAnswer) || '（空）' }}</span></div>
        <div class="answer-row">正确答案：<span class="correct-ans">{{ formatAnswer(item.correctAnswer) }}</span></div>
      </div>
      <div v-if="item.explanation" class="explanation">
        <div class="exp-title">解析</div>
        <div class="exp-content">{{ item.explanation }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../api/request'

const list = ref([])

const typeLabel = (t) => {
  const map = { CHOICE:'单选题', MULTI_CHOICE:'多选题', BLANK:'填空题', JUDGE:'判断题', ESSAY:'简答题' }
  return map[t] || t
}

const formatAnswer = (ans) => {
  if (!ans) return ''
  if (ans === 'T') return '正确'
  if (ans === 'F') return '错误'
  return ans
}

const isCorrectOpt = (item, key) => {
  if (!item.correctAnswer) return false
  const correctKeys = item.correctAnswer.split(',')
  return correctKeys.includes(key)
}

onMounted(async () => {
  try {
    const res = await request.get('/api/exam/wrong-book')
    list.value = res.data
  } catch (e) {}
})
</script>

<style scoped>
.wrong-book { max-width: 800px; }
h2 { font-size: 24px; font-weight: 700; letter-spacing: -0.02em; margin-bottom: 20px; }
.empty { text-align: center; color: var(--text-secondary); padding: 60px; font-size: 15px; }
.wrong-item { margin-bottom: 12px; }
.subject-tag { margin-bottom: 10px; }
.content { font-size: 16px; font-weight: 500; margin-bottom: 10px; line-height: 1.5; }
.options-display { display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 12px; }
.opt-item {
  padding: 4px 12px; border-radius: 6px; font-size: 14px;
  background: var(--border-light); color: var(--text-secondary);
}
.opt-item.correct { background: rgba(52,199,89,0.12); color: var(--success); font-weight: 500; }
.answers { border-top: 1px solid var(--border-light); padding-top: 10px; }
.answer-row { font-size: 14px; margin-bottom: 4px; }
.wrong-ans { color: var(--danger); text-decoration: line-through; }
.correct-ans { color: var(--success); font-weight: 600; }
.explanation { margin-top: 12px; padding: 12px 16px; background: rgba(0,122,255,0.04); border-radius: 10px; border-left: 3px solid var(--primary); }
.exp-title { font-size: 13px; font-weight: 600; color: var(--primary); margin-bottom: 6px; }
.exp-content { font-size: 14px; color: var(--text); line-height: 1.6; }
</style>
