package com.ruoyi.flowable.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.flowable.domain.FlowModelInfo;
import com.ruoyi.flowable.domain.FlowProcessDefinition;
import com.ruoyi.flowable.mapper.FlowModelInfoMapper;
import com.ruoyi.flowable.service.IFlowProcessDefinitionService;

/**
 * 流程定义服务实现
 */
@Service
public class FlowProcessDefinitionServiceImpl implements IFlowProcessDefinitionService
{
    @Autowired(required = false)
    private RepositoryService repositoryService;

    @Autowired
    private FlowModelInfoMapper flowModelInfoMapper;

    @Override
    public List<FlowProcessDefinition> selectProcessDefinitionList(FlowProcessDefinition query)
    {
        if (repositoryService == null)
        {
            return new ArrayList<>();
        }
        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery();
        if (StringUtils.isNotEmpty(query.getDefinitionKey()))
        {
            definitionQuery.processDefinitionKeyLike("%" + query.getDefinitionKey() + "%");
        }
        if (StringUtils.isNotEmpty(query.getDefinitionName()))
        {
            definitionQuery.processDefinitionNameLike("%" + query.getDefinitionName() + "%");
        }
        if (StringUtils.isNotEmpty(query.getCategory()))
        {
            definitionQuery.processDefinitionCategory(query.getCategory());
        }
        Boolean suspended = query.getSuspended();
        if (suspended != null)
        {
            if (Boolean.TRUE.equals(suspended))
            {
                definitionQuery.suspended();
            }
            else
            {
                definitionQuery.active();
            }
        }
        definitionQuery.latestVersion();
        List<ProcessDefinition> definitions = definitionQuery.orderByProcessDefinitionKey().asc().list();
        if (CollectionUtils.isEmpty(definitions))
        {
            return new ArrayList<>();
        }
        Set<String> deploymentIds = definitions.stream().map(ProcessDefinition::getDeploymentId).filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());
        Map<String, Deployment> deploymentMap = new HashMap<>();
        if (!deploymentIds.isEmpty())
        {
            DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
            deploymentQuery.deploymentIds(deploymentIds);
            List<Deployment> deployments = deploymentQuery.list();
            if (!CollectionUtils.isEmpty(deployments))
            {
                deploymentMap = deployments.stream().collect(Collectors.toMap(Deployment::getId, d -> d));
            }
        }
        Map<String, FlowModelInfo> modelInfoMap = new LinkedHashMap<>();
        Set<String> definitionKeys = definitions.stream().map(ProcessDefinition::getKey).filter(StringUtils::isNotEmpty)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (!definitionKeys.isEmpty())
        {
            List<FlowModelInfo> modelInfos = flowModelInfoMapper
                .selectFlowModelInfoByDefinitionKeys(new ArrayList<>(definitionKeys));
            if (!CollectionUtils.isEmpty(modelInfos))
            {
                for (FlowModelInfo modelInfo : modelInfos)
                {
                    if (modelInfo != null && StringUtils.isNotEmpty(modelInfo.getDefinitionKey()))
                    {
                        modelInfoMap.putIfAbsent(modelInfo.getDefinitionKey(), modelInfo);
                    }
                }
            }
        }
        List<FlowProcessDefinition> results = new ArrayList<>(definitions.size());
        for (ProcessDefinition definition : definitions)
        {
            results.add(buildDefinition(definition, deploymentMap.get(definition.getDeploymentId()),
                modelInfoMap.get(definition.getKey())));
        }
        return results;
    }

    @Override
    public FlowProcessDefinition selectProcessDefinitionById(String definitionId)
    {
        if (repositoryService == null)
        {
            throw new ServiceException("Flowable 引擎未启用");
        }
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionId(definitionId)
            .singleResult();
        if (definition == null)
        {
            throw new ServiceException("流程定义不存在");
        }
        Deployment deployment = null;
        if (StringUtils.isNotEmpty(definition.getDeploymentId()))
        {
            deployment = repositoryService.createDeploymentQuery().deploymentId(definition.getDeploymentId()).singleResult();
        }
        FlowModelInfo modelInfo = flowModelInfoMapper.selectFlowModelInfoByDefinitionKey(definition.getKey());
        return buildDefinition(definition, deployment, modelInfo);
    }

    @Override
    public String selectProcessDefinitionXml(String definitionId)
    {
        FlowProcessDefinition definition = selectProcessDefinitionById(definitionId);
        if (repositoryService == null)
        {
            throw new ServiceException("Flowable 引擎未启用");
        }
        String resourceName = definition.getResourceName();
        if (StringUtils.isEmpty(resourceName))
        {
            resourceName = definition.getDefinitionKey() + ".bpmn20.xml";
        }
        try (InputStream inputStream = repositoryService.getResourceAsStream(definition.getDeploymentId(), resourceName))
        {
            if (inputStream == null)
            {
                throw new ServiceException("流程定义XML不存在");
            }
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            throw new ServiceException("读取流程定义XML失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void suspendProcessDefinition(String definitionId)
    {
        FlowProcessDefinition definition = selectProcessDefinitionById(definitionId);
        if (Boolean.TRUE.equals(definition.getSuspended()))
        {
            throw new ServiceException("流程定义已挂起");
        }
        repositoryService.suspendProcessDefinitionById(definitionId, true, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateProcessDefinition(String definitionId)
    {
        FlowProcessDefinition definition = selectProcessDefinitionById(definitionId);
        if (!Boolean.TRUE.equals(definition.getSuspended()))
        {
            throw new ServiceException("流程定义已激活");
        }
        repositoryService.activateProcessDefinitionById(definitionId, true, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeployment(String deploymentId, boolean cascade)
    {
        if (repositoryService == null)
        {
            throw new ServiceException("Flowable 引擎未启用");
        }
        repositoryService.deleteDeployment(deploymentId, cascade);
    }

    private FlowProcessDefinition buildDefinition(ProcessDefinition definition, Deployment deployment,
            FlowModelInfo modelInfo)
    {
        FlowProcessDefinition target = new FlowProcessDefinition();
        target.setDefinitionId(definition.getId());
        target.setDefinitionKey(definition.getKey());
        target.setDefinitionName(definition.getName());
        target.setCategory(definition.getCategory());
        target.setVersion(definition.getVersion());
        target.setDeploymentId(definition.getDeploymentId());
        target.setDescription(definition.getDescription());
        target.setTenantId(definition.getTenantId());
        target.setSuspended(definition.isSuspended());
        target.setResourceName(definition.getResourceName());
        if (deployment != null)
        {
            target.setDeploymentName(deployment.getName());
            Date deploymentTime = deployment.getDeploymentTime();
            if (deploymentTime != null)
            {
                target.setDeploymentTime(new Date(deploymentTime.getTime()));
            }
        }
        if (modelInfo != null)
        {
            target.setModelId(modelInfo.getModelId());
            target.setModelName(modelInfo.getModelName());
            target.setFormId(modelInfo.getFormId());
            target.setFormKey(modelInfo.getFormKey());
        }
        return target;
    }
}
