<template>
  <div class="home-container">
    <el-card class="welcome-card">
      <div class="welcome-content">
        <h2>欢迎使用审计档案库系统</h2>
        <p>这是一个为财务审计人员设计的历年凭证相关数据档案库系统</p>
      </div>
    </el-card>
    <div class="stats-grid">
      <el-card class="stat-card">
        <div class="stat-icon users-icon">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ statistics.userCount || 0 }}</p>
          <p class="stat-label">用户数量</p>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon roles-icon">
          <el-icon><Setting /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ statistics.roleCount || 0 }}</p>
          <p class="stat-label">角色数量</p>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon units-icon">
          <el-icon><OfficeBuilding /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ statistics.unitCount || 0 }}</p>
          <p class="stat-label">单位数量</p>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon templates-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ statistics.templateCount || 0 }}</p>
          <p class="stat-label">模板数量</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { User, Setting, OfficeBuilding, Document } from '@element-plus/icons-vue'
import { getStatistics } from '../api/home'

const statistics = reactive({
  userCount: 0,
  roleCount: 0,
  unitCount: 0,
  templateCount: 0
})

const loadStatistics = () => {
  getStatistics().then((res) => {
    if (res.code === 200) {
      Object.assign(statistics, res.data)
    }
  })
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-content h2 {
  font-size: 24px;
  margin-bottom: 10px;
}

.welcome-content p {
  color: #666;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
}

.users-icon {
  background-color: #e6f7ff;
  color: #1890ff;
}

.roles-icon {
  background-color: #f6ffed;
  color: #52c41a;
}

.units-icon {
  background-color: #fff7e6;
  color: #fa8c16;
}

.templates-icon {
  background-color: #f9f0ff;
  color: #722ed1;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}
</style>
