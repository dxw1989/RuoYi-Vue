import request from '@/utils/request'

// 查询待办任务列表
export function listTodoTask(query) {
  return request({
    url: '/flowable/task/todoList',
    method: 'get',
    params: query
  })
}

// 查询已办任务列表
export function listDoneTask(query) {
  return request({
    url: '/flowable/task/doneList',
    method: 'get',
    params: query
  })
}

// 查询任务详细
export function getTask(taskId) {
  return request({
    url: `/flowable/task/${taskId}`,
    method: 'get'
  })
}

// 签收任务
export function claimTask(taskId) {
  return request({
    url: `/flowable/task/${taskId}/claim`,
    method: 'post'
  })
}

// 退签任务
export function unclaimTask(taskId) {
  return request({
    url: `/flowable/task/${taskId}/unclaim`,
    method: 'post'
  })
}

// 委派任务
export function delegateTask(data) {
  return request({
    url: '/flowable/task/delegate',
    method: 'post',
    data: data
  })
}

// 转办任务
export function transferTask(data) {
  return request({
    url: '/flowable/task/transfer',
    method: 'post',
    data: data
  })
}

// 完成任务
export function completeTask(data) {
  return request({
    url: '/flowable/task/complete',
    method: 'post',
    data: data
  })
}
