<template>
  <div class="login">
    <div class="card">
      <h1>教师登录</h1>
      <p class="sub">考试系统教师管理平台</p>
      <el-input v-model="username" placeholder="用户名" size="large" class="input" />
      <el-input v-model="password" type="password" placeholder="密码" size="large" class="input"
        @keyup.enter="handleLogin" />
      <el-button type="primary" size="large" class="btn" :loading="loading" @click="handleLogin">
        登录
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import teacherRequest from '../../api/teacherRequest'

const router = useRouter()
const username = ref('')
const password = ref('')
const loading = ref(false)

const handleLogin = async () => {
  if (!username.value || !password.value) {
    return
  }
  loading.value = true
  try {
    const res = await teacherRequest.post('/api/teacher/login', {
      username: username.value,
      password: password.value
    })
    localStorage.setItem('teacherToken', res.data.token)
    localStorage.setItem('teacherName', res.data.name)
    router.push('/teacher/dashboard')
  } catch (e) {
    // teacherRequest interceptor already shows error
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login {
  min-height: 100vh; display: flex; align-items: center; justify-content: center;
  background: #f5f5f7;
}
.card {
  background: #fff; border-radius: 20px; padding: 48px 40px;
  width: 380px; box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  text-align: center;
}
h1 { font-size: 24px; font-weight: 700; color: #1d1d1f; margin: 0; letter-spacing: -0.02em; }
.sub { color: #86868b; font-size: 14px; margin: 8px 0 32px; }
.input { margin-bottom: 16px; }
.btn { width: 100%; border-radius: 10px; height: 44px; font-size: 15px; font-weight: 600; }
</style>
