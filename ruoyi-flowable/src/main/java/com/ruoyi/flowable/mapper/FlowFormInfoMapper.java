package com.ruoyi.flowable.mapper;

import java.util.List;
import com.ruoyi.flowable.domain.FlowFormInfo;

/**
 * 动态表单 数据层
 */
public interface FlowFormInfoMapper
{
    FlowFormInfo selectFlowFormInfoById(Long formId);

    FlowFormInfo selectFlowFormInfoByKey(String formKey);

    List<FlowFormInfo> selectFlowFormInfoList(FlowFormInfo formInfo);

    int insertFlowFormInfo(FlowFormInfo formInfo);

    int updateFlowFormInfo(FlowFormInfo formInfo);

    int deleteFlowFormInfoById(Long formId);

    int deleteFlowFormInfoByIds(Long[] formIds);
}
