import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import TeacherLayout from '../layouts/TeacherLayout.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'Login', component: () => import('../views/LoginView.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/RegisterView.vue') },
  { path: '/forgot-password', name: 'ForgotPassword', component: () => import('../views/ForgotPassword.vue') },
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue') },
      { path: 'exams', name: 'ExamList', component: () => import('../views/ExamList.vue') },
      { path: 'history', name: 'History', component: () => import('../views/History.vue') },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue') },
      { path: 'exam/:paperId', name: 'ExamPage', component: () => import('../views/ExamPage.vue') },
      { path: 'result/:recordId', name: 'Result', component: () => import('../views/ResultView.vue') },
      { path: 'wrong-book', name: 'WrongBook', component: () => import('../views/WrongBook.vue') }
    ]
  },
  // 教师端路由
  { path: '/teacher/login', name: 'TeacherLogin', component: () => import('../views/teacher/Login.vue') },
  {
    path: '/teacher',
    component: TeacherLayout,
    children: [
      { path: 'dashboard', name: 'TeacherDashboard', component: () => import('../views/teacher/Dashboard.vue') },
      { path: 'questions', name: 'TeacherQuestions', component: () => import('../views/teacher/Questions.vue') },
      { path: 'papers', name: 'TeacherPapers', component: () => import('../views/teacher/Papers.vue') },
      { path: 'students', name: 'TeacherStudents', component: () => import('../views/teacher/Students.vue') },
      { path: 'records', name: 'TeacherRecords', component: () => import('../views/teacher/Records.vue') },
      { path: 'grade/:recordId', name: 'TeacherGrade', component: () => import('../views/teacher/Grade.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const teacherToken = localStorage.getItem('teacherToken')

  // 教师端路由保护
  if (to.path.startsWith('/teacher')) {
    if (to.path === '/teacher/login' && teacherToken) {
      next('/teacher/dashboard')
    } else if (to.path !== '/teacher/login' && !teacherToken) {
      next('/teacher/login')
    } else {
      next()
    }
    return
  }

  // 学生端路由保护
  if (to.path !== '/login' && to.path !== '/register' && to.path !== '/forgot-password' && !token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/dashboard')
  } else if (to.path === '/' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
