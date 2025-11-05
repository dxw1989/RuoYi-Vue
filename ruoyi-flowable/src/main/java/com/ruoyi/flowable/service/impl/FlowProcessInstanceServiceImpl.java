package com.ruoyi.flowable.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.flowable.domain.FlowFormDataInfo;
import com.ruoyi.flowable.domain.FlowModelInfo;
import com.ruoyi.flowable.domain.FlowProcessInstance;
import com.ruoyi.flowable.domain.dto.FlowProcessStartRequest;
import com.ruoyi.flowable.mapper.FlowFormDataInfoMapper;
import com.ruoyi.flowable.mapper.FlowModelInfoMapper;
import com.ruoyi.flowable.service.IFlowProcessInstanceService;

/**
 * 流程实例 服务实现
 */
@Service
public class FlowProcessInstanceServiceImpl implements IFlowProcessInstanceService
{
    @Autowired(required = false)
    private RuntimeService runtimeService;

    @Autowired(required = false)
    private HistoryService historyService;

    @Autowired(required = false)
    private TaskService taskService;

    @Autowired(required = false)
    private IdentityService identityService;

    @Autowired
    private FlowModelInfoMapper flowModelInfoMapper;

    @Autowired
    private FlowFormDataInfoMapper flowFormDataInfoMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<FlowProcessInstance> selectProcessInstanceList(FlowProcessInstance query)
    {
        if (historyService == null)
        {
            return new ArrayList<>();
        }
        HistoricProcessInstanceQuery historyQuery = historyService.createHistoricProcessInstanceQuery();
        if (StringUtils.isNotEmpty(query.getDefinitionId()))
        {
            historyQuery.processDefinitionId(query.getDefinitionId());
        }
        if (StringUtils.isNotEmpty(query.getDefinitionKey()))
        {
            historyQuery.processDefinitionKey(query.getDefinitionKey());
        }
        if (StringUtils.isNotEmpty(query.getInstanceId()))
        {
            historyQuery.processInstanceId(query.getInstanceId());
        }
        if (StringUtils.isNotEmpty(query.getBusinessKey()))
        {
            historyQuery.processInstanceBusinessKey(query.getBusinessKey());
        }
        if (StringUtils.isNotEmpty(query.getStartUserId()))
        {
            historyQuery.startedBy(query.getStartUserId());
        }
        if (StringUtils.isNotEmpty(query.getTenantId()))
        {
            historyQuery.processInstanceTenantId(query.getTenantId());
        }
        Boolean finished = query.getFinished();
        if (finished != null)
        {
            if (Boolean.TRUE.equals(finished))
            {
                historyQuery.finished();
            }
            else
            {
                historyQuery.unfinished();
            }
        }
        historyQuery.includeProcessVariables();
        historyQuery.orderByProcessInstanceStartTime().desc();
        List<HistoricProcessInstance> histories = historyQuery.list();
        if (CollectionUtils.isEmpty(histories))
        {
            return new ArrayList<>();
        }
        Map<String, FlowModelInfo> modelInfoMap = loadModelInfos(histories);
        Map<String, ProcessInstance> runtimeMap = loadRuntimeInstances(histories);
        Map<String, List<Task>> taskMap = loadRuntimeTasks(runtimeMap.keySet());
        Map<String, FlowFormDataInfo> formDataMap = loadLatestFormData(histories);
        List<FlowProcessInstance> results = new ArrayList<>(histories.size());
        for (HistoricProcessInstance history : histories)
        {
            results.add(buildProcessInstance(history, runtimeMap.get(history.getId()), taskMap.get(history.getId()),
                modelInfoMap.get(history.getProcessDefinitionKey()), formDataMap.get(history.getId())));
        }
        return results;
    }

    @Override
    public FlowProcessInstance selectProcessInstanceById(String instanceId)
    {
        if (historyService == null)
        {
            throw new ServiceException("Flowable 引擎未启用");
        }
        HistoricProcessInstance history = historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(instanceId)
            .includeProcessVariables()
            .singleResult();
        if (history == null)
        {
            throw new ServiceException("流程实例不存在");
        }
        ProcessInstance runtime = runtimeService == null ? null
                : runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
        List<Task> tasks = taskService == null ? Collections.emptyList()
                : taskService.createTaskQuery().processInstanceId(instanceId).active().list();
        FlowModelInfo modelInfo = flowModelInfoMapper.selectFlowModelInfoByDefinitionKey(history.getProcessDefinitionKey());
        FlowFormDataInfo formData = flowFormDataInfoMapper.selectLatestByInstanceId(instanceId);
        return buildProcessInstance(history, runtime, tasks, modelInfo, formData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlowProcessInstance startProcessInstance(FlowProcessStartRequest request)
    {
        if (runtimeService == null)
        {
            throw new ServiceException("Flowable 引擎未启用");
        }
        if (StringUtils.isAllEmpty(request.getDefinitionId(), request.getDefinitionKey()))
        {
            throw new ServiceException("流程定义信息不能为空");
        }
        String startUserId = request.getStartUserId();
        if (identityService != null)
        {
            identityService.setAuthenticatedUserId(startUserId);
        }
        try
        {
            Map<String, Object> variables = new HashMap<>();
            if (!CollectionUtils.isEmpty(request.getVariables()))
            {
                variables.putAll(request.getVariables());
            }
            Map<String, Object> formValues = request.getFormValues();
            if (!CollectionUtils.isEmpty(formValues))
            {
                variables.put("formData", formValues);
            }
            ProcessInstance instance;
            if (StringUtils.isNotEmpty(request.getDefinitionId()))
            {
                instance = runtimeService.startProcessInstanceById(request.getDefinitionId(), request.getBusinessKey(), variables);
            }
            else
            {
                instance = runtimeService.startProcessInstanceByKey(request.getDefinitionKey(), request.getBusinessKey(), variables);
            }
            FlowFormDataInfo formDataInfo = persistStartFormData(instance, request, formValues, startUserId);
            HistoricProcessInstance history = historyService == null ? null
                    : historyService.createHistoricProcessInstanceQuery().processInstanceId(instance.getId()).singleResult();
            FlowModelInfo modelInfo = flowModelInfoMapper
                .selectFlowModelInfoByDefinitionKey(instance.getProcessDefinitionKey());
            List<Task> tasks = taskService == null ? Collections.emptyList()
                    : taskService.createTaskQuery().processInstanceId(instance.getId()).active().list();
            return buildProcessInstance(history, instance, tasks, modelInfo, formDataInfo);
        }
        finally
        {
            if (identityService != null)
            {
                identityService.setAuthenticatedUserId(null);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProcessInstance(String instanceId, String reason)
    {
        if (runtimeService == null)
        {
            throw new ServiceException("Flowable 引擎未启用");
        }
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
        if (instance != null)
        {
            runtimeService.deleteProcessInstance(instanceId, StringUtils.defaultString(reason, "流程删除"));
        }
        if (historyService != null)
        {
            historyService.deleteHistoricProcessInstance(instanceId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void suspendProcessInstance(String instanceId)
    {
        if (runtimeService == null)
        {
            throw new ServiceException("Flowable 引擎未启用");
        }
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
        if (instance == null)
        {
            throw new ServiceException("流程实例不存在");
        }
        if (instance.isSuspended())
        {
            throw new ServiceException("流程实例已挂起");
        }
        runtimeService.suspendProcessInstanceById(instanceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateProcessInstance(String instanceId)
    {
        if (runtimeService == null)
        {
            throw new ServiceException("Flowable 引擎未启用");
        }
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
        if (instance == null)
        {
            throw new ServiceException("流程实例不存在");
        }
        if (!instance.isSuspended())
        {
            throw new ServiceException("流程实例已激活");
        }
        runtimeService.activateProcessInstanceById(instanceId);
    }

    private Map<String, FlowModelInfo> loadModelInfos(Collection<HistoricProcessInstance> histories)
    {
        Set<String> definitionKeys = histories.stream()
            .map(HistoricProcessInstance::getProcessDefinitionKey)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (definitionKeys.isEmpty())
        {
            return Collections.emptyMap();
        }
        List<FlowModelInfo> modelInfos = flowModelInfoMapper
            .selectFlowModelInfoByDefinitionKeys(new ArrayList<>(definitionKeys));
        if (CollectionUtils.isEmpty(modelInfos))
        {
            return Collections.emptyMap();
        }
        Map<String, FlowModelInfo> result = new LinkedHashMap<>();
        for (FlowModelInfo modelInfo : modelInfos)
        {
            if (modelInfo != null && StringUtils.isNotEmpty(modelInfo.getDefinitionKey()))
            {
                result.putIfAbsent(modelInfo.getDefinitionKey(), modelInfo);
            }
        }
        return result;
    }

    private Map<String, ProcessInstance> loadRuntimeInstances(Collection<HistoricProcessInstance> histories)
    {
        if (runtimeService == null)
        {
            return Collections.emptyMap();
        }
        Set<String> runtimeIds = histories.stream()
            .filter(history -> history.getEndTime() == null)
            .map(HistoricProcessInstance::getId)
            .collect(Collectors.toCollection(HashSet::new));
        if (runtimeIds.isEmpty())
        {
            return Collections.emptyMap();
        }
        List<ProcessInstance> instances = runtimeService.createProcessInstanceQuery()
            .processInstanceIds(runtimeIds)
            .list();
        if (CollectionUtils.isEmpty(instances))
        {
            return Collections.emptyMap();
        }
        return instances.stream().collect(Collectors.toMap(ProcessInstance::getId, instance -> instance));
    }

    private Map<String, List<Task>> loadRuntimeTasks(Collection<String> instanceIds)
    {
        if (taskService == null || CollectionUtils.isEmpty(instanceIds))
        {
            return Collections.emptyMap();
        }
        List<Task> tasks = taskService.createTaskQuery().processInstanceIds(new HashSet<>(instanceIds)).active().list();
        if (CollectionUtils.isEmpty(tasks))
        {
            return Collections.emptyMap();
        }
        Map<String, List<Task>> taskMap = new HashMap<>();
        for (Task task : tasks)
        {
            taskMap.computeIfAbsent(task.getProcessInstanceId(), key -> new ArrayList<>()).add(task);
        }
        return taskMap;
    }

    private Map<String, FlowFormDataInfo> loadLatestFormData(Collection<HistoricProcessInstance> histories)
    {
        Map<String, FlowFormDataInfo> result = new HashMap<>();
        for (HistoricProcessInstance history : histories)
        {
            FlowFormDataInfo formDataInfo = flowFormDataInfoMapper.selectLatestByInstanceId(history.getId());
            if (formDataInfo != null)
            {
                result.put(history.getId(), formDataInfo);
            }
        }
        return result;
    }

    private FlowProcessInstance buildProcessInstance(HistoricProcessInstance history, ProcessInstance runtime,
            Collection<Task> tasks, FlowModelInfo modelInfo, FlowFormDataInfo formData)
    {
        FlowProcessInstance target = new FlowProcessInstance();
        if (history != null)
        {
            target.setInstanceId(history.getId());
            target.setDefinitionId(history.getProcessDefinitionId());
            target.setDefinitionKey(history.getProcessDefinitionKey());
            target.setDefinitionName(history.getProcessDefinitionName());
            target.setDeploymentId(history.getDeploymentId());
            target.setBusinessKey(history.getBusinessKey());
            target.setStartUserId(history.getStartUserId());
            target.setStartTime(copyDate(history.getStartTime()));
            target.setEndTime(copyDate(history.getEndTime()));
            target.setTenantId(history.getTenantId());
            target.setFinished(history.getEndTime() != null);
        }
        if (runtime != null)
        {
            if (StringUtils.isEmpty(target.getInstanceId()))
            {
                target.setInstanceId(runtime.getId());
            }
            if (StringUtils.isEmpty(target.getDefinitionId()))
            {
                target.setDefinitionId(runtime.getProcessDefinitionId());
                target.setDefinitionKey(runtime.getProcessDefinitionKey());
                target.setDeploymentId(runtime.getDeploymentId());
            }
            target.setSuspended(runtime.isSuspended());
            target.setBusinessKey(runtime.getBusinessKey());
        }
        else if (history != null)
        {
            target.setSuspended(Boolean.FALSE);
        }
        if (!CollectionUtils.isEmpty(tasks))
        {
            List<String> taskIds = new ArrayList<>(tasks.size());
            List<String> taskNames = new ArrayList<>(tasks.size());
            List<String> assignees = new ArrayList<>(tasks.size());
            List<String> assigneeNames = new ArrayList<>(tasks.size());
            for (Task task : tasks)
            {
                taskIds.add(task.getId());
                taskNames.add(task.getName());
                assignees.add(task.getAssignee());
                assigneeNames.add(resolveAssigneeName(task));
            }
            target.setCurrentTaskIds(taskIds);
            target.setCurrentTaskNames(taskNames);
            target.setCurrentAssignees(assignees);
            target.setCurrentAssigneeNames(assigneeNames);
        }
        if (modelInfo != null)
        {
            target.setModelId(modelInfo.getModelId());
            target.setFormId(modelInfo.getFormId());
            target.setFormKey(modelInfo.getFormKey());
        }
        if (formData != null)
        {
            target.setFormDataId(formData.getDataId());
            target.setFormValues(formData.getFormValues());
        }
        if (history != null && history.getProcessVariables() != null && !history.getProcessVariables().isEmpty())
        {
            target.setVariables(new LinkedHashMap<>(history.getProcessVariables()));
        }
        return target;
    }

    private Date copyDate(Date source)
    {
        return source == null ? null : new Date(source.getTime());
    }

    private String resolveAssigneeName(Task task)
    {
        if (task == null)
        {
            return null;
        }
        String assignee = task.getAssignee();
        if (StringUtils.isNotEmpty(assignee))
        {
            return assignee;
        }
        if (taskService == null)
        {
            return null;
        }
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
        if (CollectionUtils.isEmpty(identityLinks))
        {
            return null;
        }
        return identityLinks.stream()
            .map(link -> StringUtils.defaultString(link.getUserId(), link.getGroupId()))
            .filter(StringUtils::isNotEmpty)
            .distinct()
            .collect(Collectors.joining(","));
    }

    private FlowFormDataInfo persistStartFormData(ProcessInstance instance, FlowProcessStartRequest request,
            Map<String, Object> formValues, String operator)
    {
        if (instance == null || CollectionUtils.isEmpty(formValues))
        {
            return null;
        }
        FlowFormDataInfo dataInfo = new FlowFormDataInfo();
        dataInfo.setFormId(request.getFormId());
        dataInfo.setFormKey(StringUtils.defaultIfEmpty(request.getFormKey(), request.getDefinitionKey()));
        dataInfo.setBusinessKey(instance.getBusinessKey());
        dataInfo.setProcessInstanceId(instance.getId());
        dataInfo.setTaskId(null);
        dataInfo.setFormValues(writeJson(formValues));
        dataInfo.setSubmitBy(operator);
        Date now = new Date();
        dataInfo.setSubmitTime(now);
        dataInfo.setCreateBy(operator);
        dataInfo.setCreateTime(now);
        dataInfo.setUpdateBy(operator);
        dataInfo.setUpdateTime(now);
        dataInfo.setDelFlag("0");
        flowFormDataInfoMapper.insertFlowFormDataInfo(dataInfo);
        return dataInfo;
    }

    private String writeJson(Map<String, Object> formValues)
    {
        if (CollectionUtils.isEmpty(formValues))
        {
            return null;
        }
        try
        {
            return objectMapper.writeValueAsString(formValues);
        }
        catch (JsonProcessingException e)
        {
            throw new ServiceException("表单数据序列化失败", e);
        }
    }
}
