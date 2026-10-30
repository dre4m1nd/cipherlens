/**
* 使用说明：用户只需传入options即可，options请参照官网示例中的options
* 本组件采用整包引入echarts的方法，用于适配所有的echarts控件
* 如需按需加载引入echarts，可参照写法：echarts官网/在打包环境中使用ECharts
*/
<template>
  <div
      ref="chart"
      class="chart"
      :role="ariaLabel ? 'img' : undefined"
      :aria-label="ariaLabel || undefined"
  />
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as echarts from 'echarts'
import { useEventListener } from '@vueuse/core'
const props = defineProps({
  option: Object,
  // 图表的文本替代，读屏会读这一句；不传则不加 role="img"
  ariaLabel: String
})
const chart = ref(null)
let myChart = null
// 在onMounted事件才能拿到真实dom
onMounted(() => {
  const dom = chart.value
  if (dom) {
    let option = props.option
    // 需要在页面Dom元素加载后再初始化echarts对象
    myChart = echarts.init(dom)
    myChart.setOption(option)
    // 自动监听加自动销毁
    useEventListener('resize', () => myChart && myChart.resize())
    watch(() => props.option, (newVal) => {
      myChart.setOption(newVal)
    },{ deep: true })
  }

  // 路由切走时销毁实例，避免 echarts 与 canvas 泄漏
  onBeforeUnmount(() => {
    if (myChart) {
      myChart.dispose()
      myChart = null
    }
  })
})
</script>

<style lang="scss" scoped>
  .chart {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
  }
</style>