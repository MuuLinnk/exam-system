<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="logo">教师管理</div>
      <nav class="nav">
        <router-link to="/teacher/dashboard" class="nav-item">
          <span class="icon">◻</span> 概览
        </router-link>
        <router-link to="/teacher/questions" class="nav-item">
          <span class="icon">◻</span> 题库管理
        </router-link>
        <router-link to="/teacher/papers" class="nav-item">
          <span class="icon">◻</span> 试卷管理
        </router-link>
        <router-link to="/teacher/students" class="nav-item">
          <span class="icon">◻</span> 学生管理
        </router-link>
        <router-link to="/teacher/records" class="nav-item">
          <span class="icon">◻</span> 成绩总览
        </router-link>
      </nav>
      <div class="logout" @click="logout">退出登录</div>
    </aside>
    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const logout = () => {
  localStorage.removeItem('teacherToken')
  localStorage.removeItem('teacherName')
  router.push('/teacher/login')
}
</script>

<style scoped>
.layout {
  display: flex; min-height: 100vh;
}
.sidebar {
  width: 220px;
  background: var(--sidebar-bg);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-right: 1px solid var(--border);
  display: flex; flex-direction: column;
  padding: 20px 0;
  position: fixed; top: 0; left: 0; bottom: 0; z-index: 100;
}
.logo {
  font-size: 17px; font-weight: 700; color: var(--text);
  padding: 8px 20px 20px; letter-spacing: -0.02em;
}
.nav { flex: 1; padding: 0 12px; }
.nav-item {
  display: flex; align-items: center; gap: 10px;
  padding: 10px 12px; margin-bottom: 2px;
  color: var(--text); text-decoration: none;
  font-size: 14px; font-weight: 500;
  border-radius: 10px; transition: all 0.12s;
}
.nav-item:hover { background: rgba(0,0,0,0.04); }
.nav-item.router-link-active {
  background: rgba(0,122,255,0.1); color: var(--primary);
}
.icon { font-size: 13px; opacity: 0.4; }
.logout {
  margin: 12px; padding: 10px 12px; cursor: pointer;
  font-size: 14px; color: var(--text-secondary); border-radius: 10px;
  transition: all 0.12s;
}
.logout:hover { background: rgba(255,59,48,0.06); color: var(--danger); }
.main {
  margin-left: 220px; flex: 1; padding: 32px 40px;
}
</style>
