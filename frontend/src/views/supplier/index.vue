<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.name" placeholder="供应商名称" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter" />
      <el-select v-model="listQuery.category" placeholder="供应商类别" clearable style="width: 150px" class="filter-item">
        <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-select v-model="listQuery.status" placeholder="合作状态" clearable style="width: 130px" class="filter-item">
        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" icon="el-icon-search" class="filter-item" @click="handleFilter">搜索</el-button>
      <el-button type="primary" icon="el-icon-plus" class="filter-item" style="margin-left: 10px;" @click="handleCreate">新增</el-button>
      <el-button type="warning" icon="el-icon-download" class="filter-item" @click="handleExport">导出</el-button>
    </div>

    <el-table :data="list" border style="width: 100%" v-loading="listLoading">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="code" label="供应商编码" align="center" width="120" />
      <el-table-column prop="name" label="供应商名称" align="center" min-width="180" />
      <el-table-column prop="contactPerson" label="联系人" align="center" width="120" />
      <el-table-column prop="contactPhone" label="联系电话" align="center" width="150" />
      <el-table-column prop="category" label="供应商类别" align="center" width="120">
        <template slot-scope="scope">
          {{ getCategoryLabel(scope.row.category) }}
        </template>
      </el-table-column>
      <el-table-column prop="level" label="合作等级" align="center" width="100">
        <template slot-scope="scope">
          <el-rate
            v-model="scope.row.level"
            disabled
            text-color="#ff9900"
            score-template="{value}"
          />
        </template>
      </el-table-column>
      <el-table-column prop="status" label="合作状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="cooperationStartDate" label="合作开始日期" align="center" width="150" />
      <el-table-column label="操作" align="center" fixed="right" width="280">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="handleDetail(scope.row)">详情</el-button>
          <el-button size="mini" type="success" @click="handlePerformance(scope.row)">评估</el-button>
          <el-button size="mini" type="warning" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.row)" :disabled="scope.row.status === 1">删除</el-button>
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
  name: 'SupplierList',
  data() {
    return {
      list: [
        { id: 1, code: 'S00001', name: '浙江海宁布料制造有限公司', contactPerson: '张明辉', contactPhone: '13812345678', category: 'fabric', level: 5, status: 1, cooperationStartDate: '2020-01-15' },
        { id: 2, code: 'S00002', name: '广东佛山童装面料有限公司', contactPerson: '李志强', contactPhone: '13987654321', category: 'fabric', level: 4, status: 1, cooperationStartDate: '2020-03-22' },
        { id: 3, code: 'S00003', name: '江苏苏州童装辅料有限公司', contactPerson: '王小红', contactPhone: '13567891234', category: 'accessory', level: 3, status: 1, cooperationStartDate: '2020-07-10' },
        { id: 4, code: 'S00004', name: '上海童装包装材料有限公司', contactPerson: '赵丽丽', contactPhone: '13456789012', category: 'packaging', level: 4, status: 2, cooperationStartDate: '2021-02-18' },
        { id: 5, code: 'S00005', name: '杭州童装纽扣制造有限公司', contactPerson: '钱伟', contactPhone: '13345678901', category: 'accessory', level: 5, status: 1, cooperationStartDate: '2021-05-05' },
        { id: 6, code: 'S00006', name: '义乌市童装拉链有限公司', contactPerson: '孙建国', contactPhone: '13234567890', category: 'accessory', level: 4, status: 1, cooperationStartDate: '2021-09-12' },
        { id: 7, code: 'S00007', name: '东莞市童装印花材料有限公司', contactPerson: '周丽娜', contactPhone: '13123456789', category: 'printing', level: 3, status: 3, cooperationStartDate: '2022-01-20' },
        { id: 8, code: 'S00008', name: '温州市童装标签制造有限公司', contactPerson: '吴海洋', contactPhone: '13876543210', category: 'accessory', level: 4, status: 1, cooperationStartDate: '2022-03-15' },
        { id: 9, code: 'S00009', name: '常州市童装染料有限公司', contactPerson: '郑晓华', contactPhone: '13765432109', category: 'dyeing', level: 5, status: 1, cooperationStartDate: '2022-06-08' },
        { id: 10, code: 'S00010', name: '绍兴市童装面料批发有限公司', contactPerson: '王大力', contactPhone: '13654321098', category: 'fabric', level: 4, status: 1, cooperationStartDate: '2022-09-22' }
      ],
      total: 10,
      listLoading: false,
      listQuery: {
        page: 1,
        limit: 10,
        name: '',
        category: '',
        status: ''
      },
      categoryOptions: [
        { label: '面料供应', value: 'fabric' },
        { label: '辅料供应', value: 'accessory' },
        { label: '包装材料', value: 'packaging' },
        { label: '印花材料', value: 'printing' },
        { label: '染色材料', value: 'dyeing' },
        { label: '加工服务', value: 'processing' }
      ],
      statusOptions: [
        { label: '合作中', value: 1 },
        { label: '暂停合作', value: 2 },
        { label: '终止合作', value: 3 }
      ]
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
      this.$message.success('新增供应商功能开发中')
    },
    handleEdit(row) {
      this.$message.success(`编辑供应商: ${row.name}`)
    },
    handleDetail(row) {
      this.$message.success(`查看供应商详情: ${row.name}`)
    },
    handlePerformance(row) {
      this.$router.push({ path: '/supplier/performance', query: { id: row.id } })
    },
    handleDelete(row) {
      this.$confirm('确认删除该供应商吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success(`删除供应商: ${row.name}`)
      }).catch(() => {})
    },
    handleExport() {
      this.$message.success('导出功能开发中')
    },
    getStatusType(status) {
      const statusMap = {
        1: 'success',
        2: 'warning',
        3: 'danger'
      }
      return statusMap[status]
    },
    getStatusLabel(status) {
      const statusMap = {
        1: '合作中',
        2: '暂停合作',
        3: '终止合作'
      }
      return statusMap[status]
    },
    getCategoryLabel(category) {
      const item = this.categoryOptions.find(item => item.value === category)
      return item ? item.label : category
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
</style> 