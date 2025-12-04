<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">储物柜管理</h2>
    </div>

    <div v-loading="loading" class="lockers-container">
      <el-empty
        v-if="!loading && lockers.length === 0"
        description="暂无储物柜租赁记录"
      />
      
      <el-row :gutter="20" v-else>
        <el-col
          v-for="locker in lockers"
          :key="locker.locker_id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <div class="card locker-card">
            <div class="locker-header">
              <el-icon class="locker-icon"><Box /></el-icon>
              <div class="locker-number">储物柜 #{{ locker.locker_number }}</div>
            </div>
            <div class="locker-info">
              <div class="info-item">
                <span class="label">开始时间：</span>
                <span class="value">{{ formatDate(locker.start_date, 'YYYY-MM-DD') }}</span>
              </div>
              <div class="info-item">
                <span class="label">结束时间：</span>
                <span class="value">{{ formatDate(locker.end_date, 'YYYY-MM-DD') }}</span>
              </div>
              <div class="info-item">
                <span class="label">状态：</span>
                <el-tag :type="getStatusType(locker)" size="small">
                  {{ getStatus(locker) }}
                </el-tag>
              </div>
              <div v-if="locker.end_date" class="info-item">
                <span class="label">剩余时间：</span>
                <span class="value" :class="{ 'text-warning': isExpiringSoon(locker) }">
                  {{ getRemainingTime(locker) }}
                </span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLockers } from '@/api/locker'
import { formatDate, isExpired, getTimeRemaining } from '@/utils/date'
import { Box } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const lockers = ref([])
const loading = ref(false)

const getStatus = (locker) => {
  if (!locker.end_date) return '使用中'
  if (isExpired(locker.end_date)) return '已到期'
  return '正常'
}

const getStatusType = (locker) => {
  const status = getStatus(locker)
  if (status === '已到期') return 'danger'
  if (status === '正常') return 'success'
  return 'info'
}

const isExpiringSoon = (locker) => {
  if (!locker.end_date) return false
  if (isExpired(locker.end_date)) return false
  const days = dayjs(locker.end_date).diff(dayjs(), 'day')
  return days <= 7 && days > 0
}

const getRemainingTime = (locker) => {
  if (!locker.end_date) return '-'
  if (isExpired(locker.end_date)) return '已过期'
  return getTimeRemaining(locker.end_date)
}

const fetchLockers = async () => {
  loading.value = true
  try {
    const data = await getLockers()
    lockers.value = Array.isArray(data) ? data : (data.lockers || [])
  } catch (error) {
    console.error('获取储物柜信息失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchLockers()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
}

.page-header {
  margin-bottom: 24px;
  
  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
}

.lockers-container {
  min-height: 400px;
}

.locker-card {
  margin-bottom: 20px;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
  
  .locker-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #f0f0f0;
    
    .locker-icon {
      font-size: 32px;
      color: #667eea;
    }
    
    .locker-number {
      font-size: 20px;
      font-weight: 600;
      color: #303133;
    }
  }
  
  .locker-info {
    .info-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      font-size: 14px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .label {
        color: #909399;
      }
      
      .value {
        color: #303133;
        font-weight: 500;
        
        &.text-warning {
          color: #e6a23c;
        }
      }
    }
  }
}
</style>

