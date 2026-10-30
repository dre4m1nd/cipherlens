<template>
  <div class="w-full min-h-screen box-border flex items-center justify-center p-4 bg-white bg-center bg-no-repeat bg-cover overflow-x-hidden">
    <div class="w-[1160px] max-w-full flex bg-white rounded-md min-h-[440px] overflow-hidden auth-card">
      <div class="relative hidden lg:block flex-1 auth-band">
        <span class="auth-band-glyph" aria-hidden="true">{{ systemMark }}</span>
        <div class="auth-band-content">
          <div class="auth-band-name">{{ systemTitle }}</div>
          <div class="auth-band-sub">{{ systemSubTitle }}</div>
          <span class="auth-band-rule" aria-hidden="true"></span>
        </div>
      </div>

      <div class="w-[500px] max-w-full">
        <h1 class="mt-[56px] text-center">欢迎登录</h1>
        <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-position="top"
            class="auth-form w-[80%] mx-auto mt-[32px] mb-[15px]"
            @submit.prevent="submit"
        >
          <el-form-item prop="username" label="用户名" for="login-username" class="mb-5">
            <el-input
                size="large"
                v-model="form.username"
                id="login-username"
                placeholder="例如：zhangsan"
                type="text"
                name="username"
                autocomplete="username"
                spellcheck="false"
                maxlength="50"
            >
              <template #prepend>
                <i class="sfont system-xingmingyonghumingnicheng" aria-hidden="true"></i>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password" label="密码" for="login-password" class="mb-5">
            <el-input
                size="large"
                v-model="form.password"
                id="login-password"
                :type="passwordType"
                placeholder="输入登录密码"
                name="password"
                autocomplete="current-password"
                maxlength="50"
            >
              <template #prepend>
                <i class="sfont system-mima" aria-hidden="true"></i>
              </template>
              <template #append>
                <el-button
                    text
                    :aria-pressed="passwordType === 'text'"
                    :aria-label="passwordType === 'text' ? '隐藏密码' : '显示密码'"
                    @click="passwordTypeChange"
                >
                  <i
                      class="sfont"
                      :class="passwordType === 'text' ? 'system-yanjing' : 'system-yanjing-guan'"
                      aria-hidden="true"
                  ></i>
                </el-button>
              </template>
            </el-input>
          </el-form-item>

          <el-button type="primary" native-type="submit" :loading="form.loading" class="w-full" size="medium">
            登录
          </el-button>
          <div class="text-center mt-3">
            <span>没有账号？</span>
            <router-link to="/register" class="auth-link">立即注册</router-link>
            <br>
            <span class="mt-2 inline-block">忘记密码？</span>
            <router-link to="/forgot-password" class="auth-link">立即重置</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script lang="js">
import { systemTitle, systemSubTitle, systemMark } from '@/config'
import { defineComponent, ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import { getAuthRoutes } from '@/router/permission'
import { ElMessage } from 'element-plus'

export default defineComponent({
  setup() {
    const store = useStore()
    const router = useRouter()
    const route = useRoute()
    const formRef = ref(null)
    const form = reactive({
      username: '',
      password: '',
      loading: false
    })
    const rules = {
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
    }
    const passwordType = ref('password')
    const passwordTypeChange = () => {
      passwordType.value = passwordType.value === 'text' ? 'password' : 'text'
    }
    const submit = () => {
      formRef.value.validate((valid) => {
        if (!valid) {
          return
        }
        form.loading = true
        const params = {
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
          await getAuthRoutes()
          await router.push(route.query.redirect || '/')
        })
        .catch(err => {
          ElMessage.error({
            message: err.message || '登录失败，请重试',
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

    return {
      systemMark,
      systemTitle,
      systemSubTitle,
      formRef,
      form,
      rules,
      passwordType,
      passwordTypeChange,
      submit
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
