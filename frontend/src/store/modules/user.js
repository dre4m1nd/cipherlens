import { loginApi, registerApi, getInfoApi, updateUserApi, resetPasswordApi, loginOutApi } from '@/api/user'
const state = () => ({
  token: '', // 登录token
  info: {},  // 用户信息
})

// getters
const getters = {
  token(state) {
    return state.token
  }
}

// mutations
const mutations = {
  tokenChange(state, token) {
    state.token = token
  },
  infoChange(state, info) {
    state.info = info
  }
}

// actions
const actions = {
  // login by login.vue
  login({ commit, state, getters }, params) {
    return new Promise((resolve, reject) => {
      loginApi(params)
      .then(res => {
        // 后端返回 { code, message, data: { token } }
        const token = res.data && res.data.token ? res.data.token : res.data
        commit('tokenChange', token)
        resolve(token)
        // 异步获取用户信息，失败不影响登录
        setTimeout(() => {
          this.dispatch('user/getInfo').catch(() => {
            // 用户信息获取失败不影响登录流程
          })
        }, 0)
      }).catch(err => {
        reject(err)
      })
    })
  },
  
  // register by register.vue
  register({ commit, dispatch }, params) {
    return new Promise((resolve, reject) => {
      registerApi(params)
      .then(res => {
        resolve(res.data)
      }).catch(err => {
        reject(err)
      })
    })
  },
  
  // update user info
  updateUser({ commit }, params) {
    return new Promise((resolve, reject) => {
      updateUserApi(params)
      .then(res => {
        // 更新用户信息
        commit('infoChange', res.data)
        resolve(res.data)
      }).catch(err => {
        reject(err)
      })
    })
  },
  
  // reset password
  resetPassword({ commit }, params) {
    return new Promise((resolve, reject) => {
      resetPasswordApi(params)
      .then(res => {
        resolve(res.data)
      }).catch(err => {
        reject(err)
      })
    })
  },
  
  // get user info after user logined
  getInfo({ commit }) {
    return new Promise((resolve, reject) => {
      getInfoApi()
      .then(res => {
        // 后端直接返回 UpdateUserForm（{ username, password }）
        commit('infoChange', res.data)
        resolve(res.data)
      })
      .catch(err => {
        reject(err)
      })
    })
  },

  // login out the system after user click the loginOut button
  loginOut({ commit }) {
    loginOutApi()
    .then(res => {

    })
    .catch(error => {

    })
    .finally(() => {
      localStorage.removeItem('tabs')
      localStorage.removeItem('vuex')
      sessionStorage.removeItem('vuex')
      // 清除token
      commit('tokenChange', '')
      // 清除用户信息
      commit('infoChange', {})
      // 跳转到登录页面
      location.href = '/#/login'
    })
  }
}

export default {
  namespaced: true,
  state,
  actions,
  getters,
  mutations
}
