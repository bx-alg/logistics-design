<template>
  <div class="device-container">
    <h2 class="page-title">设备管理</h2>
    
    <el-row :gutter="20">
      <el-col :span="8" v-for="device in deviceList" :key="device.id">
        <el-card shadow="hover" class="device-card">
          <div slot="header" class="card-header">
            <span>{{ device.name }}</span>
          </div>
          <div class="device-info">
            <div class="device-icon">
              <i :class="device.icon"></i>
            </div>
            <div class="device-stats">
              <div class="stat-item">
                <span class="label">设备总数:</span>
                <span class="value">{{ device.totalCount }}</span>
              </div>
              <div class="stat-item">
                <span class="label">在线设备:</span>
                <span class="value">{{ device.onlineCount }}</span>
              </div>
              <div class="stat-item">
                <span class="label">异常设备:</span>
                <span class="value">{{ device.errorCount }}</span>
              </div>
            </div>
          </div>
          <div class="device-actions">
            <el-button 
              type="primary" 
              size="small" 
              @click="goToMonitor(device.type)"
              :disabled="device.disabled">
              监控详情
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="goToMaintenance(device.type)"
              :disabled="device.disabled">
              维护记录
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'DeviceManagement',
  data() {
    return {
      deviceList: [
        {
          id: 1,
          name: 'RGV设备',
          type: 'rgv',
          icon: 'el-icon-truck',
          totalCount: 28,
          onlineCount: 24,
          errorCount: 4,
          disabled: false
        },
        {
          id: 2,
          name: '自动裁剪机',
          type: 'cutter',
          icon: 'el-icon-scissors',
          totalCount: 156,
          onlineCount: 142,
          errorCount: 14,
          disabled: true
        },
        {
          id: 3,
          name: '童装数字印花机',
          type: 'printer',
          icon: 'el-icon-printer',
          totalCount: 85,
          onlineCount: 79,
          errorCount: 6,
          disabled: true
        },
        {
          id: 4,
          name: '童装输送线',
          type: 'conveyor',
          icon: 'el-icon-s-operation',
          totalCount: 42,
          onlineCount: 38,
          errorCount: 4,
          disabled: true
        },
        {
          id: 5,
          name: '童装包装机',
          type: 'packer',
          icon: 'el-icon-box',
          totalCount: 63,
          onlineCount: 58,
          errorCount: 5,
          disabled: true
        },
        {
          id: 6,
          name: '自动缝纫机',
          type: 'sewing',
          icon: 'el-icon-s-cooperation',
          totalCount: 214,
          onlineCount: 196,
          errorCount: 18,
          disabled: true
        }
      ]
    }
  },
  methods: {
    goToMonitor(type) {
      if (type === 'rgv') {
        this.$router.push('/device/rgv-monitor')
      } else {
        this.$message({
          message: `${type}设备监控功能开发中...`,
          type: 'info'
        })
      }
    },
    goToMaintenance(type) {
      this.$message({
        message: `${type}设备维护记录功能开发中...`,
        type: 'info'
      })
    }
  }
}
</script>

<style scoped>
.device-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
}

.device-card {
  margin-bottom: 20px;
  height: 220px;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.device-info {
  flex: 1;
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.device-icon {
  width: 60px;
  height: 60px;
  background-color: #f0f9ff;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 15px;
}

.device-icon i {
  font-size: 30px;
  color: #409EFF;
}

.device-stats {
  flex: 1;
}

.stat-item {
  margin-bottom: 5px;
  display: flex;
  justify-content: space-between;
}

.label {
  color: #606266;
  font-size: 14px;
}

.value {
  font-weight: bold;
  color: #303133;
  font-size: 14px;
}

.device-actions {
  display: flex;
  justify-content: space-between;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
}
</style> 