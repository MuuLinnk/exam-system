<template>
  <div class="question">
    <div class="q-header">
      <span class="q-num">{{ index }}.</span>
      <span class="q-type-tag">多选题</span>
      <span class="q-score">({{ question.score }}分)</span>
    </div>
    <div class="q-content">{{ question.content }}</div>
    <el-checkbox-group v-model="selected" @change="onChange" class="options">
      <el-checkbox v-for="(label, key) in question.options" :key="key" :value="key" class="option-item">
        <span class="opt-key">{{ key }}.</span> {{ label }}
      </el-checkbox>
    </el-checkbox-group>
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

const selected = ref(props.modelValue ? props.modelValue.split(',') : [])

watch(() => props.modelValue, (val) => {
  selected.value = val ? val.split(',') : []
})

const onChange = () => {
  emit('update:modelValue', selected.value.join(','))
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
}
.q-type-tag {
  font-size: 11px; color: var(--primary);
  background: rgba(0,122,255,0.08); padding: 2px 8px; border-radius: 4px;
  font-weight: 500;
}
.q-score { font-size: 13px; color: var(--warning); font-weight: 500; }
.q-content { font-size: 16px; line-height: 1.6; margin-bottom: 14px; color: var(--text); }
.options {
  display: flex; flex-direction: column; gap: 2px;
}
.options :deep(.el-checkbox) {
  display: flex; align-items: flex-start;
  padding: 10px 14px; margin: 0;
  border-radius: 10px; transition: background 0.12s;
  height: auto;
}
.options :deep(.el-checkbox:hover) { background: rgba(0,122,255,0.04); }
.options :deep(.el-checkbox__input) {
  margin-top: 1px; flex-shrink: 0;
}
.options :deep(.el-checkbox__label) {
  display: flex; align-items: baseline; gap: 0;
  padding-left: 10px; font-size: 15px; line-height: 1.5;
  color: var(--text);
}
.opt-key {
  display: inline-block; font-weight: 600; color: var(--text-secondary);
  width: 22px; flex-shrink: 0;
}
</style>
