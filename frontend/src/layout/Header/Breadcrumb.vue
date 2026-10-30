<template>
  <el-breadcrumb class="app-breadcrumb hidden-sm-and-down inline-block text-base leading-[50px]" separator="/">
    <transition-group appear name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in levelList" :key="item.path">
        <span
          v-if="item.redirect === 'noRedirect' || index == levelList.length - 1"
          class="text-[var(--system-header-breadcrumb-text-color)] cursor-text"
        >{{ item.meta.title }}</span>
        <router-link
          v-else
          :to="resolveTarget(item)"
          class="text-[var(--system-header-text-color)] hover:underline"
        >
          {{ item.meta.title }}
        </router-link>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script lang="js">
import { ref, defineComponent, watch } from "vue";
import { useRoute } from "vue-router";
import { isBackMenu } from '@/config'
export default defineComponent({
  name: "BreadCrumb",
  setup() {
    const levelList = ref([]);
    const route = useRoute();
    const getBreadcrumb = () => {
      let matched = route.matched.filter(item => item.meta && item.meta.title);
      const first = matched[0];
      levelList.value = matched.filter(
        item => item.meta && item.meta.title && item.meta.breadcrumb !== false
      );
    };
    getBreadcrumb();
    watch(
      () => route.path,
      () => getBreadcrumb()
    );
    const resolveTarget = (item) => {
      return item.redirect ? String(item.redirect) : item.path;
    };
    return { levelList, resolveTarget, isBackMenu };
  }
});
</script>

<style scoped>
/* 面包屑的基础样式已通过WindiCSS类名实现 */
</style>