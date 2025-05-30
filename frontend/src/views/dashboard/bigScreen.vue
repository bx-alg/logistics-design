<template>
  <div class="board-container">
    <div class="time-stamp">{{ nowTime }}</div>
    <div class="board-title"><span>智慧物流平台 - 大屏展示</span></div>
    <div class="board-content flex-between">
      <div class="content-left flex-column-between">
        <div class="content-overview">
          <div class="box-title">库存总览</div>
          <div class="box-content flex-between">
            <div class="overview-object flex-column-center">
              <div class="object-count">5</div>
              <div class="object-name">仓库总数</div>
            </div>
            <div class="overview-meter flex-column-center">
              <div class="object-count">981</div>
              <div class="object-name">库位总数</div>
            </div>
            <div class="overview-alarm flex-column-center">
              <div class="object-count">4</div>
              <div class="object-name">异常库位</div>
            </div>
          </div>
        </div>
        <div class="content-status">
          <div class="box-title">监控设备状态</div>
          <div class="box-content">
            <div id="deviceStatusChart" style="height: 100%;"></div>
          </div>
        </div>
        <div class="content-alarm">
          <div class="box-title">报警信息</div>
          <div class="box-content">
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
        </div>
      </div>
      <div class="content-middle flex-column-between">
        <div class="content-map" id="boardMap"></div>
        <div class="content-chart">
          <div class="box-title">今日仓库耗能</div>
          <div class="box-content">
            <el-tabs
              v-model="activeName"
              @tab-click="handleClick"
              class="trend-tabs"
            >
              <el-tab-pane label="综合能耗" name="total">
                <div id="totalEnergyChart" style="height: 100%;"></div>
              </el-tab-pane>
              <el-tab-pane label="电" name="electricity">
                <div id="electricityChart" style="height: 100%;"></div>
              </el-tab-pane>
              <el-tab-pane label="水" name="water">
                <div id="waterChart" style="height: 100%;"></div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </div>
      <div class="content-right flex-column-between">
        <div class="content-statistics">
          <div class="box-title">入库统计</div>
          <div class="box-content flex-between">
            <div class="statistics-item flex-column-center">
              <div class="item-count">58</div>
              <div style="text-align: center">待入库</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">10</div>
              <div style="text-align: center">待质检</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">5</div>
              <div style="text-align: center">待上架</div>
            </div>
          </div>
        </div>
        <div class="content-statistics">
          <div class="box-title">出库统计</div>
          <div class="box-content flex-between">
            <div class="statistics-item flex-column-center">
              <div class="item-count">15</div>
              <div style="text-align: center">待配货</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">10</div>
              <div style="text-align: center">待拣货</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">5</div>
              <div style="text-align: center">待出库</div>
            </div>
          </div>
        </div>
        <div class="content-statistics">
          <div class="box-title">其他</div>
          <div class="box-content flex-between">
            <div class="statistics-item flex-column-center">
              <div class="item-count">15</div>
              <div style="text-align: center">待截单</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">10</div>
              <div style="text-align: center">异常单</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">5</div>
              <div style="text-align: center">今日到货</div>
            </div>
          </div>
        </div>

        <div class="content-carbon">
          <div class="box-title">今日出入库流水</div>
          <div class="box-content">
            <div id="warehouseFlowChart" style="height: 100%;"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import moment from 'moment'

export default {
  name: 'BigScreen',
  data() {
    return {
      nowTime: '',
      timer: null,
      activeName: 'total',
      alarmList: [
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
      ],
      xData: ['00:00', '02:00', '04:00', '06:00', '08:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00'],
      electricityData: [79, 52, 200, 334, 390, 330, 34, 79, 52, 123, 334, 34],
      waterData: [45, 62, 100, 234, 290, 230, 64, 89, 42, 103, 214, 44],
      energyData: [89, 72, 150, 284, 340, 280, 54, 99, 62, 113, 254, 54],
      warehouseFlowData: [
        [79, 52, 200, 334, 390, 330, 34, 79, 52, 123, 334, 34],
        [45, 62, 100, 234, 290, 230, 64, 89, 42, 103, 214, 44]
      ],
      pieData: [
        { value: 40, name: "正常", itemStyle: {color: '#6be6c3'} },
        { value: 38, name: "报警", itemStyle: {color: '#e0c464'} },
        { value: 32, name: "离线", itemStyle: {color: '#297ef8'} }
      ],
      charts: {},
      map: null
    }
  },
  methods: {
    getCurrentTime() {
      this.nowTime = moment().format('YYYY-MM-DD HH:mm:ss')
    },
    getTypeClass(type) {
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
    },
    getTypeText(type) {
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
    },
    handleClick(tab) {
      this.$nextTick(() => {
        this.initEnergyCharts()
      })
    },
    initCharts() {
      this.initDeviceStatusChart()
      this.initEnergyCharts()
      this.initWarehouseFlowChart()
      this.initMap()
      
      // 监听窗口大小变化，重新渲染图表
      window.addEventListener('resize', this.resizeCharts)
    },
    resizeCharts() {
      Object.keys(this.charts).forEach(key => {
        if (this.charts[key]) {
          this.charts[key].resize()
        }
      })
    },
    initDeviceStatusChart() {
      this.charts.deviceStatus = echarts.init(document.getElementById('deviceStatusChart'))
      this.charts.deviceStatus.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          bottom: '0',
          itemWidth: 10,
          itemHeight: 10,
          textStyle: {
            color: '#fff',
            fontSize: 12
          }
        },
        series: [
          {
            name: '设备状态',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 16,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: this.pieData
          }
        ]
      })
    },
    initEnergyCharts() {
      const commonOption = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
          }
        },
        grid: {
          top: '5%',
          left: '2%',
          right: '2%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            boundaryGap: false,
            data: this.xData,
            axisLabel: {
              color: '#fff'
            },
            axisLine: {
              lineStyle: {
                color: '#1be5e7'
              }
            },
            splitLine: {
              lineStyle: {
                color: '#1d3666'
              }
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            axisLabel: {
              color: '#fff'
            },
            splitLine: {
              lineStyle: {
                color: '#1d3666'
              }
            }
          }
        ],
        series: [
          {
            type: 'line',
            stack: 'Total',
            smooth: true,
            lineStyle: {
              width: 0
            },
            showSymbol: false,
            areaStyle: {
              opacity: 0.8,
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(41, 105, 232, 0.8)'
                },
                {
                  offset: 1,
                  color: 'rgba(27, 229, 231, 0.3)'
                }
              ])
            },
            emphasis: {
              focus: 'series'
            }
          }
        ]
      }
      
      if (this.activeName === 'total') {
        if (!this.charts.totalEnergy) {
          this.charts.totalEnergy = echarts.init(document.getElementById('totalEnergyChart'))
        }
        const option = JSON.parse(JSON.stringify(commonOption))
        option.series[0].name = '综合能耗'
        option.series[0].data = this.energyData
        option.yAxis[0].name = 'kgce'
        this.charts.totalEnergy.setOption(option)
      } else if (this.activeName === 'electricity') {
        if (!this.charts.electricity) {
          this.charts.electricity = echarts.init(document.getElementById('electricityChart'))
        }
        const option = JSON.parse(JSON.stringify(commonOption))
        option.series[0].name = '电力消耗'
        option.series[0].data = this.electricityData
        option.yAxis[0].name = 'kW·h'
        this.charts.electricity.setOption(option)
      } else if (this.activeName === 'water') {
        if (!this.charts.water) {
          this.charts.water = echarts.init(document.getElementById('waterChart'))
        }
        const option = JSON.parse(JSON.stringify(commonOption))
        option.series[0].name = '水资源消耗'
        option.series[0].data = this.waterData
        option.yAxis[0].name = 't'
        this.charts.water.setOption(option)
      }
    },
    initWarehouseFlowChart() {
      this.charts.warehouseFlow = echarts.init(document.getElementById('warehouseFlowChart'))
      this.charts.warehouseFlow.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['入库', '出库'],
          textStyle: {
            color: '#fff'
          }
        },
        grid: {
          top: '15%',
          left: '2%',
          right: '2%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [{
          type: 'category',
          data: this.xData,
          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            color: '#fff'
          },
          axisLine: {
            lineStyle: {
              color: '#1be5e7'
            }
          }
        }],
        yAxis: [{
          type: 'value',
          name: '件',
          axisLabel: {
            color: '#fff'
          },
          nameTextStyle: {
            color: '#fff'
          },
          splitLine: {
            lineStyle: {
              color: '#1d3666'
            }
          }
        }],
        series: [
          {
            name: '入库',
            type: 'bar',
            stack: 'total',
            barWidth: '40%',
            data: this.warehouseFlowData[0],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 1, color: '#1c508e' },
                { offset: 0, color: '#1be5e7' }
              ])
            }
          },
          {
            name: '出库',
            type: 'bar',
            stack: 'total',
            barWidth: '40%',
            data: this.warehouseFlowData[1],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 1, color: '#e0c464' },
                { offset: 0, color: '#6be6c3' }
              ])
            }
          }
        ]
      })
    },
    initMap() {
      // 简化地图实现，避免加载百度地图可能导致的问题
      const mapEl = document.getElementById('boardMap');
      if (mapEl) {
        // 添加一个简单的地图背景
        mapEl.innerHTML = `
          <div style="height: 100%; display: flex; align-items: center; justify-content: center; background: rgba(0,30,60,0.8); color: #00d1ff; font-size: 16px; text-align: center; padding: 20px;">
            <div>
              <div style="margin-bottom: 20px;">地图数据区域</div>
              <div>仓库分布: 苏州工业园区、昆山、吴江、常熟</div>
            </div>
          </div>
        `;
      }
    }
  },
  mounted() {
    this.getCurrentTime()
    this.timer = setInterval(() => {
      this.getCurrentTime()
    }, 1000)
    
    this.$nextTick(() => {
      try {
        this.initCharts()
      } catch (e) {
        console.error('初始化图表失败:', e)
      }
    })
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
    
    // 移除窗口大小变化监听
    window.removeEventListener('resize', this.resizeCharts)
    
    // 销毁图表实例
    Object.keys(this.charts).forEach(key => {
      if (this.charts[key]) {
        this.charts[key].dispose()
      }
    })
  }
}
</script>

<style scoped>
.board-container {
  width: 100%;
  height: 100vh;
  background-image: url("../../assets/images/board-bg.png");
  background-size: 100% 100%;
  color: #fff;
  position: relative;
}

.time-stamp {
  position: absolute;
  right: 2.5%;
  top: 5%;
  color: #00d1ff;
  font-size: 20px;
}

.board-title {
  background-image: linear-gradient(to top, #2571e9, #00e7ff);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
  font-size: 40px;
  font-weight: bold;
  letter-spacing: 8px;
  height: 9%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.board-content {
  height: 86%;
  width: 95%;
  margin: 6px auto 0;
}

.flex-between {
  display: flex;
  justify-content: space-between;
}

.flex-column-between {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.flex-column-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.content-left {
  width: calc(25% - 12px);
  margin-right: 12px;
}

.content-middle {
  width: 50%;
  margin-right: 12px;
}

.content-right {
  width: calc(25% - 12px);
}

.content-overview,
.content-status,
.content-alarm,
.content-statistics,
.content-trend,
.content-carbon,
.content-chart,
.content-map {
  background-image: url("../../assets/images/box-bg1.png");
  background-size: 100% 100%;
}

.content-overview {
  height: 20%;
  margin-bottom: 12px;
  padding: 12px 16px;
}

.box-title {
  height: 20px;
  margin-left: 12px;
  display: flex;
  align-items: center;
  color: #01d1ff;
}

.box-title::before {
  content: " ";
  width: 6px;
  height: 100%;
  border-radius: 10px;
  display: inline-block;
  margin-right: 6px;
  background: linear-gradient(to bottom, #00d1ff, #2869e8);
}

.box-content {
  height: calc(100% - 20px);
  overflow: hidden;
  padding: 20px 10px;
}

.overview-object,
.overview-meter,
.overview-alarm {
  width: calc(33% - 10px);
  align-items: center;
  text-align: center;
}

.object-count {
  color: #1be5e7;
  font-size: 35px;
  font-weight: bold;
  margin-bottom: 6px;
}

.object-name {
  font-size: 12px;
}

.content-status {
  height: calc(40% - 12px);
  margin-bottom: 12px;
  padding: 20px 16px;
}

.content-alarm {
  height: calc(40% - 12px);
  padding: 16px 16px;
}

.content-map {
  height: 65%;
  margin-bottom: 12px;
}

.content-chart {
  height: calc(35% - 12px);
  padding: 18px 30px;
}

.content-statistics {
  height: 20%;
  padding: 12px 16px;
  margin-bottom: 12px;
}

.content-carbon {
  height: calc(40% - 12px);
  padding: 18px 16px;
}

.statistics-item {
  width: calc(33% - 8px);
  align-items: center;
  font-size: 12px;
}

.item-count {
  text-align: center;
  font-size: 28px;
  font-weight: bold;
  color: #1be5e7;
  margin: 6px 0 6px;
}

.alarm-list {
  width: 100%;
  height: 100%;
  overflow-y: auto;
}

.alarm-item {
  padding: 8px;
  margin-bottom: 8px;
  border-radius: 4px;
  background: rgba(0, 228, 255, 0.05);
  border: 1px solid rgba(0, 228, 255, 0.1);
  transition: all 0.3s ease;
}

.alarm-item:hover {
  background: rgba(0, 228, 255, 0.1);
  border-color: rgba(0, 228, 255, 0.3);
}

.alarm-time {
  color: #00e4ff;
  font-size: 12px;
  margin-bottom: 4px;
}

.alarm-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.alarm-type {
  padding: 2px 6px;
  border-radius: 2px;
  font-size: 12px;
}

.warning-type {
  background: rgba(255, 215, 0, 0.1);
  color: #ffd700;
  border: 1px solid rgba(255, 215, 0, 0.3);
}

.error-type {
  background: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
  border: 1px solid rgba(255, 77, 79, 0.3);
}

.info-type {
  background: rgba(0, 228, 255, 0.1);
  color: #00e4ff;
  border: 1px solid rgba(0, 228, 255, 0.3);
}

.alarm-message {
  flex: 1;
  font-size: 14px;
}

.trend-tabs {
  height: 100%;
}
</style>

<style>
.trend-tabs,
.trend-tabs .el-tabs__content .el-tab-pane {
  height: 100%;
}

.trend-tabs .el-tabs__item {
  color: #fff;
}

.trend-tabs .el-tabs__item:hover,
.trend-tabs .el-tabs__item.is-active {
  color: #00d0fe;
}

.trend-tabs .el-tabs__active-bar {
  background-color: #00d0fe;
}

.trend-tabs .el-tabs__nav-wrap::after {
  background-color: #1d3666;
}

.trend-tabs .el-tabs__content {
  height: calc(100% - 55px);
}

/* 地图信息窗口样式修改 */
#boardMap .BMap_bubble_pop {
  background-color: rgba(28, 37, 80, 0.8) !important;
  border: 1px solid #186dbf !important;
}

#boardMap .BMap_bubble_pop img {
  display: none;
}

#boardMap .BMap_bubble_pop .BMap_bubble_top .BMap_bubble_title,
#boardMap .BMap_bubble_pop .BMap_bubble_center .BMap_bubble_content {
  color: #fff !important;
}
</style>