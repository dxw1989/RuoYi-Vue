package com.ruoyi.flowable.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Flowable 任务信息
 */
public class FlowTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private String taskId;

    /** 任务名称 */
    private String taskName;

    /** 任务节点标识 */
    private String taskKey;

    /** 任务分类 */
    private String category;

    /** 任务描述 */
    private String description;

    /** 所属流程实例ID */
    private String processInstanceId;

    /** 所属流程定义ID */
    private String processDefinitionId;

    /** 流程定义Key */
    private String processDefinitionKey;

    /** 流程定义名称 */
    private String processDefinitionName;

    /** 流程部署ID */
    private String deploymentId;

    /** 流程定义版本 */
    private Integer processDefinitionVersion;

    /** 业务主键 */
    private String businessKey;

    /** 发起人 */
    private String startUserId;

    /** 任务所有人 */
    private String owner;

    /** 当前办理人 */
    private String assignee;

    /** 候选用户 */
    private Set<String> candidateUsers;

    /** 候选角色/组 */
    private Set<String> candidateGroups;

    /** 委派状态 */
    private String delegationState;

    /** 优先级 */
    private Integer priority;

    /** 租户 */
    private String tenantId;

    /** 任务变量 */
    private Map<String, Object> taskVariables;

    /** 流程变量 */
    private Map<String, Object> processVariables;

    /** 是否已完成 */
    private Boolean completed;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 签收时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date claimTime;

    /** 截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dueDate;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 历时（毫秒） */
    private Long durationInMillis;

    /** 关联的模型信息 */
    @JsonInclude(Include.NON_NULL)
    private FlowModelInfo modelInfo;

    /** 关联的表单信息 */
    @JsonInclude(Include.NON_NULL)
    private FlowFormInfo formInfo;

    /** 最近一次表单数据 */
    @JsonInclude(Include.NON_NULL)
    private FlowFormDataInfo formData;

    /** 评论信息 */
    @JsonInclude(Include.NON_EMPTY)
    private List<FlowTaskComment> comments;

    /** 查询用：候选用户 */
    private String candidateUser;

    /** 查询用：候选组 */
    private String candidateGroup;

    public String getTaskId()
    {
        return taskId;
    }

    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    public String getTaskKey()
    {
        return taskKey;
    }

    public void setTaskKey(String taskKey)
    {
        this.taskKey = taskKey;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getProcessInstanceId()
    {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId)
    {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessDefinitionId()
    {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId)
    {
        this.processDefinitionId = processDefinitionId;
    }

    public String getProcessDefinitionKey()
    {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey)
    {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getProcessDefinitionName()
    {
        return processDefinitionName;
    }

    public void setProcessDefinitionName(String processDefinitionName)
    {
        this.processDefinitionName = processDefinitionName;
    }

    public String getDeploymentId()
    {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId)
    {
        this.deploymentId = deploymentId;
    }

    public Integer getProcessDefinitionVersion()
    {
        return processDefinitionVersion;
    }

    public void setProcessDefinitionVersion(Integer processDefinitionVersion)
    {
        this.processDefinitionVersion = processDefinitionVersion;
    }

    public String getBusinessKey()
    {
        return businessKey;
    }

    public void setBusinessKey(String businessKey)
    {
        this.businessKey = businessKey;
    }

    public String getStartUserId()
    {
        return startUserId;
    }

    public void setStartUserId(String startUserId)
    {
        this.startUserId = startUserId;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getAssignee()
    {
        return assignee;
    }

    public void setAssignee(String assignee)
    {
        this.assignee = assignee;
    }

    public Set<String> getCandidateUsers()
    {
        return candidateUsers;
    }

    public void setCandidateUsers(Set<String> candidateUsers)
    {
        this.candidateUsers = candidateUsers;
    }

    public Set<String> getCandidateGroups()
    {
        return candidateGroups;
    }

    public void setCandidateGroups(Set<String> candidateGroups)
    {
        this.candidateGroups = candidateGroups;
    }

    public String getDelegationState()
    {
        return delegationState;
    }

    public void setDelegationState(String delegationState)
    {
        this.delegationState = delegationState;
    }

    public Integer getPriority()
    {
        return priority;
    }

    public void setPriority(Integer priority)
    {
        this.priority = priority;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public Map<String, Object> getTaskVariables()
    {
        return taskVariables;
    }

    public void setTaskVariables(Map<String, Object> taskVariables)
    {
        this.taskVariables = taskVariables;
    }

    public Map<String, Object> getProcessVariables()
    {
        return processVariables;
    }

    public void setProcessVariables(Map<String, Object> processVariables)
    {
        this.processVariables = processVariables;
    }

    public Boolean getCompleted()
    {
        return completed;
    }

    public void setCompleted(Boolean completed)
    {
        this.completed = completed;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getClaimTime()
    {
        return claimTime;
    }

    public void setClaimTime(Date claimTime)
    {
        this.claimTime = claimTime;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Long getDurationInMillis()
    {
        return durationInMillis;
    }

    public void setDurationInMillis(Long durationInMillis)
    {
        this.durationInMillis = durationInMillis;
    }

    public FlowModelInfo getModelInfo()
    {
        return modelInfo;
    }

    public void setModelInfo(FlowModelInfo modelInfo)
    {
        this.modelInfo = modelInfo;
    }

    public FlowFormInfo getFormInfo()
    {
        return formInfo;
    }

    public void setFormInfo(FlowFormInfo formInfo)
    {
        this.formInfo = formInfo;
    }

    public FlowFormDataInfo getFormData()
    {
        return formData;
    }

    public void setFormData(FlowFormDataInfo formData)
    {
        this.formData = formData;
    }

    public List<FlowTaskComment> getComments()
    {
        return comments;
    }

    public void setComments(List<FlowTaskComment> comments)
    {
        this.comments = comments;
    }

    public String getCandidateUser()
    {
        return candidateUser;
    }

    public void setCandidateUser(String candidateUser)
    {
        this.candidateUser = candidateUser;
    }

    public String getCandidateGroup()
    {
        return candidateGroup;
    }

    public void setCandidateGroup(String candidateGroup)
    {
        this.candidateGroup = candidateGroup;
    }
}
