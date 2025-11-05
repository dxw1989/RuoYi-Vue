package com.ruoyi.flowable.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Flowable 流程定义信息
 */
public class FlowProcessDefinition extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 流程定义ID */
    private String definitionId;

    /** 流程定义Key */
    private String definitionKey;

    /** 流程定义名称 */
    private String definitionName;

    /** 流程分类 */
    private String category;

    /** 版本号 */
    private Integer version;

    /** 部署ID */
    private String deploymentId;

    /** 部署名称 */
    private String deploymentName;

    /** 部署时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deploymentTime;

    /** 是否挂起 */
    private Boolean suspended;

    /** 资源名称 */
    private String resourceName;

    /** 流程描述 */
    private String description;

    /** 租户ID */
    private String tenantId;

    /** 关联模型ID */
    private Long modelId;

    /** 关联模型名称 */
    private String modelName;

    /** 关联表单ID */
    private Long formId;

    /** 关联表单标识 */
    private String formKey;

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

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public String getDeploymentId()
    {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId)
    {
        this.deploymentId = deploymentId;
    }

    public String getDeploymentName()
    {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName)
    {
        this.deploymentName = deploymentName;
    }

    public Date getDeploymentTime()
    {
        return deploymentTime;
    }

    public void setDeploymentTime(Date deploymentTime)
    {
        this.deploymentTime = deploymentTime;
    }

    public Boolean getSuspended()
    {
        return suspended;
    }

    public void setSuspended(Boolean suspended)
    {
        this.suspended = suspended;
    }

    public String getResourceName()
    {
        return resourceName;
    }

    public void setResourceName(String resourceName)
    {
        this.resourceName = resourceName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public Long getModelId()
    {
        return modelId;
    }

    public void setModelId(Long modelId)
    {
        this.modelId = modelId;
    }

    public String getModelName()
    {
        return modelName;
    }

    public void setModelName(String modelName)
    {
        this.modelName = modelName;
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
}
