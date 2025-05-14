<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.productName" placeholder="商品名称/编码" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter" />
      <el-select v-model="listQuery.warehouseId" placeholder="仓库" clearable style="width: 150px" class="filter-item">
        <el-option v-for="item in warehouseOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-select v-model="listQuery.status" placeholder="库存状态" clearable style="width: 130px" class="filter-item">
        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" icon="el-icon-search" class="filter-item" @click="handleFilter">搜索</el-button>
      <el-button type="primary" icon="el-icon-refresh" class="filter-item" @click="handleReset">重置</el-button>
      <el-button type="success" icon="el-icon-upload2" class="filter-item" style="margin-left: 10px;" @click="handleImport">批量导入</el-button>
      <el-button type="warning" icon="el-icon-download" class="filter-item" @click="handleExport">导出</el-button>
    </div>

    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6" v-for="(item, index) in inventorySummary" :key="index">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" :style="{ backgroundColor: item.bgColor }">
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

    <el-table :data="list" border style="width: 100%" v-loading="listLoading">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="productCode" label="商品编码" align="center" width="130" />
      <el-table-column label="商品信息" align="center" min-width="200">
        <template slot-scope="scope">
          <div class="product-info">
            <el-avatar shape="square" size="medium" :src="scope.row.imageUrl" class="product-image"></el-avatar>
            <div class="product-detail">
              <div class="product-name">{{ scope.row.productName }}</div>
              <div class="product-category">{{ scope.row.categoryName }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="warehouseName" label="所在仓库" align="center" width="130" />
      <el-table-column prop="quantity" label="库存总量" align="center" width="100" sortable />
      <el-table-column prop="reservedQuantity" label="预留数量" align="center" width="100" sortable />
      <el-table-column prop="availableQuantity" label="可用库存" align="center" width="100" sortable>
        <template slot-scope="scope">
          <span>{{ scope.row.quantity - scope.row.reservedQuantity }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="inTransitQuantity" label="在途库存" align="center" width="100" sortable />
      <el-table-column prop="minStockLevel" label="最低库存" align="center" width="100" />
      <el-table-column prop="maxStockLevel" label="最高库存" align="center" width="100" />
      <el-table-column label="库存状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStockStatusType(scope.row)">
            {{ getStockStatusLabel(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastUpdateTime" label="最后更新时间" align="center" width="170" sortable />
      <el-table-column label="操作" align="center" fixed="right" width="230">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="handleDetail(scope.row)">详情</el-button>
          <el-button size="mini" type="success" @click="handleInbound(scope.row)">入库</el-button>
          <el-button size="mini" type="warning" @click="handleOutbound(scope.row)">出库</el-button>
          <el-button size="mini" type="info" @click="handleHistory(scope.row)">历史</el-button>
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

    <!-- 入库对话框 -->
    <el-dialog title="商品入库" :visible.sync="inboundDialogVisible" width="500px">
      <el-form :model="inboundForm" label-width="100px">
        <el-form-item label="商品名称">
          <span>{{ inboundForm.productName }}</span>
        </el-form-item>
        <el-form-item label="所在仓库">
          <span>{{ inboundForm.warehouseName }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ inboundForm.quantity }}</span>
        </el-form-item>
        <el-form-item label="入库数量" prop="inboundQuantity">
          <el-input-number v-model="inboundForm.inboundQuantity" :min="1" :step="1" step-strictly></el-input-number>
        </el-form-item>
        <el-form-item label="入库类型">
          <el-select v-model="inboundForm.inboundType" placeholder="请选择入库类型" style="width: 100%">
            <el-option label="采购入库" value="purchase"></el-option>
            <el-option label="生产入库" value="production"></el-option>
            <el-option label="退货入库" value="return"></el-option>
            <el-option label="调拨入库" value="transfer"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="入库备注">
          <el-input type="textarea" v-model="inboundForm.remark" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="inboundDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitInbound">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 出库对话框 -->
    <el-dialog title="商品出库" :visible.sync="outboundDialogVisible" width="500px">
      <el-form :model="outboundForm" label-width="100px">
        <el-form-item label="商品名称">
          <span>{{ outboundForm.productName }}</span>
        </el-form-item>
        <el-form-item label="所在仓库">
          <span>{{ outboundForm.warehouseName }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ outboundForm.quantity }}</span>
        </el-form-item>
        <el-form-item label="可用库存">
          <span>{{ outboundForm.quantity - outboundForm.reservedQuantity }}</span>
        </el-form-item>
        <el-form-item label="出库数量" prop="outboundQuantity">
          <el-input-number v-model="outboundForm.outboundQuantity" :min="1" :max="outboundForm.quantity - outboundForm.reservedQuantity" :step="1" step-strictly></el-input-number>
        </el-form-item>
        <el-form-item label="出库类型">
          <el-select v-model="outboundForm.outboundType" placeholder="请选择出库类型" style="width: 100%">
            <el-option label="销售出库" value="sales"></el-option>
            <el-option label="生产领料" value="material"></el-option>
            <el-option label="报废出库" value="scrap"></el-option>
            <el-option label="调拨出库" value="transfer"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出库备注">
          <el-input type="textarea" v-model="outboundForm.remark" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="outboundDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitOutbound">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'InventoryList',
  data() {
    return {
      listLoading: false,
      list: [
        { id: 1, productCode: 'P001', productName: '智能手机A', categoryName: '电子产品', imageUrl: 'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png', warehouseName: '上海仓', quantity: 200, reservedQuantity: 50, inTransitQuantity: 100, minStockLevel: 50, maxStockLevel: 500, lastUpdateTime: '2023-06-02 14:23:45' },
        { id: 2, productCode: 'P002', productName: '笔记本电脑B', categoryName: '电子产品', imageUrl: 'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png', warehouseName: '北京仓', quantity: 30, reservedQuantity: 20, inTransitQuantity: 50, minStockLevel: 40, maxStockLevel: 200, lastUpdateTime: '2023-06-03 09:56:12' },
        { id: 3, productCode: 'P003', productName: '智能音箱C', categoryName: '电子产品', imageUrl: 'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png', warehouseName: '广州仓', quantity: 150, reservedQuantity: 30, inTransitQuantity: 20, minStockLevel: 40, maxStockLevel: 300, lastUpdateTime: '2023-06-04 15:37:28' },
        { id: 4, productCode: 'P004', productName: '平板电脑D', categoryName: '电子产品', imageUrl: 'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png', warehouseName: '深圳仓', quantity: 45, reservedQuantity: 40, inTransitQuantity: 30, minStockLevel: 50, maxStockLevel: 200, lastUpdateTime: '2023-06-02 11:08:33' },
        { id: 5, productCode: 'P005', productName: '智能手表E', categoryName: '电子产品', imageUrl: 'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png', warehouseName: '上海仓', quantity: 85, reservedQuantity: 25, inTransitQuantity: 15, minStockLevel: 30, maxStockLevel: 150, lastUpdateTime: '2023-06-01 16:45:19' },
        { id: 6, productCode: 'P006', productName: '智能手环F', categoryName: '电子产品', imageUrl: 'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png', warehouseName: '北京仓', quantity: 280, reservedQuantity: 80, inTransitQuantity: 50, minStockLevel: 100, maxStockLevel: 500, lastUpdateTime: '2023-06-03 10:21:57' }
      ],
      total: 6,
      listQuery: {
        page: 1,
        limit: 10,
        productName: '',
        warehouseId: '',
        status: ''
      },
      warehouseOptions: [
        { label: '上海仓', value: 1 },
        { label: '北京仓', value: 2 },
        { label: '广州仓', value: 3 },
        { label: '深圳仓', value: 4 }
      ],
      statusOptions: [
        { label: '充足', value: 'sufficient' },
        { label: '正常', value: 'normal' },
        { label: '偏低', value: 'low' },
        { label: '紧缺', value: 'shortage' },
        { label: '超量', value: 'excess' }
      ],
      inventorySummary: [
        { title: '总库存商品数', value: '6,280', desc: '较上月增长 5.2%', icon: 'el-icon-goods', bgColor: '#ecf5ff' },
        { title: '在途库存数', value: '265', desc: '较上月下降 2.3%', icon: 'el-icon-truck', bgColor: '#f0f9eb' },
        { title: '库存预警数', value: '2', desc: '较上周减少 3 个', icon: 'el-icon-warning', bgColor: '#fdf6ec' },
        { title: '库存周转率', value: '4.2次/月', desc: '较上月提高 0.3', icon: 'el-icon-refresh-right', bgColor: '#fef0f0' }
      ],
      inboundDialogVisible: false,
      outboundDialogVisible: false,
      inboundForm: {
        id: null,
        productName: '',
        warehouseName: '',
        quantity: 0,
        inboundQuantity: 1,
        inboundType: 'purchase',
        remark: ''
      },
      outboundForm: {
        id: null,
        productName: '',
        warehouseName: '',
        quantity: 0,
        reservedQuantity: 0,
        outboundQuantity: 1,
        outboundType: 'sales',
        remark: ''
      }
    }
  },
  methods: {
    handleFilter() {
      this.listQuery.page = 1
      // 实际项目中这里会调用接口获取数据
      this.getList()
    },
    handleReset() {
      this.listQuery = {
        page: 1,
        limit: 10,
        productName: '',
        warehouseId: '',
        status: ''
      }
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
    handleDetail(row) {
      this.$message.success(`查看商品库存详情: ${row.productName}`)
    },
    handleInbound(row) {
      this.inboundForm = {
        id: row.id,
        productName: row.productName,
        warehouseName: row.warehouseName,
        quantity: row.quantity,
        inboundQuantity: 1,
        inboundType: 'purchase',
        remark: ''
      }
      this.inboundDialogVisible = true
    },
    handleOutbound(row) {
      this.outboundForm = {
        id: row.id,
        productName: row.productName,
        warehouseName: row.warehouseName,
        quantity: row.quantity,
        reservedQuantity: row.reservedQuantity,
        outboundQuantity: 1,
        outboundType: 'sales',
        remark: ''
      }
      this.outboundDialogVisible = true
    },
    handleHistory(row) {
      this.$message.success(`查看库存变动历史: ${row.productName}`)
    },
    handleImport() {
      this.$message.success('批量导入功能开发中')
    },
    handleExport() {
      this.$message.success('导出功能开发中')
    },
    submitInbound() {
      // 实际项目中这里会调用接口进行入库操作
      this.$message.success(`商品 ${this.inboundForm.productName} 已成功入库 ${this.inboundForm.inboundQuantity} 件`)
      this.inboundDialogVisible = false
      // 更新本地数据模拟
      const index = this.list.findIndex(item => item.id === this.inboundForm.id)
      if (index !== -1) {
        this.list[index].quantity += this.inboundForm.inboundQuantity
        this.list[index].lastUpdateTime = this.formatDateTime(new Date())
      }
    },
    submitOutbound() {
      // 检查是否有足够库存
      if (this.outboundForm.outboundQuantity > (this.outboundForm.quantity - this.outboundForm.reservedQuantity)) {
        this.$message.error('可用库存不足')
        return
      }
      // 实际项目中这里会调用接口进行出库操作
      this.$message.success(`商品 ${this.outboundForm.productName} 已成功出库 ${this.outboundForm.outboundQuantity} 件`)
      this.outboundDialogVisible = false
      // 更新本地数据模拟
      const index = this.list.findIndex(item => item.id === this.outboundForm.id)
      if (index !== -1) {
        this.list[index].quantity -= this.outboundForm.outboundQuantity
        this.list[index].lastUpdateTime = this.formatDateTime(new Date())
      }
    },
    getStockStatusType(row) {
      const availableQuantity = row.quantity - row.reservedQuantity
      if (availableQuantity <= 0) {
        return 'danger'
      } else if (availableQuantity < row.minStockLevel) {
        return 'warning'
      } else if (row.quantity > row.maxStockLevel) {
        return 'info'
      } else if (availableQuantity >= row.minStockLevel && availableQuantity <= row.minStockLevel * 1.5) {
        return ''
      } else {
        return 'success'
      }
    },
    getStockStatusLabel(row) {
      const availableQuantity = row.quantity - row.reservedQuantity
      if (availableQuantity <= 0) {
        return '库存告急'
      } else if (availableQuantity < row.minStockLevel) {
        return '库存偏低'
      } else if (row.quantity > row.maxStockLevel) {
        return '库存超量'
      } else if (availableQuantity >= row.minStockLevel && availableQuantity <= row.minStockLevel * 1.5) {
        return '库存正常'
      } else {
        return '库存充足'
      }
    },
    formatDateTime(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
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
.stat-card {
  display: flex;
  height: 100px;
  margin-bottom: 10px;
}
.stat-icon {
  width: 80px;
  height: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 8px;
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
.product-info {
  display: flex;
  align-items: center;
}
.product-image {
  margin-right: 10px;
}
.product-detail {
  text-align: left;
}
.product-name {
  font-weight: bold;
  margin-bottom: 5px;
}
.product-category {
  font-size: 12px;
  color: #909399;
}
</style> 