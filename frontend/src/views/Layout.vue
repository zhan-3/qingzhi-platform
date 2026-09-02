<template>
  <el-container class="layout">
    <el-aside class="sidebar" width="200px">
      <div class="sidebar-brand">青知共享平台</div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        class="sidebar-menu"
        router
      >
        <el-menu-item index="/dashboard"><el-icon><DataBoard /></el-icon><span>控制台</span></el-menu-item>
        <el-menu-item index="/users" v-if="isAdmin"><el-icon><User /></el-icon><span>用户管理</span></el-menu-item>
        <el-menu-item index="/resources"><el-icon><Folder /></el-icon><span>资源管理</span></el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ pageTitle }}</el-breadcrumb-item>
        </el-breadcrumb>
        <el-dropdown @command="handleCommand">
          <span class="user-dropdown">
            <el-avatar :size="32" icon="UserFilled" />
            <span class="user-name">{{ username }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>

    <el-dialog v-model="profileVisible" title="个人信息" width="480px">
      <el-tabs v-model="profileTab">
        <el-tab-pane label="基本资料" name="info">
          <el-form :model="profileForm" label-width="80px">
            <el-form-item label="账号"><el-input v-model="profileForm.username" disabled /></el-form-item>
            <el-form-item label="姓名"><el-input v-model="profileForm.name" /></el-form-item>
            <el-form-item label="手机"><el-input v-model="profileForm.phone" /></el-form-item>
            <el-form-item label="邮箱"><el-input v-model="profileForm.email" /></el-form-item>
            <el-form-item label="院系"><el-input v-model="profileForm.department" /></el-form-item>
            <el-form-item label="专业"><el-input v-model="profileForm.major" /></el-form-item>
          </el-form>
          <div class="dialog-actions">
            <el-button type="primary" @click="saveProfile" :loading="saving">保存</el-button>
          </div>
        </el-tab-pane>
        <el-tab-pane label="修改密码" name="pwd">
          <el-form :model="pwdForm" label-width="80px">
            <el-form-item label="原密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
            <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="至少8位，数字+字母" /></el-form-item>
          </el-form>
          <div class="dialog-actions">
            <el-button type="primary" @click="savePassword" :loading="savingPwd">修改密码</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { DataBoard, User, Folder } from '@element-plus/icons-vue'
import { getProfile, updateProfile, changePassword } from '@/api/profile'

const router = useRouter()
const route = useRoute()
const activeMenu = computed(() => route.path)
const isAdmin = computed(() => Number(localStorage.getItem('role')) === 2)
const username = ref(localStorage.getItem('username') || '用户')
const pageTitle = computed(() => (route.meta.title as string) || '')

const handleCommand = async (cmd: string) => {
  if (cmd === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('username')
    localStorage.removeItem('userId')
    router.push('/login')
  } else if (cmd === 'profile') {
    profileVisible.value = true
    try {
      const { data } = await getProfile()
      Object.assign(profileForm, {
        username: data.username,
        name: data.name || '',
        phone: data.phone || '',
        email: data.email || '',
        department: data.department || '',
        major: data.major || '',
      })
    } catch {
      /* 拦截器已处理 */
    }
  }
}

const profileVisible = ref(false)
const profileTab = ref('info')
const saving = ref(false)
const savingPwd = ref(false)
const profileForm = reactive({ username: '', name: '', phone: '', email: '', department: '', major: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '' })

const saveProfile = async () => {
  saving.value = true
  try {
    await updateProfile(profileForm)
    localStorage.setItem('username', profileForm.name)
    username.value = profileForm.name
    ElMessage({ message: '保存成功', type: 'success' })
    profileVisible.value = false
  } catch {
    /* 拦截器已处理 */
  } finally {
    saving.value = false
  }
}

const savePassword = async () => {
  if (!pwdForm.oldPassword || !pwdForm.newPassword) {
    ElMessage({ message: '密码不能为空', type: 'warning' })
    return
  }
  savingPwd.value = true
  try {
    await changePassword(pwdForm.oldPassword, pwdForm.newPassword)
    ElMessage({ message: '密码修改成功', type: 'success' })
    profileVisible.value = false
  } catch {
    /* 拦截器已处理 */
  } finally {
    savingPwd.value = false
  }
}
</script>

<style scoped>
.layout {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
}

.sidebar-brand {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #263445;
}

.sidebar-menu {
  border-right: none;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
}

.user-dropdown {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.user-name {
  margin-left: 8px;
}

.layout-main {
  background: #f0f2f5;
  padding: 20px;
}

.dialog-actions {
  text-align: center;
  margin-top: 8px;
}

.el-menu-item.is-active {
  background-color: #263445 !important;
}

:deep(.el-dialog) {
  border-radius: 12px;
}

:deep(.el-dialog__header) {
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
}
</style>
