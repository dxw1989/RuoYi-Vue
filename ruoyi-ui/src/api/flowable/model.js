import request from '@/utils/request'

// 查询流程模型列表
export function listModel(query) {
  return request({
    url: '/flowable/model/list',
    method: 'get',
    params: query
  })
}

// 查询流程模型详细
export function getModel(modelId) {
  return request({
    url: `/flowable/model/${modelId}`,
    method: 'get'
  })
}

// 根据模型标识查询流程模型
export function getModelByKey(modelKey) {
  return request({
    url: `/flowable/model/key/${modelKey}`,
    method: 'get'
  })
}

// 新增流程模型
export function addModel(data) {
  return request({
    url: '/flowable/model',
    method: 'post',
    data: data
  })
}

// 修改流程模型
export function updateModel(data) {
  return request({
    url: '/flowable/model',
    method: 'put',
    data: data
  })
}

// 保存模型设计器内容
export function saveModelEditor(modelId, data) {
  return request({
    url: `/flowable/model/${modelId}/editor`,
    method: 'put',
    data: data
  })
}

// 获取模型设计器内容
export function getModelEditor(modelId) {
  return request({
    url: `/flowable/model/${modelId}/editor`,
    method: 'get'
  })
}

// 发布流程模型
export function deployModel(modelId) {
  return request({
    url: `/flowable/model/${modelId}/deploy`,
    method: 'put'
  })
}

// 删除流程模型
export function delModel(modelIds) {
  return request({
    url: `/flowable/model/${modelIds}`,
    method: 'delete'
  })
}
