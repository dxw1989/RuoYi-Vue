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
