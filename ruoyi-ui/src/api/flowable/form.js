import request from '@/utils/request'

// 查询动态表单列表
export function listForm(query) {
  return request({
    url: '/flowable/form/list',
    method: 'get',
    params: query
  })
}

// 查询动态表单详细
export function getForm(formId) {
  return request({
    url: `/flowable/form/${formId}`,
    method: 'get'
  })
}

// 根据标识查询动态表单
export function getFormByKey(formKey) {
  return request({
    url: `/flowable/form/key/${formKey}`,
    method: 'get'
  })
}

// 新增动态表单
export function addForm(data) {
  return request({
    url: '/flowable/form',
    method: 'post',
    data: data
  })
}

// 修改动态表单
export function updateForm(data) {
  return request({
    url: '/flowable/form',
    method: 'put',
    data: data
  })
}

// 删除动态表单
export function delForm(formIds) {
  return request({
    url: `/flowable/form/${formIds}`,
    method: 'delete'
  })
}
