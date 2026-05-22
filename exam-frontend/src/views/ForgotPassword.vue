<template>
  <div class="forgot-page">
    <div class="forgot-card apple-card">
      <div class="title">找回密码</div>
      <div class="subtitle">输入学号和姓名，密码将重置为 123456</div>

      <el-form :model="form" ref="formRef" size="large" class="form">
        <el-form-item prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="学号" />
        </el-form-item>
        <el-form-item prop="name">
          <el-input v-model="form.name" placeholder="姓名" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doReset" :loading="loading" class="reset-btn">
            重置密码
          </el-button>
        </el-form-item>
      </el-form>

      <p class="link"><router-link to="/login">返回登录</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const formRef = ref()
const loading = ref(false)
const form = reactive({ studentNo: '', name: '' })

const doReset = async () => {
  if (!form.studentNo || !form.name) {
    ElMessage.warning('请填写学号和姓名')
    return
  }
  loading.value = true
  try {
    await request.post('/api/student/reset-password', form)
    ElMessage.success('密码已重置为 123456，请登录后修改')
  } catch (e) {} finally { loading.value = false }
}
</script>

<style scoped>
.forgot-page {
  min-height: 100vh; display: flex; justify-content: center; align-items: center;
  background: var(--bg);
}
.forgot-card { width: 380px; padding: 40px 36px; text-align: center; }
.title { font-size: 28px; font-weight: 700; color: var(--text); letter-spacing: -0.02em; margin-bottom: 4px; }
.subtitle { font-size: 14px; color: var(--text-secondary); margin-bottom: 28px; }
.form { text-align: left; }
.reset-btn { width: 100%; font-size: 15px; height: 44px; font-weight: 600; }
.link { margin-top: 14px; }
.link a { color: var(--primary); text-decoration: none; font-size: 14px; }
.link a:hover { text-decoration: underline; }
</style>
