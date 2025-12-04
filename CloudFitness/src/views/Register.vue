<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h1 class="title">云健健身</h1>
        <p class="subtitle">会员注册</p>
      </div>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="rules"
        class="register-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
            @blur="checkUsername"
          />
        </el-form-item>
        <el-form-item prop="phone_number">
          <el-input
            v-model="registerForm.phone_number"
            placeholder="请输入手机号"
            size="large"
            :prefix-icon="Phone"
            @blur="checkPhone"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
        <el-form-item class="login-link">
          <span>已有账号？</span>
          <el-link type="primary" @click="goToLogin">立即登录</el-link>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register, checkUsernameExists, checkPhoneExists } from '@/api/auth'
import { User, Lock, Phone } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

const registerFormRef = ref(null)
const loading = ref(false)
const usernameChecking = ref(false)
const phoneChecking = ref(false)

const registerForm = reactive({
  username: '',
  phone_number: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateUsername = async (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入用户名'))
    return
  }
  if (value.length < 3) {
    callback(new Error('用户名长度不能少于3位'))
    return
  }
  if (value.length > 20) {
    callback(new Error('用户名长度不能超过20位'))
    return
  }
  // 检查用户名格式（字母、数字、下划线）
  if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('用户名只能包含字母、数字和下划线'))
    return
  }
  // 检查用户名是否已存在
  try {
    const exists = await checkUsernameExists(value)
    if (exists) {
      callback(new Error('该用户名已被使用'))
      return
    }
  } catch (error) {
    // API失败时不阻止验证通过
    console.warn('检查用户名失败:', error.message)
  }
  callback()
}

const validatePhone = async (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'))
    return
  }
  // 验证手机号格式
  if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
    return
  }
  // 检查手机号是否已存在
  try {
    const exists = await checkPhoneExists(value)
    if (exists) {
      callback(new Error('该手机号已被注册'))
      return
    }
  } catch (error) {
    // API失败时不阻止验证通过
    console.warn('检查手机号失败:', error.message)
  }
  callback()
}

const rules = {
  username: [
    { validator: validateUsername, trigger: 'blur' }
  ],
  phone_number: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
    { max: 20, message: '密码长度不能超过20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const checkUsername = async () => {
  if (!registerForm.username || registerForm.username.length < 3) {
    return
  }
  usernameChecking.value = true
  try {
    const exists = await checkUsernameExists(registerForm.username)
    if (exists) {
      ElMessage.warning('该用户名已被使用')
      if (registerFormRef.value) {
        registerFormRef.value.validateField('username')
      }
    }
  } catch (error) {
    // API失败时静默处理，不阻止注册
    console.warn('检查用户名失败:', error.message)
  } finally {
    usernameChecking.value = false
  }
}

const checkPhone = async () => {
  if (!registerForm.phone_number || !/^1[3-9]\d{9}$/.test(registerForm.phone_number)) {
    return
  }
  phoneChecking.value = true
  try {
    const exists = await checkPhoneExists(registerForm.phone_number)
    if (exists) {
      ElMessage.warning('该手机号已被注册')
      if (registerFormRef.value) {
        registerFormRef.value.validateField('phone_number')
      }
    }
  } catch (error) {
    // API失败时静默处理，不阻止注册
    console.warn('检查手机号失败:', error.message)
  } finally {
    phoneChecking.value = false
  }
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await register({
          username: registerForm.username,
          phone_number: registerForm.phone_number,
          password: registerForm.password
        })
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.message || '注册失败，请重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.register-container {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-box {
  width: 420px;
  background: #ffffff;
  border-radius: 16px;
  padding: 48px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
  
  .title {
    font-size: 32px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
  }
  
  .subtitle {
    font-size: 14px;
    color: #909399;
  }
}

.register-form {
  .register-button {
    width: 100%;
    height: 44px;
    font-size: 16px;
    font-weight: 500;
  }
  
  .login-link {
    margin-bottom: 0;
    text-align: center;
    
    span {
      font-size: 14px;
      color: #909399;
      margin-right: 8px;
    }
  }
}
</style>

