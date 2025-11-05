import Layout from '@/layout'

const flowableRouter = {
  path: '/flowable',
  component: Layout,
  redirect: 'noRedirect',
  alwaysShow: true,
  name: 'Flowable',
  permissions: ['flowable:model:list', 'flowable:definition:list', 'flowable:instance:list', 'flowable:task:todo:list', 'flowable:form:list', 'flowable:formData:list'],
  meta: { title: '流程管理', icon: 'tree' },
  children: [
    {
      path: 'model',
      component: () => import('@/views/flowable/bpmn/model/index'),
      name: 'FlowableModel',
      permissions: ['flowable:model:list'],
      meta: { title: '模型管理' }
    },
    {
      path: 'definition',
      component: () => import('@/views/flowable/bpmn/definition/index'),
      name: 'FlowableDefinition',
      permissions: ['flowable:definition:list'],
      meta: { title: '流程定义' }
    },
    {
      path: 'instance',
      component: () => import('@/views/flowable/bpmn/instance/index'),
      name: 'FlowableInstance',
      permissions: ['flowable:instance:list'],
      meta: { title: '流程实例' }
    },
    {
      path: 'task',
      component: () => import('@/views/flowable/bpmn/task/index'),
      name: 'FlowableTask',
      permissions: ['flowable:task:todo:list', 'flowable:task:done:list'],
      meta: { title: '任务中心' }
    },
    {
      path: 'form',
      component: () => import('@/views/flowable/bpmn/form/index'),
      name: 'FlowableForm',
      permissions: ['flowable:form:list'],
      meta: { title: '表单管理' }
    },
    {
      path: 'formData',
      component: () => import('@/views/flowable/bpmn/formData/index'),
      name: 'FlowableFormData',
      permissions: ['flowable:formData:list'],
      meta: { title: '表单数据' }
    }
  ]
}

export default flowableRouter
