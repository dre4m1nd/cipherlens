<template>
  <el-config-provider :size="size">
    <a href="#main-content" class="skip-link" @click.prevent="focusMain">跳到主要内容</a>
    <main id="main-content" ref="mainRef" tabindex="-1">
      <router-view></router-view>
    </main>
  </el-config-provider>
</template>

<script lang="js">
import { defineComponent, computed, ref } from 'vue'
import { useStore } from 'vuex'
export default defineComponent({
  name: 'App',
  setup() {
    const store = useStore()
    const size = computed(() => store.state.app.elementSize)
    const mainRef = ref(null)
    // 哈希路由下不能用 href 跳锚点，改为聚焦主内容区
    const focusMain = () => {
      if (mainRef.value) {
        mainRef.value.focus()
      }
    }
    return {
      size,
      mainRef,
      focusMain,
    }
  }
})
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  width: 100%;
  height: 100vh;
}

#main-content {
  height: 100%;
}

#main-content:focus {
  outline: none;
}

.skip-link {
  position: absolute;
  left: -9999px;
  top: 0;
  z-index: 9999;
  padding: 8px 16px;
  background: #ffffff;
  color: #2c3e50;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.skip-link:focus {
  left: 8px;
  top: 8px;
}
</style>
