<template>
  <div class="question">
    <div class="q-header">
      <span class="q-num">{{ index }}.</span>
      <span class="q-type-tag">单选题</span>
      <span class="q-score">({{ question.score }}分)</span>
    </div>
    <div class="q-content">{{ question.content }}</div>
    <div class="options">
      <div v-for="(label, key) in question.options" :key="key"
        class="option-row" :class="{ selected: answer === key }"
        @click="select(key)">
        <span class="opt-bullet">{{ answer === key ? '●' : '○' }}</span>
        <span class="opt-key">{{ key }}.</span>
        <span class="opt-text">{{ label }}</span>
      </div>
    </div>
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

const select = (key) => {
  answer.value = key
  emit('update:modelValue', key)
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
.options { display: flex; flex-direction: column; gap: 2px; }
.option-row {
  display: flex; align-items: baseline; gap: 0;
  padding: 10px 14px; border-radius: 10px; cursor: pointer;
  transition: background 0.12s;
  user-select: none;
}
.option-row:hover { background: rgba(0,122,255,0.04); }
.option-row.selected { background: rgba(0,122,255,0.08); }
.opt-bullet {
  width: 18px; flex-shrink: 0; font-size: 14px;
  color: var(--primary); line-height: 1.5;
}
.opt-key {
  font-weight: 600; color: var(--text-secondary);
  width: 22px; flex-shrink: 0; line-height: 1.5;
}
.opt-text { font-size: 15px; line-height: 1.5; color: var(--text); }
</style>
