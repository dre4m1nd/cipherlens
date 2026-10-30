import request from '@/utils/system/request'

/** 添加密码项Api */
export function addPasswordItemApi(data) {
  return request({
    url: '/passwordItem/add',
    method: 'post',
    data
  })
}

/** 根据id查询密码项Api */
export function getPasswordItemApi(id) {
  return request({
    url: `/passwordItem/query/${id}`,
    method: 'get'
  })
}

/** 根据id修改密码项Api */
export function updatePasswordItemApi(id, data) {
  return request({
    url: `/passwordItem/update/${id}`,
    method: 'post',
    data
  })
}

/** 根据id删除密码项Api */
export function deletePasswordItemApi(id) {
  return request({
    url: `/passwordItem/remove/${id}`,
    method: 'delete'
  })
}

/** 获取密码项列表Api */
export function getPasswordItemListApi(data) {
  return request({
    url: '/passwordItem/list',
    method: 'post',
    data
  })
}

/** 根据id获取密码明文Api */
export function getPasswordPlainTextApi(id) {
  return request({
    url: `/passwordItem/getPlainText/${id}`,
    method: 'get'
  })
}

/** 密码评估Api */
export function evaluatePasswordApi(passwordItemId) {
  return request({
    url: `/evaluate/item/${passwordItemId}`,
    method: 'get'
  })
}

/** 输入密码评估Api（POST 请求体传密码，避免出现在 URL） */
export function evaluatePasswordByInputApi(password) {
  return request({
    url: '/evaluate',
    method: 'post',
    data: { password }
  })
}