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
        <h1 class="mt-[40px] text-center">用户注册</h1>
        <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-position="top"
            class="auth-form w-[80%] mx-auto mt-[24px] mb-[15px]"
            @submit.prevent="submit"
        >
          <el-form-item prop="username" label="用户名" for="reg-username" class="mb-4">
            <el-input
                size="large"
                v-model="form.username"
                id="reg-username"
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
          <el-form-item prop="password" label="密码" for="reg-password" class="mb-4">
            <el-input
                size="large"
                v-model="form.password"
                id="reg-password"
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
          <el-form-item prop="confirmPassword" label="确认密码" for="reg-confirm-password" class="mb-4">
            <el-input
                size="large"
                v-model="form.confirmPassword"
                id="reg-confirm-password"
                :type="passwordType"
                placeholder="再次输入密码"
                name="confirm-password"
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
          <el-form-item prop="email" label="邮箱" for="reg-email" class="mb-4">
            <el-input
                size="large"
                v-model="form.email"
                id="reg-email"
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

          <el-button type="primary" native-type="submit" :loading="form.loading" class="w-full mt-2" size="medium">
            注册
          </el-button>
          <div class="text-center mt-4">
            <span>已有账号？</span>
            <router-link to="/login" class="auth-link ml-1">立即登录</router-link>
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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default defineComponent({
  setup() {
    const store = useStore()
    const router = useRouter()
    const formRef = ref(null)
    const form = reactive({
      username: '',
      password: '',
      confirmPassword: '',
      email: '',
      loading: false
    })
    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { max: 50, message: '用户名不能超过 50 个字符', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 8, max: 64, message: '密码长度需在 8~64 位之间', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请再次输入密码', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            if (value !== form.password) {
              callback(new Error('两次输入的密码不一致'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
      ]
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
          password: form.password,
          email: form.email
        }
        store.dispatch('user/register', params)
          .then(() => {
            ElMessage.success({
              message: '注册成功',
              type: 'success',
              showClose: true,
              duration: 1000
            })
            router.push('/login')
          })
          .catch(err => {
            ElMessage.error({
              message: err.message || '注册失败',
              type: 'error'
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
