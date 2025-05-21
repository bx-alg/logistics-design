<template>
  <div class="app-container">
    <div class="page-header">
      <div class="header-title">
        <el-page-header @back="goBack" :content="'计划详情: ' + planDetail.planNo" />
      </div>
      <div class="header-actions">
        <el-button v-if="planDetail.status === 0" type="primary" size="small" @click="handleStart">开始生产</el-button>
        <el-button v-if="planDetail.status === 1" type="success" size="small" @click="handleComplete">完成生产</el-button>
        <el-button v-if="planDetail.status === 0" type="warning" size="small" @click="handleEdit">编辑计划</el-button>
        <el-button v-if="planDetail.status === 0" type="danger" size="small" @click="handleCancel">取消计划</el-button>
      </div>
    </div>

    <el-card class="plan-info-card">
      <div slot="header" class="clearfix">
        <span>基本信息</span>
        <span style="float: right" class="status-tag">
          <el-tag :type="getStatusType(planDetail.status)">{{ getStatusName(planDetail.status) }}</el-tag>
        </span>
      </div>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">计划编号</div>
            <div class="info-value">{{ planDetail.planNo }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">关联订单</div>
            <div class="info-value">
              <el-link type="primary" :underline="false" @click="viewOrderDetail">{{ planDetail.orderNo }}</el-link>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">产品名称</div>
            <div class="info-value">{{ planDetail.productName }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">计划数量</div>
            <div class="info-value">{{ planDetail.planQuantity }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">实际数量</div>
            <div class="info-value">{{ planDetail.actualQuantity }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">完成率</div>
            <div class="info-value">
              <el-progress :percentage="calculateProgress" :status="getProgressStatus"></el-progress>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">开始日期</div>
            <div class="info-value">{{ planDetail.startDate }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">结束日期</div>
            <div class="info-value">{{ planDetail.endDate }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">优先级</div>
            <div class="info-value">
              <el-tag size="medium" :type="getPriorityType(planDetail.priority)">{{ getPriorityName(planDetail.priority) }}</el-tag>
            </div>
          </div>
        </el-col>
        <el-col :span="24">
          <div class="info-item">
            <div class="info-label">计划备注</div>
            <div class="info-value">{{ planDetail.remark || '无' }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>生产进度跟踪</span>
        <el-button 
          v-if="planDetail.status === 1" 
          style="float: right; padding: 3px 0" 
          type="text"
          @click="dialogVisible = true"
        >
          更新进度
        </el-button>
      </div>
      <div class="progress-timeline">
        <el-steps :active="activeStep" finish-status="success" align-center>
          <el-step title="计划制定" :description="planDetail.createTime || '-'" />
          <el-step title="开始生产" :description="planDetail.startTime || '-'" />
          <el-step title="生产中" :description="getProgressStepDesc" />
          <el-step title="生产完成" :description="planDetail.completeTime || '-'" />
          <el-step title="产品入库" :description="planDetail.storageTime || '-'" />
        </el-steps>
      </div>

      <div v-if="progressRecords.length > 0" style="margin-top: 30px;">
        <div class="section-title">进度更新记录</div>
        <el-timeline>
          <el-timeline-item
            v-for="(record, index) in progressRecords"
            :key="index"
            :timestamp="record.updateTime"
            :type="index === 0 ? 'primary' : ''"
          >
            <p>更新进度至 <strong>{{ record.progress }}%</strong></p>
            <p v-if="record.operator">操作人：{{ record.operator }}</p>
            <p v-if="record.remark">说明：{{ record.remark }}</p>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-card>

    <el-card style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>原材料消耗</span>
      </div>
      <el-table
        :data="materialsList"
        border
        style="width: 100%"
      >
        <el-table-column prop="name" label="材料名称" />
        <el-table-column prop="specification" label="规格" />
        <el-table-column prop="unit" label="单位" width="100" />
        <el-table-column prop="planQuantity" label="计划用量" width="120" />
        <el-table-column prop="actualQuantity" label="实际用量" width="120" />
        <el-table-column prop="warehouseName" label="出库仓库" width="150" />
      </el-table>
    </el-card>

    <el-card style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>生产工序</span>
      </div>
      <el-table
        :data="processList"
        border
        style="width: 100%"
      >
        <el-table-column prop="seqNo" label="序号" width="80" />
        <el-table-column prop="processName" label="工序名称" />
        <el-table-column prop="description" label="工序描述" show-overflow-tooltip />
        <el-table-column prop="equipmentName" label="设备名称" width="150" />
        <el-table-column prop="planTime" label="计划时长" width="100" />
        <el-table-column prop="actualTime" label="实际时长" width="100" />
        <el-table-column label="状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="getProcessStatusType(scope.row.status)">{{ getProcessStatusName(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 更新进度对话框 -->
    <el-dialog title="更新生产进度" :visible.sync="dialogVisible" width="500px">
      <el-form :model="progressForm" :rules="progressRules" ref="progressForm" label-width="100px">
        <el-form-item label="当前进度" prop="currentProgress">
          <el-progress :percentage="calculateProgress" :status="getProgressStatus"></el-progress>
        </el-form-item>
        <el-form-item label="新进度值" prop="newProgress">
          <el-slider
            v-model="progressForm.newProgress"
            :min="calculateProgress"
            :max="100"
            :step="5"
            show-input
          />
        </el-form-item>
        <el-form-item label="实际生产数" prop="actualQuantity">
          <el-input-number 
            v-model="progressForm.actualQuantity" 
            :min="planDetail.actualQuantity || 0" 
            :max="planDetail.planQuantity" 
            :step="5"
          />
        </el-form-item>
        <el-form-item label="备注说明" prop="remark">
          <el-input type="textarea" v-model="progressForm.remark" rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitProgress" :loading="submitLoading">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ProductionPlanDetail',
  data() {
    return {
      planId: null,
      planDetail: {
        id: null,
        planNo: '',
        orderNo: '',
        productName: '',
        planQuantity: 0,
        actualQuantity: 0,
        startDate: '',
        endDate: '',
        status: 0,
        priority: 1,
        remark: '',
        createTime: '2023-10-01 09:00:00',
        startTime: '2023-10-05 10:00:00',
        completeTime: '',
        storageTime: ''
      },
      dialogVisible: false,
      progressForm: {
        currentProgress: 0,
        newProgress: 0,
        actualQuantity: 0,
        remark: ''
      },
      progressRules: {
        newProgress: [
          { required: true, message: '请设置新的进度值', trigger: 'change' }
        ],
        actualQuantity: [
          { required: true, message: '请输入实际生产数量', trigger: 'blur' }
        ]
      },
      submitLoading: false,
      materialsList: [
        { name: '铝合金', specification: 'AL6061', unit: 'kg', planQuantity: 100, actualQuantity: 97.5, warehouseName: '原料仓-A区' },
        { name: '钢化玻璃', specification: '0.5mm', unit: '张', planQuantity: 550, actualQuantity: 550, warehouseName: '原料仓-B区' },
        { name: '主板', specification: 'X2-1020', unit: '块', planQuantity: 500, actualQuantity: 500, warehouseName: '电子元件仓' },
        { name: '电池', specification: '4000mAh', unit: '个', planQuantity: 520, actualQuantity: 510, warehouseName: '电子元件仓' },
        { name: '包装材料', specification: '标准', unit: '套', planQuantity: 530, actualQuantity: 500, warehouseName: '包材仓' }
      ],
      processList: [
        { seqNo: 1, processName: '原材料准备', description: '准备生产所需的所有原材料和零部件', equipmentName: '-', planTime: '2小时', actualTime: '1.5小时', status: 2 },
        { seqNo: 2, processName: '组装主板', description: '组装主板和电子元件', equipmentName: '自动贴片机A', planTime: '4小时', actualTime: '4小时', status: 2 },
        { seqNo: 3, processName: '屏幕组装', description: '屏幕和触控面板组装', equipmentName: '精密组装台B', planTime: '3小时', actualTime: '3.5小时', status: 2 },
        { seqNo: 4, processName: '整机组装', description: '将所有组件组装成完整产品', equipmentName: '装配流水线C', planTime: '5小时', actualTime: '4.8小时', status: 2 },
        { seqNo: 5, processName: '质量检测', description: '对产品进行全面质量检测', equipmentName: '自动检测设备D', planTime: '2小时', actualTime: '2小时', status: 1 },
        { seqNo: 6, processName: '包装', description: '产品包装和贴标', equipmentName: '包装流水线E', planTime: '1.5小时', actualTime: '-', status: 0 }
      ],
      progressRecords: [
        { updateTime: '2023-10-05 10:00:00', progress: 0, operator: '张工', remark: '开始生产' },
        { updateTime: '2023-10-05 16:30:00', progress: 15, operator: '张工', remark: '完成原材料准备和部分组装' },
        { updateTime: '2023-10-06 11:20:00', progress: 30, operator: '李工', remark: '完成主板组装' },
        { updateTime: '2023-10-07 09:45:00', progress: 50, operator: '王工', remark: '完成屏幕组装' },
        { updateTime: '2023-10-08 14:30:00', progress: 70, operator: '张工', remark: '完成整机组装，开始质量检测' }
      ]
    }
  },
  computed: {
    calculateProgress() {
      if (!this.planDetail.planQuantity) return 0
      return Math.round((this.planDetail.actualQuantity / this.planDetail.planQuantity) * 100)
    },
    getProgressStatus() {
      if (this.calculateProgress === 100) {
        return 'success'
      } else if (this.calculateProgress >= 80) {
        return 'warning'
      }
      return ''
    },
    activeStep() {
      if (this.planDetail.status === 0) return 1
      if (this.planDetail.status === 1) {
        return this.calculateProgress < 50 ? 2 : 3
      }
      if (this.planDetail.status === 2) return 4
      if (this.planDetail.storageTime) return 5
      return 1
    },
    getProgressStepDesc() {
      if (this.planDetail.status === 1) {
        return `进度: ${this.calculateProgress}%`
      }
      return '-'
    }
  },
  created() {
    const id = this.$route.params.id
    if (id && !isNaN(parseInt(id))) {
      this.planId = parseInt(id)
      this.getPlanDetail()
    } else {
      this.$message.error('无效的计划ID')
      this.goBack()
    }
  },
  methods: {
    getPlanDetail() {
      // 模拟请求后端API获取计划详情
      setTimeout(() => {
        // 模拟数据
        this.planDetail = {
          id: this.planId,
          planNo: 'PP20231001001',
          orderNo: 'DD20231001001',
          productName: '智能手机A',
          planQuantity: 500,
          actualQuantity: 350,
          startDate: '2023-10-05',
          endDate: '2023-10-15',
          status: 1,
          priority: 2,
          remark: '高优先级订单，需优先安排生产',
          createTime: '2023-10-01 09:00:00',
          startTime: '2023-10-05 10:00:00',
          completeTime: '',
          storageTime: ''
        }
        
        // 初始化进度表单
        this.progressForm.currentProgress = this.calculateProgress
        this.progressForm.newProgress = this.calculateProgress
        this.progressForm.actualQuantity = this.planDetail.actualQuantity
      }, 500)
    },
    goBack() {
      this.$router.push({ name: 'ProductionPlan' })
    },
    viewOrderDetail() {
      this.$router.push({ 
        name: 'OrderDetail', 
        params: { id: 1 }  // 这里应该根据orderNo查询到对应的orderId
      })
    },
    // 获取状态类型
    getStatusType(status) {
      const typeMap = {
        0: 'primary',
        1: 'success',
        2: 'warning',
        3: 'danger'
      }
      return typeMap[status] || 'info'
    },
    // 获取状态名称
    getStatusName(status) {
      const nameMap = {
        0: '待生产',
        1: '生产中',
        2: '已完成',
        3: '已取消'
      }
      return nameMap[status] || '未知'
    },
    // 获取优先级类型
    getPriorityType(priority) {
      const typeMap = {
        0: 'info',
        1: '',
        2: 'warning',
        3: 'danger'
      }
      return typeMap[priority] || ''
    },
    // 获取优先级名称
    getPriorityName(priority) {
      const nameMap = {
        0: '低',
        1: '中',
        2: '高',
        3: '紧急'
      }
      return nameMap[priority] || '未知'
    },
    // 获取工序状态类型
    getProcessStatusType(status) {
      const typeMap = {
        0: 'info',
        1: 'primary',
        2: 'success'
      }
      return typeMap[status] || 'info'
    },
    // 获取工序状态名称
    getProcessStatusName(status) {
      const nameMap = {
        0: '待开始',
        1: '进行中',
        2: '已完成'
      }
      return nameMap[status] || '未知'
    },
    // 开始生产
    handleStart() {
      this.$confirm('确认开始生产该计划?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟请求
        this.planDetail.status = 1
        this.planDetail.startTime = new Date().toLocaleString()
        this.$message.success('已开始生产')
      }).catch(() => {})
    },
    // 完成生产
    handleComplete() {
      this.$confirm('确认完成该生产计划?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟请求
        this.planDetail.status = 2
        this.planDetail.actualQuantity = this.planDetail.planQuantity
        this.planDetail.completeTime = new Date().toLocaleString()
        this.$message.success('生产计划已完成')
      }).catch(() => {})
    },
    // 编辑计划
    handleEdit() {
      this.$message.info('编辑功能待实现')
    },
    // 取消计划
    handleCancel() {
      this.$confirm('确认取消该生产计划?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟请求
        this.planDetail.status = 3
        this.$message.success('生产计划已取消')
      }).catch(() => {})
    },
    // 提交进度更新
    submitProgress() {
      this.$refs.progressForm.validate(valid => {
        if (!valid) return
        
        this.submitLoading = true
        
        // 模拟提交请求
        setTimeout(() => {
          // 更新计划详情
          this.planDetail.actualQuantity = this.progressForm.actualQuantity
          
          // 添加进度记录
          const now = new Date().toLocaleString()
          this.progressRecords.unshift({
            updateTime: now,
            progress: this.progressForm.newProgress,
            operator: '当前用户',
            remark: this.progressForm.remark || '常规进度更新'
          })
          
          // 如果进度达到100%，自动完成
          if (this.progressForm.newProgress === 100) {
            this.planDetail.status = 2
            this.planDetail.completeTime = now
          }
          
          this.$message.success('进度更新成功')
          this.dialogVisible = false
          this.submitLoading = false
        }, 1000)
      })
    }
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background-color: #fff;
  padding: 15px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.header-title {
  display: flex;
  align-items: center;
}
.header-actions {
  display: flex;
  gap: 10px;
}
.plan-info-card {
  margin-bottom: 20px;
}
.info-item {
  margin-bottom: 15px;
}
.info-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}
.info-value {
  font-size: 14px;
  color: #303133;
}
.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #303133;
}
.progress-timeline {
  padding: 20px;
}
</style> 