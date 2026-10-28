import request from '@/utils/system/request'

/** 获取日志列表Api */
export function getLogListApi({ pageNum, pageSize }) {
  return request({
    url: `/log/list?pageNum=${pageNum}&pageSize=${pageSize}`,
    method: 'post'
  })
}

/** 按类型查询日志Api */
export function getLogListByTypeApi({ pageNum, pageSize, ...restData }) {
  return request({
    url: `/log/list?pageNum=${pageNum}&pageSize=${pageSize}`,
    method: 'post',
    data: restData
  })
}