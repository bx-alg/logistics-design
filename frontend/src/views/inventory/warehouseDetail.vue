<template>
  <div class="app-container">
    <div class="page-header">
      <div class="header-title">
        <el-page-header @back="goBack" :content="'仓库详情: ' + warehouseData.warehouseName" />
      </div>
      <div class="header-actions">
        <el-button type="success" size="small" @click="handleEdit">编辑仓库</el-button>
        <el-button type="danger" size="small" @click="handleDelete">删除仓库</el-button>
      </div>
    </div>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card class="warehouse-info-card">
          <div slot="header" class="clearfix">
            <span>基本信息</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="info-item">
                <div class="info-label">仓库名称</div>
                <div class="info-value">{{ warehouseData.warehouseName }}</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <div class="info-label">仓库编号</div>
                <div class="info-value">{{ warehouseData.warehouseCode }}</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <div class="info-label">仓库类型</div>
                <div class="info-value">
                  <el-tag>{{ getWarehouseTypeLabel(warehouseData.warehouseType) }}</el-tag>
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <div class="info-label">仓库面积</div>
                <div class="info-value">{{ warehouseData.area }} ㎡</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <div class="info-label">仓库容量</div>
                <div class="info-value">{{ warehouseData.capacity }} m³</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <div class="info-label">使用率</div>
                <div class="info-value">
                  <el-progress :percentage="warehouseData.utilization" :colors="progressColors"></el-progress>
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <div class="info-label">负责人</div>
                <div class="info-value">{{ warehouseData.manager }}</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <div class="info-label">联系电话</div>
                <div class="info-value">{{ warehouseData.contactPhone }}</div>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="info-item">
                <div class="info-label">仓库位置</div>
                <div class="info-value">{{ warehouseData.location }}</div>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="info-item">
                <div class="info-label">备注</div>
                <div class="info-value">{{ warehouseData.remark || '无' }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="warehouse-stats-card">
          <div slot="header" class="clearfix">
            <span>仓库统计</span>
          </div>
          <div class="stat-item" v-for="(stat, index) in warehouseStats" :key="index">
            <div class="stat-icon" :style="{ backgroundColor: stat.bgColor }">
              <i :class="stat.icon"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">{{ stat.title }}</div>
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-trend" v-if="stat.trend">
                <span :class="stat.trend > 0 ? 'trend-up' : 'trend-down'">
                  <i :class="stat.trend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  {{ Math.abs(stat.trend) }}%
                </span>
                <span class="trend-period">{{ stat.period }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>仓库商品列表</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="handleExport">导出列表</el-button>
      </div>
      <el-table
        v-loading="tableLoading"
        :data="inventoryData"
        border
        style="width: 100%"
      >
        <el-table-column prop="productCode" label="商品编码" width="130" />
        <el-table-column label="商品信息" min-width="200">
          <template slot-scope="scope">
            <div class="product-info">
              <div class="product-detail">
                <div class="product-name">{{ scope.row.productName }}</div>
                <div class="product-category">{{ scope.row.categoryName }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="库存数量" width="100" align="center" />
        <el-table-column prop="reservedQuantity" label="预留数量" width="100" align="center" />
        <el-table-column label="可用库存" width="100" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.quantity - scope.row.reservedQuantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="shelfLocation" label="货架位置" width="120" align="center" />
        <el-table-column prop="inboundTime" label="入库时间" width="180" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleInventoryDetail(scope.row)">详情</el-button>
            <el-button size="mini" type="warning" @click="handleMove(scope.row)">移库</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top: 20px; text-align: right;"
      />
    </el-card>

    <el-card style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>入库记录</span>
      </div>
      <el-timeline>
        <el-timeline-item
          v-for="(activity, index) in activities"
          :key="index"
          :timestamp="activity.timestamp"
          :type="activity.type"
          :color="activity.color"
          :size="activity.size"
        >
          {{ activity.content }}
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'WarehouseDetail',
  data() {
    return {
      warehouseId: null,
      warehouseData: {
        id: 1,
        warehouseCode: 'WH001',
        warehouseName: '织里童装成品仓',
        warehouseType: 'finished',
        location: '浙江省湖州市织里镇童装大道168号',
        area: 3500,
        capacity: 12000,
        utilization: 75,
        manager: '张仓',
        contactPhone: '13912345678',
        createTime: '2022-03-15 10:30:45',
        remark: '主要存放童装成品，包括上衣、裤子、裙装等各类童装产品。仓库配备有自动分拣系统和现代化的存储设施，能够高效地完成入库、分拣和出库作业。'
      },
      progressColors: [
        { color: '#f56c6c', percentage: 20 },
        { color: '#e6a23c', percentage: 40 },
        { color: '#5cb87a', percentage: 60 },
        { color: '#1989fa', percentage: 80 },
        { color: '#6f7ad3', percentage: 100 }
      ],
      warehouseTypeOptions: [
        { value: 'raw', label: '面料仓' },
        { value: 'finished', label: '成品仓' },
        { value: 'accessory', label: '辅料仓' },
        { value: 'export', label: '出口仓' },
        { value: 'return', label: '退货仓' }
      ],
      warehouseStats: [
        { title: '商品SKU数', value: '285', trend: 5.8, period: '较上月', icon: 'el-icon-s-goods', bgColor: '#ecf5ff' },
        { title: '库存总量', value: '18,562', trend: 7.2, period: '较上月', icon: 'el-icon-s-grid', bgColor: '#f0f9eb' },
        { title: '今日入库', value: '1,253', trend: 12.5, period: '较昨日', icon: 'el-icon-s-promotion', bgColor: '#fdf6ec' },
        { title: '今日出库', value: '968', trend: -3.2, period: '较昨日', icon: 'el-icon-s-marketing', bgColor: '#fef0f0' },
        { title: '库存周转率', value: '4.8次/月', trend: 0.6, period: '较上月', icon: 'el-icon-refresh', bgColor: '#f4f4f5' }
      ],
      tableLoading: false,
      inventoryData: [
        { id: 1, productCode: 'P001', productName: '儿童夏季短袖T恤', categoryName: '上衣', quantity: 1200, reservedQuantity: 350, shelfLocation: 'A-01-01', inboundTime: '2024-05-01 09:30:45' },
        { id: 2, productCode: 'P002', productName: '儿童牛仔裤', categoryName: '裤子', quantity: 850, reservedQuantity: 200, shelfLocation: 'A-01-02', inboundTime: '2024-05-02 10:15:22' },
        { id: 3, productCode: 'P003', productName: '女童公主连衣裙', categoryName: '裙装', quantity: 650, reservedQuantity: 120, shelfLocation: 'A-02-01', inboundTime: '2024-05-03 11:40:18' },
        { id: 4, productCode: 'P004', productName: '儿童运动套装', categoryName: '套装', quantity: 480, reservedQuantity: 85, shelfLocation: 'A-02-02', inboundTime: '2024-05-04 14:25:36' },
        { id: 5, productCode: 'P005', productName: '婴儿连体衣', categoryName: '婴儿装', quantity: 750, reservedQuantity: 180, shelfLocation: 'B-01-01', inboundTime: '2024-05-05 08:50:15' }
      ],
      pageNum: 1,
      pageSize: 10,
      total: 5,
      activities: [
        { content: '入库1200件儿童夏季短袖T恤，操作员：王五', timestamp: '2024-05-05 15:30:22', type: 'primary' },
        { content: '入库850件儿童牛仔裤，操作员：李四', timestamp: '2024-05-04 10:45:36', type: 'success' },
        { content: '出库320件儿童防晒服，订单号：ORD20240504001，操作员：张三', timestamp: '2024-05-03 09:20:15', type: 'warning' },
        { content: '入库480件儿童运动套装，操作员：赵六', timestamp: '2024-05-02 14:10:45', type: 'info' },
        { content: '仓库盘点完成，盘盈15件，盘亏8件，操作员：刘管', timestamp: '2024-05-01 16:30:00', type: 'danger' }
      ]
    }
  },
  created() {
    const id = this.$route.params.id
    if (id && !isNaN(parseInt(id))) {
      this.warehouseId = parseInt(id)
      this.getWarehouseDetail()
    } else {
      this.$message.error('无效的仓库ID')
      this.goBack()
    }
  },
  methods: {
    getWarehouseDetail() {
      // 模拟获取仓库详情的API调用
      this.tableLoading = true
      setTimeout(() => {
        // 实际项目中这里会根据ID请求后端API获取数据
        // 这里使用写死的数据
        this.tableLoading = false
      }, 500)
    },
    goBack() {
      this.$router.push({ path: '/inventory/warehouse' })
    },
    getWarehouseTypeLabel(type) {
      const option = this.warehouseTypeOptions.find(item => item.value === type)
      return option ? option.label : '未知类型'
    },
    handleEdit() {
      this.$message.info('编辑功能待实现')
    },
    handleDelete() {
      this.$confirm(`确认删除仓库【${this.warehouseData.warehouseName}】吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('删除成功')
        this.goBack()
      }).catch(() => {})
    },
    handleExport() {
      this.$message.info('导出功能待实现')
    },
    handleInventoryDetail(row) {
      this.$message.info(`查看商品 ${row.productName} 详情`)
    },
    handleMove(row) {
      this.$message.info(`移动商品 ${row.productName} 到其他仓库`)
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getWarehouseDetail()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getWarehouseDetail()
    }
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background-color: #fff;
  padding: 15px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.header-title {
  display: flex;
  align-items: center;
}
.header-actions {
  display: flex;
  gap: 10px;
}
.warehouse-info-card {
  margin-bottom: 20px;
}
.warehouse-image-container {
  margin-bottom: 20px;
  text-align: center;
}
.warehouse-main-image {
  width: 100%;
  height: 240px;
  object-fit: cover;
  border-radius: 4px;
}
.info-item {
  margin-bottom: 15px;
}
.info-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}
.info-value {
  font-size: 14px;
  color: #303133;
}
.warehouse-stats-card {
  margin-bottom: 20px;
}
.stat-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #EBEEF5;
}
.stat-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: #fff;
  font-size: 24px;
}
.stat-content {
  flex: 1;
}
.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}
.stat-value {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 5px;
}
.stat-trend {
  font-size: 12px;
}
.trend-up {
  color: #67C23A;
}
.trend-down {
  color: #F56C6C;
}
.trend-period {
  color: #909399;
  margin-left: 5px;
}
.product-info {
  display: flex;
  align-items: center;
  justify-content: center;
}
.product-detail {
  text-align: center;
}
.product-name {
  font-size: 14px;
  margin-bottom: 5px;
}
.product-category {
  font-size: 12px;
  color: #909399;
}
</style> 