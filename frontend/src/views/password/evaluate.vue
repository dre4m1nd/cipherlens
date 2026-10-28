<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>密码评估</span>
        </div>
      </template>
      <!-- 密码输入区域 -->
      <div class="password-input-container">
        <el-form :model="passwordForm" label-width="80px" size="large">
          <el-form-item label="密码">
            <el-input 
              v-model="passwordForm.password" 
              placeholder="请输入要评估的密码" 
              type="password" 
              show-password 
              style="width: 300px;"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleEvaluate" :loading="loading">开始评估</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      
      <!-- 评估结果区域 -->
      <div v-else-if="evaluateResult">
        <el-descriptions title="评估结果" :column="1" border>
          <el-descriptions-item label="评估分数">
            <el-rate v-model="score" disabled show-score text-color="#ff9900" />
          </el-descriptions-item>
          <el-descriptions-item label="风险类型">
            <el-tag :type="riskLevelType">
              {{ evaluateResult.riskType }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评估建议">{{ evaluateResult.suggestions }}</el-descriptions-item>
          <el-descriptions-item label="评估时间">{{ evaluateResult.evaluateTime }}</el-descriptions-item>
        </el-descriptions>
        
        <!-- 密码强度分析 -->
        <el-card class="mt-4" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>密码强度分析</span>
            </div>
          </template>
          <el-progress type="dashboard" :percentage="evaluateResult.evaluateScore" :width="150" />
        </el-card>
      </div>
      
      <!-- 无结果状态 -->
      <div v-else class="no-result-container">
        <el-empty description="请输入密码进行评估" />
      </div>
      

    </el-card>
  </div>
</template>

<script lang="js">
import { defineComponent, reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { evaluatePasswordByInputApi } from '@/api/passwordItem'

export default defineComponent({
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const score = ref(0)
    const evaluateResult = ref(null)
    
    // 密码表单
    const passwordForm = reactive({
      password: ''
    })
    
    // 根据风险类型获取标签类型
    const riskLevelType = computed(() => {
      if (!evaluateResult.value) return 'info'
      const riskScore = evaluateResult.value.evaluateScore
      if (riskScore >= 80) return 'success'
      if (riskScore >= 60) return 'warning'
      return 'danger'
    })

    // 开始评估
    const handleEvaluate = () => {
      if (!passwordForm.password) {
        ElMessage.warning('请输入要评估的密码')
        return
      }
      
      loading.value = true
      evaluatePasswordByInputApi(passwordForm.password)
        .then(res => {
          if (res && res.code === 200) {
            evaluateResult.value = res.data
            score.value = res.data.evaluateScore
            loading.value = false
            ElMessage.success('评估成功')
          } else {
            loading.value = false
            ElMessage.error(res?.message || '评估失败')
          }
        })
        .catch(err => {
          loading.value = false
          ElMessage.error('评估失败')
        })
    }
    
    // 重置表单
    const handleReset = () => {
      passwordForm.password = ''
      evaluateResult.value = null
    }

    return {
      loading,
      score,
      evaluateResult,
      riskLevelType,
      passwordForm,
      handleEvaluate,
      handleReset
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

.loading-container {
  padding: 20px;
}

.password-input-container {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.no-result-container {
  margin: 20px 0;
}

.progress-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
}

.progress-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.progress-item span {
  font-weight: bold;
}
</style>