<template>
  <div class="board-container">
    <div class="board-title"><span>织里镇童装智慧物流平台 - 数据总览</span></div>
    <div class="time-stamp">{{ nowTime }}</div>
    
    <div class="board-content flex-between">
      <div class="content-left flex-column-between">
        <div class="content-overview">
          <div class="box-title">童装仓库总览</div>
          <div class="box-content flex-between">
            <div class="overview-object flex-column-center">
              <div class="object-count">12</div>
              <div class="object-name">仓库总数</div>
            </div>
            <div class="overview-meter flex-column-center">
              <div class="object-count">3560</div>
              <div class="object-name">库位总数</div>
            </div>
            <div class="overview-alarm flex-column-center">
              <div class="object-count">8</div>
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
        <div class="content-map">
          <div class="box-content" style="height: 100%;">
            <video controls autoplay loop class="video-player">
              <source src="@/assets/videos/test.mp4" type="video/mp4">
              您的浏览器不支持视频播放
            </video>
          </div>
        </div>
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
          <div class="box-title">童装入库统计</div>
          <div class="box-content flex-between">
            <div class="statistics-item flex-column-center">
              <div class="item-count">235</div>
              <div style="text-align: center">待入库</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">86</div>
              <div style="text-align: center">待质检</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">42</div>
              <div style="text-align: center">待上架</div>
            </div>
          </div>
        </div>
        <div class="content-statistics">
          <div class="box-title">童装出库统计</div>
          <div class="box-content flex-between">
            <div class="statistics-item flex-column-center">
              <div class="item-count">178</div>
              <div style="text-align: center">待配货</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">93</div>
              <div style="text-align: center">待拣货</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">57</div>
              <div style="text-align: center">待出库</div>
            </div>
          </div>
        </div>
        <div class="content-statistics">
          <div class="box-title">童装订单</div>
          <div class="box-content flex-between">
            <div class="statistics-item flex-column-center">
              <div class="item-count">312</div>
              <div style="text-align: center">待生产</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">145</div>
              <div style="text-align: center">生产中</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">86</div>
              <div style="text-align: center">今日出货</div>
            </div>
          </div>
        </div>

        <div class="content-carbon">
          <div class="box-title">今日童装出入库流水</div>
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
  name: 'DataOverview',
  data() {
    return {
      nowTime: '',
      timer: null,
      activeName: 'total',
      alarmList: [
        {
          time: '09:15:23',
          type: 'warning',
          message: '童装三厂A区温度过高，请检查'
        },
        {
          time: '08:45:11',
          type: 'error',
          message: '织里童装批发市场B区湿度异常，需要立即处理'
        },
        {
          time: '08:32:45',
          type: 'info',
          message: '系统检测到春季童装库存即将清仓，建议补充夏装'
        },
        {
          time: '07:55:37',
          type: 'warning',
          message: '织里童装产业园C区监控设备离线'
        },
        {
          time: '07:20:09',
          type: 'error',
          message: '童装面料仓发现异常，请及时处理'
        }
      ],
      xData: ['00:00', '02:00', '04:00', '06:00', '08:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00'],
      electricityData: [125, 92, 280, 454, 610, 580, 345, 392, 468, 545, 465, 352],
      waterData: [85, 102, 180, 354, 410, 360, 184, 199, 252, 323, 334, 264],
      energyData: [149, 132, 270, 404, 560, 510, 374, 309, 382, 433, 474, 374],
      warehouseFlowData: [
        [189, 262, 330, 464, 520, 460, 344, 289, 362, 443, 454, 354],
        [155, 172, 220, 354, 410, 360, 274, 299, 352, 323, 334, 264]
      ],
      pieData: [
        { value: 68, name: "正常", itemStyle: {color: '#6be6c3'} },
        { value: 24, name: "报警", itemStyle: {color: '#e0c464'} },
        { value: 18, name: "离线", itemStyle: {color: '#297ef8'} }
      ],
      charts: {}
    }
  },
  methods: {
    getCurrentTime() {
      this.nowTime = moment().format('YYYY-MM-DD HH:mm:ss')
    },
    handleClick() {
      this.$nextTick(() => {
        this.initEnergyCharts()
      })
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
    initCharts() {
      this.initDeviceStatusChart()
      this.initEnergyCharts()
      this.initWarehouseFlowChart()
      
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
          appendToBody: true,
          backgroundColor: 'rgba(0, 11, 42, 0.8)',
          borderColor: '#00e4ff',
          textStyle: {
            color: '#fff'
          }
        },
        legend: {
          top: "bottom",
          textStyle: {
            color: '#fff'
          },
          itemStyle: {
            borderColor: '#00e4ff'
          }
        },
        series: [
          {
            name: "设备状态",
            type: "pie",
            radius: [0, '65%'],
            center: ["50%", "40%"],
            roseType: "area",
            label: {
              show: true,
              position: "outside",
              color: '#00e4ff',
              formatter: '{b}({d}%)'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 16,
                color: '#00e4ff'
              }
            },
            data: [
              { value: 40, name: "正常", itemStyle: {color: '#00e4ff'} },
              { value: 38, name: "报警", itemStyle: {color: '#ffd700'} },
              { value: 32, name: "离线", itemStyle: {color: '#0066ff'} }
            ],
            animationDuration: 1500
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
              backgroundColor: '#00e4ff'
            }
          },
          backgroundColor: 'rgba(0, 11, 42, 0.9)',
          borderColor: '#00e4ff',
          textStyle: {
            color: '#fff'
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
                color: '#00e4ff'
              }
            },
            splitLine: {
              lineStyle: {
                color: 'rgba(0, 228, 255, 0.1)'
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
                color: 'rgba(0, 228, 255, 0.1)'
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
              width: 2,
              color: '#00e4ff'
            },
            showSymbol: false,
            areaStyle: {
              opacity: 0.8,
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(0, 228, 255, 0.5)'
                },
                {
                  offset: 1,
                  color: 'rgba(0, 11, 42, 0.1)'
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
          },
          backgroundColor: 'rgba(0, 11, 42, 0.9)',
          borderColor: '#00e4ff',
          textStyle: {
            color: '#fff'
          }
        },
        legend: {
          data: ['入库', '出库'],
          textStyle: {
            color: '#fff'
          },
          itemStyle: {
            borderColor: '#00e4ff'
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
              color: '#00e4ff'
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(0, 228, 255, 0.1)'
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
              color: 'rgba(0, 228, 255, 0.1)'
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
                { offset: 0, color: '#00e4ff' },
                { offset: 1, color: 'rgba(0, 228, 255, 0.1)' }
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
                { offset: 0, color: '#0066ff' },
                { offset: 1, color: 'rgba(0, 102, 255, 0.1)' }
              ])
            }
          }
        ]
      })
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
  background-color: #000b2a;
  color: #fff;
  padding: 20px;
  box-sizing: border-box;
  overflow: hidden;
  position: relative;
  background-image: linear-gradient(to bottom, #000b2a, #001959);
}

.board-title {
  text-align: center;
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #00e4ff;
  text-shadow: 0 0 10px rgba(0, 228, 255, 0.5);
  position: relative;
}

.board-title::before,
.board-title::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 100px;
  height: 2px;
  background: linear-gradient(to right, transparent, #00e4ff, transparent);
}

.board-title::before {
  left: 25%;
}

.board-title::after {
  right: 25%;
}

.time-stamp {
  position: absolute;
  right: 20px;
  top: 20px;
  color: #00e4ff;
  font-size: 16px;
  background: rgba(0, 228, 255, 0.1);
  padding: 5px 15px;
  border-radius: 4px;
  border: 1px solid rgba(0, 228, 255, 0.3);
}

.board-content {
  display: flex;
  height: calc(100% - 60px);
  gap: 20px;
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

.content-left, .content-right {
  width: 25%;
  height: 100%;
}

.content-middle {
  width: 50%;
  height: 100%;
}

.box-title {
  font-size: 16px;
  font-weight: bold;
  padding: 10px;
  border-bottom: 1px solid rgba(0, 228, 255, 0.3);
  color: #00e4ff;
  display: flex;
  align-items: center;
  position: relative;
}

.box-title::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 16px;
  background: #00e4ff;
  margin-right: 8px;
  border-radius: 2px;
  box-shadow: 0 0 8px rgba(0, 228, 255, 0.5);
}

.box-title::after {
  content: '';
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  width: 30px;
  height: 4px;
  background: linear-gradient(to right, #00e4ff, transparent);
}

.box-content {
  padding: 10px;
  height: calc(100% - 37px);
}

.content-overview, .content-status, .content-alarm,
.content-statistics, .content-carbon {
  background: rgba(0, 11, 42, 0.6);
  border: 1px solid rgba(0, 228, 255, 0.3);
  border-radius: 4px;
  margin-bottom: 20px;
  height: 30%;
  position: relative;
  backdrop-filter: blur(10px);
}

.content-overview::before,
.content-status::before,
.content-alarm::before,
.content-statistics::before,
.content-carbon::before,
.content-map::before,
.content-chart::before {
  content: '';
  position: absolute;
  width: 10px;
  height: 10px;
  border-top: 2px solid #00e4ff;
  border-left: 2px solid #00e4ff;
  top: -2px;
  left: -2px;
}

.content-overview::after,
.content-status::after,
.content-alarm::after,
.content-statistics::after,
.content-carbon::after,
.content-map::after,
.content-chart::after {
  content: '';
  position: absolute;
  width: 10px;
  height: 10px;
  border-bottom: 2px solid #00e4ff;
  border-right: 2px solid #00e4ff;
  bottom: -2px;
  right: -2px;
}

.object-count {
  font-size: 28px;
  font-weight: bold;
  color: #00e4ff;
  text-shadow: 0 0 10px rgba(0, 228, 255, 0.5);
}

.object-name {
  font-size: 14px;
  margin-top: 5px;
}

.statistics-item {
  width: 30%;
  height: 100%;
  position: relative;
  padding: 10px;
  border-radius: 4px;
  background: rgba(0, 228, 255, 0.05);
  transition: all 0.3s ease;
}

.statistics-item:hover {
  background: rgba(0, 228, 255, 0.1);
  transform: translateY(-2px);
}

.item-count {
  font-size: 24px;
  font-weight: bold;
  color: #00e4ff;
  text-shadow: 0 0 10px rgba(0, 228, 255, 0.5);
  margin-bottom: 5px;
}

.trend-tabs :deep(.el-tabs__header) {
  margin-bottom: 10px;
}

.trend-tabs :deep(.el-tabs__nav-wrap::after) {
  background-color: rgba(27, 229, 231, 0.3);
}

.trend-tabs :deep(.el-tabs__item) {
  color: #fff;
}

.trend-tabs :deep(.el-tabs__item.is-active) {
  color: #1be5e7;
}

.trend-tabs :deep(.el-tabs__active-bar) {
  background-color: #1be5e7;
}

/* 报警信息样式 */
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

.content-map {
  background: rgba(0, 11, 42, 0.6);
  border: 1px solid rgba(0, 228, 255, 0.3);
  border-radius: 4px;
  height: 60%;
  position: relative;
  backdrop-filter: blur(10px);
}

.content-chart {
  background: rgba(0, 11, 42, 0.6);
  border: 1px solid rgba(0, 228, 255, 0.3);
  border-radius: 4px;
  height: 38%;
  position: relative;
  backdrop-filter: blur(10px);
}

.video-player {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 2px;
}

.board-container::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(to right,
    transparent,
    rgba(0, 228, 255, 0.5) 20%,
    rgba(0, 228, 255, 0.8) 50%,
    rgba(0, 228, 255, 0.5) 80%,
    transparent
  );
  animation: scanline 4s linear infinite;
}

@keyframes scanline {
  0% {
    transform: translateY(-100%);
  }
  100% {
    transform: translateY(100vh);
  }
}
</style>