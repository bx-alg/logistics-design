<template>
  <div class="app-container">
    <el-card class="map-container">
      <div slot="header">
        <span>物流实时追踪</span>
      </div>
      <div class="search-bar">
        <el-input v-model="searchTrackingCode" placeholder="请输入运输单号" style="width: 250px;" class="search-input">
          <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
        </el-input>
      </div>
      
      <div class="tracking-container">
        <div class="map-view" ref="mapContainer">
          <!-- 高德地图容器 -->
          <div id="amap-container" class="amap-container"></div>
        </div>
        
        <div class="tracking-info">
          <div v-if="currentShipment" class="shipment-detail">
            <h3>运输详情</h3>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="运输单号">{{ currentShipment.shipmentNo }}</el-descriptions-item>
              <el-descriptions-item label="承运商">{{ currentShipment.carrierName }}</el-descriptions-item>
              <el-descriptions-item label="发货仓库">{{ currentShipment.fromWarehouse }}</el-descriptions-item>
              <el-descriptions-item label="目的地">{{ currentShipment.toAddress }}</el-descriptions-item>
              <el-descriptions-item label="预计到达时间">{{ currentShipment.estimatedArrivalTime }}</el-descriptions-item>
              <el-descriptions-item label="运输状态">
                <el-tag :type="getStatusType(currentShipment.status)">{{ getStatusLabel(currentShipment.status) }}</el-tag>
              </el-descriptions-item>
            </el-descriptions>
            
            <h3 style="margin-top: 20px;">运输进度</h3>
            <el-progress :percentage="currentShipment.progress" :status="currentShipment.progress === 100 ? 'success' : ''" :stroke-width="15" style="margin-bottom: 20px;"></el-progress>
            
            <h3>物流轨迹</h3>
            <el-timeline>
              <el-timeline-item
                v-for="(track, index) in currentShipment.trackInfo"
                :key="index"
                :timestamp="track.time"
                :type="index === 0 ? 'primary' : ''"
                :icon="track.icon"
              >
                {{ track.content }}
              </el-timeline-item>
            </el-timeline>
          </div>
          <div v-else class="empty-info">
            <i class="el-icon-location-information"></i>
            <p>请输入运输单号查询物流信息</p>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="list-container" style="margin-top: 20px;">
      <div slot="header">
        <span>在途运输概览</span>
        <el-radio-group v-model="listViewType" size="mini" style="float: right;">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="urgent">加急</el-radio-button>
          <el-radio-button label="delay">延误</el-radio-button>
        </el-radio-group>
      </div>
      <el-table :data="filteredShipmentList" border style="width: 100%" @row-click="handleRowClick">
        <el-table-column prop="shipmentNo" label="运输单号" align="center" width="150" />
        <el-table-column prop="carrierName" label="承运商" align="center" width="150" />
        <el-table-column prop="fromWarehouse" label="发货仓库" align="center" width="120" />
        <el-table-column prop="toAddress" label="目的地" align="center" />
        <el-table-column prop="estimatedArrivalTime" label="预计到达时间" align="center" width="170" />
        <el-table-column label="运输状态" align="center" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="运输进度" align="center" width="180">
          <template slot-scope="scope">
            <el-progress :percentage="scope.row.progress" :status="scope.row.progress === 100 ? 'success' : ''"></el-progress>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isDelay" type="danger">延误</el-tag>
            <el-tag v-else-if="scope.row.isUrgent" type="warning">加急</el-tag>
            <el-tag v-else type="success">正常</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ShipmentTracking',
  data() {
    return {
      searchTrackingCode: '',
      listViewType: 'all',
      currentShipment: null,
      map: null,
      marker: null,
      startMarker: null,
      endMarker: null,
      polyline: null,
      shipmentList: [
        {
          shipmentNo: 'SHP20230001',
          carrierName: '顺丰速运',
          fromWarehouse: '上海仓',
          toAddress: '北京市朝阳区望京SOHO',
          estimatedArrivalTime: '2023-06-15 15:00:00',
          status: 2,
          progress: 45,
          isUrgent: true,
          isDelay: false,
          fromLocation: [121.4737, 31.2304], // 上海坐标
          toLocation: [116.4809, 39.9984], // 北京望京坐标
          currentLocation: [115.0328, 35.7538], // 当前位置坐标
          trackInfo: [
            { time: '2023-06-05 10:25:09', content: '运输中，当前位置：河南省郑州市', icon: 'el-icon-truck' },
            { time: '2023-06-03 16:28:10', content: '快件已从上海仓库发出', icon: 'el-icon-box' },
            { time: '2023-06-03 14:52:36', content: '顺丰速运已收取快件', icon: 'el-icon-takeaway-box' },
            { time: '2023-06-03 10:25:09', content: '快件信息已生成，等待揽收', icon: 'el-icon-receiving' }
          ]
        },
        {
          shipmentNo: 'SHP20230002',
          carrierName: '京东物流',
          fromWarehouse: '北京仓',
          toAddress: '上海市浦东新区张江高科技园区',
          estimatedArrivalTime: '2023-06-12 10:30:00',
          status: 2,
          progress: 25,
          isUrgent: false,
          isDelay: false,
          fromLocation: [116.4074, 39.9042], // 北京坐标
          toLocation: [121.5980, 31.2304], // 上海张江坐标
          currentLocation: [118.7969, 32.0603], // 南京坐标
          trackInfo: [
            { time: '2023-06-04 08:15:22', content: '运输中，当前位置：江苏省南京市', icon: 'el-icon-truck' },
            { time: '2023-06-03 18:42:15', content: '快件已从北京仓库发出', icon: 'el-icon-box' },
            { time: '2023-06-03 15:32:46', content: '京东物流已收取快件', icon: 'el-icon-takeaway-box' },
            { time: '2023-06-03 09:18:53', content: '快件信息已生成，等待揽收', icon: 'el-icon-receiving' }
          ]
        },
        {
          shipmentNo: 'SHP20230003',
          carrierName: '中通快递',
          fromWarehouse: '广州仓',
          toAddress: '深圳市南山区科技园',
          estimatedArrivalTime: '2023-06-13 16:45:00',
          status: 2,
          progress: 65,
          isUrgent: false,
          isDelay: true,
          fromLocation: [113.2644, 23.1291], // 广州坐标
          toLocation: [113.9438, 22.5255], // 深圳南山坐标
          currentLocation: [114.4126, 23.1116], // 惠州坐标
          trackInfo: [
            { time: '2023-06-05 14:35:42', content: '运输中，当前位置：广东省惠州市', icon: 'el-icon-truck' },
            { time: '2023-06-04 09:28:17', content: '快件已从广州仓库发出', icon: 'el-icon-box' },
            { time: '2023-06-04 07:52:31', content: '中通快递已收取快件', icon: 'el-icon-takeaway-box' },
            { time: '2023-06-03 17:45:09', content: '快件信息已生成，等待揽收', icon: 'el-icon-receiving' }
          ]
        },
        {
          shipmentNo: 'SHP20230004',
          carrierName: '德邦物流',
          fromWarehouse: '深圳仓',
          toAddress: '成都市高新区天府大道',
          estimatedArrivalTime: '2023-06-10 09:15:00',
          status: 2,
          progress: 78,
          isUrgent: true,
          isDelay: true,
          fromLocation: [114.0579, 22.5431], // 深圳坐标
          toLocation: [104.0680, 30.5728], // 成都坐标
          currentLocation: [104.6795, 31.4670], // 绵阳坐标
          trackInfo: [
            { time: '2023-06-06 12:25:33', content: '运输中，当前位置：四川省绵阳市', icon: 'el-icon-truck' },
            { time: '2023-06-04 14:48:26', content: '快件已从深圳仓库发出', icon: 'el-icon-box' },
            { time: '2023-06-04 11:32:41', content: '德邦物流已收取快件', icon: 'el-icon-takeaway-box' },
            { time: '2023-06-04 08:15:19', content: '快件信息已生成，等待揽收', icon: 'el-icon-receiving' }
          ]
        }
      ]
    }
  },
  computed: {
    filteredShipmentList() {
      if (this.listViewType === 'all') {
        return this.shipmentList
      } else if (this.listViewType === 'urgent') {
        return this.shipmentList.filter(item => item.isUrgent)
      } else if (this.listViewType === 'delay') {
        return this.shipmentList.filter(item => item.isDelay)
      }
      return this.shipmentList
    }
  },
  mounted() {
    // 加载高德地图脚本
    this.loadAMapScript()
  },
  methods: {
    // 加载高德地图脚本
    loadAMapScript() {
      const key = '885e5e784f95d95af217334223071400' // 替换为您的高德地图 API Key
      const script = document.createElement('script')
      script.type = 'text/javascript'
      script.async = true
      script.src = `https://webapi.amap.com/maps?v=2.0&key=${key}&plugin=AMap.Driving`
      script.onload = () => {
        this.initMap()
      }
      document.head.appendChild(script)
    },
    
    // 初始化地图
    initMap() {
      this.map = new AMap.Map('amap-container', {
        zoom: 6,
        center: [116.397428, 39.90923] // 默认中心点，中国中心
      })
      
      // 如果有当前运输信息，展示路线
      if (this.currentShipment) {
        this.showShipmentRoute(this.currentShipment)
      }
    },
    
    // 显示物流路线
    showShipmentRoute(shipment) {
      // 清除之前的标记和路线
      this.clearMapOverlays()
      
      // 创建起点标记
      this.startMarker = new AMap.Marker({
        position: shipment.fromLocation,
        map: this.map,
        icon: new AMap.Icon({
          size: new AMap.Size(25, 34),
          image: '//a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png',
          imageSize: new AMap.Size(25, 34)
        }),
        title: shipment.fromWarehouse
      })
      
      // 创建终点标记
      this.endMarker = new AMap.Marker({
        position: shipment.toLocation,
        map: this.map,
        icon: new AMap.Icon({
          size: new AMap.Size(25, 34),
          image: '//a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-red.png',
          imageSize: new AMap.Size(25, 34)
        }),
        title: shipment.toAddress
      })
      
      // 创建车辆当前位置标记
      this.marker = new AMap.Marker({
        position: shipment.currentLocation,
        map: this.map,
        icon: new AMap.Icon({
          size: new AMap.Size(32, 32),
          image: '//a.amap.com/jsapi_demos/static/demo-center/icons/blue.png',
          imageSize: new AMap.Size(32, 32)
        }),
        title: '运输车辆'
      })
      
      // 规划路线
      const driving = new AMap.Driving({
        map: this.map,
        policy: AMap.DrivingPolicy.LEAST_TIME
      })
      
      driving.search(
        shipment.fromLocation,
        shipment.toLocation,
        (status, result) => {
          if (status === 'complete') {
            // 根据起终点经纬度规划驾车导航路线
            this.map.setFitView()
          } else {
            // 如果规划失败，使用直线连接
            this.polyline = new AMap.Polyline({
              path: [shipment.fromLocation, shipment.currentLocation, shipment.toLocation],
              strokeColor: '#3366FF',
              strokeWeight: 5,
              strokeOpacity: 0.8,
              map: this.map
            })
            this.map.setFitView()
          }
        }
      )
    },
    
    // 清除地图上的标记和路线
    clearMapOverlays() {
      if (this.marker) {
        this.marker.setMap(null)
        this.marker = null
      }
      if (this.startMarker) {
        this.startMarker.setMap(null)
        this.startMarker = null
      }
      if (this.endMarker) {
        this.endMarker.setMap(null)
        this.endMarker = null
      }
      if (this.polyline) {
        this.polyline.setMap(null)
        this.polyline = null
      }
    },
    
    handleSearch() {
      if (!this.searchTrackingCode) {
        this.$message.warning('请输入运输单号')
        return
      }
      const found = this.shipmentList.find(item => item.shipmentNo === this.searchTrackingCode)
      if (found) {
        this.currentShipment = found
        this.$message.success('查询成功')
        
        // 查找成功后显示路线
        if (this.map) {
          this.showShipmentRoute(found)
        }
      } else {
        this.$message.error('未查询到相关运输单信息')
      }
    },
    
    handleRowClick(row) {
      this.currentShipment = row
      this.searchTrackingCode = row.shipmentNo
      
      // 点击行后显示路线
      if (this.map) {
        this.showShipmentRoute(row)
      }
    },
    getStatusType(status) {
      const statusMap = {
        0: 'info',
        1: 'primary',
        2: 'warning',
        3: 'success',
        4: 'danger'
      }
      return statusMap[status]
    },
    getStatusLabel(status) {
      const statusMap = {
        0: '待发货',
        1: '已发货',
        2: '运输中',
        3: '已送达',
        4: '已取消'
      }
      return statusMap[status]
    }
  }
}
</script>

<style scoped>
.search-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}
.tracking-container {
  display: flex;
  height: 500px;
}
.map-view {
  flex: 1;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}
.amap-container {
  width: 100%;
  height: 100%;
}
.tracking-info {
  width: 350px;
  margin-left: 20px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
}
.empty-info {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #909399;
}
.empty-info i {
  font-size: 48px;
  margin-bottom: 10px;
}
.list-container {
  margin-top: 20px;
}
</style> 