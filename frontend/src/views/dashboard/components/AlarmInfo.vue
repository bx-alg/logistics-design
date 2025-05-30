<template>
  <div class="alarm-container">
    <div class="alarm-list">
      <div v-for="(item, index) in alarmList" :key="index" class="alarm-item">
        <div class="alarm-time">{{ item.time }}</div>
        <div class="alarm-content">
          <span class="alarm-type" :class="getTypeClass(item.type)">{{ getTypeText(item.type) }}</span>
          <span class="alarm-message">{{ item.message }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const alarmList = ref([
  {
    time: '09:15:23',
    type: 'warning',
    message: '仓库A-12区温度过高，请检查'
  },
  {
    time: '08:45:11',
    type: 'error',
    message: '仓库B-05区湿度异常，需要立即处理'
  },
  {
    time: '08:32:45',
    type: 'info',
    message: '系统检测到库存商品即将过期'
  },
  {
    time: '07:55:37',
    type: 'warning',
    message: '仓库C-08区监控设备离线'
  },
  {
    time: '07:20:09',
    type: 'error',
    message: '仓库A-03区发现异常入侵'
  }
])

function getTypeClass(type) {
  switch (type) {
    case 'warning':
      return 'warning-type'
    case 'error':
      return 'error-type'
    case 'info':
      return 'info-type'
    default:
      return ''
  }
}

function getTypeText(type) {
  switch (type) {
    case 'warning':
      return '警告'
    case 'error':
      return '错误'
    case 'info':
      return '提示'
    default:
      return '未知'
  }
}
</script>

<style scoped>
.alarm-container {
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.alarm-list {
  width: 100%;
  height: 100%;
  overflow-y: auto;
}

.alarm-item {
  display: flex;
  flex-direction: column;
  padding: 8px 0;
  border-bottom: 1px solid rgba(27, 229, 231, 0.2);
}

.alarm-time {
  font-size: 12px;
  color: #8e9dbb;
  margin-bottom: 4px;
}

.alarm-content {
  display: flex;
  align-items: flex-start;
}

.alarm-type {
  padding: 2px 6px;
  border-radius: 2px;
  font-size: 12px;
  margin-right: 8px;
}

.warning-type {
  background-color: #e6a23c;
  color: #fff;
}

.error-type {
  background-color: #f56c6c;
  color: #fff;
}

.info-type {
  background-color: #409eff;
  color: #fff;
}

.alarm-message {
  font-size: 14px;
  line-height: 1.4;
  flex: 1;
}

.alarm-list::-webkit-scrollbar {
  width: 4px;
}

.alarm-list::-webkit-scrollbar-thumb {
  background-color: rgba(27, 229, 231, 0.3);
  border-radius: 2px;
}

.alarm-list::-webkit-scrollbar-track {
  background-color: rgba(28, 37, 80, 0.5);
}
</style> 