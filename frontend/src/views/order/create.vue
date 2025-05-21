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
        <el-step v-for="status in orderStatusList" 
                 :key="status.code" 
                 :title="status.name">
        </el-step>
      </el-steps>
    </div>

    <el-card>
      <div slot="header">
        <span>创建订单</span>
        </div>
      
      <el-form ref="orderForm" :model="orderForm" :rules="rules" label-width="120px">
        <!-- 订单基本信息 -->
        <el-divider content-position="left">基本信息</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="orderForm.customerId" placeholder="请选择客户" style="width:100%" @change="handleCustomerChange">
                <el-option
                  v-for="item in customerOptions"
                  :key="item.id"
                  :label="item.customerName"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="预计交付日期" prop="expectedDeliveryDate">
              <el-date-picker
                v-model="orderForm.expectedDeliveryDate"
                type="date"
                placeholder="选择预计交付日期"
                style="width:100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="orderForm.contactName" placeholder="请输入联系人"></el-input>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="orderForm.contactPhone" placeholder="请输入联系电话"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form item label="收货地址" prop="address">
          <el-input v-model="orderForm.address" placeholder="请输入收货地址"></el-input>
        </el-form>
        
        <el-form item label="备注" prop="remark">
          <el-input v-model="orderForm.remark" type="textarea" placeholder="请输入备注信息"></el-input>
        </el-form>
        
        <!-- 订单明细 -->
        <el-divider content-position="left">订单明细</el-divider>
        
        <el-form item>
          <el-button type="primary" size="small" @click="addOrderItem">
            <i class="el-icon-plus"></i> 添加商品
          </el-button>
        </el-form>
        
        <el-table :data="orderForm.orderItems" border style="width: 100%">
          <el-table-column label="序号" type="index" width="50" align="center"></el-table-column>
          
          <el-table-column label="商品" width="280">
            <template slot-scope="scope">
              <el-select v-model="scope.row.productId" placeholder="请选择商品" style="width:100%" @change="(val) => handleProductChange(val, scope.$index)">
                <el-option
                  v-for="item in productOptions"
                  :key="item.id"
                  :label="item.productName"
                  :value="item.id">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
          
          <el-table-column label="商品编码" prop="productCode" width="150"></el-table-column>
          
          <el-table-column label="单位" prop="unit" width="80"></el-table-column>
          
          <el-table-column label="单价(元)" prop="unitPrice" width="100">
            <template slot-scope="scope">
              {{ scope.row.unitPrice | money }}
            </template>
          </el-table-column>
          
          <el-table-column label="数量" width="120">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.quantity" :min="1" size="mini" @change="updateItemTotal(scope.$index)"></el-input-number>
            </template>
          </el-table-column>
          
          <el-table-column label="总价(元)" prop="totalPrice" width="120">
            <template slot-scope="scope">
              {{ scope.row.totalPrice | money }}
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="80" align="center">
            <template slot-scope="scope">
              <el-button type="danger" icon="el-icon-delete" size="mini" circle @click="removeOrderItem(scope.$index)"></el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="order-footer">
          <span class="order-total">订单总金额: <strong>{{ orderTotalAmount | money }}</strong></span>
        </div>
        
        <el-form item style="margin-top:20px;">
          <el-button type="primary" @click="submitForm">提交订单</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form>
    </el-form>
    </el-card>
  </div>
</template>

<script>
import { useOrderStore } from '@/stores/order'
import { customerApi, productApi } from '@/utils/mockUtils'
import { getConstants } from '@/utils/mockUtils'
import moment from 'moment'

export default {
  name: 'OrderCreate',
  filters: {
    money(val) {
      if (!val) return '0.00'
      return Number(val).toFixed(2)
    }
  },
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
      customerOptions: [],
      productOptions: [],
      orderTotalAmount: 0,
      orderStatusList: []
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
    }
  },
  created() {
    const id = this.$route.params.id
    const path = this.$route.path
    this.isCreating = path.includes('create')
    
    if (!this.isCreating && id) {
      this.id = parseInt(id)
      this.getOrderDetail()
    } else {
      this.handleAddItem()
    }
    
    this.getCustomerOptions()
    this.getProductOptions()
    this.getOrderStatusList()
  },
  methods: {
    goBack() {
      this.$router.push({ name: 'OrderList' })
    },
    async getOrderDetail() {
      const orderStore = useOrderStore()
      try {
        const order = await orderStore.fetchOrderDetail(this.id)
        if (order) {
          this.orderForm = order
          if (!this.orderForm.orderItems || this.orderForm.orderItems.length === 0) {
            this.orderForm.orderItems = []
            this.handleAddItem()
          }
        }
      } catch (error) {
        this.$message.error('获取订单详情失败')
        this.$router.push({ name: 'OrderList' })
      }
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
    handleProductChange(productId, index) {
      const product = this.productOptions.find(item => item.id === productId)
      if (product) {
        this.orderForm.orderItems[index].productName = product.productName
        this.orderForm.orderItems[index].productCode = product.productCode
        this.orderForm.orderItems[index].unit = product.unit
        this.orderForm.orderItems[index].unitPrice = product.price
        this.updateItemTotal(index)
      }
    },
    updateItemTotal(index) {
      const item = this.orderForm.orderItems[index]
      if (item.quantity && item.unitPrice) {
        item.totalPrice = (item.quantity * item.unitPrice).toFixed(2)
        this.calculateOrderTotal()
      }
    },
    calculateOrderTotal() {
      this.orderTotalAmount = this.orderForm.orderItems.reduce((total, item) => {
        return total + Number(item.totalPrice || 0)
      }, 0)
    },
    async submitForm() {
      this.$refs.orderForm.validate(async valid => {
        if (valid) {
          if (this.orderForm.orderItems.length === 0) {
            this.$message.warning('请至少添加一个商品')
            return
          }

          for (const item of this.orderForm.orderItems) {
            if (!item.productId) {
              this.$message.warning('请选择商品')
              return
            }
          }
          
          this.orderForm.totalAmount = this.orderTotalAmount
          this.submitLoading = true
          
          try {
            const orderStore = useOrderStore()
            if (this.isCreating) {
              await orderStore.createOrder(this.orderForm)
              this.$message.success('订单创建成功')
            } else {
              await orderStore.updateOrder(this.orderForm)
              this.$message.success('订单更新成功')
            }
            this.$router.push('/order/list')
          } catch (error) {
            this.$message.error(this.isCreating ? '订单创建失败' : '订单更新失败')
          } finally {
            this.submitLoading = false
          }
        } else {
          this.$message.warning('请填写完整信息')
          return false
        }
      })
    },
    resetForm() {
      this.$refs.orderForm.resetFields()
      this.orderForm.orderItems = []
      this.handleAddItem()
    },
    handleCustomerChange(customerId) {
      const customer = this.customerOptions.find(item => item.id === customerId)
      if (customer) {
        this.orderForm.contactName = customer.contactName
        this.orderForm.contactPhone = customer.contactPhone
        this.orderForm.address = customer.address
      }
    },
    addOrderItem() {
      this.orderForm.orderItems.push({
        productId: null,
        productName: '',
        productCode: '',
        unit: '',
        quantity: 1,
        unitPrice: 0,
        totalPrice: 0
      })
    },
    removeOrderItem(index) {
      this.orderForm.orderItems.splice(index, 1)
    },
    getCustomerOptions() {
      customerApi.list({
        filters: { status: 1 },
        pagination: { pageNum: 1, pageSize: 100 }
      }).then(res => {
        if (res.success) {
          this.customerOptions = res.records
        }
      })
    },
    getProductOptions() {
      productApi.list({
        filters: { status: 1 },
        pagination: { pageNum: 1, pageSize: 100 }
      }).then(res => {
        if (res.success) {
          this.productOptions = res.records
        }
      })
    },
    handlePrint() {
      window.print()
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
    async getOrderStatusList() {
      this.orderStatusList = getConstants().orderStatus
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
.order-footer {
  margin-top: 20px;
  text-align: right;
  padding-right: 20px;
}
.order-total {
  font-size: 16px;
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