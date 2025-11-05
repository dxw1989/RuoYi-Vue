package com.ruoyi.flowable.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.flowable.domain.FlowFormDataInfo;
import com.ruoyi.flowable.mapper.FlowFormDataInfoMapper;
import com.ruoyi.flowable.service.IFlowFormDataInfoService;

/**
 * 表单数据 服务实现
 */
@Service
public class FlowFormDataInfoServiceImpl implements IFlowFormDataInfoService
{
    @Autowired
    private FlowFormDataInfoMapper flowFormDataInfoMapper;

    @Override
    public FlowFormDataInfo selectFlowFormDataInfoById(Long dataId)
    {
        return flowFormDataInfoMapper.selectFlowFormDataInfoById(dataId);
    }

    @Override
    public List<FlowFormDataInfo> selectFlowFormDataInfoList(FlowFormDataInfo dataInfo)
    {
        return flowFormDataInfoMapper.selectFlowFormDataInfoList(dataInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFlowFormDataInfo(FlowFormDataInfo dataInfo)
    {
        Date now = DateUtils.getNowDate();
        dataInfo.setCreateTime(now);
        dataInfo.setUpdateTime(now);
        if (dataInfo.getSubmitTime() == null)
        {
            dataInfo.setSubmitTime(now);
        }
        dataInfo.setDelFlag(StringUtils.nvl(dataInfo.getDelFlag(), "0"));
        return flowFormDataInfoMapper.insertFlowFormDataInfo(dataInfo);
    }

    @Override
    public int updateFlowFormDataInfo(FlowFormDataInfo dataInfo)
    {
        dataInfo.setUpdateTime(DateUtils.getNowDate());
        return flowFormDataInfoMapper.updateFlowFormDataInfo(dataInfo);
    }

    @Override
    public int deleteFlowFormDataInfoByIds(Long[] dataIds)
    {
        return flowFormDataInfoMapper.deleteFlowFormDataInfoByIds(dataIds);
    }

    @Override
    public int deleteFlowFormDataInfoById(Long dataId)
    {
        return flowFormDataInfoMapper.deleteFlowFormDataInfoById(dataId);
    }
}
