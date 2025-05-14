<template>
  <div class="app-container">
    <div class="detail-header">
      <el-page-header @back="goBack" :content="title" />
      <div class="operation-group" v-if="!isCreating">
        <el-button type="primary" size="small" @click="handlePrint" icon="el-icon-printer">打印</el-button>
        <el-button type="success" size="small" @click="handleNextStatus" :disabled="!canChangeStatus">
          {{ nextStatusLabel }}
        </el-button>
        <el-button type="danger" size="small" @click="handleCancel" :disabled="orderForm.status === 4 || orderForm.status === 5">取消订单</el-button>
      </div>
    </div>

    <div class="order-status" v-if="!isCreating">
      <el-steps :active="orderForm.status" finish-status="success" simple>
        <el-step title="待确认"></el-step>
        <el-step title="已确认"></el-step>
        <el-step title="生产中"></el-step>
        <el-step title="已发货"></el-step>
        <el-step title="已完成"></el-step>
      </el-steps>
    </div>

    <el-form ref="orderForm" :model="orderForm" :rules="rules" label-position="right" label-width="120px" class="order-form">
      <el-card class="box-card">
        <div slot="header" class="card-header">
          <span>基本信息</span>
        </div>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="订单编号" prop="orderNo" v-if="!isCreating">
              <el-input v-model="orderForm.orderNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="orderForm.customerId" placeholder="请选择客户" filterable :disabled="!canEdit">
                <el-option v-for="item in customers" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="订单状态" v-if="!isCreating">
              <el-tag :type="statusTag">{{ statusName }}</el-tag>
            </el-form-item>
            <el-form-item label="总金额" v-else>
              <el-input v-model="orderForm.totalAmount" disabled>
                <template slot="prepend">¥</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="orderForm.contactName" :disabled="!canEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="orderForm.contactPhone" :disabled="!canEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预计交付日期" prop="expectedDeliveryDate">
              <el-date-picker v-model="orderForm.expectedDeliveryDate" type="date" placeholder="选择日期" style="width: 100%;" :disabled="!canEdit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="收货地址" prop="address">
              <el-input v-model="orderForm.address" :disabled="!canEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="实际交付日期" prop="actualDeliveryDate" v-if="orderForm.status >= 4">
              <el-date-picker v-model="orderForm.actualDeliveryDate" type="date" placeholder="选择日期" style="width: 100%;" :disabled="!canEdit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="orderForm.remark" type="textarea" :rows="2" placeholder="请输入备注信息" :disabled="!canEdit" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <el-card class="box-card" style="margin-top: 20px;">
        <div slot="header" class="card-header">
          <span>订单明细</span>
          <el-button v-if="canEdit" type="text" @click="handleAddItem">添加明细</el-button>
        </div>
        <el-table :data="orderForm.orderItems" border style="width: 100%">
          <el-table-column label="序号" type="index" width="50" align="center" />
          <el-table-column label="商品" prop="productId" width="220">
            <template slot-scope="scope">
              <el-select v-model="scope.row.productId" placeholder="请选择商品" filterable :disabled="!canEdit" @change="handleProductChange(scope.$index, scope.row)">
                <el-option v-for="item in products" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="商品名称" prop="productName" v-if="!canEdit" />
          <el-table-column label="商品编码" prop="productCode" v-if="!canEdit" />
          <el-table-column label="单位" prop="unit" width="80" align="center" />
          <el-table-column label="单价" prop="unitPrice" width="120">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.unitPrice" :min="0" :precision="2" size="small" :disabled="!canEdit" @change="calculateItemTotal(scope.$index)" />
            </template>
          </el-table-column>
          <el-table-column label="数量" prop="quantity" width="120">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.quantity" :min="1" size="small" :disabled="!canEdit" @change="calculateItemTotal(scope.$index)" />
            </template>
          </el-table-column>
          <el-table-column label="总价" prop="totalPrice" width="120">
            <template slot-scope="scope">
              <span>¥ {{ scope.row.totalPrice || calculateRowTotal(scope.row) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" v-if="canEdit">
            <template slot-scope="scope">
              <el-button type="danger" icon="el-icon-delete" size="mini" circle @click="handleRemoveItem(scope.$index)" />
            </template>
          </el-table-column>
        </el-table>
        <div class="order-total">
          <span>订单总金额: <strong style="color: #f56c6c; font-size: 18px;">¥ {{ calculateOrderTotal }}</strong></span>
        </div>
      </el-card>

      <div class="form-footer">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading" v-if="canEdit">保存</el-button>
      </div>
    </el-form>
  </div>
</template>

<script>
import { getOrderDetail, createOrder, updateOrder, updateOrderStatus } from '@/api/order'

export default {
  name: 'OrderDetail',
  data() {
    return {
      isCreating: false,
      id: undefined,
      submitLoading: false,
      orderForm: {
        id: undefined,
        orderNo: '',
        customerId: '',
        totalAmount: 0,
        status: 0,
        address: '',
        contactName: '',
        contactPhone: '',
        remark: '',
        expectedDeliveryDate: '',
        actualDeliveryDate: '',
        orderItems: []
      },
      rules: {
        customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
        contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
        contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
        address: [{ required: true, message: '请输入收货地址', trigger: 'blur' }],
        expectedDeliveryDate: [{ required: true, message: '请选择预计交付日期', trigger: 'change' }]
      },
      // 模拟数据，实际项目中从接口获取
      customers: [
        { id: 1, name: '上海某科技有限公司' },
        { id: 2, name: '北京某贸易有限公司' },
        { id: 3, name: '广州某电子科技公司' },
        { id: 4, name: '深圳某科技有限公司' },
        { id: 5, name: '成都某机械有限公司' },
        { id: 6, name: '杭州某科技有限公司' }
      ],
      products: [
        { id: 1, name: '产品A', price: 100, unit: '个', code: 'P001' },
        { id: 2, name: '产品B', price: 200, unit: '件', code: 'P002' },
        { id: 3, name: '产品C', price: 300, unit: '箱', code: 'P003' },
        { id: 4, name: '产品D', price: 400, unit: '套', code: 'P004' },
        { id: 5, name: '产品E', price: 500, unit: '台', code: 'P005' }
      ]
    }
  },
  computed: {
    title() {
      return this.isCreating ? '创建订单' : `订单详情: ${this.orderForm.orderNo}`
    },
    canEdit() {
      return this.isCreating || this.orderForm.status === 0
    },
    canChangeStatus() {
      return !this.isCreating && this.orderForm.status < 4
    },
    nextStatusLabel() {
      const statusMap = {
        0: '确认订单',
        1: '开始生产',
        2: '发货',
        3: '完成订单',
        4: '',
        5: ''
      }
      return statusMap[this.orderForm.status]
    },
    statusName() {
      const statusMap = {
        0: '待确认',
        1: '已确认',
        2: '生产中',
        3: '已发货',
        4: '已完成',
        5: '已取消'
      }
      return statusMap[this.orderForm.status]
    },
    statusTag() {
      const statusMap = {
        0: 'info',
        1: 'primary',
        2: 'warning',
        3: 'success',
        4: 'success',
        5: 'danger'
      }
      return statusMap[this.orderForm.status]
    },
    calculateOrderTotal() {
      let total = 0
      this.orderForm.orderItems.forEach(item => {
        total += parseFloat(item.totalPrice || this.calculateRowTotal(item))
      })
      this.orderForm.totalAmount = total.toFixed(2)
      return total.toFixed(2)
    }
  },
  created() {
    const id = this.$route.params.id
    console.log('路由参数id = ', id)
    
    // 判断是否是创建订单模式
    this.isCreating = id === 'create'
    
    if (!this.isCreating) {
      // 非创建模式，需要获取订单详情
      if (id && !isNaN(parseInt(id))) {
        this.id = parseInt(id)
        this.getOrderDetail()
      } else {
        // 参数无效，显示错误并返回
        this.$message.error('无效的订单ID参数')
        this.$router.push({ name: 'OrderList' })
      }
    } else {
      // 创建订单模式，初始化一个空的订单项
      this.handleAddItem()
    }
  },
  methods: {
    goBack() {
      this.$router.push({ name: 'OrderList' })
    },
    getOrderDetail() {
      getOrderDetail(this.id).then(response => {
        if (response.data) {
          this.orderForm = response.data
          // 如果没有明细项，添加一个空的
          if (!this.orderForm.orderItems || this.orderForm.orderItems.length === 0) {
            this.orderForm.orderItems = []
            this.handleAddItem()
          }
        }
      })
    },
    handleAddItem() {
      this.orderForm.orderItems.push({
        id: undefined,
        orderId: this.orderForm.id,
        productId: '',
        productName: '',
        productCode: '',
        unit: '',
        quantity: 1,
        unitPrice: 0,
        totalPrice: 0
      })
    },
    handleRemoveItem(index) {
      this.orderForm.orderItems.splice(index, 1)
      if (this.orderForm.orderItems.length === 0) {
        this.handleAddItem()
      }
    },
    handleProductChange(index, row) {
      const product = this.products.find(item => item.id === row.productId)
      if (product) {
        row.productName = product.name
        row.productCode = product.code
        row.unitPrice = product.price
        row.unit = product.unit
        this.calculateItemTotal(index)
      }
    },
    calculateItemTotal(index) {
      const item = this.orderForm.orderItems[index]
      if (item.quantity && item.unitPrice) {
        item.totalPrice = (item.quantity * item.unitPrice).toFixed(2)
      }
    },
    calculateRowTotal(row) {
      if (row.quantity && row.unitPrice) {
        return (row.quantity * row.unitPrice).toFixed(2)
      }
      return '0.00'
    },
    submitForm() {
      this.$refs.orderForm.validate(valid => {
        if (valid) {
          // 检查订单明细是否填写完整
          const validItems = this.orderForm.orderItems.filter(item => item.productId && item.quantity && item.unitPrice)
          if (validItems.length === 0) {
            this.$message.error('请至少添加一项有效的订单明细')
            return
          }

          this.submitLoading = true
          // 清理无效的明细项
          this.orderForm.orderItems = validItems
          
          const request = this.isCreating ? createOrder(this.orderForm) : updateOrder(this.orderForm)
          request.then(response => {
            this.submitLoading = false
            this.$message.success(this.isCreating ? '创建订单成功' : '更新订单成功')
            if (this.isCreating) {
              this.$router.push({
                name: 'OrderDetail',
                params: { id: response.data }
              })
            } else {
              this.getOrderDetail()
            }
          }).catch(() => {
            this.submitLoading = false
          })
        }
      })
    },
    handleNextStatus() {
      this.$confirm(`确认将订单状态更新为"${this.nextStatusLabel}"?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const nextStatus = this.orderForm.status + 1
        updateOrderStatus(this.id, nextStatus).then(() => {
          this.$message.success('更新订单状态成功')
          this.orderForm.status = nextStatus
        })
      }).catch(() => {})
    },
    handleCancel() {
      this.$confirm('确认取消该订单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateOrderStatus(this.id, 5).then(() => {
          this.$message.success('订单已取消')
          this.orderForm.status = 5
        })
      }).catch(() => {})
    },
    handlePrint() {
      window.print()
    }
  }
}
</script>

<style scoped>
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.operation-group {
  display: flex;
  gap: 10px;
}
.order-status {
  margin-bottom: 30px;
}
.order-form {
  margin-top: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.order-total {
  text-align: right;
  margin-top: 20px;
  padding-right: 20px;
}
.form-footer {
  margin-top: 30px;
  text-align: center;
}
@media print {
  .detail-header, .form-footer, .el-table__expand-icon {
    display: none;
  }
}
</style> 