<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="register-form">
    <h2 class="form-title">注册账号</h2>

    <el-form-item label="身份" prop="role">
      <el-radio-group v-model="form.role">
        <el-radio :value="0">学生</el-radio>
        <el-radio :value="1">教师</el-radio>
      </el-radio-group>
    </el-form-item>

    <el-form-item :label="form.role === 0 ? '学号' : '工号'" prop="username">
      <el-input v-model="form.username" :placeholder="form.role === 1 ? '请输入工号' : '请输入学号'" />
    </el-form-item>

    <el-form-item label="姓名" prop="name">
      <el-input v-model="form.name" placeholder="请输入真实姓名" />
    </el-form-item>

    <el-form-item label="密码" prop="password">
      <el-input v-model="form.password" type="password" placeholder="至少8位，包含数字和字母" show-password />
    </el-form-item>

    <el-form-item label="确认密码" prop="confirmPassword">
      <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
    </el-form-item>

    <el-form-item label="手机号" prop="phone">
      <el-input v-model="form.phone" placeholder="请输入手机号" />
    </el-form-item>

    <el-form-item label="邮箱" prop="email">
      <el-input v-model="form.email" placeholder="请输入邮箱" />
    </el-form-item>

    <el-form-item label="院系" prop="department">
      <el-input v-model="form.department" placeholder="请输入所在院系" />
    </el-form-item>

    <el-form-item v-if="form.role === 0" label="专业" prop="major">
      <el-input v-model="form.major" placeholder="请输入专业" />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="handleRegister" :loading="loading" class="form-submit">注册</el-button>
    </el-form-item>

    <el-form-item>
      <span class="form-footer">
        已有账号？
        <router-link to="/login" class="form-link">去登录</router-link>
      </span>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { register } from '@/api/auth'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  role: null as number | null,
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  phone: '',
  email: '',
  department: '',
  major: '',
})

watch(() => form.role, () => {
  form.username = ''
  formRef.value?.clearValidate('username')
})

const validatePassword = (_rule: any, value: string, callback: (error?: Error) => void) => {
  if (value.length < 8) {
    callback(new Error('密码至少8位'))
  } else if (!/[a-zA-Z]/.test(value)) {
    callback(new Error('密码必须包含字母'))
  } else if (!/[0-9]/.test(value)) {
    callback(new Error('密码必须包含数字'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (_rule: any, value: string, callback: (error?: Error) => void) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  role: [{ required: true, message: '请选择身份', trigger: 'change' }],
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await register({
        username: form.username,
        password: form.password,
        name: form.name,
        phone: form.phone,
        email: form.email,
        department: form.department,
        major: form.role === 0 ? form.major : undefined,
        role: form.role!,
      })
      ElMessage({ message: '注册成功，请登录', type: 'success' })
      router.push('/login')
    } catch {
      /* 拦截器已处理 */
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.register-form {
  width: 420px;
  margin: 80px auto;
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
