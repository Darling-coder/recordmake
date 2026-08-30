<template>
  <div class="register-box">
    <h2>唱片交易平台 - 用户注册</h2>
    <el-form ref="formRef" :model="form" label-width="80px">
      <el-form-item label="账号">
        <el-input v-model="form.username"></el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" show-password></el-input>
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="form.nickName" placeholder="请输入昵称"></el-input>
    </el-form-item>
      <el-form-item label="用户身份">
        <el-radio-group v-model="form.role">
          <el-radio label="buyer">买家</el-radio>
          <el-radio label="seller">卖家</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitRegister">注册</el-button>
        <el-button @click="$router.push('/login')">去登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api'

const router = useRouter()
const formRef = ref(null)
const form = ref({
  username: '',
  password: '',
  nickName: '', // 新增昵称
  role: 'buyer'
})

// 提交注册
const submitRegister = async () => {
    try {
        await register(form.value)
        alert('注册成功，请登录')
        router.push('/login')
    } catch (error) {
        console.error('注册异常：', error)
        alert('注册失败，请重试')
    }
}
</script>

<style scoped>
.register-box {
  width: 400px;
  margin: 100px auto;
}
</style>