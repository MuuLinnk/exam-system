<template>
  <div class="profile">
    <h2>个人中心</h2>

    <div class="info-card apple-card">
      <h3>基本信息</h3>
      <div class="info-grid">
        <div class="info-item">
          <span class="label">学号</span>
          <span class="value">{{ info.studentNo }}</span>
        </div>
        <div class="info-item">
          <span class="label">姓名</span>
          <span class="value">{{ info.name }}</span>
        </div>
        <div class="info-item">
          <span class="label">班级</span>
          <span class="value">{{ info.className }}</span>
        </div>
        <div class="info-item">
          <span class="label">注册时间</span>
          <span class="value">{{ info.createTime }}</span>
        </div>
      </div>
    </div>

    <div class="pwd-card apple-card">
      <h3>修改密码</h3>
      <el-form :model="pwdForm" :rules="pwdRules" ref="pwdRef" size="large" style="max-width:360px">
        <el-form-item prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="旧密码" />
        </el-form-item>
        <el-form-item prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="新密码（至少6位）" />
        </el-form-item>
        <el-form-item prop="rePassword">
          <el-input v-model="pwdForm.rePassword" type="password" show-password placeholder="确认新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="changePwd" :loading="loading" style="font-weight:600">修改密码</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const loading = ref(false)
const pwdRef = ref()
const info = reactive({ studentNo: '', name: '', className: '', createTime: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '', rePassword: '' })

const validateRePwd = (rule, value, callback) => {
  callback(value !== pwdForm.newPassword ? new Error('两次密码不一致') : undefined)
}
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '至少6位', trigger: 'blur' }
  ],
  rePassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateRePwd, trigger: 'blur' }
  ]
}

onMounted(async () => {
  try {
    const res = await request.get('/api/student/info')
    Object.assign(info, res.data)
  } catch (e) {}
})

const changePwd = () => {
  pwdRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await request.put('/api/student/password', {
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword
      })
      ElMessage.success('密码修改成功')
      pwdForm.oldPassword = ''; pwdForm.newPassword = ''; pwdForm.rePassword = ''
    } catch (e) {} finally { loading.value = false }
  })
}
</script>

<style scoped>
.profile { max-width: 700px; }
h2 { font-size: 24px; font-weight: 700; letter-spacing: -0.02em; margin-bottom: 20px; }
.info-card, .pwd-card { margin-bottom: 18px; }
.info-card h3, .pwd-card h3 { font-size: 16px; font-weight: 600; margin-bottom: 16px; }
.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0; }
.info-item { padding: 12px 0; border-bottom: 1px solid var(--border-light); display: flex; flex-direction: column; gap: 2px; }
.label { font-size: 12px; color: var(--text-secondary); text-transform: uppercase; letter-spacing: 0.02em; }
.value { font-size: 15px; font-weight: 500; }
</style>
