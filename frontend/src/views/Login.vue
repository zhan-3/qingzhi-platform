<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="login-form">
    <h2 class="form-title">青知共享平台</h2>

    <el-form-item label="账号" prop="username">
      <el-input v-model="form.username" placeholder="学号/工号" />
    </el-form-item>

    <el-form-item label="密码" prop="password">
      <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password
        @keyup.enter="handleLogin" />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="handleLogin" :loading="loading" class="form-submit">登录</el-button>
    </el-form-item>

    <el-form-item>
      <span class="form-footer">
        没有账号？
        <router-link to="/register" class="form-link">立即注册</router-link>
      </span>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { login } from '@/api/auth'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入学号或工号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await login(form)
      localStorage.setItem('token', res.data)
      const payload = JSON.parse(atob(res.data.split('.')[1]))
      localStorage.setItem('role', payload.role)
      localStorage.setItem('username', payload.username)
      localStorage.setItem('userId', payload.userId)
      ElMessage({ message: '登录成功', type: 'success' })
      router.push('/dashboard')
    } catch {
      /* 拦截器已处理 */
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-form {
  width: 360px;
  margin: 120px auto;
}

.form-title {
  text-align: center;
  margin-bottom: 24px;
}

.form-submit {
  width: 100%;
}

.form-footer {
  font-size: 13px;
  color: #999;
}

.form-link {
  color: #409EFF;
}
</style>
