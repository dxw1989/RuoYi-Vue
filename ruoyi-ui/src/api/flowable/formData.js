import request from '@/utils/request'

// 查询动态表单数据列表
export function listFormData(query) {
  return request({
    url: '/flowable/form/data/list',
    method: 'get',
    params: query
  })
}

// 查询动态表单数据详细
export function getFormData(dataId) {
  return request({
    url: `/flowable/form/data/${dataId}`,
    method: 'get'
  })
}

// 新增动态表单数据
export function addFormData(data) {
  return request({
    url: '/flowable/form/data',
    method: 'post',
    data: data
  })
}

// 修改动态表单数据
export function updateFormData(data) {
  return request({
    url: '/flowable/form/data',
    method: 'put',
    data: data
  })
}

// 删除动态表单数据
export function delFormData(dataIds) {
  return request({
    url: `/flowable/form/data/${dataIds}`,
    method: 'delete'
  })
}
