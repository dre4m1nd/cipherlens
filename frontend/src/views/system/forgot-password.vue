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
        <h1 class="mt-[36px] text-center">忘记密码</h1>
        <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-position="top"
            class="auth-form w-[80%] mx-auto mt-[24px] mb-[15px]"
            @submit.prevent="submit"
        >
          <el-form-item prop="username" label="用户名" for="fp-username" class="mb-5">
            <el-input
                size="large"
                v-model="form.username"
                id="fp-username"
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
          <el-form-item prop="email" label="邮箱" for="fp-email" class="mb-5">
            <el-input
                size="large"
                v-model="form.email"
                id="fp-email"
                placeholder="name@example.com"
                type="email"
                name="email"
                autocomplete="email"
                spellcheck="false"
                maxlength="50"
            >
              <template #prepend>
                <el-icon aria-hidden="true"><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code" label="邮箱验证码" for="fp-code" class="mb-5">
            <el-input
                size="large"
                v-model="form.code"
                id="fp-code"
                placeholder="6 位数字"
                name="code"
                autocomplete="one-time-code"
                inputmode="numeric"
                spellcheck="false"
                maxlength="6"
            >
              <template #prepend>
                <el-icon aria-hidden="true"><Key /></el-icon>
              </template>
              <template #append>
                <el-button
                    :disabled="countdown > 0"
                    :loading="sending"
                    @click="sendCode"
                >
                  {{ countdown > 0 ? countdown + ' 秒后重发' : '发送验证码' }}
                </el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password" label="新密码" for="fp-password" class="mb-5">
            <el-input
                size="large"
                v-model="form.password"
                id="fp-password"
                :type="passwordType"
                placeholder="至少 8 位"
                name="new-password"
                autocomplete="new-password"
                maxlength="64"
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
            重置密码
          </el-button>
          <div class="text-center mt-3">
            <span>记得密码了？</span>
            <router-link to="/login" class="auth-link">立即登录</router-link>
          </div>
          <p class="sr-only" aria-live="polite">{{ liveMessage }}</p>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script lang="js">
import { systemTitle, systemSubTitle, systemMark } from '@/config'
import { defineComponent, ref, reactive, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { resetPasswordApi, sendResetCodeApi } from '@/api/user'

export default defineComponent({
  setup() {
    const router = useRouter()
    const formRef = ref(null)
    const form = reactive({
      username: '',
      email: '',
      code: '',
      password: '',
      loading: false
    })
    const rules = {
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
      ],
      code: [
        { required: true, message: '请输入邮箱验证码', trigger: 'blur' },
        { pattern: /^\d{6}$/, message: '验证码为 6 位数字', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 8, max: 64, message: '密码长度需在 8~64 位之间', trigger: 'blur' }
      ]
    }
    const passwordType = ref('password')
    const sending = ref(false)
    const countdown = ref(0)
    const liveMessage = ref('')
    let countdownTimer = null

    const passwordTypeChange = () => {
      passwordType.value = passwordType.value === 'text' ? 'password' : 'text'
    }

    // 发送邮箱验证码（60 秒冷却，与后端限制一致）
    const sendCode = () => {
      if (!form.username) {
        ElMessage.warning('请先输入用户名')
        return
      }
      if (!form.email) {
        ElMessage.warning('请先输入邮箱')
        return
      }
      sending.value = true
      sendResetCodeApi({ username: form.username, email: form.email })
        .then(() => {
          ElMessage.success('验证码已发送，请查收邮箱')
          liveMessage.value = '验证码已发送，请查收邮箱'
          countdown.value = 60
          countdownTimer = setInterval(() => {
            countdown.value -= 1
            if (countdown.value <= 0) {
              clearInterval(countdownTimer)
              countdownTimer = null
            }
          }, 1000)
        })
        .catch(() => {
          // 错误提示已由 request 拦截器统一处理
        })
        .finally(() => {
          sending.value = false
        })
    }

    const submit = () => {
      formRef.value.validate((valid) => {
        if (!valid) {
          return
        }
        form.loading = true
        const params = {
          username: form.username,
          email: form.email,
          code: form.code,
          password: form.password
        }
        resetPasswordApi(params)
          .then(() => {
            ElMessage.success({
              message: '密码重置成功',
              type: 'success',
              showClose: true,
              duration: 1000
            })
            router.push('/login')
          })
          .catch(() => {
            ElMessage.error('密码重置失败，请重试')
          })
          .finally(() => {
            form.loading = false
          })
      })
    }

    onUnmounted(() => {
      if (countdownTimer) {
        clearInterval(countdownTimer)
      }
    })

    return {
      systemMark,
      systemTitle,
      systemSubTitle,
      formRef,
      form,
      rules,
      passwordType,
      passwordTypeChange,
      sending,
      countdown,
      liveMessage,
      sendCode,
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
