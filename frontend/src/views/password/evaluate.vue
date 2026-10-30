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
        <el-form :model="passwordForm" label-width="80px" size="large" @submit.prevent="handleEvaluate">
          <el-form-item label="密码" for="evaluate-password">
            <el-input
              id="evaluate-password"
              v-model="passwordForm.password" 
              placeholder="输入任意密码，不会保存" 
              type="password" 
              show-password 
              name="password"
              aria-label="待评估密码"
              autocomplete="off"
              spellcheck="false"
              class="evaluate-input"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" native-type="submit" :loading="loading">开始评估</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container" aria-busy="true" aria-live="polite">
        <el-skeleton :rows="10" animated />
      </div>
      
      <!-- 评估结果区域：异步替换，需要播报 -->
      <div v-else-if="evaluateResult" role="status" aria-live="polite">
        <el-descriptions title="评估结果" :column="1" border>
          <el-descriptions-item label="评估分数">
            <span class="tabular-nums text-[18px] font-semibold">{{ evaluateResult.evaluateScore }} 分</span>
          </el-descriptions-item>
          <el-descriptions-item label="风险类型">
            <el-tag :type="riskLevelType">
              {{ evaluateResult.riskType || '无' }}
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
          <div class="analysis-grid">
            <div class="analysis-score">
              <el-progress
                  type="dashboard"
                  :percentage="evaluateResult.evaluateScore"
                  :width="150"
                  :color="scoreColor"
              />
              <p class="analysis-caption">服务端评估总分</p>
            </div>
            <div class="analysis-radar">
              <div class="analysis-radar-box">
                <Charts :option="radarOption" :aria-label="radarSummary" />
              </div>
              <p class="analysis-caption">五个维度为本地拆解，仅作参考</p>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 无结果状态：不用默认空盒子插画，给一句可执行的话 -->
      <div v-else class="no-result">
        <i class="sfont system-mima no-result-icon" aria-hidden="true"></i>
        <p class="no-result-title">还没有评估结果</p>
        <p class="no-result-hint">在上方输入一个密码，立刻看到强度评分与加固建议</p>
      </div>
      

    </el-card>
  </div>
</template>

<script lang="js">
import { defineComponent, reactive, ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { evaluatePasswordByInputApi } from '@/api/passwordItem'
import Charts from '@/components/charts/index.vue'
import { evaluateDimensions } from '@/utils/passwordStrength'

export default defineComponent({
  components: { Charts },
  setup() {
    const loading = ref(false)
    const evaluateResult = ref(null)
    
    // 密码表单
    const passwordForm = reactive({
      password: ''
    })
    
    /** 雷达图的文本替代：把五个维度读成一句话，读屏才拿得到 */
    const radarSummary = computed(() => {
      const dims = evaluateDimensions(passwordForm.password)
      const parts = dims.map((d) => d.name + ' ' + d.value + ' 分').join('，')
      return '密码强度五维图（本地拆解，仅供参考）：' + parts
    })

    /** 分数配色：低于 60 危险、60-80 提醒、其余用主色 */
    const scoreColor = computed(() => {
      const score = evaluateResult.value ? evaluateResult.value.evaluateScore : 0
      const cssVar = (name, fallback) => {
        const v = getComputedStyle(document.documentElement).getPropertyValue(name)
        return v && v.trim() ? v.trim() : fallback
      }
      if (score < 60) return cssVar('--app-danger', '#b3261e')
      if (score < 80) return cssVar('--app-warning', '#8a5a00')
      return cssVar('--app-accent', '#0f766e')
    })

    /** 图表配色从 CSS 令牌读，避免在 JS 里再写一份色值 */
    const cssVar = (name, fallback) => {
      const v = getComputedStyle(document.documentElement).getPropertyValue(name)
      return v && v.trim() ? v.trim() : fallback
    }

    const radarOption = computed(() => {
      const dims = evaluateDimensions(passwordForm.password)
      const accent = cssVar('--app-accent', '#0f766e')
      const line = cssVar('--app-line', '#e3e9e7')
      const inkSoft = cssVar('--app-ink-soft', '#4f5b59')
      return {
        tooltip: {},
        radar: {
          indicator: dims.map((d) => ({ name: d.name, max: 100 })),
          radius: '62%',
          splitNumber: 4,
          axisName: { color: inkSoft, fontSize: 12 },
          splitLine: { lineStyle: { color: line } },
          splitArea: { show: false },
          axisLine: { lineStyle: { color: line } }
        },
        series: [
          {
            type: 'radar',
            symbolSize: 4,
            data: [
              {
                value: dims.map((d) => d.value),
                name: '本地拆解',
                lineStyle: { color: accent, width: 2 },
                itemStyle: { color: accent },
                areaStyle: { color: accent, opacity: 0.16 }
              }
            ]
          }
        ]
      }
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
          if (res && res.data) {
            evaluateResult.value = res.data
            loading.value = false
            ElMessage.success('评估成功')
          } else {
            loading.value = false
            ElMessage.error((res && res.message) || '评估失败')
          }
        })
        .catch(() => {
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
      evaluateResult,
      riskLevelType,
      scoreColor,
      radarOption,
      radarSummary,
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

.evaluate-input {
  max-width: 420px;
}

/* 空状态：只用一个弱化的图标 + 两行字，没有插画、没有边框盒子 */
.no-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  justify-content: center;
  min-height: 300px;
  padding: 40px 16px;
  text-align: center;
}

.no-result-icon {
  font-size: 26px;
  line-height: 1;
  color: var(--app-ink-mute);
}

.no-result-title {
  margin: 0;
  font-size: 15px;
  font-weight: 500;
  color: var(--app-ink);
}

.no-result-hint {
  margin: 0;
  font-size: 13px;
  line-height: 1.6;
  color: var(--app-ink-soft);
  max-width: 420px;
}

.loading-container {
  padding: 20px;
}

.analysis-grid {
  display: grid;
  grid-template-columns: 200px minmax(0, 400px);
  gap: 48px;
  align-items: center;
  justify-content: center;
}

.analysis-score,
.analysis-radar {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.analysis-radar-box {
  width: 100%;
  height: 240px;
}

.analysis-caption {
  margin: 0;
  font-size: 12px;
  color: var(--app-ink-soft);
}

@media (max-width: 900px) {
  .analysis-grid {
    grid-template-columns: 1fr;
  }
}

.password-input-container {
  padding: 4px 0 12px;
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