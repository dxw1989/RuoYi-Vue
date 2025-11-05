package com.ruoyi.flowable.service;

import java.util.List;
import com.ruoyi.flowable.domain.FlowProcessInstance;
import com.ruoyi.flowable.domain.dto.FlowProcessStartRequest;

/**
 * 流程实例 服务接口
 */
public interface IFlowProcessInstanceService
{
    /**
     * 查询流程实例列表
     *
     * @param query 查询条件
     * @return 实例集合
     */
    List<FlowProcessInstance> selectProcessInstanceList(FlowProcessInstance query);

    /**
     * 根据ID查询流程实例
     *
     * @param instanceId 流程实例ID
     * @return 流程实例
     */
    FlowProcessInstance selectProcessInstanceById(String instanceId);

    /**
     * 启动流程实例
     *
     * @param request 启动参数
     * @return 流程实例
     */
    FlowProcessInstance startProcessInstance(FlowProcessStartRequest request);

    /**
     * 删除流程实例
     *
     * @param instanceId 流程实例ID
     * @param reason 删除原因
     */
    void deleteProcessInstance(String instanceId, String reason);

    /**
     * 挂起流程实例
     *
     * @param instanceId 流程实例ID
     */
    void suspendProcessInstance(String instanceId);

    /**
     * 激活流程实例
     *
     * @param instanceId 流程实例ID
     */
    void activateProcessInstance(String instanceId);
}
