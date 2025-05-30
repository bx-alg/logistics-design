<template>
  <div class="agv-monitor-container">
    <h2 class="page-title">AGV设备监控</h2>
    
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
    
    <!-- AGV设备状态概览 -->
    <el-row :gutter="20" class="status-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="status-card">
          <div class="status-header">
            <i class="el-icon-shopping-cart-1"></i>
            <span>AGV总数</span>
          </div>
          <div class="status-value">{{ agvStats.total }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="status-card idle">
          <div class="status-header">
            <i class="el-icon-success"></i>
            <span>空闲AGV</span>
          </div>
          <div class="status-value">{{ agvStats.idle }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="status-card busy">
          <div class="status-header">
            <i class="el-icon-loading"></i>
            <span>运行中AGV</span>
          </div>
          <div class="status-value">{{ agvStats.running }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="status-card warning">
          <div class="status-header">
            <i class="el-icon-warning"></i>
            <span>充电中AGV</span>
          </div>
          <div class="status-value">{{ agvStats.charging }}</div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- AGV设备运行情况 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card shadow="hover" class="stat-card">
          <div slot="header" class="card-header">
            <span>AGV运行统计</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-label">任务完成数</div>
                <div class="stat-value">{{ agvStats.tasksCompleted }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-label">总运行时间(h)</div>
                <div class="stat-value">{{ agvStats.totalRunTime }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-label">总行驶距离(km)</div>
                <div class="stat-value">{{ agvStats.totalDistance }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-label">平均电量(%)</div>
                <div class="stat-value">{{ agvStats.avgBattery }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- AGV当前位置地图 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card shadow="hover" class="map-card">
          <div slot="header" class="card-header">
            <span>AGV位置分布</span>
          </div>
          <div class="map-container">
            <div class="warehouse-map">
              <div class="map-grid">
                <div v-for="(area, index) in warehouseAreas" :key="index" class="map-area" :style="getAreaStyle(area)">
                  <span class="area-name">{{ area.name }}</span>
                  <div v-if="area.agvs && area.agvs.length > 0" class="area-agvs">
                    <div v-for="agv in area.agvs" :key="agv.id" class="agv-dot" :class="getAgvStatusClass(agv.status)" @click="showAgvDetail(agv)">
                      {{ agv.id.split('-')[1] }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="map-legend">
              <div class="legend-item">
                <div class="agv-dot idle-status"></div>
                <span>空闲</span>
              </div>
              <div class="legend-item">
                <div class="agv-dot running-status"></div>
                <span>运行中</span>
              </div>
              <div class="legend-item">
                <div class="agv-dot charging-status"></div>
                <span>充电中</span>
              </div>
              <div class="legend-item">
                <div class="agv-dot error-status"></div>
                <span>故障</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- AGV设备列表 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card shadow="hover" class="agv-list-card">
          <div slot="header" class="card-header">
            <span>AGV设备列表</span>
            <div class="header-actions">
              <el-select v-model="statusFilter" placeholder="状态筛选" size="small" clearable>
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
              </el-select>
            </div>
          </div>
          <el-table
            v-loading="loading"
            :data="filteredAgvList"
            style="width: 100%"
            border>
            <el-table-column
              prop="id"
              label="AGV编号"
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
              prop="battery"
              label="电量"
              width="100">
              <template slot-scope="scope">
                <el-progress 
                  :percentage="scope.row.battery" 
                  :color="getBatteryColor(scope.row.battery)">
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
                <el-button type="text" size="small" @click="controlAgv(scope.row)">控制</el-button>
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
    
    <!-- AGV详情对话框 -->
    <el-dialog title="AGV详情" :visible.sync="dialogVisible" width="50%">
      <div v-if="selectedAgv" class="agv-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="AGV编号">{{ selectedAgv.id }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getStatusType(selectedAgv.status)">{{ selectedAgv.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="当前位置">{{ selectedAgv.location }}</el-descriptions-item>
          <el-descriptions-item label="电量">
            <el-progress :percentage="selectedAgv.battery" :color="getBatteryColor(selectedAgv.battery)"></el-progress>
          </el-descriptions-item>
          <el-descriptions-item label="型号">{{ selectedAgv.model }}</el-descriptions-item>
          <el-descriptions-item label="载重能力">{{ selectedAgv.loadCapacity }}kg</el-descriptions-item>
          <el-descriptions-item label="最大速度">{{ selectedAgv.maxSpeed }}m/s</el-descriptions-item>
          <el-descriptions-item label="任务完成数">{{ selectedAgv.taskCount }}</el-descriptions-item>
          <el-descriptions-item label="总运行时间">{{ selectedAgv.runTime }}小时</el-descriptions-item>
          <el-descriptions-item label="总行驶距离">{{ selectedAgv.distance }}km</el-descriptions-item>
          <el-descriptions-item label="投入使用时间">{{ selectedAgv.deploymentDate }}</el-descriptions-item>
          <el-descriptions-item label="上次维护时间">{{ selectedAgv.lastMaintenance }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="detail-actions">
          <el-button type="primary" size="small" @click="controlAgv(selectedAgv)">远程控制</el-button>
          <el-button type="warning" size="small" @click="maintenance(selectedAgv)">申请维护</el-button>
          <el-button type="info" size="small" @click="viewHistory(selectedAgv)">历史记录</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'AgvMonitor',
  data() {
    return {
      loading: false,
      dialogVisible: false,
      selectedAgv: null,
      dateRange: [],
      statusFilter: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      
      statusOptions: [
        { value: '空闲', label: '空闲' },
        { value: '运行中', label: '运行中' },
        { value: '充电中', label: '充电中' },
        { value: '故障', label: '故障' }
      ],
      
      agvStats: {
        total: 12,
        idle: 4,
        running: 6,
        charging: 2,
        tasksCompleted: 1856,
        totalRunTime: 3680,
        totalDistance: 4250.8,
        avgBattery: 78
      },
      
      warehouseAreas: [
        { name: '入库区', x: 0, y: 0, width: 2, height: 2, color: '#E1F3D8', agvs: [
          { id: 'AGV-001', status: '空闲' },
          { id: 'AGV-007', status: '运行中' }
        ]},
        { name: '出库区', x: 0, y: 2, width: 2, height: 2, color: '#FCDCEF', agvs: [
          { id: 'AGV-003', status: '运行中' },
          { id: 'AGV-011', status: '运行中' }
        ]},
        { name: '生产区A', x: 2, y: 0, width: 2, height: 1, color: '#D0E6FF', agvs: [
          { id: 'AGV-004', status: '空闲' }
        ]},
        { name: '生产区B', x: 2, y: 1, width: 2, height: 1, color: '#D0E6FF', agvs: [
          { id: 'AGV-005', status: '运行中' },
          { id: 'AGV-006', status: '故障' }
        ]},
        { name: '仓储区A', x: 4, y: 0, width: 1, height: 2, color: '#FCE6D2', agvs: [
          { id: 'AGV-002', status: '空闲' }
        ]},
        { name: '仓储区B', x: 5, y: 0, width: 1, height: 2, color: '#FCE6D2', agvs: [
          { id: 'AGV-008', status: '运行中' }
        ]},
        { name: '装配区', x: 4, y: 2, width: 2, height: 1, color: '#F2E6FF', agvs: [
          { id: 'AGV-009', status: '运行中' }
        ]},
        { name: '充电区', x: 4, y: 3, width: 2, height: 1, color: '#FFEECC', agvs: [
          { id: 'AGV-010', status: '充电中' },
          { id: 'AGV-012', status: '充电中' }
        ]},
        { name: '包装区', x: 2, y: 2, width: 2, height: 2, color: '#E6F7FF', agvs: [
          { id: 'AGV-013', status: '空闲' }
        ]}
      ],
      
      agvList: [
        {
          id: 'AGV-001',
          status: '空闲',
          location: '入库区',
          battery: 85,
          taskCount: 203,
          runTime: 420.5,
          distance: 520.8,
          model: 'KivaBot-X2',
          loadCapacity: 800,
          maxSpeed: 1.5,
          deploymentDate: '2022-10-15',
          lastMaintenance: '2023-05-22'
        },
        {
          id: 'AGV-002',
          status: '空闲',
          location: '仓储区A',
          battery: 92,
          taskCount: 185,
          runTime: 380.2,
          distance: 460.5,
          model: 'KivaBot-X2',
          loadCapacity: 800,
          maxSpeed: 1.5,
          deploymentDate: '2022-10-15',
          lastMaintenance: '2023-05-28'
        },
        {
          id: 'AGV-003',
          status: '运行中',
          location: '出库区',
          battery: 65,
          taskCount: 210,
          runTime: 430.8,
          distance: 530.2,
          model: 'KivaBot-X2',
          loadCapacity: 800,
          maxSpeed: 1.5,
          deploymentDate: '2022-10-15',
          lastMaintenance: '2023-06-02'
        },
        {
          id: 'AGV-004',
          status: '空闲',
          location: '生产区A',
          battery: 88,
          taskCount: 175,
          runTime: 350.5,
          distance: 430.8,
          model: 'KivaBot-X2',
          loadCapacity: 800,
          maxSpeed: 1.5,
          deploymentDate: '2022-11-05',
          lastMaintenance: '2023-06-10'
        },
        {
          id: 'AGV-005',
          status: '运行中',
          location: '生产区B',
          battery: 72,
          taskCount: 198,
          runTime: 390.2,
          distance: 480.5,
          model: 'KivaBot-X3',
          loadCapacity: 1000,
          maxSpeed: 1.8,
          deploymentDate: '2022-11-05',
          lastMaintenance: '2023-06-15'
        },
        {
          id: 'AGV-006',
          status: '故障',
          location: '生产区B',
          battery: 45,
          taskCount: 165,
          runTime: 320.8,
          distance: 390.2,
          model: 'KivaBot-X3',
          loadCapacity: 1000,
          maxSpeed: 1.8,
          deploymentDate: '2022-11-05',
          lastMaintenance: '2023-04-20'
        },
        {
          id: 'AGV-007',
          status: '运行中',
          location: '入库区',
          battery: 68,
          taskCount: 192,
          runTime: 380.5,
          distance: 470.8,
          model: 'KivaBot-X3',
          loadCapacity: 1000,
          maxSpeed: 1.8,
          deploymentDate: '2022-12-10',
          lastMaintenance: '2023-06-22'
        },
        {
          id: 'AGV-008',
          status: '运行中',
          location: '仓储区B',
          battery: 75,
          taskCount: 188,
          runTime: 370.2,
          distance: 450.5,
          model: 'KivaBot-X3',
          loadCapacity: 1000,
          maxSpeed: 1.8,
          deploymentDate: '2022-12-10',
          lastMaintenance: '2023-06-28'
        },
        {
          id: 'AGV-009',
          status: '运行中',
          location: '装配区',
          battery: 60,
          taskCount: 180,
          runTime: 360.8,
          distance: 440.2,
          model: 'KivaBot-X4',
          loadCapacity: 1200,
          maxSpeed: 2.0,
          deploymentDate: '2023-01-15',
          lastMaintenance: '2023-07-05'
        },
        {
          id: 'AGV-010',
          status: '充电中',
          location: '充电区',
          battery: 30,
          taskCount: 172,
          runTime: 340.5,
          distance: 420.8,
          model: 'KivaBot-X4',
          loadCapacity: 1200,
          maxSpeed: 2.0,
          deploymentDate: '2023-01-15',
          lastMaintenance: '2023-07-10'
        },
        {
          id: 'AGV-011',
          status: '运行中',
          location: '出库区',
          battery: 82,
          taskCount: 165,
          runTime: 330.2,
          distance: 410.5,
          model: 'KivaBot-X4',
          loadCapacity: 1200,
          maxSpeed: 2.0,
          deploymentDate: '2023-02-20',
          lastMaintenance: '2023-07-15'
        },
        {
          id: 'AGV-012',
          status: '充电中',
          location: '充电区',
          battery: 25,
          taskCount: 158,
          runTime: 320.8,
          distance: 400.2,
          model: 'KivaBot-X4',
          loadCapacity: 1200,
          maxSpeed: 2.0,
          deploymentDate: '2023-02-20',
          lastMaintenance: '2023-07-20'
        },
        {
          id: 'AGV-013',
          status: '空闲',
          location: '包装区',
          battery: 90,
          taskCount: 105,
          runTime: 280.5,
          distance: 340.8,
          model: 'KivaBot-X5',
          loadCapacity: 1500,
          maxSpeed: 2.2,
          deploymentDate: '2023-04-10',
          lastMaintenance: '2023-07-25'
        }
      ]
    }
  },
  computed: {
    filteredAgvList() {
      if (!this.statusFilter) {
        return this.agvList
      }
      return this.agvList.filter(agv => agv.status === this.statusFilter)
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
        case '充电中': return 'warning'
        case '故障': return 'danger'
        default: return 'info'
      }
    },
    getBatteryColor(battery) {
      if (battery < 30) return '#F56C6C'
      if (battery < 60) return '#E6A23C'
      return '#67C23A'
    },
    getAreaStyle(area) {
      return {
        gridColumn: `${area.x + 1} / span ${area.width}`,
        gridRow: `${area.y + 1} / span ${area.height}`,
        backgroundColor: area.color
      }
    },
    getAgvStatusClass(status) {
      switch (status) {
        case '空闲': return 'idle-status'
        case '运行中': return 'running-status'
        case '充电中': return 'charging-status'
        case '故障': return 'error-status'
        default: return ''
      }
    },
    showAgvDetail(agv) {
      this.selectedAgv = this.agvList.find(item => item.id === agv.id)
      this.dialogVisible = true
    },
    viewDetail(agv) {
      this.selectedAgv = agv
      this.dialogVisible = true
    },
    controlAgv(agv) {
      this.$message({
        message: `AGV远程控制功能开发中...(${agv.id})`,
        type: 'info'
      })
    },
    maintenance(agv) {
      this.$message({
        message: `AGV维护申请功能开发中...(${agv.id})`,
        type: 'info'
      })
    },
    viewHistory(agv) {
      this.$message({
        message: `AGV历史记录功能开发中...(${agv.id})`,
        type: 'info'
      })
    }
  },
  created() {
    this.total = this.agvList.length
  }
}
</script>

<style scoped>
.agv-monitor-container {
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

.area-agvs {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.agv-dot {
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

.charging-status {
  background-color: #E6A23C;
}

.error-status {
  background-color: #F56C6C;
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

.agv-list-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.agv-detail {
  padding: 10px;
}

.detail-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
</code_block_to_apply_changes_from>