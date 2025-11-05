package com.ruoyi.flowable.service;

import java.util.List;
import com.ruoyi.flowable.domain.FlowModelInfo;

/**
 * 流程模型 服务接口
 */
public interface IFlowModelInfoService
{
    FlowModelInfo selectFlowModelInfoById(Long modelId);

    FlowModelInfo selectFlowModelInfoByKey(String modelKey);

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

    /**
     * 保存模型设计内容
     *
     * @param modelId 模型ID
     * @param modelContent BPMN XML
     * @param modelEditorJson 设计器JSON
     * @param formId 绑定表单ID
     * @param formKey 绑定表单标识
     * @param operator 操作者
     * @return 最新模型信息
     */
    FlowModelInfo saveModelEditor(Long modelId, String modelContent, String modelEditorJson,
            Long formId, String formKey, String operator);
}
