<template>
  <el-sub-menu v-if="!menu.hideMenu && showMenuType === 2" :index="pathResolve" :show-timeout="0" :hide-timeout="0">
    <template #title>
      <icon-renderer :icon="menu.meta.icon" v-if="menu.meta.icon" />
      <span>{{ menu.meta.title }}</span>
    </template>
    <menu-item v-for="(item, key) in menu.children" :key="key" :menu="item" :basePath="pathResolve" />
  </el-sub-menu>
  <app-link v-else-if="!menu.hideMenu && showMenuType === 1" :to="pathResolve">
    <el-menu-item :index="pathResolve" v-if="!menu.children[0].children || menu.children[0].children.length === 0">
      <icon-renderer :icon="menu.children[0].meta.icon || menu.meta.icon" v-if="menu.children[0].meta.icon || menu.meta.icon" />
      <template #title>{{ menu.children[0].meta.title }}</template>
    </el-menu-item>
    <el-sub-menu v-else :index="pathResolve" :show-timeout="0" :hide-timeout="0">
      <template #title>
        <icon-renderer :icon="menu.children[0].meta.icon || menu.meta.icon" v-if="menu.children[0].meta.icon || menu.meta.icon" />
        <span>{{ menu.children[0].meta.title }}</span>
      </template>
      <menu-item v-for="(item, key) in menu.children[0].children" :key="key" :menu="item" :basePath="pathResolve" />
    </el-sub-menu>
  </app-link>
  <app-link v-else-if="!menu.hideMenu" :to="pathResolve">
    <el-menu-item :index="pathResolve">
      <icon-renderer :icon="menu.meta.icon" v-if="menu.meta.icon" />
      <template #title>{{ menu.meta.title }}</template>
    </el-menu-item>
  </app-link>
</template>

<script lang="js">
import { defineComponent, computed, h } from 'vue'
import appLink from './Link.vue'
import { isBackMenu } from '@/config'
import { ElIcon } from 'element-plus'
import { Key, User } from '@element-plus/icons-vue'
export default defineComponent({
  name: 'menu-item',
  props: {
    menu: {
      type: Object,
      required: true
    },
    basePath: {
      type: String,
      default: ''
    }
  },
  components: {
    appLink,
    IconRenderer: defineComponent({
      props: {
        icon: {
          type: [String, Object],
          required: true
        }
      },
      setup(props) {
        // 支持的Element Plus图标映射
        const iconMap = {
          Key,
          User
        }
        
        return () => {
          if (typeof props.icon === 'string') {
            // 如果是字符串，按CSS类名图标处理
            return h('i', { class: props.icon })
          } else if (props.icon.name && iconMap[props.icon.name]) {
            // 如果是对象且有name属性，按Element Plus图标组件处理
            return h(ElIcon, null, () => h(iconMap[props.icon.name]))
          }
          return null
        }
      }
    })
  },
  setup(props) {
    const menu = props.menu
    // todo: 优化if结构
    const showMenuType = computed(() => { // 0: 无子菜单， 1：有1个子菜单， 2：显示上下级子菜单
      if (menu.children && (menu.children.length > 1 || (menu.children.length === 1 && menu.alwayShow))) {
        return 2
      } else if (menu.children && menu.children.length === 1 && !menu.alwayShow) {
        return 1
      } else {
        return 0
      }
    })
    // todo: 优化多层if
    const pathResolve = computed(() => {
      let path = ''
      if (showMenuType.value === 1) {
        if (menu.children[0].path.charAt(0) === '/') {
          path = menu.children[0].path
        } else {
          let char = '/'
          if (menu.path.charAt(menu.path.length - 1) === '/') {
            char = ''
          }
          path = menu.path + char + menu.children[0].path
        }
      } else {
        path = menu.path
      }
      path = props.basePath ? props.basePath + '/' + path : path
      return path
    })
    return {
      showMenuType,
      pathResolve,
      isBackMenu
    }
  }
})
</script>

<style lang="scss" scoped>
  .el-sub-menu {
    text-align: left;
  }
  .el-menu-item {
    text-align: left;
  }
  .el-menu-item i, .el-sub-menu__title i {
    padding-right: 8px;
  }
</style>