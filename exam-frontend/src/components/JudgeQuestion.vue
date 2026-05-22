<template>
  <div class="question">
    <div class="q-header">
      <span class="q-num">{{ index }}.</span>
      <span class="q-type-tag">判断题</span>
      <span class="q-score">({{ question.score }}分)</span>
    </div>
    <div class="q-content">{{ question.content }}</div>
    <el-radio-group v-model="answer" @change="onChange" class="options">
      <el-radio value="T" class="option-item">正确</el-radio>
      <el-radio value="F" class="option-item">错误</el-radio>
    </el-radio-group>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  question: Object,
  index: Number,
  modelValue: String
})

const emit = defineEmits(['update:modelValue'])
const answer = ref(props.modelValue || '')

watch(() => props.modelValue, (val) => { answer.value = val || '' })

const onChange = () => {
  emit('update:modelValue', answer.value)
}
</script>

<style scoped>
.question {
  padding: 20px 0;
  border-bottom: 1px solid var(--border-light);
}
.question:last-child { border-bottom: none; }
.q-header {
  display: flex; align-items: center; gap: 10px; margin-bottom: 10px;
}
.q-num {
  font-size: 15px; font-weight: 600; color: var(--primary);
  background: rgba(0,122,255,0.08); width: 26px; height: 26px;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.q-type-tag {
  font-size: 11px; color: var(--primary);
  background: rgba(0,122,255,0.08); padding: 2px 8px; border-radius: 4px;
  font-weight: 500;
}
.q-score { font-size: 13px; color: var(--warning); font-weight: 500; }
.q-content { font-size: 16px; line-height: 1.6; margin-bottom: 14px; color: var(--text); }
.options { display: flex; gap: 24px; }
.options :deep(.el-radio) {
  padding: 10px 16px; margin: 0;
  border-radius: 10px; transition: background 0.12s;
  height: auto;
}
.options :deep(.el-radio:hover) { background: rgba(0,122,255,0.04); }
.options :deep(.el-radio__label) {
  font-size: 15px; padding-left: 8px; color: var(--text);
}
</style>
