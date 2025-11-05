package com.ruoyi.flowable.service.impl;

import java.util.Date;
import java.util.List;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.flowable.domain.FlowModelInfo;
import com.ruoyi.flowable.domain.enums.FlowModelStatus;
import com.ruoyi.flowable.mapper.FlowModelInfoMapper;
import com.ruoyi.flowable.service.IFlowModelInfoService;

/**
 * 流程模型 服务实现
 */
@Service
public class FlowModelInfoServiceImpl implements IFlowModelInfoService
{
    @Autowired
    private FlowModelInfoMapper flowModelInfoMapper;

    @Autowired(required = false)
    private RepositoryService repositoryService;

    @Override
    public FlowModelInfo selectFlowModelInfoById(Long modelId)
    {
        return flowModelInfoMapper.selectFlowModelInfoById(modelId);
    }

    @Override
    public List<FlowModelInfo> selectFlowModelInfoList(FlowModelInfo flowModelInfo)
    {
        return flowModelInfoMapper.selectFlowModelInfoList(flowModelInfo);
    }

    @Override
    public int insertFlowModelInfo(FlowModelInfo flowModelInfo)
    {
        Date now = DateUtils.getNowDate();
        flowModelInfo.setCreateTime(now);
        flowModelInfo.setUpdateTime(now);
        flowModelInfo.setStatus(FlowModelStatus.DRAFT.getCode());
        flowModelInfo.setDelFlag(StringUtils.nvl(flowModelInfo.getDelFlag(), "0"));
        if (flowModelInfo.getVersion() == null)
        {
            flowModelInfo.setVersion(1);
        }
        return flowModelInfoMapper.insertFlowModelInfo(flowModelInfo);
    }

    @Override
    public int updateFlowModelInfo(FlowModelInfo flowModelInfo)
    {
        flowModelInfo.setUpdateTime(DateUtils.getNowDate());
        if (flowModelInfo.getStatus() == null || FlowModelStatus.PUBLISHED.getCode() == flowModelInfo.getStatus())
        {
            flowModelInfo.setStatus(FlowModelStatus.PENDING.getCode());
        }
        return flowModelInfoMapper.updateFlowModelInfo(flowModelInfo);
    }

    @Override
    public int deleteFlowModelInfoByIds(Long[] modelIds)
    {
        return flowModelInfoMapper.deleteFlowModelInfoByIds(modelIds);
    }

    @Override
    public int deleteFlowModelInfoById(Long modelId)
    {
        return flowModelInfoMapper.deleteFlowModelInfoById(modelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlowModelInfo deployFlowModel(Long modelId, String operator)
    {
        FlowModelInfo flowModelInfo = flowModelInfoMapper.selectFlowModelInfoById(modelId);
        if (flowModelInfo == null)
        {
            throw new ServiceException("流程模型不存在");
        }
        if (StringUtils.isEmpty(flowModelInfo.getModelContent()))
        {
            throw new ServiceException("模型内容为空，无法部署");
        }
        if (repositoryService == null)
        {
            throw new ServiceException("Flowable 引擎未启用，无法部署模型");
        }

        Deployment deployment = repositoryService.createDeployment()
            .name(flowModelInfo.getModelName())
            .key(flowModelInfo.getModelKey())
            .category(flowModelInfo.getCategory())
            .addString(flowModelInfo.getModelKey() + ".bpmn20.xml", flowModelInfo.getModelContent())
            .deploy();

        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
            .deploymentId(deployment.getId())
            .latestVersion()
            .singleResult();

        Date now = DateUtils.getNowDate();
        flowModelInfo.setDeployId(deployment.getId());
        flowModelInfo.setDeployTime(now);
        flowModelInfo.setUpdateTime(now);
        flowModelInfo.setUpdateBy(operator);
        flowModelInfo.setStatus(FlowModelStatus.PUBLISHED.getCode());
        if (definition != null)
        {
            flowModelInfo.setDefinitionId(definition.getId());
            flowModelInfo.setDefinitionKey(definition.getKey());
            flowModelInfo.setDefinitionName(definition.getName());
            flowModelInfo.setVersion(definition.getVersion());
        }

        flowModelInfoMapper.updateFlowModelInfo(flowModelInfo);
        return flowModelInfo;
    }
}
