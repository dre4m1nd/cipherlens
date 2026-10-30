<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>密码项列表</span>
          <el-button type="primary" @click="handleAdd" class="ml-4">
            添加密码项
          </el-button>
        </div>
      </template>
      <el-form :inline="true" class="demo-form-inline mb-4" @submit.prevent="handleQuery">
        <el-form-item label="应用名称" for="query-app-name">
          <el-input id="query-app-name" v-model="queryParams.appName" name="appName" autocomplete="off" placeholder="例如：GitHub" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="账号" for="query-account">
          <el-input id="query-account" v-model="queryParams.account" name="account" autocomplete="off" placeholder="例如：zhangsan@example.com" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit">
            查询
          </el-button>
          <el-button @click="resetQuery">
            重置
          </el-button>
        </el-form-item>
      </el-form>
      <div v-if="selectedRows.length" class="batch-bar" role="region" aria-label="批量操作">
        <span class="batch-count">已选 {{ selectedRows.length }} 项</span>
        <div class="batch-actions">
          <el-button size="small" @click="batchEvaluate">批量评估</el-button>
          <el-button size="small" @click="exportCsv">导出 CSV</el-button>
          <el-button size="small" type="danger" plain @click="batchDelete">批量删除</el-button>
          <el-button size="small" text @click="clearSelection">取消选择</el-button>
        </div>
      </div>

      <el-table
        ref="tableRef"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        :data="passwordList"
        border
        stripe
        size="small"
        style="width: 100%"
        empty-text="还没有密码项，点右上角「添加密码项」记录第一条"
      >
        <el-table-column type="selection" width="44" align="center" />
        <!-- 序号列，替换ID列 -->
        <el-table-column type="index" label="序号" width="72" align="center" />
        <!-- 合理分配列宽，确保操作列有足够空间 -->
        <el-table-column prop="appName" label="应用名称" min-width="120" show-overflow-tooltip align="center" />
        <el-table-column prop="account" label="账号" min-width="140" show-overflow-tooltip align="center" />
        <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip align="center" />
        <el-table-column prop="createTime" label="创建时间" width="160" align="center" />
        <el-table-column prop="updateTime" label="更新时间" width="160" align="center" />
        <!-- 优化操作按钮排列，确保有足够空间显示所有按钮 -->
        <el-table-column label="操作" min-width="230" align="center">
          <template #default="scope">
            <div style="display: flex; gap: 4px; flex-wrap: nowrap; justify-content: center; align-items: center;">
              <el-button size="small" text @click="handleView(scope.row)">查看
              </el-button>
              <el-button size="small" text @click="handleEdit(scope.row)">编辑
              </el-button>
              <el-button size="small" type="primary" @click="handleEvaluate(scope.row)">评估
              </el-button>
              <el-button size="small" text type="danger" @click="handleDelete(scope.row)">删除
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
        <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="100px" @submit.prevent="submitAddForm">
          <button type="submit" class="hidden" aria-hidden="true" tabindex="-1"></button>
          <el-form-item label="应用名称" prop="appName" for="add-app-name">
            <el-input id="add-app-name" v-model="addForm.appName" name="appName" autocomplete="off" placeholder="例如：GitHub" maxlength="50" />
          </el-form-item>
          <el-form-item label="账号" prop="account" for="add-account">
            <el-input id="add-account" v-model="addForm.account" name="account" autocomplete="off" placeholder="例如：zhangsan@example.com" maxlength="50" />
          </el-form-item>
          <el-form-item label="密码" prop="password" for="add-password">
            <el-input id="add-password" v-model="addForm.password" type="password" name="new-password" autocomplete="new-password" placeholder="至少 8 位" maxlength="100" show-password>
              <template #append>
                <el-button aria-label="生成随机密码" @click="openGenerator('add')">生成</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="备注" prop="remark" for="add-remark">
            <el-input
              id="add-remark"
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
        <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px" @submit.prevent="submitEditForm">
          <button type="submit" class="hidden" aria-hidden="true" tabindex="-1"></button>
          <el-form-item label="应用名称" prop="appName" for="edit-app-name">
            <el-input id="edit-app-name" v-model="editForm.appName" name="appName" autocomplete="off" placeholder="例如：GitHub" maxlength="50" />
          </el-form-item>
          <el-form-item label="账号" prop="account" for="edit-account">
            <el-input id="edit-account" v-model="editForm.account" name="account" autocomplete="off" placeholder="例如：zhangsan@example.com" maxlength="50" />
          </el-form-item>
          <el-form-item label="密码" prop="password" for="edit-password">
            <el-input id="edit-password" v-model="editForm.password" type="password" name="new-password" autocomplete="new-password" placeholder="不修改请留空" maxlength="100" show-password>
              <template #append>
                <el-button aria-label="生成随机密码" @click="openGenerator('edit')">生成</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="备注" prop="remark" for="edit-remark">
            <el-input
              id="edit-remark"
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

      <!-- 密码评估结果弹窗 -->
      <el-dialog v-model="evaluateDialogVisible" title="密码评估结果" width="500px">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="评估分数">
            <el-tag :type="evaluateScoreType">{{ evaluateResult.evaluateScore }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="风险类型">{{ evaluateResult.riskType || '无' }}</el-descriptions-item>
          <el-descriptions-item label="评估建议">{{ evaluateResult.suggestions }}</el-descriptions-item>
          <el-descriptions-item label="评估时间">{{ evaluateResult.evaluateTime }}</el-descriptions-item>
        </el-descriptions>
        <template #footer>
          <el-button type="primary" @click="evaluateDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>
      <!-- 批量评估结果 -->
      <el-dialog v-model="batchResultVisible" title="批量评估结果" width="560px">
        <el-table :data="batchResults" size="small" border style="width: 100%">
          <el-table-column prop="appName" label="应用名称" min-width="120" show-overflow-tooltip />
          <el-table-column prop="account" label="账号" min-width="140" show-overflow-tooltip />
          <el-table-column label="评估分数" width="100" align="center">
            <template #default="scope">
              <el-tag v-if="scope.row.ok" :type="scoreTagType(scope.row.score)">{{ scope.row.score }}</el-tag>
              <span v-else class="text-muted">失败</span>
            </template>
          </el-table-column>
          <el-table-column prop="riskType" label="风险类型" min-width="120" show-overflow-tooltip>
            <template #default="scope">{{ scope.row.ok ? (scope.row.riskType || '无') : '评估失败（可能未配置 KMS）' }}</template>
          </el-table-column>
        </el-table>
        <template #footer>
          <el-button type="primary" @click="batchResultVisible = false">关闭</el-button>
        </template>
      </el-dialog>

      <!-- 随机密码生成器 -->
      <PasswordGenerator v-model="generatorVisible" @use="applyGenerated" />
    </el-card>
  </div>
</template>

<script lang="js">
import { defineComponent, ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPasswordItemListApi, deletePasswordItemApi, addPasswordItemApi, getPasswordItemApi, updatePasswordItemApi, getPasswordPlainTextApi, evaluatePasswordApi } from '@/api/passwordItem'
import PasswordGenerator from '@/components/PasswordGenerator.vue'

export default defineComponent({
  components: { PasswordGenerator },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const addDialogVisible = ref(false)
    const editDialogVisible = ref(false)
    const tableRef = ref(null)
    const selectedRows = ref([])
    const generatorVisible = ref(false)
    const generatorTarget = ref('add')
    const batchResultVisible = ref(false)
    const batchResults = ref([])
    const batchRunning = ref(false)
    const evaluateDialogVisible = ref(false)
    const evaluateResult = ref({})
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
    const PAGE_SIZES = [10, 20, 30, 50]
    const passwordList = ref([])

    const evaluateScoreType = computed(() => {
      const score = Number(evaluateResult.value.evaluateScore) || 0
      if (score >= 80) return 'success'
      if (score >= 60) return 'warning'
      return 'danger'
    })

    // 筛选/分页状态同步到 URL，便于刷新、分享与后退
    const syncQuery = () => {
      const query = {
        pageNum: String(pagination.pageNum),
        pageSize: String(pagination.pageSize)
      }
      if (queryParams.appName) query.appName = queryParams.appName
      if (queryParams.account) query.account = queryParams.account
      // 仅在参数变化时替换，避免重复导航导致页面重挂载
      if (router.resolve({ path: route.path, query }).fullPath !== route.fullPath) {
        router.replace({ path: route.path, query })
      }
    }

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
        { min: 6, message: '密码不能少于6个字符', trigger: 'blur' },
        { max: 100, message: '密码不能超过100个字符', trigger: 'blur' }
      ],

      remark: [
        { max: 200, message: '备注信息不能超过200个字符', trigger: 'blur' }
      ]
    })

    // 获取密码项列表
    const getPasswordList = () => {
      syncQuery()
      loading.value = true
      const data = {
        ...queryParams,
        ...pagination
      }
      getPasswordItemListApi(data)
        .then(res => {
          // 后端返回 { code, message, data: { records, total } }
          if (res && res.data) {
            passwordList.value = Array.isArray(res.data.records) ? res.data.records : []
            total.value = Number(res.data.total) || passwordList.value.length
          } else {
            passwordList.value = []
            total.value = 0
          }
        })
        .catch(err => {
          ElMessage.error('获取密码项列表失败：' + (err.msg || err.message || '未知错误'))
        })
        .finally(() => {
          loading.value = false
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

      addDialogVisible.value = true
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
              .catch(() => {
                ElMessage.error('添加失败')
              })
          }
        })
      }
    }

    // 编辑密码项
    const handleEdit = (row) => {
      editForm.id = row.id
      // 加载密码项详情（后端返回的是密文，不能回填到密码框，否则会被重复加密）
      getPasswordItemApi(row.id)
        .then(res => {
          editForm.appName = res.data.appName
          editForm.account = res.data.account
          editForm.password = ''
          editForm.remark = res.data.remark
          editDialogVisible.value = true
        })
        .catch(() => {
          ElMessage.error('加载密码项详情失败')
        })
    }

    // 提交编辑密码表单
    const submitEditForm = () => {
      if (editFormRef.value) {
        editFormRef.value.validate((valid) => {
          if (valid) {
            // 密码留空表示不修改，后端会忽略空密码
            updatePasswordItemApi(editForm.id, editForm)
              .then(() => {
                ElMessage.success('更新成功')
                editDialogVisible.value = false
                getPasswordList()
              })
              .catch(() => {
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
        .catch(() => {
          ElMessage.error('获取明文密码失败')
        })
    }

    // 删除密码项
    /* ---------- 密码生成器 ---------- */
    const openGenerator = (target) => {
      generatorTarget.value = target
      generatorVisible.value = true
    }

    const applyGenerated = (pwd) => {
      if (generatorTarget.value === 'add') addForm.password = pwd
      else editForm.password = pwd
    }

    /* ---------- 批量操作 ---------- */
    const handleSelectionChange = (rows) => {
      selectedRows.value = rows
    }

    const clearSelection = () => {
      if (tableRef.value) tableRef.value.clearSelection()
      selectedRows.value = []
    }

    const batchDelete = () => {
      const count = selectedRows.value.length
      if (!count) return
      ElMessageBox.confirm(
          `确定要删除选中的 ${count} 个密码项吗？此操作不可撤销。`,
          '批量删除',
          { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' }
      ).then(async () => {
        const ids = selectedRows.value.map((r) => r.id)
        const results = await Promise.allSettled(ids.map((id) => deletePasswordItemApi(id)))
        const ok = results.filter((r) => r.status === 'fulfilled').length
        const fail = results.length - ok
        if (fail === 0) ElMessage.success(`已删除 ${ok} 项`)
        else ElMessage.warning(`删除完成：成功 ${ok} 项，失败 ${fail} 项`)
        clearSelection()
        handleQuery()
      }).catch(() => {})
    }

    const batchEvaluate = async () => {
      const rows = selectedRows.value.slice()
      if (!rows.length) return
      batchRunning.value = true
      batchResults.value = []
      const out = []
      for (const row of rows) {
        try {
          const res = await evaluatePasswordApi(row.id)
          const data = res && res.data ? res.data : {}
          out.push({
            appName: row.appName,
            account: row.account,
            score: data.evaluateScore,
            riskType: data.riskType,
            ok: true
          })
        } catch (e) {
          out.push({ appName: row.appName, account: row.account, ok: false })
        }
      }
      batchResults.value = out
      batchRunning.value = false
      batchResultVisible.value = true
    }

    const scoreTagType = (score) => {
      if (score == null) return 'info'
      if (score >= 80) return 'success'
      if (score >= 60) return 'warning'
      return 'danger'
    }

    /** 导出选中项为 CSV（不含明文密码） */
    const exportCsv = () => {
      const rows = selectedRows.value
      if (!rows.length) return
      const header = ['应用名称', '账号', '备注', '创建时间', '更新时间']
      const escape = (v) => `"${String(v == null ? '' : v).replace(/"/g, '""')}"`
      const lines = rows.map((r) => [r.appName, r.account, r.remark, r.createTime, r.updateTime].map(escape).join(','))
      const csv = String.fromCharCode(65279) + header.join(",") + '\n' + lines.join('\n')
      const blob = new Blob([csv], { type: 'text/csv;charset=utf-8' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `密码项-${new Date().toISOString().slice(0, 10)}.csv`
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(url)
      ElMessage.success(`已导出 ${rows.length} 项`)
    }

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
      evaluatePasswordApi(row.id)
        .then(res => {
          if (res && res.data) {
            evaluateResult.value = res.data
            evaluateDialogVisible.value = true
          } else {
            ElMessage.error((res && res.message) || '评估失败')
          }
        })
        .catch(() => {
          ElMessage.error('评估失败')
        })
    }

    onMounted(() => {
      // 从 URL 恢复筛选与分页状态
      const q = route.query
      pagination.pageNum = Math.max(1, Number(q.pageNum) || 1)
      const size = Number(q.pageSize)
      pagination.pageSize = PAGE_SIZES.includes(size) ? size : 10
      queryParams.appName = typeof q.appName === 'string' ? q.appName : ''
      queryParams.account = typeof q.account === 'string' ? q.account : ''
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
      evaluateDialogVisible,
      evaluateResult,
      evaluateScoreType,
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
      submitEditForm,
      tableRef,
      selectedRows,
      handleSelectionChange,
      clearSelection,
      batchDelete,
      batchEvaluate,
      exportCsv,
      scoreTagType,
      batchResultVisible,
      batchResults,
      batchRunning,
      generatorVisible,
      openGenerator,
      applyGenerated
    }
  }
})
</script>

<style scoped>
.batch-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  padding: 10px 12px;
  background-color: var(--app-row-hover);
  border-radius: 4px;
}

.batch-count {
  font-size: 13px;
  font-weight: 500;
  color: var(--app-ink);
}

.batch-actions {
  display: flex;
  gap: 8px;
}

.text-muted {
  color: var(--app-ink-soft);
}

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