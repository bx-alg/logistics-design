<template>
  <div class="app-container">
    <el-card class="filter-container">
      <div slot="header">
        <span>销量预测</span>
        <el-button style="float: right; margin-left: 10px;" type="success" icon="el-icon-download" @click="exportData">导出预测数据</el-button>
      </div>
      <div class="filter-item-container">
        <div class="filter-item">
          <span class="label">产品类别：</span>
          <el-cascader
            v-model="filterForm.category"
            :options="categoryOptions"
            :props="{ checkStrictly: true }"
            clearable
            placeholder="请选择产品类别"
            style="width: 220px"
          />
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
        <div class="filter-item">
          <span class="label">置信区间：</span>
          <el-select v-model="filterForm.confidence" placeholder="请选择" style="width: 140px">
            <el-option v-for="item in confidenceOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
        <div class="filter-item">
          <span class="label">历史数据：</span>
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :picker-options="pickerOptions"
            style="width: 320px"
          />
        </div>
      </div>
      <div class="filter-item-container" style="margin-top: 10px;">
        <el-button type="primary" icon="el-icon-refresh" @click="handleForecast">生成预测</el-button>
        <el-button type="info" icon="el-icon-setting" @click="showAdvancedSettings = true">高级设置</el-button>
      </div>
    </el-card>

    <div class="chart-container" v-loading="loading">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-card shadow="hover">
            <div slot="header">
              <span>销量预测图表</span>
              <div style="float: right;">
                <el-radio-group v-model="chartType" size="mini" style="margin-right: 15px;">
                  <el-radio-button label="line">折线图</el-radio-button>
                  <el-radio-button label="bar">柱状图</el-radio-button>
                </el-radio-group>
                <el-switch
                  v-model="showConfidenceInterval"
                  active-text="显示置信区间"
                  inactive-text=""
                />
              </div>
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
              <el-select v-model="rankingType" size="mini" style="float: right; width: 120px">
                <el-option label="销量排名" value="volume" />
                <el-option label="增长率排名" value="growth" />
              </el-select>
            </div>
            <el-table :data="topProductsList" border style="width: 100%">
              <el-table-column prop="rank" label="排名" width="80" align="center" />
              <el-table-column prop="productName" label="产品名称" align="center" />
              <el-table-column prop="forecast" label="预测销量" align="center">
                <template slot-scope="scope">
                  {{ scope.row.forecast.toLocaleString() }}
                </template>
              </el-table-column>
              <el-table-column prop="growth" label="增长率" align="center">
                <template slot-scope="scope">
                  <span :class="['growth-rate', scope.row.growth >= 0 ? 'positive' : 'negative']">
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
              <span>预测分析报告</span>
            </div>
            <div class="analysis-container">
              <div class="analysis-item">
                <div class="analysis-label">预测准确率</div>
                <el-progress type="circle" :percentage="forecastAccuracy" :color="accuracyColors"></el-progress>
              </div>
              <div class="analysis-stats">
                <div class="stat-item">
                  <div class="stat-label">平均误差率</div>
                  <div class="stat-value">{{ averageError }}%</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">预测趋势</div>
                  <div class="stat-value" :class="trend.type">{{ trend.text }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">季节性指数</div>
                  <div class="stat-value">{{ seasonalityIndex }}</div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 高级设置对话框 -->
    <el-dialog title="预测高级设置" :visible.sync="showAdvancedSettings" width="600px">
      <el-form :model="advancedSettings" label-width="140px">
        <el-form-item label="季节性周期">
          <el-input-number v-model="advancedSettings.seasonality" :min="1" :max="52"></el-input-number>
        </el-form-item>
        <el-form-item label="趋势分解方法">
          <el-select v-model="advancedSettings.decomposition" style="width: 100%">
            <el-option label="加法模型" value="additive" />
            <el-option label="乘法模型" value="multiplicative" />
          </el-select>
        </el-form-item>
        <el-form-item label="异常值处理">
          <el-select v-model="advancedSettings.outlierMethod" style="width: 100%">
            <el-option label="移动平均" value="moving_average" />
            <el-option label="中位数替换" value="median" />
            <el-option label="保持原值" value="keep" />
          </el-select>
        </el-form-item>
        <el-form-item label="预测步长">
          <el-input-number v-model="advancedSettings.forecastSteps" :min="1" :max="365"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="showAdvancedSettings = false">取消</el-button>
        <el-button type="primary" @click="handleAdvancedSettingsSave">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import FileSaver from 'file-saver'
import * as XLSX from 'xlsx'

export default {
  name: 'OrderForecast',
  data() {
    return {
      loading: false,
      filterForm: {
        category: [],
        period: 'month',
        algorithm: 'arima',
        confidence: 95,
        dateRange: []
      },
      categoryOptions: [
        {
          value: 'clothing',
          label: '童装服饰',
          children: [
            { value: 'tops', label: '上衣' },
            { value: 'pants', label: '裤子' },
            { value: 'dresses', label: '裙装' },
            { value: 'suits', label: '套装' },
            { value: 'baby', label: '婴儿装' }
          ]
        },
        {
          value: 'materials',
          label: '面辅料',
          children: [
            { value: 'fabric', label: '面料' },
            { value: 'accessories', label: '辅料' }
          ]
        }
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
      confidenceOptions: [
        { label: '90%', value: 90 },
        { label: '95%', value: 95 },
        { label: '99%', value: 99 }
      ],
      pickerOptions: {
        shortcuts: [
          {
            text: '最近一周',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setMonth(start.getMonth() - 1)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setMonth(start.getMonth() - 3)
              picker.$emit('pick', [start, end])
            }
          }
        ]
      },
      chartType: 'line',
      showConfidenceInterval: true,
      rankingType: 'volume',
      topProductsList: [],
      forecastAccuracy: 85,
      averageError: 8.5,
      trend: {
        type: 'up',
        text: '上升趋势'
      },
      seasonalityIndex: 0.82,
      showAdvancedSettings: false,
      advancedSettings: {
        seasonality: 12,
        decomposition: 'multiplicative',
        outlierMethod: 'moving_average',
        forecastSteps: 30
      },
      charts: {
        forecastChart: null
      },
      accuracyColors: [
        {color: '#F56C6C', percentage: 60},
        {color: '#E6A23C', percentage: 70},
        {color: '#67C23A', percentage: 80},
        {color: '#409EFF', percentage: 90}
      ]
    }
  },
  mounted() {
    this.initCharts()
    window.addEventListener('resize', this.resizeCharts)
    this.generateMockData()
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    this.destroyCharts()
  },
  methods: {
    initCharts() {
      this.charts.forecastChart = echarts.init(this.$refs.forecastChart)
      this.updateForecastChart()
    },
    generateMockData() {
      // 生成模拟的产品排名数据
      this.topProductsList = [
        { rank: 1, productName: '儿童夏季短袖T恤', forecast: 12500, growth: 15.3 },
        { rank: 2, productName: '儿童牛仔裤', forecast: 8600, growth: 8.2 },
        { rank: 3, productName: '女童公主连衣裙', forecast: 7200, growth: 5.7 },
        { rank: 4, productName: '儿童运动套装', forecast: 5800, growth: 12.1 },
        { rank: 5, productName: '婴儿连体衣', forecast: 4500, growth: 9.5 },
        { rank: 6, productName: '儿童防晒服', forecast: 3900, growth: -2.8 },
        { rank: 7, productName: '儿童背带裤', forecast: 3200, growth: 1.5 },
        { rank: 8, productName: '儿童睡衣套装', forecast: 2800, growth: -1.2 }
      ]
    },
    updateForecastChart() {
      const dates = []
      const historicalData = []
      const forecastData = []
      const confidenceUpper = []
      const confidenceLower = []
      
      // 生成日期和数据
      const now = new Date()
      let dateFormat = 'MM-dd'
      let days = 30
      
      switch (this.filterForm.period) {
        case 'week':
          days = 7
          dateFormat = 'MM-dd'
          break
        case 'month':
          days = 30
          dateFormat = 'MM-dd'
          break
        case 'quarter':
          days = 90
          dateFormat = 'MM-dd'
          break
        case 'year':
          days = 365
          dateFormat = 'yyyy-MM'
          break
      }
      
      // 生成历史数据和日期
      for (let i = days; i > 0; i--) {
        const date = new Date(now.getTime() - i * 24 * 60 * 60 * 1000)
        dates.push(this.formatDate(date, dateFormat))
        const baseValue = Math.floor(Math.random() * 500 + 500)
        historicalData.push(baseValue)
      }
      
      // 添加今天的数据点
      dates.push(this.formatDate(now, dateFormat))
      historicalData.push(Math.floor(Math.random() * 500 + 500))
      
      // 生成预测数据和置信区间
      for (let i = 1; i <= days; i++) {
        const date = new Date(now.getTime() + i * 24 * 60 * 60 * 1000)
        dates.push(this.formatDate(date, dateFormat))
        
        const baseValue = Math.floor(Math.random() * 200 + 600)
        forecastData.push(baseValue)
        
        if (this.showConfidenceInterval) {
          const interval = baseValue * 0.1
          confidenceUpper.push(baseValue + interval)
          confidenceLower.push(baseValue - interval)
        }
      }

      // 填充预测数据的空值
      const nullArray = new Array(historicalData.length).fill(null)
      const forecastDataWithNull = [...nullArray, ...forecastData]
      const confidenceUpperWithNull = [...nullArray, ...confidenceUpper]
      const confidenceLowerWithNull = [...nullArray, ...confidenceLower]
      
      // 配置图表选项
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: ['历史销量', '预测销量'].concat(this.showConfidenceInterval ? ['置信区间'] : [])
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: this.chartType === 'bar',
          data: dates,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          name: '销量'
        },
        series: [
          {
            name: '历史销量',
            type: this.chartType,
            data: historicalData,
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '预测销量',
            type: this.chartType,
            data: forecastDataWithNull,
            itemStyle: {
              color: '#67C23A'
            }
          }
        ].concat(this.showConfidenceInterval ? [
          {
            name: '置信区间',
            type: 'line',
            data: confidenceUpperWithNull,
            lineStyle: {
              opacity: 0.3,
              type: 'dashed'
            },
            areaStyle: {
              opacity: 0.1
            },
            stack: 'confidence',
            symbol: 'none'
          },
          {
            name: '置信区间',
            type: 'line',
            data: confidenceLowerWithNull,
            lineStyle: {
              opacity: 0.3,
              type: 'dashed'
            },
            areaStyle: {
              opacity: 0.1
            },
            stack: 'confidence',
            symbol: 'none'
          }
        ] : [])
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
      this.loading = true
      setTimeout(() => {
        this.updateForecastChart()
        this.generateMockData()
        this.loading = false
        this.$message.success('预测数据已更新')
      }, 1500)
    },
    handleAdvancedSettingsSave() {
      this.showAdvancedSettings = false
      this.handleForecast()
    },
    exportData() {
      const data = [
        ['产品名称', '预测销量', '增长率'],
        ...this.topProductsList.map(item => [
          item.productName,
          item.forecast,
          `${item.growth}%`
        ])
      ]
      
      const ws = XLSX.utils.aoa_to_sheet(data)
      const wb = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(wb, ws, '销量预测数据')
      
      const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
      const blob = new Blob([wbout], { type: 'application/octet-stream' })
      FileSaver.saveAs(blob, `销量预测报告_${new Date().toLocaleDateString()}.xlsx`)
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
    },
    showConfidenceInterval() {
      this.updateForecastChart()
    },
    rankingType() {
      // 根据排名类型重新排序
      this.topProductsList.sort((a, b) => {
        return this.rankingType === 'volume'
          ? b.forecast - a.forecast
          : b.growth - a.growth
      })
      
      // 更新排名
      this.topProductsList.forEach((item, index) => {
        item.rank = index + 1
      })
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
  white-space: nowrap;
}
.chart-container {
  margin-top: 20px;
}
.analysis-container {
  display: flex;
  padding: 20px;
}
.analysis-item {
  text-align: center;
  margin-right: 40px;
}
.analysis-label {
  margin-bottom: 10px;
  font-size: 14px;
  color: #606266;
}
.analysis-stats {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.stat-item {
  text-align: center;
}
.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}
.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}
.stat-value.up {
  color: #67C23A;
}
.stat-value.down {
  color: #F56C6C;
}
.growth-rate {
  font-weight: bold;
}
.growth-rate.positive {
  color: #67C23A;
}
.growth-rate.negative {
  color: #F56C6C;
}
</style>