import axios , { AxiosError, AxiosRequestConfig, AxiosResponse, AxiosInstance } from 'axios'
import store from '@/store'
import router from '@/router'
import { ElMessage } from 'element-plus'
// 设置基础URL为相对路径，让请求通过Vite代理处理
const baseURL = '/api'

const service = axios.create({
  baseURL: baseURL,
  timeout: 5000
})

// 请求前的统一处理
service.interceptors.request.use(
  (config) => {
    // JWT鉴权处理 - 后端需要自定义token头
    const token = store.state.user.token
    if (token) {
      // 检查token是否为对象，如果是则转换为字符串
      const tokenValue = typeof token === 'object' ? JSON.stringify(token) : token
      config.headers['token'] = tokenValue
      // 也可以尝试添加Authorization头，有些后端需要这种格式
      config.headers['Authorization'] = `Bearer ${tokenValue}`
    }
    return config
  },
  (error) => {
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      showError(res)
      return Promise.reject(res)
    }
  },
  (error)=> {
    console.log(error) // for debug
    // 处理HTTP错误
    if (error.response) {
      // 服务器返回了错误状态码
      const res = error.response.data
      // 如果服务器返回了JSON格式的错误信息，使用它
      if (res && res.code) {
        showError(res)
        return Promise.reject(res)
      } else {
        // 否则，从HTTP状态码创建错误信息
        const code = error.response.status
        const message = error.response.statusText
        showError({ code, message })
        return Promise.reject({ code, message })
      }
    } else {
      // 网络错误或其他错误
      const badMessage = error.message || error
      showError({ code: 500, message: badMessage })
      return Promise.reject(error)
    }
  }
)

// 错误处理
function showError(error) {
  // 显示错误信息
  ElMessage({
    message: error.msg || error.message || '服务异常',
    type: 'error',
    duration: 3 * 1000
  })
  
  // token过期或无权限，清除本地数据，并跳转至登录页面
  if (error.code === 401 || error.code === 403) {
    // 检查当前路由，如果是登录、注册、忘记密码或重置密码页面，则不执行退出登录
    const currentPath = router.currentRoute.value.path
    if (currentPath !== '/login' && currentPath !== '/register' && currentPath !== '/forgot-password' && currentPath !== '/resetPassword') {
      // to re-login
      store.dispatch('user/loginOut')
    }
  }
}

export default service