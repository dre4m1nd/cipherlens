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
        // 兼容后端返回的两种token格式：res.data.token 或 res.data直接是token
        const token = res.data.token || res.data;
        console.log('Login response:', res)
        console.log('Token received:', token)
        commit('tokenChange', token)
        console.log('Token in state after commit:', state.token)
        console.log('Token from getter:', getters.token)
        // 直接登录成功，不再等待getInfo接口
        resolve(token)
        // 异步获取用户信息，失败不影响登录
        setTimeout(() => {
          this.dispatch('getInfo').catch(infoErr => {
            console.error('Get info error (non-blocking):', infoErr)
            // 可以在这里显示警告，但不影响登录
          })
        }, 0)
      }).catch(err => {
        console.error('Login error:', err)
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
        commit('infoChange', res.data.info)
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
        commit('infoChange', res.data.info)
        resolve(res.data.info)
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
