package com.ruoyi.flowable.mapper;

import java.util.List;
import com.ruoyi.flowable.domain.FlowModelInfo;

/**
 * 流程模型 数据层
 */
public interface FlowModelInfoMapper
{
    /**
     * 查询流程模型
     *
     * @param modelId 模型ID
     * @return 模型信息
     */
    FlowModelInfo selectFlowModelInfoById(Long modelId);

    /**
     * 查询流程模型列表
     *
     * @param flowModelInfo 查询条件
     * @return 集合
     */
    List<FlowModelInfo> selectFlowModelInfoList(FlowModelInfo flowModelInfo);

    /**
     * 根据模型标识查询
     *
     * @param modelKey 模型标识
     * @return 模型信息
     */
    FlowModelInfo selectFlowModelInfoByKey(String modelKey);

    /**
     * 根据流程定义Key查询最新模型
     *
     * @param definitionKey 流程定义Key
     * @return 模型信息
     */
    FlowModelInfo selectFlowModelInfoByDefinitionKey(String definitionKey);

    /**
     * 根据流程定义Key集合查询模型
     *
     * @param definitionKeys 流程定义Key集合
     * @return 模型集合
     */
    List<FlowModelInfo> selectFlowModelInfoByDefinitionKeys(List<String> definitionKeys);

    /**
     * 新增流程模型
     *
     * @param flowModelInfo 模型信息
     * @return 结果
     */
    int insertFlowModelInfo(FlowModelInfo flowModelInfo);

    /**
     * 修改流程模型
     *
     * @param flowModelInfo 模型信息
     * @return 结果
     */
    int updateFlowModelInfo(FlowModelInfo flowModelInfo);

    /**
     * 批量删除流程模型
     *
     * @param modelIds 主键集合
     * @return 结果
     */
    int deleteFlowModelInfoByIds(Long[] modelIds);

    /**
     * 删除流程模型
     *
     * @param modelId 主键
     * @return 结果
     */
    int deleteFlowModelInfoById(Long modelId);
}
