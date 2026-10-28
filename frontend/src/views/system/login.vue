<template>
  <div class="relative w-screen h-screen bg-white bg-center bg-no-repeat bg-cover overflow-hidden cursor-pointer select-none">
    <div class="w-[1160px] flex absolute left-1/2 top-1/2 bg-white rounded-md transform -translate-x-1/2 -translate-y-1/2 h-[440px] overflow-hidden shadow-[0_6px_20px_5px_rgba(152,152,152,0.1),0_16px_24px_2px_rgba(117,117,117,0.14)]">
      <div class="relative hidden lg:block">
        <img :src="loginLeftPng" class="h-[440px]"/>
        <div class="absolute left-0 top-0 w-full h-full bg-gradient-to-r from-[rgba(0,204,222,0.8)] to-[rgba(51,132,224,0.8)] text-center text-white text-[1.8rem] flex items-center justify-center flex-col tracking-[2px]">
          <div class="text-[3.5rem] mb-4">{{ systemTitle }}</div>
          <div>{{ systemSubTitle }}</div>
        </div>
      </div>

      <div class="w-[500px]">
        <h1 class="mt-[80px] text-center">欢迎登录</h1>
        <el-form class="w-[80%] mx-auto mt-[50px] mb-[15px]">
          <el-input
              size="large"
              v-model="form.username"
              placeholder="用户名"
              type="text"
              maxlength="50"
              class="mb-5"
          >
            <template #prepend>
              <i class="sfont system-xingmingyonghumingnicheng"></i>
            </template>
          </el-input>
          <el-input
              size="large"
              ref="password"
              v-model="form.password"
              :type="passwordType"
              placeholder="密码"
              name="password"
              maxlength="50"
              class="mb-5"
          >
            <template #prepend>
              <i class="sfont system-mima"></i>
            </template>
            <template #append>
              <i class="sfont cursor-pointer text-primary" :class="passwordType ? 'system-yanjing-guan': 'system-yanjing'"
                 @click="passwordTypeChange"></i>
            </template>
          </el-input>

          <el-button type="primary" :loading="form.loading" @click="submit" class="w-full" size="medium">
            登录
          </el-button>
          <div class="text-center mt-3">
            <span>没有账号？</span>
            <el-link type="primary" :underline="false" @click="goToRegister">立即注册</el-link>
            <br>
            <span class="mt-2 inline-block">忘记密码？</span>
            <el-link type="primary" :underline="false" @click="goToForgotPassword">立即重置</el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script lang="js">
import { systemTitle, systemSubTitle } from '@/config'
import { defineComponent, ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import { getAuthRoutes } from '@/router/permission'
import { ElMessage } from 'element-plus'
import loginLeftPng from '@/assets/login/left.jpg';

export default defineComponent({
  setup() {
    const store = useStore()
    const router = useRouter()
    const route = useRoute()
    const form = reactive({
      username: 'dre4m1nd',
      password: '12345678',
      loading: false
    })
    const passwordType = ref('password')
    const passwordTypeChange = () => {
      passwordType.value === '' ? passwordType.value = 'password' : passwordType.value = ''
    }
    const checkForm = () => {
      return new Promise((resolve, reject) => {
        if (form.username === '') {
          ElMessage.warning({
            message: '用户名不能为空',
            type: 'warning'
          });
          return;
        }
        if (form.password === '') {
          ElMessage.warning({
            message: '密码不能为空',
            type: 'warning'
          })
          return;
        }
        resolve(true)
      })
    }
    const submit = () => {
      checkForm()
      .then(() => {
        form.loading = true
        let params = {
          username: form.username,
          password: form.password
        }
        store.dispatch('user/login', params)
        .then(async () => {
          ElMessage.success({
            message: '登录成功',
            type: 'success',
            showClose: true,
            duration: 1000
          })
          // location.reload()
          await getAuthRoutes()
          await router.push(route.query.redirect || '/')
        })
        .catch(err => {
          console.error('Login action error:', err)
          ElMessage.error({
            message: err.response?.data?.msg || '登录失败，请重试',
            type: 'error',
            showClose: true,
            duration: 2000
          })
        })
        .finally(() => {
          form.loading = false
        })
      })
    }
    
    const goToRegister = () => {
      router.push('/register')
    }
    
    const goToForgotPassword = () => {
      router.push('/forgot-password')
    }
    
    return {
      loginLeftPng,
      systemTitle,
      systemSubTitle,
      form,
      passwordType,
      passwordTypeChange,
      submit,
      goToRegister,
      goToForgotPassword
    }
  }
})
</script>

<style scoped>
/* 背景图片通过内联样式或外部CSS处理 */
.container {
  background-image: url('@/assets/login/bg.png');
}
</style>
