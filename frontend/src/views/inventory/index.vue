<template>
  <div class="app-container">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="page-title">
            <i class="el-icon-goods"></i>
            库存管理
          </h1>
          <p class="page-subtitle">实时监控商品库存状态，优化库存配置</p>
        </div>
        <div class="header-actions">
          <el-button type="success" icon="el-icon-upload2" @click="handleImport">
            批量导入
          </el-button>
          <el-button type="warning" icon="el-icon-download" @click="handleExport">
            导出数据
          </el-button>
        </div>
      </div>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-container" shadow="hover">
      <div class="filter-header">
        <span class="filter-title">
          <i class="el-icon-search"></i>
          筛选条件
        </span>
      </div>
      <div class="filter-content">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">商品信息</label>
              <el-input 
                v-model="listQuery.productName" 
                placeholder="请输入商品名称或编码" 
                clearable
                prefix-icon="el-icon-goods"
                @keyup.enter.native="handleFilter"
              />
            </div>
          </el-col>
          <el-col :span="5">
            <div class="filter-item">
              <label class="filter-label">所在仓库</label>
              <el-select 
                v-model="listQuery.warehouseId" 
                placeholder="请选择仓库" 
                clearable 
                style="width: 100%"
              >
                <el-option v-for="item in warehouseOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </div>
          </el-col>
          <el-col :span="5">
            <div class="filter-item">
              <label class="filter-label">库存状态</label>
              <el-select 
                v-model="listQuery.status" 
                placeholder="请选择状态" 
                clearable 
                style="width: 100%"
              >
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="filter-actions">
              <el-button type="primary" icon="el-icon-search" @click="handleFilter">
                搜索
              </el-button>
              <el-button icon="el-icon-refresh" @click="handleReset">
                重置
              </el-button>
              <el-button type="info" icon="el-icon-setting" @click="handleAdvancedFilter">
                高级筛选
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 统计概览区域 -->
    <div class="stats-overview">
      <el-row :gutter="20">
        <el-col :span="6" v-for="(item, index) in inventorySummary" :key="index">
          <el-card shadow="hover" class="modern-stat-card" :class="`stat-card-${index + 1}`">
            <div class="stat-card-inner">
              <div class="stat-icon-container">
                <div class="stat-icon-bg" :style="{ background: item.gradient }">
                  <i :class="item.icon" class="stat-icon"></i>
                </div>
              </div>
              <div class="stat-content">
                <div class="stat-number">{{ item.value }}</div>
                <div class="stat-title">{{ item.title }}</div>
                <div class="stat-trend">
                  <i :class="item.trendIcon" :style="{ color: item.trendColor }"></i>
                  <span :style="{ color: item.trendColor }">{{ item.desc }}</span>
                </div>
              </div>
            </div>
            <div class="stat-decoration"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 库存列表 -->
    <el-card class="table-container" shadow="hover">
      <div slot="header" class="table-header">
        <div class="table-title">
          <i class="el-icon-menu"></i>
          <span>库存清单</span>
          <el-tag type="info" size="mini" style="margin-left: 10px;">共 {{ total }} 件商品</el-tag>
        </div>
        <div class="table-tools">
          <el-button-group>
            <el-button size="mini" icon="el-icon-view" @click="toggleView('card')" :type="viewMode === 'card' ? 'primary' : ''">卡片</el-button>
            <el-button size="mini" icon="el-icon-s-grid" @click="toggleView('table')" :type="viewMode === 'table' ? 'primary' : ''">表格</el-button>
          </el-button-group>
        </div>
      </div>
      
      <el-table 
        :data="list" 
        v-loading="listLoading" 
        class="modern-table"
        :header-cell-style="{ background: '#f8f9fa', color: '#495057', fontWeight: '600' }"
        :row-style="{ height: '65px' }"
        stripe      >
        <el-table-column type="selection" width="45" align="center" />
        <el-table-column prop="productCode" label="商品编码" align="center" width="120">
          <template slot-scope="scope">
            <el-tag type="primary" size="small">{{ scope.row.productCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="商品信息" min-width="200" align="left">
          <template slot-scope="scope">
            <div class="product-info-cell">
              <div class="product-avatar">
                <i class="el-icon-goods"></i>
              </div>
              <div class="product-details">
                <div class="product-name">{{ scope.row.productName }}</div>
                <div class="product-meta">
                  <el-tag size="mini" :type="getCategoryColor(scope.row.categoryName)" class="product-category">
                    {{ scope.row.categoryName }}
                  </el-tag>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="warehouseName" label="所在仓库" width="120" align="center">
          <template slot-scope="scope">
            <div class="warehouse-tag">
              <i class="el-icon-office-building"></i>
              {{ scope.row.warehouseName }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="库存数量" width="140" align="center">
          <template slot-scope="scope">
            <div class="quantity-info">
              <div class="quantity-main">
                <span class="quantity-number">{{ scope.row.quantity }}</span>
                <span class="quantity-unit">件</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="预留数量" width="100" align="center">
          <template slot-scope="scope">
            <div class="quantity-info reserved">
              <span class="quantity-number">{{ scope.row.reservedQuantity }}</span>
              <span class="quantity-unit">件</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="可用数量" width="100" align="center">
          <template slot-scope="scope">
            <div class="quantity-info available">
              <span class="quantity-number">{{ scope.row.quantity - scope.row.reservedQuantity }}</span>
              <span class="quantity-unit">件</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="在途库存" width="100" align="center">
          <template slot-scope="scope">
            <div class="quantity-info in-transit">
              <span class="quantity-number">{{ scope.row.inTransitQuantity }}</span>
              <span class="quantity-unit">件</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="最高库存" width="100" align="center">
          <template slot-scope="scope">
            <div class="max-stock">
              <span class="quantity-number">{{ scope.row.maxStockLevel }}</span>
              <span class="quantity-unit">件</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="库存状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag 
              :type="getStockStatusType(scope.row)" 
              size="small"
              class="status-tag"
            >
              <i :class="getStatusIcon(scope.row)"></i>
              {{ getStockStatusLabel(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" width="140" align="center">
          <template slot-scope="scope">
            <div class="time-info">
              <i class="el-icon-time"></i>
              <span>{{ scope.row.lastUpdateTime }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="180" fixed="right">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-button type="text" size="small" icon="el-icon-view" @click="handleDetail(scope.row)">
                详情
              </el-button>
              <el-button type="text" size="small" icon="el-icon-upload2" @click="handleInbound(scope.row)">
                入库
              </el-button>
              <el-button type="text" size="small" icon="el-icon-download" @click="handleOutbound(scope.row)">
                出库
              </el-button>
              <el-dropdown trigger="click">
                <el-button type="text" size="small" icon="el-icon-more"></el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item icon="el-icon-document">查看详情</el-dropdown-item>
                  <el-dropdown-item icon="el-icon-edit-outline">编辑信息</el-dropdown-item>
                  <el-dropdown-item icon="el-icon-download">导出数据</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <div class="pagination-info">
          <span>显示第 {{ (listQuery.page - 1) * listQuery.limit + 1 }} - {{ Math.min(listQuery.page * listQuery.limit, total) }} 条，共 {{ total }} 条记录</span>
        </div>
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="listQuery.page"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="listQuery.limit"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          class="modern-pagination"
        />
      </div>
    </el-card>

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
      viewMode: 'table',
      list: [],
      total: 0,
      listQuery: {
        page: 1,
        limit: 10,
        productName: '',
        warehouseId: '',
        status: ''
      },
      warehouseOptions: [
        { label: '全部仓库', value: 'all' },
        { label: '织里成品仓', value: 'finished' },
        { label: '织里面料仓', value: 'raw' },
        { label: '织里辅料仓', value: 'accessory' },
        { label: '织里婴儿服仓', value: 'baby' },
        { label: '织里出口仓', value: 'export' }
      ],
      statusOptions: [
        { label: '充足', value: 'sufficient' },
        { label: '正常', value: 'normal' },
        { label: '偏低', value: 'low' },
        { label: '紧缺', value: 'shortage' },
        { label: '超量', value: 'excess' }
      ],
      inventorySummary: [
        { title: '总库存商品数', value: '25,360', desc: '较上月增长 12.5%', icon: 'el-icon-goods', gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', trendIcon: 'el-icon-top', trendColor: '#67C23A' },
        { title: '在途库存数', value: '3,865', desc: '较上月增长 8.3%', icon: 'el-icon-truck', gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', trendIcon: 'el-icon-top', trendColor: '#67C23A' },
        { title: '库存预警数', value: '5', desc: '较上周增加 2 个', icon: 'el-icon-warning', gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', trendIcon: 'el-icon-warning', trendColor: '#E6A23C' },
        { title: '库存周转率', value: '4.2次/月', desc: '较上月提高 0.3', icon: 'el-icon-refresh-right', gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)', trendIcon: 'el-icon-top', trendColor: '#67C23A' }
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
  created() {
    this.getList()
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
    handleAdvancedFilter() {
      // 高级筛选功能
      this.$message.info('高级筛选功能开发中...')
    },
    toggleView(mode) {
      this.viewMode = mode
    },
    getCategoryColor(category) {
      const colors = {
        '上衣': 'primary',
        '裤子': 'success',
        '裙装': 'warning',
        '套装': 'info',
        '婴儿装': 'danger',
        '面料': '',
        '辅料': 'info'
      }
      return colors[category] || 'info'
    },
    getStatusIcon(row) {
      const availableQuantity = row.quantity - row.reservedQuantity
      if (availableQuantity <= 0) {
        return 'el-icon-close'
      } else if (availableQuantity < row.minStockLevel) {
        return 'el-icon-warning'
      } else if (row.quantity > row.maxStockLevel) {
        return 'el-icon-info'
      } else {
        return 'el-icon-check'
      }
    },
    getList() {
      this.listLoading = true
      // 模拟接口调用
      setTimeout(() => {
        const now = this.formatDateTime(new Date())
        this.list = [
          { id: 1, productCode: 'P001', productName: '儿童夏季短袖T恤', categoryName: '上衣', imageUrl: 'https://via.placeholder.com/60x60.png?text=T恤', warehouseName: '织里成品仓', quantity: 3200, reservedQuantity: 850, inTransitQuantity: 1200, minStockLevel: 500, maxStockLevel: 5000, lastUpdateTime: now },
          { id: 2, productCode: 'P002', productName: '儿童牛仔裤', categoryName: '裤子', imageUrl: 'https://via.placeholder.com/60x60.png?text=牛仔裤', warehouseName: '织里成品仓', quantity: 2800, reservedQuantity: 920, inTransitQuantity: 850, minStockLevel: 400, maxStockLevel: 4000, lastUpdateTime: now },
          { id: 3, productCode: 'P003', productName: '女童公主连衣裙', categoryName: '裙装', imageUrl: 'https://via.placeholder.com/60x60.png?text=连衣裙', warehouseName: '织里成品仓', quantity: 1850, reservedQuantity: 630, inTransitQuantity: 520, minStockLevel: 400, maxStockLevel: 3000, lastUpdateTime: now },
          { id: 4, productCode: 'P004', productName: '儿童运动套装', categoryName: '套装', imageUrl: 'https://via.placeholder.com/60x60.png?text=运动套装', warehouseName: '织里成品仓', quantity: 2100, reservedQuantity: 780, inTransitQuantity: 650, minStockLevel: 300, maxStockLevel: 3500, lastUpdateTime: now },
          { id: 5, productCode: 'P005', productName: '婴儿连体衣', categoryName: '婴儿装', imageUrl: 'https://via.placeholder.com/60x60.png?text=连体衣', warehouseName: '织里婴儿服仓', quantity: 1600, reservedQuantity: 420, inTransitQuantity: 380, minStockLevel: 200, maxStockLevel: 2500, lastUpdateTime: now },
          { id: 6, productCode: 'P006', productName: '全棉针织面料', categoryName: '面料', imageUrl: 'https://via.placeholder.com/60x60.png?text=面料', warehouseName: '织里面料仓', quantity: 5000, reservedQuantity: 1200, inTransitQuantity: 800, minStockLevel: 1000, maxStockLevel: 8000, lastUpdateTime: now },
          { id: 7, productCode: 'P007', productName: '童装纽扣', categoryName: '辅料', imageUrl: 'https://via.placeholder.com/60x60.png?text=纽扣', warehouseName: '织里辅料仓', quantity: 50000, reservedQuantity: 15000, inTransitQuantity: 10000, minStockLevel: 8000, maxStockLevel: 100000, lastUpdateTime: now },
          { id: 8, productCode: 'P008', productName: '儿童睡衣套装', categoryName: '套装', imageUrl: 'https://via.placeholder.com/60x60.png?text=睡衣套装', warehouseName: '织里成品仓', quantity: 1900, reservedQuantity: 580, inTransitQuantity: 420, minStockLevel: 300, maxStockLevel: 3000, lastUpdateTime: now }
        ]
        this.total = this.list.length
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
/* 页面标题区域 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px 32px;
  margin-bottom: 24px;
  color: white;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title i {
  font-size: 32px;
}

.page-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
  font-weight: 400;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.header-actions .el-button {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.header-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

/* 筛选区域 */
.filter-container {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.filter-header {
  padding: 16px 24px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.filter-title {
  font-size: 16px;
  font-weight: 600;
  color: #495057;
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-content {
  padding: 24px;
}

.filter-item {
  margin-bottom: 16px;
}

.filter-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #495057;
  margin-bottom: 8px;
}

.filter-actions {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  height: 100%;
  padding-top: 24px;
}

/* 统计概览区域 */
.stats-overview {
  margin-bottom: 24px;
}

.modern-stat-card {
  border-radius: 16px;
  border: none;
  overflow: hidden;
  position: relative;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.modern-stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.12);
}

.stat-card-inner {
  display: flex;
  align-items: center;
  padding: 24px;
  position: relative;
  z-index: 2;
}

.stat-icon-container {
  margin-right: 20px;
}

.stat-icon-bg {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.stat-icon {
  font-size: 28px;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
  line-height: 1;
}

.stat-title {
  font-size: 16px;
  color: #6c757d;
  margin-bottom: 8px;
  font-weight: 500;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  font-weight: 500;
}

.stat-decoration {
  position: absolute;
  top: 0;
  right: 0;
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  transform: translate(30px, -30px);
}

/* 表格容器 */
.table-container {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.table-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #495057;
}

.table-tools {
  display: flex;
  gap: 12px;
}

/* 现代化表格 */
.modern-table {
  border-radius: 8px;
  overflow: hidden;
}

.modern-table .el-table__header-wrapper {
  border-radius: 8px 8px 0 0;
}

.modern-table .el-table__body tr:hover {
  background-color: #f8f9ff !important;
}

/* 商品信息单元格 */
.product-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
}

.product-details {
  flex: 1;
}

.product-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
  font-size: 14px;
}

.product-meta {
  display: flex;
  gap: 8px;
}

.product-category {
  font-size: 12px;
}

/* 仓库标签 */
.warehouse-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #495057;
  font-weight: 500;
}

/* 数量信息 */
.quantity-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.quantity-main {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.quantity-number {
  font-size: 16px;
  font-weight: 700;
  color: #2c3e50;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

.quantity-unit {
  font-size: 12px;
  color: #6c757d;
}

.quantity-info.reserved .quantity-number {
  color: #e6a23c;
}

.quantity-info.available .quantity-number {
  color: #67c23a;
}

.quantity-info.in-transit .quantity-number {
  color: #409eff;
}

/* 最高库存 */
.max-stock {
  display: flex;
  align-items: center;
  gap: 4px;
}

.max-stock .quantity-number {
  color: #f56c6c;
}

/* 状态标签 */
.status-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}

/* 时间信息 */
.time-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #6c757d;
  font-size: 13px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.action-buttons .el-button--text {
  padding: 4px 8px;
  font-size: 13px;
  font-weight: 500;
}

/* 分页容器 */
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.pagination-info {
  color: #6c757d;
  font-size: 14px;
}

.modern-pagination {
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .filter-content .el-row {
    flex-direction: column;
  }
  
  .filter-actions {
    justify-content: center;
    padding-top: 16px;
  }
  
  .pagination-container {
    flex-direction: column;
    gap: 16px;
  }
}
</style>