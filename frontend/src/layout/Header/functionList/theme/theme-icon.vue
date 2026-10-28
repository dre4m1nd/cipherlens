<template>
  <el-tooltip class="item" effect="dark" :content="tip" placement="top">
    <div class="inline-flex w-[50px] h-[50px] shadow-[0_2px_4px_rgba(0,0,0,0.12),0_0_6px_rgba(0,0,0,0.04)] cursor-pointer outline-none rounded-[4px] overflow-hidden" @click="handleClick">
      <div class="w-[18px] flex flex-col">
        <div class="w-[20px] h-[10px]" :style="{ 'background-color': logo }"></div>
        <div class="flex-1" :style="{ 'background-color': menu }"></div>
      </div>
      <div class="flex-1 flex flex-col">
        <div class="h-[10px]" :style="{ 'background-color': header }"></div>
        <div class="flex-1 relative" :style="{ 'background-color': main }">
          <div class="absolute inset-0 flex justify-center items-center" v-if="active === name">
            <i class="sfont system-success text-[22px] font-bold" :style="{'color': activeColor}"></i>
          </div>
        </div>
      </div>
      
    </div>
  </el-tooltip> 
  
</template>

<script lang="js">
import { defineComponent } from 'vue'
export default defineComponent({
  props: {
    name: {
      type: String,
      default: 'default'
    },
    active: {
      type: String,
      default: ''
    },
    menu: {
      type: String,
      defualt: ''
    },
    logo: {
      type: String,
      defualt: ''
    },
    header: {
      type: String,
      defualt: ''
    },
    main: {
      type: String,
      defualt: ''
    },
    tip: {
      type: String,
      default: '默认菜单风格'
    },
    activeColor: {
      type: String,
      default: ''
    }
  },
  setup(props, ctx) {
    // 点击事件，触发v-model修改active值
    const handleClick = () => {
      ctx.emit('update:active', props.name)
    }
    return {
      handleClick
    }
  }
})
</script>

<style scoped>
/* 样式已通过WindiCSS类名实现 */
.theme-icon+.theme-icon {
  margin-left: 12px;
}
</style>