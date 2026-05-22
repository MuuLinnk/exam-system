<template>
  <div class="login-page">
    <div class="login-card apple-card">
      <div class="title">登录</div>
      <div class="subtitle">学生考试系统</div>

      <el-form :model="form" :rules="rules" ref="formRef" size="large" class="form">
        <el-form-item prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="学号" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码"
            show-password @keyup.enter="doLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doLogin" :loading="loading" class="login-btn">
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <p class="link">还没有账号？<router-link to="/register">注册</router-link></p>
      <p class="link"><router-link to="/forgot-password">忘记密码？</router-link></p>
      <p class="hint">测试账号：2024001 / 123456</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ studentNo: '', password: '' })
const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const doLogin = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await request.post('/api/student/login', form)
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('studentName', res.data.studentName)
      router.push('/dashboard')
    } catch (e) {} finally { loading.value = false }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh; display: flex; justify-content: center; align-items: center;
  background: var(--bg);
}
.login-card { width: 380px; padding: 40px 36px; text-align: center; }
.title { font-size: 28px; font-weight: 700; color: var(--text); letter-spacing: -0.02em; margin-bottom: 4px; }
.subtitle { font-size: 14px; color: var(--text-secondary); margin-bottom: 32px; }
.form { text-align: left; }
.login-btn { width: 100%; font-size: 15px; height: 44px; font-weight: 600; }
.link { margin-top: 14px; font-size: 14px; color: var(--text-secondary); }
.link a { color: var(--primary); text-decoration: none; }
.link a:hover { text-decoration: underline; }
.hint { margin-top: 12px; font-size: 12px; color: var(--text-tertiary); }
</style>
