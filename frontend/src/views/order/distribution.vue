<template>
  <div class="app-container">
    <el-card class="filter-container">
      <div slot="header">
        <span>订单配送地图</span>
      </div>
      <div class="filter-item-container">
        <div class="filter-item">
          <span class="label">订单编号：</span>
          <el-input v-model="filterForm.orderNo" placeholder="请输入订单编号" style="width: 180px" clearable />
        </div>
        <div class="filter-item">
          <span class="label">客户名称：</span>
          <el-input v-model="filterForm.customerName" placeholder="请输入客户名称" style="width: 180px" clearable />
        </div>
        <div class="filter-item">
          <span class="label">配送状态：</span>
          <el-select v-model="filterForm.status" placeholder="请选择" style="width: 180px" clearable>
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
        <div class="filter-item">
          <span class="label">配送日期：</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            align="right"
            unlink-panels
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 300px"
            value-format="yyyy-MM-dd"
          />
        </div>
        <el-button type="primary" icon="el-icon-search" @click="handleFilter">查询</el-button>
        <el-button icon="el-icon-refresh" @click="resetFilter">重置</el-button>
      </div>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <!-- 地图容器 -->
        <el-card shadow="hover" style="height: 700px">
          <div slot="header">
            <span>配送地图</span>
            <div style="float: right; display: flex; align-items: center;">
              <el-tag size="mini" type="info" style="margin-right: 10px;">仓库</el-tag>
              <el-tag size="mini" type="primary" style="margin-right: 10px;">待发货</el-tag>
              <el-tag size="mini" type="warning" style="margin-right: 10px;">配送中</el-tag>
              <el-tag size="mini" type="success" style="margin-right: 10px;">已签收</el-tag>
            </div>
          </div>
          <div id="map-container" style="height: 630px; width: 100%;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <!-- 配送统计及列表 -->
        <el-card shadow="hover" style="margin-bottom: 20px;">
          <div slot="header">
            <span>配送统计</span>
          </div>
          <div class="stat-container">
            <div class="stat-item">
              <div class="stat-icon"><i class="el-icon-goods"></i></div>
              <div class="stat-content">
                <div class="stat-label">总订单</div>
                <div class="stat-value">{{ statistics.total }}</div>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #409EFF;"><i class="el-icon-time"></i></div>
              <div class="stat-content">
                <div class="stat-label">待发货</div>
                <div class="stat-value">{{ statistics.pending }}</div>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #E6A23C;"><i class="el-icon-truck"></i></div>
              <div class="stat-content">
                <div class="stat-label">配送中</div>
                <div class="stat-value">{{ statistics.delivering }}</div>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #67C23A;"><i class="el-icon-circle-check"></i></div>
              <div class="stat-content">
                <div class="stat-label">已签收</div>
                <div class="stat-value">{{ statistics.completed }}</div>
              </div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover">
          <div slot="header">
            <span>订单配送列表</span>
          </div>
          <el-table
            v-loading="tableLoading"
            :data="tableData"
            border
            style="width: 100%"
            height="540"
          >
            <el-table-column prop="orderNo" label="订单编号" width="120" />
            <el-table-column prop="customerName" label="客户名称" width="150" show-overflow-tooltip />
            <el-table-column label="配送状态" width="120">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusName(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="address" label="配送地址" show-overflow-tooltip />
            <el-table-column label="操作" width="80" fixed="right">
              <template slot-scope="scope">
                <el-button type="text" @click="handleViewDetail(scope.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 订单详情弹窗 -->
    <el-dialog title="配送详情" :visible.sync="detailDialogVisible" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="客户名称">{{ currentOrder.customerName }}</el-descriptions-item>
        <el-descriptions-item label="配送状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusName(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预计送达时间">{{ currentOrder.estimatedDeliveryTime }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.address }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentOrder.contactName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="物流单号">{{ currentOrder.trackingNo }}</el-descriptions-item>
        <el-descriptions-item label="物流公司">{{ currentOrder.logisticsCompany }}</el-descriptions-item>
        <el-descriptions-item label="发货时间" v-if="currentOrder.status >= 1">{{ currentOrder.shipTime }}</el-descriptions-item>
        <el-descriptions-item label="签收时间" v-if="currentOrder.status === 2">{{ currentOrder.deliveryTime }}</el-descriptions-item>
      </el-descriptions>

      <div v-if="currentOrder.status >= 1" style="margin-top: 20px;">
        <div class="detail-title">物流轨迹</div>
        <el-timeline>
          <el-timeline-item
            v-for="(activity, index) in currentOrder.trackingInfo"
            :key="index"
            :timestamp="activity.time"
            :type="getTimelineType(index, currentOrder.trackingInfo.length)"
            :color="getTimelineColor(index, currentOrder.trackingInfo.length)"
          >
            {{ activity.content }}
          </el-timeline-item>
        </el-timeline>
      </div>

      <div v-if="currentOrder.status >= 1" style="margin-top: 20px;">
        <div class="detail-title">配送商品</div>
        <el-table :data="currentOrder.items" border style="width: 100%">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="unit" label="单位" width="80" />
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'OrderDistribution',
  data() {
    return {
      // 筛选表单
      filterForm: {
        orderNo: '',
        customerName: '',
        status: '',
        startDate: '',
        endDate: '',
        pageNum: 1,
        pageSize: 10
      },
      dateRange: [],
      statusOptions: [
        { value: 0, label: '待发货' },
        { value: 1, label: '配送中' },
        { value: 2, label: '已签收' }
      ],
      // 表格数据
      tableData: [],
      tableLoading: false,
      // 统计数据
      statistics: {
        total: 0,
        pending: 0,
        delivering: 0,
        completed: 0
      },
      // 订单详情相关
      detailDialogVisible: false,
      currentOrder: {
        orderNo: '',
        customerName: '',
        status: 0,
        estimatedDeliveryTime: '',
        address: '',
        contactName: '',
        contactPhone: '',
        trackingNo: '',
        logisticsCompany: '',
        shipTime: '',
        deliveryTime: '',
        trackingInfo: [],
        items: []
      },
      // 地图相关
      map: null,
      warehouse: {
        name: '中心仓库',
        position: [121.4737, 31.2304], // 上海
        icon: 'warehouse'
      },
      deliveryPoints: []
    }
  },
  watch: {
    dateRange(val) {
      if (val && val.length === 2) {
        this.filterForm.startDate = val[0]
        this.filterForm.endDate = val[1]
      } else {
        this.filterForm.startDate = ''
        this.filterForm.endDate = ''
      }
    }
  },
  mounted() {
    this.fetchData()
    // 使用setTimeout模拟等待DOM加载完成
    setTimeout(() => {
      this.initMap()
    }, 500)
  },
  methods: {
    fetchData() {
      this.tableLoading = true
      
      // 模拟请求接口
      setTimeout(() => {
        this.tableData = [
          {
            id: 1,
            orderNo: 'DD20231001001',
            customerName: '上海某科技有限公司',
            status: 2,
            address: '上海市浦东新区张江高科技园区博云路2号',
            position: [121.5873, 31.2047],
            contactName: '张经理',
            contactPhone: '13812345678',
            estimatedDeliveryTime: '2023-10-05 15:00:00',
            trackingNo: 'SF1234567890',
            logisticsCompany: '顺丰速运',
            shipTime: '2023-10-03 10:30:00',
            deliveryTime: '2023-10-05 14:12:00',
            trackingInfo: [
              { time: '2023-10-03 10:30:00', content: '您的订单已由【上海中心仓库】发出' },
              { time: '2023-10-03 15:45:00', content: '快件已到达【上海转运中心】' },
              { time: '2023-10-04 08:30:00', content: '快件已从【上海转运中心】发出' },
              { time: '2023-10-04 10:15:00', content: '快件已到达【上海浦东新区张江营业点】' },
              { time: '2023-10-05 09:20:00', content: '【上海市】快递员小王（电话：13987654321）正在为您派送' },
              { time: '2023-10-05 14:12:00', content: '已签收，签收人：张经理' }
            ],
            items: [
              { productName: '智能手机A', quantity: 50, unit: '台' },
              { productName: '保护壳', quantity: 100, unit: '个' }
            ]
          },
          {
            id: 2,
            orderNo: 'DD20231005002',
            customerName: '北京某贸易有限公司',
            status: 1,
            address: '北京市海淀区中关村南大街5号',
            position: [116.3129, 39.9839],
            contactName: '李总',
            contactPhone: '13912345678',
            estimatedDeliveryTime: '2023-10-12 18:00:00',
            trackingNo: 'YT9876543210',
            logisticsCompany: '圆通速递',
            shipTime: '2023-10-07 11:20:00',
            deliveryTime: '',
            trackingInfo: [
              { time: '2023-10-07 11:20:00', content: '您的订单已由【上海中心仓库】发出' },
              { time: '2023-10-07 16:30:00', content: '快件已到达【上海转运中心】' },
              { time: '2023-10-08 08:15:00', content: '快件已从【上海转运中心】发出' },
              { time: '2023-10-09 10:30:00', content: '快件已到达【北京转运中心】' },
              { time: '2023-10-10 09:00:00', content: '快件已从【北京转运中心】发出' }
            ],
            items: [
              { productName: '笔记本电脑B', quantity: 20, unit: '台' },
              { productName: '鼠标', quantity: 25, unit: '个' },
              { productName: '键盘', quantity: 15, unit: '个' }
            ]
          },
          {
            id: 3,
            orderNo: 'DD20230925003',
            customerName: '广州某电子科技公司',
            status: 2,
            address: '广州市天河区珠江新城冼村路12号',
            position: [113.3301, 23.1350],
            contactName: '王经理',
            contactPhone: '13612345678',
            estimatedDeliveryTime: '2023-09-30 16:00:00',
            trackingNo: 'ZT5678901234',
            logisticsCompany: '中通快递',
            shipTime: '2023-09-26 09:40:00',
            deliveryTime: '2023-09-30 15:30:00',
            trackingInfo: [
              { time: '2023-09-26 09:40:00', content: '您的订单已由【上海中心仓库】发出' },
              { time: '2023-09-26 14:20:00', content: '快件已到达【上海转运中心】' },
              { time: '2023-09-27 07:30:00', content: '快件已从【上海转运中心】发出' },
              { time: '2023-09-28 11:45:00', content: '快件已到达【广州转运中心】' },
              { time: '2023-09-29 08:20:00', content: '快件已从【广州转运中心】发出' },
              { time: '2023-09-29 10:15:00', content: '快件已到达【广州天河区珠江营业点】' },
              { time: '2023-09-30 09:30:00', content: '【广州市】快递员小李（电话：13876543210）正在为您派送' },
              { time: '2023-09-30 15:30:00', content: '已签收，签收人：王经理' }
            ],
            items: [
              { productName: '平板电脑C', quantity: 30, unit: '台' },
              { productName: '充电器', quantity: 40, unit: '个' }
            ]
          },
          {
            id: 4,
            orderNo: 'DD20231008004',
            customerName: '深圳某科技有限公司',
            status: 0,
            address: '深圳市南山区科技园南区',
            position: [113.9534, 22.5407],
            contactName: '陈经理',
            contactPhone: '13312345678',
            estimatedDeliveryTime: '2023-10-15 14:00:00',
            trackingNo: '',
            logisticsCompany: '',
            shipTime: '',
            deliveryTime: '',
            trackingInfo: [],
            items: [
              { productName: '智能手表D', quantity: 100, unit: '个' },
              { productName: '蓝牙耳机', quantity: 150, unit: '个' }
            ]
          },
          {
            id: 5,
            orderNo: 'DD20231002005',
            customerName: '成都某机械有限公司',
            status: 1,
            address: '成都市高新区天府大道1388号',
            position: [104.0667, 30.5820],
            contactName: '周经理',
            contactPhone: '13512345678',
            estimatedDeliveryTime: '2023-10-11 16:00:00',
            trackingNo: 'JD1357924680',
            logisticsCompany: '京东物流',
            shipTime: '2023-10-04 14:10:00',
            deliveryTime: '',
            trackingInfo: [
              { time: '2023-10-04 14:10:00', content: '您的订单已由【上海中心仓库】发出' },
              { time: '2023-10-04 17:30:00', content: '快件已到达【上海转运中心】' },
              { time: '2023-10-05 08:00:00', content: '快件已从【上海转运中心】发出' },
              { time: '2023-10-06 20:15:00', content: '快件已到达【成都转运中心】' },
              { time: '2023-10-07 09:30:00', content: '快件已从【成都转运中心】发出' },
              { time: '2023-10-08 11:20:00', content: '快件已到达【成都高新区营业点】' }
            ],
            items: [
              { productName: '智能音箱E', quantity: 40, unit: '台' }
            ]
          }
        ]
        
        this.tableLoading = false
        this.updateStatistics()
        this.updateMapData()
      }, 1000)
    },
    updateStatistics() {
      this.statistics = {
        total: this.tableData.length,
        pending: this.tableData.filter(item => item.status === 0).length,
        delivering: this.tableData.filter(item => item.status === 1).length,
        completed: this.tableData.filter(item => item.status === 2).length
      }
    },
    // 获取状态类型
    getStatusType(status) {
      const typeMap = {
        0: 'primary',
        1: 'warning',
        2: 'success'
      }
      return typeMap[status] || 'info'
    },
    // 获取状态名称
    getStatusName(status) {
      const nameMap = {
        0: '待发货',
        1: '配送中',
        2: '已签收'
      }
      return nameMap[status] || '未知'
    },
    // 查询
    handleFilter() {
      this.fetchData()
    },
    // 重置筛选条件
    resetFilter() {
      this.filterForm = {
        orderNo: '',
        customerName: '',
        status: '',
        startDate: '',
        endDate: '',
        pageNum: 1,
        pageSize: 10
      }
      this.dateRange = []
      this.fetchData()
    },
    // 查看详情
    handleViewDetail(row) {
      this.currentOrder = JSON.parse(JSON.stringify(row))
      this.detailDialogVisible = true
    },
    // 获取时间线节点类型
    getTimelineType(index, total) {
      if (index === 0) return 'primary'
      if (index === total - 1) return 'success'
      return ''
    },
    // 获取时间线节点颜色
    getTimelineColor(index, total) {
      if (index === 0) return '#409EFF'
      if (index === total - 1) return '#67C23A'
      return ''
    },
    // 初始化地图
    initMap() {
      // 这里是模拟地图，实际项目中应该使用高德地图、百度地图等第三方地图API
      // 这里只创建一个简单的div来模拟地图效果
      const mapContainer = document.getElementById('map-container')
      if (!mapContainer) return

      mapContainer.innerHTML = '<div class="map-simulate"><div class="map-title">此处应为地图组件</div><div class="map-subtitle">实际项目中集成高德地图或百度地图API</div></div>'
      
      this.updateMapData()
    },
    // 更新地图数据
    updateMapData() {
      this.deliveryPoints = this.tableData.map(item => ({
        id: item.id,
        name: item.customerName,
        orderNo: item.orderNo,
        position: item.position,
        status: item.status
      }))
      
      // 在实际项目中，这里应该调用地图API更新标记点
      // 这里只是示意
      const mapContainer = document.getElementById('map-container')
      if (!mapContainer) return
      
      const mapSimulate = mapContainer.querySelector('.map-simulate')
      if (!mapSimulate) return
      
      // 添加一些标记点的示意代码
      let markersHtml = `<div class="map-markers">
        <div class="map-marker warehouse">
          <div class="marker-icon"><i class="el-icon-office-building"></i></div>
          <div class="marker-name">${this.warehouse.name}</div>
        </div>
      `
      
      this.deliveryPoints.forEach(point => {
        const statusClass = point.status === 0 ? 'pending' : (point.status === 1 ? 'delivering' : 'completed')
        const icon = point.status === 0 ? 'el-icon-time' : (point.status === 1 ? 'el-icon-truck' : 'el-icon-circle-check')
        
        markersHtml += `
          <div class="map-marker ${statusClass}" style="left: ${Math.random() * 80 + 10}%; top: ${Math.random() * 80 + 10}%">
            <div class="marker-icon"><i class="${icon}"></i></div>
            <div class="marker-name">${point.name}</div>
            <div class="marker-info">${point.orderNo}</div>
          </div>
        `
      })
      
      markersHtml += '</div>'
      mapSimulate.innerHTML += markersHtml
    }
  }
}
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.filter-item-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}
.filter-item {
  margin-right: 15px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}
.filter-item .label {
  margin-right: 5px;
}
.stat-container {
  display: flex;
  justify-content: space-between;
}
.stat-item {
  display: flex;
  align-items: center;
  padding: 10px;
  width: 23%;
}
.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #909399;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 10px;
}
.stat-content {
  flex: 1;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}
.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}
.detail-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #EBEEF5;
}
/* 模拟地图样式 */
.map-simulate {
  width: 100%;
  height: 100%;
  background-color: #E9EEF3;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.map-title {
  font-size: 24px;
  color: #909399;
  margin-bottom: 10px;
}
.map-subtitle {
  font-size: 14px;
  color: #C0C4CC;
}
.map-markers {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}
.map-marker {
  position: absolute;
  padding: 5px 10px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 100px;
}
.map-marker.warehouse {
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  background-color: #909399;
  color: #fff;
}
.map-marker.pending {
  background-color: #ecf5ff;
  border: 1px solid #d9ecff;
}
.map-marker.delivering {
  background-color: #fdf6ec;
  border: 1px solid #faecd8;
}
.map-marker.completed {
  background-color: #f0f9eb;
  border: 1px solid #e1f3d8;
}
.marker-icon {
  font-size: 20px;
  margin-bottom: 5px;
}
.marker-name {
  font-size: 12px;
  font-weight: bold;
  margin-bottom: 2px;
}
.marker-info {
  font-size: 10px;
  color: #909399;
}
</style> 