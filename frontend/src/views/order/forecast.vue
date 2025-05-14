<template>
  <div class="app-container">
    <el-card class="filter-container">
      <div slot="header">
        <span>销量预测</span>
      </div>
      <div class="filter-item-container">
        <div class="filter-item">
          <span class="label">产品类别：</span>
          <el-select v-model="filterForm.category" placeholder="请选择" style="width: 180px">
            <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
        <div class="filter-item">
          <span class="label">预测周期：</span>
          <el-select v-model="filterForm.period" placeholder="请选择" style="width: 180px">
            <el-option v-for="item in periodOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
        <div class="filter-item">
          <span class="label">预测算法：</span>
          <el-select v-model="filterForm.algorithm" placeholder="请选择" style="width: 180px">
            <el-option v-for="item in algorithmOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
        <el-button type="primary" icon="el-icon-refresh" @click="handleForecast">生成预测</el-button>
      </div>
    </el-card>

    <div class="chart-container">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-card shadow="hover">
            <div slot="header">
              <span>销量预测图表</span>
              <el-radio-group v-model="chartType" size="mini" style="float: right;">
                <el-radio-button label="line">折线图</el-radio-button>
                <el-radio-button label="bar">柱状图</el-radio-button>
              </el-radio-group>
            </div>
            <div ref="forecastChart" style="height: 400px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header">
              <span>产品预测排行</span>
            </div>
            <el-table :data="topProductsList" border style="width: 100%">
              <el-table-column prop="rank" label="排名" width="80" align="center" />
              <el-table-column prop="productName" label="产品名称" align="center" />
              <el-table-column prop="forecast" label="预测销量" align="center" />
              <el-table-column prop="growth" label="增长率" align="center">
                <template slot-scope="scope">
                  <span :style="{ color: scope.row.growth >= 0 ? '#67C23A' : '#F56C6C' }">
                    {{ scope.row.growth >= 0 ? '+' : '' }}{{ scope.row.growth }}%
                  </span>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header">
              <span>预测准确率分析</span>
            </div>
            <div ref="accuracyChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'OrderForecast',
  data() {
    return {
      filterForm: {
        category: 'all',
        period: 'month',
        algorithm: 'arima'
      },
      categoryOptions: [
        { label: '全部产品', value: 'all' },
        { label: '电子产品', value: 'electronics' },
        { label: '家用电器', value: 'appliances' },
        { label: '办公用品', value: 'office' }
      ],
      periodOptions: [
        { label: '未来一周', value: 'week' },
        { label: '未来一个月', value: 'month' },
        { label: '未来一个季度', value: 'quarter' },
        { label: '未来一年', value: 'year' }
      ],
      algorithmOptions: [
        { label: 'ARIMA模型', value: 'arima' },
        { label: '指数平滑法', value: 'exponential' },
        { label: '季节性分解', value: 'seasonal' },
        { label: '神经网络', value: 'neural' }
      ],
      chartType: 'line',
      topProductsList: [
        { rank: 1, productName: '智能手机A', forecast: 1250, growth: 15.3 },
        { rank: 2, productName: '笔记本电脑B', forecast: 860, growth: 8.2 },
        { rank: 3, productName: '平板电脑C', forecast: 720, growth: 5.7 },
        { rank: 4, productName: '智能手表D', forecast: 580, growth: 12.1 },
        { rank: 5, productName: '智能音箱E', forecast: 450, growth: 9.5 },
        { rank: 6, productName: '耳机F', forecast: 390, growth: -2.8 },
        { rank: 7, productName: '显示器G', forecast: 320, growth: 1.5 },
        { rank: 8, productName: '键盘H', forecast: 280, growth: -1.2 }
      ],
      charts: {
        forecastChart: null,
        accuracyChart: null
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
      // 初始化销量预测图表
      this.charts.forecastChart = echarts.init(this.$refs.forecastChart)
      this.updateForecastChart()

      // 初始化准确率分析图表
      this.charts.accuracyChart = echarts.init(this.$refs.accuracyChart)
      this.charts.accuracyChart.setOption({
        title: {
          text: '历史预测准确率',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: ['非常准确', '较为准确', '一般准确', '较不准确']
        },
        series: [
          {
            name: '预测准确率',
            type: 'pie',
            radius: '60%',
            center: ['50%', '50%'],
            data: [
              { value: 42, name: '非常准确' },
              { value: 35, name: '较为准确' },
              { value: 18, name: '一般准确' },
              { value: 5, name: '较不准确' }
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
    updateForecastChart() {
      const dates = []
      const historicalData = []
      const forecastData = []
      
      // 根据选择的周期生成日期数据
      const now = new Date()
      let dateFormat = 'MM-dd'
      let days = 7
      
      if (this.filterForm.period === 'week') {
        days = 7
        dateFormat = 'MM-dd'
      } else if (this.filterForm.period === 'month') {
        days = 30
        dateFormat = 'MM-dd'
      } else if (this.filterForm.period === 'quarter') {
        days = 90
        dateFormat = 'MM-dd'
      } else if (this.filterForm.period === 'year') {
        days = 12
        dateFormat = 'yyyy-MM'
      }
      
      // 生成历史数据
      for (let i = 30; i > 0; i--) {
        const date = new Date(now.getTime() - i * 24 * 60 * 60 * 1000)
        historicalData.push(Math.floor(Math.random() * 500 + 500))
        if (i <= days) {
          dates.push(this.formatDate(date, dateFormat))
        }
      }
      
      // 生成预测数据
      for (let i = 1; i <= days; i++) {
        const date = new Date(now.getTime() + i * 24 * 60 * 60 * 1000)
        dates.push(this.formatDate(date, dateFormat))
        forecastData.push(Math.floor(Math.random() * 200 + 600))
      }
      
      // 设置图表选项
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['历史销量', '预测销量']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '历史销量',
            type: this.chartType,
            data: historicalData.slice(-days),
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '预测销量',
            type: this.chartType,
            data: forecastData,
            itemStyle: {
              color: '#67C23A'
            }
          }
        ]
      }
      
      this.charts.forecastChart.setOption(option)
    },
    formatDate(date, format) {
      const year = date.getFullYear()
      const month = date.getMonth() + 1
      const day = date.getDate()
      
      if (format === 'yyyy-MM') {
        return `${year}-${month < 10 ? '0' + month : month}`
      }
      
      return `${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day}`
    },
    handleForecast() {
      this.updateForecastChart()
      this.$message.success('预测数据已更新')
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
    chartType() {
      this.updateForecastChart()
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