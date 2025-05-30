<template>
  <div class="rgv-monitor-container">
    <h2 class="page-title">RGV设备监控</h2>
    
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="filter-container">
          <el-button type="primary" icon="el-icon-refresh" @click="refreshData">刷新数据</el-button>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            @change="handleDateChange">
          </el-date-picker>
        </div>
      </el-col>
    </el-row>
    
    <!-- RGV设备状态概览 -->
    <el-row :gutter="20" class="status-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="status-card">
          <div class="status-header">
            <i class="el-icon-truck"></i>
            <span>RGV总数</span>
          </div>
          <div class="status-value">{{ rgvStats.total }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="status-card idle">
          <div class="status-header">
            <i class="el-icon-success"></i>
            <span>空闲RGV</span>
          </div>
          <div class="status-value">{{ rgvStats.idle }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="status-card busy">
          <div class="status-header">
            <i class="el-icon-loading"></i>
            <span>运行中RGV</span>
          </div>
          <div class="status-value">{{ rgvStats.running }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="status-card warning">
          <div class="status-header">
            <i class="el-icon-warning"></i>
            <span>堵塞RGV</span>
          </div>
          <div class="status-value">{{ rgvStats.blocked }}</div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- RGV设备运行情况 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card shadow="hover" class="stat-card">
          <div slot="header" class="card-header">
            <span>RGV运行统计</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-label">任务完成数</div>
                <div class="stat-value">{{ rgvStats.tasksCompleted }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-label">总运行时间(h)</div>
                <div class="stat-value">{{ rgvStats.totalRunTime }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-label">总行驶距离(km)</div>
                <div class="stat-value">{{ rgvStats.totalDistance }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-label">堵塞次数</div>
                <div class="stat-value">{{ rgvStats.blockCount }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- RGV当前位置地图 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card shadow="hover" class="map-card">
          <div slot="header" class="card-header">
            <span>RGV位置分布</span>
          </div>
          <div class="map-container">
            <div class="warehouse-map">
              <div class="map-grid">
                <div v-for="(area, index) in warehouseAreas" :key="index" class="map-area" :style="getAreaStyle(area)">
                  <span class="area-name">{{ area.name }}</span>
                  <div v-if="area.rgvs && area.rgvs.length > 0" class="area-rgvs">
                    <div v-for="rgv in area.rgvs" :key="rgv.id" class="rgv-dot" :class="getRgvStatusClass(rgv.status)" @click="showRgvDetail(rgv)">
                      {{ rgv.id.split('-')[1] }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="map-legend">
              <div class="legend-item">
                <div class="rgv-dot idle-status"></div>
                <span>空闲</span>
              </div>
              <div class="legend-item">
                <div class="rgv-dot running-status"></div>
                <span>运行中</span>
              </div>
              <div class="legend-item">
                <div class="rgv-dot blocked-status"></div>
                <span>堵塞</span>
              </div>
              <div class="legend-item">
                <div class="rgv-dot error-status"></div>
                <span>故障</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- RGV设备列表 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card shadow="hover" class="rgv-list-card">
          <div slot="header" class="card-header">
            <span>RGV设备列表</span>
            <div class="header-actions">
              <el-select v-model="statusFilter" placeholder="状态筛选" size="small" clearable>
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
              </el-select>
            </div>
          </div>
          <el-table
            v-loading="loading"
            :data="filteredRgvList"
            style="width: 100%"
            border>
            <el-table-column
              prop="id"
              label="RGV编号"
              width="100">
            </el-table-column>
            <el-table-column
              prop="status"
              label="状态"
              width="100">
              <template slot-scope="scope">
                <el-tag
                  :type="getStatusType(scope.row.status)"
                  effect="dark">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="location"
              label="当前位置"
              width="120">
            </el-table-column>
            <el-table-column
              prop="power"
              label="电量"
              width="100">
              <template slot-scope="scope">
                <el-progress 
                  :percentage="scope.row.power" 
                  :color="getPowerColor(scope.row.power)">
                </el-progress>
              </template>
            </el-table-column>
            <el-table-column
              prop="taskCount"
              label="任务数"
              width="90">
            </el-table-column>
            <el-table-column
              prop="runTime"
              label="运行时间(h)"
              width="120">
            </el-table-column>
            <el-table-column
              prop="distance"
              label="行驶距离(km)"
              width="120">
            </el-table-column>
            <el-table-column
              prop="lastMaintenance"
              label="上次维护"
              width="160">
            </el-table-column>
            <el-table-column
              label="操作"
              width="180">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="viewDetail(scope.row)">详情</el-button>
                <el-button type="text" size="small" @click="controlRgv(scope.row)">控制</el-button>
                <el-button type="text" size="small" @click="maintenance(scope.row)">维护</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-container">
            <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :current-page="currentPage"
              :page-sizes="[10, 20, 30, 50]"
              :page-size="pageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total">
            </el-pagination>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- RGV详情对话框 -->
    <el-dialog title="RGV详情" :visible.sync="dialogVisible" width="50%">
      <div v-if="selectedRgv" class="rgv-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="RGV编号">{{ selectedRgv.id }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getStatusType(selectedRgv.status)">{{ selectedRgv.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="当前位置">{{ selectedRgv.location }}</el-descriptions-item>
          <el-descriptions-item label="电量">
            <el-progress :percentage="selectedRgv.power" :color="getPowerColor(selectedRgv.power)"></el-progress>
          </el-descriptions-item>
          <el-descriptions-item label="型号">{{ selectedRgv.model }}</el-descriptions-item>
          <el-descriptions-item label="载重能力">{{ selectedRgv.loadCapacity }}kg</el-descriptions-item>
          <el-descriptions-item label="最大速度">{{ selectedRgv.maxSpeed }}m/s</el-descriptions-item>
          <el-descriptions-item label="任务完成数">{{ selectedRgv.taskCount }}</el-descriptions-item>
          <el-descriptions-item label="总运行时间">{{ selectedRgv.runTime }}小时</el-descriptions-item>
          <el-descriptions-item label="总行驶距离">{{ selectedRgv.distance }}km</el-descriptions-item>
          <el-descriptions-item label="投入使用时间">{{ selectedRgv.deploymentDate }}</el-descriptions-item>
          <el-descriptions-item label="上次维护时间">{{ selectedRgv.lastMaintenance }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="detail-actions">
          <el-button type="primary" size="small" @click="controlRgv(selectedRgv)">远程控制</el-button>
          <el-button type="warning" size="small" @click="maintenance(selectedRgv)">申请维护</el-button>
          <el-button type="info" size="small" @click="viewHistory(selectedRgv)">历史记录</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'RgvMonitor',
  data() {
    return {
      loading: false,
      dialogVisible: false,
      selectedRgv: null,
      dateRange: [],
      statusFilter: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      
      statusOptions: [
        { value: '空闲', label: '空闲' },
        { value: '运行中', label: '运行中' },
        { value: '堵塞', label: '堵塞' },
        { value: '故障', label: '故障' },
        { value: '充电中', label: '充电中' }
      ],
      
      rgvStats: {
        total: 28,
        idle: 12,
        running: 14,
        blocked: 2,
        tasksCompleted: 1856,
        totalRunTime: 5324.5,
        totalDistance: 8762.3,
        blockCount: 47
      },
      
      warehouseAreas: [
        { name: '童装原料区', x: 0, y: 0, width: 2, height: 2, rgvs: [
          { id: 'RGV-01', status: '空闲', power: 85 },
          { id: 'RGV-02', status: '运行中', power: 72 }
        ] },
        { name: '童装裁剪区', x: 2, y: 0, width: 2, height: 1, rgvs: [
          { id: 'RGV-03', status: '运行中', power: 63 },
          { id: 'RGV-04', status: '运行中', power: 45 }
        ] },
        { name: '童装缝制区', x: 2, y: 1, width: 2, height: 1, rgvs: [
          { id: 'RGV-05', status: '空闲', power: 92 },
          { id: 'RGV-06', status: '运行中', power: 78 }
        ] },
        { name: '童装印花区', x: 0, y: 2, width: 1, height: 2, rgvs: [
          { id: 'RGV-07', status: '空闲', power: 91 }
        ] },
        { name: '童装包装区', x: 1, y: 2, width: 1, height: 2, rgvs: [
          { id: 'RGV-08', status: '运行中', power: 55 },
          { id: 'RGV-09', status: '堵塞', power: 23 }
        ] },
        { name: '童装成品区', x: 2, y: 2, width: 2, height: 2, rgvs: [
          { id: 'RGV-10', status: '空闲', power: 87 },
          { id: 'RGV-11', status: '运行中', power: 68 },
          { id: 'RGV-12', status: '运行中', power: 72 }
        ] },
        { name: '童装出货区', x: 4, y: 0, width: 1, height: 4, rgvs: [
          { id: 'RGV-13', status: '空闲', power: 95 },
          { id: 'RGV-14', status: '运行中', power: 82 },
          { id: 'RGV-15', status: '堵塞', power: 34 }
        ] }
      ],
      
      rgvList: [
        { id: 'RGV-01', status: '空闲', location: '童装原料区', power: 85, taskCount: 156, runTime: 423.5, distance: 756.2, lastMaintenance: '2024-05-12' },
        { id: 'RGV-02', status: '运行中', location: '童装原料区', power: 72, taskCount: 187, runTime: 512.8, distance: 892.4, lastMaintenance: '2024-05-10' },
        { id: 'RGV-03', status: '运行中', location: '童装裁剪区', power: 63, taskCount: 142, runTime: 386.2, distance: 645.7, lastMaintenance: '2024-05-15' },
        { id: 'RGV-04', status: '运行中', location: '童装裁剪区', power: 45, taskCount: 164, runTime: 423.9, distance: 712.3, lastMaintenance: '2024-05-08' },
        { id: 'RGV-05', status: '空闲', location: '童装缝制区', power: 92, taskCount: 128, runTime: 342.6, distance: 586.9, lastMaintenance: '2024-05-14' },
        { id: 'RGV-06', status: '运行中', location: '童装缝制区', power: 78, taskCount: 176, runTime: 465.3, distance: 782.1, lastMaintenance: '2024-05-11' },
        { id: 'RGV-07', status: '空闲', location: '童装印花区', power: 91, taskCount: 112, runTime: 298.7, distance: 523.4, lastMaintenance: '2024-05-16' },
        { id: 'RGV-08', status: '运行中', location: '童装包装区', power: 55, taskCount: 198, runTime: 532.4, distance: 876.5, lastMaintenance: '2024-05-07' },
        { id: 'RGV-09', status: '堵塞', location: '童装包装区', power: 23, taskCount: 86, runTime: 245.8, distance: 412.6, lastMaintenance: '2024-05-18' },
        { id: 'RGV-10', status: '空闲', location: '童装成品区', power: 87, taskCount: 134, runTime: 356.2, distance: 612.8, lastMaintenance: '2024-05-13' },
        { id: 'RGV-11', status: '运行中', location: '童装成品区', power: 68, taskCount: 168, runTime: 432.7, distance: 745.9, lastMaintenance: '2024-05-09' },
        { id: 'RGV-12', status: '运行中', location: '童装成品区', power: 72, taskCount: 154, runTime: 398.5, distance: 678.2, lastMaintenance: '2024-05-12' },
        { id: 'RGV-13', status: '空闲', location: '童装出货区', power: 95, taskCount: 124, runTime: 312.4, distance: 545.6, lastMaintenance: '2024-05-15' },
        { id: 'RGV-14', status: '运行中', location: '童装出货区', power: 82, taskCount: 178, runTime: 456.8, distance: 798.3, lastMaintenance: '2024-05-11' },
        { id: 'RGV-15', status: '堵塞', location: '童装出货区', power: 34, taskCount: 92, runTime: 267.3, distance: 432.5, lastMaintenance: '2024-05-17' }
      ]
    }
  },
  computed: {
    filteredRgvList() {
      if (!this.statusFilter) {
        return this.rgvList
      }
      return this.rgvList.filter(rgv => rgv.status === this.statusFilter)
    }
  },
  methods: {
    refreshData() {
      this.loading = true
      // 模拟数据加载
      setTimeout(() => {
        this.loading = false
        this.$message({
          message: '数据已刷新',
          type: 'success'
        })
      }, 1000)
    },
    handleDateChange(val) {
      console.log('日期范围变更:', val)
      this.refreshData()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.refreshData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.refreshData()
    },
    getStatusType(status) {
      switch (status) {
        case '空闲': return 'success'
        case '运行中': return 'primary'
        case '堵塞': return 'danger'
        case '故障': return 'warning'
        default: return 'info'
      }
    },
    getPowerColor(power) {
      if (power < 30) return '#F56C6C'
      if (power < 60) return '#E6A23C'
      return '#67C23A'
    },
    getAreaStyle(area) {
      return {
        gridColumn: `${area.x + 1} / span ${area.width}`,
        gridRow: `${area.y + 1} / span ${area.height}`,
        backgroundColor: area.color
      }
    },
    getRgvStatusClass(status) {
      switch (status) {
        case '空闲': return 'idle-status'
        case '运行中': return 'running-status'
        case '堵塞': return 'blocked-status'
        case '故障': return 'error-status'
        default: return ''
      }
    },
    showRgvDetail(rgv) {
      this.selectedRgv = this.rgvList.find(item => item.id === rgv.id)
      this.dialogVisible = true
    },
    viewDetail(rgv) {
      this.selectedRgv = rgv
      this.dialogVisible = true
    },
    controlRgv(rgv) {
      this.$message({
        message: `RGV远程控制功能开发中...(${rgv.id})`,
        type: 'info'
      })
    },
    maintenance(rgv) {
      this.$message({
        message: `RGV维护申请功能开发中...(${rgv.id})`,
        type: 'info'
      })
    },
    viewHistory(rgv) {
      this.$message({
        message: `RGV历史记录功能开发中...(${rgv.id})`,
        type: 'info'
      })
    }
  },
  created() {
    this.total = this.rgvList.length
  }
}
</script>

<style scoped>
.rgv-monitor-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
}

.filter-container {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.status-cards {
  margin-bottom: 20px;
}

.status-card {
  height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.status-header {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}

.status-header i {
  font-size: 20px;
}

.status-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.idle .status-header i {
  color: #67C23A;
}

.busy .status-header i {
  color: #409EFF;
}

.warning .status-header i {
  color: #E6A23C;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 15px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.map-card {
  margin-bottom: 20px;
}

.map-container {
  padding: 10px 0;
}

.warehouse-map {
  height: 320px;
  border: 1px solid #EBEEF5;
  margin-bottom: 10px;
}

.map-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  grid-template-rows: repeat(4, 1fr);
  gap: 5px;
  height: 100%;
  padding: 5px;
}

.map-area {
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  padding: 10px;
  position: relative;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.area-name {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 10px;
}

.area-rgvs {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.rgv-dot {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: bold;
  color: white;
  cursor: pointer;
}

.idle-status {
  background-color: #67C23A;
}

.running-status {
  background-color: #409EFF;
}

.blocked-status {
  background-color: #F56C6C;
}

.error-status {
  background-color: #E6A23C;
}

.map-legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 10px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.rgv-list-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.rgv-detail {
  padding: 10px;
}

.detail-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 