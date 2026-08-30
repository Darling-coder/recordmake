<template>
  <div class="login-box">
    <el-card title="唱片交易平台登录" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" show-password></el-input>
        </el-form-item>
        <el-form-item label="身份">
          <el-radio v-model="form.userRole" label="buyer">买家</el-radio>
          <el-radio v-model="form.userRole" label="seller">卖家</el-radio>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitLogin">登录</el-button>
          <el-button text @click="$router.push('/register')">去注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login as loginApi } from '@/api'

const router = useRouter()
const form = ref({ username: '', password: '', userRole: 'buyer' })

const submitLogin = async () => {
  try {
    const res = await loginApi(form.value)
    console.log('【后端返回的 code】:', res.code)
    console.log('【后端完整data】:', res.data)

    if (res.code === 200) {
      const token = res.data.token
      const realRole = res.data.role

      // 校验token非空，防止后端异常
      if (!token) {
        ElMessage.error('登录令牌获取失败，请重新登录')
        return
      }

      localStorage.setItem('token', token)
      localStorage.setItem('userRole', realRole)
      console.log('本地存储角色：', localStorage.getItem('userRole'))

      ElMessage.success('登录成功')
      // 100毫秒无感延迟，解决路由守卫读取不到token的时序bug
      setTimeout(() => {
        if (realRole === 'buyer') {
          router.push('/recordList')
        } else {
          router.push('/myRecord')
        }
      }, 100)
    } else {
      ElMessage.error(res.msg || '登录失败，请检查账号密码')
    }
  } catch (error) {
    console.error('登录异常：', error)
    ElMessage.error('网络请求异常，登录失败')
  }
}
</script>

<style scoped>
.login-box {
  width: 400px;
  margin: 100px auto;
}
</style>