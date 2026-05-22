<template>
  <div class="page">
    <div class="page-header">
      <el-button type="default" link @click="$router.back()">← 返回</el-button>
    </div>
    <div class="card info-card">
      <div class="info-item"><span class="label">学生</span><span class="value">{{ detail.studentName }} ({{ detail.studentNo }})</span></div>
      <div class="info-item"><span class="label">试卷</span><span class="value">{{ detail.paperTitle }}</span></div>
      <div class="info-item"><span class="label">总分</span><span class="value">{{ detail.score }} / {{ detail.totalScore }}</span></div>
    </div>

    <div v-for="q in detail.details" :key="q.questionId" class="card question-card">
      <div class="q-header">
        <span class="q-type-tag" :class="typeClass(q.type)">{{ typeLabel(q.type) }}</span>
        <span class="q-score">{{ q.score }}分</span>
        <span v-if="q.isCorrect !== undefined" class="q-result" :class="q.isCorrect ? 'correct' : 'wrong'">
          {{ q.isCorrect ? '正确' : '错误' }}
        </span>
      </div>
      <div class="q-content">{{ q.content }}</div>

      <div v-if="q.options && Object.keys(q.options).length" class="q-options">
        <div v-for="(text, key) in q.options" :key="key" class="opt-row" :class="optClass(q, key, text)">
          <span class="opt-key">{{ key }}.</span>
          <span class="opt-text">{{ text }}</span>
          <span v-if="isCorrectOpt(q, key)" class="opt-mark correct">✓</span>
          <span v-else-if="isWrongOpt(q, key)" class="opt-mark wrong">✗</span>
        </div>
      </div>

      <div class="answer-section">
        <div class="answer-row">
          <span class="ans-label">学生答案：</span>
          <span class="ans-value" :class="{ 'ans-wrong': q.isCorrect === false }">{{ q.studentAnswer || '未作答' }}</span>
        </div>
        <div class="answer-row">
          <span class="ans-label">正确答案：</span>
          <span class="ans-value ans-correct">{{ q.correctAnswer }}</span>
        </div>
      </div>

      <!-- 简答题批改 -->
      <div v-if="q.type === 'ESSAY'" class="grade-section">
        <div class="grade-row">
          <el-input-number v-model="essayScores[q.questionId]" :min="0" :max="q.score" size="small" />
          <span class="grade-hint">0 ~ {{ q.score }} 分</span>
        </div>
      </div>
    </div>

    <div class="submit-bar" v-if="hasEssay">
      <el-button type="primary" :loading="grading" @click="submitGrade">提交批改</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import teacherRequest from '../../api/teacherRequest'

const route = useRoute()
const router = useRouter()
const detail = ref({ details: [] })
const essayScores = ref({})
const grading = ref(false)

const hasEssay = computed(() => detail.value.details.some(q => q.type === 'ESSAY'))

const typeLabel = (t) => ({ CHOICE: '单选', MULTI_CHOICE: '多选', JUDGE: '判断', BLANK: '填空', ESSAY: '简答' }[t] || t)
const typeClass = (t) => ({ CHOICE: 'type-choice', MULTI_CHOICE: 'type-multi', JUDGE: 'type-judge', BLANK: 'type-blank', ESSAY: 'type-essay' }[t] || '')

const isCorrectOpt = (q, key) => {
  if (q.type === 'MULTI_CHOICE') {
    const correct = (q.correctAnswer || '').split(',')
    return correct.includes(key)
  }
  return q.correctAnswer === key
}

const isWrongOpt = (q, key) => {
  if (q.type === 'MULTI_CHOICE') {
    const student = (q.studentAnswer || '').split(',')
    const correct = (q.correctAnswer || '').split(',')
    return student.includes(key) && !correct.includes(key)
  }
  return q.studentAnswer === key && q.correctAnswer !== key
}

const optClass = (q, key, text) => {
  const correct = (q.correctAnswer || '').split(',')
  const student = (q.studentAnswer || '').split(',')
  if (correct.includes(key)) return 'opt-correct'
  if (student.includes(key) && !correct.includes(key)) return 'opt-wrong'
  return ''
}

const submitGrade = async () => {
  grading.value = true
  try {
    const essayQuestions = detail.value.details.filter(q => q.type === 'ESSAY')
    for (const q of essayQuestions) {
      if (essayScores.value[q.questionId] !== undefined) {
        await teacherRequest.put('/api/teacher/grade', {
          recordId: detail.value.recordId,
          questionId: q.questionId,
          score: essayScores.value[q.questionId]
        })
      }
    }
    ElMessage.success('批改完成')
    router.back()
  } catch (e) {
    ElMessage.error('批改失败')
  } finally {
    grading.value = false
  }
}

onMounted(async () => {
  try {
    const res = await teacherRequest.get(`/api/teacher/records/${route.params.recordId}`)
    detail.value = res.data
    // 初始化简答题分数为0
    for (const q of detail.value.details) {
      if (q.type === 'ESSAY') {
        essayScores.value[q.questionId] = 0
      }
    }
  } catch (e) {}
})
</script>

<style scoped>
.page-header { margin-bottom: 16px; }
.card { background: #fff; border-radius: 16px; padding: 24px; box-shadow: 0 1px 8px rgba(0,0,0,0.06); margin-bottom: 16px; }
.info-card { display: flex; gap: 32px; flex-wrap: wrap; }
.info-item { display: flex; gap: 8px; }
.info-item .label { color: #86868b; font-size: 14px; }
.info-item .value { font-weight: 600; color: #1d1d1f; font-size: 14px; }
.q-header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.q-type-tag {
  font-size: 11px; font-weight: 600; padding: 2px 8px; border-radius: 6px;
  background: #f2f2f7; color: #636366;
}
.q-score { font-size: 13px; color: #86868b; }
.q-result { font-size: 12px; font-weight: 600; padding: 2px 8px; border-radius: 6px; }
.q-result.correct { background: #e8f5e9; color: #34c759; }
.q-result.wrong { background: #fce4ec; color: #ff3b30; }
.q-content { font-size: 15px; color: #1d1d1f; line-height: 1.6; margin-bottom: 16px; }
.q-options { display: flex; flex-direction: column; gap: 8px; margin-bottom: 16px; }
.opt-row {
  display: flex; align-items: center; gap: 8px; padding: 8px 12px;
  border-radius: 8px; border: 1px solid #e5e5ea; font-size: 14px;
}
.opt-row.opt-correct { background: #e8f5e9; border-color: #c8e6c9; }
.opt-row.opt-wrong { background: #fce4ec; border-color: #ffcdd2; }
.opt-key { font-weight: 600; color: #636366; min-width: 20px; }
.opt-text { color: #1d1d1f; }
.opt-mark { font-weight: 700; font-size: 12px; margin-left: auto; }
.opt-mark.correct { color: #34c759; }
.opt-mark.wrong { color: #ff3b30; }
.answer-section { border-top: 1px solid #f2f2f7; padding-top: 12px; }
.answer-row { display: flex; gap: 8px; margin-bottom: 4px; font-size: 14px; }
.ans-label { color: #86868b; }
.ans-value { color: #1d1d1f; font-weight: 500; }
.ans-wrong { color: #ff3b30; text-decoration: line-through; }
.ans-correct { color: #34c759; }
.grade-section { border-top: 1px solid #f2f2f7; padding-top: 12px; margin-top: 12px; }
.grade-row { display: flex; align-items: center; gap: 12px; }
.grade-hint { font-size: 13px; color: #86868b; }
.submit-bar { text-align: right; margin-top: 20px; }
</style>
