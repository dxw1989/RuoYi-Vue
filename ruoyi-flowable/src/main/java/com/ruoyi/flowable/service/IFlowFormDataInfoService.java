package com.ruoyi.flowable.service;

import java.util.List;
import com.ruoyi.flowable.domain.FlowFormDataInfo;

/**
 * 表单数据 服务接口
 */
public interface IFlowFormDataInfoService
{
    FlowFormDataInfo selectFlowFormDataInfoById(Long dataId);

    List<FlowFormDataInfo> selectFlowFormDataInfoList(FlowFormDataInfo dataInfo);

    int insertFlowFormDataInfo(FlowFormDataInfo dataInfo);

    int updateFlowFormDataInfo(FlowFormDataInfo dataInfo);

    int deleteFlowFormDataInfoByIds(Long[] dataIds);

    int deleteFlowFormDataInfoById(Long dataId);
}
