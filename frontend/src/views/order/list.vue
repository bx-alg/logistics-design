<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>订单管理</span>
      </div>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="订单编号" prop="orderNo">
          <el-input
            v-model="queryParams.orderNo"
            placeholder="请输入订单编号"
            clearable
            style="width: 200px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        
        <el-form-item label="客户" prop="customerName">
          <el-input
            v-model="queryParams.customerName"
            placeholder="请输入客户名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        
        <el-form-item label="订单状态" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择订单状态"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="(label, value) in statusOptions"
              :key="value"
              :label="label"
              :value="Number(value)"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="queryParams.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 操作按钮区域 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            icon="el-icon-plus"
            @click="handleAdd"
          >新增订单</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            icon="el-icon-download"
            @click="handleExport"
          >导出</el-button>
        </el-col>
      </el-row>
      
      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
        @row-click="handleDetail"
      >
        <el-table-column type="index" label="序号" width="50" align="center" />
        <el-table-column prop="orderNo" label="订单编号" width="160" />
        <el-table-column prop="customerName" label="客户名称" min-width="180" :show-overflow-tooltip="true" />
        <el-table-column prop="totalAmount" label="订单金额" width="100">
          <template slot-scope="scope">
            {{ scope.row.totalAmount | money }}
          </template>
        </el-table-column>
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="120" />
        <el-table-column prop="status" label="订单状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ formatStatus(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expectedDeliveryDate" label="预计交付日期" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click.stop="handleDetail(scope.row)"
            >查看</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click.stop="handleUpdate(scope.row)"
              v-if="scope.row.status === 0"
            >修改</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click.stop="handleDelete(scope.row)"
              v-if="scope.row.status === 0"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页区域 -->
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
  </div>
</template>

<script>
import { useOrderStore } from '@/stores/order'
import { getConstants } from '@/utils/mockUtils'
import moment from 'moment'

export default {
  name: 'OrderList',
  components: {
    Pagination: () => import('@/components/Pagination')
  },
  filters: {
    money(val) {
      if (!val) return '0.00'
      return Number(val).toFixed(2)
    }
  },
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
      total: 0
    }
  },
  computed: {
    orderStore() {
      return useOrderStore()
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询订单列表 */
    async getList() {
      this.loading = true
      try {
        const res = await this.orderStore.fetchOrders({
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
        this.$message.error('获取订单列表失败')
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

    /** 新建按钮操作 */
    handleAdd() {
      this.$router.push('/order/create')
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.$router.push(`/order/edit/${row.id}`)
    },

    /** 删除按钮操作 */
    async handleDelete(row) {
      try {
        await this.$confirm('确认要删除该订单吗？', '提示', {
          type: 'warning'
        })
        await this.orderStore.deleteOrder(row.id)
        this.$message.success('删除成功')
        this.getList()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除订单失败')
        }
      }
    },

    /** 查看详情 */
    handleDetail(row) {
      this.$router.push(`/order/detail/${row.id}`)
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
      return moment(date).format('YYYY-MM-DD HH:mm:ss')
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

    /** 导出按钮操作 */
    handleExport() {
      this.$confirm('是否确认导出所有订单数据?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('导出成功')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}
</style> 