<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志查询</span>
        </div>
      </template>
      <el-form :inline="true" class="demo-form-inline mb-4">
        <el-form-item label="日志类型">
          <el-select v-model="queryParams.operationType" placeholder="请选择日志类型" clearable style="width: 180px">
                  <el-option label="登录操作" value="LOGIN" />
                  <el-option label="登出操作" value="LOGOUT" />
                  <el-option label="添加操作" value="ADD" />
                  <el-option label="删除操作" value="DELETE" />
                  <el-option label="更新操作" value="UPDATE" />
                  <el-option label="查询操作" value="QUERY" />
                  <el-option label="评估操作" value="EVALUATE" />
                </el-select>
        </el-form-item>
        <el-form-item label="操作内容">
          <el-input v-model="queryParams.operationContent" placeholder="请输入操作内容" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="queryParams.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 320px"
          />
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
      <el-table v-loading="loading" :data="logList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="operationType" label="日志类型" width="120">
          <template #default="scope">
            <el-tag :type="logTypeMap[scope.row.operationType]">
              {{ logTypeLabelMap[scope.row.operationType] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operationContent" label="操作内容" min-width="300" show-overflow-tooltip>
          <template #default="scope">
            {{ logSourceMap[scope.row.operationContent] || scope.row.operationContent }}
          </template>
        </el-table-column>
        <el-table-column prop="operationResult" label="操作结果" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.operationResult === 1 ? 'success' : 'danger'">
              {{ scope.row.operationResult === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="costTime" label="耗时(ms)" width="100" />
        <el-table-column prop="operationTime" label="操作时间" width="180" />
        <el-table-column label="详情" width="80" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleDetail(scope.row)">
              <i class="sfont system-chakan mr-1"></i>详情
            </el-button>
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
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="日志详情"
      width="600px"
      center
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="日志ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="日志类型">{{ logTypeLabelMap[currentLog.operationType] }}</el-descriptions-item>
        <el-descriptions-item label="操作内容">{{ logSourceMap[currentLog.operationContent] || currentLog.operationContent }}</el-descriptions-item>
        <el-descriptions-item label="操作结果">{{ currentLog.operationResult === 1 ? '成功' : '失败' }}</el-descriptions-item>
        <el-descriptions-item label="耗时(ms)">{{ currentLog.costTime }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ currentLog.operationTime }}</el-descriptions-item>
        <el-descriptions-item label="操作对象类型">{{ currentLog.sourceType ? logSourceMap[currentLog.sourceType] : '未知' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script lang="js">
import { defineComponent, reactive, ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getLogListByTypeApi } from '@/api/log'

export default defineComponent({
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const total = ref(0)
    const detailDialogVisible = ref(false)
    const currentLog = ref({})
    
    // 日志类型映射
    const logTypeMap = {
      LOGIN: 'info',
      LOGOUT: 'warning',
      ADD: 'success',
      DELETE: 'danger',
      UPDATE: 'primary',
      QUERY: 'primary',
      EVALUATE: 'info'
    }
    
    // 日志类型标签映射
    const logTypeLabelMap = {
      LOGIN: '登录操作',
      LOGOUT: '登出操作',
      ADD: '添加操作',
      DELETE: '删除操作',
      UPDATE: '更新操作',
      QUERY: '查询操作',
      EVALUATE: '评估操作'
    }
    
    // 日志来源对象类型映射
    const logSourceMap = {
      USER: '用户',
      PASSWORD_ITEM: '密码条目',
      OPERATION_LOG: '操作日志'
    }
    
    const queryParams = reactive({
      operationType: '',
      operationContent: '',
      timeRange: []
    })
    
    const pagination = reactive({
      pageNum: 1,
      pageSize: 10
    })
    
    const logList = ref([])

    // 获取日志列表
    const getLogList = () => {
      loading.value = true
      const params = {
        ...queryParams,
        startTime: queryParams.timeRange[0] || '',
        endTime: queryParams.timeRange[1] || '',
        ...pagination
      }
      // 删除不必要的参数
      delete params.timeRange
      
      console.log('请求参数:', params)
      getLogListByTypeApi(params)
        .then(res => {
          console.log('API响应:', res)
          // 将日志来源对象类型的英文标识映射为中文
          const records = res.data.records || []
          logList.value = records.map(log => ({
            ...log,
            // 如果API返回的是sourceType字段，则自动映射为中文
            ...(log.sourceType && { sourceType: log.sourceType }) // 保留原始值，在显示时通过logSourceMap映射
          }))
          total.value = res.data.total || 0
          loading.value = false
        })
        .catch(err => {
          console.error('API错误:', err)
          loading.value = false
          ElMessage.error('获取日志列表失败')
        })
    }

    // 查询
    const handleQuery = () => {
      pagination.pageNum = 1
      getLogList()
    }

    // 重置查询条件
    const resetQuery = () => {
      queryParams.operationType = ''
      queryParams.operationContent = ''
      queryParams.timeRange = []
      pagination.pageNum = 1
      getLogList()
    }

    // 分页大小变化
    const handleSizeChange = (val) => {
      pagination.pageSize = val
      getLogList()
    }

    // 当前页码变化
    const handleCurrentChange = (val) => {
      pagination.pageNum = val
      getLogList()
    }

    // 查看日志详情
    const handleDetail = (row) => {
      currentLog.value = row
      detailDialogVisible.value = true
    }



    onMounted(() => {
      getLogList()
    })

    return {
      loading,
      total,
      queryParams,
      pagination,
      logList,
      detailDialogVisible,
      currentLog,
      logTypeMap,
      logTypeLabelMap,
      logSourceMap,
      handleQuery,
      resetQuery,
      handleSizeChange,
      handleCurrentChange,
      handleDetail
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

pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  max-height: 200px;
  overflow-y: auto;
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
}
</style>