<template>
  <div>
    <div title="系统设置" @click="drawerChange(true)">
      <i class="sfont system-shezhi cursor-pointer focus:outline-none"></i>
    </div>
    <el-drawer
      title="系统设置"
      v-model="drawer"
      size="300px"
      :show-close="false"
      direction="rtl">
      <h3 class="mt-[40px] mb-[20px] text-[rgba(0,0,0,.85)] text-[14px] leading-[22px] text-left px-5 first:mt-0">整体风格设置</h3>
      <div class="theme-box text-left pl-5">
        <theme-icon
          :active="state.style"
          @update:active="state.style = $event"
          v-for="(row, index) in style"
          :key="index"
          :name="index"
          :tip="row.name"
          :logo="row.logo.background"
          :menu="row.menu.background"
          :header="row.header.background"
          :main="row.container.background"
          :activeColor="row.page.color"
        ></theme-icon>
      </div>
      <h3 class="mt-[40px] mb-[20px] text-[rgba(0,0,0,.85)] text-[14px] leading-[22px] text-left px-5">主题色</h3>
      <div class="theme-box text-left pl-5">
        <theme-color
          v-for="(item, key) in themeColorArr"
          :active="state.primaryColor"
          :activeTextColor="state.primaryTextColor"
          @update:active="state.primaryColor = $event"
          @update:activeTextColor="state.primaryTextColor = $event"
          :key="key"
          :color="item.color"
          :textColor="item.textColor"
          :tip="item.tip"
        ></theme-color>
      </div>
      <h3 class="mt-[40px] mb-[20px] text-[rgba(0,0,0,.85)] text-[14px] leading-[22px] text-left px-5">其他设置</h3>
      <div class="list px-5 py-0">
        <div class="list-item flex justify-between items-center mb-[15px]" v-for="option in options" :key="option.name">
          <span class="text-[16px]">{{ option.name }}</span>
          <el-switch
            v-model="option.value"
            active-color="#13ce66"
            inactive-color="#ff4949"
            @change="change(option)"
          >
          </el-switch>
        </div>
      </div>
  </el-drawer>
  </div>
</template>

<script lang="js">
import { defineComponent, ref, reactive, computed, watch } from 'vue'
import { useStore } from 'vuex'
import themeIcon from './theme/theme-icon.vue'
import themeColor from './theme/theme-color.vue'
import { style } from '@/theme/index'

export default defineComponent({
  components: {
    themeIcon,
    themeColor
  },
  setup() {
    const store = useStore()
    // 只取值，不做computed
    const state = reactive({
      style: store.state.app.theme.state.style,
      primaryColor: store.state.app.theme.state.primaryColor,
      primaryTextColor: store.state.app.theme.state.primaryTextColor,
      menuType: store.state.app.theme.state.menuType
    })
    const themeColorArr = [
      { color: '#409eff', textColor: '#fff', tip: '默认蓝' },
      { color: '#d60f20', textColor: '#fff', tip: '玫瑰红' },
      { color: '#ac25e6', textColor: '#fff', tip: '优雅紫' },
      { color: '#4dc86f', textColor: '#fff', tip: '故事绿' },
      { color: '#13c2c2', textColor: '#fff', tip: '明青' },
      { color: '#333', textColor: '#fff', tip: '极客黑' }
    ]
    const setTheme = () => {
      const userTheme = style[state.style]
      const body = document.getElementsByTagName('body')[0]
      // 设置全局顶部body上的class名称，即为主题名称，便于自定义配置符合当前主题的样式统一入口
      body.setAttribute('data-theme', state.style)
      // 需要设置的颜色参照theme.scss，位置：assets/style/theme.scss
      // 设置主题色
      body.style.setProperty('--system-primary-color', state.primaryColor)
      for (let i in userTheme) {
        if (i === 'name') {
          continue;
        }
        const item = userTheme[i]
        for (let y in item) {
          let cssVarName = '--system-' + i + '-' + y.replace(/([A-Z])/g, "-$1").toLowerCase()
          body.style.setProperty(cssVarName, item[y])
        }
      }
    }
    // 监听数据的变化
    watch(state, (newVal) => {
      const theme = {
        state: {
          ...state
        }
      }
      store.commit('app/stateChange', {
        name: 'theme',
        value: theme
      })
      setTheme()
    })
    let drawer = ref(false)
    const options = reactive([
      { name: '显示logo', value: store.state.app.showLogo, store: 'showLogo' },
      { name: '显示面包屑导航', value: store.state.app.showTabs, store: 'showTabs' },
      { name: '保持一个菜单展开', value: store.state.app.expandOneMenu, store: 'expandOneMenu' }
    ])
    const drawerChange = (value) => {
      drawer.value = value
    }
    const change = (option) => {
      store.commit(`app/stateChange`, { name: option.store, value: option.value })
    }
    setTheme()
    return {
      drawer,
      options,
      state,
      style,
      themeColorArr,
      drawerChange,
      change
    }
  }
})
</script>

<style scoped>
/* 样式已通过WindiCSS类名实现 */
</style>