import request from '@/utils/system/request'

/** 登录api */
export function loginApi(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

/** 注册api */
export function registerApi(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

/** 获取用户信息Api */
export function getInfoApi() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

/** 更新用户信息Api */
export function updateUserApi(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  })
}

/** 重置密码Api（需邮箱验证码） */
export function resetPasswordApi(data) {
  return request({
    url: '/user/reset',
    method: 'post',
    data
  })
}

/** 发送重置密码验证码Api */
export function sendResetCodeApi(data) {
  return request({
    url: '/user/reset/code',
    method: 'post',
    data
  })
}

/** 退出登录Api */
export function loginOutApi() {
  return request({
    url: '/user/logout',
    method: 'get',
  })
}



/** 获取登录后需要展示的菜单 */
export function getMenuApi() {
  return request({
    url: '/menu/list',
    method: 'post',
  })
}