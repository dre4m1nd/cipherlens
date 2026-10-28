<template>
  <div class="password-form-container">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="应用名称" prop="appName">
        <el-input v-model="form.appName" placeholder="请输入应用名称" maxlength="50" />
      </el-form-item>
      <el-form-item label="账号" prop="account">
        <el-input v-model="form.account" placeholder="请输入账号" maxlength="50" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" type="password" placeholder="请输入密码（不修改请保持为空）" maxlength="100" />
      </el-form-item>
      <el-form-item label="应用地址" prop="url">
        <el-input v-model="form.url" placeholder="请输入应用地址" maxlength="100" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注信息"
          maxlength="200"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm(formRef)">提交</el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="js">
import { defineComponent, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getPasswordItemApi, updatePasswordItemApi } from '@/api/passwordItem'

export default defineComponent({
  props: {
    passwordId: {
      type: String,
      required: true
    }
  },
  emits: ['success', 'cancel'],
  setup(props, { emit }) {
    const formRef = ref(null)
    const form = reactive({
      id: '',
      appName: '',
      account: '',
      password: '',
      url: '',
      remark: ''
    })
    const rules = reactive({
      appName: [
        { required: true, message: '请输入应用名称', trigger: 'blur' },
        { max: 50, message: '应用名称不能超过50个字符', trigger: 'blur' }
      ],
      account: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { max: 50, message: '账号不能超过50个字符', trigger: 'blur' }
      ],
      password: [
        { min: 6, message: '密码不能少于6个字符', trigger: 'blur' },
        { max: 100, message: '密码不能超过100个字符', trigger: 'blur' }
      ],
      url: [
        { max: 100, message: '应用地址不能超过100个字符', trigger: 'blur' },
        { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
      ],
      remark: [
        { max: 200, message: '备注信息不能超过200个字符', trigger: 'blur' }
      ]
    })

    // 加载密码项数据
    const loadPasswordItem = () => {
      const id = props.passwordId
      getPasswordItemApi(id)
        .then(res => {
          const data = res.data
          form.id = data.id
          form.appName = data.appName
          form.account = data.account
          form.url = data.url
          form.remark = data.remark
        })
        .catch(err => {
          ElMessage.error('加载密码项数据失败')
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
          updatePasswordItemApi(params.id, params)
            .then(() => {
              ElMessage.success('更新成功')
              emit('success')
            })
            .catch(err => {
              ElMessage.error('更新失败')
            })
        }
      })
    }

    const handleCancel = () => {
      emit('cancel')
    }

    // 监听passwordId变化，加载数据
    watch(() => props.passwordId, (newId) => {
      if (newId) {
        loadPasswordItem()
      }
    }, { immediate: true })

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
.password-form-container {
  padding: 20px 0;
}
</style>