<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.orderNo" placeholder="订单编号" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter" />
      <el-select v-model="listQuery.status" placeholder="订单状态" clearable style="width: 130px" class="filter-item">
        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-date-picker v-model="dateRange" type="daterange" align="right" unlink-panels range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd" class="filter-item date-range" />
      <el-button type="primary" icon="el-icon-search" class="filter-item" @click="handleFilter">搜索</el-button>
      <el-button type="primary" icon="el-icon-plus" class="filter-item" style="margin-left: 10px;" @click="handleCreate">新增</el-button>
    </div>

    <el-table :data="list" border style="width: 100%" v-loading="listLoading">
      <el-table-column prop="orderNo" label="订单编号" align="center" width="180" />
      <el-table-column prop="customerName" label="客户名称" align="center" width="180" />
      <el-table-column prop="totalAmount" label="订单金额" align="center" width="120">
        <template slot-scope="scope">
          ¥ {{ scope.row.totalAmount }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="订单状态" align="center" width="120">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status | statusFilter">
            {{ scope.row.status | statusLabelFilter }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="expectedDeliveryDate" label="预计交付日期" align="center" width="150" />
      <el-table-column prop="createTime" label="创建时间" align="center" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="handleDetail(scope.row)">查看</el-button>
          <el-button size="mini" type="warning" @click="handleEdit(scope.row)" :disabled="scope.row.status === 4 || scope.row.status === 5">编辑</el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.row)" :disabled="scope.row.status !== 0">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="listQuery.page"
      :page-sizes="[10, 20, 30, 50]"
      :page-size="listQuery.limit"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      style="margin-top: 20px; text-align: right;"
    />
  </div>
</template>

<script>
export default {
  name: 'OrderList',
  filters: {
    statusFilter(status) {
      const statusMap = {
        0: 'info',
        1: 'primary',
        2: 'warning',
        3: 'success',
        4: 'success',
        5: 'danger'
      }
      return statusMap[status]
    },
    statusLabelFilter(status) {
      const statusMap = {
        0: '待确认',
        1: '已确认',
        2: '生产中',
        3: '已发货',
        4: '已完成',
        5: '已取消'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      list: [
        { orderNo: 'ORD20230001', customerName: '上海某科技有限公司', totalAmount: 12800.00, status: 0, expectedDeliveryDate: '2023-06-15', createTime: '2023-06-01 10:23:45' },
        { orderNo: 'ORD20230002', customerName: '北京某贸易有限公司', totalAmount: 9680.00, status: 1, expectedDeliveryDate: '2023-06-18', createTime: '2023-06-02 14:12:32' },
        { orderNo: 'ORD20230003', customerName: '广州某电子科技公司', totalAmount: 22560.00, status: 2, expectedDeliveryDate: '2023-06-20', createTime: '2023-06-03 09:45:11' },
        { orderNo: 'ORD20230004', customerName: '深圳某科技有限公司', totalAmount: 15320.00, status: 3, expectedDeliveryDate: '2023-06-12', createTime: '2023-06-04 16:34:27' },
        { orderNo: 'ORD20230005', customerName: '成都某机械有限公司', totalAmount: 31420.00, status: 4, expectedDeliveryDate: '2023-06-08', createTime: '2023-06-05 11:28:56' },
        { orderNo: 'ORD20230006', customerName: '杭州某科技有限公司', totalAmount: 8450.00, status: 5, expectedDeliveryDate: '2023-06-25', createTime: '2023-06-06 08:17:33' }
      ],
      total: 6,
      listLoading: false,
      listQuery: {
        page: 1,
        limit: 10,
        orderNo: '',
        status: '',
        beginDate: '',
        endDate: ''
      },
      dateRange: [],
      statusOptions: [
        { label: '待确认', value: 0 },
        { label: '已确认', value: 1 },
        { label: '生产中', value: 2 },
        { label: '已发货', value: 3 },
        { label: '已完成', value: 4 },
        { label: '已取消', value: 5 }
      ]
    }
  },
  watch: {
    dateRange(val) {
      if (val && val.length === 2) {
        this.listQuery.beginDate = val[0]
        this.listQuery.endDate = val[1]
      } else {
        this.listQuery.beginDate = ''
        this.listQuery.endDate = ''
      }
    }
  },
  methods: {
    handleFilter() {
      this.listQuery.page = 1
      // 实际项目中这里会调用接口获取数据
      this.getList()
    },
    getList() {
      this.listLoading = true
      // 模拟接口调用
      setTimeout(() => {
        this.listLoading = false
      }, 500)
    },
    handleSizeChange(val) {
      this.listQuery.limit = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getList()
    },
    handleCreate() {
      this.$message.success('新增订单功能开发中')
    },
    handleEdit(row) {
      this.$message.success(`编辑订单: ${row.orderNo}`)
    },
    handleDetail(row) {
      this.$message.success(`查看订单详情: ${row.orderNo}`)
    },
    handleDelete(row) {
      this.$confirm('确认删除该订单吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success(`删除订单: ${row.orderNo}`)
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.filter-container {
  padding-bottom: 10px;
}
.filter-item {
  margin-right: 10px;
  margin-bottom: 10px;
}
.date-range {
  width: 350px;
}
</style> 