<template>
  <div :class="className" :style="{height:height,width:width}" ref="chartRef" />
</template>

<script setup>
import * as echarts from 'echarts'
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'

const props = defineProps({
  className: {
    type: String,
    default: 'chart'
  },
  width: {
    type: String,
    default: '100%'
  },
  height: {
    type: String,
    default: '300px'
  },
  yName: {
    type: String,
    default: 'kM·h'
  },
  xData: {
    type: Array,
    default: () => {
      return ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12']
    }
  },
  yData: {
    type: Array,
    default: () => {
      return [79, 52, 200, 334, 390, 330, 34, 79, 52, 123, 334, 34, 56]
    }
  }
})

const chart = ref(null)
const chartRef = ref()

onMounted(() => {
  nextTick(() => {
    initChart()
  })
})

onBeforeUnmount(() => {
  if (!chart.value) {
    return
  }
  chart.value.dispose()
  chart.value = null
})

watch(() => props.yData, (value) => {
  nextTick(() => {
    if (!chart.value) {
      initChart()
      return;
    }
    chart.value.dispose();
    chart.value = null;
    initChart();
  });
})

function initChart() {
  chart.value = echarts.init(chartRef.value)

  chart.value.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      },
      backgroundColor: 'rgba(28, 37, 80, 0.9)',
      borderColor: '#2969e8',
      textStyle: {
        color: '#fff'
      }
    },
    grid: {
      top: '5%',
      left: '2%',
      right: '2%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        boundaryGap: false,
        data: props.xData,
        axisLabel: {
          color: '#fff'
        },
        axisLine: {
          lineStyle: {
            color: '#1be5e7'
          }
        },
        splitLine: {
          lineStyle: {
            color: '#1d3666'
          }
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: props.yName,
        nameTextStyle: {
          color: '#fff'
        },
        axisLabel: {
          color: '#fff'
        },
        splitLine: {
          lineStyle: {
            color: '#1d3666'
          }
        }
      }
    ],
    series: [
      {
        name: '能耗',
        type: 'line',
        stack: 'Total',
        smooth: true,
        lineStyle: {
          width: 0
        },
        showSymbol: false,
        areaStyle: {
          opacity: 0.8,
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: 'rgba(41, 105, 232, 0.8)'
            },
            {
              offset: 1,
              color: 'rgba(27, 229, 231, 0.3)'
            }
          ])
        },
        emphasis: {
          focus: 'series'
        },
        data: props.yData
      }
    ]
  })
}
</script> 