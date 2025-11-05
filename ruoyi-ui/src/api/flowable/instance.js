import request from '@/utils/request'

// 查询流程实例列表
export function listInstance(query) {
  return request({
    url: '/flowable/instance/list',
    method: 'get',
    params: query
  })
}

// 查询流程实例详细
export function getInstance(instanceId) {
  return request({
    url: `/flowable/instance/${instanceId}`,
    method: 'get'
  })
}

// 启动流程实例
export function startInstance(data) {
  return request({
    url: '/flowable/instance/start',
    method: 'post',
    data: data
  })
}

// 删除流程实例
export function removeInstance(instanceId, params = {}) {
  return request({
    url: `/flowable/instance/${instanceId}`,
    method: 'delete',
    params: params
  })
}

// 挂起流程实例
export function suspendInstance(instanceId) {
  return request({
    url: `/flowable/instance/${instanceId}/suspend`,
    method: 'put'
  })
}

// 激活流程实例
export function activateInstance(instanceId) {
  return request({
    url: `/flowable/instance/${instanceId}/activate`,
    method: 'put'
  })
}
