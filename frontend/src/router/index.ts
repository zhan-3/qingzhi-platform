import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Home from '@/views/Home.vue'
import Layout from '@/views/Layout.vue'
import Dashboard from '@/views/Dashboard.vue'
import Users from '@/views/Users.vue'
import Resources from '@/views/Resources.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      { path: 'dashboard', component: Dashboard, meta: { title: '控制台' } },
      { path: 'users', component: Users, meta: { title: '用户管理', requiresAdmin: true } },
      { path: 'resources', component: Resources, meta: { title: '资源管理' } },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  const role = Number(localStorage.getItem('role'))

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.requiresAdmin && role !== 2) {
    next('/dashboard')
  } else if ((to.path === '/' || to.path === '/login' || to.path === '/register') && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
