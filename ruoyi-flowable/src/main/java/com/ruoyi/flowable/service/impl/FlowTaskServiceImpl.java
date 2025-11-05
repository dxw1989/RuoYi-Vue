package com.ruoyi.flowable.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
import org.flowable.engine.history.HistoricTaskInstance;
import org.flowable.engine.history.HistoricTaskInstanceQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.flowable.domain.FlowFormDataInfo;
import com.ruoyi.flowable.domain.FlowFormInfo;
import com.ruoyi.flowable.domain.FlowModelInfo;
import com.ruoyi.flowable.domain.FlowTask;
import com.ruoyi.flowable.domain.FlowTaskComment;
import com.ruoyi.flowable.domain.dto.FlowTaskAssignRequest;
import com.ruoyi.flowable.domain.dto.FlowTaskCompleteRequest;
import com.ruoyi.flowable.mapper.FlowFormDataInfoMapper;
import com.ruoyi.flowable.mapper.FlowFormInfoMapper;
import com.ruoyi.flowable.mapper.FlowModelInfoMapper;
import com.ruoyi.flowable.service.IFlowFormDataInfoService;
import com.ruoyi.flowable.service.IFlowTaskService;

/**
 * Flowable 任务服务实现
 */
@Service
public class FlowTaskServiceImpl implements IFlowTaskService
{
    @Autowired(required = false)
    private TaskService taskService;

    @Autowired(required = false)
    private HistoryService historyService;

    @Autowired(required = false)
    private RuntimeService runtimeService;

    @Autowired(required = false)
    private IdentityService identityService;

    @Autowired
    private FlowModelInfoMapper flowModelInfoMapper;

    @Autowired
    private FlowFormInfoMapper flowFormInfoMapper;

    @Autowired
    private FlowFormDataInfoMapper flowFormDataInfoMapper;

    @Autowired
    private IFlowFormDataInfoService flowFormDataInfoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<FlowTask> selectTodoTaskList(FlowTask query)
    {
        if (taskService == null)
        {
            return new ArrayList<>();
        }
        TaskQuery taskQuery = taskService.createTaskQuery();
        applyRuntimeTaskFilters(query, taskQuery);
        List<Task> tasks = taskQuery.orderByTaskCreateTime().desc().list();
        return buildRuntimeTasks(tasks, false);
    }

    @Override
    public List<FlowTask> selectDoneTaskList(FlowTask query)
    {
        if (historyService == null)
        {
            return new ArrayList<>();
        }
        HistoricTaskInstanceQuery historyQuery = historyService.createHistoricTaskInstanceQuery()
            .includeTaskLocalVariables()
            .includeProcessVariables()
            .finished();
        applyHistoricTaskFilters(query, historyQuery);
        List<HistoricTaskInstance> histories = historyQuery.orderByHistoricTaskInstanceEndTime().desc().list();
        return buildHistoricTasks(histories, false);
    }

    @Override
    public FlowTask selectTaskById(String taskId)
    {
        if (StringUtils.isEmpty(taskId))
        {
            throw new ServiceException("任务ID不能为空");
        }
        Task task = taskService == null ? null : taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null)
        {
            FlowTask flowTask = buildRuntimeTask(task, true);
            fillTaskComments(flowTask);
            return flowTask;
        }
        if (historyService == null)
        {
            throw new ServiceException("任务不存在");
        }
        HistoricTaskInstance history = historyService.createHistoricTaskInstanceQuery()
            .taskId(taskId)
            .includeTaskLocalVariables()
            .includeProcessVariables()
            .singleResult();
        if (history == null)
        {
            throw new ServiceException("任务不存在");
        }
        FlowTask flowTask = buildHistoricTask(history, true);
        fillTaskComments(flowTask);
        return flowTask;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claimTask(String taskId, String userId)
    {
        if (taskService == null)
        {
            throw new ServiceException("Flowable 引擎未启用");
        }
        if (StringUtils.isEmpty(taskId))
        {
            throw new ServiceException("任务ID不能为空");
        }
        if (StringUtils.isEmpty(userId))
        {
            throw new ServiceException("签收人不能为空");
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null)
        {
            throw new ServiceException("任务不存在或已完成");
        }
        taskService.claim(taskId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unclaimTask(String taskId)
    {
        if (taskService == null)
        {
            throw new ServiceException("Flowable 引擎未启用");
        }
        if (StringUtils.isEmpty(taskId))
        {
            throw new ServiceException("任务ID不能为空");
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null)
        {
            throw new ServiceException("任务不存在或已完成");
        }
        taskService.unclaim(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delegateTask(FlowTaskAssignRequest request)
    {
        if (taskService == null)
        {
            throw new ServiceException("Flowable 引擎未启用");
        }
        Task task = taskService.createTaskQuery().taskId(request.getTaskId()).singleResult();
        if (task == null)
        {
            throw new ServiceException("任务不存在或已完成");
        }
        addOperationComment(task, request.getOperator(), request.getComment());
        taskService.delegateTask(task.getId(), request.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferTask(FlowTaskAssignRequest request)
    {
        if (taskService == null)
        {
            throw new ServiceException("Flowable 引擎未启用");
        }
        Task task = taskService.createTaskQuery().taskId(request.getTaskId()).singleResult();
        if (task == null)
        {
            throw new ServiceException("任务不存在或已完成");
        }
        addOperationComment(task, request.getOperator(), request.getComment());
        taskService.setAssignee(task.getId(), request.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(FlowTaskCompleteRequest request)
    {
        if (taskService == null)
        {
            throw new ServiceException("Flowable 引擎未启用");
        }
        Task task = taskService.createTaskQuery().taskId(request.getTaskId()).singleResult();
        if (task == null)
        {
            throw new ServiceException("任务不存在或已完成");
        }
        String operator = StringUtils.defaultIfEmpty(request.getSubmitBy(), task.getAssignee());
        if (identityService != null && StringUtils.isNotEmpty(operator))
        {
            identityService.setAuthenticatedUserId(operator);
        }
        try
        {
            if (StringUtils.isNotEmpty(request.getComment()))
            {
                taskService.addComment(task.getId(), task.getProcessInstanceId(), request.getComment());
            }
            persistFormData(task, request, operator);
            if (!CollectionUtils.isEmpty(request.getLocalVariables()))
            {
                taskService.setVariablesLocal(task.getId(), request.getLocalVariables());
            }
            Map<String, Object> variables = request.getVariables();
            if (variables == null)
            {
                variables = Collections.emptyMap();
            }
            taskService.complete(task.getId(), variables);
        }
        catch (JsonProcessingException e)
        {
            throw new ServiceException("表单数据解析失败", e);
        }
        finally
        {
            if (identityService != null)
            {
                identityService.setAuthenticatedUserId(null);
            }
        }
    }

    private void applyRuntimeTaskFilters(FlowTask query, TaskQuery taskQuery)
    {
        if (StringUtils.isNotEmpty(query.getAssignee()))
        {
            taskQuery.taskAssignee(query.getAssignee());
        }
        if (StringUtils.isNotEmpty(query.getOwner()))
        {
            taskQuery.taskOwner(query.getOwner());
        }
        if (StringUtils.isNotEmpty(query.getCandidateUser()))
        {
            taskQuery.taskCandidateUser(query.getCandidateUser());
        }
        if (StringUtils.isNotEmpty(query.getCandidateGroup()))
        {
            taskQuery.taskCandidateGroup(query.getCandidateGroup());
        }
        if (StringUtils.isNotEmpty(query.getProcessDefinitionId()))
        {
            taskQuery.processDefinitionId(query.getProcessDefinitionId());
        }
        if (StringUtils.isNotEmpty(query.getProcessDefinitionKey()))
        {
            taskQuery.processDefinitionKey(query.getProcessDefinitionKey());
        }
        if (StringUtils.isNotEmpty(query.getProcessInstanceId()))
        {
            taskQuery.processInstanceId(query.getProcessInstanceId());
        }
        if (StringUtils.isNotEmpty(query.getBusinessKey()))
        {
            taskQuery.processInstanceBusinessKey(query.getBusinessKey());
        }
        if (StringUtils.isNotEmpty(query.getTaskName()))
        {
            taskQuery.taskNameLike("%" + query.getTaskName() + "%");
        }
        if (StringUtils.isNotEmpty(query.getCategory()))
        {
            taskQuery.taskCategory(query.getCategory());
        }
        if (StringUtils.isNotEmpty(query.getTenantId()))
        {
            taskQuery.taskTenantId(query.getTenantId());
        }
        Map<String, Object> params = query.getParams();
        if (!CollectionUtils.isEmpty(params))
        {
            Date beginTime = DateUtils.parseDate(params.get("beginTime"));
            if (beginTime != null)
            {
                taskQuery.taskCreatedAfter(beginTime);
            }
            Date endTime = DateUtils.parseDate(params.get("endTime"));
            if (endTime != null)
            {
                taskQuery.taskCreatedBefore(endTime);
            }
        }
    }

    private void applyHistoricTaskFilters(FlowTask query, HistoricTaskInstanceQuery historyQuery)
    {
        if (StringUtils.isNotEmpty(query.getAssignee()))
        {
            historyQuery.taskAssignee(query.getAssignee());
        }
        if (StringUtils.isNotEmpty(query.getOwner()))
        {
            historyQuery.taskOwner(query.getOwner());
        }
        if (StringUtils.isNotEmpty(query.getCandidateUser()))
        {
            historyQuery.taskInvolvedUser(query.getCandidateUser());
        }
        if (StringUtils.isNotEmpty(query.getProcessDefinitionId()))
        {
            historyQuery.processDefinitionId(query.getProcessDefinitionId());
        }
        if (StringUtils.isNotEmpty(query.getProcessDefinitionKey()))
        {
            historyQuery.processDefinitionKey(query.getProcessDefinitionKey());
        }
        if (StringUtils.isNotEmpty(query.getProcessInstanceId()))
        {
            historyQuery.processInstanceId(query.getProcessInstanceId());
        }
        if (StringUtils.isNotEmpty(query.getBusinessKey()))
        {
            historyQuery.processInstanceBusinessKey(query.getBusinessKey());
        }
        if (StringUtils.isNotEmpty(query.getTaskName()))
        {
            historyQuery.taskNameLike("%" + query.getTaskName() + "%");
        }
        if (StringUtils.isNotEmpty(query.getCategory()))
        {
            historyQuery.taskCategory(query.getCategory());
        }
        if (StringUtils.isNotEmpty(query.getTenantId()))
        {
            historyQuery.taskTenantId(query.getTenantId());
        }
        Map<String, Object> params = query.getParams();
        if (!CollectionUtils.isEmpty(params))
        {
            Date beginTime = DateUtils.parseDate(params.get("beginTime"));
            if (beginTime != null)
            {
                historyQuery.taskCreatedAfter(beginTime);
            }
            Date endTime = DateUtils.parseDate(params.get("endTime"));
            if (endTime != null)
            {
                historyQuery.taskCreatedBefore(endTime);
            }
        }
    }

    private List<FlowTask> buildRuntimeTasks(List<Task> tasks, boolean withDetail)
    {
        if (CollectionUtils.isEmpty(tasks))
        {
            return new ArrayList<>();
        }
        List<FlowTask> results = new ArrayList<>(tasks.size());
        Map<String, HistoricProcessInstance> processCache = new HashMap<>();
        Map<String, ProcessInstance> runtimeCache = new HashMap<>();
        Map<String, FlowModelInfo> modelCache = new HashMap<>();
        Map<Long, FlowFormInfo> formIdCache = new HashMap<>();
        Map<String, FlowFormInfo> formKeyCache = new HashMap<>();
        for (Task task : tasks)
        {
            FlowTask flowTask = buildRuntimeTask(task, withDetail, processCache, runtimeCache, modelCache, formIdCache,
                formKeyCache);
            results.add(flowTask);
        }
        return results;
    }

    private FlowTask buildRuntimeTask(Task task, boolean withDetail)
    {
        return buildRuntimeTask(task, withDetail, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
            new HashMap<>());
    }

    private FlowTask buildRuntimeTask(Task task, boolean withDetail, Map<String, HistoricProcessInstance> processCache,
            Map<String, ProcessInstance> runtimeCache, Map<String, FlowModelInfo> modelCache,
            Map<Long, FlowFormInfo> formIdCache, Map<String, FlowFormInfo> formKeyCache)
    {
        FlowTask flowTask = new FlowTask();
        flowTask.setTaskId(task.getId());
        flowTask.setTaskName(task.getName());
        flowTask.setTaskKey(task.getTaskDefinitionKey());
        flowTask.setDescription(task.getDescription());
        flowTask.setCategory(task.getCategory());
        flowTask.setOwner(task.getOwner());
        flowTask.setAssignee(task.getAssignee());
        flowTask.setPriority(task.getPriority());
        flowTask.setTenantId(task.getTenantId());
        flowTask.setDelegationState(task.getDelegationState() == null ? null : task.getDelegationState().toString());
        flowTask.setCreateTime(task.getCreateTime());
        flowTask.setClaimTime(task.getClaimTime());
        flowTask.setDueDate(task.getDueDate());
        flowTask.setProcessInstanceId(task.getProcessInstanceId());
        flowTask.setProcessDefinitionId(task.getProcessDefinitionId());
        flowTask.setProcessDefinitionKey(task.getProcessDefinitionKey());
        flowTask.setCompleted(Boolean.FALSE);
        fillCandidateInfo(flowTask);
        HistoricProcessInstance process = loadHistoricProcessInstance(task.getProcessInstanceId(), processCache);
        if (process != null)
        {
            populateProcessInfo(flowTask, process);
        }
        else
        {
            ProcessInstance runtime = loadRuntimeProcessInstance(task.getProcessInstanceId(), runtimeCache);
            if (runtime != null)
            {
                populateProcessInfo(flowTask, runtime);
            }
        }
        FlowModelInfo modelInfo = loadModelInfo(task.getProcessDefinitionKey(), modelCache);
        if (modelInfo != null)
        {
            flowTask.setModelInfo(modelInfo);
            flowTask.setFormInfo(resolveFormInfo(modelInfo, formIdCache, formKeyCache));
            if (StringUtils.isEmpty(flowTask.getProcessDefinitionName()))
            {
                flowTask.setProcessDefinitionName(modelInfo.getDefinitionName());
            }
            if (flowTask.getProcessDefinitionVersion() == null && modelInfo.getVersion() != null)
            {
                flowTask.setProcessDefinitionVersion(modelInfo.getVersion());
            }
        }
        flowTask.setFormData(flowFormDataInfoMapper.selectLatestByTaskId(task.getId()));
        if (withDetail)
        {
            flowTask.setTaskVariables(taskService == null ? null : taskService.getVariables(task.getId()));
        }
        return flowTask;
    }

    private List<FlowTask> buildHistoricTasks(List<HistoricTaskInstance> histories, boolean withDetail)
    {
        if (CollectionUtils.isEmpty(histories))
        {
            return new ArrayList<>();
        }
        List<FlowTask> results = new ArrayList<>(histories.size());
        Map<String, HistoricProcessInstance> processCache = new HashMap<>();
        Map<String, FlowModelInfo> modelCache = new HashMap<>();
        Map<Long, FlowFormInfo> formIdCache = new HashMap<>();
        Map<String, FlowFormInfo> formKeyCache = new HashMap<>();
        for (HistoricTaskInstance history : histories)
        {
            FlowTask flowTask = buildHistoricTask(history, withDetail, processCache, modelCache, formIdCache,
                formKeyCache);
            results.add(flowTask);
        }
        return results;
    }

    private FlowTask buildHistoricTask(HistoricTaskInstance history, boolean withDetail)
    {
        return buildHistoricTask(history, withDetail, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    private FlowTask buildHistoricTask(HistoricTaskInstance history, boolean withDetail,
            Map<String, HistoricProcessInstance> processCache, Map<String, FlowModelInfo> modelCache,
            Map<Long, FlowFormInfo> formIdCache, Map<String, FlowFormInfo> formKeyCache)
    {
        FlowTask flowTask = new FlowTask();
        flowTask.setTaskId(history.getId());
        flowTask.setTaskName(history.getName());
        flowTask.setTaskKey(history.getTaskDefinitionKey());
        flowTask.setDescription(history.getDescription());
        flowTask.setCategory(history.getCategory());
        flowTask.setOwner(history.getOwner());
        flowTask.setAssignee(history.getAssignee());
        flowTask.setPriority(history.getPriority());
        flowTask.setTenantId(history.getTenantId());
        flowTask.setDelegationState(history.getDelegationState() == null ? null
                : history.getDelegationState().toString());
        flowTask.setCreateTime(history.getCreateTime());
        flowTask.setClaimTime(history.getClaimTime());
        flowTask.setDueDate(history.getDueDate());
        flowTask.setEndTime(history.getEndTime());
        flowTask.setDurationInMillis(history.getDurationInMillis());
        flowTask.setProcessInstanceId(history.getProcessInstanceId());
        flowTask.setProcessDefinitionId(history.getProcessDefinitionId());
        flowTask.setProcessDefinitionKey(history.getProcessDefinitionKey());
        flowTask.setCompleted(Boolean.TRUE);
        HistoricProcessInstance process = loadHistoricProcessInstance(history.getProcessInstanceId(), processCache);
        if (process != null)
        {
            populateProcessInfo(flowTask, process);
        }
        FlowModelInfo modelInfo = loadModelInfo(history.getProcessDefinitionKey(), modelCache);
        if (modelInfo != null)
        {
            flowTask.setModelInfo(modelInfo);
            flowTask.setFormInfo(resolveFormInfo(modelInfo, formIdCache, formKeyCache));
            if (StringUtils.isEmpty(flowTask.getProcessDefinitionName()))
            {
                flowTask.setProcessDefinitionName(modelInfo.getDefinitionName());
            }
            if (flowTask.getProcessDefinitionVersion() == null && modelInfo.getVersion() != null)
            {
                flowTask.setProcessDefinitionVersion(modelInfo.getVersion());
            }
        }
        flowTask.setFormData(flowFormDataInfoMapper.selectLatestByTaskId(history.getId()));
        if (withDetail)
        {
            if (history.getTaskLocalVariables() != null)
            {
                flowTask.setTaskVariables(history.getTaskLocalVariables());
            }
            if (history.getProcessVariables() != null)
            {
                flowTask.setProcessVariables(history.getProcessVariables());
            }
        }
        return flowTask;
    }

    private void fillTaskComments(FlowTask task)
    {
        if (taskService == null)
        {
            return;
        }
        List<org.flowable.engine.task.Comment> comments = taskService.getTaskComments(task.getTaskId());
        if (CollectionUtils.isEmpty(comments))
        {
            return;
        }
        List<FlowTaskComment> result = comments.stream().map(comment -> {
            FlowTaskComment dto = new FlowTaskComment();
            dto.setCommentId(comment.getId());
            dto.setType(comment.getType());
            dto.setUserId(comment.getUserId());
            dto.setTaskId(comment.getTaskId());
            dto.setProcessInstanceId(comment.getProcessInstanceId());
            dto.setMessage(comment.getFullMessage());
            dto.setTime(comment.getTime());
            return dto;
        }).collect(Collectors.toList());
        task.setComments(result);
    }

    private void fillCandidateInfo(FlowTask task)
    {
        if (taskService == null)
        {
            return;
        }
        List<IdentityLink> links = taskService.getIdentityLinksForTask(task.getTaskId());
        if (CollectionUtils.isEmpty(links))
        {
            return;
        }
        Set<String> users = new LinkedHashSet<>();
        Set<String> groups = new LinkedHashSet<>();
        for (IdentityLink link : links)
        {
            if (!IdentityLinkType.CANDIDATE.equals(link.getType()))
            {
                continue;
            }
            if (StringUtils.isNotEmpty(link.getUserId()))
            {
                users.add(link.getUserId());
            }
            if (StringUtils.isNotEmpty(link.getGroupId()))
            {
                groups.add(link.getGroupId());
            }
        }
        if (!users.isEmpty())
        {
            task.setCandidateUsers(users);
        }
        if (!groups.isEmpty())
        {
            task.setCandidateGroups(groups);
        }
    }

    private HistoricProcessInstance loadHistoricProcessInstance(String processInstanceId,
            Map<String, HistoricProcessInstance> cache)
    {
        if (StringUtils.isEmpty(processInstanceId))
        {
            return null;
        }
        if (cache.containsKey(processInstanceId))
        {
            return cache.get(processInstanceId);
        }
        HistoricProcessInstance instance = historyService == null ? null
                : historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .includeProcessVariables()
                    .singleResult();
        cache.put(processInstanceId, instance);
        return instance;
    }

    private ProcessInstance loadRuntimeProcessInstance(String processInstanceId, Map<String, ProcessInstance> cache)
    {
        if (StringUtils.isEmpty(processInstanceId))
        {
            return null;
        }
        if (cache.containsKey(processInstanceId))
        {
            return cache.get(processInstanceId);
        }
        ProcessInstance instance = runtimeService == null ? null
                : runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        cache.put(processInstanceId, instance);
        return instance;
    }

    private void populateProcessInfo(FlowTask task, HistoricProcessInstance process)
    {
        task.setBusinessKey(process.getBusinessKey());
        task.setProcessDefinitionName(process.getProcessDefinitionName());
        task.setProcessDefinitionVersion(process.getProcessDefinitionVersion());
        task.setDeploymentId(process.getDeploymentId());
        task.setStartUserId(process.getStartUserId());
        if (process.getProcessVariables() != null)
        {
            task.setProcessVariables(process.getProcessVariables());
        }
    }

    private void populateProcessInfo(FlowTask task, ProcessInstance process)
    {
        task.setBusinessKey(process.getBusinessKey());
        task.setDeploymentId(process.getDeploymentId());
        task.setStartUserId(process.getStartUserId());
        if (StringUtils.isEmpty(task.getProcessDefinitionKey()))
        {
            task.setProcessDefinitionKey(process.getProcessDefinitionKey());
        }
        if (runtimeService != null)
        {
            task.setProcessVariables(runtimeService.getVariables(process.getId()));
        }
    }

    private FlowModelInfo loadModelInfo(String definitionKey, Map<String, FlowModelInfo> cache)
    {
        if (StringUtils.isEmpty(definitionKey))
        {
            return null;
        }
        if (cache.containsKey(definitionKey))
        {
            return cache.get(definitionKey);
        }
        FlowModelInfo modelInfo = flowModelInfoMapper.selectFlowModelInfoByDefinitionKey(definitionKey);
        cache.put(definitionKey, modelInfo);
        return modelInfo;
    }

    private FlowFormInfo resolveFormInfo(FlowModelInfo modelInfo, Map<Long, FlowFormInfo> formIdCache,
            Map<String, FlowFormInfo> formKeyCache)
    {
        if (modelInfo == null)
        {
            return null;
        }
        if (modelInfo.getFormId() != null)
        {
            return formIdCache.computeIfAbsent(modelInfo.getFormId(), flowFormInfoMapper::selectFlowFormInfoById);
        }
        if (StringUtils.isNotEmpty(modelInfo.getFormKey()))
        {
            return formKeyCache.computeIfAbsent(modelInfo.getFormKey(), flowFormInfoMapper::selectFlowFormInfoByKey);
        }
        return null;
    }

    private void addOperationComment(Task task, String operator, String comment)
    {
        if (taskService == null || StringUtils.isEmpty(comment))
        {
            return;
        }
        if (identityService != null && StringUtils.isNotEmpty(operator))
        {
            identityService.setAuthenticatedUserId(operator);
        }
        try
        {
            taskService.addComment(task.getId(), task.getProcessInstanceId(), comment);
        }
        finally
        {
            if (identityService != null)
            {
                identityService.setAuthenticatedUserId(null);
            }
        }
    }

    private void persistFormData(Task task, FlowTaskCompleteRequest request, String operator)
            throws JsonProcessingException
    {
        if (CollectionUtils.isEmpty(request.getFormValues()))
        {
            return;
        }
        FlowModelInfo modelInfo = flowModelInfoMapper.selectFlowModelInfoByDefinitionKey(task.getProcessDefinitionKey());
        FlowFormDataInfo dataInfo = new FlowFormDataInfo();
        dataInfo.setFormId(request.getFormId() != null ? request.getFormId()
                : (modelInfo != null ? modelInfo.getFormId() : null));
        dataInfo.setFormKey(StringUtils.isNotEmpty(request.getFormKey()) ? request.getFormKey()
                : (modelInfo != null ? modelInfo.getFormKey() : null));
        dataInfo.setProcessInstanceId(task.getProcessInstanceId());
        dataInfo.setTaskId(task.getId());
        dataInfo.setTaskKey(task.getTaskDefinitionKey());
        dataInfo.setBusinessKey(StringUtils.isNotEmpty(request.getBusinessKey()) ? request.getBusinessKey()
                : resolveBusinessKey(task.getProcessInstanceId()));
        dataInfo.setFormValues(objectMapper.writeValueAsString(request.getFormValues()));
        dataInfo.setSubmitBy(StringUtils.defaultIfEmpty(operator, request.getSubmitBy()));
        dataInfo.setCreateBy(operator);
        dataInfo.setUpdateBy(operator);
        flowFormDataInfoService.insertFlowFormDataInfo(dataInfo);
    }

    private String resolveBusinessKey(String processInstanceId)
    {
        if (StringUtils.isEmpty(processInstanceId))
        {
            return null;
        }
        if (runtimeService != null)
        {
            ProcessInstance runtime = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
            if (runtime != null && StringUtils.isNotEmpty(runtime.getBusinessKey()))
            {
                return runtime.getBusinessKey();
            }
        }
        if (historyService != null)
        {
            HistoricProcessInstance history = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
            if (history != null)
            {
                return history.getBusinessKey();
            }
        }
        return null;
    }
}
