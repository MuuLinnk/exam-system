<template>
  <div class="login-page">
    <div class="login-card apple-card">
      <div class="title">注册</div>
      <div class="subtitle">创建学生账号</div>

      <el-form :model="form" :rules="rules" ref="formRef" size="large">
        <el-form-item prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="学号" />
        </el-form-item>
        <el-form-item prop="name">
          <el-input v-model="form.name" placeholder="姓名" />
        </el-form-item>
        <el-form-item prop="className">
          <el-input v-model="form.className" placeholder="班级（如 计科一班）" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码（至少6位）" show-password />
        </el-form-item>
        <el-form-item prop="rePassword">
          <el-input v-model="form.rePassword" type="password" placeholder="确认密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doRegister" :loading="loading" style="width:100%;font-weight:600">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <p class="link">已有账号？<router-link to="/login">去登录</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ studentNo: '', name: '', className: '', password: '', rePassword: '' })

const validateRePassword = (rule, value, callback) => {
  callback(value !== form.password ? new Error('两次密码不一致') : undefined)
}
const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  rePassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateRePassword, trigger: 'blur' }
  ]
}

const doRegister = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await axios.post('http://localhost:8080/api/student/register', {
        studentNo: form.studentNo, name: form.name,
        password: form.password, className: form.className
      })
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } catch (e) {
      ElMessage.error(e.response?.data?.message || '注册失败')
    } finally { loading.value = false }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh; display: flex; justify-content: center; align-items: center;
  background: var(--bg);
}
.login-card { width: 400px; padding: 40px 36px; text-align: center; }
.title { font-size: 28px; font-weight: 700; color: var(--text); letter-spacing: -0.02em; margin-bottom: 4px; }
.subtitle { font-size: 14px; color: var(--text-secondary); margin-bottom: 28px; }
.link { margin-top: 14px; font-size: 14px; color: var(--text-secondary); }
.link a { color: var(--primary); text-decoration: none; }
.link a:hover { text-decoration: underline; }
</style>
