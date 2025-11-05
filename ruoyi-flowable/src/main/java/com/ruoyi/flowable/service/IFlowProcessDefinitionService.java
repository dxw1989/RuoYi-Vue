package com.ruoyi.flowable.service;

import java.util.List;
import com.ruoyi.flowable.domain.FlowProcessDefinition;

/**
 * 流程定义服务
 */
public interface IFlowProcessDefinitionService
{
    /**
     * 查询流程定义列表
     *
     * @param query 查询条件
     * @return 流程定义集合
     */
    List<FlowProcessDefinition> selectProcessDefinitionList(FlowProcessDefinition query);

    /**
     * 根据ID查询流程定义
     *
     * @param definitionId 流程定义ID
     * @return 流程定义
     */
    FlowProcessDefinition selectProcessDefinitionById(String definitionId);

    /**
     * 查询流程定义XML内容
     *
     * @param definitionId 流程定义ID
     * @return BPMN XML
     */
    String selectProcessDefinitionXml(String definitionId);

    /**
     * 挂起流程定义
     *
     * @param definitionId 流程定义ID
     */
    void suspendProcessDefinition(String definitionId);

    /**
     * 激活流程定义
     *
     * @param definitionId 流程定义ID
     */
    void activateProcessDefinition(String definitionId);

    /**
     * 删除部署
     *
     * @param deploymentId 部署ID
     * @param cascade 是否级联删除流程实例
     */
    void deleteDeployment(String deploymentId, boolean cascade);
}
