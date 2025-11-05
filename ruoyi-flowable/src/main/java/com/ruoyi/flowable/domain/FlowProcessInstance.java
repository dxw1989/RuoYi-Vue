package com.ruoyi.flowable.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Flowable 流程实例信息
 */
public class FlowProcessInstance extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 流程实例ID */
    private String instanceId;

    /** 流程定义ID */
    private String definitionId;

    /** 流程定义Key */
    private String definitionKey;

    /** 流程定义名称 */
    private String definitionName;

    /** 部署ID */
    private String deploymentId;

    /** 业务主键 */
    private String businessKey;

    /** 发起人ID */
    private String startUserId;

    /** 发起人名称 */
    private String startUserName;

    /** 发起时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 是否完成 */
    private Boolean finished;

    /** 是否挂起 */
    private Boolean suspended;

    /** 租户 */
    private String tenantId;

    /** 当前任务ID集合 */
    private List<String> currentTaskIds;

    /** 当前任务名称集合 */
    private List<String> currentTaskNames;

    /** 当前办理人ID集合 */
    private List<String> currentAssignees;

    /** 当前办理人名称集合 */
    private List<String> currentAssigneeNames;

    /** 关联模型ID */
    private Long modelId;

    /** 关联表单ID */
    private Long formId;

    /** 关联表单标识 */
    private String formKey;

    /** 最近一次表单数据ID */
    private Long formDataId;

    /** 最近一次表单值 */
    private String formValues;

    /** 流程变量 */
    @JsonInclude(Include.NON_EMPTY)
    private Map<String, Object> variables;

    public String getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
    }

    public String getDefinitionId()
    {
        return definitionId;
    }

    public void setDefinitionId(String definitionId)
    {
        this.definitionId = definitionId;
    }

    public String getDefinitionKey()
    {
        return definitionKey;
    }

    public void setDefinitionKey(String definitionKey)
    {
        this.definitionKey = definitionKey;
    }

    public String getDefinitionName()
    {
        return definitionName;
    }

    public void setDefinitionName(String definitionName)
    {
        this.definitionName = definitionName;
    }

    public String getDeploymentId()
    {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId)
    {
        this.deploymentId = deploymentId;
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

    public String getStartUserName()
    {
        return startUserName;
    }

    public void setStartUserName(String startUserName)
    {
        this.startUserName = startUserName;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Boolean getFinished()
    {
        return finished;
    }

    public void setFinished(Boolean finished)
    {
        this.finished = finished;
    }

    public Boolean getSuspended()
    {
        return suspended;
    }

    public void setSuspended(Boolean suspended)
    {
        this.suspended = suspended;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public List<String> getCurrentTaskIds()
    {
        return currentTaskIds;
    }

    public void setCurrentTaskIds(List<String> currentTaskIds)
    {
        this.currentTaskIds = currentTaskIds;
    }

    public List<String> getCurrentTaskNames()
    {
        return currentTaskNames;
    }

    public void setCurrentTaskNames(List<String> currentTaskNames)
    {
        this.currentTaskNames = currentTaskNames;
    }

    public List<String> getCurrentAssignees()
    {
        return currentAssignees;
    }

    public void setCurrentAssignees(List<String> currentAssignees)
    {
        this.currentAssignees = currentAssignees;
    }

    public List<String> getCurrentAssigneeNames()
    {
        return currentAssigneeNames;
    }

    public void setCurrentAssigneeNames(List<String> currentAssigneeNames)
    {
        this.currentAssigneeNames = currentAssigneeNames;
    }

    public Long getModelId()
    {
        return modelId;
    }

    public void setModelId(Long modelId)
    {
        this.modelId = modelId;
    }

    public Long getFormId()
    {
        return formId;
    }

    public void setFormId(Long formId)
    {
        this.formId = formId;
    }

    public String getFormKey()
    {
        return formKey;
    }

    public void setFormKey(String formKey)
    {
        this.formKey = formKey;
    }

    public Long getFormDataId()
    {
        return formDataId;
    }

    public void setFormDataId(Long formDataId)
    {
        this.formDataId = formDataId;
    }

    public String getFormValues()
    {
        return formValues;
    }

    public void setFormValues(String formValues)
    {
        this.formValues = formValues;
    }

    public Map<String, Object> getVariables()
    {
        return variables;
    }

    public void setVariables(Map<String, Object> variables)
    {
        this.variables = variables;
    }
}
