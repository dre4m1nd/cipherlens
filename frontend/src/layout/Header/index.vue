<template>
  <header>
    <div class="left-box">
      <!-- 收缩按钮 -->
      <button
          type="button"
          class="menu-icon"
          :aria-label="isCollapse ? '展开侧边栏' : '收起侧边栏'"
          :aria-expanded="!isCollapse"
          @click="opendStateChange"
      >
        <i class="sfont head-fold" :class="isCollapse ? 'system-s-unfold' : 'system-s-fold'" aria-hidden="true"></i>
      </button>
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
            {{ userName }}
            <i class="sfont system-xiala" aria-hidden="true"></i>
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
    const userName = computed(() => {
      const info = store.state.user.info || {}
      return info.username || '未登录'
    })
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
      userName,
      opendStateChange,
      handleEditInfo,
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
  background: none;
  border: none;
  padding: 0;
}

.left-box .menu-icon:focus-visible {
  outline: 2px solid var(--system-primary-color);
  outline-offset: -2px;
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