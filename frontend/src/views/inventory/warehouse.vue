<template>
  <div class="app-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <i class="el-icon-office-building"></i>
        仓库管理
      </h2>
      <p class="page-subtitle">管理和监控所有仓库信息</p>
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
              <label class="filter-label">仓库名称</label>
              <el-input 
                v-model="filterForm.warehouseName" 
                placeholder="请输入仓库名称" 
                clearable 
                prefix-icon="el-icon-office-building"
              />
            </div>
          </el-col>
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">仓库编号</label>
              <el-input 
                v-model="filterForm.warehouseCode" 
                placeholder="请输入仓库编号" 
                clearable 
                prefix-icon="el-icon-postcard"
              />
            </div>
          </el-col>
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">仓库类型</label>
              <el-select 
                v-model="filterForm.warehouseType" 
                placeholder="请选择类型" 
                clearable 
                style="width: 100%"
              >
                <el-option
                  v-for="item in warehouseTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="filter-actions">
              <el-button type="primary" icon="el-icon-search" @click="handleFilter">
                查询
              </el-button>
              <el-button icon="el-icon-refresh" @click="resetFilter">
                重置
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
    
    <!-- 统计概览 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6" v-for="(item, index) in warehouseSummary" :key="index">
          <el-card shadow="hover" class="stat-card" :class="`stat-card-${index + 1}`">
            <div class="stat-card-content">
              <div class="stat-icon-wrapper">
                <div class="stat-icon" :style="{ backgroundColor: item.bgColor }">
                  <i :class="item.icon"></i>
                </div>
              </div>
              <div class="stat-info">
                <div class="stat-title">{{ item.title }}</div>
                <div class="stat-value">{{ item.value }}</div>
                <div class="stat-desc">
                  <i class="el-icon-trend-charts"></i>
                  {{ item.desc }}
                </div>
              </div>
            </div>
            <div class="stat-card-bg"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 仓库列表 -->
    <el-card class="table-container" shadow="hover">
      <div slot="header" class="table-header">
        <div class="table-title">
          <i class="el-icon-menu"></i>
          <span>仓库列表</span>
          <el-tag type="info" size="mini" style="margin-left: 10px;">共 {{ total }} 个仓库</el-tag>
        </div>
        <div class="table-actions">
          <el-button type="primary" icon="el-icon-plus" @click="handleAdd">
            新增仓库
          </el-button>
          <el-button icon="el-icon-download" @click="handleExport">
            导出数据
          </el-button>
        </div>
      </div>
      <el-table
        v-loading="tableLoading"
        :data="tableData"
        stripe
        class="warehouse-table"
        :header-cell-style="{ background: '#f8f9fa', color: '#606266', fontWeight: '600' }"
        :row-style="{ height: '60px' }"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="warehouseCode" label="仓库编号" align="center" width="120">
          <template slot-scope="scope">
            <el-tag type="primary" size="small">{{ scope.row.warehouseCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="仓库信息" min-width="260">
          <template slot-scope="scope">
            <div class="warehouse-info-cell">
              <div class="warehouse-avatar">
                <i class="el-icon-office-building"></i>
              </div>
              <div class="warehouse-details">
                <div class="warehouse-name">{{ scope.row.warehouseName }}</div>
                <div class="warehouse-meta">
                  <el-tag size="mini" :type="getWarehouseTypeColor(scope.row.warehouseType)">
                    {{ getWarehouseTypeLabel(scope.row.warehouseType) }}
                  </el-tag>
                  <span class="warehouse-location">
                    <i class="el-icon-location-outline"></i>
                    {{ scope.row.location }}
                  </span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="规模信息" width="180" align="center">
          <template slot-scope="scope">
            <div class="warehouse-scale">
              <div class="scale-item">
                <span class="scale-label">面积:</span>
                <span class="scale-value">{{ scope.row.area }} ㎡</span>
              </div>
              <div class="scale-item">
                <span class="scale-label">容量:</span>
                <span class="scale-value">{{ scope.row.capacity }} m³</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="utilization" label="使用率" width="140" align="center">
          <template slot-scope="scope">
            <div class="utilization-wrapper">
              <el-progress 
                :percentage="scope.row.utilization" 
                :colors="progressColors"
                :stroke-width="8"
                :show-text="false"
              ></el-progress>
              <span class="utilization-text">{{ scope.row.utilization }}%</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="负责人" width="140" align="center">
          <template slot-scope="scope">
            <div class="manager-info">
              <div class="manager-name">{{ scope.row.manager }}</div>
              <div class="manager-phone">{{ scope.row.contactPhone }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="120" align="center">
          <template slot-scope="scope">
            <div class="create-time">
              <i class="el-icon-time"></i>
              {{ scope.row.createTime }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-button size="mini" type="primary" icon="el-icon-view" @click="handleView(scope.row)">
                查看
              </el-button>
              <el-button size="mini" type="success" icon="el-icon-edit" @click="handleEdit(scope.row)">
                编辑
              </el-button>
              <el-button size="mini" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)">
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
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

    <!-- 新增/编辑仓库对话框 -->
    <el-dialog :title="dialog.title" :visible.sync="dialog.visible" width="650px">
      <el-form :model="form" ref="warehouseForm" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仓库名称" prop="warehouseName">
              <el-input v-model="form.warehouseName" placeholder="请输入仓库名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库编号" prop="warehouseCode">
              <el-input v-model="form.warehouseCode" placeholder="请输入仓库编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仓库类型" prop="warehouseType">
              <el-select v-model="form.warehouseType" placeholder="请选择仓库类型" style="width: 100%">
                <el-option v-for="item in warehouseTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库面积" prop="area">
              <el-input-number v-model="form.area" :min="1" :precision="2" :step="10" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仓库容量" prop="capacity">
              <el-input-number v-model="form.capacity" :min="1" :precision="2" :step="10" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用率" prop="utilization">
              <el-input-number v-model="form.utilization" :min="0" :max="100" :precision="0" :step="5" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人" prop="manager">
              <el-input v-model="form.manager" placeholder="请输入负责人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="仓库位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入仓库详细地址" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="form.remark" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialog.visible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'WarehouseList',
  data() {
    return {
      filterForm: {
        warehouseName: '',
        warehouseCode: '',
        warehouseType: '',
        pageNum: 1,
        pageSize: 10
      },
      warehouseTypeOptions: [
        { value: 'raw', label: '面料仓' },
        { value: 'finished', label: '成品仓' },
        { value: 'accessory', label: '辅料仓' },
        { value: 'export', label: '出口仓' },
        { value: 'return', label: '退货仓' }
      ],
      warehouseSummary: [
        { title: '仓库总数', value: '8', desc: '较上月增加 1 个', icon: 'el-icon-office-building', bgColor: '#ecf5ff' },
        { title: '总面积', value: '12,500 ㎡', desc: '较上月增加 1,500 ㎡', icon: 'el-icon-s-grid', bgColor: '#f0f9eb' },
        { title: '平均利用率', value: '68.5%', desc: '较上月提升 3.2%', icon: 'el-icon-data-line', bgColor: '#fdf6ec' },
        { title: '库存周转率', value: '4.8次/月', desc: '较上月提高 0.3', icon: 'el-icon-refresh', bgColor: '#fef0f0' }
      ],
      tableLoading: false,
      submitLoading: false,
      tableData: [
        { id: 1, warehouseCode: 'WH001', warehouseName: '织里童装成品仓', warehouseType: 'finished', location: '浙江省湖州市织里镇童装大道168号', area: 3500, capacity: 12000, utilization: 75, manager: '张仓', contactPhone: '13912345678', createTime: '2022-03-15 10:30:45' },
        { id: 2, warehouseCode: 'WH002', warehouseName: '织里面料仓', warehouseType: 'raw', location: '浙江省湖州市织里镇织造路89号', area: 2800, capacity: 9500, utilization: 82, manager: '李材', contactPhone: '13812345679', createTime: '2022-04-20 14:22:33' },
        { id: 3, warehouseCode: 'WH003', warehouseName: '织里童装辅料仓', warehouseType: 'accessory', location: '浙江省湖州市织里镇童装产业园23号', area: 1200, capacity: 4000, utilization: 65, manager: '王辅', contactPhone: '13712345670', createTime: '2022-05-18 09:15:28' },
        { id: 4, warehouseCode: 'WH004', warehouseName: '织里童装出口仓', warehouseType: 'export', location: '浙江省湖州市织里镇国际贸易园区56号', area: 2200, capacity: 7500, utilization: 58, manager: '赵出', contactPhone: '13612345671', createTime: '2022-06-10 16:40:12' },
        { id: 5, warehouseCode: 'WH005', warehouseName: '织里童装退货仓', warehouseType: 'return', location: '浙江省湖州市织里镇物流中心12号', area: 800, capacity: 2500, utilization: 45, manager: '钱退', contactPhone: '13512345672', createTime: '2022-07-22 11:05:39' },
        { id: 6, warehouseCode: 'WH006', warehouseName: '织里婴儿服成品仓', warehouseType: 'finished', location: '浙江省湖州市织里镇童装大道195号', area: 1800, capacity: 6000, utilization: 72, manager: '孙婴', contactPhone: '13412345673', createTime: '2022-09-05 13:25:47' },
        { id: 7, warehouseCode: 'WH007', warehouseName: '织里童装包材仓', warehouseType: 'accessory', location: '浙江省湖州市织里镇童装产业园45号', area: 900, capacity: 3000, utilization: 85, manager: '周包', contactPhone: '13312345674', createTime: '2023-01-12 10:18:23' },
        { id: 8, warehouseCode: 'WH008', warehouseName: '织里童装样品仓', warehouseType: 'finished', location: '浙江省湖州市织里镇设计中心8号', area: 300, capacity: 800, utilization: 48, manager: '吴样', contactPhone: '13212345675', createTime: '2023-02-28 15:50:36' }
      ],
      total: 8,
      dialog: {
        visible: false,
        title: ''
      },
      form: {
        id: undefined,
        warehouseCode: '',
        warehouseName: '',
        warehouseType: '',
        location: '',
        area: 0,
        capacity: 0,
        utilization: 0,
        manager: '',
        contactPhone: '',
        imageUrl: '',
        remark: ''
      },
      rules: {
        warehouseName: [
          { required: true, message: '请输入仓库名称', trigger: 'blur' }
        ],
        warehouseCode: [
          { required: true, message: '请输入仓库编号', trigger: 'blur' }
        ],
        warehouseType: [
          { required: true, message: '请选择仓库类型', trigger: 'change' }
        ],
        location: [
          { required: true, message: '请输入仓库位置', trigger: 'blur' }
        ],
        manager: [
          { required: true, message: '请输入负责人姓名', trigger: 'blur' }
        ],
        contactPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      },
      progressColors: [
        { color: '#f56c6c', percentage: 20 },
        { color: '#e6a23c', percentage: 40 },
        { color: '#5cb87a', percentage: 60 },
        { color: '#1989fa', percentage: 80 },
        { color: '#6f7ad3', percentage: 100 }
      ]
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 获取仓库列表
    getList() {
      this.tableLoading = true
      // 模拟接口调用
      setTimeout(() => {
        const { warehouseName, warehouseCode, warehouseType } = this.filterForm
        let filteredData = [...this.tableData]
        
        if (warehouseName) {
          filteredData = filteredData.filter(item => item.warehouseName.includes(warehouseName))
        }
        
        if (warehouseCode) {
          filteredData = filteredData.filter(item => item.warehouseCode.includes(warehouseCode))
        }
        
        if (warehouseType) {
          filteredData = filteredData.filter(item => item.warehouseType === warehouseType)
        }
        
        this.tableData = filteredData
        this.total = filteredData.length
        this.tableLoading = false
      }, 500)
    },
    
    // 查询
    handleFilter() {
      this.filterForm.pageNum = 1
      this.getList()
    },
    
    // 重置筛选条件
    resetFilter() {
      this.filterForm = {
        warehouseName: '',
        warehouseCode: '',
        warehouseType: '',
        pageNum: 1,
        pageSize: 10
      }
      this.getList()
    },
    
    // 分页大小改变
    handleSizeChange(val) {
      this.filterForm.pageSize = val
      this.getList()
    },
    
    // 分页页码改变
    handleCurrentChange(val) {
      this.filterForm.pageNum = val
      this.getList()
    },
    
    // 获取仓库类型标签
    getWarehouseTypeLabel(type) {
      const option = this.warehouseTypeOptions.find(item => item.value === type)
      return option ? option.label : '未知类型'
    },
    
    // 获取仓库类型颜色
    getWarehouseTypeColor(type) {
      const colorMap = {
        'raw': 'warning',
        'finished': 'success', 
        'accessory': 'info',
        'export': 'primary',
        'return': 'danger'
      }
      return colorMap[type] || 'info'
    },
    
    // 导出数据
    handleExport() {
      this.$message.info('导出功能开发中...')
    },
    
    // 查看仓库详情
    handleView(row) {
      this.$router.push({
        name: 'WarehouseDetail',
        params: { id: row.id }
      })
    },
    
    // 新增仓库
    handleAdd() {
      this.dialog.visible = true
      this.dialog.title = '新增仓库'
      this.form = {
        id: undefined,
        warehouseCode: '',
        warehouseName: '',
        warehouseType: '',
        location: '',
        area: 0,
        capacity: 0,
        utilization: 0,
        manager: '',
        contactPhone: '',
        imageUrl: '',
        remark: ''
      }
    },
    
    // 编辑仓库
    handleEdit(row) {
      this.dialog.visible = true
      this.dialog.title = '编辑仓库'
      this.form = JSON.parse(JSON.stringify(row))
    },
    
    // 删除仓库
    handleDelete(row) {
      this.$confirm(`确认删除仓库【${row.warehouseName}】吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟删除
        const index = this.tableData.findIndex(item => item.id === row.id)
        if (index !== -1) {
          this.tableData.splice(index, 1)
          this.total--
          this.$message.success('删除成功')
        }
      }).catch(() => {})
    },
    
    // 提交表单
    submitForm() {
      this.$refs.warehouseForm.validate(valid => {
        if (!valid) return
        
        this.submitLoading = true
        
        // 模拟提交
        setTimeout(() => {
          if (this.form.id) {
            // 编辑
            const index = this.tableData.findIndex(item => item.id === this.form.id)
            if (index !== -1) {
              this.tableData.splice(index, 1, this.form)
            }
            this.$message.success('编辑成功')
          } else {
            // 新增
            const newId = Math.max(...this.tableData.map(item => item.id)) + 1
            this.form.id = newId
            this.form.createTime = new Date().toLocaleString()
            this.tableData.push(this.form)
            this.total++
            this.$message.success('新增成功')
          }
          
          this.dialog.visible = false
          this.submitLoading = false
        }, 600)
      })
    }
  }
}
</script>

<style scoped>
/* 页面标题样式 */
.page-header {
  margin-bottom: 24px;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-title i {
  color: #409eff;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

/* 筛选区域样式 */
.filter-container {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
}

.filter-header {
  padding: 16px 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
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
  padding: 20px;
}

.filter-item {
  margin-bottom: 16px;
}

.filter-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.filter-actions {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  height: 100%;
  padding-top: 28px;
}

/* 统计概览样式 */
.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  border: none;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.stat-card-content {
  position: relative;
  z-index: 2;
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon-wrapper {
  position: relative;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
  font-weight: 500;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 6px;
  color: #303133;
}

.stat-desc {
  font-size: 12px;
  color: #67c23a;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-card-bg {
  position: absolute;
  top: 0;
  right: 0;
  width: 100px;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
  z-index: 1;
}

/* 表格容器样式 */
.table-container {
  border-radius: 8px;
  overflow: hidden;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fafafa;
  border-bottom: 1px solid #e9ecef;
}

.table-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.table-actions {
  display: flex;
  gap: 12px;
}

/* 表格样式 */
.warehouse-table {
  border-radius: 0;
}

.warehouse-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.warehouse-avatar {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: linear-gradient(135deg, #409eff 0%, #66b3ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
}

.warehouse-details {
  flex: 1;
}

.warehouse-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.warehouse-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.warehouse-location {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 2px;
}

.warehouse-scale {
  text-align: left;
}

.scale-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
  font-size: 12px;
}

.scale-label {
  color: #909399;
}

.scale-value {
  font-weight: 600;
  color: #303133;
}

.utilization-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.utilization-text {
  font-size: 12px;
  font-weight: 600;
  color: #606266;
}

.manager-info {
  text-align: center;
}

.manager-name {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 2px;
}

.manager-phone {
  font-size: 11px;
  color: #909399;
}

.create-time {
  font-size: 12px;
  color: #606266;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.action-buttons {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
  justify-content: center;
}

/* 分页样式 */
.pagination-wrapper {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
  background: #fafafa;
  border-top: 1px solid #e9ecef;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .filter-content .el-col {
    margin-bottom: 16px;
  }
  
  .stat-card-content {
    padding: 20px 16px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 20px;
  }
  
  .table-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .table-actions {
    width: 100%;
    justify-content: flex-start;
  }
}

/* 深色主题适配 */
:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: #fafafa;
}

:deep(.el-table th.el-table__cell) {
  background: #f8f9fa !important;
}

:deep(.el-progress-bar__outer) {
  border-radius: 4px;
}

:deep(.el-progress-bar__inner) {
  border-radius: 4px;
}
</style>