<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志查询</span>
        </div>
      </template>
      <el-form :inline="true" class="demo-form-inline mb-4" @submit.prevent="handleQuery">
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
          <el-input v-model="queryParams.operationContent" name="operationContent" autocomplete="off" placeholder="请输入操作内容" clearable style="width: 200px" />
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
          <el-button type="primary" native-type="submit">
            查询
          </el-button>
          <el-button @click="resetQuery">
            重置
          </el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="logList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" class-name="tabular-nums" />
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
              详情
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
        <el-descriptions-item label="耗时(ms)">
          <span class="tabular-nums">{{ currentLog.costTime }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ currentLog.operationTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script lang="js">
import { defineComponent, reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getLogListByTypeApi } from '@/api/log'

export default defineComponent({
  setup() {
    const route = useRoute()
    const router = useRouter()
    const loading = ref(false)
    const total = ref(0)
    const detailDialogVisible = ref(false)
    const currentLog = ref({})
    
    // 日志类型映射
    const logTypeMap = {
      LOGIN: 'info',
      LOGOUT: 'info',
      ADD: 'info',
      DELETE: 'info',
      UPDATE: 'info',
      QUERY: 'info',
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
    const PAGE_SIZES = [10, 20, 30, 50]

    // 筛选/分页状态同步到 URL，便于刷新、分享与后退
    const syncQuery = () => {
      const query = {
        pageNum: String(pagination.pageNum),
        pageSize: String(pagination.pageSize)
      }
      if (queryParams.operationType) query.operationType = queryParams.operationType
      if (queryParams.operationContent) query.operationContent = queryParams.operationContent
      if (queryParams.timeRange && queryParams.timeRange.length === 2) {
        query.startTime = queryParams.timeRange[0]
        query.endTime = queryParams.timeRange[1]
      }
      // 仅在参数变化时替换，避免重复导航导致页面重挂载
      if (router.resolve({ path: route.path, query }).fullPath !== route.fullPath) {
        router.replace({ path: route.path, query })
      }
    }

    // 获取日志列表
    const getLogList = () => {
      syncQuery()
      loading.value = true
      const params = {
        operationType: queryParams.operationType || undefined,
        operationContent: queryParams.operationContent || undefined,
        ...pagination
      }
      // 仅在有选择时间范围时才传时间字段（空字符串会导致后端日期解析失败）
      if (queryParams.timeRange && queryParams.timeRange.length === 2) {
        params.startTime = queryParams.timeRange[0]
        params.endTime = queryParams.timeRange[1]
      }
      getLogListByTypeApi(params)
        .then(res => {
          logList.value = (res.data && res.data.records) || []
          total.value = Number(res.data && res.data.total) || 0
        })
        .catch(() => {
          ElMessage.error('获取日志列表失败')
        })
        .finally(() => {
          loading.value = false
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
      // 从 URL 恢复筛选与分页状态
      const q = route.query
      pagination.pageNum = Math.max(1, Number(q.pageNum) || 1)
      const size = Number(q.pageSize)
      pagination.pageSize = PAGE_SIZES.includes(size) ? size : 10
      queryParams.operationType = typeof q.operationType === 'string' ? q.operationType : ''
      queryParams.operationContent = typeof q.operationContent === 'string' ? q.operationContent : ''
      queryParams.timeRange = (typeof q.startTime === 'string' && typeof q.endTime === 'string')
        ? [q.startTime, q.endTime]
        : []
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