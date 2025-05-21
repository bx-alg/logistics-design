<template>
  <div class="app-container">
    <el-card class="filter-container">
      <div slot="header">
        <span>排产计划管理</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="handleCreatePlan">创建排产计划</el-button>
      </div>
      <div class="filter-item-container">
        <div class="filter-item">
          <span class="label">计划编号：</span>
          <el-input v-model="filterForm.planNo" placeholder="请输入计划编号" style="width: 150px" clearable />
        </div>
        <div class="filter-item">
          <span class="label">订单编号：</span>
          <el-input v-model="filterForm.orderNo" placeholder="请输入订单编号" style="width: 150px" clearable />
        </div>
        <div class="filter-item">
          <span class="label">计划状态：</span>
          <el-select v-model="filterForm.status" placeholder="请选择" style="width: 150px" clearable>
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
        <div class="filter-item">
          <span class="label">生产日期：</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            align="right"
            unlink-panels
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 260px"
            value-format="yyyy-MM-dd"
          />
        </div>
        <el-button type="primary" icon="el-icon-search" @click="handleFilter">查询</el-button>
        <el-button icon="el-icon-refresh" @click="resetFilter">重置</el-button>
      </div>
    </el-card>

    <!-- 日历视图 -->
    <el-card style="margin-top: 20px;">
      <div slot="header">
        <span>排产计划日历视图</span>
        <div style="float: right; display: flex; align-items: center;">
          <div style="margin-right: 20px;">
            <el-tag size="mini" type="primary">待生产</el-tag>
            <el-tag size="mini" type="success">生产中</el-tag>
            <el-tag size="mini" type="warning">已完成</el-tag>
            <el-tag size="mini" type="danger">已取消</el-tag>
          </div>
          <el-radio-group v-model="calendarView" size="mini">
            <el-radio-button label="month">月视图</el-radio-button>
            <el-radio-button label="week">周视图</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      <div style="margin-top: 10px;">
        <el-calendar v-if="calendarView === 'month'">
          <template slot="dateCell" slot-scope="{date, data}">
            <div class="calendar-day">
              <div class="day-number">{{ data.day.split('-')[2] }}</div>
              <div v-for="plan in getCalendarPlans(date)" :key="plan.id" class="calendar-event" :class="'status-' + plan.status">
                {{ plan.productName }} ({{ plan.planQuantity }}件)
              </div>
            </div>
          </template>
        </el-calendar>
        <div v-else class="week-view">
          <div class="week-header">
            <div class="week-day" v-for="(day, index) in weekDays" :key="index">
              <div class="day-name">{{ day.dayName }}</div>
              <div class="day-date">{{ day.date }}</div>
            </div>
          </div>
          <div class="week-body">
            <div class="week-day-content" v-for="(day, index) in weekDays" :key="index">
              <div v-for="plan in day.plans" :key="plan.id" class="week-event" :class="'status-' + plan.status">
                <div class="event-time">{{ plan.startTime }} - {{ plan.endTime }}</div>
                <div class="event-title">{{ plan.productName }}</div>
                <div class="event-quantity">数量: {{ plan.planQuantity }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 列表视图 -->
    <el-card style="margin-top: 20px;">
      <div slot="header">
        <span>排产计划列表</span>
      </div>
      <el-table
        v-loading="tableLoading"
        :data="tableData"
        border
        style="width: 100%"
      >
        <el-table-column prop="planNo" label="计划编号" width="150" />
        <el-table-column prop="orderNo" label="关联订单" width="150" />
        <el-table-column prop="productName" label="产品名称" width="150" />
        <el-table-column prop="planQuantity" label="计划数量" width="100" />
        <el-table-column prop="actualQuantity" label="实际数量" width="100" />
        <el-table-column label="完成率" width="150">
          <template slot-scope="scope">
            <el-progress :percentage="calculateProgress(scope.row)" :format="percentFormat" />
          </template>
        </el-table-column>
        <el-table-column label="生产日期" width="200">
          <template slot-scope="scope">
            {{ scope.row.startDate }} 至 {{ scope.row.endDate }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusName(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" @click="handleDetail(scope.row)">详情</el-button>
            <el-button type="text" v-if="scope.row.status === 0" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" v-if="scope.row.status === 0" @click="handleStart(scope.row)">开始生产</el-button>
            <el-button type="text" v-if="scope.row.status === 1" @click="handleComplete(scope.row)">完成</el-button>
            <el-button type="text" v-if="scope.row.status === 0" @click="handleCancel(scope.row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="filterForm.pageNum"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="filterForm.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        />
      </div>
    </el-card>

    <!-- 创建/编辑排产计划对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="650px">
      <el-form :model="planForm" :rules="planRules" ref="planForm" label-width="100px">
        <el-form-item label="关联订单" prop="orderId">
          <el-select v-model="planForm.orderId" placeholder="请选择订单" style="width: 100%">
            <el-option
              v-for="item in orderOptions"
              :key="item.value"
              :label="`${item.label} (${item.orderNo})`"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="生产产品" prop="productId">
          <el-select v-model="planForm.productId" placeholder="请选择产品" style="width: 100%">
            <el-option
              v-for="item in productOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="计划数量" prop="planQuantity">
          <el-input-number v-model="planForm.planQuantity" :min="1" style="width: 200px" />
        </el-form-item>
        <el-form-item label="生产日期" prop="dateRange">
          <el-date-picker
            v-model="planForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="planForm.priority" placeholder="请选择优先级" style="width: 200px">
            <el-option label="低" :value="0" />
            <el-option label="中" :value="1" />
            <el-option label="高" :value="2" />
            <el-option label="紧急" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="planForm.remark" rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitPlan" :loading="submitLoading">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// import { getProductionPlanList, updateProductionPlan } from '@/api/productionPlan'
import { productionApi } from '@/utils/mockUtils'
import { getConstants } from '@/utils/mockUtils'
import moment from 'moment'

export default {
  name: 'ProductionPlan',
  data() {
    return {
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: '',
        customerName: '',
        status: '',
        dateRange: []
      },
      // 状态选项
      statusOptions: getConstants().orderStatus,
      // 表格数据
      tableData: [],
      // 加载状态
      loading: false,
      // 总数
      total: 0,
      // 编辑对话框
      editDialog: {
        visible: false,
        loading: false,
        form: {
          id: null,
          orderNo: '',
          customerName: '',
          productName: '',
          startTime: '',
          endTime: '',
          status: '',
          remark: ''
        },
        rules: {
          startTime: [
            { required: true, message: '请选择开始时间', trigger: 'change' }
          ],
          endTime: [
            { required: true, message: '请选择结束时间', trigger: 'change' }
          ]
        }
      },
      // 筛选表单
      filterForm: {
        planNo: '',
        orderNo: '',
        status: '',
        startDate: '',
        endDate: '',
        pageNum: 1,
        pageSize: 10
      },
      dateRange: [],
      // 日历视图设置
      calendarView: 'month',
      weekDays: [],
      // 对话框设置
      dialogVisible: false,
      dialogTitle: '创建排产计划',
      planForm: {
        id: null,
        orderId: '',
        productId: '',
        planQuantity: 1,
        dateRange: [],
        priority: 1,
        remark: ''
      },
      planRules: {
        orderId: [{ required: true, message: '请选择关联订单', trigger: 'change' }],
        productId: [{ required: true, message: '请选择生产产品', trigger: 'change' }],
        planQuantity: [{ required: true, message: '请输入计划数量', trigger: 'blur' }],
        dateRange: [{ required: true, message: '请选择生产日期', trigger: 'change' }]
      },
      submitLoading: false,
      // 选项数据
      orderOptions: [
        { value: 1, label: '上海某科技有限公司', orderNo: 'DD20231001001' },
        { value: 2, label: '北京某贸易有限公司', orderNo: 'DD20231005002' },
        { value: 3, label: '广州某电子科技公司', orderNo: 'DD20230925003' }
      ],
      productOptions: [
        { value: 1, label: '智能手机A' },
        { value: 2, label: '笔记本电脑B' },
        { value: 3, label: '平板电脑C' },
        { value: 4, label: '智能手表D' },
        { value: 5, label: '智能音箱E' }
      ],
      calendarPlans: []
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
    },
    calendarView() {
      if (this.calendarView === 'week') {
        this.generateWeekDays()
      }
    }
  },
  created() {
    this.getList()
    this.generateWeekDays()
  },
  methods: {
    /** 获取生产计划列表 */
    async getList() {
      this.loading = true
      try {
        const res = await productionApi.list({
          filters: {
            orderNo: this.queryParams.orderNo,
            customerName: this.queryParams.customerName,
            status: this.queryParams.status,
            dateRange: this.queryParams.dateRange
          },
          pagination: {
            pageNum: this.queryParams.pageNum,
            pageSize: this.queryParams.pageSize
          }
        })
        this.tableData = res.records
        this.total = res.total
      } catch (error) {
        console.error('获取生产计划列表失败:', error)
        this.$message.error('获取生产计划列表失败')
      } finally {
        this.loading = false
      }
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        orderNo: '',
        customerName: '',
        status: '',
        dateRange: []
      }
      this.getList()
    },

    /** 编辑按钮操作 */
    handleEdit(row) {
      this.editDialog.form = {
        id: row.id,
        orderNo: row.orderNo,
        customerName: row.customerName,
        productName: row.productName,
        startTime: row.startTime,
        endTime: row.estimatedEndTime,
        status: row.status,
        remark: row.description
      }
      this.editDialog.visible = true
    },

    /** 提交编辑 */
    async submitEdit() {
      this.$refs.editForm.validate(async valid => {
        if (valid) {
          this.editDialog.loading = true
          try {
            await productionApi.updatePlan(this.editDialog.form)
            this.$message.success('更新成功')
            this.editDialog.visible = false
            this.getList()
          } catch (error) {
            console.error('更新生产计划失败:', error)
            this.$message.error('更新生产计划失败')
          } finally {
            this.editDialog.loading = false
          }
        }
      })
    },

    /** 取消编辑 */
    cancelEdit() {
      this.editDialog.visible = false
      this.editDialog.form = {
        id: null,
        orderNo: '',
        customerName: '',
        productName: '',
        startTime: '',
        endTime: '',
        status: '',
        remark: ''
      }
    },

    /** 每页显示条数改变 */
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getList()
    },

    /** 当前页改变 */
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },

    // 格式化日期
    formatDateTime(date) {
      return date ? moment(date).format('YYYY-MM-DD HH:mm:ss') : '-'
    },

    // 格式化状态
    formatStatus(status) {
      const statusMap = this.statusOptions.reduce((acc, curr) => {
        acc[curr.value] = curr.label
        return acc
      }, {})
      return statusMap[status] || '未知'
    },

    // 获取状态标签类型
    getStatusType(status) {
      const typeMap = {
        0: 'info',
        1: 'warning',
        2: 'primary',
        3: 'success',
        4: 'success'
      }
      return typeMap[status] || 'info'
    },

    // 生成日历数据
    generateCalendarData() {
      this.calendarPlans = this.tableData.filter(item => item.status !== 3).map(item => {
        const startDate = new Date(item.startDate)
        const endDate = new Date(item.endDate)
        const days = []
        
        let currentDate = new Date(startDate)
        while (currentDate <= endDate) {
          days.push(new Date(currentDate))
          currentDate.setDate(currentDate.getDate() + 1)
        }
        
        return {
          id: item.id,
          planNo: item.planNo,
          productName: item.productName,
          planQuantity: item.planQuantity,
          days: days,
          status: item.status,
          startTime: '09:00',
          endTime: '18:00'
        }
      })
    },
    // 生成周视图数据
    generateWeekDays() {
      const today = new Date()
      const weekStart = new Date(today)
      const dayOfWeek = today.getDay() || 7
      weekStart.setDate(today.getDate() - dayOfWeek + 1)
      
      this.weekDays = []
      for (let i = 0; i < 7; i++) {
        const date = new Date(weekStart)
        date.setDate(weekStart.getDate() + i)
        
        this.weekDays.push({
          date: this.formatDate(date, 'MM-dd'),
          dayName: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'][i],
          fullDate: this.formatDate(date, 'yyyy-MM-dd'),
          plans: []
        })
      }
      
      // 填充周视图计划数据
      if (this.calendarPlans.length > 0) {
        this.calendarPlans.forEach(plan => {
          plan.days.forEach(day => {
            const dayStr = this.formatDate(day, 'yyyy-MM-dd')
            const weekDay = this.weekDays.find(d => d.fullDate === dayStr)
            if (weekDay) {
              weekDay.plans.push({
                id: plan.id,
                productName: plan.productName,
                planQuantity: plan.planQuantity,
                status: plan.status,
                startTime: plan.startTime,
                endTime: plan.endTime
              })
            }
          })
        })
      }
    },
    // 获取指定日期的计划
    getCalendarPlans(date) {
      const dateStr = this.formatDate(date, 'yyyy-MM-dd')
      return this.calendarPlans.filter(plan => {
        return plan.days.some(day => this.formatDate(day, 'yyyy-MM-dd') === dateStr)
      })
    },
    // 格式化日期
    formatDate(date, format) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      
      if (format === 'yyyy-MM-dd') {
        return `${year}-${month}-${day}`
      } else if (format === 'MM-dd') {
        return `${month}-${day}`
      }
      
      return `${year}-${month}-${day}`
    },
    // 计算完成百分比
    calculateProgress(row) {
      if (row.planQuantity === 0) return 0
      return Math.round((row.actualQuantity / row.planQuantity) * 100)
    },
    // 百分比格式化
    percentFormat(percent) {
      return percent + '%'
    },
    // 获取状态名称
    getStatusName(status) {
      const nameMap = {
        0: '待生产',
        1: '生产中',
        2: '已完成',
        3: '已取消'
      }
      return nameMap[status] || '未知'
    },
    // 查询
    handleFilter() {
      this.filterForm.pageNum = 1
      this.getList()
    },
    // 重置筛选条件
    resetFilter() {
      this.filterForm = {
        planNo: '',
        orderNo: '',
        status: '',
        startDate: '',
        endDate: '',
        pageNum: 1,
        pageSize: 10
      }
      this.dateRange = []
      this.getList()
    },
    // 查看详情
    handleDetail(row) {
      this.$router.push({
        name: 'ProductionPlanDetail',
        params: { id: row.id }
      })
    },
    // 开始生产
    handleStart(row) {
      this.$confirm('确认开始生产该计划?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟请求
        const index = this.tableData.findIndex(item => item.id === row.id)
        if (index !== -1) {
          this.tableData[index].status = 1
          this.$message.success('已开始生产')
          
          // 更新日历数据
          this.generateCalendarData()
          if (this.calendarView === 'week') {
            this.generateWeekDays()
          }
        }
      }).catch(() => {})
    },
    // 完成生产
    handleComplete(row) {
      this.$confirm('确认完成该生产计划?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟请求
        const index = this.tableData.findIndex(item => item.id === row.id)
        if (index !== -1) {
          this.tableData[index].status = 2
          this.tableData[index].actualQuantity = this.tableData[index].planQuantity
          this.$message.success('生产计划已完成')
          
          // 更新日历数据
          this.generateCalendarData()
          if (this.calendarView === 'week') {
            this.generateWeekDays()
          }
        }
      }).catch(() => {})
    },
    // 取消计划
    handleCancel(row) {
      this.$confirm('确认取消该生产计划?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟请求
        const index = this.tableData.findIndex(item => item.id === row.id)
        if (index !== -1) {
          this.tableData[index].status = 3
          this.$message.success('生产计划已取消')
          
          // 更新日历数据
          this.generateCalendarData()
          if (this.calendarView === 'week') {
            this.generateWeekDays()
          }
        }
      }).catch(() => {})
    },
    // 创建排产计划
    handleCreatePlan() {
      this.dialogTitle = '创建排产计划'
      this.planForm = {
        id: null,
        orderId: '',
        productId: '',
        planQuantity: 1,
        dateRange: [],
        priority: 1,
        remark: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.planForm && this.$refs.planForm.clearValidate()
      })
    },
    // 提交排产计划
    submitPlan() {
      this.$refs.planForm.validate(valid => {
        if (!valid) return
        
        this.submitLoading = true
        
        // 模拟提交请求
        setTimeout(() => {
          if (this.planForm.id) {
            // 更新
            const index = this.tableData.findIndex(item => item.id === this.planForm.id)
            if (index !== -1) {
              const orderInfo = this.orderOptions.find(o => o.value === this.planForm.orderId)
              const productInfo = this.productOptions.find(p => p.value === this.planForm.productId)
              
              this.tableData[index] = {
                ...this.tableData[index],
                orderNo: orderInfo ? orderInfo.orderNo : '',
                productName: productInfo ? productInfo.label : '',
                planQuantity: this.planForm.planQuantity,
                startDate: this.planForm.dateRange[0],
                endDate: this.planForm.dateRange[1],
                priority: this.planForm.priority,
                remark: this.planForm.remark
              }
            }
            this.$message.success('排产计划更新成功')
          } else {
            // 创建
            const orderInfo = this.orderOptions.find(o => o.value === this.planForm.orderId)
            const productInfo = this.productOptions.find(p => p.value === this.planForm.productId)
            
            const newPlan = {
              id: Date.now(),
              planNo: 'PP' + new Date().getTime().toString().substring(3),
              orderNo: orderInfo ? orderInfo.orderNo : '',
              productName: productInfo ? productInfo.label : '',
              planQuantity: this.planForm.planQuantity,
              actualQuantity: 0,
              startDate: this.planForm.dateRange[0],
              endDate: this.planForm.dateRange[1],
              status: 0,
              priority: this.planForm.priority,
              remark: this.planForm.remark
            }
            
            this.tableData.unshift(newPlan)
            this.total += 1
            this.$message.success('排产计划创建成功')
          }
          
          this.dialogVisible = false
          this.submitLoading = false
          
          // 更新日历数据
          this.generateCalendarData()
          if (this.calendarView === 'week') {
            this.generateWeekDays()
          }
        }, 1000)
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
  margin-right: 15px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}
.filter-item .label {
  margin-right: 5px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
.calendar-day {
  height: 100%;
  padding: 2px;
  overflow: hidden;
}
.day-number {
  font-size: 14px;
  margin-bottom: 5px;
}
.calendar-event {
  font-size: 12px;
  padding: 2px 4px;
  margin-bottom: 2px;
  border-radius: 2px;
  color: #fff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.status-0 {
  background-color: #409EFF;
}
.status-1 {
  background-color: #67C23A;
}
.status-2 {
  background-color: #E6A23C;
}
.status-3 {
  background-color: #F56C6C;
}
.week-view {
  border: 1px solid #EBEEF5;
  border-radius: 4px;
}
.week-header {
  display: flex;
  background-color: #f5f7fa;
  border-bottom: 1px solid #EBEEF5;
}
.week-day {
  flex: 1;
  text-align: center;
  padding: 10px;
  border-right: 1px solid #EBEEF5;
}
.week-day:last-child {
  border-right: none;
}
.day-name {
  font-weight: bold;
  margin-bottom: 5px;
}
.week-body {
  display: flex;
  height: 400px;
}
.week-day-content {
  flex: 1;
  padding: 10px;
  border-right: 1px solid #EBEEF5;
  overflow-y: auto;
}
.week-day-content:last-child {
  border-right: none;
}
.week-event {
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 4px;
  color: #fff;
}
.event-time {
  font-size: 12px;
  margin-bottom: 5px;
}
.event-title {
  font-weight: bold;
  margin-bottom: 5px;
}
.event-quantity {
  font-size: 12px;
}
</style> 