<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>密码项列表</span>
          <el-button type="primary" @click="handleAdd" class="ml-4">
            <i class="sfont system-tianjia mr-1"></i>添加密码项
          </el-button>
        </div>
      </template>
      <el-form :inline="true" class="demo-form-inline mb-4">
        <el-form-item label="应用名称">
          <el-input v-model="queryParams.appName" placeholder="请输入应用名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="账号">
          <el-input v-model="queryParams.account" placeholder="请输入账号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <i class="sfont system-cha询 mr-1"></i>查询
          </el-button>
          <el-button @click="resetQuery">
            <i class="sfont system-chongzhi mr-1"></i>重置
          </el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="passwordList" border style="width: 100%" stripe size="small">
        <!-- 序号列，替换ID列 -->
        <el-table-column type="index" label="序号" width="97" align="center" />
        <!-- 合理分配列宽，确保操作列有足够空间 -->
        <el-table-column prop="appName" label="应用名称" width="129" show-overflow-tooltip align="center" />
        <el-table-column prop="account" label="账号" width="150" show-overflow-tooltip align="center" />
        <el-table-column prop="remark" label="备注" width="179" show-overflow-tooltip align="center" />
        <el-table-column prop="createTime" label="创建时间" width="192" align="center" />
        <el-table-column prop="updateTime" label="更新时间" width="157" align="center" />
        <!-- 优化操作按钮排列，确保有足够空间显示所有按钮 -->
        <el-table-column label="操作" width="280" align="center">
          <template #default="scope">
            <div style="display: flex; gap: 5px; flex-wrap: nowrap;">
              <el-button size="small" @click="handleView(scope.row)">
                <i class="sfont system-chakan mr-1"></i>查看
              </el-button>
              <el-button size="small" type="primary" @click="handleEdit(scope.row)">
                <i class="sfont system-bianji mr-1"></i>编辑
              </el-button>
              <el-button size="small" type="success" @click="handleEvaluate(scope.row)">
                <i class="sfont system-pinggu mr-1"></i>评估
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row)">
                <i class="sfont system-shanchu mr-1"></i>删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container mt-4">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <!-- 添加密码项弹窗 -->
      <el-dialog v-model="addDialogVisible" title="添加密码项" width="500px">
        <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="100px">
          <el-form-item label="应用名称" prop="appName">
            <el-input v-model="addForm.appName" placeholder="请输入应用名称" maxlength="50" />
          </el-form-item>
          <el-form-item label="账号" prop="account">
            <el-input v-model="addForm.account" placeholder="请输入账号" maxlength="50" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="addForm.password" type="password" placeholder="请输入密码" maxlength="100" />
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="addForm.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息"
              maxlength="200"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddForm">提交</el-button>
        </template>
      </el-dialog>

      <!-- 编辑密码项弹窗 -->
      <el-dialog v-model="editDialogVisible" title="编辑密码项" width="500px">
        <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
          <el-form-item label="应用名称" prop="appName">
            <el-input v-model="editForm.appName" placeholder="请输入应用名称" maxlength="50" />
          </el-form-item>
          <el-form-item label="账号" prop="account">
            <el-input v-model="editForm.account" placeholder="请输入账号" maxlength="50" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="editForm.password" type="password" placeholder="请输入密码" maxlength="100" />
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="editForm.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息"
              maxlength="200"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditForm">提交</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script lang="js">
import { defineComponent, ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPasswordItemListApi, deletePasswordItemApi, addPasswordItemApi, getPasswordItemApi, updatePasswordItemApi, getPasswordPlainTextApi, evaluatePasswordApi } from '@/api/passwordItem'

export default defineComponent({
  
  setup() {
    const router = useRouter()
    const addDialogVisible = ref(false)
    const editDialogVisible = ref(false)
    const editPasswordId = ref('')
    const addFormRef = ref(null)
    const editFormRef = ref(null)
    const loading = ref(false)
    const total = ref(0)
    const queryParams = reactive({
      appName: '',
      account: ''
    })
    const pagination = reactive({
      pageNum: 1,
      pageSize: 10
    })
    const passwordList = ref([])

    // 添加密码表单
    const addForm = reactive({
      appName: '',
      account: '',
      password: '',
      remark: ''
    })

    // 添加密码表单验证规则
    const addRules = reactive({
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

      remark: [
        { max: 200, message: '备注信息不能超过200个字符', trigger: 'blur' }
      ]
    })

    // 编辑密码表单
    const editForm = reactive({
      id: '',
      appName: '',
      account: '',
      password: '',
      remark: ''
    })

    // 编辑密码表单验证规则
    const editRules = reactive({
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

      remark: [
        { max: 200, message: '备注信息不能超过200个字符', trigger: 'blur' }
      ]
    })

    // 获取密码项列表
    const getPasswordList = () => {
      console.log('=== 开始获取密码项列表 ===')
      loading.value = true
      const data = {
        ...queryParams,
        ...pagination
      }
      console.log('请求参数:', data)
      console.log('API函数:', getPasswordItemListApi)
      getPasswordItemListApi(data)
        .then(res => {
          console.log('响应结果完整数据:', res)
          console.log('响应数据类型:', typeof res)
          console.log('响应数据是否为对象:', res instanceof Object)
          // 处理后端返回的完整数据结构 {code, message, data}
          if (res && res.code === 200 && res.data) {
            console.log('res.data:', res.data)
            console.log('res.data类型:', typeof res.data)
            // 根据响应格式，res.data包含records数组和分页信息
            if (res.data.records) {
              passwordList.value = Array.isArray(res.data.records) ? res.data.records : []
              total.value = Number(res.data.total) || passwordList.value.length
            } else {
              // 兼容旧格式
              passwordList.value = Array.isArray(res.data) ? res.data : []
              total.value = passwordList.value.length
            }
            console.log('passwordList.value:', passwordList.value)
            console.log('total.value:', total.value)
          } else {
            console.error('响应数据结构异常:', res)
            passwordList.value = []
            total.value = 0
          }
          loading.value = false
        })
        .catch(err => {
          console.error('请求错误完整信息:', err)
          console.error('错误消息:', err.message)
          console.error('错误响应:', err.response)
          loading.value = false
          ElMessage.error('获取密码项列表失败：' + (err.msg || err.message || '未知错误'))
        })
        .finally(() => {
          console.log('=== 获取密码项列表结束 ===')
        })
    }

    // 查询
    const handleQuery = () => {
      pagination.pageNum = 1
      getPasswordList()
    }

    // 重置查询条件
    const resetQuery = () => {
      queryParams.appName = ''
      queryParams.account = ''
      pagination.pageNum = 1
      getPasswordList()
    }

    // 分页大小变化
    const handleSizeChange = (val) => {
      pagination.pageSize = val
      getPasswordList()
    }

    // 当前页码变化
    const handleCurrentChange = (val) => {
      pagination.pageNum = val
      getPasswordList()
    }

    // 添加密码项
    const handleAdd = () => {
      // 重置表单
      if (addFormRef.value) {
        addFormRef.value.resetFields()
      }
      // 清空表单数据
      addForm.appName = ''
      addForm.account = ''
      addForm.password = ''
      addForm.remark = ''
      
      console.log('handleAdd clicked, addDialogVisible before:', addDialogVisible.value)
      addDialogVisible.value = true
      console.log('handleAdd clicked, addDialogVisible after:', addDialogVisible.value)
    }

    // 提交添加密码表单
    const submitAddForm = () => {
      if (addFormRef.value) {
        addFormRef.value.validate((valid) => {
          if (valid) {
            // 调用添加密码项API
            addPasswordItemApi(addForm)
              .then(() => {
                ElMessage.success('添加成功')
                addDialogVisible.value = false
                // 重新获取密码项列表
                getPasswordList()
              })
              .catch(err => {
                ElMessage.error('添加失败')
              })
          }
        })
      }
    }

    // 编辑密码项
    const handleEdit = (row) => {
      console.log('handleEdit clicked, row:', row)
      // 设置编辑密码项ID
      editForm.id = row.id
      // 加载密码项详情
      getPasswordItemApi(row.id)
        .then(res => {
          console.log('getPasswordItemApi response:', res)
          // 设置表单数据
          editForm.appName = res.data.appName
          editForm.account = res.data.account
          editForm.password = res.data.password
          editForm.remark = res.data.remark
          // 显示编辑弹窗
          editDialogVisible.value = true
          console.log('editDialogVisible set to:', editDialogVisible.value)
        })
        .catch(err => {
          console.error('加载密码项详情失败:', err)
          ElMessage.error('加载密码项详情失败')
        })
    }

    // 提交编辑密码表单
    const submitEditForm = () => {
      if (editFormRef.value) {
        editFormRef.value.validate((valid) => {
          if (valid) {
            console.log('submitEditForm, editForm:', editForm)
            // 调用更新密码项API
            // 注意：API需要两个参数：id和data
            updatePasswordItemApi(editForm.id, editForm)
              .then(() => {
                ElMessage.success('更新成功')
                editDialogVisible.value = false
                // 重新获取密码项列表
                getPasswordList()
              })
              .catch(err => {
                console.error('更新密码项失败:', err)
                ElMessage.error('更新失败')
              })
          }
        })
      }
    }





    // 查看密码项明文
    const handleView = (row) => {
      getPasswordPlainTextApi(row.id)
        .then(res => {
          // 使用弹窗显示密码，而不是消息提示
          ElMessageBox.alert(`明文密码：${res.data.password}`, '密码信息', {
            confirmButtonText: '关闭',
            type: 'info',
            dangerouslyUseHTMLString: false
          })
        })
        .catch(err => {
          ElMessage.error('获取明文密码失败')
        })
    }

    // 删除密码项
    const handleDelete = (row) => {
      ElMessageBox.confirm('确定要删除该密码项吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      .then(() => {
        return deletePasswordItemApi(row.id)
      })
      .then(() => {
        ElMessage.success('删除成功')
        getPasswordList()
      })
      .catch(err => {
        if (err !== 'cancel') {
          ElMessage.error('删除失败')
        }
      })
    }

    // 密码评估
    const handleEvaluate = (row) => {
      // 调用密码评估API
      evaluatePasswordApi(row.id)
        .then(res => {
          // 使用弹窗显示评估结果
          if (res && res.code === 200) {
            const evaluateData = res.data;
            // 使用HTML格式美化弹窗内容，移除密码项ID的显示
            let message = `<div style="padding: 10px;">`;
            message += `<div style="margin-bottom: 10px;"><strong>评估分数:</strong> <span style="color: ${evaluateData.evaluateScore >= 80 ? '#67c23a' : evaluateData.evaluateScore >= 60 ? '#e6a23c' : '#f56c6c'}; font-size: 18px;">${evaluateData.evaluateScore}</span></div>`;
            message += `<div style="margin-bottom: 10px;"><strong>风险类型:</strong> <span>${evaluateData.riskType}</span></div>`;
            message += `<div style="margin-bottom: 10px;"><strong>评估建议:</strong> <span style="color: #909399;">${evaluateData.suggestions}</span></div>`;
            message += `<div style="margin-bottom: 10px;"><strong>评估时间:</strong> <span style="color: #909399; font-size: 12px;">${evaluateData.evaluateTime}</span></div>`;
            message += `</div>`;
            
            ElMessageBox.alert(message, '密码评估结果', {
              confirmButtonText: '关闭',
              type: 'info',
              dangerouslyUseHTMLString: true
            });
          } else {
            ElMessage.error(res?.message || '评估失败');
          }
        })
        .catch(err => {
          console.error('评估失败:', err);
          ElMessage.error('评估失败');
        });
    }

    onMounted(() => {
      getPasswordList()
    })

    return {
      loading,
      total,
      queryParams,
      passwordList,
      pagination,
      handleQuery,
      resetQuery,
      handleSizeChange,
      handleCurrentChange,
      handleAdd,
      handleEdit,
      handleView,
      handleDelete,
      handleEvaluate,
      addDialogVisible,
      editDialogVisible,
      editPasswordId,
      addFormRef,
      addForm,
      addRules,
      submitAddForm,
      editFormRef,
      editForm,
      editRules,
      submitEditForm
    }
  }
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
}

/* 移除可能导致表头与内容宽度不一致的样式 */
</style>