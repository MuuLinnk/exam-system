<template>
  <div class="exam-page">
    <div class="top-bar apple-card">
      <h3>{{ paperTitle }}</h3>
      <div class="timer">
        剩余时间 <span :class="{ danger: remaining < 300 }">{{ formatTime(remaining) }}</span>
      </div>
    </div>

    <div class="content" v-if="questions.length > 0">
      <div class="question-list apple-card">
        <component
          v-for="(q, idx) in questions"
          :key="q.questionId"
          :is="componentMap[q.type]"
          :question="q"
          :index="idx + 1"
          v-model="answers[q.questionId]"
        />
      </div>
      <div class="actions">
        <el-button type="primary" size="large" @click="submitExam">确认提交</el-button>
      </div>
    </div>
    <div v-else class="loading">加载试卷中...</div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../api/request'
import ChoiceQuestion from '../components/ChoiceQuestion.vue'
import MultiChoiceQuestion from '../components/MultiChoiceQuestion.vue'
import JudgeQuestion from '../components/JudgeQuestion.vue'
import BlankQuestion from '../components/BlankQuestion.vue'
import EssayQuestion from '../components/EssayQuestion.vue'

const route = useRoute(); const router = useRouter()
const componentMap = {
  CHOICE: ChoiceQuestion, MULTI_CHOICE: MultiChoiceQuestion,
  JUDGE: JudgeQuestion, BLANK: BlankQuestion, ESSAY: EssayQuestion
}

const paperTitle = ref('')
const recordId = ref(null)
const questions = ref([])
const answers = reactive({})
const remaining = ref(0)
let timer = null
let warned = false

onMounted(async () => {
  try {
    const res = await request.get(`/api/exam/start/${route.params.paperId}`)
    const d = res.data
    paperTitle.value = d.paperTitle; recordId.value = d.recordId
    questions.value = d.questions; remaining.value = d.duration * 60
    startTimer()
  } catch (e) {}
})

const startTimer = () => {
  timer = setInterval(() => {
    remaining.value--
    if (remaining.value === 300 && !warned) {
      warned = true
      ElMessage.warning('⏰ 还剩 5 分钟，请抓紧时间作答！')
    }
    if (remaining.value <= 0) { clearInterval(timer); submitExam() }
  }, 1000)
}

const formatTime = (s) => {
  const m = Math.floor(s / 60), sec = s % 60
  return `${m.toString().padStart(2,'0')}:${sec.toString().padStart(2,'0')}`
}

const submitExam = () => {
  ElMessageBox.confirm('确定提交吗？提交后不可修改哦~', '🌸 确认交卷', {
    confirmButtonText: '确定提交', cancelButtonText: '继续检查', type: 'warning'
  }).then(async () => {
    clearInterval(timer)
    const answerList = questions.value.map(q => ({
      questionId: q.questionId, answer: answers[q.questionId] || ''
    }))
    try {
      const res = await request.post('/api/exam/submit', { recordId: recordId.value, answers: answerList })
      localStorage.setItem('resultData', JSON.stringify(res.data))
      router.push(`/result/${recordId.value}`)
    } catch (e) { startTimer() }
  }).catch(() => {})
}

onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.exam-page { max-width: 900px; }
.top-bar {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 24px; margin-bottom: 20px;
  position: sticky; top: 10px; z-index: 50;
}
.top-bar h3 { margin: 0; font-size: 18px; font-weight: 600; }
.timer { font-size: 15px; color: var(--text-secondary); }
.timer span { font-weight: 600; color: var(--text); font-size: 18px; font-variant-numeric: tabular-nums; }
.timer span.danger { color: var(--danger); animation: pulse 1s infinite; }
@keyframes pulse { 50% { opacity: 0.5; } }
.question-list { margin-bottom: 24px; }
.actions { text-align: center; margin-top: 8px; }
.loading { text-align: center; padding: 100px; color: var(--text-secondary); font-size: 16px; }
</style>
