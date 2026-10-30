<template>
  <button
      type="button"
      class="icon-btn"
      :aria-label="isFullscreen ? '退出全屏' : '进入全屏'"
      :title="isFullscreen ? '退出全屏' : '进入全屏'"
      @click="toggle"
  >
    <i
        class="sfont text-[18px]"
        :class="isFullscreen ? 'system-quanping' : 'system-quanping1'"
        aria-hidden="true"
    ></i>
  </button>
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
.icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  color: inherit;
}

.icon-btn:focus-visible {
  outline: 2px solid var(--system-primary-color);
  outline-offset: 2px;
  border-radius: 2px;
}
</style>
