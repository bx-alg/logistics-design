<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in statCards" :key="item.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon">
            <i :class="item.icon"></i>
          </div>
          <div class="stat-content">
            <div class="stat-title">{{ item.title }}</div>
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-desc">{{ item.desc }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span>销售趋势</span>
            <el-radio-group v-model="chartTimeRange" size="mini" style="float: right;">
              <el-radio-button label="week">周</el-radio-button>
              <el-radio-button label="month">月</el-radio-button>
              <el-radio-button label="quarter">季</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="salesChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span>热销商品排名</span>
            <el-select v-model="topProductsCount" size="mini" style="float: right; width: 120px;">
              <el-option label="Top 5" :value="5"></el-option>
              <el-option label="Top 10" :value="10"></el-option>
              <el-option label="Top 15" :value="15"></el-option>
            </el-select>
          </div>
          <div ref="topProductsChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span>库存状态</span>
          </div>
          <div ref="inventoryChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span>供应商评级分布</span>
          </div>
          <div ref="supplierChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  data() {
    return {
      chartTimeRange: 'month',
      topProductsCount: 10,
      statCards: [
        {
          title: '今日订单',
          value: '156',
          desc: '同比增长 24%',
          icon: 'el-icon-s-order'
        },
        {
          title: '销售额',
          value: '¥ 56,982',
          desc: '周环比增长 12%',
          icon: 'el-icon-s-finance'
        },
        {
          title: '运输中',
          value: '39',
          desc: '路线优化节省 8%',
          icon: 'el-icon-truck'
        },
        {
          title: '库存预警',
          value: '7',
          desc: '较昨日减少 3 个',
          icon: 'el-icon-warning'
        }
      ],
      charts: {
        salesChart: null,
        topProductsChart: null,
        inventoryChart: null,
        supplierChart: null
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
    initCharts() {
      // 初始化销售趋势图
      this.charts.salesChart = echarts.init(this.$refs.salesChart)
      this.charts.salesChart.setOption({
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['订单数量', '销售金额']
        },
        xAxis: {
          type: 'category',
          data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月']
        },
        yAxis: [
          {
            type: 'value',
            name: '订单数量',
            position: 'left'
          },
          {
            type: 'value',
            name: '金额(万元)',
            position: 'right'
          }
        ],
        series: [
          {
            name: '订单数量',
            type: 'bar',
            data: [320, 302, 341, 374, 390, 450, 420]
          },
          {
            name: '销售金额',
            type: 'line',
            yAxisIndex: 1,
            data: [82, 93, 90, 93, 129, 133, 132]
          }
        ]
      })

      // 初始化热销商品图
      this.charts.topProductsChart = echarts.init(this.$refs.topProductsChart)
      this.charts.topProductsChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'value'
        },
        yAxis: {
          type: 'category',
          data: ['商品A', '商品B', '商品C', '商品D', '商品E', '商品F', '商品G', '商品H', '商品I', '商品J']
        },
        series: [
          {
            name: '销售量',
            type: 'bar',
            data: [820, 765, 740, 690, 610, 580, 520, 480, 410, 390]
          }
        ]
      })

      // 初始化库存状态图
      this.charts.inventoryChart = echarts.init(this.$refs.inventoryChart)
      this.charts.inventoryChart.setOption({
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '库存状态',
            type: 'pie',
            radius: '60%',
            data: [
              { value: 60, name: '充足' },
              { value: 25, name: '正常' },
              { value: 10, name: '偏低' },
              { value: 5, name: '紧缺' }
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

      // 初始化供应商评级图
      this.charts.supplierChart = echarts.init(this.$refs.supplierChart)
      this.charts.supplierChart.setOption({
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '供应商评级',
            type: 'pie',
            radius: '60%',
            data: [
              { value: 15, name: 'A级' },
              { value: 25, name: 'B级' },
              { value: 35, name: 'C级' },
              { value: 20, name: 'D级' },
              { value: 5, name: 'E级' }
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
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  display: flex;
  height: 100px;
}

.stat-icon {
  width: 80px;
  height: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 8px;
  background-color: #ecf5ff;
  margin-right: 15px;
}

.stat-icon i {
  font-size: 36px;
  color: #409EFF;
}

.stat-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-desc {
  font-size: 12px;
  color: #67c23a;
}
</style> 