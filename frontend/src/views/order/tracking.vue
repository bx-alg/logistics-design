<template>
  <div class="app-container">
    <el-card class="filter-container">
      <div slot="header">
        <span>生产进度跟踪</span>
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
          <span class="label">生产状态：</span>
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
          <span class="label">日期范围：</span>
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
      <el-col :span="24">
        <el-card shadow="hover">
          <div slot="header" class="clearfix">
            <span>生产进度概览</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="status-card waiting">
                <div class="card-icon"><i class="el-icon-time"></i></div>
                <div class="card-content">
                  <div class="card-title">待生产</div>
                  <div class="card-value">{{ summaryData.waiting }}</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="status-card processing">
                <div class="card-icon"><i class="el-icon-goods"></i></div>
                <div class="card-content">
                  <div class="card-title">生产中</div>
                  <div class="card-value">{{ summaryData.processing }}</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="status-card shipped">
                <div class="card-icon"><i class="el-icon-truck"></i></div>
                <div class="card-content">
                  <div class="card-title">已发货</div>
                  <div class="card-value">{{ summaryData.shipped }}</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="status-card completed">
                <div class="card-icon"><i class="el-icon-circle-check"></i></div>
                <div class="card-content">
                  <div class="card-title">已完成</div>
                  <div class="card-value">{{ summaryData.completed }}</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <div slot="header">
        <span>生产订单列表</span>
      </div>
      <el-table
        v-loading="tableLoading"
        :data="tableData"
        border
        style="width: 100%"
      >
        <el-table-column prop="orderNo" label="订单编号" width="150" />
        <el-table-column prop="customerName" label="客户名称" width="180" />
        <el-table-column prop="productName" label="主要产品" width="180" />
        <el-table-column label="生产状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusName(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="生产进度" width="280">
          <template slot-scope="scope">
            <el-progress 
              :percentage="scope.row.progress || 0" 
              :status="getProgressStatus(scope.row.progress || 0)" 
              :format="percentageFormat"
            />
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160">
          <template slot-scope="scope">
            {{ scope.row.startTime ? formatDateTime(scope.row.startTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="estimatedEndTime" label="预计完成时间" width="160">
          <template slot-scope="scope">
            {{ scope.row.estimatedEndTime ? formatDateTime(scope.row.estimatedEndTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" @click="handleDetail(scope.row)">详情</el-button>
            <el-button 
              v-if="scope.row.status === 1 || scope.row.status === 2" 
              type="text" 
              @click="handleUpdateProgress(scope.row)"
            >更新进度</el-button>
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

    <!-- 进度更新对话框 -->
    <el-dialog title="更新生产进度" :visible.sync="progressDialogVisible" width="500px">
      <el-form :model="progressForm" ref="progressForm" label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ progressForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="产品名称">
          <span>{{ progressForm.productName }}</span>
        </el-form-item>
        <el-form-item label="当前进度">
          <el-progress 
            :percentage="progressForm.progress" 
            :status="getProgressStatus(progressForm.progress)" 
            :format="percentageFormat"
          />
        </el-form-item>
        <el-form-item label="新进度值" prop="newProgress">
          <el-slider
            v-model="progressForm.newProgress"
            :min="progressForm.progress"
            :max="100"
            :step="5"
            show-input
          />
        </el-form-item>
        <el-form-item label="进度说明" prop="description">
          <el-input type="textarea" v-model="progressForm.description" rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="progressDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitProgress" :loading="submitLoading">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// import { getProductionList, getProductionSummary, updateProgress } from '@/api/productionTracking'
import { productionApi } from '@/utils/mockUtils'
import { getConstants } from '@/utils/mockUtils'
import moment from 'moment'

export default {
  name: 'ProductionTracking',
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
      statusOptions: getConstants().orderStatus,
      // 表格数据
      tableData: [],
      tableLoading: false,
      total: 0,
      // 统计数据
      summaryData: {
        waiting: 0,
        processing: 0,
        shipped: 0,
        completed: 0
      },
      // 进度更新表单
      progressDialogVisible: false,
      progressForm: {
        id: null,
        orderNo: '',
        productName: '',
        progress: 0,
        newProgress: 0,
        description: ''
      },
      submitLoading: false
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
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.tableLoading = true
      try {
        const [listRes, summaryRes] = await Promise.all([
          productionApi.list({
            filters: {
              orderNo: this.filterForm.orderNo,
              customerName: this.filterForm.customerName,
              status: this.filterForm.status,
              dateRange: [this.filterForm.startDate, this.filterForm.endDate]
            },
            pagination: {
              pageNum: this.filterForm.pageNum,
              pageSize: this.filterForm.pageSize
            }
          }),
          productionApi.summary()
        ])

        this.tableData = listRes.records
        this.total = listRes.total
        this.summaryData = summaryRes
      } catch (error) {
        console.error('获取数据失败:', error)
        this.$message.error('获取数据失败')
      } finally {
        this.tableLoading = false
      }
    },
    // 查询
    handleFilter() {
      this.filterForm.pageNum = 1
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
    // 更改每页显示条数
    handleSizeChange(val) {
      this.filterForm.pageSize = val
      this.fetchData()
    },
    // 更改页码
    handleCurrentChange(val) {
      this.filterForm.pageNum = val
      this.fetchData()
    },
    // 获取状态类型
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
    // 获取状态名称
    getStatusName(status) {
      const nameMap = {
        0: '待生产',
        1: '生产准备中',
        2: '生产中',
        3: '已发货',
        4: '已完成'
      }
      return nameMap[status] || '未知'
    },
    // 获取进度条状态
    getProgressStatus(progress) {
      if (progress === 100) {
        return 'success'
      } else if (progress >= 80) {
        return 'warning'
      } else {
        return undefined
      }
    },
    // 添加日期格式化方法
    formatDateTime(date) {
      return moment(date).format('YYYY-MM-DD HH:mm')
    },
    // 修改进度格式化方法
    percentageFormat(percentage) {
      return (percentage || 0) + '%'
    },
    // 查看详情
    handleDetail(row) {
      this.$router.push({
        name: 'OrderDetail',
        params: { id: row.id }
      })
    },
    // 更新进度
    handleUpdateProgress(row) {
      this.progressForm = {
        id: row.id,
        orderNo: row.orderNo,
        productName: row.productName,
        progress: row.progress,
        newProgress: row.progress,
        description: ''
      }
      this.progressDialogVisible = true
    },
    // 提交进度更新
    async submitProgress() {
      this.submitLoading = true
      try {
        await productionApi.updateProgress(
          this.progressForm.id,
          this.progressForm.newProgress,
          this.progressForm.description
        )
        
        this.$message.success('进度更新成功')
        this.progressDialogVisible = false
        this.fetchData()
      } catch (error) {
        console.error('更新进度失败:', error)
        this.$message.error('更新进度失败')
      } finally {
        this.submitLoading = false
      }
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
.status-card {
  height: 100px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  padding: 20px;
  color: #fff;
}
.status-card.waiting {
  background: #909399;
}
.status-card.processing {
  background: #E6A23C;
}
.status-card.shipped {
  background: #409EFF;
}
.status-card.completed {
  background: #67C23A;
}
.card-icon {
  font-size: 40px;
  margin-right: 20px;
}
.card-content {
  flex: 1;
}
.card-title {
  font-size: 16px;
  margin-bottom: 10px;
}
.card-value {
  font-size: 28px;
  font-weight: bold;
}
</style> 