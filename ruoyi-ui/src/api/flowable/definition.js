import request from '@/utils/request'

// 查询流程定义列表
export function listDefinition(query) {
  return request({
    url: '/flowable/definition/list',
    method: 'get',
    params: query
  })
}

// 查询流程定义详细
export function getDefinition(definitionId) {
  return request({
    url: `/flowable/definition/${definitionId}`,
    method: 'get'
  })
}

// 查询流程定义XML
export function getDefinitionXml(definitionId) {
  return request({
    url: `/flowable/definition/${definitionId}/xml`,
    method: 'get'
  })
}

// 挂起流程定义
export function suspendDefinition(definitionId) {
  return request({
    url: `/flowable/definition/${definitionId}/suspend`,
    method: 'put'
  })
}

// 激活流程定义
export function activateDefinition(definitionId) {
  return request({
    url: `/flowable/definition/${definitionId}/activate`,
    method: 'put'
  })
}

// 删除流程部署
export function removeDeployment(deploymentId, cascade = true) {
  return request({
    url: `/flowable/definition/deployment/${deploymentId}`,
    method: 'delete',
    params: { cascade }
  })
}
