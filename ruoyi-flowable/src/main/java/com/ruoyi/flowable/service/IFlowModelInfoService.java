package com.ruoyi.flowable.service;

import java.util.List;
import com.ruoyi.flowable.domain.FlowModelInfo;

/**
 * 流程模型 服务接口
 */
public interface IFlowModelInfoService
{
    FlowModelInfo selectFlowModelInfoById(Long modelId);

    List<FlowModelInfo> selectFlowModelInfoList(FlowModelInfo flowModelInfo);

    int insertFlowModelInfo(FlowModelInfo flowModelInfo);

    int updateFlowModelInfo(FlowModelInfo flowModelInfo);

    int deleteFlowModelInfoByIds(Long[] modelIds);

    int deleteFlowModelInfoById(Long modelId);

    /**
     * 发布流程模型
     *
     * @param modelId 模型ID
     * @param operator 操作者
     * @return 发布后的模型
     */
    FlowModelInfo deployFlowModel(Long modelId, String operator);
}
