<template>
  <div class="app-container">
    <el-card class="filter-container">
      <div slot="header">
        <span>库存分析</span>
      </div>
      <div class="filter-item-container">
        <div class="filter-item">
          <span class="label">仓库：</span>
          <el-select v-model="filterForm.warehouseId" placeholder="请选择" style="width: 180px">
            <el-option v-for="item in warehouseOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
        <div class="filter-item">
          <span class="label">商品类别：</span>
          <el-select v-model="filterForm.category" placeholder="请选择" style="width: 180px">
            <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
        <div class="filter-item">
          <span class="label">时间范围：</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            align="right"
            unlink-panels
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 280px"
          />
        </div>
        <el-button type="primary" icon="el-icon-refresh" @click="refreshData">刷新数据</el-button>
      </div>
    </el-card>

    <div class="chart-container">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header">
              <span>库存周转率分析</span>
            </div>
            <div ref="turnoverRateChart" style="height: 350px;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header">
              <span>库存结构分析</span>
            </div>
            <div ref="inventoryStructureChart" style="height: 350px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="24">
          <el-card shadow="hover">
            <div slot="header">
              <span>库存变动趋势</span>
              <el-radio-group v-model="trendTimeUnit" size="mini" style="float: right;">
                <el-radio-button label="day">日</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
                <el-radio-button label="quarter">季</el-radio-button>
              </el-radio-group>
            </div>
            <div ref="inventoryTrendChart" style="height: 350px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header">
              <span>库存状态分布</span>
            </div>
            <div ref="inventoryStatusChart" style="height: 350px;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header">
              <span>滞销商品排行</span>
            </div>
            <el-table :data="slowMovingList" border style="width: 100%">
              <el-table-column prop="rank" label="排名" width="80" align="center" />
              <el-table-column prop="productName" label="商品名称" align="center" />
              <el-table-column prop="days" label="滞销天数" align="center" />
              <el-table-column prop="quantity" label="库存数量" align="center" />
              <el-table-column prop="value" label="库存金额" align="center">
                <template slot-scope="scope">
                  ¥ {{ scope.row.value }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'InventoryAnalysis',
  data() {
    return {
      filterForm: {
        warehouseId: 'all',
        category: 'all'
      },
      dateRange: [
        this.getDateBefore(30),
        this.getCurrentDate()
      ],
      trendTimeUnit: 'day',
      warehouseOptions: [
        { label: '全部仓库', value: 'all' },
        { label: '上海仓', value: 'shanghai' },
        { label: '北京仓', value: 'beijing' },
        { label: '广州仓', value: 'guangzhou' },
        { label: '深圳仓', value: 'shenzhen' }
      ],
      categoryOptions: [
        { label: '全部分类', value: 'all' },
        { label: '电子产品', value: 'electronics' },
        { label: '家用电器', value: 'appliances' },
        { label: '办公用品', value: 'office' }
      ],
      slowMovingList: [
        { rank: 1, productName: '智能手环J', days: 120, quantity: 158, value: 15800 },
        { rank: 2, productName: '音响系统K', days: 95, quantity: 65, value: 32500 },
        { rank: 3, productName: '智能门锁L', days: 85, quantity: 42, value: 12600 },
        { rank: 4, productName: '加湿器M', days: 80, quantity: 75, value: 9750 },
        { rank: 5, productName: '空气净化器N', days: 76, quantity: 38, value: 15200 },
        { rank: 6, productName: '电动剃须刀O', days: 72, quantity: 126, value: 12600 },
        { rank: 7, productName: '电热水壶P', days: 68, quantity: 96, value: 8640 }
      ],
      charts: {
        turnoverRateChart: null,
        inventoryStructureChart: null,
        inventoryTrendChart: null,
        inventoryStatusChart: null
      }
    }
  },
  mounted() {
    this.initCharts()
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    this.destroyCharts()
  },
  methods: {
    getCurrentDate() {
      const now = new Date()
      const year = now.getFullYear()
      const month = (now.getMonth() + 1).toString().padStart(2, '0')
      const day = now.getDate().toString().padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    getDateBefore(days) {
      const date = new Date()
      date.setDate(date.getDate() - days)
      const year = date.getFullYear()
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    initCharts() {
      // 初始化库存周转率图表
      this.charts.turnoverRateChart = echarts.init(this.$refs.turnoverRateChart)
      this.charts.turnoverRateChart.setOption({
        title: {
          text: '近12个月库存周转率',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['6月', '7月', '8月', '9月', '10月', '11月', '12月', '1月', '2月', '3月', '4月', '5月']
        },
        yAxis: {
          type: 'value',
          name: '周转率（次/月）'
        },
        series: [
          {
            name: '周转率',
            type: 'bar',
            data: [3.2, 3.5, 3.8, 4.1, 4.0, 3.9, 3.6, 3.5, 3.3, 3.8, 4.0, 4.2],
            itemStyle: {
              color: '#409EFF'
            },
            emphasis: {
              itemStyle: {
                color: '#66b1ff'
              }
            }
          }
        ]
      })

      // 初始化库存结构图表
      this.charts.inventoryStructureChart = echarts.init(this.$refs.inventoryStructureChart)
      this.charts.inventoryStructureChart.setOption({
        title: {
          text: '库存结构分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 10,
          data: ['电子产品', '家用电器', '办公用品', '手机配件', '其他']
        },
        series: [
          {
            name: '库存占比',
            type: 'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: 42, name: '电子产品' },
              { value: 28, name: '家用电器' },
              { value: 15, name: '办公用品' },
              { value: 10, name: '手机配件' },
              { value: 5, name: '其他' }
            ]
          }
        ]
      })

      // 初始化库存变动趋势图表
      this.updateTrendChart()

      // 初始化库存状态图表
      this.charts.inventoryStatusChart = echarts.init(this.$refs.inventoryStatusChart)
      this.charts.inventoryStatusChart.setOption({
        title: {
          text: '库存状态分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 10,
          data: ['库存充足', '库存正常', '库存偏低', '库存紧缺', '库存超量']
        },
        series: [
          {
            name: '库存状态',
            type: 'pie',
            radius: '55%',
            center: ['50%', '60%'],
            data: [
              { value: 35, name: '库存充足', itemStyle: { color: '#67C23A' } },
              { value: 40, name: '库存正常', itemStyle: { color: '#409EFF' } },
              { value: 15, name: '库存偏低', itemStyle: { color: '#E6A23C' } },
              { value: 5, name: '库存紧缺', itemStyle: { color: '#F56C6C' } },
              { value: 5, name: '库存超量', itemStyle: { color: '#909399' } }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      })
    },
    updateTrendChart() {
      if (!this.$refs.inventoryTrendChart) return

      if (!this.charts.inventoryTrendChart) {
        this.charts.inventoryTrendChart = echarts.init(this.$refs.inventoryTrendChart)
      }

      let xAxisData = []
      let inboundData = []
      let outboundData = []
      let stockData = []

      // 根据时间单位生成不同的数据
      if (this.trendTimeUnit === 'day') {
        for (let i = 1; i <= 30; i++) {
          xAxisData.push(`${i}日`)
          inboundData.push(Math.floor(Math.random() * 100 + 50))
          outboundData.push(Math.floor(Math.random() * 80 + 40))
          stockData.push(5000 + Math.floor(Math.random() * 1000 - 500))
        }
      } else if (this.trendTimeUnit === 'month') {
        xAxisData = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
        inboundData = [1250, 1420, 1580, 1320, 1450, 1620, 1380, 1520, 1680, 1420, 1520, 1650]
        outboundData = [1150, 1320, 1480, 1350, 1380, 1550, 1420, 1480, 1620, 1380, 1480, 1580]
        stockData = [5200, 5300, 5400, 5370, 5440, 5510, 5470, 5510, 5570, 5610, 5650, 5720]
      } else if (this.trendTimeUnit === 'quarter') {
        xAxisData = ['Q1', 'Q2', 'Q3', 'Q4']
        inboundData = [4250, 4390, 4580, 4790]
        outboundData = [3950, 4250, 4480, 4650]
        stockData = [5300, 5440, 5540, 5680]
      }

      this.charts.inventoryTrendChart.setOption({
        title: {
          text: '库存变动趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          }
        },
        toolbox: {
          feature: {
            dataView: { show: true, readOnly: false },
            magicType: { show: true, type: ['line', 'bar'] },
            restore: { show: true },
            saveAsImage: { show: true }
          }
        },
        legend: {
          data: ['入库量', '出库量', '库存量'],
          bottom: 10
        },
        xAxis: [
          {
            type: 'category',
            data: xAxisData,
            axisPointer: {
              type: 'shadow'
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            name: '入/出库量',
            min: 0,
            axisLabel: {
              formatter: '{value}'
            }
          },
          {
            type: 'value',
            name: '库存量',
            min: 0,
            axisLabel: {
              formatter: '{value}'
            }
          }
        ],
        series: [
          {
            name: '入库量',
            type: 'bar',
            data: inboundData,
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '出库量',
            type: 'bar',
            data: outboundData,
            itemStyle: {
              color: '#67C23A'
            }
          },
          {
            name: '库存量',
            type: 'line',
            yAxisIndex: 1,
            data: stockData,
            itemStyle: {
              color: '#E6A23C'
            }
          }
        ]
      })
    },
    refreshData() {
      this.$message.success('数据已更新')
      this.updateTrendChart()
    },
    resizeCharts() {
      for (const key in this.charts) {
        if (this.charts[key]) {
          this.charts[key].resize()
        }
      }
    },
    destroyCharts() {
      for (const key in this.charts) {
        if (this.charts[key]) {
          this.charts[key].dispose()
          this.charts[key] = null
        }
      }
    }
  },
  watch: {
    trendTimeUnit() {
      this.updateTrendChart()
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
  display: flex;
  align-items: center;
  margin-right: 30px;
  margin-bottom: 10px;
}
.label {
  margin-right: 10px;
}
.chart-container {
  margin-top: 20px;
}
</style> 