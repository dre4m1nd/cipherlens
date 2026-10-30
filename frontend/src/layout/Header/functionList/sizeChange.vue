<template>
  <el-dropdown @command="handleCommand" size="medium">
    <button type="button" class="icon-btn" aria-label="界面尺寸" title="界面尺寸">
      <i class="sfont system-zuixiaohua" aria-hidden="true"></i>
    </button>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="d in list"
          :key="d.size"
          :command="d.size"
          :disabled=" elementSize === d.size "
        >
          {{ d.name }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script lang="js">
import { defineComponent, computed, unref } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
export default defineComponent({
  setup() {
    const store = useStore()
    const route = useRoute()
    const elementSize = computed(() => store.state.app.elementSize)
    const list = [
      { size: 'large', name: '大' },
      { size: 'default', name: '默认' },
      { size: 'small', name: '小' },
    ]
    const { fullPath } = unref(route)
    return {
      list,
      elementSize,
      fullPath
    }
  },
  methods: {
    handleCommand(command) {
      this.$store.commit('app/stateChange', {
        name: 'elementSize',
        value: command
      })
    },
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