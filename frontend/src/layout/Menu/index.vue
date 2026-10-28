<template>
  <el-scrollbar class="bg-[var(--system-menu-background)]">
    <el-menu
      class="layout-menu system-scrollbar w-full border-none"
      background-color="var(--system-menu-background)"
      text-color="var(--system-menu-text-color)"
      active-text-color="var(--system-primary-color)"
      :default-active="activeMenu"
      :class="isCollapse? 'collapse': ''"
      :collapse="isCollapse"
      :collapse-transition="false"
      :unique-opened="expandOneMenu"
    >
      <menu-item v-for="(menu, key) in allRoutes" :key="key" :menu="menu" />
    </el-menu>
  </el-scrollbar>
</template>

<script lang="js">
import { defineComponent, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import MenuItem from './MenuItem.vue'
import { modules } from '@/router' // 引入手动配置的路由数组

export default defineComponent({
  components: {
    MenuItem
  },
  setup() {
    const store = useStore()
    const router = useRouter()
    const isCollapse = computed(() => store.state.app.isCollapse)
    const expandOneMenu = computed(() => store.state.app.expandOneMenu)
    const allRoutes = computed(() => modules) // 使用手动配置的路由数组，而不是router.getRoutes()
    const route = useRoute()
    const activeMenu = computed(() => {
      const { meta, path } = route;
      if (meta.activeMenu) {
        return meta.activeMenu;
      }
      return path;
    });
    onMounted(() => {

    })
    return {
      isCollapse,
      expandOneMenu,
      allRoutes,
      activeMenu,
    }
  }
})
</script>

<style scoped>
.layout-menu.collapse {
  margin-left: 0px;
}

/* 由于Element Plus菜单组件的内部结构复杂性，保留深度选择器样式 */
:deep(.el-menu-item),
:deep(.el-sub-menu) {
  background-color: var(--system-menu-background) !important;
}

:deep(.el-menu-item i),
:deep(.el-menu-item-group__title),
:deep(.el-sub-menu__title i) {
  color: var(--system-menu-text-color);
}

:deep(.el-menu-item.is-active),
:deep(.el-sub-menu__title.is-active) {
  background-color: var(--system-primary-color) !important;
  color: var(--system-primary-text-color) !important;
}

:deep(.el-menu-item.is-active i),
:deep(.el-sub-menu__title.is-active i) {
  color: var(--system-primary-text-color) !important;
}

:deep(.el-menu-item.is-active:hover),
:deep(.el-sub-menu__title.is-active:hover) {
  background-color: var(--system-primary-color) !important;
  color: var(--system-primary-text-color) !important;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: var(--system-menu-hover-background) !important;
}

:deep(.el-sub-menu.is-active > .el-sub-menu__title),
:deep(.el-sub-menu.is-active > .el-sub-menu__title i) {
  color: var(--system-menu-submenu-active-color) !important;
}

:deep(.el-sub-menu .el-menu-item) {
  background-color: var(--system-menu-children-background) !important;
}

:deep(.el-sub-menu .el-menu-item.is-active) {
  background-color: var(--system-primary-color) !important;
  color: var(--system-primary-text-color) !important;
}

:deep(.el-sub-menu .el-menu-item.is-active:hover) {
  background-color: var(--system-primary-color) !important;
  color: var(--system-primary-text-color) !important;
}

:deep(.el-sub-menu .el-menu-item:hover) {
  background-color: var(--system-menu-hover-background) !important;
}

:deep(.el-sub-menu .el-sub-menu .el-sub-menu__title) {
  background-color: var(--system-menu-children-background) !important;
}

:deep(.el-sub-menu .el-sub-menu .el-sub-menu__title:hover) {
  background-color: var(--system-menu-hover-background) !important;
}
</style>