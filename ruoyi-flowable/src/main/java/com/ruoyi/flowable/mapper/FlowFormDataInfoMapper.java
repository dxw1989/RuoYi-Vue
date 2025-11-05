package com.ruoyi.flowable.mapper;

import java.util.List;
import com.ruoyi.flowable.domain.FlowFormDataInfo;

/**
 * 表单数据 数据层
 */
public interface FlowFormDataInfoMapper
{
    FlowFormDataInfo selectFlowFormDataInfoById(Long dataId);

    List<FlowFormDataInfo> selectFlowFormDataInfoList(FlowFormDataInfo dataInfo);

    FlowFormDataInfo selectLatestByInstanceId(String processInstanceId);

    FlowFormDataInfo selectLatestByTaskId(String taskId);

    int insertFlowFormDataInfo(FlowFormDataInfo dataInfo);

    int updateFlowFormDataInfo(FlowFormDataInfo dataInfo);

    int deleteFlowFormDataInfoById(Long dataId);

    int deleteFlowFormDataInfoByIds(Long[] dataIds);
}
