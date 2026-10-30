<template>
  <div class="user-edit-container">
    <el-card class="user-edit-card">
      <template #header>
        <div class="card-header">
          <span>修改用户信息</span>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" @submit.prevent="submitForm(formRef)">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" name="username" autocomplete="username" spellcheck="false" placeholder="请输入用户名" maxlength="50" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" name="new-password" autocomplete="new-password" placeholder="请输入密码（不修改请保持为空）" maxlength="64" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit">提交</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="js">
import { defineComponent, reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getInfoApi, updateUserApi } from '@/api/user'

export default defineComponent({
  setup() {
    const formRef = ref(null)
    const form = reactive({
      username: '',
      password: ''
    })
    const rules = reactive({
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { max: 50, message: '用户名不能超过50个字符', trigger: 'blur' }
      ],
      password: [
        { min: 8, max: 64, message: '密码长度需在 8~64 位之间', trigger: 'blur' }
      ]
    })

    // 加载用户信息
    const loadUserInfo = () => {
      getInfoApi()
        .then(res => {
          const data = res.data
          form.username = data.username
          form.password = '' // 密码不显示，每次修改都需要重新输入
        })
        .catch(err => {
          ElMessage.error('加载用户信息失败')
        })
    }

    const submitForm = (formRef) => {
      formRef.validate((valid) => {
        if (valid) {
          // 如果密码为空，则不更新密码
          const params = { ...form }
          if (params.password === '') {
            delete params.password
          }
          updateUserApi(params)
            .then(() => {
              ElMessage.success('更新成功')
            })
            .catch(err => {
              ElMessage.error('更新失败')
            })
        }
      })
    }

    const handleCancel = () => {
      // 可以添加重置表单逻辑或返回上一页
      loadUserInfo() // 重新加载原始数据
    }

    onMounted(() => {
      loadUserInfo()
    })

    return {
      formRef,
      form,
      rules,
      submitForm,
      handleCancel
    }
  }
})
</script>

<style scoped>
.user-edit-container {
  padding: 20px;
}

.user-edit-card {
  max-width: 600px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>