package com.ruoyi.flowable.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.flowable.domain.FlowFormInfo;
import com.ruoyi.flowable.mapper.FlowFormInfoMapper;
import com.ruoyi.flowable.service.IFlowFormInfoService;

/**
 * 表单定义 服务实现
 */
@Service
public class FlowFormInfoServiceImpl implements IFlowFormInfoService
{
    @Autowired
    private FlowFormInfoMapper flowFormInfoMapper;

    @Override
    public FlowFormInfo selectFlowFormInfoById(Long formId)
    {
        return flowFormInfoMapper.selectFlowFormInfoById(formId);
    }

    @Override
    public FlowFormInfo selectFlowFormInfoByKey(String formKey)
    {
        return flowFormInfoMapper.selectFlowFormInfoByKey(formKey);
    }

    @Override
    public List<FlowFormInfo> selectFlowFormInfoList(FlowFormInfo formInfo)
    {
        return flowFormInfoMapper.selectFlowFormInfoList(formInfo);
    }

    @Override
    public int insertFlowFormInfo(FlowFormInfo formInfo)
    {
        Date now = DateUtils.getNowDate();
        formInfo.setCreateTime(now);
        formInfo.setUpdateTime(now);
        formInfo.setDelFlag(StringUtils.nvl(formInfo.getDelFlag(), "0"));
        return flowFormInfoMapper.insertFlowFormInfo(formInfo);
    }

    @Override
    public int updateFlowFormInfo(FlowFormInfo formInfo)
    {
        formInfo.setUpdateTime(DateUtils.getNowDate());
        return flowFormInfoMapper.updateFlowFormInfo(formInfo);
    }

    @Override
    public int deleteFlowFormInfoByIds(Long[] formIds)
    {
        return flowFormInfoMapper.deleteFlowFormInfoByIds(formIds);
    }

    @Override
    public int deleteFlowFormInfoById(Long formId)
    {
        return flowFormInfoMapper.deleteFlowFormInfoById(formId);
    }
}
