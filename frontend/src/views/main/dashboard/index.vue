<template>
  <div class="app-container">
    <h1 class="main-title">密鉴 · 密码管理与安全评估平台</h1>
    <div class="card-container">
      <!-- 卡片1：密码安全防护 -->
      <el-card class="feature-card card-1">
        <div class="card-content">
          <div class="card-icon">
            <i class="sfont system-anquan"></i>
          </div>
          <h3 class="card-title">多密码管理</h3>
          <p class="card-desc">支持大量密码同时管理</p>
        </div>
      </el-card>
      
      <!-- 卡片2：批量密码更新 -->
      <el-card class="feature-card card-2">
        <div class="card-content">
          <div class="card-icon">
            <i class="sfont system-batch"></i>
          </div>
          <h3 class="card-title">密码在线评估</h3>
          <p class="card-desc">实时评估密码强度,提供密码建议</p>
        </div>
      </el-card>
      
      <!-- 卡片3：操作日志审计 -->
      <el-card class="feature-card card-3">
        <div class="card-content">
          <div class="card-icon">
            <i class="sfont system-rizhi"></i>
          </div>
          <h3 class="card-title">操作日志审计</h3>
          <p class="card-desc">全程记录密码增删改查操作轨迹</p>
        </div>
      </el-card>
      

    </div>
  </div>
</template>

<script setup>
// 导入必要的模块
</script>

<style scoped>
.app-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 120px);
}

.main-title {
  text-align: center;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 40px;
  padding: 20px 0;
}

.card-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.feature-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-content {
  text-align: center;
  padding: 40px 20px;
}

.card-icon {
  font-size: 48px;
  margin-bottom: 20px;
  width: 80px;
  height: 80px;
  line-height: 80px;
  border-radius: 50%;
  margin: 0 auto 24px;
  background-color: rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-1 .card-icon {
  color: #67c23a;
  background-color: rgba(103, 194, 58, 0.1);
}

.card-2 .card-icon {
  color: #409eff;
  background-color: rgba(64, 158, 255, 0.1);
}

.card-3 .card-icon {
  color: #e6a23c;
  background-color: rgba(230, 162, 60, 0.1);
}

.card-4 .card-icon {
  color: #909399;
  background-color: rgba(144, 147, 153, 0.1);
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.card-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
  padding: 0 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .main-title {
    font-size: 24px;
    margin-bottom: 30px;
  }
  
  .card-content {
    padding: 30px 20px;
  }
  
  .card-icon {
    font-size: 40px;
    width: 70px;
    height: 70px;
    line-height: 70px;
  }
  
  .card-title {
    font-size: 18px;
  }
}
</style>