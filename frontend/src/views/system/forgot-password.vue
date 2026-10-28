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
        <h1 class="mt-[50px] text-center">忘记密码</h1>
        <el-form class="w-[80%] mx-auto mt-[30px] mb-[15px]">
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
              v-model="form.email"
              placeholder="邮箱"
              type="email"
              maxlength="50"
              class="mb-5"
          >
            <template #prepend>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
          <el-input
              size="large"
              ref="password"
              v-model="form.password"
              :type="passwordType"
              placeholder="新密码"
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
            重置密码
          </el-button>
          <div class="text-center mt-3">
            <span>记得密码了？</span>
            <el-link type="primary" :underline="false" @click="goToLogin">立即登录</el-link>
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
import { ElMessage } from 'element-plus'
import loginLeftPng from '@/assets/login/left.jpg';
import { resetPasswordApi } from '@/api/user'

export default defineComponent({
  setup() {
    const store = useStore()
    const router = useRouter()
    const route = useRoute()
    const form = reactive({
      username: '',
      email: '',
      password: '',
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
        if (form.email === '') {
          ElMessage.warning({
            message: '邮箱不能为空',
            type: 'warning'
          })
          return;
        }
        if (form.password === '') {
          ElMessage.warning({
            message: '新密码不能为空',
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
          email: form.email,
          password: form.password
        }
        resetPasswordApi(params)
        .then(res => {
          if (res && res.code === 200) {
            ElMessage.success({
              message: '密码重置成功',
              type: 'success',
              showClose: true,
              duration: 1000
            })
            router.push('/login')
          } else {
            ElMessage.error(res?.message || '密码重置失败')
          }
        })
        .catch(err => {
          console.error('Reset password error:', err)
          ElMessage.error('密码重置失败，请重试')
        })
        .finally(() => {
          form.loading = false
        })
      })
    }
    
    const goToLogin = () => {
      router.push('/login')
    }
    
    return {
      loginLeftPng,
      systemTitle,
      systemSubTitle,
      form,
      passwordType,
      passwordTypeChange,
      submit,
      goToLogin
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