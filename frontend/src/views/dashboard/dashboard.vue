<template>
  <div class="app-container home">
    <div class="station-top">
      <el-row :gutter="12" class="mt5">
        <el-col :span="6">
          <div class="top-item-box item-box-one" style="display: flex;">
            <div style="flex:2;height:100%;">
              <div>入库</div>
              <div style="text-align:center;margin-top:30px;"><span style="font-size:26px;font-weight:bold;">33</span>
              </div>
            </div>
            <div style="flex:3;display: flex;flex-direction:column;justify-content:space-evenly">
              <div>待入库：15</div>
              <div>待质检：15</div>
              <div>待上架：2</div>
              <div>待完成：1 </div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="top-item-box item-box-two" style="display: flex;">
            <div style="flex:2;height:100%;">
              <div>出库</div>
              <div style="text-align:center;margin-top:30px;"><span style="font-size:26px;font-weight:bold;"
              >20</span></div>
            </div>
            <div style="flex:3;display: flex;flex-direction:column;justify-content:space-evenly">
              <div>待配货：5</div>
              <div>待波次：5</div>
              <div>待拣货：5</div>
              <div>待出库：5</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="top-item-box item-box-three" style="display: flex;">
            <div style="flex:2;height:100%;">
              <div>其他</div>
              <div style="text-align:center;margin-top:30px;"><span style="font-size:26px;font-weight:bold;"
              >15</span></div>
            </div>
            <div style="flex:3;display: flex;flex-direction:column;justify-content:space-evenly">
              <div>待截单：5</div>
              <div>异常单：5</div>
              <div>今日到货：5</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="top-item-box item-box-four" style="display: flex;">
            <div style="flex:2;height:100%;">
              <div>库存预警</div>
              <div style="text-align:center;margin-top:30px;"><span style="font-size:26px;font-weight:bold;">5</span>
              </div>
            </div>
            <div style="flex:3;display: flex;flex-direction:column;justify-content:space-evenly">
              <div>松陵仓：1</div>
              <div>盛泽仓：2</div>
              <div>园区仓：2</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    <div class="station-middle">
      <el-row :gutter="12">
        <el-col :span="6">
          <el-card class="box-card" shadow="never">
            <div class="card-title">仓库货物占比</div>
            <div style="height: calc(100% - 30px);">
              <StationPie height="100%"></StationPie>
              <div></div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="18">
          <el-card class="box-card" shadow="never">
            <div style="display:flex;justify-content: space-between;align-items: center;">
              <div class="card-title">生产入库趋势</div>
              <el-radio-group v-model="tabPosition" @change="dateChange">
                <!-- <el-radio-button label="day">当日</el-radio-button> -->
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="year">今年</el-radio-button>
              </el-radio-group>
            </div>
            <div style="height: calc(100% - 30px);">
              <StationBar height="100%" :chartData="barChartData" :xName="barXName"/>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <div class="station-bottom">
      <el-row :gutter="12">
        <el-col :span="6">
          <el-card class="box-card" shadow="never">
            <div class="card-title">近7日销售出库</div>
            <div style="height: calc(100% - 30px);">
              <StationLine height="100%" itemColor="#ee4368" yName="件" :chartData="lineDataOne"/>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="box-card" shadow="never">
            <div class="card-title">近7日领料出库</div>
            <div style="height: calc(100% - 30px);">
              <StationLine height="100%" :chartData="lineDataTwo" yName="件" itemColor="#5470c6"/>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="box-card" shadow="never">
            <div class="card-title">近7日移库</div>
            <div style="height: calc(100% - 30px);">
              <StationLine height="100%"  :chartData="lineDataThree" yName="件"
                           itemColor="#c58bea"
              />
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="box-card" shadow="never">
            <div class="card-title">近7日退货入库</div>
            <div style="height: calc(100% - 30px);">
              <StationLine height="100%" yName="件" :chartData="lineDataFour"
                           itemColor="#c7a428"
              />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script setup>
import StationPie from './components/StationPie.vue'
import StationLine from './components/StationLine.vue'
import StationBar from './components/StationBar.vue'
import { onMounted, ref } from 'vue'
import moment from 'moment';

const tabPosition = ref('month')
const barChartData = ref({
  yData: [79, 68, 56, 72, 51, 63, 67, 71, 58, 81, 64, 77, 56, 69]
})
const barXName = ref('日')
const lineDataOne = ref({
  yData: [79, 65, 21, 67, 21, 89, 56],
})
const lineDataTwo = ref({
  yData: [45, 72, 16, 37, 64, 28, 46],
})
const lineDataThree = ref(
  {
    yData: [16, 27, 37, 16, 27, 21, 11],
  }
)
const lineDataFour = ref({
  yData: [134, 107, 94, 173, 37, 143, 86],
})

onMounted((()=>{
  initTime();
  dateChange('month');
}))

// 时间类型选择
function dateChange(value) {
  let date = new Date()
  let month = date.getMonth() + 1
  let day = date.getDate()
  let barXData = []
  let barYData = []
  if(value === 'year') {
    for(let i = 0; i < month; i++) {
      barXData.push(moment().subtract(i, 'months').format('YYYY-MM'))
      barYData.push(Math.floor(Math.random()*(180-120+1))+120)
    }
    barXName.value = '月'
  } else {
    for(let i = 0; i < day; i++) {
      barXData.push(moment().subtract(i, 'days').format('MM-DD'))
      barYData.push(Math.floor(Math.random()*(30-15+1))+15)
    }
    barXName.value = '日'
  }
  barChartData.value = {
    xData: barXData.reverse(),
    yData: barYData
  }
}
// 初始化时间模拟数据
function initTime() {
  let lineXData = []
  for(let i = 0; i < 7; i++) {
    lineXData.push(moment().subtract(i, 'days').format('MM-DD'))
  }
  lineXData = lineXData.reverse()
  lineDataOne.value.xData = lineXData
  lineDataTwo.value.xData = lineXData
  lineDataThree.value.xData = lineXData
  lineDataFour.value.xData = lineXData
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
  background-color: #f0f2f5;
}
.home {
  .station-top {
    margin-bottom: 20px;
    .top-item-box {
      height: 120px;
      padding: 15px;
      border-radius: 4px;
      color: #fff;
      &.item-box-one {
        background-color: #409EFF;
      }
      &.item-box-two {
        background-color: #67C23A;
      }
      &.item-box-three {
        background-color: #E6A23C;
      }
      &.item-box-four {
        background-color: #F56C6C;
      }
    }
  }
  .station-middle {
    margin-bottom: 20px;
    .box-card {
      height: 350px;
      .card-title {
        font-size: 16px;
        font-weight: bold;
        margin-bottom: 15px;
      }
    }
  }
  .station-bottom {
    .box-card {
      height: 350px;
      .card-title {
        font-size: 16px;
        font-weight: bold;
        margin-bottom: 15px;
      }
    }
  }
}
.mt5 {
  margin-top: 5px;
}
</style>