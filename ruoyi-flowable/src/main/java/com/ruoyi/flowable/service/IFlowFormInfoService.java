package com.ruoyi.flowable.service;

import java.util.List;
import com.ruoyi.flowable.domain.FlowFormInfo;

/**
 * 表单定义 服务接口
 */
public interface IFlowFormInfoService
{
    FlowFormInfo selectFlowFormInfoById(Long formId);

    FlowFormInfo selectFlowFormInfoByKey(String formKey);

    List<FlowFormInfo> selectFlowFormInfoList(FlowFormInfo formInfo);

    int insertFlowFormInfo(FlowFormInfo formInfo);

    int updateFlowFormInfo(FlowFormInfo formInfo);

    int deleteFlowFormInfoByIds(Long[] formIds);

    int deleteFlowFormInfoById(Long formId);
}
