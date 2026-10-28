<template>
  <header>
    <div class="left-box">
      <!-- 收缩按钮 -->
      <div class="menu-icon" @click="opendStateChange">
        <i class="sfont head-fold" :class="isCollapse ? 'system-s-unfold' : 'system-s-fold'" ></i>
      </div>
      <Breadcrumb />
    </div>
    <div class="right-box">
      <!-- 快捷功能按钮 -->
      <div class="function-list">
        <div class="function-list-item hidden-sm-and-down"><Full-screen /></div>
        <div class="function-list-item"><SizeChange /></div>
        <div class="function-list-item hidden-sm-and-down"><Theme /></div>
        <div class="function-list-item hidden-sm-and-down"><Github /></div>
      </div>
      <!-- 用户信息 -->
      <div class="user-info">
        <el-dropdown>
          <span class="el-dropdown-link">
            后台管理系统
            <i class="sfont system-xiala"></i>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleEditInfo">修改信息</el-dropdown-item>
          <el-dropdown-item divided @click="loginOut">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </header>
</template>

<script lang="js">
import { defineComponent, computed, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import FullScreen from './functionList/fullscreen.vue'
import SizeChange from './functionList/sizeChange.vue'
import Github from './functionList/github.vue'
import Theme from './functionList/theme.vue'
import Breadcrumb from './Breadcrumb.vue'
export default defineComponent({
  components: {
    FullScreen,
    Breadcrumb,
    SizeChange,
    Github,
    Theme
  },
  setup() {
    const store = useStore()
    const router = useRouter()
    const route = useRoute()
    const isCollapse = computed(() => store.state.app.isCollapse)
    // isCollapse change to hide/show the sidebar
    const opendStateChange = () => {
      store.commit('app/isCollapseChange', !isCollapse.value)
    }

    // 修改信息
    const handleEditInfo = () => {
      router.push('/user/edit')
    }

    // 退出登录
    const loginOut = () => {
      ElMessageBox.confirm('确定退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        store.dispatch('user/loginOut')
      }).catch(() => {})
    }
    
    return {
      isCollapse,
      opendStateChange,
      loginOut
    }
  }
})
</script>

<style lang="css" scoped>
header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  background-color: var(--system-header-background);
  padding-right: 22px;
}

.left-box {
  height: 100%;
  display: flex;
  align-items: center;
}

.left-box .menu-icon {
  width: 60px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 25px;
  font-weight: 100;
  cursor: pointer;
  margin-right: 10px;
}

.left-box .menu-icon:hover {
  background-color: var(--system-header-item-hover-color);
}

.left-box .menu-icon i {
  color: var(--system-header-text-color);
}

.right-box {
  display: flex;
  justify-content: center;
  align-items: center;
}

.right-box .function-list {
  display: flex;
}

.right-box .function-list .function-list-item {
  width: 30px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.right-box .function-list .function-list-item :deep(i) {
  color: var(--system-header-text-color);
}

.right-box .user-info {
  margin-left: 20px;
}

.right-box .user-info .el-dropdown-link {
  color: var(--system-header-breadcrumb-text-color);
}

.head-fold {
  font-size: 20px;
}
</style>