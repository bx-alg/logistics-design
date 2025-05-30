<template>
  <div class="app-container">
    <!-- 头部统计卡片 -->
    <el-row :gutter="20" class="dashboard-header">
      <el-col :span="6" v-for="(item, index) in statistics" :key="index">
        <el-card shadow="hover" class="dashboard-card">
          <div class="card-header">
            <count-to
              class="card-number"
              :start-val="0"
              :end-val="item.value"
              :duration="2000"
              :decimals="item.decimals || 0"
            />
            <div class="card-title">{{ item.title }}</div>
          </div>
          <div class="card-icon">
            <i :class="item.icon"></i>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选器卡片 -->
    <el-card class="filter-container">
      <div slot="header" class="card-header">
        <span class="header-title">排产计划管理</span>
        <div class="header-operations">
          <el-button-group>
            <el-button 
              type="primary" 
              icon="el-icon-plus" 
              size="small"
              @click="handleCreatePlan">创建计划</el-button>
            <el-button 
              type="success" 
              icon="el-icon-download" 
              size="small"
              @click="handleExport">导出数据</el-button>
          </el-button-group>
        </div>
      </div>
      <div class="filter-item-container">
        <div class="filter-item">
          <span class="label">计划编号：</span>
          <el-input 
            v-model="filterForm.planNo" 
            placeholder="请输入计划编号" 
            clearable
            size="small"
            prefix-icon="el-icon-search"
            @keyup.enter.native="handleFilter"
          />
        </div>
        <div class="filter-item">
          <span class="label">订单编号：</span>
          <el-input 
            v-model="filterForm.orderNo" 
            placeholder="请输入订单编号" 
            clearable
            size="small"
            prefix-icon="el-icon-search"
            @keyup.enter.native="handleFilter"
          />
        </div>
        <div class="filter-item">
          <span class="label">计划状态：</span>
          <el-select 
            v-model="filterForm.status" 
            placeholder="请选择" 
            clearable
            size="small"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <el-tag 
                :type="getStatusType(item.value)" 
                size="mini"
                effect="dark"
              >{{ item.label }}</el-tag>
            </el-option>
          </el-select>
        </div>
        <div class="filter-item">
          <span class="label">生产日期：</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            align="right"
            unlink-panels
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            size="small"
            value-format="yyyy-MM-dd"
            :picker-options="pickerOptions"
          />
        </div>
        <div class="filter-buttons">
          <el-button 
            type="primary" 
            icon="el-icon-search" 
            size="small"
            @click="handleFilter">查询</el-button>
          <el-button 
            icon="el-icon-refresh" 
            size="small"
            @click="resetFilter">重置</el-button>
        </div>
      </div>
    </el-card>

    <!-- 视图切换卡片 -->
    <el-card class="view-switch-container">
      <div class="view-switch">
        <el-radio-group v-model="currentView" size="small">
          <el-radio-button label="table">
            <i class="el-icon-tickets"></i> 列表视图
          </el-radio-button>
        </el-radio-group>
      </div>
    </el-card>

    <!-- 列表视图 -->
    <el-card class="table-container">
      <div class="table-header">
        <span class="header-title">排产计划列表</span>
        <div class="header-operations">
          <el-button-group>
            <el-button 
              type="primary" 
              icon="el-icon-plus" 
              size="small"
              @click="handleCreatePlan">创建计划</el-button>
            <el-button 
              type="success" 
              icon="el-icon-download" 
              size="small"
              @click="handleExport">导出数据</el-button>
          </el-button-group>
        </div>
      </div>

      <div class="table-content">
        <el-table
          ref="productionTable"
          v-loading="loading"
          :data="tableData"
          border
          stripe
          :size="tableSize"
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column prop="planNo" label="计划编号" min-width="130">
            <template slot-scope="scope">
              <el-link type="primary" @click="handleDetail(scope.row)">{{ scope.row.planNo }}</el-link>
            </template>
          </el-table-column>
          <el-table-column prop="orderNo" label="关联订单" min-width="130" show-overflow-tooltip />
          <el-table-column prop="productName" label="产品名称" min-width="150" show-overflow-tooltip />
          <el-table-column label="生产数量" min-width="180">
            <template slot-scope="scope">
              <el-progress 
                :percentage="calculateProgress(scope.row)"
                :format="percentFormat"
                :status="getProgressStatus(scope.row)"
              >
                <el-tooltip slot="format" placement="top">
                  <div slot="content">
                    计划：{{ scope.row.planQuantity }}<br>
                    实际：{{ scope.row.actualQuantity }}
                  </div>
                  {{ scope.row.actualQuantity }}/{{ scope.row.planQuantity }}
                </el-tooltip>
              </el-progress>
            </template>
          </el-table-column>
          <el-table-column label="生产日期" min-width="180">
            <template slot-scope="scope">
              <i class="el-icon-time"></i>
              {{ scope.row.startDate }} 至 {{ scope.row.endDate }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80" align="center">
            <template slot-scope="scope">
              <el-tag
                :type="getStatusType(scope.row.status)"
                effect="dark"
                size="mini"
              >{{ getStatusName(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="优先级" width="80" align="center">
            <template slot-scope="scope">
              <el-tag
                :type="getPriorityType(scope.row.priority)"
                size="mini"
              >{{ getPriorityName(scope.row.priority) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template slot-scope="scope">
              <div class="table-actions">
                <el-tooltip content="查看详情" placement="top">
                  <el-button 
                    type="text"
                    icon="el-icon-view"
                    @click="handleDetail(scope.row)">详情</el-button>
                </el-tooltip>
                
                <el-divider direction="vertical"></el-divider>
                
                <el-dropdown 
                  trigger="click"
                  @command="(command) => handleCommand(command, scope.row)"
                >
                  <el-button type="text">
                    更多<i class="el-icon-arrow-down el-icon--right"></i>
                  </el-button>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item 
                      v-if="scope.row.status === 0" 
                      command="edit">
                      <i class="el-icon-edit"></i> 编辑
                    </el-dropdown-item>
                    <el-dropdown-item 
                      v-if="scope.row.status === 0" 
                      command="start">
                      <i class="el-icon-video-play"></i> 开始生产
                    </el-dropdown-item>
                    <el-dropdown-item 
                      v-if="scope.row.status === 1" 
                      command="complete">
                      <i class="el-icon-check"></i> 完成生产
                    </el-dropdown-item>
                    <el-dropdown-item 
                      v-if="scope.row.status === 0" 
                      command="cancel"
                      divided>
                      <i class="el-icon-close"></i> 取消计划
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-container">
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

    <!-- 创建/编辑排产计划对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      :visible.sync="dialogVisible" 
      width="600px"
      :close-on-click-modal="false"
      @close="resetForm('planForm')"
    >
      <el-form 
        ref="planForm" 
        :model="planForm" 
        :rules="planRules" 
        label-width="100px"
        size="small"
      >
        <el-form-item label="关联订单" prop="orderId">
          <el-select 
            v-model="planForm.orderId" 
            placeholder="请选择关联订单" 
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in orderOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <span style="float: left">{{ item.label }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ item.orderNo }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="生产产品" prop="productId">
          <el-select 
            v-model="planForm.productId" 
            placeholder="请选择生产产品"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in productOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="计划数量" prop="planQuantity">
          <el-input-number 
            v-model="planForm.planQuantity" 
            :min="1" 
            :max="10000"
            controls-position="right"
            style="width: 100%" 
          />
        </el-form-item>
        <el-form-item label="生产日期" prop="dateRange">
          <el-date-picker
            v-model="planForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
            value-format="yyyy-MM-dd"
            :picker-options="pickerOptions"
          />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select 
            v-model="planForm.priority" 
            placeholder="请选择优先级"
            style="width: 100%"
          >
            <el-option 
              v-for="(label, value) in priorityOptions" 
              :key="value"
              :label="label"
              :value="Number(value)"
            >
              <el-tag 
                :type="getPriorityType(Number(value))"
                size="mini"
              >{{ label }}</el-tag>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            type="textarea" 
            v-model="planForm.remark" 
            rows="3"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitPlan" :loading="submitLoading">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import CountTo from 'vue-count-to'
import { productionApi } from '@/utils/mockUtils'
import { getConstants } from '@/utils/mockUtils'
import moment from 'moment'

export default {
  name: 'ProductionPlan',
  components: {
    CountTo
  },
  data() {
    return {
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: '',
        customerName: '',
        status: '',
        dateRange: []
      },
      // 状态选项
      statusOptions: [
        { value: 0, label: '待生产' },
        { value: 1, label: '生产中' },
        { value: 2, label: '已完成' },
        { value: 3, label: '已取消' }
      ],
      // 表格数据
      tableData: [
        {
          id: 1,
          planNo: 'PP20240501001',
          orderNo: 'ORD20240301001',
          productName: '2024夏季女童公主裙',
          planQuantity: 2000,
          actualQuantity: 800,
          startDate: '2024-05-01',
          endDate: '2024-05-10',
          startTime: '08:00',
          endTime: '18:00',
          status: 1,
          priority: 2,
          remark: '织里童装厂重点订单，面料采用优质棉，做工精细'
        },
        {
          id: 2,
          planNo: 'PP20240502001',
          orderNo: 'ORD20240302001',
          productName: '男童运动套装2024新款',
          planQuantity: 3000,
          actualQuantity: 3000,
          startDate: '2024-05-02',
          endDate: '2024-05-12',
          startTime: '08:00',
          endTime: '18:00',
          status: 2,
          priority: 1,
          remark: '织里童装批发市场热销款，采用速干面料'
        },
        {
          id: 3,
          planNo: 'PP20240503001',
          orderNo: 'ORD20240303001',
          productName: '儿童汉服夏季系列',
          planQuantity: 1500,
          actualQuantity: 0,
          startDate: '2024-05-15',
          endDate: '2024-05-25',
          startTime: '08:00',
          endTime: '18:00',
          status: 0,
          priority: 2,
          remark: '织里特色童装，采用传统工艺制作'
        },
        {
          id: 4,
          planNo: 'PP20240504001',
          orderNo: 'ORD20240304001',
          productName: '婴幼儿连体爬服',
          planQuantity: 5000,
          actualQuantity: 2000,
          startDate: '2024-05-05',
          endDate: '2024-05-20',
          startTime: '08:00',
          endTime: '18:00',
          status: 1,
          priority: 3,
          remark: '织里婴童专区畅销款，采用有机棉材质'
        },
        {
          id: 5,
          planNo: 'PP20240505001',
          orderNo: 'ORD20240305001',
          productName: '儿童防晒衣套装',
          planQuantity: 2500,
          actualQuantity: 0,
          startDate: '2024-05-20',
          endDate: '2024-05-30',
          startTime: '08:00',
          endTime: '18:00',
          status: 0,
          priority: 1,
          remark: '织里夏季新品，采用UPF50+防晒面料'
        },
        {
          id: 6,
          planNo: 'PP20240506001',
          orderNo: 'ORD20240306001',
          productName: '儿童牛仔背带裤',
          planQuantity: 3000,
          actualQuantity: 1200,
          startDate: '2024-05-08',
          endDate: '2024-05-18',
          startTime: '08:00',
          endTime: '18:00',
          status: 1,
          priority: 2,
          remark: '织里童装市场经典款式，耐磨耐穿'
        },
        {
          id: 7,
          planNo: 'PP20240507001',
          orderNo: 'ORD20240307001',
          productName: '女童纱纱裙套装',
          planQuantity: 1800,
          actualQuantity: 1800,
          startDate: '2024-05-01',
          endDate: '2024-05-10',
          startTime: '08:00',
          endTime: '18:00',
          status: 2,
          priority: 2,
          remark: '织里高端童装定制，采用进口面料'
        },
        {
          id: 8,
          planNo: 'PP20240508001',
          orderNo: 'ORD20240308001',
          productName: '儿童校服套装',
          planQuantity: 4000,
          actualQuantity: 0,
          startDate: '2024-05-25',
          endDate: '2024-06-10',
          startTime: '08:00',
          endTime: '18:00',
          status: 0,
          priority: 3,
          remark: '织里学校定制校服，采用环保面料'
        },
        {
          id: 9,
          planNo: 'PP20240509001',
          orderNo: 'ORD20240309001',
          productName: '亲子装夏季系列',
          planQuantity: 1200,
          actualQuantity: 500,
          startDate: '2024-05-15',
          endDate: '2024-05-25',
          startTime: '08:00',
          endTime: '18:00',
          status: 1,
          priority: 1,
          remark: '织里特色亲子装，情侣装定制'
        },
        {
          id: 10,
          planNo: 'PP20240510001',
          orderNo: 'ORD20240310001',
          productName: '儿童民族服装系列',
          planQuantity: 1000,
          actualQuantity: 1000,
          startDate: '2024-05-05',
          endDate: '2024-05-15',
          startTime: '08:00',
          endTime: '18:00',
          status: 2,
          priority: 2,
          remark: '织里特色民族风童装，采用传统工艺'
        }
      ],
      // 加载状态
      loading: false,
      // 总数
      total: 10,
      // 编辑对话框
      editDialog: {
        visible: false,
        loading: false,
        form: {
          id: null,
          orderNo: '',
          customerName: '',
          productName: '',
          startTime: '',
          endTime: '',
          status: '',
          remark: ''
        },
        rules: {
          startTime: [
            { required: true, message: '请选择开始时间', trigger: 'change' }
          ],
          endTime: [
            { required: true, message: '请选择结束时间', trigger: 'change' }
          ]
        }
      },
      // 筛选表单
      filterForm: {
        planNo: '',
        orderNo: '',
        status: '',
        startDate: '',
        endDate: '',
        pageNum: 1,
        pageSize: 10
      },
      dateRange: [],
      // 对话框设置
      dialogVisible: false,
      dialogTitle: '创建排产计划',
      planForm: {
        id: null,
        orderId: '',
        productId: '',
        planQuantity: 1,
        dateRange: [],
        priority: 1,
        remark: ''
      },
      planRules: {
        orderId: [{ required: true, message: '请选择关联订单', trigger: 'change' }],
        productId: [{ required: true, message: '请选择生产产品', trigger: 'change' }],
        planQuantity: [{ required: true, message: '请输入计划数量', trigger: 'blur' }],
        dateRange: [{ required: true, message: '请选择生产日期', trigger: 'change' }],
        priority: [{ required: true, message: '请选择优先级', trigger: 'change' }]
      },
      submitLoading: false,
      // 选项数据
      orderOptions: [
        { value: 1, label: '湖州织里童泰童装有限公司', orderNo: 'ORD20240301001' },
        { value: 2, label: '织里镇巴拉巴拉服饰有限公司', orderNo: 'ORD20240302001' },
        { value: 3, label: '织里镇安奈儿童装有限公司', orderNo: 'ORD20240303001' },
        { value: 4, label: '湖州织里咔咔童装有限公司', orderNo: 'ORD20240304001' },
        { value: 5, label: '织里镇小猪班纳童装有限公司', orderNo: 'ORD20240305001' },
        { value: 6, label: '湖州织里贝贝依童装有限公司', orderNo: 'ORD20240306001' },
        { value: 7, label: '织里镇巴布豆童装有限公司', orderNo: 'ORD20240307001' },
        { value: 8, label: '湖州织里哈利玛童装有限公司', orderNo: 'ORD20240308001' },
        { value: 9, label: '织里镇青蛙皇子童装有限公司', orderNo: 'ORD20240309001' },
        { value: 10, label: '湖州织里米乐熊童装有限公司', orderNo: 'ORD20240310001' }
      ],
      productOptions: [
        { value: 1, label: '2024夏季女童公主裙' },
        { value: 2, label: '男童运动套装2024新款' },
        { value: 3, label: '儿童汉服夏季系列' },
        { value: 4, label: '婴幼儿连体爬服' },
        { value: 5, label: '儿童防晒衣套装' },
        { value: 6, label: '儿童牛仔背带裤' },
        { value: 7, label: '女童纱纱裙套装' },
        { value: 8, label: '儿童校服套装' },
        { value: 9, label: '亲子装夏季系列' },
        { value: 10, label: '儿童民族服装系列' },
        { value: 11, label: '儿童泳装系列' },
        { value: 12, label: '儿童睡衣套装' },
        { value: 13, label: '儿童内衣套装' },
        { value: 14, label: '儿童演出服系列' },
        { value: 15, label: '儿童礼服系列' }
      ],
      currentView: 'table',
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            picker.$emit('pick', [start, end])
          }
        }]
      },
      tableSize: 'medium',
      statistics: [
        {
          title: '总计划数',
          value: 0,
          icon: 'el-icon-s-order'
        },
        {
          title: '生产中',
          value: 0,
          icon: 'el-icon-loading'
        },
        {
          title: '已完成',
          value: 0,
          icon: 'el-icon-success'
        },
        {
          title: '生产效率',
          value: 0,
          icon: 'el-icon-data-line'
        }
      ],
      priorityOptions: {
        0: '低',
        1: '中',
        2: '高',
        3: '紧急'
      }
    }
  },
  computed: {
    // 计算已选择的行数
    hasSelected() {
      return this.selectedRows.length > 0
    }
  },
  watch: {
    dateRange(val) {
      if (val && val.length === 2) {
        this.filterForm.startDate = val[0]
        this.filterForm.endDate = val[1]
      } else {
        this.filterForm.startDate = ''
        this.filterForm.endDate = ''
      }
    },
    tableData: {
      handler(val) {
        this.calculateStatistics()
      },
      deep: true
    }
  },
  created() {
    this.initData()
  },
  methods: {
    /** 初始化数据 */
    initData() {
      console.log('Initializing data...')
      this.loading = true
      this.error = null // 清除之前的错误
      try {
        // 计算统计数据
        const total = this.tableData.length
        const inProgress = this.tableData.filter(item => item.status === 1).length
        const completed = this.tableData.filter(item => item.status === 2).length
        const efficiency = completed > 0 ? (completed / total * 100).toFixed(2) : 0

        this.statistics = [
          {
            title: '总计划数',
            value: total,
            icon: 'el-icon-s-order'
          },
          {
            title: '生产中',
            value: inProgress,
            icon: 'el-icon-loading'
          },
          {
            title: '已完成',
            value: completed,
            icon: 'el-icon-success'
          },
          {
            title: '生产效率',
            value: parseFloat(efficiency),
            decimals: 2,
            icon: 'el-icon-data-line'
          }
        ]

        this.loading = false
      } catch (error) {
        console.error('初始化数据失败:', error)
        this.error = error.toString()
        this.$message.error('初始化数据失败')
        this.loading = false
      }
    },

    /** 获取生产计划列表 */
    async getList() {
      try {
        // 直接使用本地数据，不调用API
        setTimeout(() => {
          this.loading = false
        }, 500)
      } catch (error) {
        console.error('获取生产计划列表失败:', error)
        this.$message.error('获取生产计划列表失败')
        this.loading = false
      }
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        orderNo: '',
        customerName: '',
        status: '',
        dateRange: []
      }
      this.getList()
    },

    /** 编辑按钮操作 */
    handleEdit(row) {
      this.editDialog.form = {
        id: row.id,
        orderNo: row.orderNo,
        customerName: row.customerName,
        productName: row.productName,
        startTime: row.startTime,
        endTime: row.estimatedEndTime,
        status: row.status,
        remark: row.description
      }
      this.editDialog.visible = true
    },

    /** 提交编辑 */
    async submitEdit() {
      this.$refs.editForm.validate(async valid => {
        if (valid) {
          this.editDialog.loading = true
          try {
            await productionApi.updatePlan(this.editDialog.form)
            this.$message.success('更新成功')
            this.editDialog.visible = false
            this.getList()
          } catch (error) {
            console.error('更新生产计划失败:', error)
            this.$message.error('更新生产计划失败')
          } finally {
            this.editDialog.loading = false
          }
        }
      })
    },

    /** 取消编辑 */
    cancelEdit() {
      this.editDialog.visible = false
      this.editDialog.form = {
        id: null,
        orderNo: '',
        customerName: '',
        productName: '',
        startTime: '',
        endTime: '',
        status: '',
        remark: ''
      }
    },

    /** 每页显示条数改变 */
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getList()
    },

    /** 当前页改变 */
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },

    // 格式化日期
    formatDateTime(date) {
      return date ? moment(date).format('YYYY-MM-DD HH:mm:ss') : '-'
    },

    // 格式化状态
    formatStatus(status) {
      const statusMap = this.statusOptions.reduce((acc, curr) => {
        acc[curr.value] = curr.label
        return acc
      }, {})
      return statusMap[status] || '未知'
    },

    // 获取状态标签类型
    getStatusType(status) {
      const typeMap = {
        0: 'info',
        1: 'warning',
        2: 'primary',
        3: 'success',
        4: 'success'
      }
      return typeMap[status] || 'info'
    },

    // 查询
    handleFilter() {
      this.filterForm.pageNum = 1
      this.getList()
    },
    // 重置筛选条件
    resetFilter() {
      this.filterForm = {
        planNo: '',
        orderNo: '',
        status: '',
        startDate: '',
        endDate: '',
        pageNum: 1,
        pageSize: 10
      }
      this.dateRange = []
      this.getList()
    },
    // 查看详情
    handleDetail(row) {
      this.$router.push({
        name: 'ProductionPlanDetail',
        params: { id: row.id }
      })
    },
    // 开始生产
    handleStart(row) {
      this.$confirm('确认开始生产该计划?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟请求
        const index = this.tableData.findIndex(item => item.id === row.id)
        if (index !== -1) {
          this.tableData[index].status = 1
          this.$message.success('已开始生产')
          
          this.getList()
        }
      }).catch(() => {})
    },
    // 完成生产
    handleComplete(row) {
      this.$confirm('确认完成该生产计划?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟请求
        const index = this.tableData.findIndex(item => item.id === row.id)
        if (index !== -1) {
          this.tableData[index].status = 2
          this.tableData[index].actualQuantity = this.tableData[index].planQuantity
          this.$message.success('生产计划已完成')
          
          this.getList()
        }
      }).catch(() => {})
    },
    // 取消计划
    handleCancel(row) {
      this.$confirm('确认取消该生产计划?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟请求
        const index = this.tableData.findIndex(item => item.id === row.id)
        if (index !== -1) {
          this.tableData[index].status = 3
          this.$message.success('生产计划已取消')
          
          this.getList()
        }
      }).catch(() => {})
    },
    // 创建排产计划
    handleCreatePlan() {
      this.dialogTitle = '创建排产计划'
      this.planForm = {
        id: null,
        orderId: '',
        productId: '',
        planQuantity: 1,
        dateRange: [],
        priority: 1,
        remark: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.planForm && this.$refs.planForm.clearValidate()
      })
    },
    // 提交排产计划
    submitPlan() {
      this.$refs.planForm.validate(valid => {
        if (!valid) return
        
        this.submitLoading = true
        
        // 模拟提交请求
        setTimeout(() => {
          if (this.planForm.id) {
            // 更新
            const index = this.tableData.findIndex(item => item.id === this.planForm.id)
            if (index !== -1) {
              const orderInfo = this.orderOptions.find(o => o.value === this.planForm.orderId)
              const productInfo = this.productOptions.find(p => p.value === this.planForm.productId)
              
              this.tableData[index] = {
                ...this.tableData[index],
                orderNo: orderInfo ? orderInfo.orderNo : '',
                productName: productInfo ? productInfo.label : '',
                planQuantity: this.planForm.planQuantity,
                startDate: this.planForm.dateRange[0],
                endDate: this.planForm.dateRange[1],
                priority: this.planForm.priority,
                remark: this.planForm.remark
              }
            }
            this.$message.success('排产计划更新成功')
          } else {
            // 创建
            const orderInfo = this.orderOptions.find(o => o.value === this.planForm.orderId)
            const productInfo = this.productOptions.find(p => p.value === this.planForm.productId)
            
            const newPlan = {
              id: Date.now(),
              planNo: 'PP' + new Date().getTime().toString().substring(3),
              orderNo: orderInfo ? orderInfo.orderNo : '',
              productName: productInfo ? productInfo.label : '',
              planQuantity: this.planForm.planQuantity,
              actualQuantity: 0,
              startDate: this.planForm.dateRange[0],
              endDate: this.planForm.dateRange[1],
              status: 0,
              priority: this.planForm.priority,
              remark: this.planForm.remark
            }
            
            this.tableData.unshift(newPlan)
            this.total += 1
            this.$message.success('排产计划创建成功')
          }
          
          this.dialogVisible = false
          this.submitLoading = false
          
          this.getList()
        }, 1000)
      })
    },
    // 获取进度状态
    getProgressStatus(row) {
      const progress = this.calculateProgress(row)
      if (progress >= 100) return 'success'
      if (progress >= 80) return 'warning'
      return ''
    },
    // 获取优先级类型
    getPriorityType(priority) {
      const typeMap = {
        0: 'info',
        1: 'success',
        2: 'warning',
        3: 'danger'
      }
      return typeMap[priority] || 'info'
    },
    // 获取优先级名称
    getPriorityName(priority) {
      return this.priorityOptions[priority] || '未知'
    },
    // 获取表格大小
    getTableSize() {
      return this.tableSize
    },
    // 处理表格选择
    handleSelectionChange(val) {
      this.selectedRows = val
    },
    // 刷新表格
    refreshTable() {
      this.getList()
    },
    // 导出数据
    handleExport() {
      this.$message.success('正在导出数据，请稍候...')
      // TODO: 实现导出逻辑
    },
    // 处理命令
    handleCommand(command, row) {
      switch (command) {
        case 'start':
          this.handleStart(row)
          break
        case 'complete':
          this.handleComplete(row)
          break
        case 'cancel':
          this.handleCancel(row)
          break
      }
    },
    // 重置表单
    resetForm(formName) {
      this.$refs[formName].resetFields()
    },
    // 计算完成百分比
    calculateProgress(row) {
      if (row.planQuantity === 0) return 0
      return Math.round((row.actualQuantity / row.planQuantity) * 100)
    },
    // 百分比格式化
    percentFormat(percent) {
      return percent + '%'
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
    // 计算统计数据
    calculateStatistics() {
      const total = this.tableData.length
      const inProgress = this.tableData.filter(item => item.status === 1).length
      const completed = this.tableData.filter(item => item.status === 2).length
      const efficiency = completed > 0 ? (completed / total * 100) : 0

      this.statistics = [
        {
          title: '总计划数',
          value: total,
          icon: 'el-icon-s-order'
        },
        {
          title: '生产中',
          value: inProgress,
          icon: 'el-icon-loading'
        },
        {
          title: '已完成',
          value: completed,
          icon: 'el-icon-success'
        },
        {
          title: '生产效率',
          value: parseFloat(efficiency.toFixed(2)),
          icon: 'el-icon-data-line'
        }
      ]
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

/* 统一卡片样式 */
.el-card {
  margin-bottom: 24px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05) !important;
  transition: box-shadow 0.3s ease;
}

.el-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08) !important;
}

/* 头部统计卡片样式 */
.dashboard-header {
  margin-bottom: 24px;
}

.dashboard-card {
  height: 120px;
  background: #fff;
  border-radius: 8px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  padding: 24px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.dashboard-card:hover {
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  position: relative;
  z-index: 1;
}

.card-number {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1.2;
}

.card-title {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.card-icon {
  font-size: 42px;
  color: rgba(0,0,0,0.08);
  margin-left: auto;
}

/* 筛选器样式优化 */
.filter-container {
  background-color: #fff;
  padding: 20px 24px;
}

.filter-item-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.filter-item {
  display: flex;
  align-items: center;
}

.filter-item .label {
  min-width: 80px;
  margin-right: 12px;
  color: #606266;
  font-size: 14px;
}

.filter-item .el-input,
.filter-item .el-select,
.filter-item .el-date-editor {
  flex: 1;
}

.filter-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 8px;
  border-top: 1px solid #ebeef5;
}

/* 表格容器样式优化 */
.table-container {
  background-color: #fff;
  border-radius: 8px;
}

.table-header {
  padding: 20px 24px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  display: flex;
  align-items: center;
}

.header-title::before {
  content: '';
  width: 3px;
  height: 16px;
  background-color: #409EFF;
  margin-right: 8px;
  border-radius: 2px;
}

.header-operations {
  display: flex;
  align-items: center;
  gap: 12px;
}

.table-content {
  padding: 0 24px;
}

/* 表格样式优化 */
.el-table {
  margin: 16px 0;
}

.el-table th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 500;
  height: 48px;
}

.el-table td {
  padding: 12px 0;
}

.el-table--striped .el-table__row--striped td {
  background-color: #fafafa;
}

/* 进度条样式 */
.el-progress-bar__outer {
  background-color: #f0f2f5;
  border-radius: 4px;
}

.el-progress-bar__inner {
  border-radius: 4px;
}

/* 按钮样式统一 */
.el-button--mini {
  padding: 6px 12px;
  font-size: 12px;
  border-radius: 4px;
}

.el-button-group .el-button--mini {
  padding: 6px 12px;
}

/* 标签样式优化 */
.el-tag--mini {
  height: 22px;
  line-height: 20px;
  padding: 0 8px;
  border-radius: 4px;
  font-size: 12px;
}

/* 分页样式优化 */
.pagination-container {
  padding: 16px 24px;
  text-align: right;
  border-top: 1px solid #ebeef5;
  background-color: #fff;
}

/* 对话框样式 */
.el-dialog {
  border-radius: 8px;
}

.el-dialog__header {
  padding: 20px 24px;
  border-bottom: 1px solid #ebeef5;
}

.el-dialog__body {
  padding: 24px;
}

.el-dialog__footer {
  padding: 16px 24px;
  border-top: 1px solid #ebeef5;
}

/* 表单样式优化 */
.el-form-item {
  margin-bottom: 24px;
}

.el-form-item__label {
  font-weight: normal;
  color: #606266;
}

/* 下拉菜单样式优化 */
.el-dropdown-menu {
  padding: 8px 0;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.el-dropdown-menu__item {
  line-height: 32px;
  padding: 0 16px;
  font-size: 13px;
}

.el-dropdown-menu__item:hover {
  background-color: #f5f7fa;
}

/* 视图切换样式 */
.view-switch-container {
  margin-bottom: 24px;
  padding: 16px 24px;
}

.view-switch {
  display: flex;
  justify-content: flex-end;
}

/* 响应式优化 */
@media screen and (max-width: 768px) {
  .app-container {
    padding: 16px;
  }
  
  .filter-item-container {
    grid-template-columns: 1fr;
  }
  
  .dashboard-card {
    height: 100px;
    padding: 16px;
  }
  
  .card-number {
    font-size: 24px;
  }
  
  .card-icon {
    font-size: 36px;
  }
}

/* 表格操作列样式优化 */
.table-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
}

.table-actions .el-button {
  padding: 0 8px;
  font-size: 13px;
  color: #409EFF;
}

.table-actions .el-button:hover {
  color: #66b1ff;
  background: transparent;
}

.table-actions .el-button [class*="el-icon-"] + span {
  margin-left: 4px;
}

.table-actions .el-divider--vertical {
  height: 1em;
  margin: 0 8px;
}

.el-dropdown-menu__item [class*="el-icon-"] {
  margin-right: 8px;
  font-size: 14px;
}

.el-dropdown-menu__item.is-disabled {
  color: #c0c4cc;
}

/* 表格固定列阴影优化 */
.el-table__fixed-right {
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.05);
}

/* 下拉菜单项样式优化 */
.el-dropdown-menu__item {
  display: flex;
  align-items: center;
  padding: 8px 16px;
}

.el-dropdown-menu__item.divided {
  border-top: 1px solid #ebeef5;
  margin-top: 4px;
  padding-top: 8px;
}
</style> 