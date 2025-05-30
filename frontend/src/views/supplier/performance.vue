<template>
  <div class="app-container">
    <el-card class="supplier-info">
      <div slot="header">
        <span>供应商基本信息</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="goBack">返回列表</el-button>
      </div>
      <el-descriptions :column="4" border>
        <el-descriptions-item label="供应商编码">{{ supplierInfo.code }}</el-descriptions-item>
        <el-descriptions-item label="供应商名称">{{ supplierInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ supplierInfo.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ supplierInfo.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="供应商类别">{{ supplierInfo.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="合作等级">
          <el-rate v-model="supplierInfo.level" disabled text-color="#ff9900" />
        </el-descriptions-item>
        <el-descriptions-item label="合作状态">
          <el-tag :type="getStatusType(supplierInfo.status)">{{ supplierInfo.statusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="合作开始日期">{{ supplierInfo.cooperationStartDate }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span>绩效评分趋势</span>
            <el-radio-group v-model="timeRange" size="mini" style="float: right;">
              <el-radio-button label="month">月度</el-radio-button>
              <el-radio-button label="quarter">季度</el-radio-button>
              <el-radio-button label="year">年度</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="scoreChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span>供应商绩效雷达图</span>
          </div>
          <div ref="radarChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <div slot="header">
        <span>绩效评估记录</span>
        <el-button type="primary" size="small" style="float: right;" @click="handleNewEvaluation">新增评估</el-button>
      </div>
      <el-table :data="evaluationList" border style="width: 100%">
        <el-table-column prop="period" label="评估周期" align="center" width="150" />
        <el-table-column prop="evaluationDate" label="评估日期" align="center" width="150" />
        <el-table-column prop="evaluator" label="评估人" align="center" width="120" />
        <el-table-column label="质量评分" align="center" width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.qualityScore }}</span>
            <el-progress :percentage="scope.row.qualityScore" :color="getScoreColor(scope.row.qualityScore)" :show-text="false" style="width: 80%; margin-left: 10px;" />
          </template>
        </el-table-column>
        <el-table-column label="交付评分" align="center" width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.deliveryScore }}</span>
            <el-progress :percentage="scope.row.deliveryScore" :color="getScoreColor(scope.row.deliveryScore)" :show-text="false" style="width: 80%; margin-left: 10px;" />
          </template>
        </el-table-column>
        <el-table-column label="成本评分" align="center" width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.costScore }}</span>
            <el-progress :percentage="scope.row.costScore" :color="getScoreColor(scope.row.costScore)" :show-text="false" style="width: 80%; margin-left: 10px;" />
          </template>
        </el-table-column>
        <el-table-column label="服务评分" align="center" width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.serviceScore }}</span>
            <el-progress :percentage="scope.row.serviceScore" :color="getScoreColor(scope.row.serviceScore)" :show-text="false" style="width: 80%; margin-left: 10px;" />
          </template>
        </el-table-column>
        <el-table-column label="创新评分" align="center" width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.innovationScore }}</span>
            <el-progress :percentage="scope.row.innovationScore" :color="getScoreColor(scope.row.innovationScore)" :show-text="false" style="width: 80%; margin-left: 10px;" />
          </template>
        </el-table-column>
        <el-table-column label="综合评分" align="center" width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.totalScore }}</span>
            <el-progress :percentage="scope.row.totalScore" :color="getScoreColor(scope.row.totalScore)" :show-text="false" style="width: 80%; margin-left: 10px;" />
          </template>
        </el-table-column>
        <el-table-column label="评级" align="center" width="100">
          <template slot-scope="scope">
            <el-tag :type="getEvaluationLevelType(scope.row.level)">{{ scope.row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="150">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleViewDetail(scope.row)">详情</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)" :disabled="isHistoricalRecord(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增评估对话框 -->
    <el-dialog title="供应商绩效评估" :visible.sync="evaluationDialogVisible" width="700px">
      <el-form :model="evaluationForm" label-width="120px">
        <el-form-item label="评估周期">
          <el-date-picker
            v-model="evaluationForm.period"
            type="month"
            placeholder="选择月份"
            value-format="yyyy-MM"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="评估日期">
          <el-date-picker
            v-model="evaluationForm.evaluationDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="质量评分">
          <el-slider
            v-model="evaluationForm.qualityScore"
            :format-tooltip="formatTooltip"
            :step="1"
            :min="0"
            :max="100"
            show-stops
          />
        </el-form-item>
        <el-form-item label="交付评分">
          <el-slider
            v-model="evaluationForm.deliveryScore"
            :format-tooltip="formatTooltip"
            :step="1"
            :min="0"
            :max="100"
            show-stops
          />
        </el-form-item>
        <el-form-item label="成本评分">
          <el-slider
            v-model="evaluationForm.costScore"
            :format-tooltip="formatTooltip"
            :step="1"
            :min="0"
            :max="100"
            show-stops
          />
        </el-form-item>
        <el-form-item label="服务评分">
          <el-slider
            v-model="evaluationForm.serviceScore"
            :format-tooltip="formatTooltip"
            :step="1"
            :min="0"
            :max="100"
            show-stops
          />
        </el-form-item>
        <el-form-item label="创新评分">
          <el-slider
            v-model="evaluationForm.innovationScore"
            :format-tooltip="formatTooltip"
            :step="1"
            :min="0"
            :max="100"
            show-stops
          />
        </el-form-item>
        <el-form-item label="评估意见">
          <el-input type="textarea" v-model="evaluationForm.comments" :rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="evaluationDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitEvaluation">提 交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'SupplierPerformance',
  data() {
    return {
      supplierId: null,
      timeRange: 'quarter',
      supplierInfo: {
        code: 'S00001',
        name: '浙江海宁布料制造有限公司',
        contactPerson: '张明辉',
        contactPhone: '13812345678',
        category: 'fabric',
        categoryName: '面料供应',
        level: 5,
        status: 1,
        statusName: '合作中',
        cooperationStartDate: '2020-01-15'
      },
      evaluationList: [
        { id: 1, period: '2023-Q1', evaluationDate: '2023-04-05', evaluator: '李经理', qualityScore: 92, deliveryScore: 88, costScore: 85, serviceScore: 90, innovationScore: 78, totalScore: 87, level: 'A', comments: '质量稳定，交付及时，服务态度好' },
        { id: 2, period: '2023-Q2', evaluationDate: '2023-07-08', evaluator: '王经理', qualityScore: 95, deliveryScore: 90, costScore: 82, serviceScore: 92, innovationScore: 80, totalScore: 88, level: 'A', comments: '本季度表现优秀，质量有所提高' },
        { id: 3, period: '2023-Q3', evaluationDate: '2023-10-10', evaluator: '张经理', qualityScore: 90, deliveryScore: 85, costScore: 88, serviceScore: 89, innovationScore: 82, totalScore: 87, level: 'A', comments: '整体表现不错，创新能力有提升' },
        { id: 4, period: '2023-Q4', evaluationDate: '2024-01-15', evaluator: '刘经理', qualityScore: 93, deliveryScore: 92, costScore: 90, serviceScore: 94, innovationScore: 85, totalScore: 91, level: 'A+', comments: '年末表现优异，各项指标均有提升' }
      ],
      evaluationDialogVisible: false,
      evaluationForm: {
        period: '',
        evaluationDate: '',
        qualityScore: 80,
        deliveryScore: 80,
        costScore: 80,
        serviceScore: 80,
        innovationScore: 80,
        comments: ''
      },
      charts: {
        scoreChart: null,
        radarChart: null
      }
    }
  },
  computed: {
    currentEvaluationLevel() {
      const latestEvaluation = this.evaluationList[this.evaluationList.length - 1]
      return latestEvaluation ? latestEvaluation.level : '-'
    }
  },
  mounted() {
    this.supplierId = this.$route.query.id
    // 实际项目中这里会根据supplierId调用接口获取供应商信息
    this.initCharts()
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    this.destroyCharts()
  },
  methods: {
    goBack() {
      this.$router.push('/supplier')
    },
    initCharts() {
      // 初始化分数趋势图表
      this.updateScoreChart()
      
      // 初始化雷达图
      this.charts.radarChart = echarts.init(this.$refs.radarChart)
      const latestEvaluation = this.evaluationList[this.evaluationList.length - 1]
      this.charts.radarChart.setOption({
        title: {
          text: '供应商绩效雷达图',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        radar: {
          indicator: [
            { name: '质量', max: 100 },
            { name: '交付', max: 100 },
            { name: '成本', max: 100 },
            { name: '服务', max: 100 },
            { name: '创新', max: 100 }
          ],
          shape: 'circle',
          splitNumber: 5,
          name: {
            textStyle: {
              color: '#333'
            }
          },
          splitLine: {
            lineStyle: {
              color: [
                'rgba(210, 210, 210, 0.5)',
                'rgba(180, 180, 180, 0.5)',
                'rgba(150, 150, 150, 0.5)',
                'rgba(120, 120, 120, 0.5)',
                'rgba(90, 90, 90, 0.5)'
              ].reverse()
            }
          },
          splitArea: {
            show: false
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(120, 120, 120, 0.3)'
            }
          }
        },
        series: [
          {
            name: '绩效评分',
            type: 'radar',
            emphasis: {
              lineStyle: {
                width: 4
              }
            },
            data: [
              {
                value: [
                  latestEvaluation.qualityScore,
                  latestEvaluation.deliveryScore,
                  latestEvaluation.costScore,
                  latestEvaluation.serviceScore,
                  latestEvaluation.innovationScore
                ],
                name: '当前评分',
                areaStyle: {
                  color: 'rgba(64, 158, 255, 0.3)'
                },
                lineStyle: {
                  color: '#409EFF'
                },
                itemStyle: {
                  color: '#409EFF'
                }
              },
              {
                value: [90, 90, 90, 90, 90],
                name: '行业标杆',
                lineStyle: {
                  color: '#67C23A'
                },
                itemStyle: {
                  color: '#67C23A'
                }
              }
            ]
          }
        ]
      })
    },
    updateScoreChart() {
      if (!this.$refs.scoreChart) return

      if (!this.charts.scoreChart) {
        this.charts.scoreChart = echarts.init(this.$refs.scoreChart)
      }

      let xAxisData = []
      let qualityData = []
      let deliveryData = []
      let costData = []
      let serviceData = []
      let innovationData = []
      let totalData = []

      // 根据选择的时间范围准备数据
      if (this.timeRange === 'month') {
        xAxisData = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
        qualityData = [89, 90, 92, 91, 93, 94, 92, 93, 91, 90, 93, 95]
        deliveryData = [85, 86, 88, 87, 89, 90, 88, 89, 90, 91, 92, 93]
        costData = [80, 82, 83, 85, 84, 83, 85, 86, 87, 86, 88, 90]
        serviceData = [88, 90, 89, 91, 92, 93, 91, 92, 93, 94, 93, 95]
        innovationData = [75, 76, 78, 77, 79, 80, 81, 80, 82, 83, 84, 85]
        totalData = [83, 85, 86, 86, 87, 88, 87, 88, 89, 89, 90, 92]
      } else if (this.timeRange === 'quarter') {
        xAxisData = ['2022-Q1', '2022-Q2', '2022-Q3', '2022-Q4', '2023-Q1', '2023-Q2', '2023-Q3', '2023-Q4']
        qualityData = [88, 90, 89, 91, 92, 95, 90, 93]
        deliveryData = [84, 86, 85, 88, 88, 90, 85, 92]
        costData = [80, 82, 81, 83, 85, 82, 88, 90]
        serviceData = [85, 87, 86, 89, 90, 92, 89, 94]
        innovationData = [72, 75, 74, 77, 78, 80, 82, 85]
        totalData = [82, 84, 83, 86, 87, 88, 87, 91]
      } else {
        xAxisData = ['2019', '2020', '2021', '2022', '2023']
        qualityData = [85, 87, 90, 92, 93]
        deliveryData = [82, 84, 87, 89, 90]
        costData = [78, 80, 82, 85, 88]
        serviceData = [83, 85, 88, 90, 92]
        innovationData = [70, 72, 75, 78, 82]
        totalData = [80, 82, 84, 87, 89]
      }

      this.charts.scoreChart.setOption({
        title: {
          text: '绩效评分趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['质量', '交付', '成本', '服务', '创新', '综合'],
          bottom: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: xAxisData
        },
        yAxis: {
          type: 'value',
          name: '分数',
          min: 50
        },
        series: [
          {
            name: '质量',
            type: 'line',
            data: qualityData,
            lineStyle: { width: 2 },
            itemStyle: { color: '#F56C6C' }
          },
          {
            name: '交付',
            type: 'line',
            data: deliveryData,
            lineStyle: { width: 2 },
            itemStyle: { color: '#E6A23C' }
          },
          {
            name: '成本',
            type: 'line',
            data: costData,
            lineStyle: { width: 2 },
            itemStyle: { color: '#67C23A' }
          },
          {
            name: '服务',
            type: 'line',
            data: serviceData,
            lineStyle: { width: 2 },
            itemStyle: { color: '#409EFF' }
          },
          {
            name: '创新',
            type: 'line',
            data: innovationData,
            lineStyle: { width: 2 },
            itemStyle: { color: '#909399' }
          },
          {
            name: '综合',
            type: 'line',
            data: totalData,
            lineStyle: { width: 3, type: 'dotted' },
            itemStyle: { color: '#9B2BFF' }
          }
        ]
      })
    },
    getStatusType(status) {
      const statusMap = {
        1: 'success',
        2: 'warning',
        3: 'danger'
      }
      return statusMap[status]
    },
    getScoreColor(score) {
      if (score >= 90) {
        return '#67C23A'
      } else if (score >= 80) {
        return '#409EFF'
      } else if (score >= 70) {
        return '#E6A23C'
      } else {
        return '#F56C6C'
      }
    },
    getEvaluationLevelType(level) {
      const levelMap = {
        'A+': 'success',
        'A': 'primary',
        'B+': '',
        'B': 'info',
        'C': 'warning',
        'D': 'danger'
      }
      return levelMap[level] || 'info'
    },
    formatTooltip(val) {
      return val + '分'
    },
    isHistoricalRecord(row) {
      // 判断是否为历史记录（超过3个月的评估不允许删除）
      const now = new Date()
      const evaluationDate = new Date(row.evaluationDate)
      const threeMonthsAgo = new Date(now.setMonth(now.getMonth() - 3))
      return evaluationDate < threeMonthsAgo
    },
    handleNewEvaluation() {
      // 重置表单
      const now = new Date()
      const year = now.getFullYear()
      const month = (now.getMonth() + 1).toString().padStart(2, '0')
      this.evaluationForm = {
        period: `${year}-${month}`,
        evaluationDate: this.formatDate(now),
        qualityScore: 80,
        deliveryScore: 80,
        costScore: 80,
        serviceScore: 80,
        innovationScore: 80,
        comments: ''
      }
      this.evaluationDialogVisible = true
    },
    formatDate(date) {
      const year = date.getFullYear()
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    submitEvaluation() {
      if (!this.evaluationForm.period || !this.evaluationForm.evaluationDate) {
        this.$message.warning('请选择评估周期和评估日期')
        return
      }

      // 计算总分
      const totalScore = Math.round(
        (this.evaluationForm.qualityScore * 0.3 +
        this.evaluationForm.deliveryScore * 0.25 +
        this.evaluationForm.costScore * 0.2 +
        this.evaluationForm.serviceScore * 0.15 +
        this.evaluationForm.innovationScore * 0.1) * 10
      ) / 10

      // 确定评级
      let level = 'C'
      if (totalScore >= 90) {
        level = 'A+'
      } else if (totalScore >= 85) {
        level = 'A'
      } else if (totalScore >= 80) {
        level = 'B+'
      } else if (totalScore >= 70) {
        level = 'B'
      } else if (totalScore >= 60) {
        level = 'C'
      } else {
        level = 'D'
      }

      // 创建新评估记录
      const newEvaluation = {
        id: this.evaluationList.length + 1,
        period: this.evaluationForm.period,
        evaluationDate: this.evaluationForm.evaluationDate,
        evaluator: '当前用户',
        qualityScore: this.evaluationForm.qualityScore,
        deliveryScore: this.evaluationForm.deliveryScore,
        costScore: this.evaluationForm.costScore,
        serviceScore: this.evaluationForm.serviceScore,
        innovationScore: this.evaluationForm.innovationScore,
        totalScore: totalScore,
        level: level,
        comments: this.evaluationForm.comments
      }

      // 添加到列表
      this.evaluationList.push(newEvaluation)
      this.$message.success('评估提交成功')
      this.evaluationDialogVisible = false

      // 更新图表
      this.initCharts()
    },
    handleViewDetail(row) {
      this.$message.success(`查看评估详情：${row.period}`)
    },
    handleDelete(row) {
      this.$confirm('确认删除该评估记录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const index = this.evaluationList.findIndex(item => item.id === row.id)
        if (index !== -1) {
          this.evaluationList.splice(index, 1)
        }
        this.$message.success('删除成功')
        this.initCharts()
      }).catch(() => {})
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
    timeRange() {
      this.updateScoreChart()
    }
  }
}
</script>

<style scoped>
.supplier-info {
  margin-bottom: 20px;
}
</style>