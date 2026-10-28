<template>
  <div :title="isFullscreen ? '退出全屏' : '全屏'">
    <i class="sfont cursor-pointer text-[18px] focus:outline-none" :class="isFullscreen ? 'system-quanping':'system-quanping1'" @click="toggle"></i>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { useFullscreen } from '@vueuse/core'

export default defineComponent({
  name: 'fullscreen',
  setup() {
    const { isFullscreen, toggle } = useFullscreen()
    return {
      isFullscreen,
      toggle
    }
  }
})
</script>

<style scoped>
/* 样式已通过WindiCSS类名实现 */
</style>
