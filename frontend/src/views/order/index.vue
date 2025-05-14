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
            {{ scope.row.statusName }}
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
      :current-page="listQuery.pageNum"
      :page-sizes="[10, 20, 30, 50]"
      :page-size="listQuery.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      style="margin-top: 20px; text-align: right;"
    />
  </div>
</template>

<script>
import { getOrderList, deleteOrder } from '@/api/order'

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
    }
  },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        orderNo: '',
        status: '',
        startDate: '',
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
  created() {
    this.getList()
  },
  watch: {
    dateRange(val) {
      if (val && val.length === 2) {
        this.listQuery.startDate = val[0]
        this.listQuery.endDate = val[1]
      } else {
        this.listQuery.startDate = ''
        this.listQuery.endDate = ''
      }
    }
  },
  methods: {
    handleFilter() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    getList() {
      this.listLoading = true
      getOrderList(this.listQuery).then(response => {
        this.list = response.data.list
        this.total = response.data.total
        this.listLoading = false
      }).catch(() => {
        this.listLoading = false
      })
    },
    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    },
    handleCreate() {
      this.$router.push({
        name: 'OrderCreate'
      })
    },
    handleEdit(row) {
      this.$router.push({
        name: 'OrderDetail',
        params: { id: row.id }
      })
    },
    handleDetail(row) {
      this.$router.push({
        name: 'OrderDetail',
        params: { id: row.id }
      })
    },
    handleDelete(row) {
      this.$confirm('确认删除该订单吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteOrder(row.id).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
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