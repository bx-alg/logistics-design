<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.shipmentNo" placeholder="运输单号" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter" />
      <el-select v-model="listQuery.status" placeholder="运输状态" clearable style="width: 130px" class="filter-item">
        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-select v-model="listQuery.carrierId" placeholder="承运商" clearable style="width: 150px" class="filter-item">
        <el-option v-for="item in carrierOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-date-picker v-model="dateRange" type="daterange" align="right" unlink-panels range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd" class="filter-item date-range" />
      <el-button type="primary" icon="el-icon-search" class="filter-item" @click="handleFilter">搜索</el-button>
      <el-button type="primary" icon="el-icon-plus" class="filter-item" style="margin-left: 10px;" @click="handleCreate">新增</el-button>
    </div>

    <el-table :data="list" border style="width: 100%" v-loading="listLoading">
      <el-table-column prop="shipmentNo" label="运输单号" align="center" width="150" />
      <el-table-column prop="orderNo" label="关联订单" align="center" width="150" />
      <el-table-column prop="carrierName" label="承运商" align="center" width="150" />
      <el-table-column prop="fromWarehouse" label="发货仓库" align="center" width="120" />
      <el-table-column prop="estimatedArrivalTime" label="预计到达时间" align="center" width="170" />
      <el-table-column prop="status" label="运输状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status | statusFilter">
            {{ scope.row.status | statusLabelFilter }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="运输进度" align="center" width="180">
        <template slot-scope="scope">
          <el-progress :percentage="scope.row.progress" :status="scope.row.progress === 100 ? 'success' : ''"></el-progress>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" align="center" width="170" />
      <el-table-column label="操作" align="center" fixed="right" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="success" @click="handleTrack(scope.row)">追踪</el-button>
          <el-button size="mini" type="primary" @click="handleDetail(scope.row)">详情</el-button>
          <el-button size="mini" type="warning" @click="handleUpdate(scope.row)" :disabled="scope.row.status === 3 || scope.row.status === 4">更新</el-button>
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

    <!-- 追踪对话框 -->
    <el-dialog title="物流追踪详情" :visible.sync="trackDialogVisible" width="600px">
      <el-steps direction="vertical" :active="activeTrackStep">
        <el-step v-for="(track, index) in trackInfo" :key="index" :title="track.title" :description="track.description">
          <template slot="icon">
            <i :class="track.icon"></i>
          </template>
        </el-step>
      </el-steps>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ShipmentList',
  filters: {
    statusFilter(status) {
      const statusMap = {
        0: 'info',
        1: 'primary',
        2: 'warning',
        3: 'success',
        4: 'danger'
      }
      return statusMap[status]
    },
    statusLabelFilter(status) {
      const statusMap = {
        0: '待发货',
        1: '已发货',
        2: '运输中',
        3: '已送达',
        4: '已取消'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      list: [
        { shipmentNo: 'SHP20230001', orderNo: 'ORD20230001', carrierName: '顺丰速运', fromWarehouse: '上海仓', estimatedArrivalTime: '2023-06-15 15:00:00', status: 0, progress: 0, createTime: '2023-06-01 10:23:45' },
        { shipmentNo: 'SHP20230002', orderNo: 'ORD20230002', carrierName: '京东物流', fromWarehouse: '北京仓', estimatedArrivalTime: '2023-06-12 10:30:00', status: 1, progress: 25, createTime: '2023-06-02 14:12:32' },
        { shipmentNo: 'SHP20230003', orderNo: 'ORD20230003', carrierName: '中通快递', fromWarehouse: '广州仓', estimatedArrivalTime: '2023-06-13 16:45:00', status: 2, progress: 65, createTime: '2023-06-03 09:45:11' },
        { shipmentNo: 'SHP20230004', orderNo: 'ORD20230004', carrierName: '德邦物流', fromWarehouse: '深圳仓', estimatedArrivalTime: '2023-06-10 09:15:00', status: 2, progress: 78, createTime: '2023-06-04 16:34:27' },
        { shipmentNo: 'SHP20230005', orderNo: 'ORD20230005', carrierName: '顺丰速运', fromWarehouse: '成都仓', estimatedArrivalTime: '2023-06-07 14:00:00', status: 3, progress: 100, createTime: '2023-06-05 11:28:56' },
        { shipmentNo: 'SHP20230006', orderNo: 'ORD20230006', carrierName: '圆通速递', fromWarehouse: '杭州仓', estimatedArrivalTime: '2023-06-25 18:30:00', status: 4, progress: 0, createTime: '2023-06-06 08:17:33' }
      ],
      total: 6,
      listLoading: false,
      listQuery: {
        page: 1,
        limit: 10,
        shipmentNo: '',
        status: '',
        carrierId: '',
        beginDate: '',
        endDate: ''
      },
      dateRange: [],
      statusOptions: [
        { label: '待发货', value: 0 },
        { label: '已发货', value: 1 },
        { label: '运输中', value: 2 },
        { label: '已送达', value: 3 },
        { label: '已取消', value: 4 }
      ],
      carrierOptions: [
        { label: '顺丰速运', value: 1 },
        { label: '京东物流', value: 2 },
        { label: '中通快递', value: 3 },
        { label: '德邦物流', value: 4 },
        { label: '圆通速递', value: 5 }
      ],
      trackDialogVisible: false,
      activeTrackStep: 3,
      trackInfo: [
        { 
          title: '2023-06-05 18:30:45', 
          description: '您的快件已签收，签收人：张三，感谢您使用顺丰速运，期待再次为您服务',
          icon: 'el-icon-check'
        },
        { 
          title: '2023-06-05 10:15:32', 
          description: '快件已到达北京朝阳区望京营业点，快递员刘师傅（13812345678）正在为您派送',
          icon: 'el-icon-position'
        },
        { 
          title: '2023-06-04 23:45:21', 
          description: '快件已到达北京转运中心',
          icon: 'el-icon-truck'
        },
        { 
          title: '2023-06-03 16:28:10', 
          description: '快件已从上海仓库发出',
          icon: 'el-icon-box'
        },
        { 
          title: '2023-06-03 14:52:36', 
          description: '顺丰速运已收取快件',
          icon: 'el-icon-takeaway-box'
        },
        { 
          title: '2023-06-03 10:25:09', 
          description: '快件信息已生成，等待揽收',
          icon: 'el-icon-receiving'
        }
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
      this.$message.success('新增运输单功能开发中')
    },
    handleUpdate(row) {
      this.$message.success(`更新运输单状态: ${row.shipmentNo}`)
    },
    handleDetail(row) {
      this.$message.success(`查看运输单详情: ${row.shipmentNo}`)
    },
    handleTrack(row) {
      this.trackDialogVisible = true
      // 实际项目中这里会根据运输单号调用接口获取物流轨迹
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