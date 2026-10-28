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
        <el-input v-model="form.password" type="password" placeholder="请输入密码" maxlength="100" />
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
        <el-button @click="resetForm(formRef)">重置</el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="js">
import { defineComponent, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addPasswordItemApi } from '@/api/passwordItem'

export default defineComponent({
  emits: ['success', 'cancel'],
  setup(props, { emit }) {
    const formRef = ref(null)
    const form = reactive({
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
        { required: true, message: '请输入密码', trigger: 'blur' },
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

    const submitForm = (formRef) => {
      formRef.validate((valid) => {
        if (valid) {
          addPasswordItemApi(form)
            .then(() => {
              ElMessage.success('添加成功')
              emit('success')
              resetForm(formRef)
            })
            .catch(err => {
              ElMessage.error('添加失败')
            })
        }
      })
    }

    const resetForm = (formRef) => {
      formRef.resetFields()
    }

    const handleCancel = () => {
      emit('cancel')
    }

    // 监听表单重置
    watch(() => formRef.value, (newVal) => {
      if (newVal) {
        resetForm(newVal)
      }
    })

    return {
      formRef,
      form,
      rules,
      submitForm,
      resetForm,
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